/*
 * Confirm and Exit – content script
 * Replace SELECTOR_* constants below with real CSS selectors for your site.
 */

(async () => {
  //                                     ▼▼ REPLACE THESE ▼▼
  const SELECTOR_END_BUTTON   = "#meeting-sidebar > div.right-dock.scroll > div:nth-child(2) > div > div > a";           // top‑level “End” button
  const SELECTOR_LEAVE_ALL_BUTTON = "body > div.react-root.react-root--auto.meeting-app > div > a.tappable.dropdown-item.btn.btn-danger"; // Button for "End meeting for all"
  const SELECTOR_DIALOG       = "#react-root > div > div > div > div.sr-modal.sr-modal--open.dialog > div.sr-modal__header > div.sr-modal__title";              // modal container (optional wait target)
  const SELECTOR_INPUT        = "#react-root > div > div > div > div.sr-modal.sr-modal--open.dialog > div.sr-modal__body > div.dialog__body > div > input";        // text box inside dialog
  const SELECTOR_SUBMIT       = "#react-root > div > div > div > div.sr-modal.sr-modal--open.dialog > div.sr-modal__body > div.dialog__actions > a.tappable.btn.dialog__action.btn-danger";           // final “End / Submit” button
  const CONFIRM_TEXT          = "confirm";                     // text to type (lower‑case)
  const RETRIES               = 5;                             // how many times to retry each step
  const RETRY_DELAY_MS        = 500;                           // delay between retries

  /** Helper: retry an async fn up to N times */
  async function retry(fn, label) {
    for (let i = 0; i < RETRIES; i++) {
      const ok = await fn();
      if (ok) return true;
      await new Promise(r => setTimeout(r, RETRY_DELAY_MS));
    }
    console.warn(`Confirm and Exit: ${label} – element not found after ${RETRIES} attempts`);
    alert(`Confirm and Exit failed at step: ${label}. Check console for details.`);
    return false;
  }

  /** Step 1: click End Meeting */
  if (!(await retry(() => {
    const btn = document.querySelector(SELECTOR_END_BUTTON); // Reverted to SELECTOR_END_BUTTON
    if (!btn) return false;
    btn.click();
    return true;
  }, "End button"))) return; // Reverted label

  /** Step 1.5: click Leave All Button */
  if (!(await retry(() => {
    const btn = document.querySelector(SELECTOR_LEAVE_ALL_BUTTON);
    if (!btn) return false;
    btn.click();
    return true;
  }, "Leave All button"))) return;

  /** Optional: wait until dialog appears (helps slow UIs) */
  // If a second, different dialog appears after "Leave All", a new SELECTOR and wait might be needed here.
  // For now, reusing SELECTOR_DIALOG or assuming the confirm input is now available.
  if (SELECTOR_DIALOG) {
    // This wait is now more critical: it's for the dialog expected to contain the input field.
    if (!(await retry(() => !!document.querySelector(SELECTOR_DIALOG), "confirmation dialog after Leave All button"))) return;
  }

  /** Step 2: type ‘confirm’ */
  if (!(await retry(() => {
    const input = document.querySelector(SELECTOR_INPUT);
    if (!input) return false;
    input.focus();
    input.value = CONFIRM_TEXT;
    input.dispatchEvent(new Event("input", { bubbles: true }));
    return true;
  }, "confirm box"))) return;

  /** Step 3: click Submit */
  if (!(await retry(() => {
    const btn = document.querySelector(SELECTOR_SUBMIT);
    if (!btn) return false;
    btn.click();
    return true;
  }, "Submit button"))) return;

  console.log("Confirm and Exit ✔ finished");
})();
