package com.template.project.common;

import com.sun.mail.iap.Response;
import io.qameta.allure.Attachment;
import io.qameta.allure.Step;
import lombok.SneakyThrows;
import org.apache.logging.log4j.LogManager;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.Properties;

import static com.template.project.web.utils.BrowserStackFactory.getDriver;
import static org.testng.Reporter.log;

/** Adds content to allure reporting. */
public class Logger {

  static final org.apache.logging.log4j.Logger log = LogManager.getLogger(Logger.class);

  /**
   * This method generates log message in the console & the allure report.
   *
   * @param message Content of message to be added to allure report
   */
  @Step("INFO: {0}")
  public static void logInfo(final String message) {
    if (null == message) {
      return;
    }
    log("INFO: " + message);
    Logger.log.info(message);
  }

  /**
   * This method generates log message in the console & the allure report.
   *
   * @param format Content of message to be added to allure report
   * @param argArray variables to add to format
   */
  @Step("INFO: {0}")
  public static void logInfo(final String format, final Object... argArray) {
    if (null == format) {
      return;
    }
    log(new MessageFormat("INFO: " + format).format(argArray));
  }

  /**
   * This method generates log message in the console & the allure report.
   *
   * @param message Content of message to be added to allure report
   */
  @Step("DEBUG: {0}")
  public static void logDebug(final String message) {
    if (null == message) {
      return;
    }
    log("DEBUG: " + message);
    Logger.log.debug(message);
  }

  /**
   * This method generates warning log message in the console & the allure report.
   *
   * @param message Content of message to be added to allure report
   */
  @Step("WARN: {0}")
  public static void logWarn(final String message) {
    if (null == message) {
      return;
    }
    log("WARN: " + message);
    Logger.log.warn(message);
  }

  @Step("WARN: {0}")
  public static void logWarn(final String format, final Object... argArray) {
    if (null == format) {
      return;
    }
    log(new MessageFormat("WARN: " + format).format(argArray));
  }

  /**
   * This method generates error log message in the console & the allure report.
   *
   * @param message Content of message to be added to allure report
   */
  @Step("ERROR: {0}")
  public static void logError(final String message) {
    if (null == message) {
      return;
    }
    log("ERROR: " + message);
    Logger.log.error(message);
  }

  @Step("ERROR: {0}")
  public static void logError(final String format, final Object... argArray) {
    if (null == format) {
      return;
    }
    log(new MessageFormat("ERROR: " + format).format(argArray));
  }

  /**
   * This method generates a stacktrace log message in the allure report.
   *
   * @param trace Content of message to be added to allure report
   */
  @Step("TRACE: {0}")
  public static void logTrace(StackTraceElement trace) {
    if (null == trace) {
      return;
    }
    log("TRACE:\n" + trace);
    Logger.log.trace(trace);
  }

  /**
   * Log payload of an Http Request in the allure report.
   *
   * @param message Content of message to be added to allure report
   */
  @Attachment(value = "request body :")
  public static String logRequest(String message) {
    if (null == message) {
      return "null request";
    }
    attachJsonToReportPortal(message, "Request body: ");
    return message;
  }

  private static void attachJsonToReportPortal(String jsonBody, String message) {
    File file = null;
    try {
      file = File.createTempFile("rp-test", ".json");
      try (FileWriter writer = new FileWriter(file)) {
        writer.write(jsonBody);
      }
      //      log.info(new ObjectMessage(new ReportPortalMessage(file, message)));
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  /**
   * Log response body in the allure report, note that it does not write to the system log.
   *
   * @param rawRes a valid not null restassured response instance
   */
  @Attachment(value = "response body :")
  public static String logResponse(final Response rawRes) {
    if (null == rawRes) {
      Logger.log.debug("Response was null");
      return "null Response";
    }
    attachJsonToReportPortal(convertRawToString(rawRes), "Response body: ");
    return convertRawToString(rawRes);
  }

  /**
   * This method is to convert response body in Response format to String.
   *
   * @param rawRes A not null restassured response instance.
   * @return an empty string if {@code rawRes is null}
   */
  public static String convertRawToString(final Response rawRes) {
    if (null == rawRes) {
      Logger.log.debug("Attempting to convert null Response to String, will return empty string");
      return "";
    }
    return rawRes.toString();
  }

  /**
   * Take and save a screen shot as an attachment to the allure report.
   *
   * @return the screenshot, which will be handled by the annotation
   */
  @SneakyThrows
  @Attachment(value = "page screenshot", type = "image/png")
  public static byte[] saveScreenshotPNG() {
    Logger.log.debug("Taking screenshot");
    //    log.info(
    //            new ObjectMessage(
    //                    new ReportPortalMessage(
    //                            ((TakesScreenshot)
    // WebDriverHolder.getDriver()).getScreenshotAs(OutputType.FILE),
    //                            "Appending Screenshot")));
    return ((TakesScreenshot) getDriver()).getScreenshotAs(OutputType.BYTES);
  }

  /**
   * Print the console message as a attachment to the allure report.
   *
   * @param message Text which is to be added to the allure report.
   * @return The {@code message}, unchanged.
   */
  @Attachment
  public static String saveTextLog(final String message) {
    Logger.log.debug("Saving text log to allure");
    return message;
  }

  /**
   * Attach the configured system properties to the allure log.
   *
   * @return Should generally not be used by calling code, this is consumed by allure.
   */
  @Attachment(value = "System Environment", type = "text/plain")
  public byte[] attachSystemProperties() {
    Logger.log.debug("Attaching system properties to allure report");
    final Properties props = System.getProperties();
    final StringBuilder result = new StringBuilder();
    for (final String prop : props.stringPropertyNames()) {
      if (prop.toLowerCase().contains("password")) {
        // Dont log things that look like passwords
        result.append(prop).append(" = ").append("********").append("\n");
      } else {
        result.append(prop).append(" = ").append(System.getProperty(prop)).append("\n");
      }
    }
    final String propertyBlock = result.toString();
    Logger.log.debug(propertyBlock);
    return propertyBlock.getBytes();
  }
}
