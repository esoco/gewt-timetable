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

		if (rDate != null)
		{
			aComponent.setDate(rDate);
		}

		if (nScrollToHour >= 0)
		{
			aComponent.scrollToHour(nScrollToHour);
		}

		rBuilder.addComponent(aComponent, rStyle, null, null);

		return aComponent;
	}
}
