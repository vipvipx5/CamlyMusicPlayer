package vn.com.camly.musicplayer;

import vn.com.camly.musicplayer.R;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class Fragment_Player extends Fragment{
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		View rootView = inflater.inflate(R.layout.fragment_player, container, false);
		
		ViewPager mViewPage = (ViewPager)rootView.findViewById(R.id.PageNowplaying);
		AdapterFrag_Player mAdapter = new AdapterFrag_Player(getChildFragmentManager());
		mViewPage.setAdapter(mAdapter);
		
		Log.d(">>>> THAI <<<<", "onCreateView() Fragment_Player");
		return rootView;
	}
	
}
