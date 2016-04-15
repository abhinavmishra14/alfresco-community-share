package org.alfresco.po.share;

import org.testng.annotations.Test;
import org.testng.annotations.BeforeClass;
import org.testng.Assert;
import org.testng.Assert;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import org.alfresco.po.AbstractTest;
import org.alfresco.po.exception.PageOperationException;
import org.alfresco.po.share.admin.AdminConsolePage;
import org.alfresco.po.share.admin.ManageSitesPage;
import org.alfresco.po.share.search.AdvanceSearchPage;
import org.alfresco.po.share.site.CreateSitePage;
import org.alfresco.po.share.site.SiteDashboardPage;
import org.alfresco.po.share.user.MyProfilePage;
import org.alfresco.po.share.user.UserSitesPage;
import org.alfresco.test.FailedTestListener;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

/**
 * Alfresco Share navigation bar integration test.
 * 
 * @author Michael Suzuki
 * @since 1.0
 */
@Listeners(FailedTestListener.class)
@SuppressWarnings("unused")
public class NavigationBarTest extends AbstractTest
{
    private SharePage page;
    private String siteName;

    private static final String pentahoBusinessAnalystGroup = "ANALYTICS_BUSINESS_ANALYSTS";
    private String businessAnalystsUserName = "BusinessAnalystUser_" + System.currentTimeMillis();
    
    
    @BeforeClass(groups={"alfresco-one"}, alwaysRun=true)
    public void setup() throws Exception
    {
        siteName = String.format("test-%d-site-crud",System.currentTimeMillis());
        page = loginAs(username, password);
    }
    
//    /**
//     * Selects My Sites from Sites menu and checks that User Sites List is Displayed
//     * @throws Exception
//     */
//    @Test(groups={"alfresco-one"}, priority=1)
//    public void navigateToMySites() throws Exception
//    {
//        UserSitesPage userSitesPage = page.getNav().selectMySites().render();
//        assertEquals(userSitesPage.getPageTitle(), "User Sites List");
//    }  
//    
//   
//    /**
//     * Navigate to people finder from the dashboard page
//     * and back to dash board page by selecting the 
//     * navigation icons.
//     * @throws Exception if error
//     */
//    @Test(dependsOnMethods = "navigateToMySites",groups={"alfresco-one"}, priority=2)
//    public void navigateToPeopleFinder() throws Exception
//    {
//        PeopleFinderPage peoplePage = page.getNav().selectPeople().render();
//        assertEquals(peoplePage.getPageTitle(), "People Finder");
//    }
//    
    /**
     * Selects My Sites from Sites menu and checks that User Sites List is Displayed
     * @throws Exception
     */
    @Test(groups={"alfresco-one"})
    public void navigateToMySites() throws Exception
    {
        UserSitesPage userSitesPage = page.getNav().selectMySites().render();
        assertEquals(userSitesPage.getPageTitle(), "User Sites List");
    }  
    
   
    /**
     * Navigate to people finder from the dashboard page
     * and back to dash board page by selecting the 
     * navigation icons.
     * @throws Exception if error
     */
    @Test(dependsOnMethods = "navigateToMySites",groups={"alfresco-one"})
    public void navigateToPeopleFinder() throws Exception
    {
        PeopleFinderPage peoplePage = page.getNav().selectPeople().render();
        assertEquals(peoplePage.getPageTitle(), "People Finder");
    }
    
    /**
     * Test navigating to site finder page.
     * @throws Exception if error
     */
    @Test(dependsOnMethods = "navigateToPeopleFinder",groups={"alfresco-one"})
    public void navigateToSearchForSites() throws Exception
    {
        page = page.getNav().selectSearchForSites().render();
        Assert.assertEquals(page.getPageTitle(), "Site Finder");
    }
    
    /**
     * Test navigating to create site page.
     * @throws Exception if error
     */
    @Test(dependsOnMethods = "navigateToSearchForSites",groups={"alfresco-one"})
    public void navigateToCreateSite() throws Exception
    {
        Assert.assertTrue(page.getNav().isCreateSitePresent());
        CreateSitePage createSitePage = page.getNav().selectCreateSite().render();
        Assert.assertTrue(createSitePage.isCreateSiteDialogDisplayed());
        createSitePage.cancel();
    }
    
    /**
     * Test navigating to my profile page.
     * @throws Exception if error
     */
    @Test(dependsOnMethods= "navigateToCreateSite" ,groups={"alfresco-one"})
    public void navigateToMyProfile() throws Exception
    {
        MyProfilePage myProfilePage = page.getNav().selectMyProfile().render();
        Assert.assertTrue(myProfilePage.titlePresent());
    }
    
    /**
     * Test navigating to change password page.
     * @throws Exception if error
     */
    @Test(dependsOnMethods= "navigateToMyProfile",groups={"alfresco-one"})
    public void navigateChangePassword() throws Exception
    {
        ChangePasswordPage changePasswordPage = page.getNav().selectChangePassword().render();
        Assert.assertTrue(changePasswordPage.formPresent());
    }
    
     /**
     * Test navigating to change password page.
     * @throws Exception if error
     */
    @Test(dependsOnMethods= "navigateChangePassword",groups={"alfresco-one"})
    public void navigateDashBoard() throws Exception
    {
        DashBoardPage dash = page.getNav().selectMyDashBoard().render();
        Assert.assertTrue(dash.titlePresent());
        String title = dash.getTitle();
        Assert.assertTrue(title.contains("Dashboard"));
    }
    
    /**
     * Test repository link, note that this is for non cloud product.
     * @throws Exception if error
     */
    @Test(dependsOnMethods= "navigateDashBoard",groups = "alfresco-one")
    public void navigateToRepository() throws Exception
    {
        RepositoryPage repoPage = page.getNav().selectRepository().render();
        Assert.assertTrue(repoPage.isBrowserTitle("Repository"));
    }
    
    /**
     * Test advance search link.
     * Note supported in cloud.
     * @throws Exception if error
     */
    @Test(dependsOnMethods= "navigateToRepository",groups= "Enterprise-only")
    public void advanceSearch() throws Exception
    {
        AdvanceSearchPage searchPage = page.getNav().selectAdvanceSearch().render();
        Assert.assertEquals(searchPage.getPageTitle(), "Advanced Search");
    }

    
    /**
     * Navigate to admin tools from the dashboard page.
     * 
     * @throws Exception if error
     */
    @Test(groups = {"Enterprise-only"})
    public void navigateToAdminTools() throws Exception
    {
        AdminConsolePage adminConsolePage = page.getNav().selectAdminTools().render();
        Assert.assertEquals(adminConsolePage.getPageTitle(), "Admin Tools");
    }

    /**
     * Navigate to manage sites from the dashboard page by Repo Admin
     * 
     * @throws Exception if error
     */
    @Test(groups = { "Enterprise-only" })
    public void navigateToManageSites() throws Exception
    {
        ManageSitesPage manageSitesPage = page.getNav().selectManageSitesPage().render();
        Assert.assertEquals(manageSitesPage.getPageTitle(), "Sites Manager");
    }

    /**
     * Navigate to manage sites from the dashboard page by Repo Admin
     * 
     * @throws Exception if error
     */
    @Test (groups = { "Enterprise-only"}, dependsOnGroups ="alfresco-one")
    public void navigateToManageSitesSiteAdmin() throws Exception
    {
        String siteAdmin = "SITE_ADMINISTRATORS";
        UserSearchPage userPage = page.getNav().getUsersPage().render();
        NewUserPage newPage = userPage.selectNewUser().render();
        String userinfo = "user" + System.currentTimeMillis() + "@test.com";
        newPage.createEnterpriseUserWithGroup(userinfo, userinfo, userinfo, userinfo, userinfo, siteAdmin);
        shareUtil.logout(driver);
        shareUtil.loginAs(driver, shareUrl, userinfo, userinfo);
        ManageSitesPage manageSitesPage = page.getNav().selectManageSitesSiteAdmin().render();
        Assert.assertEquals(manageSitesPage.getPageTitle(), "Sites Manager");

    }
    
    @Test(groups= "Enterprise-only", dependsOnMethods ="navigateToManageSitesSiteAdmin")
    public void noRecentSites() throws Exception
    {   
        try
        {
            Assert.assertTrue(page.getNav().getRecentSitesPresent().isEmpty());
        }
        catch (PageOperationException e)
        {
            String patternString = "No Recent Site(s) Available";
            Assert.assertTrue(e.getMessage().startsWith(patternString), "Exception Message should Start with " + patternString);
        }
    }
 }
