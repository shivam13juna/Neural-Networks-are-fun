// Popup logic: send message and update progress
const dlBtn = document.getElementById('dl');
const bar = document.getElementById('bar');
const status = document.getElementById('status');

dlBtn.addEventListener('click', () => {
  dlBtn.disabled = true;
  status.textContent = 'Starting...';
  chrome.runtime.sendMessage({ cmd: 'download-latest' }, response => {
    if (response?.ok) {
      status.textContent = 'Download initiated. Check browser downloads.';
      bar.value = 1;
    } else {
      status.textContent = 'Error: ' + (response?.error || 'Unknown');
      bar.value = 0;
    }
    dlBtn.disabled = false;
  });
});

// Listen for progress updates (optional)
chrome.runtime.onMessage.addListener((msg) => {
  if (msg.cmd === 'progress') {
    bar.value = msg.value;
    status.textContent = msg.text;
  }
});
