package org.alfresco.share.alfrescoContent.workingWithFilesOutsideTheLibrary.myFiles;

import org.alfresco.common.DataUtil;
import org.alfresco.po.share.MyFilesPage;
import org.alfresco.po.share.alfrescoContent.organizingContent.CopyMoveUnzipToDialog;
import org.alfresco.po.share.alfrescoContent.document.DocumentDetailsPage;
import org.alfresco.po.share.alfrescoContent.document.UploadContent;
import org.alfresco.po.share.site.SiteDashboardPage;
import org.alfresco.share.ContextAwareWebTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.model.TestGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.Collections;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

/**
 * @author Razvan.Dorobantu
 */
public class MyFilesUnzipContentTests extends ContextAwareWebTest
{
    @Autowired private MyFilesPage myFilesPage;

    @Autowired private DocumentDetailsPage documentDetailsPage;

    @Autowired private CopyMoveUnzipToDialog unzipToDialog;

    @Autowired private UploadContent uploadContent;

    @Autowired private SiteDashboardPage sitePage;

    private final String zipFile = "archiveC7816.zip";
    private final String zipContent = "TestFileC7816";
    private final String zipFilePath = testDataFolder + zipFile;
    private final String acpFile = "archiveC7816.acp";
    private final String acpPath = testDataFolder + acpFile;

    @TestRail(id = "C7816")
    @Test(groups = { TestGroup.SANITY, TestGroup.ALFRESCO_CONTENT})
    public void unzipZipArchiveToMyFiles()
    {
        String user = "user" + DataUtil.getUniqueIdentifier();
        userService.create(adminUser, adminPassword, user, password, user + "@tests.com", user, user);

        LOG.info("Precondition: Login as user, navigate to My Files page and upload a zip archive.");
        setupAuthenticatedSession(user, password);
        sitePage.clickMyFilesLink();
        Assert.assertEquals(myFilesPage.getPageTitle(), "Alfresco » My Files");
        uploadContent.uploadContent(zipFilePath);

        LOG.info("STEP1: Click archive name, e.g: testArchive");
        myFilesPage.clickOnFile(zipFile);
        assertTrue(documentDetailsPage.getFileName().equals(zipFile), "Wrong file name!");

        LOG.info("STEP2: Click 'Unzip to...' link from 'Documents Actions'");
        documentDetailsPage.clickDocumentActionsOption("Unzip to...");
        assertEquals(unzipToDialog.getDialogTitle(), "Unzip " + zipFile + " to...", "'Unzip to....' dialog is displayed");

        LOG.info("STEP3: Select option My Files from 'Destination' section");
        unzipToDialog.clickDestinationButton("My Files");
        ArrayList expectedDestionationPath = new ArrayList(Collections.singletonList("My Files"));
        assertEquals(unzipToDialog.getPathList(), expectedDestionationPath.toString(), "Destionation set to=");

        LOG.info("STEP4: Click 'Unzip' button and navigate to My Files");
        unzipToDialog.clickButtton(language.translate("documentLibrary.contentActions.unzip"));
        sitePage.clickMyFilesLink();
        assertTrue(myFilesPage.isContentNameDisplayed(zipContent), zipFile + "'s content is displayed, " + zipContent);
    }

    @TestRail(id = "C7817")
    @Test(groups = { TestGroup.SANITY, TestGroup.ALFRESCO_CONTENT})
    public void unzipACPArchiveToMyFiles()
    {
        String user = "user" + DataUtil.getUniqueIdentifier();
        userService.create(adminUser, adminPassword, user, password, user + "@tests.com", user, user);

        LOG.info("Precondition: Login as user, navigate to My Files page and upload a zip archive.");
        setupAuthenticatedSession(user, password);
        sitePage.clickMyFilesLink();
        Assert.assertEquals(myFilesPage.getPageTitle(), "Alfresco » My Files");
        uploadContent.uploadContent(acpPath);

        LOG.info("STEP1: Click archive name, e.g: testArchive");
        myFilesPage.clickOnFile(acpFile);
        assertTrue(documentDetailsPage.getFileName().equals(acpFile), "Wrong file name!");

        LOG.info("STEP2: Click 'Unzip to...' link from 'Documents Actions'");
        documentDetailsPage.clickDocumentActionsOption("Unzip to...");
        assertEquals(unzipToDialog.getDialogTitle(), "Unzip " + acpFile + " to...", "'Unzip to....' dialog is displayed");

        LOG.info("STEP3: Select option My Files from 'Destination' section");
        unzipToDialog.clickDestinationButton("My Files");
        ArrayList expectedDestionationPath = new ArrayList(Collections.singletonList("My Files"));
        assertEquals(unzipToDialog.getPathList(), expectedDestionationPath.toString(), "Destionation set to=");

        LOG.info("STEP4: Click 'Unzip' button and navigate to My Files");
        unzipToDialog.clickButtton(language.translate("documentLibrary.contentActions.unzip"));
        sitePage.clickMyFilesLink();
        assertTrue(myFilesPage.isContentNameDisplayed(acpFile.substring(0, acpFile.indexOf("."))),
                "A folder with archive name is present in Documents list.");
    }
}
