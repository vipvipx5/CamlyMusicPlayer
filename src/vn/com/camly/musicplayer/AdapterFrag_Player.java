package vn.com.camly.musicplayer;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class AdapterFrag_Player extends FragmentPagerAdapter{

	private String titles[] = {
			Fragment_Player_Nowplaying.TITLE, 
			Fragment_Player_Sequence.TITLE
	};
	
	private Fragment frag[] = new Fragment[titles.length];
	
	public AdapterFrag_Player(	FragmentManager fm) {
		super(fm);
		
		frag[0] = new Fragment_Player_Nowplaying();
		frag[1] = new Fragment_Player_Sequence();
	}

	@Override
	public Fragment getItem(int arg0) {
		return frag[arg0];
	}

	@Override
	public int getCount() {
		return frag.length;
	}

	@Override
	public CharSequence getPageTitle(int position) {
		return titles[position];
	}

}
