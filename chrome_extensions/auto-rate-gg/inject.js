/* *****************  USER MUST EDIT SELECTORS BELOW  ***************** */
// Button that opens the rating UI (e.g. has star icon)
const OPEN_SELECTOR     = '#react-root > div > div > div > div.layout.m-layout > div > div.live.layout > div.meeting.layout__content.layout__content--transparent > div.meeting-main > div.m-header > div.m-header__actions > div:nth-child(1) > a > i';           // opener button
// After UI opens, click these:
const STAR_SELECTOR     = '#react-root > div > div.sr-modal.sr-modal--center.sr-modal--large.sr-modal--open.p-10 > div > div._2fKzMNF6cYlq9-KewCTCJ6 > div.eu7aj2zuIRCOZVf8U0eK0 > div:nth-child(5) > img'; // 5-star button
const TEXTBOX_SELECTOR  = '#react-root > div > div.sr-modal.sr-modal--center.sr-modal--large.sr-modal--open.p-10 > div > div._2fKzMNF6cYlq9-KewCTCJ6 > div._1hJTd9S9ODrUD9lkKSAMgU > textarea';              // comment box
const SUBMIT_SELECTOR   = '#react-root > div > div.sr-modal.sr-modal--center.sr-modal--large.sr-modal--open.p-10 > div > div._3TWODmj7Gz3dam-MhP3ZA1 > button';       // submit button
// After submit, close the feedback confirmation popup
const CLOSE_SELECTOR    = '#react-root > div > div.sr-modal.sr-modal--center.sr-modal--open._3CscLbgqWnl_n0trBnMJTM > div > div._160cWsFZvy39cggItvG1TA > a > i';    // close confirmation button
// Settings panel selectors for disabling chat cooldown
const SETTINGS_BUTTON_SELECTOR = '[data-cy="meeting-settings-button"] > i';           // settings button
const ADMIN_TAB_SELECTOR = 'a:nth-of-type(5) > span.m-setting-panels__label';        // admin tab (from recording)
const ADMIN_TAB_SELECTOR_ALT = 'span.m-setting-panels__label';                       // alternative admin tab selector
const ADMIN_TAB_SELECTOR_XPATH = 'a.m-setting-panels__item span.m-setting-panels__label'; // xpath-based fallback
const CHAT_COOLDOWN_SELECTOR = 'div.m-settings__sections > div > label:nth-of-type(1) input'; // chat cooldown checkbox
const SETTINGS_CLOSE_SELECTOR = '[data-testid="backdrop"]';                          // close settings by clicking backdrop
const POPUP_DIALOG_CLOSE_SELECTOR = '#react-root > div > div > div > div.sr-modal.sr-modal--open.m-modal.m-settings > div > div > div.m-settings__main.m-settings__main--active > div.m-settings__header > span.hide-in-mobile > div > a > i'; // close popup dialog cross icon
// Test-session setup selectors
const TEST_SETUP_SELECTOR = '#react-root > div > div > div > div.layout.m-layout > div > div > div.row.flex-c.m-t-20 > a:nth-child(7)';       // opens test setup UI
const ALT_TEST_SETUP_SELECTOR = '#root > div > div.layout.m-layout > div > div.m-upcoming > div.row.flex-c.m-t-20 > a:nth-child(7)'; // alternative test setup UI opener
const MIC_SELECTOR        = '#react-root > div > div > div > div.layout.m-layout > div > div.live.layout > div.m-login > div > form > div.aspect-ratio.av-preview > div.aspect-ratio__container > div > a.tappable.btn.btn-icon.m-btn.audio-indicator.av-preview__action.av-preview__action--audio.btn-light.btn-large.m-btn--default > i';             // mic icon in setup UI
const VIDEO_SELECTOR      = '#react-root > div > div > div > div.layout.m-layout > div > div.live.layout > div.m-login > div > form > div.aspect-ratio.av-preview > div.aspect-ratio__container > div > a.tappable.btn.btn-icon.m-btn.av-preview__action.av-preview__action--video.btn-light.btn-large.m-btn--default > i';           // video icon in setup UI
const JOIN_SELECTOR       = '#react-root > div > div > div > div.layout.m-layout > div > div.live.layout > div.m-login > div > form > div.m-login__container > div.row.m-login__footer > button';            // join session button in setup UI
/* ******************************************************************* */

(async function autoRate() {
  // 0) initiate test setup, then join session (attempt up to 5 times)
  {
    // attempt to find and click test-setup button (up to 5 attempts, 500ms apart)
    let testSetupBtn;
    for (let attempt = 1; attempt <= 5; attempt++) {
      testSetupBtn = document.querySelector(TEST_SETUP_SELECTOR) || document.querySelector(ALT_TEST_SETUP_SELECTOR);
      if (testSetupBtn) break;
      console.warn(`Auto-Rate GG: Test-setup button not found (attempt ${attempt}), retrying in 500ms...`);
      await new Promise(r => setTimeout(r, 500));
    }
    if (!testSetupBtn) {
      console.warn('Auto-Rate GG: Could not find test-setup button after 5 attempts. Skipping pre-setup.');
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
      // attempt to find and click join button (up to 5 attempts, 500ms apart)
      let joinBtn;
      for (let attempt = 1; attempt <= 5; attempt++) {
        joinBtn = document.querySelector(JOIN_SELECTOR);
        if (joinBtn) break;
        console.warn(`Auto-Rate GG: Join button not found (attempt ${attempt}), retrying in 500ms...`);
        await new Promise(r => setTimeout(r, 500));
      }
      if (joinBtn) {
        joinBtn.click();
      } else {
        console.warn('Auto-Rate GG: Cannot find join button after 5 attempts');
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
    console.warn('Auto-Rate GG: Rating button not found after 3 attempts. Skipping rating, but continuing with chat cooldown workflow.');
  } else {
    // Rating workflow
    opener.click();
    // wait for rating UI to render
    await new Promise(resolve => setTimeout(resolve, 500));
    
    const star   = document.querySelector(STAR_SELECTOR);
    const box    = document.querySelector(TEXTBOX_SELECTOR);
    const submit = document.querySelector(SUBMIT_SELECTOR);

    if (!star || !box || !submit) {
      console.warn('Auto-Rate GG: Could not find rating elements. Skipping rating, but continuing with chat cooldown workflow.');
    } else {
      // 2) click 5-star
      star.click();

      // 3) enter message
      box.focus();
      box.value = 'GG';
      box.dispatchEvent(new Event('input', { bubbles: true }));

      // 4) click submit
      submit.click();
      
      // 5) close confirmation popup
      await new Promise(resolve => setTimeout(resolve, 500));
      const closer = document.querySelector(CLOSE_SELECTOR);
      if (closer) {
        closer.click();
      } else {
        console.warn('Auto-Rate GG: Could not find close button. Check CLOSE_SELECTOR in inject.js');
      }
    }
  }

  // 6) Open settings to disable chat cooldown
  const settingsBtn = document.querySelector(SETTINGS_BUTTON_SELECTOR);
  if (settingsBtn) {
    settingsBtn.click();
    console.log('Auto-Rate GG: Settings panel opened');
    
    // Wait for settings panel UI to render
    await new Promise(resolve => setTimeout(resolve, 300));
    
    // 7) Click Admin tab
    let adminTab = document.querySelector(ADMIN_TAB_SELECTOR);
    
    // If first selector doesn't work, try alternative
    if (!adminTab) {
      adminTab = document.querySelector(ADMIN_TAB_SELECTOR_ALT);
    }
    
    // Try xpath-based selector as fallback
    if (!adminTab) {
      adminTab = document.querySelector(ADMIN_TAB_SELECTOR_XPATH);
    }
    
    // If still not found, try finding by text content "Admin"
    if (!adminTab) {
      const allTabs = document.querySelectorAll('span.m-setting-panels__label');
      adminTab = Array.from(allTabs).find(tab => 
        tab.textContent && tab.textContent.trim().toLowerCase() === 'admin'
      );
    }
    
    console.log('Auto-Rate GG: Admin tab found:', !!adminTab);
    console.log('Auto-Rate GG: Admin tab text:', adminTab ? adminTab.textContent : 'N/A');
    
    if (adminTab) {
      adminTab.click();
      console.log('Auto-Rate GG: Admin tab clicked');
      
      // 8) Check and disable chat cooldown if enabled
      const cooldownCheckbox = document.querySelector(CHAT_COOLDOWN_SELECTOR);
      if (cooldownCheckbox) {
        // Multiple ways to check if cooldown is enabled
        const isEnabled = cooldownCheckbox.checked || 
                         cooldownCheckbox.getAttribute('aria-checked') === 'true' ||
                         cooldownCheckbox.getAttribute('checked') !== null;
        
        console.log('Auto-Rate GG: Chat cooldown current state - enabled:', isEnabled);
        
        if (isEnabled) {
          cooldownCheckbox.click();
          console.log('Auto-Rate GG: Chat cooldown disabled (was enabled)');
        } else {
          console.log('Auto-Rate GG: Chat cooldown already disabled, no action needed');
        }
        
        // 9) Close settings panel by clicking backdrop
        const settingsClose = document.querySelector(SETTINGS_CLOSE_SELECTOR);
        if (settingsClose) {
          settingsClose.click();
          console.log('Auto-Rate GG: Settings panel closed (backdrop clicked)');
          
          // 10) Close the popup dialog using the cross icon
          const popupDialogClose = document.querySelector(POPUP_DIALOG_CLOSE_SELECTOR);
          if (popupDialogClose) {
            popupDialogClose.click();
            console.log('Auto-Rate GG: Popup dialog closed (cross icon clicked)');
          } else {
            console.warn('Auto-Rate GG: Could not find popup dialog close button');
          }
        } else {
          console.warn('Auto-Rate GG: Could not find backdrop to close settings');
        }
      } else {
        console.warn('Auto-Rate GG: Could not find chat cooldown checkbox');
      }
    } else {
      console.warn('Auto-Rate GG: Could not find Admin tab');
    }
  } else {
    console.warn('Auto-Rate GG: Could not find settings button');
  }
})();
