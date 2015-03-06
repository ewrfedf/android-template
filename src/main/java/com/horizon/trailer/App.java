package com.horizon.trailer;

import android.app.Application;
import android.widget.Toast;

import com.horizon.trailer.util.NetBroadcastReceiver;
import com.horizon.trailer.util.NetTool;


/**
 * @author Zheng
 *
 */
public class App extends Application {
    /**
     * 用来判断 新增业务员邀请 false 还是邀请用户注册 true
     * 微信分享 回调 需要重新判断 WXEntryActivity内判断无效
     */

    public boolean isNetOk = false;

    private NetBroadcastReceiver mNetworkStateReceiver;

    @Override
    public void onCreate() {
        super.onCreate();
        //添加程序异常捕捉
//	   CrashHandler catchHandler = CrashHandler.getInstance();
//       catchHandler.init(getApplicationContext());

        mNetworkStateReceiver = new NetBroadcastReceiver();
        if (checkNet()) {
        }
    }

    protected boolean checkNet() {
        if (NetTool.isNetworkAvailable(this.getBaseContext())) {
            isNetOk = true;
            return true;
        } else {
            isNetOk = false;
            Toast.makeText(this.getBaseContext(), "网络连接异常", Toast.LENGTH_SHORT)
                    .show();
            return false;
        }
    }

    public NetBroadcastReceiver getmNetworkStateReceiver() {
        return mNetworkStateReceiver;
    }

    public void setmNetworkStateReceiver(
            NetBroadcastReceiver mNetworkStateReceiver) {
        this.mNetworkStateReceiver = mNetworkStateReceiver;
    }
}
