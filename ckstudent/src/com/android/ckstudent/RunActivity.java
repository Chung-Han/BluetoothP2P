package com.android.ckstudent;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pManager.ActionListener;
import android.net.wifi.p2p.WifiP2pManager.Channel;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class RunActivity extends Activity implements WifiActivityInterface {
	//變數
	WifiP2pManager mManager;
	Channel mChannel;
	BroadcastReceiver mReceiver;
	IntentFilter mIntentFilter;
	private boolean isWifiP2pEnabled = false;
	
	//wifi-direct 是否打開
	@Override
	public void setIsWifiP2pEnabled(boolean isWifiP2pEnabled) {
        this.isWifiP2pEnabled = isWifiP2pEnabled;
    }
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_run);
		
		mIntentFilter = new IntentFilter();
		mManager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
	    mChannel = mManager.initialize(this, getMainLooper(), null);
		//要接收的動作
	    mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
	    mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
	    mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
	    mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);
        
	    
		//接收所設定的密碼與密碼型態
		Bundle bundle = this.getIntent().getExtras();
		String type = bundle.getString("Type");
		String pwd = bundle.getString("Pwd_Lock");
		
		//建立wifi direct 群組
		//建立接收到客戶端的反應
		//確認密碼是否正確
		
		//按下Home 鍵 回首頁
		Button btn1 = (Button)this.findViewById(R.id.button1);
		btn1.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				
				intent.setClass(RunActivity.this, MenuActivity.class);
				RunActivity.this.startActivity(intent);
				RunActivity.this.finish();
			}
		});
	}

	@Override
	protected void onResume() {
		super.onResume();
		mReceiver = new WifiDirectBroadcastReceiver(mManager, mChannel, this);
		//註冊廣播接受器
        registerReceiver(mReceiver, mIntentFilter);
        createGroup();
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		unregisterReceiver(mReceiver);
		//移除wifi direct群組
        mManager.removeGroup(mChannel, new ActionListener() {
        	//失敗訊息
            @Override
            public void onFailure(int reasonCode) {
                Log.d("WifiDirectAptestActivity", "Disconnect failed. Reason :" + reasonCode);
            }
            //成功訊息
            @Override
            public void onSuccess() {
                Log.d("WifiDirectAptestActivity", "Should have been sucessfully removed");
            }
        });
	}
	
	//當狀態發生改變呼叫此函式清空端點列表
	@Override
	public void resetData() {}

	//建立wifi direct群組
	public void createGroup()
	{
	    mManager.createGroup(mChannel, new ActionListener() {
	    	//成功訊息
	        @Override
	        public void onSuccess() {
	            // WiFiDirectBroadcastReceiver will notify us. Ignore for now.
	            Log.d("WifiDirectAPtestActivity", "Group creating request successfully send");
	        }
	        //失敗訊息
	        @Override
	        public void onFailure(int reason) {
	            Toast.makeText(RunActivity.this, "Connect failed. Retry.",
	                    Toast.LENGTH_SHORT).show();
	        }
	    });
	}
}
