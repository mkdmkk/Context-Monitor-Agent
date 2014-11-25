package net.infidea.cma.setting;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.util.SparseArray;

public class SettingActivity extends Activity {
	
	private static SharedPreferences sharedPreferences = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		getFragmentManager().beginTransaction().replace(android.R.id.content, new SettingFragment()).commit();
		
	}
	
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		// TODO Auto-generated method stub
		super.onConfigurationChanged(newConfig);
	}
	
	public static void init(Context context) {
		sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
	}
	
	public static SparseArray<Boolean> getContextList() {
		if(sharedPreferences != null) {
			SparseArray<Boolean> contextList = new SparseArray<Boolean>();
			if(!sharedPreferences.getBoolean("contextTypeAll", false)) {
				contextList.put(0, false);
				for(int i=1; i <= 13; i++) {
					contextList.put(i, sharedPreferences.getBoolean("contextType"+i, false));
				}
			} else {
				contextList.put(0, true);
			}
			return contextList;
		}
		return null;
	}
	
	public static boolean isContextAvailable(int contextType) {
		if(sharedPreferences.getBoolean("contextTypeAll", false)) {
			return true;
		} else {
			return sharedPreferences.getBoolean("contextType"+contextType, false);
		}
	}
	
	public static int getSensingDuration() {
		if(sharedPreferences != null) {
			return Integer.parseInt(sharedPreferences.getString("sensingDuration", "0"));
		}
		return 0;
	}
	
	public static boolean isTransmissionAvailable() {
		if(sharedPreferences != null) {
			Log.d("infidea",""+sharedPreferences.getBoolean("transferOn", false));
			return sharedPreferences.getBoolean("transferOn", false);
		}
		return false;
	}
	
	public static String getServerAddress() {
		if(sharedPreferences != null) {
			return sharedPreferences.getString("serverAddress", "");
		}
		return "";
	}
	
	public static double getTransferPeriod() {
		if(sharedPreferences != null) {
			return Double.parseDouble(sharedPreferences.getString("transferPeriod", "0"));
		}
		return 0;
	}
	
}
