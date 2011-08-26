package net.zaczek.PInfo;

import android.app.ListActivity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;

public class Network extends ListActivity {
	private ConnectivityManager connMgr;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.network);
        
        connMgr = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        updateConnectivity();
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
}
