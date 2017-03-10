package org.alfresco.po.share.user.admin.adminTools.DialogPages;

import org.alfresco.utility.web.HtmlPage;
import org.alfresco.po.share.ShareDialog;
import org.alfresco.utility.web.annotation.PageObject;
import org.alfresco.utility.web.annotation.RenderWebElement;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

/**
 * Created by Mirela Tifui on 12/6/2016.
 */
@PageObject
public class CreateCustomTypeDialog extends ShareDialog
{
    @RenderWebElement
    private By createCustomTypeWindow = By.id("CMM_CREATE_TYPE_DIALOG");

    @RenderWebElement
    private By createButton = By.id("CMM_CREATE_TYPE_DIALOG_OK_label");

    @RenderWebElement
    private By cancelButton = By.id("CMM_CREATE_TYPE_DIALOG_CANCEL_label");

    @FindBy(xpath ="//div[@id ='CMM_CREATE_TYPE_DIALOG']//input[@name='name']")
    private WebElement nameField;

    @FindBy(xpath ="//div[@id ='CMM_CREATE_TYPE_DIALOG']//input[@name='title']")
    private WebElement displayLabelField;

    @FindBy(xpath = "//div[@id ='CMM_CREATE_TYPE_DIALOG']//div[@class='control']//textarea")
    private WebElement descriptionField;

    public HtmlPage clickCreateButton(HtmlPage page)
    {
        browser.findElement(createButton).click();
        return page.renderedPage();
    }

    public void clickCancelButton()
    {
        browser.findElement(cancelButton).click();
    }

    public void sendNameInput(String name)
    {
        nameField.clear();
        nameField.sendKeys(name);
    }

    public void sendDisplayLabelInput(String displayLabel)
    {
        displayLabelField.clear();
        displayLabelField.sendKeys(displayLabel);
    }

    public void sendDescriptionFieldInput(String description)
    {
        descriptionField.clear();
        descriptionField.sendKeys(description);
    }

    public boolean isCreateCustomTypeWindowDisplayed()
    {
        browser.waitUntilElementVisible(createCustomTypeWindow);
        return browser.isElementDisplayed(createCustomTypeWindow);
    }

}
