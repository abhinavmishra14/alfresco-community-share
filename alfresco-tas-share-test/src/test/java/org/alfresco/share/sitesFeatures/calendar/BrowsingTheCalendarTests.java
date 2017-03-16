package org.alfresco.share.sitesFeatures.calendar;

import org.alfresco.common.DataUtil;
import org.alfresco.dataprep.DashboardCustomization.Page;
import org.alfresco.dataprep.SitePagesService;
import org.alfresco.po.share.site.calendar.CalendarPage;
import org.alfresco.po.share.site.calendar.CalendarUtility;
import org.alfresco.po.share.site.calendar.MiniCalendar;
import org.alfresco.share.ContextAwareWebTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.model.TestGroup;
import org.joda.time.DateTime;
import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.social.alfresco.api.entities.Site.Visibility;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.text.SimpleDateFormat;
import java.util.*;

public class BrowsingTheCalendarTests extends ContextAwareWebTest
{
    @Autowired
    SitePagesService sitePagesService;

    @Autowired
    CalendarPage calendarPage;

    @Autowired
    MiniCalendar miniCalendar;

    @Autowired
    CalendarUtility calendarUtility;

    private String user = "C3155User" + DataUtil.getUniqueIdentifier();
    private String siteName = "C3155SiteName" + DataUtil.getUniqueIdentifier();
    private String description = "C3155SiteDescription" + DataUtil.getUniqueIdentifier();
    private DateTime today = new DateTime();
    private DateTime yesterday = today.minusDays(1);
    private DateTime tomorrow = today.plusDays(1);
    private Date startDate = today.toDate();

    Calendar refferenceCalendar = Calendar.getInstance();
    Integer dayOfMonth = refferenceCalendar.get(Calendar.DAY_OF_MONTH);

    private String eventTitle = "testEvent";
    private String eventTitle2 = "testEvent2";
    private String eventTitle3 = "testEvent3";
    private String eventLocation = "Iasi C5805";
    private String eventDescription = "Event description C5805";

    @BeforeClass
    public void setupTest()
    {
        List<Page> pagesToAdd = new ArrayList<Page>();
        pagesToAdd.add(Page.CALENDAR);
        userService.create(adminUser, adminPassword, user, password, user + "@tests.com", user, user);
        siteService.create(adminUser, adminPassword, user, siteName, description, Visibility.PUBLIC);
        siteService.addPagesToSite(adminUser, adminPassword, siteName, pagesToAdd);
        setupAuthenticatedSession(adminUser, adminPassword);
    }

    @TestRail(id = "C5805")
    @Test(groups = { TestGroup.SANITY, TestGroup.SITES })
    public void changeTheMainCalendarView()
    {
        LOG.info("Precondition:  Add calendar event");

        sitePagesService.addCalendarEvent(user, password, siteName, eventTitle, eventLocation, eventDescription, startDate, startDate, "", "", false, "tag1");
        sitePagesService.addCalendarEvent(user, password, siteName, eventTitle2, eventLocation, eventDescription, calendarUtility.firstDayOfCW(),
                calendarUtility.firstDayOfCW(), "", "", false, "tag1");
        sitePagesService.addCalendarEvent(user, password, siteName, eventTitle3, eventLocation, eventDescription, calendarUtility.firstDayOfCM(),
                calendarUtility.firstDayOfCM(), "", "", false, "tag1");

        LOG.info("Step 1: Navigate to the Calendar page for Site.");
        calendarPage.navigate(siteName);
        calendarPage.renderedPage();
        getBrowser().waitInSeconds(2);
        Assert.assertEquals(calendarPage.getSelectedViewName(), "Month");
        Assert.assertEquals(calendarPage.getCalendarHeader(), calendarUtility.currentMonthReference());
        Assert.assertTrue(calendarPage.isTodayHighlightedInCalendar());
        Assert.assertTrue(calendarPage.isEventPresentInCalendar(eventTitle));
        Assert.assertTrue(calendarPage.isEventPresentInCalendar(eventTitle2));
        Assert.assertTrue(calendarPage.isEventPresentInCalendar(eventTitle3));

        LOG.info("Step 2: Click on Week tab.");
        calendarPage.clickWeekButton();
        Assert.assertEquals(calendarPage.getSelectedViewName(), "Week");
        Assert.assertTrue(calendarPage.viewDisplayed().contains("view=week"));
        // TODO Still need to check the current day is highlighted on the calendar. Not implemented for now as it is not a sanity check

        Assert.assertTrue(calendarPage.isEventPresentInCalendar(eventTitle));
        Assert.assertTrue(calendarPage.isEventPresentInCalendar(eventTitle2));

        LOG.info("Step 3: Click on Day tab.");
        calendarPage.clickDayButton();
        Assert.assertTrue(calendarPage.isEventPresentInCalendar(eventTitle));
        System.out.print("View displayed is: " + calendarPage.viewDisplayed());
        Assert.assertTrue(calendarPage.viewDisplayed().contains("view=day"));

    }

    @TestRail(id = "C5806")
    @Test(groups = { TestGroup.SANITY, TestGroup.SITES })
    public void moveForwardThroughTheCalendar()
    {
        /**
         * Precondition: Add test events
         */
        String eventTitle1 = "testEvent1";
        String eventTitle2 = "testEvent2";
        String eventTitle3 = "testEvent3";
        String eventTitle4 = "testEvent4";
        String eventTitle5 = "testEvent5";
        String eventTitle6 = "testEvent6";

        sitePagesService.addCalendarEvent(user, password, siteName, eventTitle1, eventLocation, eventDescription, startDate, startDate, "", "", false, "tag1");
        sitePagesService.addCalendarEvent(user, password, siteName, eventTitle2, eventLocation, eventDescription, calendarUtility.tomorrow(),
                calendarUtility.tomorrow(), "", "", false, "tag2");
        sitePagesService.addCalendarEvent(user, password, siteName, eventTitle3, eventLocation, eventDescription, calendarUtility.dayAfterTomorrow(),
                calendarUtility.dayAfterTomorrow(), "", "", false, "tag3");
        sitePagesService.addCalendarEvent(user, password, siteName, eventTitle4, eventLocation, eventDescription, calendarUtility.dayOfNextWeek(),
                calendarUtility.dayOfNextWeek(), "", "", false, "tag4");
        sitePagesService.addCalendarEvent(user, password, siteName, eventTitle5, eventLocation, eventDescription, calendarUtility.firstDayOfCM(),
                calendarUtility.firstDayOfCM(), "", "", false, "tag5");
        sitePagesService.addCalendarEvent(user, password, siteName, eventTitle6, eventLocation, eventDescription, calendarUtility.firstDayOfNextMonth(),
                calendarUtility.firstDayOfNextMonth(), "", "", false, "tag6");

        /**
         * Test Steps
         */
        LOG.info("Step 1: Open Calendar page - Day view.");
        calendarPage.navigate(siteName);
        calendarPage.renderedPage();
        getBrowser().waitInSeconds(2);
        calendarPage.clickDayButton();
        Assert.assertTrue(calendarPage.isEventPresentInCalendar(eventTitle1));
        String dayRefferenceToday = new SimpleDateFormat("EE, dd, MMMM yyyy", Locale.ENGLISH).format(Calendar.getInstance().getTime());
        Assert.assertEquals(calendarPage.getCalendarHeader(), dayRefferenceToday);

        LOG.info("Step 2: Click on Next button (next to Agenda tab).");
        calendarPage.clickOnNextButton();
        Assert.assertTrue(calendarPage.isEventPresentInCalendar(eventTitle2));
        String dayRefferenceTomorrow = new SimpleDateFormat("EE, dd, MMMM yyyy", Locale.ENGLISH).format(calendarUtility.tomorrow());
        Assert.assertEquals(calendarPage.getCalendarHeader(), dayRefferenceTomorrow);

        LOG.info("Step 3: Switch to Week view.");
        calendarPage.clickWeekButton();
        if(tomorrow.dayOfWeek().getAsText(Locale.ENGLISH).equals("Sunday"))
        {
            Assert.assertTrue(calendarPage.isEventPresentInCalendar(eventTitle1));
            Assert.assertTrue(calendarPage.isEventPresentInCalendar(eventTitle2));
            Assert.assertFalse(calendarPage.isEventPresentInCalendar(eventTitle3));
        } else
        {
            Assert.assertTrue(calendarPage.isEventPresentInCalendar(eventTitle3));
        }
        Assert.assertEquals(calendarPage.getSelectedViewName(), "Week");
        Assert.assertTrue(calendarPage.viewDisplayed().contains("view=week"));
        String firstDayOfCurrentWeek = (today.dayOfWeek().getAsText(Locale.ENGLISH).equals("Sunday"))
                ? new SimpleDateFormat("d MMMM yyyy", Locale.ENGLISH).format(calendarUtility.firstDayOfNextWeek())
                : new SimpleDateFormat("d MMMM yyyy", Locale.ENGLISH).format(calendarUtility.firstDayOfCW());
        Assert.assertEquals(calendarPage.getCalendarHeader(), firstDayOfCurrentWeek);
        // TODO Still need to check the current day is highlighted on the calendar. Not implemented for now as it is not a sanity check

        LOG.info("Step 4: Click on Next button (next to Agenda tab).");
        calendarPage.clickTodayButton();
        calendarPage.clickOnNextButton();
        Assert.assertTrue(calendarPage.isEventPresentInCalendar(eventTitle4));
        String firstDayOfNextWeek = new SimpleDateFormat("d MMMM yyyy", Locale.ENGLISH).format(calendarUtility.firstDayOfNextWeek());
        Assert.assertEquals(calendarPage.getCalendarHeader(), firstDayOfNextWeek);

        LOG.info("Step 5: Switch to Month view.");
        calendarPage.clickMonthButton();
        calendarPage.isEventPresentInCalendar(eventTitle5);

        if (calendarUtility.currentMonth() != calendarUtility.monthOfNextWeek())
        {
            Assert.assertEquals(calendarPage.getCalendarHeader(), calendarUtility.refferenceNextMonth());
            calendarPage.isEventPresentInCalendar(eventTitle6);
        }
        else
        {
            Assert.assertEquals(calendarPage.getCalendarHeader(), calendarUtility.currentMonthReference());
            Assert.assertTrue(calendarPage.isEventPresentInCalendar(eventTitle5));
            Assert.assertTrue(calendarPage.isTodayHighlightedInCalendar());
        }

        LOG.info("Step 6: Click on Next button (next to Agenda tab).");
        calendarPage.clickOnNextButton();
        if (calendarUtility.currentMonth() != calendarUtility.monthOfNextWeek())
        {
            Assert.assertEquals(calendarPage.getCalendarHeader(), calendarUtility.monthAfterNextMonth());
        }
        else
        {
            Assert.assertEquals(calendarPage.getCalendarHeader(), calendarUtility.refferenceNextMonth());
            Assert.assertTrue(calendarPage.isEventPresentInCalendar(eventTitle6));
        }

        LOG.info("Step 7: Switch to Agenda view");
        calendarPage.clickAgendaButton();
        Assert.assertEquals(calendarPage.getNextButtonState(), "true");
    }

    @TestRail(id = "C5807")
    @Test(groups = { TestGroup.SANITY, TestGroup.SITES })
    public void moveBackwardsThroughTheCalendar()
    {
        /**
         * Precondition: Add test events
         */

        String eventTitle1 = "testEvent1";
        String eventTitle2 = "testEvent2";
        String eventTitle3 = "testEvent3";
        String eventTitle4 = "testEvent4";
        String eventTitle5 = "testEvent5";
        String eventTitle6 = "testEvent6";

        String firstDayOfLastWeekView = today.minusWeeks(1).dayOfWeek().withMinimumValue().toString("d MMMM yyyy");
        String previousMonth = today.minusMonths(1).toString("MMMM yyyy");

        sitePagesService.addCalendarEvent(user, password, siteName, eventTitle1, eventLocation, eventDescription, startDate, startDate, "", "", false, "tag1");
        sitePagesService.addCalendarEvent(user, password, siteName, eventTitle2, eventLocation, eventDescription, yesterday.toDate(), yesterday.toDate(), "",
                "", false, "tag2");
        sitePagesService.addCalendarEvent(user, password, siteName, eventTitle3, eventLocation, eventDescription, tomorrow.toDate(), tomorrow.toDate(), "", "",
                false, "tag3");
        sitePagesService.addCalendarEvent(user, password, siteName, eventTitle4, eventLocation, eventDescription, calendarUtility.randomDayOfLastWeek(),
                calendarUtility.randomDayOfLastWeek(), "", "", false, "tag4");
        sitePagesService.addCalendarEvent(user, password, siteName, eventTitle5, eventLocation, eventDescription, calendarUtility.firstDayOfCurrentMonth(),
                calendarUtility.firstDayOfCurrentMonth(), "", "", false, "tag5");
        sitePagesService.addCalendarEvent(user, password, siteName, eventTitle6, eventLocation, eventDescription, calendarUtility.dayFromPreviousMonth(),
                calendarUtility.dayFromPreviousMonth(), "", "", false, "tag6");

        /**
         * Test steps
         */

        LOG.info("Step 1: Open Calendar page - Day view.");
        calendarPage.navigate(siteName);
        calendarPage.renderedPage();
        getBrowser().waitInSeconds(2);
        calendarPage.clickDayButton();
        Assert.assertTrue(calendarPage.isEventPresentInCalendar(eventTitle1), "Only testEvent1 is displayed on the Calendar.");
        String dayRefferenceToday = new SimpleDateFormat("EE, dd, MMMM yyyy", Locale.ENGLISH).format(Calendar.getInstance().getTime());
        Assert.assertEquals(calendarPage.getCalendarHeader(), dayRefferenceToday, "Current day is displayed on the Calendar page.");

        LOG.info("Step 2: Click on Previous button (near Day tab).");
        calendarPage.clickOnPreviousButton();
        Assert.assertTrue(calendarPage.isEventPresentInCalendar(eventTitle2));
        Assert.assertEquals(calendarPage.getCalendarHeader(), yesterday.toString("EE, dd, MMMM yyyy"),
                "The day before current date is displayed on the Calendar page.");

        LOG.info("Step 3: Click on Today button. Switch to Week view.");
        calendarPage.clickTodayButton();
        calendarPage.clickWeekButton();
        Assert.assertEquals(calendarPage.getSelectedViewName(), "Week", "Current week is displayed on the Calendar page");
        Assert.assertTrue(calendarPage.viewDisplayed().contains("view=week"));
        Assert.assertTrue(calendarPage.isEventPresentInCalendar(eventTitle3), "testEvent3 is displayed on the Calendar.");
        Assert.assertEquals(calendarPage.getCalendarHeader(), calendarUtility.firstDayOfCurrentWeek());
        Assert.assertTrue(calendarPage.isTodayHighlightedInCalendar(), "Current date is highlighted.");

        LOG.info("Step 4: Click on Previous button (near Day tab).");
        calendarPage.clickOnPreviousButton();
        Assert.assertEquals(calendarPage.getCalendarHeader(), firstDayOfLastWeekView, "Previous week is displayed on the Calendar page.");
        Assert.assertTrue(calendarPage.isEventPresentInCalendar(eventTitle4), "testEvent4 is displayed on the Calendar .");

        LOG.info("Step 5: Click on Today button. Switch to Month view.");
        calendarPage.clickTodayButton();
        calendarPage.clickMonthButton();
        Assert.assertEquals(calendarPage.getCalendarHeader(), calendarUtility.currentMonthReference(), "Current month is displayed on the Calendar page");
        Assert.assertTrue(calendarPage.isTodayHighlightedInCalendar(), "Current date is highlighted.");
        Assert.assertTrue(calendarPage.isEventPresentInCalendar(eventTitle5), "testEvent5 is displayed on the Calendar.");

        LOG.info("Step 6: Click on Previous button (near Day tab).");
        calendarPage.clickOnPreviousButton();
        Assert.assertEquals(calendarPage.getCalendarHeader(), previousMonth, "Previous month is displayed on the Calendar page.");
        Assert.assertTrue(calendarPage.isEventPresentInCalendar(eventTitle6), "testEvent6 is displayed on the Calendar.");

        LOG.info("Step 7: Switch to Agenda view");
        calendarPage.clickAgendaButton();
        Assert.assertEquals(calendarPage.getNextButtonState(), "true", "Previous button is disabled.");
    }

    @TestRail(id = "C5809")
    @Test(groups = { TestGroup.SANITY, TestGroup.SITES })
    public void displayUpcomingEvents()
    {
        /**
         * Preconditions
         */
        String eventTitle1 = "testEvent1";
        String eventTitle2 = "testEvent2";
        String eventTitle3 = "testEvent3";

        sitePagesService.addCalendarEvent(user, password, siteName, eventTitle1, eventLocation, eventDescription, calendarUtility.tomorrow(),
                calendarUtility.tomorrow(), "", "", false, "tag1");
        sitePagesService.addCalendarEvent(user, password, siteName, eventTitle2, eventLocation, eventDescription, calendarUtility.dayAfterTomorrow(),
                calendarUtility.dayAfterTomorrow(), "", "", false, "tag1");
        sitePagesService.addCalendarEvent(user, password, siteName, eventTitle3, eventLocation, eventDescription, calendarUtility.dayOfNextWeek(),
                calendarUtility.dayOfNextWeek(), "", "", false, "tag1");

        /**
         * Test Steps
         */
        LOG.info("Navigate to Calendar page for Site - Agenda view.");
        calendarPage.navigate(siteName);
        calendarPage.renderedPage();
        getBrowser().waitInSeconds(2);
        calendarPage.clickAgendaButton();
        Assert.assertTrue(calendarPage.isEventPresentInAgenda(eventTitle1));
        Assert.assertTrue(calendarPage.isEventPresentInAgenda(eventTitle2));
        Assert.assertTrue(calendarPage.isEventPresentInAgenda(eventTitle3));
    }

    @TestRail(id = "C5905")
    @Test(groups = { TestGroup.SANITY, TestGroup.SITES })
    public void browserEventsByTags()
    {
        /**
         * Preconditions
         */
        String eventTitle1 = "testEvent1";
        String eventTitle2 = "testEvent2";
        String eventTitle3 = "testEvent3";
        WebDriverWait wait = new WebDriverWait(getBrowser(), 1);

        sitePagesService.addCalendarEvent(user, password, siteName, eventTitle1, eventLocation, eventDescription, calendarUtility.tomorrow(),
                calendarUtility.tomorrow(), "", "", false, "tag1");
        sitePagesService.addCalendarEvent(user, password, siteName, eventTitle2, eventLocation, eventDescription, calendarUtility.dayAfterTomorrow(),
                calendarUtility.dayAfterTomorrow(), "", "", false, "tag2");
        sitePagesService.addCalendarEvent(user, password, siteName, eventTitle3, eventLocation, eventDescription, calendarUtility.dayOfNextWeek(),
                calendarUtility.dayOfNextWeek(), "", "", false, "tag3");

        calendarPage.navigate(siteName);
        calendarPage.renderedPage();
        getBrowser().waitInSeconds(2);

        /**
         * Test Steps
         */
        LOG.info("Step 1: Click on tag1 on the Tags section.");
        calendarPage.clickTagLink("tag1");
        Assert.assertTrue(calendarPage.isEventPresentInCalendar(eventTitle1));
        Assert.assertFalse(calendarPage.isEventPresentInCalendar(eventTitle2));
        Assert.assertFalse(calendarPage.isEventPresentInCalendar(eventTitle3));

        LOG.info("Step 2: Click on tag2 on the Tags section.");
        calendarPage.clickTagLink("tag2");
        Assert.assertTrue(calendarPage.isEventPresentInCalendar(eventTitle2));
        Assert.assertFalse(calendarPage.isEventPresentInCalendar(eventTitle1));
        Assert.assertFalse(calendarPage.isEventPresentInCalendar(eventTitle3));

        LOG.info("Step 3: Click on tag3 on the Tags section.");
        calendarPage.clickTagLink("tag3");
        Assert.assertTrue(calendarPage.isEventPresentInCalendar(eventTitle3));
        Assert.assertFalse(calendarPage.isEventPresentInCalendar(eventTitle2));
        Assert.assertFalse(calendarPage.isEventPresentInCalendar(eventTitle1));

        LOG.info("Step 4: Click on Show All Items link");
        Assert.assertTrue(calendarPage.isShowAllItemsLinkDisplayed());
        calendarPage.clickShowAllItems();
        getBrowser().waitInSeconds(2);
        wait.until(ExpectedConditions.presenceOfElementLocated(By
                .xpath("//div[contains(@class, 'fc-view') and not(contains(@style,'display: none'))]//a[contains(@class , 'fc-event')]//*[@class='fc-event-title']")));
        Assert.assertTrue(calendarPage.isEventPresentInCalendar(eventTitle1));
        Assert.assertTrue(calendarPage.isEventPresentInCalendar(eventTitle2));
        Assert.assertTrue(calendarPage.isEventPresentInCalendar(eventTitle3));
    }

    @TestRail(id = "C5808")
    @Test(groups = { TestGroup.SANITY, TestGroup.SITES })
    public void displayTodayEvents()
    {
        /**
         * Preconditions
         */
        String eventTitle1 = "testEvent1";

        sitePagesService.addCalendarEvent(user, password, siteName, eventTitle1, eventLocation, eventDescription, startDate, startDate, "", "", false, "tag1");
        calendarPage.navigate(siteName);
        calendarPage.renderedPage();
        getBrowser().waitInSeconds(2);
        calendarPage.clickMonthButton();

        /**
         * Test Steps
         */
        LOG.info("Step 1: Click on Previous button (before the Day tab).");
        calendarPage.clickOnPreviousButton();
        Assert.assertEquals(calendarPage.getCalendarHeader(), calendarUtility.refferencePreviousMonth());

        LOG.info("Step 2: Click on Today button.");
        calendarPage.clickTodayButton();
        calendarPage.renderedPage();
        Assert.assertEquals(calendarPage.getCalendarHeader(), calendarUtility.currentMonthReference());
        Assert.assertTrue(calendarPage.isEventPresentInCalendar(eventTitle1));

        LOG.info("Step 3: Click on Next button (next to Agenda tab).");
        calendarPage.clickOnNextButton();
        calendarPage.renderedPage();
        Assert.assertEquals(calendarPage.getCalendarHeader(), calendarUtility.refferenceNextMonth());

        LOG.info("Step 4: Click on Today button");
        calendarPage.clickTodayButton();
        calendarPage.renderedPage();
        Assert.assertEquals(calendarPage.getCalendarHeader(), calendarUtility.currentMonthReference());
        Assert.assertTrue(calendarPage.isEventPresentInCalendar(eventTitle1));

        LOG.info("Step 5: Switch the calendar to Week view.");
        calendarPage.clickWeekButton();
        calendarPage.renderedPage();
        Assert.assertEquals(calendarPage.getCalendarHeader(), calendarUtility.firstDayOfCurrentWeek());
        Assert.assertTrue(calendarPage.isEventPresentInCalendar(eventTitle1));

        LOG.info("Step 6: Click on Previous button (before the Day tab)");
        calendarPage.clickOnPreviousButton();
        calendarPage.renderedPage();
        Assert.assertEquals(calendarPage.getCalendarHeader(), calendarUtility.firstDayOfLastWeek());

        LOG.info("Step 7: Click on Today button");
        calendarPage.clickTodayButton();
        calendarPage.renderedPage();
        Assert.assertEquals(calendarPage.getCalendarHeader(), calendarUtility.firstDayOfCurrentWeek());
        Assert.assertTrue(calendarPage.isEventPresentInCalendar(eventTitle1));

        LOG.info("Step 8: Click on Next button (next to Agenda tab).");
        calendarPage.clickOnNextButton();
        calendarPage.renderedPage();
        String firstDayOfNextWeek = new SimpleDateFormat("d MMMM yyyy", Locale.ENGLISH).format(calendarUtility.firstDayOfNextWeek());
        Assert.assertEquals(calendarPage.getCalendarHeader(), firstDayOfNextWeek);

        LOG.info("Step 9: Click on Today button");
        calendarPage.clickTodayButton();
        calendarPage.renderedPage();
        Assert.assertEquals(calendarPage.getCalendarHeader(), calendarUtility.firstDayOfCurrentWeek());
        Assert.assertTrue(calendarPage.isEventPresentInCalendar(eventTitle1));

        LOG.info("Step 10: Switch to Day view.");
        calendarPage.clickDayButton();
        calendarPage.renderedPage();
        Assert.assertEquals(calendarPage.getCalendarHeader(), calendarUtility.currentDay());
        Assert.assertTrue(calendarPage.isEventPresentInCalendar(eventTitle1));

        LOG.info("Step 11: Click on Previous button (before the Day tab).");
        calendarPage.clickOnPreviousButton();
        calendarPage.renderedPage();
        Assert.assertEquals(calendarPage.getCalendarHeader(), yesterday.toString("E, dd, MMMM yyyy"));

        LOG.info("Step 12: Click on Today button");
        calendarPage.clickTodayButton();
        calendarPage.renderedPage();
        Assert.assertEquals(calendarPage.getCalendarHeader(), calendarUtility.currentDay());
        Assert.assertTrue(calendarPage.isEventPresentInCalendar(eventTitle1));

        LOG.info("Step 13: Click on Next button (next to Agenda tab).");
        calendarPage.clickOnNextButton();
        calendarPage.renderedPage();
        String tomorrow = new SimpleDateFormat("E, dd, MMMM yyyy", Locale.ENGLISH).format(calendarUtility.tomorrow());
        Assert.assertEquals(calendarPage.getCalendarHeader(), tomorrow);

        LOG.info("Step 14: Click on Today button");
        calendarPage.clickTodayButton();
        calendarPage.renderedPage();
        Assert.assertEquals(calendarPage.getCalendarHeader(), calendarUtility.currentDay());
        Assert.assertTrue(calendarPage.isEventPresentInCalendar(eventTitle1));

        LOG.info("Step 15: Switch to Agenda view.");
        calendarPage.clickAgendaButton();
        calendarPage.renderedPage();
        Assert.assertEquals(calendarPage.getTodayButtonState(), "true");
    }

    @TestRail(id = "C3155")
    @Test(groups = { TestGroup.SANITY, TestGroup.SITES })
    public void verifyTheMiniCalendar()
    {
        LOG.info("Step 1: Verify the presence of the mini-calendar.");

        calendarPage.navigate(siteName);
        calendarPage.renderedPage();
        getBrowser().waitInSeconds(2);
        Assert.assertTrue(calendarPage.isMiniCalendarPresent(), "Mini Calendar is present.");
        Integer dayFromCalendar = Integer.valueOf(miniCalendar.getCurrentDayMiniCalendar().trim());
        Assert.assertEquals(dayFromCalendar, dayOfMonth, "Current date is highlighted.");
        Assert.assertEquals(miniCalendar.getCurrentMonthMiniCalendar(), calendarUtility.currentMonthReference(), "Current month is displayed");

        LOG.info("Step 2: Press '<' button on the calendar.");

        miniCalendar.clickOnPreviousMonthButtonMiniCalendar();
        Assert.assertEquals(miniCalendar.getCurrentMonthMiniCalendar(), calendarUtility.refferencePreviousMonth(),
                "Previous month is displayed on the mini-calendar.");

        LOG.info("Step 3: Click on any day from the mini-calendar.");
        miniCalendar.clickOnRandomDate();
        Assert.assertEquals(calendarPage.getCalendarHeader(), miniCalendar.getCurrentMonthMiniCalendar());
        // TODO Still need to check The selected day is highlighted on the mini-calendar. Not implemented for now as it is not a sanity check

        LOG.info("Step 4: Click 'This Month' button.");
        miniCalendar.clickOnThisMonthButton();
        Assert.assertEquals(dayFromCalendar, dayOfMonth);
        Assert.assertEquals(miniCalendar.getCurrentMonthMiniCalendar(), calendarUtility.currentMonthReference());

        LOG.info("Step 5: Press '>' button in the calendar.");
        miniCalendar.clickOnNextMonthButtonInMiniCalendar();
        Assert.assertEquals(miniCalendar.getCurrentMonthMiniCalendar(), calendarUtility.refferenceNextMonth());

        LOG.info("Step 6: Click on any date from the calendar.");
        miniCalendar.clickOnRandomDate();
        Assert.assertEquals(calendarPage.getCalendarHeader(), miniCalendar.getCurrentMonthMiniCalendar());
        // TODO Still need to check The selected day is highlighted on the mini-calendar. Not implemented for now as it is not a sanity check
    }

    @TestRail(id = "C5833")
    @Test(groups = { TestGroup.SANITY, TestGroup.SITES })
    public void showAllHours()
    {
        /**
         * Preconditions
         */
        String eventTitle1 = "testEvent1";
        String eventTitle2 = "testEvent2";
        String eventTitle3 = "testEvent3";
        String eventTitle4 = "testEvent4";

        sitePagesService.addCalendarEvent(user, password, siteName, eventTitle1, eventLocation, eventDescription, startDate, startDate, "20:00", "23:00",
                false, "tag1");
        sitePagesService.addCalendarEvent(user, password, siteName, eventTitle2, eventLocation, eventDescription, startDate, startDate, "10:00", "12:00",
                false, "tag2");
        sitePagesService.addCalendarEvent(user, password, siteName, eventTitle3, eventLocation, eventDescription, calendarUtility.firstDayOfCW(),
                calendarUtility.firstDayOfCW(), "20:00", "23:00", false, "tag3");
        sitePagesService.addCalendarEvent(user, password, siteName, eventTitle4, eventLocation, eventDescription, calendarUtility.firstDayOfCW(),
                calendarUtility.firstDayOfCW(), "10:00", "12:00", false, "tag4");

        calendarPage.navigate(siteName);
        calendarPage.renderedPage();
        getBrowser().waitInSeconds(2);
        calendarPage.clickDayButton();
        Assert.assertEquals(calendarPage.getCalendarHeader(), calendarUtility.currentDay());
    }
}
