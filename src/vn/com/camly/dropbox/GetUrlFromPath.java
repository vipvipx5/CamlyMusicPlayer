package vn.com.camly.dropbox;

import vn.com.camly.musiccontrol.PlayService;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.exception.DropboxException;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

public class GetUrlFromPath extends AsyncTask<String, Void, String>{

	DropboxAPI<?> dropboxApi;
	Context context;
	
	public GetUrlFromPath(DropboxAPI<?> dropboxApi, Context context){
		this.dropboxApi = dropboxApi;
		this.context = context;
	}
	
	@Override
	protected String doInBackground(String... params) {
		
		String url = null;
		try {
			url = dropboxApi.media(params[0], false).url;
		} catch (DropboxException e) {
			e.printStackTrace();
			Log.i(">>>> THAI <<<<", "ASYNTASK: Get media url from Dropbox: " + url +  e.toString());
		}
		
		return url;
	}

	@Override
	protected void onPostExecute(String result) {
		
		Intent controlIntent = new Intent(PlayService.BROADCAST_CONTROL);
		controlIntent.putExtra(PlayService.KEY_CONTROL_ACTION, PlayService.ACTION_GOT_URL);
		controlIntent.putExtra(PlayService.KEY_CONTROL_VALUE, result);
		
		context.sendBroadcast(controlIntent);
	}
	
}
