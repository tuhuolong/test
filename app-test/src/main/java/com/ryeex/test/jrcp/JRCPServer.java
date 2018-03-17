package com.ryeex.test.jrcp;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.ryeex.test.JRCPActivity;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by chenhao on 2018/1/5.
 */

public class JRCPServer extends Thread {
    private static final String TAG = "JRCPServer";

    private final int SERVER_TIMEOUT = 5000;
    private ServerSocket mServerSock = null;
    private Socket mSock = null;
    private InputStream mis = null;
    private OutputStream mos = null;
    private boolean mRunning = true;
    private SMXCardReader mSMXReader = null;

    private NfcAdapter mNfcAdapter;

    private Context mContext;
    private Handler mHandler;

    public JRCPServer(Context context, Handler handler) {
        mContext = context;
        mHandler = handler;
    }

    private void openSE() {
        try {
            // mNfcAdapter = NfcAdapter.getDefaultAdapter(mContext);
            // System.out.println(NfcAdapter.SMART_MX_ID);
            // mNfcAdapter.selectDefaultSecureElement("com.nxp.smart_mx.ID");
            // // mNfcAdapter.selectDefaultSecureElement(NfcAdapter.SMART_MX_ID);
            // mNfcAdapter.setDefaultSecureElementState(true);
            // String seID = mNfcAdapter.getDefaultSelectedSecureElement();
            // Log.d(TAG, "seID=" + seID);
            // NfcSecureElement Se = mNfcAdapter.createNfcSecureElementConnection();
            // mSMXReader = new SMXCardReader(Se, seID, mContext);
            // mSMXReader.OpenSecureElementConnection();
            mSMXReader = new SMXCardReader(null, null, mContext, mHandler);

        } catch (Exception e) {
            e.printStackTrace();
            sendMsg(JRCPActivity.MSG_FAILURE, e.getMessage());
        }
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

    public void run() {
        mRunning = true;
        while (mRunning) {
            // Server is waiting for client here, if needed
            openSE();

            try {
                createServer();
                Log.d(TAG, "waiting for accept...");
                mSock = mServerSock.accept();
                sendMsg(JRCPActivity.MSG_SUCCESS, "accept by " + mSock.getInetAddress().getHostAddress());
                // mServerSock.setSoTimeout(SERVER_TIMEOUT);
                Log.d(TAG, "accepted");
                mos = mSock.getOutputStream();
                mis = mSock.getInputStream();
                while (mSock.isConnected()) {
                    mSMXReader.exchangeAPDU(this);
                }
                closeConnection();
                // CloseSocket();
            } catch (IOException ioe) {
                ioe.printStackTrace();
                sendMsg(JRCPActivity.MSG_FAILURE, ioe.getMessage() + ", SMX has disconnected.");
            } catch (Exception e) {
                e.printStackTrace();
                sendMsg(JRCPActivity.MSG_FAILURE, e.getMessage());
            } finally {
                try {
                    if (mSMXReader != null) {
                        mSMXReader.CloseSecureElementConnection();
                    }
                    mSock.close();
                } catch (Exception e) {
                    e.printStackTrace();
                    sendMsg(JRCPActivity.MSG_FAILURE, e.getMessage());
                }
            }
        }

        try {
            if (mServerSock != null) {
                mServerSock.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
            sendMsg(JRCPActivity.MSG_FAILURE, e.getMessage());
        }
    }

    public void closeConnection() {
        try {
            if (mSMXReader != null) {
                mSMXReader.CloseSecureElementConnection();
            }
        } catch (Exception e) {
            e.printStackTrace();
            sendMsg(JRCPActivity.MSG_FAILURE, e.getMessage());
        } finally {
            try {
                if (mServerSock != null)
                    mServerSock.close();
            } catch (Exception e) {

            }
        }
        mRunning = false;

    }

    private void createServer() throws IOException {
        WifiManager wifiManager = (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        int ipAddress = wifiInfo.getIpAddress();
        Log.d(TAG, "ipaddress = " + (ipAddress & 0xFF) + "." + ((ipAddress >> 8) & 0xFF) + "." + ((ipAddress >> 16) & 0xFF) + "." + ((ipAddress >> 24) & 0xFF));
        String host = (ipAddress & 0xFF) + "." + ((ipAddress >> 8) & 0xFF) + "." + ((ipAddress >> 16) & 0xFF) + "." + ((ipAddress >> 24) & 0xFF);
//        host = "0.0.0.0";
        int port = 8050;
        InetSocketAddress addr = new InetSocketAddress(host, port);
        mServerSock = new ServerSocket();
        mServerSock.bind(addr);
        Log.d(TAG, "listening in " + host + ", port " + port + "...");
        sendMsg(JRCPActivity.MSG_SUCCESS, "listening in " + host + ", port " + port + "...");
    }

    private void CloseSocket() throws IOException {
        if (mis != null) {
            mis.close();
        }
        if (mos != null) {
            mos.close();
        }
        if (mSock != null) {
            mSock.close();
        }
        sendMsg(JRCPActivity.MSG_SUCCESS, "connection closed..");
    }

    public int receiveData(byte[] inBuffer, int size) throws IOException {
        int length = mis.read(inBuffer, 0, size);
        // sendMsg(JRCPTestActivity.MSG_SUCCESS, "received data: "+Utils.toHexString(inBuffer, 0,
        // length));
        return length;
    }

    public void sendData(byte[] outBuffer, int offset, int size) throws IOException {
//        Log.d(TAG, "sending data: " + Utils.toHexString(outBuffer, offset, size));
        // sendMsg(JRCPTestActivity.MSG_SUCCESS, "sending data: "+Utils.toHexString(outBuffer,
        // offset, size));

        mos.write(outBuffer, offset, size);
        mos.flush();
    }

    public boolean isRunning() {
        return mRunning;
    }
}
