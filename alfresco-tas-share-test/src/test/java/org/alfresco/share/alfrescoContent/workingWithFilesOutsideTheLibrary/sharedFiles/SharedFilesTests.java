package org.alfresco.share.alfrescoContent.workingWithFilesOutsideTheLibrary.sharedFiles;

import org.alfresco.dataprep.CMISUtil;
import org.alfresco.po.share.alfrescoContent.SharedFilesPage;
import org.alfresco.po.share.alfrescoContent.document.SocialFeatures;
import org.alfresco.share.ContextAwareWebTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.data.RandomData;
import org.alfresco.utility.model.TestGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static org.testng.Assert.assertTrue;

/**
 * @author Laura.Capsa
 */
public class SharedFilesTests extends ContextAwareWebTest
{
    @Autowired private SharedFilesPage sharedFilesPage;

    @Autowired private SocialFeatures socialFeatures;

    private final String docName = String.format("Doc-C7661-%s", RandomData.getRandomAlphanumeric());
    private final String path = "Shared/";

    @BeforeClass(alwaysRun = true)
    public void setupTest()
    {
        contentService.createDocumentInRepository(adminUser, adminPassword, path, CMISUtil.DocumentType.TEXT_PLAIN, docName, docName + " Content");

        setupAuthenticatedSession(adminUser, adminPassword);
        sharedFilesPage.navigate();
    }

    @TestRail(id = "C7661")
    @Test(groups = { TestGroup.SANITY, TestGroup.CONTENT})
    public void verifyShareButton()
    {
        LOG.info("STEP1: Hover over a file and click on the \"Share\" button.");
        sharedFilesPage.mouseOverContentItem(docName);
        assertTrue(socialFeatures.checkShareButtonAvailability(), "Share button is displayed.");
        socialFeatures.clickShareButton(docName);
        assertTrue(socialFeatures.isPublicLinkInputFieldDisplayed(), "Public link input field is displayed.");
    }

    @AfterClass
    public void cleanUp()
    {
        contentService.deleteContentByPath(adminUser, adminPassword, path + docName);
    }
}