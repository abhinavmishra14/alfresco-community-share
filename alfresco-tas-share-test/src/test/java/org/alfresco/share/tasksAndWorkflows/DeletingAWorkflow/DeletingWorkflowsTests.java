package org.alfresco.share.tasksAndWorkflows.DeletingAWorkflow;

import org.alfresco.common.DataUtil;
import org.alfresco.dataprep.CMISUtil;
import org.alfresco.dataprep.WorkflowService;
import org.alfresco.po.share.tasksAndWorkflows.MyTasksPage;
import org.alfresco.po.share.tasksAndWorkflows.WorkflowsIveStartedPage;
import org.alfresco.share.ContextAwareWebTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.model.TestGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.Date;
import java.util.List;

/**
 * @author Razvan.Dorobantu
 */
public class DeletingWorkflowsTests  extends ContextAwareWebTest
{
    @Autowired
    WorkflowsIveStartedPage workflowsIveStartedPage;

    @Autowired
    WorkflowService workflow;

    @Autowired
    MyTasksPage myTasksPage;

    private String workflowName = String.format("taskName%s", DataUtil.getUniqueIdentifier());

    @TestRail(id = "C8501")
    @Test(groups = { TestGroup.SANITY, TestGroup.TASKS})
    public void deleteWorkflow()
    {
        LOG.info("Precondition: Create user and a workflow.");
        String testUser = String.format("testUser%s", DataUtil.getUniqueIdentifier());
        userService.create(adminUser, adminPassword, testUser, password, testUser + domain, testUser, "lastName");
        String workflowID = workflow.startNewTask(testUser, password, workflowName, new Date(), testUser, CMISUtil.Priority.Normal, null, false);
        workflow.taskDone(testUser, password, workflowID, WorkflowService.TaskStatus.COMPLETED, "C8434");
        workflow.taskDone(testUser, password, workflowID, WorkflowService.TaskStatus.COMPLETED, "C8434");
        setupAuthenticatedSession(testUser, password);

        LOG.info("STEP 1: From 'Tasks' dropdown click 'Workflows I've Started' option.");
        workflowsIveStartedPage.navigate();
        List<String> workflows = workflowsIveStartedPage.getActiveWorkflows();
        Assert.assertFalse(workflows.contains(workflowName), String.format("Workflow: %s is not completed.", workflowName));

        LOG.info("STEP 2: Click on 'Completed' option from the left side panel");
        workflowsIveStartedPage.clickCompletedFilter();
        workflows = workflowsIveStartedPage.getActiveWorkflows();
        Assert.assertTrue(workflows.contains(workflowName), String.format("Workflow: %s is completed.", workflowName));

        LOG.info("STEP 3: Click on 'Delete Workflow' option.");
        workflowsIveStartedPage.clickDeleteWorkflow(workflowName, true);
        workflows = workflowsIveStartedPage.getActiveWorkflows();
        Assert.assertFalse(workflows.contains(workflowName), String.format("Workflow: %s is not deleted.", workflowName));

        LOG.info("STEP 4: Verify the deleted workflow is not present in 'My Tasks' page.");
        myTasksPage.navigate();
        myTasksPage.clickCompletedTasks();
        Assert.assertFalse(myTasksPage.checkTaskWasFound(workflowName), String.format("Workflow: %s is found in 'My Tasks' page.", workflowName));
    }
}
