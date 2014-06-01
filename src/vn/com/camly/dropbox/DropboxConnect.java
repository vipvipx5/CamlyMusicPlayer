package vn.com.camly.dropbox;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;
import android.widget.Toast;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.android.AndroidAuthSession;
import com.dropbox.client2.session.AppKeyPair;

public class DropboxConnect{

    final static private String APP_KEY = "6sr71u1psqnc00k";
    final static private String APP_SECRET = "ner49w3ck2gr4gd";

    final static public String ACCOUNT_PREFS_NAME = "prefsDropbox";
    final static public String KEY_ACCESS_TOKEN = "KEY_ACCESS_TOKEN";
	
    final static public String MUSIC_DIR = "";
    
    private DropboxAPI<AndroidAuthSession> mApi;
    private boolean mLoggedIn = false;
    private Context activity;
    
    
    
    public DropboxConnect(Context context) {
    	
    	this.activity = context;
    	
    	AppKeyPair keyPair = new AppKeyPair(APP_KEY, APP_SECRET);  	
    	AndroidAuthSession session = new AndroidAuthSession(keyPair);
    	try{
    		loadAccessToken(session); 
		}
		catch(Exception e){
			Log.e(">>>> THAI <<<<", "DropboxConnect(): " + e.toString());
		}
    	mApi = new DropboxAPI<AndroidAuthSession>(session); 		
    	
    	mLoggedIn = mApi.getSession().isLinked();
	}
    
    public DropboxAPI<?> getDropboxAPI(){
    	return mApi;
    }
    
    public AndroidAuthSession getSession(){
    	return mApi.getSession();
    }
       
    public boolean isLinked(){
    	return mLoggedIn;
    }
    
    public void unLink(){
    	mApi.getSession().unlink();
    	mLoggedIn = false;
    }
    
    public void saveAccessToken(){
    	AndroidAuthSession session = mApi.getSession();
    	
        if (session.authenticationSuccessful()) {
            try {
                // Mandatory call to complete the auth
                session.finishAuthentication();
                String oauth2AccessToken = session.getOAuth2AccessToken();
                
                if (oauth2AccessToken != null) {
                    SharedPreferences prefs = activity.getSharedPreferences(ACCOUNT_PREFS_NAME, 0);
                    Editor edit = prefs.edit();     
                    edit.putString(KEY_ACCESS_TOKEN, oauth2AccessToken);
                    edit.commit();
                    
                    mLoggedIn = session.isLinked();
                    
                    Log.i(">>>> THAI <<<<", "saveAccessToken(): " + oauth2AccessToken);
                    return;
                }
                
            } catch (IllegalStateException e) {
                Toast.makeText(activity, "Couldn't authenticate with Dropbox:" + e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                Log.i(">>>> THAI <<<<", "Error authenticating", e);
            }
        }
        
    }
    
    private void loadAccessToken(AndroidAuthSession session) {
    	
        SharedPreferences prefs = activity.getSharedPreferences(ACCOUNT_PREFS_NAME, 0);  
        String secret = prefs.getString(KEY_ACCESS_TOKEN, null);
        
        if(secret != null && secret.length() > 0)
        	session.setOAuth2AccessToken(secret);

    	Log.i(">>>> THAI <<<<", "loadAccessToken(): " + secret);
    }
    
    public void clearAccessToken(){
    	SharedPreferences prefs = activity.getSharedPreferences(ACCOUNT_PREFS_NAME, 0);
        Editor edit = prefs.edit();
        edit.clear();
        edit.commit();
    }
    
    
}
