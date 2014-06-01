package vn.com.camly.musicplayer;

import java.util.ArrayList;
import java.util.List;

import vn.com.camly.musiccontrol.Control;
import vn.com.camly.musiccontrol.Local_Library;
import vn.com.camly.musiccontrol.PlayService;
import vn.com.camly.musiccontrol.Local_Library.AlbumItem;
import vn.com.camly.musicplayer.R;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class Fragment_Library_AlbumList extends Fragment {

	public static final String TITLE = "ALBUM";
	
	private ListView listAlbum;	
		
	List<AlbumItem> albumListItem;
	ArrayAdapter<AlbumItem> adapterAlbum = null;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		
		View rootView = inflater.inflate(R.layout.fragment_library_songlist, container, false);
		
		listAlbum = (ListView)rootView.findViewById(R.id.listSongLibrary);
		try{
		albumListItem = Local_Library.getAllAlbum();
	}catch(Exception e){
		Log.d(">>>> THAI <<<<", "Local_Library.getAllSong()() False: " + e.toString());
	}
		if(albumListItem == null) albumListItem = new ArrayList<>();
		
		adapterAlbum = new ArrayAdapter<AlbumItem>(getActivity(), 
				android.R.layout.simple_list_item_1, 
				albumListItem);
		
		listAlbum.setAdapter(adapterAlbum);
		listAlbum.setOnItemClickListener(onListSongSelect);
		
		Log.d(">>>> THAI <<<<", "onCreateView() Fragment_Library_AlbumList");
		return rootView;
	}
	
	private OnItemClickListener onListSongSelect = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			
			Control.sendSelectSubList(PlayService.LIST_ALBUM, albumListItem.get(position).ID, 0);
			Control.selectFragment(0);
		}
	};
	

	
}
