package com.horizon.trailer;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.horizon.trailer.adapter.GuidePageAdapter;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.OnClick;


public class GuideActivity extends BaseFragmentActivity {

    @OnClick(R.id.start)void start(){
        preferences = getSharedPreferences("phone", Context.MODE_PRIVATE);
        editor = preferences.edit();
        // 将登录标志位设置为false，下次登录时不在显示首次登录界面
        editor.putBoolean("firststart", false);
        editor.commit();
        startActivity(new Intent(GuideActivity.this,  MainActivity.class));
        this.finish();
    }
    private ViewPager viewPager;
    AlphaAnimation aa;
    Button start;
    /**
     * 用户信息存储
     */
    private SharedPreferences sp;

    /**
     * 判断第一次启动 存储
     */
    private SharedPreferences preferences;

    private Editor editor;
    /**
     * 引导页三张图片资源
     */
    private int[] pics = { R.drawable.guide_1, R.drawable.guide_2,
            R.drawable.guide_3 };

    /**
     * 要是显示的paper的view 如果启动页只加 一个view 如果引导页 则添加三个view
     */
    private ArrayList<View> views;

    /**
     * 新版app 下载地址
     */
    protected String new_apk_url;
    /**
     * 新版app 更新内容
     */
    protected String content_msg;

    private boolean isAnimationFinished = false;
    private boolean isCheckNewVersionFinished = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = View.inflate(GuideActivity.this, R.layout.activity_guide,
                null);
        setContentView(view);
        ButterKnife.inject(this);
        sp = this.getSharedPreferences("userInfo",
                getApplicationContext().MODE_PRIVATE);
        start = (Button) findViewById(R.id.start);
        start.setVisibility(View.INVISIBLE);
        LinearLayout.LayoutParams mParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        views = new ArrayList<View>();
        preferences = getSharedPreferences("phone", Context.MODE_PRIVATE);

        /**
         * 判断是否第一次 启动
         */
        if (preferences.getBoolean("firststart", true)) {
            // 引导页 则添加三个view
            for (int i = 0; i < pics.length; i++) {
                ImageView iv = new ImageView(this);
                iv.setLayoutParams(mParams);
                iv.setImageResource(pics[i]);
                views.add(iv);
            }
        } else {
            // 启动页 添加一个view
            ImageView iv = new ImageView(this);
            iv.setLayoutParams(mParams);
            iv.setImageResource(R.drawable.splash);
            iv.setScaleType(ScaleType.FIT_XY);
            views.add(iv);
        }

        viewPager = (ViewPager) findViewById(R.id.guidePages);
        viewPager.setAdapter(new GuidePageAdapter(views));
        viewPager.setOnPageChangeListener(new OnPageChangeListener() {
            @Override
            public void onPageSelected(int arg0) {
                if (arg0 == 2) {// 如果进入到 第三页 则显示 启动app 按钮
                    start.setVisibility(View.VISIBLE);
                } else {// 其他页面 则隐藏 启动app 按钮
                    start.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {

            }

            @Override
            public void onPageScrollStateChanged(int arg0) {

            }
        });

        // 判断是不是首次登录，
        if (!preferences.getBoolean("firststart", true)) {// 不是第一次启动

            // 执行检测
            // checkNewVersion();
            // 没有任何变化的 动画 （静止动画）
            aa = new AlphaAnimation(1f, 1f);
            aa.setDuration(1500);
            aa.setAnimationListener(new AnimationListener() {
                @Override
                public void onAnimationStart(Animation arg0) {
                    checkNewVersion();
                }

                @Override
                public void onAnimationRepeat(Animation arg0) {
                }

                @Override
                public void onAnimationEnd(Animation arg0) {
                    Intent intent = new Intent(GuideActivity.this,
                            MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            });
            // 未启动 停留动画
            view.startAnimation(aa);
        }

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
            public void onClick(View v) {// 点击下次再说按钮 执行 下一步操作（自动登录 或则 进入登录页面）
                dialog.dismiss();
                if (sp.getBoolean("auto_login", false)) {
                    if (checkNet()) {
//                        login();
                    } else {
                        Intent intent = new Intent(GuideActivity.this,
                                 MainActivity.class);
                        startActivity(intent);
                        finish();
                    }
                } else {
                    Intent intent = new Intent(GuideActivity.this,
                             MainActivity.class);
                    startActivity(intent);
                    finish();
                }
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



    /**
     * 启动
     */
    private void startActivity() {
        if (isAnimationFinished == true && isCheckNewVersionFinished == true) {
            if (sp.getBoolean("auto_login", false)) {// 判断
                // 是否自动登录
                if (checkNet()) {// 自动登录 每次网络请求都要检测网络
//                    login();
                } else {
                    Intent intent = new Intent(GuideActivity.this,
                             MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            } else {
                Intent intent = new Intent(GuideActivity.this,
                         MainActivity.class);
                startActivity(intent);
                finish();
            }
        }
    }
}

