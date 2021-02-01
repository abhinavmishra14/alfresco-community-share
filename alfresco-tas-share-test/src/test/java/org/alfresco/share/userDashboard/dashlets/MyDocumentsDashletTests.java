package org.alfresco.share.userDashboard.dashlets;

import static org.alfresco.share.TestUtils.FILE_CONTENT;

import org.alfresco.po.share.dashlet.MyDocumentsDashlet;
import org.alfresco.po.share.dashlet.MyDocumentsDashlet.DocumentsFilter;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.model.*;
import org.testng.annotations.*;

public class MyDocumentsDashletTests extends AbstractUserDashboardDashletsTests
{
    private MyDocumentsDashlet myDocumentsDashlet;

    private final ThreadLocal<UserModel> user = new ThreadLocal<>();
    private final ThreadLocal<SiteModel> site = new ThreadLocal<>();

    @BeforeMethod(alwaysRun = true)
    public void setupTest()
    {
        myDocumentsDashlet = new MyDocumentsDashlet(webDriver);

        user.set(dataUser.usingAdmin().createRandomTestUser());
        site.set(dataSite.usingUser(user.get()).createPublicRandomSite());
        setupAuthenticatedSession(user.get());
    }

    @TestRail (id = "C2134")
    @Test (groups = { TestGroup.SANITY, TestGroup.USER_DASHBOARD })
    public void checkDetailedView()
    {
        FileModel testFile = FileModel.getRandomFileModel(FileType.TEXT_PLAIN, FILE_CONTENT);
        getCmisApi().authenticateUser(user.get())
            .usingSite(site.get()).createFile(testFile);

        userDashboardPage.navigate(user.get());
        myDocumentsDashlet.usingDocument(testFile)
            .assertFileIsDisplayed()
            .assertSmallIconThumbnailIsDisplayed();
        myDocumentsDashlet.selectDetailedView();
        myDocumentsDashlet.usingDocument(testFile)
            .assertCommentLinkIsDisplayed()
            .assertLikeIsDisplayed()
            .assertNumberOfLikesIs(0)
            .like()
            .assertUnlikeIsDisplayed()
            .assertNumberOfLikesIs(1)
            .assertAddToFavoriteIsDisplayed()
            .addToFavorite()
                .assertRemoveFromFavoriteIsDisplayed()
            .removeFromFavorite()
                .assertAddToFavoriteIsDisplayed()
            .assertNoDescriptionIsDisplayed()
            .assertVersionIs(1.0)
            .assertThumbnailIsDisplayed()
            .selectDocument().assertDocumentTitleEquals(testFile);
        userDashboardPage.navigate(user.get());
        myDocumentsDashlet.usingDocument(testFile)
            .clickThumbnail().assertDocumentTitleEquals(testFile);
        userDashboardPage.navigate(user.get());
        myDocumentsDashlet.usingDocument(testFile)
            .addComment().assertCommentsAreaIsOpened();
    }

    @TestRail (id = "C2138")
    @Test (groups = { TestGroup.SANITY, TestGroup.USER_DASHBOARD })
    public void filterDocuments() throws Exception
    {
        FileModel file1 = FileModel.getRandomFileModel(FileType.TEXT_PLAIN, FILE_CONTENT);
        FileModel file2 = FileModel.getRandomFileModel(FileType.HTML, FILE_CONTENT);
        FileModel file3 = FileModel.getRandomFileModel(FileType.TEXT_PLAIN, FILE_CONTENT);

        getCmisApi().authenticateUser(user.get())
            .usingSite(site.get())
                .createFile(file1).createFile(file2).createFile(file3)
                    .then().usingResource(file1)
                        .checkOut()
                            .assertThat().documentIsCheckedOut();
        getRestApi().authenticateUser(user.get())
            .withCoreAPI().usingAuthUser().addFileToFavorites(file2);

        userDashboardPage.navigate(user.get());
        myDocumentsDashlet.assertSelectedFilterIs(DocumentsFilter.RECENTLY_MODIFIED)
            .usingDocument(file1).assertFileIsDisplayed();
        myDocumentsDashlet.usingDocument(file2).assertFileIsDisplayed();
        myDocumentsDashlet.usingDocument(file3).assertFileIsDisplayed();
        myDocumentsDashlet.filter(DocumentsFilter.EDITING)
            .assertNrOfDisplayedDocumentsIs(1)
                .usingDocument(file1).assertFileIsDisplayed();
        myDocumentsDashlet.usingDocument(file2).assertFileIsNotDisplayed();
        myDocumentsDashlet.usingDocument(file3).assertFileIsNotDisplayed();

        myDocumentsDashlet.filter(DocumentsFilter.MY_FAVORITES)
            .usingDocument(file2).assertFileIsDisplayed();
        myDocumentsDashlet.usingDocument(file1).assertFileIsNotDisplayed();
        myDocumentsDashlet.usingDocument(file3).assertFileIsNotDisplayed();
    }

    @AfterMethod(alwaysRun = true)
    public void cleanup()
    {
        deleteUsersIfNotNull(user.get());
        deleteSitesIfNotNull(site.get());
    }
}