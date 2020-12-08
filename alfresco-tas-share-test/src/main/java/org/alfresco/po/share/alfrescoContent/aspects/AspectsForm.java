package org.alfresco.po.share.alfrescoContent.aspects;

import java.util.List;

import org.alfresco.po.share.BaseDialogComponent;
import org.alfresco.utility.web.annotation.RenderWebElement;
import org.alfresco.utility.web.browser.WebBrowser;
import org.alfresco.utility.web.common.Parameter;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;

public class AspectsForm extends BaseDialogComponent
{
    @RenderWebElement
    private final By aspectsFormTitle = By.cssSelector("[id$='default-aspects-title']");
    private final By availableToAddPanel = By.cssSelector("[id$='default-aspects-left']");
    private final By closeButton = By.cssSelector(".container-close");
    private final By cancelButton = By.cssSelector("button[id*='cancel']");
    private final By saveButton = By.cssSelector("button[id*='ok-button']");
    private final By availableAspectsList = By.cssSelector("div[class*='list-left yui-dt'] tr[class*='yui-dt-rec']");
    private final By aspectsSelectedList = By.cssSelector("div[class*='list-right yui-dt'] tr[class*='yui-dt-rec']");
    private final By addButtonsList = By.cssSelector("span[class*='addIcon']");
    private final By removeButtonsList = By.cssSelector("span[class*='removeIcon']");

    public AspectsForm(ThreadLocal<WebBrowser> browser)
    {
        super(browser);
    }

    public List<WebElement> getAddButtonsList()
    {
        return getBrowser().findElements(addButtonsList);
    }

    public List<WebElement> getRemoveButtonsList()
    {
        return getBrowser().findElements(removeButtonsList);
    }

    public boolean areAddButtonsDisplayed()
    {
        List<WebElement> aspectsToBeAddedList = getBrowser().findElements(availableAspectsList);
        int numberOfAspectsAvailableToAdd = aspectsToBeAddedList.size();
        int numberOfAddButtonsDisplayed = getAddButtonsList().size();

        return numberOfAspectsAvailableToAdd == numberOfAddButtonsDisplayed;
    }

    public boolean areRemoveButtonsDisplayed()
    {
        List<WebElement> selectedAspectsList = getBrowser().findElements(aspectsSelectedList);
        int numberOfSelectedAspects = selectedAspectsList.size();
        int numberOfRemoveButtonsDisplayed = getRemoveButtonsList().size();

        return numberOfSelectedAspects == numberOfRemoveButtonsDisplayed;
    }

    public boolean isAspectsFormTitleDisplayed()
    {
        return getBrowser().isElementDisplayed(aspectsFormTitle);
    }

    public boolean isAvailableToAddPanelDisplayed()
    {
        return getBrowser().isElementDisplayed(availableToAddPanel);
    }

    public boolean isCurrentlySelectedPanel()
    {
        return getBrowser().isElementDisplayed(availableToAddPanel);
    }

    public boolean isCancelButtonDisplayed()
    {
        return getBrowser().isElementDisplayed(cancelButton);
    }

    public boolean isSaveButtonDisplayed()
    {
        return getBrowser().isElementDisplayed(saveButton);
    }

    @Override
    public boolean isCloseButtonDisplayed()
    {
        return getBrowser().isElementDisplayed(closeButton);
    }

    public WebElement getAvailableAspects(final String aspectName)
    {
        return getBrowser().findFirstElementWithValue(availableAspectsList, aspectName);
    }

    public WebElement getSelectedAspects(final String aspectName)
    {
        return getBrowser().findFirstElementWithValue(aspectsSelectedList, aspectName);
    }

    public boolean isAspectPresentOnAvailableAspectList(String aspectName)
    {
        return getBrowser().isElementDisplayed(getAvailableAspects(aspectName));
    }

    public boolean isAspectPresentOnCurrentlySelectedList(String aspectName)
    {
        return getBrowser().isElementDisplayed(getSelectedAspects(aspectName));
    }

    public void addAspect(String aspectName)
    {
        try
        {
            WebElement availableAspect = getBrowser().findFirstElementWithValue(availableAspectsList, aspectName);
            Parameter.checkIsMandotary("Available aspect", availableAspect);
            getBrowser().scrollIntoView(availableAspect);
            availableAspect.findElement(addButtonsList).click();
        }
        catch (NoSuchElementException noSuchElementExp)
        {
            LOG.error("Add icon for item: " + aspectName + " is not present.", noSuchElementExp);
        }
    }

    public void removeAspect(String aspectName)
    {
        try
        {
            WebElement selectedAspect = getBrowser().findFirstElementWithValue(aspectsSelectedList, aspectName);
            Parameter.checkIsMandotary("Selected aspect", selectedAspect);
            selectedAspect.findElement(removeButtonsList).click();
        } catch (NoSuchElementException noSuchElementExp)
        {
            LOG.error("Remove icon for item: " + aspectName + " is not present.", noSuchElementExp);
        }
    }

    public void clickApplyChangesButton()
    {
        getBrowser().findElement(saveButton).click();
        waitUntilNotificationMessageDisappears();
    }

    public void clickCloseButton()
    {
        getBrowser().findElement(closeButton).click();
    }

    public void clickCancelButton()
    {
        getBrowser().findElement(cancelButton).click();
    }
}
