package org.alfresco.po.share.alfrescoContent.organizingContent;

import org.alfresco.po.share.alfrescoContent.SelectDestinationDialog;
import org.alfresco.utility.web.annotation.PageObject;
import org.alfresco.utility.web.annotation.RenderWebElement;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import java.util.List;

/**
 * @author Laura.Capsa
 */
@PageObject
public class CopyMoveUnzipToDialog extends SelectDestinationDialog
{
    @RenderWebElement
    @FindBy(css = ".bdft button[id*='copyMoveTo']")
    protected List<WebElement> buttonsList;

    /**
     * Click on a button from the bottom of MoveTo dialog
     *
     * @param buttonName name of the button to be clicked (e.g: Move, Cancel)
     */
    public void clickButtton(String buttonName)
    {
        browser.waitInSeconds(1);
        for (WebElement aButtonsList : buttonsList)
        {
            if (aButtonsList.getText().equals(buttonName))
                aButtonsList.click();
        }
        browser.waitInSeconds(3);
    }
}