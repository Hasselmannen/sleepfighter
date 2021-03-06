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

package se.toxbee.sleepfighter.model.audio;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import se.toxbee.sleepfighter.utils.model.IdProvider;

/**
 * <p>AudioSource is the model for knowing where to look for audio and with what to read it and get metadata</p>
 *
 * <p>It has 2 properties, one accessed by {@link #getType()}, which tells us what to read source with.<br/>
 * The next property is the URI which is implementation specfic depending on {@link #getType()}.</p>
 *
 * <p>AudioSource is immutable.</p>
 *
 * @author Centril<twingoow@gmail.com> / Mazdak Farrokhzad.
 * @version 1.0
 * @since Sep 26, 2013
 */
@DatabaseTable(tableName = "audio_source")
public class AudioSource implements IdProvider {
	/** Signifies a silent/non-existent AudioSource which is = null. */
	public static final AudioSource SILENT = null;

	public static final String ID_COLUMN = "id";

	@DatabaseField(generatedId = true, columnName = ID_COLUMN)
	private int id;

	@DatabaseField
	private AudioSourceType type;

	@DatabaseField
	private String uri;

	/**
	 * Constructs an AudioSource, for DB purposes only.
	 */
	public AudioSource() {
	}

	/**
	 * Constructs an AudioSource given type & URI.
	 *
	 * @param type the type.
	 * @param uri the URI.
	 */
	public AudioSource( AudioSourceType type, String uri ) {
		this.type = type;
		this.uri = uri;
	}

	/**
	 * Copy constructor.
	 *
	 * @param rhs the source to copy from.
	 */
	public AudioSource( AudioSource rhs ) {
		this.type = rhs.type;
		this.uri = rhs.uri;
	}


	/**
	 * Returns the type of this audio source.
	 *
	 * @return the type.
	 */
	public AudioSourceType getType() {
		return this.type;
	}

	/**
	 * Returns a representation of the source as an implementation specific URI.<br/>
	 * Has varying meaning depending on {@link #getType()}.
	 *
	 * @return URI representation of source.
	 */
	public String getUri() {
		return this.uri;
	}

	public String toString() {
		return "AudioSource[type: " + this.getType() + ", uri: " + this.getUri() + "]";
	}

	/**
	 * Returns the id of AudioSource (in DB).
	 *
	 * @return the id.
	 */
	public int getId() {
		return this.id;
	}

	/**
	 * Sets the ID of the AudioSource, only used for DB purposes or testing.
	 *
	 * @param id the ID.
	 */

	public void setId( int id ) {
		this.id = id;
	}
}
