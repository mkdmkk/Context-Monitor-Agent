package net.infidea.cma;

import net.infidea.cma.util.TimeConverter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.hardware.SensorEvent;
import android.location.Location;
import android.util.Log;

public class ContextPack extends JSONObject {
	public void put(SensorEvent event) {
		JSONObject context = new JSONObject();
		try {
			context.put("name", event.sensor.getName());
			context.put("version", event.sensor.getVersion());
			context.put("maximumRange", event.sensor.getMaximumRange());
			context.put("power", event.sensor.getName());
			context.put("resolution", event.sensor.getName());
			context.put("minimumDelay", event.sensor.getName());
			context.put("accuracy", event.accuracy);
			context.put("time", TimeConverter.convertToMilliseconds(event.timestamp));
			JSONArray values = new JSONArray();
			for(int i = 0; i < event.values.length; i++) {
				values.put(i, event.values[i]);
			}
			context.put("values", values);
			put(""+event.sensor.getType(), context);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void put(Location location) {
		JSONObject context = new JSONObject();
		try {
			context.put("provider", location.getProvider());
			context.put("accuracy", location.getAccuracy());
			context.put("bearing", location.getBearing());
			context.put("speed", location.getSpeed());
			context.put("time", TimeConverter.convertToMilliseconds(location.getTime()));
			context.put("latitude", location.getLatitude());
			context.put("longitude", location.getLongitude());
			context.put("altitude", location.getAltitude());
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
