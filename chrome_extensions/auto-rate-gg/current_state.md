<!-- filepath: /Users/shivam13juna/Documents/learn/learn_stuff/chrome_extensions/auto-rate-gg/current_state.md -->
# Chrome Extension: Auto-Rate GG - Current State

This document outlines the files, functionality, and workings of the "Auto-Rate GG" Chrome extension.

## Files in the Project:

*   **`manifest.json`**:
    *   **Functionality**: The core configuration file for the extension. It defines metadata (name: "Auto-Rate GG", description: "One-click 5★ rating + GG message.", version: "1.0.0"), icons (`feedback.png`), permissions (`scripting`, `activeTab`), and background scripts. It uses `manifest_version: 3`. It also specifies `host_permissions` for `"https://example.com/*"`, which means the extension intends to run on pages under that domain. The `action` key defines the browser action (toolbar icon).
    *   **How it works**: Chrome reads this to understand the extension's requirements and capabilities.
    *   **Libraries/Packages**: Chrome extension framework.

*   **`background.js`**:
    *   **Functionality**: A service worker script that runs in the background. It listens for clicks on the extension's toolbar icon (`chrome.action.onClicked`).
    *   **How it works**: Upon an icon click, it executes `inject.js` into the currently active tab.
    *   **Libraries/Packages**: `chrome.action`, `chrome.scripting` (Chrome Extension APIs).

*   **`inject.js`**:
    *   **Functionality**: A content script injected into web pages to automate rating. It contains CSS selectors to identify UI elements for a specific website (these selectors are user-configurable by editing the script).
    *   **How it works**:
        1.  **Test Setup (Optional)**: Attempts to click through a series of UI elements to simulate joining a test session (mic, video, join button). Includes warnings if elements are not found.
        2.  **Open Rating UI**: Clicks a button to open the feedback/rating interface (retries up to 3 times).
        3.  **Rate**: Clicks a 5-star rating element.
        4.  **Comment**: Enters "GG" into a designated textbox.
        5.  **Submit**: Clicks a submit button.
        6.  **Close Confirmation**: Clicks a button to close any confirmation popups.
        7.  Uses `console.warn` for logging issues and `alert` if critical elements for rating are missing.
    *   **Libraries/Packages**: Standard browser DOM manipulation (e.g., `document.querySelector`, `.click()`, `.focus()`, `.value`), `Promise`, `setTimeout`. No external libraries.

*   **`popup.html`**:
    *   **Functionality**: Defines the HTML structure for a popup window that can be triggered by the extension. Contains a "Rate 5★" button.
    *   **How it works**: Provides a simple UI with a button. Includes basic inline CSS for styling.
    *   **Libraries/Packages**: Standard HTML.

*   **`popup.js`**:
    *   **Functionality**: Provides the JavaScript logic for `popup.html`.
    *   **How it works**: Listens for a click on the "rate" button in `popup.html`. When clicked, it gets the active tab and injects `inject.js` into it. It then closes the popup.
    *   **Libraries/Packages**: `chrome.tabs`, `chrome.scripting` (Chrome Extension APIs).

*   **`README.md`**:
    *   **Functionality**: Standard project documentation file.
    *   **Content**: Likely contains general information about the extension.

*   **`current_state.md`**:
    *   **Functionality**: This file, intended to document the current state of the extension.

*   **`feedback.png`**:
    *   **Functionality**: Image file used as the extension's icon (128x128).

*   **`improvements.md`**:
    *   **Functionality**: Markdown file, likely for tracking future enhancements.

*   **`instructions.md`**:
    *   **Functionality**: Markdown file, likely containing usage or development instructions.

## Overall Workflow:

The primary workflow is initiated by clicking the extension's icon:

1.  User clicks the "Auto-Rate GG" icon in the Chrome toolbar.
2.  `background.js` detects this click.
3.  `background.js` injects `inject.js` into the active web page.
4.  `inject.js` executes its automation logic:
    *   Performs pre-setup steps (if applicable for the target site).
    *   Opens the rating form.
    *   Selects 5 stars.
    *   Enters "GG" as a comment.
    *   Submits the form.
    *   Closes any confirmation dialog.

Alternatively, if the `popup.html` is used (e.g., if the `default_popup` was set in `manifest.json` or if the user right-clicks the icon and there's an option), clicking the "Rate 5★" button in the popup would trigger `popup.js`, which then injects `inject.js`.

## Libraries and Packages:

*   **Chrome Extension APIs**:
    *   `chrome.action`: For managing the extension's icon and its behavior.
    *   `chrome.scripting`: For injecting scripts into web pages.
    *   `chrome.tabs`: For querying and interacting with browser tabs.
*   **Standard Web APIs**:
    *   DOM Manipulation: `document.querySelector`, element properties (`.value`, `.click()`, `.focus()`), `Event`, `dispatchEvent`.
    *   Asynchronous Operations: `Promise`, `setTimeout`.

The extension does **not** use any external third-party JavaScript libraries (like jQuery, React, Vue, Angular, etc.) for its core functionality.

## Package Number / Version:

*   **Version**: "1.0.0" (as specified in `manifest.json`).
*   The concept of a "package number" in the sense of npm packages (e.g., from a `package-lock.json`) is not directly applicable here as there are no external Node.js dependencies being managed for the runtime of the extension itself.
