package com.ryeex.test;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.ryeex.groot.lib.ble.BleManager;
import com.ryeex.groot.lib.ble.requestresult.DescriptorWriteResult;
import com.ryeex.groot.lib.ble.requestresult.ReadRssiResult;
import com.ryeex.groot.lib.ble.stack.crypto.Crypto;
import com.ryeex.groot.lib.common.Error;
import com.ryeex.groot.lib.common.asynccallback.AsyncCallback;
import com.ryeex.test.device.TestDevice;
import com.ryeex.test.device.TestDeviceManager;

import static com.ryeex.groot.lib.ble.BleSetting.CHARACTER_RYEEX_JSON;
import static com.ryeex.groot.lib.ble.BleSetting.SERVICE_RYEEX;

/**
 * Created by chenhao on 2018/1/19.
 */

public class TestBleActivity extends FragmentActivity {
    Context mContext;

    TestDevice mDevice;
    private View mTipBar;
    private TextView mTipTextView;
    BroadcastReceiver mDeviceReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (action.equals(TestDevice.ACTION_DEVICE_CONNECTED)) {
                String mac = intent.getStringExtra(TestDevice.KEY_MAC);
                if (!TextUtils.isEmpty(mac) && mDevice != null && mac.equals(mDevice.getMac())) {
                    refreshUI();
                }
            } else if (action.equals(TestDevice.ACTION_DEVICE_DISCONNECTED)) {
                String mac = intent.getStringExtra(TestDevice.KEY_MAC);
                if (!TextUtils.isEmpty(mac) && mDevice != null && mac.equals(mDevice.getMac())) {
                    refreshUI();
                }
            }
        }
    };
    private TextView mLogView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mContext = this;

        mDevice = TestDeviceManager.getInstance().getDevice();
        if (mDevice == null) {
            Toast.makeText(mContext, "请先选择设备", Toast.LENGTH_SHORT).show();
            return;
        }

        setContentView(R.layout.activity_test_ble);

        findViewById(R.id.clear).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mLogView.setText("");
            }
        });

        mTipBar = findViewById(R.id.tip_bar);
        mTipBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mDevice.isConnected() && !mDevice.isConnecting()) {
                    startConnect();
                }
            }
        });
        mTipTextView = findViewById(R.id.tip);

        findViewById(R.id.test_split_transfer_speed).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                testSplitTransferSpeed();
            }
        });

        findViewById(R.id.test_connect_speed).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mDevice.isConnected()) {
                    showMsg("disconnect start");
                    mDevice.disconnect(new AsyncCallback<Void, Error>() {
                        @Override
                        public void onSuccess(Void result) {
                            refreshUI();

                            showMsg("disconnect success");

                            testConnectSpeed();
                        }

                        @Override
                        public void onFailure(Error error) {
                            refreshUI();

                            showMsg("disconnect failure");
                        }
                    });
                } else {
                    testConnectSpeed();
                }
            }
        });

        mLogView = findViewById(R.id.logs);

        IntentFilter deviceFilter = new IntentFilter();
        deviceFilter.addAction(TestDevice.ACTION_DEVICE_CONNECTED);
        deviceFilter.addAction(TestDevice.ACTION_DEVICE_DISCONNECTED);
        LocalBroadcastManager.getInstance(mContext).registerReceiver(mDeviceReceiver, deviceFilter);

        refreshUI();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        LocalBroadcastManager.getInstance(mContext).unregisterReceiver(mDeviceReceiver);
    }

    private void startConnect() {
        TestPolicy.startConnect(mDevice, new AsyncCallback<Void, Error>() {
            @Override
            public void onSuccess(Void result) {
                refreshUI();
            }

            @Override
            public void onFailure(Error error) {
                Toast.makeText(mContext, "连接失败", Toast.LENGTH_SHORT).show();
                refreshUI();
            }
        });

        refreshUI();
    }

    private void testSplitTransferSpeed() {
        final BleManager bleManager = TestDeviceManager.getInstance().getDevice().getBleManager();

        bleManager.notify(SERVICE_RYEEX, CHARACTER_RYEEX_JSON, new AsyncCallback<DescriptorWriteResult, Error>() {
            @Override
            public void onSuccess(DescriptorWriteResult result) {

                final int bytesNum = 3072;

                final byte[] bytes = new byte[bytesNum];
                for (int i = 0; i < bytesNum; i++) {
                    bytes[i] = (byte) i;
                }

                final long startTime = System.currentTimeMillis();
                showMsg("sendBytes start bytesNum:" + bytesNum);

                bleManager.sendBytes(bytes, Crypto.CRYPTO.JSON, new AsyncCallback<Void, Error>() {
                    @Override
                    public void onSuccess(Void result) {
                        long endTime = System.currentTimeMillis();
                        final long spendTime = (endTime - startTime);

                        final float speed = bytesNum * 1000 / (float) spendTime;

                        bleManager.readRssi(new AsyncCallback<ReadRssiResult, Error>() {
                            @Override
                            public void onSuccess(ReadRssiResult result) {
                                showMsg("sendBytes success time:" + spendTime + " ms, speed:" + String.format("%.3f", speed) + " byte/s" + " rssi:" + result.rssi);
                            }

                            @Override
                            public void onFailure(Error error) {
                                showMsg("readRssi failure " + error);
                            }
                        });


                    }

                    @Override
                    public void onFailure(Error error) {
                        showMsg("sendBytes failure " + error);
                    }
                });
            }

            @Override
            public void onFailure(Error error) {
                showMsg("notify failure " + SERVICE_RYEEX + " " + CHARACTER_RYEEX_JSON);
            }
        });
    }

    private void testConnectSpeed() {
        showMsg("connect start");

        final long startTime = System.currentTimeMillis();

        mDevice.connect(new AsyncCallback<Void, Error>() {
            @Override
            public void onSuccess(Void result) {
                final long endTime = System.currentTimeMillis();

                showMsg("connect success time:" + (endTime - startTime) + " ms");

                refreshUI();
            }

            @Override
            public void onFailure(Error error) {
                showMsg("connect failure");

                refreshUI();
            }
        });

    }

    private void refreshUI() {
        if (mDevice.isConnected()) {
            mTipBar.setVisibility(View.GONE);
            mTipTextView.setText("");
        } else if (mDevice.isConnecting()) {
            mTipBar.setVisibility(View.VISIBLE);
            mTipTextView.setText("设备连接中...");
        } else {
            mTipBar.setVisibility(View.VISIBLE);
            mTipTextView.setText("设备已断开，请将设备靠近手机恢复连接");
        }
    }

    private void showMsg(String msg) {
        mLogView.append(msg + "\n");
        mLogView.setOverScrollMode(View.OVER_SCROLL_ALWAYS);
        mLogView.scrollTo(0, mLogView.getHeight());
    }
}
