package rajawali.tutorials;

import rajawali.RajawaliActivity;
import android.os.Bundle;

public class RajawaliTutorial1Activity extends RajawaliActivity {

    public RajawaliTutorial1Renderer mRenderer; 

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mRenderer = new RajawaliTutorial1Renderer(this);
        mRenderer.setSurfaceView(mSurfaceView);
        super.setRenderer(mRenderer);
    }
}