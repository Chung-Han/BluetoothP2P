package com.android.ckstudent;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.content.Context;

public class TeacherSocketHandler extends Thread {
	ServerSocket serverSocket = null;
	Context context;

	public TeacherSocketHandler(Context context) throws IOException {
		this.context = context;
        try {
            serverSocket = new ServerSocket(4545);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

	@Override
	public void run() {
		try {
			Socket socket = serverSocket.accept();
			String time;
			
			//receive student name
			InputStream is = socket.getInputStream();
			byte[] buffer = new byte[20];
			is.read(buffer);
			time = currentTime();
			GlobalData globalVariable = (GlobalData)context.getApplicationContext();
			globalVariable.studentName = new String(buffer);
			
			//send current time
			OutputStream os = socket.getOutputStream();
			os.write(time.getBytes());
			os.flush();
			os.close();
			
			socket.close();
			
			//update to database
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		super.run();
	}
	
	public String currentTime(){
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
		String time = sdf.format(new Date());
		return time;
	}
}
