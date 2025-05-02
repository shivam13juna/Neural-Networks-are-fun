// Fires when you press the "Rate 5★" button in the popup UI
document.getElementById('rate').addEventListener('click', async () => {
  // Get the active tab in the current window
  const [tab] = await chrome.tabs.query({ active: true, currentWindow: true });
  if (!tab?.id) return;

  // Inject the automation script into that tab
  await chrome.scripting.executeScript({
    target: { tabId: tab.id },
    files: ['inject.js'],
  });

  window.close(); // optional: auto-close the popup
});
