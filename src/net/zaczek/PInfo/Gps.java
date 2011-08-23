package net.zaczek.PInfo;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.location.GpsSatellite;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.TextView;

public class Gps extends Activity {

	private LocationManager locationManager;
	private GpsStatus status = null;
	private Location location = null;

	private TextView txtStatus;
	private TextView txtSpeed;
	private TextView txtAccuracy;
	private TextView txtAltitude;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.gps);

		txtStatus = (TextView) findViewById(R.id.txtStatus);
		txtSpeed = (TextView) findViewById(R.id.txtSpeed);
		txtAccuracy = (TextView) findViewById(R.id.txtAccuracy);
		txtAltitude = (TextView) findViewById(R.id.txtAltitude);

		initGps();
		updateGps();
	}
	
	private void initGps() {
		if (locationManager == null) {
			// Acquire a reference to the system Location Manager
			locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

			locationManager.addGpsStatusListener(new GpsStatus.Listener() {
				@Override
				public void onGpsStatusChanged(int event) {
					status = locationManager.getGpsStatus(status);
					updateGps();
				}
			});
			
			locationManager.requestLocationUpdates(
					LocationManager.GPS_PROVIDER, 0, 0, new LocationListener() {
						public void onLocationChanged(Location l) {
							// Called when a new location is found by the network
							// location provider.
							updateGps();
						}

						public void onStatusChanged(String provider, int status,
								Bundle extras) {
						}

						public void onProviderEnabled(String provider) {
						}

						public void onProviderDisabled(String provider) {
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
			txtStatus.setText(n + "/" + max + " Satellites");
		} else {
			txtStatus.setText("no GPS Status");
		}
		
		if(location != null) {
			txtSpeed.setText(String.format("%f.0 km/h", location.getSpeed() / 3.6));
			txtAccuracy.setText(String.format("%f.2 m", location.getAccuracy()));
			txtAltitude.setText(String.format("%f.2 m", location.getAltitude()));
		} else {
			txtSpeed.setText("? km/h");
			txtAccuracy.setText("? m");
			txtAltitude.setText("? m");			
		}
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		switch (keyCode) {
		case KeyEvent.KEYCODE_DPAD_DOWN:
			startActivity(new Intent(this, Main.class));
			return true;
		case KeyEvent.KEYCODE_DPAD_UP:
			startActivity(new Intent(this, Main.class));
			return true;
		default:
			return super.onKeyDown(keyCode, event);
		}
	}
}
