package vn.com.camly.musicplayer;

import java.util.ArrayList;
import java.util.List;

import vn.com.camly.musiccontrol.Control;
import vn.com.camly.musiccontrol.Local_Library;
import vn.com.camly.musiccontrol.PlayService;
import vn.com.camly.musiccontrol.Local_Library.SongItem;
import vn.com.camly.musicplayer.R;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.PopupMenu.OnMenuItemClickListener;

public class Fragment_Library_Songlist extends Fragment{
	
	public static final String TITLE = "ALL TRACK";
	
	private ListView listSong;	
	
	List<SongItem> songListItem;
	ArrayAdapter<SongItem> adapterSong = null;
	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		
		View rootView = inflater.inflate(R.layout.fragment_library_songlist, container, false);
		
		listSong = (ListView)rootView.findViewById(R.id.listSongLibrary);
		
		songListItem = Local_Library.getAllSong();

		if(songListItem == null) songListItem = new ArrayList<>();
		
		adapterSong = new ArrayAdapter<SongItem>(getActivity(), 
				android.R.layout.simple_list_item_1, 
				songListItem);
		
		listSong.setAdapter(adapterSong);
		listSong.setOnItemClickListener(onListSongSelect);
		
		Log.d(">>>> THAI <<<<", "onCreateView() SONG_LIST_FRAGMENT");
		return rootView;
	}
	
	private OnItemClickListener onListSongSelect = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, final int position,
				long id) {
			
			PopupMenu menu = new PopupMenu(getActivity(), view);
			menu.inflate(R.menu.popup);
			
			menu.setOnMenuItemClickListener(new OnMenuItemClickListener() {		
				@Override
				public boolean onMenuItemClick(MenuItem item) {
					
					if(item.getItemId() == R.id.popup_playall){
						Control.sendSelectList(PlayService.LIST_ALL_LOCAL , position);
						Control.selectFragment(0);
					}
					else{
						Control.sendAddItem(PlayService.LIST_ALL_LOCAL , position);
					}
					
					return true;
				}
			});
			menu.show();

		}
	};
	

	
}
