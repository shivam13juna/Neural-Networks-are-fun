import { PDFDocument } from 'pdf-lib';

// Function to inject for finding the latest month URL
function extractLatestMonthUrl() {
  const link = document.querySelector('a[href*="advance-murli-"][href*="-20"]'); // Basic selector, adjust if needed
  return link ? link.href : null;
}

// Function to inject for extracting Hindi PDF links from a month page
function extractHindiPdfLinks() {
  const links = Array.from(document.querySelectorAll('a[href$="\.pdf"]'));
  // Filter for links containing 'hindi' and likely date patterns (adjust regex as needed)
  const hindiPdfLinks = links
    .filter(a => /hindi/i.test(a.textContent) || /hindi/i.test(a.href))
    .map(a => ({ href: a.href, text: a.textContent.trim() }));
  return hindiPdfLinks;
}


// Listen for the extension icon click
chrome.action.onClicked.addListener(async (tab) => {
  console.log("Action clicked on tab:", tab.id, tab.url);

  let targetTabId = tab.id; // Use the tab where the icon was clicked
  const startUrl = 'https://www.shivbabas.org/blog/tags/advance-murli';

  try {
    // --- Ensure the tab is on the correct domain or navigate it ---
    // Check if the tab URL is valid and accessible (e.g., not chrome:// pages)
    if (!tab.url || tab.url.startsWith('chrome://') || tab.url.startsWith('about:')) {
        console.log(`Clicked on an invalid tab (${tab.url}). Opening new tab.`);
        const newTab = await chrome.tabs.create({ url: startUrl, active: true });
        targetTabId = newTab.id;
        await new Promise(resolve => setTimeout(resolve, 2500)); // Wait for new tab
    } else if (!tab.url.startsWith('https://www.shivbabas.org')) {
        console.log(`Tab ${targetTabId} is not on the correct domain. Navigating to ${startUrl}`);
        await chrome.tabs.update(targetTabId, { url: startUrl, active: true });
        // Wait for navigation after update
        await new Promise(resolve => setTimeout(resolve, 2500)); // Allow time for navigation
    } else if (!tab.url.startsWith(startUrl)) {
         // If on the domain but not the specific start page, navigate
         console.log(`Tab ${targetTabId} is on the domain but not start URL. Navigating to ${startUrl}`);
         await chrome.tabs.update(targetTabId, { url: startUrl, active: true });
         await new Promise(resolve => setTimeout(resolve, 1500));
    } else {
         // Make sure the tab is active if it's already correct
         await chrome.tabs.update(targetTabId, { active: true });
    }
    // --- End tab check/navigation ---

    console.log(`Proceeding with tab ID: ${targetTabId}`);
    // --- Use scripting to find the latest month URL ---
    console.log('Finding latest month page...');
    let injectionResults;
    try {
        injectionResults = await chrome.scripting.executeScript({
            target: { tabId: targetTabId },
            func: extractLatestMonthUrl,
        });
    } catch (scriptError) {
        console.error("Error injecting script to find month URL:", scriptError);
        throw new Error(`Failed to execute script on tab ${targetTabId}. Ensure the extension has permissions for the page. Error: ${scriptError.message}`);
    }


    // Ensure results exist and get the URL
    if (!injectionResults || !injectionResults[0]) {
        // Handle cases where the script executed but returned nothing (e.g., link not found)
         throw new Error('Script executed, but could not find the latest month URL link on the page.');
    }
     if (injectionResults[0].result === null) {
         throw new Error('Could not find the latest month URL link on the page (selector returned null).');
     }
    const monthUrl = injectionResults[0].result;
    console.log(`Found month URL: ${monthUrl}`);
    // --- End scripting for month URL ---

    // --- Use scripting to get PDF links from the month page ---
    console.log(`Navigating tab ${targetTabId} to month URL: ${monthUrl}`);
    await chrome.tabs.update(targetTabId, { url: monthUrl });
    await new Promise(resolve => setTimeout(resolve, 2000)); // Wait for page load

    console.log('Extracting PDF links...');
    try {
        injectionResults = await chrome.scripting.executeScript({
            target: { tabId: targetTabId }, // Execute in the same tab
            func: extractHindiPdfLinks,
        });
    } catch (scriptError) {
        console.error("Error injecting script to find PDF links:", scriptError);
        throw new Error(`Failed to execute script on tab ${targetTabId} to find PDF links. Error: ${scriptError.message}`);
    }


     // Ensure results exist and get the links
    if (!injectionResults || !injectionResults[0]) {
         throw new Error(`Script executed, but could not extract PDF links from ${monthUrl}`);
    }
     if (!injectionResults[0].result) {
         throw new Error(`Could not extract PDF links from ${monthUrl} (selector returned null or empty).`);
     }
    const pdfLinks = injectionResults[0].result;
    console.log(`Found ${pdfLinks.length} Hindi PDFs.`);
     // --- End scripting for PDF links ---


    if (!pdfLinks.length) throw new Error('No Hindi PDF links found on the month page');

    const fileBuffers = [];
    for (let i = 0; i < pdfLinks.length; i++) {
      const { href, text } = pdfLinks[i];
      // Ensure href is absolute
      const absoluteHref = new URL(href, monthUrl).href;
      console.log(`Downloading ${i + 1}/${pdfLinks.length}: ${text} (${absoluteHref})`);
      // Removed progress message
      const res = await fetch(absoluteHref); // Use absolute URL
      if (!res.ok) {
        console.warn(`Failed to download ${absoluteHref}: ${res.status}`);
        continue; // Skip failed downloads
      }
      const arrayBuffer = await res.arrayBuffer();
      fileBuffers.push(arrayBuffer);
    }

    if (!fileBuffers.length) throw new Error('Downloaded no valid PDFs');
    console.log('Merging PDFs...');

    const mergedPdf = await PDFDocument.create();
    for (const buf of fileBuffers) {
      try {
        const src = await PDFDocument.load(buf);
        const pages = await mergedPdf.copyPages(src, src.getPageIndices());
        pages.forEach(p => mergedPdf.addPage(p));
      } catch (loadErr) {
        console.warn("Skipping a PDF due to loading error:", loadErr.message);
      }
    }

    if (mergedPdf.getPageCount() === 0) {
        throw new Error("Failed to merge any PDFs. Check individual file downloads.");
    }

    const mergedBytes = await mergedPdf.save();

    // --- Convert Uint8Array to Base64 Data URL ---
    let binary = '';
    const len = mergedBytes.byteLength;
    for (let i = 0; i < len; i++) {
        binary += String.fromCharCode(mergedBytes[i]);
    }
    const base64Data = btoa(binary);
    const dataUrl = `data:application/pdf;base64,${base64Data}`;
    // --- End Data URL conversion ---

    // Construct filename from monthUrl or fallback to current date
    let filename;
    // Updated regex to be more flexible with separators and case
    const match = monthUrl.match(/advance[-_]?murli[-_]([a-zA-Z]+)[-_]([0-9]{4})/i);
    if (match) {
      const month = match[1].toLowerCase();
      const year = match[2];
      filename = `advance-murli-hindi-${month}-${year}.pdf`;
    } else {
      const now = new Date();
      // Use consistent month formatting
      const month = now.toLocaleString('default', { month: 'long' }).toLowerCase();
      const year = now.getFullYear();
      filename = `advance-murli-hindi-${month}-${year}.pdf`;
    }


    console.log(`Initiating download for: ${filename}`);
    await chrome.downloads.download({
      url: dataUrl, // Use the data URL instead of blob URL
      filename,
      saveAs: true // Prompt user where to save
    });
    // Removed progress message and sendResponse

  } catch (e) {
    console.error("Error during action click processing:", e);
    // Maybe notify the user via other means if needed, e.g., chrome.notifications API
    // Cannot use sendResponse here as there's no message listener context
  }
  // No need to return true here
});

// Ensure the old chrome.runtime.onMessage listener is removed or commented out.
/*
chrome.runtime.onMessage.addListener(async (msg, sender, sendResponse) => {
  // ... old code ...
});
*/
