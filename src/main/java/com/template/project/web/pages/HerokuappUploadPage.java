package com.template.project.web.pages;

import com.template.project.web.locators.HerukuappUploadPageLocator;

import static com.template.project.common.Logger.logInfo;
import static com.template.project.web.utils.SeleniumUtils.*;
import static com.template.project.web.utils.Waiters.waitUntilElementVisible;

public class HerokuappUploadPage extends HerukuappUploadPageLocator {

  public void verifyIfPageHeaderIsDisplayed() {
    waitUntilElementVisible(PAGE_HEADER);
    logInfo("Page header:" + getElementText(PAGE_HEADER) + " is displayed");
  }

  public void uploadImageToField(String locator, String fileName) {
    uploadImage(locator, fileName);
  }

  public void clickUploadButton() {
    clickElement(UPLOAD_BUTTON);
  }

  public String getUploadedFileName() {
    return getElementText(UPLOADED_FILE_NAME);
  }
}
