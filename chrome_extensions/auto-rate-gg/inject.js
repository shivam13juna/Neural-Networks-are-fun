/* *****************  USER MUST EDIT SELECTORS BELOW  ***************** */
// Button that opens the rating UI (e.g. has star icon)
const OPEN_SELECTOR     = '#react-root > div > div > div > div.layout.m-layout > div > div.live.layout > div.meeting.layout__content.layout__content--transparent > div.meeting-main > div.m-header > div.m-header__actions > div:nth-child(1) > a > i';           // opener button
// After UI opens, click these:
const STAR_SELECTOR     = '#react-root > div > div.sr-modal.sr-modal--center.sr-modal--large.sr-modal--open.p-10 > div > div._2fKzMNF6cYlq9-KewCTCJ6 > div.eu7aj2zuIRCOZVf8U0eK0 > div:nth-child(5) > img'; // 5-star button
const TEXTBOX_SELECTOR  = '#react-root > div > div.sr-modal.sr-modal--center.sr-modal--large.sr-modal--open.p-10 > div > div._2fKzMNF6cYlq9-KewCTCJ6 > div._1hJTd9S9ODrUD9lkKSAMgU > textarea';              // comment box
const SUBMIT_SELECTOR   = '#react-root > div > div.sr-modal.sr-modal--center.sr-modal--large.sr-modal--open.p-10 > div > div._3TWODmj7Gz3dam-MhP3ZA1 > button';       // submit button
// After submit, close the feedback confirmation popup
const CLOSE_SELECTOR    = '#react-root > div > div.sr-modal.sr-modal--center.sr-modal--open._3CscLbgqWnl_n0trBnMJTM > div > div._160cWsFZvy39cggItvG1TA > a > i';    // close confirmation button
// Test-session setup selectors
const TEST_SETUP_SELECTOR = '#react-root > div > div > div > div.layout.m-layout > div > div > div.row.flex-c.m-t-20 > a:nth-child(7)';       // opens test setup UI
const ALT_TEST_SETUP_SELECTOR = '#root > div > div.layout.m-layout > div > div.m-upcoming > div.row.flex-c.m-t-20 > a:nth-child(7)'; // alternative test setup UI opener
const MIC_SELECTOR        = '#react-root > div > div > div > div.layout.m-layout > div > div.live.layout > div.m-login > div > form > div.aspect-ratio.av-preview > div.aspect-ratio__container > div > a.tappable.btn.btn-icon.m-btn.audio-indicator.av-preview__action.av-preview__action--audio.btn-light.btn-large.m-btn--default > i';             // mic icon in setup UI
const VIDEO_SELECTOR      = '#react-root > div > div > div > div.layout.m-layout > div > div.live.layout > div.m-login > div > form > div.aspect-ratio.av-preview > div.aspect-ratio__container > div > a.tappable.btn.btn-icon.m-btn.av-preview__action.av-preview__action--video.btn-light.btn-large.m-btn--default > i';           // video icon in setup UI
const JOIN_SELECTOR       = '#react-root > div > div > div > div.layout.m-layout > div > div.live.layout > div.m-login > div > form > div.m-login__container > div.row.m-login__footer > button';            // join session button in setup UI
/* ******************************************************************* */

(async function autoRate() {
  // 0) initiate test setup, then join session
  {
    let testSetupBtn = document.querySelector(TEST_SETUP_SELECTOR);
    if (!testSetupBtn) {
      console.warn('Auto-Rate GG: Could not find test-setup button with primary selector, trying alternative.');
      testSetupBtn = document.querySelector(ALT_TEST_SETUP_SELECTOR);
    }

    if (!testSetupBtn) {
      console.warn('Auto-Rate GG: Could not find test-setup button. Skipping pre-setup.');
    } else {
      testSetupBtn.click();
      // wait for setup UI to render
      await new Promise(r => setTimeout(r, 500));
      const micIcon = document.querySelector(MIC_SELECTOR);
      const videoIcon = document.querySelector(VIDEO_SELECTOR);
      if (micIcon) micIcon.click(); else console.warn('Auto-Rate GG: Cannot find mic icon');
      if (videoIcon) videoIcon.click(); else console.warn('Auto-Rate GG: Cannot find video icon');
      // wait before attempting to join session
      await new Promise(r => setTimeout(r, 500));
      // attempt to find and click join button, retry once after 500ms
      let joinBtn = document.querySelector(JOIN_SELECTOR);
      if (!joinBtn) {
        console.warn('Auto-Rate GG: Join button not found, retrying in 500ms...');
        await new Promise(r => setTimeout(r, 500));
        joinBtn = document.querySelector(JOIN_SELECTOR);
      }
      if (joinBtn) {
        joinBtn.click();
      } else {
        console.warn('Auto-Rate GG: Cannot find join button');
      }
      // wait for session to join
      await new Promise(r => setTimeout(r, 1000));
    }
  }
// 1) open the rating UI (up to 3 attempts, 1s apart)
let opener;
for (let attempt = 1; attempt <= 3; attempt++) {
    opener = document.querySelector(OPEN_SELECTOR);
    if (opener) break;
    console.warn(`Auto-Rate GG: Rating button not found (attempt ${attempt}), retrying in 1s…`);
    await new Promise(r => setTimeout(r, 1000));
}
if (!opener) {
    console.warn('Auto-Rate GG: Rating button not found after 3 attempts. Skipping rating.');
    return;
}
opener.click();
  // wait for rating UI to render
  await new Promise(resolve => setTimeout(resolve, 500));
  
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
  // 4) close confirmation popup
  await new Promise(resolve => setTimeout(resolve, 500));
  const closer = document.querySelector(CLOSE_SELECTOR);
  if (closer) {
    closer.click();
  } else {
    console.warn('Auto-Rate GG: Could not find close button. Check CLOSE_SELECTOR in inject.js');
  }
})();
