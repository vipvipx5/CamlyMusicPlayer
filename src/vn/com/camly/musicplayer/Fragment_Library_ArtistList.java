package vn.com.camly.musicplayer;

import java.util.ArrayList;
import java.util.List;

import vn.com.camly.musiccontrol.Control;
import vn.com.camly.musiccontrol.Local_Library;
import vn.com.camly.musiccontrol.PlayService;
import vn.com.camly.musiccontrol.Local_Library.ArtistItem;
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

public class Fragment_Library_ArtistList extends Fragment {

	public static final String TITLE = "ARTIST";
	
	private ListView listArtist;	
	
	Local_Library songManage;	
	List<ArtistItem> artistListItem;
	ArrayAdapter<ArtistItem> adapterArtist = null;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		
		View rootView = inflater.inflate(R.layout.fragment_library_songlist, container, false);

		listArtist = (ListView)rootView.findViewById(R.id.listSongLibrary);
		
		artistListItem = Local_Library.getAllArtist();
		if(artistListItem == null) artistListItem = new ArrayList<>();
		
		adapterArtist = new ArrayAdapter<ArtistItem>(getActivity(), 
				android.R.layout.simple_list_item_1, 
				artistListItem);
		
		listArtist.setAdapter(adapterArtist);
		listArtist.setOnItemClickListener(onListSongSelect);

		Log.d(">>>> THAI <<<<", "onCreateView() Fragment_Library_ArtistList");
		return rootView;
	}
	
	private OnItemClickListener onListSongSelect = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			Control.sendSelectSubList(PlayService.LIST_ARTIST, artistListItem.get(position).ID, 0);
			Control.selectFragment(0);
		}
	};
	
}
