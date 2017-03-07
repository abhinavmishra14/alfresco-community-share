package org.alfresco.share.sitesFeatures.Links;

import java.util.ArrayList;
import java.util.List;

import org.alfresco.common.DataUtil;
import org.alfresco.dataprep.DashboardCustomization;
import org.alfresco.po.share.site.CustomizeSitePage;
import org.alfresco.po.share.site.SiteDashboardPage;
import org.alfresco.po.share.site.link.CreateLinkPage;
import org.alfresco.po.share.site.link.EditLinkPage;
import org.alfresco.po.share.site.link.LinkDetailsViewPage;
import org.alfresco.po.share.site.link.LinkPage;
import org.alfresco.share.ContextAwareWebTest;
import org.alfresco.testrail.TestRail;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.social.alfresco.api.entities.Site.Visibility;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * @author iulia.cojocea
 */
public class EditingLinkTests extends ContextAwareWebTest
{
    @Autowired
    SiteDashboardPage siteDashboardPage;

    @Autowired
    LinkPage linkPage;

    @Autowired
    CustomizeSitePage customizeSitePage;

    @Autowired
    CreateLinkPage createLinkPage;

    @Autowired
    LinkDetailsViewPage linkDetailsViewPage;

    @Autowired
    EditLinkPage editLinkPage;

    private String testUser = "testUser" + DataUtil.getUniqueIdentifier();
    private String siteName = "";
    private DateTime currentDate = new DateTime();
    private String linkTitle = "Google" + DataUtil.getUniqueIdentifier();
    private String linkURL = "https://www.google.ro";
    private String linkDescription = "Link description" + DataUtil.getUniqueIdentifier();
    private List<String> linkTags = new ArrayList<>();

    @BeforeClass
    public void setupTest()
    {
        userService.create(adminUser, adminPassword, testUser, password, testUser + "@tests.com", "firstName", "lastName");
        setupAuthenticatedSession(testUser, password);
    }

    @Test
    @TestRail(id = "C6184")
    public void editLinkFromLinkPageView()
    {
        LOG.info("Precondition: Create site, add 'Links' page to it and create an external link");
        linkTags.add("tag1");
        siteName = "siteName" + DataUtil.getUniqueIdentifier();
        siteService.create(testUser, password, domain, siteName, siteName, Visibility.PUBLIC);
        siteService.addPageToSite(testUser, password, siteName, DashboardCustomization.Page.LINKS, null);
        sitePagesService.createLink(testUser, password, siteName, linkTitle, linkURL, linkDescription, false, linkTags);

        LOG.info("STEP 1: Navigate to 'Links' page for siteName. Click on the link created in precondition");
        linkPage.navigate(siteName);
        Assert.assertTrue(linkPage.getLinksTitlesList().contains(linkTitle), "Link is not displayed in the list!");

        LOG.info("STEP 2: Click on link's URL.");
        // Store the current window handle
        String currentWindow = browser.getWindowHandle();
        linkPage.clickOnLinkURL(linkURL);
        // switchWindow();
        browser.waitInSeconds(5);

        // Switch to new window opened
        for (String winHandle : browser.getWindowHandles())
        {
            browser.switchTo().window(winHandle);
            if (browser.getCurrentUrl().contains("google"))
            {
                break;
            }
            else
            {
                browser.switchTo().window(currentWindow);
            }
        }

        /*
         * Assert.assertTrue(browser.getCurrentUrl().contains(linkURL), "Google page is displayed in a new window");
         * Assert.assertEquals(linkDetailsViewPage.getWinHandlesNo(), 2,
         * "Wrong no of windows handles, expected 2 but found " + linkDetailsViewPage.getWinHandlesNo());
         */

        Assert.assertTrue(browser.getCurrentUrl().contains("google"), "After clicking on the link, the title is: " + browser.getCurrentUrl());

        LOG.info("STEP 3: Close the opened window, go back to Link View page and click 'Edit' button.");

        // browser.navigate().back();

        browser.close();
        browser.switchTo().window(currentWindow);

        linkPage.clickOnLinkName(linkTitle);
        linkDetailsViewPage.clickOnEditLink();

        LOG.info("STEP 4: Enter a new title, URL, description, check 'Internal' checkbox and click on 'Update' button");
        String newLinkTitle = "Bing";
        String newLinkURL = "http://www.bing.com/";
        String newLinkDescription = "another search engine";
        editLinkPage.updateLinkTitle(newLinkTitle);
        editLinkPage.updateLinkURL(newLinkURL);
        editLinkPage.updateLinkDescription(newLinkDescription);
        editLinkPage.checkLinkInternal();
        editLinkPage.clickOnUpdateButton();

        Assert.assertTrue(linkDetailsViewPage.getLinkTitle().equals(newLinkTitle), "Wrong link title! expected " + newLinkTitle + "but found "
                + linkDetailsViewPage.getLinkTitle());
        Assert.assertTrue(linkDetailsViewPage.getLinkURL().equals(newLinkURL),
                "Wrong link URL! expected" + newLinkURL + "but found " + linkDetailsViewPage.getLinkURL());
        Assert.assertTrue(linkDetailsViewPage.getCreationDate().contains(currentDate.toString("EEE d MMM yyyy")), "Wrong link creation date!");
        Assert.assertTrue(linkDetailsViewPage.getCreatedBy().equals("firstName" + " " + "lastName"), "Wrong author of the link!");
        Assert.assertTrue(linkDetailsViewPage.getDescription().equals(newLinkDescription), "Wrong link description! expected " + newLinkDescription
                + "but found" + linkDetailsViewPage.getDescription());
        Assert.assertTrue(linkDetailsViewPage.isTagDisplayedInTagsList(linkTags.get(0)), "Tag is not displayed!");
        Assert.assertTrue(linkDetailsViewPage.getNoCommentsMessage().equals("No comments"), "'No comments' message should be displayed!");

        LOG.info("STEP 5: Click on link's URL.");
        // Store the current window handle
        String currentWindow1 = browser.getWindowHandle();
        linkDetailsViewPage.clickOnLinkURL(newLinkURL);
        browser.waitInSeconds(5);

        // Switch to new window opened
        for (String winHandle : browser.getWindowHandles())
        {
            browser.switchTo().window(winHandle);
            if (browser.getCurrentUrl().contains("bing"))
            {
                break;
            }
            else
            {
                browser.switchTo().window(currentWindow1);
            }
        }

        /*
         * Assert.assertTrue(browser.getCurrentUrl().contains(newLinkURL), "Wrong URL!");
         * Assert.assertEquals(linkDetailsViewPage.getWinHandlesNo(), 1,
         * "Wrong no of windows handles, expected 1 but found " + linkDetailsViewPage.getWinHandlesNo());
         */

        Assert.assertTrue(browser.getCurrentUrl().contains("bing"), "After clicking on the link, the title is: " + browser.getCurrentUrl());

    }

    @Test
    @TestRail(id = "C6185")
    public void editLinkFromLinksPage()
    {
        LOG.info("Precondition: Create site, add 'Links' page to it and create an external link");
        linkTags.add("tag1");
        linkTags.add("tag2");
        siteName = "siteName" + DataUtil.getUniqueIdentifier();
        siteService.create(testUser, password, domain, siteName, siteName, Visibility.PUBLIC);
        siteService.addPageToSite(testUser, password, siteName, DashboardCustomization.Page.LINKS, null);
        sitePagesService.createLink(testUser, password, siteName, linkTitle, linkURL, linkDescription, true, linkTags);

        LOG.info("STEP 1: Navigate to 'Links' page for siteName. Click on the link created in precondition");
        linkPage.navigate(siteName);
        Assert.assertTrue(linkPage.getLinksTitlesList().contains(linkTitle), "Link is not displayed in the list!");
        Assert.assertEquals(linkPage.getLinkTags(linkTitle).size(), 2, "Wrong no of tags for link title!");
        Assert.assertTrue(linkPage.getLinkTags(linkTitle).contains("tag1"), "Tag1 is not displayed in the list of tags!");
        Assert.assertTrue(linkPage.getLinkTags(linkTitle).contains("tag2"), "Tag2 is not displayed in the list of tags!");

        LOG.info("STEP 2: Click on link's URL.");
        // Store the current window handle
        String currentWindow = browser.getWindowHandle();
        linkDetailsViewPage.clickOnLinkURL(linkURL);
        browser.waitInSeconds(5);

        /*
         * Assert.assertEquals(linkDetailsViewPage.getWinHandlesNo(), 1,
         * "Wrong no of windows handles, expected 1 but found " + linkDetailsViewPage.getWinHandlesNo());
         */

        // Switch to new window opened
        for (String winHandle : browser.getWindowHandles())
        {
            browser.switchTo().window(winHandle);
            if (browser.getCurrentUrl().contains("google"))
            {
                break;
            }
            else
            {
                browser.switchTo().window(currentWindow);
            }
        }

        /*
         * Assert.assertEquals(linkDetailsViewPage.getWinHandlesNo(), 1,
         * "Wrong no of windows handles, expected 1 but found " + linkDetailsViewPage.getWinHandlesNo());
         */

        Assert.assertTrue(browser.getCurrentUrl().contains("google"), "After clicking on the link, the title is: " + browser.getCurrentUrl());

        LOG.info("STEP 3: Go back in browser and click 'Edit' button for 'Google' link.");

        browser.navigate().back();
        linkPage.renderedPage();
        linkPage.clickEditLink(linkTitle);

        LOG.info("STEP 4: Enter a new title, URL, description, check 'Internal' checkbox and click on 'Update' button");
        String newLinkTitle = "Bing";
        String newLinkURL = "http://www.bing.com/";
        String newLinkDescription = "another search engine";
        editLinkPage.updateLinkTitle(newLinkTitle);
        editLinkPage.updateLinkURL(newLinkURL);
        editLinkPage.updateLinkDescription(newLinkDescription);
        editLinkPage.uncheckLinkInternal();
        editLinkPage.removeTag("tag2");
        editLinkPage.clickOnUpdateButton();

        Assert.assertTrue(linkDetailsViewPage.getLinkTitle().equals(newLinkTitle), "Wrong link title! expected " + newLinkTitle + "but found "
                + linkDetailsViewPage.getLinkTitle());
        Assert.assertTrue(linkDetailsViewPage.getLinkURL().equals(newLinkURL),
                "Wrong link URL! expected" + newLinkURL + "but found " + linkDetailsViewPage.getLinkURL());
        Assert.assertTrue(linkDetailsViewPage.getCreationDate().contains(currentDate.toString("EEE d MMM yyyy")), "Wrong link creation date!");
        Assert.assertTrue(linkDetailsViewPage.getCreatedBy().equals("firstName" + " " + "lastName"), "Wrong author of the link!");
        Assert.assertTrue(linkDetailsViewPage.getDescription().equals(newLinkDescription), "Wrong link description! expected " + newLinkDescription
                + "but found" + linkDetailsViewPage.getDescription());
        Assert.assertTrue(linkDetailsViewPage.isTagDisplayedInTagsList(linkTags.get(0)), "Tag1 is not displayed!");
        Assert.assertFalse(linkDetailsViewPage.isTagDisplayedInTagsList(linkTags.get(1)), "Tag is not displayed!");
        Assert.assertTrue(linkDetailsViewPage.getNoCommentsMessage().equals("No comments"), "'No comments' message should be displayed!");

        LOG.info("STEP 4: Click on 'Links List' link.");
        linkDetailsViewPage.clickOnLinksListLink();
        Assert.assertTrue(linkPage.getLinksTitlesList().contains(newLinkTitle), "Link is not displayed in the list!");
        Assert.assertEquals(linkPage.getLinkTags(newLinkTitle).size(), 1, "Only one tag should be displayed!");
        Assert.assertTrue(linkPage.getLinkTags(newLinkTitle).contains("tag1"), "Tag1 is not displayed in the list of tags!");

        LOG.info("STEP 5: Click on 'Bing' link's URL.");

        // Store the current window handle
        String currentWindow1 = browser.getWindowHandle();

        linkPage.clickOnLinkURL(newLinkURL);
        browser.waitInSeconds(5);

        // Switch to new window opened
        for (String winHandle : browser.getWindowHandles())
        {
            browser.switchTo().window(winHandle);
            if (browser.getCurrentUrl().contains("bing"))
            {
                break;
            }
            else
            {
                browser.switchTo().window(currentWindow1);
            }
        }

        /*
         * Assert.assertTrue(browser.getCurrentUrl().contains(newLinkURL), "Wrong URL displayed!!");
         * Assert.assertEquals(linkDetailsViewPage.getWinHandlesNo(), 2,
         * "Wrong no of windows handles, expected 2 but found " + linkDetailsViewPage.getWinHandlesNo());
         * closeWindowAndSwitchBack();
         */
        Assert.assertTrue(browser.getCurrentUrl().contains("bing"), "After clicking on the link, the title is: " + browser.getCurrentUrl());
    }

    @Test
    @TestRail(id = "C6186")
    public void cancelEditingLink()
    {
        LOG.info("Precondition: Create site, add 'Links' page to it and create an external link");
        linkTags.add("tag1");
        linkTags.add("tag2");
        siteName = "siteName" + DataUtil.getUniqueIdentifier();
        siteService.create(testUser, password, domain, siteName, siteName, Visibility.PUBLIC);
        siteService.addPageToSite(testUser, password, siteName, DashboardCustomization.Page.LINKS, null);
        sitePagesService.createLink(testUser, password, siteName, linkTitle, linkURL, linkDescription, true, linkTags);

        LOG.info("STEP 1: Navigate to 'Links' page for siteName. Click on the link created in precondition");
        linkPage.navigate(siteName);
        Assert.assertTrue(linkPage.getLinksTitlesList().contains(linkTitle), "Link is not displayed in the list!");
        Assert.assertEquals(linkPage.getLinkTags(linkTitle).size(), 2, "Wrong no of tags for link title!");
        Assert.assertTrue(linkPage.getLinkTags(linkTitle).contains("tag1"), "Tag1 is not displayed in the list of tags!");
        Assert.assertTrue(linkPage.getLinkTags(linkTitle).contains("tag2"), "Tag2 is not displayed in the list of tags!");

        LOG.info("STEP 2: Click on 'Edit' button for 'Google' link");
        linkPage.clickEditLink(linkTitle);

        LOG.info("STEP 3: Enter a new title, URL, description, check 'Internal' checkbox and click on 'Update' button");
        String newLinkTitle = "Bing";
        String newLinkURL = "http://www.bing.com/";
        String newLinkDescription = "another search engine";
        editLinkPage.updateLinkTitle(newLinkTitle);
        editLinkPage.updateLinkURL(newLinkURL);
        editLinkPage.updateLinkDescription(newLinkDescription);
        editLinkPage.uncheckLinkInternal();
        editLinkPage.removeTag("tag2");
        editLinkPage.clickOnCancelButton();
        Assert.assertTrue(linkPage.getLinksTitlesList().contains(linkTitle), "Link is not displayed in the list!");
        Assert.assertEquals(linkPage.getLinkTags(linkTitle).size(), 2, "Wrong no of tags for link title!");
        Assert.assertTrue(linkPage.getLinkTags(linkTitle).contains("tag1"), "Tag1 is not displayed in the list of tags!");
        Assert.assertTrue(linkPage.getLinkTags(linkTitle).contains("tag2"), "Tag2 is not displayed in the list of tags!");
    }
}
