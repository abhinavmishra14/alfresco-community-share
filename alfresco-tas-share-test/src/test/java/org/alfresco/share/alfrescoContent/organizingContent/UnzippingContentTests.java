package org.alfresco.share.alfrescoContent.organizingContent;

import org.alfresco.po.share.site.DocumentLibraryPage2;
import org.alfresco.share.BaseTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.data.DataContent;
import org.alfresco.utility.model.*;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.File;

public class UnzippingContentTests extends BaseTest
{
    private String srcRoot = System.getProperty("user.dir") + File.separator;
    private String testDataFolder = srcRoot + "testdata" + File.separator;

    private final String zipFileName = "archiveC7409.zip";
    private final String acpFileName = "archiveC7410.acp";
    private final File zipFile = new File(testDataFolder.concat(zipFileName));
    private final File acpFile = new File(testDataFolder.concat(acpFileName));

    private UserModel user;
    private SiteModel site;

    private DocumentLibraryPage2 documentLibraryPage;

    @Autowired
    protected DataContent dataContent;

    @BeforeClass (alwaysRun = true)
    public void dataPrep()
    {
        user = dataUser.usingAdmin().createRandomTestUser();
        site = dataSite.usingUser(user).createPublicRandomSite();
    }

    @BeforeMethod(alwaysRun = true)
    public void setupTest()
    {
        documentLibraryPage = new DocumentLibraryPage2(browser);
        cmisApi.authenticateUser(user);
        setupAuthenticatedSession(user);
    }

    @AfterClass (alwaysRun = true)
    public void cleanup()
    {
        removeUserFromAlfresco(user);
        deleteSites(site);
    }

    @TestRail (id = "C7409")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT })
    public void verifyUnzipFileToDocumentLibraryOfTheSameSite()
    {
        FileModel zipFileModel = dataContent.usingUser(user)
            .usingSite(site).uploadDocument(zipFile);
        documentLibraryPage.navigate(site)
            .usingContent(zipFileModel)
            .selectFile()
            .clickUnzipTo()
            .selectRecentSitesDestination()
            .selectSite(site)
            .clickUnzipButton();
        FolderModel unzipFolder = new FolderModel(FilenameUtils.getBaseName(zipFileModel.getName()));
        FileModel unzipFileName = new FileModel("fileC7409");
        documentLibraryPage.navigate(site)
            .usingContent(unzipFolder).assertContentIsDisplayed()
            .selectFolder()
                .usingContent(unzipFileName).assertContentIsDisplayed();
    }

    @TestRail (id = "C7410")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT })
    public void verifyUnzipAcpFileToDocumentLibraryOfTheSameSite()
    {
        FileModel acpFileModel = dataContent.usingUser(user)
            .usingSite(site)
            .uploadDocument(acpFile);
        documentLibraryPage.navigate(site)
            .usingContent(acpFileModel)
            .selectFile()
            .clickUnzipTo()
            .selectRecentSitesDestination()
            .selectSite(site)
            .clickUnzipButton();
        FolderModel unzipFolder = new FolderModel(FilenameUtils.getBaseName(acpFileModel.getName()));
        FileModel unzipFileName = new FileModel("fileC7410");
        documentLibraryPage.navigate(site)
            .usingContent(unzipFolder).assertContentIsDisplayed()
            .selectFolder()
            .usingContent(unzipFileName).assertContentIsDisplayed();
    }

    @TestRail (id = "C202869")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT })
    public void verifyCancelUnzipAcpFile()
    {
        FolderModel folder = FolderModel.getRandomFolderModel();
        cmisApi.usingSite(site).createFolder(folder);
        FileModel acpFileModel = dataContent.usingUser(user)
            .usingResource(folder)
            .uploadDocument(acpFile);
        documentLibraryPage.navigate(site)
            .usingContent(folder).selectFolder()
            .usingContent(acpFileModel)
            .selectFile()
            .clickUnzipTo()
            .selectRecentSitesDestination()
            .selectSite(site)
            .selectFolder(folder)
            .clickCancelButton();
        FolderModel unzipFolder = new FolderModel(FilenameUtils.getBaseName(acpFileModel.getName()));
        documentLibraryPage.navigate(site)
            .usingContent(folder).selectFolder()
            .usingContent(unzipFolder).assertContentIsNotDisplayed();
    }
}
