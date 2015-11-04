//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
// This file is a part of the 'gewt-timetable' project.
// Copyright 2015 Elmar Sonnenschein, esoco GmbH, Flensburg, Germany
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//	  http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
package de.esoco.ewt.component;

import de.esoco.ewt.EWT;
import de.esoco.ewt.event.EventType;

import de.esoco.lib.property.DateAttribute;
import de.esoco.lib.property.HasProperties;
import de.esoco.lib.property.StandardProperties;
import de.esoco.lib.property.StringProperties;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import com.bradrydzewski.gwt.calendar.client.Appointment;
import com.bradrydzewski.gwt.calendar.client.Calendar;
import com.bradrydzewski.gwt.calendar.client.CalendarFormat;
import com.bradrydzewski.gwt.calendar.client.CalendarSettings;
import com.bradrydzewski.gwt.calendar.client.CalendarSettings.Click;
import com.bradrydzewski.gwt.calendar.client.CalendarViews;
import com.bradrydzewski.gwt.calendar.client.agenda.AgendaView;
import com.bradrydzewski.gwt.calendar.client.event.CreateEvent;
import com.bradrydzewski.gwt.calendar.client.event.CreateHandler;
import com.bradrydzewski.gwt.calendar.client.event.DateRequestEvent;
import com.bradrydzewski.gwt.calendar.client.event.DateRequestHandler;
import com.bradrydzewski.gwt.calendar.client.event.DeleteEvent;
import com.bradrydzewski.gwt.calendar.client.event.DeleteHandler;
import com.bradrydzewski.gwt.calendar.client.event.MouseOverEvent;
import com.bradrydzewski.gwt.calendar.client.event.MouseOverHandler;
import com.bradrydzewski.gwt.calendar.client.event.TimeBlockClickEvent;
import com.bradrydzewski.gwt.calendar.client.event.TimeBlockClickHandler;
import com.bradrydzewski.gwt.calendar.client.event.UpdateEvent;
import com.bradrydzewski.gwt.calendar.client.event.UpdateHandler;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.logical.shared.OpenEvent;
import com.google.gwt.event.logical.shared.OpenHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Widget;

import static de.esoco.lib.property.StandardProperties.ALL_DAY;
import static de.esoco.lib.property.StandardProperties.DESCRIPTION;
import static de.esoco.lib.property.StandardProperties.END_DATE;
import static de.esoco.lib.property.StandardProperties.ID;
import static de.esoco.lib.property.StandardProperties.LOCATION;
import static de.esoco.lib.property.StandardProperties.READONLY;
import static de.esoco.lib.property.StandardProperties.START_DATE;
import static de.esoco.lib.property.StandardProperties.TITLE;


/********************************************************************
 * A component that can display calendar events with different styles.
 *
 * @author eso
 */
public class Timetable extends Component implements DateAttribute
{
	//~ Enums ------------------------------------------------------------------

	/********************************************************************
	 * Enumeration of the possible display styles.
	 */
	public enum TimetableStyle { DAY, MONTH, AGENDA }

	//~ Constructors -----------------------------------------------------------

	/***************************************
	 * Creates a new instance.
	 */
	public Timetable()
	{
		super(new TimetableWidget());

		final TimetableWidget rWidget   = getTimetableWidget();
		CalendarSettings	  rSettings = rWidget.getSettings();

		CalendarFormat.INSTANCE.setFirstDayOfWeek(1);
		CalendarFormat.INSTANCE.setAm("");
		CalendarFormat.INSTANCE.setPm("");
		rSettings.setShowWeekNumbers(true);
		rSettings.setTimeBlockClickNumber(Click.Double);
		rWidget.setSettings(rSettings);
		rWidget.addDateRequestHandler(new DateRequestHandler<Date>()
			{
				@Override
				public void onDateRequested(DateRequestEvent<Date> rEvent)
				{
					rWidget.setView(CalendarViews.DAY, 1);
					rWidget.setDate(rEvent.getTarget());
				}
			});
	}

	//~ Methods ----------------------------------------------------------------

	/***************************************
	 * Adds a calendar event to this instance. The event must be an instance of
	 * an implementation of {@link HasProperties} that has several properties
	 * from {@link StandardProperties} set. The supported properties are:
	 *
	 * <ul>
	 *   <li>{@link StandardProperties#ID}: a unique identifier of the event
	 *     (required)</li>
	 *   <li>{@link StandardProperties#TITLE}: the event title (required)</li>
	 *   <li>{@link StandardProperties#START_DATE}: the start date of the event
	 *     (required)</li>
	 *   <li>{@link StandardProperties#END_DATE}: the end date of the event
	 *     (required if not an all-day event)</li>
	 *   <li>{@link StandardProperties#DESCRIPTION}: an additional event
	 *     description</li>
	 *   <li>{@link StandardProperties#LOCATION}: the event location</li>
	 *   <li>{@link StandardProperties#ALL_DAY}: TRUE for an all-day event on
	 *     the start date</li>
	 * </ul>
	 *
	 * @param rEvent A properties object containing the event data
	 */
	public void addEvent(HasProperties rEvent)
	{
		getTimetableWidget().addAppointment(new TimetableEvent(rEvent));
	}

	/***************************************
	 * Adds multiple events to this instance.
	 *
	 * @param rEvents The events to add
	 *
	 * @see   #addEvent(HasProperties)
	 */
	public void addEvents(Collection<? extends HasProperties> rEvents)
	{
		List<Appointment> aAppointments = new ArrayList<>(rEvents.size());

		for (HasProperties rEvent : rEvents)
		{
			aAppointments.add(new TimetableEvent(rEvent));
		}

		getTimetableWidget().addAppointments(aAppointments);
	}

	/***************************************
	 * Removes all calendar events from this instance.
	 */
	public void clear()
	{
		getTimetableWidget().clearAppointments();
	}

	/***************************************
	 * Enables or disables the editing of events with mouse interactions. Will
	 * only be effective after a UI refresh through {@link #repaint()}.
	 *
	 * @param bEnable TRUE to enable interactive editing
	 */
	public void enableEditing(boolean bEnable)
	{
		getTimetableWidget().getSettings().setEnableDragDrop(bEnable);
	}

	/***************************************
	 * {@inheritDoc}
	 */
	@Override
	public Date getDate()
	{
		return getTimetableWidget().getDate();
	}

	/***************************************
	 * Returns the currently selected calendar event.
	 *
	 * @return The selected calendar event or NULL for none
	 */
	public HasProperties getSelectedEvent()
	{
		Appointment rAppointment =
			getTimetableWidget().getSelectedAppointment();

		return rAppointment != null ? createEvent(rAppointment) : null;
	}

	/***************************************
	 * Returns the number of days that are displayed if the display type is
	 * {@link TimetableStyle#DAYS}.
	 *
	 * @return The number of visible days
	 */
	public int getVisibleDays()
	{
		return getTimetableWidget().getDays();
	}

	/***************************************
	 * Removes an event from this instance.
	 *
	 * @param sId The unique ID of the event to remove
	 */
	public void removeEvent(String sId)
	{
		List<Appointment> rAppointments =
			getTimetableWidget().getAppointments();

		for (Appointment rAppointment : rAppointments)
		{
			if (rAppointment.getId().equals(sId))
			{
				getTimetableWidget().removeAppointment(rAppointment);

				break;
			}
		}
	}

	/***************************************
	 * {@inheritDoc}
	 */
	@Override
	public void repaint()
	{
		getTimetableWidget().doLayout();
		getTimetableWidget().doSizing();
	}

	/***************************************
	 * Sets the first hour to be visible and scrolls the UI accordingly if
	 * necessary. Will only be effective after a UI refresh through {@link
	 * #repaint()}.
	 *
	 * @param nHour The hour to scroll to
	 */
	public void scrollToHour(final int nHour)
	{
		Scheduler.get()
				 .scheduleDeferred(new ScheduledCommand()
			{
				@Override
				public void execute()
				{
					getTimetableWidget().getSettings().setScrollToHour(nHour);
				}
			});
	}

	/***************************************
	 * {@inheritDoc}
	 */
	@Override
	public void setDate(Date rDate)
	{
		getTimetableWidget().setDate(rDate);
	}

	/***************************************
	 * Sets the first hour to be displayed for a day. Will only be effective
	 * after a UI refresh through {@link #repaint()}.
	 *
	 * @param nHour The starting hour of a day
	 */
	public void setDayStart(int nHour)
	{
		getTimetableWidget().getSettings().setDayStartsAt(nHour);
	}

	/***************************************
	 * Sets the parameters for the display of hour first intervals. Will only be
	 * effective after a UI refresh through {@link #repaint()}.
	 *
	 * @param nSubdivisions      The number of subdivisions of an hour or -1 to
	 *                           ignore
	 * @param nSubdivisionHeight The height of a subdivision in pixels or -1 to
	 *                           ignore
	 */
	public void setHourIntervals(int nSubdivisions, int nSubdivisionHeight)
	{
		CalendarSettings rSettings = getTimetableWidget().getSettings();

		if (nSubdivisions > 0)
		{
			rSettings.setIntervalsPerHour(nSubdivisions);
		}

		if (nSubdivisionHeight > 0)
		{
			rSettings.setPixelsPerInterval(nSubdivisionHeight);
		}
	}

	/***************************************
	 * Sets the display type.
	 *
	 * @param eType The new display type
	 */
	public void setTimetableStyle(TimetableStyle eType)
	{
		getTimetableWidget().setView(CalendarViews.valueOf(eType.name()));
	}

	/***************************************
	 * Sets the number of days that are displayed if the display type is {@link
	 * TimetableStyle#DAYS}.
	 *
	 * @param nDays The number of visible days
	 */
	public void setVisibleDays(int nDays)
	{
		getTimetableWidget().setDays(nDays);
	}

	/***************************************
	 * Sets the working hours of a day. Will only be effective after a UI
	 * refresh through {@link #repaint()}.
	 *
	 * @param nFirstWorkHour The first work hour of a day or -1 to ignore
	 * @param nLastWorkHour  The last work hour of a day or -1 to ignore
	 */
	public void setWorkingHours(int nFirstWorkHour, int nLastWorkHour)
	{
		CalendarSettings rSettings = getTimetableWidget().getSettings();

		if (nFirstWorkHour >= 0)
		{
			rSettings.setWorkingHourStart(nFirstWorkHour);
		}

		if (nLastWorkHour >= 0)
		{
			rSettings.setWorkingHourEnd(nLastWorkHour);
		}
	}

	/***************************************
	 * Enables or disables the display of week numbers. Will only be effective
	 * after a UI refresh through {@link #repaint()}.
	 *
	 * @param bShow TRUE to show week numbers
	 */
	public void showWeekNumbers(boolean bShow)
	{
		getTimetableWidget().getSettings().setShowWeekNumbers(bShow);
	}

	/***************************************
	 * Creates an event properties object from an {@link Appointment}.
	 *
	 * @param  rAppointment The source object
	 *
	 * @return The new event properties
	 */
	HasProperties createEvent(Appointment rAppointment)
	{
		StringProperties aEvent = new StringProperties();

		aEvent.setProperty(ID, rAppointment.getId());
		aEvent.setProperty(TITLE, rAppointment.getTitle());
		aEvent.setProperty(DESCRIPTION, rAppointment.getDescription());
		aEvent.setProperty(START_DATE, rAppointment.getStart());
		aEvent.setProperty(END_DATE, rAppointment.getEnd());
		aEvent.setProperty(LOCATION, rAppointment.getLocation());
		aEvent.setProperty(ALL_DAY, rAppointment.isAllDay());

		return aEvent;
	}

	/***************************************
	 * {@inheritDoc}
	 */
	@Override
	ComponentEventDispatcher createEventDispatcher()
	{
		return new TimetableEventDispatcher();
	}

	/***************************************
	 * Returns the timetable widget wrapped by this instance.
	 *
	 * @return The timetable widget
	 */
	private TimetableWidget getTimetableWidget()
	{
		return (TimetableWidget) getWidget();
	}

	//~ Inner Classes ----------------------------------------------------------

	/********************************************************************
	 * An appointment subclass that also contains the original event properties.
	 *
	 * @author eso
	 */
	static class TimetableEvent extends Appointment
	{
		//~ Static fields/initializers -----------------------------------------

		private static final long serialVersionUID = 1L;

		//~ Instance fields ----------------------------------------------------

		private final HasProperties rEventProperties;

		//~ Constructors -------------------------------------------------------

		/***************************************
		 * Creates a new instance from an event properties object.
		 *
		 * @param rEventProperties The event to initialize this instance from
		 */
		public TimetableEvent(HasProperties rEventProperties)
		{
			this.rEventProperties = rEventProperties;

			setId(rEventProperties.getProperty(ID, null));
			setTitle(rEventProperties.getProperty(TITLE, ""));
			setDescription(rEventProperties.getProperty(DESCRIPTION, ""));
			setStart(rEventProperties.getProperty(START_DATE, null));
			setEnd(rEventProperties.getProperty(END_DATE, null));
			setLocation(rEventProperties.getProperty(LOCATION, ""));
			setAllDay(rEventProperties.hasFlag(ALL_DAY));
			setReadOnly(rEventProperties.hasFlag(READONLY));
		}

		//~ Methods ------------------------------------------------------------

		/***************************************
		 * Returns the original event properties.
		 *
		 * @return The event properties
		 */
		public final HasProperties getEventProperties()
		{
			return rEventProperties;
		}
	}

	/********************************************************************
	 * A timetable calendar subclass that fixes some issues with the original
	 * implementation.
	 *
	 * @author eso
	 */
	static class TimetableWidget extends Calendar
	{
		//~ Methods ------------------------------------------------------------

		/***************************************
		 * Overridden to forward to {@link #setStylePrimaryName(String)} because
		 * otherwise original code overwrites secondary styles.
		 *
		 * @see Calendar#setStyleName(String)
		 */
		@Override
		public void setStyleName(String sStyle)
		{
			setStylePrimaryName(sStyle);
		}

		/***************************************
		 * @see Calendar#setView(CalendarViews, int)
		 */
		@Override
		public void setView(CalendarViews eViewStyle, int nDays)
		{
			if (eViewStyle == CalendarViews.AGENDA)
			{
				// enabled although disabled in original code
				// TODO: check how the bug mentioned there affects functionality
				setView(new AgendaView());
			}
			else
			{
				super.setView(eViewStyle, nDays);
			}
		}
	}

	/********************************************************************
	 * Dispatcher for calendar-specific events.
	 *
	 * @author eso
	 */
	class TimetableEventDispatcher extends ComponentEventDispatcher
		implements SelectionHandler<Appointment>, DeleteHandler<Appointment>,
				   MouseOverHandler<Appointment>, TimeBlockClickHandler<Date>,
				   OpenHandler<Appointment>, UpdateHandler<Appointment>,
				   CreateHandler<Appointment>
	{
		//~ Instance fields ----------------------------------------------------

		private TimetableEvent rPreviousSelection = null;
		private Timer		   aDoubleClickTimer  = null;

		//~ Methods ------------------------------------------------------------

		/***************************************
		 * {@inheritDoc}
		 */
		@Override
		public void onCreate(CreateEvent<Appointment> rEvent)
		{
			notifyEventHandler(EventType.ELEMENT_CREATED,
							   createEvent(rEvent.getTarget()));
		}

		/***************************************
		 * {@inheritDoc}
		 */
		@Override
		public void onDelete(DeleteEvent<Appointment> rEvent)
		{
			TimetableEvent rTimetableEvent =
				(TimetableEvent) rEvent.getTarget();

			notifyEventHandler(EventType.ELEMENT_DELETED,
							   rTimetableEvent.getEventProperties());

			// cancel the original event to prevent deletion before the
			// application could query the user for a confirmation
			rEvent.setCancelled(true);
		}

		/***************************************
		 * {@inheritDoc}
		 */
		@Override
		public void onMouseOver(MouseOverEvent<Appointment> rEvent)
		{
			notifyEventHandler(EventType.POINTER_HOVER,
							   ((TimetableEvent) rEvent.getTarget())
							   .getEventProperties());
		}

		/***************************************
		 * {@inheritDoc}
		 */
		@Override
		public void onOpen(OpenEvent<Appointment> rEvent)
		{
			if (aDoubleClickTimer != null)
			{
				aDoubleClickTimer.cancel();
				aDoubleClickTimer = null;
			}

			notifyEventHandler(EventType.ACTION,
							   ((TimetableEvent) rEvent.getTarget())
							   .getEventProperties());
		}

		/***************************************
		 * {@inheritDoc}
		 */
		@Override
		public void onSelection(SelectionEvent<Appointment> rEvent)
		{
			final TimetableEvent rNewSelection =
				(TimetableEvent) rEvent.getSelectedItem();

			if (rPreviousSelection == null ||
				!rPreviousSelection.getId().equals(rNewSelection.getId()))
			{
				if (aDoubleClickTimer == null)
				{
					aDoubleClickTimer =
						new Timer()
						{
							@Override
							public void run()
							{
								aDoubleClickTimer = null;
								notifyEventHandler(EventType.SELECTION,
												   rNewSelection
												   .getEventProperties());
							}
						};
					aDoubleClickTimer.schedule(EWT.getDoubleClickInterval());
				}
			}

			rPreviousSelection = rNewSelection;
		}

		/***************************************
		 * {@inheritDoc}
		 */
		@Override
		public void onTimeBlockClick(TimeBlockClickEvent<Date> rEvent)
		{
			notifyEventHandler(EventType.ACTION, rEvent.getTarget());
		}

		/***************************************
		 * {@inheritDoc}
		 */
		@Override
		public void onUpdate(UpdateEvent<Appointment> rEvent)
		{
			notifyEventHandler(EventType.ELEMENT_UPDATED,
							   createEvent(rEvent.getTarget()));
		}

		/***************************************
		 * {@inheritDoc}
		 */
		@Override
		void initEventDispatching(Widget rWidget)
		{
			super.initEventDispatching(rWidget);

			TimetableWidget rTimetableWidget = getTimetableWidget();

			rTimetableWidget.addCreateHandler(this);
			rTimetableWidget.addDeleteHandler(this);
			rTimetableWidget.addMouseOverHandler(this);
			rTimetableWidget.addOpenHandler(this);
			rTimetableWidget.addSelectionHandler(this);
			rTimetableWidget.addTimeBlockClickHandler(this);
			rTimetableWidget.addUpdateHandler(this);
		}
	}
}
