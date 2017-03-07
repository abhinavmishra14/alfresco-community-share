package org.alfresco.share.sitesFeatures.Links;

import java.util.ArrayList;
import java.util.List;

import org.alfresco.common.DataUtil;
import org.alfresco.dataprep.DashboardCustomization;
import org.alfresco.po.share.site.CustomizeSitePage;
import org.alfresco.po.share.site.SiteDashboardPage;
import org.alfresco.po.share.site.SitePageType;
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
public class AccessingLinkTests extends ContextAwareWebTest
{
    @Autowired
    SiteDashboardPage siteDashboardPage;

    @Autowired
    LinkPage linkPage;

    @Autowired
    CustomizeSitePage customizeSitePage;

    private String testUser = "testUser" + DataUtil.getUniqueIdentifier();
    private String siteName = "siteName" + DataUtil.getUniqueIdentifier();
    private List<String> tags = new ArrayList<String>();
    private DateTime currentDate = new DateTime();

    @BeforeClass
    public void setupTest()
    {
        userService.create(adminUser, adminPassword, testUser, password, testUser + "@tests.com", "firstName", "lastName");
        siteService.create(testUser, password, domain, siteName, siteName, Visibility.PUBLIC);
        siteService.addPageToSite(testUser, password, siteName, DashboardCustomization.Page.LINKS, null);
        setupAuthenticatedSession(testUser, password);
    }

    @Test
    @TestRail(id = "C6250")
    public void accessingTheSiteLinks()
    {
        String newLinksPageName = "newLinks";
        LOG.info("Step 1 - Open Site1's dashboard and click on 'Links' link.");
        siteDashboardPage.navigate(siteName);
        siteDashboardPage.clickLinkFromHeaderNavigationMenu(SitePageType.LINKS);
        Assert.assertTrue(linkPage.getLinksListTitle().equals("All Links"), "All links should be displayed after clicking on 'Links' link!");

        LOG.info("Step 2 : Open customize site and rename 'Links' to 'newLinks'");
        customizeSitePage.navigate(siteName);
        customizeSitePage.renamePage(SitePageType.LINKS, newLinksPageName);
        Assert.assertEquals(customizeSitePage.getPageDisplayName(SitePageType.LINKS), newLinksPageName, "Wrong page name after it was renamed!");

        LOG.info("Step 3 : Click 'Ok' button");
        customizeSitePage.clickOk();
        Assert.assertEquals(siteDashboardPage.getPageDisplayName(SitePageType.LINKS), newLinksPageName, "Wrong 'Links' page name on site dashboard!");

        LOG.info("Step 4 : Click 'newLinks' link");
        siteDashboardPage.clickLinkFromHeaderNavigationMenu(SitePageType.LINKS);
        Assert.assertTrue(linkPage.getLinksListTitle().equals("All Links"), "All links should be displayed after clicking on 'Links' link!");
    }

    @Test
    @TestRail(id = "C6251")
    public void toggleBetweenSimpleAndDetailedView()
    {
        String linkTitle = "Title link1";
        LOG.info("Precondition - Any link is created e.g.: title Link1 URL link1.com description: link1 description tags: tag1");
        tags.add("tag1");
        sitePagesService.createLink(testUser, password, siteName, linkTitle, "link1.com", "link1 description", true, tags);

        LOG.info("Step 1 - Navigate to 'Links' page for Site1.");
        linkPage.navigate(siteName);
        Assert.assertTrue(linkPage.getLinksTitlesList().contains(linkTitle), "Link title is not in the list!");
        Assert.assertTrue(linkPage.getLinkURL(linkTitle).equals("link1.com"), "Link URL is not correct!");
        Assert.assertTrue(linkPage.getLinkAuthor(linkTitle).equals("firstName " + "lastName"), "Wrong author of the link!");
        Assert.assertTrue(linkPage.getLinkDescription(linkTitle).equals("link1 description"), "Wrong description of the link!");
        Assert.assertTrue(linkPage.getLinkCreationDate(linkTitle).contains(currentDate.toString("EEE d MMM yyyy")), "Wrong link creation date!");
        Assert.assertTrue(linkPage.getLinkTags(linkTitle).contains("tag1"), "Tag not available for the specified link!");

        LOG.info("Step 2 - Click 'Simple View' button.");
        linkPage.changeViewMode();
        Assert.assertTrue(linkPage.getLinksTitlesList().contains(linkTitle), "Link title is not in the list!");
        Assert.assertTrue(linkPage.getLinksURL().contains("link1.com"), "Link URL is not in the list!");

        LOG.info("Step 3 - Click 'Detailed View' button.");
        linkPage.changeViewMode();
        Assert.assertTrue(linkPage.getLinksTitlesList().contains(linkTitle), "Link title is not in the list!");
        Assert.assertTrue(linkPage.getLinkURL(linkTitle).equals("link1.com"), "Link URL is not correct!");
        Assert.assertTrue(linkPage.getLinkAuthor(linkTitle).equals("firstName " + "lastName"), "Wrong author of the link!");
        Assert.assertTrue(linkPage.getLinkDescription(linkTitle).equals("link1 description"), "Wrong description of the link!");
        Assert.assertTrue(linkPage.getLinkCreationDate(linkTitle).contains(currentDate.toString("EEE d MMM yyyy")), "Wrong link creation date!");
        Assert.assertTrue(linkPage.getLinkTags(linkTitle).contains("tag1"), "Tag not available for the specified link!");
    }
}
