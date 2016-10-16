//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
// gewt-timetable source file
// Copyright (c) 2016 Elmar Sonnenschein / esoco GmbH
// Last Change: 16.10.2016 by eso
//
// gewt-timetable is free software; you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published
// by the Free Software Foundation; either version 3 of the License,
// or (at your option) any later version.
//
// gewt-timetable is distributed in the hope that it will be useful, but
// WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License along
// with gewt-timetable; if not, write to the Free Software Foundation, Inc.,
// 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA or use the
// contact information on the FSF website http://www.fsf.org
//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
package de.esoco.ewt.build;

import de.esoco.ewt.component.Timetable;
import de.esoco.ewt.style.StyleData;

import java.util.Date;


/********************************************************************
 * A simple builder that creates {@link Timetable} instances in a container
 * builder.
 *
 * @author eso
 */
public class TimetableBuilder
{
	//~ Constructors -----------------------------------------------------------

	/***************************************
	 * Private, only static use.
	 */
	private TimetableBuilder()
	{
	}

	//~ Static methods ---------------------------------------------------------

	/***************************************
	 * Creates a new {@link Timetable} component.
	 *
	 * @param  rBuilder      The builder to create the component in
	 * @param  rStyle        The style data
	 * @param  rDate         The initial date of the component
	 * @param  nScrollToHour The hour to be displayed first or -1 for the
	 *                       default
	 *
	 * @return The new component
	 */
	public static Timetable addTimetable(ContainerBuilder<?> rBuilder,
										 StyleData			 rStyle,
										 Date				 rDate,
										 int				 nScrollToHour)
	{
		Timetable aComponent = new Timetable();

		rBuilder.addComponent(aComponent, rStyle, null, null);

		if (rDate != null)
		{
			aComponent.setDate(rDate);
		}

		if (nScrollToHour >= 0)
		{
			aComponent.scrollToHour(nScrollToHour);
		}

		return aComponent;
	}
}
