package org.alfresco.po.share.site;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;

import org.alfresco.po.ElementState;
import org.alfresco.po.RenderTime;
import org.alfresco.po.exception.PageException;
import org.alfresco.po.share.SharePage;
import org.alfresco.po.share.workflow.DestinationAndAssigneePage;
import org.apache.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;

/**
 * Create folder page object, holds all element of the HTML page relating to
 * share's create new folder in cloud page.
 * 
 * @author Ranjith Manyam
 * @since 1.0
 */
public class CreateNewFolderInCloudPage extends SharePage
{
    private static final Logger logger = Logger.getLogger(CreateNewFolderInCloudPage.class);

    private static final By TITLE = By.cssSelector("input[id$='createFolderInTheCloud_prop_cm_title']");
    private static final By NAME = By.cssSelector("input[id$='_default-cloud-folder-createFolderInTheCloud_prop_cm_name']");
    private final By NAME_LABEL = By.cssSelector("div[class='form-field']>label[for$='folder-createFolderInTheCloud_prop_cm_name']");
    private static final By DESCRIPTION = By.cssSelector("textarea[id$='_default-cloud-folder-createFolderInTheCloud_prop_cm_description']");
    private final By DESCRIPTION_LABEL = By.cssSelector("div>label[for$='folder-createFolderInTheCloud_prop_cm_description']");
    private static final By SAVE_BUTTON = By.cssSelector("button[id$='_default-cloud-folder-createFolderInTheCloud-form-submit-button']");

    @SuppressWarnings("unchecked")
    @Override
    public CreateNewFolderInCloudPage render(RenderTime timer)
    {
        basicRender(timer);
        return this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public CreateNewFolderInCloudPage render()
    {
        return render(new RenderTime(maxPageLoadingTime));
    }

    /**
     * @see #createNewFolder(String, String)
     */
    public DestinationAndAssigneePage createNewFolder(final String folderName)
    {
        return createNewFolder(folderName, null);
    }

    /**
     * Create a new folder action by completing and submitting the form.
     * 
     * @param folderName mandatory folder name
     * @param description optional folder description
     * @return {@link DestinationAndAssigneePage} page response
     */
    public DestinationAndAssigneePage createNewFolder(final String folderName, final String description)
    {
        if (folderName == null || folderName.isEmpty())
        {
            throw new UnsupportedOperationException("Folder Name input required.");
        }
        try
        {
            WebElement inputFolderName = findAndWait(NAME);
            inputFolderName.sendKeys(folderName);
            if (description != null)
            {
                WebElement inputDescription = driver.findElement(DESCRIPTION);
                inputDescription.sendKeys(description);
            }
            submit(SAVE_BUTTON, ElementState.INVISIBLE);
            // Wait till the pop up disappears
            // canResume();
            try
            {
                waitForElement(By.id("AlfrescoWebdriverz1"), SECONDS.convert(getDefaultWaitTime(), MILLISECONDS));
            }
            catch (TimeoutException e)
            {
            }
            return factoryPage.instantiatePage(driver, DestinationAndAssigneePage.class);
        }
        catch (TimeoutException te)
        {
            if (logger.isTraceEnabled())
            {
                logger.trace("Unable to find \"Name\" element", te);
            }
        }
        catch (NoSuchElementException nse)
        {
            if (logger.isTraceEnabled())
            {
                logger.trace("Unable to find \"Description/Save button\" elements", nse);
            }
        }
        throw new PageException("Unable to find element");
    }

    /**
     * Create a new folder action by completing and submitting the form.
     * 
     * @param folderName mandatory folder name
     * @param description optional folder description
     * @param folderTitle options folder Title
     * @return {@link DestinationAndAssigneePage} page response
     */
    public DestinationAndAssigneePage createNewFolder(final String folderName, final String folderTitle, final String description)
    {
        if (folderName == null || folderName.isEmpty())
        {
            throw new UnsupportedOperationException("Folder Name input required.");
        }

        if (folderTitle != null && !folderTitle.isEmpty())
        {
            WebElement inputFolderName = findAndWait(TITLE);
            inputFolderName.sendKeys(folderTitle);
        }

        return createNewFolder(folderName, description);
    }

    public boolean isNameLabelDisplayed()
    {
        try
        {
            driver.findElement(NAME_LABEL);
            return true;
        }
        catch (TimeoutException te)
        {
        }
        return false;
    }

    public boolean isDescriptionLabelDisplayed()
    {
        try
        {
            driver.findElement(DESCRIPTION_LABEL);
            return true;
        }
        catch (TimeoutException te)
        {
        }
        return false;
    }
}
