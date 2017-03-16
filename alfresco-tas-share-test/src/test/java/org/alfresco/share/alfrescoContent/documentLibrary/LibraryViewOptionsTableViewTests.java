package org.alfresco.share.alfrescoContent.documentLibrary;

import org.alfresco.common.DataUtil;
import org.alfresco.dataprep.CMISUtil.DocumentType;
import org.alfresco.po.share.alfrescoContent.pageCommon.TableView;
import org.alfresco.po.share.site.DocumentLibraryPage;
import org.alfresco.share.ContextAwareWebTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.model.TestGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.social.alfresco.api.entities.Site.Visibility;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class LibraryViewOptionsTableViewTests extends ContextAwareWebTest
{
    @Autowired private DocumentLibraryPage documentLibraryPage;

    @Autowired private TableView tableView;

    private final String user = "C2266User" + DataUtil.getUniqueIdentifier();
    private final String description = "C2266SiteDescription" + DataUtil.getUniqueIdentifier();
    private final String siteName = "C2266SiteName" + DataUtil.getUniqueIdentifier();
    private final String docName = "testFile1";
    private final String docContent = "C2266 content";
    private final String folderName = "C2266 test folder";
    private final String docName1 = "testFile1";
    @BeforeClass

    public void setupTest()
    {
        userService.create(adminUser, adminPassword, user, password, user + "@tests.com", user, user);
        siteService.create(user, password, domain, siteName, description, Visibility.PUBLIC);
        contentService.createFolder(user, password, folderName, siteName);
        contentService.createDocument(user, password, siteName, DocumentType.TEXT_PLAIN, docName, docContent);
        contentService.createDocumentInFolder(user, password, siteName, folderName, DocumentType.TEXT_PLAIN, docName1, "Document content");
        setupAuthenticatedSession(user, password);
    }

    @TestRail(id="C2266")
    @Test(groups = { TestGroup.SANITY, TestGroup.ALFRESCO_CONTENT})
    
    public void tableViewOption()
    {
        documentLibraryPage.navigate(siteName);
        
        LOG.info("Step 1: Expand the Options menu and check that the Table View is present");
        
        Assert.assertTrue(documentLibraryPage.isOptionsMenuDisplayed(), "Options menu is not displayed");
        documentLibraryPage.clickOptionsButton();
        Assert.assertTrue(documentLibraryPage.isviewOptionDisplayed("Table View"), "Table view is not displayed");
    }
    
    @TestRail(id ="C2267")
    @Test(groups = { TestGroup.SANITY, TestGroup.ALFRESCO_CONTENT})
    
    public void tableViewDisplayingItems()
    {
        documentLibraryPage.navigate(siteName);
        
        LOG.info("Step 1: Check the table view action presence in the Options menu.");
  
        Assert.assertTrue(documentLibraryPage.isOptionsMenuDisplayed(), "Options menu is not displayed");
        documentLibraryPage.clickOptionsButton();
        Assert.assertTrue(documentLibraryPage.isviewOptionDisplayed("Table View"), "Table view is not displayed");
        
        LOG.info("Step 2: Click on Table view action.");
        documentLibraryPage.clickOptionsButton();
        documentLibraryPage.selectViewFromOptionsMenu("Table View");
        getBrowser().waitInSeconds(2);
        Assert.assertTrue(tableView.isTableViewDisplayed());
        Assert.assertEquals(tableView.getContentNameTableView(docName), docName, "testFile1 is not displayed in table view");
        Assert.assertEquals(tableView.getContentNameTableView(folderName), folderName, "C2266 test folder is not displayed in table view");    
        Assert.assertTrue(tableView.isSelectedColumnDisplayed(), "Selected column is not displayed in table view");
        Assert.assertTrue(tableView.isStatusColumnDisplayed(), "Status column is not displayed in table view");
        Assert.assertTrue(tableView.isThumbnailColumnDisplayed(), "Thumbnail column is not displayed in table view");
        Assert.assertTrue(tableView.isTitleColumnDisplayed(), "Title column is not displayed in table view");
        Assert.assertTrue(tableView.isDescriptionColumnDisplayed(), "Description column is not displayed in table view");
        Assert.assertTrue(tableView.isCreatorColumnDisplayed(), "Creator column is not displayed in table view");
        Assert.assertTrue(tableView.isCreatedColumnDisplayed(), "Created column is not displayed in table view");
        Assert.assertTrue(tableView.isModifierColumnDisplayed(), "Modifier column is not displayed in table view");
        Assert.assertTrue(tableView.isModifiedColumnDisplayed(), "Modified column is not displayed in table view");
        Assert.assertTrue(tableView.isActionsColumnDisplayed(), "Actions column is not displayed in table view");
    }
}
