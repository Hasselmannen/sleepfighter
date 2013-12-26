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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * An abstract factory class relating a key K to a {@link FactoryInstantiator} of type T.
 * The factory can - given a set of given FactoryInstantiators produce objects of type T.
 * Extending the class is easy. Just extend it with
 * AbstractFactory<KeyType, YourRestrictedType>, eg.: AbstractFactory<String, Icecream>.
 * and either use constructor or initializer blocks (non-static, and most compact method...)
 * to add your relations with: add( "Strawberry", StrawberryIcecream.class );
 *
 * @param <K> Key type restriction.
 * @param <V> Value type restriction. All items produced from factory must be at least of this super-class.
 * @author Centril<twingoow@gmail.com> / Mazdak Farrokhzad.
 * @version 2
 * @since Nov 14, 2013
 */
public abstract class AbstractFactory<K, V> implements Factory<K, V> {
	/**
	 * A map/list relating a key to a FactoryInstantiator.
	 * This map has a 1x1 relation between keys and FactoryInstantiators and is thus bijective.
	 */
	protected final Map<K, FactoryInstantiator<K, V>> relations;

	/**
	 * Constructor: creates the relations map.
	 */
	public AbstractFactory() {
		this.relations = this.createMap();
	}

	/**
	 * Creates/instantiates the relations map.
	 * Override if you want a different data structure.
	 *
	 * @return The relations map.
	 */
	protected Map<K, FactoryInstantiator<K, V>> createMap() {
		return new HashMap<K, FactoryInstantiator<K, V>>();
	}

	/**
	 * Enables run-time addition of a Class object.
	 *
	 * @param key What the FactoryInstantiator is called "publicly".
	 * @param _class Class object of class to instantiate
	 */
	public AbstractFactory<K, V> add( final K key, final Class<? extends V> _class ) {
		return add( key, new FactoryClassInstantiator<K, V>( _class ) );
	}

	/**
	 * Enables run-time addition of a FactoryInstantiator.
	 *
	 * @param name What the FactoryInstantiator is called "publicly".
	 * @param instantiator A FactoryInstantiator used to instantiate an object.
	 */
	public AbstractFactory<K, V> add( final K key, final FactoryInstantiator<K, V> instantiator ) {
		relations.put( key, instantiator );
		return this;
	}

	/**
	 * Disables run-time instantiation of a given "public" key from factory.
	 *
	 * @param name What the FactoryInstantiator is called "publicly".
	 */
	public AbstractFactory<K, V> remove( final K key ) {
		relations.remove( key );
		return this;
	}

	/**
	 * {@inheritDoc}
	 * In the case of AbstractFactory it relates to FactoryInstatiator:s.
	 */
	public Set<K> getKeys() {
		return this.relations.keySet();
	}

	/** {@inheritDoc} */
	public V get( final K key ) {
		return this.produce( key );
	}

	/**
	 * {@inheritDoc}
	 * In the case of AbstractFactory it relates to FactoryInstatiator:s.
	 *
	 * @throws FactoryInstantiationException if no object can be produced.
	 */
	public V produce( final K key ) {
		FactoryInstantiator<K, V> ins = relations.get( key );

		if ( ins == null ) {
			return null;
		} else {
			try {
				return ins.produce( key );
			} catch ( Throwable e ) {
				throw new FactoryInstantiationException( e );
			}
		}
	}

	/**
	 * Returns returns a list of the produced objects from all keys.
	 * NOTE: This method walks through all keys and calls produce(),
	 * thus it's recommended to limit the number of times it is called,
	 * especially if not using singleton-production.
	 *
	 * @return List of the produced objects from all keys.
	 */
	public List<V> getAll() {
		Set<? extends K> keys = this.getKeys();
		List<V> all = new ArrayList<V>( keys.size() );

		for ( K key : keys ) {
			all.add( this.produce( key ) );
		}

		return all;
	}
}