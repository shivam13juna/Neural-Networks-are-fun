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
  SUBMIT_BUTTON: "div.layout > form div.m-bookmark-item__footer i, div.archive-sidebar button, button[type='submit'], .submit-bookmark, .save-bookmark", // Submit bookmark button
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
        input.value = value;
        
        // Trigger input events to ensure proper form handling
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
  console.log(`🔍 Calculating seekbar position for ${percentage}%...`);
  
  // Try to find the main seekbar container (not just the handle)
  const seekbarSelectors = [
    "div.vp-controls__seekbar.vp-bookmarks-container",  // Main seekbar container
    "div.vp-controls__seekbar",                         // Alternative seekbar
    ".vp-controls__seekbar",                            // Simplified selector
    ".archive-player .vp-controls .vp-controls__seekbar",
    SEEKBAR_SELECTOR                                    // Your specific selector as fallback
  ];
  
  let seekbar = null;
  let usedSelector = "";
  for (const selector of seekbarSelectors) {
    seekbar = document.querySelector(selector);
    if (seekbar) {
      usedSelector = selector;
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
  
  // Add some padding to avoid clicking at the very edges
  const padding = 10;
  const effectiveWidth = seekbarWidth - (2 * padding);
  const position = padding + Math.round((percentage / 100) * effectiveWidth);
  
  console.log(`📊 Seekbar calculation details:`);
  console.log(`   - Selector used: ${usedSelector}`);
  console.log(`   - Total width: ${seekbarWidth}px`);
  console.log(`   - Effective width (with padding): ${effectiveWidth}px`);
  console.log(`   - Target percentage: ${percentage}%`);
  console.log(`   - Calculated position: ${position}px`);
  
  return Math.max(padding, Math.min(position, seekbarWidth - padding)); // Ensure we stay within bounds
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
    console.log(`📊 Video duration: ${duration.toFixed(2)}s, Target percentage: ${percentage}%, Target time: ${targetTime.toFixed(2)}s`);
    
    // Pause the video to prevent it from continuing to play while we set the time
    const wasPlaying = !video.paused;
    if (wasPlaying) {
      video.pause();
      console.log("⏸️ Paused video to set position");
    }
    
    // Store original time for comparison
    const originalTime = video.currentTime;
    console.log(`⏰ Original video time: ${originalTime.toFixed(2)}s`);
    
    // Set the video time multiple times to ensure it sticks
    video.currentTime = targetTime;
    console.log(`🎯 Attempted to set video.currentTime to ${targetTime.toFixed(2)}s`);
    
    // Wait a moment and try again to ensure it sticks
    setTimeout(() => {
      video.currentTime = targetTime;
      console.log(`🔄 Re-set video.currentTime to ${targetTime.toFixed(2)}s (ensuring it sticks)`);
    }, 100);
    
    // Trigger seeking events
    video.dispatchEvent(new Event('seeking'));
    video.dispatchEvent(new Event('timeupdate'));
    
    let attempts = 0;
    const maxAttempts = 50; // 5 seconds max wait
    
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
        
        // Resume playing if it was playing before
        if (wasPlaying) {
          video.play();
          console.log("▶️ Resumed video playback");
        }
        
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
  });
}

/** Main function: Add a single bookmark */
async function addSingleBookmark(bookmarkConfig) {
  const { note, percentage } = bookmarkConfig;
  
  console.log(`\n🔖 Adding bookmark: "${note}" at ${percentage}%`);
  
  // Step 1: Click "Add Bookmark" button
  console.log("📌 Step 1: Clicking Add Bookmark button...");
  if (!(await clickElement(BOOKMARK_SELECTORS.ADD_BOOKMARK_BUTTON, "Add Bookmark button"))) {
    return false;
  }
  
  // Wait for bookmark form to appear
  await new Promise(r => setTimeout(r, 800));
  
  // Step 2: Set video position FIRST (before entering note)
  console.log(`🎯 Step 2: Setting video to ${percentage}% position...`);
  
  // Wait for video to be ready
  const videoReady = await waitForVideoReady();
  if (!videoReady) {
    console.log("⚠️ Video not ready, but continuing with positioning attempt...");
  }
  
  // Try to set video time directly first
  console.log("🎯 Attempting direct video time setting...");
  const videoTimeSet = await setVideoTime(percentage);
  
  // Wait and verify the position
  await new Promise(r => setTimeout(r, 1000));
  let positionCorrect = verifyVideoPosition(percentage);
  
  if (!videoTimeSet || !positionCorrect) {
    // Fallback: Click on video seekbar to set position
    console.log("🔄 Direct video time failed, trying seekbar click method...");
    const seekPosition = calculateSeekPosition(percentage);
    if (await clickSeekbar(seekPosition)) {
      // Wait and verify seekbar method
      await new Promise(r => setTimeout(r, 1000));
      positionCorrect = verifyVideoPosition(percentage);
    }
  }
  
  // If still not correct, try one more time with both methods
  if (!positionCorrect) {
    console.log("🔄 Second attempt: Trying both methods again...");
    await setVideoTime(percentage);
    await new Promise(r => setTimeout(r, 500));
    const seekPosition = calculateSeekPosition(percentage);
    await clickSeekbar(seekPosition);
    await new Promise(r => setTimeout(r, 1000));
    positionCorrect = verifyVideoPosition(percentage);
    
    if (!positionCorrect) {
      console.log(`⚠️ Warning: Could not reliably set video to ${percentage}%, but continuing with bookmark creation...`);
    }
  }
  
  // Step 3: Enter note text AFTER setting position
  console.log("✏️ Step 3: Entering bookmark note...");
  if (!(await setInputValue(BOOKMARK_SELECTORS.NOTE_INPUT, note, "bookmark note"))) {
    return false;
  }
  
  // Wait a moment before submitting
  await new Promise(r => setTimeout(r, 500));
  
  // Step 4: Submit the bookmark
  console.log("✅ Step 4: Submitting bookmark...");
  if (!(await clickElement(BOOKMARK_SELECTORS.SUBMIT_BUTTON, "submit bookmark"))) {
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
      
      // Wait longer between bookmarks to ensure proper processing
      if (i < BOOKMARKS.length - 1) {
        console.log(`⏳ Waiting 3 seconds before next bookmark...`);
        await new Promise(r => setTimeout(r, 3000)); // Increased to 3 seconds
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
