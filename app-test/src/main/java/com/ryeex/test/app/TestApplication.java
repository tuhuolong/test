package com.ryeex.test.app;

import android.app.Application;
import android.content.Context;
import android.os.Handler;
import android.support.multidex.MultiDexApplication;

import com.orhanobut.logger.Logger;
import com.ryeex.groot.lib.ble.BleContext;
import com.ryeex.groot.lib.ble.scan.BleScanner;
import com.ryeex.groot.lib.common.thread.MessageHandlerThread;
import com.ryeex.groot.lib.log.GrootDiskLogAdapter;
import com.ryeex.groot.lib.log.GrootLogcatLogAdapter;
import com.ryeex.test.device.TestDeviceManager;

/**
 * Created by chenhao on 2018/1/5.
 */

public class TestApplication extends MultiDexApplication {

    private static Application sInstance;

    private static Handler sGlobalUiHandler;
    private static Handler sGlobalWorkerHandler;
    private MessageHandlerThread sGlobalWorkerThread;

    public static Context getAppContext() {
        return sInstance;
    }

    public static Handler getGlobalUiHandler() {
        return sGlobalUiHandler;
    }

    public static Handler getGlobalWorkerHandler() {
        return sGlobalWorkerHandler;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        sInstance = this;

        if (sGlobalUiHandler == null) {
            sGlobalUiHandler = new Handler();
        }

        if (sGlobalWorkerThread == null) {
            sGlobalWorkerThread = new MessageHandlerThread("GlobalWorker");
            sGlobalWorkerThread.start();
            sGlobalWorkerHandler = new Handler(sGlobalWorkerThread.getLooper());
        }

        initLogger();
        initBle();
        initDevice();
    }

    private void initLogger() {
        Logger.addLogAdapter(new GrootDiskLogAdapter(getAppContext(), true));
        Logger.addLogAdapter(new GrootLogcatLogAdapter(true));
    }

    private void initBle() {
        BleContext.sAppContext = getAppContext();
        BleScanner.getInstance();
    }

    private void initDevice() {
        TestDeviceManager.getInstance();
    }
}
