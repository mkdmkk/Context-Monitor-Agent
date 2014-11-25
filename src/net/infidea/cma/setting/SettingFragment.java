package net.infidea.cma.setting;

import net.infidea.cma.R;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.text.InputType;

public class SettingFragment extends PreferenceFragment {
	
	private PreferenceManager preferenceManager = null;
	
	private EditTextPreference sensingDurationEt = null;
	private EditTextPreference serverAddressEt = null;
	private EditTextPreference transmissionPeriodEt = null;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		addPreferencesFromResource(R.xml.setting);
		
		preferenceManager = getPreferenceManager();
		
		sensingDurationEt = (EditTextPreference) preferenceManager.findPreference("sensingDuration");
		serverAddressEt = (EditTextPreference) preferenceManager.findPreference("serverAddress");
		transmissionPeriodEt = (EditTextPreference) preferenceManager.findPreference("transferPeriod");
		
		sensingDurationEt.getEditText().setInputType(InputType.TYPE_CLASS_NUMBER);
		serverAddressEt.getEditText().setInputType(InputType.TYPE_CLASS_TEXT|InputType.TYPE_TEXT_VARIATION_URI);
		transmissionPeriodEt.getEditText().setInputType(InputType.TYPE_CLASS_NUMBER|InputType.TYPE_NUMBER_FLAG_DECIMAL);
		
		sensingDurationEt.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
			
			public boolean onPreferenceChange(Preference preference, Object newValue) {
				// TODO Auto-generated method stub
				try {
					int val = Integer.parseInt((String)newValue);
					if(val >= 0) {
						sensingDurationEt.setText(""+val);
					} else {
						sensingDurationEt.setText("0");
					}
				} catch (Exception e) {
					// TODO: handle exception
					sensingDurationEt.setText("0");
				}
				return false;
			}
		});
		
		transmissionPeriodEt.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
			
			public boolean onPreferenceChange(Preference preference, Object newValue) {
				// TODO Auto-generated method stub
				try {
					double val = Double.parseDouble((String)newValue);
					if(val >= 0.1) {
						transmissionPeriodEt.setText(""+val);
					} else {
						transmissionPeriodEt.setText("0.1");
					}
				} catch (Exception e) {
					// TODO: handle exception
					transmissionPeriodEt.setText("0");
				}
				return false;
			}
		});
	}
}
