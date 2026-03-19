/*
 * Bookmark Automation - content script
 * Automatically adds bookmarks when "missing bookmarks" message is detected
 * Based on recorded browser steps for Scaler.com platform
 */

// CSS Selectors extracted from recorded steps for Scaler.com
const BOOKMARK_SELECTORS = {
  BOOKMARKS_TAB: "[data-cy='archived-meetings-sidebar-bookmark-tab'], [data-cy='archived-meetings-sidebar-bookmark-tab'] > div",
  VIDEO_SEEKBAR: "div.vp-controls__seekbar > div",
  // NOTE: CSS selectors for "Add Bookmark" shift after bookmarks exist, so we
  // find it by text content instead. See clickAddBookmark().
  ADD_BOOKMARK_FALLBACK: "div.p-10 > a, div.m-sidebar > div.layout a",
  SUBMIT_BUTTON: "div.archive-sidebar button, div.layout > form button, div.layout > form div.m-bookmark-item__footer button",
  SIDEBAR_CLOSE: "div.m-sidebar__header i, div.m-sidebar__header a"
};

// Updated seekbar selector based on your provided selector
const SEEKBAR_SELECTOR = "#root > div > div.layout.m-layout > div > div:nth-child(1) > div > div.archive-main > div.m-playlist > div.vp-container.archive-player > div.vp-controls > div > div.vp-controls__seekbar.vp-bookmarks-container > div > div.vp-seekbar-handle > div";

// Bookmark configuration with percentage-based positions
const BOOKMARKS = [
  { note: "Intro", percentage: 5 },           // 5% from beginning 
  { note: "Case-Study", percentage: 45 },     // Middle of the video
  { note: "Conclusion", percentage: 85 }      // Near the end (85%)
];

const RETRY_CONFIG = {
  MAX_RETRIES: 5,
  DELAY_MS: 500
};

/** Helper: retry an async function up to N times */
async function retryBookmarkAction(fn, label) {
  for (let i = 0; i < RETRY_CONFIG.MAX_RETRIES; i++) {
    try {
      const result = await fn();
      if (result) return true;
    } catch (error) {
      console.warn(`Bookmark Automation: ${label} - attempt ${i + 1} failed:`, error);
    }
    await new Promise(r => setTimeout(r, RETRY_CONFIG.DELAY_MS));
  }
  console.error(`Bookmark Automation: ${label} - failed after ${RETRY_CONFIG.MAX_RETRIES} attempts`);
  return false;
}

/** Helper: click an element with retry logic, trying multiple selectors if provided */
async function clickElement(selector, label) {
  return retryBookmarkAction(() => {
    // Handle multiple selectors separated by comma
    const selectors = selector.split(',').map(s => s.trim());
    
    for (const sel of selectors) {
      const element = document.querySelector(sel);
      if (element) {
        element.click();
        console.log(`✓ Clicked ${label} using selector: ${sel}`);
        return true;
      }
    }
    console.log(`✗ Could not find ${label} with any selector: ${selector}`);
    return false;
  }, `Click ${label}`);
}

/** Helper: click the "Add Bookmark" link by its text content.
 *  CSS selectors for this link change depending on how many bookmarks already
 *  exist, so matching by text is the most reliable approach. */
async function clickAddBookmark() {
  return retryBookmarkAction(() => {
    // Strategy 1: find <a> whose text is "Add Bookmark"
    const links = document.querySelectorAll('a');
    for (const link of links) {
      if (link.textContent.trim() === 'Add Bookmark') {
        link.click();
        console.log("✓ Clicked 'Add Bookmark' (found by text content)");
        return true;
      }
    }

    // Strategy 2: CSS fallback selectors from the recording
    const fallbacks = BOOKMARK_SELECTORS.ADD_BOOKMARK_FALLBACK.split(',').map(s => s.trim());
    for (const sel of fallbacks) {
      const el = document.querySelector(sel);
      if (el) {
        el.click();
        console.log(`✓ Clicked 'Add Bookmark' using fallback selector: ${sel}`);
        return true;
      }
    }

    console.log("✗ Could not find 'Add Bookmark' link");
    return false;
  }, 'Click Add Bookmark');
}

/** Helper: click on video seekbar at specific position */
async function clickSeekbar(position) {
  return retryBookmarkAction(() => {
    console.log(`🎯 Attempting to click seekbar at position: ${position}px`);
    
    // Use the same selectors as calculateSeekPosition for consistency
    const seekbarSelectors = [
      "div.vp-controls__seekbar.vp-bookmarks-container",  // Main seekbar container
      "div.vp-controls__seekbar",                         // Alternative seekbar
      ".vp-controls__seekbar",                            // Simplified selector
      BOOKMARK_SELECTORS.VIDEO_SEEKBAR,                  // Original selector
      SEEKBAR_SELECTOR                                    // Your specific selector
    ];
    
    let seekbar = null;
    for (const selector of seekbarSelectors) {
      seekbar = document.querySelector(selector);
      if (seekbar) {
        console.log(`✓ Found seekbar for clicking using: ${selector}`);
        break;
      }
    }
    
    if (!seekbar) {
      console.error("❌ Could not find seekbar for clicking");
      return false;
    }
    
    // Get the exact position and dimensions
    const rect = seekbar.getBoundingClientRect();
    console.log(`📐 Seekbar rect: left=${rect.left}, top=${rect.top}, width=${rect.width}, height=${rect.height}`);
    
    const clickX = rect.left + position;
    const clickY = rect.top + rect.height / 2;
    
    console.log(`🎯 Clicking at absolute coordinates: (${clickX}, ${clickY})`);
    
    // Create multiple types of events to ensure compatibility
    const events = [
      new MouseEvent('mousedown', {
        bubbles: true,
        cancelable: true,
        clientX: clickX,
        clientY: clickY
      }),
      new MouseEvent('click', {
        bubbles: true,
        cancelable: true,
        clientX: clickX,
        clientY: clickY
      }),
      new MouseEvent('mouseup', {
        bubbles: true,
        cancelable: true,
        clientX: clickX,
        clientY: clickY
      })
    ];
    
    // Dispatch all events
    events.forEach(event => seekbar.dispatchEvent(event));
    
    console.log(`✓ Dispatched click events at position ${position}px`);
    return true;
  }, `Click seekbar at position ${position}`);
}

/** Calculate position on seekbar based on percentage */
function calculateSeekPosition(percentage) {
  // Try to find the main seekbar container (not just the handle)
  const seekbarSelectors = [
    "div.vp-controls__seekbar.vp-bookmarks-container",  // Main seekbar container
    "div.vp-controls__seekbar",                         // Alternative seekbar
    ".vp-controls__seekbar",                            // Simplified selector
    SEEKBAR_SELECTOR,                                   // Your specific selector as fallback
    ".archive-player .vp-controls .vp-controls__seekbar"
  ];
  
  let seekbar = null;
  for (const selector of seekbarSelectors) {
    seekbar = document.querySelector(selector);
    if (seekbar) {
      console.log(`✓ Found seekbar for calculation using: ${selector}`);
      console.log(`📏 Seekbar dimensions: ${seekbar.offsetWidth}x${seekbar.offsetHeight}px`);
      break;
    }
  }
  
  if (!seekbar) {
    console.error("❌ Could not find seekbar element for position calculation");
    return 50; // Fallback to middle position
  }
  
  const seekbarWidth = seekbar.offsetWidth || seekbar.clientWidth;
  if (!seekbarWidth) {
    console.error("❌ Could not determine seekbar width");
    return 50; // Fallback
  }
  
  const position = Math.round((percentage / 100) * seekbarWidth);
  console.log(`📊 Calculated position: ${position}px for ${percentage}% of ${seekbarWidth}px seekbar`);
  
  return Math.max(5, Math.min(position, seekbarWidth - 5)); // Ensure we don't click at the very edges
}

/** Helper: verify that the video position has changed to approximately the expected percentage */
function verifyVideoPosition(expectedPercentage) {
  // First try to get actual video time
  const video = document.querySelector('video');
  if (video && video.duration) {
    const currentPercentage = (video.currentTime / video.duration) * 100;
    const timeDiff = Math.abs(currentPercentage - expectedPercentage);
    
    console.log(`⏰ Video verification: ${video.currentTime.toFixed(2)}s (${currentPercentage.toFixed(1)}%) - Expected: ${expectedPercentage}% - Diff: ${timeDiff.toFixed(1)}%`);
    
    // Allow some tolerance (±5% instead of ±10% for better accuracy)
    if (timeDiff <= 5) {
      console.log(`✅ Video position is correctly set to ${currentPercentage.toFixed(1)}% (within ±5% tolerance)`);
      return true;
    } else {
      console.log(`⚠️ Video position ${currentPercentage.toFixed(1)}% differs significantly from expected ${expectedPercentage}% (diff: ${timeDiff.toFixed(1)}%)`);
      return false;
    }
  }
  
  // Fallback: Try to find video time indicators in the UI
  const timeSelectors = [
    ".vp-current-time",
    ".current-time", 
    ".video-time",
    "[class*='time']:not([class*='total'])",
    ".vp-controls .time"
  ];
  
  for (const selector of timeSelectors) {
    const timeElement = document.querySelector(selector);
    if (timeElement) {
      console.log(`⏰ Video time display found: ${timeElement.textContent}`);
      return true; // At least we found a time indicator
    }
  }
  
  console.log("⚠️ Could not verify video position - no video element or time indicators found");
  return false; // Return false to indicate verification failed
}

/** Helper: wait for video to be ready for seeking */
async function waitForVideoReady() {
  return new Promise((resolve) => {
    const video = document.querySelector('video');
    if (!video) {
      console.log("⚠️ No video element found");
      resolve(false);
      return;
    }
    
    // Check if video is already ready
    if (video.readyState >= 2 && video.duration && !isNaN(video.duration)) {
      console.log(`✓ Video ready: duration=${video.duration.toFixed(2)}s, readyState=${video.readyState}`);
      resolve(true);
      return;
    }
    
    let attempts = 0;
    const maxAttempts = 30; // 3 seconds max wait
    
    const checkReady = () => {
      attempts++;
      
      if (video.readyState >= 2 && video.duration && !isNaN(video.duration)) {
        console.log(`✓ Video became ready after ${attempts} attempts: duration=${video.duration.toFixed(2)}s`);
        resolve(true);
        return;
      }
      
      if (attempts >= maxAttempts) {
        console.log(`⚠️ Video readiness timeout after ${attempts} attempts`);
        resolve(false);
        return;
      }
      
      setTimeout(checkReady, 100);
    };
    
    setTimeout(checkReady, 100);
  });
}

/** Helper: Set video time directly by finding the video element */
async function setVideoTime(percentage) {
  return new Promise((resolve) => {
    // Try to find the video element
    const videoSelectors = [
      'video',
      '.video-player video',
      '.vp-container video',
      '[data-testid="video"]',
      '.archive-player video'
    ];
    
    let video = null;
    for (const selector of videoSelectors) {
      video = document.querySelector(selector);
      if (video) {
        console.log(`✓ Found video element using: ${selector}`);
        break;
      }
    }
    
    if (!video) {
      console.log("⚠️ Could not find video element - falling back to seekbar click");
      resolve(false);
      return;
    }
    
    // Calculate the target time based on percentage
    const duration = video.duration;
    if (!duration || isNaN(duration)) {
      console.log("⚠️ Video duration not available - falling back to seekbar click");
      resolve(false);
      return;
    }
    
    const targetTime = (percentage / 100) * duration;
    console.log(`🎬 Setting video time to ${targetTime.toFixed(2)}s (${percentage}% of ${duration.toFixed(2)}s)`);
    
    // CRITICAL FIX: Improved video state management to prevent race conditions
    const wasPlaying = !video.paused;
    
    // Always pause first and wait for pause to complete
    if (wasPlaying) {
      video.pause();
      console.log("⏸️ Paused video to set position");
    }
    
    // Wait a moment for pause to settle
    setTimeout(() => {
      // Set the video time
      video.currentTime = targetTime;
      
      // Trigger seeking event
      video.dispatchEvent(new Event('seeking'));
      
      let attempts = 0;
      const maxAttempts = 20; // 2 seconds max wait
      
      // Wait for the time to be set and seeked event
      const checkTime = () => {
        attempts++;
        const currentTime = video.currentTime;
        const timeDiff = Math.abs(currentTime - targetTime);
        
        console.log(`🕐 Check ${attempts}: video at ${currentTime.toFixed(2)}s, target ${targetTime.toFixed(2)}s (diff: ${timeDiff.toFixed(2)}s)`);
        
        if (timeDiff < 1 || attempts >= maxAttempts) {
          if (timeDiff < 1) {
            console.log(`✓ Video time successfully set to ${currentTime.toFixed(2)}s (${((currentTime/duration)*100).toFixed(1)}%)`);
          } else {
            console.log(`⚠️ Video time setting timed out after ${attempts} attempts`);
          }
          
          // Keep video paused during bookmark automation to prevent
          // AbortError race conditions between consecutive bookmarks.
          
          resolve(timeDiff < 1);
        } else {
          setTimeout(checkTime, 100);
        }
      };
      
      // Add event listener for seeked event
      const onSeeked = () => {
        console.log(`🎯 Video seeked to ${video.currentTime.toFixed(2)}s`);
        video.removeEventListener('seeked', onSeeked);
      };
      video.addEventListener('seeked', onSeeked, { once: true });
      
      setTimeout(checkTime, 100);
    }, 200); // Wait 200ms for pause to settle
  });
}

/** Clear leftover text from the new bookmark form (only the unsaved "new" form, not existing bookmarks) */
async function clearNewBookmarkForm() {
  // Only target the explicit "new bookmark" form — never use a broad selector
  // that could match existing saved bookmarks and accidentally wipe their text.
  const newForm = document.querySelector('form.m-bookmark-item:not(.m-bookmark-item--shadow)');
  if (!newForm) return;

  const textarea = newForm.querySelector('textarea');
  if (!textarea || !textarea.value || textarea.value.trim() === '') return;

  console.log("🧹 Clearing leftover content from new bookmark form...");
  const nativeSetter = Object.getOwnPropertyDescriptor(HTMLTextAreaElement.prototype, 'value')?.set;
  if (nativeSetter) {
    nativeSetter.call(textarea, '');
  } else {
    textarea.value = '';
  }
  textarea.dispatchEvent(new Event('input', { bubbles: true }));
  textarea.dispatchEvent(new Event('change', { bubbles: true }));
  await new Promise(r => setTimeout(r, 500));
}

/** Main function: Add a single bookmark */
async function addSingleBookmark(bookmarkConfig) {
  const { note, percentage } = bookmarkConfig;

  console.log(`\n🔖 Adding bookmark: "${note}" at ${percentage}%`);

  // Clear any leftover text from a previous failed attempt
  await clearNewBookmarkForm();

  // Step 1: Seek video to the target position.
  // We set video.currentTime AND click the seekbar so the platform's internal
  // bookmark-timestamp tracker is updated (it listens to seekbar interactions).
  console.log(`🎯 Step 1: Setting video to ${percentage}% position...`);

  const videoReady = await waitForVideoReady();
  if (!videoReady) {
    console.log("⚠️ Video not ready, but continuing with positioning attempt...");
  }

  // 1a: Set video.currentTime directly
  await setVideoTime(percentage);

  // 1b: Also click the seekbar at the matching position so the platform
  //     registers the change in its own state (not just the <video> element).
  const seekPosition = calculateSeekPosition(percentage);
  await clickSeekbar(seekPosition);

  // 1c: Wait for the native 'seeked' event to confirm the video has arrived
  console.log("⏳ Waiting for video to reach target position...");
  await new Promise((resolve) => {
    const video = document.querySelector('video');
    if (!video) { resolve(); return; }

    const targetTime = (percentage / 100) * video.duration;
    const alreadyThere = Math.abs(video.currentTime - targetTime) < 1;
    if (alreadyThere) {
      console.log(`✓ Video already at target position (${video.currentTime.toFixed(2)}s)`);
      resolve();
      return;
    }

    const onSeeked = () => {
      video.removeEventListener('seeked', onSeeked);
      console.log(`✓ Video seeked event fired — now at ${video.currentTime.toFixed(2)}s`);
      resolve();
    };
    video.addEventListener('seeked', onSeeked);

    // Safety timeout so we don't hang forever
    setTimeout(() => {
      video.removeEventListener('seeked', onSeeked);
      console.log(`⚠️ Seeked event timeout — video at ${video.currentTime.toFixed(2)}s`);
      resolve();
    }, 5000);
  });

  // 1d: Extra settle time for the platform to sync its internal state
  await new Promise(r => setTimeout(r, 2000));
  verifyVideoPosition(percentage);

  // Step 2: Click "Add Bookmark" to open the bookmark form
  console.log("📝 Step 2: Clicking 'Add Bookmark' button...");
  if (!(await clickAddBookmark())) {
    console.error("❌ Could not find 'Add Bookmark' button");
    return false;
  }

  // Wait for the form to appear
  await new Promise(r => setTimeout(r, 1000));

  // Step 2b: Find the bookmark form (textarea)
  console.log("📝 Step 2b: Finding bookmark form...");
  const formFound = await retryBookmarkAction(() => {
    const ta = document.querySelector('form textarea, form.m-bookmark-item textarea, div.layout form textarea');
    if (ta) {
      console.log("✓ Found bookmark textarea");
      return true;
    }
    return false;
  }, 'Find bookmark textarea');

  if (!formFound) {
    console.error("❌ Could not find bookmark textarea");
    return false;
  }

  const formEl = document.querySelector('form textarea, form.m-bookmark-item textarea, div.layout form textarea')?.closest('form');
  const textarea = formEl?.querySelector('textarea');
  if (!textarea) {
    console.error("❌ Could not find textarea in new bookmark form");
    return false;
  }

  // Step 3: Enter note text
  console.log("✏️ Step 3: Entering bookmark note...");
  textarea.focus();
  const nativeSetter = Object.getOwnPropertyDescriptor(HTMLTextAreaElement.prototype, 'value')?.set;
  if (nativeSetter) {
    nativeSetter.call(textarea, note);
  } else {
    textarea.value = note;
  }
  textarea.dispatchEvent(new Event('input', { bubbles: true }));
  textarea.dispatchEvent(new Event('change', { bubbles: true }));
  console.log(`✓ Set bookmark note to "${note}"`);

  await new Promise(r => setTimeout(r, 800));

  // Step 4: Submit — the form says "Enter to save, Shift+Enter for new line"
  console.log("✅ Step 4: Submitting bookmark...");

  let submitSuccess = false;
  for (let attempt = 1; attempt <= 5; attempt++) {
    console.log(`🔄 Submit attempt ${attempt}/5...`);

    // Strategy 1: Click the submit button (matches recorded selectors)
    const submitSelectors = BOOKMARK_SELECTORS.SUBMIT_BUTTON.split(',').map(s => s.trim());
    let clicked = false;
    for (const sel of submitSelectors) {
      const submitBtn = document.querySelector(sel);
      if (submitBtn) {
        submitBtn.click();
        console.log(`✓ Clicked submit button using: ${sel}`);
        clicked = true;
        break;
      }
    }
    // Also try the button inside the form itself
    if (!clicked) {
      const formBtn = formEl.querySelector('button');
      if (formBtn) {
        formBtn.click();
        console.log("✓ Clicked form button fallback");
        clicked = true;
      }
    }

    // Strategy 2: Press Enter in the textarea as backup
    if (!clicked) {
      textarea.dispatchEvent(new KeyboardEvent('keydown', {
        key: 'Enter', code: 'Enter', keyCode: 13, which: 13,
        bubbles: true, cancelable: true
      }));
      console.log("✓ Dispatched Enter keydown as fallback");
    }

    await new Promise(r => setTimeout(r, 1500));

    // Verify: textarea should be cleared or form replaced after successful submit
    const currentTextarea = formEl.querySelector('textarea');
    const formGone = !formEl.isConnected;
    const textCleared = currentTextarea && (!currentTextarea.value || currentTextarea.value.trim() === '');

    if (formGone || textCleared) {
      console.log(`✅ Submit succeeded on attempt ${attempt}`);
      submitSuccess = true;
      break;
    }

    console.log(`⚠️ Submit attempt ${attempt} - form still has content, retrying...`);

    // Re-enter the value in case React lost it
    if (currentTextarea && attempt < 5) {
      if (nativeSetter) {
        nativeSetter.call(currentTextarea, note);
      }
      currentTextarea.dispatchEvent(new Event('input', { bubbles: true }));
      currentTextarea.dispatchEvent(new Event('change', { bubbles: true }));
      await new Promise(r => setTimeout(r, 500));
    }
  }

  if (!submitSuccess) {
    console.error(`❌ Failed to submit bookmark "${note}" after 5 attempts`);
    await clearNewBookmarkForm();
    return false;
  }

  // Wait for the bookmark to be processed
  await new Promise(r => setTimeout(r, 1500));

  console.log(`✅ Bookmark "${note}" added successfully at ${percentage}%`);
  return true;
}

/** Main function: Add all bookmarks */
async function addBookmarks() {
  console.log("🔖 Starting bookmark automation with improved positioning...");
  console.log("📊 Bookmark schedule:", BOOKMARKS.map(b => `"${b.note}" at ${b.percentage}%`).join(", "));
  
  try {
    // Step 1: Click on "Bookmarks & Notes" tab to open the sidebar
    console.log("\n📂 Opening bookmarks sidebar...");
    if (!(await clickElement(BOOKMARK_SELECTORS.BOOKMARKS_TAB, "Bookmarks & Notes tab"))) {
      console.error("❌ Failed to open bookmarks sidebar");
      return false;
    }
    
    // Wait for sidebar to open
    await new Promise(r => setTimeout(r, 1500));
    
    // Step 2: Add each bookmark with improved positioning
    let successCount = 0;
    for (let i = 0; i < BOOKMARKS.length; i++) {
      const bookmark = BOOKMARKS[i];
      console.log(`\n${'='.repeat(50)}`);
      console.log(`🔖 Processing bookmark ${i + 1}/${BOOKMARKS.length}: "${bookmark.note}" at ${bookmark.percentage}%`);
      console.log(`${'='.repeat(50)}`);
      
      const success = await addSingleBookmark(bookmark);
      if (success) {
        successCount++;
        console.log(`✅ Bookmark ${i + 1} "${bookmark.note}" completed successfully!`);
      } else {
        console.error(`❌ Failed to add bookmark ${i + 1}: "${bookmark.note}" at ${bookmark.percentage}%`);
      }
      
      // CRITICAL FIX: Enhanced wait between bookmarks to ensure proper processing and DOM stability
      if (i < BOOKMARKS.length - 1) {
        console.log(`⏳ Waiting 4 seconds before next bookmark to ensure DOM stability...`);
        await new Promise(r => setTimeout(r, 4000)); // Increased from 3 seconds to 4 seconds
      }
    }
    
    console.log(`\n${'='.repeat(60)}`);
    console.log(`📊 BOOKMARK AUTOMATION SUMMARY`);
    console.log(`${'='.repeat(60)}`);
    console.log(`✅ Success: ${successCount}/${BOOKMARKS.length} bookmarks added`);
    
    if (successCount === BOOKMARKS.length) {
      console.log(`🎉 All bookmarks created successfully!`);
    } else {
      console.log(`⚠️ ${BOOKMARKS.length - successCount} bookmarks failed to create`);
    }
    
    // Step 3: Pause the video
    const video = document.querySelector('video');
    if (video && !video.paused) {
      video.pause();
      console.log("⏸️ Video paused");
    }

    // Step 4: Close the sidebar (optional)
    console.log("\n📂 Closing sidebar...");
    await clickElement(BOOKMARK_SELECTORS.SIDEBAR_CLOSE, "close sidebar");

    console.log("🔖 Bookmark automation completed!");
    return successCount === BOOKMARKS.length;
    
  } catch (error) {
    console.error("❌ Bookmark automation failed with error:", error);
    return false;
  }
}

// Auto-execute bookmark automation when this script is injected
(async function() {
  console.log("🔖 Bookmark automation script loaded");
  if (typeof addBookmarks === 'function') {
    await addBookmarks();
  }
})();
