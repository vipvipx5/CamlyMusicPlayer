package vn.com.camly.musicplayer;

import java.util.ArrayList;
import java.util.List;


import vn.com.camly.musiccontrol.Local_Library;
import vn.com.camly.musiccontrol.Local_Library.PlaylistItem;
import vn.com.camly.musicplayer.R;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

public class Fragment_Library_Playlist extends Fragment {

	public static final String TITLE = "PLAYLIST";
	
	private ListView listPlayList;	
	
	Local_Library songManage;	
	List<PlaylistItem> playListItem;
	ArrayAdapter<PlaylistItem> adapterPlayList = null;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		
		View rootView = inflater.inflate(R.layout.fragment_library_songlist, container, false);
		
		listPlayList = (ListView)rootView.findViewById(R.id.listSongLibrary);
		
		playListItem = Local_Library.getAllPlayList();
		if(playListItem == null) playListItem = new ArrayList<>();
		
		adapterPlayList = new ArrayAdapter<PlaylistItem>(getActivity(), 
				android.R.layout.simple_list_item_1, 
				playListItem);
		
		listPlayList.setAdapter(adapterPlayList);
		listPlayList.setOnItemClickListener(onListSongSelect);
		
		Log.d(">>>> THAI <<<<", "onCreateView() Fragment_Library_ArtistList");
		return rootView;
	}
	
	private OnItemClickListener onListSongSelect = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			
		}
	};
}
