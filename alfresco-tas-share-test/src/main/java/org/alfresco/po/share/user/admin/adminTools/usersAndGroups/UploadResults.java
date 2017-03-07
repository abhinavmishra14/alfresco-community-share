package org.alfresco.po.share.user.admin.adminTools.usersAndGroups;

import org.alfresco.po.annotation.PageObject;
import org.alfresco.po.annotation.RenderWebElement;
import org.alfresco.po.share.SharePage;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

@PageObject
public class UploadResults extends SharePage<UploadResults>
{

    @RenderWebElement
    @FindBy(css = "button[id*='default-csv-goback-button-button']")
    private WebElement goBackButton;

    public void clickGoBack()
    {
        goBackButton.click();
    }

    @Override
    public String getRelativePath()
    {
        // TODO Auto-generated method stub
        return null;
    }

}
