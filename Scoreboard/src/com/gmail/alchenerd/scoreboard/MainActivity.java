package com.gmail.alchenerd.scoreboard;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.ArrayList;
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
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
	//main activity aka. lobby
	LinearLayout roomLinearLayout;
	ScrollView chatScrollView;
	TextView chatTextView;
	TextView playerTextView;
	ArrayList<String> playerList;
	ArrayList<RoomData> roomList;
	Button sendButton;
	//Button dummyRoomButton;
	EditText chatEditText;
	Time t;
	WifiManager wifi;
	MsgHandler msgHandler = new MsgHandler(this);
	private static final int UDP_SERVER_PORT = 51202;
	
	Handler handler=new Handler() {
		        @Override
		        public void handleMessage(Message msg) { 
		            super.handleMessage(msg);
		            String msgString = (String)msg.obj;     
		            msgHandler.handle(msgString);
		        }
		    };
		    
	AtomicBoolean isRunning=new AtomicBoolean(false);
	boolean clearRoomFlipFlop = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		chatTextView=(TextView) findViewById(R.id.textView1);
		playerTextView=(TextView) findViewById(R.id.textView4);
		chatScrollView=(ScrollView) findViewById(R.id.scrollView1);
		chatEditText=(EditText) findViewById(R.id.editText1);
		sendButton=(Button) findViewById(R.id.button1);
		//dummyRoomButton=(Button) findViewById(R.id.button2);
		playerList = new ArrayList<String>();
		roomList = new ArrayList<RoomData>();
		
		//register chat send button
		sendButton.setOnClickListener(new Button.OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(!chatEditText.getText().toString().equals(""))
				{
					String sendData = chatEditText.getText().toString();
					SendPacketTask task = new SendPacketTask (UDP_SERVER_PORT, wifi);
					task.execute(sendData);
					chatEditText.setText("");
				}
			}
		});
		
		//this is a dummy room button for testing
		/*
		dummyRoomButton.setOnClickListener(new Button.OnClickListener(){

			@Override
			public void onClick(View v) {
				//Switch to report page
				Intent intent = new Intent();
				intent.setClass(MainActivity.this, RoomActivity.class);
				intent.putExtra("port", "1203");
				startActivity(intent);
			}
		});
		*/
		
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
	    playerList.add(""+(wifi.getConnectionInfo().getIpAddress()>>24&0xff));
	}
	
	 DatagramSocket ds = null;
	 
	 public void onStart() {
		         super.onStart();
		         String playerString = "";
			      for(int i = 0; i<playerList.size(); i++){
			    	  playerString+=playerList.get(i)+"\n";
			      }
			      playerTextView.setText(playerString);
		         chatTextView.setText("You are now on chat");
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
		                         data = new String(recevieData, 0, dp.getLength());
		                         handler.sendMessage(handler.obtainMessage(1,data));
		                     }
		                 }
		                 catch (Throwable t) {
		                     // just end the background thread
		                 }
		             }
		         });
		         
		         Thread clearRoom=new Thread(new Runnable() {
		             public void run() {
		                 try {
		                	 for(;isRunning.get();)
		                	 {
		                		 Thread.sleep(1000);
		                		 if(clearRoomFlipFlop)
		                		 {
		                		 MainActivity.this.roomList.clear();
		                		 }
		                		 else if(!clearRoomFlipFlop)
		                		 {
		                		 MainActivity.this.msgHandler.sortAndRedrawButtons();
		                		 }
		                		 clearRoomFlipFlop = !clearRoomFlipFlop;
		                	 }
		                 }
		                 catch (Throwable t) {
		                     // just end the background thread
		                 }
		             }
		         });
		  
		         isRunning.set(true);
		         background.start();
		         clearRoom.start();
		     }
		  
		     public void onStop() {
		         super.onStop();
		         isRunning.set(false);
		         ds.close();
		     }
		     
		     /*
		     @Override
		     public boolean onKeyDown(int keyCode, KeyEvent event) {
		         if ( keyCode == KeyEvent.KEYCODE_MENU ) {
		             //handle pressed menu
		        	 //sends a fake Msg to MsgHandler
			         msgHandler.handle("01 02 05 0 0 0 0 0 1234");
		             return true;
		         }
		         return super.onKeyDown(keyCode, event);
		     }
		     */
}