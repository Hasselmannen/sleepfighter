/*
 * Copyright 2014 toxbee.se
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package se.toxbee.sleepfighter.preference;

import se.toxbee.sleepfighter.utils.prefs.PreferenceNode;

/**
 * {@link WeatherPreferences} contains info about weather.
 *
 * @author Centril<twingoow@gmail.com> / Mazdak Farrokhzad.
 * @version 1.0
 * @since Dec 15, 2013
 */
public class WeatherPreferences extends AppPreferenceNode {
	protected WeatherPreferences( PreferenceNode b ) {
		super( b, "weather" );
	}

	/**
	 * Used to temporarily store the weather. Some seconds before the app starts, the weather is fetched.
	 * This preference is used to temporarily store the weather info. 
	 *
	 * @param weather
	 */
	public void setTemp( String weather ) {
		p.setString( "temp", weather );
	}

	/**
	 * Returns the weather.
	 *
	 * @see #setTemp(String)
	 * @return the weather.
	 */
	public String getWeather() {
		return p.getString( "temp", null );
	}
}
