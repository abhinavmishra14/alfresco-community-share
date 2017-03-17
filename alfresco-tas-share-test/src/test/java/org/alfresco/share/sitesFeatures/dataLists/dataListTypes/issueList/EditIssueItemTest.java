package org.alfresco.share.sitesFeatures.dataLists.dataListTypes.issueList;

import org.alfresco.common.DataUtil;
import org.alfresco.dataprep.CMISUtil;
import org.alfresco.dataprep.DashboardCustomization;
import org.alfresco.dataprep.DataListsService;
import org.alfresco.po.share.site.dataLists.CreateNewItemPopUp.IssueFields;
import org.alfresco.po.share.site.dataLists.DataListsPage;
import org.alfresco.po.share.site.dataLists.EditItemPopUp;
import org.alfresco.share.ContextAwareWebTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.model.TestGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.social.alfresco.api.entities.Site;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.testng.Assert.assertTrue;

/**
 * @author Laura.Capsa
 */
public class EditIssueItemTest extends ContextAwareWebTest
{
    @Autowired
    DataListsService dataLists;

    @Autowired
    DataListsPage dataListsPage;

    @Autowired
    EditItemPopUp editItemPopUp;

    private List<DashboardCustomization.Page> pagesToAdd = new ArrayList<>();
    String random = DataUtil.getUniqueIdentifier();
    String userName = "User-" + random;
    String userAssignee = "userAssignee-" + random;
    String siteName = "SiteName-" + random;
    String listName = "List name" + random;
    String itemTitle = "Item title";
    String itemId = "item ID-" + random;
    String itemDescription = "item description";
    String itemComment = "item comment";
    CMISUtil.Priority itemPriority = CMISUtil.Priority.Normal;
    CMISUtil.Status itemStatus = CMISUtil.Status.NOT_STARTED;
    String itemFile = "testFile1";

    String newItemTitle = " edited ItemTitle";
    String newItemID = "edited ItemID";
    String newItemDescription = "edited ItemDescription";
    String newItemComments = "edited ItemComment";
    String newItemDate = "01/11/2016";
    String updatedItemDate = "Thu 10 Nov 2016";
    String newItemStatus = CMISUtil.Status.COMPLETE.getValue();
    String newItemPriority = CMISUtil.Priority.High.toString();
    String attachedFile = "testDoc.txt";

    @BeforeClass(alwaysRun = true)
    public void setupTest()
    {
        pagesToAdd.add(DashboardCustomization.Page.DATALISTS);
        userService.create(adminUser, adminPassword, userName, password, "@tests.com", userName, userName);
        userService.create(adminUser, adminPassword, userAssignee, password, "@tests.com", userName, userName);
        siteService.create(userName, password, domain, siteName, siteName, Site.Visibility.PUBLIC);
        siteService.addPagesToSite(userName, password, siteName, pagesToAdd);
        dataLists.createDataList(adminUser, adminPassword, siteName, DataListsService.DataList.ISSUE_LIST, listName, "Issue List description");

        String path = srcRoot + "testdata" + File.separator;
        contentService.uploadFileInSite(userName, password, siteName, path + itemFile);
        contentService.uploadFileInSite(userName, password, siteName, path + attachedFile);
        dataLists.addIssueListItem(adminUser, adminPassword, siteName, listName, itemId, itemTitle, Collections.singletonList(userName), itemStatus, itemPriority,
                itemDescription, null, itemComment, Collections.singletonList(itemFile));

        setupAuthenticatedSession(userName, password);
        dataListsPage.navigate(siteName);
        dataListsPage.clickIssueListItem(listName);
    }

    @TestRail(id = "C6713")
    @Test(groups = { TestGroup.SANITY, TestGroup.SITES })
    public void saveEditItem()
    {
        LOG.info("STEP1: Click 'Edit' icon for the simple task list item to be edited");
        // List<String> item = Arrays.asList(itemId, itemTitle, userName, itemStatus.getValue(), itemPriority.toString(), itemDescription, "", itemComment,
        // itemFile);
        // dataListsPage.currentContent.editItem(item);
        dataListsPage.clickEditButtonForListItem();

        LOG.info("STEP2: Fill in 'Edit Data' form fields with valid data");
        editItemPopUp.editContent(IssueFields.Id.toString(), newItemID);
        editItemPopUp.editContent(IssueFields.Title.toString(), newItemTitle);
        editItemPopUp.addAssignedTo(userAssignee);
        editItemPopUp.selectDropDownItem(newItemStatus, "issueStatus");
        editItemPopUp.selectDropDownItem(newItemPriority, "issuePriority");
        editItemPopUp.editContent(IssueFields.Description.toString(), newItemDescription);
        editItemPopUp.editContent(IssueFields.DueDate.toString(), newItemDate);
        editItemPopUp.editContent(IssueFields.Comments.toString(), newItemComments);
        editItemPopUp.addAttachmentFromDocumentLibrary(attachedFile);

        LOG.info("STEP3: Click Save button");
        editItemPopUp.clickSave();
        List<String> attachmentsList = Arrays.asList(attachedFile, itemFile);
        List<String> expectedItem = Arrays.asList(newItemID, newItemTitle, userAssignee, newItemStatus, newItemPriority, newItemDescription, updatedItemDate,
                newItemComments, attachmentsList.toString());
        assertTrue(dataListsPage.currentContent.isListItemDisplayed(expectedItem), newItemTitle + " issue list item is displayed.");

        cleanupAuthenticatedSession();
    }
}