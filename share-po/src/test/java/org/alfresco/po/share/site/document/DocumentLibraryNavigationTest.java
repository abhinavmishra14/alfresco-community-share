package org.alfresco.po.share.site.document;

import static org.testng.Assert.assertTrue;

import java.io.File;

import org.alfresco.po.AbstractTest;
import org.alfresco.po.share.enums.ViewType;
import org.alfresco.po.share.site.SiteDashboardPage;
import org.alfresco.po.share.site.SiteFinderPage;
import org.alfresco.po.share.site.SitePage;
import org.alfresco.po.share.site.UploadFilePage;
import org.alfresco.po.share.site.document.TreeMenuNavigation.DocumentsMenu;

import org.alfresco.po.thirdparty.firefox.RssFeedPage;
import org.alfresco.test.FailedTestListener;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

/**
 * Unit test to verify document library page tag operations are operating correctly.
 * 
 * @author Abhijeet Bharade
 * @version 2.1
 */
@Listeners(FailedTestListener.class)
@Test(groups = "Enterprise-only")
public class DocumentLibraryNavigationTest extends AbstractTest
{
    private static String siteName;
    private static DocumentLibraryPage documentLibPage;
    private File file1;

    /**
     * Pre test setup of a dummy file to upload.
     * 
     * @throws Exception
     */
    @BeforeClass
    public void prepare() throws Exception
    {
        siteName = "site" + System.currentTimeMillis();
        file1 = siteUtil.prepareFile(siteName + "1");
        loginAs(username, password);
        siteUtil.createSite(driver, username, password, siteName, "", "Public");
        SitePage site = resolvePage(driver).render();
        documentLibPage = site.getSiteNav().selectDocumentLibrary().render();
        // uploading new files.
        UploadFilePage uploadForm = documentLibPage.getNavigation().selectFileUpload().render();
        documentLibPage = uploadForm.uploadFile(file1.getCanonicalPath()).render();
    }

    @AfterClass
    public void teardown()
    {
        SiteFinderPage siteFinder = siteUtil.searchSite(driver, siteName);
        SiteDashboardPage siteDash = siteFinder.selectSite(siteName).render();
        DocumentLibraryPage docPage = siteDash.getSiteNav().selectDocumentLibrary().render();
        docPage.getNavigation().selectDetailedView();
        siteUtil.deleteSite(username, password, siteName);
    }

    @Test(enabled = true, priority = 1)
    public void testNavigateToAudioView() throws Exception
    {
        documentLibPage = documentLibPage.getNavigation().selectAudioView().render();
        assertTrue(documentLibPage.getViewType().equals(ViewType.AUDIO_VIEW), documentLibPage.getViewType() + " was selected");
    }

    @Test(enabled = true, priority = 2)
    public void testNavigateToMediaView() throws Exception
    {
        documentLibPage = documentLibPage.getNavigation().selectMediaView().render();
        assertTrue(documentLibPage.getViewType().equals(ViewType.MEDIA_VIEW), documentLibPage.getViewType() + " was selected");
    }

    @Test(enabled = true, priority = 3)
    public void testLocateFile() throws Exception
    {
        documentLibPage = documentLibPage.getNavigation().selectDetailedView().render();
        TreeMenuNavigation treeMenuNav = documentLibPage.getLeftMenus();
        treeMenuNav.selectDocumentNode(DocumentsMenu.ALL_DOCUMENTS);
        int count = 0;
        while (count < 6)
        {
            driver.navigate().refresh();
            treeMenuNav.selectDocumentNode(DocumentsMenu.ALL_DOCUMENTS);
            if (documentLibPage.isFileVisible(file1.getName()))
            {
                break;
            }
            count++;
        }
        FileDirectoryInfo file = documentLibPage.getFileDirectoryInfo(file1.getName());
        file.selectLocateFile();
        documentLibPage.render();
        assertTrue(documentLibPage.isFileVisible(file1.getName()));
    }
    
    @Test(enabled = true, priority = 4)
    public void testRssPageAlfOneLogo() throws Exception
    {
        RssFeedPage rssPage = documentLibPage.getNavigation().selectRssFeed(username, password, siteName).render();
        String logoSrc = rssPage.getLogoImgSrc();
        Assert.assertTrue(logoSrc.contains("logo-enterprise.png"));    
        rssPage.clickOnContentLocation(file1.getName()).render();
    }

}
