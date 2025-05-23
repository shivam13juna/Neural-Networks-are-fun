**Providing README instructions**

The user just needs a README file with detailed instructions on setting up their Chrome extension. They don’t need auto-login or an auto-activation schedule and prefer to handle login manually. The extension should have a button that selects 5 stars, fills "GG" as the message, and submits upon clicking. I'll include instructions for setup and any necessary code in the README. I'll also adapt the messages and architecture for this simplified version with just the basic button trigger.

**Creating README instructions**

The user has requested a README file with detailed setup instructions for a Chrome MV3 extension, including code examples. We'll structure it with a proper overview, step-by-step setup, and security notes. The setup involves a manifest with permissions, a popup HTML with a "Rate 5★" button, and a script to select the star, fill in a comment, and submit. I'll confirm only necessary internal messaging for simplicity and include customization steps like updating selectors. No browsing required for this general task. I'll focus on a clear, concise README without unnecessary add-ons.



```markdown
# ⭐ Auto-Rate “GG” Chrome Extension

A minimal Manifest V3 extension that adds a single **Rate 5★** button to your Chrome toolbar.  
Press the button **after you’ve logged in and navigated to the rating page**; it will:

1. Click the 5-star element  
2. Type `GG` in the comment box  
3. Click the **Submit** button  

No scheduling, no auto-login—purely manual.

---

## 📂 Folder Structure

```

auto-rate-gg/
├── manifest.json
├── popup.html
├── popup.js          # Runs when you click the toolbar icon
└── inject.js         # Executed in the active tab to automate the DOM

````

---

## 🔑 manifest.json

```jsonc
{
  "name": "Auto-Rate GG",
  "description": "One-click 5★ rating + GG message.",
  "version": "1.0.0",
  "manifest_version": 3,
  "action": { "default_popup": "popup.html" },
  "permissions": ["scripting", "activeTab"],
  "host_permissions": ["https://example.com/*"]  // ⬅️ replace with real domain
}
````

*Only two permissions are required:*

| Permission    | Why it’s needed                                         |
| ------------- | ------------------------------------------------------- |
| **activeTab** | Lets the popup act on the current page you’re viewing   |
| **scripting** | Allows injection of the automation code ( `inject.js` ) |

---

## 🖱️ popup.html

```html
<!doctype html>
<html>
  <head>
    <meta charset="utf-8" />
    <style>
      body { font-family: system-ui; margin: 16px; }
      button { padding: 6px 12px; font-size: 14px; }
    </style>
  </head>
  <body>
    <button id="rate">Rate 5★</button>
    <script src="popup.js"></script>
  </body>
</html>
```

---

## 🏗️ popup.js

```js
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
```

---

## 🤖 inject.js

```js
/* *****************  USER MUST EDIT SELECTORS BELOW  ***************** */
const STAR_SELECTOR     = 'button.star[data-value="5"]';   // 5-star button
const TEXTBOX_SELECTOR  = 'textarea#comment';              // comment box
const SUBMIT_SELECTOR   = 'button[type="submit"]';         // submit button
/* ******************************************************************* */

(function autoRate() {
  const star   = document.querySelector(STAR_SELECTOR);
  const box    = document.querySelector(TEXTBOX_SELECTOR);
  const submit = document.querySelector(SUBMIT_SELECTOR);

  if (!star || !box || !submit) {
    alert('Auto-Rate GG:\nCould not find one or more elements.\nCheck selectors in inject.js');
    return;
  }

  // 1) click 5-star
  star.click();

  // 2) enter message
  box.focus();
  box.value = 'GG';
  box.dispatchEvent(new Event('input', { bubbles: true }));

  // 3) click submit
  submit.click();
})();
```

> **How to find selectors**
>
> * Open DevTools (F12) ➜ Elements tab ➜ Right-click element ➜ **Copy → CSS selector**.
>   Replace the three constants above with what you copy.

---

## 🛠️ Installation & Usage

1. Clone or download this folder.
2. Go to `chrome://extensions` ➜ **Load unpacked** ➜ pick the `auto-rate-gg` directory.
3. Log in to the target website and open the rating page.
4. Click the **Auto-Rate GG** toolbar icon ➜ **Rate 5★** button.

   * If selectors are right, the page will instantly submit.
   * First time fails? Update selectors in `inject.js`, hit **Ctrl + R** in the extensions page to reload, and retry.

---

## 💬 Internal Message Flow

| Sender        | Receiver | Mechanism                        | Purpose                                    |
| ------------- | -------- | -------------------------------- | ------------------------------------------ |
| **popup.js**  | Chrome   | `chrome.scripting.executeScript` | Injects `inject.js` into the active tab    |
| **inject.js** | N/A      | direct DOM calls                 | Performs star click, message entry, submit |

*No background service worker, alarms, or storage needed.*

---

## 🔒 Security / Privacy

* The script runs **only when you click** the extension button.
* No credentials are stored.
* No external network requests or analytics.

---

## 🧩 Customisation

| What                   | How                                            |
| ---------------------- | ---------------------------------------------- |
| Change default message | Edit the string `'GG'` in `inject.js`          |
| Different score        | Point `STAR_SELECTOR` at a 4-star button, etc. |
| Extra fields           | Add more DOM operations inside `autoRate()`    |

---

## ❓ Troubleshooting

| Symptom                         | Fix                                                                 |
| ------------------------------- | ------------------------------------------------------------------- |
| Alert: “Could not find element” | Double-check CSS selectors with DevTools                            |
| Nothing happens on click        | Make sure you’re on the correct domain (matches `host_permissions`) |
| Popup says “Cannot access page” | Tab might be `chrome://`, PDF, or another restricted scheme         |

---

That’s the entire extension—**four tiny files**.
Fork it, tweak the selectors, and you’re done! 🎉

```
```
