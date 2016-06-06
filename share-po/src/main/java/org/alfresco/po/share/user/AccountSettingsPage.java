package org.alfresco.po.share.user;

import java.util.concurrent.TimeUnit;

import org.alfresco.po.HtmlPage;
import org.alfresco.po.RenderTime;
import org.alfresco.po.exception.PageException;
import org.alfresco.po.share.InviteToAlfrescoPage;
import org.alfresco.po.share.MyTasksPage;
import org.alfresco.po.share.SharePage;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;

/**
 * My profile page object, holds all element of the html page relating to
 * share's my profile page.
 */
public class AccountSettingsPage extends SharePage
{
    private final Log logger = LogFactory.getLog(this.getClass());

    private static final By MANAGEUSERS_LINK = By.cssSelector("a[href='manage-users']");
    private static final By SUB_TITLE = By.cssSelector("div[class$='first cloud-manage-users-header-title']>h1");
    private static final By INVITE_BUTTON = By.cssSelector("button[id$='cloud-console_x0023_default-newUser-button']");


    @SuppressWarnings("unchecked")
    @Override
    public AccountSettingsPage render(RenderTime timer)
    {
        basicRender(timer);
        return this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public AccountSettingsPage render()
    {
        return render(new RenderTime(maxPageLoadingTime));
    }

    /**
     * Clicks on Manage Users link.
     *
     * @return {@link MyTasksPage}
     */
    public HtmlPage selectManageUsers()
    {
        try
        {
            logger.info("Select Manage Users");
            findAndWait(MANAGEUSERS_LINK).click();
            waitForElement(SUB_TITLE, TimeUnit.SECONDS.convert(maxPageLoadingTime, TimeUnit.MILLISECONDS));
            return getCurrentPage();
        }
        catch (NoSuchElementException te)
        {
            throw new PageException("Not able to find the Manage Users Page.");
        }
       
    }

    /**
     * Clicks on Invite People button to invoke Invite To Alfresco Page.
     * 
     * @return NewUserPage
     */
    public InviteToAlfrescoPage selectInvitePeople()
    {
        try
        {
            logger.info("Click Invite People button");
            WebElement newUserButton = findAndWait(INVITE_BUTTON);
            newUserButton.click();
            return factoryPage.instantiatePage(driver,InviteToAlfrescoPage.class);
        }
        catch (NoSuchElementException te)
        {
            throw new PageException("Not able to find the Invite People Button.");
        }
    }

}
