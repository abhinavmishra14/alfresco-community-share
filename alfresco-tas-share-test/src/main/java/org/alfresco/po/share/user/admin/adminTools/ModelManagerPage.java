package org.alfresco.po.share.user.admin.adminTools;

import org.alfresco.po.share.user.admin.adminTools.DialogPages.CreateModelDialogPage;
import org.alfresco.po.share.user.admin.adminTools.DialogPages.ImportModelDialogPage;
import org.alfresco.utility.web.HtmlPage;
import org.alfresco.utility.web.annotation.PageObject;
import org.alfresco.utility.web.annotation.RenderWebElement;
import org.openqa.selenium.By;
import org.openqa.selenium.NoAlertPresentException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindAll;
import org.openqa.selenium.support.FindBy;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * Created by Mirela Tifui on 11/28/2016.
 */
@PageObject
public class ModelManagerPage extends AdminToolsPage
{
    @Autowired CreateModelDialogPage createModelDialogPage;
    @Autowired ImportModelDialogPage importModelDialogPage;

    @RenderWebElement
    @FindBy (css="span[class*='createButton'] span[class='dijitReset dijitStretch dijitButtonContents']")
    private WebElement createModelButton;

    @RenderWebElement
    @FindBy (css="span[class*='importButton'] span[class='dijitReset dijitStretch dijitButtonContents']")
    private WebElement importModelButton;

    private By nameColumn = By.cssSelector("th[class*=' nameColumn '] span");

    private By namespaceColumn =  By.cssSelector("th[class*=' namespaceColumn '] span");

    private By statusColumn = By.cssSelector("th[class*=' statusColumn '] span");

    private By actionsColumn = By.cssSelector("th[class*=' actionsColumn '] span");

    private By modelsList =  By.cssSelector("tr[id^='alfresco_lists_views_layouts_Row']");

    public By actionsButton = By.cssSelector("div[id^='alfresco_menus_AlfMenuBarPopup']");

    public By status = By.xpath(".//td[contains(@class, 'statusColumn ')]//span[@class='value']");

    @FindBy (css="div.alfresco-lists-views-AlfListView__no-data")
    private WebElement noModelsText;

    @FindAll(@FindBy (css= "tr[id^='alfresco_lists_views_layouts_Row']"))
    private List<WebElement> modelItemsList;

    @Override
    public String getRelativePath()
    {
        return "share/page/console/admin-console/custom-model-manager";
    }

    public boolean isCreateModelButtonDisplayed()
    {
        return browser.isElementDisplayed(createModelButton);
    }

    public boolean isImportModelButtonDisplayed()
    {
        return browser.isElementDisplayed(importModelButton);
    }

    public boolean isNameColumnDisplayed()
    {
        return browser.isElementDisplayed(nameColumn);
    }

    public boolean isNamespaceColumnDisplayed()
    {
        return browser.isElementDisplayed(namespaceColumn);
    }

    public boolean isStatusColumnDisplayed()
    {
        return browser.isElementDisplayed(statusColumn);
    }

    public boolean isActionsColumnDisplayed()
    {
        return browser.isElementDisplayed(actionsColumn);
    }

    public String getNoModelsFoundText()
    {
        return noModelsText.getText();
    }

    public CreateModelDialogPage clickCreateModelButton()
    {
        createModelButton.click();
        return (CreateModelDialogPage) createModelDialogPage.renderedPage();
    }

    public WebElement selectRow (String modelName)
    {
        browser.waitUntilElementIsDisplayedWithRetry(modelsList, 6);
        List<WebElement> itemsList = browser.findElements(modelsList);
        return browser.findFirstElementWithValue(itemsList, modelName);
    }

    public WebElement selectModelByName(String modelName)
    {
        return browser.findElement(By.xpath("//tr[contains(@id,'alfresco_lists_views_layouts_Row')]//span[text()='" + modelName + "']/../../../.."));
    }

    public boolean isModelDisplayed(String modelName)
    {
        return browser.isElementDisplayed(selectRow(modelName));
    }

    public String getModelDetails(String modelName)
    {
        browser.waitUntilElementVisible(selectRow(modelName));
        //browser.waitUntilWebElementIsDisplayedWithRetry(selectAspectRow(modelName), 6);
        return selectRow(modelName).getText();
    }

    public ImportModelDialogPage clickImportModel()
    {
        importModelButton.click();
        return (ImportModelDialogPage) importModelDialogPage.renderedPage();
    }

    public HtmlPage clickModelName(String modelName, HtmlPage page)
    {
        //browser.waitInSeconds(4);
        browser.waitUntilElementClickable(By.xpath("//tr[contains(@id,'alfresco_lists_views_layouts_Row')]//span[text()='" + modelName + "']"), 15);
        browser.findElement(By.xpath("//tr[contains(@id,'alfresco_lists_views_layouts_Row')]//span[text()='" + modelName + "']")).click();
        return page.renderedPage();
    }

    public Boolean isActionsButtonDisplayed()
    {
        return browser.isElementDisplayed(actionsButton);
    }

    public void clickActionsButtonForModel(String modelName)
    {
        //browser.waitInSeconds(2);
        browser.waitUntilElementVisible(selectModelByName(modelName));
        browser.waitUntilElementClickable(selectModelByName(modelName).findElement(actionsButton), 5);
        selectModelByName(modelName).findElement(actionsButton).click();
    }

    public void mouseOverModelItem(String modelName)
    {
        browser.mouseOver(selectModelByName(modelName));
    }

    public boolean isActionAvailable(String action)
    {
        browser.waitUntilElementsVisible(By.cssSelector("div[id^='alfresco_menus_AlfMenuBarPopup_'] td[class ='dijitReset dijitMenuItemLabel']"));
        List<WebElement> actionsOptions = browser.findElements(By.cssSelector("div[id^='alfresco_menus_AlfMenuBarPopup_'] td[class ='dijitReset dijitMenuItemLabel']"));
        for (WebElement actionOption : actionsOptions)
        {
            if (actionOption.getText().equals(action))
                return true;
        }
        return false;
    }

    public HtmlPage clickOnAction(String actionName, HtmlPage page)
    {
        List<WebElement> actionsOptions = browser.findElements(By.cssSelector("div[id^='alfresco_menus_AlfMenuBarPopup_'] td[class ='dijitReset dijitMenuItemLabel']"));
        browser.findFirstElementWithValue(actionsOptions, actionName).click();
        return page.renderedPage();
    }

    public String getModelStatus(String modelName)
    {
        //browser.waitUntilWebElementIsDisplayedWithRetry(selectModelByName(modelName).findElement(status), 5);
        return selectModelByName(modelName).findElement(status).getText();
    }

    public boolean isAlertPresent()
    {
        try
        {
            browser.switchTo().alert();
            return true;
        }
        catch (NoAlertPresentException noAlertPresentException)
        {
            return false;
        }
    }
}
