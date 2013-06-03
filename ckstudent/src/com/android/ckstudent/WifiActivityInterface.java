package com.android.ckstudent;

import android.app.FragmentManager;

public interface WifiActivityInterface {
	void resetData();
	void setIsWifiP2pEnabled(boolean isWifiP2pEnabled);
	FragmentManager getFragmentManager();
}
