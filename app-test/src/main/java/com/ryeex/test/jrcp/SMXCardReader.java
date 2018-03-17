package com.ryeex.test.jrcp;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.ryeex.groot.lib.ble.stack.app.BleApi;
import com.ryeex.groot.lib.common.util.ByteUtil;
import com.ryeex.test.JRCPActivity;
import com.ryeex.test.device.TestDeviceManager;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

/**
 * Created by chenhao on 2018/1/5.
 */

public class SMXCardReader {
    private static final String TAG = "groot-ble";
    private final static int JRCP_HEADER_LENGTH = 4;
    private final static int JRCP_MTY_OFFSET = 0;
    private final static int JRCP_NAD_OFFSET = 1;
    private final static int JRCP_LNH_OFFSET = 2;
    private final static int JRCP_LNL_OFFSET = 3;
    private final static int JRCP_PAYLOAD_OFFSET = 4;
    private final static int JRCP_WAIT_FOR_CARD = 0;
    private final static int JRCP_APDU_DATA = 1;
    private final static int JRCP_STATUS = 2;
    private final static int JRCP_ERROR_MESSAGE = 3;
    private final static int JRCP_TERMINAL_INFO = 4;
    private final static int JRCP_INIT_INFO = 5;
    private final static int JRCP_ECHO = 6;
    private final static int JRCP_DEBUG = 7;
    String mSEId = "";
    int mSMXHandle = -1;
    Context mContext;
    private Handler mHandler;
    // private NfcSecureElement mSE = null;

    public SMXCardReader(Object se, String se_id, Context cntx, Handler handler) {
        // mSE = se;
        mSEId = se_id;
        mContext = cntx;
        mHandler = handler;
    }

    private void sendMsg(int type, String message) {
        Bundle b = new Bundle();
        b.putString("msg", message);
        if (mHandler != null) {
            Message msg = mHandler.obtainMessage(type);
            msg.setData(b);
            mHandler.sendMessage(msg);
        }
    }

    public void exchangeAPDU(JRCPServer server) throws IOException {
        byte[] dataBuffer = new byte[1024];
        byte[] headerBuffer = new byte[4];

        int length = server.receiveData(headerBuffer, headerBuffer.length);

        // try again if all zero
        if (headerBuffer[0] == 0 && headerBuffer[1] == 0 && headerBuffer[2] == 0 && headerBuffer[3] == 0) {
            length = server.receiveData(headerBuffer, headerBuffer.length);
        }
        if (length != JRCP_HEADER_LENGTH) {
            throw new IOException("bad data! haderDuffer = " + headerBuffer + ",length=" + length);
        }

        // Toast.makeText(appContext, "receive Data: " + Utils.toHexString(buffer, 0, length),
        // Toast.LENGTH_SHORT).show();
//        Log.d(TAG, "receive header: " + Utils.toHexString(headerBuffer, 0, length));

        switch (headerBuffer[JRCP_MTY_OFFSET]) {
            case JRCP_WAIT_FOR_CARD: {
//                Log.d(TAG, "WAIT_FOR_CARD cmd, no API can retreive ATR, ignore?");
                length = server.receiveData(dataBuffer, dataBuffer.length);
                byte[] apduBytes = new byte[JRCP_HEADER_LENGTH];
                System.arraycopy(headerBuffer, 0, apduBytes, JRCP_PAYLOAD_OFFSET, length - JRCP_HEADER_LENGTH);
//                byte[] respAPDU = new byte[]{
//                        (byte) 0x90, 0x00
//                };

                sendMsg(JRCPActivity.MSG_SUCCESS, "BleApi.sendApduTest " + ByteUtil.byteToString(apduBytes));
                byte[] respAPDU = BleApi.sendApduTest(TestDeviceManager.getInstance().getDevice().getBleManager(), apduBytes);
                // byte[] respAPDU = mSE.exchangeAPDU(mSMXHandle, apdu);

                Log.d(TAG, "APDU response: " + ByteUtil.byteToString(respAPDU));
                sendMsg(JRCPActivity.MSG_SUCCESS, "APDU response: " + ByteUtil.byteToString(respAPDU));
//                Log.d(TAG, "APDU response: " + Utils.toHexString(respAPDU, 0, respAPDU.length));
                byte[] sock_resp = new byte[respAPDU.length + JRCP_HEADER_LENGTH];

                sock_resp[JRCP_MTY_OFFSET] = headerBuffer[JRCP_MTY_OFFSET];
                sock_resp[JRCP_NAD_OFFSET] = headerBuffer[JRCP_NAD_OFFSET];
                sock_resp[JRCP_LNH_OFFSET] = (byte) ((respAPDU.length >> 8) & 0xFF);
                sock_resp[JRCP_LNL_OFFSET] = (byte) (respAPDU.length & 0xFF);
                System.arraycopy(respAPDU, 0, sock_resp, JRCP_PAYLOAD_OFFSET, respAPDU.length);

                server.sendData(sock_resp, 0, JRCP_HEADER_LENGTH);
                server.sendData(sock_resp, JRCP_PAYLOAD_OFFSET, sock_resp.length - JRCP_HEADER_LENGTH);
                break;
            }
            case JRCP_APDU_DATA: {
                Log.d(TAG, "JRCP_APDU_DATA cmd");
                sendMsg(JRCPActivity.MSG_SUCCESS, "JRCP_APDU_DATA");

                if (length == 0) {
                    // empty APDU, e.g. CARD_PRESENT
                    byte[] sock_resp = new byte[JRCP_HEADER_LENGTH];

                    sock_resp[JRCP_MTY_OFFSET] = headerBuffer[JRCP_MTY_OFFSET];
                    sock_resp[JRCP_NAD_OFFSET] = headerBuffer[JRCP_NAD_OFFSET];
                    sock_resp[JRCP_LNH_OFFSET] = 0;
                    sock_resp[JRCP_LNL_OFFSET] = 0;
                    server.sendData(sock_resp, 0, JRCP_HEADER_LENGTH);
                    break;
                } else {
                    length = server.receiveData(dataBuffer, dataBuffer.length);
                    if (length == 0) {
                        throw new IOException("APDU cmd error");
                    }
                    Log.d(TAG, "send APDU: " + Utils.toHexString(dataBuffer, 0, length));
                    sendMsg(JRCPActivity.MSG_SUCCESS, "send APDU: " + Utils.toHexString(dataBuffer, 0, length));
                    byte[] apdu = new byte[length];
                    System.arraycopy(dataBuffer, 0, apdu, 0, length);
                    if (apdu[0] == (byte) 0x80 && apdu[1] == (byte) 0xE4) {
                        // delete cmd, reduce HCI timeout

                        Random r = new Random();
                        int timeout = r.nextInt(1800) + 200;
                        // mSE.setTimeout(timeout);
                        Log.d(TAG, "delete command, set HCI timeout to " + timeout);
                    } else {
                        // mSE.setTimeout(1000000);
                    }


                    sendMsg(JRCPActivity.MSG_SUCCESS, "BleApi.sendApduTest " + ByteUtil.byteToString(apdu));
                    byte[] respAPDU = BleApi.sendApduTest(TestDeviceManager.getInstance().getDevice().getBleManager(), apdu);

                    // byte[] respAPDU = mSE.exchangeAPDU(mSMXHandle, apdu);
//                    byte[] respAPDU = null;// = mSE.exchangeAPDU(mSMXHandle, apdu);
                    Log.d(TAG, "APDU response: " + ByteUtil.byteToString(respAPDU));// Utils.toHexString(respAPDU, 0, respAPDU.length));
                    sendMsg(JRCPActivity.MSG_SUCCESS, "APDU response: " + ByteUtil.byteToString(respAPDU));
                    byte[] sock_resp = new byte[respAPDU.length + JRCP_HEADER_LENGTH];

                    sock_resp[JRCP_MTY_OFFSET] = headerBuffer[JRCP_MTY_OFFSET];
                    sock_resp[JRCP_NAD_OFFSET] = headerBuffer[JRCP_NAD_OFFSET];
                    sock_resp[JRCP_LNH_OFFSET] = (byte) ((respAPDU.length >> 8) & 0xFF);
                    sock_resp[JRCP_LNL_OFFSET] = (byte) (respAPDU.length & 0xFF);
                    System.arraycopy(respAPDU, 0, sock_resp, JRCP_PAYLOAD_OFFSET, respAPDU.length);
                    server.sendData(sock_resp, 0, JRCP_HEADER_LENGTH);
                    server.sendData(sock_resp, JRCP_PAYLOAD_OFFSET, sock_resp.length - JRCP_HEADER_LENGTH);
                    break;
                }
            }
            case JRCP_STATUS: {
                Log.d(TAG, "JRCP_STATUS cmd.");
                int payload_length = 4;
                byte[] sock_resp = new byte[JRCP_HEADER_LENGTH + payload_length];

                sock_resp[JRCP_MTY_OFFSET] = headerBuffer[JRCP_MTY_OFFSET];
                sock_resp[JRCP_NAD_OFFSET] = headerBuffer[JRCP_NAD_OFFSET];
                sock_resp[JRCP_LNH_OFFSET] = (byte) ((payload_length >> 8) & 0xFF);
                sock_resp[JRCP_LNL_OFFSET] = (byte) (payload_length & 0xFF);
                sock_resp[JRCP_PAYLOAD_OFFSET] = 0;
                sock_resp[5] = 0;
                sock_resp[6] = 0;
                sock_resp[7] = 0x04;
//                Log.d(TAG, "data response: " + Utils.toHexString(sock_resp, 0, sock_resp.length));
                server.sendData(sock_resp, 0, JRCP_HEADER_LENGTH);
                server.sendData(sock_resp, JRCP_PAYLOAD_OFFSET, sock_resp.length - JRCP_HEADER_LENGTH);
                break;
            }
            case JRCP_ERROR_MESSAGE: {
                Log.d(TAG, "JRCP_ERROR_MESSAGE cmd.");
                if (length != JRCP_HEADER_LENGTH || headerBuffer[JRCP_LNH_OFFSET] != 0
                        || headerBuffer[JRCP_LNL_OFFSET] != 0) {
                    throw new IOException("bad cmd");
                }
                // TODO: how to get the error message?
                server.sendData(dataBuffer, 0, length);
                break;
            }
            case JRCP_TERMINAL_INFO: {
                Log.d(TAG, "JRCP_TERMINAL_INFO cmd.");
                Date date = new Date();
                DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String s = df.format(date);
                byte[] sock_resp = new byte[JRCP_HEADER_LENGTH + s.length()];
                sock_resp[JRCP_MTY_OFFSET] = headerBuffer[JRCP_MTY_OFFSET];
                sock_resp[JRCP_NAD_OFFSET] = headerBuffer[JRCP_NAD_OFFSET];
                sock_resp[JRCP_LNH_OFFSET] = (byte) ((s.length() >> 8) & 0xFF);
                sock_resp[JRCP_LNL_OFFSET] = (byte) (s.length() & 0xFF);
                System.arraycopy(s.getBytes(), 0, sock_resp, JRCP_PAYLOAD_OFFSET, s.length());
                server.sendData(sock_resp, 0, sock_resp.length);
                break;
            }
            case JRCP_INIT_INFO: {
                Log.d(TAG, "JRCP_INIT_INFO cmd.");
                length = server.receiveData(dataBuffer, dataBuffer.length);
                if (length == 0) {
                    throw new IOException("JRCP_INIT_INFO cmd error");
                }
                server.sendData(dataBuffer, 0, JRCP_HEADER_LENGTH);
                break;
            }
            case JRCP_ECHO: {
                Log.d(TAG, "JRCP_ECHO cmd.");
                length = server.receiveData(dataBuffer, dataBuffer.length);
                if (length == 0) {
                    throw new IOException("JRCP_ECHO cmd error");
                }
                byte[] textBytes = new byte[length - JRCP_HEADER_LENGTH];
                System.arraycopy(dataBuffer, 0, textBytes, JRCP_PAYLOAD_OFFSET,
                        length - JRCP_HEADER_LENGTH);
                String msg = new String(textBytes);
                Toast.makeText(mContext, "echo msg: " + msg, Toast.LENGTH_SHORT).show();
                break;
            }
            case JRCP_DEBUG: {
                Log.d(TAG, "JRCP_DEBUG cmd.");
                length = server.receiveData(dataBuffer, dataBuffer.length);
                if (length == 0) {
                    throw new IOException("APDU cmd error");
                }
                byte[] sock_resp = new byte[JRCP_HEADER_LENGTH];

                sock_resp[JRCP_MTY_OFFSET] = headerBuffer[JRCP_MTY_OFFSET];
                sock_resp[JRCP_NAD_OFFSET] = headerBuffer[JRCP_NAD_OFFSET];
                sock_resp[JRCP_LNH_OFFSET] = 0;
                sock_resp[JRCP_LNL_OFFSET] = 0;
                server.sendData(sock_resp, 0, JRCP_HEADER_LENGTH);
                break;
            }
            default: {
                throw new IOException("bad cmd! cmd = " + headerBuffer[0]);
            }
        }

    }

    public void OpenSecureElementConnection() {
        // try {
        // if (mSE != null)
        // mSMXHandle = mSE.openSecureElementConnection(mSEId);
        // if (mSMXHandle == -1) {
        // throw new IOException("fail to open SE in wired mode!");
        // }
        // } catch (IOException ioe) {
        // ioe.printStackTrace();
        // mSMXHandle = -1;
        // Toast.makeText(mContext, "IO Excetpion during open secure element connection: " + ioe,
        // Toast.LENGTH_SHORT).show();
        // } catch (Exception e) {
        // e.printStackTrace();
        // Toast.makeText(mContext, "Excetpion during APDU trancieve: " + e, Toast.LENGTH_SHORT)
        // .show();
        // mSMXHandle = -1;
        // }
    }

    public void CloseSecureElementConnection() {
        // try {
        // if ((mSE != null) && (mSMXHandle != -1))
        // System.out.println("close SE connection 11111111");
        // mSE.closeSecureElementConnection(mSMXHandle);
        // System.out.println("close SE connection 22222222");
        // } catch (IOException ioe) {
        // ioe.printStackTrace();
        // Toast.makeText(mContext, "IO Excetpion during close secure element connection: " + ioe,
        // Toast.LENGTH_SHORT).show();
        // } catch (Exception e) {
        // e.printStackTrace();
        // Toast.makeText(mContext, "Excetpion during APDU trancieve: " + e, Toast.LENGTH_SHORT)
        // .show();
        // mSMXHandle = -1;
        // }
    }
}
