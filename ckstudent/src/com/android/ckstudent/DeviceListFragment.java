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

    //��activity��onCreate()��k�Q��^����A�եγo�Ӥ�k
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //setListAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,items));
        //�C��ListAdapter��View���Oformat_device
        this.setListAdapter(new WiFiPeerListAdapter(getActivity(), R.layout.format_device, peers));
    }
    
    //�Ы�fragment�������Ϫ��ɭԡA�եγo�Ӥ�k
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

    //���o�˸m���s�����A
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
    //�I��Ӻ��I
    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
    	//�o��˸m��T
        final WifiP2pDevice device = (WifiP2pDevice) getListAdapter().getItem(position);
        //AlertDialog�T�{�O�_�s�u
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
    	builder.setTitle("�s���˸m");
		builder.setPositiveButton("�s�u",new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				WifiP2pConfig config = new WifiP2pConfig();
				//getDevice���o���˸m ���T�w!!!!!!!!!
                config.deviceAddress = device.deviceAddress;
                //����
                config.wps.setup = WpsInfo.PBC;
                if (progressDialog != null && progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                progressDialog = ProgressDialog.show(getActivity(), "Press back to cancel",
                        "Connecting to :" + device.deviceAddress, true, true);
                ((DeviceActionListener) getActivity()).connect(config);
                
                //�ǰe�I�W��T
                
                //�ǧ��I�W��T�ᤤ�_
                ((DeviceActionListener) getActivity()).disconnect();
			}
		});
		builder.setNegativeButton("����",new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
			}
		});
		builder.show();
    }

    /**
     * Array adapter for ListFragment that maintains WifiP2pDevice list.
     */
    //�إߤ@��List, �x�s�U�Ӻ��I��T
    //�@�Ӻ��I�@��format_device �̫��blist_device��
    private class WiFiPeerListAdapter extends ArrayAdapter<WifiP2pDevice> {
    	//WiFiPeerListAdapter(Context context, int textViewResourceId, List<WifiP2pDevice> objects)
        private List<WifiP2pDevice> items;
        public WiFiPeerListAdapter(Context context, int textViewResourceId,
                List<WifiP2pDevice> objects) {
            super(context, textViewResourceId, objects);
            items = objects;
        }
        //����layout
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View v = convertView;
            if (v == null) {
            	//�Hformat_device����ܵe��
                LayoutInflater vi = (LayoutInflater) getActivity().getSystemService(
                        Context.LAYOUT_INFLATER_SERVICE);
                v = vi.inflate(R.layout.format_device, null);
            }
            //��ܪ��˸m
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
            Log.d(WifiDirectActivity.TAG, "�S�����˸m");
            return;
        }

    }

    //�M�����I
    public void clearPeers() {
        peers.clear();
        ((WiFiPeerListAdapter) getListAdapter()).notifyDataSetChanged();
    }

    //�M�䤤�����ݵ���
    public void onInitiateDiscovery() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
        progressDialog = ProgressDialog.show(getActivity(), "�M�䤤", "�M��i��Device", true, true);
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