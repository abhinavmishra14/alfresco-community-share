package org.alfresco.po.share.dashlet;

import static org.testng.Assert.assertEquals;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class WikiDashlet extends Dashlet<WikiDashlet>
{
    private final By dashletContainer = By.cssSelector("div.dashlet.wiki");
    private final By defaultDashletMessage = By.cssSelector("div.dashlet.wiki [class$='rich-content dashlet-padding']");
    private final By wikiDashletTitle = By.cssSelector("div.dashlet.wiki [class$='title']");
    private final By configureDashlet = By.cssSelector("div.dashlet.wiki [class$='edit']");
    private final By wikiDashletText = By.cssSelector("div.dashlet.wiki [class$='body scrollablePanel']");
    private final String dashletLinkTitleLocator = "//a[contains(text(),'%s')]";

    private final String UNDERLINE_REPLACEMENT = "\\_";

    public WikiDashlet(ThreadLocal<WebDriver> webDriver)
    {
        super(webDriver);
    }

    @Override
    protected String getDashletTitle()
    {
        return webElementInteraction.getElementText(webElementInteraction.waitUntilElementIsVisible(dashletContainer)
            .findElement(dashletTitle));
    }

    public WikiDashlet assertWikiDashletEmptyMessageEquals(String expectedEmptyMessage)
    {
        LOG.info("Assert wiki dashlet empty message equals: {}", expectedEmptyMessage);
        assertEquals(webElementInteraction.getElementText(defaultDashletMessage), expectedEmptyMessage,
            String.format("Empty message not equals %s ", expectedEmptyMessage));

        return this;
    }

    public SelectWikiPagePopUp clickOnConfigureDashletIcon()
    {
        webElementInteraction.mouseOver(wikiDashletTitle);
        webElementInteraction.clickElement(configureDashlet);
        return new SelectWikiPagePopUp(webDriver);
    }

    public WikiDashlet assertWikiDashletMessageEquals(String expectedWikiDashletMessage)
    {
        LOG.info("Assert wiki dashlet message equals: {}", expectedWikiDashletMessage);
        assertEquals(webElementInteraction.getElementText(wikiDashletText), expectedWikiDashletMessage);
        return this;
    }

    public WikiDashlet clickDashletLinkTitle(String wikiDashletLinkTitle)
    {
        LOG.info("Click dashlet link title: {}", wikiDashletLinkTitle);
        webElementInteraction.waitUntilElementIsVisible(By.xpath(String.format(dashletLinkTitleLocator, wikiDashletLinkTitle))).click();

        return this;
    }
}
