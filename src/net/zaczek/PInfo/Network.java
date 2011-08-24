package net.zaczek.PInfo;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;

public class Network extends ListActivity {
	private ConnectivityManager connMgr;
	
	private Handler mHandler = new Handler();
	private Runnable mUpdateTimeTask = new Runnable() {
		public void run() {
			updateConnectivity();
			mHandler.postDelayed(this, 5000);
		}
	};
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.network);
        
        connMgr = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        updateConnectivity();
        
        mHandler.postDelayed(mUpdateTimeTask, 5000);
    }
	
	private void updateConnectivity() {
		if (connMgr != null) {
		    NetworkInfo[] info = connMgr.getAllNetworkInfo();
		    if (info != null) {
		    	ListAdapter adapter = new ArrayAdapter<NetworkInfo>(this, 
		    			android.R.layout.simple_list_item_1, info);
		    	setListAdapter(adapter);
		    }
		}
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		switch (keyCode) {
		case KeyEvent.KEYCODE_DPAD_DOWN:
			startActivity(new Intent(this, Main.class));
			return true;
		case KeyEvent.KEYCODE_DPAD_UP:
			startActivity(new Intent(this, Gps.class));
			return true;
		default:
			return super.onKeyDown(keyCode, event);
		}
	}
}
