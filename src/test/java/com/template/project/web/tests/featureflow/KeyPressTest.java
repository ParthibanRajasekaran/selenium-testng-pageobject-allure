package com.template.project.web.tests.featureflow;

import com.template.project.common.SampleDataProvider;
import com.template.project.web.pages.HerokuappKeyPressPage;
import com.template.project.web.pages.HerokuappLandingPage;
import com.template.project.web.tests.common.BrowserStackBaseTest;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import org.testng.annotations.Test;

import static com.template.project.common.ConfigFileReaderUtils.getValueFromEnvironmentFile;
import static com.template.project.common.Groups.INPUT_FIELD;
import static com.template.project.web.utils.SeleniumUtils.clickElement;
import static com.template.project.web.utils.SeleniumUtils.openPage;

@Feature("Herokuapp")
@Story("Herokuapp Key Press")
public class KeyPressTest extends BrowserStackBaseTest {

  private HerokuappLandingPage herokuappLandingPage;
  private HerokuappKeyPressPage herokuappKeyPressPage;


  @Test(
      dataProvider = "testData",
      dataProviderClass = SampleDataProvider.class,
      groups = {INPUT_FIELD})
  public void keyPressTest(String Key, String Result) {
    herokuappLandingPage = new HerokuappLandingPage();
    herokuappKeyPressPage = new HerokuappKeyPressPage();

    final String url = getValueFromEnvironmentFile("host");
    openPage(url);

    herokuappLandingPage.verifyIfPageHeaderIsDisplayed();
    herokuappLandingPage.navigateToSubPage("Key Presses");
    herokuappKeyPressPage.verifyIfPageHeaderIsDisplayed();
    herokuappKeyPressPage.sendKeyAndGetResult(Key, Result);
  }
}
