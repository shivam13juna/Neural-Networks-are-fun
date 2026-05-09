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

// Listen for page navigation completion to resume bookmark automation after a
// reload triggered by the end-meeting flow. IMPORTANT: this MUST inject
// bookmark-resume.js, NOT inject.js. inject.js contains the destructive
// end-meeting flow and may only be injected by an explicit user action from
// the popup. Auto-injecting inject.js here previously ended live meetings
// without user trigger — see incident note.
chrome.webNavigation.onCompleted.addListener(async (details) => {
  if (details.frameId !== 0) return;
  if (!details.url.includes('scaler.com')) return;

  try {
    await chrome.scripting.executeScript({
      target: { tabId: details.tabId },
      files: ["bookmark-resume.js"]
    });
  } catch (err) {
    console.log("Bookmark-resume injection failed (normal if tab closed):", err.message);
  }
});
