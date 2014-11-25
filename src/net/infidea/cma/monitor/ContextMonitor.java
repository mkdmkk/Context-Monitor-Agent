package net.infidea.cma.monitor;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;

import android.content.Context;
import android.graphics.Typeface;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;
import net.infidea.cma.ContextInfoUpdater;
import net.infidea.cma.ContextPack;
import net.infidea.cma.R;
import net.infidea.cma.communicator.ContextTransmitter;
import net.infidea.cma.constant.ContextType;
import net.infidea.cma.setting.SettingActivity;

public class ContextMonitor implements SensorEventListener, LocationListener {
	private Context context = null;
	private ContextInfoUpdater contextInfoUpdater = null;
	private ContextTransmitter contextTransmitter = null;
	
	private int contextTypeDisplayed = 0;
	private boolean isStaticContextInfoSet = false;
	
	// Sensor manager
	private SensorManager sensorManager = null;
	private List<Sensor> availableSensors = null;
	private ContextPack contextPack = null;
	
	// Location manager
	private LocationManager locationManager = null;
	
	public ContextMonitor(Context context, ContextInfoUpdater contextInfoUpdater) {
		this.context = context;
		this.contextInfoUpdater = contextInfoUpdater;
		
		// Init sensor manager
		sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
		availableSensors = sensorManager.getSensorList(Sensor.TYPE_ALL);
		
		// Init location manager
		locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
	}
	
	public ArrayList<ViewGroup> start() {
		ArrayList<ViewGroup> contextList = new ArrayList<ViewGroup>();
		
		/*
		 * Transmission configuration
		 * Prepare a context pack for transmitting
		 * Schedule the transmission timer 
		 */
		if(SettingActivity.isTransmissionAvailable()) {
			contextPack = new ContextPack();
			contextTransmitter = new ContextTransmitter(context, this);
			contextTransmitter.start();
		}
		
		/*
		 * Sensor manipulation
		 * Add available contexts to the context list
		 * Generate a context view for each available context
		 */
		for(Sensor sensor:availableSensors) {
			if(SettingActivity.isContextAvailable(sensor.getType())) {
				sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL);
				contextList.add(generateContextView(sensor.getType()));
			}
		}
		
		/*
		 * Location manipulation
		 * Add the location context to the context list if it is available
		 * Generate a context view for the location context
		 */
		if(SettingActivity.isContextAvailable(ContextType.LOCATION)) {
			Criteria criteria = new Criteria();
			criteria.setAccuracy(Criteria.ACCURACY_FINE);
			criteria.setAltitudeRequired(false);
			criteria.setBearingRequired(false);
			criteria.setCostAllowed(true);
			criteria.setPowerRequirement(Criteria.POWER_LOW);
			
			String bestProvider = locationManager.getBestProvider(criteria, true);
//			bestProvider = LocationManager.GPS_PROVIDER;
			
			if(!bestProvider.equals("")/* && availableSensors[SensorInfo.TYPE_GPS]*/) {
//				Toast.makeText(this, "Selected Location Provider: "+bestProvider, Toast.LENGTH_LONG).show();	
				locationManager.requestLocationUpdates(bestProvider, 1000L, 1F, this);
				//			Location location = locationManager.getLastKnownLocation(bestProvider);
				//			Toast.makeText(this, bestProvider+" Lat: "+location.getLatitude()+" Lng: "+location.getLongitude(), Toast.LENGTH_LONG).show();
			}
			
			contextList.add(generateContextView(ContextType.LOCATION));
		}

		return contextList;
	}
	
	public void stop() {
		sensorManager.unregisterListener(this);
		locationManager.removeUpdates(this);
		if(contextPack != null) {
			contextPack = null;
		}
		if(contextTransmitter != null) {
			contextTransmitter.stop();
			contextTransmitter = null;
		}
	}

	public void displayContextInfo(int contextType) {
		// TODO Auto-generated method stub
		contextTypeDisplayed = contextType;
		isStaticContextInfoSet = false;
		contextInfoUpdater.setContextName(contextType);
	}
	
	public ContextPack getContextPack() {
		return contextPack;
	}
	
	private ViewGroup generateContextView(int contextType) {
		LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		ViewGroup contextView = (ViewGroup) layoutInflater.inflate(R.layout.context_view, null);
		contextView.setTag(contextType);
		TextView contextTv =(TextView) contextView.findViewById(R.id.contextTv);
		contextTv.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/Roboto-Bold.ttf"));
		contextTv.setText(ContextType.convertToString(contextType));
//		if(!SettingActivity.isContextAvailable(contextType)) {
//			contextView.setEnabled(false);
//			contextView.setAlpha(0.5f);
//		}
		return contextView;
	}

	/**************************************************
	 * Methods for SensorEventListener
	 **************************************************/
	public void onSensorChanged(SensorEvent event) {
		// TODO Auto-generated method stub
		if(contextTypeDisplayed != 0) {
			if(contextTypeDisplayed == event.sensor.getType()) {
				if(!isStaticContextInfoSet) {
					contextInfoUpdater.setContextInfo(event);
					isStaticContextInfoSet = true;
				}
				contextInfoUpdater.update(event);
			}
		}
		// Pack the context to the JSON
		if(contextPack != null) contextPack.put(event);	
	}
	
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub
		
	}
	
	/**************************************************
	 * Methods for LocationListener
	 **************************************************/
	public void onLocationChanged(Location location) {
		// TODO Auto-generated method stub
		if(contextTypeDisplayed != 0) {
			if(contextTypeDisplayed == ContextType.LOCATION) {
				if(!isStaticContextInfoSet) {
					contextInfoUpdater.setContextInfo(location);
					isStaticContextInfoSet = true;
				}
				contextInfoUpdater.update(location);
			}
			
			// Pack the context to the JSON
			if(contextPack != null) contextPack.put(location);	
		}
	}

	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub
		
	}

	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub
		
	}

	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub
		
	}

}
