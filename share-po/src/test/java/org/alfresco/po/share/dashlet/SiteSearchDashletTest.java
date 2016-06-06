package org.alfresco.po.share.dashlet;

import java.util.Arrays;
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
 * Integration test site search dashlet page elements.
 * 
 * @author Shan Nagarajan
 */
@Listeners(FailedTestListener.class)
@Test(groups = { "Enterprise4.2"})
public class SiteSearchDashletTest extends AbstractSiteDashletTest
{
    private static final String SITE_SEARCH = "site-search";
    
    private SiteSearchDashlet searchDashlet = null;
    private CustomiseSiteDashboardPage customiseSiteDashBoard = null;
    private static final String expectedHelpBallonMsg = "Use this dashlet to perform a site search and view the results.\nClicking the item name takes you to the details page so you can preview or work with the item.";
    
    @BeforeTest
    public void prepare() throws Exception
    {
        siteName = "sitesearchdashlettest" + System.currentTimeMillis();
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
        siteDashBoard = customiseSiteDashBoard.addDashlet(Dashlets.SITE_SEARCH, 1).render();
        searchDashlet = siteDashBoard.getDashlet(SITE_SEARCH).render();
        Assert.assertNotNull(searchDashlet);
    }

    @Test(dependsOnMethods="instantiateDashlet")
    public void getAvailableResultSizes()
    {
        Assert.assertEquals(searchDashlet.getAvailableResultSizes().size(), 4);
        Assert.assertEquals(searchDashlet.getAvailableResultSizes(), Arrays.asList("10", "25", "50", "100"));
        Assert.assertEquals(searchDashlet.getSelectedSearchLimit(), SearchLimit.TEN);
    }
    
    @Test(dependsOnMethods="getAvailableResultSizes")
    public void search()
    {
        String searchText = "xyz123";
        searchDashlet.search(searchText).render();
        Assert.assertEquals(searchDashlet.getContent(), "No results found.");
        Assert.assertEquals(searchDashlet.getSearchText(), searchText);
    }
    
    @Test(dependsOnMethods="search", groups="TestBug")
    public void getSearchItems()
    {
        searchDashlet.search(fileName).render();
        List<SiteSearchItem> items = searchDashlet.getSearchItems();
        Assert.assertNotNull(items);
        Assert.assertEquals(items.size(), 1);
        Assert.assertEquals(items.get(0).getItemName().getDescription(), fileName);
        Assert.assertEquals(items.get(0).getPath().getDescription(), "/");
        Assert.assertNotNull(items.get(0).getThumbnail());
    }

    @Test(dependsOnMethods="getSearchItems", groups="TestBug")
    public void searchWithSearchLimit()
    {
        searchDashlet.search(fileName, SearchLimit.TWENTY_FIVE).render();
        List<SiteSearchItem> items = searchDashlet.getSearchItems();
        Assert.assertNotNull(items);
        Assert.assertEquals(items.size(), 1);
        Assert.assertEquals(items.get(0).getItemName().getDescription(), fileName);
        Assert.assertEquals(searchDashlet.getSelectedSearchLimit(), SearchLimit.TWENTY_FIVE);
    }

}
