package net.zaczek.PInfo;

import java.util.ArrayList;
import java.util.List;

import android.app.ListActivity;
import android.content.Context;
import android.location.GpsSatellite;
import android.location.GpsStatus;
import android.location.GpsStatus.Listener;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.widget.ArrayAdapter;

public class Gps extends ListActivity implements LocationListener, Listener {

	private LocationManager locationManager;
	private GpsStatus status = null;
	private Location location = null;
	private GpsListAdapter adapter;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.gps);

		initGps();
		
	    adapter = new GpsListAdapter(this,
				android.R.layout.simple_list_item_1);
		setListAdapter(adapter);
	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	@Override
	protected void onResume() {
		super.onResume();
		adapter.updateGps();
	}

	private void initGps() {
		if (locationManager == null) {
			// Acquire a reference to the system Location Manager
			locationManager = (LocationManager) this
					.getSystemService(Context.LOCATION_SERVICE);

			locationManager.addGpsStatusListener(this);
			locationManager.requestLocationUpdates(
					LocationManager.GPS_PROVIDER, 0, 0, this);
		}
	}

	public void onGpsStatusChanged(int event) {
		status = locationManager.getGpsStatus(status);
		adapter.updateGps();
	}

	public void onLocationChanged(Location l) {
		location = l;
		adapter.updateGps();
	}

	public void onStatusChanged(String provider, int status, Bundle extras) {
	}

	public void onProviderEnabled(String provider) {
	}

	public void onProviderDisabled(String provider) {
	}
	
	private class GpsListAdapter extends ArrayAdapter<String> {
		
		List<String> list = new ArrayList<String>();
		private final int IDX_FIRST_SAT = 7;
		
		public GpsListAdapter(Context context, int textViewResourceId) {
			super(context, textViewResourceId);
			list.add("Lat: -");
			list.add("Lon: -");
			list.add("Accuracy: -");
			list.add("Altitude: -");
			list.add("Bearing: -");
			list.add("Speed: -");
			list.add("Time: -");
			list.add("----- Satellites -----");
		}
		
		@Override
		public String getItem(int position) {
			return list.get(position);
		}

		@Override
		public int getCount() {
			return list.size();
		}
		
		public void updateGps() {
			if(location != null) {
				list.set(0, String.format("Lat: %f", location.getLatitude()));
				list.set(1, String.format("Lon: %f", location.getLongitude()));
				list.set(2, String.format("Accuracy: %.2f m", location.getAccuracy()));
				list.set(3, String.format("Altitude: %.2f m", location.getAltitude()));
				list.set(4, String.format("Bearing: %.2f °", location.getBearing()));
				list.set(5, String.format("Speed: %.2f m/s", location.getSpeed()));
				list.set(6, String.format("Time: %c", location.getTime()));
			} else {
				list.set(0, "Lat: -");
				list.set(1, "Lon: -");
				list.set(2, "Accuracy: -");
				list.set(3, "Altitude: -");
				list.set(4, "Bearing: -");
				list.set(5, "Speed: -");
				list.set(6, "Time: -");
			}
			
			while(list.size() > IDX_FIRST_SAT + 1)
				list.remove(list.size() - 1);
			
			if (status != null) {
				for (GpsSatellite s : status.getSatellites()) {
					StringBuilder sb = new StringBuilder();
					sb.append("Prn: " + s.getPrn());
					sb.append("; InFix: " + s.usedInFix());
					sb.append(String.format("; Snr: %.2f", s.getSnr()));
					sb.append(String.format("; Elevation: %.2f °", s.getElevation()));
					sb.append(String.format("; Azimuth: %.2f °", s.getAzimuth()));
					list.add(sb.toString());
				}
			} 	
			
			notifyDataSetChanged();
		}
	}	
}
