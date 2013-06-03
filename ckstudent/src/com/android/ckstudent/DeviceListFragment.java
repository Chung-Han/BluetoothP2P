package com.android.ckstudent;


import android.app.AlertDialog;
import android.app.ListFragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.wifi.WpsInfo;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pManager.PeerListListener;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * A ListFragment that displays available peers on discovery and requests the
 * parent activity to handle user interaction events
 */
public class DeviceListFragment extends ListFragment implements PeerListListener {
	//client for student
    private List<WifiP2pDevice> peers = new ArrayList<WifiP2pDevice>();
    ProgressDialog progressDialog = null;
    View mContentView = null;
    private WifiP2pDevice device;

    //當activity的onCreate()方法被返回之後，調用這個方法
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //setListAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,items));
        //每個ListAdapter的View都是format_device
        this.setListAdapter(new WiFiPeerListAdapter(getActivity(), R.layout.format_device, peers));
    }
    
    //創建fragment中的視圖的時候，調用這個方法
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mContentView = inflater.inflate(R.layout.list_device, null);
        return mContentView;
    }
    
    /**
     * @return this device
     */
    public WifiP2pDevice getDevice() {
        return device;
    }

    //取得裝置的連接狀態
    private static String getDeviceStatus(int deviceStatus) {
        Log.d(WifiDirectActivity.TAG, "Peer status :" + deviceStatus);
        switch (deviceStatus) {
            case WifiP2pDevice.AVAILABLE:
                return "Available";
            case WifiP2pDevice.INVITED:
                return "Invited";
            case WifiP2pDevice.CONNECTED:
                return "Connected";
            case WifiP2pDevice.FAILED:
                return "Failed";
            case WifiP2pDevice.UNAVAILABLE:
                return "Unavailable";
            default:
                return "Unknown";

        }
    }

    /**
     * Initiate a connection with the peer.
     */
    //點選該端點
    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
    	//得到裝置資訊
        final WifiP2pDevice device = (WifiP2pDevice) getListAdapter().getItem(position);
        //AlertDialog確認是否連線
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
    	builder.setTitle("連接裝置");
		builder.setPositiveButton("連線",new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				WifiP2pConfig config = new WifiP2pConfig();
				//getDevice取得的裝置 不確定!!!!!!!!!
                config.deviceAddress = device.deviceAddress;
                //不懂
                config.wps.setup = WpsInfo.PBC;
                if (progressDialog != null && progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                progressDialog = ProgressDialog.show(getActivity(), "Press back to cancel",
                        "Connecting to :" + device.deviceAddress, true, true);
                ((DeviceActionListener) getActivity()).connect(config);
                
                //傳送點名資訊
                
                //傳完點名資訊後中斷
                ((DeviceActionListener) getActivity()).disconnect();
			}
		});
		builder.setNegativeButton("取消",new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
			}
		});
		builder.show();
    }

    /**
     * Array adapter for ListFragment that maintains WifiP2pDevice list.
     */
    //建立一個List, 儲存各個端點資訊
    //一個端點一個format_device 最後放在list_device中
    private class WiFiPeerListAdapter extends ArrayAdapter<WifiP2pDevice> {
    	//WiFiPeerListAdapter(Context context, int textViewResourceId, List<WifiP2pDevice> objects)
        private List<WifiP2pDevice> items;
        public WiFiPeerListAdapter(Context context, int textViewResourceId,
                List<WifiP2pDevice> objects) {
            super(context, textViewResourceId, objects);
            items = objects;
        }
        //切換layout
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View v = convertView;
            if (v == null) {
            	//以format_device為顯示畫面
                LayoutInflater vi = (LayoutInflater) getActivity().getSystemService(
                        Context.LAYOUT_INFLATER_SERVICE);
                v = vi.inflate(R.layout.format_device, null);
            }
            //選擇的裝置
            WifiP2pDevice device = items.get(position);
            if (device != null) {
                TextView deviceName = (TextView) v.findViewById(R.id.device_name);
                TextView deviceDetails = (TextView) v.findViewById(R.id.device_details);
                if (deviceName != null) {
                    deviceName.setText(device.deviceName);
                }
                if (deviceDetails != null) {
                    deviceDetails.setText(getDeviceStatus(device.status));
                }
            }
            return v;
        }
    }

    /**
     * Update UI for this device.
     * 
     * @param device WifiP2pDevice object
     */
    public void updateThisDevice(WifiP2pDevice device) {
        this.device = device;
        TextView view = (TextView) mContentView.findViewById(R.id.my_name);
        view.setText(device.deviceName);
        view = (TextView) mContentView.findViewById(R.id.my_status);
        view.setText(getDeviceStatus(device.status));
    }

    @Override
    public void onPeersAvailable(WifiP2pDeviceList peerList) {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
        peers.clear();
        peers.addAll(peerList.getDeviceList());
        ((WiFiPeerListAdapter) getListAdapter()).notifyDataSetChanged();
        if (peers.size() == 0) {
            Log.d(WifiDirectActivity.TAG, "沒有找到裝置");
            return;
        }

    }

    //清除端點
    public void clearPeers() {
        peers.clear();
        ((WiFiPeerListAdapter) getListAdapter()).notifyDataSetChanged();
    }

    //尋找中的等待視窗
    public void onInitiateDiscovery() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
        progressDialog = ProgressDialog.show(getActivity(), "尋找中", "尋找可用Device", true, true);
    }
    
    /**
     * An interface-callback for the activity to listen to fragment interaction
     * events.
     */
    public interface DeviceActionListener {
        //void showDetails(WifiP2pDevice device);
        void cancelDisconnect();
        void connect(WifiP2pConfig config);
        void disconnect();
    }

}