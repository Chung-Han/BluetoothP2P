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
	//�ܼ�
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

    //��wifi framework �s��������intent�ɡA�N�|callback ��onReceive��k
	@Override
	public void onReceive(Context context, Intent intent) {
		//����N��
		String action = intent.getAction();
		//�N�Ϭ�WIFI P2P State�o���ܤ�  
		if (WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION.equals(action)) {
            int state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1);
            //WIFI P2P State��enable
            if (state == WifiP2pManager.WIFI_P2P_STATE_ENABLED) {
            	//�O�_�i�H�ϥ�wifi-direct
                activity.setIsWifiP2pEnabled(true);
            } else {
                activity.setIsWifiP2pEnabled(false);
                activity.resetData();
            }
            //�N�Ϭ�WIFI P2P���I�M��o���ܤ�
		} else if (WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION.equals(action)) {
            if (manager != null) {
            	// �M��i�Ϊ����I
            	//requestPeers(channel, myPeerListListener)
                manager.requestPeers(channel, (PeerListListener) activity.getFragmentManager()
                        .findFragmentById(R.id.frag_list));
            }
            //�N�Ϭ�WIFI P2P�s�����A�o���ܤ�
        } else if (WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION.equals(action)) {
            if (manager == null) {
                return;
            }
            //���wifi p2p�s�����A
            NetworkInfo networkInfo = (NetworkInfo) intent
                    .getParcelableExtra(WifiP2pManager.EXTRA_NETWORK_INFO);
            //WIFI P2P �s�����A��"connected"
            if (networkInfo.isConnected()) {
            	//�i��s���L�˸m request��T���s��owner IP
                DeviceDetailFragment fragment = (DeviceDetailFragment) activity
                        .getFragmentManager().findFragmentById(R.id.frag_detail);
                manager.requestConnectionInfo(channel, fragment);
                //WIFI P2P connection state is "disconnected"
            } else {
                //�s�����_
                activity.resetData();
            }
            //�N�Ϭ����s����device�o���ܤƵo���ܤ�
        } else if (WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION.equals(action)) {
            DeviceListFragment fragment = (DeviceListFragment) activity.getFragmentManager()
                    .findFragmentById(R.id.frag_list);
            //������s����device
            fragment.updateThisDevice((WifiP2pDevice) intent.getParcelableExtra(
                    WifiP2pManager.EXTRA_WIFI_P2P_DEVICE));
        }
	}
}
