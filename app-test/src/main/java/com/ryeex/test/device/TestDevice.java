package com.ryeex.test.device;

import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

import com.ryeex.groot.lib.ble.BleManager;
import com.ryeex.groot.lib.ble.requestresult.DescriptorWriteResult;
import com.ryeex.groot.lib.ble.stack.json.JsonApi;
import com.ryeex.groot.lib.common.Error;
import com.ryeex.groot.lib.common.asynccallback.AsyncCallback;
import com.ryeex.test.app.TestApplication;

import java.util.UUID;

import static com.ryeex.groot.lib.ble.BleSetting.CHARACTER_RYEEX_JSON;
import static com.ryeex.groot.lib.ble.BleSetting.SERVICE_RYEEX;

/**
 * Created by chenhao on 2018/1/5.
 */

public class TestDevice {
    public static final String ACTION_DEVICE_CONNECTED = "com.ryeex.test.device.connected";
    public static final String ACTION_DEVICE_DISCONNECTED = "com.ryeex.test.device.disconnected";
    public static final String ACTION_DEVICE_ON_RECEIVE_JSON = "com.ryeex.test.device.on_receive_json";
    public static final String KEY_MAC = "mac";
    public static final String KEY_JSON = "json";

    private BleManager mBleManager;
    private String mMac;

    public TestDevice() {
        mBleManager = new BleManager();
        mBleManager.addManagerListener(new BleManager.ManagerListener() {
            @Override
            public void onDisconnected() {
                LocalBroadcastManager localBroadManager = LocalBroadcastManager.getInstance(TestApplication.getAppContext());
                Intent param = new Intent(ACTION_DEVICE_DISCONNECTED);
                param.putExtra(KEY_MAC, mMac);
                localBroadManager.sendBroadcast(param);
            }

            @Override
            public void onReceiveBytes(UUID serviceId, UUID characterId, byte[] plainBytes) {
                if (serviceId.equals(SERVICE_RYEEX) && characterId.equals(CHARACTER_RYEEX_JSON)) {

                    if (!JsonApi.isSending()) {
                        String json = new String(plainBytes);

                        LocalBroadcastManager localBroadManager = LocalBroadcastManager.getInstance(TestApplication.getAppContext());
                        Intent param = new Intent(ACTION_DEVICE_ON_RECEIVE_JSON);
                        param.putExtra(KEY_MAC, mMac);
                        param.putExtra(KEY_JSON, json);
                        localBroadManager.sendBroadcast(param);
                    }
                }
            }
        });
    }

    public synchronized String getMac() {
        return mMac;
    }

    public synchronized void setMac(String mac) {
        mMac = mac;
        mBleManager.setMac(mac);
    }

    public synchronized BleManager getBleManager() {
        return mBleManager;
    }

    public synchronized void connect(final AsyncCallback<Void, Error> callback) {
        mBleManager.connect(new AsyncCallback<Void, Error>() {
            @Override
            public void onSuccess(Void result) {
                if (callback != null) {
                    callback.sendSuccessMessage(null);
                }

                LocalBroadcastManager localBroadManager = LocalBroadcastManager.getInstance(TestApplication.getAppContext());
                Intent param = new Intent(ACTION_DEVICE_CONNECTED);
                param.putExtra(KEY_MAC, mMac);
                localBroadManager.sendBroadcast(param);
            }

            @Override
            public void onFailure(Error error) {
                if (callback != null) {
                    callback.sendFailureMessage(error);
                }
            }
        });
    }

    public synchronized void disconnect(AsyncCallback<Void, Error> callback) {
        mBleManager.disconnect(callback);
    }

    public synchronized boolean isConnected() {
        return mBleManager.isConnected();
    }

    public synchronized boolean isConnecting() {
        return mBleManager.isConnecting();
    }

    public synchronized boolean isDisconnected() {
        return mBleManager.isDisconnected();
    }

    public void notify(UUID service, UUID character, AsyncCallback<DescriptorWriteResult, Error> callback) {
        mBleManager.notify(service, character, callback);
    }
}
