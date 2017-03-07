package org.alfresco.po.share.alfrescoContent.workingWithFilesAndFolders;

import org.alfresco.po.annotation.PageObject;
import org.alfresco.po.annotation.RenderWebElement;
import org.alfresco.po.share.ShareDialog;
import org.alfresco.po.share.alfrescoContent.organizingContent.taggingAndCategorizingContent.SelectDialog;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindAll;
import org.openqa.selenium.support.FindBy;
import org.springframework.beans.factory.annotation.Autowired;
import ru.yandex.qatools.htmlelements.element.Button;
import ru.yandex.qatools.htmlelements.element.TextInput;

import java.util.List;

/**
 * Created by Claudia Agache on 9/14/2016.
 */
@PageObject
public class EditPropertiesDialog extends ShareDialog
{
    @Autowired
    SelectDialog selectDialog;

    @Autowired
    EditPropertiesPage editPropertiesPage;

    @RenderWebElement
    @FindBy(css = "div[id$='dialogTitle']")
    private WebElement dialogTitle;

    @RenderWebElement
    @FindBy(css = "input[name='prop_cm_name']")
    private TextInput propertyName;

    @FindBy(css = "input[name='prop_cm_title']")
    private TextInput propertyTitle;

    @FindBy(css = "textarea[name='prop_cm_description']")
    private TextInput propertyDescription;

    @FindBy(css = "input[id*='endpointhost']")
    private WebElement hostInputField;

    @FindBy(css = "input[id*='endpointport']")
    private WebElement portInputField;

    @FindBy(css = "input[id*='username']")
    private WebElement usernameInputField;

    @FindBy(css = "input[id*='password']")
    private WebElement passwordInputField;

    @FindBy(css = ".formsCheckBox")
    private WebElement enabledCheckbox;

    @FindBy(css = "div[id$='prop_cm_taggable-cntrl-itemGroupActions'] button")
    private WebElement selectTagsButton;

    @FindBy(css = "div[id$='prop_cm_categories-cntrl-itemGroupActions'] button")
    private Button selectCategoriesButton;

    @FindBy(css = "a[id*='editMetadata-button']")
    private WebElement allPropertiesButton;

    @FindAll(@FindBy(css = "div[id$='prop_cm_taggable-cntrl-currentValueDisplay'] div"))
    private List<WebElement> selectedTags;

    @FindAll(@FindBy(css = "div[id$='prop_cm_categories-cntrl-currentValueDisplay'] div"))
    private List<WebElement> selectedCategories;

    @RenderWebElement
    @FindBy(css = "button[id$='form-submit-button']")
    private WebElement saveButton;

    @FindBy(css = "button[id$='form-cancel-button']")
    private Button cancelButton;

    public String getDialogTitle()
    {
        return dialogTitle.getText();
    }

    public void clickSelectCategories()
    {
        selectCategoriesButton.click();
    }

    public boolean isSelectTagsButtonDisplayed()
    {
        return browser.isElementDisplayed(selectTagsButton);
    }

    public void clickSelectTags()
    {
        selectTagsButton.click();
        browser.waitUntilWebElementIsDisplayedWithRetry(saveButton, 10);
    }

    public void clickSave()
    {
        saveButton.click();
        browser.waitInSeconds(3);
    }

    public void clickCancel()
    {
        cancelButton.click();
    }

    public boolean isCategorySelected(String category)
    {
        return browser.findFirstElementWithValue(selectedCategories, category) != null;
    }

    public boolean isTagSelected(String tag)
    {
        return browser.findFirstElementWithValue(selectedTags, tag) != null;
    }

    public void setName(String fileName)
    {
        propertyName.clear();
        propertyName.sendKeys(fileName);
    }

    public void setTitle(String fileTitle)
    {
        propertyTitle.clear();
        propertyTitle.sendKeys(fileTitle);
    }

    public void setDescription(String fileDescription)
    {
        propertyDescription.clear();
        propertyDescription.sendKeys(fileDescription);
    }

    public boolean verifyAllElementsAreDisplayed()
    {
        if (!propertyName.isDisplayed())
            return false;
        if (!propertyTitle.isDisplayed())
            return false;
        if (!propertyDescription.isDisplayed())
            return false;
        if (!selectTagsButton.isDisplayed())
            return false;
        if (!saveButton.isDisplayed())
            return false;
        if (!cancelButton.isDisplayed())
            return false;
        return true;
    }

    public void typeHost(String host)
    {
        hostInputField.clear();
        hostInputField.sendKeys(host);
    }

    public void typePort(String port)
    {
        portInputField.clear();
        portInputField.sendKeys(port);
    }

    public void typeUsername(String username)
    {
        usernameInputField.clear();
        usernameInputField.sendKeys(username);
    }

    public void typePassword(String password)
    {
        passwordInputField.clear();
        passwordInputField.sendKeys(password);
    }

    public void checkEnabled()
    {
        enabledCheckbox.click();
    }

    public void updateFolderDetailsForReplication(String host, String port, String username, String password)
    {
        typeHost(host);
        typePort(port);
        typeUsername(username);
        typePassword(password);
        checkEnabled();
        clickSave();
    }

    public EditPropertiesPage clickAllPropertiesLink()

    {
        browser.waitUntilElementVisible(allPropertiesButton);
        allPropertiesButton.click();
        return (EditPropertiesPage) editPropertiesPage.renderedPage();
    }
}