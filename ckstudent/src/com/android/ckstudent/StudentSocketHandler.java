package com.android.ckstudent;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

public class StudentSocketHandler extends Thread {
	Context context;
	Handler handler;
    private InetAddress mAddress;

	public StudentSocketHandler(InetAddress groupOwnerAddress, Context context, Handler handler) {
		this.handler = handler;
        this.mAddress = groupOwnerAddress;
        this.context = context;
    }

	@Override
	public void run() {
		Socket socket = new Socket();
		String time;
		GlobalData globalVariable = (GlobalData)context.getApplicationContext();
		try {
			//send student name
			socket.connect(new InetSocketAddress(mAddress.getHostAddress(),
                    WiFiStudentActivity.SERVER_PORT), 5000);
			OutputStream os = socket.getOutputStream();
			os.write(globalVariable.studentName.getBytes());
			os.flush();
			os.close();
			
			//receive current time
			InputStream is = socket.getInputStream();
			byte[] buffer = new byte[20];
			is.read(buffer);
			time = new String(buffer);
			
			//send time to WiFiStudentActivity
			Message msg = handler.obtainMessage(1, time);
			handler.sendMessage(msg);
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
