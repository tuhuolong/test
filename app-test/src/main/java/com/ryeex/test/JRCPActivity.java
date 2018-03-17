package com.ryeex.test;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.ryeex.test.jrcp.JRCPServer;

/**
 * Created by chenhao on 2018/1/5.
 */

public class JRCPActivity extends FragmentActivity {
    public static final int MSG_SUCCESS = 1;
    public static final int MSG_FAILURE = 2;

    private Button mStartServer;
    private TextView mTextView;
    private JRCPServer mServer = null;

    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_SUCCESS:
                    mTextView.append(msg.getData().getCharSequence("msg") + "\n");
                    mTextView.setOverScrollMode(View.OVER_SCROLL_ALWAYS);
                    mTextView.scrollTo(0, mTextView.getHeight());
                    break;
                case MSG_FAILURE:
                    mTextView.append("[ERROR]" + msg.getData().getCharSequence("msg") + "\n");
                    mTextView.scrollTo(0, mTextView.getHeight());
                    break;
            }
        }
    };


    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_test_jrcp);

        mStartServer = findViewById(R.id.start_server);
        mStartServer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startServer();
            }
        });

        mTextView = findViewById(R.id.logs);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mServer != null) {
            mServer.closeConnection();
        }
    }

    public void startServer() {
        try {
            if (mServer == null || mServer.isRunning() == false) {
                if (mServer == null) {
                    mServer = new JRCPServer(this, mHandler);
                }
                mServer.start();
            } else {
                mServer.closeConnection();
                mServer.stop();
                finish();
            }
        } catch (Exception e) {
            finish();
        }
    }
}
