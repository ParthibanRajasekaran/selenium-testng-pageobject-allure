package com.template.project.web.tests.common;

import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;

import static com.template.project.common.Logger.logInfo;
import static com.template.project.web.utils.BrowserStackFactory.*;

public class BrowserStackBaseTest {

    @BeforeSuite(alwaysRun=true)
    public void beforeSuite() {
        getBrowserStackAccessKey();
    }

    @BeforeMethod(alwaysRun = true)
    public void browserConfig(){
        setBrowserCapabilities();
    }

    @AfterMethod(alwaysRun = true)
    protected static void tearDown(ITestResult testResult) {
        tearDownBrowser();
    }

    @AfterSuite(alwaysRun = true)
    public void afterSuite() {
        if (local != null) local.stop();
    }

    public static void tearDownBrowser() {
        logInfo("Closing Browser");
        if (driver != null) {
            driver.close();
        }
    }
}
