import argparse
import json
import os
import re
import time
from pathlib import Path
from typing import Dict, List, Optional
from urllib.parse import quote

import truststore
truststore.inject_into_ssl()

from selenium import webdriver
from selenium.webdriver.chrome.options import Options
from selenium.webdriver.chrome.service import Service
from selenium.webdriver.common.by import By
from selenium.webdriver.remote.webelement import WebElement
from selenium.webdriver.support.ui import WebDriverWait
from selenium.common.exceptions import (
    TimeoutException,
    StaleElementReferenceException,
    ElementClickInterceptedException,
)

BOOK_A_TEST_BASE = "https://www.thyrocare.com/book-a-test"
DETAIL_URL_PREFIX = {
    "package": "https://www.thyrocare.com/profile/",
    "single-test": "https://www.thyrocare.com/test/",
}

CHROMEDRIVER_PATH = "/Users/shivam13juna/Documents/side_stuff/learn_stuff/scrape_thyrocare/chromedriver-mac-arm64/chromedriver"
PROFILE_DIR = "/Users/shivam13juna/Documents/side_stuff/learn_stuff/scrape_thyrocare/selenium_profile"

OUTPUT_DIR = Path("thyrocare_output")
OUTPUT_DIR.mkdir(exist_ok=True)


def normalize_space(text: str) -> str:
    return " ".join((text or "").split()).strip()


class ThyrocareScraper:
    CARD_SELECTOR = "[class*='TestCard__ProductCard']"

    def __init__(self, headless: bool = False, timeout: int = 20):
        chrome_options = Options()
        if headless:
            chrome_options.add_argument("--headless=new")

        chrome_options.add_argument("--start-maximized")
        chrome_options.add_argument("--disable-notifications")
        chrome_options.add_argument("--disable-blink-features=AutomationControlled")
        chrome_options.add_argument("--no-sandbox")
        chrome_options.add_argument("--disable-dev-shm-usage")
        chrome_options.add_argument(f"--user-data-dir={PROFILE_DIR}")
        chrome_options.add_argument("--profile-directory=Default")

        self.driver = webdriver.Chrome(
            service=Service(CHROMEDRIVER_PATH),
            options=chrome_options,
        )
        self.wait = WebDriverWait(self.driver, timeout)
        self._cf_cleared = False

    def close(self):
        try:
            self.driver.quit()
        except Exception:
            pass

    # ── helpers ───────────────────────────────────────────────────────────

    def safe_click(self, elem: WebElement):
        self.driver.execute_script(
            "arguments[0].scrollIntoView({block:'center', inline:'nearest'});", elem
        )
        time.sleep(0.3)
        try:
            elem.click()
        except (ElementClickInterceptedException, StaleElementReferenceException):
            self.driver.execute_script("arguments[0].click();", elem)

    def _get_cookie_names(self) -> set:
        try:
            return {c["name"] for c in self.driver.get_cookies()}
        except Exception:
            return set()

    # ── cloudflare ────────────────────────────────────────────────────────

    def is_cloudflare_challenge_active(self) -> bool:
        try:
            current_url = (self.driver.current_url or "").lower()
        except Exception:
            current_url = ""
        try:
            title = (self.driver.title or "").lower()
        except Exception:
            title = ""

        url_markers = ["/cdn-cgi/challenge-platform/", "challenge-platform"]
        title_markers = [
            "verify you are human",
            "checking your browser",
            "just a moment",
            "attention required",
        ]

        if any(m in current_url for m in url_markers):
            return True
        if any(m in title for m in title_markers):
            return True
        return False

    def ensure_human_verified(self, timeout: int = 60) -> bool:
        if self._cf_cleared and not self.is_cloudflare_challenge_active():
            return True

        if not self.is_cloudflare_challenge_active():
            try:
                current_url = (self.driver.current_url or "").lower()
            except Exception:
                current_url = ""
            if "thyrocare.com" in current_url:
                self._cf_cleared = True
                return True
            if "cf_clearance" in self._get_cookie_names():
                self._cf_cleared = True
                return True

        start = time.time()
        printed = False
        while time.time() - start < timeout:
            if not self.is_cloudflare_challenge_active():
                if "cf_clearance" in self._get_cookie_names():
                    self._cf_cleared = True
                    return True
                try:
                    current_url = (self.driver.current_url or "").lower()
                except Exception:
                    current_url = ""
                if "thyrocare.com" in current_url:
                    self._cf_cleared = True
                    return True
            if not printed:
                print("[INFO] Cloudflare challenge detected — waiting for verification...")
                printed = True
            time.sleep(2)

        raise TimeoutException("Cloudflare verification was not completed in time.")

    def wait_for_user_ready(self, mode: str):
        """Open the site and wait for the user to pass Cloudflare / log in."""
        url = f"{BOOK_A_TEST_BASE}?type={mode}"
        self.driver.get(url)
        print(f"[INFO] Browser opened to {mode} listing.")
        print("[INFO] Complete Cloudflare verification if needed.")
        print("[INFO] Close any popups yourself.")
        input("[INFO] Press ENTER here when you're ready to start scraping...")
        self._cf_cleared = True

    # ── popup ─────────────────────────────────────────────────────────────

    def close_popup_if_present(self, timeout: int = 5) -> bool:
        locators = [
            (By.XPATH, "//*[local-name()='svg' and @data-icon='cross']/ancestor::button[1]"),
            (By.XPATH, "//*[local-name()='svg' and @data-icon='cross']/ancestor::*[@role='button'][1]"),
            (By.XPATH, "//*[local-name()='svg' and @data-icon='cross']/ancestor::div[1]"),
            (By.XPATH, "//*[local-name()='svg' and @data-icon='cross']"),
            (By.CSS_SELECTOR, "svg[data-icon='cross']"),
        ]
        end_time = time.time() + timeout
        while time.time() < end_time:
            self.ensure_human_verified(timeout=60)
            for by, selector in locators:
                try:
                    elems = self.driver.find_elements(by, selector)
                    for elem in elems:
                        try:
                            if elem.is_displayed():
                                self.safe_click(elem)
                                time.sleep(1)
                                return True
                        except Exception:
                            continue
                except Exception:
                    continue
            time.sleep(0.5)
        return False

    # ── phase 1: scroll listing & collect card names/prices ───────────────

    def _collect_card_info_from_page(self) -> List[Dict[str, str]]:
        """Grab name and price from all currently visible cards."""
        return self.driver.execute_script("""
            const cards = document.querySelectorAll("[class*='TestCard__ProductCard']");
            const results = [];
            for (const card of cards) {
                const h3 = card.querySelector('h3');
                if (!h3) continue;
                const name = h3.textContent.trim();
                if (!name) continue;
                const h4 = card.querySelector('h4');
                const price = h4 ? h4.textContent.trim() : '';
                results.push({name, price});
            }
            return results;
        """) or []

    def scroll_and_collect_cards(self, max_rounds: int = 80, pause: float = 2.5) -> List[Dict[str, str]]:
        """Scroll the listing page and collect card info as we go."""
        seen_names: set = set()
        all_cards: List[Dict[str, str]] = []
        stable_rounds = 0
        stable_needed = 4

        for rnd in range(max_rounds):
            self.ensure_human_verified(timeout=60)
            self.driver.execute_script("window.scrollTo(0, document.body.scrollHeight);")
            time.sleep(pause)
            self.ensure_human_verified(timeout=60)

            current = self._collect_card_info_from_page()
            new_count = 0
            for card in current:
                if card["name"] not in seen_names:
                    seen_names.add(card["name"])
                    all_cards.append(card)
                    new_count += 1

            if new_count > 0:
                stable_rounds = 0
                print(f"  [scroll] round {rnd+1}: {len(all_cards)} cards total (+{new_count} new)")
            else:
                stable_rounds += 1

            if stable_rounds >= stable_needed:
                print(f"  [scroll] no new cards after {stable_needed} rounds, done.")
                break

        self.driver.execute_script("window.scrollTo(0, 0);")
        time.sleep(1)
        return all_cards

    # ── phase 2: scrape each detail page by URL ─────────────────────────

    def collect_detail_via_accordion(self) -> Dict[str, List[str]]:
        """Click each accordion section in #testsAccordian and read tests."""
        groups: Dict[str, List[str]] = {}

        try:
            accordion = self.driver.find_element(By.CSS_SELECTOR, "#testsAccordian")
        except Exception:
            return groups

        buttons = accordion.find_elements(By.CSS_SELECTOR, "div > button, [id^='heading_'] > button")
        if not buttons:
            buttons = accordion.find_elements(By.TAG_NAME, "button")

        for btn in buttons:
            try:
                heading_text = normalize_space(btn.text)
                if not heading_text:
                    continue

                self.safe_click(btn)
                time.sleep(0.8)

                # Find the expanded panel after this button
                panel = None
                for xpath in [
                    "./following-sibling::div[1]",
                    "./ancestor::div[1]/following-sibling::div[1]",
                    "./ancestor::*[starts-with(@id,'heading_')]/following-sibling::div[1]",
                ]:
                    try:
                        panel = btn.find_element(By.XPATH, xpath)
                        break
                    except Exception:
                        continue

                if not panel:
                    continue

                test_names = []
                test_elems = panel.find_elements(By.CSS_SELECTOR, "p, span, li, div > div")
                seen_texts: set = set()
                for el in test_elems:
                    txt = normalize_space(el.text)
                    if not txt or len(txt) > 140 or txt in seen_texts:
                        continue
                    if txt.lower() == heading_text.lower():
                        continue
                    if txt in {"Book Now", "Book", "Add", "View", "Show More"}:
                        continue
                    if "includes" in txt.lower() and "test" in txt.lower():
                        continue
                    seen_texts.add(txt)
                    test_names.append(txt)

                if test_names:
                    groups[heading_text] = test_names

            except (StaleElementReferenceException, Exception):
                continue

        return groups

    def _parse_tests_from_text(self, text_block: str) -> Dict[str, List[str]]:
        """Fallback: parse grouped tests from visible text on the page."""
        heading_re = re.compile(r".+\(Includes\s+\d+\s+tests?\)", re.IGNORECASE)

        lines = [normalize_space(x) for x in text_block.splitlines() if normalize_space(x)]

        groups: Dict[str, List[str]] = {}
        current_heading: Optional[str] = None

        ignore_exact = {"Book Now", "Book", "Add", "View", "Show More"}
        ignore_contains = [
            "Tests included in this package",
            "Tests included in this test",
            "Free Sample Pickup",
            "Report on-time Delivery",
            "Why Thyrocare",
            "Frequently Asked Questions",
            "Book Your Test Now",
            "Process:",
            "Recommended Packages",
        ]

        for line in lines:
            if heading_re.fullmatch(line):
                current_heading = line
                groups.setdefault(current_heading, [])
                continue
            if current_heading is None:
                continue
            if line in ignore_exact:
                continue
            if any(s.lower() in line.lower() for s in ignore_contains):
                continue
            if len(line) > 140:
                continue

            parts = [normalize_space(p) for p in line.split(";") if normalize_space(p)]
            for part in parts:
                if part == current_heading:
                    continue
                if "includes" in part.lower() and "test" in part.lower():
                    continue
                if part not in groups[current_heading]:
                    groups[current_heading].append(part)

        return {k: v for k, v in groups.items() if v}

    def _get_page_text_block(self) -> str:
        """Get the most relevant text block from a detail page."""
        for xp in [
            "//*[contains(normalize-space(), 'Tests included in this package')]",
            "//*[contains(normalize-space(), 'Tests included in this test')]",
            "//*[contains(normalize-space(), 'Tests included')]",
        ]:
            try:
                elems = self.driver.find_elements(By.XPATH, xp)
                for elem in elems:
                    if not elem.is_displayed():
                        continue
                    # Walk up ancestors to find the largest text block
                    best = ""
                    for anc_xpath in ["./ancestor::div[%d]", "./ancestor::section[%d]"]:
                        for depth in [1, 2, 3, 4]:
                            try:
                                anc = elem.find_element(By.XPATH, anc_xpath % depth)
                                txt = normalize_space(anc.text)
                                if len(txt) > len(best):
                                    best = txt
                            except Exception:
                                pass
                    if best:
                        return best
            except Exception:
                continue

        body = self.driver.find_element(By.TAG_NAME, "body")
        return normalize_space(body.text)

    def scrape_detail_page(self) -> Dict[str, List[str]]:
        """Scrape tests from the current detail page using accordion, with text fallback."""
        self.close_popup_if_present(timeout=5)

        groups = self.collect_detail_via_accordion()
        if not groups:
            text_block = self._get_page_text_block()
            groups = self._parse_tests_from_text(text_block)
        return groups

    # ── main orchestrator ─────────────────────────────────────────────────

    @staticmethod
    def _build_detail_urls(name: str, mode: str) -> List[str]:
        """Return URLs to try in order: primary, then offer fallbacks."""
        primary = DETAIL_URL_PREFIX[mode] + quote(name.lower())
        slug = re.sub(r'[^a-z0-9]+', '-', name.lower()).strip('-')
        return [
            primary,
            "https://www.thyrocare.com/offer/" + quote(name.lower()),
            "https://www.thyrocare.com/offer/" + slug,
        ]

    def scrape_all(self, mode: str, limit: int = 0, output_path: Path = None) -> Dict:
        """
        1. Load card name/price list from cache (or scroll to build it).
        2. For each unscraped card, navigate directly to its profile URL and scrape.
        """
        card_list_path = output_path.parent / "card_list.json" if output_path else None

        # ── load/update shared card list cache ────────────────────────
        all_cards: List[Dict[str, str]] = []
        if card_list_path and card_list_path.exists():
            with open(card_list_path, "r", encoding="utf-8") as f:
                all_cards = json.load(f)

        existing_for_mode = [c for c in all_cards if c.get("mode") == mode]

        if existing_for_mode and limit == 0:
            card_info = existing_for_mode
            print(f"[INFO] Loaded {len(card_info)} {mode} cards from cache (skipping scroll)")
        elif limit > 0:
            print(f"[INFO] Limit set to {limit}, skipping full scroll.")
            card_info = self._collect_card_info_from_page()[:limit]
            for c in card_info:
                c["mode"] = mode
        else:
            print("[INFO] Scrolling to load all items...")
            card_info = self.scroll_and_collect_cards()
            for c in card_info:
                c["mode"] = mode

        # Merge into shared card list (replace entries for this mode)
        if card_list_path:
            other_cards = [c for c in all_cards if c.get("mode") != mode]
            all_cards = other_cards + card_info
            save_json(all_cards, card_list_path)

        print(f"[INFO] {len(card_info)} {mode} cards to process")

        # ── load existing results ─────────────────────────────────────
        results: Dict[str, Dict] = {}
        if output_path and output_path.exists():
            with open(output_path, "r", encoding="utf-8") as f:
                results = json.load(f)
            already = sum(1 for v in results.values() if v.get("mode") == mode)
            print(f"[INFO] Resuming — {already} {mode} already scraped ({len(results)} total)")

        # ── scrape each card ──────────────────────────────────────────
        for i, info in enumerate(card_info):
            name = info["name"]
            price = info["price"]
            tag = f"[{i+1}/{len(card_info)}]"

            if name in results:
                continue

            if mode == "single-test":
                url = DETAIL_URL_PREFIX[mode] + quote(name.lower())
                results[name] = {
                    "url": url,
                    "price": price,
                    "mode": mode,
                    "groups": None,
                }
                print(f"  {tag} {name} ({price})")
                if output_path:
                    save_json(results, output_path)
                continue

            urls = self._build_detail_urls(name, mode)
            print(f"  {tag} {name} ({price})")

            groups = {}
            url = urls[0]
            for try_url in urls:
                self.driver.get(try_url)
                time.sleep(2)
                self.ensure_human_verified(timeout=60)

                groups = self.scrape_detail_page()
                if groups:
                    url = try_url
                    break
                print(f"    -> 0 results at {try_url}, trying next...")

            cat_count = len(groups)
            test_count = sum(len(v) for v in groups.values())
            print(f"    -> {cat_count} catalogues, {test_count} tests ({url})")

            results[name] = {
                "url": url,
                "price": price,
                "mode": mode,
                "groups": groups,
            }
            if output_path:
                save_json(results, output_path)

        return results


def save_json(obj, path: Path):
    with open(path, "w", encoding="utf-8") as f:
        json.dump(obj, f, indent=2, ensure_ascii=False)


def run_scrape(mode: str, headless: bool, limit: int = 0):
    json_path = OUTPUT_DIR / "thyrocare_results.json"

    scraper = ThyrocareScraper(headless=headless, timeout=20)
    try:
        scraper.wait_for_user_ready(mode=mode)
        results = scraper.scrape_all(mode=mode, limit=limit, output_path=json_path)
        print(f"[INFO] Done! {len(results)} packages -> {json_path}")
    finally:
        scraper.close()


def main():
    parser = argparse.ArgumentParser()
    parser.add_argument("--mode", choices=["package", "single-test"], default="package")
    parser.add_argument("--headless", action="store_true")
    parser.add_argument("--limit", type=int, default=0, help="Limit cards to process (0 = all)")
    args = parser.parse_args()

    os.makedirs(PROFILE_DIR, exist_ok=True)
    run_scrape(mode=args.mode, headless=args.headless, limit=args.limit)
    os._exit(0)


if __name__ == "__main__":
    main()
