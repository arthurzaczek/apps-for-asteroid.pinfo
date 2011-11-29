package net.zaczek.PInfo;

import java.io.IOException;
import java.io.RandomAccessFile;
import android.R.layout;
import android.app.ActivityManager;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.location.GpsSatellite;
import android.location.GpsStatus;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class Main extends ListActivity {
	// GPS Status
	private LocationManager locationManager;
	private GpsStatus gpsStatus = null;

	private ActivityManager aMgr;
	private ConnectivityManager connMgr;
	private MainListAdapter adapter;
	
	private static final int POS_CPU = 0;
	private static final int POS_MEM = 1;
	private static final int POS_GPS = 2;
	private static final int POS_NETWORK = 3;
	private static final int POS_MAX = 4;
	
	private static final int MENU_ABOUT = 1;
	private static final int MENU_EXIT = 2;

	private class MainListAdapter extends ArrayAdapter<Object> {

		private Handler mHandler = new Handler();
		private Runnable mUpdateTimeTask = new Runnable() {
			public void run() {
				updateCpu();
				updateMem();
				updateConnectivity();
				adapter.notifyDataSetChanged();
				mHandler.postDelayed(this, 1000);
			}
		};
		private String gps;
		private String cpu;
		private String mem;
		private String network;

		public MainListAdapter(Context context, int textViewResourceId) {
			super(context, textViewResourceId);

			initGps();
			updateGps();
			updateCpu();
			updateMem();
			updateConnectivity();

			mHandler.postDelayed(mUpdateTimeTask, 1000);
		}

		@Override
		public Object getItem(int position) {
			switch (position) {
			case POS_GPS:
				return gps;
			case POS_CPU:
				return cpu;
			case POS_MEM:
				return mem;
			case POS_NETWORK:
				return network;
			default:
				return null;
			}
		}

		@Override
		public int getCount() {
			return POS_MAX;
		}

		private void updateConnectivity() {
			String result = "Network unavailable";
			if (connMgr != null) {
				NetworkInfo[] info = connMgr.getAllNetworkInfo();
				if (info != null) {
					int connected = 0;
					for (int i = 0; i < info.length; i++) {						
						if (info[i].getState() == NetworkInfo.State.CONNECTED) {
							// Connected to internet
							connected++;
						}
					}
					result = String.format("%d/%d networks connected", connected, info.length);
				}
			}

			network = result;
		}

		private void updateMem() {
			ActivityManager.MemoryInfo info = new ActivityManager.MemoryInfo();
			aMgr.getMemoryInfo(info);
			mem = String.format("%.2f MB available mem", info.availMem / 1024.0 / 1024.0);
		}

		private void updateCpu() {
			cpu = String.format("%.0f %% CPU", readCpuUsage() * 100.0);
		}

		// http://stackoverflow.com/questions/3118234/how-to-get-memory-usage-and-cpu-usage-in-android
		private float readCpuUsage() {
			try {
				RandomAccessFile reader = new RandomAccessFile("/proc/stat", "r");
				String load = reader.readLine();

				String[] toks = load.split(" ");

				long idle1 = Long.parseLong(toks[5]);
				long cpu1 = Long.parseLong(toks[2]) + Long.parseLong(toks[3]) + Long.parseLong(toks[4]) + Long.parseLong(toks[6]) + Long.parseLong(toks[7]) + Long.parseLong(toks[8]);

				try {
					Thread.sleep(360);
				} catch (Exception e) {
				}

				reader.seek(0);
				load = reader.readLine();
				reader.close();

				toks = load.split(" ");

				long idle2 = Long.parseLong(toks[5]);
				long cpu2 = Long.parseLong(toks[2]) + Long.parseLong(toks[3]) + Long.parseLong(toks[4]) + Long.parseLong(toks[6]) + Long.parseLong(toks[7]) + Long.parseLong(toks[8]);

				return (float) (cpu2 - cpu1) / ((cpu2 + idle2) - (cpu1 + idle1));

			} catch (IOException ex) {
				ex.printStackTrace();
			}

			return 0;
		}

		private void initGps() {
			if (locationManager == null) {
				// Acquire a reference to the system Location Manager
				locationManager = (LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE);

				locationManager.addGpsStatusListener(new GpsStatus.Listener() {
					@Override
					public void onGpsStatusChanged(int event) {
						gpsStatus = locationManager.getGpsStatus(gpsStatus);
						updateGps();
					}
				});
			}
		}

		private void updateGps() {
			if (gpsStatus != null) {
				int n = 0;
				int max = 0;
				for (GpsSatellite s : gpsStatus.getSatellites()) {
					max++;
					if (s.usedInFix()) {
						n++;
					}
				}
				gps = n + "/" + max + " Satellites";
			} else {
				gps = "no GPS Status";
			}
		}
	}

	/** Called when the activity is first created. */
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		aMgr = (ActivityManager) this.getSystemService(ACTIVITY_SERVICE);
		connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

		adapter = new MainListAdapter(this, layout.simple_list_item_1);
		setListAdapter(adapter);
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		switch (position) {
		case POS_GPS:
			startActivity(new Intent(this, Gps.class));
			break;
		case POS_NETWORK:
			startActivity(new Intent(this, Network.class));
			break;
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		menu.add(0, MENU_ABOUT, 0, "About");
		menu.add(1, MENU_EXIT, 0, "Exit");
		return true;
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		int itemId = item.getItemId();
		switch (itemId) {
		case MENU_ABOUT:
			startActivity(new Intent(this, About.class));
			return true;
		case MENU_EXIT:
			finish();
			return true;
		}

		return super.onMenuItemSelected(featureId, item);
	}
}