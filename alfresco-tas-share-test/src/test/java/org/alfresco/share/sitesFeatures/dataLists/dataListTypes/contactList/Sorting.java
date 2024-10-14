package org.alfresco.share.sitesFeatures.dataLists.dataListTypes.contactList;

import lombok.extern.slf4j.Slf4j;
import org.alfresco.dataprep.DashboardCustomization;
import org.alfresco.dataprep.DataListsService;
import org.alfresco.dataprep.SiteService;
import org.alfresco.po.share.site.dataLists.DataListsPage;
import org.alfresco.share.BaseTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.model.SiteModel;
import org.alfresco.utility.model.TestGroup;
import org.alfresco.utility.model.UserModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.alfresco.po.share.site.dataLists.ContactListSelectedContent.ContactListColumns;

import static org.testng.Assert.assertTrue;
@Slf4j

public class Sorting extends BaseTest {

    DataListsPage dataListsPage;
    @Autowired
    SiteService siteService;
    @Autowired
    DataListsService dataListsService;
    private final ThreadLocal<UserModel> userName = new ThreadLocal<>();
    private final ThreadLocal<SiteModel> siteName = new ThreadLocal<>();
    String domain = "@test.com";

    @BeforeMethod(alwaysRun = true)
    public void precondition()
    {
        log.info("Precondition: Any Test user is created");
        userName.set(getDataUser().usingAdmin().createRandomTestUser());
        getCmisApi().authenticateUser(getAdminUser());

        log.info("PreCondition: Site siteName is created");
        siteName.set(getDataSite().usingUser(userName.get()).createPublicRandomSite());
        getCmisApi().authenticateUser(userName.get());

        siteService.addPageToSite(userName.get().getUsername(), userName.get().getPassword(), siteName.get().getId(), DashboardCustomization.Page.DATALISTS, null);

        dataListsPage = new DataListsPage(webDriver);


        authenticateUsingCookies(userName.get());
    }

    @AfterMethod(alwaysRun = true)
    public void cleanupMethod()
    {
        contentService.deleteTreeByPath(getAdminUser().getUsername(), getAdminUser().getPassword(), "/User Homes/" + userName.get().getUsername());
        deleteSitesIfNotNull(siteName.get());
        deleteUsersIfNotNull(userName.get());
    }

    @TestRail(id = "C6552")
    @Test(groups = { TestGroup.SANITY, TestGroup.SITES_FEATURES })
    public void sortDataListItemsByColumn()
    {
        log.info("Preconditions: Create a new List and add two items to it, descendent ordered by Company Name");
        String listName = "list" + System.currentTimeMillis();
        dataListsService.createDataList(getAdminUser().getUsername(), getAdminUser().getPassword(), siteName.get().getId(), DataListsService.DataList.CONTACT_LIST, listName, "contact link description");

        for (int i = 0; i < 2; i++)
        {
            dataListsService.addContactListItem(userName.get().getUsername(), userName.get().getPassword(), siteName.get().getId(), listName, userName.get().getFirstName() + i, userName.get().getLastName() + i, userName.get().getUsername() + i + domain, i + "comapanyName", "jobTitle", "phoneOffice", "phoneMobile", "notes");
        }

        dataListsPage.navigate(siteName.get().getId());

        log.info("Step 1: Click the created list displayed under Lists view.");
        dataListsPage.clickContactListItem(listName);

        log.info("Step 2: In the table click column headings and check the items are sorted by Name.");
        assertTrue(dataListsPage.areItemsSortedByColumnAfterClickingTheColumn(ContactListColumns.FirstName.toString()), "The data list items are sorted by First Name");
        assertTrue(dataListsPage.areItemsSortedByColumnAfterClickingTheColumn(ContactListColumns.LastName.toString()), "The data list items are sorted by Last Name");
        assertTrue(dataListsPage.areItemsSortedByColumnAfterClickingTheColumn(ContactListColumns.Email.toString()), "The data list items are sorted by Email");
        assertTrue(dataListsPage.areItemsSortedByColumnAfterClickingTheColumn(ContactListColumns.Company.toString()), "The data list items are sorted by Company");
    }
}
