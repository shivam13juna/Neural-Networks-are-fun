/*
 * Bookmark Automation - content script
 * Automatically adds bookmarks when "missing bookmarks" message is detected
 * Based on recorded browser steps for Scaler.com platform
 */

// CSS Selectors extracted from recorded steps for Scaler.com
const BOOKMARK_SELECTORS = {
  BOOKMARKS_TAB: "[data-cy='archived-meetings-sidebar-bookmark-tab'] > div, .archive-sidebar .bookmark-tab, [role='tab']:contains('Bookmark')",
  ADD_BOOKMARK_BUTTON: "div.m-sidebar > div.layout a, div.p-10 > a, .add-bookmark-btn, button[title*='bookmark'], a[title*='bookmark']", // Multiple possible selectors
  NOTE_INPUT: "textarea, div.layout > form textarea, input[placeholder*='note'], textarea[placeholder*='note']", // Note text input field
  VIDEO_SEEKBAR: "div.vp-controls__seekbar > div, .seekbar, .video-progress", // Video timeline for positioning
  // CRITICAL FIX: Enhanced submit button selector order with more reliable patterns
  SUBMIT_BUTTON: "div.layout > form div.m-bookmark-item__footer i, div.layout > form button[type='submit'], div.archive-sidebar button, button[type='submit'], .submit-bookmark, .save-bookmark, form button:last-child, .m-bookmark-item__footer button, .m-bookmark-item__footer i", // Submit bookmark button with enhanced reliability
  SIDEBAR_CLOSE: "div.m-sidebar__header i, .close-sidebar, .sidebar-close" // Close sidebar button
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

/** Helper: set input value with proper event triggering, trying multiple selectors */
async function setInputValue(selector, value, label) {
  return retryBookmarkAction(() => {
    // Handle multiple selectors separated by comma
    const selectors = selector.split(',').map(s => s.trim());
    
    for (const sel of selectors) {
      const input = document.querySelector(sel);
      if (input) {
        input.focus();

        // Use native setter for React-controlled inputs.
        // React overrides the value property; setting .value directly
        // doesn't trigger React's state update, so the form sees empty text.
        const proto = input.tagName === 'TEXTAREA'
            ? HTMLTextAreaElement.prototype
            : HTMLInputElement.prototype;
        const nativeSetter = Object.getOwnPropertyDescriptor(proto, 'value')?.set;
        if (nativeSetter) {
          nativeSetter.call(input, value);
        } else {
          input.value = value;
        }

        // Dispatch events that React's synthetic event system detects
        input.dispatchEvent(new Event('input', { bubbles: true }));
        input.dispatchEvent(new Event('change', { bubbles: true }));
        
        console.log(`✓ Set ${label} to "${value}" using selector: ${sel}`);
        return true;
      }
    }
    console.log(`✗ Could not find ${label} input with any selector: ${selector}`);
    return false;
  }, `Set ${label} input`);
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

/** Dismiss any open bookmark form to prevent cascading failures */
async function dismissOpenForm() {
  const textarea = document.querySelector(BOOKMARK_SELECTORS.NOTE_INPUT);
  if (!textarea) return;

  // Check if form has unsaved content
  const formEl = textarea.closest('form');
  if (!formEl) return;

  console.log("🧹 Dismissing leftover bookmark form...");
  // Try pressing Escape to cancel
  document.dispatchEvent(new KeyboardEvent('keydown', { key: 'Escape', bubbles: true }));
  await new Promise(r => setTimeout(r, 500));

  // If form is still there, try clicking outside or a cancel button
  const stillOpen = document.querySelector(BOOKMARK_SELECTORS.NOTE_INPUT)?.closest('form');
  if (stillOpen) {
    const cancelBtn = stillOpen.querySelector('button:not([type="submit"]), a.cancel, .cancel, i.close');
    if (cancelBtn) {
      cancelBtn.click();
      await new Promise(r => setTimeout(r, 500));
    }
  }
}

/** Main function: Add a single bookmark */
async function addSingleBookmark(bookmarkConfig) {
  const { note, percentage } = bookmarkConfig;

  console.log(`\n🔖 Adding bookmark: "${note}" at ${percentage}%`);

  // Dismiss any leftover form from a previous failed attempt
  await dismissOpenForm();

  // Step 1: Click "Add Bookmark" button
  console.log("📌 Step 1: Clicking Add Bookmark button...");
  if (!(await clickElement(BOOKMARK_SELECTORS.ADD_BOOKMARK_BUTTON, "Add Bookmark button"))) {
    return false;
  }
  
  // CRITICAL FIX: Enhanced wait for bookmark form to appear and stabilize
  console.log("⏳ Waiting for bookmark form to appear and stabilize...");
  await new Promise(r => setTimeout(r, 1200)); // Increased from 800ms
  
  // Step 2: Set video position FIRST (before entering note)
  console.log(`🎯 Step 2: Setting video to ${percentage}% position...`);
  
  // Wait for video to be ready
  const videoReady = await waitForVideoReady();
  if (!videoReady) {
    console.log("⚠️ Video not ready, but continuing with positioning attempt...");
  }
  
  // Try to set video time directly first
  const videoTimeSet = await setVideoTime(percentage);
  
  if (!videoTimeSet) {
    // Fallback: Click on video seekbar to set position
    console.log("🔄 Falling back to seekbar click method...");
    const seekPosition = calculateSeekPosition(percentage);
    if (!(await clickSeekbar(seekPosition))) {
      console.error(`❌ Failed to set video position for ${percentage}%`);
      return false;
    }
  }
  
  // CRITICAL FIX: Enhanced wait for position to settle and verify
  console.log("⏳ Waiting for video position to settle...");
  await new Promise(r => setTimeout(r, 2000)); // Increased from 1500ms
  const positionCorrect = verifyVideoPosition(percentage);
  
  if (!positionCorrect) {
    console.log(`⚠️ Video position verification failed, but continuing...`);
  }
  
  // Step 3: Enter note text AFTER setting position
  console.log("✏️ Step 3: Entering bookmark note...");
  if (!(await setInputValue(BOOKMARK_SELECTORS.NOTE_INPUT, note, "bookmark note"))) {
    return false;
  }
  
  // CRITICAL FIX: Enhanced wait before submitting to ensure UI stability
  await new Promise(r => setTimeout(r, 800)); // Increased from 500ms
  
  // Step 4: Submit the bookmark with enhanced retry logic
  console.log("✅ Step 4: Submitting bookmark...");
  
  let submitSuccess = false;
  for (let attempt = 1; attempt <= 5; attempt++) {
    console.log(`🔄 Submit attempt ${attempt}/5...`);

    // Strategy 1: click the submit button/icon
    await clickElement(BOOKMARK_SELECTORS.SUBMIT_BUTTON, "submit bookmark");

    // Strategy 2: try form.requestSubmit() as backup
    const formEl = document.querySelector('div.layout > form, .m-bookmark-item form');
    if (formEl && attempt >= 2) {
      try {
        formEl.requestSubmit();
        console.log("🔄 Tried form.requestSubmit() as backup");
      } catch (e) {
        // requestSubmit may not be available or may throw
      }
    }

    await new Promise(r => setTimeout(r, 1500));

    // Verify: check if form textarea is gone or cleared (not just any textarea)
    const textarea = document.querySelector(BOOKMARK_SELECTORS.NOTE_INPUT);
    const formGone = !textarea?.closest('form');
    const textCleared = textarea && (!textarea.value || textarea.value.trim() === '');

    if (formGone || textCleared) {
      console.log(`✅ Submit succeeded on attempt ${attempt}`);
      submitSuccess = true;
      break;
    }

    console.log(`⚠️ Submit attempt ${attempt} - form still has content, retrying...`);

    // Re-enter the value in case React lost it
    if (textarea && attempt < 5) {
      const proto = textarea.tagName === 'TEXTAREA'
          ? HTMLTextAreaElement.prototype
          : HTMLInputElement.prototype;
      const nativeSetter = Object.getOwnPropertyDescriptor(proto, 'value')?.set;
      if (nativeSetter) {
        nativeSetter.call(textarea, note);
      }
      textarea.dispatchEvent(new Event('input', { bubbles: true }));
      textarea.dispatchEvent(new Event('change', { bubbles: true }));
      await new Promise(r => setTimeout(r, 500));
    }
  }

  if (!submitSuccess) {
    console.error(`❌ Failed to submit bookmark "${note}" after 5 attempts`);
    // Clean up the failed form so the next bookmark has a chance
    await dismissOpenForm();
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
    
    // Step 3: Close the sidebar (optional)
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
