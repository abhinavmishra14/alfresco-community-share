package org.alfresco.share.adminTools.users;

import org.alfresco.common.DataUtil;
import org.alfresco.po.share.user.admin.adminTools.AdminToolsPage;
import org.alfresco.po.share.user.admin.adminTools.usersAndGroups.UploadResults;
import org.alfresco.po.share.user.admin.adminTools.usersAndGroups.UsersPage;
import org.alfresco.share.ContextAwareWebTest;
import org.alfresco.testrail.TestRail;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.Test;

import static org.testng.Assert.assertTrue;

public class ImportUsersTests extends ContextAwareWebTest
{
    @Autowired
    AdminToolsPage adminTools;

    @Autowired
    UsersPage usersPage;

    @Autowired
    UploadResults uploadResults;

    @TestRail(id = "C9438")
    @Test
    public void importUsers()
    {

        String userName = "testUser" + DataUtil.getUniqueIdentifier();
        String groupName = "ALFRESCO_ADMINISTRATORS";
        String file = "C9438.csv";
        String filePath = testDataFolder + file;
        String importedUser = "C9438user";

        logger.info("Preconditions: User with administrator rights is created and logged into Share.");
        userService.create(adminUser, adminPassword, userName, password, "@tests.com", userName, userName);
        groupService.addUserToGroup(adminUser, adminPassword, groupName, userName);
        setupAuthenticatedSession(userName, password);

        logger.info("Step1: Navigate to 'Admin Tools' and click 'Users' link on 'Tools' pane.");
        adminTools.navigateToNodeFromToolsPanel("Users", usersPage);

        logger.info("Step2: Click 'Upload User CSV File' button. Select users CSV file and click 'Upload File'.");
        usersPage.uploadUsers(filePath, "bla");
        uploadResults.renderedPage();

        logger.info("Step3: Click 'Go Back' button from 'Upload Users' page.");
        uploadResults.clickGoBack();
        usersPage.renderedPage();

        logger.info("Step4: Search for the imported user.");
        usersPage.searchUser(importedUser);
        assertTrue(usersPage.verifyUserIsFound(importedUser), "User " + importedUser + " found");

    }

}