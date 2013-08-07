package com.android.ckstudent;

import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pManager.ActionListener;
import android.net.wifi.p2p.WifiP2pManager.Channel;
import android.net.wifi.p2p.WifiP2pManager.ConnectionInfoListener;
import android.net.wifi.p2p.nsd.WifiP2pDnsSdServiceInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

/**
 * This activity for student to registration a local service.
 * Each student registration its own service that teacher can
 * discovery student's service and connect it within a loop. 
 */
public class WiFiStudentActivity extends Activity implements ConnectionInfoListener, Handler.Callback{
	private WifiP2pManager manager;
	private Channel channel;
	private Handler handler = new Handler();
	
	public static final String TXTRECORD_PROP_AVAILABLE = "available";
    public static final String SERVICE_INSTANCE = "_wifip2p";
    public static final String SERVICE_REG_TYPE = "_attendance._tcp";
    static final int SERVER_PORT = 4545;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.student_waiting);
		manager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        channel = manager.initialize(this, getMainLooper(), null);
        startRegistration();
	}
	
	private void startRegistration() {
		Map<String, String> record = new HashMap<String, String>();
        record.put(TXTRECORD_PROP_AVAILABLE, "visible");
        //register a service
        WifiP2pDnsSdServiceInfo service = WifiP2pDnsSdServiceInfo.newInstance(
                SERVICE_INSTANCE, SERVICE_REG_TYPE, record);
        manager.addLocalService(channel, service, new ActionListener() {
            @Override
            public void onSuccess() {
            	Toast.makeText(WiFiStudentActivity.this,
            			"Added Local Service", Toast.LENGTH_LONG).show();
            }
            @Override
            public void onFailure(int error) {
            	Toast.makeText(WiFiStudentActivity.this,
            			"Failed to add a service", Toast.LENGTH_LONG).show();
            }
        });
	}

	@Override
	public void onConnectionInfoAvailable(WifiP2pInfo p2pInfo) {
		Thread thread = null;
		//open socket
		thread = new StudentSocketHandler(p2pInfo.groupOwnerAddress, this, handler);
        thread.start();
	}

	/*
	 * When end of socket, change layout to notify success.
	 */
	@Override
	public boolean handleMessage(Message msg) {
		String time = (String)msg.obj;
		//change layout
		if (msg.what == 1) {
			setContentView(R.layout.student_success);
			//show the time
			TextView txt2 = (TextView)this.findViewById(R.id.textView2);
			txt2.setText("Finished at " + time);
			//go back to menu
			Button btn1 = (Button)this.findViewById(R.id.button1);
			btn1.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent intent = new Intent();
					intent.setClass(WiFiStudentActivity.this, MenuActivityS.class);
					WiFiStudentActivity.this.startActivity(intent);
					WiFiStudentActivity.this.finish();
				}
			});
		}
		return false;
	}

}
