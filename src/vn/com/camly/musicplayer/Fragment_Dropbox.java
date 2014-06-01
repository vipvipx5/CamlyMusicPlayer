package vn.com.camly.musicplayer;

import java.util.ArrayList;

import vn.com.camly.dropbox.DropboxConnect;
import vn.com.camly.dropbox.DropboxLoadFileList;
import vn.com.camly.musiccontrol.Control;
import vn.com.camly.musiccontrol.PlayService;
import vn.com.camly.musiccontrol.SongSQLite;
import vn.com.camly.musiccontrol.Local_Library.SongItem;
import vn.com.camly.musicplayer.R;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.PopupMenu.OnMenuItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.PopupMenu;

public class Fragment_Dropbox extends Fragment{

	private Button 	btnLinkToDropbox,
					btnLoadList;
	
	private DropboxConnect dropboxConnect;
	private ListView listDropbox;
	private ArrayList<SongItem> songList;
	public ArrayAdapter<SongItem> adapter = null;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		dropboxConnect = new DropboxConnect(getActivity());
		
		SongSQLite sql = new SongSQLite(getActivity());
		songList = sql.getAllSong(SongSQLite.TABLE_ONLINE_SONG, null);		
		sql.close();
		
		if (songList == null) songList = new ArrayList<>();
				
		Log.d(">>>> THAI <<<<", "onCreate() Fragment_Dropbox");
	}
	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_dropbox, container, false);
		
		btnLinkToDropbox= (Button)rootView.findViewById(R.id.btnLinkToDropbox);
		btnLoadList 	= (Button)rootView.findViewById(R.id.btnLoadDropboxList);
		
		listDropbox 	= (ListView)rootView.findViewById(R.id.ListDropbox);
		
		adapter = new ArrayAdapter<SongItem>(getActivity(), 
				android.R.layout.simple_list_item_1, 
				songList);
		listDropbox.setAdapter(adapter);
		listDropbox.setOnItemClickListener(onDroptItempClick);
		
		btnLinkToDropbox.setOnClickListener(onClickLinktoDropbox);	
		btnLoadList.setOnClickListener(onClickLoadListFile);

		setLinked(dropboxConnect.isLinked());
		
		Log.d(">>>> THAI <<<<", "onCreateView() Fragment_Dropbox");
		return rootView;
	}
	
	
	@Override
	public void onResume() {
		super.onResume();
		dropboxConnect.saveAccessToken();
		setLinked(dropboxConnect.isLinked());
		Control.sendRefreshDropbox();
		
		Log.d(">>>> THAI <<<<", "onResume() Fragment_Dropbox");
	}
	
	OnClickListener onClickLinktoDropbox = new OnClickListener() {	
		@Override
		public void onClick(View v) {
			if(dropboxConnect.isLinked()){
				
				dropboxConnect.unLink();			
				dropboxConnect.clearAccessToken();
				btnLinkToDropbox.setText("Link to DropBox");
			}
			else
				dropboxConnect.getSession().startOAuth2Authentication(getActivity());

		}
	};
	
	
	OnClickListener onClickLoadListFile = new OnClickListener() {	
		@Override
		public void onClick(View v) {
			if(dropboxConnect.isLinked()){
				DropboxLoadFileList loadFileList = new DropboxLoadFileList(
												Fragment_Dropbox.this, 
												dropboxConnect.getDropboxAPI(), 
												DropboxConnect.MUSIC_DIR);		
				loadFileList.execute();	
			}
			else {
				dropboxConnect.getSession().startOAuth2Authentication(getActivity());
			}
			
			Log.d(">>>> THAI <<<<", "DropboxLoadFileList()");
		}
	};
	
	
	private void setLinked(boolean linked){
		if(linked){
			btnLinkToDropbox.setText("UnLink to Dropbox");
		}
		else
			btnLinkToDropbox.setText("Link to Dropbox");
	}
	
	private OnItemClickListener onDroptItempClick = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, final int position,
				long id) {
			
			PopupMenu menu = new PopupMenu(getActivity(), view);
			menu.inflate(R.menu.popup);
			
			menu.setOnMenuItemClickListener(new OnMenuItemClickListener() {		
				@Override
				public boolean onMenuItemClick(MenuItem item) {
					
					if(item.getItemId() == R.id.popup_playall){
						Control.sendSelectList(PlayService.LIST_ALL_DROPBOX , position);
						Control.selectFragment(0);
					}
					else{
						Control.sendAddItem(PlayService.LIST_ALL_DROPBOX , position);
					}
					
					return true;
				}
			});
			menu.show();
			
		}
	};
	
}
