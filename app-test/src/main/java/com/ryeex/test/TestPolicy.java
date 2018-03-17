package com.ryeex.test;

import com.ryeex.groot.lib.ble.requestresult.DescriptorWriteResult;
import com.ryeex.groot.lib.common.Error;
import com.ryeex.groot.lib.common.asynccallback.AsyncCallback;
import com.ryeex.test.device.TestDevice;

import static com.ryeex.groot.lib.ble.BleSetting.CHARACTER_RYEEX_OPEN;
import static com.ryeex.groot.lib.ble.BleSetting.CHARACTER_RYEEX_RC4;
import static com.ryeex.groot.lib.ble.BleSetting.SERVICE_RYEEX;

/**
 * Created by chenhao on 2018/1/20.
 */

public class TestPolicy {
    public static void startConnect(final TestDevice testDevice, final AsyncCallback<Void, Error> callback) {
        if (testDevice.isConnected()) {
            if (callback != null) {
                callback.sendSuccessMessage(null);
            }
        } else {
            testDevice.connect(new AsyncCallback<Void, Error>() {
                @Override
                public void onSuccess(Void result) {
                    testDevice.notify(SERVICE_RYEEX, CHARACTER_RYEEX_OPEN, new AsyncCallback<DescriptorWriteResult, Error>() {
                        @Override
                        public void onSuccess(DescriptorWriteResult result) {
                            testDevice.notify(SERVICE_RYEEX, CHARACTER_RYEEX_RC4, new AsyncCallback<DescriptorWriteResult, Error>() {
                                @Override
                                public void onSuccess(DescriptorWriteResult result) {
                                    if (callback != null) {
                                        callback.sendSuccessMessage(null);
                                    }
                                }

                                @Override
                                public void onFailure(Error error) {
                                    if (callback != null) {
                                        callback.sendFailureMessage(error);
                                    }
                                }
                            });
                        }

                        @Override
                        public void onFailure(Error error) {
                            if (callback != null) {
                                callback.sendFailureMessage(error);
                            }
                        }
                    });

                }

                @Override
                public void onFailure(Error error) {
                    if (callback != null) {
                        callback.sendFailureMessage(error);
                    }
                }
            });
        }
    }
}
