/*
 * #%L
 * share-po
 * %%
 * Copyright (C) 2005 - 2016 Alfresco Software Limited
 * %%
 * This file is part of the Alfresco software. 
 * If the software was purchased under a paid Alfresco license, the terms of 
 * the paid license agreement will prevail.  Otherwise, the software is 
 * provided under the following open source license terms:
 * 
 * Alfresco is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * Alfresco is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with Alfresco. If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */
package org.alfresco.po.share.search;

import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.alfresco.po.AbstractTest;
import org.alfresco.po.share.RepositoryPage;
import org.alfresco.po.share.site.SitePage;
import org.alfresco.po.share.site.UploadFilePage;
import org.alfresco.po.share.site.document.DocumentLibraryPage;
import org.alfresco.po.share.site.document.FileDirectoryInfo;
import org.alfresco.po.share.workflow.NewWorkflowPage;
import org.alfresco.po.share.workflow.StartWorkFlowPage;
import org.alfresco.po.share.workflow.WorkFlowFormDetails;
import org.alfresco.po.share.workflow.WorkFlowType;
import org.alfresco.test.FailedTestListener;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

/**
 * UI test to verify document library page tag operations are operating correctly. 
 * @author Charu
 * 
 */
@Listeners(FailedTestListener.class)
@Test(groups = "Enterprise-only")
public class FacetedSearchBulkActionsTest extends AbstractTest
{
   // private SearchSelectedItemsMenu SearchSelectedItemsMenu;
	private static String siteName;
    private static DocumentLibraryPage documentLibPage;
    //private static FacetedSearchPage facetedSearchPage;
    private static SearchConfirmDeletePage searchConfirmDeletePage;    
    private static FacetedSearchPage resultsPage;    
    private File file1;
    private File file2;
    private String newtaskname1 = "newtask1"+ System.currentTimeMillis();

    /**
     * Pre test setup of a dummy file to upload.
     * 
     * @throws Exception
     */
    @BeforeClass(groups = "Enterprise-only")
    public void setup() throws Exception
    {
        siteName = "site" + System.currentTimeMillis();
        file1 = siteUtil.prepareFile();
        file2 = siteUtil.prepareFile();
        
        loginAs(username, password);
        
        siteUtil.createSite(driver, username, password, siteName, "", "Public");
        
        SitePage site = resolvePage(driver).render();
        
        documentLibPage = site.getSiteNav().selectDocumentLibrary().render();
        
        // uploading new files.
        UploadFilePage uploadForm = documentLibPage.getNavigation().selectFileUpload().render();
        documentLibPage = uploadForm.uploadFile(file1.getCanonicalPath()).render();
        
        UploadFilePage uploadForm1 = documentLibPage.getNavigation().selectFileUpload().render();
        documentLibPage = uploadForm1.uploadFile(file2.getCanonicalPath()).render();
        
        siteActions.search(driver, "myfile");
        
//        SearchBox search = documentLibPage.getSearch();
//
//        resultsPage = search.search("myfile").render();
        
        // TODO: Remove asserts from setup?
        
        Assert.assertTrue(siteActions.checkSearchResultsWithRetry(driver, "myfile", file2.getName(), true, 3));
        
        Assert.assertTrue(resultsPage.hasResults(),file1.getName());
        Assert.assertTrue(resultsPage.hasResults(),file2.getName());

    }

    /*@AfterClass
    public void teardown()
    {
        SiteFinderPage siteFinder = siteUtil.searchSite(driver, siteName);
        SiteDashboardPage siteDash = siteFinder.selectSite(siteName).render();
        DocumentLibraryPage docPage = siteDash.getSiteNav().selectDocumentLibrary().render();
        docPage.getNavigation().selectDetailedView();
        siteUtil.deleteSite(username, password, siteName);
    }*/
    
    @Test(groups = "Enterprise-only", priority = 1, enabled = false)
    public void testSelectMenuEnabled() throws Exception
    {
    	// Might get staleElement ref errors as resultsPage isn't latest
    	Assert.assertTrue(resultsPage.getNavigation().isSelectMenuEnabled());    	
    }
    
    @Test(groups = "Enterprise-only", priority = 2, enabled = false)
    public void testSelectAllCheckBox() throws Exception
    {    	   	
    	resultsPage.getNavigation().bulkSelect(BulkSelectCheckBox.ALL).render();
    	Assert.assertTrue(resultsPage.getNavigation().isSelectedItemsMenuEnabled());
    	Assert.assertTrue(resultsPage.getResultByName(file1.getName()).isItemCheckBoxSelected());    	
    }
    
    @Test(groups = "Enterprise-only", priority = 3, enabled = false)
    public void testSelectNoneCheckBox() throws Exception
    {    	   	
    	 SearchBox search = documentLibPage.getSearch();
         resultsPage = search.search("myfile").render();
    	resultsPage.getNavigation().bulkSelect(BulkSelectCheckBox.NONE).render();
    	Assert.assertFalse(resultsPage.getNavigation().isSelectedItemsMenuEnabled());
    	Assert.assertFalse(resultsPage.getResultByName(file1.getName()).isItemCheckBoxSelected());
    } 
    
    @Test(groups = "Enterprise-only", priority = 4, enabled = false)
    public void testSelectInvertCheckBox() throws Exception
    {    	   	
    	 SearchBox search = documentLibPage.getSearch();
         resultsPage = search.search("myfile").render();
    	resultsPage = resultsPage.getNavigation().bulkSelect(BulkSelectCheckBox.INVERT).render();
    	Assert.assertTrue(resultsPage.getNavigation().isSelectedItemsMenuEnabled());
    	Assert.assertTrue(resultsPage.getResultByName(file1.getName()).isItemCheckBoxSelected());    	
    }
       
    @Test(groups = "Enterprise-only", priority = 5, enabled = false)
    public void testSelectDownload() throws Exception
    {      	 	
    	 SearchBox search = documentLibPage.getSearch();
         resultsPage = search.search("myfile").render();
    	resultsPage.getNavigation().bulkSelect(BulkSelectCheckBox.ALL).render();
    	resultsPage.getNavigation().selectActionFromSelectedItemsMenu(SearchSelectedItemsMenu.DOWNLOAD_AS_ZIP).render();
    	Assert.assertTrue(resultsPage.hasResults(),file1.getName());    	 
    }
    
    @Test(groups = "Enterprise-only", priority = 6, enabled = false)
    public void testSelectStartWorkFlow() throws Exception
    {       		
    	//resultsPage.getNavigation().bulkSelect(BulkSelectCheckBox.ALL).render();
    	StartWorkFlowPage startWorkFlowPage = resultsPage.getNavigation().selectActionFromSelectedItemsMenu(SearchSelectedItemsMenu.START_WORKFLOW).render();
    	Assert.assertTrue(startWorkFlowPage.isWorkFlowTextPresent());
    	 NewWorkflowPage newWorkflowPage = ((NewWorkflowPage) startWorkFlowPage.getWorkflowPage(WorkFlowType.NEW_WORKFLOW)).render();

         List<String> reviewers = new ArrayList<String>();
         reviewers.add(username);
         WorkFlowFormDetails formDetails = new WorkFlowFormDetails(siteName, newtaskname1, reviewers);
         newWorkflowPage.startWorkflow(formDetails).render();
         openSiteDocumentLibraryFromSearch(driver, siteName);
         FileDirectoryInfo thisRow = documentLibPage.getFileDirectoryInfo(file1.getName());
         assertTrue(thisRow.isPartOfWorkflow(), "Document should not be part of workflow.");
    	 
    }
    
    @Test(groups = "Enterprise-only", priority = 7, enabled = false)
    public void testSelectActionCopy() throws Exception
    {   
    	SearchBox search = documentLibPage.getSearch();
    	resultsPage = search.search("myfile").render();
    	resultsPage = resultsPage.getResultByName(file1.getName()).selectItemCheckBox().render();   	
    	CopyAndMoveContentFromSearchPage copyAndMoveContentFromSearchPage = resultsPage.getNavigation().selectActionFromSelectedItemsMenu(SearchSelectedItemsMenu.COPY_TO).render();
    	Assert.assertTrue(copyAndMoveContentFromSearchPage.getDialogTitle().contains("Copy"));
    	copyAndMoveContentFromSearchPage.selectDestination("Repository").render();  
        copyAndMoveContentFromSearchPage.selectSiteInRepo("Shared").render();        
        resultsPage = copyAndMoveContentFromSearchPage.selectCopyButton().render();
        openSiteDocumentLibraryFromSearch(driver, siteName);        
        assertTrue(documentLibPage.isItemVisble(file1.getName()), "File not displayed");
        RepositoryPage repositoryPage = resultsPage.getNav().selectRepository().render();
        repositoryPage.getFileDirectoryInfo("Shared").clickOnTitle().render();        
        Assert.assertTrue(repositoryPage.isFileVisible(file1.getName()));
    }
    
    @Test(groups = "Enterprise-only", priority = 8, enabled = false)
    public void testSelectActionMove() throws Exception
    {   
    	SearchBox search = documentLibPage.getSearch();
    	resultsPage = search.search("myfile").render();
    	Assert.assertTrue(siteActions.checkSearchResultsWithRetry(driver, "myfile", file2.getName(), true, 3));
    	resultsPage = resultsPage.getResultByName(file2.getName()).selectItemCheckBox().render();   		
    	CopyAndMoveContentFromSearchPage copyAndMoveContentFromSearchPage = resultsPage.getNavigation().selectActionFromSelectedItemsMenu(SearchSelectedItemsMenu.MOVE_TO).render();
    	Assert.assertTrue(copyAndMoveContentFromSearchPage.getDialogTitle().contains("Move"));
    	copyAndMoveContentFromSearchPage.selectDestination("Repository").render();  
        copyAndMoveContentFromSearchPage.selectSiteInRepo("Shared").render();        
        resultsPage = copyAndMoveContentFromSearchPage.selectMoveButton().render();
        documentLibPage = openSiteDocumentLibraryFromSearch(driver, siteName);       
        assertFalse(documentLibPage.isItemVisble(file2.getName()), "File not displayed");           
        RepositoryPage repositoryPage = resultsPage.getNav().selectRepository().render();
        repositoryPage.getFileDirectoryInfo("Shared").clickOnTitle().render();        
        Assert.assertTrue(repositoryPage.isFileVisible(file2.getName()));
       	
    }  
        
    @Test(groups = "Enterprise-only", priority = 9, enabled = false)
    public void testSelectActionDeleteConfirmCancel() throws Exception
    {   
    	SearchBox search = documentLibPage.getSearch();
    	resultsPage = search.search("myfile").render();
    	Assert.assertTrue(siteActions.checkSearchResultsWithRetry(driver, "myfile", file2.getName(), true, 3));
    	resultsPage.getNavigation().bulkSelect(BulkSelectCheckBox.ALL).render();
    	searchConfirmDeletePage = resultsPage.getNavigation().selectActionFromSelectedItemsMenu(SearchSelectedItemsMenu.DELETE).render();
    	resultsPage = searchConfirmDeletePage.confirmCancel().render();
    	Assert.assertTrue(resultsPage.hasResults(),file1.getName());
    	
    }
    
    @Test(groups = "Enterprise-only", priority = 10, enabled = false)
    public void testSelectActionDeleteConfirmDelete() throws Exception
    {   
    	SearchBox search = documentLibPage.getSearch();
    	resultsPage = search.search("myfile").render();
    	Assert.assertTrue(siteActions.checkSearchResultsWithRetry(driver, "myfile", file2.getName(), true, 3));    	
    	resultsPage.getNavigation().bulkSelect(BulkSelectCheckBox.ALL).render(); 	
        searchConfirmDeletePage = resultsPage.getNavigation().selectActionFromSelectedItemsMenu(SearchSelectedItemsMenu.DELETE).render();
    	resultsPage = searchConfirmDeletePage.confirmDelete().render();
    	Assert.assertTrue(resultsPage.hasResults(),file1.getName());
    	
    }
    
}
