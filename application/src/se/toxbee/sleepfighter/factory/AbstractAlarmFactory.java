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
package se.toxbee.sleepfighter.factory;

import se.toxbee.sleepfighter.model.Alarm;
import se.toxbee.sleepfighter.model.SnoozeConfig;
import se.toxbee.sleepfighter.model.audio.AudioConfig;
import se.toxbee.sleepfighter.model.audio.AudioSource;
import se.toxbee.sleepfighter.model.challenge.ChallengeConfigSet;

/**
 * AbstractAlarmFactory is the abstract factory implementation of AlarmFactory. 
 *
 * @author Centril<twingoow@gmail.com> / Mazdak Farrokhzad.
 * @version 1.0
 * @since Oct 4, 2013
 */
public abstract class AbstractAlarmFactory implements AlarmFactory {
	@Override
	public Alarm createAlarm() {
		Alarm alarm = this.instantiateAlarm();

		// How to set the time is varied, let concrete factory implement how to set it.
		this.setTime( alarm );

		// Set basic Alarm native properties.
		alarm.setIsPresetAlarm( this.createIsPresetFlag() );
		alarm.setActivated( this.createIsActivated() );
		alarm.setEnabledDays( this.createEnabledDays() );
		alarm.setRepeat( this.createIsRepeatingFlag() );
		alarm.setName( this.createName() );
		alarm.setSpeech(this.createIsSpeech());
		alarm.setFlash(this.createIsFlashEnabled());

		// Set foreign objects.
		alarm.setAudioSource( this.createAudioSource() );
		alarm.setFetched( this.createAudioConfig() );
		alarm.setChallenges( this.createChallenges() );
		alarm.setFetched(createSnoozeConfig());

		return alarm;
	}

	protected abstract Alarm instantiateAlarm();

	protected abstract void setTime( Alarm alarm );

	protected abstract boolean createIsRepeatingFlag();

	protected abstract String createName();

	protected abstract boolean createIsPresetFlag();

	protected abstract boolean createIsActivated();

	protected abstract boolean[] createEnabledDays();

	protected abstract AudioSource createAudioSource();

	protected abstract AudioConfig createAudioConfig();

	protected abstract SnoozeConfig createSnoozeConfig();

	protected abstract boolean createVibrationFlag();

	protected abstract ChallengeConfigSet createChallenges();
	
	protected abstract boolean createIsFlashEnabled();


	protected abstract boolean createIsSpeech();
}