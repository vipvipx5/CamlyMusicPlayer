package vn.com.camly.musicplayer;

import vn.com.camly.musiccontrol.Control;
import vn.com.camly.musiccontrol.PlayService;
import vn.com.camly.musiccontrol.Local_Library.SongItem;
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

public class Fragment_Player_Sequence extends Fragment {

	public static final String TITLE = "SEQUENCE";
	
	ListView listSequence;
	
	//public static List<SongItem> playingSongList;
	
	ArrayAdapter<SongItem> adapterSong = null;
	
	public static int 	
			intCurrentSong = 0,
			intTotalSong = 0;
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		intTotalSong = PlayService.playingSongList.size();

		Log.d(">>>> THAI <<<<", "onCreate() Fragment_Player_Sequence");
	}
	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		View rootView = inflater.inflate(R.layout.fragment_player_sequence, container, false);
			
		listSequence = (ListView)rootView.findViewById(R.id.listPlayer);
	
		adapterSong = new ArrayAdapter<SongItem>(getActivity(), 
				android.R.layout.simple_list_item_1, 
				PlayService.playingSongList);
		
		listSequence.setAdapter(adapterSong);
		listSequence.setOnItemClickListener(onListSongSelect);
		
		Log.d(">>>> THAI <<<<", "onCreateView() Fragment_Player_Sequence");
		return rootView;
	}
	
	private OnItemClickListener onListSongSelect = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			
			intCurrentSong = position;
			Control.sendControl(PlayService.ACTION_CONTROL_SELECT_SONG, position);
		}
	};
	
	
	
	@Override
	public void onDestroy() {
		super.onDestroy();;
		Log.d(">>>> THAI <<<<", "onDestroy() Fragment_Player_Sequence");
	};
}
