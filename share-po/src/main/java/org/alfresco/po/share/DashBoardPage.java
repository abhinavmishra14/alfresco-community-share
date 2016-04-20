package org.alfresco.po.share;

import org.alfresco.po.RenderWebElement;
import org.alfresco.po.share.dashlet.Dashlet;
import org.alfresco.po.share.dashlet.FactoryShareDashlet;
import org.alfresco.po.share.dashlet.MyActivitiesDashlet;
import org.alfresco.po.share.dashlet.MyDocumentsDashlet;
import org.alfresco.po.share.dashlet.MySitesDashlet;
import org.alfresco.po.exception.PageException;
import org.alfresco.po.exception.PageOperationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Dashboard page object, holds all element of the HTML page relating to share's
 * 
 * @author Michael Suzuki
 * @since 1.0
 */
public class DashBoardPage extends SharePage implements Dashboard
{
    @Autowired
    FactoryShareDashlet factoryDashlet;
    private final Log logger = LogFactory.getLog(DashBoardPage.class);
    @RenderWebElement
    MySitesDashlet mySitesDashlet;
    @RenderWebElement
    MyDocumentsDashlet myDocumentsDashlet;
    @RenderWebElement
    MyActivitiesDashlet myActivitiesDashlet;

    // Get Started Panel

    // Get Started Panel Title
    private static final String GET_STARTED_PANEL_TITLE = ".welcome-info";

    // Get Started Panel Icon
    public static final By GET_STARTED_PANEL_ICON = By.cssSelector("");

    // Get Started Panel Text
    public static final By GET_STARTED_PANEL_TEXT = By.cssSelector(".welcome-info-text");

    // Get Started Panel Hide Button
    public static final By HIDE_GET_STARTED_PANEL_BUTTON = By.xpath("//button[text()='Hide']");

    /**
     * Verify if home page banner web element is present
     * 
     * @return true if exists
     */
    public boolean titlePresent()
    {
        try
        {
            return getPageTitle().contains("Dashboard");
        }
        catch (Exception e)
        {
            logger.error(e);
        }
        return false;
    }

    /**
     * Clicks on Hide button on Get Started Panel
     * 
     * @return
     */
    public HideGetStartedPanel clickOnHideGetStartedPanelButton()
    {
        try
        {
            findAndWait(HIDE_GET_STARTED_PANEL_BUTTON, maxPageLoadingTime).click();
            waitUntilAlert();

        }
        catch (TimeoutException toe)
        {
            logger.error(toe);
        }
        return factoryPage.instantiatePage(driver, HideGetStartedPanel.class).render();
    }

    /**
     * Gets dashlets in the dashboard page.
     * 
     * @param name String title of dashlet
     * @return HtmlPage page object
     */
    public Dashlet getDashlet(final String name)
    {
        return factoryDashlet.getPage(driver, name);
    }

    /**
     * Retrns css selector for Get Started Panel title
     * 
     * @return
     */
    public String getGetStartedPanelTitle()
    {
        return GET_STARTED_PANEL_TITLE;
    }

    /**
     * Click the 'View the tutorials' link
     */
    public void clickTutorialsLink()
    {
        try
        {
            findAndWait(By.xpath("//h1[text()='GET STARTED']")).click();
        }
        catch (NoSuchElementException ex)
        {
            logger.error("Unable to find Get Started link.", ex);
            throw new PageException("Unable to find Get Started link");
        }
        catch (TimeoutException e)
        {
            logger.error("Exceeded the time to find Get Started link.", e);
            throw new PageOperationException("Not able to find Get Started link");
        }
    }
}
