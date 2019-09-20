package com.gmail.alchenerd.wifibroadcast;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.concurrent.atomic.AtomicBoolean;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.format.Time;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

public class WifiBroadcastActivity extends Activity {
	ScrollView mainScrollView;
	TextView mainTextView;
	Button sendButton;
	Button portButton;
	EditText mainEditText;
	EditText portEditText;
	Time t;
	WifiManager wifi;
	int UDP_SERVER_PORT = 51202;
	//TODO: determine a port.
	
	Handler handler=new Handler() {
		        @Override
		        public void handleMessage(Message msg) { 
		            super.handleMessage(msg);
		            String msgString = (String)msg.obj;     
		            t.setToNow();
		            mainTextView.append("\n"+String.valueOf(t.hour)+":"+
		                    String.valueOf(t.minute)+":"+
		                    String.valueOf(t.second)+"=>"+msgString);
		            mainScrollView.post(new Runnable() {

		                @Override
		                public void run() {
		                	mainScrollView.fullScroll(ScrollView.FOCUS_DOWN);
		                    }
		                });
		        }
		    };
		    
	AtomicBoolean isRunning=new AtomicBoolean(false);
	DatagramSocket ds = null;
	DatagramSocket sds = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_wifi_broadcast);
		mainTextView=(TextView) findViewById(R.id.textView1);
		sendButton=(Button) findViewById(R.id.button1);
		portButton=(Button) findViewById(R.id.button2);
		mainEditText=(EditText) findViewById(R.id.editText1);
		portEditText=(EditText) findViewById(R.id.editText2);
		portEditText.setText(""+UDP_SERVER_PORT);
		mainScrollView=(ScrollView) findViewById(R.id.scrollView1);
		t=new Time();
		wifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);
	    if(wifi.isWifiEnabled()==false)
	    {
	      AlertDialog.Builder dialog = new AlertDialog.Builder(this);
	      dialog.setTitle("Hello");
	      dialog.setMessage("Your Wi-Fi isn't on, enable it now?");
	      dialog.setIcon(android.R.drawable.ic_dialog_info);
	      dialog.setCancelable(false);
	      dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
					
	        @Override
	        public void onClick(DialogInterface dialog, int which) {
	          // TODO Auto-generated method stub
	          wifi.setWifiEnabled(true);
	          Toast.makeText(getApplicationContext(), "enabling...", Toast.LENGTH_LONG).show();	
	        }
	      });
	      dialog.show();
	    }
	}
	
	 public void bt1_OnClick(View view)
	 {
		 String sendData = mainEditText.getText().toString();
		 SendPacketTask task = new SendPacketTask (UDP_SERVER_PORT, wifi);
		 task.execute(sendData);
		 mainEditText.setText("");
		 }
	 
	 public void bt2_OnClick(View view)
	 {
		 UDP_SERVER_PORT = Integer.valueOf(portEditText.getText().toString()) ;
		 portEditText.setText(""+UDP_SERVER_PORT);
		 try {
			 ds = new DatagramSocket(UDP_SERVER_PORT);
			 } catch (SocketException e) {
				// TODO Auto-generated catch block
			}
		 }
	 
	 public void onStart() {
		         super.onStart();
		         mainTextView.setText("Welcome to wifi broadcast chat!");
		         try {
					ds = new DatagramSocket(UDP_SERVER_PORT);
				} catch (SocketException e) {
					// TODO Auto-generated catch block
				}
		         Thread background=new Thread(new Runnable() {
		             public void run() {
		                 try {
		                     String data;
		                     byte[] recevieData = new byte[1024];
		                     DatagramPacket dp = new DatagramPacket(recevieData, recevieData.length);
		                     for (;isRunning.get();) {
		                         ds.receive(dp);
		                         Thread.sleep(100);
		                         data = new String(recevieData, 0, dp.getLength());
		                         handler.sendMessage(handler.obtainMessage(1,data));
		                     }
		                 }
		                 catch (Throwable t) {
		                     // just end the background thread
		                 }
		             }
		         });
		  
		         isRunning.set(true);
		         background.start();
		     }
		  
		     public void onStop() {
		         super.onStop();
		         isRunning.set(false);
		         ds.close();
		     }
}