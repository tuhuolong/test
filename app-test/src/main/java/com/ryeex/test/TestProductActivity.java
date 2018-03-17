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

import com.ryeex.groot.lib.ble.stack.json.JsonApi;
import com.ryeex.groot.lib.common.Error;
import com.ryeex.groot.lib.common.asynccallback.AsyncCallback;
import com.ryeex.groot.lib.common.util.RandomUtil;
import com.ryeex.test.device.TestDevice;
import com.ryeex.test.device.TestDeviceManager;

/**
 * Created by chenhao on 2018/1/18.
 */

public class TestProductActivity extends FragmentActivity {
    Context mContext;
    private TextView mLogView;

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

        setContentView(R.layout.activity_test_product);

        findViewById(R.id.clear).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mLogView.setText("");
            }
        });

        findViewById(R.id.nfc_ce_open).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String json = "{\"id\":" + RandomUtil.randomInt(100000) + ", \"method\":\"nfc_ce\", \"para\":\"on\"}";
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

        findViewById(R.id.nfc_ce_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String json = "{\"id\":" + RandomUtil.randomInt(100000) + ", \"method\":\"nfc_ce\", \"para\":\"off\"}";
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

        findViewById(R.id.nfc_reader_open).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String json = "{\"id\":" + RandomUtil.randomInt(100000) + ", \"method\":\"nfc_reader\", \"para\":\"on\"}";
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

        findViewById(R.id.nfc_reader_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String json = "{\"id\":" + RandomUtil.randomInt(100000) + ", \"method\":\"nfc_reader\", \"para\":\"off\"}";
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

        findViewById(R.id.oled_open).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String json = "{\"id\":" + RandomUtil.randomInt(100000) + ", \"method\":\"oled\", \"para\":\"on\"}";
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

        findViewById(R.id.oled_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String json = "{\"id\":" + RandomUtil.randomInt(100000) + ", \"method\":\"oled\", \"para\":\"off\"}";
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

        findViewById(R.id.reset).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String json = "{\"id\":" + RandomUtil.randomInt(100000) + ", \"method\":\"reset\", \"para\":\"\"}";
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

        findViewById(R.id.color_on).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String json = "{\"id\":" + RandomUtil.randomInt(100000) + ", \"method\":\"debug\", \"para\":\"set_true_color\"}";
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

        findViewById(R.id.color_off).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String json = "{\"id\":" + RandomUtil.randomInt(100000) + ", \"method\":\"debug\", \"para\":\"set_false_color\"}";
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

        findViewById(R.id.start_uart_tool).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String json = "{\"id\":" + RandomUtil.randomInt(100000) + ", \"method\":\"debug\", \"para\":\"start_uart_tool\"}";
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

        findViewById(R.id.heart_rate_on).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String json = "{\"id\":" + RandomUtil.randomInt(100000) + ", \"method\":\"heartrate\", \"para\":\"on\"}";
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

        findViewById(R.id.heart_rate_off).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String json = "{\"id\":" + RandomUtil.randomInt(100000) + ", \"method\":\"heartrate\", \"para\":\"off\"}";
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

        findViewById(R.id.led_on).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String json = "{\"id\":" + RandomUtil.randomInt(100000) + ", \"method\":\"led\", \"para\":\"on\"}";
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

        findViewById(R.id.led_off).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String json = "{\"id\":" + RandomUtil.randomInt(100000) + ", \"method\":\"led\", \"para\":\"off\"}";
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

        findViewById(R.id.nfc_on).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String json = "{\"id\":" + RandomUtil.randomInt(100000) + ", \"method\":\"nfc\", \"para\":\"on\"}";
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

        findViewById(R.id.nfc_off).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String json = "{\"id\":" + RandomUtil.randomInt(100000) + ", \"method\":\"nfc\", \"para\":\"off\"}";
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

        findViewById(R.id.gsensor_on).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String json = "{\"id\":" + RandomUtil.randomInt(100000) + ", \"method\":\"gsensor\", \"para\":\"on\"}";
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
        findViewById(R.id.gsensor_off).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String json = "{\"id\":" + RandomUtil.randomInt(100000) + ", \"method\":\"gsensor\", \"para\":\"off\"}";
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

        findViewById(R.id.motar_on).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String json = "{\"id\":" + RandomUtil.randomInt(100000) + ", \"method\":\"motar\", \"para\":\"on\"}";
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
        findViewById(R.id.motar_off).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String json = "{\"id\":" + RandomUtil.randomInt(100000) + ", \"method\":\"motar\", \"para\":\"off\"}";
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

        findViewById(R.id.exflash_get_id).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String json = "{\"id\":" + RandomUtil.randomInt(100000) + ", \"method\":\"exflash\", \"para\":\"get_id\"}";
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

        findViewById(R.id.device_info_version).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String json = "{\"id\":" + RandomUtil.randomInt(100000) + ", \"method\":\"get_device_info\", \"para\":\"version\"}";
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
        findViewById(R.id.device_info_sn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String json = "{\"id\":" + RandomUtil.randomInt(100000) + ", \"method\":\"get_device_info\", \"para\":\"sn\"}";
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
        findViewById(R.id.device_info_did).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String json = "{\"id\":" + RandomUtil.randomInt(100000) + ", \"method\":\"get_device_info\", \"para\":\"did\"}";
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
        findViewById(R.id.device_info_mac).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String json = "{\"id\":" + RandomUtil.randomInt(100000) + ", \"method\":\"get_device_info\", \"para\":\"mac\"}";
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
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        LocalBroadcastManager.getInstance(mContext).unregisterReceiver(mSendJsonReceiver);
    }

    private void showMsg(String msg) {
        mLogView.append(msg + "\n");
        mLogView.setOverScrollMode(View.OVER_SCROLL_ALWAYS);
        mLogView.scrollTo(0, mLogView.getHeight());
    }
}
