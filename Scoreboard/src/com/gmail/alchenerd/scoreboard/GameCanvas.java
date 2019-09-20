package com.gmail.alchenerd.scoreboard;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;

public class GameCanvas extends View {
	RoomActivity context;
	int screenWidth;
	int screenHeight;
	Bitmap bitmapBackgroundAdjusted;
	Bitmap bitmapBall;
	int ballAdjustX;
	int ballAdjustY;
	Bitmap bitmapP1;
	Bitmap bitmapP2;
	int playerAdjustX;
	int playerAdjustY;
	public GameCanvas(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		this.context = (RoomActivity)context;
		DisplayMetrics dm = new DisplayMetrics();
		this.context.getWindowManager().getDefaultDisplay().getMetrics(dm);
		screenWidth = dm.widthPixels;
		screenHeight = dm.heightPixels;
		init();
	}
	
	public GameCanvas(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		this.context = (RoomActivity)context;
		DisplayMetrics dm = new DisplayMetrics();
		this.context.getWindowManager().getDefaultDisplay().getMetrics(dm);
		screenWidth = dm.widthPixels;
		screenHeight = dm.heightPixels;
		init();
	}

	public GameCanvas(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		// TODO Auto-generated constructor stub
		this.context = (RoomActivity)context;
		DisplayMetrics dm = new DisplayMetrics();
		this.context.getWindowManager().getDefaultDisplay().getMetrics(dm);
		screenWidth = dm.widthPixels;
		screenHeight = dm.heightPixels;
		init();
	}
	
	private void init()
	{
		Resources resources = getResources();
		Bitmap bitmapBackground = BitmapFactory.decodeResource(resources, R.drawable.hh);
		Bitmap bitmapBackgroundAdjusted = Bitmap.createScaledBitmap
			     (bitmapBackground, screenWidth, screenHeight, false);
		this.bitmapBackgroundAdjusted = bitmapBackgroundAdjusted;
		Bitmap bitmapBall = BitmapFactory.decodeResource(resources, R.drawable.ball);
		this.bitmapBall = bitmapBall;
		this.ballAdjustX = bitmapBall.getHeight()/2;
		this.ballAdjustY = bitmapBall.getWidth()/2;
		Bitmap bitmapP1 = BitmapFactory.decodeResource(resources, R.drawable.player);
		Bitmap bitmapP2 = BitmapFactory.decodeResource(resources, R.drawable.player);
		this.bitmapP1 = bitmapP1;
		this.bitmapP2 = bitmapP2;
		this.playerAdjustX = bitmapP1.getHeight()/2;
		this.playerAdjustY = bitmapP1.getWidth()/2;
	}
	
	public void onDraw(Canvas canvas) {
		canvas.drawColor(Color.BLACK);
		//draw background
		canvas.drawBitmap(bitmapBackgroundAdjusted,0,0,null);
		//draw ball
        canvas.drawBitmap(bitmapBall,
        		(context.ball.getData("Y")-RoomActivity.minY)/(RoomActivity.maxY-RoomActivity.minY)*screenWidth-ballAdjustY,
        		(context.ball.getData("X")-RoomActivity.minX)/(RoomActivity.maxX-RoomActivity.minX)*screenHeight-ballAdjustX,
        		null);
        //draw P1
        canvas.drawBitmap(bitmapP1,
        		(context.player1.getData("Y")-RoomActivity.minY)/(RoomActivity.maxY-RoomActivity.minY)*screenWidth-playerAdjustY,
        		(context.player1.getData("X")-RoomActivity.minX)/(RoomActivity.maxX-RoomActivity.minX)*screenHeight-playerAdjustX,
        		null);
        //draw P2
        canvas.drawBitmap(bitmapP2,
        		(context.player2.getData("Y")-RoomActivity.minY)/(RoomActivity.maxY-RoomActivity.minY)*screenWidth-playerAdjustY,
        		(context.player2.getData("X")-RoomActivity.minX)/(RoomActivity.maxX-RoomActivity.minX)*screenHeight-playerAdjustX,
        		null);
	}
}
