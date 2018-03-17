package com.ryeex.test.device;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.ryeex.groot.lib.common.util.PrefsUtil;
import com.ryeex.test.app.TestApplication;

/**
 * Created by chenhao on 2018/1/5.
 */

public class TestDeviceManager {
    private static final String PREFERENCE = "com.ryeex.test.device";
    private static final String KEY_MAC = "mac";
    private static TestDeviceManager sInstance;
    private static Object sLock = new Object();
    Context mAppContext;

    private TestDevice mDevice;

    private SharedPreferences mPreference;

    private TestDeviceManager() {
        mAppContext = TestApplication.getAppContext();
        init();
    }

    public static TestDeviceManager getInstance() {
        if (sInstance == null) {
            synchronized (sLock) {
                // 有可能在其他线程已创建
                if (sInstance == null) {
                    sInstance = new TestDeviceManager();
                }
            }
        }
        return sInstance;
    }

    private void init() {
        mPreference = mAppContext.getSharedPreferences(PREFERENCE, Context.MODE_PRIVATE);
        String mac = PrefsUtil.getSettingString(mPreference, KEY_MAC, null);
        if (!TextUtils.isEmpty(mac)) {
            mDevice = new TestDevice();
            mDevice.setMac(mac);
        }
    }

    public Context getAppContext() {
        return mAppContext;
    }

    public synchronized void addDevice(TestDevice testDevice) {
        mDevice = testDevice;
        PrefsUtil.setSettingString(mPreference, KEY_MAC, testDevice.getMac());
    }

    public synchronized TestDevice getDevice() {
        return mDevice;
    }
}
