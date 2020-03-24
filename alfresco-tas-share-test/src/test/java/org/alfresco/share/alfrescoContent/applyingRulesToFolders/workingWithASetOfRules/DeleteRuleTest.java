package org.alfresco.share.alfrescoContent.applyingRulesToFolders.workingWithASetOfRules;

import static org.testng.Assert.assertEquals;

import java.util.Arrays;
import java.util.List;

import org.alfresco.dataprep.SiteService;
import org.alfresco.po.share.DeleteDialog;
import org.alfresco.po.share.alfrescoContent.SelectDestinationDialog;
import org.alfresco.po.share.alfrescoContent.applyingRulesToFolders.EditRulesPage;
import org.alfresco.po.share.alfrescoContent.applyingRulesToFolders.ManageRulesPage;
import org.alfresco.po.share.alfrescoContent.applyingRulesToFolders.RuleDetailsPage;
import org.alfresco.po.share.site.DocumentLibraryPage;
import org.alfresco.share.ContextAwareWebTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.data.RandomData;
import org.alfresco.utility.model.TestGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * @author Laura.Capsa
 */
public class DeleteRuleTest extends ContextAwareWebTest
{
    private final String random = RandomData.getRandomAlphanumeric();
    private final String userName = "user-" + random;
    private final String siteName = "Site-" + random;
    private final String description = "description-" + random;
    private final String path = "Documents";
    private final String ruleName = "rule-C7254-" + random;
    private final String folderName = "Folder-C7254-" + random;
    @Autowired
    private DocumentLibraryPage documentLibraryPage;
    @Autowired
    private ManageRulesPage manageRulesPage;
    @Autowired
    private EditRulesPage editRulesPage;
    @Autowired
    private RuleDetailsPage ruleDetailsPage;
    @Autowired
    private SelectDestinationDialog selectDestinationDialog;
    @Autowired
    private DeleteDialog deleteDialog;

    @BeforeClass (alwaysRun = true)
    public void setupTest()
    {
        userService.create(adminUser, adminPassword, userName, password, userName + domain, "First Name", "Last Name");
        siteService.create(userName, password, domain, siteName, description, SiteService.Visibility.PUBLIC);

        contentService.createFolder(userName, password, folderName, siteName);

        setupAuthenticatedSession(userName, password);
        documentLibraryPage.navigate(siteName);
        assertEquals(documentLibraryPage.getPageTitle(), "Alfresco » Document Library", "Displayed page:");

        LOG.info("Navigate to Manage Rule page for folder");
        documentLibraryPage.clickDocumentLibraryItemAction(folderName, language.translate("documentLibrary.contentActions.manageRules"), manageRulesPage);
        assertEquals(manageRulesPage.getPageTitle(), "Alfresco » Folder Rules", "Displayed page=");
        assertEquals(manageRulesPage.getRuleTitle(), folderName + ": Rules", "Rule title=");

        LOG.info("Navigate to Create rule page");
        manageRulesPage.clickCreateRules();
        editRulesPage.setCurrentSiteName(siteName);
        assertEquals(editRulesPage.getRelativePath(), "share/page/site/" + siteName + "/rule-edit", "Redirected to=");

        LOG.info("Fill in Create Rule details and submit form");
        List<Integer> indexOfOptionFromDropdown = Arrays.asList(0, 0, 2);
        editRulesPage.typeRuleDetails(ruleName, description, indexOfOptionFromDropdown);
        selectDestinationDialog.clickSite(siteName);
        selectDestinationDialog.clickPathFolder(path);
        selectDestinationDialog.clickOkButton();
        editRulesPage.renderedPage();
        editRulesPage.clickCreateButton();
        assertEquals(manageRulesPage.getPageTitle(), "Alfresco » Folder Rules", "Displayed page=");
        editRulesPage.cleanupSelectedValues();
    }

    @AfterClass (alwaysRun = true)
    public void cleanup()
    {
        userService.delete(adminUser, adminPassword, userName);
        contentService.deleteTreeByPath(adminUser, adminPassword, "/User Homes/" + userName);
        siteService.delete(adminUser, adminPassword, siteName);
    }

    @TestRail (id = "C7267")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT, "tobefixed" })
    public void deleteRule()
    {
        LOG.info("STEP1: Click 'Delete' button for rule");
        ruleDetailsPage.clickButton("delete");
        assertEquals(deleteDialog.getMessage(), language.translate("documentLibrary.rules.delete.dialogMessage"), "Delete dialog=");

        LOG.info("STEP2: Click 'Delete' button from Delete dialog");
        deleteDialog.clickDelete();
        manageRulesPage.renderedPage();
        assertEquals(manageRulesPage.getNoRulesText(), language.translate("documentLibrary.rules.noRules"), "Displayed rules=");
    }
}