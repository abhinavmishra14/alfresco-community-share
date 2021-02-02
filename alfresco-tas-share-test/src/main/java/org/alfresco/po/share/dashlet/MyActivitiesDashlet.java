package org.alfresco.po.share.dashlet;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import org.alfresco.po.share.alfrescoContent.document.DocumentDetailsPage;
import org.alfresco.po.share.user.profile.UserProfilePage;
import org.alfresco.utility.model.ContentModel;
import org.alfresco.utility.model.FileModel;
import org.alfresco.utility.model.FolderModel;
import org.alfresco.utility.model.SiteModel;
import org.alfresco.utility.model.UserModel;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.List;

public class MyActivitiesDashlet extends AbstractActivitiesDashlet<MyActivitiesDashlet>
{
    private final String CREATE_ACTIVITY = "activitiesDashlet.document.createActivity";

    public MyActivitiesDashlet(ThreadLocal<WebDriver> webDriver)
    {
        super(webDriver);
    }

    public MyActivitiesDashlet assertAddDocumentActivityIsDisplayed(UserModel user, FileModel file, SiteModel site)
    {
        LOG.info("Assert add document activity is displayed for document {}", file.getName());
        assertTrue(webElementInteraction.isElementDisplayed(getActivityRow(
            String.format(language.translate(CREATE_ACTIVITY),
                user.getFirstName(), user.getLastName(), file.getName(), site.getTitle()))),
                "Add document activity is not displayed");
        return this;
    }

    public MyActivitiesDashlet assertAddDocumentActivityIsNotDisplayedForUser(UserModel user, FileModel file, SiteModel site)
    {
        LOG.info("Assert add document activity for {} is not displayed for user {}",file.getName(), user.getUsername());
        List<WebElement> rows = webElementInteraction.waitUntilElementsAreVisible(activityRows); // TODO check it with no activities
        assertFalse(webElementInteraction.getTextFromElementList(rows).contains(
            String.format(language.translate(CREATE_ACTIVITY),
                user.getFirstName(), user.getLastName(), file.getName(), site.getTitle())),
                    String.format("Add document activity is displayed for user %s ", user.getUsername()));
        return this;
    }

    public MyActivitiesDashlet assertUpdateDocumentActivityIsDisplayed(UserModel user, FileModel file, SiteModel site)
    {
        LOG.info("Assert update document activity is displayed for document {}", file.getName());
        assertTrue(webElementInteraction.isElementDisplayed(getActivityRow(
            String.format(language.translate("activitiesDashlet.document.updateActivity"),
                user.getFirstName(), user.getLastName(), file.getName(), site.getTitle()))),
                "Update document activity is not displayed");
        return this;
    }

    public MyActivitiesDashlet assertDeleteDocumentActivityIsDisplayed(UserModel user, FileModel file, SiteModel site)
    {
        LOG.info("Assert delete document activity is displayed for document {}", file.getName());
        assertTrue(webElementInteraction.isElementDisplayed(getActivityRow(
            String.format(language.translate("activitiesDashlet.document.deleteActivity"),
                user.getFirstName(), user.getLastName(), file.getName(), site.getTitle()))),
                "Delete document activity is not displayed");
        return this;
    }

    public MyActivitiesDashlet assertAddedFolderActivityIsDisplayed(UserModel user, FolderModel folder, SiteModel site)
    {
        LOG.info("Assert add folder activity is displayed for {}", folder.getName());
        assertTrue(webElementInteraction.isElementDisplayed(getActivityRow(
            String.format(language.translate("activitiesDashlet.folder.createActivity"),
                user.getFirstName(), user.getLastName(), folder.getName(), site.getTitle()))),
                "Add folder activity is not displayed");
        return this;
    }

    public MyActivitiesDashlet assertDeletedFolderActivityIsDisplayed(UserModel user, FolderModel folder, SiteModel site)
    {
        LOG.info("Assert delete folder activity is displayed for {}", folder.getName());
        assertTrue(webElementInteraction.isElementDisplayed(getActivityRow(
            String.format(language.translate("activitiesDashlet.folder.deleteActivity"),
                user.getFirstName(), user.getLastName(), folder.getName(), site.getTitle()))),
                "Delete folder activity is not displayed");
        return this;
    }

    public MyActivitiesDashlet assertCreatedLinkActivityIsDisplayed(UserModel user, ContentModel contentModel, SiteModel site)
    {
        LOG.info("Assert create link activity is displayed for content {}", contentModel.getName());
        assertTrue(webElementInteraction.isElementDisplayed(getActivityRow(
            String.format(language.translate("activitiesDashlet.link.createActivity"),
                user.getFirstName(), user.getLastName(), contentModel.getName(), site.getTitle()))),
            "Create link activity is not displayed");
        return this;
    }

    public UserProfilePage clickUserFromAddedDocumentActivity(UserModel user, FileModel file, SiteModel site)
    {
        getActivityRow(String.format(language.translate(CREATE_ACTIVITY),
            user.getFirstName(), user.getLastName(), file.getName(), site.getTitle())).findElement(userLinkLocator).click();
        return new UserProfilePage(webDriver);
    }

    public MyActivitiesDashlet assertEmptyDashletMessageEquals()
    {
        LOG.info("Assert my activities dashlet message is correct when there are no activities");
        assertEquals(webElementInteraction.getElementText(activitiesEmptyList), language.translate("myactivitiesDashlet.empty"),
            "Empty dashlet message is not correct");
        return this;
    }
}

