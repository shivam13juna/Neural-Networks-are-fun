const msgInput = document.getElementById('msgInput');
const intervalInput = document.getElementById('intervalInput');
const queueEl = document.getElementById('queue');
const statusEl = document.getElementById('status');
const endPromptInput = document.getElementById('endPromptInput');
const addBtn = document.getElementById('addBtn');
const startBtn = document.getElementById('startBtn');
const stopBtn = document.getElementById('stopBtn');
const clearBtn = document.getElementById('clearBtn');
const countdownEl = document.getElementById('countdown');
const progressSection = document.getElementById('progressSection');
const progressFill = document.getElementById('progressFill');
const sentLabel = document.getElementById('sentLabel');
const remainingLabel = document.getElementById('remainingLabel');
const progressLabel = document.getElementById('progressLabel');

let messages = [];
let sentCount = 0;
let running = false;
let nextSendTime = null;
let boundTabId = null;

function isChatGPTUrl(url) {
  return !!url && (url.includes('chatgpt.com') || url.includes('chat.openai.com'));
}

async function getActiveChatGPTTab() {
  const [tab] = await chrome.tabs.query({ active: true, currentWindow: true });
  return tab && isChatGPTUrl(tab.url) ? tab : null;
}

async function send(action, payload = {}) {
  const tab = await getActiveChatGPTTab();
  if (!tab) return null;
  boundTabId = tab.id;
  try {
    return await chrome.tabs.sendMessage(tab.id, { action, ...payload });
  } catch (_) {
    // Tab existed before extension was installed/reloaded — content script hasn't been injected yet.
    try {
      await chrome.scripting.executeScript({ target: { tabId: tab.id }, files: ['content.js'] });
      return await chrome.tabs.sendMessage(tab.id, { action, ...payload });
    } catch (_) {
      return null;
    }
  }
}

function applyState(state) {
  if (!state) {
    messages = [];
    sentCount = 0;
    running = false;
    nextSendTime = null;
  } else {
    messages = state.messages || [];
    sentCount = state.sentCount || 0;
    running = !!state.running;
    nextSendTime = state.nextSendTime || null;
  }
  renderQueue();
  updateStatus();
  if (running) startCountdown();
}

function renderQueue() {
  queueEl.innerHTML = '';
  messages.forEach((msg, i) => {
    const li = document.createElement('li');
    if (i < sentCount) li.classList.add('sent');
    if (running && i === sentCount) li.classList.add('processing');

    const text = document.createElement('span');
    const prefix = i < sentCount ? '[sent] ' : (running && i === sentCount ? '[processing] ' : `[${i + 1}] `);
    text.textContent = prefix + msg;
    li.appendChild(text);

    if (i >= sentCount) {
      const removeBtn = document.createElement('span');
      removeBtn.className = 'remove';
      removeBtn.textContent = 'x';
      removeBtn.addEventListener('click', async () => {
        const state = await send('removeMessage', { index: i });
        applyState(state);
      });
      li.appendChild(removeBtn);
    }

    queueEl.appendChild(li);
  });
}

function updateProgress() {
  const total = messages.length;
  const remaining = total - sentCount;

  if (total === 0) {
    progressSection.classList.remove('visible');
    return;
  }

  progressSection.classList.add('visible');

  const pct = total > 0 ? (sentCount / total) * 100 : 0;
  progressFill.style.width = pct + '%';

  if (sentCount === total && total > 0) {
    progressFill.classList.add('done');
  } else {
    progressFill.classList.remove('done');
  }

  sentLabel.textContent = `${sentCount} sent`;
  remainingLabel.textContent = `${remaining} remaining`;
  progressLabel.textContent = `${sentCount} / ${total}`;
}

function updateStatus() {
  const remaining = messages.length - sentCount;
  if (running && remaining > 0) {
    const currentMsg = sentCount + 1;
    const left = remaining - 1;
    statusEl.textContent = `Message ${currentMsg} is being processed, ${left} message${left !== 1 ? 's' : ''} left`;
  } else if (running && remaining === 0) {
    statusEl.textContent = 'All messages sent!';
  } else {
    statusEl.textContent = remaining > 0
      ? `${remaining} messages queued`
      : 'Queue empty';
  }
  updateProgress();
  startBtn.disabled = running;
  stopBtn.disabled = !running;
}

addBtn.addEventListener('click', async () => {
  const text = msgInput.value.trim();
  if (!text) return;
  const tab = await getActiveChatGPTTab();
  if (!tab) {
    statusEl.textContent = 'Navigate to ChatGPT first!';
    return;
  }
  msgInput.value = '';
  const state = await send('addMessage', { text });
  if (state) applyState(state);
});

startBtn.addEventListener('click', async () => {
  const remaining = messages.length - sentCount;
  if (remaining === 0) {
    statusEl.textContent = 'Nothing to send. Add messages first.';
    return;
  }
  const tab = await getActiveChatGPTTab();
  if (!tab) {
    statusEl.textContent = 'Navigate to ChatGPT first!';
    return;
  }
  await savePrefs();
  const state = await send('startQueue');
  if (state) applyState(state);
});

stopBtn.addEventListener('click', async () => {
  const state = await send('stopQueue');
  if (state) applyState(state);
});

clearBtn.addEventListener('click', async () => {
  const state = await send('clearQueue');
  if (state) applyState(state);
});

chrome.runtime.onMessage.addListener((msg, sender) => {
  // Ignore updates from other tabs (each ChatGPT tab has its own queue).
  if (boundTabId != null && sender.tab && sender.tab.id !== boundTabId) return;

  if (msg.action === 'messageSent') {
    sentCount = msg.sentCount;
    if (typeof msg.total === 'number' && messages.length !== msg.total) {
      // Keep local cache in sync if it drifts.
      send('getState').then(applyState);
      return;
    }
    renderQueue();
    updateStatus();
  }
  if (msg.action === 'timerStarted') {
    nextSendTime = msg.nextSendTime;
  }
  if (msg.action === 'queueDone') {
    running = false;
    nextSendTime = null;
    renderQueue();
    updateStatus();
  }
});

let countdownInterval = null;

function startCountdown() {
  clearInterval(countdownInterval);
  countdownInterval = setInterval(() => {
    if (!running) {
      countdownEl.textContent = '';
      nextSendTime = null;
      clearInterval(countdownInterval);
      return;
    }
    if (!nextSendTime) {
      const remaining = messages.length - sentCount;
      countdownEl.textContent = remaining <= 1
        ? 'Waiting for last response to finish...'
        : 'Waiting for response...';
      return;
    }
    const remaining = Math.max(0, Math.ceil((nextSendTime - Date.now()) / 1000));
    if (remaining === 0) {
      countdownEl.textContent = 'Sending...';
      nextSendTime = null;
      return;
    }
    const min = Math.floor(remaining / 60);
    const sec = remaining % 60;
    countdownEl.textContent = `Next message in ${min}:${sec.toString().padStart(2, '0')}`;
  }, 1000);
}

function savePrefs() {
  return chrome.storage.local.set({
    interval: parseInt(intervalInput.value) || 5,
    endPrompt: endPromptInput.value
  });
}

function loadPrefs() {
  return chrome.storage.local.get(['interval', 'endPrompt']).then((data) => {
    if (data.interval) intervalInput.value = data.interval;
    if (data.endPrompt) endPromptInput.value = data.endPrompt;
  });
}

intervalInput.addEventListener('change', savePrefs);
endPromptInput.addEventListener('change', savePrefs);

(async function init() {
  await loadPrefs();
  const tab = await getActiveChatGPTTab();
  if (!tab) {
    applyState(null);
    statusEl.textContent = 'Navigate to ChatGPT first!';
    return;
  }
  const state = await send('getState');
  applyState(state);
})();
