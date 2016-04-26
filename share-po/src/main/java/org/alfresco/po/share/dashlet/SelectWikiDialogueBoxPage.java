package org.alfresco.po.share.dashlet;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.alfresco.po.RenderElement.getVisibleRenderElement;

import org.alfresco.po.RenderTime;
import org.alfresco.po.share.SharePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;

/**
 * Page object holds the elements of Select Wiki box page
 *
 * @author Marina.Nenadovets
 */

public class SelectWikiDialogueBoxPage extends SharePage
{
    private static final By SELECT_DROP_DOWN = By.cssSelector("select[name='wikipage']");
    private static final By OK_BUTTON = By.cssSelector("button[id$='configDialog-ok-button']");
    private static final By CANCEL_BUTTON = By.cssSelector("button[id$='configDialog-cancel-button']");
    private static final By CLOSE_BUTTON = By.cssSelector(".container-close");

    @SuppressWarnings("unchecked")
    @Override
    public SelectWikiDialogueBoxPage render(RenderTime timer)
    {
        elementRender(timer, getVisibleRenderElement(OK_BUTTON), getVisibleRenderElement(CANCEL_BUTTON),
                getVisibleRenderElement(CLOSE_BUTTON));
        return this;

    }

    @SuppressWarnings("unchecked")
    @Override
    public SelectWikiDialogueBoxPage render()
    {
        return render(new RenderTime(maxPageLoadingTime));
    }

    /**
     * Method for config wikiPages
     * @param visibleText String
     */
    public void selectWikiPageBy(String visibleText)
    {
        checkNotNull(visibleText);
        WebElement element = findAndWait(SELECT_DROP_DOWN);
        Select select = new Select(element);
        select.selectByVisibleText(visibleText);
        findAndWait(OK_BUTTON).click();
        waitUntilAlert();
    }
}
