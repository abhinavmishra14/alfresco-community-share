package org.alfresco.share.alfrescoContent.organizingContent;

import org.alfresco.common.DataUtil;
import org.alfresco.dataprep.CMISUtil;
import org.alfresco.po.share.alfrescoContent.SharedFilesPage;
import org.alfresco.po.share.alfrescoContent.organizingContent.CopyMoveUnzipToDialog;
import org.alfresco.po.share.site.DocumentLibraryPage;
import org.alfresco.po.share.toolbar.Toolbar;
import org.alfresco.share.ContextAwareWebTest;
import org.alfresco.testrail.TestRail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.social.alfresco.api.entities.Site;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.ArrayList;

import static java.util.Arrays.asList;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

/**
 * @author Laura.Capsa
 */
public class CopyingContentTests extends ContextAwareWebTest
{
    @Autowired
    Toolbar toolbar;

    @Autowired
    DocumentLibraryPage documentLibraryPage;

    @Autowired
    SharedFilesPage sharedFilesPage;

    @Autowired
    CopyMoveUnzipToDialog copyMoveToDialog;

    String userName = "profileUser1-" + DataUtil.getUniqueIdentifier();
    String firstName = "FirstName";
    String lastName = "LastName";
    String description = "Description-" + DataUtil.getUniqueIdentifier();
    String docContent = "content of the file.";
    String copyAction = "Copy to...";

    @BeforeClass
    public void setupTest()
    {
        userService.create(adminUser, adminPassword, userName, DataUtil.PASSWORD, userName + domain, firstName, lastName);
    }

    @TestRail(id = "C7377")
    @Test
    public void copyFileToSharedFiles()
    {
        String siteName = "Site-C7377-" + DataUtil.getUniqueIdentifier();
        String docName = "Doc-C7377-" + DataUtil.getUniqueIdentifier();
        siteService.create(userName, DataUtil.PASSWORD, domain, siteName, description, Site.Visibility.PUBLIC);
        content.createDocument(userName, DataUtil.PASSWORD, siteName, CMISUtil.DocumentType.TEXT_PLAIN, docName, docContent);

        setupAuthenticatedSession(userName, password);
        documentLibraryPage.navigate(siteName);
        assertEquals(documentLibraryPage.getPageTitle(), "Alfresco » Document Library", "Displayed page:");

        LOG.info("STEP1: Hover over the file");
        documentLibraryPage.mouseOverContentItem(docName);

        LOG.info("STEP2: Click 'More...' link. Click 'Copy to...' link");
        documentLibraryPage.clickDocumentLibraryItemAction(docName, copyAction, copyMoveToDialog);
        assertEquals(copyMoveToDialog.getDialogTitle(), "Copy " + docName + " to...", "Displayed pop up");

        LOG.info("STEP3: Set the destination to 'Shared Files'");
        copyMoveToDialog.clickDestinationButton("Shared Files");

        LOG.info("STEP4: Click 'Copy' button");
        copyMoveToDialog.clickButtton("Copy");
        assertTrue(documentLibraryPage.isOptionsMenuDisplayed(), "'Copy to' dialog not displayed");

        LOG.info("STEP5: Verify displayed files from Documents");
        assertTrue(documentLibraryPage.isContentNameDisplayed(docName), docName + " displayed in 'Documents'");

        LOG.info("STEP6: Go to 'Shared Files', from toolbar and verify the displayed files");
        sharedFilesPage.navigate();
        assertEquals(sharedFilesPage.getPageTitle(), "Alfresco » Shared Files", "Displayed page=");
        if (!documentLibraryPage.isContentNameDisplayed(docName))
            browser.refresh();
        assertTrue(documentLibraryPage.getFilesList().toString().contains(docName),
                docName + " displayed in 'Shared Files'. List of 'Shared Files' documents=" + documentLibraryPage.getFilesList().toString());

        cleanupAuthenticatedSession();
    }

    @TestRail(id = "C7378")
    @Test
    public void cancelCopyFileToSharedFiles()
    {
        String siteName = "Site-C7378-" + DataUtil.getUniqueIdentifier();
        String docName = "Doc-C7378-" + DataUtil.getUniqueIdentifier();
        siteService.create(userName, DataUtil.PASSWORD, domain, siteName, description, Site.Visibility.PUBLIC);
        content.createDocument(userName, DataUtil.PASSWORD, siteName, CMISUtil.DocumentType.TEXT_PLAIN, docName, docContent);

        setupAuthenticatedSession(userName, password);
        documentLibraryPage.navigate(siteName);
        assertEquals(documentLibraryPage.getPageTitle(), "Alfresco » Document Library", "Displayed page:");

        LOG.info("STEP1: Hover over the file");
        documentLibraryPage.mouseOverContentItem(docName);

        LOG.info("STEP2: Click 'More...' link. Click 'Copy to...' link");
        documentLibraryPage.clickDocumentLibraryItemAction(docName, copyAction, copyMoveToDialog);
        assertEquals(copyMoveToDialog.getDialogTitle(), "Copy " + docName + " to...", "Displayed pop up");

        LOG.info("STEP3: Set the destination to 'Shared Files'");
        copyMoveToDialog.clickDestinationButton("Shared Files");

        LOG.info("STEP4: Click 'Cancel' button");
        copyMoveToDialog.clickButtton("Cancel");
        assertTrue(documentLibraryPage.isOptionsMenuDisplayed(), "'Copy to' dialog not displayed");

        LOG.info("STEP5: Verify displayed files from Documents");
        assertTrue(documentLibraryPage.isContentNameDisplayed(docName), docName + " displayed in 'Documents'");

        LOG.info("STEP6: Go to 'Shared Files', from toolbar and verify the displayed files");
        toolbar.clickSharedFiles();
        assertFalse(documentLibraryPage.getFilesList().toString().contains(docName), docName + " displayed in 'Shared Files'");

        cleanupAuthenticatedSession();
    }

    @TestRail(id = "C7388")
    @Test
    public void copyFolderToPublicSite()
    {
        String siteName1 = "Site1-C7388-" + DataUtil.getUniqueIdentifier();
        String siteName2 = "Site2-C7388-" + DataUtil.getUniqueIdentifier();
        String docName = "TestDoc-C7388-" + DataUtil.getUniqueIdentifier();
        String folderName = "Folder-C7388-" + DataUtil.getUniqueIdentifier();
        siteService.create(userName, DataUtil.PASSWORD, domain, siteName1, description, Site.Visibility.PUBLIC);
        siteService.create(userName, DataUtil.PASSWORD, domain, siteName2, description, Site.Visibility.PUBLIC);
        content.createDocument(userName, DataUtil.PASSWORD, siteName1, CMISUtil.DocumentType.TEXT_PLAIN, docName, docContent);
        content.createFolder(userName, password, folderName, siteName1);

        setupAuthenticatedSession(userName, password);
        documentLibraryPage.navigate(siteName1);
        assertEquals(documentLibraryPage.getPageTitle(), "Alfresco » Document Library", "Page displayed");

        LOG.info("STEP1: Hover over folder");
        documentLibraryPage.mouseOverContentItem(folderName);

        LOG.info("STEP2: Click 'More...' link. Click 'Copy to...' link");
        documentLibraryPage.clickDocumentLibraryItemAction(folderName, copyAction, copyMoveToDialog);
        assertEquals(copyMoveToDialog.getDialogTitle(), "Copy " + folderName + " to...", "Displayed pop up");

        LOG.info("STEP4: Set the destination to 'All Sites'");
        copyMoveToDialog.clickDestinationButton("All Sites");
        ArrayList<String> expectedPath_destination = new ArrayList<>(asList("Documents", folderName));
        assertEquals(copyMoveToDialog.getPathList(), expectedPath_destination.toString(), "Path");

        LOG.info("STEP5: Select a site");
        copyMoveToDialog.clickSite(siteName2);
        ArrayList<String> expectedPath = new ArrayList<>(asList("Documents"));
        assertEquals(copyMoveToDialog.getPathList(), expectedPath.toString(), "Path");

        LOG.info("STEP6: Click 'Copy' button");
        copyMoveToDialog.clickButtton("Copy");
        assertTrue(documentLibraryPage.isOptionsMenuDisplayed(), "'Copy to' dialog not displayed");

        LOG.info("STEP7: Verify that the folder has been copied");
        documentLibraryPage.navigate(siteName2);
        assertEquals(documentLibraryPage.getPageTitle(), "Alfresco » Document Library", "Page displayed");
        ArrayList<String> expectedFolderList = new ArrayList<>(asList(folderName));
        assertEquals(documentLibraryPage.getFoldersList().toString(), expectedFolderList.toString(), "Displayed folders=");

        cleanupAuthenticatedSession();
    }
}
