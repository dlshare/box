package com.box.myview.MyBroadCastView;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.box.myview.R;

/**
 * Created by zhangbeibei on 17/4/7.
 */

public class BroadCastView extends RelativeLayout implements View.OnClickListener {

    private boolean mAttached = false;
    public static final int MODE_NET = 0;
    public static final int MODE_SELF = 1;
    private int mMode = 0;

    private RelativeLayout mLay;
    private ImageView mImgLeft;
    private ImageView mImgRight;
    private TextView mTvDesc;

    private Drawable mDrawableLeft;
    private Drawable mDrawableRight;
    private String mDescStr;

    public BroadCastView(Context context) {
        super(context);
        init(context);
    }

    public BroadCastView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BroadCastView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        final TypedArray a = context.obtainStyledAttributes(
                attrs, R.styleable.BroadCastView);
        try {
            mDescStr = a.getString(R.styleable.BroadCastView_broadcastview_desc);
            mDrawableLeft = a.getDrawable(R.styleable.BroadCastView_broadcastview_left_icon);
            mDrawableRight = a.getDrawable(R.styleable.BroadCastView_broadcastview_left_icon);
        } finally {
            a.recycle();
        }
        init(context);
        setVisible(false);
    }

    private void setImageDrawable(ImageView imageView, Drawable drawable) {
        imageView.setImageDrawable(drawable);
    }

    private void init(Context context) {
        View.inflate(context, R.layout.view_broadcast_layout, this);
        mImgLeft = (ImageView) findViewById(R.id.id_view_broadcast_layout_icon_left);
        mImgRight = (ImageView) findViewById(R.id.id_view_broadcast_layout_icon_right);
        mTvDesc = (TextView) findViewById(R.id.id_view_broadcast_layout_tv_info);
        mLay = (RelativeLayout) findViewById(R.id.id_view_broadcast_layout);
        mLay.setOnClickListener(this);
        if (TextUtils.isEmpty(mDescStr)) mDescStr = "默认提示信息";
        mTvDesc.setText(mDescStr);
        if (mDrawableLeft != null) {
            setImageDrawable(mImgLeft, mDrawableLeft);
        }
        if (mDrawableRight != null) {
            setImageDrawable(mImgRight, mDrawableRight);
        }
    }

    //设置本身是否可见
    private void setVisible(boolean visible) {
        if (visible) {
            setVisibility(View.VISIBLE);
        } else {
            setVisibility(View.GONE);
        }
    }

    private void setDesc(String content) {
        this.mDescStr = content;
        this.mTvDesc.setText(mDescStr);
    }

    public Handler getHandler() {
        return netHandler;
    }

    public static final int STATE_HIDE = 0;
    public static final int STATE_SHOW = 1;
    private Handler netHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (mMode == MODE_NET) {
                NetWorkState netWorkState = (NetWorkState) msg.obj;
                if (netWorkState.isNetWorkOK()) {
                    setVisible(false);
                    switch (netWorkState.getNetWorkType()) {
                        case NetWorkState.TYPE_DATA:
//                        ToastUtil.showToast("移动数据");
                            break;
                        case NetWorkState.TYPE_WIFI:
                        case NetWorkState.TYPE_WIFI_DATA:
//                        ToastUtil.showToast("wifi连接");
                            break;
                    }
                } else {
                    setVisible(true);
                    setDesc("网络已断开，请检测网络设置");
//                ToastUtil.showToast("无网络连接");
                }
            } else {
                switch (msg.what) {
                    case STATE_HIDE:
                        setVisible(false);
                        if (msg.obj != null)
                            setDesc(msg.obj.toString());
                        break;
                    case STATE_SHOW:
                        setVisible(true);
                        setDesc(msg.obj.toString());
                        break;
                }
            }
        }
    };
    //网络状态监听
    private BroadcastReceiver mBroadcastReceiver;
    IntentFilter mFfilter;

    private void registerReceiver() {
        //在MODE_NET下broadcastReceiver及filter为空
        if (mMode == MODE_NET) {
            if (mBroadcastReceiver == null) {
                mBroadcastReceiver = new NetWorkStateReceiver(netHandler);
            }
            if (mFfilter == null) {
                mFfilter = new IntentFilter();
                mFfilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
            }
        }
        //android.net.conn.CONNECTIVITY_CHANGE
        getContext().registerReceiver(mBroadcastReceiver, mFfilter);
    }

    private void unregisterReceiver() {
        getContext().unregisterReceiver(mBroadcastReceiver);
    }


    //用户设置展示模式
    public void setMode(int mode) {
        this.mMode = mode;
    }

    //在MODE_SELF下需要设置接收的广播
    public void setReceiver(BroadcastReceiver receiver, IntentFilter filter) {
        this.mBroadcastReceiver = receiver;
        this.mFfilter = filter;
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (!mAttached) {
            mAttached = true;
            registerReceiver();
            // TODO: 17/2/8 初始化电池电量和描述信息,no need
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();

        if (mAttached) {
            unregisterReceiver();
            mAttached = false;
        }
    }

    //点击事件
    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.id_view_broadcast_layout) {
            setVisible(false);
            if (mMode == MODE_NET) {

            } else {
                Toast.makeText(getContext(), "已查看", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
