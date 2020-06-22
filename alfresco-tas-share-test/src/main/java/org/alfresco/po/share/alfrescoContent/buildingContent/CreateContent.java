package org.alfresco.po.share.alfrescoContent.buildingContent;

import java.util.ArrayList;
import java.util.List;

import org.alfresco.common.Utils;
import org.alfresco.po.share.TinyMce.TinyMceEditor;
import org.alfresco.po.share.alfrescoContent.document.DocumentDetailsPage;
import org.alfresco.po.share.alfrescoContent.document.GoogleDocsCommon;
import org.alfresco.po.share.site.SiteCommon;
import org.alfresco.utility.web.HtmlPage;
import org.alfresco.utility.web.annotation.PageObject;
import org.alfresco.utility.web.annotation.RenderWebElement;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindAll;
import org.openqa.selenium.support.FindBy;
import org.springframework.beans.factory.annotation.Autowired;

@PageObject
public class CreateContent extends SiteCommon<CreateContent>
{
    public By message = By.cssSelector("span.message span.wait");
    @Autowired
    TinyMceEditor tinyMceEditor;

    @Autowired
    DocumentDetailsPage documentDetailsPage;

    @RenderWebElement
    @FindBy (css = "div[id*='_default-form-fields']")
    private WebElement createForm;
    @RenderWebElement
    @FindBy (css = "input[id*='prop_cm_name']")
    private WebElement nameField;
    @FindBy (css = "textarea[id*='prop_cm_content']")
    private WebElement contentField;
    @FindBy (css = "input[id*='prop_cm_title']")
    private WebElement titleField;
    @FindBy (css = "textarea[id*='prop_cm_description']")
    private WebElement descriptionField;
    @FindBy (xpath = "//div[@class ='form-field']//label[contains(@for,'_default_prop_cm_name')]//span[@class = 'mandatory-indicator']")
    private WebElement nameFieldIsMandatoryMarker;
    @RenderWebElement
    @FindBy (css = "button[id*='form-submit-button']")
    private WebElement submitButton;
    @FindBy (css = "button[id*='form-cancel-button']")
    private WebElement cancelButton;
    @FindBy (css = "div[class ='mce-edit-area mce-container mce-panel mce-stack-layout-item'] iframe")
    private WebElement htmlContentField;


    @FindAll (@FindBy (css = ".yuimenuitemlabel-hassubmenu-selected+.yuimenu.visible span"))
    private List<WebElement> templatesList;

    private WebElement selectCreateFromTemplateButton(String buttonName)
    {
        return browser.findElement(By.xpath("//a[contains(@class, 'yuimenuitemlabel-hassubmenu')]//span[text()='" + buttonName + "']"));
    }

    public WebElement selectTemplate(String templateName)
    {
        return browser.waitUntilElementVisible(By.xpath("//a[@class = 'yuimenuitemlabel']//span[text()='" + templateName + "']"));
    }

    @Override
    public String getRelativePath()
    {
        return String.format("share/page/site/%s/create-content", getCurrentSiteName());
    }

    /**
     * Method to check that create document from template button is available
     */
    public boolean isCreateFromTemplateAvailable(String buttonName)
    {
        return selectCreateFromTemplateButton(buttonName).isDisplayed();
    }

    /**
     * Method to check the the Name Field is present on the Create form
     */
    public boolean isNameFieldDisplayedOnTheCreateForm()
    {
        return nameField.isDisplayed();
    }

    /**
     * Method to check the the Content Field is present on the Create form
     */
    public boolean isContentFieldDisplayedOnTheCreateForm()
    {
        return contentField.isDisplayed();
    }

    /**
     * Method to check the the Title Field is present on the Create form
     */
    public boolean isTitleFieldDisplayedOnTheCreateForm()
    {
        return titleField.isDisplayed();
    }

    /**
     * Method to check the the Description Field is present on the Create form
     */
    public boolean isDescriptionFieldDisplayedOnTheCreateForm()
    {
        return descriptionField.isDisplayed();
    }

    /**
     * Method to determine if the mandatory marker is present for the Name field
     */
    public boolean isMandatoryMarkPresentForNameField()
    {
        return nameFieldIsMandatoryMarker.isDisplayed();
    }

    /**
     * Method to check if the Save button is present on the Create form
     */
    public boolean isCreateButtonPresent()
    {
        return submitButton.isDisplayed();
    }

    /**
     * Method to check if the Cancel button is present on the Create form
     */
    public boolean isCancelButtonPresent()
    {
        return cancelButton.isDisplayed();
    }

    /**
     * Method to send input to the Name field
     */
    public void sendInputForName(String name)
    {
        Utils.clearAndType(nameField, name);
    }

    /**
     * Method to send input to the Title field
     */
    public void sendInputForTitle(String title)
    {
        Utils.clearAndType(titleField, title);
    }

    /**
     * Method to send input to the Content field
     */
    public void sendInputForContent(String content)
    {
        Utils.clearAndType(contentField, content);
    }

    /**
     * Method to send input to the Description field
     */
    public void sendInputForDescription(String description)
    {
        Utils.clearAndType(descriptionField, description);
    }

    /**
     * Method to click the Create button
     */
    public DocumentDetailsPage clickCreateButton()
    {
        submitButton.click();
        return (DocumentDetailsPage) documentDetailsPage.renderedPage();
    }

    /**
     * Method to click the Cancel button
     */
    public void clickCancelButton()
    {
        cancelButton.click();
    }

    /**
     * Method to check if the HTML content field is displayed on the create form
     */
    public boolean isHTMLContentFieldDisplayed()
    {
        return htmlContentField.isDisplayed();
    }

    /**
     * Method to send input to the HTML content field
     *
     * @param inputHTMLContent
     */
    public void sendInputForHTMLContent(String inputHTMLContent)
    {
        tinyMceEditor.setText(inputHTMLContent);
    }

    /**
     * Method to check if the Content field is marked as mandatory
     *
     * @return
     */
    public boolean isContentMarkedAsMandatory()
    {
        return browser.isElementDisplayed(By.cssSelector("textarea[id*='_prop_cm_content'] span.mandatory-indicator"));
    }

    /**
     * Method to check if the Title field is marked as mandatory
     */
    public boolean isTitleMarkedAsMandatory()
    {
        return browser.isElementDisplayed(By.cssSelector("input[id*='_default_prop_cm_title'] span.mandatory-indicator"));
    }

    /**
     * Method to check if the Description field is marked as mandatory
     */
    public boolean isDescriptionMarkedAsMandatory()
    {
        return browser.isElementDisplayed(By.cssSelector("textarea[id*='_default_prop_cm_description'] span.mandatory-indicator"));
    }

    /**
     * Method to click on create Document from Template
     */
    public void clickCreateFromTemplateButton(String btnName)
    {
        browser.waitUntilElementClickable(selectCreateFromTemplateButton(btnName));
        selectCreateFromTemplateButton(btnName).click();
        if (browser.isElementDisplayed(By.cssSelector(".yuimenuitemlabel-hassubmenu-selected+.yuimenu.visible")) == false)
        {
            browser.mouseOver(selectCreateFromTemplateButton(btnName));
        }
        browser.waitUntilElementVisible(By.cssSelector(".yuimenuitemlabel-hassubmenu-selected+.yuimenu.visible"));
    }

    /**
     * Method to select template
     */
    public HtmlPage clickOnTemplate(String templateName, HtmlPage page)
    {
        selectTemplate(templateName).click();
        return page.renderedPage();
    }

    /**
     * Method to check if the template is present
     */
    public boolean isTemplateDisplayed(String templateName)
    {
        return selectTemplate(templateName).isDisplayed();
    }


    public HtmlPage clickOnDocumentTemplate(String templateName, HtmlPage page)
    {
        // browser.mouseOver(browser.findElement(By.cssSelector("li[class$='yuimenuitem-hassubmenu first-of-type']")));
        browser.waitUntilElementVisible(selectTemplate(templateName)).click();
        return page.renderedPage();
    }

    /**
     * Method to click on folder template
     */

    public HtmlPage clickOnFolderTemplate(String templateName, HtmlPage page)
    {
        browser.waitUntilElementsVisible(templatesList);
        browser.findFirstElementWithValue(templatesList, templateName).click();
        // browser.waitUntilElementVisible(selectTemplate(templateName)).click();
        return page.renderedPage();
    }

    public boolean isFileTemplateDisplayed(String templateName)
    {
        browser.mouseOver(browser.waitUntilElementVisible(By.cssSelector("li[class$='yuimenuitem-hassubmenu first-of-type']")));
        browser.waitInSeconds(2);
        if (!selectTemplate(templateName).isDisplayed())
        {
            browser.findElement(By.cssSelector("By.cssSelector(\"li[class$='yuimenuitem-hassubmenu first-of-type']")).click();
        }
        return selectTemplate(templateName).isDisplayed();
    }

    /**
     * Method to check if the template is present
     */
    public boolean isFolderTemplateDisplayed(String templateName)
    {
        List<String> templatesName = new ArrayList<>();
        browser.waitUntilElementsVisible(templatesList);
        for (WebElement template : templatesList)
        {
            templatesName.add(template.getText());
        }
        LOG.info("templates available are: " + templatesName.toArray());
        return templatesName.contains(templateName);
    }
}