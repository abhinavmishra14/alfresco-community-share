package org.alfresco.po.share.site.dataLists;

import org.alfresco.po.share.site.SelectPopUpPage;
import org.alfresco.utility.web.annotation.PageObject;
import org.alfresco.utility.web.annotation.RenderWebElement;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

@PageObject
public class AssignUserToAdvancedTaskPopUpPage extends SelectPopUpPage
{
    @RenderWebElement
    @FindBy(css = "button[id$=taskAssignee-cntrl-ok-button]")
    private WebElement okButton;

    @FindBy(css = "[class*=picker-header] input[id*='taskAssignee-cntrl-picker-searchText']")
    private WebElement searchField;

    @FindBy(css = "button[id$='searchButton-button']")
    private WebElement searchButton;

    private By addIcon = By.cssSelector("a[class$='taskAssignee-cntrl'] [class*=addIcon]");

    public void clickAddIcon(String item)
    {
        browser.waitUntilElementsVisible(addIcon);
        selectDetailsRow(item).findElement(addIcon).click();
    }

    public void clickOkButton()
    {
        okButton.click();
    }

    public void searchUser(String userName)
    {
        browser.waitUntilElementVisible(By.cssSelector("[class*=picker-header] input[id*='taskAssignee-cntrl-picker-searchText']"));
        searchField.sendKeys(userName);
        searchButton.click();
    }
}
