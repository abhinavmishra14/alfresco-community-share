package org.alfresco.share.alfrescoContent.organizingContent;

import org.alfresco.common.DataUtil;
import org.alfresco.dataprep.CMISUtil;
import org.alfresco.po.share.Notification;
import org.alfresco.po.share.alfrescoContent.organizingContent.DeleteDocumentOrFolderDialog;
import org.alfresco.po.share.site.DocumentLibraryPage;
import org.alfresco.share.ContextAwareWebTest;
import org.alfresco.testrail.TestRail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.social.alfresco.api.entities.Site;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.Arrays;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

/**
 * Created by Claudia Agache on 9/2/2016.
 */
public class DeletingContentTests extends ContextAwareWebTest
{
    @Autowired
    DocumentLibraryPage documentLibraryPage;

    @Autowired
    DeleteDocumentOrFolderDialog deleteDialog;

    @Autowired
    Notification notification;

    private String testUser = "testUser" + DataUtil.getUniqueIdentifier();
    private String siteName = "siteName" + DataUtil.getUniqueIdentifier();
    private String folderNameD = "delFolder" + DataUtil.getUniqueIdentifier();
    private String subFolder = "delSubfolder" + DataUtil.getUniqueIdentifier();
    private String folderNameC = "cancelFolder" + DataUtil.getUniqueIdentifier();
    private String docName = "testDoc" + DataUtil.getUniqueIdentifier();
    String folderPathInRepository = "Sites/"+siteName+"/documentLibrary/";

    @BeforeClass
    public void setupTest()
    {
        userService.create(adminUser, adminPassword, testUser, password, testUser + "@tests.com", "firstName", "lastName");
        siteService.create(testUser, password, domain, siteName, siteName, Site.Visibility.PUBLIC);
        setupAuthenticatedSession(testUser, password);
    }

    @TestRail(id = "C9544")
    @Test
    public void deleteDocument()
    {
        contentService.createDocument(testUser, password, siteName, CMISUtil.DocumentType.TEXT_PLAIN, docName, "Document content");
        documentLibraryPage.navigate(siteName);

        LOG.info("STEP1: Hover over the file");
        documentLibraryPage.mouseOverFileName(docName);

        LOG.info("STEP2: Click 'More...' link. Click 'Delete Document' link");
        documentLibraryPage.clickDocumentLibraryItemAction(docName, "Delete Document", deleteDialog);
        assertEquals(deleteDialog.getHeader(), language.translate("documentLibrary.deleteDocument"), "'Delete Document' pop-up is displayed");
        assertEquals(deleteDialog.getMessage(), String.format(language.translate("confirmDeletion.message"), docName));

        LOG.info("STEP3: Click 'Delete' button");
        deleteDialog.confirmDocumentOrFolderDelete();
//        assertEquals(notification.getDisplayedNotification(), String.format(language.translate("documentLibrary.deletedNotification"), docName), "'testDoc' was deleted pop-up is displayed.");

        LOG.info("STEP4: Verify that the file was deleted");
        assertFalse(documentLibraryPage.isContentNameDisplayed(docName), "Documents item list is refreshed and is empty");
    }

    @TestRail(id = "C6968")
    @Test
    public void deleteFolder()
    {
        contentService.createFolder(testUser, password, folderNameD, siteName);
        contentService.createFolderInRepository(adminUser, adminPassword, subFolder, folderPathInRepository+folderNameD+"/");
        documentLibraryPage.navigate(siteName);

        LOG.info("STEP1: Hover over the folder");
        documentLibraryPage.mouseOverContentItem(folderNameD);

        LOG.info("STEP2: Click on 'More...' link and choose 'Delete Folder' from the dropdown list.");
        documentLibraryPage.clickDocumentLibraryItemAction(folderNameD, "Delete Folder", deleteDialog);
        assertEquals(deleteDialog.getHeader(), language.translate("documentLibrary.deleteFolder"), "'Delete Folder' pop-up is displayed");
        assertEquals(deleteDialog.getMessage(), String.format(language.translate("confirmDeletion.message"), folderNameD));

        LOG.info("STEP3: Click 'Delete' button");
        deleteDialog.confirmDocumentOrFolderDelete();
//        assertEquals(notification.getDisplayedNotification(), String.format(language.translate("documentLibrary.deletedNotification"), folderNameD), "'delFolder' was deleted pop-up is displayed.");
        assertFalse(documentLibraryPage.isContentNameDisplayed(folderNameD), "Documents item list is refreshed and is empty");
        assertFalse(documentLibraryPage.getExplorerPanelDocuments().contains(folderNameD), "'DelFolder' is not visible in 'Library' section of the browsing pane.");
        assertFalse(documentLibraryPage.getExplorerPanelDocuments().contains(subFolder), "'DelSubfolder' is not visible in 'Library' section of the browsing pane.");
    }

    @TestRail(id = "C6968")
    @Test
    public void cancelDeletingFolder()
    {
        contentService.createFolder(testUser, password, folderNameC, siteName);
        contentService.createFolderInRepository(adminUser, adminPassword, subFolder, folderPathInRepository+folderNameC+"/");
        documentLibraryPage.navigate(siteName);
        documentLibraryPage.clickFolderFromExplorerPanel(folderNameC);

        LOG.info("STEP1: Hover 'DelSubfolder' name from the content item list.");
        documentLibraryPage.mouseOverContentItem(subFolder);

        LOG.info("STEP2: Click on 'More...' link and choose 'Delete Folder' from the dropdown list.");
        documentLibraryPage.clickDocumentLibraryItemAction(subFolder, "Delete Folder", deleteDialog);
        assertEquals(deleteDialog.getHeader(), language.translate("documentLibrary.deleteFolder"), "'Delete Folder' pop-up is displayed");
        assertEquals(deleteDialog.getMessage(), String.format(language.translate("confirmDeletion.message"), subFolder));

        LOG.info("STEP3: Click 'Cancel' button");
        deleteDialog.clickCancel();
        ArrayList<String> breadcrumbExpected = new ArrayList<>(Arrays.asList("Documents", folderNameC));
        assertEquals(documentLibraryPage.getBreadcrumbList(), breadcrumbExpected.toString(), "Document Library breadcrumb");
        assertTrue(documentLibraryPage.isContentNameDisplayed(subFolder), "User returns to 'DelFolder' item list which contains 'DelSubfolder'.");
        assertTrue(documentLibraryPage.getExplorerPanelDocuments().contains(folderNameC), "'DelFolder' is still visible in 'Library' section of the browsing pane.");
        assertTrue(documentLibraryPage.getExplorerPanelDocuments().contains(subFolder), "'DelSubfolder' is still visible in 'Library' section of the browsing pane.");
    }
}
