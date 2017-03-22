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

public class LibraryViewOptionsLibraryViewsTests extends ContextAwareWebTest
{
    @Autowired private DocumentLibraryPage documentLibraryPage;
    
    @Autowired private TableView tableView;


    private final String user = "C6909User" + DataUtil.getUniqueIdentifier();
    private final String description = "C6909SiteDescription" + DataUtil.getUniqueIdentifier();
    private final String siteName = "C6909SiteName" + DataUtil.getUniqueIdentifier();
    private final String docName = "testFile1";
    private final String docContent = "C6909 content";
    private final String folderName = "C6909 test folder";
    private final String docName1 = "testFile1";
    private final String docName2 = "testFile2";
    @BeforeClass(alwaysRun = true)

    public void setupTest()
    {
        userService.create(adminUser, adminPassword, user, password, user + "@tests.com", user, user);
        siteService.create(user, password, domain, siteName, description, Visibility.PUBLIC);
        contentService.createFolder(user, password, folderName, siteName);
        contentService.createDocument(user, password, siteName, DocumentType.TEXT_PLAIN, docName, docContent);
        contentService.createDocumentInFolder(user, password, siteName, folderName, DocumentType.TEXT_PLAIN, docName1, "Document content");
        contentService.createDocumentInFolder(user, password, siteName, folderName, DocumentType.TEXT_PLAIN, docName2, "Document content");
        setupAuthenticatedSession(user, password);
    }

    @TestRail(id = "C6909")
    @Test(groups = { TestGroup.SANITY, TestGroup.CONTENT})

    public void verifyDocumentLibraryViewOptions()
    {
        documentLibraryPage.navigate(siteName);

        LOG.info("Step 1: Check the View options available");
        Assert.assertTrue(documentLibraryPage.isOptionsMenuDisplayed(), "Options menu is not displayed");
        documentLibraryPage.clickOptionsButton();
        Assert.assertTrue(documentLibraryPage.isviewOptionDisplayed("Simple View"), "Simple View option is not available under options");
        Assert.assertTrue(documentLibraryPage.isviewOptionDisplayed("Detailed View"), "Detailed View option is not available under options");
        Assert.assertTrue(documentLibraryPage.isviewOptionDisplayed("Gallery View"), "Gallery View option is not available under options");
        Assert.assertTrue(documentLibraryPage.isviewOptionDisplayed("Filmstrip View"), "Filmstrip View option is not available under options");
        Assert.assertTrue(documentLibraryPage.isviewOptionDisplayed("Table View"), "Table View option is not available under options");
        Assert.assertTrue(documentLibraryPage.isviewOptionDisplayed("Audio View"), "Audio View option is not available under options");
        Assert.assertTrue(documentLibraryPage.isviewOptionDisplayed("Media View"), "Media View option is not available under options");

    }
    
    @TestRail(id ="C6955")
    @Test(groups = { TestGroup.SANITY, TestGroup.CONTENT})
    
    public void checkThatDefaultViewIsDetailedView()
    {
        String expectedText = "Set \"Detailed View\" as default for this folder";
        String hideFolders = "Hide Folders";
        documentLibraryPage.navigate(siteName);
        LOG.info("Step 1: Verify current view");
        Assert.assertTrue(documentLibraryPage.isFavoriteLinkPresent(docName), "Favorite link is not present, file is not displayed in Detailed View");
        Assert.assertTrue(documentLibraryPage.isLikeButtonDisplayed(docName), "Like button is not displayed, file is not displayed in Detailed View");
        Assert.assertTrue(documentLibraryPage.isCommentButtonDisplayed(docName), "Comment button is not displayed, file is not displayed in Detailed View");
        Assert.assertTrue(documentLibraryPage.isShareButtonDisplayed(docName), "Share button is not displayed, file is not displayed in Detailed View");
        
        LOG.info("Step 2: Click Options menu and check the text displayed for set as default option");
        documentLibraryPage.clickOptionsButton();
        Assert.assertTrue(documentLibraryPage.getAllOptionsText().contains(hideFolders), "The Hide Folders is not displayed");
        Assert.assertTrue(documentLibraryPage.getAllOptionsText().contains(expectedText), "The Set detailed view for this folder message is not displayed");
        Assert.assertEquals(documentLibraryPage.getOptionsSetDefaultViewText(expectedText), expectedText, "The Set detailed view for this folder message is not displayed");
    }
    
    @TestRail(id ="C6953")
    @Test(groups = { TestGroup.SANITY, TestGroup.CONTENT})
    
    public void setCurrentViewAsDefault()
    {
        String expectedText = "Set \"Table View\" as default for this folder";
        String folderNameC6953 = "c6953folder";
        String docNameC6953 = "C6953 test file";
        contentService.createFolder(user, password, folderNameC6953, siteName);
        contentService.createDocumentInFolder(user, password, siteName, folderNameC6953, DocumentType.TEXT_PLAIN, docNameC6953, "Document content");
        
        documentLibraryPage.navigate(siteName);
        LOG.info("Step 1: Verify current view is detailed view");
        Assert.assertTrue(documentLibraryPage.isFavoriteLinkPresent(docName), "Favorite link is not present, file is not displayed in Detailed View");
        documentLibraryPage.clickOptionsButton();
        documentLibraryPage.clickSetDefaultView();
        documentLibraryPage.clickOptionsButton();
        getBrowser().waitUntilElementVisible(tableView.removeDefaultView);
        Assert.assertEquals(documentLibraryPage.getRemoveDefaultViewText(), "Remove \"Detailed View\" as default for this folder");
        documentLibraryPage.clickFolderFromExplorerPanel(folderName);
        
        LOG.info("Step 2: Select Table view ");
        documentLibraryPage.clickOptionsButton();
        documentLibraryPage.clickOptionsButton();
        documentLibraryPage.selectViewFromOptionsMenu("Table View");
        getBrowser().waitUntilElementVisible(tableView.tableView);
        Assert.assertTrue(tableView.isTableViewDisplayed(), "Table view is not displayed");
        documentLibraryPage.clickOptionsButton();
        getBrowser().waitUntilElementVisible(tableView.setDefaultView);
        
        LOG.info("Step 3: Select Set Table view as default for this folder");
        Assert.assertEquals(documentLibraryPage.getOptionsSetDefaultViewText(expectedText), expectedText, expectedText+" is not displayed");
        documentLibraryPage.clickSetDefaultView();
        documentLibraryPage.clickOptionsButton();
        getBrowser().waitUntilElementVisible(tableView.removeDefaultView);
        Assert.assertEquals(documentLibraryPage.getRemoveDefaultViewText(), "Remove \"Table View\" as default for this folder");
        
        LOG.info("Step 4: Navigate to another folder");
        documentLibraryPage.navigate(siteName);
        Assert.assertTrue(documentLibraryPage.isFavoriteLinkPresent(docName), "Favorite link is not present, file is not displayed in Detailed View");
        
    }
}
