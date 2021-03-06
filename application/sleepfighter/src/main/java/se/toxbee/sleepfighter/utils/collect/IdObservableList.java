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

package se.toxbee.sleepfighter.utils.collect;

import se.toxbee.sleepfighter.utils.model.IdProvider;

/**
 * {@link IdObservableList} is an {@link ObservableList} of {@link IdProvider}s.
 *
 * @author Centril<twingoow@gmail.com> / Mazdak Farrokhzad.
 * @version 1.0
 * @since Dec 14, 2013
 */
public class IdObservableList<E extends IdProvider> extends ObservableList<E> {
	/**
	 * Returns an element with the unique id provided.
	 * 
	 * @param id the unique id of the element.
	 * @return the element, if not found it returns null.
	 */
	public E getById( int id ) {
		for ( E elem : this ) {
			if ( elem.getId() == id ) {
				return elem;
			}
		}

		return null;
	}
}
