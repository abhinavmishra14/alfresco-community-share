package org.alfresco.share.alfrescoContent.buildingContent;

import org.alfresco.common.DataUtil;
import org.alfresco.po.share.alfrescoContent.buildingContent.CreateContent;
import org.alfresco.po.share.alfrescoContent.buildingContent.NewContentDialog;
import org.alfresco.po.share.site.DocumentLibraryPage;
import org.alfresco.share.ContextAwareWebTest;
import org.alfresco.testrail.TestRail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.social.alfresco.api.entities.Site;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

/**
 * @author Laura.Capsa
 */
public class OrganizingFoldersTests extends ContextAwareWebTest
{
    @Autowired
    DocumentLibraryPage documentLibraryPage;

    @Autowired
    NewContentDialog newContentDialog;

    @Autowired
    CreateContent createContent;

    private final String uniqueId = DataUtil.getUniqueIdentifier();
    private final String userName = "User-" + uniqueId;
    private final String description = "Description-" + uniqueId;

    @BeforeClass
    public void setupTest()
    {
        String lastName = "LastName";
        String firstName = "FirstName";

        userService.create(adminUser, adminPassword, userName, password, userName + domain, firstName, lastName);
    }

    @TestRail(id = "C6276")
    @Test
    public void createNewFolderUsingMenu()
    {
        String siteName1 = "Site-C6276-" + uniqueId;
        siteService.create(userName, password, domain, siteName1, description, Site.Visibility.PUBLIC);
        String folderName = "C6276-folderName-" + uniqueId;
        String folderTitle = "C6276-folderTitle" + uniqueId;
        String folderDescription = "C6276-folderDescription" + uniqueId;

        setupAuthenticatedSession(userName, password);
        documentLibraryPage.navigate(siteName1);
        assertEquals(documentLibraryPage.getPageTitle(), "Alfresco » Document Library", "Displayed page=");

        LOG.info("STEP1: Click \"Create\" -> \"Folder\"");
        documentLibraryPage.clickCreateButton();
        documentLibraryPage.clickFolderLink();
        assertTrue(newContentDialog.isNameFieldDisplayed(), "'Name' field displayed.");
        assertTrue(newContentDialog.isMandatoryIndicatorDisplayed(), "'Name' mandatory field.");
        assertTrue(newContentDialog.isTitleFieldDisplayed(), "'Title' field displayed.");
        assertTrue(newContentDialog.isDescriptionFieldDisplayed(), "'Description' field displayed.");
        assertTrue(newContentDialog.isSaveButtonDisplayed(), "'Save' button displayed.");
        assertTrue(newContentDialog.isCancelButtonDisplayed(), "'Cancel' button displayed.");

        LOG.info("STEP2: Fill in 'Name', 'Title' and 'Description' fields. Click 'Save' button");
        newContentDialog.fillInDetails(folderName, folderTitle, folderDescription);
        newContentDialog.clickSaveButton();
        assertTrue(documentLibraryPage.isContentNameDisplayed(folderName), folderName + " displayed in Documents list.");
        getBrowser().waitInSeconds(5);
        ArrayList<String> foldersExpected = new ArrayList<>(Arrays.asList("Documents", folderName));
        assertEquals(documentLibraryPage.getExplorerPanelDocuments(), foldersExpected.toString(), "Document Library explorer panel Library --> Documents: ");

        LOG.info("STEP3: Click on the 'FolderName' link");
        documentLibraryPage.clickOnFolderName(folderName);
        assertTrue(documentLibraryPage.getCurrentUrl().contains(folderName), folderName + " opened.");
        ArrayList<String> breadcrumbExpected = new ArrayList<>(Arrays.asList("Documents", folderName));
        assertEquals(documentLibraryPage.getBreadcrumbList(), breadcrumbExpected.toString(), "Breadcrumb= ");
        assertEquals(documentLibraryPage.getFoldersList().toString(), "[]", "Folders displayed in " + folderName);
        assertEquals(documentLibraryPage.getFilesList().toString(), "[]", "Files displayed in " + folderName);
    }

    @TestRail(id = "C6277")
    @Test
    public void createFolderLink()
    {
        String siteName1 = "Site-C6277-" + uniqueId;
        siteService.create(userName, password, domain, siteName1, description, Site.Visibility.PUBLIC);
        String folderName = "C6277-folderName-" + uniqueId;

        setupAuthenticatedSession(userName, password);
        documentLibraryPage.navigate(siteName1);
        assertEquals(documentLibraryPage.getPageTitle(), "Alfresco » Document Library", "Displayed page=");

        LOG.info("STEP1: Click on the 'Create a folder' link");
        documentLibraryPage.clickCreateButton();
        createContent.clickCreateFolderOption();
        assertTrue(newContentDialog.isNameFieldDisplayed(), "'Name' field displayed.");
        assertTrue(newContentDialog.isMandatoryIndicatorDisplayed(), "'Name' mandatory field.");
        assertTrue(newContentDialog.isTitleFieldDisplayed(), "'Title' field displayed.");
        assertTrue(newContentDialog.isDescriptionFieldDisplayed(), "'Description' field displayed.");
        assertTrue(newContentDialog.isSaveButtonDisplayed(), "'Save' button displayed.");
        assertTrue(newContentDialog.isCancelButtonDisplayed(), "'Cancel' button displayed.");

        LOG.info("STEP2: Fill in Name field and click 'Save'");
        newContentDialog.fillInNameField(folderName);
        newContentDialog.clickSaveButton();
        assertTrue(documentLibraryPage.isContentNameDisplayed(folderName), folderName + " displayed in Documents list.");
        ArrayList<String> foldersExpected = new ArrayList<>(Arrays.asList("Documents", folderName));
        assertEquals(documentLibraryPage.getExplorerPanelDocuments(), foldersExpected.toString(), "Document Library explorer panel: Library->Documents: ");
    }

    @TestRail(id = "C6278")
    @Test
    public void cancelCreatingFolder()
    {
        String siteName1 = "Site-C6278-" + uniqueId;
        siteService.create(userName, password, domain, siteName1, description, Site.Visibility.PUBLIC);
        String folderName = "C6278-folderName-" + uniqueId;

        setupAuthenticatedSession(userName, password);
        documentLibraryPage.navigate(siteName1);
        assertEquals(documentLibraryPage.getPageTitle(), "Alfresco » Document Library", "Displayed page=");

        LOG.info("STEP1: Click on the 'Create a folder' link");
        documentLibraryPage.clickCreateButton();
        createContent.clickCreateFolderOption();
        assertTrue(newContentDialog.isSaveButtonDisplayed(), "'Save' button is displayed.");

        LOG.info("STEP2: Fill in 'Name' field and click 'Cancel'");
        newContentDialog.fillInNameField(folderName);
        newContentDialog.clickCancelButton();
        assertFalse(newContentDialog.isCancelButtonDisplayed(), "'Cancel' button is displayed.");
        assertFalse(documentLibraryPage.isContentNameDisplayed(folderName), folderName + " displayed in Documents list.");
    }

    @TestRail(id = "C6291")
    @Test
    public void checkFolderStructure()
    {
        String siteName1 = "Site-C6291-" + uniqueId;
        String folderName1 = "C6291-folderName1-" + uniqueId;
        String folderName2 = "C6291-folderName2-" + uniqueId;
        siteService.create(userName, password, domain, siteName1, description, Site.Visibility.PUBLIC);
        contentService.createFolder(userName, password, folderName1, siteName1);

        setupAuthenticatedSession(userName, password);
        documentLibraryPage.navigate(siteName1);
        assertEquals(documentLibraryPage.getPageTitle(), "Alfresco » Document Library", "Displayed page=");

        LOG.info("STEP1: Click on the created folder in 'Library' section of the browsing pane.");
        documentLibraryPage.clickFolderFromExplorerPanel(folderName1);
        assertTrue(documentLibraryPage.getCurrentUrl().contains(folderName1), folderName1 + " opened.");

        LOG.info("STEP2: Click on the 'Create a folder' link");
        documentLibraryPage.clickCreateButton();
        createContent.clickCreateFolderOption();
        assertTrue(newContentDialog.isSaveButtonDisplayed(), "'Save' button is displayed.");

        LOG.info("STEP3: Fill in Name field and click 'Save' button");
        newContentDialog.fillInNameField(folderName2);
        newContentDialog.clickSaveButton();
        documentLibraryPage.renderedPage();
        assertTrue(documentLibraryPage.isContentNameDisplayed(folderName2), folderName2 + " displayed in Documents list.");
        ArrayList<String> foldersExpected = new ArrayList<>(Arrays.asList("Documents", folderName1, folderName2));
        assertEquals(documentLibraryPage.getExplorerPanelDocuments(), foldersExpected.toString(), "Document Library explorer panel Library->Documents= ");

        LOG.info("STEP4: Click on the created subfolder in 'Library' section of the browsing pane");
        documentLibraryPage.clickFolderFromExplorerPanel(folderName2);
        assertTrue(documentLibraryPage.getCurrentUrl().contains(folderName2), folderName2 + " opened.");
        ArrayList<String> breadcrumbExpected = new ArrayList<>(Arrays.asList("Documents", folderName1, folderName2));
        assertEquals(documentLibraryPage.getBreadcrumbList(), breadcrumbExpected.toString(), "Document Library breadcrumb=");

        LOG.info("STEP5: Click 'Up' button");
        documentLibraryPage.clickFolderUpButton();
        assertTrue(documentLibraryPage.isContentNameDisplayed(folderName2), folderName2 + " displayed in Documents list.");
        ArrayList<String> breadcrumbExpected2 = new ArrayList<>(Arrays.asList("Documents", folderName1));
        assertEquals(documentLibraryPage.getBreadcrumbList(), breadcrumbExpected2.toString(), "Document Library breadcrumb=");
        assertEquals(documentLibraryPage.getFoldersList().toString(), "[" + folderName2 + "]", "Folders displayed in " + folderName1);

        LOG.info("STEP6: Click on the created subfolder name in content item list");
        documentLibraryPage.clickOnFolderName(folderName2);
        assertTrue(documentLibraryPage.getCurrentUrl().contains(folderName2), folderName2 + " opened.");
        ArrayList<String> breadcrumbExpected3 = new ArrayList<>(Arrays.asList("Documents", folderName1, folderName2));
        assertEquals(documentLibraryPage.getBreadcrumbList(), breadcrumbExpected3.toString(), "Document Library breadcrumb=");
        assertEquals(documentLibraryPage.getFoldersList().toString(), "[]", "Folders displayed in " + folderName2);
        assertEquals(documentLibraryPage.getFilesList().toString(), "[]", "Files displayed in " + folderName2);

        LOG.info("STEP7: Click on 'Documents' link from breadcrumb path");
        documentLibraryPage.clickFolderFromBreadcrumb("Documents");
        ArrayList<String> breadcrumbExpected4 = new ArrayList<>(Collections.singletonList("Documents"));
        assertEquals(documentLibraryPage.getBreadcrumbList(), breadcrumbExpected4.toString(), "Document Library breadcrumb=");
        assertEquals(documentLibraryPage.getFoldersList().toString(), "[" + folderName1 + "]", "Folders displayed in Documents=");
    }
}