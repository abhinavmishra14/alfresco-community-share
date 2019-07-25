package org.alfresco.po.alfrescoconsoles;

import static org.alfresco.utility.report.log.Step.STEP;

import org.alfresco.utility.web.HtmlPage;

/**
 * Created by Mirela Tifui on 11/1/2017.
 * Consoles Page
 */
public abstract class Console<T> extends HtmlPage
{
    protected abstract String relativePathToURL();

    public T navigate()
    {
        String baseUrl = String.format("%s://%s:%s@%s:%s", properties.getScheme(), properties.getAdminUser(), properties.getAdminPassword(), properties.getServer(), properties.getPort());

        STEP(String.format("Navigate to %s", baseUrl + "/" + relativePathToURL()));

        browser.navigate().to(baseUrl + "/" + relativePathToURL());
        return (T) renderedPage();
    }

}
