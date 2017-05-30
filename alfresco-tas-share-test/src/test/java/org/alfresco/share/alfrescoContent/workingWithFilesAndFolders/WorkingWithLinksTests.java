package org.alfresco.share.alfrescoContent.workingWithFilesAndFolders;

import org.alfresco.dataprep.CMISUtil;
import org.alfresco.po.share.DeleteDialog;
import org.alfresco.po.share.alfrescoContent.RepositoryPage;
import org.alfresco.po.share.alfrescoContent.SharedFilesPage;
import org.alfresco.po.share.alfrescoContent.document.DocumentDetailsPage;
import org.alfresco.po.share.alfrescoContent.document.GoogleDocsCommon;
import org.alfresco.po.share.alfrescoContent.document.UploadContent;
import org.alfresco.po.share.alfrescoContent.organizingContent.CopyMoveUnzipToDialog;
import org.alfresco.po.share.site.DocumentLibraryPage;
import org.alfresco.share.ContextAwareWebTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.data.RandomData;
import org.alfresco.utility.model.TestGroup;
import org.alfresco.utility.report.Bug;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.social.alfresco.api.entities.Site;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.Arrays;

import static org.testng.Assert.*;

/**
 * @author Laura.Capsa
 */
public class WorkingWithLinksTests extends ContextAwareWebTest {
    @Autowired
    private DocumentLibraryPage documentLibraryPage;

    @Autowired
    private CopyMoveUnzipToDialog copyMoveUnzipToDialog;

    @Autowired
    private SharedFilesPage sharedFilesPage;

    @Autowired
    private DocumentDetailsPage documentDetailsPage;

    @Autowired
    private GoogleDocsCommon googleDocsCommon;

    @Autowired
    private UploadContent uploadContent;

    @Autowired
    private RepositoryPage repositoryPage;

    @Autowired
    private DeleteDialog deleteDialog;

    private final String uniqueIdentifier = RandomData.getRandomAlphanumeric();
    private final String userName = "user" + uniqueIdentifier;
    private final String firstName = "user";
    private final String lastName = uniqueIdentifier;
    private final String description = "description" + uniqueIdentifier;
    private final String content = "content" + uniqueIdentifier;
    private final String siteName1 = "1site" + uniqueIdentifier;
    private final String siteName2 = "2site-" + uniqueIdentifier;
    private final String fileC42624 = "C42624-" + uniqueIdentifier;
    private final String fileC42625 = "C42625-" + uniqueIdentifier;
    private final String linkC42624 = "Link to " + fileC42624;
    private final String linkC42625 = "Link to " + fileC42625;
    private final String folderC42626 = "C42626-" + uniqueIdentifier;
    private final String linkC42626 = "Link to " + folderC42626;
    private final String fileNameC42627 = "C42627FileName";
    private final String linkC42627 = "Link to " + fileNameC42627;
    private final String fileC42628 = "C42628-" + uniqueIdentifier;
    private final String newFileC42628 = "EditedFileC7074.txt";
    private final String linkC42628 = "Link to " + newFileC42628;
    private final String newVersionFilePath = testDataFolder + newFileC42628;
    private final String fileC42629 = "C42629-" + uniqueIdentifier;
    private final String linkC42629 = "Link to " + fileC42629;
    private final String fileC42630 = "C42630-" + uniqueIdentifier;
    private final String linkC42630 = "Link to " + fileC42630;
    private final String folderC42631 = "C42631-" + uniqueIdentifier;
    private final String linkC42631 = "Link to " + folderC42631;

    @BeforeClass(alwaysRun = true)
    public void setupTest() {
        userService.create(adminUser, adminPassword, userName, password, userName + domain, firstName, lastName);
        siteService.create(userName, password, domain, siteName1, description, Site.Visibility.PUBLIC);
        siteService.create(userName, password, domain, siteName2, description, Site.Visibility.PRIVATE);
        contentService.createDocument(userName, password, siteName1, CMISUtil.DocumentType.TEXT_PLAIN, fileC42624, content);
        contentService.createDocument(userName, password, siteName1, CMISUtil.DocumentType.TEXT_PLAIN, fileC42625, content);
        contentService.createDocument(userName, password, siteName1, CMISUtil.DocumentType.TEXT_PLAIN, fileC42628, content);
        contentService.createDocument(userName, password, siteName2, CMISUtil.DocumentType.TEXT_PLAIN, fileC42629, content);
        contentService.createDocument(userName, password, siteName2, CMISUtil.DocumentType.TEXT_PLAIN, fileC42630, content);
        contentService.createDocument(userName, password, siteName1, CMISUtil.DocumentType.TEXT_PLAIN, fileNameC42627, content);
        contentService.createFolder(userName, password, folderC42626, siteName1);
        contentService.createFolder(userName, password, folderC42631, siteName2);
        setupAuthenticatedSession(userName, password);
    }

    @TestRail(id = "C42624")
    @Test(groups = {TestGroup.SANITY, TestGroup.CONTENT})
    public void verifyDisabledActionsForCreatedLink() {
        LOG.info("STEP1: Go to Document Library of the site");
        documentLibraryPage.navigate(siteName1);
        LOG.info("STEP2: For a file/folder, click on \"Copy to\", select a destination folder");
        documentLibraryPage.mouseOverContentItem(fileC42624);
        documentLibraryPage.clickDocumentLibraryItemAction(fileC42624, language.translate("documentLibrary.contentActions.copyTo"), copyMoveUnzipToDialog);
        assertEquals(copyMoveUnzipToDialog.getDialogTitle(), "Copy " + fileC42624 + " to...", "Displayed dialog=");
        copyMoveUnzipToDialog.clickDestinationButton(language.translate("documentLibrary.sharedFiles"));
        copyMoveUnzipToDialog.clickButton(language.translate("documentLibrary.contentActions.createLink"));
        LOG.info("STEP3: Go to the link's destination " + linkC42624);
        sharedFilesPage.navigate();
        assertEquals(sharedFilesPage.getPageTitle(), "Alfresco » Shared Files", "Displayed page=");
        assertTrue(sharedFilesPage.isContentNameDisplayed(linkC42624), linkC42624 + " is displayed in destination of copy file, Shared Files.");
        assertFalse(sharedFilesPage.isLikeButtonDisplayed(linkC42624), linkC42624 + " 'Like' button is displayed.");
        assertFalse(sharedFilesPage.isCommentButtonDisplayed(linkC42624), linkC42624 + " 'Comment' button is displayed.");
        assertFalse(sharedFilesPage.isShareButtonDisplayed(linkC42624), linkC42624 + " 'Share' button is displayed.");
    }

    @TestRail(id = "C42625")
    @Test(groups = {TestGroup.SANITY, TestGroup.CONTENT})
    public void linkToFileRedirectsToDocDetailsPage() {
        documentLibraryPage.navigate(siteName1);
        LOG.info("From Document actions, click on \"Copy to\" option");
        documentLibraryPage.mouseOverContentItem(fileC42625);
        documentLibraryPage.clickDocumentLibraryItemAction(fileC42625, language.translate("documentLibrary.contentActions.copyTo"), copyMoveUnzipToDialog);
        assertEquals(copyMoveUnzipToDialog.getDialogTitle(), "Copy " + fileC42625 + " to...", "Displayed dialog=");
        LOG.info("Select a destination folder and click \"Create Link\" button");
        copyMoveUnzipToDialog.clickDestinationButton(language.translate("documentLibrary.sharedFiles"));
        copyMoveUnzipToDialog.clickButton(language.translate("documentLibrary.contentActions.createLink"));
        LOG.info("STEP1: Go to the link's location");
        sharedFilesPage.navigate();
        assertEquals(sharedFilesPage.getPageTitle(), "Alfresco » Shared Files", "Displayed page=");
        assertTrue(sharedFilesPage.isContentNameDisplayed(linkC42625), linkC42625 + " is displayed in destination of copy file, Shared Files.");
        LOG.info("STEP2: Click on the link to file");
        sharedFilesPage.clickOnFile(linkC42625);
        assertEquals(documentDetailsPage.getPageTitle(), "Alfresco » Document Details", "Displayed page=");
        assertEquals(documentDetailsPage.getFileName(), fileC42625, "Document name=");
    }

    @TestRail(id = "C42626")
    @Test(groups ={TestGroup.SANITY, TestGroup.CONTENT})
    public void linkToFolderRedirectsToFolderContentsPage() {
        documentLibraryPage.navigate(siteName1);
        LOG.info("From Document actions, click on \"Copy to\" option");
        documentLibraryPage.mouseOverContentItem(folderC42626);
        documentLibraryPage.clickDocumentLibraryItemAction(folderC42626, language.translate("documentLibrary.contentActions.copyTo"), copyMoveUnzipToDialog);
        assertEquals(copyMoveUnzipToDialog.getDialogTitle(), "Copy " + folderC42626 + " to...", "Displayed dialog=");
        LOG.info("Select a destination folder and click \"Create Link\" button");
        copyMoveUnzipToDialog.clickDestinationButton(language.translate("documentLibrary.sharedFiles"));
        copyMoveUnzipToDialog.clickButton(language.translate("documentLibrary.contentActions.createLink"));
        LOG.info("STEP1: Go to the link's location");
        sharedFilesPage.navigate();
        assertEquals(sharedFilesPage.getPageTitle(), "Alfresco » Shared Files", "Displayed page=");
        assertTrue(sharedFilesPage.isContentNameDisplayed(linkC42626), linkC42626 + " is displayed in destination of copy file, Shared Files.");
        LOG.info("STEP2: Click on the link to folder");
        sharedFilesPage.clickLinkToFolder(linkC42626);
        assertEquals(repositoryPage.getPageTitle(), "Alfresco » Repository Browser", "Displayed page=");
        ArrayList<String> breadcrumb = new ArrayList<>(Arrays.asList("Repository", "Sites", siteName1, "documentLibrary", folderC42626));
        assertEquals(repositoryPage.getBreadcrumbList(), breadcrumb.toString(), "Breadcrumb=");
    }

    @TestRail(id = "C42627")
    @Test(groups = { TestGroup.SANITY, TestGroup.CONTENT })
    public void linkToLockedDocRedirectsToOriginalDoc() {
        logger.info("Precondition1: Login to Share/Google Docs and navigate to Document Library page for the test site; upload a .docx file");
        setupAuthenticatedSession(userName, password);
        LOG.info("Precondition2: Go to Document Library of the site. Create link for document");
        documentLibraryPage.navigate(siteName1);
        documentLibraryPage.mouseOverContentItem(fileNameC42627);
        documentLibraryPage.clickDocumentLibraryItemAction(fileNameC42627, language.translate("documentLibrary.contentActions.copyTo"), copyMoveUnzipToDialog);
        assertEquals(copyMoveUnzipToDialog.getDialogTitle(), "Copy " + fileNameC42627 + " to...", "Displayed dialog=");
        copyMoveUnzipToDialog.clickDestinationButton(language.translate("documentLibrary.sharedFiles"));
        copyMoveUnzipToDialog.clickButton(language.translate("documentLibrary.contentActions.createLink"));
        LOG.info("STEP1: Lock the document, e.g: edit it Google Docs");
        documentLibraryPage.navigate(siteName1);
        documentLibraryPage.clickDocumentLibraryItemAction(fileNameC42627, "Edit Offline", documentLibraryPage);
        LOG.info("STEP2: Go to the location where the link was created");
        sharedFilesPage.navigate();
        assertEquals(sharedFilesPage.getPageTitle(), "Alfresco » Shared Files", "Displayed page=");
        assertTrue(documentLibraryPage.isContentNameDisplayed(linkC42627), linkC42627 + " is displayed in destination of copy file, Shared Files.");
        LOG.info("STEP3: Click on the link");
        documentLibraryPage.clickOnFile(linkC42627);
        assertEquals(documentDetailsPage.getPageTitle(), "Alfresco » Document Details", "Displayed page=");
        assertEquals(documentDetailsPage.getFileName(), fileNameC42627, "Document name=");
    }

    @TestRail(id = "C42628")
    @Test(groups = {TestGroup.SANITY, TestGroup.CONTENT})
    public void linkToMultipleVersionsDocRedirectsToLastVersion() {
        documentLibraryPage.navigate(siteName1);
        LOG.info("Precondition1: 'Upload new version' for a file");
        documentLibraryPage.clickDocumentLibraryItemAction(fileC42628, "Upload New Version", uploadContent);
        uploadContent.updateDocumentVersion(newVersionFilePath, "New Version", UploadContent.Version.Major);
        assertTrue(documentLibraryPage.isContentNameDisplayed(newFileC42628), String.format("File [%s] is displayed.", newFileC42628));
        LOG.info("Precondition2: Create link for the file with new version " + newFileC42628);
        documentLibraryPage.mouseOverContentItem(newFileC42628);
        documentLibraryPage.clickDocumentLibraryItemAction(newFileC42628, language.translate("documentLibrary.contentActions.copyTo"), copyMoveUnzipToDialog);
        assertEquals(copyMoveUnzipToDialog.getDialogTitle(), "Copy " + newFileC42628 + " to...", "Displayed dialog=");
        copyMoveUnzipToDialog.clickDestinationButton(language.translate("documentLibrary.sharedFiles"));
        copyMoveUnzipToDialog.clickButton(language.translate("documentLibrary.contentActions.createLink"));
        LOG.info("STEP1: Go to the location where the link was created");
        sharedFilesPage.navigate();
        assertEquals(sharedFilesPage.getPageTitle(), "Alfresco » Shared Files", "Displayed page=");
        LOG.info("STEP2: Click on the created link");
        sharedFilesPage.clickOnFile(linkC42628);
        assertEquals(documentDetailsPage.getPageTitle(), "Alfresco » Document Details", "Displayed page=");
        assertEquals(documentDetailsPage.getFileName(), newFileC42628, "Document name=");
    }

    @TestRail(id = "C42629")
    @Test(groups = {TestGroup.SANITY, TestGroup.CONTENT})
    public void verifyDisplayedActionsForLinkToFile() {
        LOG.info("Precondition: For a file click 'Copy to' option");
        documentLibraryPage.navigate(siteName2);
        documentLibraryPage.mouseOverContentItem(fileC42629);
        documentLibraryPage.clickDocumentLibraryItemAction(fileC42629, language.translate("documentLibrary.contentActions.copyTo"), copyMoveUnzipToDialog);
        assertEquals(copyMoveUnzipToDialog.getDialogTitle(), "Copy " + fileC42629 + " to...", "Displayed dialog=");
        LOG.info("Precondition: Click \"Create Link\" button");
        copyMoveUnzipToDialog.clickDestinationButton(language.translate("documentLibrary.sharedFiles"));
        copyMoveUnzipToDialog.clickButton(language.translate("documentLibrary.contentActions.createLink"));
        LOG.info("STEP1: Go to the location where the link was created");
        sharedFilesPage.navigate();
        assertEquals(sharedFilesPage.getPageTitle(), "Alfresco » Shared Files", "Displayed page=");
        assertTrue(documentLibraryPage.isContentNameDisplayed(linkC42629), linkC42629 + " is displayed in destination of copy file, Shared Files.");
        LOG.info("STEP2: Verify available actions for " + linkC42629);
        assertTrue(sharedFilesPage.isActionAvailableForLibraryItem(linkC42629, language.translate("documentLibrary.contentActions.locateLinkedItem")),
                "'Locate Linked Item' action is displayed for " + linkC42629);
        assertTrue(sharedFilesPage.isActionAvailableForLibraryItem(linkC42629, language.translate("documentLibrary.contentActions.deleteLink")),
                "'Delete Link' action is displayed for " + linkC42629);
        assertTrue(sharedFilesPage.isActionAvailableForLibraryItem(linkC42629, language.translate("documentLibrary.contentActions.copyTo")),
                "'Copy to...' action is displayed for " + linkC42629);
        assertTrue(sharedFilesPage.isActionAvailableForLibraryItem(linkC42629, language.translate("documentLibrary.contentActions.moveTo")),
                "'Move to...' action is displayed for " + linkC42629);
    }

    @TestRail(id = "C42630")
    @Test(groups = {TestGroup.SANITY, TestGroup.CONTENT})
    public void verifyLocateLinkedItemRedirectsToOriginalDoc() {
        LOG.info("Precondition: For a file click 'Copy to' option");
        documentLibraryPage.navigate(siteName2);
        documentLibraryPage.mouseOverContentItem(fileC42630);
        documentLibraryPage.clickDocumentLibraryItemAction(fileC42630, language.translate("documentLibrary.contentActions.copyTo"), copyMoveUnzipToDialog);
        assertEquals(copyMoveUnzipToDialog.getDialogTitle(), "Copy " + fileC42630 + " to...", "Displayed dialog=");
        LOG.info("Precondition: Click \"Create Link\" button");
        copyMoveUnzipToDialog.clickDestinationButton(language.translate("documentLibrary.sharedFiles"));
        copyMoveUnzipToDialog.clickButton(language.translate("documentLibrary.contentActions.createLink"));
        LOG.info("STEP1: Go to the location where the link was created");
        sharedFilesPage.navigate();
        assertEquals(sharedFilesPage.getPageTitle(), "Alfresco » Shared Files", "Displayed page=");
        assertTrue(sharedFilesPage.isContentNameDisplayed(linkC42630), linkC42630 + " is displayed in destination of copy file, Shared Files.");
        LOG.info("STEP2: Mouse over the link and click on 'Locate Linked Item' option");
        sharedFilesPage.clickDocumentLibraryItemAction(linkC42630, language.translate("documentLibrary.contentActions.locateLinkedItem"), repositoryPage);
        assertEquals(repositoryPage.getPageTitle(), "Alfresco » Repository Browser", "Displayed page=");
        assertTrue(repositoryPage.isContentSelected(fileC42630), fileC42630 + " is selected");
    }
    @Bug(id = "TBD")
    @TestRail(id = "C42631")
    @Test(groups = {TestGroup.SANITY, TestGroup.CONTENT})
    public void verifyLocateLinkedItemRedirectsToFolder() {
        LOG.info("Precondition: For a file click 'Copy to' option");
        documentLibraryPage.navigate(siteName2);
        documentLibraryPage.mouseOverContentItem(folderC42631);
        documentLibraryPage.clickDocumentLibraryItemAction(folderC42631, language.translate("documentLibrary.contentActions.copyTo"), copyMoveUnzipToDialog);
        assertEquals(copyMoveUnzipToDialog.getDialogTitle(), "Copy " + folderC42631 + " to...", "Displayed dialog=");
        LOG.info("Precondition: Click \"Create Link\" button");
        copyMoveUnzipToDialog.clickDestinationButton(language.translate("documentLibrary.sharedFiles"));
        copyMoveUnzipToDialog.clickButton(language.translate("documentLibrary.contentActions.createLink"));
        LOG.info("STEP1: Go to the location where the link was created");
        sharedFilesPage.navigate();
        assertEquals(sharedFilesPage.getPageTitle(), "Alfresco » Shared Files", "Displayed page=");
        assertTrue(sharedFilesPage.isContentNameDisplayed(linkC42631), linkC42631 + " is displayed in destination of copy file, Shared Files.");
        LOG.info("STEP2: Mouse over the link and click on 'Locate Linked Item' option");
        sharedFilesPage.clickDocumentLibraryItemAction(linkC42631, language.translate("documentLibrary.contentActions.locateLinkedItem"), repositoryPage);
        assertEquals(repositoryPage.getPageTitle(), "Alfresco » Repository Browser", "Displayed page=");
        assertTrue(repositoryPage.isContentSelected(folderC42631), folderC42631 + " is selected");
    }

    @Bug(id = "TBD")
    @TestRail(id = "C42632")
    @Test(groups = {TestGroup.SANITY, TestGroup.CONTENT})
    public void deleteLinkRemovesLink() {
        LOG.info("Precondition: For a file click 'Copy to' option");
        documentLibraryPage.navigate(siteName2);
        documentLibraryPage.mouseOverContentItem(folderC42631);
        documentLibraryPage.clickDocumentLibraryItemAction(folderC42631, language.translate("documentLibrary.contentActions.copyTo"), copyMoveUnzipToDialog);
        assertEquals(copyMoveUnzipToDialog.getDialogTitle(), "Copy " + folderC42631 + " to...", "Displayed dialog=");
        LOG.info("Precondition: Click \"Create Link\" button");
        copyMoveUnzipToDialog.clickDestinationButton(language.translate("documentLibrary.sharedFiles"));
        copyMoveUnzipToDialog.clickButton(language.translate("documentLibrary.contentActions.createLink"));
        LOG.info("STEP1: Go to the location where the link was created");
        sharedFilesPage.navigate();
        assertEquals(sharedFilesPage.getPageTitle(), "Alfresco » Shared Files", "Displayed page=");
        assertTrue(sharedFilesPage.isContentNameDisplayed(linkC42631), linkC42631 + " is displayed in destination of copy file, Shared Files.");
        LOG.info("STEP2: Mouse over the link and click on 'Delete Link' option");
        sharedFilesPage.clickDocumentLibraryItemAction(linkC42631, language.translate("documentLibrary.contentActions.deleteLink"), documentLibraryPage);
        assertEquals(deleteDialog.getHeader(), language.translate("documentLibrary.contentActions.deleteDocument"), "Displayed dialog=");
        LOG.info("STEP3: In the Delete confirmation dialog press 'Delete' button");
        deleteDialog.clickDelete();
        assertFalse(sharedFilesPage.isContentNameDisplayed(linkC42631), linkC42631 + " is displayed in destination of copy file, Shared Files.");
    }
}