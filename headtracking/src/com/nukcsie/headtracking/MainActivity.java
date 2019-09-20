package com.nukcsie.headtracking;

import java.util.*;
import android.os.*;
import android.util.Log;
import android.view.*;
import android.widget.*;
import android.app.Activity;
import android.hardware.*;
import android.hardware.Camera.*;


public class MainActivity extends Activity {
	SurfaceView sv;
	SurfaceHolder sh;
	Camera cm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LinearLayout ll = new LinearLayout(this);
        ll.setOrientation(LinearLayout.VERTICAL);
        setContentView(ll);
        
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        sv = new SurfaceView(this);
        sh = sv.getHolder();
        
        sh.addCallback(new SampleSurfaceHolderCallback());
        sh.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        
        ll.addView(sv);

    }
    
    class SampleSurfaceHolderCallback implements SurfaceHolder.Callback
    {
    	public void surfaceCreated(SurfaceHolder holder)
    	{
    		cm = openFrontFacingCameraGingerbread();
    		Camera.Parameters pr = cm.getParameters();
    		List<Size> ss = pr.getSupportedPictureSizes();
    		Size s = ss.get(0);
    		pr.setPictureSize(s.width, s.height);
    		cm.setParameters(pr);
    		try
    		{
    			WindowManager wm = (WindowManager)getSystemService(WINDOW_SERVICE);
    			Display dp = wm.getDefaultDisplay();
    			cm.setDisplayOrientation(90);
    			cm.setPreviewDisplay(sv.getHolder());
    			cm.startPreview();
    		}
    		catch(Exception e){}
    	}
    	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height)
    	{
    		try
    		{
    			WindowManager wm = (WindowManager)getSystemService(WINDOW_SERVICE);
    			Display dp = wm.getDefaultDisplay();
    			cm.setDisplayOrientation(90);
    			cm.setPreviewDisplay(sv.getHolder());
    			Camera.Parameters pr = cm.getParameters();
    			pr.setPreviewSize(width, height);
    			cm.setParameters(pr);
    			//cm.startPreview();
    		}
    		catch(Exception e){}
    	}
    	public void surfaceDestroyed(SurfaceHolder holder)
    	{
    		if (cm!=null){
    			cm.stopPreview();
    			cm.release();
    			cm=null;
        }
    	}
    	private Camera openFrontFacingCameraGingerbread() {
            int cameraCount = 0;
            Camera cam = null;
            Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
            cameraCount = Camera.getNumberOfCameras();
            for (int camIdx = 0; camIdx < cameraCount; camIdx++) {
                Camera.getCameraInfo(camIdx, cameraInfo);
                if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                    try {
                        cam = Camera.open(camIdx);
                    } catch (RuntimeException e) {
                        Log.e("TAG", "Camera failed to open: " + e.getLocalizedMessage());
                    }
                }
            }

            return cam;
        }
    }
}
