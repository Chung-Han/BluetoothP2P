package com.android.ckstudent;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pManager.ActionListener;
import android.net.wifi.p2p.WifiP2pManager.Channel;
import android.net.wifi.p2p.WifiP2pManager.ChannelListener;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import com.android.ckstudent.DeviceListFragment.DeviceActionListener;

public class WifiDirectActivity extends Activity implements WifiActivityInterface, ChannelListener, DeviceActionListener {
	//�ܼ�
	public static final String TAG = "ckstudent";
	WifiP2pManager mManager;
	Channel mChannel;
	BroadcastReceiver mReceiver;
	IntentFilter mIntentFilter;
	private boolean isWifiP2pEnabled = false;
	private boolean retryChannel = false;
	
	//�T�{����O�_���wifi-direct
	@Override
	public void setIsWifiP2pEnabled(boolean isWifiP2pEnabled) {
        this.isWifiP2pEnabled = isWifiP2pEnabled;
    }
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mIntentFilter = new IntentFilter();
		//��ܥu�|����o4��intent��broadcast message
	    mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
	    mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
	    mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
	    mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);
        
	    mManager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
	    mChannel = mManager.initialize(this, getMainLooper(), null);
	    
	    //------------------�ɶ��ܪ�
	    //��dialog��wifi-direct�˸m�����}��, wifi-direct���䴩�Υ����}
	    if (!isWifiP2pEnabled) {
	    	//�Y���}�ҥX�{alertdialog�i������]�w����
	    	AlertDialog.Builder builder = new AlertDialog.Builder(WifiDirectActivity.this);
	    	builder.setTitle("Wifi-Direct���}��");
	    	builder.setMessage("�Цܳ]�w�ˬdWifi-Direct�}��");
			builder.setPositiveButton("�]�w",new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					startActivity(new Intent(Settings.ACTION_WIRELESS_SETTINGS));
				}
			});
			builder.setNegativeButton("����",new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					Intent intent = new Intent();
					//���������^��menu����
					intent.setClass(WifiDirectActivity.this, MenuActivityS.class);
					WifiDirectActivity.this.startActivity(intent);
					WifiDirectActivity.this.finish();
				}
			});
			builder.show();
        }
	    final DeviceListFragment fragment = (DeviceListFragment) getFragmentManager()
                .findFragmentById(R.id.frag_list);
	    //�M�䤤�����ݵ���
        fragment.onInitiateDiscovery();
	    //�j�M���I
	    mManager.discoverPeers(mChannel, new WifiP2pManager.ActionListener() {
	    	//���\�T��
            @Override
            public void onSuccess() {
                Toast.makeText(WifiDirectActivity.this, "Discovery Initiated",
                        Toast.LENGTH_SHORT).show();
            }
            //���ѰT��
            @Override
            public void onFailure(int reasonCode) {
                Toast.makeText(WifiDirectActivity.this, "Discovery Failed : " + reasonCode,
                        Toast.LENGTH_SHORT).show();
            }
        });
	    
	    //�i��fragment���I�諸�ʧ@
	    
	}
	
	@Override
	public void onResume() {
        super.onResume();
        mReceiver = new WifiDirectBroadcastReceiver(mManager, mChannel, this);
        //���U�s��������
        registerReceiver(mReceiver, mIntentFilter);
    }

    @Override
    public void onPause() {
        super.onPause();
        //���P�s��������
        unregisterReceiver(mReceiver);
    }
    
    //���A�o�ͧ��ܩI�s���禡�M�ź��I�C��
	@Override
	public void resetData() {
        DeviceListFragment fragmentList = (DeviceListFragment) getFragmentManager()
                .findFragmentById(R.id.frag_list);
        if (fragmentList != null) {
        	//�M�ź��I
            fragmentList.clearPeers();   
        }
    }
	
	//�s��
	@Override
    public void connect(WifiP2pConfig config) {
        mManager.connect(mChannel, config, new ActionListener() {
            @Override
            public void onSuccess() {
                // WiFiDirectBroadcastReceiver�|�q��, �i������
            }
            @Override
            public void onFailure(int reason) {
                Toast.makeText(WifiDirectActivity.this, "�s������, �A�դ@��",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

	//���_�s�u
    @Override
    public void disconnect() {
        mManager.removeGroup(mChannel, new ActionListener() {
            @Override
            public void onFailure(int reasonCode) {
                Log.d(TAG, "Disconnect failed. Reason :" + reasonCode);
            }
            @Override
            public void onSuccess() {
            	Toast.makeText(WifiDirectActivity.this, "�w���_",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    //�s�����_
    @Override
    public void onChannelDisconnected() {
        //�A�յ۳s���@��
        if (mManager != null && !retryChannel) {
            Toast.makeText(this, "���hChannel, ���s�s����", Toast.LENGTH_LONG).show();
            resetData();
            retryChannel = true;
            mManager.initialize(this, getMainLooper(), this);
        } else {
            Toast.makeText(this,
                    "��Channel, ���խ��}Wifi-Direct",
                    Toast.LENGTH_LONG).show();
        }
    }

    
    @Override
    public void cancelDisconnect() {
        /*
         * A cancel abort request by user. Disconnect i.e. removeGroup if
         * already connected. Else, request WifiP2pManager to abort the ongoing
         * request
         */
        if (mManager != null) {
            final DeviceListFragment fragment = (DeviceListFragment) getFragmentManager()
                    .findFragmentById(R.id.frag_list);
            if (fragment.getDevice() == null
                    || fragment.getDevice().status == WifiP2pDevice.CONNECTED) {
                disconnect();
            } else if (fragment.getDevice().status == WifiP2pDevice.AVAILABLE
                    || fragment.getDevice().status == WifiP2pDevice.INVITED) {
                mManager.cancelConnect(mChannel, new ActionListener() {
                    @Override
                    public void onSuccess() {
                        Toast.makeText(WifiDirectActivity.this, "����s��",
                                Toast.LENGTH_SHORT).show();
                    }
                    @Override
                    public void onFailure(int reasonCode) {
                        Toast.makeText(WifiDirectActivity.this,
                                "����s������. Reason Code: " + reasonCode,
                                Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }

    }
}
