package org.alfresco.po.share.user.admin.adminTools.DialogPages;

import org.alfresco.po.annotation.PageObject;
import org.alfresco.po.annotation.RenderWebElement;
import org.alfresco.po.share.ShareDialog;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import ru.yandex.qatools.htmlelements.element.FileInput;

/**
 * Created by Mirela Tifui on 11/28/2016.
 */
@PageObject
public class ImportModelDialogPage extends ShareDialog
{
    @RenderWebElement
    private By importButton = By.id("CMM_IMPORT_DIALOG_OK_label");

    @RenderWebElement
    private By cancelButton = By.id("CMM_IMPORT_DIALOG_CANCEL_label");

    @RenderWebElement
    private By browserButton = By.cssSelector(".alfresco-html-FileInput");

    @RenderWebElement
    private By importModelWindow = By.id("CMM_IMPORT_DIALOG");

    @FindBy(css ="div[class='dijitDialogTitleBar'] span[id ='CMM_IMPORT_DIALOG_title']")
    private WebElement importModelTitle;

    private By closeButton = By.cssSelector("span[class='dijitDialogCloseIcon']");

    public boolean isImportModelWindowDisplayed()
    {
        return browser.isElementDisplayed(importModelWindow);
    }

    public String getImportModelWindowTitle()
    {
        return importModelTitle.getText();
    }

    public boolean isCloseButtonDisplayed()
    {
        return browser.isElementDisplayed(closeButton);
    }

    public boolean isBrowserButtonDisplayed()
    {
        return browser.isElementDisplayed(browserButton);
    }

    public boolean isImportButtonDisplayed()
    {
        return browser.isElementDisplayed(importButton);
    }

    public boolean isCancelButtonDisplayed()
    {
        return browser.isElementDisplayed(cancelButton);
    }

    public void clickChooseFilesButton()
    {
        browser.findElement(browserButton).click();
    }

    public void importFile(String filePath)
    {
        browser.findElement(browserButton).sendKeys(filePath);
    }

    public void clickImportButton()
    {
        browser.findElement(importButton).click();
    }
}
