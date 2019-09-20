package com.gmail.alchenerd.scoreboard;

import java.util.Random;

import android.os.AsyncTask;

public class RandomizeTask extends AsyncTask<RoomActivity, Void, Void> {

	@Override
	protected Void doInBackground(RoomActivity... context) {
		// TODO Auto-generated method stub
		//do random assigns on ball, p1, and p2
   	 	Random ran = new Random();
   	 	//ball.x
   	 	float next = context[0].ball.getData("X");
   	 	next += (ran.nextInt(2)==1)?-1:1;
   	 	context[0].ball.setData("X", next);
   	 	//ball.y
   	 	next = context[0].ball.getData("Y");
   	 	next += (ran.nextInt(2)==1)?-1:1;
   	 	context[0].ball.setData("Y", next);
   	 	//player1.y
   	 	next = context[0].player1.getData("Y");
   	 	next += (ran.nextInt(2)==1)?-1:1;
   	 	context[0].player1.setData("Y", next);
   	 	//player2.y
   	 	next = context[0].player2.getData("Y");
   	 	next += (ran.nextInt(2)==1)?-1:1;
   	 	context[0].player2.setData("Y", next);
		return null;
	}

}
