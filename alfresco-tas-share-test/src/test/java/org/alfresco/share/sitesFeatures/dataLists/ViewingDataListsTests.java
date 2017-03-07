package org.alfresco.share.sitesFeatures.dataLists;

import org.alfresco.common.DataUtil;
import org.alfresco.dataprep.DashboardCustomization.Page;
import org.alfresco.dataprep.DataListsService;
import org.alfresco.dataprep.DataListsService.DataList;
import org.alfresco.po.share.site.dataLists.ContactListSelectedContent.ListColumns;
import org.alfresco.po.share.site.dataLists.DataListsPage;
import org.alfresco.share.ContextAwareWebTest;
import org.alfresco.testrail.TestRail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.social.alfresco.api.entities.Site;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

import static org.testng.Assert.assertTrue;

public class ViewingDataListsTests extends ContextAwareWebTest
{
    
    @Autowired
    DataListsPage dataListsPage;
    
    @Autowired
    DataListsService dataLists;

    private String userName;
    private String siteName;
    private List<Page> pagesToAdd = new ArrayList<Page>();
    
    @BeforeMethod
    public void setup()
    {
        super.setup();
        pagesToAdd.add(Page.DATALISTS);
        userName = "User" + DataUtil.getUniqueIdentifier();
        siteName = "SiteName" + DataUtil.getUniqueIdentifier();
        userService.create(adminUser, adminPassword, userName, password, "@tests.com", userName, userName);
        siteService.create(userName, password, domain, siteName, siteName, Site.Visibility.PUBLIC);
        siteService.addPagesToSite(userName, password, siteName, pagesToAdd);
        setupAuthenticatedSession(userName, password);
    }
    
    @TestRail(id = "C5853")
    @Test
    public void filterAreVisibleOnlyIfAListIsSelected()
    {       
        logger.info("Preconditions: Create a new List");
        String listName = "list" + System.currentTimeMillis();
        dataLists.createDataList(adminUser, adminPassword, siteName, DataList.CONTACT_LIST, listName, "contact link description");
        
        dataListsPage.navigate(siteName);
        
        logger.info("Step 1: Check the Items filter");
        Assert.assertFalse(dataListsPage.currentContent.allFilterOptionsAreDisplayed(), "The filter options are displayed.");
        
        logger.info("Step 2: Click the created list displayed under Lists view.");
        dataListsPage.clickContactListItem(listName);
        
        logger.info("Step 3: Check the available filtering options.");
        assertTrue(dataListsPage.currentContent.allFilterOptionsAreDisplayed(), "Not all filters are displayed.");
    }
    
    //@TestRail(id = "C5854")
    //@Test
    public void selectAListAndApplyAllFilter()
    {       
        // TO_DO
        // Precondition
        // Data list items created eight days ago and six days ago, modified eight days ago and six days ago are added to the DataList by User1. -> can not create data list items any number of days in the past or the future
    }
    
    //@TestRail(id = "C5855")
    //@Test
    public void selectAListAndApplyRecentlyAddedFilter()
    {       
        // TO_DO
        // Precondition
        // Data list items created eight days ago and six days ago, modified eight days ago and six days ago are added to the DataList by User1. -> can not create data list items any number of days in the past or the future
    }
    
    //@TestRail(id = "C5856")
    //@Test
    public void selectAListAndApplyRecentlyModifiedFilter()
    {       
        // TO_DO
        // Precondition
        // Data list items created eight days ago and six days ago, modified eight days ago and six days ago are added to the DataList by User1. -> can not create data list items any number of days in the past or the future
    }
    
    //@TestRail(id = "C5857")
    //@Test
    public void selectAListAndApplyCreatedByMeFilter()
    {       
        // TO_DO
        // Precondition
        // Data list items created eight days ago and six days ago, modified eight days ago and six days ago are added to the DataList by User1. -> can not create data list items any number of days in the past or the future
    }
    
    @TestRail(id = "C5858")
    @Test
    public void multiplePagesViewedInDataListsExplorerPanel()
    {       
        logger.info("Preconditions: Create a new List and add new items to it");
        String listName = "list" + System.currentTimeMillis();
        dataLists.createDataList(adminUser, adminPassword, siteName, DataList.CONTACT_LIST, listName, "contact link description");
        
        for(int i=0; i<51; i++)
        {
            dataLists.addContactListItem(userName, password, siteName, listName, userName+i, userName+i, userName+i + "@tests.com", "companyName", "jobTitle", "phoneOffice", "phoneMobile", "notes");
        }
        
        dataListsPage.navigate(siteName);
        
        logger.info("Step 1: Click the created list displayed under Lists view.");
        dataListsPage.clickContactListItem(listName);
        assertTrue(dataListsPage.currentContent.areNavigationLinksDisplayed(), "The navigation links are not displayed.");
        Assert.assertEquals(dataListsPage.currentContent.getCurrentPageNumber(), "1", "The current page is not the expected one.");
        
        logger.info("Step 2: Click on the 'next' navigation item.");
        dataListsPage.currentContent.clickNextNavigationItem();
        Assert.assertEquals(dataListsPage.currentContent.getCurrentPageNumber(), "2", "The current page is not the expected one.");
        
        logger.info("Step 3: Click on the 'previous' navigation item.");
        dataListsPage.currentContent.clickPreviousNavigationItem();
        Assert.assertEquals(dataListsPage.currentContent.getCurrentPageNumber(), "1", "The current page is not the expected one.");
        
        logger.info("Step 3: Click on page number '2'.");
        dataListsPage.currentContent.clickOnSpecificPage("2");
        Assert.assertEquals(dataListsPage.currentContent.getCurrentPageNumber(), "2", "The current page is not the expected one.");
    }
    
    @TestRail(id = "C5859")
    @Test
    public void dataListItemsSortedByColumn()
    {       
        logger.info("Preconditions: Create a new List and add two items to it, descendent ordered by Company Name");
        String listName = "list" + System.currentTimeMillis();
        dataLists.createDataList(adminUser, adminPassword, siteName, DataList.CONTACT_LIST, listName, "contact link description");
        
        for(int i=0; i<2; i++)
        {
            dataLists.addContactListItem(userName, password, siteName, listName, userName+i, userName+i, userName+i + "@tests.com", i+"comapanyName", "jobTitle", "phoneOffice", "phoneMobile", "notes");
        }
        
        dataListsPage.navigate(siteName);
        
        logger.info("Step 1: Click the created list displayed under Lists view.");
        dataListsPage.clickContactListItem(listName);
        
        logger.info("Step 2: In the table click 'Company' column headings and check the items are sorted by Company Name.");
        assertTrue(dataListsPage.currentContent.areItemsSortedByColumnAfterClickingTheColumn(ListColumns.Company.toString()), "The data list items are sorted by Company");
    }
    
    @TestRail(id = "C5860")
    @Test
    public void aSinglePageOfListItemsViewed()
    {       
        logger.info("Preconditions: Create a new List and add two items to it, descendent ordered by Company Name");
        String listName = "list" + System.currentTimeMillis();
        dataLists.createDataList(adminUser, adminPassword, siteName, DataList.CONTACT_LIST, listName, "contact link description");
        
        for(int i=0; i<2; i++)
        {
            dataLists.addContactListItem(userName, password, siteName, listName, userName+i, userName+i, userName+i + "@tests.com", i+"comapanyName", "jobTitle", "phoneOffice", "phoneMobile", "notes");
        }
        
        dataListsPage.navigate(siteName);
        
        logger.info("Step 1: Click the created list displayed under Lists view.");
        dataListsPage.clickContactListItem(listName);
        
        logger.info("Step 2: Click the 'next' navigation item.");
        dataListsPage.currentContent.clickNextNavigationItem();
        Assert.assertEquals(dataListsPage.currentContent.getCurrentPageNumber(), "1", "The current page is not the expected one.");
        
        logger.info("Step 3: Click on the 'previous' navigation item.");
        dataListsPage.currentContent.clickPreviousNavigationItem();
        Assert.assertEquals(dataListsPage.currentContent.getCurrentPageNumber(), "1", "The current page is not the expected one.");
    }
}