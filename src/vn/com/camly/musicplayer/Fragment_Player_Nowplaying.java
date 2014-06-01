package vn.com.camly.musicplayer;

import vn.com.camly.musiccontrol.Control;
import vn.com.camly.musiccontrol.PlayService;
import vn.com.camly.musiccontrol.Util;
import vn.com.camly.musiccontrol.Local_Library.SongItem;
import vn.com.camly.musicplayer.R;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

public class Fragment_Player_Nowplaying extends Fragment {

	public static final String TITLE = "NOWPLAYING";
	
	private SeekBar seekbar;
	
	private int 
			counter,
			intSeekMax;

	
	private TextView
			lblTimePlaying,
			lblDuration,
			lblTitle,
			lblAlbumArtist;
	
	private ImageButton 
			btnPlayPause,
			btnPrevious,
			btnNext,
			btnFastForward,
			btnRewind;	
	
//////////////////////////////////////////////////////////////////////////////

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		Log.d(">>>> THAI <<<<", "onCreate() NOWPLAYING_FRAGMENT");
	}
//////////////////////////////////////////////////////////////////////////////
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		View rootView = inflater.inflate(R.layout.fragment_player_nowplaying, container, false);
		
        btnPlayPause 	= (ImageButton)rootView.findViewById(R.id.btnPlay_pauseFull);
        btnNext 		= (ImageButton)rootView.findViewById(R.id.btnNextFull);
        btnPrevious 	= (ImageButton)rootView.findViewById(R.id.btnPreviousFull);
        btnFastForward	= (ImageButton)rootView.findViewById(R.id.btnFastForwardFull);
		btnRewind		= (ImageButton)rootView.findViewById(R.id.btnrewindFull);
		
        seekbar 		= (SeekBar)rootView.findViewById(R.id.seekbar_player);
        lblDuration 	= (TextView)rootView.findViewById(R.id.lblDuration);
        lblTimePlaying 	= (TextView)rootView.findViewById(R.id.lblTimePlaying);
        
		lblTitle 		= (TextView)rootView.findViewById(R.id.lblTitle);
		lblAlbumArtist 	= (TextView)rootView.findViewById(R.id.lblArtistAlbum);
        
        btnNext			.setOnClickListener(onNextClick);
        btnPlayPause	.setOnClickListener(onTogglePlayClick);
        btnPrevious		.setOnClickListener(onPreviousClick);
        btnFastForward	.setOnClickListener(onFastForwardClick);
        btnRewind		.setOnClickListener(onRewindClick);
        
        seekbar.setOnSeekBarChangeListener(onSeekChange);
        
        Log.d(">>>> THAI <<<<", "onCreateView() NOWPLAYING_FRAGMENT");
		return rootView;
	}

	
	

    private OnClickListener onTogglePlayClick = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			Control.sendControl(PlayService.ACTION_CONTROL_START_PAUSE, null);
			Control.isPlaying = !Control.isPlaying;
			if(Control.isPlaying)
				btnPlayPause.setImageResource( R.drawable.ic_action_pause);
			else
				btnPlayPause.setImageResource( R.drawable.ic_action_play);
		}
	};
  
	
    private OnClickListener onNextClick = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			
			Control.sendControl(PlayService.ACTION_CONTROL_NEXT, null);
			
			Fragment_Player_Sequence.intCurrentSong++;
			
			if (Fragment_Player_Sequence.intCurrentSong >= Fragment_Player_Sequence.intTotalSong) {
				Fragment_Player_Sequence.intCurrentSong = 0;
			}

			updateView(Fragment_Player_Sequence.intCurrentSong);
		}
	};
	
	
    private OnClickListener onPreviousClick = new OnClickListener() {
		
		@Override
		public void onClick(View v) {

			Control.sendControl(PlayService.ACTION_CONTROL_PREVIOUS, null);
			
			Fragment_Player_Sequence.intCurrentSong--;
			
			if (Fragment_Player_Sequence.intCurrentSong < 0) {
				Fragment_Player_Sequence.intCurrentSong = 0;
			}

			updateView(Fragment_Player_Sequence.intCurrentSong);
		}
	};
	
	
    private OnClickListener onRewindClick = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			Control.sendControl(PlayService.ACTION_CONTROL_REWIND, null);
		}
	};
	
    private OnClickListener onFastForwardClick = new OnClickListener() {
		
		@Override
		public void onClick(View v) {

			Control.sendControl(PlayService.ACTION_CONTROL_FAST_FORWARD, null);
		}
	};
	
	

//////////////////////////////////////////////////////////////////////////////
	
	@Override
	public void onPause() {
		registerReceiver(false);
		Log.d(">>>> THAI <<<<", "onPause() NOWPLAYING_FRAGMENT");
		super.onPause();
	}

	@Override
	public void onResume() {
		super.onResume();
		registerReceiver(true);
		Control.sendControl(PlayService.ACTION_REQUEST_POSITION, null);
		Log.d(">>>> THAI <<<<", "onResume() NOWPLAYING_FRAGMENT");
	}

//////////////////////////////////////////////////////////////////////////////
	
	public String getTitle(){
		return TITLE;
	}

//////////////////////////////////////////////////////////////////////////////	
	
	private BroadcastReceiver seekReceiver = new BroadcastReceiver() {
		
		@Override
		public void onReceive(Context context, Intent intent) {

			counter = intent.getIntExtra(PlayService.KEY_UPDATE_SEEK_POSITION,0);
			intSeekMax = intent.getIntExtra(PlayService.KEY_UPDATE_SEEK_DURATION, 0);
			
			seekbar.setMax(intSeekMax);
			seekbar.setProgress(counter);
			
			lblDuration.setText(Util.milliSecondsToTimer(intSeekMax));
			lblTimePlaying.setText(Util.milliSecondsToTimer(counter));

		}
	};
	
	private void registerReceiver(boolean reg){
		
		FragmentActivity activity = getActivity();
		
		if(reg){
			activity.registerReceiver(seekReceiver, new IntentFilter(PlayService.BROADCAST_SEEK));
			activity.registerReceiver(feedBackReceiver, new IntentFilter(PlayService.BROADCAST_FEEDBACK));
		}
		else {
			try{
				activity.unregisterReceiver(seekReceiver);
				activity.unregisterReceiver(feedBackReceiver);
			}
			catch(Exception e){
				e.printStackTrace();
				Toast.makeText(getActivity(), e.toString(), Toast.LENGTH_SHORT).show();
			}
		}
	}

	private OnSeekBarChangeListener onSeekChange = new OnSeekBarChangeListener() {
		
		@Override
		public void onStopTrackingTouch(SeekBar seekBar) {}
		
		@Override
		public void onStartTrackingTouch(SeekBar seekBar) {}
		
		@Override
		public void onProgressChanged(SeekBar seekBar, int progress,
				boolean fromUser) {
			
			if(fromUser){
				Control.sendControl(PlayService.ACTION_CONTROL_SEEK, seekbar.getProgress());
			}
		}
	};
	
    private BroadcastReceiver feedBackReceiver = new BroadcastReceiver() {
		
		@Override
		public void onReceive(Context context, Intent intent) {
			int action = intent.getIntExtra(PlayService.KEY_FEED_ACTION, 1);
			
			switch (action) {
			case PlayService.FEED_ACTION_SEND_POSITION:
				int position = intent.getIntExtra(PlayService.KEY_FEEDBACK, 0);
				Control.isPlaying = intent.getBooleanExtra(PlayService.KEY_FEEDBACK_ISPLAYING, false);
				updateView(position);
				break;
				
			default:
				break;
			}
			
			Log.d(">>>> THAI <<<<", "Feedback Receiver, ACTION = " + action );
		}
	};
	
	public  void updateView(int position){
		SongItem song;
		try{
			song = PlayService.playingSongList.get(position);
			lblAlbumArtist.setText(song.artist + " - " + song.album);
			lblTitle.setText(song.title);
		}catch(Exception e){}
		
		if(Control.isPlaying)
			btnPlayPause.setImageResource( R.drawable.ic_action_pause);
		else
			btnPlayPause.setImageResource( R.drawable.ic_action_play);
		
		Log.d(">>>> THAI <<<<", "UPDATE NOWPLAYING VIEW");
	}
	
}
