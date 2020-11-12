package org.alfresco.share.alfrescoContent.workingWithFilesOutsideTheLibrary.myFiles;

import static org.testng.Assert.assertEquals;

import org.alfresco.dataprep.CMISUtil;
import org.alfresco.po.share.MyFilesPage;
import org.alfresco.po.share.alfrescoContent.buildingContent.CreateContentPage;
import org.alfresco.po.share.alfrescoContent.buildingContent.NewFolderDialog;
import org.alfresco.po.share.alfrescoContent.document.DocumentDetailsPage;
import org.alfresco.po.share.alfrescoContent.document.GoogleDocsCommon;
import org.alfresco.po.share.site.DocumentLibraryPage.CreateMenuOption;
import org.alfresco.share.ContextAwareWebTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.data.RandomData;
import org.alfresco.utility.model.TestGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * @author Razvan.Dorobantu
 */
public class MyFilesCreateContentTests extends ContextAwareWebTest
{
    private final String folderTemplateName = "Software Engineering Project";
    private final String fileTemplateName = String.format("fileTemplate%s", RandomData.getRandomAlphanumeric());
    private final String title = "googleDoc title";
    private final String googleDocName = "googleDoc title.docx";
    private final String googleDocSpreadsheet = "googleDoc title.xlsx";
    private final String googleDocPresentation = "googleDoc title.pptx";
    private final String docContent = "googleDoccontent";
    private final String user = String.format("user%s", RandomData.getRandomAlphanumeric());
   // @Autowired
    private MyFilesPage myFilesPage;
    @Autowired
    private DocumentDetailsPage documentDetailsPage;
    @Autowired
    private CreateContentPage createContent;
    @Autowired
    private NewFolderDialog createFolderFromTemplate;
    @Autowired
    private GoogleDocsCommon googleDocs;

    @BeforeClass (alwaysRun = true)
    public void createPrecondition()
    {
        userService.create(adminUser, adminPassword, user, password, user + domain, user, user);
        contentService.createDocumentInRepository(adminUser, adminPassword, "Data Dictionary/Node Templates", CMISUtil.DocumentType.TEXT_PLAIN, fileTemplateName, "some content");

        setupAuthenticatedSession(user, password);
    }

    @AfterClass (alwaysRun = false)
    public void cleanup()
    {
        userService.delete(adminUser, adminPassword, user);
        contentService.deleteTreeByPath(adminUser, adminPassword, "/User Homes/" + user);
        contentService.deleteContentByPath(adminUser, adminPassword, "Data Dictionary/Node Templates/" + fileTemplateName);
    }

    @TestRail (id = "C7650")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT, "tobefixed" })
    public void myFilesCreatePlainTextFile()
    {
        LOG.info("Precondition: Login as user and navigate to My Files page.");
        myFilesPage.navigate();

        LOG.info("Step 1: Click Create... button");
        myFilesPage.clickCreateButton();
        Assert.assertTrue(myFilesPage.areCreateOptionsAvailable(), "Create menu options are not available");

        LOG.info("Step 2: Click \"Plain Text...\" option.");
        myFilesPage.clickCreateContentOption(CreateMenuOption.PLAIN_TEXT);
        Assert.assertEquals(createContent.getPageTitle(), "Alfresco » Create Content", "Create content page is not opened");

        LOG.info("Step 3: Fill in the name, content, title and description fields");
        createContent.typeName("C7650 test name");
        createContent.typeContent("C7650 test content");
        createContent.typeTitle("C7650 test title");
        createContent.typeDescription("C7650 test description");

        LOG.info("Step 4: Click the Create button");
        createContent.clickCreate();
        assertEquals(documentDetailsPage.getPageTitle(), "Alfresco » Document Details", "File is not previewed in Document Details Page");

        LOG.info("Step 5 : Verify the mimetype for the created file.");
        assertEquals(documentDetailsPage.getPropertyValue("Mimetype:"), "Plain Text", "Mimetype property is not Plain Text");

        LOG.info("Step 6: Verify the document's preview");
        assertEquals(documentDetailsPage.getContentText(), "C7650 test content", "\"C7650 test content \" is not the content displayed in preview");
        assertEquals(documentDetailsPage.getFileName(), "C7650 test name", "\"C7650 test name\" is not the file name for the file in preview");
    }

    @TestRail (id = "C7696")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT })
    public void myFilesCreateHTMLFile()
    {
        LOG.info("Precondition: Login as user and navigate to My Files page.");
        myFilesPage.navigate();

        LOG.info("Step 1: Click Create... button");
        myFilesPage.clickCreateButton();

        LOG.info("Step 2: Click \"HTML...\" option.");
        myFilesPage.clickCreateContentOption(CreateMenuOption.HTML);
        Assert.assertEquals(createContent.getPageTitle(), "Alfresco » Create Content", "Create content page is not opened");

        LOG.info("Step 3: Fill in the name, content, title and description fields");
        createContent.typeName("C7696 test name");
        createContent.sendInputForHTMLContent("C7696 test content");
        createContent.typeTitle("C7696 test title");
        createContent.typeDescription("C7696 test description");

        LOG.info("Step 4: Click the Create button");
        createContent.clickCreate();
        Assert.assertEquals(documentDetailsPage.getPageTitle(), "Alfresco » Document Details", "File is not previewed in Document Details Page");

        LOG.info("Step 5 : Verify the mimetype for the created file.");
        Assert.assertEquals(documentDetailsPage.getPropertyValue("Mimetype:"), "HTML", "Mimetype property is not HTML");

        LOG.info("Step 6: Verify the document's preview");
        Assert.assertEquals(documentDetailsPage.getFileName(), "C7696 test name", "\"C7696 test name\" is not the file name for the file in preview");
    }

    @TestRail (id = "C7697")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT })
    public void myFilesCreateXMLFile()
    {
        LOG.info("Precondition: Login as user and navigate to My Files page.");
        myFilesPage.navigate();
        Assert.assertEquals(myFilesPage.getPageTitle(), "Alfresco » My Files");

        LOG.info("Step 1: Click Create... button");
        myFilesPage.clickCreateButton();

        LOG.info("Step 2: Click \"XML...\" option.");
        myFilesPage.clickCreateContentOption(CreateMenuOption.XML);
        Assert.assertEquals(createContent.getPageTitle(), "Alfresco » Create Content", "Create content page is not opened");

        LOG.info("Step 3: Fill in the name, content, title and description fields");
        createContent.typeName("C7697 test name");
        createContent.typeContent("C7697 test content");
        createContent.typeTitle("C7697 test title");
        createContent.typeDescription("C7697 test description");

        LOG.info("Step 4: Click the Create button");
        createContent.clickCreate();
        Assert.assertEquals(documentDetailsPage.getPageTitle(), "Alfresco » Document Details", "File is not previewed in Document Details Page");

        LOG.info("Step 5 : Verify the mimetype for the created file.");
        Assert.assertEquals(documentDetailsPage.getPropertyValue("Mimetype:"), "XML", "Mimetype property is not Plain Text");

        LOG.info("Step 6: Verify the document's preview");
        Assert.assertEquals(documentDetailsPage.getContentText().trim(), "C7697 test content", "\"C7697 test content \" is not the content displayed in preview");
        Assert.assertEquals(documentDetailsPage.getFileName(), "C7697 test name", "\"C6978 test name\" is not the file name for the file in preview");
    }

    @TestRail (id = "C7653")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT })
    public void myFilesCreateFolderFromTemplate()
    {
        LOG.info("Precondition: Login as user and navigate to My Files page.");
        myFilesPage.navigate();
        Assert.assertEquals(myFilesPage.getPageTitle(), "Alfresco » My Files");

        LOG.info("STEP 1: Click 'Create' then 'Create folder from template'.");
        myFilesPage.clickCreateButton();
        myFilesPage.clickCreateFromTemplateOption(CreateMenuOption.CREATE_FOLDER_FROM_TEMPLATE);
        Assert.assertTrue(myFilesPage.isTemplateDisplayed(folderTemplateName));
        LOG.info("STEP 2: Select the template: 'Software Engineering Project'");
        myFilesPage.clickOnTemplate(folderTemplateName);
        //Assert.assertTrue(createFolderFromTemplate.isCreateFolderFromTemplatePopupDisplayed());
        Assert.assertEquals(createFolderFromTemplate.getNameFieldValue(), folderTemplateName);

        LOG.info("STEP 3: Insert data into input fields and save.");
        createFolderFromTemplate.fillInDetails("Test Folder", "Test Title", "Test Description");
        createFolderFromTemplate.clickSave();
        Assert.assertTrue(myFilesPage.getFoldersList().contains("Test Folder"), "Subfolder not found");
        Assert.assertTrue(myFilesPage.getExplorerPanelDocuments().contains("Test Folder"), "Subfolder not found in Documents explorer panel");
    }

    @TestRail (id = "C12858")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT })
    public void myFilesCreateFileFromTemplate()
    {
        LOG.info("Precondition: Login as user and navigate to My Files page.");
        myFilesPage.navigate();

        LOG.info("STEP 1: Click 'Create' then 'Create file from template'.");
        myFilesPage.clickCreateButton();
        myFilesPage.clickCreateFromTemplateOption(CreateMenuOption.CREATE_DOC_FROM_TEMPLATE);
        Assert.assertTrue(myFilesPage.isTemplateDisplayed(fileTemplateName));

        LOG.info("STEP 2: Select the template: 'Software Engineering Project'");
        myFilesPage.clickOnTemplate(fileTemplateName);
        Assert.assertTrue(myFilesPage.isContentNameDisplayed(fileTemplateName), String.format("Content: %s is not displayed.", fileTemplateName));
    }

    @TestRail (id = "C7693")
    @Test (groups = { TestGroup.SANITY, TestGroup.GOOGLE_DOCS })
    public void myFilesCreateGoogleDocsDocument() throws Exception
    {
        googleDocs.loginToGoogleDocs();
        LOG.info("Precondition: Login as user, authorize google docs and navigate to My Files page.");
        myFilesPage.navigate();
        Assert.assertEquals(myFilesPage.getPageTitle(), "Alfresco » My Files");

        LOG.info("Step 1: Click 'Create' button and select the type 'Google Docs Document'.");
        myFilesPage.clickCreateButton();
        myFilesPage.clickGoogleDocsOption(CreateMenuOption.GOOGLE_DOCS_DOCUMENT);
        Assert.assertTrue(googleDocs.isAuthorizeWithGoogleDocsDisplayed(), "Authorize with Google Docs popup is not displayed");

        LOG.info("Step 2: Click Ok button on the Authorize ");
        googleDocs.clickOkButtonOnTheAuthPopup();

        LOG.info("Step 3: Edit the document in the Google Docs tab.");
        googleDocs.switchToGoogleDocsWindowandAndEditContent(title, docContent);
        Assert.assertTrue(myFilesPage.isContentNameDisplayed("Untitled Document"), "The file created with Google Docs is not present");
        Assert.assertTrue(googleDocs.isLockedDocumentMessageDisplayed(), "Locked label is not displayed");
        Assert.assertTrue(googleDocs.isGoogleDriveIconDisplayed(), "Google Drive icon is not displayed");

        LOG.info("Step 4: Click Check in Google Doc button for the created document and verify it's not locked anymore.");
        googleDocs.checkInGoogleDoc("Untitled Document");
        Assert.assertFalse(myFilesPage.isInfoBannerDisplayed(googleDocName), "Document is unlocked");
        Assert.assertFalse(googleDocs.isGoogleDriveIconDisplayed());
        Assert.assertTrue(myFilesPage.isContentNameDisplayed(googleDocName));
    }

    @TestRail (id = "C7694")
    @Test (groups = { TestGroup.SANITY, TestGroup.GOOGLE_DOCS })
    public void myFilesCreateGoogleDocsSpreadsheet() throws Exception
    {
        googleDocs.loginToGoogleDocs();
        LOG.info("Precondition: Login as user, authorize google docs and navigate to My Files page.");
        myFilesPage.navigate();
        Assert.assertEquals(myFilesPage.getPageTitle(), "Alfresco » My Files");

        LOG.info("Step 1: Click 'Create' button and select the type 'Google Docs Spreadsheet'");
        myFilesPage.clickCreateButton();
        myFilesPage.clickGoogleDocsOption(CreateMenuOption.GOOGLE_DOCS_SPREADSHEET);

        LOG.info("Step 2: Click Ok button on the Authorize ");
        googleDocs.clickOkButtonOnTheAuthPopup();

        LOG.info("Step 3: Edit the document in the Google Docs tab.");
        googleDocs.switchToGoogleSheetsWindowandAndEditContent(title, docContent);
        Assert.assertTrue(myFilesPage.isContentNameDisplayed("Untitled Spreadsheet.xlsx"), "The file created with Google Docs is not present");
        Assert.assertTrue(googleDocs.isLockedDocumentMessageDisplayed(), "Locked label is not displayed");
        Assert.assertTrue(googleDocs.isGoogleDriveIconDisplayed(), "Google Drive icon is not displayed");

        LOG.info("Step 4: Click Check in Google Doc button for the created document and verify it's not locked anymore.");
        googleDocs.checkInGoogleDoc("Untitled Spreadsheet");
        Assert.assertFalse(myFilesPage.isInfoBannerDisplayed(googleDocSpreadsheet), "Document is unlocked");
        Assert.assertFalse(googleDocs.isGoogleDriveIconDisplayed());
        Assert.assertTrue(myFilesPage.isContentNameDisplayed(googleDocSpreadsheet));
    }

    @TestRail (id = "C7695")
    @Test (groups = { TestGroup.SANITY, TestGroup.GOOGLE_DOCS })
    public void myFilesCreateGoogleDocsPresentation() throws Exception
    {
        googleDocs.loginToGoogleDocs();
        LOG.info("Precondition: Login as user, authorize google docs and navigate to My Files page.");
        myFilesPage.navigate();
        Assert.assertEquals(myFilesPage.getPageTitle(), "Alfresco » My Files");

        LOG.info("Step 1: Click 'Create' button and select the type 'Google Docs Presentation'");
        myFilesPage.clickCreateButton();
        myFilesPage.clickGoogleDocsOption(CreateMenuOption.GOOGLE_DOCS_PRESENTATION);

        LOG.info("Step 2: Click Ok button on the Authorize ");
        googleDocs.clickOkButtonOnTheAuthPopup();

        LOG.info("Step 3: Edit the document in the Google Docs tab ");
        googleDocs.switchToGooglePresentationsAndEditContent(title);
        Assert.assertTrue(myFilesPage.isContentNameDisplayed("Untitled Presentation"), "The file created with Google Docs is not present");
        Assert.assertTrue(googleDocs.isLockedDocumentMessageDisplayed(), "Locked label is not displayed");
        Assert.assertTrue(googleDocs.isGoogleDriveIconDisplayed(), "Google Drive icon is not displayed");

        LOG.info("Step 4: Click Check in Google Doc button for the created document and verify it's not locked anymore.");
        googleDocs.checkInGoogleDoc("Untitled Presentation");
        Assert.assertFalse(myFilesPage.isInfoBannerDisplayed(googleDocPresentation), "Document is unlocked");
        Assert.assertEquals(googleDocs.isGoogleDriveIconDisplayed(), false);
        Assert.assertTrue(myFilesPage.isContentNameDisplayed(googleDocPresentation));
    }
}

