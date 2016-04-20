package org.alfresco.po.share.dashlet;

import java.util.List;

import org.alfresco.po.share.enums.Dashlets;
import org.alfresco.po.share.site.CustomiseSiteDashboardPage;
import org.alfresco.test.FailedTestListener;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

/**
 * Integration test Saved search dashlet page elements.
 * 
 * @author Ranjith Manyam
 */
@Listeners(FailedTestListener.class)
@Test(groups = { "Enterprise4.2" })
public class SavedSearchDashletTest extends AbstractSiteDashletTest
{
    private static final String SAVED_SEARCH = "saved-search";

    private SavedSearchDashlet savedSearchDashlet = null;
    private CustomiseSiteDashboardPage customiseSiteDashBoard = null;
    ConfigureSavedSearchDialogBoxPage configureSavedSearchDialogBoxPage = null;
    private static final String expectedHelpBallonMsg = "Use this dashlet to set up a search and view the results.\n"
            + "Configure the dashlet to save the search and set the title text of the dashlet.\n"
            + "Only a Site Manager can configure the search and title - this dashlet is ideal for generating report views in a site.";
    private static final String savedSearchTitle = "Test Saved Search";

    @BeforeTest
    public void prepare() throws Exception
    {
        siteName = "savedsearchdashlettest" + System.currentTimeMillis();
    }

    @BeforeClass
    public void loadFile() throws Exception
    {
        uploadDocument();
        navigateToSiteDashboard();
    }

    @Test
    public void instantiateDashlet()
    {
        customiseSiteDashBoard = siteDashBoard.getSiteNav().selectCustomizeDashboard().render();;
        customiseSiteDashBoard.render();
        siteDashBoard = customiseSiteDashBoard.addDashlet(Dashlets.SAVED_SEARCH, 1).render();
        savedSearchDashlet = siteDashBoard.getDashlet(SAVED_SEARCH).render();
        Assert.assertNotNull(savedSearchDashlet);
    }

    @Test(dependsOnMethods = "instantiateDashlet")
    public void verifyHelpAndConfigureIcons() throws Exception
    {
        Assert.assertTrue(savedSearchDashlet.isHelpIconDisplayed());
    }

    @Test(dependsOnMethods = "verifyHelpAndConfigureIcons")
    public void selectHelpIcon() throws Exception
    {
        savedSearchDashlet.clickOnHelpIcon();
        Assert.assertTrue(savedSearchDashlet.isBalloonDisplayed());

        String actualHelpBallonMsg = savedSearchDashlet.getHelpBalloonMessage();
        Assert.assertEquals(actualHelpBallonMsg, expectedHelpBallonMsg);
        savedSearchDashlet.closeHelpBallon().render();
        Assert.assertFalse(savedSearchDashlet.isBalloonDisplayed());
    }

    @Test(dependsOnMethods = "selectHelpIcon")
    public void getTitle()
    {
        Assert.assertEquals(savedSearchDashlet.getTitle(), "Saved Search");
    }

    @Test(dependsOnMethods = "getTitle")
    public void getContent()
    {
        Assert.assertEquals(savedSearchDashlet.getContent(), "No results found.");
    }

    @Test(dependsOnMethods = "getContent", expectedExceptions=IllegalArgumentException.class)
    public void getSearchItemsWithEmptyResult()
    {
        Assert.assertTrue(savedSearchDashlet.getSearchItems().isEmpty());
        Assert.assertFalse(savedSearchDashlet.isItemFound(siteName));
        Assert.assertFalse(savedSearchDashlet.isItemFound(null));
    }

    @Test(dependsOnMethods = "getSearchItemsWithEmptyResult")
    public void clickOnEditButton()
    {
        configureSavedSearchDialogBoxPage = savedSearchDashlet.clickOnEditButton().render();
        Assert.assertNotNull(configureSavedSearchDialogBoxPage);
    }

    @Test(dependsOnMethods = "clickOnEditButton")
    public void configureSavedSearch()
    {
        configureSavedSearchDialogBoxPage.setSearchTerm(fileName);
        configureSavedSearchDialogBoxPage.setTitle(savedSearchTitle);
        configureSavedSearchDialogBoxPage.setSearchLimit(SearchLimit.HUNDRED);
        siteDashBoard = configureSavedSearchDialogBoxPage.clickOnOKButton().render();
    }

    @Test(dependsOnMethods = "configureSavedSearch", groups="TestBug")
    public void verifySavedSearchResult()
    {
        Assert.assertNotNull(siteDashBoard);
        savedSearchDashlet = siteDashBoard.getDashlet(SAVED_SEARCH).render();
        Assert.assertNotNull(savedSearchDashlet);

        Assert.assertEquals(savedSearchDashlet.getTitle(), savedSearchTitle);

        List<SiteSearchItem> searchResults = savedSearchDashlet.getSearchItems();
        Assert.assertEquals(searchResults.size(), 1);
        Assert.assertEquals(searchResults.get(0).getItemName().getDescription(), fileName);
        Assert.assertNotNull(searchResults.get(0).getThumbnail());
        Assert.assertNotNull(searchResults.get(0).getPath());
        Assert.assertTrue(savedSearchDashlet.isItemFound(fileName));
    }
}
