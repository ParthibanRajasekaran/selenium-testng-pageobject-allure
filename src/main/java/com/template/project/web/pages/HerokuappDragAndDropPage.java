package com.template.project.web.pages;

import com.template.project.web.locators.HerokuappDragAndDropPageLocator;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

import static com.template.project.common.Logger.logInfo;
import static com.template.project.web.utils.SeleniumUtils.getElementText;
import static com.template.project.web.utils.SeleniumUtils.tryFindElement;
import static com.template.project.web.utils.Waiters.waitUntilElementVisible;
import static com.template.project.web.utils.WebDriverHolder.getDriver;

public class HerokuappDragAndDropPage extends HerokuappDragAndDropPageLocator {

    public void verifyIfPageHeaderIsDisplayed() {
        waitUntilElementVisible(PAGE_HEADER);
        logInfo("Page header:" + getElementText(PAGE_HEADER) + " is displayed");
    }

    public void dragAndDropSourceElementToTargetElement(){
        WebDriverWait wait = new WebDriverWait(getDriver(), Duration.ofSeconds(10));

        // Create an instance of Actions class
        Actions actions = new Actions(getDriver());

        WebElement sourceElement = tryFindElement(SOURCE_ELEMENT);
        WebElement targetElement = tryFindElement(TARGET_ELEMENT);

        wait.until(ExpectedConditions.elementToBeClickable(sourceElement));
        wait.until(ExpectedConditions.elementToBeClickable(targetElement));

        // Use the dragAndDrop() method to perform drag and drop
        actions.dragAndDrop(sourceElement, targetElement).perform();
    }

}
