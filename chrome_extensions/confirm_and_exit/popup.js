(function() {
  // Wrapped in IIFE to avoid variable conflicts
  const endBtn = document.getElementById("endBtn");
  endBtn.addEventListener("click", async () => {
    const [tab] = await chrome.tabs.query({ active: true, currentWindow: true });
    if (tab?.id) {
      await chrome.scripting.executeScript({ target: { tabId: tab.id }, files: ["inject.js"] });
    }
    window.close();
  });

  // Test Bookmarks button functionality
  const testBookmarksBtn = document.getElementById("testBookmarksBtn");
  testBookmarksBtn.addEventListener("click", async () => {
    const [tab] = await chrome.tabs.query({ active: true, currentWindow: true });
    if (tab?.id) {
      try {
        console.log("🧪 Testing bookmark automation directly...");
        
        // Directly inject the bookmark automation script into the page
        await chrome.scripting.executeScript({ 
          target: { tabId: tab.id }, 
          files: ["bookmark-automation.js"]
        });
        
        console.log("🔖 Bookmark automation script injected successfully!");
        
      } catch (error) {
        console.error("❌ Failed to test bookmark automation:", error);
      }
    }
    window.close();
  });
})();
