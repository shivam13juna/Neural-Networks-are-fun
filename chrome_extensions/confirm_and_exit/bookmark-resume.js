/*
 * Confirm and Exit – bookmark resume script.
 *
 * This file is intentionally separate from inject.js. inject.js performs the
 * destructive end-meeting flow and must ONLY run when the user explicitly
 * triggers it from the popup. This file only resumes bookmark automation
 * after a page reload that was initiated by the end-meeting flow, and is
 * therefore safe to auto-inject on every scaler.com navigation.
 */

if (sessionStorage.getItem('needsBookmarkAutomation') === 'true') {
  sessionStorage.removeItem('needsBookmarkAutomation');

  if (document.readyState === 'loading') {
    document.addEventListener('DOMContentLoaded', initBookmarkAutomation);
  } else {
    initBookmarkAutomation();
  }
}

async function initBookmarkAutomation() {
  await new Promise(r => setTimeout(r, 3000));

  console.log("🔖 Initializing bookmark automation...");

  try {
    const response = await chrome.runtime.sendMessage({
      action: 'injectBookmarkScript',
      tabId: 'current'
    });

    if (response && response.success) {
      console.log("🔖 Bookmark automation script injection requested successfully!");
    } else {
      console.log("🔄 Falling back to direct bookmark automation execution...");
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
