package org.alfresco.po.share;

import static org.alfresco.po.RenderElement.getVisibleRenderElement;

import org.alfresco.po.HtmlPage;
import org.alfresco.po.RenderTime;
import org.alfresco.po.exception.PageException;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;

/**
 * @author nshah
 */
public class NewGroupPage extends SharePage
{
    private static final String TEXT_INPUT_IDENTIFIER = "input[id$='default-create-shortname']";
    private static final String TEXT_INPUT_DISPLAY_NAME = "input[id$='default-create-displayname']";
    private static final String BUTTON_CREATE_GROUP = "button[id$='-creategroup-ok-button-button']";
    private static final String BUTTON_CREATE_GROUP_CANCEL = "button[id$='-creategroup-cancel-button-button']";
    private static final String BUTTON_CREATE_ANOTHER_GROUP = "button[id$='-creategroup-another-button-button']";

    public enum ActionButton
    {
        CREATE_GROUP,
        CREATE_ANOTHER,
        CANCEL_GROUP;
    }

    @SuppressWarnings("unchecked")
    @Override
    public NewGroupPage render(RenderTime timer)
    {
        try
        {
            elementRender(timer, getVisibleRenderElement(By.cssSelector(TEXT_INPUT_IDENTIFIER)), getVisibleRenderElement(By.cssSelector(BUTTON_CREATE_GROUP)),
                    getVisibleRenderElement(By.cssSelector(TEXT_INPUT_DISPLAY_NAME)), getVisibleRenderElement(By.cssSelector(BUTTON_CREATE_GROUP_CANCEL)));
        }
        catch (NoSuchElementException e)
        {
        }
        catch (TimeoutException e)
        {
        }
        return this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public NewGroupPage render()
    {
        return render(new RenderTime(maxPageLoadingTime));
    }

    private void setIdentifier(String identifier)
    {
        if (StringUtils.isEmpty(identifier))
        {
            throw new PageException("Enter value of Identifier");
        }

        WebElement input = findAndWait(By.cssSelector(TEXT_INPUT_IDENTIFIER));
        input.clear();
        input.sendKeys(identifier);
    }

    private void setDisplayName(String displayName)
    {
        if (StringUtils.isEmpty(displayName))
        {
            throw new PageException("Enter value of DisplayName");
        }
        WebElement input = findAndWait(By.cssSelector(TEXT_INPUT_DISPLAY_NAME));
        input.clear();
        input.sendKeys(displayName);
    }

    private HtmlPage clickButton(ActionButton groupButton)
    {
        switch (groupButton)
        {
            case CREATE_GROUP:
                findAndWait(By.cssSelector(BUTTON_CREATE_GROUP)).click();
                waitUntilAlert();
                return factoryPage.instantiatePage(driver, GroupsPage.class);
            case CREATE_ANOTHER:
                findAndWait(By.cssSelector(BUTTON_CREATE_ANOTHER_GROUP)).click();
                waitUntilAlert();
                return factoryPage.instantiatePage(driver, NewGroupPage.class);
            default:
                findAndWait(By.cssSelector(BUTTON_CREATE_GROUP_CANCEL)).click();
                return factoryPage.instantiatePage(driver, GroupsPage.class);
        }
    }

    /**
     * @param identifier String
     * @param displayName String
     * @param groupButton ActionButton
     * @return HtmlPage
     */
    public HtmlPage createGroup(String identifier, String displayName, ActionButton groupButton)
    {
        setIdentifier(identifier);
        setDisplayName(displayName);
        return clickButton(groupButton);
    }

}
