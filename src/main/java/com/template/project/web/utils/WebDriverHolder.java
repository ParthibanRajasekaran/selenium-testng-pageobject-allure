package com.template.project.web.utils;

import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.HasAuthentication;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.devtools.DevTools;
import org.openqa.selenium.devtools.HasDevTools;
import org.openqa.selenium.remote.Augmenter;

import java.net.MalformedURLException;
import java.time.Duration;

import static com.template.project.common.Logger.logInfo;
import static com.template.project.web.utils.BrowserFactory.getBrowser;

@Slf4j
public class WebDriverHolder {

  private static final ThreadLocal<WebDriver> driver = new ThreadLocal<>();

  public static void setDriver(String browserName) {
    try {
      driver.set(getBrowser(browserName));
      logInfo(String.format("Starting browser : %s", browserName));
    } catch (MalformedURLException e) {
      log.error("Unable to open {}", browserName, e);
    }
    getDriver().manage().timeouts().implicitlyWait(Duration.ofSeconds(3));
  }

  public static WebDriver getDriver() {
    return driver.get();
  }

  public static WebDriver getAugmentedDriver() {
    Augmenter augmenter = new Augmenter();
    DevTools devTools = ((HasDevTools) augmenter.augment(getDriver())).getDevTools();
    devTools.createSession();
    return augmenter.
        addDriverAugmentation("chrome", HasAuthentication.class,
            (caps, exec) -> (whenThisMatches, useTheseCredentials) -> devTools.getDomains()
                .network().addAuthHandler(whenThisMatches, useTheseCredentials))
        .augment(getDriver());
  }

  public static void tearDownBrowser() {
    logInfo("Closing Browser");
    if (getDriver() != null) {
      getDriver().quit();
      driver.remove();
    }
  }
}
