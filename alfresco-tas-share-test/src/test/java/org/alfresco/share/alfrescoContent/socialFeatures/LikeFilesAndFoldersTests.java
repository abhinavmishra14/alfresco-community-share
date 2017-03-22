package org.alfresco.share.alfrescoContent.socialFeatures;

import org.alfresco.common.DataUtil;
import org.alfresco.dataprep.CMISUtil.DocumentType;
import org.alfresco.po.share.alfrescoContent.buildingContent.CreateContent;
import org.alfresco.po.share.alfrescoContent.document.DocumentDetailsPage;
import org.alfresco.po.share.alfrescoContent.document.SocialFeatures;
import org.alfresco.po.share.site.DocumentLibraryPage;
import org.alfresco.share.ContextAwareWebTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.model.TestGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.social.alfresco.api.entities.Site.Visibility;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class LikeFilesAndFoldersTests extends ContextAwareWebTest
{
    @Autowired private DocumentLibraryPage documentLibraryPage;

    @Autowired
    CreateContent create;

    @Autowired
    DocumentDetailsPage documentDetailsPage;
    
    @Autowired private SocialFeatures social;
    
    private final String user = "C7906User" + DataUtil.getUniqueIdentifier();
    private final String description = "C7906SiteDescription" + DataUtil.getUniqueIdentifier();
    private final String siteName = "C7906SiteName" + DataUtil.getUniqueIdentifier();
    private final String fileNameC7906 = "C7906 title";
    private final String fileContentC7906 ="C7906 content";
    private final String folderNameC7907 ="C7907 folder name";
    private final String fileName7908 ="7908 fileName";
    private final String folderName7909 = "7909 folderName";
    
    @BeforeClass(alwaysRun = true)

    public void setupTest()
    {
        userService.create(adminUser, adminPassword, user, password, user + "@tests.com", user, user);
        siteService.create(user, password, domain, siteName, description, Visibility.PUBLIC);
        contentService.createDocument(user, password, siteName, DocumentType.TEXT_PLAIN, fileNameC7906, fileContentC7906);
        contentService.createDocument(user, password, siteName, DocumentType.TEXT_PLAIN, fileName7908, fileContentC7906);
        contentService.createFolder(user, password, folderNameC7907, siteName);
        contentService.createFolder(user, password, folderName7909, siteName);
        setupAuthenticatedSession(user, password);
    }
    
    @TestRail(id="C7906")
    @Test(groups = { TestGroup.SANITY, TestGroup.CONTENT})
    
    public void likeFile()
    {
        
        documentLibraryPage.navigate(siteName);        
        LOG.info("Step 1: Hover over the file Like link.");
        
        Assert.assertTrue(documentLibraryPage.isLikeButtonDisplayed(fileNameC7906), "Documents link is not present");
        Assert.assertEquals(social.getLikeButtonMessage(fileNameC7906), "Like this document", "Like Button message is not correct");
        Assert.assertEquals(social.getNumberOfLikes(fileNameC7906), 0, "The number of likes is not correct");
        
        LOG.info("Step 2: Click on the Like button");
        social.clickLikeButton(fileNameC7906);
        getBrowser().waitUntilElementVisible(social.enabledLikeButton);
        Assert.assertEquals(social.getNumberOfLikes(fileNameC7906), 1, "The number of likes is not correct"); 
        Assert.assertTrue(social.isLikeButtonEnabled(fileNameC7906), "Like button is not enabled");
        
    }
    
    @TestRail(id ="7907")
    @Test(groups = { TestGroup.SANITY, TestGroup.CONTENT})
    
    public void likeFolder()
    {
        documentLibraryPage.navigate(siteName);        
        LOG.info("Step 1: Hover over the file Like link.");
        Assert.assertTrue(documentLibraryPage.isLikeButtonDisplayed(folderNameC7907), "Documents link is not present");
        Assert.assertEquals(social.getLikeButtonMessage(folderNameC7907), "Like this folder", "Like Button message is not correct");
        Assert.assertEquals(social.getNumberOfLikes(folderNameC7907), 0, "The number of likes is not correct");
        
        LOG.info("Step 2: Click on the Like button");
        social.clickLikeButton(folderNameC7907);
        Assert.assertEquals(social.getNumberOfLikes(folderNameC7907), 1, "The number of likes is not correct");   
        Assert.assertTrue(social.isLikeButtonEnabled(folderNameC7907), "Like button is not enabled");
    }  
    
    @TestRail(id ="C7908")
    @Test(groups = { TestGroup.SANITY, TestGroup.CONTENT})
    
    public void unlikeFile()
    {
        //preconditions
        documentLibraryPage.navigate(siteName); 
        social.clickLikeButton(fileName7908);
        
        LOG.info("Step 1: Hover over the file Like link.");
        Assert.assertEquals(social.getLikeButtonEnabledText(fileName7908), "Unlike", "Unlike is not displayed");
        Assert.assertEquals(social.getNumberOfLikes(fileName7908), 1, "The number of likes is not correct");
        
        LOG.info("Step 2: Click on Unlike");
        social.clickUnlike(fileName7908);
        Assert.assertEquals(social.getNumberOfLikes(fileName7908), 0, "The number of likes is not correct");       
    }
    
    @TestRail(id ="C7909")
    @Test(groups = { TestGroup.SANITY, TestGroup.CONTENT})
    
    public void unlikeFolder()
    {
        //preconditions
        documentLibraryPage.navigate(siteName); 
        social.clickLikeButton(folderName7909);
        
        LOG.info("Step 1: Hover over the folder Like link.");
        Assert.assertEquals(social.getLikeButtonEnabledText(folderName7909), "Unlike", "Unlike is not displayed");
        Assert.assertEquals(social.getNumberOfLikes(folderName7909), 1, "The number of likes is not correct");
        
        LOG.info("Step 2: Click on Unlike");
        social.clickUnlike(folderName7909);
        Assert.assertEquals(social.getNumberOfLikes(folderName7909), 0, "The number of likes is not correct");       
    }
}       
