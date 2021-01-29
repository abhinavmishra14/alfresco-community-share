package org.alfresco.po.share.site.members;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.alfresco.po.share.site.SiteCommon;
import org.alfresco.utility.model.UserModel;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class SiteMembersPage extends SiteCommon<SiteMembersPage>
{
    private final int BEGIN_INDEX = 0;
    private final String EMPTY_SPACE = " ";

    private final By listNameLocator = By.cssSelector("td+td>div.yui-dt-liner>h3");
    private final By siteUsers = By.cssSelector("a[id*='site-members-link']");
    private final By siteGroups = By.cssSelector("a[id*='site-groups-link']");
    private final By pendingInvites = By.cssSelector("a[id*='pending-invites-link']");
    private final By siteMemberRow = By.cssSelector("tbody[class='yui-dt-data'] tr");
    private final By dropDownOptionsList = By.cssSelector("div.visible ul.first-of-type li a");
    private final By currentRoleButton = By.cssSelector("td[class*='role'] button");
    private final By removeButton = By.cssSelector(".uninvite button");
    private final By currentRole = By.cssSelector("td[class*='role'] div :first-child");
    private final String memberName = "td>a[href$='%s/profile']";

    public SiteMembersPage(ThreadLocal<WebDriver> webDriver)
    {
        super(webDriver);
    }

    @Override
    public String getRelativePath()
    {
        return String.format("share/page/site/%s/site-members", getCurrentSiteName());
    }

    public SiteMembersPage assertSelectedRoleEqualsTo(String expectedRole, String groupName)
    {
        String actualFormattedRole = getMemberName(groupName).findElement(currentRole).getText()
            .substring(BEGIN_INDEX, getMemberName(groupName).findElement(currentRole).getText()
                .indexOf(EMPTY_SPACE));

        assertEquals(actualFormattedRole, expectedRole,
            String.format("Selected roles not equals with expected %s ", expectedRole));
         return this;
    }

    public String getRole(String name)
    {
        return getMemberName(name).findElement(currentRole).getText();
    }

    public boolean isRemoveButtonEnabledForMember(String name)
    {
        return getMemberName(name).findElement(removeButton).isEnabled();
    }

    /**
     * Method returns if the role drop down button is displayed for the specified name
     *
     * @param name String
     * @return True if the role drop down button is displayed for the specified name
     */
    public boolean isRoleButtonDisplayed(String name)
    {
        return webElementInteraction.isElementDisplayed(getMemberName(name), currentRoleButton);
    }

    public SiteGroupsPage openSiteGroupsPage()
    {
        webElementInteraction.clickElement(siteGroups);
        return new SiteGroupsPage(webDriver);
    }

    public SiteUsersPage openSiteUsersPage()
    {
        webElementInteraction.clickElement(siteUsers);
        return new SiteUsersPage(webDriver);
    }

    public List<String> getSiteMembersList()
    {
        webElementInteraction.waitUntilElementsAreVisible(listNameLocator);
        List<String> names = Collections.synchronizedList(new ArrayList<>());
        for (WebElement listName : webElementInteraction.findElements(listNameLocator))
        {
            names.add(listName.getText());
        }
        return names;
    }

    public SiteMembersPage assertSiteGroupNameEqualsTo(String expectedSiteMemberName)
    {
        LOG.info("Assert site member name equals to: {}", expectedSiteMemberName);
        assertTrue(getSiteMembersList().stream().anyMatch(member -> member.equals(expectedSiteMemberName)),
            String.format("Site member name not equals %s ", expectedSiteMemberName));
        return this;
    }

    public SiteMembersPage isSiteMember(UserModel userModel)
    {
        return assertSiteGroupNameEqualsTo(userModel.getFirstName() + " " + userModel.getLastName());
    }

    public void waitSiteMemberToDisappear(String siteMember)
    {
        webElementInteraction.waitUntilElementDisappears(
            webElementInteraction.findElement(By.cssSelector(String.format(memberName, siteMember))));
    }

    public WebElement getMemberName(String name)
    {
        return webElementInteraction.findFirstElementWithValue(siteMemberRow, name);
    }

    public void changeRoleForMember(String newRole, String userName)
    {
        webElementInteraction.clickElement(getMemberName(userName).findElement(currentRoleButton));
        webElementInteraction.waitUntilElementsAreVisible(dropDownOptionsList);
        webElementInteraction.selectOptionFromFilterOptionsList(newRole, webElementInteraction.findElements(dropDownOptionsList));
        waitUntilNotificationMessageDisappears();
    }

    public boolean isPendingInvitesDisplayed()
    {
        return webElementInteraction.isElementDisplayed(pendingInvites);
    }
}