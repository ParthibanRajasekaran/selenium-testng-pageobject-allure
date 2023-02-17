package com.template.project.web.tests.featureflow;

import com.template.project.common.ConfigFileReaderUtils;
import com.template.project.web.pages.HerokuappCheckboxPage;
import com.template.project.web.pages.HerokuappLandingPage;
import com.template.project.web.tests.common.BrowserStackBaseTest;
import io.qameta.allure.*;
import org.testng.annotations.Test;

import static com.template.project.common.Groups.INPUT_FIELD;
import static com.template.project.web.utils.SeleniumUtils.openPage;

@Feature("Herokuapp")
@Story("Herokuapp checkbox")
public class CheckBoxTest extends BrowserStackBaseTest {

  private HerokuappLandingPage herokuappLandingPage;
  private HerokuappCheckboxPage herokuappCheckboxPage;

  @Test(groups = {INPUT_FIELD})
  @Severity(SeverityLevel.MINOR)
  @Description("This is a test for handling checkboxes")
  public void checkboxTest() {

    herokuappCheckboxPage = new HerokuappCheckboxPage();
    herokuappLandingPage = new HerokuappLandingPage();
    final String url = ConfigFileReaderUtils.getValueFromEnvironmentFile("host");

    openPage(url);
    herokuappLandingPage.verifyIfPageHeaderIsDisplayed();
    herokuappLandingPage.navigateToSubPage("Checkboxes");
    herokuappCheckboxPage.verifyCheckboxLandingPage();

    herokuappCheckboxPage.checkOnCheckBox1();
    herokuappCheckboxPage.unCheckCheckbox1();
    herokuappCheckboxPage.unCheckCheckbox2();
    herokuappCheckboxPage.checkOnCheckBox2();
  }
}
