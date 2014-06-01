package vn.com.camly.musicplayer;

import vn.com.camly.musiccontrol.Control;
import vn.com.camly.musiccontrol.PlayService;
import vn.com.camly.musiccontrol.Local_Library.SongItem;
import vn.com.camly.musicplayer.R;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

public class Fragment_Library extends Fragment{
	
	private ImageButton 
			btnPlayPause,
			btnPrevious,
			btnNext;	
	
	private TextView
			lblTitle,
			lblAlbumArtist;      
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
    	
        View rootView = inflater.inflate(R.layout.fragment_library, container, false);
        
        ViewPager mViewPage = (ViewPager)rootView.findViewById(R.id.Page);
        AdapterFrag_Library adap = new AdapterFrag_Library(getChildFragmentManager());       
        mViewPage.setAdapter(adap);
        
        btnPlayPause = (ImageButton)rootView.findViewById(R.id.btnPlay_pause);
        btnNext = (ImageButton)rootView.findViewById(R.id.btnNext);
        btnPrevious = (ImageButton)rootView.findViewById(R.id.btnPrevious);
        
		lblTitle = (TextView)rootView.findViewById(R.id.lblTitleLibrary);
		lblAlbumArtist = (TextView)rootView.findViewById(R.id.lblArtistAlbumLibrary);
        
        btnNext.setOnClickListener(onNextClick);
        btnPlayPause.setOnClickListener(onTogglePlayClick);
        btnPrevious.setOnClickListener(onPreviousClick);
      
        rootView.findViewById(R.id.controlLayout).setOnClickListener(onClickMiniPlayer);
        
		Log.d(">>>> THAI <<<<", "onCreateView(), Fragment_Library " );
        return rootView;
    }
    
    

	private OnClickListener onClickMiniPlayer = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			Control.selectFragment(0);
		}
	};
	
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
			
			Control.sendNext();
		}
	};
	
	
    private OnClickListener onPreviousClick = new OnClickListener() {
		
		@Override
		public void onClick(View v) {

			Control.sendPrevious();
		}
	};
	
//////////////////////////////////////////////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	@Override
	public void onResume() {
		super.onResume();
		registerReceiver(true);
        Control.sendControl(PlayService.ACTION_REQUEST_POSITION, null);
        
	};
	
	@Override
	public void onPause() {
		registerReceiver(false);
		super.onPause();
	}
	
	private void registerReceiver(boolean reg){
		
		FragmentActivity activity = getActivity();
		
		if(reg){
			activity.registerReceiver(feedBackReceiver, new IntentFilter(PlayService.BROADCAST_FEEDBACK));
		}
		else {
			try{
				activity.unregisterReceiver(feedBackReceiver);
			}
			catch(Exception e){
				e.printStackTrace();
				Toast.makeText(getActivity(), e.toString(), Toast.LENGTH_SHORT).show();
			}
		}
	}
	
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
