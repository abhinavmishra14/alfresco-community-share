package org.alfresco.po.share.TinyMce;

import org.alfresco.po.share.BasePages;
import org.alfresco.utility.web.browser.WebBrowser;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;

public class TinyMceEditor extends BasePages
{
    public String FRAME_ID = "";
    public String TINYMCE_CONTENT = "body[id$='tinymce']";
    private String TINY_MCE_SELECT_ALL_COMMAND = "tinyMCE.activeEditor.selection.select(tinyMCE.activeEditor.getBody(),true);";

    private final By CSS_STR_FORE_COLOUR = By.cssSelector("div[aria-label^='Text'] button[class$='mce-open']");
    private final By iFrame = By.cssSelector("iframe[id*='content_ifr']");
    private final By CSS_REMOVE_FORMAT = By.cssSelector("i.mce-i-removeformat");
    private final By CSS_STR_BOLD = By.cssSelector("i[class$='mce-i-bold']");
    private final By CSS_STR_ITALIC = By.cssSelector("i[class$='mce-i-italic']");
    private final By CSS_STR_UNDER_LINED = By.cssSelector("i[class$='mce-i-underline']");
    private final By CSS_STR_BULLETS = By.cssSelector("i[class$='mce-i-bullist']");
    private final By CSS_STR_NUMBERS = By.cssSelector("i[class$='mce-i-numlist']");
    private final By CSS_STR_BOLD_FMT_TXT = By.cssSelector("#tinymce>p>b");
    private final By CSS_STR_ITALIC_FMT_TXT = By.cssSelector("#tinymce>p>i");
    private final By CSS_STR_UNDER_LINED_FMT_TXT = By.cssSelector("#tinymce>p>u");
    private final By CSS_STR_BULLET_FMT_TXT = By.cssSelector("#tinymce>ul>li");
    private final By CSS_STR_NUMBER_FMT_TXT = By.cssSelector("#tinymce>ol>li");
    private final By CSS_STR_TEXT_TAG = By.cssSelector("#tinymce>p");
    private final By CSS_COLOR_FONT = By.cssSelector("#tinymce>p>font");
    private final By CSS_EDIT = By.cssSelector("button[id$='mce_43-open']");
    private final By CSS_FORMAT = By.cssSelector("button[id$='mce_46-open']");
    private final By CSS_UNDO = By.cssSelector("i[class$='mce-i-undo']");
    private final By CSS_REDO = By.cssSelector("i[class$='mce-i-redo']");
    private final By CSS_BULLET_TEXT = By.cssSelector("#tinymce>ul>li");
    private final By CSS_BOLD_EDIT = By.cssSelector("DIV[class='comments-list']>DIV[class='comment-form'] i[class$='mce-i-bold']");
    private final By CSS_STR_BACK_GROUND_COLOUR = By.cssSelector("div[aria-label*='Background'] button[class='mce-open']");
    private String frameId = null;
    private FormatType formatType;

    public TinyMceEditor(ThreadLocal<WebBrowser> browser)
    {
        super(browser);
    }

    public String getFrameId()
    {
        return frameId;
    }

    private void setFrameId(String frameId)
    {

        this.frameId = frameId;
    }

    public void setFormatType(FormatType formatType)
    {
        this.formatType = formatType;
    }

    public By textElements()
    {
        switch (formatType)
        {
            case BOLD:
                return CSS_STR_BOLD;
            case ITALIC:
                return CSS_STR_ITALIC;
            case UNDERLINED:
                return CSS_STR_UNDER_LINED;
            case BULLET:
                return CSS_STR_BULLETS;
            case NUMBER:
                return CSS_STR_NUMBERS;
            case COLOR:
                return CSS_STR_FORE_COLOUR;
            case FORMAT:
                return CSS_FORMAT;
            case EDIT:
                return CSS_EDIT;
            case UNDO:
                return CSS_UNDO;
            case REDO:
                return CSS_REDO;
            // temporary solution
            case BOLD_EDIT:
                return CSS_BOLD_EDIT;
            case BACK_GROUND_COLOR:
                return CSS_STR_BACK_GROUND_COLOUR;
            default:
                //should changeeeeee
                return null;

        }
    }

    public By getCSSOfText(FormatType formatType)
    {
        switch (formatType)
        {
            case BOLD_FMT_TXT:
                return CSS_STR_BOLD_FMT_TXT;
            case ITALIC_FMT_TXT:
                return CSS_STR_ITALIC_FMT_TXT;
            case UNDER_LINED_FMT_TXT:
                return CSS_STR_UNDER_LINED_FMT_TXT;
            case BULLET_FMT_TXT:
                return CSS_STR_BULLET_FMT_TXT;
            case NUMBER_FMT_TXT:
                return CSS_STR_NUMBER_FMT_TXT;
            case COLOR_FONT:
                return CSS_COLOR_FONT;
            case BULLET_TEXT:
                return CSS_BULLET_TEXT;
            default:
                return CSS_STR_TEXT_TAG;
        }
    }

    public void setTinyMce(String frameId)
    {
        setFrameId(frameId);
    }

    public void addContent(String txt)
    {
        try
        {
            String setCommentJs = String.format("tinyMCE.activeEditor.setContent('%s');", txt);
            getBrowser().executeJavaScript(setCommentJs);
        } catch (NoSuchElementException noSuchElementExp)
        {
            LOG.error("Element : " + txt + " is not present", noSuchElementExp);
        }
    }

    public void clickTextFormatter(FormatType formatType)
    {
        setFormatType(formatType);
        selectTextFromEditor();
        clickElementOnRichTextFormatter(getBrowser().findElement(textElements()));
    }

    public void clickColorCode(TinyMceColourCode colourCode)
    {
        selectTextFromEditor();
        setFormatType(FormatType.COLOR);
        clickElementOnRichTextFormatter(getBrowser().findElement(textElements()));
        WebElement colour = getBrowser().findElement(By.cssSelector(colourCode.getForeColourLocator()));
        clickElementOnRichTextFormatter(colour);
    }

    public void clickBackgroundColorCode(TinyMceColourCode bgColourCode)
    {
        selectTextFromEditor();
        setFormatType(FormatType.BACK_GROUND_COLOR);
        clickElementOnRichTextFormatter(getBrowser().findElement(textElements()));
        WebElement bgColour = getBrowser().findElement(By.cssSelector(bgColourCode.getBgColourLocator()));
        clickElementOnRichTextFormatter(bgColour);
    }

    public void clickUndo()
    {
        setFormatType(FormatType.UNDO);
        clickElementOnRichTextFormatter(getBrowser().findElement(textElements()));
    }

    public void clickEdit()
    {
        setFormatType(FormatType.EDIT);
        clickElementOnRichTextFormatter(getBrowser().findElement(textElements()));
    }

    public void clickFormat()
    {
        setFormatType(FormatType.FORMAT);
        clickElementOnRichTextFormatter(getBrowser().findElement(textElements()));
    }

    public void clickRedo()
    {
        setFormatType(FormatType.REDO);
        clickElementOnRichTextFormatter(getBrowser().findElement(textElements()));
    }

    /**
     * Click to remove formatting from text.
     */
    public void removeFormatting()
    {
        try
        {
            getBrowser().findElement(CSS_REMOVE_FORMAT).click();
        }
        catch (NoSuchElementException noSuchElementExp)
        {
            LOG.error("Element :" + CSS_REMOVE_FORMAT + " does not exist", noSuchElementExp);
        }
    }

    public void selectTextFromEditor()
    {
        // This select all in the edit pane
        /**
         * @author Michael Suzuki Changed to use tinymce directly as its faster to edit with tinymce object instead of using the ui.
         * The script below will select every thing inside the editing pane.
         */
        getBrowser().executeJavaScript(TINY_MCE_SELECT_ALL_COMMAND);
    }

    protected void clickElementOnRichTextFormatter(WebElement element)
    {
        try
        {
            getBrowser().switchToDefaultContent();
            element.click();

        } catch (NoSuchElementException noSuchElementExp)
        {
            LOG.error("Element :" + element + " does not exist", noSuchElementExp);
        }
    }

    public String getText()
    {
        try
        {
            getBrowser().switchToFrame(getFrameId());
            String text = getBrowser().findElement(By.cssSelector(TINYMCE_CONTENT)).getText();
            getBrowser().switchToDefaultContent();
            return text;
        }
        catch (NoSuchElementException noSuchElementExp)
        {
            LOG.error("Element : does not exist", noSuchElementExp);
            return "";
        }
    }

    public void setText(String text)
    {
        getBrowser().clickJS(getBrowser().findElement(iFrame));

        if (text == null)
        {
            throw new IllegalArgumentException("Text is required");
        }

        String setCommentJs = String.format("tinyMCE.activeEditor.setContent('%s');", "");
        getBrowser().executeJavaScript(setCommentJs);
        setCommentJs = String.format("tinyMCE.activeEditor.setContent('%s');", text);
        getBrowser().executeJavaScript(setCommentJs);
    }

    public void clickTextFormatterWithOutSelectingText(FormatType formatType)
    {
        setFormatType(formatType);
        clickElementOnRichTextFormatter(getBrowser().findElement(textElements()));
    }


    public void clearAll()
    {
        String setCommentJs = String.format("tinyMCE.activeEditor.setContent('%s');", "");
        getBrowser().executeJavaScript(setCommentJs);
    }

    public enum FormatType
    {
        BOLD,
        ITALIC,
        UNDERLINED,
        NUMBER,
        BULLET,
        BOLD_FMT_TXT,
        ITALIC_FMT_TXT,
        UNDER_LINED_FMT_TXT,
        BULLET_FMT_TXT,
        NUMBER_FMT_TXT,
        COLOR,
        FORMAT,
        EDIT,
        UNDO,
        REDO,
        DEFAULT,
        COLOR_FONT,
        BULLET_TEXT,
        BOLD_EDIT,
        BACK_GROUND_COLOR
    }
}
