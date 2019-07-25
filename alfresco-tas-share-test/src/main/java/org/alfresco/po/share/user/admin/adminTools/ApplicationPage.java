package org.alfresco.po.share.user.admin.adminTools;

import java.io.File;
import java.util.List;

import org.alfresco.utility.web.annotation.PageObject;
import org.alfresco.utility.web.annotation.RenderWebElement;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import ru.yandex.qatools.htmlelements.element.Button;
import ru.yandex.qatools.htmlelements.element.FileInput;
import ru.yandex.qatools.htmlelements.element.Select;

/**
 * @author Razvan.Dorobantu
 */
@PageObject
public class ApplicationPage extends AdminToolsPage
{
    protected String srcRoot = System.getProperty("user.dir") + File.separator;
    protected String testDataFolder = srcRoot + "testdata" + File.separator;
    protected String mainTextString = "";
    @FindBy (className = "dnd-file-selection-button")
    private FileInput fileInput;
    @RenderWebElement
    @FindBy (css = "select#console-options-theme-menu")
    private Select themeDropdown;
    @FindBy (css = "div.apply button[id$='_default-apply-button-button']")
    private Button applyButton;
    @FindBy (xpath = "//img[contains(@id, '_default-logoimg') and contains(@src, '/images/app-logo-48.png')]")
    private WebElement defaultAlfrescoImage;
    //  @FindBy(xpath= "/html/body/div[9]/div[1]/div[2]/div[1]/div[1]/div/div/div/div/div/div[2]")
    @FindBy (css = ".info")
    private WebElement mainText;
    @RenderWebElement
    @FindBy (css = "button[id$='reset-button-button']")
    private Button resetButton;
    @FindBy (css = "div[id*='_dnd-upload_'] button[id$='_default-cancelOk-button-button']")
    private WebElement okButton;
    @RenderWebElement
    @FindBy (css = "form[id*=admin-console] button[id*=upload-button-button]")
    private Button uploadButton;

    @Override
    public String getRelativePath()
    {
        return "share/page/console/admin-console/application";
    }

    public void uploadImage()
    {
        String testFile = "alfrescoLogo.png";
        String testFilePath = testDataFolder + testFile;
        getBrowser().waitUntilElementClickable(uploadButton).click();

        //click Upload button
        browser.waitUntilElementClickable(uploadButton).click();
        //  uploadButton.click();

        //upload the new image
        fileInput.setFileToUpload(testFilePath);
        browser.waitInSeconds(5);
        if (browser.isElementDisplayed(By.cssSelector("div[id*='_dnd-upload_'] button[id$='_default-cancelOk-button-button']")))
        {
            getBrowser().waitInSeconds(5);
            getBrowser().waitUntilElementClickable(okButton).click();
        }

        browser.mouseOver(applyButton);
        browser.waitInSeconds(2);
        browser.waitUntilElementClickable(applyButton).click();
    }

    public boolean isAlfrescoDefaultImageDisplayed()
    {
        return browser.isElementDisplayed(defaultAlfrescoImage);
    }

    public void resetImageToDefault()
    {
        //click Reset button
        resetButton.click();
    }

    public void selectTheme(Theme theme)
    {
        themeDropdown.selectByValue(theme.getTheme());
        //click 'Apply' button to save the theme
        applyButton.click();
    }

    public boolean isThemeOptionPresent(Theme theme)
    {
        List<WebElement> options = themeDropdown.getOptions();
        for (WebElement value : options)
            if (value.getAttribute("value").contains(theme.getTheme()))
                return true;
        return false;
    }

    public boolean isThemeOptionSelected(Theme theme)
    {
        List<WebElement> options = themeDropdown.getOptions();
        for (WebElement value : options)
            if (value.getAttribute("value").contains(theme.getTheme()) && value.isSelected())
                return true;
        return false;
    }

    public boolean doesBodyContainTheme(Theme theme)
    {
        By themeToBeFound = By.xpath("//body[@id = 'Share' and contains(@class, 'skin-" + theme.getTheme() + "')]");
        browser.waitUntilElementVisible(themeToBeFound);
        return browser.isElementDisplayed(themeToBeFound);
    }

    public String checkText()
    {
        return mainText.getText();
    }


    public enum Theme
    {
        YELLOW_THEME("yellowTheme"),
        GREEN_THEME("greenTheme"),
        BLUE_THEME("default"),
        LIGHT_THEME("lightTheme"),
        GOOGLE_DOCS_THEME("gdocs"),
        HIGH_CONTRAST_THEME("hcBlack");

        private String theme;

        Theme(String theme)
        {
            this.theme = theme;
        }

        public String getTheme()
        {
            return this.theme;
        }
    }
}