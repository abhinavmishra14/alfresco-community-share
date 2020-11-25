package org.alfresco.share.alfrescoContent.organizingContent;

import org.alfresco.po.share.site.DocumentLibraryPage2;
import org.alfresco.share.BaseTests;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.model.*;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class RenamingContentTests extends BaseTests
{
    private DocumentLibraryPage2 documentLibraryPage;

    private UserModel user;
    private SiteModel site;

    @BeforeClass (alwaysRun = true)
    public void dataPrep()
    {
        user = dataUser.usingAdmin().createRandomTestUser();
        site = dataSite.usingUser(user).createPublicRandomSite();
        cmisApi.authenticateUser(user);
    }

    @BeforeMethod(alwaysRun = true)
    public void setupTest()
    {
        documentLibraryPage = new DocumentLibraryPage2(browser);
        setupAuthenticatedSession(user);
    }

    @TestRail (id = "C7419")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT })
    public void renameFileByEditIcon()
    {
        FileModel fileToRename = FileModel.getRandomFileModel(FileType.TEXT_PLAIN, FILE_CONTENT);
        FileModel newFile = FileModel.getRandomFileModel(FileType.TEXT_PLAIN, "");
        cmisApi.usingSite(site).createFile(fileToRename);

        documentLibraryPage.navigate(site)
            .usingContent(fileToRename)
            .clickRenameIcon()
            .typeNewName(newFile.getName())
            .clickSave();
        documentLibraryPage.usingContent(fileToRename).assertContentIsNotDisplayed();
        documentLibraryPage.usingContent(newFile).assertContentIsDisplayed();
    }

    @TestRail (id = "C7420")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT })
    public void renameFolderByEditIcon()
    {
        FolderModel folderToRename = FolderModel.getRandomFolderModel();
        FolderModel newFolder = FolderModel.getRandomFolderModel();
        cmisApi.usingSite(site).createFolder(folderToRename);

        documentLibraryPage.navigate(site)
            .usingContent(folderToRename)
            .clickRenameIcon()
            .typeNewName(newFolder.getName())
            .clickSave();
        documentLibraryPage.usingContent(folderToRename).assertContentIsNotDisplayed();
        documentLibraryPage.usingContent(newFolder).assertContentIsDisplayed();
    }

    @TestRail (id = "C7431")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT })
    public void cancelRenamingContent()
    {
        FileModel fileToRename = FileModel.getRandomFileModel(FileType.TEXT_PLAIN, FILE_CONTENT);
        FileModel newFile = FileModel.getRandomFileModel(FileType.TEXT_PLAIN, "");
        cmisApi.usingSite(site).createFile(fileToRename);

        documentLibraryPage.navigate(site)
            .usingContent(fileToRename)
            .clickRenameIcon()
            .typeNewName(newFile.getName())
            .clickCancel();
        documentLibraryPage.usingContent(fileToRename).assertContentIsDisplayed();
        documentLibraryPage.usingContent(newFile).assertContentIsNotDisplayed();
    }

    @AfterClass (alwaysRun = true)
    public void cleanup()
    {
        removeUserFromAlfresco(user);
        deleteSites(site);
    }
}