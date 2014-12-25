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
 * ͨ��Socket�����˷���һ����Ϣ������˷���һ����ͬ����Ϣ
 * @author renzhongfeng
 *
 */
public class MainActivity extends Activity {

	private EditText sendInfo = null;
	private TextView receivedInfo = null;
	//��handler����������˴����������ݣ�����Ϣ��ʾ��ҳ����
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
	 * layout�ļ��еİ�ť���ִ�з���
	 * @param view
	 */
	public void send(View view){
		//����һ�����߳�����Socket���ӣ���ΪSocket������һ����ʱ����
		Log.i("MainActivity", "send");
		new Thread(){
			public void run(){
				Log.i("MainActivity", "run");
				try {
					//��ȡ�û�������ַ�
					String s = sendInfo.getText().toString();
					Log.i("MainActivity", s);
					//ʵ����һ��socket����ʱ��Ҫ��׽UnknownHostException�쳣��IOException�쳣
					Socket socket = new Socket("172.21.23.69", 30002);
					Log.i("MainActivity", "socket.isConnected = "+socket.isConnected());
					//��ȡsocket�������,�������������Ϣ
					OutputStream out = socket.getOutputStream();
					//���������װ��OutputStreamWriter�У����ڴ����ַ���
					OutputStreamWriter osw = new OutputStreamWriter(out);
					//���ַ�����װ��������
					BufferedWriter bw = new BufferedWriter(osw);
					//��ȡsocket��������
					InputStream is = socket.getInputStream();
					//��InputStream��������װ��InputStreamReader�У����ڴ����ַ���
					InputStreamReader isr = new InputStreamReader(is);
					//��InputStreamReader���������л��洦��
					BufferedReader br = new BufferedReader(isr);
					System.getProperty("line.separator");
					//������������ַ���s
					bw.write(s);
					bw.flush();	
					bw.close();
					Log.i("MainAcitivity", "bw.flush");
					//���շ��������͹��������ݣ�
					Thread.yield();
					String info = br.readLine();
					Log.i("MainAcitivity", "info = " + info);
					//���ӷ������˻�ȡ������Ϣ��װ��Message�У�ͨ��handler���͵����߳�
					Message msg = new Message();
					msg.what = 0x123;
					Bundle data = new Bundle();
					data.putString("msg", info);
					msg.setData(data);
					handler.sendMessage(msg);
					//�ر�socket 
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
