package com.ryeex.test;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.content.PermissionChecker;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ryeex.groot.lib.ble.scan.BleScanner;
import com.ryeex.groot.lib.ble.scan.ScannedDevice;
import com.ryeex.groot.lib.common.recyclerview.RecyclerViewItemTouchListener;
import com.ryeex.groot.lib.common.util.DisplayUtil;
import com.ryeex.test.app.TestApplication;
import com.ryeex.test.device.TestDevice;
import com.ryeex.test.device.TestDeviceManager;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;

/**
 * Created by chenhao on 2018/1/5.
 */

public class TestSelectActivity extends FragmentActivity {
    public static final String ACTION_COMPLETE = "com.ryeex.test.select.complete";
    public static final String ERROR_CODE = "error_code";
    public static final int SUCCESS = 1;
    public static final int FAILURE = 2;
    public static final int CANCEL = 3;

    Context mContext;

    ImageView mScanIcon;

    List<ScannedDevice> mScannedDeviceList = new CopyOnWriteArrayList<>();
    RecyclerView mRecyclerView;
    DeviceAdapter mAdapter;
    BroadcastReceiver mBleScanReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(BleScanner.ACTION_SCANNED_DEVICE)) {
                ScannedDevice scannedDevice = intent.getParcelableExtra(BleScanner.DEVICE);

                boolean already = false;
                for (ScannedDevice deviceItem : mScannedDeviceList) {
                    if (deviceItem.getMac().equals(scannedDevice.getMac())) {
                        already = true;
                        break;
                    }
                }

                if (!already) {
                    mScannedDeviceList.add(scannedDevice);
                    refreshUI();
                }
            } else if (action.equals(BleScanner.ACTION_SCAN_STOPPED)) {
                stopScan();
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mContext = this;

        checkPermission();

        setContentView(R.layout.activity_test_select);

        mScanIcon = findViewById(R.id.scan_btn);
        mScanIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startScan();
            }
        });

        mRecyclerView = findViewById(R.id.device_list);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        mAdapter = new DeviceAdapter();
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(getResources(), LinearLayoutManager.VERTICAL));
        mRecyclerView.addOnItemTouchListener(new RecyclerViewItemTouchListener(mContext, mRecyclerView, new RecyclerViewItemTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                startSelect(mScannedDeviceList.get(position));
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));

        IntentFilter bleScanFilter = new IntentFilter();
        bleScanFilter.addAction(BleScanner.ACTION_SCANNED_DEVICE);
        bleScanFilter.addAction(BleScanner.ACTION_SCAN_STOPPED);
        LocalBroadcastManager.getInstance(mContext).registerReceiver(mBleScanReceiver, bleScanFilter);

        startScan();
        refreshUI();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        sendCompleteBroadcast(CANCEL);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        LocalBroadcastManager.getInstance(mContext).unregisterReceiver(mBleScanReceiver);
        stopScan();
    }

    private void checkPermission() {
        if (PermissionChecker.checkSelfPermission(this, ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, ACCESS_COARSE_LOCATION)) {
                Toast.makeText(mContext, "请开启位置信息权限", Toast.LENGTH_SHORT).show();
            } else {
                ActivityCompat.requestPermissions(this, new String[]{ACCESS_COARSE_LOCATION}, 12345);
            }
        }
    }

    private void refreshUI() {
        mAdapter.notifyDataSetChanged();
    }

    private void startScan() {
        mScannedDeviceList.clear();
        BleScanner.getInstance().startScan();

        int round = 50;
        RotateAnimation animation = new RotateAnimation(0f, 360f * round, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        animation.setDuration(1000 * round);
        animation.setRepeatCount(-1);
        animation.setFillAfter(true);
        animation.setInterpolator(new LinearInterpolator());
        mScanIcon.startAnimation(animation);

        TestApplication.getGlobalUiHandler().postDelayed(new Runnable() {
            @Override
            public void run() {
                stopScan();
            }
        }, 60 * DateUtils.SECOND_IN_MILLIS);
    }

    private void stopScan() {
        BleScanner.getInstance().stopScan();

        mScanIcon.clearAnimation();
    }

    private void startSelect(ScannedDevice scannedDevice) {
        TestDevice device = new TestDevice();
        device.setMac(scannedDevice.getMac());
        TestDeviceManager.getInstance().addDevice(device);

        finish();
        sendCompleteBroadcast(SUCCESS);
    }

    private void sendCompleteBroadcast(int errorCode) {
        LocalBroadcastManager localBroadManager = LocalBroadcastManager.getInstance(mContext);
        Intent param = new Intent(ACTION_COMPLETE);
        param.putExtra(ERROR_CODE, errorCode);
        localBroadManager.sendBroadcast(param);
    }

    public class DeviceAdapter extends RecyclerView.Adapter<DeviceAdapter.DeviceViewHolder> {
        @Override
        public DeviceViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            final LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            View view = inflater.inflate(R.layout.lemon_device_item, parent, false);
            return new DeviceViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final DeviceViewHolder holder, final int position) {
            ScannedDevice device = mScannedDeviceList.get(position);
            holder.mName.setText(device.getName());
            holder.mMac.setText(device.getMac());
        }

        @Override
        public int getItemCount() {
            return mScannedDeviceList.size();
        }

        public class DeviceViewHolder extends RecyclerView.ViewHolder {
            public TextView mName;
            public TextView mMac;

            DeviceViewHolder(View itemView) {
                super(itemView);

                mName = itemView.findViewById(R.id.name);
                mMac = itemView.findViewById(R.id.mac);
            }

        }

    }

    public class DividerItemDecoration extends RecyclerView.ItemDecoration {
        private final Drawable mDivider;
        private final int mSize;
        private final int mOrientation;

        public DividerItemDecoration(Resources resources, int orientation) {
            mDivider = new ColorDrawable(Color.parseColor("#0F000000"));
            mSize = resources.getDimensionPixelSize(R.dimen.test_divider_size);
            mOrientation = orientation;
        }

        @Override
        public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
            int left;
            int right;
            int top;
            int bottom;
            if (mOrientation == LinearLayoutManager.HORIZONTAL) {
                top = parent.getPaddingTop();
                bottom = parent.getHeight() - parent.getPaddingBottom();
                final int childCount = parent.getChildCount();
                for (int i = 0; i < childCount - 1; i++) {
                    final View child = parent.getChildAt(i);
                    final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();
                    left = child.getRight() + params.rightMargin;
                    right = left + mSize;
                    mDivider.setBounds(left, top, right, bottom);
                    mDivider.draw(c);
                }
            } else {
                int padding = DisplayUtil.dip2px(TestApplication.getAppContext(), 0);
                left = parent.getPaddingLeft();
                right = parent.getWidth() - parent.getPaddingRight();
                final int childCount = parent.getChildCount();
                for (int i = 0; i < childCount - 1; i++) {
                    final View child = parent.getChildAt(i);
                    final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();
                    top = child.getBottom() + params.bottomMargin;
                    bottom = top + mSize;
                    mDivider.setBounds(left + padding, top, right - padding, bottom);
                    mDivider.draw(c);
                }
            }
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            if (mOrientation == LinearLayoutManager.HORIZONTAL) {
                outRect.set(0, 0, mSize, 0);
            } else {
                outRect.set(0, 0, 0, mSize);
            }
        }
    }
}
