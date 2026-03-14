/*
 * Confirm and Exit – content script
 * Replace SELECTOR_* constants below with real CSS selectors for your site.
 */

(async () => {
  // Skip meeting-end flow if we're here for bookmark automation after reload
  if (sessionStorage.getItem('needsBookmarkAutomation') === 'true') {
    console.log("Confirm and Exit: skipping meeting-end flow — bookmark automation pending");
    return;
  }

  //                                     ▼▼ REPLACE THESE ▼▼
  const SELECTOR_END_BUTTON   = "#meeting-sidebar > div.right-dock.scroll > div:nth-child(2) > div > div > a";           // top‑level "End" button
  const SELECTOR_LEAVE_ALL_BUTTON = "body > div.react-root.react-root--auto.meeting-app > div > a.tappable.dropdown-item.btn.btn-danger"; // Button for "End meeting for all"
  const SELECTOR_LEAVE_ALL_BUTTON_ALT = "body > div.react-root.react-root--auto.meeting-app > div > a.tappable.dropdown-item.btn.btn-danger"; // Alternative selector for "Leave All" button
  const SELECTOR_DIALOG       = "#react-root > div > div > div > div.sr-modal.sr-modal--open.dialog > div.sr-modal__header > div.sr-modal__title"; 
  const SELECTOR_DIALOG_ALT = "#root > div > div.sr-modal.sr-modal--open.dialog > div.sr-modal__header > div.sr-modal__title"             // modal container (optional wait target)
  const SELECTOR_INPUT        = "#react-root > div > div > div > div.sr-modal.sr-modal--open.dialog > div.sr-modal__body > div.dialog__body > div > input";        // text box inside dialog
  const SELECTOR_INPUT_ALT    = "#root > div > div.sr-modal.sr-modal--open.dialog > div.sr-modal__body > div.dialog__body > div > input"; // alternative text box inside dialog
  const SELECTOR_SUBMIT       = "#react-root > div > div > div > div.sr-modal.sr-modal--open.dialog > div.sr-modal__body > div.dialog__actions > a.tappable.btn.dialog__action.btn-danger";           // final "End / Submit" button
  const SELECTOR_SUBMIT_ALT   = "#root > div > div.sr-modal.sr-modal--open.dialog > div.sr-modal__body > div.dialog__actions > a.tappable.btn.dialog__action.btn-danger"; // alternative final "End / Submit" button
  const CONFIRM_TEXT          = "confirm";                     // text to type (lower‑case)
  const RETRIES               = 5;                             // how many times to retry each step
  const RETRY_DELAY_MS        = 500;                           // delay between retries
    // Check if the "missing bookmarks" message appears
  const TEXT_MISSING_BOOKMARKS_MESSAGE = "This class has bookmarks missing. Please add bookmarks.";

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

  /** Helper: retry an async fn up to N times WITHOUT showing popup (for optional checks) */
  async function retryQuiet(fn, label) {
    for (let i = 0; i < RETRIES; i++) {
      const ok = await fn();
      if (ok) return true;
      await new Promise(r => setTimeout(r, RETRY_DELAY_MS));
    }
    console.log(`Confirm and Exit: ${label} – not found after ${RETRIES} attempts (this is normal)`);
    return false;
  }

  /** Step 1: click End Meeting */
  if (!(await retryQuiet(() => {
    const btn = document.querySelector(SELECTOR_END_BUTTON); // Reverted to SELECTOR_END_BUTTON
    if (!btn) return false;
    btn.click();
    return true;
  }, "End button"))) return; // Reverted label

  /** Step 1.5: click Leave All Button */
  if (!(await retryQuiet(() => {
    // Try primary selector first
    let btn = document.querySelector(SELECTOR_LEAVE_ALL_BUTTON);
    if (btn) {
      btn.click();
      console.log("✓ Leave All button clicked using primary selector");
      return true;
    }
    
    // Try alternative selector if primary fails
    btn = document.querySelector(SELECTOR_LEAVE_ALL_BUTTON_ALT);
    if (btn) {
      btn.click();
      console.log("✓ Leave All button clicked using alternative selector");
      return true;
    }
    
    console.log("✗ Leave All button not found with either selector");
    return false;
  }, "Leave All button"))) return;

  /** Optional: wait until dialog appears (helps slow UIs) */
  // If a second, different dialog appears after "Leave All", a new SELECTOR and wait might be needed here.
  // For now, reusing SELECTOR_DIALOG or assuming the confirm input is now available.
  if (SELECTOR_DIALOG) {
    // This wait is now more critical: it's for the dialog expected to contain the input field.
    if (!(await retryQuiet(() => {
      // Try primary dialog selector first
      if (document.querySelector(SELECTOR_DIALOG)) {
        console.log("✓ Dialog found using primary selector");
        return true;
      }
      
      // Try alternative dialog selector if primary fails
      if (document.querySelector(SELECTOR_DIALOG_ALT)) {
        console.log("✓ Dialog found using alternative selector");
        return true;
      }
      
      console.log("✗ Dialog not found with either selector");
      return false;
    }, "confirmation dialog after Leave All button"))) return;
  }

  /** Step 2: type 'confirm' */
  if (!(await retryQuiet(() => {
    let input = document.querySelector(SELECTOR_INPUT)
             || document.querySelector(SELECTOR_INPUT_ALT);
    if (!input) {
      console.log("✗ Input field not found with either selector");
      return false;
    }

    input.focus();

    // Use native setter — works for both React-controlled and plain inputs
    const nativeSetter = Object.getOwnPropertyDescriptor(
        HTMLInputElement.prototype, 'value')?.set;
    if (nativeSetter) {
      nativeSetter.call(input, CONFIRM_TEXT);
    } else {
      input.value = CONFIRM_TEXT;
    }
    input.dispatchEvent(new Event("input", { bubbles: true }));
    input.dispatchEvent(new Event("change", { bubbles: true }));

    console.log("✓ Confirm text entered");
    return true;
  }, "confirm box"))) return;

  /** Step 3: click Submit */
  if (!(await retryQuiet(() => {
    // Try primary submit selector first
    let btn = document.querySelector(SELECTOR_SUBMIT);
    if (btn) {
      btn.click();
      console.log("✓ Submit button clicked using primary selector");
      return true;
    }
    
    // Try alternative submit selector if primary fails
    btn = document.querySelector(SELECTOR_SUBMIT_ALT);
    if (btn) {
      btn.click();
      console.log("✓ Submit button clicked using alternative selector");
      return true;
    }
    
    console.log("✗ Submit button not found with either selector");
    return false;
  }, "Submit button"))) return;

  console.log("Confirm and Exit ✔ finished");

  /** Step 4: Check for missing bookmarks and add them if needed */
  // Wait a moment for the page to process the meeting end
  await new Promise(r => setTimeout(r, 2000));
  
  const hasBookmarkMessage = await retryQuiet(() => {
    // Method 1: Look for the specific div element
    const bookmarkDiv = document.querySelector('.adios__bookmark-title');
    if (bookmarkDiv && bookmarkDiv.textContent.includes('This class has bookmarks missing')) {
      console.log("✓ Found missing bookmarks message in div element");
      return true;
    }
    
    // Method 2: Check if the exact text exists in the page (case-insensitive)
    const pageText = document.body.innerText;
    if (pageText.includes('This class has bookmarks missing')) {
      console.log("✓ Found missing bookmarks message in page text");
      return true;
    }
    
    // Method 3: Check for the lowercased version of the message
    if (pageText.toLowerCase().includes('this class has bookmarks missing')) {
      console.log("✓ Found missing bookmarks message (lowercase match)");
      return true;
    }
    
    console.log("✗ Missing bookmarks message not found");
    return false;
  }, "check for missing bookmarks message");

  if (!hasBookmarkMessage) {
    console.log("No missing bookmarks message found - automation complete");
    return;
  }

  console.log("📋 Missing bookmarks detected!");
  console.log("⏳ You have 75 seconds to upload notes if needed...");
  console.log("🔄 Bookmark automation will start after 75 seconds");

  // Store a flag to indicate we need to run bookmark automation after reload
  sessionStorage.setItem('needsBookmarkAutomation', 'true');

  // Wait 75 seconds on the current page (giving time for notes upload & recording to become valid)
  await new Promise(r => setTimeout(r, 75000));

  // Now refresh the page for bookmark automation
  console.log("✓ 75 seconds completed - starting bookmark automation...");
  console.log("🔄 Refreshing page for bookmark automation...");
  location.reload();

})();

// Check if we need to run bookmark automation after page reload
if (sessionStorage.getItem('needsBookmarkAutomation') === 'true') {
  sessionStorage.removeItem('needsBookmarkAutomation');
  
  // Wait for page to be fully loaded
  if (document.readyState === 'loading') {
    document.addEventListener('DOMContentLoaded', initBookmarkAutomation);
  } else {
    initBookmarkAutomation();
  }
}

async function initBookmarkAutomation() {
  // Wait a bit more for the page to be fully ready
  await new Promise(r => setTimeout(r, 3000));
  
  console.log("🔖 Initializing bookmark automation...");
  
  // CRITICAL FIX: Direct script injection using chrome extension APIs
  // Send message to background script to inject bookmark automation
  try {
    const response = await chrome.runtime.sendMessage({
      action: 'injectBookmarkScript',
      tabId: 'current'
    });
    
    if (response && response.success) {
      console.log("🔖 Bookmark automation script injection requested successfully!");
    } else {
      console.log("🔄 Falling back to direct bookmark automation execution...");
      // If messaging fails, we'll trigger bookmark automation directly
      setTimeout(() => {
        if (typeof addBookmarks === 'function') {
          addBookmarks();
        } else {
          console.log("⚠️ Bookmark automation function not available - script may not be loaded");
        }
      }, 2000);
    }
    
  } catch (error) {
    console.log("🔄 Background script messaging failed, using direct approach...", error.message);
    
    // Fallback: Try to load the script directly via extension URL
    const script = document.createElement('script');
    script.src = chrome.runtime.getURL('bookmark-automation.js');
    script.onload = function() {
      console.log("✅ Bookmark automation script loaded via direct injection!");
    };
    script.onerror = function() {
      console.error("❌ Failed to load bookmark automation script");
    };
    document.head.appendChild(script);
  }
}
