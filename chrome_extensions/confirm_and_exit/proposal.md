# Bookmark Automation Fix Proposal

## THE EXACT PROBLEM

**Root Cause**: After `location.reload()` (inject.js:196), the content script is destroyed and Chrome does not automatically re-inject it. Lines 200-233 never execute.

**Why Reload Is Required**: 
- Meeting page has "End meeting" UI
- Archive page (after reload) has bookmark creation UI
- Page refresh is MANDATORY to get bookmark interface

**Current Broken Flow**:
1. User clicks extension → inject.js executes
2. Meeting ends → Bookmark message detected  
3. `sessionStorage.setItem('needsBookmarkAutomation', 'true')`
4. Wait 30s → `location.reload()` (REQUIRED for archive UI)
5. **🔴 SCRIPT DESTROYED** → Lines 200-233 never run
6. **🔴 NO BOOKMARK AUTOMATION**

## THE PRECISE SOLUTION

Keep the reload (required for UI) but add background script re-injection.

### 1. Keep inject.js lines 149-196 UNCHANGED

The current flow is correct:
- Detects bookmark message
- Sets sessionStorage flag  
- Waits 30 seconds
- Calls `location.reload()` (REQUIRED for archive UI)

NO CHANGES needed to inject.js lines 149-196.

### 2. Update background.js

Add navigation listener to re-inject script after reload:

```javascript
chrome.action.onClicked.addListener(async (tab) => {
  if (!tab.id) return;
  try {
    await chrome.scripting.executeScript({
      target: { tabId: tab.id },
      files: ["inject.js"],
    });
  } catch (err) {
    console.error("Confirm and Exit – injection failed", err);
  }
});

// Listen for page navigation completion
chrome.webNavigation.onCompleted.addListener(async (details) => {
  // Only handle main frame (not iframes)
  if (details.frameId !== 0) return;
  
  // Only handle scaler.com pages
  if (!details.url.includes('scaler.com')) return;
  
  try {
    // Re-inject script to check for pending bookmark automation
    await chrome.scripting.executeScript({
      target: { tabId: details.tabId },
      files: ["inject.js"]
    });
  } catch (err) {
    // Tab may be closed or navigation failed - ignore
    console.log("Re-injection failed (normal if tab closed):", err.message);
  }
});
```

### 3. Update manifest.json

Add webNavigation permission:

```json
{
  "name": "Confirm and Exit",
  "description": "One‑click end meeting automation (click End, type 'confirm', submit).",
  "version": "1.0.0",
  "manifest_version": 3,
  "icons": {
    "128": "icons/end.png"
  },
  "permissions": ["scripting", "activeTab", "webNavigation"],
  "host_permissions": [
    "https://www.scaler.com/*"
  ],
  "web_accessible_resources": [
    {
      "resources": ["bookmark-automation.js"],
      "matches": ["https://www.scaler.com/*"]
    }
  ],
  "background": {
    "service_worker": "background.js"
  },
  "action": {
    "default_title": "Confirm and Exit",
    "default_icon": "icons/end.png",
    "default_popup": "popup.html"
  }
}
```

### 4. Keep lines 200-233 in inject.js

Lines 200-233 are the bookmark automation code that will execute after reload when the script is re-injected by background.js.

## Why This Works

1. **Reload preserved** - gets required archive UI
2. **sessionStorage survives** - flag persists across reload
3. **Background re-injection** - script automatically re-injected after navigation
4. **Lines 200-233 execute** - bookmark automation runs on archive page
5. **Scoped to scaler.com** - only re-injects on relevant pages

## New Execution Flow

User clicks → Meeting ends → Bookmark detected → sessionStorage flag set → 30s wait → `location.reload()` → Background detects navigation → Script re-injected → Lines 200-233 check sessionStorage → Bookmark automation triggers

This solution fixes the re-injection problem while preserving the required reload for archive UI.