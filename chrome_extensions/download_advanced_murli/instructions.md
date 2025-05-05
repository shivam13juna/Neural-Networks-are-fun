# instructions.md

## 1 Purpose

Provide BK students a one‑click way to obtain a monthly **Hindi Advance Murli compilation PDF** without manual browsing, clicking, or file merging.

## 2 Scope

* **Included** – Chrome extension, Manifest V3, automated scraping, PDF downloading, client‑side merging, and file delivery.
* **Excluded** – Server‑side services, English Murli handling, non‑Chrome browsers.

## 3 Definitions

| Term          | Meaning                                                 |
| ------------- | ------------------------------------------------------- |
| Tag‑page      | `https://www.shivbabas.org/blog/tags/advance-murli`     |
| Month‑post    | Post with slug `/post/advance-murli-<month>-<year>`     |
| Hindi section | `<h2>## Hindi Murli PDF</h2>` and following anchor list |

## 4 Functional Requirements

| ID   | Requirement                                                                                                                        |
| ---- | ---------------------------------------------------------------------------------------------------------------------------------- |
| F‑1  | Detect the most recent Month‑post by comparing publish dates visible on the tag‑page.                                              |
| F‑2  | Open/fetch that Month‑post.                                                                                                        |
| F‑3  | Extract **only** anchors (`<a>` with `href` ending in `.pdf`) beneath the Hindi section header until another header or blank line. |
| F‑4  | Preserve chronological order (earliest → latest).                                                                                  |
| F‑5  | Download each PDF to memory (ArrayBuffer).                                                                                         |
| F‑6  | Merge all PDFs into one using `pdf-lib` (client‑side).                                                                             |
| F‑7  | Trigger `chrome.downloads.download` with a filename pattern `advance-murli-hindi-<month>-<year>.pdf`.                              |
| F‑8  | Expose a browser‑action popup with a single button **“Download latest Hindi Advance Murli”** and a progress bar.                   |
| F‑9  | Gracefully report errors (network, CORS, PDF merge) in the popup UI.                                                               |
## 5 Non‑Functional Requirements

* **Performance** – DOM parsing < 100 ms, merge < 3 s for 40 PDFs.
* **Compatibility** – Chrome 122+, Manifest V3.
* **Security** – No remote code eval, CSP default‑src `'self'`.
* **Privacy** – No data stored beyond session unless user ticks *“Remember last download”* (uses `chrome.storage.local`).
* **i18n** – All UI strings isolated for future translation.

## 6 Architecture Overview

```mermaid
flowchart LR
  A[Popup UI] -->|click| B(Background Service‑Worker)
  B --> C{Fetch tag‑page}
  C --> D[Parse latest link]
  D --> E{Fetch Month‑post}
  E --> F[Extract Hindi PDF URLs]
  F --> G[Download PDFs (parallel)]
  G --> H[Merge with pdf-lib]
  H --> I[Create Blob URL]
  I --> J[chrome.downloads.download]
  subgraph Permissions
    activeTab & downloads & storage & host_permissions
  end
```

### 6.1 Key Components

| File                    | Role                                         |
| ----------------------- | -------------------------------------------- |
| `manifest.json`         | Metadata, permissions, service‑worker entry  |
| `service_worker.js`     | Fetch, scrape, download, merge, notify popup |
| `popup.html / popup.js` | User interaction & progress display          |
| `lib/pdf-lib.min.js`    | Third‑party PDF merger                       |
| `utils/scraper.js`      | Pure DOM‑parser helpers (imported by worker) |

## 7 Implementation Details

### 7.1 `manifest.json` (excerpt)

```json
{
  "manifest_version": 3,
  "name": "Advance Murli Merger",
  "version": "1.0.0",
  "description": "Downloads and merges the latest Hindi Advance Murli PDFs.",
  "icons": { "16": "icons/16.png", "48": "icons/48.png", "128": "icons/128.png" },
  "action": { "default_popup": "popup.html" },
  "permissions": ["downloads", "storage", "scripting", "activeTab"],
  "host_permissions": [
    "https://www.shivbabas.org/*",
    "https://babamurli.net/*",
    "https://drive.google.com/*"
  ],
  "content_security_policy": {
    "extension_pages": "script-src 'self'; object-src 'self';"
  },
  "background": { "service_worker": "service_worker.js", "type": "module" }
}
```

### 7.2 Scraping Logic (`utils/scraper.js`)

```js
export async function findLatestMonthUrl() {
  const res = await fetch('https://www.shivbabas.org/blog/tags/advance-murli');
  const html = await res.text();
  const doc = new DOMParser().parseFromString(html, 'text/html');
  const anchors = [...doc.querySelectorAll('a')].filter(a =>
      /^Advance Murlis for/i.test(a.textContent.trim()));
  const sorted = anchors
    .map(a => ({ url: a.href, date: new Date(a.closest('article time')?.dateTime || a.textContent) }))
    .sort((a, b) => b.date - a.date);
  return sorted[0].url;
}

export async function getHindiPdfLinks(monthUrl) {
  const res = await fetch(monthUrl);
  const html = await res.text();
  const doc = new DOMParser().parseFromString(html, 'text/html');
  const h2 = [...doc.querySelectorAll('h2')].find(h => /Hindi Murli PDF/i.test(h.textContent));
  const links = [];
  let node = h2?.nextElementSibling;
  while (node && node.tagName !== 'H2') {
    if (node.tagName === 'A' && /\.pdf$/i.test(node.getAttribute('href'))) {
      links.push({ href: node.href, text: node.textContent.trim() });
    }
    node = node.nextElementSibling;
  }
  return links;
}
```

### 7.3 Download & Merge (inside `service_worker.js`)

```js
import { PDFDocument } from './lib/pdf-lib.min.js';
import { findLatestMonthUrl, getHindiPdfLinks } from './utils/scraper.js';

chrome.runtime.onMessage.addListener(async (msg, sender, sendResponse) => {
  if (msg.cmd !== 'download-latest') return;

  try {
    const monthUrl = await findLatestMonthUrl();
    const pdfLinks = await getHindiPdfLinks(monthUrl);

    const fileBuffers = await Promise.all(pdfLinks.map(async ({ href }) => {
      const res = await fetch(href);
      return res.arrayBuffer();
    }));

    const merged = await PDFDocument.create();
    for (const buf of fileBuffers) {
      const src = await PDFDocument.load(buf);
      const pages = await merged.copyPages(src, src.getPageIndices());
      pages.forEach(p => merged.addPage(p));
    }
    const mergedBytes = await merged.save();
    const blobUrl = URL.createObjectURL(
      new Blob([mergedBytes], { type: 'application/pdf' })
    );

    await chrome.downloads.download({
      url: blobUrl,
      filename: `advance-murli-hindi-${new Date().toLocaleString('en', { month: 'long', year: 'numeric' }).toLowerCase()}.pdf`,
      saveAs: true
    });
    sendResponse({ ok: true });
  } catch (e) {
    console.error(e);
    sendResponse({ ok: false, error: e.message });
  }
  return true; // keep channel open
});
```

### 7.4 Popup UI (`popup.html` + `popup.js`)

* **popup.html** – Minimal HTML with `<button id="dl">Download latest Hindi Advance Murli</button>` and `<progress id="bar" max="100" value="0"></progress>`.
* **popup.js** – Sends `{cmd:'download-latest'}` to the service‑worker, updates progress with `chrome.runtime.onMessage`.

## 8 Testing & QA

| Test                          | Expected                             |
| ----------------------------- | ------------------------------------ |
| New month appears on tag‑page | Extension still finds it             |
| No PDFs available             | UI shows “No files found” error      |
| Link returns 404              | Graceful skip with warning           |
| 20 + MB combined              | Merge succeeds, final size ≤ Σ sizes |

Automated Jest + Puppeteer tests stub `fetch` to validate parsing and merging.

## 9 Deployment

1. `npm install && npm run build` – bundles ES modules with Rollup.
2. Zip `dist` folder and upload to Chrome Web Store.
3. After approval, push tag `v1.0.0`.

## 10 Future Enhancements

* Support **English** PDFs.
* Allow user to choose month retroactively.
* Offline caching to avoid re‑downloads.
* CLI version (Node.js) for headless servers.

---

### End of `instructions.md`

[1]: https://www.shivbabas.org/blog/tags/advance-murli "advance murli | Brahma Kumaris Blog"
[2]: https://www.shivbabas.org/post/advance-murli-may-2025 "Advance Murli, May 2025"
