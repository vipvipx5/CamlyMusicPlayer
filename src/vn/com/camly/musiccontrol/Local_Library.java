package vn.com.camly.musiccontrol;

import java.util.ArrayList;
import java.util.Date;


import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

public class Local_Library {
	
	public static final String 
			COLUMN_ID 			= MediaStore.Audio.Media._ID,
			COLUMN_TITLE 		= MediaStore.Audio.Media.TITLE,
			COLUMN_ARTIST_ID 	= MediaStore.Audio.Media.ARTIST_ID,	
			COLUMN_ARTIST 		= MediaStore.Audio.Media.ARTIST,	
			COLUMN_ALBUM 		= MediaStore.Audio.Media.ALBUM,
			COLUMN_ALBUM_ID 	= MediaStore.Audio.Media.ALBUM_ID,
			COLUMN_DURATION 	= MediaStore.Audio.Media.DURATION,
			COLUMN_DATA 		= MediaStore.Audio.Media.DATA,
			
			COLUMN_ALBUM_ART	= MediaStore.Audio.Albums.ALBUM_ART,
			COLUMN_ALBUM_TRACK	= MediaStore.Audio.Albums.NUMBER_OF_SONGS,
			COLUMN_ALBUM_YEAR	= MediaStore.Audio.Albums.FIRST_YEAR,
	
			COLUMN_ARTIST_ALBUM = MediaStore.Audio.Artists.NUMBER_OF_ALBUMS,
			COLUMN_ARTIST_TRACK = MediaStore.Audio.Artists.NUMBER_OF_TRACKS,
			
			COLUMN_PLAYLIST_NAME = MediaStore.Audio.Playlists.NAME,
			COLUMN_PLAYLIST_DATA = MediaStore.Audio.Playlists.DATA,
			COLUMN_PLAYLIST_DATE = MediaStore.Audio.Playlists.DATE_ADDED,
			
			COLUMN_SERVER = "server";
	
	
	public static ContentResolver mContentResolver = Control.context.getContentResolver();

//////////////////////////////////////////////////////////////////////////////
	
	// SongItem
	public static class SongItem {
		public long 	id 		= 0;
		public String 	title 	= "";
		public String 	artist 	= null;
		public long 	artistID 	= 0;
		public String 	album 		= "";
		public long 	albumID 	= 0;
		public long 	duration 	= 0;
		public String 	dataStream 	= null;
		public int 		server 		= 0;

		public SongItem() {
		}

		public SongItem(long _id, String t, String a, String p) {
			id = _id;
			title = t;
			artist = a;
			dataStream = p;
		}

		@Override
		public String toString() {
			
			if(artist == null) return title;
			else return title + " - " + artist;
		}
		
	}

	public static class AlbumItem{
		public long ID = 0;
		public String Title = "";
		public String Artist = "";
		public String AlbumArt = "";
		public int track = 0;
		public int year = 0;
		
		public AlbumItem(){
			
		}
		
		public ArrayList<SongItem> getTrackList() {
			return getTrackOfAlbum(ID);
		}
		
		@Override
		public String toString() {
			return AlbumArt + " - " + Title;
		}
	}
	
	public static class ArtistItem{
		public long ID = 0;
		public String Artist = "";
		public int numAlbum = 0;
		public int numTrack = 0;
		
		public ArtistItem() {
		}
		
		public ArrayList<SongItem> getTrackList() {
			return getTrackOfArtist(ID);
		}
		
		public ArrayList<AlbumItem> getAlbumList() {
			return getAlbumOfArtist(ID);
		}
		
		@Override
		public String toString() {
			// TODO Auto-generated method stub
			return Artist;
		}
	}
	
	public static class PlaylistItem{
		public long ID = 0;
		public String Name = "";
		public String Data = "";
		public Date Date = null;
		
		public PlaylistItem() {
		}
		
		public ArrayList<SongItem> getTrackList() {
			return getTrackOfPlayList(ID);
		}
		
		@Override
		public String toString() {
			// TODO Auto-generated method stub
			return Name;
		}
	}
	
	public static ArrayList<SongItem> getAllSong () {

		String selection = MediaStore.Audio.Media.IS_MUSIC + "=1";
	
		return queryForSongs( 	MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
		        				null, selection, null, COLUMN_TITLE);

	}
	
	public static ArrayList<AlbumItem> getAllAlbum(){
		String selection = null;	
		return queryForAlbum(	MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI, 
								null, selection, null, COLUMN_ALBUM);
	}
	
	public static ArrayList<ArtistItem> getAllArtist() {
		String selection = null;
		Cursor cur = mContentResolver.query( 
				MediaStore.Audio.Artists.EXTERNAL_CONTENT_URI,
		        null, selection, null, COLUMN_ARTIST);
		
		if (cur == null) {
			return null;
		}

		if (!cur.moveToFirst()) {
			return null;
		}
		
		ArrayList<ArtistItem> artistList = new ArrayList<>();
		
		int idColumn 		= cur.getColumnIndex(COLUMN_ID);
		int artistColumn 	= cur.getColumnIndex(COLUMN_ARTIST);
		int numAlbumColumn 	= cur.getColumnIndex(COLUMN_ARTIST_ALBUM);
		int numTrackColumn 	= cur.getColumnIndex(COLUMN_ARTIST_TRACK);
		
		do {
			ArtistItem ar = new ArtistItem();
			
			ar.ID = cur.getLong(idColumn);
			ar.Artist = cur.getString(artistColumn);
			ar.numTrack = cur.getInt(numTrackColumn);
			ar.numAlbum = cur.getInt(numAlbumColumn);
			
			artistList.add(ar);
		} while (cur.moveToNext());
		
		return artistList;
	}

	public static ArrayList<PlaylistItem> getAllPlayList(){
		String selection = null;
		Cursor cur = mContentResolver.query( 
				MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI,
		        null, selection, null, COLUMN_PLAYLIST_NAME);
		
		if (cur == null) {
			return null;
		}

		if (!cur.moveToFirst()) {
			return null;
		}
		
		ArrayList<PlaylistItem> List = new ArrayList<>();
		
		int idColumn 	= cur.getColumnIndex(COLUMN_ID);
		int nameColumn 	= cur.getColumnIndex(COLUMN_PLAYLIST_NAME);
		int dataColumn 	= cur.getColumnIndex(COLUMN_PLAYLIST_DATA);
		int dateColumn 	= cur.getColumnIndex(COLUMN_PLAYLIST_DATE);
		
		do {
			PlaylistItem pl = new PlaylistItem();
			
			pl.ID = cur.getLong(idColumn);
			pl.Name = cur.getString(nameColumn);
			pl.Data = cur.getString(dataColumn);
			pl.Date = new Date(cur.getLong(dateColumn));//;
			
			List.add(pl);
		} while (cur.moveToNext());
		
		return List;
	}
		
	
	
	public static ArrayList<SongItem> getTrackOfAlbum(long AlbumID){	
		String selection = COLUMN_ALBUM_ID + " = " + AlbumID;	
		return queryForSongs(	MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
		        				null, selection, null, COLUMN_TITLE);
	}

	public static ArrayList<SongItem> getTrackOfArtist(long ArtistID){
		String selection = COLUMN_ARTIST_ID + " = " + ArtistID;
		
		return queryForSongs( 	MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
		        				null,  selection, null, COLUMN_TITLE);
	}

	public static ArrayList<AlbumItem> getAlbumOfArtist(long ArtistID){
		
		String selection = null;	
		return queryForAlbum( 	MediaStore.Audio.Artists.Albums.getContentUri("external", ArtistID),
		        				null,  selection, null, null);
	}
	
	public static ArrayList<SongItem> getTrackOfPlayList(long PlaylistID){
		
		String selection = null;
		
		return queryForSongs( 	MediaStore.Audio.Playlists.Members.getContentUri("external", PlaylistID),
		        				null,  selection, null, null);
		
	}
	
	
	// Query for Songs
	private static ArrayList<SongItem> queryForSongs(Uri uri, String[] projection, String selection, String[] args,
			String sortOrder) {
		
		Cursor cur = mContentResolver.query(uri, projection, selection, args, sortOrder);	

		return cursorToSongList(cur, SongSQLite.SERVER_LOCAL );
	}
	
	// Query for Songs
	private static ArrayList<AlbumItem> queryForAlbum(Uri uri, String[] projection, String selection, String[] args,
			String sortOrder) {
		
		Cursor cur = mContentResolver.query( uri, projection, selection, args, sortOrder);
		
		if (cur == null) {
			return null;
		}

		if (!cur.moveToFirst()) {
			return null;
		}
		
		ArrayList<AlbumItem> albumlist = new ArrayList<>();
		
		int idColumn 		= cur.getColumnIndex(COLUMN_ID);
		int titleColumn 	= cur.getColumnIndex(COLUMN_ALBUM);
		int artistColumn 	= cur.getColumnIndex(COLUMN_ARTIST);
		int artColumn 		= cur.getColumnIndex(COLUMN_ALBUM_ART);
		int trackColumn 	= cur.getColumnIndex(COLUMN_ALBUM_TRACK);
		int yearColumn 		= cur.getColumnIndex(COLUMN_ALBUM_YEAR);
		
		do {
			AlbumItem al = new AlbumItem();
			
			al.ID = cur.getLong(idColumn);
			al.Title = cur.getString(titleColumn);
			al.Artist = cur.getString(artistColumn);
			al.AlbumArt = cur.getString(artColumn);
			al.track = cur.getInt(trackColumn);
			al.year = cur.getInt(yearColumn);
			
			albumlist.add(al);
		} while (cur.moveToNext());
		
		return albumlist;
	}
	
	public static ArrayList<SongItem> cursorToSongList(Cursor cur, int sever){
		if (cur == null) {
			return null;
		}

		if (!cur.moveToFirst()) {
			return null;
		}

		ArrayList<SongItem> list = new ArrayList<SongItem>();

		int idColumn 		= cur.getColumnIndex(COLUMN_ID);
		int titleColumn 	= cur.getColumnIndex(COLUMN_TITLE);
		int artistColumn 	= cur.getColumnIndex(COLUMN_ARTIST);
		int artistIDColumn 	= cur.getColumnIndex(COLUMN_ARTIST_ID);
		int albumColumn 	= cur.getColumnIndex(COLUMN_ALBUM);
		int albumIDColumn 	= cur.getColumnIndex(COLUMN_ALBUM_ID);
		int durationColumn 	= cur.getColumnIndex(COLUMN_DURATION);
		int dataColumn 		= cur.getColumnIndex(COLUMN_DATA);
		int serverColumn 	= cur.getColumnIndex(COLUMN_SERVER);
		
		boolean getFull = false;
		
		if(sever == SongSQLite.SERVER_LOCAL)
			getFull = true;
		
		do {
			SongItem si = new SongItem();
			
			
			si.title = cur.getString(titleColumn);
			si.artist = cur.getString(artistColumn);			
			si.album = cur.getString(albumColumn);		
			si.duration = cur.getLong(durationColumn);
			si.dataStream = cur.getString(dataColumn);
			
			if(getFull){
				si.id = cur.getLong(idColumn);
				si.artistID = cur.getLong(artistIDColumn);
				si.albumID = cur.getLong(albumIDColumn);
				si.server = SongSQLite.SERVER_LOCAL;
			}
			else {
				si.server = cur.getInt(serverColumn);
			}
			
			list.add(si);
		} while (cur.moveToNext());

		return list;
	}
	
}
	

