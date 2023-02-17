package com.template.project.web.tests.featureflow;

import com.template.project.common.PropertyFileReaderUtils;
import com.template.project.web.pages.HerokuappBasicAuthPage;
import com.template.project.web.tests.common.BrowserStackBaseTest;
import com.template.project.web.utils.SeleniumUtils;
import io.qameta.allure.Description;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static com.template.project.common.Cipher.decrypt;
import static com.template.project.common.Groups.AUTH;
import static com.template.project.web.utils.SeleniumUtils.handleBasicAuthViaChromeDevTools;

@Feature("Herokuapp")
@Story("Herokuapp Basic Authorization")
public class BasicAuthTest extends BrowserStackBaseTest {

  private HerokuappBasicAuthPage herokuappBasicAuthPage;

  @BeforeMethod(alwaysRun = true)
  public void setup() {
    herokuappBasicAuthPage = new HerokuappBasicAuthPage();
  }

  @Test(groups = {AUTH}, enabled = false)
  @Description(
      "This is to validate the authorization via encoded URL without chrome dev tool protocol")
  public void navigateToEncodedURL() {
    final String url = herokuappBasicAuthPage.generateEncodedURL();
    SeleniumUtils.openPage(url);
    herokuappBasicAuthPage.verifyIfPageHeaderIsDisplayed();
    herokuappBasicAuthPage.verifyIfSuccessMessageIsDisplayed();
    herokuappBasicAuthPage.verifyIfPageFooterIsPresent();
  }

//
//  @Test(groups = {AUTH}, enabled = false)
//  @Description("This is to validate the authorization via Bi-Directional protocol")
//  public void basicAuthUsingBiDiApi() {
//    handleBasicAuthViaBiDiApi(
//        ConfigFileReaderUtils.getValueFromEnvironmentFile("host_url"),
//        PropertyFileReaderUtils.getValueFromTestDataFile("USERNAME"),
//        decrypt(PropertyFileReaderUtils.getValueFromTestDataFile("PASSWORD")));
//    SeleniumUtils.openPage(herokuappBasicAuthPage.generateURL());
//    herokuappBasicAuthPage.verifyIfPageHeaderIsDisplayed();
//    herokuappBasicAuthPage.verifyIfSuccessMessageIsDisplayed();
//    herokuappBasicAuthPage.verifyIfPageFooterIsPresent();
//  }

  @Test(groups = {AUTH}, enabled = false)
  @Description("This is to validate the authorization via Chrome Dev Tools protocol")
  public void basicAuthUsingChromeDevTools() {
    handleBasicAuthViaChromeDevTools(
        PropertyFileReaderUtils.getValueFromTestDataFile("USERNAME"),
        decrypt(PropertyFileReaderUtils.getValueFromTestDataFile("PASSWORD")));
    SeleniumUtils.openPage(herokuappBasicAuthPage.generateURL());
    herokuappBasicAuthPage.verifyIfPageHeaderIsDisplayed();
    herokuappBasicAuthPage.verifyIfSuccessMessageIsDisplayed();
    herokuappBasicAuthPage.verifyIfPageFooterIsPresent();
  }
}
