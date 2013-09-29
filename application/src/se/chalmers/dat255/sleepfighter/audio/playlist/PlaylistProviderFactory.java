package se.chalmers.dat255.sleepfighter.audio.playlist;

import java.util.Map;

import android.net.Uri;

import com.google.common.collect.Maps;

/**
 * PlaylistProviderFactory is a factory for PlaylistProvider
 *
 * @see PlaylistProvider
 * @author Centril<twingoow@gmail.com> / Mazdak Farrokhzad.
 * @version 1.0
 * @since Sep 28, 2013
 */
public class PlaylistProviderFactory {
	public enum ProviderID {
		LOCAL
	}

	private Map<ProviderID, PlaylistProvider> entries;

	/**
	 * <p>Returns a PlaylistProvider that is a valid provider for a given playlistUri.<br/>
	 * This has the unfortunate downside of calling {@link #getAll()} internally,<br/>
	 * thus lazy loading all providers available.</p>
	 *
	 * <p>The first provider that is a match is returned.</p>
	 *
	 * @param playlistUri the URI of a playlist.
	 * @return the provider or null if none was found.
	 */
	public PlaylistProvider getProvider( String playlistUri ) {
		PlaylistProvider[] all = this.getAll();

		for ( PlaylistProvider provider : all ) {
			if ( provider.isProviderFor( playlistUri ) ) {
				return provider;
			}
		}

		return null;
	}

	/**
	 * <p>Returns a PlaylistProvider that matches the given URI.<br/>
	 * This has the unfortunate downside of calling {@link #getAll()} internally,<br/>
	 * thus lazy loading all providers available.</p>
	 *
	 * <p>The first provider that is a match is returned.</p>
	 *
	 * @param uri the URI of a the provider.
	 * @return the provider or null if none was found.
	 */
	public PlaylistProvider getProvider( Uri uri ) {
		PlaylistProvider[] all = this.getAll();

		for ( PlaylistProvider provider : all ) {
			if ( provider.getUri().equals( uri ) ) {
				return provider;
			}
		}

		return null;
	}

	/**
	 * Returns a PlaylistProvider that matches the ProviderID.<br/>
	 * This is lazy loaded.
	 *
	 * @param id the ProviderID.
	 * @return the provider.
	 */
	public PlaylistProvider getProvider( ProviderID id ) {
		if ( this.entries == null ) {
			this.entries = Maps.newEnumMap( ProviderID.class );
		}

		PlaylistProvider provider = this.entries.get( id );

		if ( provider == null ) {
			switch ( id ) {
			case LOCAL:
				provider = new LocalPlaylistProvider();
				break;
			}

			this.entries.put( id, provider );
		}

		return provider;
	}

	/**
	 * Returns all available PlaylistProvider:s.
	 *
	 * @return all available PlaylistProvider:s.
	 */
	public PlaylistProvider[] getAll() {
		ProviderID[] ids = ProviderID.values();
		PlaylistProvider[] providers = new PlaylistProvider[ids.length];

		for ( int i = 0; i < ids.length; ++i ) {
			providers[i] = this.getProvider( ids[i] );
		}

		return providers;
	}
}