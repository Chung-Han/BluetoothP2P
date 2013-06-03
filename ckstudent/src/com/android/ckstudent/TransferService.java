package com.android.ckstudent;

import android.app.IntentService;
import android.content.Intent;

public class TransferService extends IntentService {

	public TransferService(String name) {
        super(name);
    }

    public TransferService() {
        super("TransferService");
    }
    
	@Override
	protected void onHandleIntent(Intent arg0) {
		// TODO Auto-generated method stub
		
	}

}
