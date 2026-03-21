# Thyrocare Scraper & Package Finder

## Overview

Two parts:
1. **Scraper** (`selenium_scraper.py`) — scrapes all packages and single tests from thyrocare.com
2. **Web app** (`app.py`) — Flask app that helps users find the best package(s) for the tests they need

## Repo Structure

```
selenium_scraper.py          # Scraper — collects all package/test data
app.py                       # Flask web app — package finder
templates/
  index.html                 # Single-page frontend (vanilla JS, no build step)
thyrocare_output/
  card_list.json             # Cached card names/prices/mode (no URLs — constructed at runtime)
  thyrocare_results.json     # All scraped results — packages and tests together
selenium_profile/            # Persistent Chrome profile (Cloudflare cookies survive restarts)
chrome-mac-arm64/            # Chrome binary
chromedriver-mac-arm64/      # ChromeDriver binary
procedure.md                 # This file
```

---

## Part 1: Scraper

### The Website

- **thyrocare.com/book-a-test** is a Next.js SPA
- Lazy-loads cards on scroll — not all in the DOM at once
- Cards have **no `<a href>` links** — JS click handlers only
- Behind **Cloudflare** protection
- **Two sections** with different URL patterns:
  - Packages: listed at `?type=package`, detail pages at `/profile/{name}`
  - Single Tests: listed at `?type=single-test`, detail pages at `/test/{name}`
  - Some items live under `/offer/` with dashes instead of `%20`

### How It Works

**Phase 1: Discover cards (one-time per mode)**
- Opens listing page with correct `?type=` filter
- Scrolls to bottom repeatedly to lazy-load all cards
- Collects `name` + `price` from each `TestCard__ProductCard` element
- Saves to `card_list.json` with `mode` field

**Phase 2: Scrape detail pages (packages only, resumable)**
- Loads `card_list.json`, filters by current mode
- Loads `thyrocare_results.json`, skips already-scraped entries
- For packages: constructs URL from name, navigates directly, scrapes accordion (`#testsAccordian`)
- Tries fallback URLs if primary returns 0 results: `/profile/` -> `/offer/%20` -> `/offer/-`
- Saves after every card for crash resilience
- For single tests: no detail page visit needed — just records name/price/mode with `groups: null`

### How to Run the Scraper

```bash
# Scrape all packages
python selenium_scraper.py --mode package

# Scrape all single tests (fast — no page visits, just saves from card list)
python selenium_scraper.py --mode single-test

# Test with a few cards
python selenium_scraper.py --mode package --limit 5
```

1. Browser opens to the correct listing page
2. Pass Cloudflare if prompted, close popups
3. Press ENTER in the terminal
4. Scraper runs — saves after each card
5. Process exits cleanly when done

### Key Scraper Decisions

| Challenge | Solution |
|---|---|
| No URLs on cards | URL pattern discovered: `/profile/{name.lower() url-encoded}`. Constructed at runtime, not stored. |
| Lazy-loaded infinite scroll | Scroll once in Phase 1, cache card list. Phase 2 navigates directly to URLs. |
| SPA breaks `driver.back()` | Never use `back()` — each card is a direct `driver.get(url)`. |
| Cloudflare | Persistent Chrome profile + manual verification on start + auto-detect during scrape. |
| `/offer/` URLs | `_build_detail_urls()` tries 3 URL variants until one returns data. |
| Crash resilience | `thyrocare_results.json` saved after every single card. Skip check: `name in results`. |
| ChromeDriver hangs on exit | `close()` wrapped in try/except + `os._exit(0)` at end. |

### Data Format

**`thyrocare_results.json`** — single file, both modes:
```json
{
  "HEALTHY 2026 PACKAGE": {
    "url": "https://www.thyrocare.com/profile/healthy%202026%20package",
    "price": "₹2026",
    "mode": "package",
    "groups": {
      "Cardiac Risk Markers(Includes 5 tests)": ["Apo b / apo a1 ratio (apo b/a1)", "..."],
      "Complete Hemogram(Includes 28 tests)": ["Basophils - absolute count", "..."]
    }
  },
  "BETA HCG": {
    "url": "https://www.thyrocare.com/test/beta%20hcg",
    "price": "₹450",
    "mode": "single-test",
    "groups": null
  }
}
```

**`card_list.json`** — no URLs, just discovery cache:
```json
[
  {"name": "HEALTHY 2026 PACKAGE", "price": "₹2026", "mode": "package"},
  {"name": "BETA HCG", "price": "₹450", "mode": "single-test"}
]
```

### Making Changes to the Scraper

- **URL pattern changed?** Edit `_build_detail_urls()` and `DETAIL_URL_PREFIX` in `selenium_scraper.py`
- **Card selector changed?** Update `CARD_SELECTOR` (currently `[class*='TestCard__ProductCard']`)
- **Accordion structure changed?** Edit `collect_detail_via_accordion()` — looks for `#testsAccordian`, clicks buttons, reads panels
- **New URL variant?** Add to the list in `_build_detail_urls()`
- **Re-scrape a specific card?** Delete its entry from `thyrocare_results.json` and re-run

---

## Part 2: Flask Web App (Package Finder)

### What It Does

Three features, each in its own tab:

1. **Find Packages** — select tests you need, find the best single package or cheapest combination
2. **Compare Packages** — pick 2+ packages side-by-side, see shared tests, unique tests per package, and price difference per extra test
3. **Break Down Package** — select an expensive package, find out if buying smaller packages/single tests is cheaper. Shows savings amount + percentage, or confirms the big package is the best deal

### How to Run

```bash
python app.py
# Opens at http://localhost:5050
```

### App Architecture

**Backend (`app.py`):**
- Loads `thyrocare_results.json` on startup, builds a reverse index: `test_name -> [packages]`
- Endpoints:
  - `GET /` — serves the single-page HTML
  - `GET /api/tests?q=chol` — autocomplete for test names
  - `GET /api/packages?q=aarog` — autocomplete for package names (used by compare/breakdown)
  - `POST /api/search` — `{"tests": [...]}` -> ranked packages + optimal combo
  - `POST /api/compare` — `{"packages": ["PKG A", "PKG B"]}` -> shared/unique tests, price diffs
  - `POST /api/breakdown` — `{"package": "PKG NAME"}` -> cheaper alternative breakdown

**Frontend (`templates/index.html`):**
- Single HTML file, no build step, no dependencies
- Vanilla JS with reusable autocomplete (keyboard nav: arrows + enter)
- Tab-based UI: Find / Compare / Break Down
- Tag-based selection for tests and packages
- Color-coded test chips: green (covered), red (missing), blue (shared), yellow (exclusive)
- Coverage bars, expandable test lists, links to Thyrocare
- Dark theme (GitHub-dark inspired color palette)

### Key Algorithms

**`find_best_packages()`** — ranks all packages by:
1. Coverage % (how many wanted tests it includes)
2. Price (cheaper first among same coverage)
3. Also computes price-per-matched-test

**`find_combo()`** — greedy set cover:
1. Find the package covering the most remaining wanted tests (tie-break: cheapest)
2. Remove those tests from remaining set
3. Repeat until all covered or no more packages help

**`compare_packages()`** — for 2+ packages:
1. Computes set intersection (shared tests) and set difference (unique per package)
2. Sorts by price, shows price difference and extra unique tests vs the cheapest option

**`breakdown_package()`** — can a big package be replaced cheaper?

Two-phase approach, always staying within the original package's price as budget:

1. **Phase 1 — Packages:** Greedily adds smaller packages (most coverage first, tie-break cheapest). Stops when adding another package would exceed the original price. Each package shows how many of the original's tests it covers and a running total.
2. **Phase 2 — Single tests:** Fills remaining gaps with individual single tests (cheapest first), still within budget.
3. If total < original price -> shows savings amount and %. If some tests can't fit within budget -> shows what's missing and why. If no savings possible -> confirms the original is the best deal.

### Making Changes to the Web App

- **Change ranking logic?** Edit `find_best_packages()` in `app.py` — the `scored.sort()` key
- **Change combo algorithm?** Edit `find_combo()` — currently greedy, could swap for exact optimization
- **Change breakdown strategy?** Edit `breakdown_package()` — currently greedy by cost-per-test ratio
- **Change compare logic?** Edit `compare_packages()` in `app.py`
- **Add filters (price range, max packages)?** Add params to the relevant `/api/` endpoint, pass from frontend
- **Change styling/theme?** All CSS is inline in `templates/index.html` `<style>` block. Current theme uses GitHub-dark colors (`#0d1117` bg, `#161b22` cards, `#58a6ff` accent, `#3fb950` prices, `#f85149` warnings)
- **Add a new tab?** Add a `tab-panel` div in HTML, a `switchTab()` case, and a new `/api/` endpoint
- **Data refresh?** Re-run the scraper, restart the Flask app (data loads on startup)

### Dependencies

```
flask
selenium
truststore
```
