// Copyright 2007-2014 metaio GmbH. All rights reserved.
package com.metaio.Example;

//import android.hardware.Sensor;
//import android.hardware.SensorEvent;
//import android.hardware.SensorEventListener;
//import android.hardware.SensorManager;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.concurrent.atomic.AtomicBoolean;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.hardware.Camera;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.metaio.Example.SendPacketTask;
import com.metaio.sdk.ARViewActivity;
import com.metaio.sdk.GestureHandlerAndroid;
import com.metaio.sdk.MetaioDebug;
import com.metaio.sdk.jni.ELIGHT_TYPE;
import com.metaio.sdk.jni.GestureHandler;
import com.metaio.sdk.jni.IGeometry;
import com.metaio.sdk.jni.ILight;
import com.metaio.sdk.jni.IMetaioSDKCallback;
import com.metaio.sdk.jni.ImageStruct;
import com.metaio.sdk.jni.TrackingValuesVector;
import com.metaio.sdk.jni.Vector3d;
import com.metaio.tools.io.AssetsManager;

public class TutorialTrackingSamples extends ARViewActivity {
	boolean[] item = new boolean[] { false, false, false };
	private ILight mPointLight;
	//private ILight mPointLight1;
	//private ILight mPointLight2;
	//private ILight mPointLight3;
	private SensorManager Mgr;
	TextView value_0;
	float light_val, light_new, light_abm, diffuse;
	int light_flag = 0;
	// private IGeometry mPointLightGeo;
	private GestureHandlerAndroid mGestureHandler;
	private IGeometry mMetaioMan;
	private IGeometry mchair;
	private IGeometry mpic;
	private IGeometry mpic1;
	float upX, upY, downX, downY;
	String trackingConfigFile;
	float up = 0, down = 0, left = 0, right = 0;
	private MetaioSDKCallbackHandler mCallbackHandler;
	int size = 0, flag = 0, flag2 = 0, flag3 = 0;
	int scr_width, scr_height;
	int con1 = 0;
	boolean lightcon = false;
	float l_R, l_G, l_B, l_Value;
	Bitmap show;

	WifiManager wifi;
	private static final int UDP_SERVER_PORT = 1202;
	DatagramSocket ds = null;
	AtomicBoolean isRunning = new AtomicBoolean(false);
	
	Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			String msgString = (String) msg.obj;	
			final String[] locationXYZName = msgString.split(",");
			MetaioDebug.log("String is" + msgString + ",Man is " + locationXYZName[3]);
			if(locationXYZName[3].equals(mMetaioMan.getName()) == true)
			{
				mMetaioMan.setTranslation(new Vector3d(Float.valueOf(locationXYZName[0]),Float.valueOf(locationXYZName[1]),Float.valueOf(locationXYZName[2])));
				MetaioDebug.log("Moving is OK!!");
			}
			if(locationXYZName[3].equals(mchair.getName()) == true)
				mchair.setTranslation(new Vector3d(Float.valueOf(locationXYZName[0]),Float.valueOf(locationXYZName[1]),Float.valueOf(locationXYZName[2])));
			/*runOnUiThread(new Runnable(){
				@Override
				public void run()
				{
					TextView lo = (TextView)findViewById(R.id.Text_X);
					lo.setText(locationXYZName[0]);
					lo = (TextView)findViewById(R.id.Text_Y);
					lo.setText(locationXYZName[1]);
					lo = (TextView)findViewById(R.id.Text_Z);
					lo.setText(locationXYZName[2]);
				}
			});*/
		}
	};
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mCallbackHandler = new MetaioSDKCallbackHandler();
		Mgr = (SensorManager) getSystemService(SENSOR_SERVICE);
		wifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);
		if (wifi.isWifiEnabled() == false) {
			AlertDialog.Builder dialog = new AlertDialog.Builder(this);
			dialog.setTitle("Hello");
			dialog.setMessage("Your Wi-Fi isn't on, enable it now?");
			dialog.setIcon(android.R.drawable.ic_dialog_info);
			dialog.setCancelable(false);
			dialog.setPositiveButton("OK",
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							wifi.setWifiEnabled(true);
							Toast.makeText(getApplicationContext(),
									"enabling...", Toast.LENGTH_LONG).show();
						}
					});
			dialog.show();
		}
	}

	public void onStart() {
		super.onStart();
		try {
			ds = new DatagramSocket(UDP_SERVER_PORT);
		} catch (SocketException e) {
			// TODO Auto-generated catch block
		}
		Thread background = new Thread(new Runnable() {
			public void run() {
				try {
					String data;
					byte[] recevieData = new byte[1024];
					DatagramPacket dp = new DatagramPacket(recevieData,
							recevieData.length);
					for (; isRunning.get();) {
						Thread.sleep(100);
						ds.receive(dp);
						data = new String(recevieData, 0, dp.getLength());
						handler.sendMessage(handler.obtainMessage(1, data));
					}
				} catch (Throwable t) {
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
	
	
	public void but2_onClick(View v) {
		metaioSDK.requestScreenshot();
	}

	public void but3_onClick(View v) {
		 if(lightcon == false)
		 lightcon = true;
		metaioSDK.requestCameraImage();
	}

	public static Bitmap ice(Bitmap bmp) {
		int width = bmp.getWidth();
		int height = bmp.getHeight();
		Bitmap bitmap = Bitmap.createBitmap(width, height,
				Bitmap.Config.RGB_565);
		int dst[] = new int[width * height];
		bmp.getPixels(dst, 0, width, 0, 0, width, height);
		int R, G, B, pixel;
		int pos, pixColor;
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				pos = y * width + x;
				pixColor = dst[pos]; // 獲取圖片當前點的像素值
				R = Color.red(pixColor); // 獲取RGB三原色
				G = Color.green(pixColor);
				B = Color.blue(pixColor);
				pixel = R - G - B;
				pixel = pixel * 3 / 2;

				if (pixel < 0)
					pixel = -pixel;
				if (pixel > 255)
					pixel = 255;

				R = pixel; // 計算後重置R值，以下類同
				pixel = G - B - R;
				pixel = pixel * 3 / 2;

				if (pixel < 0)
					pixel = -pixel;
				if (pixel > 255)
					pixel = 255;

				G = pixel;
				pixel = B - R - G;
				pixel = pixel * 3 / 2;

				if (pixel < 0)
					pixel = -pixel;
				if (pixel > 255)
					pixel = 255;
				B = pixel;
				dst[pos] = Color.rgb(R, G, B); // 重置當前點的像素值
			} // x
		} // y
		bitmap.setPixels(dst, 0, width, 0, 0, width, height);
		return bitmap;
	}

	public Bitmap test1(Bitmap bmp) {
		int width = bmp.getWidth();
		int height = bmp.getHeight();
		// Bitmap bitmap = Bitmap.createBitmap(width, height,
		// Bitmap.Config.RGB_565);
		// int dst[] = new int[width * height];
		// bmp.getPixels(dst, 0, width, 0, 0, width, height);
		int R, G, B, pixel;
		int pos, pixColor;
		for (int y = height - 10; y < height; y++) {
			for (int x = width - 10; x < width; x++) {
				int P = bmp.getPixel(x, y);
				R = Color.red(P) + 10;
				G = Color.green(P) + 10;
				B = Color.blue(P) + 10;

				if (R >= 255)
					R = 255;
				if (G >= 255)
					G = 255;
				if (B >= 255)
					B = 255;
				P = Color.rgb(R, G, B);
				bmp.setPixel(x, y, P);
				MetaioDebug.log("OKOK!!");
			} // x
		} // y
		return bmp;
	}

	static public void decodeYUV420SP(int[] rgb, byte[] yuv420sp, int width,
			int height) {
		final int frameSize = width * height;
		for (int j = 0, yp = 0; j < height; j++) {
			int uvp = frameSize + (j >> 1) * width, u = 0, v = 0;
			for (int i = 0; i < width; i++, yp++) {
				int y = (0xff & ((int) yuv420sp[yp])) - 16;
				if (y < 0)
					y = 0;
				if ((i & 1) == 0) {
					v = (0xff & yuv420sp[uvp++]) - 128;
					u = (0xff & yuv420sp[uvp++]) - 128;
				}

				int y1192 = 1192 * y;
				int r = (y1192 + 1634 * v);
				int g = (y1192 - 833 * v - 400 * u);
				int b = (y1192 + 2066 * u);

				if (r < 0)
					r = 0;
				else if (r > 262143)
					r = 262143;
				if (g < 0)
					g = 0;
				else if (g > 262143)
					g = 262143;
				if (b < 0)
					b = 0;
				else if (b > 262143)
					b = 262143;
				rgb[yp] = 0xff000000 | ((r << 6) & 0xff0000)
						| ((g >> 2) & 0xff00) | ((b >> 10) & 0xff);
			}
		}
	}

	/*
	 * private IGeometry createLightGeometry() { final String modelPath =
	 * AssetsManager.getAssetPath(getApplicationContext(),
	 * "TutorialDynamicLighting/Assets/sphere_10mm.obj"); if (modelPath != null)
	 * { IGeometry model = metaioSDK.createGeometry(modelPath); if (model !=
	 * null) return model; else MetaioDebug.log(Log.ERROR,
	 * "Error loading geometry: " + modelPath); } else
	 * MetaioDebug.log(Log.ERROR,
	 * "Could not find 3D model to use as light indicator");
	 * 
	 * return null; }
	 */
	/*
	 * //裡面的upX upY downX downY 是float全域變數
	 * 
	 * @Override public boolean onTouchEvent(MotionEvent event) {
	 * 
	 * float X = event.getX(); // 觸控的 X 軸位置 float Y = event.getY(); // 觸控的 Y 軸位置
	 * // if (mMetaioMan != null) // { switch (event.getAction()) { // 判斷觸控的動作
	 * 
	 * case MotionEvent.ACTION_DOWN: // 按下 downX = event.getX(); downY =
	 * event.getY();
	 * 
	 * break; case MotionEvent.ACTION_MOVE: // 拖曳
	 * 
	 * break; case MotionEvent.ACTION_UP: // 放開
	 * //Log.d("onTouchEvent-ACTION_UP","UP"); upX = event.getX(); upY =
	 * event.getY(); float x=Math.abs(upX-downX); float y=Math.abs(upY-downY);
	 * float z=(float)(Math.sqrt(x*x+y*y));
	 * 
	 * int jiaodu=Math.round((float)(Math.asin(y/z)/Math.PI*180));//角度
	 * 
	 * if (upY < downY && jiaodu>45) {//上
	 * Log.d("onTouchEvent-ACTION_UP","角度:"+jiaodu+", 動作:上"); up = up + z;
	 * mMetaioMan.setTranslation(new Vector3d(0,up, 0),true); }else if(upY >
	 * downY && jiaodu>45) {//下
	 * Log.d("onTouchEvent-ACTION_UP","角度:"+jiaodu+", 動作:下"); down = down - z;
	 * mMetaioMan.setTranslation(new Vector3d(0,down, 0),true); }else if(upX <
	 * downX && jiaodu <= 45) {//左
	 * Log.d("onTouchEvent-ACTION_UP","角度:"+jiaodu+", 動作:左"); left = left+ z;
	 * mMetaioMan.setTranslation(new Vector3d(left,0, 0),true); // 原方向不是向右時，方向轉右
	 * // if (mDirection != EAST) { // mNextDirection = WEST; // } }else if(upX
	 * > downX && jiaodu <= 45) {//右
	 * Log.d("onTouchEvent-ACTION_UP","角度:"+jiaodu+", 動作:右"); right = right - z;
	 * mMetaioMan.setTranslation(new Vector3d(right,0, 0),true); //
	 * 原方向不是向左時，方向轉右 // if (mDirection ! = WEST) { // mNextDirection = EAST; //
	 * } } break; } // }
	 * 
	 * return super.onTouchEvent(event); }
	 */

	@Override
	protected void onDestroy() {
		super.onDestroy();
		mCallbackHandler.delete();
		mCallbackHandler = null;
	}

	@Override
	protected int getGUILayout() {
		return R.layout.tutorial_tracking_samples;
	}

	@Override
	public void onDrawFrame() {
		// if(con1 == 0)
		super.onDrawFrame();
		/*
		 * if(tmpBmp == null) MetaioDebug.log(Log.ERROR,
		 * "getImageCache is null"); else MetaioDebug.log(Log.ERROR,
		 * "Width: "+String.valueOf(tmpBmp.getWidth())
		 * +"Height: "+String.valueOf(tmpBmp.getHeight()));
		 */
		if (metaioSDK != null) {
			// get all detected poses/targets
			TrackingValuesVector poses = metaioSDK.getTrackingValues();

			/*
			 * for (int i=0; i<poses.size(); i++) { if
			 * (poses.get(i).isTrackingState()) { int ids =
			 * poses.get(i).getCoordinateSystemID(); TextView lbout =
			 * (TextView)findViewById(R.id.textView1);
			 * lbout.setText(Integer.toString(ids)); } }
			 */

			// if we have detected one, attach our metaio man to this coordinate
			// system Id
			// if (poses.size() != 0)
			// {
			/*
			 * if(flag == 0) { size = (int)poses.size(); flag = 1; TextView
			 * lbout = (TextView)findViewById(R.id.textView1);
			 * lbout.setText(String.valueOf(size)); Log.d("???????" +
			 * size,"OK"); }
			 */
			// mMetaioMan.setCoordinateSystemID(0);
			// mchair.setCoordinateSystemID(poses.get(1).getCoordinateSystemID());
			// }
			/*
			 * TextView lbout = (TextView)findViewById(R.id.value1);
			 * lbout.setText(Float.toString(up)); up = up + 1;
			 */
		}

		/*
		 * final double time = System.currentTimeMillis() / 1000.0; final
		 * Vector3d lightPos = new Vector3d( 200.0f * (float)Math.cos(time),
		 * 120.0f * (float)Math.sin(0.25f*time), 200.0f *
		 * (float)Math.sin(time));
		 * 
		 * final float FREQ2MUL = 0.4f; final Vector3d lightPos2 = new Vector3d(
		 * 150.0f * (float)(Math.cos(FREQ2MUL*2.2*time) * (1 +
		 * 2+2*Math.sin(FREQ2MUL*0.6*time))), 30.0f *
		 * (float)Math.sin(FREQ2MUL*0.35*time), 150.0f *
		 * (float)Math.sin(FREQ2MUL*2.2*time));
		 * 
		 * final Vector3d directionalLightDir = new Vector3d(
		 * (float)Math.cos(1.2*time), (float)Math.sin(0.25*time),
		 * (float)Math.sin(0.8*time));
		 * 
		 * mPointLight.setTranslation(lightPos);
		 * updateLightIndicator(mPointLightGeo, mPointLight);
		 */

		// updateLightIndicator(mPointLightGeo, mPointLight);
		if (lightcon == true) {
			MetaioDebug.log("L_Value is :" + l_Value);
			// MetaioDebug.log("lightcontrol is running!!");
			if (l_Value > 0.95) {
				l_Value = 0.95f;
			}
			mPointLight.setAmbientColor(new Vector3d(l_Value + 0.25f,
					l_Value + 0.25f, l_Value + 0.25f));
		}
		if (light_flag == 1) {
			// light_flag = 1;
			light_abm = (float) ((0.01 * (light_val)));
			if (light_abm > 0.95) {
				light_abm = 0.95f;
			}
			mPointLight.setAmbientColor(new Vector3d(light_abm, light_abm,
					light_abm));
		}

		light_new = light_val;
		diffuse = light_val;
		// if (lightcon == true)
		// metaioSDK.requestScreenshot();
	}

	public void onButtonClick(View v) {
		finish();
	}

	public void onIdButtonClick(View v) {
		/*
		 * trackingConfigFile =
		 * AssetsManager.getAssetPath(getApplicationContext(),
		 * "TutorialTrackingSamples/Assets/ExtensibleTracking.xml");
		 * MetaioDebug.log("Tracking Config path = "+trackingConfigFile);
		 * 
		 * boolean result =
		 * metaioSDK.setTrackingConfiguration(trackingConfigFile);
		 * MetaioDebug.log("Id Marker tracking data loaded: " + result);
		 * //mMetaioMan.setTranslation(new Vector3d(20f, 20f, 0));
		 * mMetaioMan.setScale(new Vector3d(10f, 10f, 10f));
		 * //mMetaioMan.setRotation(new Rotation(1.5707963f,0,0));
		 */
		if (flag == 0) {
			openalert();
			// mchair.setVisible(true);
			// flag = 1;
		} else {
			// flag = 0;
			// mchair.setVisible(false);
		}

		// up = up + 1;
		// mMetaioMan.setTranslation(new Vector3d(0,up, 0),true);
	}

	private void openalert() {
		final CharSequence[] items = { "櫃子", "椅子", "圖畫" };
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("選擇要顯示的模型");
		builder.setMultiChoiceItems(items, item,
				new DialogSelectionClickHandler());
		builder.setPositiveButton("確定", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
			}
		});
		builder.show();
	}

	public class DialogSelectionClickHandler implements
			DialogInterface.OnMultiChoiceClickListener {
		public void onClick(DialogInterface dialog, int clicked,
				boolean selected) {
			item[clicked] = selected;
			checkshow();
		}
	}

	private void checkshow() {
		if (item[0] == true)
			mMetaioMan.setVisible(true);
		else
			mMetaioMan.setVisible(false);
		if (item[1] == true)
			mchair.setVisible(true);
		else
			mchair.setVisible(false);
		if (item[2] == true)
			mpic.setVisible(true);
		else
			mpic.setVisible(false);
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		super.onTouch(v, event);

		mGestureHandler.onTouch(v, event);

		return true;
	}

	@Override
	protected void loadContents() {
		try {

			// Load desired tracking data for planar marker tracking

			trackingConfigFile = AssetsManager.getAssetPath(
					getApplicationContext(),
					"TutorialTrackingSamples/Assets/SLAM.xml");
			MetaioDebug.log("Tracking Config path = " + trackingConfigFile);
			boolean result = metaioSDK
					.setTrackingConfiguration(trackingConfigFile);
			MetaioDebug.log("Id Marker tracking data loaded: " + result);

			// Load all the geometries. First - Model
			String metaioManModel = AssetsManager.getAssetPath(
					getApplicationContext(),
					"TutorialTrackingSamples/Assets/color.obj");
			String chair = AssetsManager.getAssetPath(getApplicationContext(),
					"TutorialTrackingSamples/Assets/couch.obj");
			String pic = AssetsManager.getAssetPath(getApplicationContext(),
					"TutorialTrackingSamples/Assets/7a3a29f910b9dd4def16aea7d43e9560.jpg");
			String pic1 = AssetsManager
					.getAssetPath(getApplicationContext(),
							"TutorialTrackingSamples/Assets/19300000404632130629997413788.jpg");
			if (metaioManModel != null) {
				mMetaioMan = metaioSDK.createGeometry(metaioManModel);
				mMetaioMan.setName("mMetaioMan");
				mchair = metaioSDK.createGeometry(chair);
				mchair.setName("mchair");
				mpic = metaioSDK.createGeometryFromImage(pic);
				mpic1 = metaioSDK.createGeometryFromImage(pic1);
				if (mMetaioMan != null) {
					// metaioSDK.setAmbientLight(new Vector3d(0.5f,0,0));
					mMetaioMan.setScale(new Vector3d(100f, 100f, 100f));
					mMetaioMan.setVisible(false);
					mchair.setScale(new Vector3d(10f, 10f, 10f));
					mchair.setVisible(false);
					up = 0;
					down = 0;
					// Set geometry properties
					// mMetaioMan.setScale(new Vector3d(20.0f, 20.0f, 20.0f));
					// MetaioDebug.log("Loaded geometry "+metaioManModel);
				} else
					MetaioDebug.log(Log.ERROR, "Error loading geometry: "
							+ metaioManModel);
				if (mpic != null) {
					mpic.setScale(7f);
					mpic.setVisible(false);
				}
				if (mpic1 != null) {
					mpic1.setScale(3f);
					mpic1.setVisible(false);
				}
			}
			mGestureHandler = new GestureHandlerAndroid(metaioSDK,
					GestureHandler.GESTURE_DRAG);
			mGestureHandler.addObject(mMetaioMan, 1); // true is for make it
														// pickable
			mGestureHandler.addObject(mchair, 2); // true is for make it
													// pickable

		} catch (Exception e) {

		}

		mPointLight = metaioSDK.createLight();
		mPointLight.setType(ELIGHT_TYPE.ELIGHT_TYPE_POINT);
		mPointLight.setAmbientColor(new Vector3d(0.9f, 0.9f, 0.9f)); //
		// slightly blue ambient
		mPointLight.setAttenuation(new Vector3d(0, 0, 1));
		mPointLight.setDiffuseColor(new Vector3d(0.4f, 0.4f, 0.4f)); // green-ish
		// mPointLight.setRadiusDegrees(50);
		mPointLight.setCoordinateSystemID(1);
		mPointLight.setTranslation(new Vector3d(500, 500, 500));

		/*
		 * mPointLight1 = metaioSDK.createLight();
		 * mPointLight1.setType(ELIGHT_TYPE.ELIGHT_TYPE_POINT);
		 * //mPointLight.setAmbientColor(new Vector3d(0.9f, 0.9f, 0.9f)); //
		 * slightly blue ambient mPointLight1.setAttenuation(new Vector3d(0, 0,
		 * 1)); mPointLight1.setDiffuseColor(new Vector3d(0.45f, 0.45f, 0.45f));
		 * // green-ish //mPointLight.setRadiusDegrees(50);
		 * mPointLight1.setCoordinateSystemID(1);
		 * mPointLight1.setTranslation(new Vector3d(-500,500,500 ));
		 */

		/*
		 * mPointLight2 = metaioSDK.createLight();
		 * mPointLight2.setType(ELIGHT_TYPE.ELIGHT_TYPE_POINT);
		 * //mPointLight.setAmbientColor(new Vector3d(0.9f, 0.9f, 0.9f)); //
		 * slightly blue ambient mPointLight2.setAttenuation(new Vector3d(0, 0,
		 * 1)); mPointLight2.setDiffuseColor(new Vector3d(0.45f, 0.45f, 0.45f));
		 * // green-ish //mPointLight.setRadiusDegrees(50);
		 * mPointLight2.setCoordinateSystemID(1);
		 * mPointLight2.setTranslation(new Vector3d(-500,-500,500 ));
		 */

		/*
		 * mPointLight3 = metaioSDK.createLight();
		 * mPointLight3.setType(ELIGHT_TYPE.ELIGHT_TYPE_POINT);
		 * //mPointLight.setAmbientColor(new Vector3d(0.9f, 0.9f, 0.9f)); //
		 * slightly blue ambient mPointLight3.setAttenuation(new Vector3d(0, 0,
		 * 1)); mPointLight3.setDiffuseColor(new Vector3d(0.45f, 0.45f, 0.45f));
		 * // green-ish //mPointLight.setRadiusDegrees(50);
		 * mPointLight3.setCoordinateSystemID(1);
		 * mPointLight3.setTranslation(new Vector3d(500,-500,500 ));
		 */
	}

	@Override
	protected void onGeometryTouched(final IGeometry geometry) {
		MetaioDebug.log("Model name is " + geometry.getName());
		String location = geometry.getTranslation().toString() + "," + geometry.getName();
		SendPacketTask task = new SendPacketTask (UDP_SERVER_PORT, wifi);
		task.execute(location);
		// TODO Auto-generated method stub
		if(geometry != null)
		{
			/*runOnUiThread(new Runnable(){
				@Override
				public void run()
				{
					TextView tx = (TextView)findViewById(R.id.Text_gemlocation);
					tx.setText(geometry.getTranslation().toString());
				}
				
			});*/
		}
	}

	@Override
	protected IMetaioSDKCallback getMetaioSDKCallbackHandler() {
		return mCallbackHandler;
	}

	final class MetaioSDKCallbackHandler extends IMetaioSDKCallback {

		@Override
		public void onNewCameraFrame(ImageStruct cameraFrame) {
			show = cameraFrame.getBitmap();
			if (lightcon == true) {

				new Thread(new Runnable() {

					public void run() {
						ComputingLightIntensity();
					}
				}).start();

			} 
			else 
			{
				MetaioDebug.log(
						Log.ERROR,
						"getImageCache OK!! "
								+ String.valueOf(cameraFrame.getColorFormat()));
				//scr_width = cameraFrame.getWidth();
				//scr_height = cameraFrame.getHeight();
				//Bitmap xd = Bitmap.createBitmap(test1(show));
				int width = show.getWidth();
				int height = show.getHeight();
				// Bitmap bitmap = Bitmap.createBitmap(width, height,
				// Bitmap.Config.RGB_565);
				// int dst[] = new int[width * height];
				// bmp.getPixels(dst, 0, width, 0, 0, width, height);
				int R, G, B, pixel;
				int pos, pixColor;
				for (int i = 0; i < height; i++) {
					for (int j = 0; j < width; j++) {
						int P = show.getPixel(j, i);
						R = Color.red(P) + 10;
						G = Color.green(P) + 10;
						B = Color.blue(P) + 10;
						if (R >= 255)
							R = 255;
						if (G >= 255)
							G = 255;
						if (B >= 255)
							B = 255;
						P = Color.rgb(R, G, B);
						MetaioDebug.log("OKOK!!");
						show.setHasAlpha(false);
						show.setPixel(j, i, P);
						
					}
				}

						
				
				/*
				 * new Thread(new Runnable() {
				 * 
				 * @Override public void run() { Canvas canvas =
				 * mSurfaceView.getHolder().lockCanvas(); //
				 * 判斷非null，才能drawBitmap. if (show != null) { show =
				 * Bitmap.createScaledBitmap(show, scr_width, scr_height,
				 * false); canvas.drawBitmap(show, 0, 0, null); }
				 * mSurfaceView.getHolder().unlockCanvasAndPost(canvas);
				 * show.recycle(); } });
				 */
			}

		}

		@Override
		public void onScreenshotImage(ImageStruct image) {
			show = image.getBitmap();
			if (show != null) {
				File path = Environment
						.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM
								+ "/Camera/");
				String picname;
				for (int k = 0;; k++) {
					picname = "Img" + String.valueOf(k) + ".JPEG";
					File file = new File(path, picname);
					if (!file.exists()) {
						break;
					}
					// file.delete();
				}
				saveMyBitmap(picname, show);
				show.recycle();
				runOnUiThread(new Runnable() {

					@Override
					public void run() {
						// a toast message to alert the user

						String message = "The screenshot has been added to the gallery.";

						Toast toast = Toast.makeText(getApplicationContext(),
								message, Toast.LENGTH_SHORT);
						toast.setGravity(Gravity.CENTER, 0, 0);
						toast.show();

					}
				});
			}
		}

		@Override
		public void onSDKReady() {
			// show GUI
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					mGUIView.setVisibility(View.VISIBLE);

				}
			});
		}

		/*
		 * @Override public void onTrackingEvent(TrackingValuesVector
		 * trackingValues) { if(!trackingValues.isEmpty()){ for(int i = 0;
		 * i<trackingValues.size(); i++){
		 * if(trackingValues.get(i).isTrackingState()){ //Log.d(TAG,
		 * "Tracking successful! "+trackingValues.get(i).getCosName());
		 * 
		 * final int cosId = trackingValues.get(i).getCoordinateSystemID();
		 * //metaioSDK.setTrackingConfiguration("GPS");
		 * mSurfaceView.queueEvent(new Runnable() {
		 * 
		 * @Override public void run() { String chair =
		 * AssetsManager.getAssetPath(getApplicationContext(),
		 * "TutorialTrackingSamples/Assets/couch.obj"); //Log.d(TAG,
		 * "local3DResourcePath="+local3DResourcePath); if(chair != null){
		 * mchair = metaioSDK.createGeometry(chair); // Log.d(TAG,
		 * "mModel="+mModel);
		 * 
		 * if (mchair != null) { // Set geometry properties mchair.setScale(new
		 * Vector3d(4.0f, 4.0f, 4.0f)); } //else //Log.e(TAG,
		 * "Error loading geometry: " + local3DResourcePath); }
		 * 
		 * if(mchair != null){ mchair.setCoordinateSystemID(cosId); } } }); } }
		 * } }
		 */

	}

	public void ComputingLightIntensity() {
		int img_w = show.getWidth();
		int img_h = show.getHeight();
		int area = img_w * img_h;
		// int com_l = img_w/2 - 50,com_r = img_w/2 + 50;
		// int com_u = img_h/2 - 100,com_b = img_h/2 + 100;
		// int area = (com_b - com_u) * (com_r - com_l);
		for (int i = 0; i < img_h; i++) {
			for (int j = 0; j < img_w; j++) {
				int P = show.getPixel(j, i);
				l_R += Color.red(P);
				l_G += Color.green(P);
				l_B += Color.blue(P);
			}
		}
		l_R = l_R / area / 255;
		l_G = l_G / area / 255;
		l_B = l_B / area / 255;
		l_Value = (float) (l_R + l_G + l_B) / 3;
		/*runOnUiThread(new Runnable() {
			@Override
			public void run() {
				TextView Pixel = (TextView) findViewById(R.id.Text_Value);
				Pixel.setText(String.valueOf(l_Value));

			}
		});*/
	}

	public void saveMyBitmap(String bitName, Bitmap mBitmap) {
		try {
			// 開啟檔案
			File path = Environment
					.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM
							+ "/Camera");
			File file = new File(path, bitName);
			// 開啟檔案串流
			FileOutputStream out = new FileOutputStream(file);
			// 將 Bitmap壓縮成指定格式的圖片並寫入檔案串流
			mBitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
			// 刷新並關閉檔案串流
			out.flush();
			out.close();
			MetaioDebug.log("path is " + path);
			galleryAddPic(path.toString(), bitName);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private void galleryAddPic(String path, String bitName) {
		Intent mediaScanIntent = new Intent(
				Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
		File f = new File(path + "/" + bitName);
		Uri contentUri = Uri.fromFile(f);
		mediaScanIntent.setData(contentUri);
		this.sendBroadcast(mediaScanIntent);
	}

	private SensorEventListener listener = new SensorEventListener() {
		@Override
		public void onSensorChanged(SensorEvent event) {
			// TODO Auto-generated method stub
			if (event.sensor.getType() == Sensor.TYPE_LIGHT) {
				float[] values = event.values;
				//value_0 = (TextView) findViewById(R.id.value_0);
				//value_0.setText("lux:" + values[0]);
				light_val = values[0];

			}

		}

		@Override
		public void onAccuracyChanged(Sensor sensor, int accuracy) {
			// TODO Auto-generated method stub
			/*
			 * if(sensor.getType() == Sensor.TYPE_LIGHT) { TextView
			 * accuracy_view = (TextView)findViewById(R.id.textView1);
			 * accuracy_view.setText("accuracy:"+accuracy); }
			 */
		}
	};

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		Mgr.unregisterListener(listener);
		super.onPause();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		Mgr.registerListener(listener, Mgr.getDefaultSensor(Sensor.TYPE_LIGHT),
				SensorManager.SENSOR_DELAY_UI);
		super.onResume();
	}

}
