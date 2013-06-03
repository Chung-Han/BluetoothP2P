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
	//變數
	public static final String TAG = "ckstudent";
	WifiP2pManager mManager;
	Channel mChannel;
	BroadcastReceiver mReceiver;
	IntentFilter mIntentFilter;
	private boolean isWifiP2pEnabled = false;
	private boolean retryChannel = false;
	
	//確認手機是否支持wifi-direct
	@Override
	public void setIsWifiP2pEnabled(boolean isWifiP2pEnabled) {
        this.isWifiP2pEnabled = isWifiP2pEnabled;
    }
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mIntentFilter = new IntentFilter();
		//表示只會收到這4個intent的broadcast message
	    mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
	    mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
	    mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
	    mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);
        
	    mManager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
	    mChannel = mManager.initialize(this, getMainLooper(), null);
	    
	    //------------------時間很長
	    //用dialog建wifi-direct裝置提醒開關, wifi-direct不支援或未打開
	    if (!isWifiP2pEnabled) {
	    	//若未開啟出現alertdialog可直接到設定頁面
	    	AlertDialog.Builder builder = new AlertDialog.Builder(WifiDirectActivity.this);
	    	builder.setTitle("Wifi-Direct未開啟");
	    	builder.setMessage("請至設定檢查Wifi-Direct開關");
			builder.setPositiveButton("設定",new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					startActivity(new Intent(Settings.ACTION_WIRELESS_SETTINGS));
				}
			});
			builder.setNegativeButton("取消",new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					Intent intent = new Intent();
					//取消直接回到menu頁面
					intent.setClass(WifiDirectActivity.this, MenuActivityS.class);
					WifiDirectActivity.this.startActivity(intent);
					WifiDirectActivity.this.finish();
				}
			});
			builder.show();
        }
	    final DeviceListFragment fragment = (DeviceListFragment) getFragmentManager()
                .findFragmentById(R.id.frag_list);
	    //尋找中的等待視窗
        fragment.onInitiateDiscovery();
	    //搜尋端點
	    mManager.discoverPeers(mChannel, new WifiP2pManager.ActionListener() {
	    	//成功訊息
            @Override
            public void onSuccess() {
                Toast.makeText(WifiDirectActivity.this, "Discovery Initiated",
                        Toast.LENGTH_SHORT).show();
            }
            //失敗訊息
            @Override
            public void onFailure(int reasonCode) {
                Toast.makeText(WifiDirectActivity.this, "Discovery Failed : " + reasonCode,
                        Toast.LENGTH_SHORT).show();
            }
        });
	    
	    //進行fragment中點選的動作
	    
	}
	
	@Override
	public void onResume() {
        super.onResume();
        mReceiver = new WifiDirectBroadcastReceiver(mManager, mChannel, this);
        //註冊廣播接收器
        registerReceiver(mReceiver, mIntentFilter);
    }

    @Override
    public void onPause() {
        super.onPause();
        //註銷廣播接收器
        unregisterReceiver(mReceiver);
    }
    
    //當狀態發生改變呼叫此函式清空端點列表
	@Override
	public void resetData() {
        DeviceListFragment fragmentList = (DeviceListFragment) getFragmentManager()
                .findFragmentById(R.id.frag_list);
        if (fragmentList != null) {
        	//清空端點
            fragmentList.clearPeers();   
        }
    }
	
	//連接
	@Override
    public void connect(WifiP2pConfig config) {
        mManager.connect(mChannel, config, new ActionListener() {
            @Override
            public void onSuccess() {
                // WiFiDirectBroadcastReceiver會通知, 可先忽略
            }
            @Override
            public void onFailure(int reason) {
                Toast.makeText(WifiDirectActivity.this, "連接失敗, 再試一次",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

	//中斷連線
    @Override
    public void disconnect() {
        mManager.removeGroup(mChannel, new ActionListener() {
            @Override
            public void onFailure(int reasonCode) {
                Log.d(TAG, "Disconnect failed. Reason :" + reasonCode);
            }
            @Override
            public void onSuccess() {
            	Toast.makeText(WifiDirectActivity.this, "已中斷",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    //連接中斷
    @Override
    public void onChannelDisconnected() {
        //再試著連接一次
        if (mManager != null && !retryChannel) {
            Toast.makeText(this, "失去Channel, 重新連接中", Toast.LENGTH_LONG).show();
            resetData();
            retryChannel = true;
            mManager.initialize(this, getMainLooper(), this);
        } else {
            Toast.makeText(this,
                    "遺失Channel, 嘗試重開Wifi-Direct",
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
                        Toast.makeText(WifiDirectActivity.this, "中止連接",
                                Toast.LENGTH_SHORT).show();
                    }
                    @Override
                    public void onFailure(int reasonCode) {
                        Toast.makeText(WifiDirectActivity.this,
                                "中止連接失敗. Reason Code: " + reasonCode,
                                Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }

    }
}
