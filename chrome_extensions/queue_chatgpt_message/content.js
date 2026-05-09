// Queue state lives on the page — dies naturally when the tab is closed or refreshed.
let messages = [];
let sentCount = 0;
let running = false;
let nextSendTime = null;
let endPromptText = '';

let queueTimer = null;
let timerResolve = null;

function getState() {
  return {
    messages: messages.slice(),
    sentCount,
    running,
    nextSendTime
  };
}

function typeIntoEditor(text) {
  const editor = document.querySelector('#prompt-textarea');
  if (!editor) {
    console.error('ChatGPT input not found');
    return false;
  }

  editor.focus();
  editor.innerHTML = '';

  const lines = text.split('\n');
  lines.forEach(line => {
    const p = document.createElement('p');
    p.textContent = line || '\u200B';
    editor.appendChild(p);
  });

  editor.dispatchEvent(new Event('input', { bubbles: true }));
  return true;
}

function clickSendButton() {
  const sendBtn = document.querySelector('[data-testid="send-button"]');
  if (sendBtn && !sendBtn.disabled) {
    sendBtn.click();
    return true;
  }
  const fallback = document.querySelector('button[aria-label="Send prompt"]');
  if (fallback && !fallback.disabled) {
    fallback.click();
    return true;
  }
  return false;
}

function waitForResponseDone() {
  return new Promise((resolve) => {
    const check = () => {
      if (!running) { resolve(); return; }
      const stopBtn = document.querySelector('[data-testid="stop-button"]');
      if (!stopBtn) resolve();
      else setTimeout(check, 2000);
    };
    setTimeout(check, 3000);
  });
}

function armTimer(ms) {
  return new Promise((resolve) => {
    timerResolve = resolve;
    queueTimer = setTimeout(() => {
      queueTimer = null;
      const r = timerResolve;
      timerResolve = null;
      if (r) r();
    }, ms);
  });
}

function releaseTimer() {
  if (queueTimer) {
    clearTimeout(queueTimer);
    queueTimer = null;
  }
  if (timerResolve) {
    const r = timerResolve;
    timerResolve = null;
    r();
  }
}

async function sendNextMessage() {
  if (!running) return;
  if (sentCount >= messages.length) {
    running = false;
    nextSendTime = null;
    chrome.runtime.sendMessage({ action: 'queueDone' }).catch(() => {});
    return;
  }

  let msg = messages[sentCount];
  if (endPromptText) msg = msg + '\n\n\n' + endPromptText;
  console.log(`[ChatGPT Queue] Sending ${sentCount + 1}/${messages.length}: "${msg.substring(0, 50)}..."`);

  if (!typeIntoEditor(msg)) {
    console.error('[ChatGPT Queue] Editor not found, retrying in 5s...');
    queueTimer = setTimeout(() => { queueTimer = null; if (running) sendNextMessage(); }, 5000);
    return;
  }

  await new Promise(r => setTimeout(r, 500));
  if (!running) return;

  if (!clickSendButton()) {
    console.error('[ChatGPT Queue] Send button unavailable, retrying in 5s...');
    queueTimer = setTimeout(() => { queueTimer = null; if (running) sendNextMessage(); }, 5000);
    return;
  }

  // Read latest interval so mid-queue changes take effect on the next gap.
  const { interval } = await chrome.storage.local.get('interval');
  const intervalMs = (interval || 5) * 60 * 1000;
  nextSendTime = Date.now() + intervalMs;
  chrome.runtime.sendMessage({ action: 'timerStarted', nextSendTime }).catch(() => {});

  const timerPromise = armTimer(intervalMs);
  await waitForResponseDone();
  if (!running) return;

  sentCount++;
  chrome.runtime.sendMessage({
    action: 'messageSent',
    sentCount,
    total: messages.length
  }).catch(() => {});

  if (sentCount >= messages.length) {
    running = false;
    nextSendTime = null;
    releaseTimer();
    chrome.runtime.sendMessage({ action: 'queueDone' }).catch(() => {});
    return;
  }

  await timerPromise;
  if (!running) return;
  sendNextMessage();
}

function stopQueue() {
  running = false;
  nextSendTime = null;
  releaseTimer();
  console.log('[ChatGPT Queue] Stopped');
}

function clearQueue() {
  stopQueue();
  messages = [];
  sentCount = 0;
}

async function startQueue() {
  if (running) return;
  if (sentCount >= messages.length) return;
  running = true;
  const { endPrompt } = await chrome.storage.local.get('endPrompt');
  endPromptText = (endPrompt || '').trim();
  console.log(`[ChatGPT Queue] Starting at ${sentCount + 1}/${messages.length}`);
  sendNextMessage();
}

chrome.runtime.onMessage.addListener((msg, sender, sendResponse) => {
  switch (msg.action) {
    case 'getState':
      sendResponse(getState());
      return false;

    case 'addMessage': {
      const text = (msg.text || '').trim();
      if (text) messages.push(text);
      sendResponse(getState());
      return false;
    }

    case 'removeMessage': {
      const i = msg.index;
      if (Number.isInteger(i) && i >= sentCount && i < messages.length) {
        messages.splice(i, 1);
      }
      sendResponse(getState());
      return false;
    }

    case 'startQueue':
      startQueue();
      sendResponse(getState());
      return false;

    case 'stopQueue':
      stopQueue();
      sendResponse(getState());
      return false;

    case 'clearQueue':
      clearQueue();
      sendResponse(getState());
      return false;

    default:
      return false;
  }
});
