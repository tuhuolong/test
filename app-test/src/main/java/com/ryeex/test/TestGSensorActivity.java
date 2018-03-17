package com.ryeex.test;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.LocalBroadcastManager;
import android.view.View;
import android.widget.TextView;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.ryeex.groot.lib.ble.BleManager;
import com.ryeex.groot.lib.ble.stack.json.JsonApi;
import com.ryeex.groot.lib.common.Error;
import com.ryeex.groot.lib.common.asynccallback.AsyncCallback;
import com.ryeex.groot.lib.common.util.FileUtil;
import com.ryeex.groot.lib.common.util.RandomUtil;
import com.ryeex.test.app.TestApplication;
import com.ryeex.test.device.TestDevice;
import com.ryeex.test.device.TestDeviceManager;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

import static com.ryeex.groot.lib.ble.BleSetting.CHARACTER_RYEEX_JSON;
import static com.ryeex.groot.lib.ble.BleSetting.SERVICE_RYEEX;

/**
 * Created by chenhao on 2018/2/28.
 */
public class TestGSensorActivity extends FragmentActivity {
    private static final String FILE_DIR = "/sdcard/ryeex";

    private static String mFilePath;

    Context mContext;

    private TextView mLogView;

    BleManager.ManagerListener mListener = new BleManager.ManagerListener() {
        @Override
        public void onDisconnected() {

        }

        @Override
        public void onReceiveBytes(UUID serviceId, UUID characterId, final byte[] plainBytes) {
            if (serviceId.equals(SERVICE_RYEEX) && characterId.equals(CHARACTER_RYEEX_JSON)) {

                com.google.gson.JsonParser gsonParser = new com.google.gson.JsonParser();

                try {
                    JsonElement jsonElement = gsonParser.parse(new String(plainBytes));
                    String method = jsonElement.getAsJsonObject().get("method").getAsString();
                    if (method.equalsIgnoreCase("alg_raw_data")) {
                        JsonArray jsonArray = jsonElement.getAsJsonObject().get("result").getAsJsonArray();
                        for (int i = 0, len = jsonArray.size(); i < len; i++) {
                            final String line = jsonArray.get(i).getAsString();
                            FileUtil.appendString(mFilePath, (line + "\n"));

                            TestApplication.getGlobalUiHandler().post(new Runnable() {
                                @Override
                                public void run() {
                                    showMsg("File.writeBytes:" + line);
                                }
                            });
                        }

                    }
                } catch (Exception e) {
                }
            }
        }
    };

    BroadcastReceiver mSendJsonReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (action.equals(TestReceiver.ACTION_ON_SEND_JSON)) {
                String json = intent.getStringExtra(TestReceiver.KEY_JSON);
                showMsg(json);
            } else if (action.equals(TestReceiver.ACTION_ON_SEND_JSON_RESULT)) {
                String result = intent.getStringExtra(TestReceiver.KEY_RESULT);
                showMsg(result);
            } else if (action.equals(TestDevice.ACTION_DEVICE_ON_RECEIVE_JSON)) {
                String mac = intent.getStringExtra(TestDevice.KEY_MAC);
                String json = intent.getStringExtra(TestDevice.KEY_JSON);

                showMsg("onReceiveJson:" + json);
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mContext = this;

        setContentView(R.layout.activity_test_gsensor);

        findViewById(R.id.start).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
                final String time = df.format(new Date());
                mFilePath = FILE_DIR + "/" + time + ".txt";
                FileUtil.createFileIfNotExists(mFilePath);

                String json = "{\"id\":" + RandomUtil.randomInt(100000) + ", \"method\":\"alg_raw_switch\", \"para\":\"on\"}";
                showMsg("JsonApi.sendJson " + json);
                JsonApi.sendJson(TestDeviceManager.getInstance().getDevice().getBleManager(), json, new AsyncCallback<String, Error>() {
                    @Override
                    public void onSuccess(String result) {

                        showMsg("success " + result);
                    }

                    @Override
                    public void onFailure(Error error) {
                        showMsg("failure " + error);
                    }
                });
            }
        });

        findViewById(R.id.stop).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String json = "{\"id\":" + RandomUtil.randomInt(100000) + ", \"method\":\"alg_raw_switch\", \"para\":\"off\"}";
                showMsg("JsonApi.sendJson " + json);
                JsonApi.sendJson(TestDeviceManager.getInstance().getDevice().getBleManager(), json, new AsyncCallback<String, Error>() {
                    @Override
                    public void onSuccess(String result) {
                        showMsg("success " + result);
                    }

                    @Override
                    public void onFailure(Error error) {
                        showMsg("failure " + error);
                    }
                });
            }
        });

        mLogView = findViewById(R.id.logs);

        IntentFilter deviceFilter = new IntentFilter();
        deviceFilter.addAction(TestReceiver.ACTION_ON_SEND_JSON);
        deviceFilter.addAction(TestReceiver.ACTION_ON_SEND_JSON_RESULT);
        deviceFilter.addAction(TestDevice.ACTION_DEVICE_ON_RECEIVE_JSON);
        LocalBroadcastManager.getInstance(mContext).registerReceiver(mSendJsonReceiver, deviceFilter);

        TestDeviceManager.getInstance().getDevice().getBleManager().addManagerListener(mListener);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        LocalBroadcastManager.getInstance(mContext).unregisterReceiver(mSendJsonReceiver);

        TestDeviceManager.getInstance().getDevice().getBleManager().removeManagerListener(mListener);
    }

    private void showMsg(String msg) {
        mLogView.append(msg + "\n");
        mLogView.setOverScrollMode(View.OVER_SCROLL_ALWAYS);
        mLogView.scrollTo(0, mLogView.getHeight());
    }
}
