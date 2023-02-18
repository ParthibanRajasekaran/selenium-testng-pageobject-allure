package com.template.project.web.tests.featureflow;

import com.template.project.common.ConfigFileReaderUtils;
import com.template.project.web.pages.HerokuappDragAndDropPage;
import com.template.project.web.pages.HerokuappLandingPage;
import com.template.project.web.tests.common.BaseTest;
import io.qameta.allure.*;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static com.template.project.web.utils.SeleniumUtils.openPage;
import static com.template.project.web.utils.WebDriverHolder.getDriver;
import static java.lang.Thread.sleep;

@Feature("Herokuapp")
@Story("Herokuapp drag and drop")
public class DragAndDropTest extends BaseTest {


        private HerokuappLandingPage herokuappLandingPage;
        private HerokuappDragAndDropPage herokuappDragAndDropPage;

        @BeforeMethod(alwaysRun = true)
        public void setup() throws InterruptedException {
            herokuappDragAndDropPage = new HerokuappDragAndDropPage();
            herokuappLandingPage = new HerokuappLandingPage();
            final String url = ConfigFileReaderUtils.getValueFromEnvironmentFile("host");
            openPage(url);
            getDriver().manage().window().maximize();
            herokuappLandingPage.verifyIfPageHeaderIsDisplayed();
            herokuappLandingPage.navigateToSubPage("Drag and Drop");
            sleep(5000);
        }

        @Test
        @Severity(SeverityLevel.MINOR)
        @Description("This is a test for handling drag and drop scenarios")
        public void checkboxTest() {
            herokuappDragAndDropPage.verifyIfPageHeaderIsDisplayed();
            herokuappDragAndDropPage.dragAndDropSourceElementToTargetElement();
        }
}
