package com.template.project.web.pages;

import com.template.project.common.ConfigFileReaderUtils;
import com.template.project.common.PropertyFileReaderUtils;
import com.template.project.web.locators.HerukuappBasicAuthPageLocator;

import static com.template.project.common.Cipher.decrypt;
import static com.template.project.common.Logger.logInfo;
import static com.template.project.web.utils.SeleniumUtils.assertElementPresent;
import static com.template.project.web.utils.SeleniumUtils.getElementText;
import static com.template.project.web.utils.Waiters.waitUntilElementVisible;

public class HerokuappBasicAuthPage extends HerukuappBasicAuthPageLocator {

  public String generateEncodedURL() {

    return ConfigFileReaderUtils.getValueFromEnvironmentFile("protocol")
        + ":"
        + "//"
        + PropertyFileReaderUtils.getValueFromTestDataFile("USERNAME")
        + ":"
        + decrypt(PropertyFileReaderUtils.getValueFromTestDataFile("PASSWORD"))
        + "@"
        + ConfigFileReaderUtils.getValueFromEnvironmentFile("host_url")
        + "/basic_auth";
  }

  public final String generateURL() {

    return ConfigFileReaderUtils.getValueFromEnvironmentFile("protocol")
        + ":"
        + "//"
        + ConfigFileReaderUtils.getValueFromEnvironmentFile("host_url")
        + "/basic_auth";
  }

  public void verifyIfPageHeaderIsDisplayed() {
    waitUntilElementVisible(PAGE_HEADER);
    logInfo("Page header:" + getElementText(PAGE_HEADER) + " is displayed");
  }

  public void verifyIfSuccessMessageIsDisplayed() {
    assertElementPresent(SUCCESS_MESSAGE);
    logInfo("Message: " + getElementText(SUCCESS_MESSAGE) + " is displayed on the landing page");
  }

  public void verifyIfPageFooterIsPresent() {
    waitUntilElementVisible(PAGE_FOOTER);
    logInfo("Page footer contains the link:" + getElementText(PAGE_FOOTER_LINK));
  }
}
