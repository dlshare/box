package com.box.myview.MyBroadCastView;

/**
 * Created by zhangbeibei on 17/3/20.
 * email:hearstzhang@gmail.com
 * 功能描述：
 */

public class NetWorkState {
    private boolean isNetWorkOK = false;
    private int netWorkType = -1;//0wifi;1数据
    public static final int TYPE_WIFI_DATA = 0;
    public static final int TYPE_WIFI = 1;
    public static final int TYPE_DATA = 2;
    public static final int TYPE_NO = -1;

    public boolean isNetWorkOK() {
        return isNetWorkOK;
    }

    public void setNetWorkOK(boolean netWorkOK) {
        isNetWorkOK = netWorkOK;
    }

    public int getNetWorkType() {
        return netWorkType;
    }

    public void setNetWorkType(int netWorkType) {
        this.netWorkType = netWorkType;
    }
}
