package vn.com.camly.musiccontrol;

import java.util.ArrayList;

import vn.com.camly.musiccontrol.Local_Library.SongItem;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class SongSQLite extends SQLiteOpenHelper{

	private static final String DATABASE_NAME = "nowPlaying.sqlite";
	private static final int 	SCHEMA_VERSION = 1;

	public static final String 
			TABLE_NOWPLAYING 	= "Nowplaying",
			TABLE_ONLINE_SONG 	= "SongOnline";

	public static final int SERVER_ALL 		= 0;
	public static final int SERVER_LOCAL 	= 1;
	public static final int SERVER_DROPBOX 	= 2;
	
	public static final String COLUMN_ID = "table_ID";
	
	private static SongSQLite mInstance = null;
	
	
	public static SongSQLite getmInstance(Context context) {
		
		if(mInstance == null)
			mInstance = new SongSQLite(context);
		
		return mInstance;
	}
	
	public SongSQLite(Context context) {
		super(context, DATABASE_NAME, null, SCHEMA_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		
		db.execSQL("CREATE TABLE " 	+ TABLE_NOWPLAYING 	+ " ("		
				+ COLUMN_ID 	+ " INTEGER PRIMARY KEY AUTOINCREMENT,"
				+ Local_Library.COLUMN_ID 	+ " INTEGER,"
				+ Local_Library.COLUMN_TITLE 		+ " TEXT,"
				+ Local_Library.COLUMN_ARTIST_ID 	+ " INTEGER,"
				+ Local_Library.COLUMN_ARTIST 		+ " TEXT,"
				+ Local_Library.COLUMN_ALBUM_ID	+ " INTEGER,"
				+ Local_Library.COLUMN_ALBUM 		+ " TEXT,"
				+ Local_Library.COLUMN_DURATION 	+ " INTEGER,"
				+ Local_Library.COLUMN_DATA 		+ " TEXT,"
				+ Local_Library.COLUMN_SERVER		+ " INTEGER);");	
		
		db.execSQL("CREATE TABLE " 	+ TABLE_ONLINE_SONG 	+ " ("		
				+ COLUMN_ID 	+ " INTEGER PRIMARY KEY AUTOINCREMENT,"
				+ Local_Library.COLUMN_TITLE 		+ " TEXT,"
				+ Local_Library.COLUMN_ARTIST 		+ " TEXT,"
				+ Local_Library.COLUMN_ALBUM 		+ " TEXT,"
				+ Local_Library.COLUMN_DURATION 	+ " INTEGER,"
				+ Local_Library.COLUMN_DATA 		+ " TEXT,"
				+ Local_Library.COLUMN_SERVER		+ " INTEGER);");	
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		
	}
	
	
	public void insertSong(SongItem song, String table) {
		
		if(song == null) return;
		
		ContentValues cv = new ContentValues();
		
		
		cv.put(Local_Library.COLUMN_TITLE, 	song.title);
		cv.put(Local_Library.COLUMN_ARTIST, 	song.artist);
		cv.put(Local_Library.COLUMN_ALBUM, 	song.album);
		cv.put(Local_Library.COLUMN_DURATION, 	song.duration);
		cv.put(Local_Library.COLUMN_DATA, 		song.dataStream);
		cv.put(Local_Library.COLUMN_SERVER, 	song.server);
		
		if(table.equals(SongSQLite.TABLE_NOWPLAYING)){
			cv.put(Local_Library.COLUMN_ID, 		song.id);
			cv.put(Local_Library.COLUMN_ARTIST_ID,	song.artistID);
			cv.put(Local_Library.COLUMN_ALBUM_ID, 	song.albumID);
		}
		
		try{
			getWritableDatabase().insert(table, COLUMN_ID, cv);
		}
		catch(Exception e){
			Log.e(">>>> THAI <<<<", "Insert FAILS: " + e.toString());
		}
	}
	
	public ArrayList<SongItem> getAllSong(String table, String sortByColumn){
		
		String sort = "";
		if(sortByColumn != null)
			sort = " ORDER BY " + sortByColumn;
		
		Cursor c = getReadableDatabase().rawQuery("SELECT * "
				+ " FROM " + table +  sort
				, null);
		return Local_Library.cursorToSongList(c, SERVER_ALL);
	}

	
	public ArrayList<SongItem> getFromServer(int server, String sortByColumn){
		
		String sort = "";
		if(sortByColumn != null)
			sort = " ORDER BY " + sortByColumn;
		
		Cursor c = getReadableDatabase().rawQuery("SELECT * "			
				+ " FROM " + TABLE_ONLINE_SONG
				+ " WHERE " + Local_Library.COLUMN_SERVER + " = " + server 
				+  sort
				, null);
		
		return Local_Library.cursorToSongList(c, server);
	}
	
	
	public void remove(long SongID) {
		getWritableDatabase().delete(
				TABLE_NOWPLAYING, 
				Local_Library.COLUMN_ID + "=" + SongID,
				null);
	}
	
	public void removeALL(String table) {
		getWritableDatabase().delete(table, null, null);
	}


	
}
