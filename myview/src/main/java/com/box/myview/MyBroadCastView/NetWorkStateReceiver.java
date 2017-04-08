package com.box.myview.MyBroadCastView;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.Message;

/**
 * Created by zhangbeibei on 17/3/20.
 * email:hearstzhang@gmail.com
 * 功能描述：
 */

public class NetWorkStateReceiver extends BroadcastReceiver {
    private Handler handler;

    public NetWorkStateReceiver() {
    }

    public NetWorkStateReceiver(Handler handler) {
        this.handler = handler;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (ConnectivityManager.CONNECTIVITY_ACTION.equals(intent.getAction())) {

            NetWorkState networkState = new NetWorkState();
            System.out.println("网络状态发生变化");
            //检测API是不是小于23，因为到了API23之后getNetworkInfo(int networkType)方法被弃用
            if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.LOLLIPOP) {

                //获得ConnectivityManager对象
                ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

                //获取ConnectivityManager对象对应的NetworkInfo对象
                //获取WIFI连接的信息
                NetworkInfo wifiNetworkInfo = connMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
                //获取移动数据连接的信息
                NetworkInfo dataNetworkInfo = connMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
                if (wifiNetworkInfo.isConnected() && dataNetworkInfo.isConnected()) {
                    networkState.setNetWorkOK(true);
                    networkState.setNetWorkType(NetWorkState.TYPE_WIFI_DATA);
//                Toast.makeText(context, "WIFI已连接,移动数据已连接", Toast.LENGTH_SHORT).show();
                } else if (wifiNetworkInfo.isConnected() && !dataNetworkInfo.isConnected()) {
                    networkState.setNetWorkOK(true);
                    networkState.setNetWorkType(NetWorkState.TYPE_WIFI);
//                Toast.makeText(context, "WIFI已连接,移动数据已断开", Toast.LENGTH_SHORT).show();
                } else if (!wifiNetworkInfo.isConnected() && dataNetworkInfo.isConnected()) {
                    networkState.setNetWorkOK(true);
                    networkState.setNetWorkType(NetWorkState.TYPE_DATA);
//                Toast.makeText(context, "WIFI已断开,移动数据已连接", Toast.LENGTH_SHORT).show();
                } else {
                    networkState.setNetWorkOK(false);
                    networkState.setNetWorkType(NetWorkState.TYPE_NO);
//                Toast.makeText(context, "WIFI已断开,移动数据已断开", Toast.LENGTH_SHORT).show();
                }

//API大于23时使用下面的方式进行网络监听
            } else {

                System.out.println("API level 大于23");
                //获得ConnectivityManager对象
                ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

                //获取所有网络连接的信息
                Network[] networks = connMgr.getAllNetworks();
                //用于存放网络连接信息
                StringBuilder sb = new StringBuilder();
                /**
                 * type:MOBILE; connect is true
                 *
                 */
                //通过循环将网络信息逐个取出来
                for (int i = 0; i < networks.length; i++) {
                    //获取ConnectivityManager对象对应的NetworkInfo对象
                    NetworkInfo networkInfo = connMgr.getNetworkInfo(networks[i]);
                    if (networkInfo.isConnected()) {
                        if (networkInfo.getTypeName().equals("WIFI")) {
                            networkState.setNetWorkOK(true);
                            networkState.setNetWorkType(NetWorkState.TYPE_WIFI);
                            break;
                        } else {
                            networkState.setNetWorkOK(true);
                            networkState.setNetWorkType(NetWorkState.TYPE_DATA);
                        }
                    }
                }
            }
            Message msg = new Message();
            msg.obj = networkState;
            handler.sendMessage(msg);
        }
    }
}
