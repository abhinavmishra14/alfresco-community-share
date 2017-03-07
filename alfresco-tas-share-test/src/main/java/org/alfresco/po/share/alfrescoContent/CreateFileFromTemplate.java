package org.alfresco.po.share.alfrescoContent;

import org.alfresco.browser.WebBrowser;
import org.alfresco.po.annotation.PageObject;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.List;

/**
 * @author Razvan.Dorobantu
 */
@PageObject
public class CreateFileFromTemplate
{
    @Autowired
    @Qualifier("webBrowserInstance")
    protected WebBrowser browser;

    @FindBy(css = "li[class$='first-of-type'] a.yuimenuitemlabel.yuimenuitemlabel-hassubmenu span")
    protected List<WebElement> createDocumentFromTemplateLink;

    @FindBy(css = "ul.first-of-type span[title=\"\"]")
    protected List<WebElement> documentTemplates;

    @FindBy(css = ".create-content button")
    protected WebElement createContentButton;

    @FindBy(css = "span.message")
    protected WebElement message;

    public void clickCreateContentButton()
    {
        createContentButton.click();
    }

    public void hoverOverCreateDocumentFromTemplateLink()
    {
        for (int i = 0; i < createDocumentFromTemplateLink.size(); i++)
        {
            browser.mouseOver(createDocumentFromTemplateLink.get(i));
        }
    }

    public boolean isListOfAvailableTemplatesDisplayed()
    {
        return (documentTemplates.size() > 0);
    }

    public void clickOnFileTemplate(String templateName)
    {
        for (int i = 0; i < documentTemplates.size(); i++)
        {
            if (documentTemplates.get(i).getText().equalsIgnoreCase(templateName))
                documentTemplates.get(i).click();
        }
    }

    public boolean isDocumentCreatedMessageDisplayed()
    {
        browser.waitUntilElementClickable(message, 5);
        return message.isDisplayed();
    }
}
