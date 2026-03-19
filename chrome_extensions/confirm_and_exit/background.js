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

// Handle bookmark script injection requests from content script.
// IMPORTANT: listener must NOT be async — Chrome needs a synchronous
// `return true` to keep the sendResponse channel open for the await.
chrome.runtime.onMessage.addListener((message, sender, sendResponse) => {
  if (message.action === 'injectBookmarkScript') {
    const tabId = sender.tab?.id;
    if (!tabId) {
      sendResponse({ success: false, error: 'No tab ID available' });
      return;
    }

    console.log("🔖 Injecting bookmark automation script into tab:", tabId);

    chrome.scripting.executeScript({
      target: { tabId: tabId },
      files: ["bookmark-automation.js"]
    }).then(() => {
      console.log("✅ Bookmark automation script injected successfully");
      sendResponse({ success: true });
    }).catch((error) => {
      console.error("❌ Failed to inject bookmark automation script:", error);
      sendResponse({ success: false, error: error.message });
    });

    return true; // synchronous — keeps message channel open
  }


});

// Listen for page navigation completion
chrome.webNavigation.onCompleted.addListener(async (details) => {
  // Only handle main frame (not iframes)
  if (details.frameId !== 0) return;
  
  // Only handle scaler.com pages
  if (!details.url.includes('scaler.com')) return;
  
  try {
    // Re-inject script to check for pending bookmark automation
    await chrome.scripting.executeScript({
      target: { tabId: details.tabId },
      files: ["inject.js"]
    });
  } catch (err) {
    // Tab may be closed or navigation failed - ignore
    console.log("Re-injection failed (normal if tab closed):", err.message);
  }
});
