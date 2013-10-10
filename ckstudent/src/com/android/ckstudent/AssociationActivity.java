package com.android.ckstudent;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.widget.TabHost;
public class AssociationActivity extends FragmentActivity {
	private TabHost mTabHost;
	private TabManager mTabManager;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.association_main);
		mTabHost = (TabHost)findViewById(android.R.id.tabhost);
		mTabHost.setup();
		
		mTabManager = new TabManager(this, mTabHost, R.id.realtabcontent);
		mTabHost.setCurrentTab(0);
        mTabManager.addTab(mTabHost.newTabSpec("Fragment1").setIndicator("�̷s����",
        		this.getResources().getDrawable(android.R.drawable.ic_dialog_alert)),
            AssociationFragment1.class, null);
        mTabManager.addTab(mTabHost.newTabSpec("Fragment2").setIndicator("���θ�T"),
            AssociationFragment2.class, null);
        mTabManager.addTab(mTabHost.newTabSpec("Fragment3").setIndicator("���ì���"),
            AssociationFragment3.class, null);
	}
}
