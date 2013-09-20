package se.chalmers.dat255.sleepfighter.utils;

import junit.framework.TestCase;

import org.joda.time.MutablePeriod;
import org.joda.time.Period;

import se.chalmers.dat255.sleepfighter.model.Alarm;
import se.chalmers.dat255.sleepfighter.model.AlarmsManager.EarliestInfo;

public class DateTextUtilsTests extends TestCase {
	private static final String[] formats = { "No alarm active",
			"In &lt; 1 minute", "In %1$s", "In %1$s and %2$s",
			"In %1$s, %2$s and %3$s" };

	private static final String[] partFormats = { "%1$dd", "%2$dh", "%3$dm" };

	private Period diff( int d, int h, int m ) {
		MutablePeriod diff = new MutablePeriod();
		diff.setDays( 2 );
		diff.setHours( 3 );
		diff.setMinutes( 0 );
		return diff.toPeriod();
	}

	private void test( String text, int d, int h, int m ) {
		EarliestInfo info = new EarliestInfo( Long.valueOf( 1 ), null, 0 );
		Period diff = diff( d, h, m );
		text = DateTextUtils.getEarliestText( formats, partFormats, diff, info );
		assertEquals( text, text );
	}

	// public static final String getEarliestText( String[] formats , String[]
	// partFormats, Calendar now, EarliestInfo earliestInfo ) {
	public void testGetEarliestTextTest() {
		// not active
		EarliestInfo info = new EarliestInfo( Alarm.NEXT_NON_REAL, null, 0 );
		String earliest = DateTextUtils.getEarliestText( formats, partFormats, null, info );
		assertEquals( formats[0], earliest );

		// days and hours
		test( "In 2d and 3h", 2, 3, 0 );

		// days and minutes
		test( "In 2d and 3m", 2, 0, 0 );

		// hours and minutes
		test( "In 2h and 3m", 0, 2, 3 );

		// days
		test( "In 2d", 2, 0, 0 );

		// minutes
		test( "In 3m", 0, 0, 3 );

		// days, hours, and seconds.
		test( "In 1d, 2h and 3m", 1, 2, 3 );
	}
}