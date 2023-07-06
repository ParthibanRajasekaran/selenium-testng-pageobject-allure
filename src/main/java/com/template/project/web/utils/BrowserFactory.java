package com.template.project.web.utils;

import com.template.project.common.Cipher;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.Platform;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.manager.SeleniumManager;
import org.openqa.selenium.remote.RemoteWebDriver;

import javax.naming.ConfigurationException;
import java.net.MalformedURLException;
import java.net.URL;

import static com.template.project.common.ConfigFileReaderUtils.getValueFromJsonConfigFile;

public class BrowserFactory {

  private static String HUB;
  private static String HUB_IE;
  private static String STANDALONE_CHROME;

  private static Boolean HEADLESS;
  private static Boolean INSECURE_CERTIFICATE;
  private static Boolean NO_SANDBOX;
  private static Boolean INCOGNITO;

  static {
    try {
      HUB = readFromSeleniumConfigFile("hub");
    } catch (ConfigurationException e) {
      e.printStackTrace();
    }

    try {
      HEADLESS = Boolean.parseBoolean(readFromSeleniumConfigFile("headless"));
    } catch (ConfigurationException e) {
      e.printStackTrace();
    }
    try {
      INSECURE_CERTIFICATE =
          Boolean.parseBoolean(readFromSeleniumConfigFile("insecure_certificate"));
    } catch (ConfigurationException e) {
      e.printStackTrace();
    }
    try {
      NO_SANDBOX = Boolean.parseBoolean(readFromSeleniumConfigFile("no_sandbox"));
    } catch (ConfigurationException e) {
      e.printStackTrace();
    }
    try {
      INCOGNITO = Boolean.parseBoolean(readFromSeleniumConfigFile("incognito"));
    } catch (ConfigurationException e) {
      e.printStackTrace();
    }
  }

  /** This method is to setup webDriverManager for different browser */
  private static void setupWebDriverManager() {
    SeleniumManager.getInstance().getDriverPath(new ChromeOptions());
    SeleniumManager.getInstance().getDriverPath(new FirefoxOptions());
    SeleniumManager.getInstance().getDriverPath(new EdgeOptions());
//    WebDriverManager.chromedriver().setup();
//    WebDriverManager.firefoxdriver().driverVersion("0.30.0").setup();
//    WebDriverManager.iedriver().setup();
//    WebDriverManager.edgedriver().setup();
  }

  /**
   * Handle the known issue with WebDriverManager : HTTP response code 403 Reference:
   * https://github.com/bonigarcia/webdrivermanager#http-response-code-403
   *
   * @param tokenSecret tokenSecret to be set in settings.xml and passed as parameter
   */
  public static void handleWebDriverManagerAuthorizationIssue(String tokenSecret) {
    System.setProperty("wdm.gitHubTokenName", "test-automation");
    System.setProperty("wdm.gitHubTokenSecret", Cipher.decrypt(tokenSecret));
  }

  /**
   * Set Chrome browser capabilities.
   *
   * @return A not null chrome driver
   * @throws MalformedURLException if the constant HUB is not a valid URL.
   */
  private static WebDriver getChromeBrowser() throws MalformedURLException {
    final ChromeOptions options = new ChromeOptions();
    options.addArguments("--disable-dev-shm-usage");
    if(HEADLESS) options.addArguments("--headless=new"); // In line with Selenium 4.8.0 headless guidelines for chrome
    options.setAcceptInsecureCerts(INSECURE_CERTIFICATE);
    if (NO_SANDBOX) options.addArguments("--no-sandbox");
    if (INCOGNITO) options.addArguments("--incognito");

    if (isRemote()) {
      if (isStandaloneChrome()) {
        return new RemoteWebDriver(new URL(STANDALONE_CHROME), options);
      } else {
        return new RemoteWebDriver(new URL(HUB), options);
      }
    } else {
      setupWebDriverManager();
      return new ChromeDriver(options);
    }
  }

  /**
   * Set Firefox browser capabilities.
   *
   * @return A valid instance
   * @throws MalformedURLException if the constant HUB is not a valid URL.
   */
  private static WebDriver getFirefoxBrowser() throws MalformedURLException {
    FirefoxOptions options = new FirefoxOptions();
    if(HEADLESS) options.addArguments("-headless"); // In line with Selenium 4.8.0 headless guidelines for firefox
    options.setCapability("moz:firefoxOptions", options);
    System.setProperty(FirefoxDriver.SystemProperty.BROWSER_LOGFILE, "/dev/null");

    if (isRemote()) {
      setupWebDriverManager();
      return new RemoteWebDriver(new URL(HUB), options);
    } else {
      setupWebDriverManager();
      return new FirefoxDriver(options);
    }
  }

  /**
   * Check a system property to decide if we should use a remote web driver.
   *
   * @return True only if the property is correctly set.
   */
  private static boolean isRemote() {
    return Boolean.TRUE.equals(Boolean.valueOf(System.getProperty("remoteExecution")));
  }

  /**
   * Check a system property to decide if we should chrome from grid or node.
   *
   * @return True only if the property is correctly set.
   */
  private static boolean isStandaloneChrome() {
    return Boolean.TRUE.equals(Boolean.valueOf(System.getProperty("standaloneChrome")));
  }

  /**
   * Set Edge browser capabilities.
   *
   * @return A usable, not null edge driver.
   * @throws MalformedURLException if the constant HUB_IE is not a valid URL.
   */
  private static WebDriver getEdgeBrowser() throws MalformedURLException {
    EdgeOptions options = new EdgeOptions();
    options.setCapability("platform", Platform.ANY);
    if (isRemote()) {
      setupWebDriverManager();
      return new RemoteWebDriver(new URL(HUB_IE), options);
    } else {
      setupWebDriverManager();
      return new EdgeDriver();
    }
  }

  /**
   * This method returns the browser driver based on the browser name specified in the test suite
   * xml.
   *
   * @param browserName One of firefox, edge or chrome.
   * @return default is chrome
   * @throws MalformedURLException if the constant for the hub (HUB or HUB_IE) is not a valid URL.
   */
  static WebDriver getBrowser(String browserName) throws MalformedURLException {
    switch (browserName) {
      case "firefox":
        return getFirefoxBrowser();
      case "edge":
        return getEdgeBrowser();
      case "chrome":
      default:
        return getChromeBrowser();
    }
  }

  private static String readFromSeleniumConfigFile(String propName) throws ConfigurationException {
    return getValueFromJsonConfigFile("selenium_config.json", propName);
  }
}
