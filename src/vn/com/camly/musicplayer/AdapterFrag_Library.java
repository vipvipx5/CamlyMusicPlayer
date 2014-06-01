package vn.com.camly.musicplayer;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class AdapterFrag_Library extends FragmentPagerAdapter{

	private String titles[] = {
			Fragment_Library_Songlist.TITLE, 
			Fragment_Library_AlbumList.TITLE, 
			Fragment_Library_ArtistList.TITLE,
			Fragment_Library_Playlist.TITLE };
	
	private Fragment frags[] = new Fragment[titles.length];
	
	public AdapterFrag_Library(FragmentManager fm) {
		super(fm);
		frags[0] = new Fragment_Library_Songlist();
		frags[1] = new Fragment_Library_AlbumList();
		frags[2] = new Fragment_Library_ArtistList();
		frags[3] = new Fragment_Library_Playlist();
	}

	
	@Override
	public CharSequence getPageTitle(int position) {
		return titles[position];
	}

	@Override
	public Fragment getItem(int arg0) {
		return frags[arg0];
	}

	@Override
	public int getCount() {
		return titles.length;
	}

}
