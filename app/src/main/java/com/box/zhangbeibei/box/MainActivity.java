package com.box.zhangbeibei.box;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Message;
import android.support.animation.SpringAnimation;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;

import com.box.myview.MyBroadCastView.BroadCastView;
import com.box.myview.MyTopSnackBar.Prompt;
import com.box.myview.MyTopSnackBar.TSnackbar;
/**
 * 1.switchbutton
 * 2.mysnackbar
 * 3.弹簧动画
 * 4.自定义广播view
 */
public class MainActivity extends AppCompatActivity {
    private TSnackbar snackBar;
    private ViewGroup viewGroup;
    private BroadCastView broadCastView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        viewGroup = (ViewGroup) findViewById(android.R.id.content).getRootView();
        findViewById(R.id.id_activity_text_my_view_btn_top_snackbar).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //显示顶部SnackBar
                snackBar = TSnackbar.make(viewGroup, "顶部信息", TSnackbar.LENGTH_SHORT, TSnackbar.APPEAR_FROM_TOP_TO_DOWN);
                snackBar.setAction("取消", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                });
                snackBar.setPromptThemBackground(Prompt.WARNING);
                snackBar.show();
            }
        });
        findViewById(R.id.id_activity_text_my_view_btn_bottom_snackbar).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //显示底部SnackBar
                snackBar = TSnackbar.make(viewGroup, "底部信息", TSnackbar.LENGTH_SHORT, TSnackbar.APPEAR_FROM_BOTTOM_TO_TOP);
                snackBar.setAction("取消", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                });
                snackBar.setPromptThemBackground(Prompt.SUCCESS);
                snackBar.show();
            }
        });


//弹簧动画测试
        stiffness = (SeekBar) findViewById(R.id.id_stiffness);
        damping = (SeekBar) findViewById(R.id.id_damping);
        velocityTracker = VelocityTracker.obtain();
        final View box = findViewById(R.id.id_box);
        findViewById(R.id.id_root).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        downX = event.getX();
                        downY = event.getY();
                        velocityTracker.addMovement(event);
                        return true;
                    case MotionEvent.ACTION_MOVE:
                        box.setTranslationX(event.getX() - downX);
                        box.setTranslationY(event.getY() - downY);
                        velocityTracker.addMovement(event);
                        return true;
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL:
                        velocityTracker.computeCurrentVelocity(1000);
                        if (box.getTranslationX() != 0) {
                            SpringAnimation animX = new SpringAnimation(box, SpringAnimation.TRANSLATION_X, 0);
                            animX.getSpring().setStiffness(getStiffness());
                            animX.getSpring().setDampingRatio(getDamping());
                            animX.setStartVelocity(velocityTracker.getXVelocity());
                            animX.start();
                        }
                        if (box.getTranslationY() != 0) {
                            SpringAnimation animY = new SpringAnimation(box, SpringAnimation.TRANSLATION_Y, 0);
                            animY.getSpring().setStiffness(getStiffness());
                            animY.getSpring().setDampingRatio(getDamping());
                            animY.setStartVelocity(velocityTracker.getYVelocity());
                            animY.start();
                        }
                        velocityTracker.clear();
                        return true;
                }
                return false;
            }
        });

        broadCastView = (BroadCastView) findViewById(R.id.id_activity_test_my_view_broadcastview);
//        broadCastView.setMode(BroadCastView.MODE_NET);
//        testSelfMode();
        broadCastView.setMode(BroadCastView.MODE_SELF);

        UpdateBroadcastReceiver receiver = new UpdateBroadcastReceiver(broadCastView.getHandler());
        IntentFilter filter = new IntentFilter();
        filter.addAction(UPDATE_APP_ACTION);
        filter.addAction(UPDATE_APP_ACTION_GONE);

        broadCastView.setReceiver(receiver, filter);

        findViewById(R.id.id_activity_test_my_view_send_update_broadcast).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UPDATE_APP_ACTION);
                sendBroadcast(intent);
            }
        });
        findViewById(R.id.id_activity_test_my_view_send_update_broadcast_gone).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UPDATE_APP_ACTION_GONE);
                sendBroadcast(intent);
            }
        });

    }

    private static String UPDATE_APP_ACTION = "com.box.my.APP_NEW_VERSION";
    private static String UPDATE_APP_ACTION_GONE = "com.box.my.APP_NEW_VERSION_GONE";

    class UpdateBroadcastReceiver extends BroadcastReceiver {
        Handler mHandler;

        public UpdateBroadcastReceiver(Handler handler) {
            this.mHandler = handler;
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            if (UPDATE_APP_ACTION.equals(intent.getAction())) {
                Message msg = Message.obtain();
                msg.what = BroadCastView.STATE_SHOW;
                msg.obj = "有新版本，点击查看";
                mHandler.sendMessage(msg);
            } else if (UPDATE_APP_ACTION_GONE.equals(intent.getAction())) {
                Message msg = Message.obtain();
                msg.what = BroadCastView.STATE_HIDE;
                mHandler.sendMessage(msg);
            }
        }
    }

    private void testSelfMode() {

    }

    private float downX, downY;
    private SeekBar damping, stiffness;
    private VelocityTracker velocityTracker;

    //最大值1.0
    private float getStiffness() {
        return Math.max(stiffness.getProgress() / 100, 1f);
    }

    private float getDamping() {
        return damping.getProgress() / 100f;
    }
}
