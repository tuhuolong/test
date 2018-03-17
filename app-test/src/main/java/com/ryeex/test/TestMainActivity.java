package com.ryeex.test;

import android.accounts.OperationCanceledException;
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

import com.ryeex.groot.lib.common.Error;
import com.ryeex.groot.lib.common.asynccallback.AsyncCallback;
import com.ryeex.test.device.TestDevice;
import com.ryeex.test.device.TestDeviceManager;
import com.xiaomi.account.openauth.XiaomiOAuthFuture;
import com.xiaomi.account.openauth.XiaomiOAuthResults;
import com.xiaomi.account.openauth.XiaomiOAuthorize;


public class TestMainActivity extends FragmentActivity {
    Context mContext;

    TestDevice mDevice;

    TextView mTitle;
    View mUnSelectedLayer;
    View mConnectedLayer;
    View mDisconnectedLayer;

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

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mContext = this;

        mDevice = TestDeviceManager.getInstance().getDevice();
        if (mDevice == null) {
            gotoSelectDevice();
        }

        setContentView(R.layout.activity_test_main);

        mTitle = findViewById(R.id.title);

        findViewById(R.id.re_select).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoSelectDevice();
            }
        });

        mUnSelectedLayer = findViewById(R.id.unselected_layer);
        mConnectedLayer = findViewById(R.id.connected_layer);
        mDisconnectedLayer = findViewById(R.id.disconnected_layer);

        findViewById(R.id.tip_bar).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mDevice != null) {
                    startConnect();
                }
            }
        });

        findViewById(R.id.oauth).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startMiLogin();
            }
        });

        findViewById(R.id.se_debug).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoJRCP();
            }
        });

        findViewById(R.id.product_debug).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoTestProduct();
            }
        });

        findViewById(R.id.ble_debug).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoTestBle();
            }
        });

        findViewById(R.id.gsensor_debug).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoTestGSensor();
            }
        });

        findViewById(R.id.nfc_debug).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoTestNfc();
            }
        });

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

    private void gotoSelectDevice() {
        IntentFilter intentFilter = new IntentFilter(TestSelectActivity.ACTION_COMPLETE);
        BroadcastReceiver callbackReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                LocalBroadcastManager.getInstance(mContext).unregisterReceiver(this);

                int errorCode = intent.getIntExtra(TestSelectActivity.ERROR_CODE, TestSelectActivity.FAILURE);

                if (errorCode == TestSelectActivity.SUCCESS) {
                    mDevice = TestDeviceManager.getInstance().getDevice();
                    startConnect();
                }
            }
        };
        LocalBroadcastManager.getInstance(mContext).registerReceiver(callbackReceiver, intentFilter);

        Intent intent = new Intent(mContext, TestSelectActivity.class);
        startActivity(intent);
    }

    private void gotoJRCP() {
        Intent intent = new Intent(mContext, JRCPActivity.class);
        startActivity(intent);
    }

    private void gotoTestProduct() {
        Intent intent = new Intent(mContext, TestProductActivity.class);
        startActivity(intent);
    }

    private void gotoTestBle() {
        Intent intent = new Intent(mContext, TestBleActivity.class);
        startActivity(intent);
    }

    private void gotoTestGSensor() {
        Intent intent = new Intent(mContext, TestGSensorActivity.class);
        startActivity(intent);
    }

    private void gotoTestNfc() {
        Intent intent = new Intent(mContext, TestNfcActivity.class);
        startActivity(intent);
    }

    private void startConnect() {
        TestPolicy.startConnect(mDevice, new AsyncCallback<Void, Error>() {
            @Override
            public void onSuccess(Void result) {
                refreshUI();
            }

            @Override
            public void onFailure(Error error) {
                refreshUI();
            }
        });

        refreshUI();
    }

    private void refreshUI() {
        if (mDevice == null) {
            mUnSelectedLayer.setVisibility(View.VISIBLE);
            mConnectedLayer.setVisibility(View.GONE);
            mDisconnectedLayer.setVisibility(View.GONE);

            mTitle.setText("你还未选择设备");
        } else {
            if (mDevice.isConnected()) {
                mUnSelectedLayer.setVisibility(View.GONE);
                mConnectedLayer.setVisibility(View.VISIBLE);
                mDisconnectedLayer.setVisibility(View.GONE);

                mTitle.setText(mDevice.getMac() + " 已连接");
            } else if (mDevice.isConnecting()) {
                mUnSelectedLayer.setVisibility(View.GONE);
                mConnectedLayer.setVisibility(View.GONE);
                mDisconnectedLayer.setVisibility(View.GONE);

                mTitle.setText(mDevice.getMac() + " 连接中...");
            } else {
                mUnSelectedLayer.setVisibility(View.GONE);
                mConnectedLayer.setVisibility(View.GONE);
                mDisconnectedLayer.setVisibility(View.VISIBLE);

                mTitle.setText(mDevice.getMac());
            }
        }
    }

    private void startMiLogin() {
        final int[] MI_SCOPE = {16001};

        final XiaomiOAuthFuture<XiaomiOAuthResults> future = new XiaomiOAuthorize()
                .setAppId(2882303761517479675L)
                .setRedirectUrl("https://mapi.ryeex.com/third/mi_sts")
                .setNoMiui(true)
                .setScope(MI_SCOPE)
                .startGetAccessToken(this);
//                .startGetOAuthCode(this);


        try {
            XiaomiOAuthResults results = future.getResult();

            if (results.hasError()) {
                throw new RuntimeException("");
            } else {

                String accessToken = results.getAccessToken();

//                mAccessToken = accessToken;

                refreshUI();

            }
        } catch (OperationCanceledException e) {
        } catch (Exception e) {
            hashCode();
        }
    }
}