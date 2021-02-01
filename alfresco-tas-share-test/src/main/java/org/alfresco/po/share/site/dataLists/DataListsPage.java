package org.alfresco.po.share.site.dataLists;

import static org.testng.Assert.*;

import org.alfresco.po.share.site.SiteCommon;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.ArrayList;
import java.util.List;

public class DataListsPage extends SiteCommon<DataListsPage>
{
    public Content currentContent = new NoListItemSelectedContent();
    private final By dataListsSection = By.cssSelector("div.datalists div.filter");
    private final By dataListsBody = By.className("datagrid");
    private final By newListButton = By.cssSelector("button[id*='newListButton']");
    private final By editListButton = By.cssSelector(".filter-link>.edit");
    private final By editButtonDisabled = By.cssSelector("span[class='edit-disabled']");
    private final By deleteListButton = By.cssSelector(".filter-link>.delete");
    private final By noListDisplayed = By.cssSelector("div[class='no-lists']");
    private final By successfullyCreatedMessage = By.cssSelector("div[id='message_c'] span[class='message']");
    private final By listWithCreatedLists = By.cssSelector(".datalists ul");
    private final By editListItemButton = By.cssSelector(".yui-dt-col-actions .onActionEdit>a");
    private final By listItemActionsField = By.cssSelector("td[headers*='actions']");
    private final By listSelected = By.cssSelector("[class='selected'] a[class='filter-link']");
    private final By tableColumnHeader = By.cssSelector("div[id$='default-grid'] th span");
    private final By newListDialogTitle = By.cssSelector(".hd");
    private final By listTitleTextInput = By.cssSelector("input[name$='prop_cm_title']");
    private final By listDescriptionTextAreaInput = By.cssSelector("textarea[title$='Content Description']");
    private final By newItemButton = By.cssSelector("div[class$='new-row'] span span button[id$='_default-newRowButton-button']");
    private final By newListSaveButton = By.cssSelector(".bdft button[id*='submit-button']");
    private final By newListCancelButton = By.cssSelector(".bdft button[id*='cancel-button']");
    private final By listType = By.cssSelector(".item-types div");
    private final By listMessage = By.cssSelector(".select-list-message");
    private final By createDataListLinkLocator = By.cssSelector("a[href='data-lists#new']");

    private final String createNewItemForm = "//form[contains(@action, '%s')]";
    private final String listLinkLocator = "//a[@class='filter-link']";
    private final String listItemTitleLocator = "//div[@class='datagrid']//h2[text()='%s']";

    public DataListsPage(ThreadLocal<WebDriver> webDriver)
    {
        super(webDriver);
    }

    @Override
    public String getRelativePath()
    {
        return String.format("share/page/site/%s/data-lists", getCurrentSiteName());
    }

    public DataListsPage assertDataListPageIsOpened()
    {
        LOG.info("Assert data list page is opened");
        assertTrue(webElementInteraction.getCurrentUrl().contains("data-lists"), "Data List page is not opened");
        return this;
    }

    public DataListsPage assertNoDataListSelectedMessageIsDisplayed()
    {
        LOG.info("Assert no data list selected message is displayed");
        assertEquals(webElementInteraction.getElementText(listMessage), language.translate("dataListPage.noListSelected.message"),
            "No list message is not displayed");
        return this;
    }

    public DataListsPage assertEmptyListMessageEquals(String emptyMessageExpected)
    {
        LOG.info("Assert empty list message equals: {}", emptyMessageExpected);
        assertEquals(webElementInteraction.getElementText(listMessage), emptyMessageExpected,
            String.format("Empty list message not equals %s ", emptyMessageExpected));

        return this;
    }

    public boolean isExpectedListSelected(String expectedList)
    {
        return webElementInteraction.findElement(listSelected).getText().equals(expectedList);
    }

    public DataListsPage assertDataListContentIsDisplayed()
    {
        LOG.info("Assert data list content is displayed");
        assertTrue(currentContent.isDataListContentDisplayed(), "Data list content is not displayed");
        return this;
    }

    public DataListsPage assertNewListDialogIsNotDisplayed()
    {
        LOG.info("Assert new list dialog is not displayed");
        assertFalse(webElementInteraction.isElementDisplayed(newListDialogTitle), "New list dialog is displayed");
        return this;
    }

    public DataListsPage clickOnCreateDataListLink()
    {
        LOG.info("Click New List button");
        webElementInteraction.findElement(createDataListLinkLocator).click();
        return this;
    }

    public DataListsPage assertNewListDialogTitleEquals(String expectedDialogTitle)
    {
        LOG.info("Assert \"New List\" dialog title is: {}", expectedDialogTitle);
        assertEquals(webElementInteraction.getElementText(newListDialogTitle), expectedDialogTitle,
            String.format("Dialog title not equals %s", expectedDialogTitle));
        return this;
    }

    public DataListsPage setTitle(String title)
    {
        LOG.info("Set title value: {}", title);
        webElementInteraction.clearAndType(listTitleTextInput, title);
        return this;
    }

    public DataListsPage setDescription(String description)
    {
        LOG.info("Set description value: {}", description);
        webElementInteraction.clearAndType(listDescriptionTextAreaInput, description);
        return this;
    }

    public DataListsPage assertDialogInputDescriptionEquals(String expectedDialogInputDescription)
    {
        LOG.info("Assert list dialog input description equals with: {}", expectedDialogInputDescription);
        assertEquals(webElementInteraction.waitUntilElementIsVisible(listDescriptionTextAreaInput)
            .getAttribute("value"), expectedDialogInputDescription, String.format(
                "List dialog input description not equals with %s ", expectedDialogInputDescription));
        return this;
    }

    public DataListsPage clickDialogSaveButton()
    {
        LOG.info("Click save button from new list dialog");
        webElementInteraction.clickElement(newListSaveButton);
        return this;
    }

    public DataListsPage clickDialogCancelButton()
    {
        LOG.info("Click cancel button from new list dialog");
        webElementInteraction.clickElement(newListCancelButton);
        return this;
    }

    public DataListsPage assertDialogInputTitleEquals(String expectedDialogInputTitle)
    {
        LOG.info("Assert list dialog input title equals with: {}", expectedDialogInputTitle);
        assertEquals(webElementInteraction.waitUntilElementIsVisible(listTitleTextInput)
            .getAttribute("value"), expectedDialogInputTitle, String.format(
                "List dialog input title not equals with %s ", expectedDialogInputTitle));
        return this;
    }

    public DataListsPage assertDataListLinkDescriptionEquals(String expectedListDescription)
    {
        LOG.info("Assert data list link description equals: {}", expectedListDescription);
        WebElement linkLocator = webElementInteraction.waitUntilElementIsVisible(By.xpath(listLinkLocator));
        assertEquals(linkLocator.getText(), expectedListDescription,
            String.format("List link description not equals %s", linkLocator.getText()));
        return this;
    }

    public List<String> getListsItemsTitle()
    {
        webElementInteraction.waitUntilElementIsVisible(listWithCreatedLists);

        List<WebElement> linksList = webElementInteraction.waitUntilElementIsVisible(dataListsSection)
            .findElements(By.cssSelector("a.filter-link"));
        List<String> dataListsName = new ArrayList<>(linksList.size());
        for (WebElement list : linksList)
        {
            dataListsName.add(list.getText());
        }
        return dataListsName;
    }

    private WebElement getDataListElement(String listName)
    {
        List<WebElement> linksList = webElementInteraction.waitUntilElementIsVisible(dataListsSection)
            .findElements(By.cssSelector("a.filter-link"));
        return webElementInteraction.findFirstElementWithValue(linksList, listName);
    }

    public boolean noListDisplayed()
    {
        return webElementInteraction.isElementDisplayed(noListDisplayed);
    }

    public DataListsPage assertNewListButtonIsDisplayed()
    {
        LOG.info("Assert new list button is displayed");
        assertTrue(webElementInteraction.isElementDisplayed(newListButton), "New list button is not displayed");
        return this;
    }

    public DataListsPage assertSelectItemsButtonIsDisplayed()
    {
        LOG.info("Assert select items button is displayed");
        assertTrue(currentContent.isSelectItemsButtonDisplayed(), "Select items button is not displayed");
        return this;
    }

    public DataListsPage assertSelectButtonIsDisplayed()
    {
        LOG.info("Assert select button is displayed");
        assertTrue(currentContent.isSelectButtonDisplayed(), "Select button is not displayed");
        return this;
    }

    public DataListsPage assertSelectAllButtonOptionIsDisplayed()
    {
        LOG.info("Assert select all button option is displayed");
        assertTrue(currentContent.isSelectAllButtonOptionDisplayed(), "Select all button option is not displayed");
        return this;
    }

    public DataListsPage assertSelectNoneButtonOptionIsDisplayed()
    {
        LOG.info("Assert select none button option is displayed");
        assertTrue(currentContent.isSelectNoneButtonOptionDisplayed(), "Select none button option is not displayed");
        return this;
    }

    public DataListsPage assertInvertSelectionButtonOptionIsEnabled()
    {
        LOG.info("Assert invert selection button option is enabled");
        assertTrue(currentContent.isInvertSelectionButtonOptionEnabled(),
            "Invert selection button option is disabled");
        return this;
    }

    public boolean isNewItemButtonDisplayed()
    {
        return webElementInteraction.isElementDisplayed(newItemButton);
    }

    private DataListsPage clickDataList(String listName, Class c)
    {
        getDataListElement(listName).click();
        try
        {
            currentContent = (Content) c.getDeclaredConstructor().newInstance();
        } catch (Exception e)
        {
            e.printStackTrace();
        }
        //todo: test
//        currentContent.setBrowser(getWebDriver());
        return this;
    }

    public DataListsPage clickContactListItem(String listName)
    {
        return clickDataList(listName, ContactListSelectedContent.class);
    }

    public DataListsPage clickEventAgendaListItem(String listName)
    {
        return clickDataList(listName, EventAgendaSelectedContent.class);
    }

    public DataListsPage clickEventListItem(String listName)
    {
        return clickDataList(listName, EventListSelectedContent.class);
    }

    public DataListsPage clickLocationListItem(String listName)
    {
        return clickDataList(listName, LocationListSelectedContent.class);
    }

    public DataListsPage clickIssueListItem(String listName)
    {
        return clickDataList(listName, IssueListSelectedContent.class);
    }

    public DataListsPage clickMeetingAgendaListItem(String listName)
    {
        return clickDataList(listName, MeetingAgendaListSelectedContent.class);
    }

    public DataListsPage clickAdvancedTaskListItem(String listName)
    {
        return clickDataList(listName, AdvancedTaskListSelectedContent.class);
    }

    public DataListsPage clickSimpleTaskListItem(String listName)
    {
        return clickDataList(listName, SimpleTaskListSelectedContent.class);
    }

    public DataListsPage clickToDoListItem(String listName)
    {
        return clickDataList(listName, ToDoListSelectedContent.class);
    }

    public DataListsPage clickVisitorFeedbackListItem(String listName)
    {
        return clickDataList(listName, VisitorFeedbackListSelectedContent.class);
    }

    public boolean isEditButtonDisplayedForList(String listName)
    {
        webElementInteraction.mouseOver(getDataListElement(listName));
        return webElementInteraction.isElementDisplayed(editListButton);
    }

    public boolean isDeleteButtonDisplayedForList(String listName)
    {
        webElementInteraction.mouseOver(getDataListElement(listName));
        return webElementInteraction.isElementDisplayed(deleteListButton);
    }

    public EditListDetailsPopUp clickEditButtonForList(String listName)
    {
        webElementInteraction.mouseOver(getDataListElement(listName));
        webElementInteraction.clickElement(editListButton);
        return new EditListDetailsPopUp(webDriver);
    }


    public DeleteListPopUp clickDeleteButtonForList(String listName)
    {
        webElementInteraction.mouseOver(getDataListElement(listName));
        webElementInteraction.clickElement(deleteListButton);
        return new DeleteListPopUp(webDriver);
    }

    public CreateDataListDialog clickOnNewListButton()
    {
        webElementInteraction.clickElement(newListButton);
        return new CreateDataListDialog(webDriver);
    }

    public String successfullyCreatedDataListMessage()
    {
        return webElementInteraction.getElementText(successfullyCreatedMessage);
    }

    public boolean isEditButtonDisabled(String listName)
    {
        webElementInteraction.mouseOver(getDataListElement(listName));
        return webElementInteraction.isElementDisplayed(editButtonDisabled);
    }

    public void clickOnDisabledEditButton(String listName)
    {
        webElementInteraction.mouseOver(getDataListElement(listName));
        webElementInteraction.clickElement(editButtonDisabled);
    }

    public DataListsPage assertDataListItemTitleEquals(String itemTitle)
    {
        LOG.info("Assert data list item title equals: {}", itemTitle);
        WebElement actualTitle = webElementInteraction.findElement(By.xpath(String.format(listItemTitleLocator, itemTitle)));
        assertEquals( actualTitle.getText(), itemTitle, String.format("Data list item title not equals %s: ", itemTitle));

        return this;
    }

    public boolean isListWithCreatedListsDisplayed()
    {
        return webElementInteraction.isElementDisplayed(listWithCreatedLists);
    }

    public EditItemPopUp clickEditButtonForListItem()
    {
        webElementInteraction.mouseOver(listItemActionsField);
        webElementInteraction.waitUntilElementIsVisible(editListItemButton).click();
        return new EditItemPopUp(webDriver);
    }

    public CreateNewItemPopUp clickNewItemButton()
    {
        webElementInteraction.waitUntilElementIsVisible(newItemButton).click();
        return new CreateNewItemPopUp(webDriver);
    }

    public List<String> getTextOfTableColumnHeader()
    {
        List<String> tableHeaderListString = new ArrayList<>();
        for (WebElement item : webElementInteraction.waitUntilElementsAreVisible(tableColumnHeader))
        {
            tableHeaderListString.add(item.getText());
        }

        return tableHeaderListString;
    }

    public boolean isNewItemPopupFormDisplayed(CreateNewItemPopUp.NewItemPopupForm listName)
    {
        return webElementInteraction.isElementDisplayed(By.xpath(String.format(createNewItemForm, listName.name)));
    }
}
