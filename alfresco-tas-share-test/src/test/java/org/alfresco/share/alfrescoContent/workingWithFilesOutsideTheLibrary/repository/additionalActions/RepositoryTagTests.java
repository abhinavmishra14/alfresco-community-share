package org.alfresco.share.alfrescoContent.workingWithFilesOutsideTheLibrary.repository.additionalActions;

import org.alfresco.common.DataUtil;
import org.alfresco.dataprep.CMISUtil.DocumentType;
import org.alfresco.dataprep.ContentActions;
import org.alfresco.dataprep.ContentService;
import org.alfresco.dataprep.SiteService;
import org.alfresco.po.share.DeleteDialog;
import org.alfresco.po.share.alfrescoContent.RepositoryPage;
import org.alfresco.po.share.alfrescoContent.buildingContent.CreateContent;
import org.alfresco.po.share.alfrescoContent.organizingContent.taggingAndCategorizingContent.SelectDialog;
import org.alfresco.po.share.alfrescoContent.pageCommon.HeaderMenuBar;
import org.alfresco.po.share.alfrescoContent.workingWithFilesAndFolders.EditPropertiesDialog;
import org.alfresco.share.ContextAwareWebTest;
import org.alfresco.testrail.TestRail;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.Arrays;

import static org.testng.Assert.*;

public class RepositoryTagTests extends ContextAwareWebTest
{
    @Autowired
    RepositoryPage repositoryPage;

    @Autowired
    EditPropertiesDialog editPropertiesDialog;

    @Autowired
    SelectDialog selectDialog;

    @Autowired
    ContentActions contentActions;

    @Autowired
    ContentService contentService;

    private String user = "C8266TestUser" + DataUtil.getUniqueIdentifier();
    private String fileNameC8266 = "C8266 file";
    private String fileNameC8290 = "C8290 file";
    private String fileNameC8278 = "C8278 file";
    private String fileNameC8291 = "C8291 file";
    private String fileNameC8300 = "C8300 file";
    private String fileContent = "test file content";
    private String path = "User Homes/" + user;
    private String folderName = "C8167 Folder";
    private String tagC8266 = "tagNameFile-C8266-" + DataUtil.getUniqueIdentifier();
    private String tagC8267 = "tag-C8267" + DataUtil.getUniqueIdentifier();
    private String tagC8278 = "tag-C8278" + DataUtil.getUniqueIdentifier();
    private String tagC8290 = "tag-C8290" + DataUtil.getUniqueIdentifier();

    @BeforeClass
    public void setupTest()
    {
        userService.create(adminUser, adminPassword, user, password, user + "@tests.com", user, user);
        setupAuthenticatedSession(user, password);
    }

    @TestRail(id = "C8266")
    @Test
    public void createTagForFile()
    {
        // Preconditions
        String deletePath = path + "/" + fileNameC8266;
        contentService.createDocumentInRepository(adminUser, adminPassword, path, DocumentType.TEXT_PLAIN, fileNameC8266, fileContent);
        repositoryPage.navigate();
        repositoryPage.renderedPage();
        repositoryPage.clickOnContent("User Homes");
        repositoryPage.clickOnContent(user);
        browser.waitInSeconds(4);
        Assert.assertTrue(repositoryPage.isContentNameDisplayed(fileNameC8266), fileNameC8266 + " is not available in Repository");

        LOG.info("STEP1: Hover over one tag from the content name");
        repositoryPage.mouseOverNoTags(fileNameC8266);
        browser.waitInSeconds(2);

        LOG.info("STEP2: Click \"Tag\" icon");
        repositoryPage.clickEditTagIcon(fileNameC8266);
        assertTrue(repositoryPage.isEditTagInputFieldDisplayed(), fileNameC8266 + " -> Edit tag text input field is displayed.");

        LOG.info("STEP3: Type any tag name in the input field and click \"Save\" link");
        repositoryPage.typeTagName(tagC8266);
        repositoryPage.clickEditTagLink(language.translate("documentLibrary.tag.link.save"));
        ArrayList<String> tagsList = new ArrayList<>(Arrays.asList(tagC8266.toLowerCase()));
        assertEquals(repositoryPage.getTags(fileNameC8266), tagsList.toString(), tagC8266 + " -> tags=");
        contentService.deleteContentByPath(adminUser, adminPassword, deletePath);
    }

    @TestRail(id = "C8267")
    @Test
    public void createTagForFolder()
    {
        String deletePath = path + "/" + folderName;
        contentService.createFolderInRepository(adminUser, adminPassword, folderName, path);
        repositoryPage.navigate();
        repositoryPage.renderedPage();
        repositoryPage.clickOnContent("User Homes");
        repositoryPage.clickOnContent(user);
        browser.waitInSeconds(4);
        Assert.assertTrue(repositoryPage.isContentNameDisplayed(folderName), folderName + " is not available in Repository");

        LOG.info("STEP1: Hover over the text \"No Tags\" from the folder");
        repositoryPage.mouseOverNoTags(folderName);
        browser.waitInSeconds(2);
        assertTrue(repositoryPage.isEditTagIconDisplayed(folderName), folderName + " -> \"Edit Tag\" icon is displayed");

        LOG.info("STEP2: Click \"Edit Tag\" icon");
        repositoryPage.clickEditTagIcon(folderName);
        assertTrue(repositoryPage.isEditTagInputFieldDisplayed(), folderName + " -> Edit tag text input field is displayed.");

        LOG.info("STEP3: Type any tag name in the input field and click \"Save\" link");
        repositoryPage.typeTagName(tagC8267);
        repositoryPage.clickEditTagLink("Save");
        ArrayList<String> tagsList = new ArrayList<>(Arrays.asList(tagC8267.toLowerCase()));
        assertEquals(repositoryPage.getTags(folderName), tagsList.toString(), folderName + " -> tags=");

        contentService.deleteContentByPath(adminUser, adminPassword, deletePath);
    }

    @TestRail(id = "C8278")
    @Test()
    public void addExistingTag()
    {
        String deletePath = path + "/" + fileNameC8278;
        contentService.createDocumentInRepository(adminUser, adminPassword, path, DocumentType.TEXT_PLAIN, fileNameC8278, fileContent);
        repositoryPage.navigate();
        repositoryPage.renderedPage();
        repositoryPage.clickOnContent("User Homes");
        repositoryPage.clickOnContent(user);
        browser.waitInSeconds(4);
        Assert.assertTrue(repositoryPage.isContentNameDisplayed(fileNameC8278), fileNameC8278 + " is not available in Repository");
        LOG.info("STEP1: Hover over the content created in the preconditions");

        repositoryPage.mouseOverFileName(fileNameC8278);
        browser.waitInSeconds(2);
        repositoryPage.clickMore();
        LOG.info("STEP2: Click \"Edit Properties\" option");
        repositoryPage.clickDocumentLibraryItemAction(fileNameC8278, "Edit Properties", repositoryPage);
        assertEquals(editPropertiesDialog.getDialogTitle(), String.format(language.translate("editPropertiesDialog.title"), fileNameC8278),
                "Displayed dialog=");
        assertTrue(editPropertiesDialog.isSelectTagsButtonDisplayed(), "'Select' tag button is displayed.");

        LOG.info("STEP3: Click \"Select\" button");
        editPropertiesDialog.clickSelectTags();
        assertEquals(selectDialog.getDialogTitle(), language.translate("selectDialog.title"), "Displayed dialog=");

        LOG.info("STEP4: Pick tag from the available tags list and click \"Add\" then click OK");
        selectDialog.typeTag(tagC8278.toLowerCase());
        browser.waitInSeconds(1);
        selectDialog.clickCreateNewIcon();
        selectDialog.clickOk();
        assertTrue(editPropertiesDialog.isTagSelected(tagC8278.toLowerCase()), "'Tags:' section=");            

        LOG.info("STEP5: Click \"Save\" button");
        editPropertiesDialog.clickSave();
        ArrayList<String> tagsList = new ArrayList<>(Arrays.asList(tagC8278.toLowerCase()));
        assertEquals(repositoryPage.getPageTitle(), "Alfresco » Repository Browser", "Displayed page=");
        assertEquals(repositoryPage.getTags(fileNameC8278), tagsList.toString(), fileNameC8278 + " -> tags=");
        contentService.deleteContentByPath(adminUser, adminPassword, deletePath);
    }

    @TestRail(id = "C8290")
    @Test()
    public void editTag()
    {
        // Preconditions
        String deletePath = path + "/" + fileNameC8290;
        contentService.createDocumentInRepository(adminUser, adminPassword, path, DocumentType.TEXT_PLAIN, fileNameC8290, fileContent);
        String pathToItem = path + "/" + fileNameC8290;
        contentActions.addSingleTag(adminUser, adminPassword, pathToItem, "testtag");
        repositoryPage.navigate();
        repositoryPage.renderedPage();
        repositoryPage.clickOnContent("User Homes");
        repositoryPage.clickOnContent(user);
        browser.waitInSeconds(4);
        Assert.assertTrue(repositoryPage.isContentNameDisplayed(fileNameC8290), fileNameC8278 + " is not available in Repository");

        LOG.info("STEP1: Hover over the tag(s) from the content");
        repositoryPage.mouseOverTags(fileNameC8290);
        browser.waitInSeconds(2);
        assertTrue(repositoryPage.isEditTagIconDisplayed(fileNameC8290), fileNameC8290 + " -> 'Edit Tag' icon is displayed.");

        LOG.info("STEP2: Click \"Edit Tag\" icon");
        repositoryPage.mouseOverTags(fileNameC8290);
        browser.waitInSeconds(2);
        repositoryPage.clickEditTagIcon(fileNameC8290);
        assertTrue(repositoryPage.isEditTagInputFieldDisplayed(), fileNameC8290 + " -> 'Edit Tag' text input field is displayed.");

        LOG.info("STEP3: Click on any tag and type a valid tag name");
        repositoryPage.editTag(fileNameC8290, "testtag", tagC8290.toLowerCase());

        LOG.info("STEP4: Click \"Save\" link and verify the content tags");
        repositoryPage.clickEditTagLink(language.translate("documentLibrary.tag.link.save"));
        assertTrue(repositoryPage.getTags(fileNameC8290).contains(tagC8290.toLowerCase()));
        contentService.deleteContentByPath(adminUser, adminPassword, deletePath);
    }

    @TestRail(id = "C8291")
    @Test()
    public void removeTag()
    {
        // Preconditions
        String deletePath = path + "/" + fileNameC8291;
        contentService.createDocumentInRepository(adminUser, adminPassword, path, DocumentType.TEXT_PLAIN, fileNameC8291, fileContent);
        String pathToItem = path + "/" + fileNameC8291;
        String tagC8291 = "tagc8291";
        contentActions.addSingleTag(adminUser, adminPassword, pathToItem, tagC8291);
        repositoryPage.navigate();
        repositoryPage.renderedPage();
        repositoryPage.clickOnContent("User Homes");
        repositoryPage.clickOnContent(user);
        browser.waitInSeconds(4);
        Assert.assertTrue(repositoryPage.isContentNameDisplayed(fileNameC8291), fileNameC8291 + " is not available in Repository");

        LOG.info("STEP1: Hover over the tag from " + fileNameC8291);
        repositoryPage.mouseOverTags(fileNameC8291);
        browser.waitInSeconds(2);
        assertTrue(repositoryPage.isEditTagIconDisplayed(fileNameC8291), fileNameC8291 + " -> 'Edit Tag' icon is displayed.");

        LOG.info("STEP2: Click \"Edit Tags\" icon");
        repositoryPage.clickEditTagIcon(fileNameC8291);
        assertTrue(repositoryPage.isEditTagInputFieldDisplayed(), fileNameC8291 + " -> 'Edit Tag' text input field is displayed.");

        LOG.info("STEP3: Hover over the tag and click 'Remove' icon");
        repositoryPage.removeTag(tagC8291);

        LOG.info("STEP4: Click 'Save' link");
        repositoryPage.clickEditTagLink(language.translate("documentLibrary.tag.link.save"));
        assertTrue(repositoryPage.isNoTagsTextDisplayed(fileNameC8291), fileNameC8291 + " -> " + tagC8291 + " is removed.");
        contentService.deleteContentByPath(adminUser, adminPassword, deletePath);
    }

    @TestRail(id = "C8300")
    @Test()
    public void updateTags()
    {
        // Preconditions
        String deletePath = path + "/" + fileNameC8300;
        contentService.createDocumentInRepository(adminUser, adminPassword, path, DocumentType.TEXT_PLAIN, fileNameC8300, fileContent);
        String tagC8300 = "tagc8300";
        repositoryPage.navigate();
        repositoryPage.renderedPage();
        repositoryPage.clickOnContent("User Homes");
        repositoryPage.clickOnContent(user);
        browser.waitInSeconds(4);
        Assert.assertTrue(repositoryPage.isContentNameDisplayed(fileNameC8300), fileNameC8300 + " is not available in Repository");

        LOG.info("STEP1: Hover over the text \"No Tags\" from " + fileNameC8300);
        repositoryPage.mouseOverNoTags(fileNameC8300);
        browser.waitInSeconds(2);
        assertTrue(repositoryPage.isEditTagIconDisplayed(fileNameC8300), fileNameC8300 + " -> \"Edit Tag\" icon is displayed");

        LOG.info("STEP2: Click \"Edit Tag\" icon");
        repositoryPage.clickEditTagIcon(fileNameC8300);
        assertTrue(repositoryPage.isEditTagInputFieldDisplayed(), fileNameC8300 + " -> Edit tag text input field is displayed.");

        LOG.info("STEP3: Add a tag");
        repositoryPage.typeTagName(tagC8300);

        LOG.info("STEP3: Click 'Remove' icon");
        assertEquals(repositoryPage.removeTag(tagC8300.toLowerCase()), tagC8300.toLowerCase(), "Removed tag=");

        LOG.info("STEP4: Click 'Save' link");
        repositoryPage.clickEditTagLink(language.translate("documentLibrary.tag.link.save"));
        browser.waitInSeconds(2);
        assertTrue(repositoryPage.isNoTagsTextDisplayed(fileNameC8300), fileNameC8300 + " -> " + tagC8300 + " is removed.");
        contentService.deleteContentByPath(adminUser, adminPassword, deletePath);
    }
}
