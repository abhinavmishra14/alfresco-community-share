package org.alfresco.po.share.alfrescoContent;

import org.alfresco.po.share.navigation.AccessibleByMenuBar;
import org.alfresco.po.share.site.DocumentLibraryPage;
import org.alfresco.po.share.toolbar.Toolbar;
import org.alfresco.utility.web.annotation.PageObject;
import org.alfresco.utility.web.common.Parameter;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.springframework.beans.factory.annotation.Autowired;

@PageObject
public class RepositoryPage extends DocumentLibraryPage implements AccessibleByMenuBar
{
    @Autowired
    Toolbar toolbar;

    @Override
    public String getRelativePath()
    {
        return "share/page/repository";
    }

    @SuppressWarnings ("unchecked")
    @Override
    public RepositoryPage navigateByMenuBar()
    {
        toolbar.clickRepository();
        return (RepositoryPage) renderedPage();
    }
}
