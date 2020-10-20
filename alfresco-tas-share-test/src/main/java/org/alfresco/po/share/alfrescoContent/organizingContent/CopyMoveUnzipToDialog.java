package org.alfresco.po.share.alfrescoContent.organizingContent;

import java.util.List;

import org.alfresco.po.share.SharePage;
import org.alfresco.po.share.alfrescoContent.SelectDestinationDialog;
import org.alfresco.po.share.site.SiteManagerDeleteSiteDialog;
import org.alfresco.utility.model.FolderModel;
import org.alfresco.utility.model.SiteModel;
import org.alfresco.utility.web.annotation.PageObject;
import org.alfresco.utility.web.annotation.RenderWebElement;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindAll;
import org.openqa.selenium.support.FindBy;
import org.testng.Assert;

/**
 * @author Laura.Capsa
 */
@PageObject
public class CopyMoveUnzipToDialog extends SelectDestinationDialog
{
    @FindAll (@FindBy (css = "div[id='ALF_COPY_MOVE_DIALOG'] span[class*='alfresco-buttons-AlfButton']"))
    private List<WebElement> buttonsList;

    @FindBy (css = ".message")
    private WebElement message;

    @FindBy (css = "button[id$='_default-copyMoveTo-link-button']")
    private WebElement createLinkButton;

    @RenderWebElement
    @FindBy (css = "button[id$='_default-copyMoveTo-ok-button']")
    private WebElement unzipCopyMoveButton;

    @FindBy (css = "button[id$='_default-copyMoveTo-cancel-button']")
    private WebElement cancelButton;

    @FindBy (css = "button[id$='copyMoveTo-recentsites-button']")
    private WebElement recentSitesDestination;

    @FindBy (css = "div[id$='default-copyMoveTo-treeview']")
    private WebElement folderPathsArea;

    private By createLinkMessage = By.cssSelector("div[id*='message_c'] .bd .message");
    private String siteToSelect = "//h4[text()='%s']";
    private String folderElementToSelect = "//span[@class='ygtvlabel' and text()='%s']";

    public CopyMoveUnzipToDialog selectRecentSitesDestination()
    {
        recentSitesDestination.click();
        return this;
    }

    public CopyMoveUnzipToDialog selectSite(SiteModel site)
    {
        browser.waitUntilElementVisible(By.xpath(String.format(siteToSelect, site.getTitle()))).click();
        return this;
    }

    public CopyMoveUnzipToDialog selectFolder(FolderModel folderToSelect)
    {
        browser.waitUntilElementVisible(folderPathsArea);
        WebElement folder = browser.waitUntilChildElementIsPresent(folderPathsArea,
            By.xpath(String.format(folderElementToSelect, folderToSelect.getName())));
        browser.waitUntilElementClickable(folder).click();
        return this;
    }

    /**
     * Click on a button from the bottom of Copy/MoveTo dialog
     *
     * @param buttonName name of the button to be clicked (e.g: Move, Cancel)
     */
    public void clickButton(String buttonName)
    {
        for (WebElement aButtonsList : buttonsList)
        {
            if (aButtonsList.getText().equals(buttonName))
                aButtonsList.click();
        }
    }

    public void clickCreateLink()
    {
        browser.waitUntilElementClickable(createLinkButton).click();
        waitUntilNotificationMessageDisappears();
    }

    public SharePage clickCreateLink(SharePage page)
    {
        browser.waitUntilElementClickable(createLinkButton).click();
        waitUntilMessageDisappears();
        return (SharePage) page.renderedPage();
    }

    public boolean isCreateLinkButtonDisplayed()
    {
        return getBrowser().isElementDisplayed(createLinkButton);
    }

    public String getMessage()
    {
        return message.getText();
    }

    public CopyMoveUnzipToDialog assertCreateLinkButtonIsDisplayed()
    {
        LOG.info("Assert Create Link button is displayed");
        Assert.assertTrue(browser.isElementDisplayed(createLinkButton), "Create link button is displayed");
        return this;
    }

    public CopyMoveUnzipToDialog assertCreateLinkButtonIsNotDisplayed()
    {
        LOG.info("Assert Create Link button is displayed");
        Assert.assertFalse(browser.isElementDisplayed(createLinkButton), "Create link button is displayed");
        return this;
    }

    public SharePage clickUnzipButton(SharePage page)
    {
        getBrowser().waitUntilElementClickable(unzipCopyMoveButton, 3).click();
        return (SharePage) page.renderedPage();
    }

    public SharePage clickCopyButton(SharePage page)
    {
        getBrowser().waitUntilElementClickable(unzipCopyMoveButton).click();
        waitUntilMessageDisappears();
        return (SharePage) page.renderedPage();
    }

    public SharePage clickCancelButton(SharePage page)
    {
        getBrowser().waitUntilElementClickable(cancelButton, 3).click();
        return (SharePage) page.renderedPage();
    }

    public SharePage clickMoveButton(SharePage page)
    {
        getBrowser().waitUntilElementClickable(unzipCopyMoveButton).click();
        waitUntilMessageDisappears();
        return (SharePage) page.renderedPage();
    }
}