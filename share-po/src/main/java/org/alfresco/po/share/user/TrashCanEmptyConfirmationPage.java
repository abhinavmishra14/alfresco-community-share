
package org.alfresco.po.share.user;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;

import java.util.List;

import org.alfresco.po.RenderTime;
import org.alfresco.po.exception.PageOperationException;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;

/**
 * When the users empty the trashcan they will be presented with confirmation Dialog.
 * This page is validate the confirmation dialog.
 * 
 * @author Subashni Prasanna
 * @since 1.7.0
 */

public class TrashCanEmptyConfirmationPage extends TrashCanPage
{
    protected static final By EMPTY_CONFIRMATION_PROMPT = By.cssSelector("div[id='prompt']");
    protected static final By CONFIRMATION_BUTTON = By.cssSelector("div.ft>span button");

    /**
     * Basic Render method
     */
    @SuppressWarnings("unchecked")
    @Override
    public TrashCanEmptyConfirmationPage render(RenderTime timer)
    {
        basicRender(timer);
        return this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public TrashCanEmptyConfirmationPage render()
    {
        return render(new RenderTime(maxPageLoadingTime));
    }

    /**
     * Is confirmation Dialog displayed
     * 
     * @return - Boolean
     */
    public boolean isConfirmationDialogDisplayed()
    {
        boolean displayed = false;
        try
        {
            WebElement prompt = findAndWait(EMPTY_CONFIRMATION_PROMPT);
            displayed = prompt.isDisplayed();
        }
        catch (TimeoutException e)
        {
            displayed = false;
        }
        return displayed;
    }

    /**
     * Click on Cancel button in the confirmation dialog should take the control back to trashcan Page
     * 
     * @return - TrashCanPage
     * @throws - PageOperationException
     */
    public TrashCanPage clickCancelButton() throws PageOperationException
    {
        try
        {
            List<WebElement> buttons = findAndWaitForElements(CONFIRMATION_BUTTON);
            for (WebElement buttonElement : buttons)
            {
                if (buttonElement.getText().equalsIgnoreCase("Cancel"))
                {
                    buttonElement.click();
                    return factoryPage.instantiatePage(driver, TrashCanPage.class);
                }
            }
        }
        catch (TimeoutException te)
        {
            throw new PageOperationException("Cancel button is not visible", te);
        }
        return factoryPage.instantiatePage(driver, TrashCanPage.class);
    }

    /**
     * Click on Cancel button in the confirmation dialog should take the control back to trashcan Page
     * 
     * @return - TrashCanPage
     * @throws - PageOperationException
     */
    public TrashCanPage clickOkButton() throws PageOperationException
    {
        try
        {
            List<WebElement> buttons = findAndWaitForElements(CONFIRMATION_BUTTON);
            for (WebElement buttonElement : buttons)
            {
                if (buttonElement.getText().equalsIgnoreCase("OK"))
                {
                    buttonElement.click();
                    waitForElement(By.cssSelector("div.bd>span.message"), SECONDS.convert(maxPageLoadingTime, MILLISECONDS));
                    waitUntilElementDeletedFromDom(By.cssSelector("div.bd>span.message"), SECONDS.convert(maxPageLoadingTime, MILLISECONDS));
                    return factoryPage.instantiatePage(driver, TrashCanPage.class);
                }
            }
        }
        catch (TimeoutException te)
        {
            throw new PageOperationException("Ok button is not visible", te);
        }
        return factoryPage.instantiatePage(driver, TrashCanPage.class);
    }
}
