package org.alfresco.po.share.site.dataLists;

import org.alfresco.po.share.site.SiteCommon;
import org.alfresco.utility.web.annotation.PageObject;
import org.alfresco.utility.web.annotation.RenderWebElement;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

@PageObject
public class DataListsPage extends SiteCommon<DataListsPage>
{
    @Autowired
    CreateDataListPopUp createDataListPopUp;
    
    @Autowired 
    EditListDetailsPopUp editListDetailsPopUp;

    @Autowired 
    DeleteListPopUp deleteListPopUp;

    @RenderWebElement
    @FindBy(css = "div.datalists div.filter")
    protected WebElement dataListsSection;
    
    @RenderWebElement
    @FindBy(css = "button[id*='newListButton']")
    protected WebElement newListButton;

    @FindBy(css = "div[id*='datalists'] span[class='edit']")
    protected WebElement editListButton;
    
    @FindBy(css = "span[class='edit-disabled']")
    protected WebElement editButtonDisabled;
    
    @FindBy(css = "div[id*='datalists'] span[class='delete']")
    protected WebElement deleteListButton;
    
    @FindBy(css = "div[class='no-lists']")
    protected WebElement noListDisplayed;
    
    @FindBy(css = "div[id='message_c'] span[class='message']")
    protected WebElement successfullyCreatedMessage;
    
    @FindBy(css = ".datalists ul")
    protected WebElement listWithCreatedLists;
       
    @FindBy(css = "table a[title='Edit']")
    protected WebElement editListItemButton;

    @FindBy(css = "td[headers*='actions']")
    protected WebElement listItemActionsField;
    
    public Content currentContent = (Content) new NoListItemSelectedContent(); 
    
    @Override
    public String getRelativePath()
    {
        return String.format("share/page/site/%s/data-lists", getCurrentSiteName());
    }
    
    public void setListItemSelectedContent()
    {
        currentContent = new ListItemSelectedContent();
        currentContent.setBrowser(browser);
    }
    
    public List<String> getListsDisplayName()
    {
        List<WebElement> linksList = dataListsSection.findElements(By.cssSelector("a.filter-link"));
        List<String> dataListsName = new ArrayList<>(linksList.size());
        for(WebElement list : linksList)
        {
            dataListsName.add(list.getText());
        }
        return dataListsName;
    }

    private WebElement getDataListElement(String listName)
    {
        List<WebElement> linksList = dataListsSection.findElements(By.cssSelector("a.filter-link"));
        return browser.findFirstElementWithValue(linksList, listName);
    }

    public boolean noListDisplayed()
    {
        return noListDisplayed.isDisplayed();
    }
    
    public boolean isNewListButtonDisplayed()
    {
        return browser.isElementDisplayed(newListButton);
    }
    
    private DataListsPage clickDataList(String listName, Class c)
    {
        getDataListElement(listName).click();
        browser.waitInSeconds(3);
        try
        {
            currentContent = (Content) c.newInstance();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        currentContent.setBrowser(browser);
        return this;
    }
    
    public DataListsPage clickContactListItem(String listName)
    {
        return clickDataList(listName, ContactListSelectedContent.class);
    }
    
    public DataListsPage clickEventAgendaListItem(String listName)
    {
        return clickDataList(listName, EventAgendaSelectedContent.class );
    }
    
    public DataListsPage clickEventListItem(String listName)
    {
        return clickDataList(listName, EventListSelectedContent.class );
    }
    
    public DataListsPage clickLocationListItem(String listName)
    {
        return clickDataList(listName, LocationListSelectedContent.class );
    }
    
    public DataListsPage clickIssueListItem(String listName)
    {
        return clickDataList(listName, IssueListSelectedContent.class );
    }
    
    public DataListsPage clickMeetingAgendaListItem(String listName)
    {
        return clickDataList(listName, MeetingAgendaListSelectedContent.class );
    }
    
    public DataListsPage clickAdvancedTaskListItem(String listName)
    {
        return clickDataList(listName, AdvancedTaskListSelectedContent.class );
    }
    
    public DataListsPage clickSimpleTaskListItem(String listName)
    {
        return clickDataList(listName, SimpleTaskListSelectedContent.class );
    }
    
    public DataListsPage clickToDoListItem(String listName)
    {
        return clickDataList(listName, ToDoListSelectedContent.class );
    }
    
    public DataListsPage clickVisitorFeedbackListItem(String listName)
    {
        return clickDataList(listName, VisitorFeedbackListSelectedContent.class );
    }
    
    public boolean isEditButtonDisplayedForList(String listName)
    {
        browser.mouseOver(getDataListElement(listName));
        return browser.isElementDisplayed(editListButton);
    }
    
    public boolean isDeleteButtonDisplayedForList(String listName)
    {
        browser.mouseOver(getDataListElement(listName));
        return browser.isElementDisplayed(deleteListButton);
    }
    
    public EditListDetailsPopUp clickEditButtonForList(String listName)
    {
        browser.mouseOver(getDataListElement(listName));
        editListButton.click();
        return (EditListDetailsPopUp) editListDetailsPopUp.renderedPage();
    }
    

    public DeleteListPopUp clickDeleteButtonForList(String listName)
    {
        browser.mouseOver(getDataListElement(listName));
        deleteListButton.click();
        return (DeleteListPopUp) deleteListPopUp.renderedPage();
    }
    
    public CreateDataListPopUp clickOnNewListButton()
    {
        newListButton.click();
        return (CreateDataListPopUp) createDataListPopUp.renderedPage();
    }
    
    public String successfullyCreatedDataListMessage()
    {
        return successfullyCreatedMessage.getText();
    }
    
    public boolean isEditButtonDisabled(String listName)
    {
        browser.mouseOver(getDataListElement(listName));
        return editButtonDisabled.isDisplayed();
    }
    
    public void clickOnDisabledEditButton(String listName)
    {
        browser.mouseOver(getDataListElement(listName));
        editButtonDisabled.click();
    }
    
    public boolean isDataListTitleDisplayed(String listsDisplayName) 
    {
        return getListsDisplayName().contains(listsDisplayName);
    }
    
    public boolean isListWithCreatedListsDisplayed()
    {
        return browser.isElementDisplayed(listWithCreatedLists);
    }
    
    public void clickEditButtonForListItem()
    {
    	browser.mouseOver(listItemActionsField);    	
        editListItemButton.click();
    }

    public void clickNewItemButton()
    {
        browser.waitUntilElementVisible(By.cssSelector("div[class$='new-row'] span span button[id$='_default-newRowButton-button']")).click();
    }
}
