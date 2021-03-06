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

package se.toxbee.sleepfighter.receiver;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.LocationManager;
import android.os.Bundle;

import org.joda.time.DateTime;

import se.toxbee.sleepfighter.android.power.WakeLocker;
import se.toxbee.sleepfighter.app.SFApplication;
import se.toxbee.sleepfighter.gps.GPSFilterLocationRetriever;
import se.toxbee.sleepfighter.model.AlarmList;
import se.toxbee.sleepfighter.model.AlarmTimestamp;
import se.toxbee.sleepfighter.preference.LocationFilterPreferences;

/**
 * GPSFilterRefreshReceiver is responsible for simply scheduling and requesting updates,<br/>
 * and receiving responses to said requests.
 *
 * @author Centril<twingoow@gmail.com> / Mazdak Farrokhzad.
 * @version 1.0
 * @since Oct 10, 2013
 */
public class GPSFilterRefreshReceiver extends BroadcastReceiver {
	private static final String FROM_ALARM_MANAGER = "from_alarm_manager";
	private static final String REQUESTING_SINGLE = "requesting_single";

	private static final int MINUTE_TO_MS_FACTOR = 60000;

	private static LocationFilterPreferences prefs() {
		return SFApplication.get().getPrefs().locFilter;
	}

	@Override
	public void onReceive( Context context, Intent intent ) {
		WakeLocker.acquire( context );

		if ( intent.getBooleanExtra( FROM_ALARM_MANAGER, false ) ) {
			// We got this from AlarmManager, we need to make a single request.
			// Make pending intent.
			Bundle bundle = new Bundle();
			bundle.putBoolean( FROM_ALARM_MANAGER, true );
			PendingIntent pi = makePi( context, bundle );

			// Make the request.
			GPSFilterLocationRetriever locRet = getLocRet( context );
			if ( locRet == null ) {
				WakeLocker.release();
				return;
			}

			locRet.requestSingleUpdate( context, pi );
		} else {
			// Get refresh interval.
			LocationFilterPreferences prefs = prefs();
			int interval = prefs.requestRefreshInterval();

			boolean reqSingle = intent.getBooleanExtra( REQUESTING_SINGLE, false );

			if ( interval == 0 ) {
				if ( !reqSingle ) {
					unscheduleUpdates( context );
				}

				WakeLocker.release();
				return;
			}

			// Check if next update will be after alarm (too late).
			long now = new DateTime().getMillis();
			AlarmList list = SFApplication.get().getAlarms();
			AlarmTimestamp at = list.getEarliestAlarm( now );
			if ( at.getMillis() + (long)interval * MINUTE_TO_MS_FACTOR > now ) {
				if ( !reqSingle ) {
					unscheduleUpdates( context );
				}

				WakeLocker.release();
				return;
			}

			if ( reqSingle ) {
				// We've made our single requests and interval updates are on, start requesting them.
				Bundle bundle = new Bundle();
				PendingIntent pi = makePi( context, bundle );
				getLocRet( context ).requestUpdates( context, pi, interval, prefs.minDistance() );
			}
		}
	}

	/**
	 * Schedules for a fix.
	 *
	 * @param context android context.
	 * @param alarmTime the time of earliest alarm in Unix time.
	 */
	public static void scheduleFix( Context context, long alarmTime ) {
		SFApplication app = SFApplication.get();
		LocationFilterPreferences prefs = prefs();

		// Make sure we're allowed to make refreshes and that there are any areas to refresh for.
		if ( !(prefs.isEnabled() && app.getPersister().fetchGPSFilterAreas().hasEnabledAndValid()) ) {
			// No areas, unschedule instead.
			unscheduleUpdates( context );
			return;
		}

		// Figure out Unix time of when to make the first fix.
		int frtd = prefs.firstRequestDT();

		// If first request time delta = 0 then it is disabled.
		if ( frtd == 0 ) {
			return;
		}

		long unixTime = alarmTime + (long)frtd * MINUTE_TO_MS_FACTOR;

		// Make pending intent.
		Bundle bundle = new Bundle();
		bundle.putBoolean( FROM_ALARM_MANAGER, true );
		PendingIntent pi = makePi( context, bundle );

		// Schedule.
		getAm( context ).set( AlarmManager.RTC_WAKEUP, unixTime, pi );
	}

	/**
	 * Unschedules fixes if any.
	 *
	 * @param context android context.
	 */
	public static void unscheduleFix( Context context ) {
		getAm( context ).cancel( makePi( context, null ) );
		unscheduleUpdates( context );
	}

	private static GPSFilterLocationRetriever getLocRet( Context context ) {
		Criteria criteria = new Criteria();
		criteria.setAccuracy( Criteria.ACCURACY_FINE );

		GPSFilterLocationRetriever locRet = new GPSFilterLocationRetriever( criteria );

		return locRet.getBestProvider( context ) == null ? null : locRet;
	}

	private static void unscheduleUpdates( Context context ) {
		getLM( context ).removeUpdates( makePi( context, null ) );
	}

	private static PendingIntent makePi( Context context, Bundle extras ) {
		Intent intent = new Intent( context, GPSFilterRefreshReceiver.class );

		if ( extras != null ) {
			intent.putExtras( extras );
		}

		PendingIntent pi = PendingIntent.getBroadcast( context, -1, intent, PendingIntent.FLAG_UPDATE_CURRENT );
		return pi;
	}

	private static AlarmManager getAm( Context context ) {
		return (AlarmManager) context.getSystemService( Context.ALARM_SERVICE );
	}

	private static LocationManager getLM( Context context ) {
		return (LocationManager) context.getSystemService( Context.LOCATION_SERVICE );
	}
}