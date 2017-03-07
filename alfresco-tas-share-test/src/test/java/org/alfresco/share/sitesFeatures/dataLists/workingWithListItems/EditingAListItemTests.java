package org.alfresco.share.sitesFeatures.dataLists.workingWithListItems;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.alfresco.common.DataUtil;
import org.alfresco.dataprep.DataListsService;
import org.alfresco.dataprep.CMISUtil.Status;
import org.alfresco.dataprep.DashboardCustomization.Page;
import org.alfresco.dataprep.DataListsService.DataList;
import org.alfresco.po.share.site.dataLists.CreateNewItemPopUp.ContactListFields;
import org.alfresco.po.share.site.dataLists.CreateNewItemPopUp.ToDoAgendaFields;
import org.alfresco.po.share.site.dataLists.DataListsPage;
import org.alfresco.po.share.site.dataLists.EditItemPopUp;
import org.alfresco.share.ContextAwareWebTest;
import org.alfresco.testrail.TestRail;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.social.alfresco.api.entities.Site;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import junit.framework.Assert;

public class EditingAListItemTests extends ContextAwareWebTest
{

    @Autowired
    DataListsService dataLists;
    
    @Autowired
    DataListsPage dataListsPage;
    
    @Autowired
    protected EditItemPopUp editItemPopUp;
    
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
    
    @TestRail(id = "C6391")
    @Test
    public void editingAMandatoryFieldOfAListItem()
    {      
        logger.info("Preconditions: Create a second user"); 
        String userTest = "User" + DataUtil.getUniqueIdentifier();
        userService.create(adminUser, adminPassword, userTest, password, "@tests.com", userTest, userTest);
        
        logger.info("Preconditions: Create a 'test.xlsx' file");
        String folderName = "testFolder";
        String testDataFolder = srcRoot + "testdata" + File.separator + "testDataC6381" + File.separator;
        contentService.createFolder(userName, password, folderName, siteName);
        contentService.uploadFilesInFolder(testDataFolder, userName, password, siteName, folderName);
        
        logger.info("Preconditions: Create a new 'To Do' List with an item");
        String toDoListName = "toDo" + System.currentTimeMillis();
        dataLists.createDataList(adminUser, adminPassword, siteName, DataList.TODO_LIST, toDoListName, "To Do list description");
        DateTime currentDate = new DateTime();
        dataLists.addToDoItem(adminUser, adminPassword, siteName, toDoListName, "itemTitle", currentDate.toDate(), 1, Status.IN_PROGRESS, "notes", userTest, Arrays.asList("test.xlsx"));

        dataListsPage.navigate(siteName);
        dataListsPage.clickToDoListItem(toDoListName);
        
        logger.info("Step 1: Click the 'Edit' button for the to do list item to be edited.");
        dataListsPage.currentContent.editItem(Arrays.asList("itemTitle", "1",  "In Progress", userTest+" "+userTest, "test.xlsx"));
        
        logger.info("Step 2: Edit the title and click on 'Save' button.");
        editItemPopUp.editContent(ToDoAgendaFields.Title.toString(), "new Title");
        editItemPopUp.clickSave();
        Assert.assertEquals("The pop-up message isn't as expected.", "Data Item updated successfully", dataListsPage.currentContent.messageDisplayed());
        
        logger.info("Step 3: Check the new firstName for the To do list item.");
        Assert.assertEquals("The data list was not updated.", dataListsPage.currentContent.isListItemDisplayed(Arrays.asList("new Title", currentDate.toString("EEE dd MMM yyyy HH:mm")+":00", "1",  "In Progress", userTest+" "+userTest, "test.xlsx")), true);

    }
    
    @TestRail(id = "C6392")
    @Test
    public void editingANonMandatoryFieldOfAListItem()
    {      
       
        logger.info("Preconditions: Create a new 'Contact' List with an item");
        String contactListName = "contact" + System.currentTimeMillis();
        dataLists.createDataList(adminUser, adminPassword, siteName, DataList.CONTACT_LIST, contactListName, "Contact list description");
        dataLists.addContactListItem(adminUser, adminPassword, siteName, contactListName, "firstName", "lastName", "test@test.com", "companyName", "jobTitle", "123456", "+41256422", "testNotes");

        dataListsPage.navigate(siteName);
        dataListsPage.clickContactListItem(contactListName);
        
        logger.info("Step 1: Click the 'Edit' button for the contact list item to be edited.");
        dataListsPage.currentContent.editItem(Arrays.asList("firstName", "lastName", "test@test.com", "companyName", "jobTitle", "123456", "+41256422", "testNotes"));
        
        logger.info("Step 2: Edit the firstName and click on 'Save' button.");
        editItemPopUp.editContent(ContactListFields.FirstName.toString(), "new Name");
        editItemPopUp.clickSave();
        Assert.assertEquals("The pop-up message isn't as expected.", "Data Item updated successfully", dataListsPage.currentContent.messageDisplayed());
        
        logger.info("Step 3: Check the new firstName for the Contact list item.");
        Assert.assertEquals("The data list was not updated.", dataListsPage.currentContent.isListItemDisplayed(Arrays.asList("new Name", "lastName", "test@test.com", "companyName", "jobTitle", "123456", "+41256422", "testNotes")), true);

    }
    
}
