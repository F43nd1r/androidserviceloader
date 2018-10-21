package com.faendir.asl.serviceloader;

import android.content.Context;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author lukas
 * @since 21.10.18
 */
public class ServiceLoader {
    private static final String LOG_TAG = "[ServiceLoader]";
    private final Context context;

    public ServiceLoader(Context context) {
        this.context = context;
    }

    public <T> List<T> load(Class<T> service) {
        List<T> result = new ArrayList<>();
        try {
            for (String name : context.getAssets().list("serviceloader/" + service.getName())) {
                try {
                    Object o = context.getClassLoader().loadClass(name).newInstance();
                    if (service.isInstance(o)) {
                        //noinspection unchecked
                        result.add((T) o);
                    } else {
                        Log.w(LOG_TAG, "Registered Service " + name + " does not implement " + service.getName() + ". It was ignored.");
                    }
                } catch (Exception e) {
                    Log.w(LOG_TAG, "Registered Service " + name + " could not be found");
                }
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Unable to open assets for " + service.getName() + ". No services will be loaded");
        }
        return result;
    }
}
