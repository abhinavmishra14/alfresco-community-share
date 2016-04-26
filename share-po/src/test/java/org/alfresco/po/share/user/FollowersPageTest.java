
package org.alfresco.po.share.user;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.fail;

import java.util.List;

import org.alfresco.po.AbstractTest;
import org.alfresco.po.share.DashBoardPage;
import org.alfresco.po.share.PeopleFinderPage;
import org.alfresco.po.share.ShareLink;
import org.alfresco.po.share.SharePage;

import org.alfresco.test.FailedTestListener;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

/**
 * Integration test to verify Following Me page elements are in place.
 * Created by Olga Lokhach
 */
@Listeners(FailedTestListener.class)
public class FollowersPageTest extends AbstractTest
{
    private DashBoardPage dashBoard;
    private PeopleFinderPage peopleFinderPage;
    private MyProfilePage myProfilePage;
    private FollowersPage followersPage;
    private String userName1;
    private String userName2;


    @BeforeClass(groups = { "Enterprise-only" }, alwaysRun = true)
    public void setup() throws Exception
    {
        userName1 = "User_1_" + System.currentTimeMillis();
        userName2 = "User_2_" + System.currentTimeMillis();
        createEnterpriseUser(userName1);
        createEnterpriseUser(userName2);
        shareUtil.loginAs(driver, shareUrl, userName1, UNAME_PASSWORD).render();
    }

    @Test(groups = { "Enterprise-only"})
    public void openFollowersPage()
    {
        SharePage page = resolvePage(driver).render();
        dashBoard = page.getNav().selectMyDashBoard().render();
        myProfilePage = dashBoard.getNav().selectMyProfile().render();
        followersPage = myProfilePage.getProfileNav().selectFollowers().render();
        assertNotNull(followersPage);
    }

    @Test(groups="Enterprise-only", dependsOnMethods = "openFollowersPage")
    public void isHeaderTitlePresent() throws Exception
    {
        SharePage page = resolvePage(driver).render();
        dashBoard = page.getNav().selectMyDashBoard().render();
        myProfilePage = dashBoard.getNav().selectMyProfile().render();
        followersPage = myProfilePage.getProfileNav().selectFollowers().render();
        assertTrue(followersPage.isTitlePresent("Followers"), "Title is incorrect");
    }

    @Test(groups="Enterprise-only", dependsOnMethods = "isHeaderTitlePresent")
    public void isNoFollowersMessagePresent() throws Exception
    {
        SharePage page = resolvePage(driver).render();
        dashBoard = page.getNav().selectMyDashBoard().render();
        myProfilePage = dashBoard.getNav().selectMyProfile().render();
        followersPage = myProfilePage.getProfileNav().selectFollowers().render();
        assertTrue(followersPage.isNoFollowersMessagePresent(), "No Followers message isn't displayed");
        assertEquals(followersPage.getFollowersCount(), "0");
        shareUtil.logout(driver);
    }

    @Test(groups="Enterprise-only", dependsOnMethods = "isNoFollowersMessagePresent")
    public void isUserLinkPresent() throws Exception
    {
        shareUtil.loginAs(driver, shareUrl, userName2, UNAME_PASSWORD).render();
        SharePage page = resolvePage(driver).render();
        dashBoard = page.getNav().selectMyDashBoard().render();
        peopleFinderPage = dashBoard.getNav().selectPeople().render();
        peopleFinderPage = peopleFinderPage.searchFor(userName1).render();
        List<ShareLink> searchLinks = peopleFinderPage.getResults();
        if (!searchLinks.isEmpty())
        {
            for (ShareLink result : searchLinks)
            {
                if (result.getDescription().contains(userName1))
                {
                    peopleFinderPage.selectFollowForUser(userName1);
                }
            }
        }
        else
        {
            fail(userName1 + " is not found");
        }
        assertEquals(peopleFinderPage.getTextForFollowButton(userName1), "Unfollow");
        shareUtil.logout(driver);
        shareUtil.loginAs(driver, shareUrl, userName1, UNAME_PASSWORD).render();
        page = resolvePage(driver).render();
        dashBoard = page.getNav().selectMyDashBoard().render();
        myProfilePage = dashBoard.getNav().selectMyProfile().render();
        followersPage = myProfilePage.getProfileNav().selectFollowers().render();
        assertTrue(followersPage.isUserLinkPresent(userName2), "Can't find " + userName2);
        assertEquals(followersPage.getFollowersCount(), "1");
    }
}

