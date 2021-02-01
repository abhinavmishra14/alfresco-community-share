package org.alfresco.po.share.site.members;

import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class SiteGroupsPage extends SiteMembersPage
{
    private final By addGroups = By.cssSelector("a[id*='addGroups']");
    private final By searchGroupField = By.cssSelector("input[class*='search-term']");
    private final By searchButton = By.cssSelector("button[id$='site-groups_x0023_default-button-button']");
    private final By searchButtonGroup = By.cssSelector("button[id$='_default-group-search-button-button']");

    private final String groupPath = "[id$='_default-button-GROUP_";
    private final String removeButtonPath = "']>span>span>button";

    public SiteGroupsPage(ThreadLocal<WebDriver> webDriver)
    {
        super(webDriver);
    }

    @Override
    public String getRelativePath()
    {
        return String.format("share/page/site/%s/site-groups", getCurrentSiteName());
    }

    public SiteGroupsPage searchGroupByName(String groupName)
    {
        LOG.info("Search group by name {}", groupName);
        webElementInteraction.clearAndType(searchGroupField, groupName);
        return this;
    }

    public void clickSearch()
    {
        LOG.info("Click search button");
        webElementInteraction.clickElement(searchButton);
    }

    public SiteGroupsPage assertRemoveGroupButtonIsDisplayed(String groupName)
    {
        LOG.info("Assert remove group {} button is displayed", groupName);
        By removeButton = By.cssSelector(groupPath.concat(groupName).concat(removeButtonPath));

        assertTrue(webElementInteraction.isElementDisplayed(removeButton),
            String.format("Remove group button is not displayed %s ", groupName));
        return this;
    }

    public void removeGroup(String groupName)
    {
        WebElement groupRemoveButton = webElementInteraction.findElement(By.cssSelector("span[id$='_default-button-GROUP_" + groupName + "']>span>span>button"));
        webElementInteraction.clickElement(groupRemoveButton);
    }

    public SiteGroupsPage assertAddGroupsButtonIsNotDisplayed()
    {
        LOG.info("Assert Add Groups button is not displayed");
        assertFalse(webElementInteraction.isElementDisplayed(addGroups),
            "Add Groups button is displayed");
        return this;
    }

    public void clickSearchButton()
    {
        webElementInteraction.clickElement(searchButtonGroup);
    }
}