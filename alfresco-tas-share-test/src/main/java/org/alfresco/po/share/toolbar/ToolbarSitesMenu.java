package org.alfresco.po.share.toolbar;

import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.alfresco.po.share.BasePage;
import org.alfresco.po.share.SiteFinderPage;
import org.alfresco.po.share.site.CreateSiteDialog;
import org.alfresco.po.share.site.SiteDashboardPage;
import org.alfresco.po.share.user.profile.UserSitesListPage;
import org.alfresco.utility.model.SiteModel;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

@Slf4j
public class ToolbarSitesMenu extends BasePage
{
    private final By recentSitesSection = By.xpath("//tr[starts-with(@id,'HEADER_SITES_MENU_RECENT')]/../../../../div[@class='alf-menu-group-title']");
    private final By recentSitesRowList = By.cssSelector("td[id^='HEADER_SITES_MENU_RECENT_']");
    private final By usefulSection = By.cssSelector("#HEADER_SITES_MENU_dropdown .alf-menu-group:last-child > *:first-child");
    private final By mySites = By.id("HEADER_SITES_MENU_MY_SITES_text");
    private final By siteFinder = By.id("HEADER_SITES_MENU_SITE_FINDER_text");
    private final By createSite = By.id("HEADER_SITES_MENU_CREATE_SITE_text");
    private final By favorites = By.id("HEADER_SITES_MENU_FAVOURITES_text");
    private final By sitesDropdown = By.id("HEADER_SITES_MENU_dropdown");
    private final By favoriteSitesRowList = By.cssSelector("td[id^='HEADER_SITES_MENU_FAVOURITE_'] a");
    private final By favoriteDropDown = By.id("HEADER_SITES_MENU_FAVOURITES_dropdown");
    private final By addCurrentSiteToFavorites = By.id("HEADER_SITES_MENU_ADD_FAVOURITE_text");
    private final By removeCurrentSiteFromFavorites = By.id("HEADER_SITES_MENU_REMOVE_FAVOURITE_text");

    public ToolbarSitesMenu(ThreadLocal<WebDriver> webDriver)
    {
        super(webDriver);
    }

    protected void waitForMySitesMenuToBeOpened()
    {
        webElementInteraction.waitUntilElementIsVisible(mySites);
    }

    public ToolbarSitesMenu assertRecentSitesSectionIsDisplayed()
    {
        log.info("Assert Recent Sites section is displayed");
        webElementInteraction.waitUntilElementIsVisible(recentSitesSection);
        assertTrue(webElementInteraction.isElementDisplayed(recentSitesSection), "Recent sites section is displayed");
        return this;
    }

    public ToolbarSitesMenu assertRecentSitesSectionIsNotDisplayed()
    {
        log.info("Assert Recent Sites section is NOT displayed");
        assertFalse(webElementInteraction.isElementDisplayed(recentSitesSection), "Recent sites section is displayed");
        return this;
    }

    public SiteDashboardPage clickRecentSite(String siteName)
    {
        log.info("Click recent site {}", siteName);
        List<WebElement> sites = webElementInteraction.findElements(recentSitesRowList);
        webElementInteraction.findFirstElementWithValue(sites, siteName).click();
        return new SiteDashboardPage(webDriver);
    }

    public SiteDashboardPage clickRecentSite(SiteModel site)
    {
        return clickRecentSite(site.getTitle());
    }

    public ToolbarSitesMenu assertSiteIsInRecentSites(String siteName)
    {
        log.info("Assert site %s is displayed in recent sites: {}", siteName);
        List<WebElement> recentSites = webElementInteraction.findElements(recentSitesRowList);
        assertNotNull(webElementInteraction.findFirstElementWithValue(recentSites, siteName),
            String.format("Assert site %s is found in Recent Sites section", siteName));
        return this;
    }

    public ToolbarSitesMenu assertSiteIsInRecentSites(SiteModel site)
    {
        return assertSiteIsInRecentSites(site.getTitle());
    }

    public ToolbarSitesMenu assertUsefulSectionIsDisplayed()
    {
        log.info("Assert Useful section is displayed");
        webElementInteraction.waitUntilElementIsVisible(usefulSection);
        assertTrue(webElementInteraction.isElementDisplayed(usefulSection), "Useful section is displayed");
        return this;
    }

    public ToolbarSitesMenu assertMySitesIsDisplayed()
    {
        log.info("Assert My Sites link is displayed");
        webElementInteraction.waitUntilElementIsVisible(mySites);
        assertTrue(webElementInteraction.isElementDisplayed(mySites), "My Sites link is not displayed");
        return this;
    }

    public UserSitesListPage clickMySites()
    {
        log.info("Click My Sites");
        webElementInteraction.waitUntilElementsAreVisible(mySites);
        webElementInteraction.clickElement(mySites);
        return new UserSitesListPage(webDriver);
    }

    public ToolbarSitesMenu assertSiteFinderIsDisplayed()
    {
        log.info("Assert Site Finder link is displayed");
        assertTrue(webElementInteraction.isElementDisplayed(siteFinder), "Site Finder is displayed");
        return this;
    }

    public SiteFinderPage clickSiteFinder()
    {
        log.info("Click Site Finder");
        webElementInteraction.waitUntilElementIsVisible(siteFinder);
        webElementInteraction.clickElement(siteFinder);
        return new SiteFinderPage(webDriver);
    }

    public ToolbarSitesMenu assertCreateSiteIsDisplayed()
    {
        log.info("Assert Create Site is displayed");
        assertTrue(webElementInteraction.isElementDisplayed(createSite), "Create Site is displayed");
        return this;
    }

    public CreateSiteDialog clickCreateSite()
    {
        log.info("Click Create Site");
        webElementInteraction.waitUntilElementIsVisible(createSite);
        webElementInteraction.clickElement(createSite);
        return new CreateSiteDialog(webDriver);
    }

    public ToolbarSitesMenu assertFavoritesIsDisplayed()
    {
        log.info("Assert Favorites link is displayed");
        assertTrue(webElementInteraction.isElementDisplayed(favorites), "Favorites is displayed");
        return this;
    }

    public ToolbarSitesMenu assertAddCurrentSiteToFavoritesDisplayed()
    {
        log.info("Assert Add current site to favorites is displayed");
        webElementInteraction.waitUntilElementIsVisible(addCurrentSiteToFavorites);
        assertTrue(webElementInteraction.isElementDisplayed(addCurrentSiteToFavorites), "Favorites is displayed");
        return this;
    }

    public ToolbarSitesMenu assertRemoveCurrentSiteFromFavoritesIsDisplayed()
    {
        log.info("Assert Remove Current Site from Favorites is displayed");
        webElementInteraction.waitUntilElementIsVisible(removeCurrentSiteFromFavorites);
        assertTrue(webElementInteraction.isElementDisplayed(removeCurrentSiteFromFavorites), "Remove current site from favorites is displayed");
        return this;
    }

    public ToolbarSitesMenu assertRemoveCurrentSiteFromFavoritesIsNotDisplayed()
    {
        log.info("Assert Remove Current Site from Favorites is NOT displayed");
        assertFalse(webElementInteraction.isElementDisplayed(removeCurrentSiteFromFavorites), "Remove current site from favorites is displayed");
        return this;
    }

    public void clickAddCurrentSiteToFavorites()
    {
        log.info("Click Add current site to favorites");
        webElementInteraction.clickElement(addCurrentSiteToFavorites);
    }

    public void clickRemoveCurrentSiteFromFavorites()
    {
        log.info("Click Remove current site from favorites");
        WebElement remove = webElementInteraction.waitUntilElementIsVisible(removeCurrentSiteFromFavorites);
        webElementInteraction.clickElement(remove);
        webElementInteraction.waitUntilElementDisappears(removeCurrentSiteFromFavorites);
        if (defaultProperties.get().getBrowserName().equals("chrome"))
        {
            webElementInteraction.waitUntilElementDeletedFromDom(removeCurrentSiteFromFavorites);
        }
        webElementInteraction.waitUntilElementDisappears(sitesDropdown);
    }

    private boolean isSiteFavorite(String siteName)
    {
        webElementInteraction.waitUntilElementIsVisible(favorites);
        webElementInteraction.clickElement(favorites);
        webElementInteraction.waitUntilElementIsVisible(favoriteDropDown);
        return webElementInteraction.findFirstElementWithValue(favoriteSitesRowList, siteName) != null;
    }

    public ToolbarSitesMenu assertSiteIsFavorite(String siteName)
    {
        log.info("Assert site is found in favorites :{}", siteName);
        assertTrue(isSiteFavorite(siteName), String.format("Site %s is found in favorites", siteName));
        return this;
    }

    public ToolbarSitesMenu assertSiteIsFavorite(SiteModel site)
    {
        return assertSiteIsFavorite(site.getTitle());
    }

    public ToolbarSitesMenu assertSiteIsNotFavorite(String siteName)
    {
        log.info("Assert site is not found in favorites: {}", siteName);
        assertFalse(isSiteFavorite(siteName),
            String.format("Site %s is found in favorites", siteName));
        return this;
    }

    public SiteDashboardPage clickFavoriteSite(String siteName)
    {
        log.info("Select site %s from favorites: {}", siteName);
        webElementInteraction.waitUntilElementIsVisible(favorites);
        webElementInteraction.clickElement(favorites);
        webElementInteraction.waitUntilElementIsVisible(favoriteDropDown);
        webElementInteraction.findFirstElementWithValue(favoriteSitesRowList, siteName).click();
        return new SiteDashboardPage(webDriver);
    }

    public SiteDashboardPage clickFavoriteSite(SiteModel site)
    {
        return clickFavoriteSite(site.getTitle());
    }
}
