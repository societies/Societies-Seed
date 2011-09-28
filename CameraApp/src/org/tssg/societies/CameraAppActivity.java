package org.tssg.societies;

import com.phonegap.DroidGap;
import android.os.Bundle;

public class CameraAppActivity extends DroidGap {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//      setContentView(R.layout.main);
      super.loadUrl("file:///android_asset/www/index.html");
    }
}