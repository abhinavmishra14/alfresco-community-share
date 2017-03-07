package org.alfresco.po.share.user.admin;

import org.alfresco.po.annotation.PageObject;
import org.alfresco.po.annotation.RenderWebElement;
import org.alfresco.po.share.SharePage;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * @author Laura.Capsa
 */
@PageObject
public class ReplicationJobsPage extends SharePage<ReplicationJobsPage>
{
    @Autowired
    CreateEditReplicationJobPage createEditReplicationJobPage;

    @RenderWebElement
    @FindBy(css = "a[id*='create']")
    private WebElement createJobButton;

    @FindBy(css = "button[id*='run']")
    private WebElement runJobButton;

    @FindBy(css = "[id*='jobsList'] a")
    private List<WebElement> jobsList;

    @FindBy(css = "[id*='jobStatus']")
    private WebElement status;

    @Override
    public String getRelativePath()
    {
        return "/page/console/admin-console/replication-jobs";
    }

    public CreateEditReplicationJobPage clickCreateJobButton()
    {
        createJobButton.click();
        return (CreateEditReplicationJobPage) createEditReplicationJobPage.renderedPage();
    }

    public void clickRunJobButton()
    {
        runJobButton.click();
    }

    /**
     * Verify presence of job in "Jobs" section
     * 
     * @param jobName name of the job to be checked
     * @return true if job is displayed
     */
    public boolean isJobDisplayedInList(String jobName)
    {
        for (int i = 0; i < jobsList.size(); i++)
        {
            if (jobsList.get(i).getText().equals(jobName))
                return true;
        }
        return false;
    }

    public String getJobStatus()
    {
        return status.getText();
    }
}