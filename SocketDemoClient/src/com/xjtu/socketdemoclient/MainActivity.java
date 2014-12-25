package com.xjtu.socketdemoclient;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
/**
 * 通过Socket向服务端发送一条信息，服务端返回一条相同的信息
 * @author renzhongfeng
 *
 */
public class MainActivity extends Activity {

	private EditText sendInfo = null;
	private TextView receivedInfo = null;
	//用handler处理服务器端传回来的数据，将信息显示到页面上
	private Handler handler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			if(msg.what == 0x123){
				receivedInfo.setText(msg.getData().getString("msg"));
			}
		}
		
	};
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		sendInfo = (EditText)findViewById(R.id.send_info);
		receivedInfo = (TextView)findViewById(R.id.recieve_info);
	}
	/**
	 * layout文件中的按钮点击执行方法
	 * @param view
	 */
	public void send(View view){
		//开启一个子线程用于Socket连接，因为Socket连接是一个耗时操作
		Log.i("MainActivity", "send");
		new Thread(){
			public void run(){
				Log.i("MainActivity", "run");
				try {
					//获取用户输入的字符
					String s = sendInfo.getText().toString();
					Log.i("MainActivity", s);
					//实例化一个socket，此时需要捕捉UnknownHostException异常和IOException异常
					Socket socket = new Socket("172.21.23.69", 30002);
					Log.i("MainActivity", "socket.isConnected = "+socket.isConnected());
					//获取socket的输出流,向服务器发送信息
					OutputStream out = socket.getOutputStream();
					//将输出流封装到OutputStreamWriter中，用于传输字符流
					OutputStreamWriter osw = new OutputStreamWriter(out);
					//将字符流封装到缓存中
					BufferedWriter bw = new BufferedWriter(osw);
					//获取socket的输入流
					InputStream is = socket.getInputStream();
					//把InputStream输入流封装到InputStreamReader中，用于传输字符流
					InputStreamReader isr = new InputStreamReader(is);
					//对InputStreamReader输入流进行缓存处理
					BufferedReader br = new BufferedReader(isr);
					System.getProperty("line.separator");
					//向服务器传送字符串s
					bw.write(s);
					bw.flush();	
					bw.close();
					Log.i("MainAcitivity", "bw.flush");
					//接收服务器发送过来的数据；
					Thread.yield();
					String info = br.readLine();
					Log.i("MainAcitivity", "info = " + info);
					//将从服务器端获取到是信息封装到Message中，通过handler传送到主线程
					Message msg = new Message();
					msg.what = 0x123;
					Bundle data = new Bundle();
					data.putString("msg", info);
					msg.setData(data);
					handler.sendMessage(msg);
					//关闭socket 
					//socket.close();
					
				} catch (UnknownHostException e) {
					// TODO Auto-generated catch block
					Log.i("MainActivity", "UnknownHostException");
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					Log.i("MainActivity", "IOException");
					e.printStackTrace();
				} 
			}
		}.start();
	}
}
