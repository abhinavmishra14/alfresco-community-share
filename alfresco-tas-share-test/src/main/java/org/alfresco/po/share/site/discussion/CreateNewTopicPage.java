package org.alfresco.po.share.site.discussion;

import org.alfresco.po.annotation.PageObject;
import org.alfresco.po.annotation.RenderWebElement;
import org.alfresco.po.share.site.SiteCommon;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindAll;
import org.openqa.selenium.support.FindBy;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Claudia Agache on 8/8/2016.
 */
@PageObject
public class CreateNewTopicPage extends SiteCommon<CreateNewTopicPage>
{
    @Autowired
    TopicListPage topicListPage;

    @RenderWebElement
    @FindBy(css = ".page-form-header>h1")
    protected WebElement pageHeader;

    @RenderWebElement
    @FindBy(css = "button[id$='discussions-createtopic_x0023_default-submit-button']")
    protected WebElement saveButton;

    @FindBy(css = "button[id$='discussions-createtopic_x0023_default-cancel-button']")
    protected WebElement cancelButton;

    @RenderWebElement
    @FindBy(css = "input[id$='discussions-createtopic_x0023_default-title']")
    protected WebElement topicTitle;

    @FindBy(css = "input[id$='discussions-createtopic_x0023_default-tag-input-field']")
    protected WebElement tagInput;

    @FindBy(css = "button[id*='discussions-createtopic_x0023_default-add-tag-button']")
    protected WebElement addTagButton;

    @FindAll(@FindBy(css = "ul[id$='discussions-createtopic_x0023_default-current-tags'] .taglibrary-action"))
    protected List<WebElement> currentTagList;

    protected By topicContent = By.cssSelector("iframe[id*='discussions-createtopic_x0023_default-content']");

    @Override
    public String getRelativePath()
    {
        return String.format("share/page/site/%s/discussions-createtopic", getCurrentSiteName());
    }

    /**
     * Check if page is opened
     *
     * @return true if page header is Create New Topic
     */
    public boolean isPageDisplayed()
    {
        return pageHeader.getText().equals("Create New Topic");
    }

    /**
     * Method used to type topic title
     */
    public void typeTopicTitle(String title)
    {
        topicTitle.clear();
        topicTitle.sendKeys(title);
    }

    /**
     * Method used to type topic content
     */
    public void typeTopicContent(String content)
    {
        browser.switchToFrame(browser.findElement(topicContent).getAttribute("id"));
        WebElement editable = browser.switchTo().activeElement();
        editable.clear();
        editable.sendKeys(Keys.chord(Keys.CONTROL, "a"), content);
        browser.switchToDefaultContent();
    }

    /**
     * Click on save button
     *
     * @return wiki page
     */
    public TopicViewPage clickSaveButton()
    {
        saveButton.click();
        return new TopicViewPage();
    }

    /**
     * Click on cancel button
     *
     * @return wiki pages list
     */
    public TopicListPage clickCancelButton()
    {
        cancelButton.click();
        return (TopicListPage) topicListPage.renderedPage();
    }

    /**
     * Method used to add tag
     */
    public void addTag(String tagName)
    {
        tagInput.clear();
        tagInput.sendKeys(tagName);
        addTagButton.click();
    }

    /**
     * This method returns the list of topic tags
     *
     * @return list of wiki page titles
     */
    public List<String> getTopicCurrentTagsList()
    {
        List<String> topicTags = new ArrayList<>();
        for (WebElement tag : currentTagList)
        {
            topicTags.add(tag.getText());
        }
        return topicTags;
    }

    public String getTopicTitle()
    {
        browser.waitInSeconds(1);
        return topicTitle.getAttribute("value");
    }

    public String getTopicContent()
    {
        browser.switchToFrame(browser.findElement(topicContent).getAttribute("id"));
        WebElement editable = browser.switchTo().activeElement();
        String editableText = editable.getText();
        browser.switchToDefaultContent();
        return editableText;
    }

    /**
     * Remove tag by clicking on it
     *
     * @param tag
     */
    public void removeTag(String tag)
    {
        browser.findFirstElementWithValue(currentTagList, tag).click();
    }
}