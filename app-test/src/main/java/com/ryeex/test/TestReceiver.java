package com.ryeex.test;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.widget.Toast;

import com.ryeex.groot.lib.ble.stack.json.JsonApi;
import com.ryeex.groot.lib.common.Error;
import com.ryeex.groot.lib.common.asynccallback.AsyncCallback;
import com.ryeex.test.device.TestDevice;
import com.ryeex.test.device.TestDeviceManager;

/**
 * Created by chenhao on 2018/1/17.
 */

public class TestReceiver extends BroadcastReceiver {
    //adb shell am broadcast -a com.ryeex.test.action.TEST_API --es type 'send_json' --es json '{\"test\":\"123\"}'

    public static final String TAG = "TestReceiver";

    public static final String ACTION_API = "com.ryeex.test.action.TEST_API";

    public static final String ACTION_ON_SEND_JSON = "com.ryeex.test.action.TestReceiver.onSendJson";
    public static final String ACTION_ON_SEND_JSON_RESULT = "com.ryeex.test.action.TestReceiver.onSendJsonResult";
    public static final String KEY_JSON = "json";
    public static final String KEY_RESULT = "result";

    @Override
    public void onReceive(final Context context, Intent intent) {
        TestDevice device = TestDeviceManager.getInstance().getDevice();
        if (device == null) {
            Toast.makeText(context, TAG + " 未选择设备", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!device.isConnected()) {
            Toast.makeText(context, TAG + " 设备未连接", Toast.LENGTH_SHORT).show();
            return;
        }

        String action = intent.getAction();
        if (TextUtils.isEmpty(action)) {
            return;
        }

        if (action.equals(ACTION_API)) {
            String type = intent.getStringExtra("type");
            if (TextUtils.isEmpty(type)) {
                return;
            }

            if (type.equalsIgnoreCase("send_json")) {
                String jsonStr = intent.getStringExtra("json");
                if (TextUtils.isEmpty(jsonStr)) {
                    Toast.makeText(context, TAG + " json为空", Toast.LENGTH_SHORT).show();
                    return;
                }

                LocalBroadcastManager localBroadManager = LocalBroadcastManager.getInstance(context);
                Intent param = new Intent(ACTION_ON_SEND_JSON);
                param.putExtra(KEY_JSON, "JsonApi.sendJson " + jsonStr);
                localBroadManager.sendBroadcast(param);

                JsonApi.sendJson(TestDeviceManager.getInstance().getDevice().getBleManager(), jsonStr, new AsyncCallback<String, Error>() {
                    @Override
                    public void onSuccess(String result) {
                        LocalBroadcastManager localBroadManager = LocalBroadcastManager.getInstance(context);
                        Intent param = new Intent(ACTION_ON_SEND_JSON_RESULT);
                        param.putExtra(KEY_RESULT, "success " + result);
                        localBroadManager.sendBroadcast(param);
                    }

                    @Override
                    public void onFailure(Error error) {
                        LocalBroadcastManager localBroadManager = LocalBroadcastManager.getInstance(context);
                        Intent param = new Intent(ACTION_ON_SEND_JSON_RESULT);
                        param.putExtra(KEY_RESULT, "failure " + error);
                        localBroadManager.sendBroadcast(param);
                    }
                });

            }
        }
    }
}
