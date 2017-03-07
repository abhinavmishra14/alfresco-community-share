package org.alfresco.share.adminTools.nodeBrowser;

import org.alfresco.common.DataUtil;
import org.alfresco.dataprep.CMISUtil;
import org.alfresco.dataprep.ContentService;
import org.alfresco.dataprep.SiteService;
import org.alfresco.po.share.user.admin.adminTools.NodeBrowserPage;
import org.alfresco.share.ContextAwareWebTest;
import org.alfresco.testrail.TestRail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.social.alfresco.api.entities.Site;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.List;

import static org.testng.Assert.assertTrue;

/**
 * @author Razvan.Dorobantu
 */
public class NodeBrowserTests extends ContextAwareWebTest
{
    @Autowired
    NodeBrowserPage nodeBrowserPage;

    @Autowired
    protected SiteService siteService;

    @Autowired
    ContentService contentService;

    private String description = "nodeBrowserTests" + DataUtil.getUniqueIdentifier();
    private String siteName = "nodeBrowserTests" + DataUtil.getUniqueIdentifier();
    private String fileName = "nodeBrowserTests.xml" + DataUtil.getUniqueIdentifier();
    private String content = "nodeBrowserTestsContent";
    private String xpathSearchTerm = String.format("/app:company_home/st:sites/cm:%s/cm:documentLibrary/cm:%s",siteName,fileName);
    private String cmisSearchTerm = String.format("SELECT * from cmis:document where cmis:name =  '%s'",fileName);

    @BeforeClass
    public void beforeClass()
    {
        siteService.create(adminUser, adminPassword, domain, siteName, description, Site.Visibility.PUBLIC);
        contentService.createDocument(adminUser, adminPassword, siteName, CMISUtil.DocumentType.XML, fileName, content);
        setupAuthenticatedSession(adminUser, adminPassword);
        nodeBrowserPage.navigateByMenuBar();
    }

    @TestRail(id = "C9309")
    @Test
    public void luceneSearch()
    {
        LOG.info("Step 1: Do a 'lucene' search.");
        nodeBrowserPage.selectSearchType(NodeBrowserPage.SEARCH_TYPE.LUCENE);
        nodeBrowserPage.selectStoreType(NodeBrowserPage.SELECT_STORE.WORKSPACE_SPACES_STORE);
        nodeBrowserPage.writeInSearchInput(content);
        nodeBrowserPage.clickSearchButton();
        browser.waitInSeconds(4);

        LOG.info("Step 2: Verify if the file created in precondition is displayed and its parent is correct.");
        List<String> values = nodeBrowserPage.getResults().get(fileName);
        assertTrue(values.get(0).contains(siteName), String.format("Parent result for %s is wrong.", fileName));
    }

    @TestRail(id = "C9307")
    @Test
    public void nodeRefSearch()
    {

        LOG.info("Step 1: Do a 'nodeRef' search.");
        String nodeRef = contentService.getNodeRef(adminUser, adminPassword, siteName, fileName);
        nodeBrowserPage.selectSearchType(NodeBrowserPage.SEARCH_TYPE.NODEREF);
        nodeBrowserPage.selectStoreType(NodeBrowserPage.SELECT_STORE.WORKSPACE_SPACES_STORE);
        nodeBrowserPage.writeInSearchInput("workspace://SpacesStore/" + nodeRef);
        nodeBrowserPage.clickSearchButton();
        browser.waitInSeconds(4);

        LOG.info("Step 2: Verify if the file created in precondition is displayed and its parent is correct.");
        List<String> values = nodeBrowserPage.getResults().get(fileName);
        assertTrue(values.get(0).contains(siteName), String.format("Parent result for %s is wrong.", fileName));
    }

    @TestRail(id = "C9308")
    @Test
    public void xpathSearch()
    {
        LOG.info("Step 1: Do a 'xpath' search.");
        nodeBrowserPage.selectSearchType(NodeBrowserPage.SEARCH_TYPE.XPATH);
        nodeBrowserPage.selectStoreType(NodeBrowserPage.SELECT_STORE.WORKSPACE_SPACES_STORE);
        nodeBrowserPage.writeInSearchInput(xpathSearchTerm);
        nodeBrowserPage.clickSearchButton();
        browser.waitInSeconds(4);

        LOG.info("Step 2: Verify if the file created in precondition is displayed and its parent is correct.");
        List<String> values = nodeBrowserPage.getResults().get(fileName);
        assertTrue(values.get(0).contains(siteName), String.format("Parent result for %s is wrong.", fileName));
    }

    @TestRail(id = "C9310")
    @Test
    public void ftsAlfrescoSearch()
    {
        LOG.info("Step 1: Do a 'fts-alfresco' search.");
        nodeBrowserPage.selectSearchType(NodeBrowserPage.SEARCH_TYPE.FTS_ALFRESCO);
        nodeBrowserPage.selectStoreType(NodeBrowserPage.SELECT_STORE.WORKSPACE_SPACES_STORE);
        nodeBrowserPage.writeInSearchInput("cm:name:" + fileName);
        nodeBrowserPage.clickSearchButton();
        browser.waitInSeconds(4);

        LOG.info("Step 2: Verify if the file created in precondition is displayed and its parent is correct.");
        List<String> values = nodeBrowserPage.getResults().get(fileName);
        assertTrue(values.get(0).contains(siteName), String.format("Parent result for %s is wrong.", fileName));
    }

    @TestRail(id = "C9311")
    @Test
    public void cmisStrictSearch()
    {
        LOG.info("Step 1: Do a 'cmis-strict' search.");
        nodeBrowserPage.selectSearchType(NodeBrowserPage.SEARCH_TYPE.CMIS_STRICT);
        nodeBrowserPage.selectStoreType(NodeBrowserPage.SELECT_STORE.WORKSPACE_SPACES_STORE);
        nodeBrowserPage.writeInSearchInput(cmisSearchTerm);
        nodeBrowserPage.clickSearchButton();
        browser.waitInSeconds(4);

        LOG.info("Step 2: Verify if the file created in precondition is displayed and its parent is correct.");
        List<String> values = nodeBrowserPage.getResults().get(fileName);
        assertTrue(values.get(0).contains(siteName), String.format("Parent result for %s is wrong.", fileName));
    }

    @TestRail(id = "C9312")
    @Test
    public void cmisAlfrescoSearch()
    {
        LOG.info("Step 1: Do a 'cmis-alfresco' search.");
        nodeBrowserPage.selectSearchType(NodeBrowserPage.SEARCH_TYPE.CMIS_ALFRESCO);
        nodeBrowserPage.selectStoreType(NodeBrowserPage.SELECT_STORE.WORKSPACE_SPACES_STORE);
        nodeBrowserPage.writeInSearchInput(cmisSearchTerm);
        nodeBrowserPage.clickSearchButton();
        browser.waitInSeconds(4);

        LOG.info("Step 2: Verify if the file created in precondition is displayed and its parent is correct.");
        List<String> values = nodeBrowserPage.getResults().get(fileName);
        assertTrue(values.get(0).contains(siteName), String.format("Parent result for %s is wrong.", fileName));
    }

    @TestRail(id = "C9306")
    @Test
    public void verifyNodeBrowserPage()
    {
        LOG.info("Step 1: Login as administrator and navigate to Admin Tools - Node Browser page.");
        cleanupAuthenticatedSession();
        setupAuthenticatedSession(adminUser, adminPassword);
        nodeBrowserPage.navigateByMenuBar();

        LOG.info("Step 2: Verify if the items on the page are displayed correctly.");
        assertTrue(nodeBrowserPage.isSearchTypeSelected(NodeBrowserPage.SEARCH_TYPE.FTS_ALFRESCO));
        assertTrue(nodeBrowserPage.isStoreTypeSelected(NodeBrowserPage.SELECT_STORE.WORKSPACE_SPACES_STORE));
        assertTrue(nodeBrowserPage.isNameColumnPresent());
        assertTrue(nodeBrowserPage.isParentColumnPresent());
        assertTrue(nodeBrowserPage.isReferenceColumnPresent());
        assertTrue(nodeBrowserPage.isSearchButtonPresent());
    }
}
