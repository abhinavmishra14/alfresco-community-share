package org.alfresco.po.share;

import org.alfresco.utility.model.UserModel;
import org.alfresco.utility.web.HtmlPage;

public abstract class CommonLoginPage extends HtmlPage
{

    public abstract HtmlPage loginSucced(String adminUser, String adminPassword);

    public abstract CommonLoginPage assertPageIsOpened();

    public abstract void login(String username, String password);

    public abstract CommonLoginPage navigate();

    public abstract CommonLoginPage assertAuthenticationErrorIsDisplayed();

    public abstract CommonLoginPage assertAuthenticationErrorMessageIsCorrect();

    public abstract void autoCompleteUsername(String username);

    public abstract void typePassword(String password);

    public abstract void clickLogin();

    public abstract boolean isAuthenticationErrorDisplayed();

    public abstract void login(UserModel specialUser);
    
    public abstract CommonLoginPage assertLoginPageTitleIsCorrect();

}
