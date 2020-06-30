package org.alfresco.po.share.user.admin.adminTools.DialogPages;

import static org.alfresco.common.Utils.clearAndType;

import java.util.List;

import org.alfresco.po.share.ShareDialog;
import org.alfresco.po.share.user.admin.adminTools.ModelDetailsPage;
import org.alfresco.utility.web.annotation.PageObject;
import org.alfresco.utility.web.annotation.RenderWebElement;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindAll;
import org.openqa.selenium.support.FindBy;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by Mirela Tifui on 12/6/2016.
 */
@PageObject
public class CreateCustomTypeDialog extends ShareDialog
{
    @Autowired
    ModelDetailsPage modelDetailsPage;
    @RenderWebElement
    @FindBy (id = "CMM_CREATE_TYPE_DIALOG")
    private WebElement createCustomTypeWindow;

    @RenderWebElement
    @FindBy (css = "span[widgetid='CMM_CREATE_TYPE_DIALOG_OK']>span")
    private WebElement createButton;

    @RenderWebElement
    @FindBy (id = "CMM_CREATE_TYPE_DIALOG_CANCEL_label")
    private WebElement cancelButton;

    @FindBy (xpath = "//div[@id ='CMM_CREATE_TYPE_DIALOG']//input[@name='name']")
    private WebElement nameField;

    @FindBy (xpath = "//div[@id ='CMM_CREATE_TYPE_DIALOG']//input[@name='title']")
    private WebElement displayLabelField;

    @FindBy (xpath = "//div[@id ='CMM_CREATE_TYPE_DIALOG']//div[@class='control']//textarea")
    private WebElement descriptionField;

    @FindBy (css = ".dijitSelectLabel")
    private WebElement parentTypeElement;

    @FindAll (@FindBy (css = "table[role='listbox'] > tbody > tr[id^='dijit_MenuItem']"))
    private List<WebElement> parentTypeElements;

    public ModelDetailsPage clickCreateButton()
    {
        getBrowser().waitUntilElementClickable(createButton).click();
        return (ModelDetailsPage) modelDetailsPage.renderedPage();
    }

    public void selectParentType(String parentType)
    {
        getBrowser().waitUntilElementVisible(parentTypeElement).click();
        for (WebElement type : parentTypeElements)
        {
            if (type.getAttribute("aria-label").equals(parentType))
            {
                getBrowser().clickJS(type);
            }
        }
    }


    public void clickCancelButton()
    {
        cancelButton.click();
    }

    public void sendNameInput(String name)
    {
        clearAndType(nameField, name);
    }

    public void sendDisplayLabelInput(String displayLabel)
    {
        clearAndType(displayLabelField, displayLabel);
    }

    public void sendDescriptionFieldInput(String description)
    {
        clearAndType(descriptionField, description);
    }

    public boolean isCreateCustomTypeWindowDisplayed()
    {
        return browser.isElementDisplayed(createCustomTypeWindow);
    }

}
