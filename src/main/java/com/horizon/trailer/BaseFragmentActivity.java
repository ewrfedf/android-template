package com.horizon.trailer;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.horizon.trailer.util.AsyncHttpCilentUtil;
import com.horizon.trailer.util.NetTool;
import com.horizon.trailer.view.WaitingDialog;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class BaseFragmentActivity extends FragmentActivity {

    protected App app;
    private static boolean isActive;
    /**
     * 新版app 下载地址
     */
    protected String new_apk_url;
    /**
     * 新版app 更新内容
     */
    protected String content_msg;
    Button rightBt;
    WaitingDialog pd;
    Handler mHandler = new Handler() {
        public void dispatchMessage(android.os.Message msg) {
            if (pd != null) {
                pd.cancel();
            }
        };
    };

    private Toast toast;

    public void showToast(String msg) {
        if (toast == null) {
            toast = Toast.makeText(this.getApplicationContext(), msg,
                    Toast.LENGTH_SHORT);
        } else {
            toast.setText(msg);
        }
        toast.show();
    }

    public void checkSensitiveWord(EditText mEditText) {
        mEditText.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    check(((EditText) v).getText().toString().trim());
                }
            }
        });
    }

    protected void check(String word) {
    }

    // protected TextView title;
    protected Button back;

    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        app = (App) (this.getApplicationContext());
        getWindow().getDecorView().setSystemUiVisibility(View.GONE);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            //透明状态栏
           // getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            //透明导航栏
           // getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (!isAppOnForeground()) { // app 进入后台
            isActive = false;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!isActive) { // app 从后台唤醒，进入前台
            isActive = true;
            checkNet();
        }
    }
    /**
     * 检测 版本更新
     *
     * 需要更新 则显示对话框
     *
     * 不需要 则 进入下一步 操作
     *
     */
    protected void checkNewVersion() {
        AsyncHttpClient client = AsyncHttpCilentUtil
                .getInstance(getApplicationContext());
        RequestParams p = new RequestParams();
        p.put("appversion", getVersionName());
        p.put("system", "android");
        client.post(getString(R.string.base_url) + "/user/updateapp", p,
                new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(String content) {
                        super.onSuccess(content);
                        mHandler.sendEmptyMessage(0);
                        try {
                            JSONObject json = new JSONObject(content);
                            if ("true".equals(json.getString("ok"))) {// 不是最新版本
                                json = new JSONObject(json.getString("res"));
                                if (!"".equals(json.getString("apk_url"))
                                        && !"".equals(json
                                        .getString("app_version"))
                                        && !getVersionName().equals(
                                        json.getString("app_version"))) {
                                    new_apk_url = json.getString("apk_url");
                                    content_msg = json.getString("content");
                                    content_msg = content_msg.replace("\\n","\n");
                                    if ("1".equals(json.getString("flag"))) {
                                        // flag 1 表示 强制更新
                                        dialog(true);
                                    } else {
                                        dialog(false);
                                    }
                                }
                            }
                        } catch (JSONException e) {
                            showToast(e.toString());

                        }
                    }

                    @Override
                    public void onFailure(Throwable error, String content) {
                        super.onFailure(error, content);
                    }
                });
    }
    /**
     * @return 获取app 版本号
     */
    private String getVersionName() {
        PackageManager packageManager = getPackageManager();
        PackageInfo packInfo = null;
        try {
            packInfo = packageManager.getPackageInfo(getPackageName(), 0);
        } catch (NameNotFoundException e) {
            return "";
        }
        if (packInfo != null) {
            return packInfo.versionName + "";
        } else {
            return "";
        }
    }

    private String getCurrentActivityName(Context context) {
        ActivityManager am = (ActivityManager) context
                .getSystemService(Activity.ACTIVITY_SERVICE);

        // get the info from the currently running task
        List<ActivityManager.RunningTaskInfo> taskInfo = am.getRunningTasks(1);

        ComponentName componentInfo = taskInfo.get(0).topActivity;
        String name = componentInfo.getClassName();
        System.out.println(name);
        return componentInfo.getClassName();
    }

    /**
     * @param isForce
     *            是否强制更新 强制则 下次再说按钮隐藏 非强制 则显示 下次再说按钮
     */
    protected void dialog(boolean isForce) {
        LayoutInflater inflater = LayoutInflater.from(this);
        RelativeLayout layout = (RelativeLayout) inflater.inflate(
                R.layout.alert_dialog_update, null);

        final Dialog dialog = new AlertDialog.Builder(this).create();
        dialog.setCancelable(false);
        dialog.show();
        dialog.getWindow().setContentView(layout);

        TextView msg = (TextView) layout.findViewById(R.id.content_msg);
        msg.setText(content_msg);

        Button btnOK = (Button) layout.findViewById(R.id.dialog_ok);
        if (isForce) {// 如果强制更新 则 隐藏 下次再说按钮
            btnOK.setVisibility(View.GONE);
        }

        btnOK.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        Button btnCancel = (Button) layout.findViewById(R.id.dialog_cancel);
        btnCancel.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {// 点击现在更新按钮 执行浏览器下载
                Intent intent = new Intent();
                intent.setAction("android.intent.action.VIEW");
                Uri content_url = Uri.parse(new_apk_url);
                intent.setData(content_url);
                startActivity(intent);
            }
        });
    }

    /**
     * 检测程序是否进入前台
     *
     * @return true 进入前台 false 进入后台
     */
    private boolean isAppOnForeground() {

        ActivityManager activityManager = (ActivityManager) getApplicationContext()
                .getSystemService(Context.ACTIVITY_SERVICE);
        String packageName = getApplicationContext().getPackageName();
        List<RunningAppProcessInfo> appProcesses = activityManager
                .getRunningAppProcesses();
        if (appProcesses == null)
            return false;
        for (RunningAppProcessInfo appProcess : appProcesses) {
            if (appProcess.processName.equals(packageName)
                    && appProcess.importance == RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                return true;
            }
        }
        return false;
    }

    protected boolean checkNet() {
        if (NetTool.isNetworkAvailable(getBaseContext())) {
            if (app != null) {
                app.isNetOk = true;
            }
            return true;
        } else {
            if (app != null) {
                app.isNetOk = false;
            }
            showToast("网络连接异常");
            return false;
        }
    }

    private void registerNetCheck() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        app.registerReceiver(app.getmNetworkStateReceiver(), filter);
    }

}
