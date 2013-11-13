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
package se.toxbee.sleepfighter.model;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import static org.junit.Assert.*;

import org.joda.time.DateTime;

import se.toxbee.sleepfighter.utils.message.Message;
import se.toxbee.sleepfighter.utils.message.MessageBus;

public class AlarmListTest {
	@Test
	public void testGetEarliestInfo() {
		// Bootstrap.
		Alarm first = new Alarm( new AlarmTime( 0, 2 ) );
		first.setId( 1 );
		first.setActivated(true);
		Alarm second = new Alarm( new AlarmTime( 0, 3 ) );
		second.setId( 2 );
		second.setActivated(true);

		List<Alarm> list = new ArrayList<Alarm>();
		list.add(first);
		list.add( second );

		AlarmList manager = new AlarmList( list );

		long now = new DateTime(0,1,1,0,0).getMillis();

		// Test index correctness.
		AlarmTimestamp info = manager.getEarliestAlarm( now );
		assertTrue( info != AlarmTimestamp.INVALID );
		assertEquals( info.getAlarm(), first );

		second.setTime( new AlarmTime( 0, 1 ) );
		info = manager.getEarliestAlarm( now );
		assertTrue( info != AlarmTimestamp.INVALID );
		assertEquals( info.getAlarm(), second );

		// Test isReal() correctness.
		first.setActivated( false );
		second.setActivated( false );
		info = manager.getEarliestAlarm( now );
		assertTrue( info == AlarmTimestamp.INVALID );
	}

	@Test
	public void testSetMessageBus(  ) {
		Alarm first = new Alarm( new AlarmTime( 12, 2 ) );
		Alarm second = new Alarm( new AlarmTime( 12, 3 ) );

		List<Alarm> list = new ArrayList<Alarm>();
		list.add(first);
		list.add( second );

		AlarmList manager = new AlarmList( list );

		MessageBus<Message> bus = new MessageBus<Message>();
		manager.setMessageBus(bus);
		
		assertEquals(bus, first.getMessageBus());
		assertEquals(bus, second.getMessageBus());
	}

	@Test
	public void testFireEvent() {
		List<Alarm> list = new ArrayList<Alarm>();
		AlarmList manager = new AlarmList( list );
		MessageBus<Message> bus = new MessageBus<Message>();
		manager.setMessageBus(bus);
		
		Alarm first = new Alarm( new AlarmTime( 12, 2 ) );
		manager.add(first);
		assertEquals(bus, first.getMessageBus());
		
		Alarm second = new Alarm( new AlarmTime( 12, 3 ) );
		manager.set(0, second);
		assertEquals(bus, second.getMessageBus());
	
	}

	@Test
	public void testGetById() {
		AlarmList alarmList = new AlarmList();
		
		// Test with no elements in list
		assertEquals(null, alarmList.getById(0));
		
		// Add one element to list
		Alarm alarm1 = new Alarm();
		alarm1.setId(5);
		alarmList.add(alarm1);
		
		// Test with 1 element in list
		assertEquals(alarm1, alarmList.getById(5));
		
		// Add a second element
		Alarm alarm2 = new Alarm();
		alarm2.setId(7);
		alarmList.add(alarm2);
		
		// Look for both IDs when both in list
		assertEquals(alarm1, alarmList.getById(5));
		assertEquals(alarm2, alarmList.getById(7));
		
		// Test looking for ID that no alarm in list has
		assertEquals(null, alarmList.getById(6));
	}

	@Test
	public void testFindLowestUnnamedPlacement() {
		AlarmList list = new AlarmList();

		testUnnamedPart( list, 1 );

		Alarm first = new Alarm( new AlarmTime( 0, 0 ) );
		list.add(first);
		list.add(new Alarm(first));
		list.add(new Alarm(first));
		list.add(new Alarm(first));

		// tests for whole list.
		for ( int i = 0; i < 4; i++ ) {
			list.get( i ).setUnnamedPlacement( testUnnamedPart( list, i + 1 ) );
		}

		// name the second alarm and test, slot 2 should be empty and be returned now.
		list.get( 1 ).setName( "test-name" );
		testUnnamedPart( list, 2 );
	}

	private int testUnnamedPart( AlarmList list, int test ) {
		int lowest = list.findLowestUnnamedPlacement();
		assertTrue( lowest == test );
		return lowest;
	}
}