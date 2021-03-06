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

package se.toxbee.sleepfighter.persist;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.google.common.collect.Maps;
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;
import java.util.Map;

import se.toxbee.sleepfighter.model.Alarm;
import se.toxbee.sleepfighter.model.SnoozeConfig;
import se.toxbee.sleepfighter.model.audio.AudioConfig;
import se.toxbee.sleepfighter.model.audio.AudioSource;
import se.toxbee.sleepfighter.model.challenge.ChallengeConfig;
import se.toxbee.sleepfighter.model.challenge.ChallengeConfigSet;
import se.toxbee.sleepfighter.model.challenge.ChallengeParam;
import se.toxbee.sleepfighter.model.gps.GPSFilterArea;
import se.toxbee.sleepfighter.persist.dao.PersistenceException;
import se.toxbee.sleepfighter.persist.dao.PersistenceExceptionDao;
import se.toxbee.sleepfighter.persist.migration.MigrationExecutor;

/**
 * Provides an OrmLiteSqliteOpenHelper for persistence layer.
 *
 * @author Centril<twingoow@gmail.com> / Mazdak Farrokhzad.
 * @version 1.0
 * @since Sep 21, 2013
 */
public class OrmHelper extends OrmLiteSqliteOpenHelper {
	// Name of the database file in application.
	private static final String DATABASE_NAME = "sleep_fighter.db";

	// Current database version, change when database structure changes.
	private static final int DATABASE_VERSION = 27;

	// List of all classes that is managed by helper.
	private static final Class<?>[] CLASSES = new Class<?>[] {
		Alarm.class,

		AudioSource.class,
		AudioConfig.class,

		SnoozeConfig.class,

		ChallengeConfigSet.class,
		ChallengeConfig.class,
		ChallengeParam.class,

		GPSFilterArea.class
	};

	private final Map<Class<?>, PersistenceExceptionDao<Class<?>, ?>> daoMap = Maps.newHashMap();
	private final Map<Class<?>, DaoInitRunner<?>> daoInitRunners = Maps.newHashMap();

	/**
	 * DaoInitRunner is a method run when a Dao is first initialized.
	 *
	 * @author Centril<twingoow@gmail.com> / Mazdak Farrokhzad.
	 * @version 1.0
	 * @since Nov 12, 2013
	 */
	public static interface DaoInitRunner<T> {
		/**
		 * Called when Dao is initialized.
		 *
		 * @param helper the OrmHelper.
		 * @param daoClazz the Class of the Dao.
		 * @param dao the Dao itself.
		 */
		public <D extends PersistenceExceptionDao<T, ?>> void daoInit( OrmHelper helper, Class<T> daoClazz, D dao );
	}

	/**
	 * CacheEnabler is a DaoInitRunner that simply enables object cache.
	 *
	 * @author Centril<twingoow@gmail.com> / Mazdak Farrokhzad.
	 * @version 1.0
	 * @since Nov 12, 2013
	 */
	public class CacheEnabler<T> implements DaoInitRunner<T> {
		@Override
		public <D extends PersistenceExceptionDao<T, ?>> void daoInit( OrmHelper helper, Class<T> daoClazz, D dao ) {
			dao.setObjectCache( true );
		}
	}

	// Initialize DaoInitRunner:s here.
	{
		Map<Class<?>, DaoInitRunner<?>> r = this.daoInitRunners;
		r.put( Alarm.class, new CacheEnabler<Alarm>() );
	}

	public <T, I> PersistenceExceptionDao<T, I> dao( Class<T> clazz ) {
		@SuppressWarnings( "unchecked" )
		PersistenceExceptionDao<T, I> dao = (PersistenceExceptionDao<T, I>) this.daoMap.get( clazz );

		if ( dao == null ) {
			dao = this.getExceptionDao( clazz );

			this.runDaoInit( clazz, dao );

			@SuppressWarnings( "unchecked" )
			PersistenceExceptionDao<Class<?>, ?> castDao = (PersistenceExceptionDao<Class<?>, ?>) dao;

			this.daoMap.put( clazz, castDao );
		}

		return dao;
	}

	public <T> PersistenceExceptionDao<T, String> dao_s( Class<T> clazz ) {
		return this.dao( clazz );
	}

	public <T> PersistenceExceptionDao<T, Integer> dao_i( Class<T> clazz ) {
		return this.dao( clazz );
	}

	/**
	 * Runs the DaoInitRunner for clazz if any.
	 *
	 * @param clazz the to run for.
	 * @param dao the dao for the clazz.
	 */
	private <D extends PersistenceExceptionDao<T, ?>, T> void runDaoInit( Class<T> clazz, D dao ) {
		@SuppressWarnings( "unchecked" )
		DaoInitRunner<T> onInit = (DaoInitRunner<T>) this.daoInitRunners.get( clazz );
		if ( onInit != null ) {
			onInit.daoInit( this, clazz, dao );
		}
	}

	/**
	 * Get a PersistenceExceptionDao for given class. This uses the {@link DaoManager} to cache the DAO for future gets.
	 *
	 * @param clazz the class object to get Dao for.
	 */
	private <D extends PersistenceExceptionDao<T, ?>, T> D getExceptionDao(Class<T> clazz) {
		try {
			Dao<T, ?> dao = getDao(clazz);
			@SuppressWarnings({ "unchecked", "rawtypes" })
			D castDao = (D) new PersistenceExceptionDao(dao);
			return castDao;
		} catch (SQLException e) {
			throw new PersistenceException("Could not create RuntimeExcepitionDao for class " + clazz, e);
		}
	}

	/**
	 * Constructs the helper from a given context.
	 *
	 * @param context the context to use.
	 */
	public OrmHelper( Context context ) {
		super( context, DATABASE_NAME, null, DATABASE_VERSION );
	}

	/**
	 * Called when database is created (on install of android app).<br/>
	 * Creates all tables, etc.
	 */
	@Override
	public void onCreate(SQLiteDatabase db, ConnectionSource connectionSource) {
		try {
			for ( Class<?> clazz : CLASSES ) {
				TableUtils.createTable( connectionSource, clazz );
			}
		} catch ( SQLException e ) {
			Log.e( OrmHelper.class.getName(), "Fatal error: Couldn't create database." );
			throw new PersistenceException( e );
		}
	}

	/**
	 * <p>Called when application is updates from google play or something<br/>
	 * and this entails a changed model and therefore changed DB structure.</p>
	 */
	@Override
	public void onUpgrade(SQLiteDatabase db, ConnectionSource cs, int oldVersion, int newVersion) {
		if ( oldVersion == newVersion ) {
			return;
		}

		// Execute the migration.
		MigrationExecutor mig = new MigrationExecutor();
		boolean success = mig.execute( cs, db, oldVersion, newVersion );

		// Rebuild on failure.
		if ( !success ) {
			Log.e( OrmHelper.class.getName(), "Fatal error: Couldn't upgrade database." );
			this.rebuild();
		}

		// Nuke caches.
		this.nukeCache();
	}

	/**
	 * Returns a Dao which you can run raw statements on.
	 *
	 * @return the Dao.
	 */
	public PersistenceExceptionDao<?, ?> rawDao() {
		return this.dao( Alarm.class );
	}

	/**
	 * Drops all DB tables for clazzes.
	 *
	 * @param clazzes the classes to drop tables for.
	 * @return this.
	 */
	public OrmHelper drop( Class<?>[] clazzes ) {
		try {
			for ( Class<?> clazz : clazzes ) {
				TableUtils.dropTable( this.getConnectionSource(), clazz, true );
			}
		} catch ( SQLException e ) {
			Log.e( OrmHelper.class.getName(), "Can't drop databases", e );
			throw new PersistenceException( e );
		}
		return this;
	}

	/**
	 * Clears all DB tables for clazzes.
	 *
	 * @param clazzes the classes to clear tables for.
	 * @return this.
	 */
	public OrmHelper clear( Class<?>[] clazzes ) {
		try {
			for ( Class<?> clazz : clazzes ) {
				TableUtils.clearTable( this.getConnectionSource(), clazz );
			}
		} catch ( SQLException e ) {
			Log.e( OrmHelper.class.getName(), "Can't drop databases", e );
			throw new PersistenceException( e );
		}
		return this;
	}

	/**
	 * Drops a DB table for given clazz.
	 *
	 * @param clazz the class to drop table for.
	 * @return this.
	 */
	public OrmHelper drop( Class<?> clazz ) {
		try {
			TableUtils.dropTable( this.getConnectionSource(), clazz, true );
		} catch ( SQLException e ) {
			throw new PersistenceException( e );
		}

		return this;
	}

	/**
	 * Clears a DB table for given clazz.
	 *
	 * @param clazz the class to clear table for.
	 * @return this.
	 */
	public OrmHelper clear( Class<?> clazz ) {
		try {
			TableUtils.clearTable( this.getConnectionSource(), clazz );
		} catch ( SQLException e ) {
			throw new PersistenceException( e );
		}

		return this;
	}

	/**
	 * Rebuilds database. Any data is lost.
	 */
	public void rebuild() {
		SQLiteDatabase db = this.getWritableDatabase();
		this.drop( CLASSES );
		this.onCreate( db, this.getConnectionSource() );
	}

	/**
	 * Closes the database connections and clear any cached DAOs.
	 */
	@Override
	public void close() {
		super.close();
		this.nukeCache();
	}

	/**
	 * Nukes the cache if any (Dao + Object.
	 */
	private void nukeCache() {
		// Get rid of reference.
		this.daoMap.clear();

		// Clear all caches (Dao + Object).
		DaoManager.clearCache();
	}
}