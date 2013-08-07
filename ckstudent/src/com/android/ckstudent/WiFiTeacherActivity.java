package com.android.ckstudent;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.android.ckstudent.WiFiDirectServicesList.DeviceClickListener;
import com.android.ckstudent.WiFiDirectServicesList.WiFiDevicesAdapter;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.net.wifi.WpsInfo;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pManager.ActionListener;
import android.net.wifi.p2p.WifiP2pManager.Channel;
import android.net.wifi.p2p.WifiP2pManager.ConnectionInfoListener;
import android.net.wifi.p2p.WifiP2pManager.DnsSdServiceResponseListener;
import android.net.wifi.p2p.WifiP2pManager.DnsSdTxtRecordListener;
import android.net.wifi.p2p.nsd.WifiP2pDnsSdServiceInfo;
import android.net.wifi.p2p.nsd.WifiP2pDnsSdServiceRequest;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class WiFiTeacherActivity extends Activity implements DeviceClickListener, ConnectionInfoListener{
	public static final String TAG = "wifiteacher";
	private WifiP2pManager manager;
	private Channel channel;
	private BroadcastReceiver receiver = null;
	private WiFiDirectServicesList servicesList;
	private WifiP2pDnsSdServiceRequest serviceRequest;
	private final IntentFilter intentFilter = new IntentFilter();
	
	public static final String TXTRECORD_PROP_AVAILABLE = "available";
    public static final String SERVICE_INSTANCE = "_wifip2p";
    public static final String SERVICE_REG_TYPE = "_attendance._tcp";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_run);
		
		intentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);
        
		Button btn1 = (Button)this.findViewById(R.id.button1);
		btn1.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (serviceRequest != null)
		            manager.removeServiceRequest(channel, serviceRequest, new ActionListener() {
		            	@Override
		                public void onSuccess() {
		            	}
		                @Override
		                public void onFailure(int arg0) {
		                }
		            });
			}
		});
		
		Button btn2 = (Button)this.findViewById(R.id.button2);
		btn2.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				getFragmentManager().beginTransaction()
		        .add(R.id.container_root, servicesList, "services").commit();
			}
		});
		
		manager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        channel = manager.initialize(this, getMainLooper(), null);
        
        startRegistrationAndDiscovery();
        servicesList = new WiFiDirectServicesList();
	}

	private void startRegistrationAndDiscovery() {
        Map<String, String> record = new HashMap<String, String>();
        record.put(TXTRECORD_PROP_AVAILABLE, "visible");

        WifiP2pDnsSdServiceInfo service = WifiP2pDnsSdServiceInfo.newInstance(
                SERVICE_INSTANCE, SERVICE_REG_TYPE, record);
        manager.addLocalService(channel, service, new ActionListener() {
            @Override
            public void onSuccess() {
            	notifyMessage(WiFiTeacherActivity.this, "Added Local Service");
            }
            @Override
            public void onFailure(int error) {
            	notifyMessage(WiFiTeacherActivity.this, "Failed to add a service");
            }
        });
        new DiscoverAndCallAsyncTask(WiFiTeacherActivity.this).execute();
    }
	
	public class DiscoverAndCallAsyncTask extends AsyncTask<Void, Void, Void> {
		Context context;
		public DiscoverAndCallAsyncTask(Context context){
			this.context = context;
		}
		@Override
		protected Void doInBackground(Void... arg0) {
			WiFiDirectServicesList fragment = (WiFiDirectServicesList) getFragmentManager()
        			.findFragmentByTag("services");
			WiFiDevicesAdapter adapter = ((WiFiDevicesAdapter) fragment.getListAdapter());
			do {
				//run discover
				discoverService();
				//from 1st device to last one
				for (int i=0; i<adapter.getCount(); i++) {
					//call and change image status
					if(adapter.getItem(i).studentName == "Unknown"){
						connectP2p(adapter.getItem(i));
						//get studentName
						GlobalData globalVariable = (GlobalData)context.getApplicationContext();
						adapter.getItem(i).studentName = globalVariable.studentName;
						//change UI
						adapter.notifyDataSetChanged();
						//disconnect
						disconnectP2p();
					}
				}
				//Thread.sleep(30000);
			} while(serviceRequest != null);
			return null;
		}
	}
	
	private void discoverService() {
        manager.setDnsSdResponseListeners(channel, new DnsSdServiceResponseListener() {
        	@Override
            public void onDnsSdServiceAvailable(String instanceName,
            		String registrationType, WifiP2pDevice srcDevice) {
        		// A service has been discovered. Is this our app?
                if (instanceName.equalsIgnoreCase(SERVICE_INSTANCE)) {
                // update the UI and add the item the discovered device.
                	WiFiDirectServicesList fragment = (WiFiDirectServicesList) getFragmentManager()
                			.findFragmentByTag("services");
                	if (fragment != null) {
                		WiFiDevicesAdapter adapter = ((WiFiDevicesAdapter) fragment
                				.getListAdapter());
                        WiFiP2pService service = new WiFiP2pService();
                        service.device = srcDevice;
                        service.instanceName = instanceName;
                        service.serviceRegistrationType = registrationType;
                        adapter.add(service);
                        adapter.notifyDataSetChanged();
                        Log.d(TAG, "onBonjourServiceAvailable " + instanceName);
                    }
                }
        	}
        	}, new DnsSdTxtRecordListener() {
        		@Override
                public void onDnsSdTxtRecordAvailable(
                		String fullDomainName, Map<String, String> record,
                        WifiP2pDevice device) {
        			Log.d(TAG,
                    device.deviceName + " is " + record.get(TXTRECORD_PROP_AVAILABLE));
                }
            });
        // After attaching listeners, create a service request and initiate
        // discovery.
        serviceRequest = WifiP2pDnsSdServiceRequest.newInstance();
        manager.addServiceRequest(channel, serviceRequest,
                new ActionListener() {
                    @Override
                    public void onSuccess() {
                    	notifyMessage(WiFiTeacherActivity.this, "Added service discovery request");
                    }
                    @Override
                    public void onFailure(int arg0) {
                    	notifyMessage(WiFiTeacherActivity.this, "Failed adding service discovery request");
                    }
                });
        manager.discoverServices(channel, new ActionListener() {
            @Override
            public void onSuccess() {
            	notifyMessage(WiFiTeacherActivity.this, "Service discovery initiated");
            }
            @Override
            public void onFailure(int arg0) {
            	notifyMessage(WiFiTeacherActivity.this, "Service discovery failed");
            }
        });
    }
	
	@Override
	public void connectP2p(WiFiP2pService service) {
		WifiP2pConfig config = new WifiP2pConfig();
        config.deviceAddress = service.device.deviceAddress;
        config.wps.setup = WpsInfo.PBC;
        manager.connect(channel, config, new ActionListener() {
            @Override
            public void onSuccess() {
            	notifyMessage(WiFiTeacherActivity.this, "Connecting to service");
            }
            @Override
            public void onFailure(int errorCode) {
            	notifyMessage(WiFiTeacherActivity.this, "Failed connecting to service");
            }
        });
	}
	
	public void disconnectP2p() {
		manager.cancelConnect(channel, new ActionListener() {
			@Override
            public void onSuccess() {
            	notifyMessage(WiFiTeacherActivity.this, "Aborting connection");
            }
            @Override
            public void onFailure(int errorCode) {
            	notifyMessage(WiFiTeacherActivity.this, "Connect abort request failed.");
            }
		});
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		unregisterReceiver(receiver);
	}

	@Override
	protected void onResume() {
		super.onResume();
		receiver = new WiFiDirectBroadcastReceiver(manager, channel, this);
        registerReceiver(receiver, intentFilter);
	}

	public void notifyMessage(Context context, String message) {
		Toast.makeText(context, message, Toast.LENGTH_LONG).show();
	}

	@Override
	public void onConnectionInfoAvailable(WifiP2pInfo p2pInfo) {
		Thread thread = null;
		try {
            thread = new TeacherSocketHandler(this);
            thread.start();
        } catch (IOException e) {
            Log.d(TAG,
                    "Failed to create a server thread - " + e.getMessage());
            return;
        }
	}
}
