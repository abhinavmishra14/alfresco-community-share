package org.alfresco.share.adminTools;

import org.alfresco.po.share.user.admin.adminTools.TagManagerPage;
import org.alfresco.rest.model.RestTagModelsCollection;
import org.alfresco.share.BaseTests;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.data.RandomData;
import org.alfresco.utility.model.FileModel;
import org.alfresco.utility.model.FileType;
import org.alfresco.utility.model.TestGroup;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class TagManagerTests extends BaseTests
{
    private TagManagerPage tagManagerPage;

    private ThreadLocal<String> tag = new ThreadLocal<>();
    private ThreadLocal<FileModel> tagFile = new ThreadLocal<>();

    @BeforeMethod(alwaysRun = true)
    public void setupTest() throws Exception
    {
        tagManagerPage = new TagManagerPage(browser);

        tagFile.set(FileModel.getRandomFileModel(FileType.TEXT_PLAIN, FILE_CONTENT));
        tag.set("tag" + RandomData.getRandomAlphanumeric().toLowerCase());

        getCmisApi().authenticateUser(getAdminUser()).usingShared().createFile(tagFile.get());
        getRestApi().authenticateUser(getAdminUser())
            .withCoreAPI().usingResource(tagFile.get()).addTags(tag.get());

        setupAuthenticatedSession(getAdminUser());
    }

    @AfterMethod(alwaysRun = true)
    public void cleanUp()
    {
        getCmisApi().authenticateUser(getAdminUser())
            .usingResource(tagFile.get()).delete();
    }

    @TestRail (id = "C9383")
    @Test (groups = { TestGroup.SANITY, TestGroup.ADMIN_TOOLS })
    public void renamingTag() throws Exception
    {
        String updatedTag = "tagupdate" + RandomData.getRandomAlphanumeric().toLowerCase();
        tagManagerPage.navigate();
        tagManagerPage.searchTagWithRetry(tag.get())
            .clickEdit(tag.get())
            .renameTag(updatedTag)
            .searchTagWithRetry(updatedTag)
            .assertTagIsDisplayed(updatedTag);
        RestTagModelsCollection tags = getRestApi().withCoreAPI().usingResource(tagFile.get()).getNodeTags();
        tags.assertThat()
            .entriesListContains("tag", updatedTag);
    }

    @TestRail (id = "C9385")
    @Test (groups = { TestGroup.SANITY, TestGroup.ADMIN_TOOLS })
    public void verifyTagManagerPage()
    {
        tagManagerPage.navigate();
        tagManagerPage.assertSearchButtonIsDisplayed()
            .assertSearchInputFieldDisplayed()
            .assertTableTitleIsCorrect()
            .searchTagWithRetry(tag.get())
                .assertTableHeadersAreCorrect()
                .clickEdit(tag.get())
                    .assertRenameTagLabelIsCorrect()
                    .assertOkButtonIsDisplayed()
                    .assertCancelButtonIsDisplayed()
                    .assertRequiredSymbolIsDisplayed();
    }

    @TestRail (id = "C9388")
    @Test (groups = { TestGroup.SANITY, TestGroup.ADMIN_TOOLS })
    public void deleteTag()
    {
        tagManagerPage.navigate();
        tagManagerPage.searchTagWithRetry(tag.get())
            .clickDelete(tag.get())
                .assertConfirmDeleteMessageForContentEqualsTo(tag.get())
                .assertDeleteButtonIsDisplayed()
                .assertCancelButtonIsDisplayed()
                .clickDelete();
        tagManagerPage.assertNoTagFoundMessageIsDisplayed()
            .search(tag.get())
            .assertTagIsNotDisplayed(tag.get());
    }

}