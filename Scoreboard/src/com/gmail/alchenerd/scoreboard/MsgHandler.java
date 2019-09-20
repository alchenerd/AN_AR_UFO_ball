package com.gmail.alchenerd.scoreboard;

import java.util.Collections;
import java.util.Comparator;

import android.content.Intent;
import android.text.format.Time;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

public class MsgHandler {
	protected MainActivity context;

	public MsgHandler(MainActivity m) {
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
		 */
		try{
			if(packetData[2].equals("04"))
			{
				context.runOnUiThread(new Runnable() {
	                @Override
	                public void run() {
	                    int index = context.playerList.indexOf(packetData[0]);
	                    if(index<0){
	                    	context.playerList.add(packetData[0]);
	                    }
	                    Collections.sort(context.playerList, new Comparator<String>(){

							@Override
							public int compare(String arg0, String arg1) {
								// TODO Auto-generated method stub
								return Integer.valueOf(arg0).compareTo(Integer.valueOf(arg1));
							}});
	                    String playerString = "";
	                    for(int i = 0; i<context.playerList.size(); i++){
	                    	playerString+=context.playerList.get(i)+"\n";
	                    	}
	                    context.playerTextView.setText(playerString);
	                    //do player ack handling
	                }
	            });
			}
			else if(packetData[2].equals("05"))
			{
				if(packetData[6].equals("ask")){
					// handle ask for game packet
					// note: not in use
				}
				else if(packetData[6].equals("prepare")){
					// handle game attempt packet
				}
				else if(packetData[6].equals("done")){
					// handle game ended packet
					RoomData newRoom = new RoomData(Integer.valueOf(packetData[0]), Integer.valueOf(packetData[1]), Integer.valueOf(packetData[6]), Integer.valueOf(packetData[7]), Integer.valueOf(packetData[8]));
					for(RoomData r : context.roomList){
						if(r.getData("player1ID").equals(newRoom.getData("player1ID"))){
							//remove room
							context.roomList.remove(r);
						}
					}
					sortAndRedrawButtons();
				}
				else{
					// handle game data packet
					final RoomData newRoom = new RoomData(Integer.valueOf(packetData[0]), Integer.valueOf(packetData[1]), Integer.valueOf(packetData[6]), Integer.valueOf(packetData[7]), Integer.valueOf(packetData[8]));
					/*
					 * context.runOnUiThread(new Runnable() {
			            @Override
			            public void run() {
			            	TextView tv = (TextView) context.findViewById(R.id.textView1);
			            	tv.append("P2 is:"+newRoom.getData("player2Score")+"\n");
			            	context.chatScrollView.post(new Runnable() {
		                    	@Override
				                public void run() {
				                	context.chatScrollView.fullScroll(ScrollView.FOCUS_DOWN);
				                    }
				                });
			            	}
			            });
			            */
					boolean isInRoomList = false;
					for(RoomData r : context.roomList){
						if(r.getData("player1ID").equals(newRoom.getData("player1ID"))){
							//update score
							isInRoomList = true;
							r.setData("player1Score", Integer.valueOf(newRoom.getData("player1Score")));
							r.setData("player2Score", Integer.valueOf(newRoom.getData("player2Score")));
						}
						if(r.getData("player2ID").equals(newRoom.getData("player2ID"))){
							//update score
							isInRoomList = true;
							r.setData("player2Score", Integer.valueOf(newRoom.getData("player1Score")));
							r.setData("player1Score", Integer.valueOf(newRoom.getData("player2Score")));
						}
					}
					if(isInRoomList == false){
						context.roomList.add(newRoom);
					}
					sortAndRedrawButtons();
				}
			}
			else if(packetData[2].equals("06"))
			{
				context.runOnUiThread(new Runnable() {
	                @Override
	                public void run() {
	                	String chatMessage = "";
	                	for(int i = 3; i<packetData.length-5; i++){
	                		chatMessage += packetData[i]+' ';
	                	}
	                    context.chatTextView.append("\n"+
	                    		packetData[0]+":"+chatMessage
	    						//do message handling
	    						);
	                    context.chatScrollView.post(new Runnable() {
	                    	@Override
			                public void run() {
			                	context.chatScrollView.fullScroll(ScrollView.FOCUS_DOWN);
			                    }
			                });
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
	void sortAndRedrawButtons(){
		Collections.sort(context.roomList, new Comparator<RoomData> (){

			@Override
			public int compare(RoomData r0, RoomData r1) {
				// TODO Auto-generated method stub
				if(Integer.valueOf(r0.getData("player1ID")) < Integer.valueOf(r1.getData("player1ID"))){
					return 1;
				}
				else return 0;
			}});
		context.runOnUiThread(new Runnable() {
            @Override
            public void run() {
            	//adds button
            	context.roomLinearLayout = (LinearLayout)context.findViewById(R.id.roomLinearLayout);
            	context.roomLinearLayout.removeAllViews();
            	for(RoomData r : context.roomList){
            		Button b = new Button(context);
            		b.setText(r.getData("player1ID")+"("+r.getData("player1Score")+") VS "+r.getData("player2ID")+"("+r.getData("player2Score")+")");
            		final String port = r.getData("port");
            		b.setOnClickListener(new Button.OnClickListener(){
            			@Override
                		public void onClick(View v) {
                			//Switch to report page
                			Intent intent = new Intent(context, RoomActivity.class);
                			intent.putExtra("port", port);
                			context.startActivity(intent);
                			}
                		});
            		context.roomLinearLayout.addView(b);
            		}
            	}
            });
		}
	}

