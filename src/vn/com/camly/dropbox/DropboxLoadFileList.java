package vn.com.camly.dropbox;

import java.util.ArrayList;
import java.util.List;

import vn.com.camly.musiccontrol.Control;
import vn.com.camly.musiccontrol.SongSQLite;
import vn.com.camly.musiccontrol.Local_Library.SongItem;
import vn.com.camly.musicplayer.Fragment_Dropbox;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.DropboxAPI.Entry;
import com.dropbox.client2.exception.DropboxException;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

public class DropboxLoadFileList extends AsyncTask<Void, Integer, Void>{

	private DropboxAPI<?> mApi;
	private String mPath;
	private Fragment_Dropbox fragment;
	private int lastIndex = 0,
				total = 0;
	private ArrayList<SongItem> listsong;
	private ProgressDialog progress;
	
    public DropboxLoadFileList(Fragment_Dropbox fragment_dropbox, DropboxAPI<?> api, String dropboxPath) {
    	fragment = fragment_dropbox;
        mApi = api;
        mPath = dropboxPath;
        listsong = new ArrayList<>();
        
        //sql.insertSong(song)
	}
    
    @Override
    protected void onPreExecute() {
    	super.onPreExecute();
    	fragment.adapter.clear();
    	progress = ProgressDialog.show(Control.context, "Searching...", "Searching for mp3 file in your Dropbox");
    }
    
	@Override
	protected Void doInBackground(Void... params) {
		
		SongSQLite sql = SongSQLite.getmInstance(fragment.getActivity());
        try{  	
            sql.removeALL(SongSQLite.TABLE_ONLINE_SONG);
            
			List<Entry> listDirent = mApi.search(mPath, ".mp3", 1000, false);
			
			total = listDirent.size();
			
            for (Entry ent: listDirent){//dirent.contents) {
                if (!ent.isDir) {
                	
                	SongItem song = new SongItem();
                	song.title = ent.fileName().substring(0, ent.fileName().length() - 4);
                	song.dataStream = ent.path;
                	song.server = SongSQLite.SERVER_DROPBOX;
                	
                	sql.insertSong(song, SongSQLite.TABLE_ONLINE_SONG);
                	listsong.add(song);
                	publishProgress(lastIndex);
                }
            }
            		
		} catch (DropboxException e) {
			e.printStackTrace();
		}
        
        Log.i(">>>> THAI <<<<", "doInBackground() DropboxLoadFileList: " + getStatus());
		return null;
	}
	
	@Override
	protected void onProgressUpdate(Integer... values) {
		super.onProgressUpdate(values);
		fragment.adapter.add(listsong.get(lastIndex));
		lastIndex++;
		progress.setMessage(lastIndex + "/" + total );
		Log.i(">>>> THAI <<<<", "onProgressUpdate() DropboxLoadFileList: " + lastIndex);
	}

	@Override
	protected void onPostExecute(Void result) {
		super.onPostExecute(result);
		for(int i = lastIndex; i < listsong.size(); i++){
			fragment.adapter.add(listsong.get(i));
		}
		progress.dismiss();
		Toast.makeText(Control.context, "Load song list from dropbox done! ", Toast.LENGTH_LONG).show();
		Log.i(">>>> THAI <<<<", "onPostExecute() DropboxLoadFileList");
	}
	
}
