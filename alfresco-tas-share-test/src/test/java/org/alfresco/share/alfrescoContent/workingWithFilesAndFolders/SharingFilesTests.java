package org.alfresco.share.alfrescoContent.workingWithFilesAndFolders;

import org.alfresco.common.DataUtil;
import org.alfresco.dataprep.CMISUtil.DocumentType;
import org.alfresco.po.share.LoginPage;
import org.alfresco.po.share.alfrescoContent.document.DocumentDetailsPage;
import org.alfresco.po.share.alfrescoContent.document.SocialFeatures;
import org.alfresco.po.share.site.DocumentLibraryPage;
import org.alfresco.share.ContextAwareWebTest;
import org.alfresco.testrail.TestRail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.social.alfresco.api.entities.Site.Visibility;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class SharingFilesTests extends ContextAwareWebTest
{
    @Autowired
    DocumentLibraryPage documentLibraryPage;

    @Autowired
    SocialFeatures social;

    @Autowired
    LoginPage loginPage;

    @Autowired
    DocumentDetailsPage documentDetails;

    private String user = "C7095User" + DataUtil.getUniqueIdentifier();
    private String description = "C7095SiteDescription" + DataUtil.getUniqueIdentifier();
    private String siteName = "C7095SiteName" + DataUtil.getUniqueIdentifier();
    private String fileNameC7095 = "C7095title";
    private String fileContent = "content";
    private String fileNameC7096 = "file C7076";
    private String fielNameC7097 = "File C7097";
    private String fileNameC7099 = "File C7099";
    private String fileNameC7649 = "fileC7649" + DataUtil.getUniqueIdentifier();
    private String folderName = "C7093 folder";
    private String siteNameC7093 = "C7093" + DataUtil.getUniqueIdentifier();
    private String siteNameC7649 = "siteNameC7649" + DataUtil.getUniqueIdentifier();
    private String sharedUrl;
    private String windowToSwitchToAlfresco;
    private String windowToCloseGPlus;

    @BeforeClass
    public void setupTest()
    {
        userService.create(adminUser, adminPassword, user, password, user + "@tests.com", user, user);
        siteService.create(user, password, domain, siteNameC7093, description, Visibility.PUBLIC);
        siteService.create(user, password, domain, siteNameC7649, description, Visibility.PUBLIC);
        siteService.create(user, password, domain, siteName, description, Visibility.PUBLIC);
        contentService.createDocument(user, password, siteName, DocumentType.TEXT_PLAIN, fileNameC7095, fileContent);
        contentService.createDocument(user, password, siteName, DocumentType.TEXT_PLAIN, fielNameC7097, fileContent);
        contentService.createDocument(user, password, siteName, DocumentType.TEXT_PLAIN, fileNameC7096, fileContent);
        contentService.createDocument(user, password, siteName, DocumentType.TEXT_PLAIN, fileNameC7099, fileContent);
        contentService.createDocument(user, password, siteNameC7649, DocumentType.TEXT_PLAIN, fileNameC7649, fileContent);
        contentService.createFolder(user, password, folderName, siteNameC7093);
        //setupAuthenticatedSession(user, password);
    }


   @TestRail(id = "C7095")
   @Test
    public void shareWithFacebook()
    {

            setupAuthenticatedSession(user, password);
            documentLibraryPage.navigate(siteName);

            LOG.info("Step 1: Check that the Share button is available and click Share");
            Assert.assertTrue(social.isShareButtonDisplayed(fileNameC7095), "The Share button is not displayed");
            Assert.assertEquals(social.getShareButtonTooltip(fileNameC7095), "Share document", "Share button tooltip is not correct");
            social.clickShareButton(fileNameC7095);
            getBrowser().waitUntilElementClickable(social.quickShareWindow, 10L);
            Assert.assertTrue(social.isQuickshareWindowDisplayed(), "Quickshare window is not displayed");
            Assert.assertTrue(social.isPublicLinkDisplayed(), "public link is not displayed");

            LOG.info("Step 2: Click Facebook icon");
            social.clickShareWithFacebook();
            getBrowser().switchWindow();
            getBrowser().waitUntilElementIsDisplayedWithRetry(social.facebookHomeLink, 2);
            Assert.assertEquals(social.getFacebookWindowTitle(), "Facebook", "User is not redirected to the Facebook page");
            social.loginFacebook();
            Assert.assertTrue(social.isShareLinkDisplayedOnFacebook(), "Share link is not displayed on Facebook");
            getBrowser().closeWindowAcceptingModalDialog();
            cleanupAuthenticatedSession();
    }

 @TestRail(id = "C7096")
 @Test
    public void shareWithTwitter()
    {
            setupAuthenticatedSession(user, password);
            String currentWindow = getBrowser().getWindowHandle();
            //documentLibraryPage.getRelativePath();
            documentLibraryPage.navigate(siteName);
            String url = getBrowser().getCurrentUrl();
            String server = url.substring(7, 26);
            String expectedLink = "File" + " " + fileNameC7096 + " " + "shared from Alfresco http://" + server + "/share/s";
            System.out.println("Current URL: " + getBrowser().getCurrentUrl());
            System.out.println(server);
            documentLibraryPage.getRelativePath();

            LOG.info("Step 1: Check that the Share button is available and click Share");
            Assert.assertTrue(social.isShareButtonDisplayed(fileNameC7096), "The Share button is not displayed");
            Assert.assertEquals(social.getShareButtonTooltip(fileNameC7096), "Share document", "Share button tooltip is not correct");
            social.clickShareButton(fileNameC7096);
            getBrowser().waitUntilElementClickable(social.quickShareWindow, 10L);
            Assert.assertTrue(social.isQuickshareWindowDisplayed(), "Quickshare window is not displayed");
            Assert.assertTrue(social.isPublicLinkDisplayed(), "public link is not displayed");

            LOG.info("Step 2: Click Twitter icon");

            social.clickTwitterIcon();
            getBrowser().waitInSeconds(5);
            //Switch to new window opened
            for (String winHandle : getBrowser().getWindowHandles()) {
                getBrowser().switchTo().window(winHandle);
                if (getBrowser().getCurrentUrl().contains("https://twitter.com")) {
                    break;
                } else {
                    getBrowser().switchTo().window(currentWindow);
                }
            }
            Assert.assertEquals(social.getPageTitle(), "Share a link on Twitter", "User is not redirected to Twitter");
            Assert.assertEquals(social.getTwitterShareLink(), expectedLink, "Share link is not correct");
            Assert.assertEquals(social.getTwitterPageTitle(), "Share a link with your followers");
            getBrowser().closeWindowAndSwitchBack();
            cleanupAuthenticatedSession();
    }

  @TestRail(id = "C7097")
  @Test

    public void shareWithGooglePlus() {

    {
        try {
            setupAuthenticatedSession(user, password);
            documentLibraryPage.navigate(siteName);
            String url = getBrowser().getCurrentUrl();
            String server = url.substring(7, 26);
            String expectedLink = "http://" + server + "/share/s";
            windowToSwitchToAlfresco = getBrowser().getWindowHandle();
            LOG.info("Step 1: For file click Share icon");
            social.clickShareButton(fielNameC7097);
            getBrowser().waitUntilElementClickable(social.quickShareWindow, 10L);
            Assert.assertTrue(social.isQuickshareWindowDisplayed(), "Quickshare window is not displayed");
            Assert.assertTrue(social.isPublicLinkDisplayed(), "public link is not displayed");

            LOG.info("Step 2: Click Google+ icon");
            social.clickGooglePlus();

            getBrowser().switchWindow();
            social.loginToGoogleAccount();
            windowToCloseGPlus = getBrowser().getWindowHandle();
            getBrowser().waitInSeconds(6);
            Assert.assertEquals(getBrowser().getCurrentUrl().substring(0, 24), "https://plus.google.com/");
            Assert.assertEquals(social.getLinkSharedWithGooglePlus(), expectedLink, "Link shared on Google Plus is not corerct");
            getBrowser().closeWindowAndSwitchBack();
            cleanupAuthenticatedSession();
        }
        finally
        {
            getBrowser().closeWindowAndSwitchBackParametrized(windowToSwitchToAlfresco, windowToCloseGPlus);
        }

    }
}

   @TestRail(id = "C7099")
   @Test
    public void unshareDocument()
    {
        /**
         * Precondition - File is shared
         */

            cleanupAuthenticatedSession();
            setupAuthenticatedSession(user, password);
            documentLibraryPage.navigate(siteName);
            social.clickShareButton(fileNameC7099);

            LOG.info("Step 1: Mouse over Share icon and get the Share tooltip message");
            documentLibraryPage.navigate(siteName);
            Assert.assertEquals(social.getShareButtonTooltip(fileNameC7099), "This document is shared (click for more options)", "Tootltip message is not correct");

            LOG.info("Step 2: Click the Share icon");
            social.clickShareButton(fileNameC7099);
            getBrowser().waitUntilElementClickable(social.quickShareWindow, 10L);
            Assert.assertTrue(social.isQuickshareWindowDisplayed(), "Quickshare window is not displayed");

            LOG.info("Step 3: Click Unshare link");
            social.clickUnshareButton();
            Assert.assertEquals(social.getShareButtonTooltip(fileNameC7099), "Share document", "Share button tooltip is not correct");
            cleanupAuthenticatedSession();
        }

    @TestRail(id = "C7093")
    @Test
    public void shareFolder()

    {
        setupAuthenticatedSession(user,password);
        LOG.info("Step 1: Check that Share is not available for folders");
        documentLibraryPage.navigate(siteNameC7093);
        Assert.assertFalse(social.checkShareButtonAvailability(), "The Share button is displayed for folder");
        cleanupAuthenticatedSession();
    }

    @TestRail(id = "C7649")
    @Test
    public void sharedFilesContentAvailability()

    {
        setupAuthenticatedSession(user, password);
        LOG.info("Preconditions: Navigate to Document Library ppage for the created site");
        documentLibraryPage.navigate(siteNameC7649);

        LOG.info("Step1: Click Share icon on the file and verify a pop-up displaying the URL for the file is opened.");

        social.clickShareButton(fileNameC7649);
        Assert.assertTrue(social.isShareUrlDisplayed(), "Shared Url is not diaplyed");

        LOG.info("Step2: Save the URL for the file and logout from Share");
        social.clickPublicLinkViewButton();
        sharedUrl = getBrowser().getCurrentUrl();
        getBrowser().cleanUpAuthenticatedSession();

        LOG.info("Step3: Access the saved URL and verify the file is successfully displayed and 'Login' button is availbale on the page");
        getBrowser().get(sharedUrl);
        Assert.assertEquals(social.getContentTextFromSharedFilePage(), fileContent);
        Assert.assertTrue(social.isLoginButtonOnSharedFilePage(), "Login button is not displayed");

        LOG.info("Step4: Click 'Login' button and login to Share");
        social.clickLoginButtonOnSharedFilePage();
        loginPage.typeUserName(user);
        loginPage.typePassword(password);
        loginPage.clickLogin();

        LOG.info("Step5: Access the URL for the shared file and verify it is successfully displayed and 'Document Details' button is available on the page");
        getBrowser().get(sharedUrl);
        Assert.assertEquals(social.getContentTextFromSharedFilePage(), fileContent);
        Assert.assertTrue(social.isDocumentDetailsButtonOnSharedFilePageDisplayed(), "Document Details button is not displayed");

        LOG.info("Step6: Click 'Document details' button and verify the document's details page with correct preview is displayed");
        social.clickDocumentDetailsButtonOnSharedFilePage();
        Assert.assertEquals(documentDetails.getContentText(), fileContent);
        cleanupAuthenticatedSession();
    }
}
