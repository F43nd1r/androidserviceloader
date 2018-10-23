package com.faendir.serviceloader;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import com.faendir.asl.serviceloader.ServiceLoader;

/**
 * @author lukas
 * @since 21.10.18
 */
public class MainActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        for (InterfaceTest t : new ServiceLoader(this).load(InterfaceTest.class)) {
            Log.e("RESULT",t.getClass().getName());
        }
    }
}
