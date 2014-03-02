/*
 * Copyright (C) 2005-2014 Alfresco Software Limited.
 *
 * This file is part of Alfresco
 *
 * Alfresco is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Alfresco is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Alfresco. If not, see <http://www.gnu.org/licenses/>.
 */
package org.alfresco.po.rm.functional;

import org.alfresco.po.rm.fileplan.FilePlanPage;
import org.alfresco.po.rm.fileplan.filter.FilePlanFilter;
import org.alfresco.po.rm.fileplan.filter.hold.HoldsContainer;
import org.alfresco.po.rm.fileplan.toolbar.CreateNewHoldDialog;
import org.alfresco.po.rm.util.RmPageObjectUtils;
import org.alfresco.po.share.site.document.FileDirectoryInfo;
import org.alfresco.po.share.util.FailedTestListener;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

/**
 * This test suite tests the following new features:
 * <p>
 * <ul>
 *  <li>Creating a new hold in the holds container</li>
 *  <li>Viewing the manage permissions page for the for hold container</li>
 *  <li>Viewing the manage permissions page for the newly created hold</li>
 *  <li>Viewing the details page of the hold container</li>
 *  <li>Deleting the new hold in the holds container</li>
 * </ul>
 * <p>
 * @author Tuna Aksoy
 * @version 2.2
 */
@Listeners(FailedTestListener.class)
public class HoldContainerIntTest extends AbstractIntegrationTest
{
    /** Constants for the new hold dialog */
    private static final String NAME = "New Hold";
    private static final String REASON = "Reason for hold";

    /** Selectors */
    private static final String ACTION_SELECTOR_TEXT_MANAGE_PERMISSONS = "div.rm-manage-permissions>a";
    private static final String ACTION_SELECTOR_TEXT_EDIT_DETAILS = "div.rm-edit-details>a";
    private static final String ACTION_SELECTOR_TEXT_DELETE = "div.rm-delete>a";
    private static final String ACTIONS = "td:nth-of-type(5)";
    private static final By MANAGE_PERMISSIONS_BUTTON = By.cssSelector("button[id$='default-holdPermissions-button-button']");
    private static final By ADD_USER_GROUP_BUTTON = By.cssSelector("button[id$='-addusergroup-button-button']");
    private static final By FINISH_BUTTON = By.cssSelector("button[id$='-finish-button-button']");
    private static final By PROMPT = By.cssSelector("div#prompt div.ft span span button");

    /** Member variables */
    private HoldsContainer holdsContainer;
    private FilePlanPage filePlan;
    private FilePlanFilter filePlanFilter;

    /**
     * Helper method to select the holds container
     *
     * @return {@link HoldsContainer} Returns the hold container object
     */
    private HoldsContainer selectHoldsContainer()
    {
        filePlan = rmSiteDashBoard.selectFilePlan().render();
        filePlanFilter = filePlan.getFilePlanFilter();
        return filePlanFilter.selectHoldsContainer().render();
    }

    /**
     * Helper method to click on an action for a given hold
     *
     * @param selector {@link String} The selector text for the action to select
     */
    private void clickAction(String selector)
    {
        holdsContainer = selectHoldsContainer();
        FileDirectoryInfo hold = holdsContainer.getFileDirectoryInfo(NAME);
        WebElement actions = hold.findElement(By.cssSelector(ACTIONS));
        drone.mouseOverOnElement(actions);
        hold.findElement(By.cssSelector(selector)).click();
    }

    @Test
    public void createNewHold()
    {
        holdsContainer = selectHoldsContainer();
        CreateNewHoldDialog newHoldDialog = holdsContainer.selectCreateNewHold().render();
        newHoldDialog.enterName(NAME);
        newHoldDialog.enterReason(REASON);
        newHoldDialog.tickDeleteHold(true);
        holdsContainer = ((HoldsContainer) newHoldDialog.selectSave()).render(NAME);
    }

    @Test(dependsOnMethods="createNewHold")
    public void managePermissionsForRoot()
    {
        RmPageObjectUtils.select(drone, MANAGE_PERMISSIONS_BUTTON);
        RmPageObjectUtils.select(drone, ADD_USER_GROUP_BUTTON);
        RmPageObjectUtils.select(drone, FINISH_BUTTON);
    }

    @Test(dependsOnMethods="managePermissionsForRoot")
    public void managePermissions()
    {
        clickAction(ACTION_SELECTOR_TEXT_MANAGE_PERMISSONS);
    }

    @Test(dependsOnMethods="managePermissions")
    public void editDetails()
    {
        clickAction(ACTION_SELECTOR_TEXT_EDIT_DETAILS);
    }

    @Test(dependsOnMethods="editDetails")
    public void deleteHold()
    {
        clickAction(ACTION_SELECTOR_TEXT_DELETE);
        RmPageObjectUtils.select(drone, PROMPT);
    }
}