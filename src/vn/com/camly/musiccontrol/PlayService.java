package vn.com.camly.musiccontrol;

import java.util.ArrayList;
import com.dropbox.client2.DropboxAPI;

import vn.com.camly.dropbox.DropboxConnect;
import vn.com.camly.dropbox.GetUrlFromPath;
import vn.com.camly.musiccontrol.Local_Library.SongItem;
import vn.com.camly.musicplayer.MainActivity;
import vn.com.camly.musicplayer.R;


import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnBufferingUpdateListener;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnInfoListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.media.MediaPlayer.OnSeekCompleteListener;
import android.os.Handler;
import android.os.IBinder;
import android.widget.RemoteViews;
import android.widget.Toast;


public class PlayService extends Service implements OnCompletionListener,
OnPreparedListener, OnErrorListener, OnSeekCompleteListener,
OnInfoListener, OnBufferingUpdateListener {
	
/////////////////////////////////////////////////////////////////////////////////////

	private MediaPlayer mediaPlayer = new MediaPlayer();
	final static int 	NOTIFICATON_ID = 55;
	
	private NotificationCompat.Builder 	mBuilder;
	private NotificationManager 		mNotificationManager;
	private RemoteViews 				notifiExpandView,
										notifiSmallView;
	private Notification 				notification;
	
	//Action of intent
	public final static String 
			//Action send audio preparing to activity 
			BROADCAST_FEEDBACK = "vn.com.camly.musicplayer.broadcastfeedback",
			//Action send update position to activity
			BROADCAST_SEEK = "vn.com.camly.musicplayer.seekprogress",
			//Action Activity send seek to service
			BROADCAST_CONTROL = "vn.com.camly.musicplayer.control";
	
	//Key to get/set Extra from/to intent
	public final static String
			KEY_FEED_ACTION 		= "feedaction",
			KEY_FEEDBACK 			= "feedbacktoactivity",
			KEY_FEEDBACK_ISPLAYING 	= "feedbackisplaying",
					
			KEY_UPDATE_SEEK_POSITION 	= "mediaposition",
			KEY_UPDATE_SEEK_DURATION 	= "mediaduration",
			
			KEY_CONTROL_ACTION 	= "controlaction",
			KEY_CONTROL_VALUE 	= "controlvalue",
			
			KEY_SELECT_SUB_LIST 		= "controlsublist",
			KEY_SELECT_LIST_POSITION 	= "controllistposition",
				
			NO_ACTION_TOGGLE_PLAY 	= "toggleplayback",
			NO_ACTION_NEXT 			= "nextmedia",
			NO_ACTION_PREVIOUS 		= "previousmedia",
			NO_ACTION_STOP 			= "stopmedia";	
	
	public final static int
			ACTION_CONTROL_SEEK 		= 0,		
			ACTION_CONTROL_START_PAUSE 	= 1,
			ACTION_CONTROL_PREVIOUS 	= 2,
			ACTION_CONTROL_NEXT 		= 3,
			ACTION_CONTROL_SELECT_SONG 	= 4,
			ACTION_CONTROL_FAST_FORWARD	= 5,
			ACTION_CONTROL_REWIND 		= 6,
			ACTION_CONTROL_STOP			= 7,
			ACTION_REQUEST_POSITION		= 8,
			ACTION_GOT_URL				= 9,
			ACTION_CONTROL_SELECT_LIST	= 10,
			ACTION_CONTROL_ADD_ITEM		= 11,
			ACTION_REFRESH_DROPBOX_API	= 12,
			
			LIST_ALL_LOCAL		= 1,
			LIST_ALL_DROPBOX	= 2,
			LIST_ALBUM			= 3,
			LIST_ARTIST			= 4,
			
			FEED_ACTION_BUFFER 			= 1,
			FEED_ACTION_SEND_POSITION 	= 2;
	
	private Intent 	
			feedBackIntent, 	//Intent send preparing
			seekIntent;		//Intent send update position to activity
	
	//Variable to send positon and duration of media to the activity (send update position)
	private int
			intMediaPosition,
			intMediaDuration;
	public static int
			intCurrentSong,
			intTotalSong;
	
	
	public static ArrayList<SongItem> 	playingSongList = new ArrayList<>();
	private SongSQLite 		nowplayingData;
	private SongItem 		currentSong;
	
	private boolean isPause = false;
	
	private DropboxAPI<?> dropboxApi;
	//handler to create the thread to send update position
	private final Handler handler = new Handler();
	
	
///////////////////////////////////////////////////////////////////////////////////	
///////////////////////////////////////////////////////////////////////////////////	
	
	@Override
	public void onCreate() {
		
		feedBackIntent = new Intent(BROADCAST_FEEDBACK);	
		seekIntent = new Intent(BROADCAST_SEEK);
	
		mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
		mediaPlayer.setOnCompletionListener(this);
		mediaPlayer.setOnErrorListener(this);
		mediaPlayer.setOnBufferingUpdateListener(this);
		mediaPlayer.setOnInfoListener(this);
		mediaPlayer.setOnSeekCompleteListener(this);
		mediaPlayer.setOnPreparedListener(this);
		mediaPlayer.reset();
		
		DropboxConnect dropConnect = new DropboxConnect(getApplicationContext());
		dropboxApi = dropConnect.getDropboxAPI();	
		
		nowplayingData = SongSQLite.getmInstance(getApplicationContext());
		playingSongList = nowplayingData.getAllSong(SongSQLite.TABLE_NOWPLAYING, null);
		intCurrentSong = 0;
			
		if(playingSongList == null) playingSongList = new ArrayList<>(); //If Database is Emply

		intTotalSong = playingSongList.size();
		updateCurrentSong();	
		
		initNotification();
		Log.d(">>>> THAI <<<<", "onCreat() Service");
	}

//////////////////////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////////

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		
		registerReceiver(controlReceiver, new IntentFilter(BROADCAST_CONTROL));
		if(intent != null){	
		
			String action = intent.getAction();
	
			if(	NO_ACTION_TOGGLE_PLAY.equals(action)){
				TogglePlayAudio();
			}
			else if(NO_ACTION_NEXT.equals(action)){
				NextAudio();
			}	
			else if(NO_ACTION_PREVIOUS.equals(action)){
				PrevousAudio();
			}
			else if(NO_ACTION_STOP.equals(action)){
				StopAudio();
			}
			
		}
		
		Log.d(">>>> THAI <<<<", "onStartCommand() Service");
		return START_STICKY;
	}


	
	///////////////////////////////////////////////////////////////////////////////
	//Thread use to send position update
	private Runnable sendUpdate2UI = new Runnable() {		
		@Override
		public void run() {
			if(mediaPlayer.isPlaying()){
				intMediaPosition = mediaPlayer.getCurrentPosition();
				intMediaDuration = mediaPlayer.getDuration();
				
				seekIntent.putExtra(KEY_UPDATE_SEEK_POSITION, intMediaPosition);
				seekIntent.putExtra(KEY_UPDATE_SEEK_DURATION, intMediaDuration);
				//seekIntent.putExtra(KEY_UPDATE_SEEK_ENDED, songEnd);
				sendBroadcast(seekIntent);
			}
			
			handler.postDelayed(sendUpdate2UI, 1000);
		}
	};
	
	///////////////////////////////////////////////////////////////////////////////
	//Setup the notification
	private void initNotification() {
		
		Intent resultIntent = new Intent(this, MainActivity.class);
		PendingIntent resultPendingIntent = PendingIntent
				.getActivity(getApplicationContext(), 0, resultIntent, 0);
		
		PendingIntent toggleIntent = makePendingIntent(PlayService.class, NO_ACTION_TOGGLE_PLAY);
		PendingIntent nextIntent = makePendingIntent(PlayService.class, NO_ACTION_NEXT);
		PendingIntent prevIntent = makePendingIntent(PlayService.class, NO_ACTION_PREVIOUS);
		PendingIntent stopIntent = makePendingIntent(PlayService.class, NO_ACTION_STOP);
		
		notifiExpandView = new RemoteViews(getPackageName(), R.layout.notification_expanded);
		
		notifiExpandView.setTextViewText		(R.id.lblNotificationTitle, currentSong.title);
		notifiExpandView.setImageViewResource	(R.id.notificationAlbum, R.drawable.earth);
		notifiExpandView.setTextViewText		(R.id.lblNotificationArtist, currentSong.artist);
		notifiExpandView.setTextViewText		(R.id.lblNotificationAlbum, currentSong.album);
		notifiExpandView.setOnClickPendingIntent(R.id.btnPlay_pause, toggleIntent);
		notifiExpandView.setOnClickPendingIntent(R.id.btnNext, nextIntent);
		notifiExpandView.setOnClickPendingIntent(R.id.btnPrevious, prevIntent);
		notifiExpandView.setOnClickPendingIntent(R.id.btnNotificationStop, stopIntent);
		
		
		notifiSmallView = new RemoteViews(getPackageName(), R.layout.notification_small); 
		
		notifiSmallView.setTextViewText			(R.id.lblNotificationTitle, currentSong.title);
		notifiSmallView.setImageViewResource	(R.id.notificationAlbum, R.drawable.earth);
		notifiSmallView.setTextViewText			(R.id.lblNotificationAlbumArtist, currentSong.artist 
												+ " - " + currentSong.album);
		notifiSmallView.setOnClickPendingIntent	(R.id.btnPlay_pause, toggleIntent);
		notifiSmallView.setOnClickPendingIntent	(R.id.btnNext, nextIntent);
		notifiSmallView.setOnClickPendingIntent	(R.id.btnPrevious, prevIntent);	
		
		mBuilder = new NotificationCompat.Builder(this)
						.setSmallIcon(R.drawable.ic_action_play)
						.setOngoing(true)
						.setContentIntent(resultPendingIntent);
		
		notification = mBuilder.build();	
		notification.bigContentView = notifiExpandView;
		notification.contentView = notifiSmallView;
		
		mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		mNotificationManager.notify(NOTIFICATON_ID, notification);
		
	}
	
	private PendingIntent makePendingIntent(Class<PlayService> partnerClass, String action){
		Intent intent = new Intent(this, partnerClass);	
		intent.setAction(action);	
		return PendingIntent.getService(this, 0, intent, 0);
	}

	private void blindNotification(boolean isPlaying){
		
		notifiExpandView.setTextViewText		(R.id.lblNotificationTitle, currentSong.title);
		notifiExpandView.setImageViewResource	(R.id.notificationAlbum, R.drawable.earth);
		notifiExpandView.setTextViewText		(R.id.lblNotificationArtist, currentSong.artist);
		notifiExpandView.setTextViewText		(R.id.lblNotificationAlbum, currentSong.album);
		
		notifiSmallView.setTextViewText		(R.id.lblNotificationTitle, currentSong.title);
		notifiSmallView.setImageViewResource(R.id.notificationAlbum, R.drawable.earth);
		notifiSmallView.setTextViewText		(R.id.lblNotificationAlbumArtist, currentSong.artist 
											+ " - " + currentSong.album);
		if(isPlaying){
			notifiExpandView.setImageViewResource(R.id.btnPlay_pause, R.drawable.ic_action_pause);
			notifiSmallView.setImageViewResource(R.id.btnPlay_pause, R.drawable.ic_action_pause);
		}
		else{
			notifiExpandView.setImageViewResource(R.id.btnPlay_pause, R.drawable.ic_action_play);
			notifiSmallView.setImageViewResource(R.id.btnPlay_pause, R.drawable.ic_action_play);
		}
			
		mNotificationManager.notify(NOTIFICATON_ID, notification);
	}
	
	
	private void updateCurrentSong(){
		try{
			currentSong = playingSongList.get(intCurrentSong);
		}catch(Exception e){
			currentSong = new SongItem();
		}
	}
/////////////////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////////	
	

	private void StartAudio(){
		
		updateCurrentSong();
			
		String path = currentSong.dataStream;
		
		if(currentSong.server == SongSQLite.SERVER_DROPBOX){
			
			try {
				sendFeedBackBroadcast(FEED_ACTION_BUFFER, 0, true); //Start buffering
				new GetUrlFromPath(dropboxApi, getApplicationContext()).execute(path);
			} catch (Exception e1) {
				e1.printStackTrace();
				Log.e(">>>> THAI <<<<", "SERICE: Get media url from Dropbox FALSE: " + e1.toString());
			}
			return;
		}
			
		StartAudio(path);

	}

	private void StartAudio(String source){
		
		handler.removeCallbacks(sendUpdate2UI);
		mediaPlayer.reset();
		
		//Start buffer
		try{			
			mediaPlayer.setDataSource(source);
		}
		catch(Exception e){
			e.printStackTrace();
			Toast.makeText(this, e.toString() + ": " + source, Toast.LENGTH_LONG).show();
		}	
		
		sendFeedBackBroadcast(FEED_ACTION_SEND_POSITION, intCurrentSong, true);
		mediaPlayer.prepareAsync();
		
		blindNotification(true);
		
		//Start send media playing position to activity	
		handler.postDelayed(sendUpdate2UI, 1000);
	}
	
/////////////////////////////////////////////////////////////////////////////////		
	
	private void TogglePlayAudio(){
		if(mediaPlayer.isPlaying()){
			mediaPlayer.pause();
			isPause = true;
			sendFeedBackBroadcast(FEED_ACTION_SEND_POSITION, intCurrentSong, false);
			blindNotification(false);
			
			return;
		}
		
		if(isPause){
			sendFeedBackBroadcast(FEED_ACTION_SEND_POSITION, intCurrentSong, true);
			blindNotification(true);
			try {
				mediaPlayer.start();
			} catch (Exception e) {
				StartAudio();
			}	
		}
		else {
			StartAudio();
		}
		isPause = false;
	}

/////////////////////////////////////////////////////////////////////////////////		
	
	private void StopAudio(){
		mediaPlayer.stop();
		isPause= false;
		sendFeedBackBroadcast(FEED_ACTION_SEND_POSITION, intCurrentSong, false);
		blindNotification(false);
	}

/////////////////////////////////////////////////////////////////////////////////		
/////////////////////////////////////////////////////////////////////////////////		
	
	private void NextAudio(){
		
		intCurrentSong++;	
		if (intCurrentSong >= intTotalSong) {
			intCurrentSong = 0;
		}		
		
		StartAudio();
	}

/////////////////////////////////////////////////////////////////////////////////		
/////////////////////////////////////////////////////////////////////////////////		

	private void PrevousAudio(){
		
		intCurrentSong--;
		if (intCurrentSong < 0) {
			intCurrentSong = 0;
		}
		
		StartAudio();
	}

/////////////////////////////////////////////////////////////////////////////////		

	
	private void FastForward(int milisecond){
		int position = mediaPlayer.getCurrentPosition(),
			duration = mediaPlayer.getDuration();
		position += milisecond;
		
		if(position > (duration - 5000) ){
			if(duration > 5000)
				position = duration - 5000;
			else 
				position = duration;
		}
		
		mediaPlayer.seekTo(position);
	}
	
/////////////////////////////////////////////////////////////////////////////////		
	
	private void Rewind(int milisecond){
		int position = mediaPlayer.getCurrentPosition();
		position -= milisecond;
		
		if(position < 1)
			position = 0;
		
		mediaPlayer.seekTo(position);
	}
/////////////////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////////
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		
		//Stop media
		if(mediaPlayer != null){
			if(mediaPlayer.isPlaying()){
				mediaPlayer.stop();
			}
			mediaPlayer.release();
		}
		
		//Destroy the notification
		cancelNotification();
		
		//Destroy the update seek from activity
		unregisterReceiver(controlReceiver);
		
		//Stop send update media position to activity
		handler.removeCallbacks(sendUpdate2UI);
		
		nowplayingData.close();
		Log.d(">>>> THAI <<<<", "onDestroy() SERVICE");
	}

	private void cancelNotification() {
		NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		mNotificationManager.cancel(NOTIFICATON_ID);
	}
	
/////////////////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////////
	
	@Override
	public void onPrepared(MediaPlayer mp) {
		//Send to activity about the preparing is done
		sendFeedBackBroadcast(FEED_ACTION_BUFFER, 1, true);
		
		//Start media
		mediaPlayer.start();
		
		Log.d(">>>> THAI <<<<", "onPrepared() SERVICE");
	}
	
	///////////////////////////////////////////////////////////////////////////////
	private void sendFeedBackBroadcast(int action, int value, boolean isplaying) {
		feedBackIntent.putExtra(KEY_FEED_ACTION, action);
		feedBackIntent.putExtra(KEY_FEEDBACK, value);	
		feedBackIntent.putExtra(KEY_FEEDBACK_ISPLAYING, isplaying);
		
		sendBroadcast(feedBackIntent);
	}
	
/////////////////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////////
	
	@Override
	//Played to the end of song
	public void onCompletion(MediaPlayer mp) {
		NextAudio();	
		Log.d(">>>> THAI <<<<", "ONCOMPLETION PLAYER");
	}
	
/////////////////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////////

	@Override
	public boolean onError(MediaPlayer mp, int what, int extra) {
		switch (what) {
			case MediaPlayer.MEDIA_ERROR_NOT_VALID_FOR_PROGRESSIVE_PLAYBACK:
				Toast.makeText(this, "MEDIA ERROR NOT VALID FOR PROGRESSIVE PLAYBACK " + extra, Toast.LENGTH_LONG).show();
				break;
			
			case MediaPlayer.MEDIA_ERROR_SERVER_DIED:
				Toast.makeText(this, "MEDIA ERROR SERVER DIED " + extra, Toast.LENGTH_LONG).show();
				break;
				
			case MediaPlayer.MEDIA_ERROR_UNKNOWN:
				Toast.makeText(this, "MEDIA ERROR UNKNOWN " + extra, Toast.LENGTH_LONG).show();
				break;
				
			default:
				break;
		}		
		return false;
	}

/////////////////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////////
	
	@Override
	public void onBufferingUpdate(MediaPlayer mp, int percent) {
	}

/////////////////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////////
	
	@Override
	public boolean onInfo(MediaPlayer mp, int what, int extra) {
		Log.d(">>>> THAI <<<<", "onInfo() SERVICE");
		return false;
	}

/////////////////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////////

	@Override
	public IBinder onBind(Intent intent) {
		Log.d(">>>> THAI <<<<", "onBind() SERVICE");
		return null;
	}
	 
	
/////////////////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////////
	
	@Override
	public void onSeekComplete(MediaPlayer mp) {
		//Play media after seek to new position
		if(!mediaPlayer.isPlaying())
			mediaPlayer.start();
			
		Log.d(">>>> THAI <<<<", "onSeekComplete() SERVICE");
	}

	private BroadcastReceiver controlReceiver = new BroadcastReceiver() {	
		@Override
		public void onReceive(Context context, Intent intent) {
			Log.d(">>>> THAI <<<<", "1. BroadcastReceiver controlReceiver - SERVICE");
			
			int action = intent.getExtras().getInt(KEY_CONTROL_ACTION);
			
			switch (action) {
			
			case ACTION_CONTROL_SEEK:
				handler.removeCallbacks(sendUpdate2UI);			
				int seekMediaTo = intent.getIntExtra(KEY_CONTROL_VALUE, 0);							
				mediaPlayer.seekTo(seekMediaTo);			
				handler.postDelayed(sendUpdate2UI, 1000);
				break;
				
				
			case ACTION_CONTROL_NEXT:
				NextAudio();
				break;
				
				
			case ACTION_CONTROL_PREVIOUS:
				PrevousAudio();
				break;
				
				
			case ACTION_CONTROL_START_PAUSE:
				TogglePlayAudio();
				
				break;
			
				
			case ACTION_CONTROL_SELECT_SONG:
				intCurrentSong = intent.getIntExtra(KEY_CONTROL_VALUE, 0);
				StartAudio();
				break;
				
				
			case ACTION_CONTROL_FAST_FORWARD:
				FastForward(10000);
				break;
				
				
			case ACTION_CONTROL_REWIND:
				Rewind(10000);
				break;
				
				
			case ACTION_CONTROL_STOP:
				StopAudio();
				break;
				
				
			case ACTION_REQUEST_POSITION:
				sendFeedBackBroadcast(FEED_ACTION_SEND_POSITION, intCurrentSong, mediaPlayer.isPlaying());
				break;
			
				
			case ACTION_GOT_URL:
				String sourceURL = intent.getStringExtra(KEY_CONTROL_VALUE);
				if(sourceURL != null)
					StartAudio(sourceURL);
				else 
					Toast.makeText(getApplicationContext(), "Song URL is NULL", Toast.LENGTH_SHORT).show();
				break;
			
				
			case ACTION_CONTROL_SELECT_LIST:								
				selectList(intent);
				intCurrentSong = intent.getIntExtra(KEY_SELECT_LIST_POSITION, 0);
				intTotalSong = playingSongList.size();
				StartAudio();
				break;
				
				
			case ACTION_CONTROL_ADD_ITEM:
				addtoNowplaying(intent);
				break;
				
			case ACTION_REFRESH_DROPBOX_API:
				DropboxConnect dropConnect = new DropboxConnect(getApplicationContext());
				dropboxApi = dropConnect.getDropboxAPI();
				break;
				
			default:
				break;
			}

			Log.d(">>>> THAI <<<<", "2. BroadcastReceiver controlReceiver - SERVICE, Action = " + action);
		}
	};
	

	

/////////////////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////////
	
	private void selectList(Intent intent){		
		Log.d(">>>> THAI <<<<", "1. selectList - SERVICE");
		
		int listSelected = intent.getIntExtra(KEY_CONTROL_VALUE, LIST_ALL_LOCAL);
		
		playingSongList.clear();
		
		switch (listSelected) {
		
		case LIST_ALL_LOCAL:
			playingSongList = Local_Library.getAllSong();
			break;

		case LIST_ALL_DROPBOX:
			SongSQLite dropboxManage = new SongSQLite(getApplicationContext());
			playingSongList = dropboxManage.getAllSong(SongSQLite.TABLE_ONLINE_SONG, null);					
			break;
			
		case LIST_ARTIST:
			long ArtistID = intent.getLongExtra(KEY_SELECT_SUB_LIST, 0);	
			playingSongList = Local_Library.getTrackOfArtist(ArtistID);		
			break;
			
		case LIST_ALBUM:
			long AlbumID = intent.getLongExtra(KEY_SELECT_SUB_LIST, 0);	
			playingSongList = Local_Library.getTrackOfAlbum(AlbumID);			
			break;
			
		default:
			break;
		}
		if(playingSongList == null) playingSongList = new ArrayList<>();
		replaceNowPlayingSQL(playingSongList);
		
		Log.d(">>>> THAI <<<<", "2. selectList - SERVICE, List = " + listSelected);
	}
	
	private void replaceNowPlayingSQL(ArrayList<SongItem> songList){
		
		nowplayingData.removeALL(SongSQLite.TABLE_NOWPLAYING);
		
		int size = songList.size();
		for (int i = 0; i < size; i++) {
			nowplayingData.insertSong(songList.get(i), SongSQLite.TABLE_NOWPLAYING);
		}
		Log.d(">>>> THAI <<<<", "addToNowPlayingSQL - SERVICE, Number of song = " + size);
	}
	
	
	private void addtoNowplaying(Intent intent){		
		Log.d(">>>> THAI <<<<", "1. addtoNowplaying - SERVICE");
		
		int listSelected = intent.getIntExtra(KEY_CONTROL_VALUE, LIST_ALL_LOCAL);
		int songSelected = intent.getIntExtra(KEY_SELECT_LIST_POSITION, 0);
		SongItem song = new SongItem();
		
		switch (listSelected) {
		
		case LIST_ALL_LOCAL:
			song = Local_Library.getAllSong().get(songSelected);
			break;

		case LIST_ALL_DROPBOX:
			SongSQLite dropboxManage = new SongSQLite(getApplicationContext());
			song = dropboxManage.getAllSong(SongSQLite.TABLE_ONLINE_SONG, null).get(songSelected);					
			break;
			
		default:
			break;
		}
		
		if(playingSongList == null) playingSongList = new ArrayList<>();
		
		nowplayingData.insertSong(song, SongSQLite.TABLE_NOWPLAYING);
		playingSongList.add(song);
		intTotalSong ++;
		
	}
}
