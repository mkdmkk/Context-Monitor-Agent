package net.infidea.cma;

import net.infidea.cma.constant.ContextType;
import net.infidea.cma.util.TimeConverter;
import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.hardware.SensorEvent;
import android.location.Location;
import android.widget.TextView;

public class ContextInfoUpdater {

	private TextView contextNameTv = null;
	private TextView contextStaticInfoTv = null;
	private TextView contextInfoTv = null;
	private boolean isUpdatable = false;
	
	public ContextInfoUpdater(Context context) {
		// TODO Auto-generated constructor stub
		contextNameTv = (TextView) ((Activity) context).findViewById(R.id.contextNameTv);
		contextStaticInfoTv = (TextView) ((Activity) context).findViewById(R.id.contextStaticInfoTv);
		contextInfoTv = (TextView) ((Activity) context).findViewById(R.id.contextInfoTv);
		
		contextNameTv.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/Roboto-Medium.ttf"));
		contextStaticInfoTv.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/Roboto-Light.ttf"));
		contextInfoTv.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/Roboto-Light.ttf"));
	}
	
	public void setContextName(int contextType) {
		contextNameTv.setText(ContextType.convertToString(contextType));
		contextStaticInfoTv.setText("");
		contextInfoTv.setText("");
	}
	
	public void setContextInfo(SensorEvent event) {
		isUpdatable = true;
		String contextInfo = "Model: "+event.sensor.getName()+"\n";
		contextInfo += "Vendor: "+event.sensor.getVendor()+"\n";
		contextInfo += "Version: "+event.sensor.getVersion()+"\n";
		contextInfo += "Maxium range: "+event.sensor.getMaximumRange()+"\n";
		contextInfo += "Power: "+event.sensor.getPower()+"\n";
		contextInfo += "Resolution: "+event.sensor.getResolution()+"\n";
		contextInfo += "Minimum delay: "+event.sensor.getMinDelay()+"\n";
		contextInfo += "Accuracy: "+event.accuracy+"\n";
		contextStaticInfoTv.setText(contextInfo);
	}
	
	public void setContextInfo(Location location) {
		isUpdatable = true;
		String contextInfo = "Provider: "+location.getProvider()+"\n";
		contextInfo += "Accuracy: "+location.getAccuracy()+"\n";
		contextInfo += "Speed: "+location.getSpeed()+"\n";
		contextInfo += "Bearing: "+location.getBearing()+"\n";
		contextStaticInfoTv.setText(contextInfo);
	}
	
	public void update(SensorEvent event) {
		if(!isUpdatable) return;
		String contextInfo = "Time:\t\t\t"+TimeConverter.convertToMilliseconds(event.timestamp);
		int i = 0;
		for(i = 0; i < event.values.length; i++) {
			contextInfo += "\nvalue["+i+"]:\t\t"+event.values[i];
		}
		contextInfoTv.setText(contextInfo);
	}

	public void update(Location location) {
		if(!isUpdatable) return;
		String contextInfo = "Time:\t\t\t"+TimeConverter.convertToMilliseconds(location.getTime())+"\n";
		contextInfo += "Latitude:\t\t"+location.getLatitude()+"\n";
		contextInfo += "Longitude:\t"+location.getLongitude()+"\n";
		contextInfo += "Altitude:\t\t"+location.getAltitude();
		contextInfoTv.setText(contextInfo);
	}
	
	public void clear() {
		isUpdatable = false;
		contextNameTv.setText("");
		contextStaticInfoTv.setText("");
		contextInfoTv.setText("");
	}
}
