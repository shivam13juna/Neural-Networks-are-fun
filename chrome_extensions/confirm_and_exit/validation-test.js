/*
 * VALIDATION SCRIPT: Third Bookmark Consistency Fix
 * Tests the critical fixes implemented to ensure all 3 bookmarks are created reliably
 */

// Test configuration
const TEST_CONFIG = {
  iterations: 5, // Number of test iterations
  bookmarkCount: 3, // Expected number of bookmarks
  maxWaitTime: 30000, // Max wait time per test (30 seconds)
  selectors: {
    bookmarkList: '.bookmark-list, .m-bookmark-list, [data-cy="bookmark-item"]',
    bookmarkItems: '.bookmark-item, .m-bookmark-item, [data-cy="bookmark-item"]'
  }
};

// Test results tracking
const testResults = {
  runs: [],
  totalSuccess: 0,
  totalFailures: 0,
  failureReasons: {}
};

/** Validation Test: Check if all 3 bookmarks were created */
async function validateBookmarkCreation() {
  console.log("🧪 VALIDATION: Checking bookmark creation...");
  
  // Wait for bookmarks to be processed
  await new Promise(r => setTimeout(r, 3000));
  
  // Try multiple methods to count bookmarks
  const countMethods = [
    // Method 1: Count bookmark items in sidebar
    () => {
      const items = document.querySelectorAll('.m-bookmark-item, .bookmark-item');
      return items.length;
    },
    
    // Method 2: Count bookmark entries in list
    () => {
      const list = document.querySelector('.m-bookmark-list, .bookmark-list');
      if (list) {
        const items = list.querySelectorAll('li, .item, [data-bookmark]');
        return items.length;
      }
      return 0;
    },
    
    // Method 3: Count by data attributes
    () => {
      const items = document.querySelectorAll('[data-cy="bookmark-item"], [data-testid="bookmark"]');
      return items.length;
    },
    
    // Method 4: Count by text content (look for our specific bookmarks)
    () => {
      const text = document.body.innerText;
      let count = 0;
      if (text.includes('Intro')) count++;
      if (text.includes('Case-Study')) count++;
      if (text.includes('Conclusion')) count++;
      return count;
    }
  ];
  
  const counts = countMethods.map((method, index) => {
    try {
      const count = method();
      console.log(`📊 Count method ${index + 1}: ${count} bookmarks`);
      return count;
    } catch (error) {
      console.log(`⚠️ Count method ${index + 1} failed:`, error);
      return 0;
    }
  });
  
  // Use the highest count as our result (most reliable)
  const maxCount = Math.max(...counts);
  const avgCount = counts.reduce((a, b) => a + b, 0) / counts.length;
  
  console.log(`📈 Bookmark count results: max=${maxCount}, avg=${avgCount.toFixed(1)}`);
  
  return {
    count: maxCount,
    methods: counts,
    success: maxCount >= TEST_CONFIG.bookmarkCount
  };
}

/** Enhanced Test Runner */
async function runValidationTest(iteration) {
  console.log(`\n${'='.repeat(60)}`);
  console.log(`🧪 VALIDATION TEST ${iteration}/${TEST_CONFIG.iterations}`);
  console.log(`${'='.repeat(60)}`);
  
  const testStart = Date.now();
  const testResult = {
    iteration,
    timestamp: new Date().toISOString(),
    success: false,
    bookmarkCount: 0,
    duration: 0,
    errors: [],
    details: {}
  };
  
  try {
    // Step 1: Trigger bookmark automation
    console.log("🚀 Step 1: Triggering bookmark automation...");
    
    if (typeof addBookmarks === 'function') {
      const automationResult = await addBookmarks();
      testResult.details.automationSuccess = automationResult;
    } else {
      throw new Error("addBookmarks function not available");
    }
    
    // Step 2: Validate results
    console.log("✅ Step 2: Validating bookmark creation...");
    const validation = await validateBookmarkCreation();
    
    testResult.bookmarkCount = validation.count;
    testResult.success = validation.success;
    testResult.details.validation = validation;
    
    if (validation.success) {
      console.log(`✅ TEST ${iteration} PASSED: ${validation.count}/${TEST_CONFIG.bookmarkCount} bookmarks created`);
      testResults.totalSuccess++;
    } else {
      console.log(`❌ TEST ${iteration} FAILED: Only ${validation.count}/${TEST_CONFIG.bookmarkCount} bookmarks created`);
      testResults.totalFailures++;
      
      const reason = `Insufficient bookmarks: ${validation.count}/${TEST_CONFIG.bookmarkCount}`;
      testResults.failureReasons[reason] = (testResults.failureReasons[reason] || 0) + 1;
    }
    
  } catch (error) {
    console.error(`❌ TEST ${iteration} ERROR:`, error);
    testResult.errors.push(error.message);
    testResults.totalFailures++;
    
    const reason = error.message || 'Unknown error';
    testResults.failureReasons[reason] = (testResults.failureReasons[reason] || 0) + 1;
  }
  
  testResult.duration = Date.now() - testStart;
  testResults.runs.push(testResult);
  
  console.log(`⏱️ Test duration: ${testResult.duration}ms`);
  return testResult;
}

/** Generate Comprehensive Test Report */
function generateTestReport() {
  console.log(`\n${'='.repeat(80)}`);
  console.log(`📊 VALIDATION TEST REPORT - THIRD BOOKMARK CONSISTENCY FIX`);
  console.log(`${'='.repeat(80)}`);
  
  const successRate = (testResults.totalSuccess / TEST_CONFIG.iterations * 100).toFixed(1);
  const avgDuration = testResults.runs.reduce((sum, run) => sum + run.duration, 0) / testResults.runs.length;
  
  console.log(`📈 OVERALL RESULTS:`);
  console.log(`   ✅ Successful runs: ${testResults.totalSuccess}/${TEST_CONFIG.iterations} (${successRate}%)`);
  console.log(`   ❌ Failed runs: ${testResults.totalFailures}/${TEST_CONFIG.iterations}`);
  console.log(`   ⏱️ Average duration: ${avgDuration.toFixed(0)}ms`);
  
  if (testResults.totalFailures > 0) {
    console.log(`\n🔍 FAILURE ANALYSIS:`);
    Object.entries(testResults.failureReasons).forEach(([reason, count]) => {
      console.log(`   • ${reason}: ${count} occurrence(s)`);
    });
  }
  
  console.log(`\n📋 DETAILED RESULTS:`);
  testResults.runs.forEach((run, index) => {
    const status = run.success ? '✅' : '❌';
    console.log(`   ${status} Test ${run.iteration}: ${run.bookmarkCount} bookmarks (${run.duration}ms)`);
  });
  
  if (successRate >= 90) {
    console.log(`\n🎉 EXCELLENT: ${successRate}% success rate - Third bookmark issue appears to be resolved!`);
  } else if (successRate >= 70) {
    console.log(`\n⚠️ IMPROVED: ${successRate}% success rate - Significant improvement but may need further optimization`);
  } else {
    console.log(`\n🚨 NEEDS WORK: ${successRate}% success rate - Additional fixes required`);
  }
  
  console.log(`${'='.repeat(80)}`);
  
  return {
    successRate: parseFloat(successRate),
    totalTests: TEST_CONFIG.iterations,
    successfulTests: testResults.totalSuccess,
    failedTests: testResults.totalFailures,
    avgDuration: avgDuration,
    failureReasons: testResults.failureReasons,
    runs: testResults.runs
  };
}

/** Main Validation Function */
async function runFullValidation() {
  console.log("🔬 STARTING COMPREHENSIVE VALIDATION TESTING");
  console.log(`📊 Configuration: ${TEST_CONFIG.iterations} iterations, expecting ${TEST_CONFIG.bookmarkCount} bookmarks each`);
  
  // Reset test results
  testResults.runs = [];
  testResults.totalSuccess = 0;
  testResults.totalFailures = 0;
  testResults.failureReasons = {};
  
  // Run all test iterations
  for (let i = 1; i <= TEST_CONFIG.iterations; i++) {
    await runValidationTest(i);
    
    // Wait between tests if not the last one
    if (i < TEST_CONFIG.iterations) {
      console.log(`⏳ Waiting 5 seconds before next test...`);
      await new Promise(r => setTimeout(r, 5000));
    }
  }
  
  // Generate final report
  return generateTestReport();
}

// Export for use in console or other scripts
if (typeof window !== 'undefined') {
  window.validateBookmarks = runFullValidation;
  window.validateSingle = validateBookmarkCreation;
  console.log("🧪 Validation functions loaded. Run window.validateBookmarks() to test.");
}

// Auto-run if this script is executed directly
if (typeof addBookmarks === 'function') {
  console.log("🚀 Auto-running validation test...");
  runFullValidation().then(report => {
    console.log("📊 Validation complete. Report:", report);
  });
}
