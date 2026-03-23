import json
import re
from pathlib import Path

from flask import Flask, render_template, request, jsonify

app = Flask(__name__)

DATA_PATH = Path(__file__).parent / "thyrocare_output" / "thyrocare_results.json"


def load_data():
    with open(DATA_PATH, "r", encoding="utf-8") as f:
        return json.load(f)


def parse_price(price_str: str) -> int:
    return int(re.sub(r"[^\d]", "", price_str) or 0)


def build_index(data: dict):
    """Build a reverse index: test_name -> list of packages containing it."""
    test_to_packages = {}
    all_test_names = set()

    for pkg_name, info in data.items():
        if info.get("mode") != "package" or not info.get("groups"):
            continue
        for group, tests in info["groups"].items():
            for test in tests:
                key = test.lower().strip()
                all_test_names.add(key)
                if key not in test_to_packages:
                    test_to_packages[key] = []
                test_to_packages[key].append(pkg_name)

    return test_to_packages, sorted(all_test_names)


DATA = load_data()
TEST_TO_PACKAGES, ALL_TEST_NAMES = build_index(DATA)

# Build display-name mapping (lowercase -> original case)
TEST_DISPLAY = {}
for info in DATA.values():
    if not info.get("groups"):
        continue
    for tests in info["groups"].values():
        for t in tests:
            TEST_DISPLAY[t.lower().strip()] = t


def find_best_packages(wanted_tests: list[str], max_results: int = 20):
    """Find packages that cover the most wanted tests, ranked by value."""
    wanted = {t.lower().strip() for t in wanted_tests if t.strip()}
    if not wanted:
        return []

    scored = []
    for pkg_name, info in DATA.items():
        if info.get("mode") != "package" or not info.get("groups"):
            continue

        pkg_tests = set()
        for tests in info["groups"].values():
            for t in tests:
                pkg_tests.add(t.lower().strip())

        covered = wanted & pkg_tests
        if not covered:
            continue

        total_tests = sum(len(v) for v in info["groups"].values())
        price = parse_price(info["price"])
        coverage_pct = len(covered) / len(wanted) * 100

        scored.append({
            "name": pkg_name,
            "price": info["price"],
            "price_num": price,
            "url": info.get("url", ""),
            "total_tests": total_tests,
            "covered": sorted(TEST_DISPLAY.get(t, t) for t in covered),
            "covered_count": len(covered),
            "missing": sorted(TEST_DISPLAY.get(t, t) for t in (wanted - covered)),
            "missing_count": len(wanted - covered),
            "coverage_pct": round(coverage_pct, 1),
            "price_per_covered": round(price / len(covered), 1) if covered else 9999,
        })

    # Sort: most coverage first, then cheapest
    scored.sort(key=lambda x: (-x["coverage_pct"], x["price_num"]))
    return scored[:max_results]


def find_combo(wanted_tests: list[str], budget: int = 0):
    """Find cheapest combination of packages + single tests to cover all wanted tests.

    Evaluates multiple strategies and returns the cheapest fully-covering solution:
      A) All single tests
      B) Each relevant package + singles for remainder
      C) Each pair of relevant packages + singles for remainder
      D) Greedy by cost-effectiveness (price / covered count) + singles
    """
    wanted = {t.lower().strip() for t in wanted_tests if t.strip()}
    if not wanted:
        return {}

    # --- Build candidates ---
    # Relevant packages: those covering at least 1 wanted test
    pkg_candidates = []
    for pkg_name, info in DATA.items():
        if info.get("mode") != "package" or not info.get("groups"):
            continue
        pkg_tests = set()
        for tests in info["groups"].values():
            for t in tests:
                pkg_tests.add(t.lower().strip())
        covered = wanted & pkg_tests
        if covered:
            price = parse_price(info["price"])
            pkg_candidates.append((pkg_name, info, price, covered))

    # Single test lookup: wanted test key -> (name, info, price)
    single_test_map = {}
    for pkg_name, info in DATA.items():
        if info.get("mode") != "single-test":
            continue
        test_key = pkg_name.lower().strip()
        if test_key in wanted:
            single_test_map[test_key] = (pkg_name, info, parse_price(info["price"]))

    def _fill_singles(remaining):
        """Fill remaining tests with singles. Returns (items, cost, missing)."""
        items, cost, missing = [], 0, set()
        for t in sorted(remaining):
            if t in single_test_map:
                sname, sinfo, sprice = single_test_map[t]
                items.append({
                    "name": sname, "price": sinfo["price"], "price_num": sprice,
                    "url": sinfo.get("url", ""),
                    "covers": [TEST_DISPLAY.get(t, sname)], "mode": "single-test",
                })
                cost += sprice
            else:
                missing.add(t)
        return items, cost, missing

    def _make_result(pkg_items, singles, total, missing):
        return {
            "packages": pkg_items,
            "single_tests": singles,
            "total_price": total,
            "total_price_display": f"₹{total}",
            "fully_covered": len(missing) == 0,
            "missing": sorted(TEST_DISPLAY.get(t, t) for t in missing),
        }

    def _pkg_item(name, info, price, covers):
        return {
            "name": name, "price": info["price"], "price_num": price,
            "url": info.get("url", ""),
            "covers": sorted(TEST_DISPLAY.get(t, t) for t in covers),
            "mode": "package",
        }

    best_price = float("inf")
    best_missing = float("inf")
    best_result = None

    def _consider(total, missing_count, result):
        nonlocal best_price, best_missing, best_result
        # Prefer fewer missing tests, then cheaper total
        if (missing_count, total) < (best_missing, best_price):
            best_price = total
            best_missing = missing_count
            best_result = result

    # Strategy A: All singles
    singles, cost, missing = _fill_singles(wanted)
    _consider(cost, len(missing), _make_result([], singles, cost, missing))

    # Strategy B: Each single package + singles for remainder
    for pkg_name, info, price, covered in pkg_candidates:
        if price >= best_price and best_missing == 0:
            continue  # Package alone already exceeds best known price
        remaining = wanted - covered
        singles, scost, missing = _fill_singles(remaining)
        total = price + scost
        _consider(total, len(missing),
                  _make_result([_pkg_item(pkg_name, info, price, covered)],
                               singles, total, missing))

    # Strategy C: Pairs of packages + singles for remainder
    # Only consider pairs that cover more wanted tests together than either alone
    pkg_candidates.sort(key=lambda x: x[2])  # sort by price for pruning
    for i in range(len(pkg_candidates)):
        name1, info1, price1, cov1 = pkg_candidates[i]
        if price1 >= best_price and best_missing == 0:
            break  # All remaining pairs will be even more expensive
        for j in range(i + 1, len(pkg_candidates)):
            name2, info2, price2, cov2 = pkg_candidates[j]
            if price1 + price2 >= best_price and best_missing == 0:
                break  # Rest of inner loop will be more expensive
            combined = cov1 | cov2
            if len(combined) <= max(len(cov1), len(cov2)):
                continue  # Pair doesn't cover more than better individual
            remaining = wanted - combined
            singles, scost, missing = _fill_singles(remaining)
            total = price1 + price2 + scost
            # Display: assign covers without overlap
            pkg1_covers = cov1
            pkg2_covers = cov2 - cov1
            _consider(total, len(missing),
                      _make_result([_pkg_item(name1, info1, price1, pkg1_covers),
                                    _pkg_item(name2, info2, price2, pkg2_covers)],
                                   singles, total, missing))

    # Strategy D: Greedy by cost-effectiveness (price / covered count) + singles
    remaining = set(wanted)
    greedy_pkgs = []
    used = set()
    while remaining:
        best = None
        best_score = float("inf")
        best_covered = set()
        for pkg_name, info, price, covered_wanted in pkg_candidates:
            if pkg_name in used:
                continue
            covered = remaining & covered_wanted
            if not covered:
                continue
            score = price / len(covered)
            if score < best_score or (score == best_score and price < (best[2] if best else float("inf"))):
                best = (pkg_name, info, price)
                best_score = score
                best_covered = covered
        if not best:
            break
        pkg_name, info, price = best
        remaining -= best_covered
        used.add(pkg_name)
        greedy_pkgs.append(_pkg_item(pkg_name, info, price, best_covered))

    singles, scost, missing = _fill_singles(remaining)
    total = sum(p["price_num"] for p in greedy_pkgs) + scost
    _consider(total, len(missing), _make_result(greedy_pkgs, singles, total, missing))

    return best_result or _make_result([], [], 0, wanted)


def _get_pkg_tests(name: str) -> set:
    """Get all test names (lowercase) for a package."""
    info = DATA.get(name, {})
    tests = set()
    for group_tests in (info.get("groups") or {}).values():
        for t in group_tests:
            tests.add(t.lower().strip())
    return tests


def compare_packages(names: list[str]):
    """Compare 2+ packages: shared tests, unique tests, price difference."""
    if len(names) < 2:
        return {}

    pkgs = []
    for name in names:
        info = DATA.get(name)
        if not info or not info.get("groups"):
            continue
        pkgs.append({
            "name": name,
            "price": info["price"],
            "price_num": parse_price(info["price"]),
            "url": info.get("url", ""),
            "tests": _get_pkg_tests(name),
        })

    if len(pkgs) < 2:
        return {}

    # Tests in ALL packages
    shared = set.intersection(*(p["tests"] for p in pkgs))
    # Tests unique to each package
    all_tests = set.union(*(p["tests"] for p in pkgs))

    result_pkgs = []
    for p in pkgs:
        unique = p["tests"] - set.union(*(o["tests"] for o in pkgs if o["name"] != p["name"]))
        result_pkgs.append({
            "name": p["name"],
            "price": p["price"],
            "price_num": p["price_num"],
            "url": p["url"],
            "total_tests": len(p["tests"]),
            "unique_tests": sorted(TEST_DISPLAY.get(t, t) for t in unique),
            "unique_count": len(unique),
        })

    # Sort cheapest first
    result_pkgs.sort(key=lambda x: x["price_num"])
    cheapest = result_pkgs[0]
    for p in result_pkgs:
        p["price_diff"] = p["price_num"] - cheapest["price_num"]
        p["extra_tests_vs_cheapest"] = len(p["unique_tests"])

    return {
        "packages": result_pkgs,
        "shared_tests": sorted(TEST_DISPLAY.get(t, t) for t in shared),
        "shared_count": len(shared),
        "total_unique_tests": len(all_tests),
    }


def breakdown_package(name: str):
    """Find if a package can be replaced by cheaper smaller packages/single tests.

    Strategy:
    1. Add packages greedily (most coverage first), staying under original price.
    2. Once no more packages fit the budget, fill remaining gaps with single tests.
    3. If total still exceeds original price, report no savings.
    """
    info = DATA.get(name)
    if not info or not info.get("groups"):
        return {}

    target_tests = _get_pkg_tests(name)
    target_price = parse_price(info["price"])

    if not target_tests:
        return {}

    # Build candidate packages (cheaper than original, overlap with target)
    pkg_candidates = []
    single_test_candidates = []

    for pkg_name, pkg_info in DATA.items():
        if pkg_name == name:
            continue
        price = parse_price(pkg_info["price"])
        if price >= target_price:
            continue

        if pkg_info.get("mode") == "package" and pkg_info.get("groups"):
            pkg_tests = set()
            for tests in pkg_info["groups"].values():
                for t in tests:
                    pkg_tests.add(t.lower().strip())
            if target_tests & pkg_tests:
                pkg_candidates.append((pkg_name, pkg_info, price, pkg_tests))

        elif pkg_info.get("mode") == "single-test":
            test_key = pkg_name.lower().strip()
            if test_key in target_tests:
                single_test_candidates.append((pkg_name, pkg_info, price, {test_key}))

    # Phase 1: Add packages greedily, staying under budget
    remaining = set(target_tests)
    combo = []
    spent = 0
    used_names = set()

    while remaining:
        best = None
        best_covered = set()
        best_price = 0

        for pkg_name, pkg_info, price, pkg_tests in pkg_candidates:
            if pkg_name in used_names:
                continue
            if spent + price >= target_price:
                continue  # Would bust the budget

            covered = remaining & pkg_tests
            if not covered:
                continue

            # Pick the one that covers the most tests (tie-break: cheapest)
            if (len(covered) > len(best_covered)) or (
                len(covered) == len(best_covered) and price < best_price
            ):
                best = (pkg_name, pkg_info, price)
                best_covered = covered
                best_price = price

        if not best:
            break

        pkg_name, pkg_info, price = best
        remaining -= best_covered
        spent += price
        used_names.add(pkg_name)
        combo.append({
            "name": pkg_name,
            "price": pkg_info["price"],
            "price_num": price,
            "url": pkg_info.get("url", ""),
            "mode": "package",
            "covers": sorted(TEST_DISPLAY.get(t, t) for t in best_covered),
            "covers_count": len(best_covered),
        })

    # Phase 2: Fill remaining gaps with single tests (within budget)
    single_tests_added = []
    if remaining:
        # Sort single tests by price
        single_test_candidates.sort(key=lambda x: x[2])

        for st_name, st_info, st_price, st_tests in single_test_candidates:
            gap = remaining & st_tests
            if not gap:
                continue
            if spent + st_price >= target_price:
                continue  # Would bust the budget

            spent += st_price
            remaining -= gap
            single_tests_added.append({
                "name": st_name,
                "price": st_info["price"],
                "price_num": st_price,
                "url": st_info.get("url", ""),
                "mode": "single-test",
                "covers": sorted(TEST_DISPLAY.get(t, t) for t in gap),
                "covers_count": len(gap),
            })

    all_items = combo + single_tests_added
    total_price = sum(c["price_num"] for c in all_items)
    covered_count = sum(c["covers_count"] for c in all_items)
    missing = sorted(TEST_DISPLAY.get(t, t) for t in remaining)

    return {
        "original": {
            "name": name,
            "price": info["price"],
            "price_num": target_price,
            "total_tests": len(target_tests),
        },
        "breakdown_packages": combo,
        "breakdown_single_tests": single_tests_added,
        "breakdown": all_items,
        "breakdown_price": total_price,
        "breakdown_price_display": f"₹{total_price}",
        "savings": target_price - total_price,
        "savings_display": f"₹{target_price - total_price}",
        "savings_pct": round((target_price - total_price) / target_price * 100, 1) if target_price else 0,
        "is_cheaper": total_price < target_price,
        "fully_covered": len(remaining) == 0,
        "covered_count": covered_count,
        "missing": missing,
        "missing_count": len(remaining),
    }


def find_single_tests(wanted_tests: list[str]):
    """Find individual single tests available for wanted test names."""
    wanted = {t.lower().strip() for t in wanted_tests if t.strip()}
    results = []
    for pkg_name, info in DATA.items():
        if info.get("mode") != "single-test":
            continue
        test_key = pkg_name.lower().strip()
        if test_key in wanted:
            results.append({
                "name": pkg_name,
                "price": info["price"],
                "price_num": parse_price(info["price"]),
                "url": info.get("url", ""),
            })
    results.sort(key=lambda x: x["price_num"])
    total = sum(r["price_num"] for r in results)
    covered = {r["name"].lower().strip() for r in results}
    missing = sorted(TEST_DISPLAY.get(t, t) for t in wanted if t not in covered)
    return {
        "tests": results,
        "total": total,
        "total_display": f"₹{total}",
        "covers_count": len(results),
        "wanted_count": len(wanted),
        "missing": missing,
    }


@app.route("/")
def index():
    return render_template("index.html")


@app.route("/api/tests")
def api_tests():
    """Autocomplete endpoint — return test names matching query."""
    q = (request.args.get("q") or "").lower().strip()
    if len(q) < 2:
        return jsonify([])
    matches = [TEST_DISPLAY.get(t, t) for t in ALL_TEST_NAMES if q in t]
    matches.sort(key=lambda x: (not x.lower().startswith(q), len(x)))
    return jsonify(matches[:30])


@app.route("/api/packages")
def api_packages():
    """Search packages by name for compare/breakdown."""
    q = (request.args.get("q") or "").lower().strip()
    if len(q) < 2:
        return jsonify([])
    matches = []
    for name, info in DATA.items():
        if info.get("mode") != "package" or not info.get("groups"):
            continue
        if q in name.lower():
            total = sum(len(v) for v in info["groups"].values())
            matches.append({"name": name, "price": info["price"], "total_tests": total})
    matches.sort(key=lambda x: (not x["name"].lower().startswith(q), x["name"]))
    return jsonify(matches[:20])


@app.route("/api/search", methods=["POST"])
def api_search():
    body = request.get_json(force=True)
    wanted = body.get("tests", [])
    results = find_best_packages(wanted)
    combo = find_combo(wanted)
    singles = find_single_tests(wanted)
    return jsonify({"packages": results, "combo": combo, "singles": singles})


@app.route("/api/compare", methods=["POST"])
def api_compare():
    body = request.get_json(force=True)
    names = body.get("packages", [])
    return jsonify(compare_packages(names))


@app.route("/api/breakdown", methods=["POST"])
def api_breakdown():
    body = request.get_json(force=True)
    name = body.get("package", "")
    return jsonify(breakdown_package(name))


if __name__ == "__main__":
    app.run(debug=True, port=5050)
