/* *****************  USER MUST EDIT SELECTORS BELOW  ***************** */
// Button that opens the rating UI (e.g. has star icon)
const OPEN_SELECTOR     = '#react-root > div > div > div > div.layout.m-layout > div > div.live.layout > div.meeting.layout__content.layout__content--transparent > div.meeting-main > div.m-header > div.m-header__actions > div:nth-child(1) > a > i';           // opener button
// After UI opens, click these:
const STAR_SELECTOR     = '#react-root > div > div.sr-modal.sr-modal--center.sr-modal--large.sr-modal--open.p-10 > div > div._2fKzMNF6cYlq9-KewCTCJ6 > div.eu7aj2zuIRCOZVf8U0eK0 > div:nth-child(5) > img'; // 5-star button
const TEXTBOX_SELECTOR  = '#react-root > div > div.sr-modal.sr-modal--center.sr-modal--large.sr-modal--open.p-10 > div > div._2fKzMNF6cYlq9-KewCTCJ6 > div._1hJTd9S9ODrUD9lkKSAMgU > textarea';              // comment box
const SUBMIT_SELECTOR   = '#react-root > div > div.sr-modal.sr-modal--center.sr-modal--large.sr-modal--open.p-10 > div > div._3TWODmj7Gz3dam-MhP3ZA1 > button';       // submit button
// After submit, close the feedback confirmation popup
const CLOSE_SELECTOR    = '#react-root > div > div.sr-modal.sr-modal--center.sr-modal--open._3CscLbgqWnl_n0trBnMJTM > div > div._160cWsFZvy39cggItvG1TA > a > i';    // close confirmation button
/* ******************************************************************* */

(async function autoRate() {
  // 0) open the rating UI
  const opener = document.querySelector(OPEN_SELECTOR);
  if (!opener) {
    alert('Auto-Rate GG:\nCould not find opener element.\nCheck OPEN_SELECTOR in inject.js');
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
