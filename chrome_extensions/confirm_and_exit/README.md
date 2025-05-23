# Confirm and Exit - Chrome Extension

> **Purpose:** Automate the final steps of ending an online lecture/meeting by
>
> 1. clicking the **“End Meeting”** button,
> 2. waiting for the confirmation dialog,
> 3. typing `confirm`, and
> 4. pressing the **“Submit / End”** button.
>
> This README is designed for a developer/agent who already built the **Auto‑Rate GG** extension and now needs a second, independent extension that follows the same architectural style (Manifest V3 + background service‑worker + injected content script).

---

## 1 . Folder / File Layout

```
confirm-and-exit/
├── manifest.json            # Extension metadata & permissions
├── background.js            # Service‑worker that injects the content script
├── inject.js                # Core automation (runs in the web page)
├── popup.html               # (optional) Minimal UI with an “End Meeting” button
├── popup.js                 # (optional) Injects `inject.js` from the popup
├── icons/
│   └── end.png              # 128×128 icon shown in Chrome toolbar
└── README.md                # <‑‑ this file
```

*You can rename the root folder however you like; “confirm-and-exit” is used throughout this document.*

---

## 2 . Quick‑Start (TL;DR for busy agents)

1. Clone / copy the folder above.
2. Replace **CSS selectors** inside `inject.js` with selectors that match *your* LMS / conferencing platform.
3. Visit `chrome://extensions` → **Load unpacked** → select the folder.
4. Open a meeting tab, click the extension’s toolbar icon.
   The script ends the meeting automatically.

---

## 3 . File‑by‑File Breakdown

### 3.1 manifest.json

```json
{
  "name": "Confirm and Exit",
  "description": "One‑click end meeting automation (click End, type ‘confirm’, submit).",
  "version": "1.0.0",
  "manifest_version": 3,
  "icons": {
    "128": "icons/end.png"
  },
  "permissions": ["scripting", "activeTab"],
  "host_permissions": [
    "https://example.com/*"          // ↔ Replace with the actual meeting domain
  ],
  "background": {
    "service_worker": "background.js"
  },
  "action": {
    "default_title": "Confirm and Exit",
    "default_icon": "icons/end.png"
  }
}
```

**Key points**

* **`scripting`** + **`activeTab`** → lets us inject `inject.js` into the current tab.
* **`host_permissions`** → list every domain that should allow injection (wildcards supported).
* The toolbar icon itself will trigger the action.

---

### 3.2 background.js (Service Worker)

```js
chrome.action.onClicked.addListener(async (tab) => {
  if (!tab.id) return;
  try {
    await chrome.scripting.executeScript({
      target: { tabId: tab.id },
      files: ["inject.js"],
    });
  } catch (err) {
    console.error("Confirm and Exit – injection failed", err);
  }
});
```

* Injects `inject.js` into whatever page is active when the icon is clicked.

---

### 3.3 inject.js (Content Script)

```js
/*
 * Confirm and Exit – content script
 * Replace SELECTOR_* constants below with real CSS selectors for your site.
 */

(async () => {
  //                                     ▼▼ REPLACE THESE ▼▼
  const SELECTOR_END_BUTTON   = "button.end-meeting";           // top‑level “End” button
  const SELECTOR_DIALOG       = "div.end-dialog";              // modal container (optional wait target)
  const SELECTOR_INPUT        = "input[name=\'confirm\']";        // text box inside dialog
  const SELECTOR_END_ALL      = 'something'; //a confirmation before entering text "confirm" and before clicking submit
  const SELECTOR_SUBMIT       = "button.submit-end";           // final “End / Submit” button
  const CONFIRM_TEXT          = "confirm";                     // text to type (lower‑case)
  const RETRIES               = 5;                             // how many times to retry each step
  const RETRY_DELAY_MS        = 500;                           // delay between retries

  /** Helper: retry an async fn up to N times */
  async function retry(fn, label) {
    for (let i = 0; i < RETRIES; i++) {
      const ok = await fn();
      if (ok) return true;
      await new Promise(r => setTimeout(r, RETRY_DELAY_MS));
    }
    console.warn(`Confirm and Exit: ${label} – element not found after ${RETRIES} attempts`);
    alert(`Confirm and Exit failed at step: ${label}. Check console for details.`);
    return false;
  }

  /** Step 1: click End Meeting */
  if (!(await retry(() => {
    const btn = document.querySelector(SELECTOR_END_BUTTON);
    if (!btn) return false;
    btn.click();
    return true;
  }, "End button"))) return;

  /** Optional: wait until dialog appears (helps slow UIs) */
  if (SELECTOR_DIALOG) {
    await retry(() => !!document.querySelector(SELECTOR_DIALOG), "dialog appearance");
  }

  /** Step 2: type ‘confirm’ */
  if (!(await retry(() => {
    const input = document.querySelector(SELECTOR_INPUT);
    if (!input) return false;
    input.focus();
    input.value = CONFIRM_TEXT;
    input.dispatchEvent(new Event("input", { bubbles: true }));
    return true;
  }, "confirm box"))) return;

  /** Step 3: click Submit */
  if (!(await retry(() => {
    const btn = document.querySelector(SELECTOR_SUBMIT);
    if (!btn) return false;
    btn.click();
    return true;
  }, "Submit button"))) return;

  console.log("Confirm and Exit ✔ finished");
})();
```

#### Editing Selectors

* **`SELECTOR_END_BUTTON`** – the initial red “End / Leave” button.
* **`SELECTOR_DIALOG`** – the modal’s outer container (optional but improves reliability).
* **`SELECTOR_INPUT`** – the textbox where the user must type `confirm`.
* **`SELECTOR_SUBMIT`** – the final confirmation button.

Use your browser’s DevTools → *Elements* tab to inspect the DOM and copy selectors.  Keep them as simple & stable as possible; avoid random hash‑based classes if the platform regenerates them per build.

---

### 3.4 popup.html & popup.js (No longer used)

These files are no longer used as the extension now triggers directly from the toolbar icon.

---

## 4 . Installation & Testing

1. **Load Unpacked**: `chrome://extensions` → enable **Developer mode** → **Load unpacked** → select the *confirm-and-exit* folder.
2. Ensure the extension shows up with your chosen icon.
3. Navigate to your conferencing platform, start a dummy meeting.
4. Click the extension icon.
5. Observe: the script should complete all three steps.  Open DevTools > Console for log messages if something fails.

---

## 5 . Required APIs / Libraries

* **Chrome Extension APIs** (built‑in):

  * `chrome.action` – detect icon clicks.
  * `chrome.scripting` – inject scripts.
  * `chrome.tabs` – (popup version) query active tab.
* **Standard Web APIs** only (DOM, Promises, `setTimeout`).
  **No** external dependencies (jQuery, React, etc.).

---

## 6 . Customisation Tips

| What you might change  | Where                                                                   | Notes                                                                   |
| ---------------------- | ----------------------------------------------------------------------- | ----------------------------------------------------------------------- |
| Extension name / icon  | `manifest.json`, `/icons`                                               | Update `name`, `description`, and supply up‑to‑date PNGs (16/48/128px). |
| Confirmation text      | `inject.js` → `CONFIRM_TEXT`                                            | Rarely changes, but some platforms capitalise it (`"Confirm"`).         |
| Retry timings          | `RETRIES`, `RETRY_DELAY_MS`                                             | Increase for slow networks.                                             |
| Target domains         | `manifest.json` → `host_permissions`                                    | Multiple entries allowed; wildcards (`*`) supported.                    |
| Popup vs. direct click | Remove/keep `popup.html` and `popup.js`; adjust `action.default_popup`. |                                                                         |

---

## 7 . Packaging for the Chrome Web Store

1. Increment `version` in `manifest.json` (semantic versioning recommended).
2. Zip the entire folder (exclude `README.md` if desired).
3. Upload via [https://chrome.google.com/webstore/devconsole](https://chrome.google.com/webstore/devconsole) → *Add New Item*.
4. Provide screenshots/GIF of the extension in action.  A 128×128 icon is required; 48×48 and 16×16 are highly recommended.

---

## 8 . Troubleshooting

| Symptom                                    | Likely Cause                                                             | Fix                                                                                                  |
| ------------------------------------------ | ------------------------------------------------------------------------ | ---------------------------------------------------------------------------------------------------- |
| “Failed to inject” error in console        | Host page not matched by `host_permissions`                              | Add correct domain or wildcard. Reload extension.                                                    |
| Script times out on a step                 | Wrong selector or UI element only appears after extra click              | Re‑check selectors, add explicit waits (see `retry(...)`).                                           |
| Nothing happens when icon clicked          | Popup exists but `popup.js` missing logic                                | Implement popup injection or remove popup.                                                           |
| Confirmation typed but meeting doesn’t end | Submit button selector incorrect, or platform blocks programmatic clicks | Check DevTools event listeners; some frameworks require \[`pointerdown`, `pointerup`]—simulate both. |

---

## 9 . Future Enhancements (Optional Roadmap)

* **Multi‑platform support** – Detect domain → load per‑site selector map.
* **Options page** – Let users edit selectors and confirmation text without touching code.
* **Internationalisation** – Auto‑detect locale: `confirm`, `確認`, `确认`, …
* **Metrics** – Track success/failure rates using `chrome.storage`.
* **Hotkey** – Add a command in `manifest.json` → users can press a keyboard shortcut instead of clicking.

---

## 10 . Change‑Log

| Version | Date (YYYY‑MM‑DD) | Notes                                               |
| ------- | ----------------- | --------------------------------------------------- |
| 1.0.0   | 2025‑05‑13        | Initial release – parity with Auto‑Rate GG pattern. |

---

### © 2025 \<Your Name / Org>

Licensed under the MIT License (include `LICENSE` file if you decide to open‑source).
