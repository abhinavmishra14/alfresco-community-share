package org.alfresco.po.share.user.admin.adminTools;

import org.alfresco.po.share.DeleteDialog;
import org.alfresco.utility.Utility;
import org.alfresco.utility.web.annotation.RenderWebElement;
import org.alfresco.utility.web.browser.WebBrowser;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;

public class TagManagerPage extends AdminToolsPage
{
    private DeleteDialog deleteDialog;
    private EditTagDialog editTagDialog;

    private By editIconSelector = By.cssSelector("a[class$='edit-tag-active']");
    private By deleteIconSelector = By.cssSelector("a[class$='delete-tag-active']");
    private By tableTitle = By.cssSelector(".tags-List>.title");
    private By loadingTagsMessage = By.cssSelector("div[class='dashlet tags-List'] .yui-dt-message");
    private By tableHead = By.cssSelector(".dashlet thead");
    private By nextLink = By.cssSelector("a[id*='next-link']");
    private By nextLinkDisabled = By.cssSelector("span[id*='next-span']");
    private By previousLink = By.cssSelector("a[id*='prev-link']");
    private By previousLinkDisabled = By.cssSelector("span[id*='prev-span']");
    private By pagesList = By.cssSelector("div[id*='list-bar-bottom'] span[id*='pages'] .yui-pg-page");
    private By currentPage = By.cssSelector("span[class*='current-page']");
    @RenderWebElement
    private By searchInput = By.cssSelector("input[id*='search']");
    @RenderWebElement
    private By searchButton = By.cssSelector("button[id*='search']");
    private By noFoundMessage = By.cssSelector("div[class='tags-list-info']");

    private String tagRow = "//b[text()='%s']/../../../../..";

    public TagManagerPage(ThreadLocal<WebBrowser> browser)
    {
        super(browser);
        deleteDialog = new DeleteDialog(browser);
        editTagDialog = new EditTagDialog(browser);
    }

    @Override
    public String getRelativePath()
    {
        return "share/page/console/admin-console/tag-management";
    }

    public EditTagDialog clickEdit(String tag)
    {
        LOG.info(String.format("Click edit for tag: %s", tag));
        WebElement tagRow = getTagRow(tag);
        getBrowser().mouseOver(getBrowser().findElement(searchInput));
        getBrowser().mouseOver(tagRow);
        getBrowser().waitUntilElementVisible(editIconSelector);
        getBrowser().waitUntilElementVisible(tagRow.findElement(editIconSelector)).click();

        return (EditTagDialog) editTagDialog.renderedPage();
    }

    public DeleteDialog clickDelete(String tag)
    {
        LOG.info(String.format("Click delete for tag: %s", tag));
        WebElement tagRow = getTagRow(tag);
        getBrowser().mouseOver(getBrowser().findElement(searchInput));
        getBrowser().mouseOver(tagRow);
        getBrowser().waitUntilElementVisible(tagRow.findElement(deleteIconSelector)).click();

        return (DeleteDialog) deleteDialog.renderedPage();
    }

    private WebElement getTagRow(String tagName)
    {
        return getBrowser().waitWithRetryAndReturnWebElement(By.xpath(String.format(tagRow, tagName)), 1, WAIT_30);
    }

    public boolean isTagDisplayed(String tagName)
    {
        return getBrowser().isElementDisplayed(By.xpath(String.format(tagRow, tagName)));
    }

    private TagManagerPage clickNextPage()
    {
        if (!getElementText(currentPage).equals(Integer.toString(getBrowser().findElements(pagesList).size())))
        {
            clickElement(nextLink);
        }
        return (TagManagerPage) this.renderedPage();
    }

    public TagManagerPage assertSearchButtonIsDisplayed()
    {
        LOG.info("Assert Search button is displayed");
        Assert.assertTrue(getBrowser().isElementDisplayed(searchButton), "Search button is displayed");
        return this;
    }

    public TagManagerPage assertSearchInputFieldDisplayed()
    {
        LOG.info("Assert Search input is displayed");
        Assert.assertTrue(getBrowser().isElementDisplayed(searchInput), "Search input is displayed");
        return this;
    }

    public TagManagerPage assertTableTitleIsCorrect()
    {
        LOG.info(String.format("Assert tags table title is: %s", language.translate("tagManager.tableTitle")));
        Assert.assertEquals(getElementText(tableTitle), language.translate("tagManager.tableTitle"), "Table title");
        return this;
    }

    public TagManagerPage assertTableHeadersAreCorrect()
    {
        LOG.info("Assert tag table headers are correct");
        Assert.assertEquals(getElementText(tableHead), language.translate("tagManager.tableHead"), "Table headers");
        return this;
    }

    public TagManagerPage searchTagWithRetry(String tagName)
    {
        LOG.info(String.format("Search for tag: %s", tagName));
        search(tagName);
        boolean found = isTagDisplayed(tagName);
        int retryCount = 0;
        while(!found && retryCount < WAIT_30)
        {
            Utility.waitToLoopTime(1);
            LOG.error(String.format("Wait for tag %s to be displayed - retry: %s", tagName, retryCount));
            clickSearchAndWaitForTagTableToBeLoaded();
            found = isTagDisplayed(tagName);
            retryCount++;
        }
        return this;
    }

    private void clickSearchAndWaitForTagTableToBeLoaded()
    {
        WebElement search = getBrowser().findElement(searchButton);
        getBrowser().mouseOver(search);
        getBrowser().clickJS(search);
        if(!getBrowser().isElementDisplayed(noFoundMessage))
        {
            getBrowser().waitUntilElementHasAttribute(getBrowser().findElement(loadingTagsMessage), "style", "display: none;");
        }
    }

    public TagManagerPage search(String tagName)
    {
        getBrowser().waitUntilElementVisible(searchInput);
        clearAndType(searchInput, tagName);
        clickSearchAndWaitForTagTableToBeLoaded();
        return (TagManagerPage) this.renderedPage();
    }

    public TagManagerPage assertTagIsDisplayed(String tag)
    {
        LOG.info(String.format("Assert tag %s is displayed", tag));
        Assert.assertTrue(isTagDisplayed(tag), String.format("Tag %s was found", tag));
        return this;
    }

    public TagManagerPage assertTagIsNotDisplayed(String tag)
    {
        LOG.info(String.format("Assert tag %s is NOT displayed", tag));
        Assert.assertFalse(isTagDisplayed(tag), String.format("Tag %s was found", tag));
        return this;
    }
}