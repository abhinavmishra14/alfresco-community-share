package org.alfresco.po.share.systemsummary.directorymanagement;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.ArrayList;
import java.util.List;

import org.alfresco.po.HtmlPage;
import org.alfresco.po.RenderTime;
import org.alfresco.po.RenderWebElement;
import org.alfresco.po.exception.PageOperationException;
import org.alfresco.po.share.systemsummary.AdvancedAdminConsolePage;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;

/**
 * Class associated with page in admin console system summary page 'Directory Manage'
 * 
 * @author Aliaksei Boole
 */
public class DirectoryManagementPage extends AdvancedAdminConsolePage
{
    @RenderWebElement
    private final static By COMPANY_DIRECTORY_NAME_INPUT = By.xpath("//input[@id='dm-name']");
    @RenderWebElement
    private final static By SELECT_DIRECTORY_TYPE = By.xpath("//select[@id='dm-type']");
    @RenderWebElement
    private final static By ADD_DIRECTORY_BUTTON = By.xpath("//input[@value='Add']");
    @RenderWebElement
    private final static By RUN_SYNC_BUTTON = By.xpath("//input[@value='Run Synchronize']");
    @RenderWebElement
    private final static By SYNC_SETTINGS = By.xpath("//input[@value='Synchronization Settings']");
    @RenderWebElement
    private final static By SAVE_BUTTON = By.xpath("//input[@value='Save']");
    @RenderWebElement
    private final static By CANCEL_BUTTON = By.xpath("//input[@value='Cancel']");

    private final static By DIRECTORY_INFO_ROW = By.xpath("//table[@id='dm-authtable']/tbody/tr/td/..");
    private final static By SYNC_STATUS = By.xpath("//p[@style='padding-left:3em']/b");

    // TestSyncPopUp
    private final static By RUN_TEST_SYNC = By.xpath("//input[contains(@onclick,'testSync()')]");
    private final static By TEST_STATUS = By.xpath("//p[@id='test-auth-passed']");

    @SuppressWarnings("unchecked")
    @Override
    public DirectoryManagementPage render(RenderTime timer)
    {
        webElementRender(timer);
        return this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public DirectoryManagementPage render()
    {
        return render(new RenderTime(maxPageLoadingTime));
    }

    /**
     * Add new authChain.
     * 
     * @param authType AuthType
     * @param authName String
     */
    public HtmlPage addAuthChain(AuthType authType, String authName)
    {
        fillField(COMPANY_DIRECTORY_NAME_INPUT, authName);
        selectAuthChainType(authType);
        click(ADD_DIRECTORY_BUTTON);
        click(SAVE_BUTTON);
        return getCurrentPage();
    }

    private void selectAuthChainType(AuthType authType)
    {
        checkNotNull(authType);
        WebElement selectAuthElem = findAndWait(SELECT_DIRECTORY_TYPE);
        Select authSelect = new Select(selectAuthElem);
        authSelect.selectByValue(authType.value);
        waitUntilAlert();
    }

    /**
     * Return list of objects associated with Auth Chain Rows in table.
     * 
     * @return List<DirectoryInfoRow>
     */
    public List<DirectoryInfoRow> getDirectoryInfoRows()
    {
        try
        {
            List<WebElement> elemList = findAndWaitForElements(DIRECTORY_INFO_ROW);
            List<DirectoryInfoRow> directoryInfoRowList = new ArrayList<>();
            for (WebElement webElement : elemList)
            {
                directoryInfoRowList.add(new DirectoryInfoRow(webElement, driver));
            }
            return directoryInfoRowList;
        }
        catch (StaleElementReferenceException e)
        {
            return getDirectoryInfoRows();
        }
    }

    /**
     * Return object by 'name' associated with Auth Chain Row in table.
     * 
     * @param name String
     * @return DirectoryInfoRow
     */
    public DirectoryInfoRow getDirectoryInfoRowBy(String name)
    {
        checkNotNull(name);
        List<DirectoryInfoRow> directoryInfoRowList = getDirectoryInfoRows();
        for (DirectoryInfoRow directoryInfoRow : directoryInfoRowList)
        {
            if (directoryInfoRow.getName().equals(name))
            {
                return directoryInfoRow;
            }
        }
        throw new PageOperationException("Can't find directoryInfoRow with name[" + name + "].");
    }

    /**
     * Remove auth chain by 'name' from alfresco.
     * 
     * @param name String
     * @return DirectoryManagementPage
     */
    public HtmlPage removeAuthChain(String name)
    {
        DirectoryInfoRow directoryInfoRow = getDirectoryInfoRowBy(name);
        directoryInfoRow.clickRemove();
        click(SAVE_BUTTON);
        return getCurrentPage();
    }

    /**
     * Return sync status.
     * 
     * @return String
     */
    public String getSyncStatus()
    {
        return findAndWait(SYNC_STATUS).getText();
    }
    
    /**
     * Return sync status.
     * 
     * @return boolean
     */
    public boolean isSyncStatusDisplayed()
    {
        try
        {
            return driver.findElement(SYNC_STATUS).isDisplayed();
        }
        catch (NoSuchElementException e)
        {
            return false;
        }
       
    }

    /**
     * Run sync with auth chains.
     */
    public void runSync()
    {
        click(RUN_SYNC_BUTTON);
        driver.switchTo().frame("admin-dialog");
        click(By.xpath("//input[@value='Sync']"));
        driver.switchTo().defaultContent();
    }

    /**
     * Run test sync for auth chain selected by 'name'
     * 
     * @param name String
     * @return String
     */
    public String runTestSyncFor(String name)
    {
        checkNotNull(name);
        DirectoryInfoRow directoryInfoRow = getDirectoryInfoRowBy(name);
        directoryInfoRow.clickTestSync();
        driver.switchTo().frame("admin-dialog");
        click(RUN_TEST_SYNC);
        String testStatus = findAndWait(TEST_STATUS).getText();
        driver.switchTo().defaultContent();
        return testStatus;
    }

    private void click(By locator)
    {
        waitUntilElementPresent(locator, 5);
        WebElement element = findAndWait(locator);
        executeJavaScript("arguments[0].click();", element);
    }

    private void fillField(By selector, String text)
    {
        checkNotNull(text);
        WebElement inputField = findAndWait(selector);
        inputField.clear();
        if (text != null)
        {
            inputField.sendKeys(text);
        }
    }

    public HtmlPage clickSave()
    {
        click(SAVE_BUTTON);
        return getCurrentPage();
    }
    

}
