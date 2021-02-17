package org.alfresco.po.share.user.admin;

import static org.alfresco.common.Wait.WAIT_2;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;

import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.alfresco.common.Language;
import org.alfresco.common.WebElementInteraction;
import org.alfresco.dataprep.SiteService.Visibility;
import org.alfresco.po.share.site.SiteManagerDeleteSiteDialog;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

@Slf4j
public class ManagerSiteActionComponent
{
    private final String siteName;

    private final SitesManagerPage sitesManagerPage;
    private final SiteManagerDeleteSiteDialog deleteSiteDialog;
    private final WebElementInteraction webElementInteraction;
    private final Language language;

    private final By siteRowName = By.cssSelector("td.alfresco-lists-views-layouts-Cell.siteName span.inner");
    private final By siteRowActionsButton = By.cssSelector("td.alfresco-lists-views-layouts-Cell.actions div.dijitPopupMenuItem");
    private final By siteRowSiteManager = By.cssSelector("td.alfresco-lists-views-layouts-Cell.siteManager .value");
    private final By siteRowVisibility = By.cssSelector("td.alfresco-lists-views-layouts-Cell.visibility table");
    private final By siteRowVisibilityArrow = By.cssSelector("input[class$='dijitArrowButtonInner']");
    private final By successIndicator = By.cssSelector("div[class='indicator success']");
    private final By siteRowDescription = By.cssSelector("td.alfresco-lists-views-layouts-Cell.siteDescription");
    private final By dropdownOptionsList = By.cssSelector("div.dijitPopup[style*=visible] td.dijitMenuItemLabel");

    private final String dropdownOptions = "//div[@class='dijitPopup Popup' and contains(@style, visible)]//td[@class='dijitReset dijitMenuItemLabel' and text()='%s']";

    protected ManagerSiteActionComponent(SitesManagerPage sitesManagerPage,
                                      WebElementInteraction webElementInteraction,
                                      String siteName,
                                      SiteManagerDeleteSiteDialog deleteSiteDialog,
                                      Language language)
    {
        this.sitesManagerPage = sitesManagerPage;
        this.webElementInteraction = webElementInteraction;
        this.siteName = siteName;
        this.deleteSiteDialog = deleteSiteDialog;
        this.language = language;
    }

    public ManagerSiteActionComponent assertSiteIsNotDisplayed()
    {
        log.info("Assert site is not displayed");
        assertNull(getSiteRow(), String.format("Site %s is displayed", siteName));
        return this;
    }

    private void clickActionsButton()
    {
        WebElement actionsButton = getSiteRow().findElement(siteRowActionsButton);
        webElementInteraction.mouseOver(actionsButton);
        webElementInteraction.clickElement(actionsButton);
    }

    public ManagerSiteActionComponent becomeSiteManager()
    {
        log.info("Select action Become Site Manager");
        clickActionsButton();
        webElementInteraction.waitUntilElementsAreVisible(dropdownOptionsList);
        WebElement becomeBtn = webElementInteraction.findFirstElementWithValue(dropdownOptionsList,
            sitesManagerPage.language.translate("sitesManager.becomeSiteManager"));
        webElementInteraction.mouseOver(becomeBtn);
        webElementInteraction.clickElement(becomeBtn);
        sitesManagerPage.waitUntilLoadingMessageDisappears();

        return this;
    }

    public ManagerSiteActionComponent assertSiteManagerIsYes()
    {
        log.info("Assert I'm site manager is set to Yes");
        assertEquals(getSiteRow().findElement(siteRowSiteManager).getText(),
            language.translate("adminTools.siteManager.yes"), "Is site manager");
        return this;
    }

    public ManagerSiteActionComponent assertSiteManagerIsNo()
    {
        log.info("Assert I'm site manager is set to No");
        assertEquals(getSiteRow().findElement(siteRowSiteManager).getText(),
            language.translate("adminTools.siteManager.no"), "Is site manager");
        return this;
    }

    public SiteManagerDeleteSiteDialog clickDelete()
    {
        log.info("Click Delete");
        clickActionsButton();
        selectDeleteSite();
        return deleteSiteDialog;
    }

    private void selectDeleteSite()
    {
        webElementInteraction.clickElement(webElementInteraction
            .findFirstElementWithValue(dropdownOptionsList,
                sitesManagerPage.language.translate("sitesManager.deleteSite")));
    }

    public ManagerSiteActionComponent deleteSite()
    {
        log.info("Delete site");
        clickDelete();
        deleteSiteDialog.clickDeleteFromSitesManager();
        webElementInteraction.waitUntilElementDisappears(getSiteRow());
        return this;
    }

    public ManagerSiteActionComponent changeSiteVisibility(Visibility visibility)
    {
        log.info("Change site visibility to {}", visibility.toString());
        WebElement siteRow = getSiteRow();
        webElementInteraction.clickElement(siteRow.findElement(siteRowVisibilityArrow));

        List<WebElement> options = getVisibilityOptions();
        String visibilityValue = getCapitalizedVisibility(visibility);
        WebElement option = webElementInteraction
            .findFirstElementWithValue(options, visibilityValue);

        clickOptionAndWaitForChildLocatorPresence(option, successIndicator);

        return this;
    }

    private WebElement clickOptionAndWaitForChildLocatorPresence(WebElement option, By locator)
    {
        webElementInteraction.mouseOver(option);
        webElementInteraction.clickElement(option);
        webElementInteraction
            .waitUntilChildElementIsPresent(getSiteRow(), locator);

        return option;
    }

    private List<WebElement> getVisibilityOptions()
    {
        List<WebElement> options = webElementInteraction
            .waitUntilElementsAreVisible(dropdownOptionsList);
        webElementInteraction.waitInSeconds(WAIT_2.getValue());
        return options;
    }

    private String getCapitalizedVisibility(Visibility visibility)
    {
        String visibilityValue = visibility.toString().toLowerCase();
        visibilityValue = StringUtils.capitalize(visibilityValue);
        return visibilityValue;
    }

    public ManagerSiteActionComponent assertSiteVisibilityEquals(Visibility visibility)
    {
        log.info("Assert site visibility is: {}", visibility.toString());
        String visibilityValue = getCapitalizedVisibility(visibility);

        WebElement siteRow = getSiteRow();
        WebElement visibilityElement = webElementInteraction.waitUntilChildElementIsPresent(siteRow, siteRowVisibility);
        String actualVisibility = webElementInteraction.getElementText(visibilityElement);
        assertEquals(actualVisibility, visibilityValue,"Site visibility is correct");

        return this;
    }

    public ManagerSiteActionComponent assertSuccessIndicatorIsDisplayed()
    {
        log.info("Assert success indicator is displayed");
        boolean isSuccessIndicatorDisplayed = webElementInteraction
            .isElementDisplayed(getSiteRow().findElement(successIndicator));

        assertTrue(isSuccessIndicatorDisplayed, "Success indicator is not displayed");
        return this;
    }

    public ManagerSiteActionComponent assertSiteDescriptionEqualsTo(String expectedSiteDescription)
    {
        log.info(String.format("Assert site description is %s", expectedSiteDescription));
        String actualSiteDescription = webElementInteraction
            .getElementText(getSiteRow().findElement(siteRowDescription));

        assertEquals(actualSiteDescription, expectedSiteDescription,
            "Site description is correct");
        return this;
    }

    public ManagerSiteActionComponent assertBecomeSiteManagerOptionIsNotDisplayed()
    {
        log.info("Assert Become site manager option is not displayed");
        assertFalse(isBecomeSiteManagerDisplayed(), "Become site manager is displayed");
        return this;
    }

    public ManagerSiteActionComponent assertBecomeSiteManagerOptionIsDisplayed()
    {
        log.info("Assert Become site manager option is not displayed");
        assertTrue(isBecomeSiteManagerDisplayed(), "Become site manager is not displayed");
        return this;
    }

    public ManagerSiteActionComponent assertDeleteSiteOptionIsDisplayed()
    {
        log.info("Assert Delete Site option is available");
        assertTrue(isDeleteSiteDisplayed(), "Delete site is not displayed");
        return this;
    }

    private boolean isDeleteSiteDisplayed()
    {
        return webElementInteraction
            .isElementDisplayed(By.xpath(String.format(dropdownOptions,
                sitesManagerPage.language.translate("sitesManager.deleteSite"))));
    }

    private boolean isBecomeSiteManagerDisplayed()
    {
        return webElementInteraction
            .isElementDisplayed(By.xpath(String.format(dropdownOptions,
                sitesManagerPage.language.translate("sitesManager.becomeSiteManager"))));
    }

    private WebElement getSiteRow()
    {
        return sitesManagerPage.getSiteRowBasedOnSiteName(siteName);
    }

    public ManagerSiteActionComponent openActionsDropDown()
    {
        log.info("Open Actions dropdown with site name {}", siteName);
        webElementInteraction.clickElement(getSiteRow().findElement(siteRowActionsButton));
        return this;
    }

    public void clickSiteName()
    {
        log.info("Click Site Name");
        webElementInteraction.clickElement(getSiteRow().findElement(siteRowName));
    }
}
