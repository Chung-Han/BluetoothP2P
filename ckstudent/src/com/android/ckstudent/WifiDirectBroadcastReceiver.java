package com.android.ckstudent;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pManager.Channel;
import android.net.wifi.p2p.WifiP2pManager.PeerListListener;

public class WifiDirectBroadcastReceiver extends BroadcastReceiver {
	//變數
	private WifiP2pManager manager;
    private Channel channel;
    private WifiActivityInterface activity;
    public WifiDirectBroadcastReceiver(WifiP2pManager manager, Channel channel,
    		WifiActivityInterface activity) {
        super();
        this.manager = manager;
        this.channel = channel;
        this.activity = activity;
    }

    //當wifi framework 廣播相關的intent時，就會callback 該onReceive方法
	@Override
	public void onReceive(Context context, Intent intent) {
		//獲取意圖
		String action = intent.getAction();
		//意圖為WIFI P2P State發生變化  
		if (WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION.equals(action)) {
            int state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1);
            //WIFI P2P State為enable
            if (state == WifiP2pManager.WIFI_P2P_STATE_ENABLED) {
            	//是否可以使用wifi-direct
                activity.setIsWifiP2pEnabled(true);
            } else {
                activity.setIsWifiP2pEnabled(false);
                activity.resetData();
            }
            //意圖為WIFI P2P端點清單發生變化
		} else if (WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION.equals(action)) {
            if (manager != null) {
            	// 尋找可用的端點
            	//requestPeers(channel, myPeerListListener)
                manager.requestPeers(channel, (PeerListListener) activity.getFragmentManager()
                        .findFragmentById(R.id.frag_list));
            }
            //意圖為WIFI P2P連接狀態發生變化
        } else if (WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION.equals(action)) {
            if (manager == null) {
                return;
            }
            //獲取wifi p2p連接狀態
            NetworkInfo networkInfo = (NetworkInfo) intent
                    .getParcelableExtra(WifiP2pManager.EXTRA_NETWORK_INFO);
            //WIFI P2P 連接狀態為"connected"
            if (networkInfo.isConnected()) {
            	//可能連到其他裝置 request資訊重新找owner IP
                DeviceDetailFragment fragment = (DeviceDetailFragment) activity
                        .getFragmentManager().findFragmentById(R.id.frag_detail);
                manager.requestConnectionInfo(channel, fragment);
                //WIFI P2P connection state is "disconnected"
            } else {
                //連接中斷
                activity.resetData();
            }
            //意圖為正連接的device發生變化發生變化
        } else if (WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION.equals(action)) {
            DeviceListFragment fragment = (DeviceListFragment) activity.getFragmentManager()
                    .findFragmentById(R.id.frag_list);
            //獲取正連接的device
            fragment.updateThisDevice((WifiP2pDevice) intent.getParcelableExtra(
                    WifiP2pManager.EXTRA_WIFI_P2P_DEVICE));
        }
	}
}
