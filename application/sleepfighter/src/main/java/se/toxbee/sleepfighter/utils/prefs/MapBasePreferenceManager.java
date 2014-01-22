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
package se.toxbee.sleepfighter.utils.prefs;

import java.io.Serializable;
import java.util.Map;

import com.google.common.base.Preconditions;

/**
 * {@link MapBasePreferenceManager} builds on {@link BasePreferenceManager}<br/>
 * using a {@link Map} as in-memory backend and provides hooks for persistence.
 *
 * @author Centril<twingoow@gmail.com> / Mazdak Farrokhzad.
 * @version 1.0
 * @since Dec 14, 2013
 */
public abstract class MapBasePreferenceManager extends BasePreferenceManager {
	/**
	 * Returns the in-memory map of entities.
	 *
	 * @return the map.
	 */
	protected abstract Map<String, Serializable> memory();

	/**
	 * Sets the key-value pair in backend.
	 *
	 * @param key the key.
	 * @param value the value.
	 */
	protected abstract <U extends Serializable> void backendSet( String key, U value );

	/**
	 * Removes the key-value pair in backend.
	 *
	 * @param key the key.
	 */
	protected abstract <U extends Serializable> void backendRemove( String key );

	/**
	 * Loads the given preference into the in-memory map given by {@link #memory()}.
	 *
	 * @param prefs the preferences to load.
	 * @return this.
	 */
	protected MapBasePreferenceManager load( Iterable<? extends SerializablePreference> prefs ) {
		for ( SerializablePreference p : prefs ) {
			this.memory().put( p.key(), p.value() );
		}

		return this;
	}

	@Override
	public boolean contains( String key ) {
		return this.memory().containsKey( key );
	}

	@Override
	public Map<String, ?> getAll() {
		return this.memory();
	}

	public <U extends Serializable> U set( String key, U value ) {
		@SuppressWarnings( "unchecked" )
		U old = (U) this.memory().put( Preconditions.checkNotNull( key ), value );

		// Write to persistence if changed.
		if ( old != value ) {
			if ( value == null ) {
				this.backendRemove( key );
			} else {
				this.backendSet( key, value );
			}
		}

		return old;
	}

	public <U extends Serializable> U get( String key, U def ) {
		@SuppressWarnings( "unchecked" )
		U val = (U) this.memory().get( key );
		return val == null ? def : val;
	}

	@Override
	public boolean getBoolean( String key, boolean def ) {
		return this.get( key, def );
	}

	@Override
	public char getChar( String key, char def ) {
		return this.get( key, def );
	}

	@Override
	public short getShort( String key, short def ) {
		return this.get( key, def );
	}

	@Override
	public int getInt( String key, int def ) {
		return this.get( key, def );
	}

	@Override
	public long getLong( String key, long def ) {
		return this.get( key, def );
	}

	@Override
	public float getFloat( String key, float def ) {
		return this.get( key, def );
	}

	@Override
	public double getDouble( String key, double def ) {
		return this.get( key, def );
	}

	@Override
	public String getString( String key, String def ) {
		return this.get( key, def );
	}

	private <U extends Serializable> PreferenceNode setc( String key, U value ) {
		this.set( key, value );
		return this;
	}

	@Override
	public PreferenceNode setBoolean( String key, boolean val ) {
		return this.setc( key, val );
	}

	@Override
	public PreferenceNode setChar( String key, char val ) {
		return this.setc( key, val );
	}

	@Override
	public PreferenceNode setShort( String key, short val ) {
		return this.setc( key, val );
	}

	@Override
	public PreferenceNode setInt( String key, int val ) {
		return this.setc( key, val );
	}

	@Override
	public PreferenceNode setLong( String key, long val ) {
		return this.setc( key, val );
	}

	@Override
	public PreferenceNode setFloat( String key, float val ) {
		return this.setc( key, val );
	}

	@Override
	public PreferenceNode setDouble( String key, double val ) {
		return this.setc( key, val );
	}

	@Override
	public PreferenceNode setString( String key, String val ) {
		return this.setc( key, val );
	}

	@Override
	public PreferenceNode remove( String key ) {
		return this.setc( key, null );
	}

	@Override
	public PreferenceNode clear() {
		for ( String key : this.getAll().keySet() ) {
			this.remove( key );
		}

		return this;
	}
}