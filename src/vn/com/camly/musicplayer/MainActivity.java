package vn.com.camly.musicplayer;

import vn.com.camly.musiccontrol.Control;
import vn.com.camly.musiccontrol.PlayService;
import vn.com.camly.musicplayer.R;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class MainActivity extends FragmentActivity {
	
	boolean playing = false;
	
	private Intent 	serviceIntent;				
	
    private DrawerLayout mDrawerLayout;
    
    private ActionBarDrawerToggle mDrawerToggle;

    private String[] mPlanetTitles;
    
    private ProgressDialog buffdialog = null;
    
/////////////////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////////
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);	
		
		Control.context = this;
		
        mPlanetTitles = getResources().getStringArray(R.array.planets_array);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        Control.mDrawerList = (ListView) findViewById(R.id.left_drawer);

        Control.mDrawerList.setAdapter(new ArrayAdapter<String>(this,
                R.layout.fragment_drawer, mPlanetTitles));
        
        Control.mDrawerList.setOnItemClickListener(new DrawerItemClickListener());
        
		serviceIntent = new Intent(this, PlayService.class);
		
		mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, 50);
		
        mDrawerToggle = new ActionBarDrawerToggle(
                this,                  /* host Activity */
                mDrawerLayout,         /* DrawerLayout object */
                R.drawable.ic_drawer,  /* nav drawer image to replace 'Up' caret */
                R.string.drawer_open,  /* "open drawer" description for accessibility */
                R.string.drawer_close  /* "close drawer" description for accessibility */
                ){
        	//Override listener 
        };
        
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        if (savedInstanceState == null) {
           Control.selectFragment(0);
        }
        startService(serviceIntent); 
        
        Log.d(">>>> THAI <<<<", "onCreat() MAIN_ACTIVITY");
	}
	
	
    
/////////////////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////////

	@Override
	protected void onStart() {
		Log.d(">>>> THAI <<<<", "onStart() MAIN_ACTIVITY");
		super.onStart();
	}

	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		Log.d(">>>> THAI <<<<", "onCreateOptionsMenu() MAIN_ACTIVITY");
		return true;
	}
	
	

	@Override
	protected void onDestroy() {
		Log.d(">>>> THAI <<<<", "onDestroy() MAIN_ACTIVITY");
		super.onDestroy();
	}



	@Override
	protected void onStop() {
		Log.d(">>>> THAI <<<<", "onStop() MAIN_ACTIVITY");
		super.onStop();
	}

    
/////////////////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////////



	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
    
/////////////////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////////

    /* The click listner for ListView in the navigation drawer */
    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        	
            Control.selectFragment(position);
            
            //setTitle(mPlanetTitles[position]);
            mDrawerLayout.closeDrawer(Control.mDrawerList);
        }
    }


    
/////////////////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////////

    @Override
	protected void onPause() {
    	Log.d(">>>> THAI <<<<", "onPause() MAIN_ACTIVITY");
    	unregisterReceiver(feedBackReceiver);
		super.onPause();
	}

	@Override
	protected void onResume() {
		Log.d(">>>> THAI <<<<", "onResume() MAIN_ACTIVITY");
		super.onResume();
		registerReceiver(feedBackReceiver, new IntentFilter(PlayService.BROADCAST_FEEDBACK));
	}
    
/////////////////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////////
    
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
        Log.d(">>>> THAI <<<<", "onPostCreate() MAIN_ACTIVITY");
    }

	@Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggls
        mDrawerToggle.onConfigurationChanged(newConfig);
        Log.d(">>>> THAI <<<<", "onConfigurationChanged() MAIN_ACTIVITY");
    }
    
	
	
    private BroadcastReceiver feedBackReceiver = new BroadcastReceiver() {
		
		@Override
		public void onReceive(Context context, Intent intent) {
			int action = intent.getIntExtra(PlayService.KEY_FEED_ACTION, 1);
			
			switch (action) {
			case PlayService.FEED_ACTION_BUFFER:
				int bufered = intent.getIntExtra(PlayService.KEY_FEEDBACK, 0);
				
				if(bufered == 0){
					buffdialog = ProgressDialog.show(MainActivity.this, "Buffering...", "Please wait while acquiring song...");
				}
				else{
					if(buffdialog != null)
						buffdialog.dismiss();
				}
				
				break;
				
			default:
				break;
			}
			
			Log.d(">>>> THAI <<<<", "Feedback Receiver, ACTION = " + action );
		}
	};
	
	
}
