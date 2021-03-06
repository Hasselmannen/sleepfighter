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

package se.toxbee.sleepfighter.gps.gui;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
import android.location.Criteria;
import android.location.Location;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Display;
import android.view.ViewGroup;

import java.util.List;

import se.toxbee.sleepfighter.R;
import se.toxbee.sleepfighter.android.location.LocationAdapter;
import se.toxbee.sleepfighter.gps.GPSFilterLocationRetriever;
import se.toxbee.sleepfighter.model.gps.GPSFilterArea;
import se.toxbee.sleepfighter.model.gps.GPSFilterMode;
import se.toxbee.sleepfighter.model.gps.GPSFilterPolygon;
import se.toxbee.sleepfighter.model.gps.GPSLatLng;

/**
* {@link LocationGUIHandler} handles all interaction with maps.
*
*
* @author Centril<twingoow@gmail.com> / Mazdak Farrokhzad.
* @version 1.0
* @since Jan, 08, 2014
*/
public class LocationGUIHandler implements LocationGUIReceiver {
	private static final String TAG = LocationGUIHandler.class.getSimpleName();

	private static final int POLYGON_EXCLUDE_FILL_COLOR = R.color.gpsfilter_polygon_fill_exclude;
	private static final int POLYGON_INCLUDE_FILL_COLOR = R.color.gpsfilter_polygon_fill_include;
	private static final int POLYGON_STROKE_COLOR = R.color.shadow;

	private static final long MAX_LOCATION_FIX_AGE = 5 * 60 * 1000; // 5 minutes.

	private static final float MAP_MY_LOCATION_ZOOM = 13f;
	private static final float CAMERA_MOVE_BOUNDS_PADDING = 0.2f; // padding as a % of lowest of screen width/height.

	private final FragmentActivity activity;
	private final GPSFilterArea area;
	private final OnMapClick clickListener;

	private LocationGUIProvider provider;

	private List<GPSLatLng> points;

	public LocationGUIHandler( FragmentActivity activity, GPSFilterArea area, OnMapClick clickListener ) {
		this.area = area;
		this.activity = activity;
		this.clickListener = clickListener;

		// Get a working list of points.
		this.points = this.area.getPoints();
	}

	/**
	 * Returns the screen dimensions as a Point.
	 *
	 * @return the screen dimensions.
	 */
	private Point getScreenDim() {
		Display display = this.activity.getWindowManager().getDefaultDisplay();

		@SuppressWarnings( "deprecation" )
		Point size = new Point( display.getWidth(), display.getHeight() );
		return size;
	}

	public void setupMap( LocationGUIProvider provider, ViewGroup viewContainer ) {
		this.provider = provider;
		this.provider.initMap( viewContainer );
	}

	public void zoomToArea() {
		this.provider.moveCameraToPolygon( true );
	}

	public void updatePolygon() {
		this.provider.updateGuiPolygon();
	}

	/**
	 * Adds a point to polygon at point.<br/>
	 * Also adds a marker to GUI.
	 *
	 * @param point the point to add to polygon and where to place marker.
	 */
	public void addPoint( GPSLatLng point ) {
		this.points.add( point );
		this.provider.addPoint( point );

		this.commit();
	}

	/**
	 * Adds a point to polygon at point.<br/>
	 * Also adds a marker to GUI.
	 *
	 * @param lat the latitude of point.
	 * @param lng the longitude of point.
	 */
	public void addPoint( CharSequence lat, CharSequence lng ) {
		this.addPoint( new GPSLatLng( lat, lng ) );
	}

	/**
	 * Removes the last added marker/point.
	 */
	public void undoLastPoint() {
		if ( !this.points.isEmpty() ) {
			this.points.remove( this.points.size() - 1 );
			this.disableIfDissatisfied();

			this.commitPolygon();
		}

		if ( this.provider.hasPoints() ) {
			this.provider.undoLastPoint();
			this.provider.updateGuiPolygon();

			if ( this.provider.hasPoints() ) {
				this.provider.moveCameraToPolygon( true );
			}
		}
	}

	/**
	 * Clears all markers/points.
	 */
	public void clearPoints() {
		this.points.clear();
		this.provider.clearPoints();

		this.commit();
		this.disableIfDissatisfied();
	}

	/**
	 * Disables the area when/if dissatisfied.
	 */
	private void disableIfDissatisfied() {
		if ( !this.satisfiesPolygon() ) {
			this.area.setEnabled( false );
		}
	}

	/**
	 * Commits the polygon saving it while also performing a GUI update.<br/>
	 * If the polygon doesn't honor {@link LocationGUIProvider#satisfiesPolygon()}, the area is disabled.
	 */
	private void commit() {
		this.provider.updateGuiPolygon();
		this.commitPolygon();
	}

	private void commitPolygon() {
		this.area.setPolygon( this.points.isEmpty() ? null : new GPSFilterPolygon( this.points ) );
	}

	/**
	 * Returns whether or not this activity instance is considered<br/>
	 * fresh. It is fresh when the area is either new, or the polygon is undefined.
	 *
	 * @return true if new & undefined polygon.
	 */
	public boolean isFresh() {
		return this.points.isEmpty();
	}

	/* -----------------------------------
	 * LocationGUIReceiver implementation.
	 * -----------------------------------
	 */

	@Override
	public FragmentActivity getActivity() {
		return this.activity;
	}

	@Override
	public int computeMoveCameraPadding() {
		Point dim = this.getScreenDim();
		int min = Math.min( dim.x, dim.y );
		return (int) (CAMERA_MOVE_BOUNDS_PADDING * min);
	}

	@Override
	public boolean satisfiesPolygon() {
		return this.points.size() >= 3;
	}

	@Override
	public int getPolygonFillColor() {
		int id = this.area.getMode() == GPSFilterMode.INCLUDE ? POLYGON_INCLUDE_FILL_COLOR : POLYGON_EXCLUDE_FILL_COLOR;
		return adjustAlpha( getActivity().getResources().getColor( id ), 255 * 0.5f );
	}

	@Override
	public int getPolygonStrokeColor() {
		return getActivity().getResources().getColor( POLYGON_STROKE_COLOR );
	}

	/**
	 * Alpha-adjusts a given color setting the alpha-component to alphaArg, rounded.
	 *
	 * @param color the color.
	 * @param alphaArg the alpha value to use.
	 * @return the color.
	 */
	private int adjustAlpha( int color, float alphaArg ) {
		int alpha = Math.round( alphaArg );
		int red = Color.red( color );
		int green = Color.green( color );
		int blue = Color.blue( color );
		return Color.argb( alpha, red, green, blue );
	}

	@Override
	public float getZoomFactor() {
		return MAP_MY_LOCATION_ZOOM;
	}

	public void moveToCurrentLocation() {
		Context context = this.getActivity();

		GPSFilterLocationRetriever retriever = new GPSFilterLocationRetriever( new Criteria() );
		Location loc = retriever.getLocation( context );

		if ( loc == null ) {
			// User turned off location fetching, send it to device location settings.
			this.gotoLocationSettings();
		} else {
			// First move to the last known location..
			this.provider.moveCamera( loc, false );

			// Check if location fix is out dated, if it is, request a new location, ONCE.
			long elapsedTime = System.currentTimeMillis() - loc.getTime();
			if ( elapsedTime > MAX_LOCATION_FIX_AGE ) {
				// Therefore, we request a single fix.
				retriever.requestSingleUpdate( context, new LocationAdapter() {
					@Override
					public void onLocationChanged( Location loc ) {
						provider.moveCamera( loc, true );
					}
				} );
			}
		}
	}

	/**
	 * Sends users to android location settings.
	 */
	private void gotoLocationSettings() {
		Intent i = new Intent( android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS );
		this.getActivity().startActivity( i );
	}

	@Override
	public void onMapReady() {
		if ( this.isFresh() ) {
			this.moveToCurrentLocation();
		}

		this.setupMarkers();
	}

	/**
	 * Sets up the markers if available.
	 */
	private void setupMarkers() {
		if ( !this.points.isEmpty() ) {
			// Put markers for each edge.
			for ( GPSLatLng pos : this.points ) {
				this.provider.addPoint( pos );
			}

			this.provider.initCameraToPolygon();
			this.provider.updateGuiPolygon();
		}
	}

	@Override
	public boolean onMapClick( GPSLatLng loc ) {
		if ( !this.clickListener.onMapClick( loc ) ) {
			this.addPoint( loc );
		}

		return true;
	}

	@Override
	public void onMarkerDrag( GPSLatLng loc ) {
		this.provider.updateGuiPolygon();
	}

	@Override
	public void onMarkerDragEnd( int pointIndex, GPSLatLng loc ) {
		if ( pointIndex == -1 ) {
			Log.wtf( TAG, "Should never happen!" );
			return;
		}

		this.replacePoint( pointIndex, loc );
	}

	private void replacePoint( int pointIndex, GPSLatLng point ) {
		this.points.set( pointIndex, point );

		this.commit();
	}

	@Override
	public void onMarkerDragStart( GPSLatLng loc ) {
	}
}
