package org.alfresco.share.userRolesAndPermissions.consumer;

import org.alfresco.po.share.alfrescoContent.pageCommon.DocumentsFilters;
import org.alfresco.po.share.site.DocumentLibraryPage;
import org.alfresco.share.ContextAwareWebTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.constants.UserRole;
import org.alfresco.utility.data.RandomData;
import org.alfresco.utility.model.TestGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.alfresco.dataprep.SiteService;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.Arrays;
import static org.alfresco.utility.constants.UserRole.SiteConsumer;
import static org.testng.Assert.*;

/**
 * @author Laura.Capsa
 */
public class ConsumerFoldersOnlyTests extends ContextAwareWebTest
{
    @Autowired
    private DocumentLibraryPage documentLibraryPage;

    @Autowired
    private DocumentsFilters documentsFilters;

    private final String uniqueId = RandomData.getRandomAlphanumeric();
    private final String user = "Consumer-" + uniqueId;
    private final String site = "site-" + uniqueId;
    private final String name = "name";
    private final String siteDescription = "Site Description";
    private final String folderName = "Folder-" + uniqueId;
    private final String subFolderName = "subFolder-" + uniqueId;
    private final String path = "Sites/" + site + "/documentLibrary/" + folderName;
    private final String tag = "tag-" + uniqueId.toLowerCase();

    @BeforeClass(alwaysRun = true)
    public void setupTest()
    {
        userService.create(adminUser, adminPassword, user, password, domain, name, user);
        siteService.create(adminUser, adminPassword, domain, site, siteDescription, SiteService.Visibility.PUBLIC);
        userService.createSiteMember(adminUser, adminPassword, user, site, UserRole.SiteConsumer.toString());
        contentService.createFolder(adminUser, adminPassword, folderName, site);
        contentService.createFolderInRepository(adminUser, adminPassword, subFolderName, path);
        contentAction.addSingleTag(adminUser, adminPassword, path + "/" + subFolderName, tag);

        setupAuthenticatedSession(user, password);
        assertEquals(documentLibraryPage.getPageTitle(), "Alfresco » User Dashboard", "Displayed page=");
    }

    @AfterClass(alwaysRun = true)
    public void cleanup()
    {
        userService.delete(adminUser,adminPassword, user);
        contentService.deleteTreeByPath(adminUser, adminPassword, "/User Homes/" + user);
        siteService.delete(adminUser,adminPassword,site );
    }

    @TestRail(id = "C8867")
    @Test(groups = { TestGroup.SANITY, TestGroup.USER})
    public void locateFolder()
    {
        documentLibraryPage.navigate(site);
        assertEquals(documentLibraryPage.getPageTitle(), "Alfresco » Document Library", "Displayed page= ");

        LOG.info("STEP1: From \"Document View\" left side panel, click 'My Favorites'");
        documentsFilters.clickSidebarTag(tag);
        assertTrue(documentLibraryPage.isContentNameDisplayed(subFolderName), subFolderName + " is displayed in 'My Favorites'.");

        LOG.info("STEP2: Click 'More' menu for " + subFolderName + ", and verify presence of \"Locate Folder\" option");
        assertTrue(documentLibraryPage.isActionAvailableForLibraryItem(subFolderName, language.translate("documentLibrary.contentActions.locateFolder")),
                "'Locate Folder' option is displayed for " + subFolderName);

        LOG.info("STEP3: Click \"Locate Folder\" option");
        documentLibraryPage.clickOnAction(subFolderName, language.translate("documentLibrary.contentActions.locateFolder"));
        assertEquals(documentLibraryPage.getBreadcrumbList(), Arrays.asList("Documents", folderName).toString(), "Breadcrumb=");
    }

    @TestRail(id = "C8869")
    @Test(groups = { TestGroup.SANITY, TestGroup.USER})
    public void manageRulesFolderCreatedByOther()
    {
        documentLibraryPage.navigate(site);
        documentLibraryPage.clickFolderFromExplorerPanel(folderName);
        LOG.info("STEP1: Mouse over folder and verify presence of \"Manage Rules\" option");
        assertFalse(documentLibraryPage.isActionAvailableForLibraryItem(subFolderName, language.translate("documentLibrary.contentActions.manageRules")),
                "'Manage Rules' option is displayed for " + subFolderName);
    }
}
