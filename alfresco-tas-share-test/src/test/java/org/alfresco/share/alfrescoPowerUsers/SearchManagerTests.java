package org.alfresco.share.alfrescoPowerUsers;

import org.alfresco.share.ContextAwareWebTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.data.RandomData;
import org.alfresco.utility.model.TestGroup;
import org.alfresco.utility.model.UserModel;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class SearchManagerTests extends ContextAwareWebTest
{
    private UserModel userAdmin, searchAdmin;

    @BeforeClass (alwaysRun = true)
    public void setupTest()
    {
        userAdmin = dataUser.usingAdmin().createRandomTestUser();
        searchAdmin = dataUser.createRandomTestUser();
    }

    @AfterClass (alwaysRun = true)
    public void cleanup()
    {
        removeUserFromAlfresco(userAdmin, searchAdmin);
    }

    @TestRail (id = "C8703")
    @Test (groups = { TestGroup.SANITY, TestGroup.USER })
    public void userHasSearchManagerRightsWhenAddedToAdminGroup()
    {
        setupAuthenticatedSession(userAdmin);
        toolbar.search("test").assertSearchManagerButtonIsNotDisplayed();
        dataGroup.usingUser(userAdmin).addUserToGroup(ALFRESCO_ADMIN_GROUP);
        setupAuthenticatedSession(userAdmin);
        toolbar.search("test").assertSearchManagerButtonIsDisplayed()
            .clickSearchManagerLink()
                .assertBrowserPageTitleIs(language.translate("searchManager.browser.pageTitle"))
                .assertSearchManagerPageIsOpened();
    }

    @TestRail (id = "C8704, C8713")
    @Test (groups = { TestGroup.SANITY, TestGroup.USER })
    public void userHasSearchManagerRightsWhenAddedToSearchAdministratorsGroup()
    {
        String filterId = RandomData.getRandomAlphanumeric();
        setupAuthenticatedSession(searchAdmin);
        toolbar.search("test").assertSearchManagerButtonIsNotDisplayed();
        dataGroup.usingUser(searchAdmin).addUserToGroup(ALFRESCO_SEARCH_ADMINISTRATORS);
        setupAuthenticatedSession(searchAdmin);
        toolbar.search("test").assertSearchManagerButtonIsDisplayed()
            .clickSearchManagerLink()
                .assertBrowserPageTitleIs(language.translate("searchManager.browser.pageTitle"))
                .assertSearchManagerPageIsOpened()
                .createNewFilter()
                    .typeFilterId(filterId)
                    .typeFilterName(filterId)
                    .clickSave()
                        .editFilterProperty(filterId, "audio:album (Album)")
                        .assertFilterPropertyIs(filterId, "audio:album (Album)")
                        .deleteFilter(filterId).assertFilterIsNotDisplayed(filterId);
    }
}