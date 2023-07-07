package com.template.project.web.utils;

import io.qameta.allure.Step;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.openqa.selenium.*;
import org.openqa.selenium.devtools.DevTools;
import org.openqa.selenium.devtools.HasDevTools;
import org.openqa.selenium.devtools.v114.network.Network;
import org.openqa.selenium.devtools.v114.network.model.Headers;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.remote.Augmenter;
import org.openqa.selenium.remote.LocalFileDetector;
import org.openqa.selenium.remote.RemoteWebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;

import java.io.File;
import java.net.URI;
import java.util.*;
import java.util.function.Predicate;

import static com.template.project.common.Logger.logError;
import static com.template.project.common.Logger.logInfo;
import static com.template.project.web.utils.Waiters.*;
import static com.template.project.web.utils.WebDriverHolder.getAugmentedDriver;
import static com.template.project.web.utils.WebDriverHolder.getDriver;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;
import static org.testng.AssertJUnit.fail;

/** Tools for testing web elements. */
@Slf4j
public class SeleniumUtils {

  private static final Random random = new Random();

  /**
   * Find the webelement based on the xpath provided.
   *
   * @param locator A valid xpath locator.
   * @return webelement the WebElement once it is located, otherwise a {@code Throwable} will be
   *     thrown
   */
  @Step
  public static WebElement tryFindElement(final String locator) {
    return getFluentWait().until(ExpectedConditions.presenceOfElementLocated(By.xpath(locator)));
  }

  /**
   * Find the webelements based on the xpath provided.
   *
   * @param locator A valid xpath locator.
   * @return webelements the WebElements once located, otherwise a {@code Throwable} will be thrown
   */
  @Step
  public static List<WebElement> tryFindElements(final String locator) {
    return getFluentWait()
        .until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath(locator)));
  }

  /**
   * Check if the webelement is present.
   *
   * @param locator A valid xpath locator.
   * @return true only if the element is found and is visible
   */
  @Step
  public static boolean isElementPresent(final String locator) {
    try {
      return null
          != getFluentWait()
              .until(ExpectedConditions.visibilityOfElementLocated(By.xpath(locator)));
    } catch (final TimeoutException ignored) {
      return false;
    }
  }

  /**
   * Verify if the webelement is displayed BUT DOESN'T perform assertion instead logs the error
   * message and proceed with the test.
   *
   * @param locator A valid xpath locator.
   */
  @Step("Verify if element is present: {0}")
  public static void verifyElementPresent(final String locator) {
    if (isElementPresent(locator)) {
      logInfo(String.format("Element is present: %s", locator));
    } else {
      logError(String.format("Element %s is not present", locator));
    }
  }

  /**
   * Check if the webelement is displayed and assert if the element does not exist.
   *
   * @param locator A valid xpath locator.
   */
  @Step("Assert if element is not present: {0}")
  public static void assertElementPresent(final String locator) {
    try {
      getFluentWait().until(ExpectedConditions.visibilityOfElementLocated(By.xpath(locator)));
      logInfo(String.format("Element is present: %s", locator));
    } catch (Exception ex) {
      fail(String.format("Element %s is not present", locator));
    }
  }

  /**
   * Check if the webelement is not displayed.
   *
   * @param locator A valid xpath locator.
   * @return true only if the element is not found (ie that it becomes not present within the
   *     timeout)
   */
  @Step
  public static boolean isElementNotPresent(final String locator) {
    try {
      getFluentWait().until(ExpectedConditions.invisibilityOfElementLocated(By.xpath(locator)));
      return true;
    } catch (TimeoutException ignored) {
      return false;
    }
  }

  /**
   * Verify if the webelement is not displayed BUT DOESN'T perform assertion instead logs the error
   * message and proceed with the test.
   *
   * @param locator A valid xpath locator.
   */
  @Step("Verify if element is not present: {0}")
  public static void verifyElementNotPresent(final String locator) {
    if (isElementNotPresent(locator)) {
      logInfo(String.format("Element %s is not present: ", locator));
    } else {
      logError(String.format("Element %s is still present", locator));
    }
  }

  /**
   * Check if the webelement is not displayed and assert if the element exists.
   *
   * @param locator A valid xpath locator.
   */
  @Step("Assert if element is present: {0}")
  public static void assertElementNotPresent(final String locator) {
    try {
      getFluentWait().until(ExpectedConditions.invisibilityOfElementLocated(By.xpath(locator)));
      logInfo(String.format("Element %s is present: ", locator));
    } catch (Exception ex) {
      fail(String.format("Element %s is still present", locator));
    }
  }

  /**
   * Check if the webelement is enabled.
   *
   * @param locator A valid xpath locator.
   * @return true only if the element is found and is enabled
   */
  @Step
  public static boolean isElementEnabled(final String locator) {
    return tryFindElement(locator).isEnabled();
  }

  /**
   * Check if the webelement is disabled.
   *
   * @param locator A valid xpath locator
   * @return true if the element is either not found or is enabled
   */
  @Step
  public static boolean isElementDisabled(String locator) {
    return !tryFindElement(locator).isEnabled();
  }

  /**
   * Check if the checkbox is checked on.
   *
   * @param locator A valid xpath locator.
   * @return true if the element is found and is selected
   */
  @Step
  public static Boolean isCheckBoxSelected(String locator) {
    try {
      getFluentWait().until(ExpectedConditions.elementToBeSelected(By.xpath(locator)));
      return true;
    } catch (TimeoutException e) {
      return false;
    }
  }

  /**
   * Check if the webelement contains the text. The {@code text} should not be a regular expression,
   * just plain text.
   *
   * @param locator A valid xpath locator.
   * @param text Which should be present on the webelement located by {@code locator}
   * @return true only if the webelement is found and contains the text
   */
  @Step
  public static boolean isTextPresent(final String locator, final String text) {
    try {
      return tryFindElement(locator).getText().contains(text);
    } catch (TimeoutException ignored) {
      log.debug("Timeout looking for {}", locator, ignored);
      return false;
    }
  }

  /**
   * Check if the webelement contains the text BUT DOESN'T FAIL instead logs error message.
   *
   * @param locator A valid xpath locator.
   * @param text Which should be present on the webelement located by {@code locator}
   */
  @Step
  public static void verifyTextPresent(final String locator, final String text) {
    boolean textPresent = isTextPresent(locator, text);
    if (textPresent) {
      logInfo(
          String.format(
              "Text: '%s' displayed as expected",
              tryFindElement(locator).getText().contains(text)));
    } else {
      logError(String.format("Text '%s' is not displayed", text));
    }
  }

  /**
   * Check if the webelement contains the text and assert if not.
   *
   * @param locator A valid xpath locator.
   * @param text Which should be present on the webelement located by {@code locator}
   */
  @Step
  public static void assertTextPresent(String locator, String text) {
    assertTrue(tryFindElement(locator).getText().contains(text));
  }

  /**
   * Check if the webelement does not contain the text.
   *
   * @see #isTextPresent(String, String)
   * @param locator A valid xpath locator.
   * @param text
   * @return true if {@code locator} with {@code text} is missing (not found).
   */
  @Step
  public static boolean isTextNotPresent(final String locator, final String text) {
    return !isTextPresent(locator, text);
  }

  /**
   * Check if the webelement does not contain the text BUT DOESN'T FAIL instead logs error message.
   *
   * @param locator A valid xpath locator.
   * @param text Text which should not be present on the weblement found by {@code locator}
   */
  @Step
  public static void verifyTextNotPresent(final String locator, final String text) {
    boolean textNotPresent = !isTextPresent(locator, text);
    if (textNotPresent) {
      logInfo(String.format("Text: '%s' is not displayed as expected", text));
    } else {
      logError(
          String.format(
              "Text '%s' is still displayed", tryFindElement(locator).getText().contains(text)));
    }
  }

  /**
   * Check if the webelement does not contain the text and assert if it does.
   *
   * @param locator a valid xpath
   * @param text Text which should not be found on the {@code locator}
   */
  @Step
  public static void assertTextNotPresent(final String locator, final String text) {
    assertFalse(tryFindElement(locator).getText().contains(text));
  }

  /**
   * Check if the webelement contains the value.
   *
   * @param locator A valid xpath locator.
   * @param value Text which should be present in the value attribute of {@code locator}
   * @return true only if {@code locator} was found and contains {@code value} in the value
   *     attribute
   */
  @Step
  public static Boolean isValuePresent(final String locator, final String value) {
    try {
      return attributeValue(locator).contains(value);
    } catch (TimeoutException ignored) {
      return false;
    }
  }

  /**
   * Get the value attribute of an element identified by the xpath {@code locator}.
   *
   * @param locator Must be a valid not null xpath
   * @return The value, potentially null
   */
  private static String attributeValue(final String locator) {
    return tryFindElement(locator).getAttribute("value");
  }

  /**
   * Check if the webelement contains the value BUT DOESN'T FAIL instead logs error message.
   *
   * @param locator A valid xpath locator.
   * @param value Text which should be present on {@code locator}
   */
  @Step
  public static void verifyValuePresent(final String locator, final String value) {
    boolean textPresent = isValuePresent(locator, value);
    if (textPresent) {
      logInfo(
          String.format("Value '%s' present as expected", attributeValue(locator).contains(value)));
    } else {
      logError(String.format("Value '%s' is not present", value));
    }
  }

  /**
   * Check if the webelement contains the value and assert if not.
   *
   * @param locator A valid xpath locator.
   * @param value Text which should be present in the &quot;value&quot; attribute
   */
  @Step
  public static void assertValuePresent(final String locator, final String value) {
    assertTrue(attributeValue(locator).contains(value));
  }

  /**
   * Check the attribute value of the locator specified.
   *
   * @param locator A valid xpath locator.
   * @param attribute
   * @param attributeValue
   * @return
   */
  @Step
  public static boolean isAttributeValuePresent(
      final String locator, final String attribute, final String attributeValue) {
    try {
      return tryFindElement(locator).getAttribute(attribute).contains(attributeValue);
    } catch (TimeoutException ignored) {
      return false;
    }
  }

  /**
   * Check the attribute value of the locator specified BUT DOESN'T FAIL instead logs error message.
   *
   * @param locator A valid xpath locator.
   * @param attribute
   * @param attributeValue
   * @return
   */
  @Step
  public static void verifyAttributeValuePresent(
      final String locator, final String attribute, final String attributeValue) {
    boolean textPresent = isAttributeValuePresent(locator, attribute, attributeValue);
    if (textPresent) {
      logInfo(
          String.format(
              "Attribute '%s' contains value '%s' as expected",
              attribute, tryFindElement(locator).getAttribute(attribute).contains(attributeValue)));
    } else {
      logError(
          String.format(
              "Attribute '%s' does not contain expeected value '%s'", attribute, attributeValue));
    }
  }

  /**
   * This method is to check the attribute value of the locator specified and assert if not.
   *
   * @param locator A valid xpath locator.
   * @param attribute
   * @param attributeValue
   * @return
   */
  @Step
  public static void assertAttributeValuePresent(
      final String locator, final String attribute, final String attributeValue) {
    assertTrue(tryFindElement(locator).getAttribute(attribute).contains(attributeValue));
  }

  /**
   * Open the webpage and navigate to the URL specified.
   *
   * @param url The URL to load. It is best to use a fully qualified URL.
   */
  @Step
  public static void openPage(String url) {
    getDriver().get(url);
  }

  /**
   * Navigate to the URL specified from the current page.
   *
   * @param url The URL to load. It is best to use a fully qualified URL.
   */
  @Step
  public static void navigateToPage(String url) {
    logInfo(String.format("Navigating to web page %s", url));
    getDriver().navigate().to(url);
  }

  /**
   * Navigate to the URL specified from the current page with JavaScript Executor.
   *
   * @param url The URL to load. It is best to use a fully qualified URL.
   */
  @Step
  public static void navigateToPageWithJavascriptExecutor(String url) {
    logInfo(String.format("Navigating to web page %s with JavascriptExecutor ", url));
    JavascriptExecutor js = (JavascriptExecutor) getDriver();
    js.executeScript(String.format("window.location = %s", url));
  }

  /**
   * Get the URL of the current page.
   *
   * @return The URL of the page currently loaded in the browser
   */
  @Step
  public static String getUrl() {
    return getDriver().getCurrentUrl();
  }

  /**
   * Switch to the iframe window.
   *
   * @param locator A valid xpath locator.
   * @param locatorWithinFrame
   */
  @Step
  public static void switchToFrame(String locator, String locatorWithinFrame) {
    WebElement mainFrame = getDriver().findElement(By.xpath(locator));
    getDriver().switchTo().frame(mainFrame);
    WebElement elementOnFrame = getDriver().findElement(By.xpath(locatorWithinFrame));
    assertTrue(elementOnFrame.isDisplayed());
    logInfo("Switched to expected frame");
  }

  /**
   * Switch back to the parent window.
   *
   * @param locatorOutsideFrame
   */
  @Step
  public static void switchBackFromFrame(String locatorOutsideFrame) {
    getDriver().switchTo().defaultContent();
    WebElement elementOnFrame = getDriver().findElement(By.xpath(locatorOutsideFrame));
    assertTrue(elementOnFrame.isDisplayed());
    logInfo("Switched back from frame");
  }

  /** Scroll up in the current page. */
  @Step
  public static void scrollUp() {
    final JavascriptExecutor jse = (JavascriptExecutor) getDriver();
    jse.executeScript("window.scrollBy(0, -1000);");

    sleep(1500);
  }

  /** Scroll down in the current page. */
  @Step
  public static void scrollDown() {
    final JavascriptExecutor jse = (JavascriptExecutor) getDriver();
    jse.executeScript("window.scrollBy(0,800)");

    sleep(1000);
  }

  @Step
  public static void scrollToElement(String locator) {
    final Actions actions = new Actions(getDriver());
    actions.moveToElement(tryFindElement(locator)).perform();
  }

  /** Reload the current page. */
  @Step
  public static void reload() {
    getDriver().navigate().refresh();
  }

  /**
   * Click on the weblement.
   *
   * @param locator A valid xpath locator.
   */
  @Step
  public static void clickElement(String locator) {
    waitUntilElementClickable(locator);
    tryFindElement(locator).click();
  }

  /**
   * Click on the weblement.
   *
   * @param locator A valid xpath locator.
   */
  @Step
  public static void clickElementByYaxis(String locator) {
    final WebElement elementToClick = getDriver().findElement(By.xpath(locator));
    ((JavascriptExecutor) getDriver())
        .executeScript("window.scrollTo(0," + elementToClick.getLocation().y + ")");
    elementToClick.click();
  }

  /**
   * Click on the weblement.
   *
   * @param locator A valid xpath locator.
   */
  @Step
  public static void clickElementByXaxis(String locator) {
    final WebElement elementToClick = getDriver().findElement(By.xpath(locator));
    ((JavascriptExecutor) getDriver())
        .executeScript("window.scrollTo(0," + elementToClick.getLocation().x + ")");
    elementToClick.click();
  }

  /**
   * Clear the input field.
   *
   * @param locator A valid xpath locator.
   */
  @Step
  public static void clearField(String locator) {
    tryFindElement(locator).clear();
    waitUntilValueWillBePresentInElement(locator, "");
  }

  /**
   * Clear and enter the value in the input field.
   *
   * @param locator A valid xpath locator.
   * @param text
   */
  @Step
  public static void clearAndFillInFieldWith(final String locator, final String text) {
    tryFindElement(locator).clear();
    waitUntilValueWillBePresentInElement(locator, "");
    tryFindElement(locator).sendKeys(text);
    waitUntilValueWillBePresentInElement(locator, text);
  }

  /**
   * Enter the value in the input field.
   *
   * @param locator A valid xpath locator.
   * @param text
   */
  @Step
  public static void fillInFieldWith(final String locator, final String text) {
    tryFindElement(locator).sendKeys(text);
    waitUntilValueWillBePresentInElement(locator, text);
  }

  /**
   * Generate a random number between 2 limits. Note that these random numbers are not
   * cryptographically strong.
   *
   * @param min The minimum value (inclusive) that may be returned
   * @param max The maximum value (inclusive) that may be returned.
   * @return An integer that will be equal to or between the bounds provided
   */
  @Step
  public static int getRandomNumberBetween(final int min, final int max) {
    return random.nextInt((max - min) + 1) + min;
  }

  /**
   * Send a key stroke.
   *
   * @param locator A valid xpath locator.
   * @param key Identifies one of the key to press, for example enter.
   */
  @Step
  public static void sendKeyTo(final String locator, final String key) {
    switch (key.toLowerCase()) {
      case "enter":
        tryFindElement(locator).sendKeys(Keys.ENTER);
        break;
      case "down":
        tryFindElement(locator).sendKeys(Keys.ARROW_DOWN);
        break;
      case "up":
        tryFindElement(locator).sendKeys(Keys.ARROW_UP);
        break;
      case "space":
        tryFindElement(locator).sendKeys(Keys.SPACE);
        break;
      case "right":
        tryFindElement(locator).sendKeys(Keys.ARROW_RIGHT);
        break;
      case "left":
        tryFindElement(locator).sendKeys(Keys.ARROW_LEFT);
        break;
      case "escape":
        tryFindElement(locator).sendKeys(Keys.ESCAPE);
        break;
      case "backspace":
        tryFindElement(locator).sendKeys(Keys.BACK_SPACE);
        break;
      default:
        logError("key " + key + " not recognized");
        return;
    }
    log.debug("Sucessfully sent key {}", key);
  }

  public static void refreshPageIfSomethingWentWrong() {
    sleep(1000);
    if (getDriver().getPageSource().contains("something went wrong")) {
      reload();
      sleep(2000);
    }
  }

  /**
   * Switch between the tabs.
   *
   * @param tabNumber
   */
  @Step
  public static void switchToTab(int tabNumber) {
    final List<String> tabs = new ArrayList<>(getDriver().getWindowHandles());
    getDriver().switchTo().window(tabs.get(tabNumber));
  }

  /**
   * Get the current time stamp.
   *
   * @return The current time expressed in milliseconds
   */
  @Step
  static long getTimeStamp() {
    return Calendar.getInstance().getTime().getTime();
  }

  /**
   * Select a value from the list box / dropdown based on the index.
   *
   * @param list
   * @param option
   */
  @Step
  public static void selectFromListByIndex(String list, int option) {
    getFluentWait().until(ExpectedConditions.numberOfElementsToBeMoreThan(By.xpath(list), 1));
    new Select(tryFindElement(list)).selectByIndex(option);
  }

  /**
   * Select a value from the list box / dropdown based on the value.
   *
   * @param list
   * @param value
   */
  @Step
  public static void selectFromListByValue(String list, String value) {
    logInfo(String.format("Selecting from list %s by value ", list));
    getFluentWait().until(ExpectedConditions.numberOfElementsToBeMoreThan(By.xpath(list), 1));
    new Select(tryFindElement(list)).selectByValue(value);
    getFluentWait().until(ExpectedConditions.attributeToBe(By.xpath(list), "value", value));
  }

  /**
   * Select a value from the list box / dropdown based on the text.
   *
   * @param list
   * @param text
   */
  @Step
  public static void selectFromListByVisibleText(String list, String text) {
    new Select(tryFindElement(list)).selectByVisibleText(text);
  }

  /**
   * Hover the mouse pointer over the webelement.
   *
   * @param locator A valid xpath locator.
   */
  @Step
  public static void hoverOverElement(String locator) {
    final Actions builder = new Actions(getDriver());
    builder.moveToElement(tryFindElement(locator)).perform();
  }

  /**
   * Get the text from the weblement.
   *
   * @param locator A valid xpath locator.
   * @return
   */
  @Step
  public static String getElementText(String locator) {
    return tryFindElement(locator).getText();
  }

  /**
   * Get the number out of a string.
   *
   * @param text
   * @return
   */
  @Step
  static Integer getNumberFromText(String text) {
    return Integer.parseInt(text.replaceAll("[^0-9]", ""));
  }

  /**
   * Get the value from the weblement.
   *
   * @param locator A valid xpath locator.
   * @return
   */
  @Step
  public static String getElementValue(final String locator) {
    return attributeValue(locator);
  }

  /**
   * Get the attribute value from the webelement.
   *
   * @param locator a valid xpath locator
   * @param attribute the name of an attribute of the located webelement
   * @return the value of the {@code attribute} of the webelement located by {@code locator}
   */
  @Step
  public static String getElementAttributeValue(String locator, String attribute) {
    return tryFindElement(locator).getAttribute(attribute);
  }

  /**
   * Upload an image from the resources path.
   *
   * @param locator a valid xpath locator
   * @param fileName
   */
  @Step
  public static void uploadImage(final String locator, final String fileName) {
    final WebElement uploadField = getDriver().findElement(By.xpath(locator));
    ((RemoteWebElement) uploadField).setFileDetector(new LocalFileDetector());
    final String filePath = System.getProperty("user.dir") + "/src/main/resources/images/";
    log.debug("Image filename \"{}\"", filePath);
    final File file = new File(filePath, FilenameUtils.getName(fileName));
    uploadField.sendKeys(file.getAbsolutePath());
  }

  /** Delete all browser cookies. */
  @Step
  public static void deleteAllCookies() {
    getDriver().manage().deleteAllCookies();
  }

  @Step
  public static void reloadPageIfElementIsNotPresent(final String locator) {
    try {
      assertElementPresent(locator);
    } catch (AssertionError ex) {
      logInfo(String.format("Page url is %s", getDriver().getCurrentUrl()));
      reload();
      reload();
      assertElementPresent(locator);
    }
  }

  /**
   * Attempt to wait {@code milliseconds} (divide by 1000 to get seconds). Note that this method is
   * not guaranteed to wait the requested time and will often wait slightly longer.
   *
   * @param milliseconds Must be a positive integer.
   */
  protected static void sleep(final int milliseconds) {
    try {
      Thread.sleep(milliseconds);
    } catch (final InterruptedException e) {
      log.error("Thread Interrupted, this may cause tests to fail unexpectedly", e);
      Thread.currentThread().interrupt();
    }
  }

  /** @return domain name of the url loaded in the browser */
  public static String getDomainNameFromSiteWithJavascriptExecutor() {
    // Creating the JavascriptExecutor interface object by Type casting
    JavascriptExecutor js = (JavascriptExecutor) getDriver();
    return js.executeScript("return document.domain;").toString();
  }

  /** @return url of the website loaded in the browser */
  public static String getUrlFromSiteWithJavascriptExecutor() {
    // Creating the JavascriptExecutor interface object by Type casting
    JavascriptExecutor js = (JavascriptExecutor) getDriver();
    return js.executeScript("return document.URL;").toString();
  }

  /** @return the title name on the browser tab for the website loaded in the browser */
  public static String getTitleNameFromSiteWithJavascriptExecutor() {
    // Creating the JavascriptExecutor interface object by Type casting
    JavascriptExecutor js = (JavascriptExecutor) getDriver();
    return js.executeScript("return document.title;").toString();
  }

  /** @param locator perform a click operation on the webelement via JavaScriptExecutor */
  public static void clickWithJavaScriptExecutor(final String locator) {
    final WebElement webElement = tryFindElement(locator);
    JavascriptExecutor executor = (JavascriptExecutor) getDriver();
    executor.executeScript("arguments[0].click();", webElement);
  }

  /** @param locator perform a double click on the webelement */
  public static void doubleClick(String locator) {
    Actions actions = new Actions(getDriver());
    actions.doubleClick(tryFindElement(locator)).perform();
  }

  /** Handle basic auth via bi-directional api
   * @param hostName as String
   * @param username as String
   * @param password as String
   */
  public static void handleBasicAuthViaBiDiApi(
      final String hostName, final String username, final String password) {
    WebDriver driver = getAugmentedDriver();
    Predicate<URI> uriPredicate = uri -> uri.getHost().contains(hostName);
    ((HasAuthentication) driver)
        .register(uriPredicate, UsernameAndPassword.of(username, password));
  }

  /**
   * Handle basic auth via chrome dev tools
   * @param username as String
   * @param password as String
   */
  public static void handleBasicAuthViaChromeDevTools(
      final String username, final String password) {
    Augmenter augmenter = new Augmenter();
    DevTools devTools = ((HasDevTools) augmenter.augment(getDriver())).getDevTools();
    devTools.createSession();
    devTools.send(Network.enable(Optional.of(100000), Optional.of(100000), Optional.of(100000)));
    final String auth = username + ":" + password;
    String encodeToString = Base64.getEncoder().encodeToString(auth.getBytes());
    Map<String, Object> headers = new HashMap<>();
    headers.put("Authorization", "Basic " + encodeToString);
    devTools.send(Network.setExtraHTTPHeaders(new Headers(headers)));

//    ((HasAuthentication) getDriver())
//        .register(() -> new UsernameAndPassword(username,password));
  }
}
