package net.zaczek.PInfo;

import android.app.Activity;
import android.content.Context;
import android.location.GpsSatellite;
import android.location.GpsStatus;
import android.location.GpsStatus.Listener;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.widget.TextView;

public class Gps extends Activity implements LocationListener, Listener {

	private LocationManager locationManager;
	private GpsStatus status = null;
	private Location location = null;
	
	private WakeLock wl;

	private TextView txtStatus;
	private TextView txtSpeed;
	private TextView txtAccuracy;
	private TextView txtAltitude;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.gps);
		
		PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
		wl = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK, "ViewGpsAndStayAwake");

		txtStatus = (TextView) findViewById(R.id.txtStatus);
		txtSpeed = (TextView) findViewById(R.id.txtSpeed);
		txtAccuracy = (TextView) findViewById(R.id.txtAccuracy);
		txtAltitude = (TextView) findViewById(R.id.txtAltitude);

		initGps();
	}
	
	@Override
	protected void onPause() {
		wl.release();
		super.onPause();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		wl.acquire();
		updateGps();
	}

	private void initGps() {
		if (locationManager == null) {
			// Acquire a reference to the system Location Manager
			locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

			locationManager.addGpsStatusListener(this);
			locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
		}
	}

	public void onGpsStatusChanged(int event) {
		status = locationManager.getGpsStatus(status);
		updateGps();
	}

	public void onLocationChanged(Location l) {
		location = l;
		updateGps();
	}

	public void onStatusChanged(String provider, int status, Bundle extras) {
	}

	public void onProviderEnabled(String provider) {
	}

	public void onProviderDisabled(String provider) {
	}

	private void updateGps() {
		if (status != null) {
			int n = 0;
			int max = 0;
			for (GpsSatellite s : status.getSatellites()) {
				max++;
				if (s.usedInFix()) {
					n++;
				}
			}
			txtStatus.setText(n + "/" + max + " Satellites");
		} else {
			txtStatus.setText("no GPS Status");
		}

		if (location != null) {
			txtSpeed.setText(String.format("%.0f km/h", location.getSpeed() * 3.6));
			txtAccuracy.setText(String.format("%.2f m", location.getAccuracy()));
			txtAltitude.setText(String.format("%.2f m", location.getAltitude()));
		} else {
			txtSpeed.setText("? km/h");
			txtAccuracy.setText("? m");
			txtAltitude.setText("? m");
		}
	}
}
