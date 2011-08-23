package net.zaczek.PInfo;

import java.io.IOException;
import java.io.RandomAccessFile;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.location.GpsSatellite;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.widget.TextView;

public class Main extends Activity {
	// GPS Status
	private LocationManager locationManager;
	private GpsStatus status = null;

	private TextView txtGPS;
	private TextView txtCPU;
	private TextView txtMem;

	private Handler mHandler = new Handler();
	private ActivityManager aMgr;

	private Runnable mUpdateTimeTask = new Runnable() {
		public void run() {
			updateCpu();
			updateMem();
			mHandler.postDelayed(this, 1000);
		}
	};

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		txtGPS = (TextView) findViewById(R.id.txtGPS);
		txtCPU = (TextView) findViewById(R.id.txtCPU);
		txtMem = (TextView) findViewById(R.id.txtMem);

		aMgr = (ActivityManager) this.getSystemService(ACTIVITY_SERVICE);

		initGps();
		updateGps();
		updateCpu();
		updateMem();

		mHandler.postDelayed(mUpdateTimeTask, 1000);
	}

	private void updateMem() {
		ActivityManager.MemoryInfo info = new ActivityManager.MemoryInfo();
		aMgr.getMemoryInfo(info);
		txtMem.setText(String.format("%.2f MB available mem", info.availMem / 1024.0 / 1024.0));
	}

	private void updateCpu() {
		txtCPU.setText(String.format("%.0f %% CPU", readCpuUsage() * 100.0));
	}
	
	// http://stackoverflow.com/questions/3118234/how-to-get-memory-usage-and-cpu-usage-in-android
	private float readCpuUsage() {
	    try {
	        RandomAccessFile reader = new RandomAccessFile("/proc/stat", "r");
	        String load = reader.readLine();

	        String[] toks = load.split(" ");

	        long idle1 = Long.parseLong(toks[5]);
	        long cpu1 = Long.parseLong(toks[2]) + Long.parseLong(toks[3]) + Long.parseLong(toks[4])
	              + Long.parseLong(toks[6]) + Long.parseLong(toks[7]) + Long.parseLong(toks[8]);

	        try {
	            Thread.sleep(360);
	        } catch (Exception e) {}

	        reader.seek(0);
	        load = reader.readLine();
	        reader.close();

	        toks = load.split(" ");

	        long idle2 = Long.parseLong(toks[5]);
	        long cpu2 = Long.parseLong(toks[2]) + Long.parseLong(toks[3]) + Long.parseLong(toks[4])
	            + Long.parseLong(toks[6]) + Long.parseLong(toks[7]) + Long.parseLong(toks[8]);

	        return (float)(cpu2 - cpu1) / ((cpu2 + idle2) - (cpu1 + idle1));

	    } catch (IOException ex) {
	        ex.printStackTrace();
	    }

	    return 0;
	} 

	private void initGps() {
		if (locationManager == null) {
			// Acquire a reference to the system Location Manager
			locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

			// Register the listener with the Location Manager to receive
			// location updates
			locationManager.addGpsStatusListener(new GpsStatus.Listener() {
				@Override
				public void onGpsStatusChanged(int event) {
					status = locationManager.getGpsStatus(status);
					updateGps();
				}
			});
		}
	}

	private void updateGps() {
		if (status != null) {
			int n = 0;
			for (GpsSatellite s : status.getSatellites()) {
				n++;
			}
			final int max = status.getMaxSatellites();
			txtGPS.setText(n + "/" + max + " Satellites");
		} else {
			txtGPS.setText("no GPS Status");
		}
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		switch (keyCode) {
		case KeyEvent.KEYCODE_DPAD_DOWN:
			startActivity(new Intent(this, Gps.class));
			return true;
		case KeyEvent.KEYCODE_DPAD_UP:
			startActivity(new Intent(this, Gps.class));
			return true;
		default:
			return super.onKeyDown(keyCode, event);
		}
	}
}