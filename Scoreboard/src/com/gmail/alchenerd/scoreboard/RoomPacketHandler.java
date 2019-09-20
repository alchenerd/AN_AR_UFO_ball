package com.gmail.alchenerd.scoreboard;

import android.widget.TextView;



public class RoomPacketHandler {
	protected RoomActivity context;

	public RoomPacketHandler(RoomActivity m) {
		// TODO Auto-generated constructor stub
		this.context = m;
	}
	
	public void handle(String str) {
		 final String[] packetData = str.split(" ");
		/*
		* [0]: source ip
		* [1]: dest ip
		* [2]: msg type
		* [3]: x azis value
		* [4]: y azis value
		* [5]: z azis value
		* [6]: p1 score
		* [7]: p2 score
		* [8]: port
		* 
		* only handle type = 00(ball) 01(p1) 02(p2) 10(score)
		*/
		 try{
				if(packetData[2].equals("00")){
					//do ball handling
					//out of bound handling
					/*
					if(Float.valueOf(packetData[3])>RoomActivity.maxX)
					{packetData[3] = ""+RoomActivity.maxX;}
					if(Float.valueOf(packetData[3])<RoomActivity.minX)
					{packetData[3] = ""+RoomActivity.minX;}
					if(Float.valueOf(packetData[4])>RoomActivity.maxY)
					{packetData[4] = ""+RoomActivity.maxY;}
					if(Float.valueOf(packetData[4])<RoomActivity.minY)
					{packetData[4] = ""+RoomActivity.minY;}
					*/
					//change ball data
					context.ball.setData("X", Float.valueOf(packetData[3]));
					context.ball.setData("Y", Float.valueOf(packetData[4]));
					context.runOnUiThread(new Runnable() {     
				        public void run()     
				        {     
				        	GameCanvas gameCanvas = (GameCanvas)context.findViewById(R.id.canvas);
				        	gameCanvas.invalidate(); 
				        }     
				    });
					}
				else if(packetData[2].equals("01")){
					//do player 1 handling
					//out of bound handling
					if(Float.valueOf(packetData[3])>RoomActivity.maxX)
					{packetData[3] = ""+RoomActivity.maxX;}
					if(Float.valueOf(packetData[3])<RoomActivity.minX)
					{packetData[3] = ""+RoomActivity.minX;}
					if(Float.valueOf(packetData[4])>RoomActivity.maxY)
					{packetData[4] = ""+RoomActivity.maxY;}
					if(Float.valueOf(packetData[4])<RoomActivity.minY)
					{packetData[4] = ""+RoomActivity.minY;}
					//change player1 data
					context.player1.setData("X", Float.valueOf(packetData[3]));
					context.player1.setData("Y", Float.valueOf(packetData[4]));
					}
				else if(packetData[2].equals("02")){
					//do player 2 handling
					//out of bound handling
					if(Float.valueOf(packetData[3])>RoomActivity.maxX)
					{packetData[3] = ""+RoomActivity.maxX;}
					if(Float.valueOf(packetData[3])<RoomActivity.minX)
					{packetData[3] = ""+RoomActivity.minX;}
					if(Float.valueOf(packetData[4])>RoomActivity.maxY)
					{packetData[4] = ""+RoomActivity.maxY;}
					if(Float.valueOf(packetData[4])<RoomActivity.minY)
					{packetData[4] = ""+RoomActivity.minY;}
					//change player2 data
					context.player2.setData("X", Float.valueOf(packetData[3]));
					context.player2.setData("Y", Float.valueOf(packetData[4]));
					}
				else if(packetData[2].equals("10")){
					//do score handling
					//change score
					context.blueScore = (TextView)context.findViewById(R.id.text_bluescore);
					context.redScore = (TextView)context.findViewById(R.id.text_redscore);
					context.runOnUiThread(new Runnable() {     
				        public void run()     
				        {     
				        	context.blueScore.setText("0"+packetData[6]);
							context.redScore.setText("0"+packetData[7]);
				        }     
				    });
					}
				else{
					/*context.runOnUiThread(new Runnable() {
		                @Override
		                public void run() {
		                    context.chatTextView.append("\n"+
		                    		"can't recognize type\n"
		    						//debug zone
		    						);
		                }
		            });*/
				}
			}
			catch(Throwable t1){
			}
		}
	}