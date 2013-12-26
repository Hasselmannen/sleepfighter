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
package se.toxbee.sleepfighter.utils.factory;

import java.util.EnumMap;
import java.util.Map;

/**
 * Changes the data structure of relations in a AbstractFactory to EnumMap.
 * Extending classes must implement {@link #getEnumClass()}.
 *
 * @param <K> Key type restriction.
 * @param <V> Value type restriction. All items produced from factory must be at least of this super-class.
 * @author Centril<twingoow@gmail.com> / Mazdak Farrokhzad.
 * @since 2012-12-21
 * @version 1.0
 */
public abstract class EnumKeyedFactory<K extends Enum<K>, V> extends AbstractFactory<K, V> {
	/**
	 * Provides EnumMap as data structure for factory relations.
	 * Extending classes must implement {@link #keyClass()}
	 * which returns the Class object of the enum used for key.
	 *
	 * @return The EnumMap.
	 */
	protected final Map<K, FactoryInstantiator<K, V>> createMap() {
		return new EnumMap<K, FactoryInstantiator<K, V>>( this.keyClass() );
	}

	/**
	 * Returns the Class object of the enum class used for keys.
	 * Must be implemented by sub-classes.
	 *
	 * @return The Class object of the enum-key class.
	 */
	protected abstract Class<K> keyClass();
}