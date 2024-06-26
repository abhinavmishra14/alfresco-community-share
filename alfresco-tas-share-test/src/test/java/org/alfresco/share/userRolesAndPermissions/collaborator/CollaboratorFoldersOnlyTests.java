package org.alfresco.share.userRolesAndPermissions.collaborator;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import java.util.Arrays;

import org.alfresco.dataprep.SiteService;
import org.alfresco.po.share.alfrescoContent.applyingRulesToFolders.ManageRulesPage;
import org.alfresco.po.share.alfrescoContent.buildingContent.NewFolderDialog;
import org.alfresco.po.share.alfrescoContent.pageCommon.DocumentsFilters;
import org.alfresco.po.share.site.DocumentLibraryPage;
import org.alfresco.po.share.site.ItemActions;
import org.alfresco.share.ContextAwareWebTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.data.RandomData;
import org.alfresco.utility.model.TestGroup;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * @author Laura.Capsa
 */
public class CollaboratorFoldersOnlyTests extends ContextAwareWebTest
{
    private final String uniqueId = RandomData.getRandomAlphanumeric();
    private final String user = "Collaborator-" + uniqueId;
    private final String site = "site-" + uniqueId;
    private final String name = "name";
    private final String description = "Description";
    private final String folderName = "Folder-" + uniqueId;
    private final String folderName2 = "Folder2-" + uniqueId;
    private final String folderName3 = "Folder3-" + uniqueId;
    private final String subFolderName = "subFolder-" + uniqueId;
    private final String path = "Sites/" + site + "/documentLibrary/" + folderName;
    private final String tag = "tag-" + uniqueId.toLowerCase();
    private final String title = "Title-" + uniqueId;
   // @Autowired
    private DocumentsFilters documentsFilters;
    //@Autowired
    private DocumentLibraryPage documentLibraryPage;
    //@Autowired
    private NewFolderDialog newFolderDialog;
    //@Autowired
    private ManageRulesPage manageRulesPage;

    @BeforeClass (alwaysRun = true)
    public void setupTest()
    {
        userService.create(adminUser, adminPassword, user, password, domain, name, user);
        siteService.create(adminUser, adminPassword, domain, site, description, SiteService.Visibility.PUBLIC);
        userService.createSiteMember(adminUser, adminPassword, user, site, "SiteCollaborator");
        contentService.createFolder(adminUser, adminPassword, folderName, site);
        contentService.createFolder(user, password, folderName3, site);
        contentService.createFolderInRepository(adminUser, adminPassword, subFolderName, path);
        contentAction.addSingleTag(adminUser, adminPassword, path + "/" + subFolderName, tag);

        setupAuthenticatedSession(user, password);
//        assertEquals(documentLibraryPage.getPageTitle(), "Alfresco » User Dashboard", "Displayed page=");
    }

    @AfterClass (alwaysRun = true)
    public void cleanup()
    {
        userService.delete(adminUser, adminPassword, user);
        contentService.deleteTreeByPath(adminUser, adminPassword, "/User Homes/" + user);
        siteService.delete(adminUser, adminPassword, site);
    }

    @TestRail (id = "C8874")
    @Test (groups = { TestGroup.SANITY, TestGroup.USER_ROLES })
    public void collaboratorCreateFolder()
    {
        documentLibraryPage.navigate(site);
//        assertEquals(documentLibraryPage.getPageTitle(), "Alfresco » Document Library", "Displayed page=");

        LOG.info("STEP1: Click on 'Create' button");
        documentLibraryPage.clickCreateButton();

        LOG.info("STEP2: Select 'Folder' option");
        documentLibraryPage.clickFolderLink();

        LOG.info("STEP3: Set input for name, title, description and click on Save button");
        newFolderDialog.fillInDetails(folderName2, title, description);
        newFolderDialog.clickSave();
        assertTrue(documentLibraryPage.isContentNameDisplayed(folderName2), String.format("Folder [%s] is displayed in Document Library.", folderName2));
    }

    @TestRail (id = "C8875")
    @Test (groups = { TestGroup.SANITY, TestGroup.USER_ROLES, "tobefixed" })
    public void locateFolder()
    {
        documentLibraryPage.navigate(site);
//        assertEquals(documentLibraryPage.getPageTitle(), "Alfresco » Document Library", "Displayed page= ");

        LOG.info("STEP1: From \"Document View\" left side panel, click 'My Favorites'");
        documentsFilters.clickSidebarTag(tag);
        assertTrue(documentLibraryPage.isContentNameDisplayed(subFolderName), subFolderName + " is displayed in 'My Favorites'.");

        LOG.info("STEP2: Click 'More' menu for " + subFolderName + ", and verify presence of \"Locate Folder\" option");
        assertTrue(documentLibraryPage.isActionAvailableForLibraryItem(subFolderName, ItemActions.LOCATE_FOLDER),
            "'Locate Folder' option is displayed for " + subFolderName);

        LOG.info("STEP3: Click \"Locate Folder\" option");
        documentLibraryPage.selectItemAction(subFolderName, ItemActions.LOCATE_FOLDER);
        assertEquals(documentLibraryPage.getBreadcrumbList(), Arrays.asList("Documents", folderName).toString(), "Breadcrumb=");
    }

    @TestRail (id = "C8876")
    @Test (groups = { TestGroup.SANITY, TestGroup.USER_ROLES })
    public void manageRulesFolderSelfCreated()
    {
        documentLibraryPage.navigate(site);
//        assertEquals(documentLibraryPage.getPageTitle(), "Alfresco » Document Library", "Displayed page=");

        LOG.info("STEP1: Mouse over folder and verify presence of \"Manage Rules\" option");
        assertTrue(documentLibraryPage.isActionAvailableForLibraryItem(folderName3, ItemActions.MANAGE_RULES),
            "'Manage Rules' option is displayed for " + folderName3);

        LOG.info("STEP2: Click 'Manage Rules' option for " + folderName3);
        documentLibraryPage.selectItemAction(folderName3, ItemActions.MANAGE_RULES);
//        assertEquals(documentLibraryPage.getPageTitle(), "Alfresco » Folder Rules", "Displayed page=");
    }

    @TestRail (id = "C8877")
    @Test (groups = { TestGroup.SANITY, TestGroup.USER_ROLES })
    public void manageRulesFolderCreatedByOther()
    {
        documentLibraryPage.navigate(site);
        documentLibraryPage.clickFolderFromExplorerPanel(folderName);
        LOG.info("STEP1: Mouse over folder and verify presence of \"Manage Rules\" option");
        assertFalse(documentLibraryPage.isActionAvailableForLibraryItem(subFolderName, ItemActions.MANAGE_RULES),
            "'Manage Rules' option is displayed for " + subFolderName);
    }
}