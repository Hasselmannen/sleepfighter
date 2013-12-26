/*******************************************************************************
 * Copyright (c) 2013 See AUTHORS file.
 * 
 * This file is part of SleepFighter.
 * 
 * SleepFighter is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * SleepFighter is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with SleepFighter. If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
package se.toxbee.sleepfighter.preference.migration;

import se.toxbee.sleepfighter.utils.migration.IMigrationException;
import se.toxbee.sleepfighter.utils.prefs.PreferenceNode;

public class Version8 extends Migrater.Adapter {
	@Override
	public void applyMigration( PreferenceNode p ) throws IMigrationException {
		p.remove( "pref_temp_weather" );

		// control
		b( p, "pref_alarm_turn_screen_on", "alarmControl.turnScreenOn", true );
		b( p, "pref_alarm_bypass_lock_screen", "alarmControl.bypassLockScreen", true );

		// location filter
		b( p, "pref_location_filter_enabled", "locfilter.isEnabled", true );
		i( p, "pref_location_filter_ttff", "locfilter.firstRequestDT", 15 );
		i( p, "pref_location_filter_refresh_interval", "locfilter.requestRefreshInterval", 1 );
		i( p, "pref_location_filter_min_distance", "locfilter.minDistance", 100 );
		i( p, "pref_location_filter_max_age", "locfilter.maxAllowedAge", 20 );

		// challenge points
		b( p, "challenges_activated", "challenges.isActivated", true );
		i( p, "challenge_points", "challenges.points", 0 );
		l( p, "lastChallengePointsGained", "challenges.lastTimeEarned", 0 );

		// display
		b( p, "pref_global_when_or_in_how_much", "display.earliestAsPeriod", false );
	}

	private void b( PreferenceNode p, String f, String t, boolean d ) {
		boolean v = p.getBoolean( f, d );
		p.remove( f );
		p.setBoolean( t, v );
	}

	private void i( PreferenceNode p, String f, String t, int d ) {
		int v = p.getInt( f, d );
		p.remove( f );
		p.setInt( t, v );
	}

	private void l( PreferenceNode p, String f, String t, long d ) {
		long v = p.getLong( f, d );
		p.remove( f );
		p.setLong( t, v );
	}
};