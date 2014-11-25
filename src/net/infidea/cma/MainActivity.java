package net.infidea.cma;

import java.util.Timer;
import java.util.TimerTask;

import net.infidea.cma.monitor.ContextMonitor;
import net.infidea.cma.setting.SettingActivity;
import android.os.Bundle;
import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity implements OnClickListener {

	private final int SETTING_REQCODE = 888;
	
	private boolean isRunning = false;
	
	private ImageButton startBt = null;
	
	private TextView statusTv = null;
	private TextView sensingDurationTv = null;
	private TextView serverAddressTv = null;
	private TextView transferPeriodTv = null;
	private TextView elapsedTimeTv = null;
	private GridView contextInfoGv = null;
	
	// Notification-related attributes
	private final int NOTIFICATION_ID = 26;
	private NotificationManager notificationManager = null;
	
	// Modules for context monitoring
	private ContextInfoUpdater contextInfoUpdater = null;
	private ContextMonitor contextMonitor = null;
	private ContextListAdapter contextListAdapter = null;
	
	// Timer for elapsed time counting
	private int sensingDuration = 0;
	private Timer elapsedTimer = new Timer();
	private int elapsedSeconds = 0;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ((View) findViewById(R.id.settingBt)).setOnClickListener(this);
        ((View) findViewById(R.id.aboutBt)).setOnClickListener(this);
        ((View) findViewById(R.id.cancelDisplayingContextInfoBt)).setOnClickListener(this);
        
        startBt = (ImageButton) findViewById(R.id.startBt);
        startBt.setOnClickListener(this);
        
        statusTv = (TextView) findViewById(R.id.statusTv);
        sensingDurationTv = (TextView) findViewById(R.id.sensingDurationTv);
        serverAddressTv = (TextView) findViewById(R.id.serverAddressTv);
        transferPeriodTv = (TextView) findViewById(R.id.transferPeriodTv);
        elapsedTimeTv = (TextView) findViewById(R.id.elapsedTimeTv);
        contextInfoGv = (GridView) findViewById(R.id.contextInfoGv);
        
		notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        contextInfoUpdater = new ContextInfoUpdater(this);
        contextMonitor = new ContextMonitor(this, contextInfoUpdater);
		contextListAdapter = new ContextListAdapter(this, contextMonitor);

    	SettingActivity.init(this);
		setSettingInformation();
    }
    
    @Override
    protected void onResume() {
    	// TODO Auto-generated method stub
    	super.onResume();
    }
    
    @Override
    protected void onDestroy() {
    	// TODO Auto-generated method stub
    	super.onDestroy();
    	notificationManager.cancel(NOTIFICATION_ID);
    	stopMonitoring();
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    	// TODO Auto-generated method stub
    	super.onActivityResult(requestCode, resultCode, data);
    	
    	switch(requestCode) {
		case SETTING_REQCODE:
			setSettingInformation();
			break;
		default:
			break;
		}
    }

	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId()) {
		case R.id.startBt:
			isRunning = isRunning?false:true;
			if(isRunning) {
				((ImageButton) v).setImageResource(R.drawable.ic_av_stop);
				
				// Start monitoring...
				startMonitoring();
				
				// Set to monitoring state
				statusTv.setText(getResources().getString(R.string.monitoring_state));
				statusTv.setTextColor(getResources().getColor(R.color.yellowgreen));
			} else {
				((ImageButton) v).setImageResource(R.drawable.ic_av_play);
				
				// Stop monitoring...
				stopMonitoring();

				// Set to idle state
				statusTv.setText(getResources().getString(R.string.idle_state));
				statusTv.setTextColor(getResources().getColor(R.color.white));
			}
			break;
		case R.id.settingBt:
			if(isRunning) {
				Toast.makeText(this, "Stop monitoring first.", Toast.LENGTH_LONG).show();
			} else {
				startActivityForResult(new Intent(this, SettingActivity.class), SETTING_REQCODE);
			}
			break;
		case R.id.aboutBt:
			startActivity(new Intent(this, AboutActivity.class));
			break;
		case R.id.cancelDisplayingContextInfoBt:
			contextInfoUpdater.clear();
			break;
		}
	}
	
	public void startMonitoring() {
		Intent notificationIntent = new Intent(this, MainActivity.class);
		notificationIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
		PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);		
		
		NotificationCompat.Builder notificationBuilder =
				new NotificationCompat.Builder(this)
		.setSmallIcon(R.drawable.ic_launcher)
		.setContentTitle("Context monitoring")
		.setContentText("Context monitoring started.")
		.setContentIntent(contentIntent);
		
		notificationManager.notify(NOTIFICATION_ID, notificationBuilder.build());

		contextListAdapter.setContextList(contextMonitor.start());
		contextInfoGv.setAdapter(contextListAdapter);
		
		turnOnTimer();
	}
	
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		Intent intent = new Intent();
        intent.setAction(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);

        startActivity(intent);
	}
	
	public void stopMonitoring() {
		notificationManager.cancel(NOTIFICATION_ID);
		contextMonitor.stop();
		contextListAdapter.clear();
		contextInfoUpdater.clear();
		turnOffTimer();
	}
	
	private void setSettingInformation() {
    	sensingDuration = SettingActivity.getSensingDuration();
    	
    	if(sensingDuration == 0) {
    		sensingDurationTv.setText("Continuous Sensing");	
    	} else {
    		sensingDurationTv.setText(sensingDuration+" (s)");
    	}
		
    	if(SettingActivity.isTransmissionAvailable()) {
    		serverAddressTv.setText(SettingActivity.getServerAddress());
    		transferPeriodTv.setText(SettingActivity.getTransferPeriod()+" (s)");
    	} else {
    		serverAddressTv.setText("Not Applicable");	
			transferPeriodTv.setText("Not Applicable");
    	}
	}
	
	private void turnOnTimer() {
		elapsedTimer = new Timer();
		elapsedSeconds = 0;
		elapsedTimer.schedule(new TimerTask() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				runOnUiThread(new Runnable() {
					
					public void run() {
						// TODO Auto-generated method stub
						elapsedTimeTv.setText((elapsedSeconds++)+" (s)");
						
						// When the elapsed time is over the sensing duration, stop monitoring.
						if(sensingDuration > 0 && sensingDuration < elapsedSeconds && isRunning == true) {
							startBt.performClick();
						}
					}
				});
			}
		}, 0, 1000);
	}
	
	private void turnOffTimer() {
		elapsedTimer.cancel();
		elapsedTimeTv.setText("");
	}
}
