package com.faendir.asl.serviceloader;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author lukas
 * @since 21.10.18
 */
public class ServiceLoader {
    private static final String LOG_TAG = "[ServiceLoader]";
    public static final String PREFIX = "com.faendir.asl.serviceloader:";
    private final Map<Class<?>, List<Class<?>>> map;

    public ServiceLoader(Context context) {
        map = new HashMap<>();
        try {
            ApplicationInfo info = context.getPackageManager().getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
            for (String name : info.metaData.keySet()) {
                if (name.startsWith(PREFIX)) {
                    try {
                        //noinspection unchecked
                        Map<Class<?>, List<Class<?>>> definitions = (Map<Class<?>, List<Class<?>>>) context.getClassLoader().loadClass(info.metaData.getString(name)).getMethod("getDefinitions").invoke(null);
                        for (Map.Entry<Class<?>, List<Class<?>>> entry : definitions.entrySet()) {
                            if (map.containsKey(entry.getKey())) {
                                map.get(entry.getKey()).addAll(entry.getValue());
                            } else {
                                map.put(entry.getKey(), entry.getValue());
                            }
                        }
                    } catch (Exception e) {
                        Log.e(LOG_TAG, "Failed loading services from" + name, e);
                    }
                }
            }
        } catch (Exception e) {
            Log.e(LOG_TAG, "Service loading failed ", e);
        }
    }

    public <T> List<T> load(Class<T> service) {
        if (map.containsKey(service)) {
            List<T> result = new ArrayList<>();
            for (Class<?> clazz : map.get(service)) {
                try {
                    Object o = clazz.newInstance();
                    if (service.isInstance(o)) {
                        //noinspection unchecked
                        result.add((T) o);
                    } else {
                        Log.w(LOG_TAG, "Registered Service " + clazz.getName() + " does not implement " + service.getName() + ". It was ignored.");
                    }
                } catch (Exception e) {
                    Log.w(LOG_TAG, "Registered Service " + clazz.getName() + " could not be found");
                }
            }
            return result;
        }
        return Collections.emptyList();
    }
}
