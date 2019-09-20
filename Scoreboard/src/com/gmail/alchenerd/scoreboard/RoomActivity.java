package com.gmail.alchenerd.scoreboard;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class RoomActivity extends Activity {
	//some global variables, not writen in stone
	static final float maxX = 240;
	static final float minX = -240;
	static final float maxY = 510;
	static final float minY = -510;
	static final float borderThickness = 70;
	Coord3D ball = new Coord3D(0,0,0);
	Coord3D player1 = new Coord3D(0,maxY,0);
	Coord3D player2 = new Coord3D(0,minY,0);
	int player1Score = 0;
	int player2Score = 0;
	GameCanvas gameCanvas;
	TextView blueScore;
	TextView redScore;
	RelativeLayout mainLayout;
	int roomPort;
	RoomPacketHandler roomPacketHandler = new RoomPacketHandler(this);
	Handler handler=new Handler() {
        @Override
        public void handleMessage(Message msg) { 
            super.handleMessage(msg);
            String msgString = (String)msg.obj;     
            roomPacketHandler.handle(msgString);
        }
    };
    AtomicBoolean isRunning=new AtomicBoolean(false);
	
	public RoomActivity() {
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//set room port
		String strPort = getIntent().getStringExtra("port");
		roomPort = Integer.valueOf(strPort);
		gameCanvas = (GameCanvas)findViewById(R.id.canvas);
		setContentView(R.layout.activity_room);
		}
	
	 DatagramSocket ds = null;
	 
	 public void onStart() {
		         super.onStart();
		         try {
					ds = new DatagramSocket(roomPort);
					ds.setSoTimeout(2000);
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
		                 catch (SocketTimeoutException STE) {
		                     // socket timeout
		                	 blueScore = (TextView) findViewById(R.id.text_bluescore);
		                	 redScore = (TextView) findViewById(R.id.text_redscore);
		                	 runOnUiThread(new Runnable(){
		                         public void run() {
		                             Toast.makeText(getApplicationContext(),"(RED:"+redScore.getText()+" VS BLUE:"+blueScore.getText()+")",Toast.LENGTH_LONG).show();
		                         }
		                       });
		                	 finish();
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
		     
		     /*
		     @Override
		     public boolean onKeyDown(int keyCode, KeyEvent event) {
		         if ( keyCode == KeyEvent.KEYCODE_MENU ) {
		             //handle pressed menu
		        	 new RandomizeTask().execute(this);
		        	 gameCanvas.invalidate();
		             return true;
		         }
		         return super.onKeyDown(keyCode, event);
		     }
		     */
}
