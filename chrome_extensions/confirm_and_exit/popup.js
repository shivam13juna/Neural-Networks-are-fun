const btn = document.getElementById("endBtn");
btn.addEventListener("click", async () => {
  const [tab] = await chrome.tabs.query({ active: true, currentWindow: true });
  if (tab?.id) {
    await chrome.scripting.executeScript({ target: { tabId: tab.id }, files: ["inject.js"] });
    window.close();
  }
});
