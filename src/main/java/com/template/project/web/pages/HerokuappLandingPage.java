package com.template.project.web.pages;

import com.template.project.web.locators.HerukuappLandingPageLocator;

import static com.template.project.common.Logger.logInfo;
import static com.template.project.web.utils.SeleniumUtils.*;
import static com.template.project.web.utils.Waiters.waitUntilElementVisible;

public class HerokuappLandingPage extends HerukuappLandingPageLocator {

  public void navigateToSubPage(String subPageName) {
    final String subPageLink = "//li/a[contains(text(),'" + subPageName + "')]";
    assertElementPresent(subPageLink);
    clickElement(subPageLink);
  }

  public void verifyIfPageHeaderIsDisplayed() {
    waitUntilElementVisible(PAGE_HEADER);
    logInfo("Page header:" + getElementText(PAGE_HEADER) + " is displayed");
    waitUntilElementVisible(PAGE_SUB_HEADER);
    logInfo("Sub header:" + getElementText(PAGE_SUB_HEADER) + " is displayed");
  }
}
