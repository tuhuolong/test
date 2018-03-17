package com.ryeex.test;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.ryeex.groot.lib.ble.stack.json.JsonApi;
import com.ryeex.groot.lib.common.Error;
import com.ryeex.groot.lib.common.asynccallback.AsyncCallback;
import com.ryeex.groot.lib.common.util.PrefsUtil;
import com.ryeex.groot.lib.common.util.RandomUtil;
import com.ryeex.test.device.TestDevice;
import com.ryeex.test.device.TestDeviceManager;
import com.ryeex.test.dialog.TestNfcDialogFragment;

import org.json.JSONObject;

/**
 * Created by chenhao on 2018/3/4.
 */

public class TestNfcActivity extends FragmentActivity {

    public static final String KEY_BLK_1 = "test_nfc_blk_1";
    public static final String KEY_BLK_2 = "test_nfc_blk_2";
    public static final String KEY_BLK_3 = "test_nfc_blk_3";
    public static final String KEY_BLK_4 = "test_nfc_blk_4";
    public static final String KEY_BLK_5 = "test_nfc_blk_5";
    public static final String KEY_BLK_6 = "test_nfc_blk_6";
    public static final String KEY_Tvdd_Cfg_1 = "test_nfc_Tvdd_Cfg_1";
    public static final String KEY_Tvdd_Cfg_2 = "test_nfc_Tvdd_Cfg_2";
    public static final String KEY_Tvdd_Cfg_3 = "test_nfc_Tvdd_Cfg_3";
    public static final String KEY_Core_Conf = "test_nfc_Core_Conf";


    Context mContext;

    TextView mBlk_1_Content;
    TextView mBlk_2_Content;
    TextView mBlk_3_Content;
    TextView mBlk_4_Content;
    TextView mBlk_5_Content;
    TextView mBlk_6_Content;
    TextView mTvdd_Cfg_1_Content;
    TextView mTvdd_Cfg_2_Content;
    TextView mTvdd_Cfg_3_Content;
    TextView mCore_Conf_Content;

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

        setContentView(R.layout.activity_test_nfc);

        findViewById(R.id.apply).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String json = "{\"id\":" + RandomUtil.randomInt(100000) + ", \"method\":\"nfc\", \"para\":\"apply\"}";
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

        findViewById(R.id.clear).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mLogView.setText("");
            }
        });

        findViewById(R.id.blk_1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String blk_1_text = mBlk_1_Content.getText().toString();
                if (TextUtils.isEmpty(blk_1_text)) {
                    return;
                }

                sendJson("rf1", blk_1_text);
            }
        });
        findViewById(R.id.blk_2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String blk_2_text = mBlk_2_Content.getText().toString();
                if (TextUtils.isEmpty(blk_2_text)) {
                    return;
                }

                sendJson("rf2", blk_2_text);
            }
        });
        findViewById(R.id.blk_3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String blk_3_text = mBlk_3_Content.getText().toString();
                if (TextUtils.isEmpty(blk_3_text)) {
                    return;
                }

                sendJson("rf3", blk_3_text);
            }
        });
        findViewById(R.id.blk_4).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String blk_4_text = mBlk_4_Content.getText().toString();
                if (TextUtils.isEmpty(blk_4_text)) {
                    return;
                }

                sendJson("rf4", blk_4_text);
            }
        });
        findViewById(R.id.blk_5).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String blk_5_text = mBlk_5_Content.getText().toString();
                if (TextUtils.isEmpty(blk_5_text)) {
                    return;
                }

                sendJson("rf5", blk_5_text);
            }
        });
        findViewById(R.id.blk_6).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String blk_6_text = mBlk_6_Content.getText().toString();
                if (TextUtils.isEmpty(blk_6_text)) {
                    return;
                }

                sendJson("rf6", blk_6_text);
            }
        });
        findViewById(R.id.Tvdd_Cfg_1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String Tvdd_Cfg_1_text = mTvdd_Cfg_1_Content.getText().toString();
                if (TextUtils.isEmpty(Tvdd_Cfg_1_text)) {
                    return;
                }

                sendJson("tvdd_cfg_1", Tvdd_Cfg_1_text);
            }
        });
        findViewById(R.id.Tvdd_Cfg_2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String Tvdd_Cfg_2_text = mTvdd_Cfg_2_Content.getText().toString();
                if (TextUtils.isEmpty(Tvdd_Cfg_2_text)) {
                    return;
                }

                sendJson("tvdd_cfg_2", Tvdd_Cfg_2_text);
            }
        });
        findViewById(R.id.Tvdd_Cfg_3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String Tvdd_Cfg_3_text = mTvdd_Cfg_3_Content.getText().toString();
                if (TextUtils.isEmpty(Tvdd_Cfg_3_text)) {
                    return;
                }

                sendJson("tvdd_cfg_3", Tvdd_Cfg_3_text);
            }
        });
        findViewById(R.id.Core_Conf).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String Core_Conf_text = mCore_Conf_Content.getText().toString();
                if (TextUtils.isEmpty(Core_Conf_text)) {
                    return;
                }

                sendJson("nxp_core_conf", Core_Conf_text);
            }
        });

        findViewById(R.id.blk_1_reset).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBlk_1_Content.setText(getString(R.string.nfc_debug_blk_1_default));
                setPreference(KEY_BLK_1, "");
            }
        });
        findViewById(R.id.blk_2_reset).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBlk_2_Content.setText(getString(R.string.nfc_debug_blk_2_default));
                setPreference(KEY_BLK_2, "");
            }
        });
        findViewById(R.id.blk_3_reset).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBlk_3_Content.setText(getString(R.string.nfc_debug_blk_3_default));
                setPreference(KEY_BLK_3, "");
            }
        });
        findViewById(R.id.blk_4_reset).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBlk_4_Content.setText(getString(R.string.nfc_debug_blk_4_default));
                setPreference(KEY_BLK_4, "");
            }
        });
        findViewById(R.id.blk_5_reset).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBlk_5_Content.setText(getString(R.string.nfc_debug_blk_5_default));
                setPreference(KEY_BLK_5, "");
            }
        });
        findViewById(R.id.blk_6_reset).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBlk_6_Content.setText(getString(R.string.nfc_debug_blk_6_default));
                setPreference(KEY_BLK_6, "");
            }
        });
        findViewById(R.id.Tvdd_Cfg_1_reset).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mTvdd_Cfg_1_Content.setText(getString(R.string.nfc_debug_Tvdd_Cfg_1_default));
                setPreference(KEY_Tvdd_Cfg_1, "");
            }
        });
        findViewById(R.id.Tvdd_Cfg_2_reset).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mTvdd_Cfg_2_Content.setText(getString(R.string.nfc_debug_Tvdd_Cfg_2_default));
                setPreference(KEY_Tvdd_Cfg_2, "");
            }
        });
        findViewById(R.id.Tvdd_Cfg_3_reset).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mTvdd_Cfg_3_Content.setText(getString(R.string.nfc_debug_Tvdd_Cfg_3_default));
                setPreference(KEY_Tvdd_Cfg_3, "");
            }
        });findViewById(R.id.Core_Conf_reset).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCore_Conf_Content.setText(getString(R.string.nfc_debug_Core_Conf_default));
                setPreference(KEY_Core_Conf, "");
            }
        });

        mBlk_1_Content = findViewById(R.id.blk_1_content);
        mBlk_1_Content.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                TestNfcDialogFragment dialog = new TestNfcDialogFragment();
                dialog.setContent(mBlk_1_Content.getText().toString());
                dialog.setOnClickListener(new TestNfcDialogFragment.OnClickListener() {
                    @Override
                    public void onChanged(String content) {
                        if (!TextUtils.isEmpty(content)) {
                            mBlk_1_Content.setText(content);
                            setPreference(KEY_BLK_1, content);
                        }
                    }
                });
                dialog.show(getFragmentManager(), "");
            }
        });

        mBlk_2_Content = findViewById(R.id.blk_2_content);
        mBlk_2_Content.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                TestNfcDialogFragment dialog = new TestNfcDialogFragment();
                dialog.setContent(mBlk_2_Content.getText().toString());
                dialog.setOnClickListener(new TestNfcDialogFragment.OnClickListener() {
                    @Override
                    public void onChanged(String content) {
                        if (!TextUtils.isEmpty(content)) {
                            mBlk_2_Content.setText(content);
                            setPreference(KEY_BLK_2, content);
                        }
                    }
                });
                dialog.show(getFragmentManager(), "");
            }
        });

        mBlk_3_Content = findViewById(R.id.blk_3_content);
        mBlk_3_Content.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                TestNfcDialogFragment dialog = new TestNfcDialogFragment();
                dialog.setContent(mBlk_3_Content.getText().toString());
                dialog.setOnClickListener(new TestNfcDialogFragment.OnClickListener() {
                    @Override
                    public void onChanged(String content) {
                        if (!TextUtils.isEmpty(content)) {
                            mBlk_3_Content.setText(content);
                            setPreference(KEY_BLK_3, content);
                        }
                    }
                });
                dialog.show(getFragmentManager(), "");
            }
        });

        mBlk_4_Content = findViewById(R.id.blk_4_content);
        mBlk_4_Content.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                TestNfcDialogFragment dialog = new TestNfcDialogFragment();
                dialog.setContent(mBlk_4_Content.getText().toString());
                dialog.setOnClickListener(new TestNfcDialogFragment.OnClickListener() {
                    @Override
                    public void onChanged(String content) {
                        if (!TextUtils.isEmpty(content)) {
                            mBlk_4_Content.setText(content);
                            setPreference(KEY_BLK_4, content);
                        }
                    }
                });
                dialog.show(getFragmentManager(), "");
            }
        });

        mBlk_5_Content = findViewById(R.id.blk_5_content);
        mBlk_5_Content.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                TestNfcDialogFragment dialog = new TestNfcDialogFragment();
                dialog.setContent(mBlk_5_Content.getText().toString());
                dialog.setOnClickListener(new TestNfcDialogFragment.OnClickListener() {
                    @Override
                    public void onChanged(String content) {
                        if (!TextUtils.isEmpty(content)) {
                            mBlk_5_Content.setText(content);
                            setPreference(KEY_BLK_5, content);
                        }
                    }
                });
                dialog.show(getFragmentManager(), "");
            }
        });

        mBlk_6_Content = findViewById(R.id.blk_6_content);
        mBlk_6_Content.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                TestNfcDialogFragment dialog = new TestNfcDialogFragment();
                dialog.setContent(mBlk_6_Content.getText().toString());
                dialog.setOnClickListener(new TestNfcDialogFragment.OnClickListener() {
                    @Override
                    public void onChanged(String content) {
                        if (!TextUtils.isEmpty(content)) {
                            mBlk_6_Content.setText(content);
                            setPreference(KEY_BLK_6, content);
                        }
                    }
                });
                dialog.show(getFragmentManager(), "");
            }
        });
        mTvdd_Cfg_1_Content = findViewById(R.id.Tvdd_Cfg_1_content);
        mTvdd_Cfg_1_Content.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                TestNfcDialogFragment dialog = new TestNfcDialogFragment();
                dialog.setContent(mTvdd_Cfg_1_Content.getText().toString());
                dialog.setOnClickListener(new TestNfcDialogFragment.OnClickListener() {
                    @Override
                    public void onChanged(String content) {
                        if (!TextUtils.isEmpty(content)) {
                            mTvdd_Cfg_1_Content.setText(content);
                            setPreference(KEY_Tvdd_Cfg_1, content);
                        }
                    }
                });
                dialog.show(getFragmentManager(), "");
            }
        });
        mTvdd_Cfg_2_Content = findViewById(R.id.Tvdd_Cfg_2_content);
        mTvdd_Cfg_2_Content.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                TestNfcDialogFragment dialog = new TestNfcDialogFragment();
                dialog.setContent(mTvdd_Cfg_2_Content.getText().toString());
                dialog.setOnClickListener(new TestNfcDialogFragment.OnClickListener() {
                    @Override
                    public void onChanged(String content) {
                        if (!TextUtils.isEmpty(content)) {
                            mTvdd_Cfg_2_Content.setText(content);
                            setPreference(KEY_Tvdd_Cfg_2, content);
                        }
                    }
                });
                dialog.show(getFragmentManager(), "");
            }
        });
        mTvdd_Cfg_3_Content = findViewById(R.id.Tvdd_Cfg_3_content);
        mTvdd_Cfg_3_Content.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                TestNfcDialogFragment dialog = new TestNfcDialogFragment();
                dialog.setContent(mTvdd_Cfg_3_Content.getText().toString());
                dialog.setOnClickListener(new TestNfcDialogFragment.OnClickListener() {
                    @Override
                    public void onChanged(String content) {
                        if (!TextUtils.isEmpty(content)) {
                            mTvdd_Cfg_3_Content.setText(content);
                            setPreference(KEY_Tvdd_Cfg_3, content);
                        }
                    }
                });
                dialog.show(getFragmentManager(), "");
            }
        });
        mCore_Conf_Content = findViewById(R.id.Core_Conf_content);
        mCore_Conf_Content.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                TestNfcDialogFragment dialog = new TestNfcDialogFragment();
                dialog.setContent(mCore_Conf_Content.getText().toString());
                dialog.setOnClickListener(new TestNfcDialogFragment.OnClickListener() {
                    @Override
                    public void onChanged(String content) {
                        if (!TextUtils.isEmpty(content)) {
                            mCore_Conf_Content.setText(content);
                            setPreference(KEY_Core_Conf, content);
                        }
                    }
                });
                dialog.show(getFragmentManager(), "");
            }
        });

        String blk_1_pref_str = getPreference(KEY_BLK_1);
        if (!TextUtils.isEmpty(blk_1_pref_str)) {
            mBlk_1_Content.setText(blk_1_pref_str);
        } else {
            mBlk_1_Content.setText(getString(R.string.nfc_debug_blk_1_default));
        }
        String blk_2_pref_str = getPreference(KEY_BLK_2);
        if (!TextUtils.isEmpty(blk_2_pref_str)) {
            mBlk_2_Content.setText(blk_2_pref_str);
        } else {
            mBlk_2_Content.setText(getString(R.string.nfc_debug_blk_2_default));
        }
        String blk_3_pref_str = getPreference(KEY_BLK_3);
        if (!TextUtils.isEmpty(blk_3_pref_str)) {
            mBlk_3_Content.setText(blk_3_pref_str);
        } else {
            mBlk_3_Content.setText(getString(R.string.nfc_debug_blk_3_default));
        }
        String blk_4_pref_str = getPreference(KEY_BLK_4);
        if (!TextUtils.isEmpty(blk_4_pref_str)) {
            mBlk_4_Content.setText(blk_4_pref_str);
        } else {
            mBlk_4_Content.setText(getString(R.string.nfc_debug_blk_4_default));
        }
        String blk_5_pref_str = getPreference(KEY_BLK_5);
        if (!TextUtils.isEmpty(blk_5_pref_str)) {
            mBlk_5_Content.setText(blk_5_pref_str);
        } else {
            mBlk_5_Content.setText(getString(R.string.nfc_debug_blk_5_default));
        }
        String blk_6_pref_str = getPreference(KEY_BLK_6);
        if (!TextUtils.isEmpty(blk_6_pref_str)) {
            mBlk_6_Content.setText(blk_6_pref_str);
        } else {
            mBlk_6_Content.setText(getString(R.string.nfc_debug_blk_6_default));
        }
        String tvdd_cfg_1_pref_str = getPreference(KEY_Tvdd_Cfg_1);
        if (!TextUtils.isEmpty(tvdd_cfg_1_pref_str)) {
            mTvdd_Cfg_1_Content.setText(tvdd_cfg_1_pref_str);
        } else {
            mTvdd_Cfg_1_Content.setText(getString(R.string.nfc_debug_Tvdd_Cfg_1_default));
        }
        String tvdd_cfg_2_pref_str = getPreference(KEY_Tvdd_Cfg_2);
        if (!TextUtils.isEmpty(tvdd_cfg_1_pref_str)) {
            mTvdd_Cfg_2_Content.setText(tvdd_cfg_2_pref_str);
        } else {
            mTvdd_Cfg_2_Content.setText(getString(R.string.nfc_debug_Tvdd_Cfg_2_default));
        }
        String tvdd_cfg_3_pref_str = getPreference(KEY_Tvdd_Cfg_3);
        if (!TextUtils.isEmpty(tvdd_cfg_3_pref_str)) {
            mTvdd_Cfg_3_Content.setText(tvdd_cfg_3_pref_str);
        } else {
            mTvdd_Cfg_3_Content.setText(getString(R.string.nfc_debug_Tvdd_Cfg_3_default));
        }
        String core_conf_pref_str = getPreference(KEY_Core_Conf);
        if (!TextUtils.isEmpty(core_conf_pref_str)) {
            mCore_Conf_Content.setText(core_conf_pref_str);
        } else {
            mCore_Conf_Content.setText(getString(R.string.nfc_debug_Core_Conf_default));
        }

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

    private void sendJson(String paramKey, String paramValue) {
        String finalValue = paramValue.replace("\n", "").replace(" ", "").replace(",", "").replace("0x", "");

        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("id", RandomUtil.randomInt(100000));
            jsonObject.put("method", "nfc");
            JSONObject paramObj = new JSONObject();
            paramObj.put(paramKey, finalValue);
            jsonObject.put("para", paramObj);

            String json = jsonObject.toString();
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
        } catch (Exception e) {

        }
    }

    private void setPreference(String key, String value) {
        PrefsUtil.setSettingString(PreferenceManager.getDefaultSharedPreferences(mContext), key, value);
    }

    private String getPreference(String key) {
        return PrefsUtil.getSettingString(PreferenceManager.getDefaultSharedPreferences(mContext), key, "");
    }

    private void showMsg(String msg) {
        mLogView.append(msg + "\n");
        mLogView.setOverScrollMode(View.OVER_SCROLL_ALWAYS);
        mLogView.scrollTo(0, mLogView.getHeight());
    }
}
