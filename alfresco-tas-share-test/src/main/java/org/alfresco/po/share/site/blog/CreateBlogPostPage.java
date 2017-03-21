package org.alfresco.po.share.site.blog;

import org.alfresco.po.share.site.SiteCommon;
import org.alfresco.utility.web.annotation.PageObject;
import org.alfresco.utility.web.annotation.RenderWebElement;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

@PageObject
public class CreateBlogPostPage extends SiteCommon<CreateBlogPostPage>
{
    private By pageTitle = By.xpath("//div[@id ='bd']//div[@class = 'page-form-header']//h1");

    @RenderWebElement
    @FindBy(css = "input[id*='_default-title']")
    private WebElement titleField;

    @RenderWebElement
    @FindBy(xpath = "//div[@class = 'mce-edit-area mce-container mce-panel mce-stack-layout-item']")
    private WebElement frame;

    @FindBy(xpath = "//div[@class = 'taglibrary']//input")
    private WebElement tagsField;

    @FindBy(xpath = "//div[@class = 'taglibrary']//span[@class = 'yui-button yui-push-button']//button[text()='Add']")
    private WebElement addTagButton;

    private By deleteTagButton = By.xpath("//div[@class = 'taglibrary']//a[@class = 'taglibrary-action']//span[@class = 'remove']");

    private By publishInternallyButton = By.cssSelector("button[id$='_default-publish-button-button']");

    public WebElement findTag(String Tag)
    {
        return browser.findElement(By.xpath("//div[@class = 'taglibrary']//span[text() = '" + Tag + "']"));
    }

    private By cancelButton = By.cssSelector("button[id$='_default-cancel-button-button']");

    private By saveAsDraftButton = By.cssSelector("button[id$='_default-save-button-button']");

    @Override
    public String getRelativePath()
    {
        return String.format("share/page/site/%s/blog-postedit", getCurrentSiteName());
    }

    /**
     * Method to ge the Page title
     */
    public String getPageTitle()
    {
        return browser.findElement(pageTitle).getText();
    }

    /**
     * Method to send input to the Title field
     * 
     * @param newBlogPostTitle
     */
    public void sendTitleInput(String newBlogPostTitle)
    {
        titleField.sendKeys(newBlogPostTitle);
    }

    /**
     * Method to send input to the Content field
     * 
     * @param blogPostContentText
     */
    public void sendBlogPostTextInput(String blogPostContentText)
    {
        browser.switchTo().frame(browser.findElement(By.xpath("//div[@class = 'mce-edit-area mce-container mce-panel mce-stack-layout-item']//iframe")));
        WebElement element = browser.findElement(By.id("tinymce"));
        element.sendKeys(blogPostContentText);
        browser.switchTo().defaultContent();
    }

    /**
     * Method to send input to the Tag field
     * 
     * @param Tag
     */
    public void sendTagsInput(String Tag)
    {
        tagsField.sendKeys(Tag);
    }

    /**
     * Method to click the Add tag button
     */
    public void clickAddTagButton()
    {
        addTagButton.click();
    }

    /**
     * Method to click the delete tag button for the selected tag
     * 
     * @param Tag
     */
    public void clickDeleteTag(String Tag)
    {
        browser.mouseOver(findTag(Tag));
        browser.findElement(By.xpath("//div[@class = 'taglibrary']//a[@class = 'taglibrary-action']")).click();
    }

    /**
     * Method to get the Tag text
     * 
     * @param Tag
     * @return
     */
    public String getTagText(String Tag)
    {
        return findTag(Tag).getText();
    }

    /**
     * Method to check if Tag is present
     * 
     * @param Tag
     * @return
     */
    public boolean isTagPresent(String Tag)
    {
        return browser.isElementDisplayed(By.xpath("//div[@class = 'taglibrary']//span[text() = '" + Tag + "']"));
    }

    /**
     * Method to check if the blog post content is displayed
     * 
     * @param title
     * @return
     */
    public boolean isBlogPostContentDisplayed(String title)
    {
        return browser.isElementDisplayed(By.xpath(".//div[@class = 'content yuieditor']"));
    }

    /**
     * Method to check if the Delete button is available for the selected tag
     * 
     * @param Tag
     * @return
     */
    public boolean isDeleteButtonAvailable(String Tag)
    {
        browser.mouseOver(findTag(Tag));
        return browser.isElementDisplayed(By.xpath("//div[@class = 'taglibrary']//a[@class = 'taglibrary-action']//span[@class = 'remove']"));
    }

    /**
     * Method to click Publish Internally button
     */
    public void clickPublishInternally()
    {
        browser.findElement(publishInternallyButton).click();
    }

    /**
     * Method to click the Cancel button
     */
    public void clickCancelButton()
    {
        browser.findElement(cancelButton).click();
    }

    /**
     * Method to click Save As Draft button
     */
    public void clickSaveAsDraftButton()
    {
        browser.findElement(saveAsDraftButton).click();
    }
}
