package vn.com.camly.musiccontrol;

import vn.com.camly.musicplayer.Fragment_Dropbox;
import vn.com.camly.musicplayer.Fragment_Library;
import vn.com.camly.musicplayer.Fragment_Player;
import vn.com.camly.musicplayer.R;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.widget.ListView;

public class Control {
	
	public static Intent controlIntent = new Intent(PlayService.BROADCAST_CONTROL);
	
	public static Context context;	 
	
	public static boolean isPlaying = false;
	
	public static FragmentManager fragmentManager;
	private static int selectedFragment = -1;
	public static ListView mDrawerList;
	
	
	public static void sendControl(int action, Integer value){
		
		controlIntent.putExtra(PlayService.KEY_CONTROL_ACTION, action);
		
		if(value != null)
			controlIntent.putExtra(PlayService.KEY_CONTROL_VALUE, value.intValue());
		
		context.sendBroadcast(controlIntent);
	}
	
	
	
	public static void sendNext(){
		sendControl(PlayService.ACTION_CONTROL_NEXT, null);
	}
	
	
	public static void sendPrevious(){
		sendControl(PlayService.ACTION_CONTROL_PREVIOUS, null);
	}
	
	
	public static void sendTogglePlay(){
		sendControl(PlayService.ACTION_CONTROL_START_PAUSE, null);
	}
	
	
	public static void sendSelectList(int List, int position) {
		controlIntent.putExtra(PlayService.KEY_CONTROL_ACTION, PlayService.ACTION_CONTROL_SELECT_LIST);
		controlIntent.putExtra(PlayService.KEY_CONTROL_VALUE, List);
		controlIntent.putExtra(PlayService.KEY_SELECT_LIST_POSITION, position);
		context.sendBroadcast(controlIntent);
	}
	
	
	public static void sendSelectSubList(int List, long sublistID, int position) {
		controlIntent.putExtra(PlayService.KEY_CONTROL_ACTION, PlayService.ACTION_CONTROL_SELECT_LIST);
		controlIntent.putExtra(PlayService.KEY_CONTROL_VALUE, List);
		controlIntent.putExtra(PlayService.KEY_SELECT_SUB_LIST, sublistID);
		controlIntent.putExtra(PlayService.KEY_SELECT_LIST_POSITION, position);
		context.sendBroadcast(controlIntent);
	}
	
	public static void sendAddItem(int List, int position) {
		controlIntent.putExtra(PlayService.KEY_CONTROL_ACTION, PlayService.ACTION_CONTROL_ADD_ITEM);
		controlIntent.putExtra(PlayService.KEY_CONTROL_VALUE, List);
		controlIntent.putExtra(PlayService.KEY_SELECT_LIST_POSITION, position);
		context.sendBroadcast(controlIntent);
	}
	
    public static void selectFragment(int position) {
    	
        // update the main content by replacing fragments
    	Fragment fragment;
    	
    	if(position != selectedFragment){
	    	if(position == 0){	
	    		fragment = new Fragment_Player();
	    	}
	    	else if(position == 1)
	    		fragment = new Fragment_Library();	
	    	else
	    		fragment = new Fragment_Dropbox();
	    	
	    	if(fragmentManager == null)
	    		fragmentManager = ((FragmentActivity)context).getSupportFragmentManager();
	               
	    	fragmentManager.beginTransaction()	.setCustomAnimations(	R.anim.push_up_in, 
														    			R.anim.push_up_out)
	    										.replace(R.id.content_frame, fragment)
	    										.commit();
	        selectedFragment = position;
	        // update selected item and title, then close the drawer
	        mDrawerList.setItemChecked(position, true);
    	}
        Log.d(">>>> THAI <<<<", "selectItem(): " + position);
    }
    
    
    
    public static void sendRefreshDropbox(){
		controlIntent.putExtra(PlayService.KEY_CONTROL_ACTION, PlayService.ACTION_REFRESH_DROPBOX_API);
		context.sendBroadcast(controlIntent);
    }
    
}
