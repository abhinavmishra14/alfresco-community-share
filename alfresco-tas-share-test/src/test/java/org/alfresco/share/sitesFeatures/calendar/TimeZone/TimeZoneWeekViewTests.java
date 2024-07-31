package org.alfresco.share.sitesFeatures.calendar.TimeZone;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import java.io.IOException;

import lombok.extern.slf4j.Slf4j;
import org.alfresco.dataprep.DashboardCustomization.Page;
import org.alfresco.dataprep.SiteService;

import org.alfresco.po.share.Notification;
import org.alfresco.po.share.site.calendar.AddEventDialogPage;
import org.alfresco.po.share.site.calendar.CalendarPage;
import org.alfresco.po.share.site.calendar.EventInformationDialogPage;

import org.alfresco.share.BaseTest;
import org.alfresco.testrail.TestRail;

import org.alfresco.utility.model.SiteModel;
import org.alfresco.utility.model.TestGroup;
import org.alfresco.utility.model.UserModel;

import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;

@Slf4j
/**
 * Created by Claudia Agache on 7/26/2016.
 */
public class TimeZoneWeekViewTests extends BaseTest
{
    //@Autowired
    CalendarPage calendarPage;
    AddEventDialogPage addEventDialogPage;
    EventInformationDialogPage eventInformationDialogPage;
    @Autowired
    private SiteService siteService;
    @Autowired
    Notification notification;
    DateTime today;
    DateTime tomorrow;
    DateTime dayAfterTomorrow;
    DateTime aWeekAgo;
    DateTime nextWeek;
    DateTime aMonthAgo;
    DateTime nextMonth;
    private String clientATimeZone = "tzutil /s \"GTB Standard Time\"";
    private String clientBTimeZone = "tzutil /s \"GMT Standard Time\"";
    private final ThreadLocal<UserModel> user = new ThreadLocal<>();
    private final ThreadLocal<SiteModel> siteName = new ThreadLocal<>();
    @BeforeMethod (alwaysRun = true)
    public void setupMethod()
    {
        today = new DateTime();
        tomorrow = today.plusDays(1);
        dayAfterTomorrow = today.plusDays(2);
        aWeekAgo = today.minusWeeks(1);
        nextWeek = today.plusWeeks(1);
        aMonthAgo = today.minusMonths(1);
        nextMonth = today.plusMonths(1);
    }

    @BeforeMethod(alwaysRun = true)
    public void setupTest()
    {
        log.info("Precondition: Any Test user is created");
        user.set(getDataUser().usingAdmin().createRandomTestUser());
        getCmisApi().authenticateUser(getAdminUser());

        log.info("PreCondition: Site siteNameC2216 is created");
        siteName.set(getDataSite().usingUser(user.get()).createPublicRandomSite());
        getCmisApi().authenticateUser(user.get());

        calendarPage = new CalendarPage(webDriver);
        addEventDialogPage = new AddEventDialogPage(webDriver);
        eventInformationDialogPage = new EventInformationDialogPage(webDriver);

        siteService.addPageToSite(user.get().getUsername(), user.get().getPassword(), siteName.get().getId(), Page.CALENDAR, null);
        authenticateUsingLoginPage(user.get());
    }

    @AfterMethod(alwaysRun = true)
    public void cleanup()
    {
        contentService.deleteTreeByPath(getAdminUser().getUsername(), getAdminUser().getPassword(), "/User Homes/" + user.get().getUsername());
        deleteSitesIfNotNull(siteName.get());
        deleteUsersIfNotNull(user.get());
    }


    private String formatDate(DateTime date, String hour)
    {
        return date.toString("EEEE, d MMMM, yyyy") + " at " + hour;
    }

    private void changeTimeZone(String command)
    {
        try
        {
            Runtime.getRuntime().exec(command);
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }

 //   @Bug (id = "SHA-2165", status = Bug.Status.OPENED, description = "Time displayed in Firefox is not the same as the time on local machine")
    @TestRail (id = "C5960")
    @Test(groups = { TestGroup.SANITY, TestGroup.SITES_FEATURES, "TimeZoneTests" }, enabled = true)
    public void singleDayEvent()
    {
        String currentEventName = "testEvent-C5960";
        changeTimeZone(clientATimeZone);
        calendarPage.navigate(siteName.get());
        calendarPage.clickWeekButton();

        log.info("STEP 1: Create a new single day event on clientA: Start Date today at 2:30 PM, End Date today at 5:30 PM");
        calendarPage.clickAddEventButton();
        addEventDialogPage.typeInEventTitleInput(currentEventName);
        addEventDialogPage.typeInStartTimeInput("2:30 PM");
        addEventDialogPage.typeInEndTimeInput("5:30 PM");
        addEventDialogPage.clickSaveButton();
//        assertEquals(notification.getDisplayedNotification(), "Event created", "'Event created' pop-up appears.");
        assertTrue(calendarPage.isEventPresentInCalendar(currentEventName), "Event is created and displayed on the 'Calendar' page.");
        assertEquals(calendarPage.getEventStartTimeFromCalendar(currentEventName), "2:30pm", "Event starting time is the one filled at creation.");

        log.info("STEP 2: Open 'Calendar' page ('Week' view) on clientB and verify event's details, by clicking on the event.");
        changeTimeZone(clientBTimeZone);

        assertTrue(calendarPage.isEventPresentInCalendar(currentEventName), "Event is displayed on the 'Calendar' page.");
        calendarPage.click_Event(currentEventName);
        assertEquals(eventInformationDialogPage.getStartDateTime(), formatDate(today, "12:30 PM"),
            "Following information is available: Time section with Start Date");
        assertEquals(eventInformationDialogPage.getEndDateTime(), formatDate(today, "3:30 PM"), "Following information is available: Time section with End Date");
    }

    @TestRail (id = "C5961")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITES_FEATURES })
    public void singleDayAllDayEvent()
    {
        String currentEventName = "testEvent-C5961";
        changeTimeZone(clientATimeZone);
        calendarPage.navigate(siteName.get());
        calendarPage.clickWeekButton();

        log.info("STEP 1: Create a new single day event on clientA: Start Date today, End Date today, All Day yes");
        calendarPage.clickAddEventButton();
        addEventDialogPage.typeInEventTitleInput(currentEventName);
        addEventDialogPage.checkAllDayCheckBox();
        addEventDialogPage.clickSaveButton();
        assertTrue(calendarPage.isEventPresentInCalendar(currentEventName), "Event is created and displayed on the 'Calendar' page.");
//        assertTrue(calendarPage.isAllDayEvent(currentEventName), "Created event is an all day event.");

        log.info("STEP 2: Open 'Calendar' page ('Week' view) on clientB and verify event's details, by clicking on the event.");
        changeTimeZone(clientBTimeZone);

        assertTrue(calendarPage.isEventPresentInCalendar(currentEventName), "Event is displayed on the 'Calendar' page.");
        calendarPage.click_Event(currentEventName);
        assertEquals(eventInformationDialogPage.getStartDateTime(), today.toString("EEEE, d MMMM, yyyy"),
            "Following information is available: Time section with Start Date");
        assertEquals(eventInformationDialogPage.getEndDateTime(), today.toString("EEEE, d MMMM, yyyy"),
            "Following information is available: Time section with End Date");
    }

//    @Bug (id = "SHA-2165", status = Bug.Status.OPENED, description = "Time displayed in Firefox is not the same as the time on local machine")
    @TestRail (id = "C5962")
    @Test(groups = { TestGroup.SANITY, TestGroup.SITES_FEATURES, "TimeZoneTests" }, enabled = true)
    public void multipleDaysEvent()
    {
        String currentEventName = "testEvent-C5962";
        changeTimeZone(clientATimeZone);
        calendarPage.navigate(siteName.get());
        calendarPage.clickWeekButton();

        log.info("STEP 1:Create a multiple days event on clientA: Start Date today at 2:30 PM, End Date the day after tomorrow at 5:25 PM");
        calendarPage.clickAddEventButton();
        addEventDialogPage.typeInEventTitleInput(currentEventName);
        addEventDialogPage.typeInStartTimeInput("2:30 PM");
        addEventDialogPage.selectEndDateFromCalendarPicker(dayAfterTomorrow.getDayOfMonth(), dayAfterTomorrow.getMonthOfYear(), dayAfterTomorrow.getYear());
        addEventDialogPage.typeInEndTimeInput("5:25 PM");
        addEventDialogPage.clickSaveButton();
        assertTrue(calendarPage.isEventPresentInCalendar(currentEventName), "Event is created and displayed on the 'Calendar' page.");
        assertEquals(calendarPage.getEventStartTimeFromCalendar(currentEventName), "2:30pm", "Event starting time is the one filled at creation.");

        log.info("STEP 2: Open 'Calendar' page ('Week' view) on clientB and verify event's details, by clicking on the event.");
        changeTimeZone(clientBTimeZone);

        assertTrue(calendarPage.isEventPresentInCalendar(currentEventName), "Event is displayed on the 'Calendar' page.");
        calendarPage.click_Event(currentEventName);
        assertEquals(eventInformationDialogPage.getStartDateTime(), formatDate(today, "12:30 PM"),
            "Following information is available: Time section with Start Date");
        assertEquals(eventInformationDialogPage.getEndDateTime(), formatDate(dayAfterTomorrow, "3:25 PM"),
            "Following information is available: Time section with End Date");
    }

    @TestRail (id = "C5963")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITES_FEATURES })
    public void multipleDaysFAllDayEvent()
    {
        String currentEventName = "testEvent-C5963";
        changeTimeZone(clientATimeZone);
        calendarPage.navigate(siteName.get());
        calendarPage.clickWeekButton();

        log.info("STEP 1:Create a multiple days event on clientA: Start Date today, End Date the day after tomorrow, All Day yes");
        calendarPage.clickAddEventButton();
        addEventDialogPage.typeInEventTitleInput(currentEventName);
        addEventDialogPage.checkAllDayCheckBox();
        addEventDialogPage.selectEndDateFromCalendarPicker(dayAfterTomorrow.getDayOfMonth(), dayAfterTomorrow.getMonthOfYear(), dayAfterTomorrow.getYear());
        addEventDialogPage.clickSaveButton();
        assertTrue(calendarPage.isEventPresentInCalendar(currentEventName), "Event is created and displayed on the 'Calendar' page.");
//        assertTrue(calendarPage.isAllDayEvent(currentEventName), "Event created is an all day event.");

        log.info("STEP 2: Open 'Calendar' page ('Week' view) on clientB and verify event's details, by clicking on the event.");
        changeTimeZone(clientBTimeZone);

        assertTrue(calendarPage.isEventPresentInCalendar(currentEventName), "Event is displayed on the 'Calendar' page.");
        calendarPage.click_Event(currentEventName);
        assertEquals(eventInformationDialogPage.getStartDateTime(), today.toString("EEEE, d MMMM, yyyy"),
            "Following information is available: Time section with Start Date");
        assertEquals(eventInformationDialogPage.getEndDateTime(), dayAfterTomorrow.toString("EEEE, d MMMM, yyyy"),
            "Following information is available: Time section with End Date");
        changeTimeZone(clientATimeZone);
    }

//    @Bug (id = "SHA-2165", status = Bug.Status.OPENED, description = "Time displayed in Firefox is not the same as the time on local machine")
    @TestRail (id = "C5964")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITES_FEATURES }, enabled = false)
    public void multipleWeeksEvent()
    {
        String currentEventName = "testEvent-C5964";
        changeTimeZone(clientATimeZone);
        calendarPage.navigate(siteName.get());
        calendarPage.clickWeekButton();

        log.info("STEP 1:Create a multiple days event on clientA: Start Date a date from a week ago at 2:30 PM, End Date a date from next week at 5:25 PM");
        calendarPage.clickAddEventButton();
        addEventDialogPage.typeInEventTitleInput(currentEventName);
        addEventDialogPage.selectStartDateFromCalendarPicker(aWeekAgo.getDayOfMonth(), aWeekAgo.getMonthOfYear(), aWeekAgo.getYear());
        addEventDialogPage.typeInStartTimeInput("2:30 PM");
        addEventDialogPage.selectEndDateFromCalendarPicker(nextWeek.getDayOfMonth(), nextWeek.getMonthOfYear(), nextWeek.getYear());
        addEventDialogPage.typeInEndTimeInput("5:25 PM");
        addEventDialogPage.clickSaveButton();
        assertTrue(calendarPage.isEventPresentInCalendar(currentEventName), "Event is created and displayed on the 'Calendar' page.");
        assertEquals(calendarPage.getEventStartTimeFromCalendar(currentEventName), "2:30pm", "Event starting time is the one filled at creation.");

        log.info("STEP 2: Open 'Calendar' page ('Week' view) on clientB and verify event's details, by clicking on the event.");
        changeTimeZone(clientBTimeZone);

        assertTrue(calendarPage.isEventPresentInCalendar(currentEventName), "Event is displayed on the 'Calendar' page.");
        calendarPage.clickOnEvent(currentEventName);
        assertEquals(eventInformationDialogPage.getStartDateTime(), formatDate(aWeekAgo, "12:30 PM"),
            "Following information is available: Time section with Start Date");
        assertEquals(eventInformationDialogPage.getEndDateTime(), formatDate(nextWeek, "3:25 PM"), "Following information is available: Time section with End Date");
    }

    @TestRail (id = "C5965")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITES_FEATURES })
    public void multipleWeeksAllDayEvent()
    {
        String currentEventName = "testEvent-C5965";
        changeTimeZone(clientATimeZone);
        calendarPage.navigate(siteName.get());
        calendarPage.clickWeekButton();

        log.info("STEP 1:Create a multiple days event on clientA: Start Date a date from a week ago, End Date a date from next week, All day yes");
        calendarPage.clickAddEventButton();
        addEventDialogPage.typeInEventTitleInput(currentEventName);
        addEventDialogPage.checkAllDayCheckBox();
        addEventDialogPage.selectStartDateFromCalendarPicker(aWeekAgo.getDayOfMonth(), aWeekAgo.getMonthOfYear(), aWeekAgo.getYear());
        addEventDialogPage.selectEndDateFromCalendarPicker(nextWeek.getDayOfMonth(), nextWeek.getMonthOfYear(), nextWeek.getYear());
        addEventDialogPage.clickSaveButton();
        assertTrue(calendarPage.isEventPresentInCalendar(currentEventName), "Event is created and displayed on the 'Calendar' page.");
//        assertTrue(calendarPage.isAllDayEvent(currentEventName), "Event created is an all day event.");

        log.info("STEP 2: Open 'Calendar' page ('Week' view) on clientB and verify event's details, by clicking on the event.");
        changeTimeZone(clientBTimeZone);

        assertTrue(calendarPage.isEventPresentInCalendar(currentEventName), "Event is displayed on the 'Calendar' page.");
        calendarPage.click_Event(currentEventName);
        assertEquals(eventInformationDialogPage.getStartDateTime(), aWeekAgo.toString("EEEE, d MMMM, yyyy"),
            "Following information is available: Time section with Start Date");
        assertEquals(eventInformationDialogPage.getEndDateTime(), nextWeek.toString("EEEE, d MMMM, yyyy"),
            "Following information is available: Time section with End Date");
    }

//    @Bug (id = "SHA-2165", status = Bug.Status.OPENED, description = "Time displayed in Firefox is not the same as the time on local machine")
    @TestRail (id = "C5966")
    @Test(groups = { TestGroup.SANITY, TestGroup.SITES_FEATURES, "TimeZoneTests" }, enabled = true)
    public void multipleMonthsEvent()
    {
        String currentEventName = "testEvent-C5966";
        changeTimeZone(clientATimeZone);
        calendarPage.navigate(siteName.get());
        calendarPage.clickWeekButton();

        log.info("STEP 1:Create a multiple days event on clientA: Start Date a date from a month ago at 2:30 PM, End Date a date from next month at 5:25 PM");
        calendarPage.clickAddEventButton();
        addEventDialogPage.typeInEventTitleInput(currentEventName);
        addEventDialogPage.selectStartDateFromCalendarPicker(aMonthAgo.getDayOfMonth(), aMonthAgo.getMonthOfYear(), aMonthAgo.getYear());
        addEventDialogPage.typeInStartTimeInput("2:30 PM");
        addEventDialogPage.selectEndDateFromCalendarPicker(nextMonth.getDayOfMonth(), nextMonth.getMonthOfYear(), nextMonth.getYear());
        addEventDialogPage.typeInEndTimeInput("5:25 PM");
        addEventDialogPage.clickSaveButton();
        assertTrue(calendarPage.isEventPresentInCalendar(currentEventName), "Event is created and displayed on the 'Calendar' page.");

        log.info("STEP 2: Open 'Calendar' page ('Week' view) on clientB and verify event's details, by clicking on the event.");
        changeTimeZone(clientBTimeZone);

        assertTrue(calendarPage.isEventPresentInCalendar(currentEventName), "Event is displayed on the 'Calendar' page.");
        calendarPage.click_Event(currentEventName);
        assertEquals(eventInformationDialogPage.getStartDateTime(), formatDate(aMonthAgo, "12:30 PM"),
            "Following information is available: Time section with Start Date");
        assertEquals(eventInformationDialogPage.getEndDateTime(), formatDate(nextMonth, "3:25 PM"),
            "Following information is available: Time section with End Date");
    }

    @TestRail (id = "C5968")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITES_FEATURES, "tobefixed" })
    public void multipleMonthsAllDayEvent()
    {
        String currentEventName = "testEvent-C5968";
        changeTimeZone(clientATimeZone);
        calendarPage.navigate(siteName.get());
        calendarPage.clickWeekButton();

        log.info("STEP 1:Create a multiple days event on clientA: Start Date a date from a month ago, End Date a date from next month, All day yes");
        calendarPage.clickAddEventButton();
        addEventDialogPage.typeInEventTitleInput(currentEventName);
        addEventDialogPage.checkAllDayCheckBox();
        addEventDialogPage.selectStartDateFromCalendarPicker(aMonthAgo.getDayOfMonth(), aMonthAgo.getMonthOfYear(), aMonthAgo.getYear());
        addEventDialogPage.selectEndDateFromCalendarPicker(nextMonth.getDayOfMonth(), nextMonth.getMonthOfYear(), nextMonth.getYear());
        addEventDialogPage.clickSaveButton();
        assertTrue(calendarPage.isEventPresentInCalendar(currentEventName), "Event is created and displayed on the 'Calendar' page.");
//        assertTrue(calendarPage.isAllDayEvent(currentEventName), "Event created is an all day event.");

        log.info("STEP 2: Open 'Calendar' page ('Week' view) on clientB and verify event's details, by clicking on the event.");
        changeTimeZone(clientBTimeZone);

        assertTrue(calendarPage.isEventPresentInCalendar(currentEventName), "Event is displayed on the 'Calendar' page.");
        calendarPage.click_Event(currentEventName);
        assertEquals(eventInformationDialogPage.getStartDateTime(), aMonthAgo.toString("EEEE, d MMMM, yyyy"),
            "Following information is available: Time section with Start Date");
        assertEquals(eventInformationDialogPage.getEndDateTime(), nextMonth.toString("EEEE, d MMMM, yyyy"),
            "Following information is available: Time section with End Date");
    }

//    @Bug (id = "SHA-2165", status = Bug.Status.OPENED, description = "Time displayed in Firefox is not the same as the time on local machine")
    @TestRail (id = "C5969")
    @Test(groups = { TestGroup.SANITY, TestGroup.SITES_FEATURES, "TimeZoneTests" }, enabled = true)
    public void specificScenario()
    {
        String currentEventName = "testEvent-C5969";
        changeTimeZone(clientATimeZone);
        calendarPage.navigate(siteName.get());
        calendarPage.clickWeekButton();

        log.info("STEP 1:Create a multiple days event on clientA: Start Date today at 2:30 PM, End Date tomorrow at 1:59 AM");
        calendarPage.clickAddEventButton();
        addEventDialogPage.typeInEventTitleInput(currentEventName);
        addEventDialogPage.typeInStartTimeInput("2:30 PM");
        addEventDialogPage.selectEndDateFromCalendarPicker(tomorrow.getDayOfMonth(), tomorrow.getMonthOfYear(), tomorrow.getYear());
        addEventDialogPage.typeInEndTimeInput("1:59 AM");
        addEventDialogPage.clickSaveButton();
        assertTrue(calendarPage.isEventPresentInCalendar(currentEventName), "Event is created and displayed on the 'Calendar' page.");
        assertEquals(calendarPage.getEventStartTimeFromCalendar(currentEventName), "2:30pm", "Event starting time is the one filled at creation.");

        log.info("STEP 2: Open 'Calendar' page ('Week' view) on clientB and verify event's details, by clicking on the event.");
        changeTimeZone(clientBTimeZone);

        assertTrue(calendarPage.isEventPresentInCalendar(currentEventName), "Event is displayed on the 'Calendar' page.");
        calendarPage.click_Event(currentEventName);
        assertEquals(eventInformationDialogPage.getStartDateTime(), formatDate(today, "12:30 PM"),
            "Following information is available: Time section with Start Date");
        assertEquals(eventInformationDialogPage.getEndDateTime(), formatDate(today, "11:59 PM"), "Following information is available: Time section with End Date");
    }
}
