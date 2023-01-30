package com.template.project.web.tests.common;

import com.template.project.common.RetryListener;
import com.template.project.web.utils.SeleniumListener;
import org.testng.ITestContext;
import org.testng.ITestResult;
import org.testng.annotations.*;

import java.net.InetAddress;
import java.net.UnknownHostException;

import static com.template.project.common.Logger.logInfo;
import static com.template.project.web.utils.WebDriverHolder.setDriver;
import static com.template.project.web.utils.WebDriverHolder.tearDownBrowser;

@Listeners({SeleniumListener.class})
public class BaseTest extends RetryListener {

  @Parameters("browserName")
  @BeforeMethod(alwaysRun = true)
  protected static void setUpBrowser(@Optional("chrome") String browserName, ITestContext result)
      throws UnknownHostException {
    logInfo(String.format("%s Tests running on host: %s", result.getName(),
        InetAddress.getLocalHost().getHostName()));
    setDriver(browserName);
  }

  @AfterMethod(alwaysRun = true)
  protected static void tearDown(ITestResult testResult) {
    tearDownBrowser();
  }
}
