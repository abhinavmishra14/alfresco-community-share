package org.alfresco.share.alfrescoContent.organizingContent;

import org.alfresco.common.DataUtil;
import org.alfresco.dataprep.CMISUtil;
import org.alfresco.po.share.site.DocumentLibraryPage;
import org.alfresco.share.ContextAwareWebTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.report.Bug;
import org.openqa.selenium.By;
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
 * Created by Claudia Agache on 9/13/2016.
 */
public class LocateItemsAndFoldersTests extends ContextAwareWebTest
{
    @Autowired
    DocumentLibraryPage documentLibraryPage;

    private String testUser = "user" + DataUtil.getUniqueIdentifier();
    private String siteName = "siteName" + DataUtil.getUniqueIdentifier();
    private String folderName = "locateFolder" + DataUtil.getUniqueIdentifier();
    private String docName = "locateDoc" + DataUtil.getUniqueIdentifier();

    @BeforeClass
    public void setupTest()
    {
        userService.create(adminUser, adminPassword, testUser, password, testUser + "@tests.com", "firstName", "lastName");
        siteService.create(testUser, password, domain, siteName, siteName, Site.Visibility.PUBLIC);
        contentService.createDocument(testUser, password, siteName, CMISUtil.DocumentType.TEXT_PLAIN, docName, "Document content");
        contentService.createFolder(testUser, password, folderName, siteName);
        contentAction.setFolderAsFavorite(testUser, password, siteName, folderName);

        setupAuthenticatedSession(testUser, password);
    }

    @Bug(id = "MNT-17556")
    @TestRail(id = "C7516")
    @Test
    public void locateFileDetailedView()
    {
        documentLibraryPage.navigate(siteName);

        LOG.info("STEP 1: From 'Options' dropdown choose 'Detailed View' option");
        documentLibraryPage.selectViewFromOptionsMenu("Detailed View");

        LOG.info("STEP 2: Choose a view option from left side explorer pane -> 'Documents' section");
        documentLibraryPage.clickDocumentsFilterOption(DocumentLibraryPage.DocumentsFilters.RecentlyAdded.title);
        getBrowser().waitUntilElementIsDisplayedWithRetry(By.xpath("//div[contains(@class, 'message') and text()='Documents Added Recently']"));
        assertEquals(documentLibraryPage.getDocumentListHeader(), DocumentLibraryPage.DocumentsFilters.RecentlyAdded.header, "Header=");
        assertTrue(documentLibraryPage.isContentNameDisplayed(docName), docName + " is displayed in Recently added documents list.");

        LOG.info("STEP3: Hover over the file name and click 'Locate file' link from 'More' menu");
        documentLibraryPage.mouseOverFileName(docName);
        documentLibraryPage.clickDocumentLibraryItemAction(docName, "Locate File", documentLibraryPage);
        ArrayList<String> breadcrumbExpected = new ArrayList<>(Arrays.asList("Documents"));
        assertEquals(documentLibraryPage.getBreadcrumbList(), breadcrumbExpected.toString(), "Breadcrumb=");
        assertTrue(documentLibraryPage.isContentNameDisplayed(docName), "User is redirected to location of the created document.");
        assertTrue(documentLibraryPage.isContentSelected(docName), docName + " is selected.");
        assertFalse(documentLibraryPage.isContentSelected(folderName), folderName + " is selected.");
    }

    @Bug(id = "MNT-17556")
    @TestRail(id = "C7517")
    @Test
    public void locateFolderDetailedView()
    {
        documentLibraryPage.navigate(siteName);

        LOG.info("STEP 1: From 'Options' dropdown choose 'Detailed View' option");
        documentLibraryPage.selectViewFromOptionsMenu("Detailed View");

        LOG.info("STEP 2: Choose a view option from left side explorer pane -> 'Documents' section");
        documentLibraryPage.clickDocumentsFilterOption(DocumentLibraryPage.DocumentsFilters.Favorites.title);
        assertEquals(documentLibraryPage.getDocumentListHeader(), DocumentLibraryPage.DocumentsFilters.Favorites.header,
                "My Favorites documents are displayed.");

        LOG.info("STEP3: Hover over the folder name and click 'Locate folder' link from 'More' menu");
        documentLibraryPage.mouseOverContentItem(folderName);
        documentLibraryPage.clickDocumentLibraryItemAction(folderName, "Locate Folder", documentLibraryPage);
        ArrayList<String> breadcrumbExpected = new ArrayList<>(Arrays.asList("Documents"));
        assertEquals(documentLibraryPage.getBreadcrumbList(), breadcrumbExpected.toString(), "Breadcrumb=");
        assertTrue(documentLibraryPage.isContentNameDisplayed(folderName), "User is redirected to location of the created folder.");
        assertTrue(documentLibraryPage.isContentSelected(folderName), folderName + " is selected.");
        assertFalse(documentLibraryPage.isContentSelected(docName), docName + " is selected.");
    }
}
