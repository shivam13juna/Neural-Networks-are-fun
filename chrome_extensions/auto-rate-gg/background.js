// background.js

const MEETING_URL_PATTERN = /^https:\/\/www\.scaler\.com\/instructor\/meetings/;

async function runOnMeetingTab(tab) {
  if (!tab?.id || !tab.url || !MEETING_URL_PATTERN.test(tab.url)) {
    console.warn('Auto-Rate GG: Not on a Scaler meeting page, skipping.');
    return;
  }
  await chrome.scripting.executeScript({
    target: { tabId: tab.id },
    files: ['inject.js'],
  });
}

chrome.action.onClicked.addListener(runOnMeetingTab);

chrome.commands.onCommand.addListener(async (command) => {
  if (command !== 'trigger-auto-rate') return;
  const [tab] = await chrome.tabs.query({ active: true, currentWindow: true });
  await runOnMeetingTab(tab);
});
