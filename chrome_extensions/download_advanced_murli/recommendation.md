# Chrome Extension Improvement Recommendations

Based on a review of the project files, here are some areas for potential improvement and increased efficiency:

## 1. Unify Trigger Mechanism & UI Flow

*   **Issue:** The extension currently has conflicting triggers. `service_worker.js` listens for the action icon click (`chrome.action.onClicked`), while `popup.js` tries to initiate the download via a message (`chrome.runtime.sendMessage`). This leads to confusion about how the extension is intended to be used and bypasses the popup UI.
*   **Recommendation:**
    *   Choose one primary trigger. Using the popup (`popup.html` and `popup.js`) is generally better for providing user feedback before starting.
    *   Remove the `chrome.action.onClicked` listener in `service_worker.js`.
    *   Update `manifest.json` to declare the popup: `"action": { "default_popup": "popup.html" }`.
    *   Ensure `service_worker.js` properly listens for and handles the `download-latest` message from `popup.js`.

## 2. Improve Scraping Robustness

*   **Issue:** The functions injected by `chrome.scripting.executeScript` in `service_worker.js` rely on specific CSS selectors (`a[href*="advance-murli-"]`, `a[href$=".pdf"]`, filtering by `/hindi/i`). These are brittle and likely to break if the target website (`shivbabas.org`) changes its HTML structure, class names, or URL patterns. The unused `src/utils/scraper.js` contains potentially more robust logic (like date sorting for the latest month) but attempts to fetch/parse directly in the service worker (which won't work due to CORS).
*   **Recommendation:**
    *   Make selectors less dependent on exact attributes that might change. Consider selecting based on text content, ARIA attributes, or more stable structural relationships.
    *   Adapt the more robust logic from `src/utils/scraper.js` (e.g., date sorting) to be injectable functions used with `chrome.scripting.executeScript`.
    *   Remove the unused `src/utils/scraper.js` file once any useful logic is migrated.

## 3. Eliminate Hardcoded Delays (`setTimeout`)

*   **Issue:** `service_worker.js` uses `await new Promise(resolve => setTimeout(resolve, ...))` multiple times (e.g., 2500ms, 2000ms) to wait for tab navigation and page loading. This is unreliable as load times vary.
*   **Recommendation:** Replace these waits with more deterministic methods:
    *   **Wait for Tab Updates:** Use the `chrome.tabs.onUpdated` listener. Filter for the specific `tabId` and `status === 'complete'`.
    *   **Wait After Navigation:** After calling `chrome.tabs.update`, you can inject a simple script into the updated tab that waits for `document.readyState === 'complete'` (or for a specific element to exist) and sends a message back to the service worker when the page is ready.

## 4. Enhance Error Handling & User Feedback

*   **Issue:** While `service_worker.js` has `try...catch` blocks, errors aren't effectively communicated back to the user via the popup. Progress reporting seems to have been removed or commented out.
*   **Recommendation:**
    *   When handling messages from the popup in the service worker, use the `sendResponse` callback to send back success status or detailed error messages.
    *   Update `popup.js` to display these errors clearly to the user (e.g., in the `status` element).
    *   Reinstate progress reporting. Send `progress` messages from the service worker during long operations (finding links, downloading, merging) and update the progress bar and status text in `popup.js` accordingly.

## 5. Optimize PDF Downloading

*   **Issue:** PDFs are downloaded sequentially using `await fetch` in a loop.
*   **Recommendation:** For potentially faster downloads, consider fetching PDFs in parallel using `Promise.allSettled` on an array of fetch promises. This allows multiple downloads to happen concurrently. Be mindful of potential rate limiting by the server – if issues arise, limit concurrency (e.g., download in batches of 3-5). `Promise.allSettled` is useful here to handle individual download failures gracefully.

## 6. Review PDF Download Method (Data URL)

*   **Issue:** The merged PDF is converted to a Base64 Data URL for download via `chrome.downloads.download`. While reliable in service workers, this is memory-inefficient (Base64 is ~33% larger than binary) and has browser-dependent size limits (can fail for very large merged PDFs).
*   **Recommendation:**
    *   **If PDFs are small:** Keep the current method for simplicity.
    *   **If PDFs can be large:**
        *   *Option 1 (Requires careful lifecycle management):* Create a `Blob` from the `mergedBytes` (`new Blob([mergedBytes], { type: 'application/pdf' })`) and use `URL.createObjectURL()` to get a blob URL for the `chrome.downloads.download` API. Object URLs have short lifecycles in service workers, so ensure the download starts before the URL is revoked.
        *   *Option 2 (Experimental, Chrome 121+):* Investigate using the `body` parameter of `chrome.downloads.download({ ..., body: mergedBytes.buffer })` if your target users have up-to-date browsers.

## 7. Refine Permissions

*   **Issue:** The `manifest.json` includes host permissions for `https://babamurli.net/*` and `https://drive.google.com/*`. The current code doesn't seem to use these URLs. The `tabs` permission is broad.
*   **Recommendation:**
    *   Remove unused host permissions (`babamurli.net`, `drive.google.com`) to follow the principle of least privilege.
    *   Evaluate if the `tabs` permission is strictly necessary. If the workflow only ever modifies the *active* tab after user interaction (like clicking the popup), the `activeTab` permission might suffice. However, the current logic involves navigating tabs, so `tabs` might still be required unless the workflow changes significantly.

## 8. Bundle `popup.js`

*   **Issue:** `rollup.config.js` only bundles `service_worker.js`. `popup.js` is used unbundled.
*   **Recommendation:** Add `src/popup.js` as another input to the Rollup configuration. This allows using modules in `popup.js` if needed later and keeps the build process consistent. You'll need to adjust the Rollup config to handle multiple inputs/outputs.

## 9. Code Cleanup

*   **Issue:** There's commented-out code (like the old `onMessage` listener in `service_worker.js`) and an apparently unused file (`src/utils/scraper.js`).
*   **Recommendation:** Remove dead/commented-out code and unused files to improve maintainability.

## 10. Update Dependencies

*   **Issue:** Dependencies in `package.json` might have newer versions available with bug fixes or improvements.
*   **Recommendation:** Run `npm outdated` to check for updates to `pdf-lib`, `rollup`, and Rollup plugins. Consider updating to newer stable versions after testing compatibility.
