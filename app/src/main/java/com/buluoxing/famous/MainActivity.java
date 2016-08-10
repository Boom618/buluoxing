package com.buluoxing.famous;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTabHost;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import com.buluoxing.famous.index.IndexFragment;
import com.buluoxing.famous.index.KolFragment;
import com.buluoxing.famous.index.MoneyFragment;
import com.buluoxing.famous.index.UserFragment;
import com.buluoxing.famous.mission.MissionTypeSheetActivity;
import com.umeng.analytics.MobclickAgent;
import com.umeng.message.PushAgent;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.util.ScreenUtils;

public class MainActivity extends FragmentActivity {

    //定义FragmentTabHost对象
    public FragmentTabHost mTabHost;

    //定义一个布局
    private LayoutInflater layoutInflater;

    //定义数组来存放Fragment界面
    private Class fragmentArray[] = {IndexFragment.class, MoneyFragment.class, MoneyFragment.class, KolFragment.class, UserFragment.class};

    //定义数组来存放按钮图片
    private int mImageViewArray[] = {R.drawable.bottom_index_icon, R.drawable.bottom_rep_icon, R.drawable.bottom_rep_icon, R.drawable.bottom_cup_icon, R.drawable.bottom_user_icon};

    //Tab选项卡的文字
    private String mTextviewArray[] = {"首页", "赚钱", "  ", "网红", "我的"};

    //private MyApplication mainApp;

    private UMShareListener umShareListener = new UMShareListener() {
        @Override
        public void onResult(SHARE_MEDIA platform) {
            com.umeng.socialize.utils.Log.d("plat", "platform" + platform);
            if (platform.name().equals("WEIXIN_FAVORITE")) {
                Toast.makeText(MainActivity.this, platform + " 收藏成功啦", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(MainActivity.this, platform + " 分享成功啦", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void onError(SHARE_MEDIA platform, Throwable t) {
            Toast.makeText(MainActivity.this, platform + " 分享失败啦", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onCancel(SHARE_MEDIA platform) {
            Toast.makeText(MainActivity.this, platform + " 分享取消了", Toast.LENGTH_SHORT).show();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        MyApplication application = (MyApplication) getApplication();
//        mainApp = (MyApplication) getApplication();
//        mainApp.mainActivity = this;
        // 获取屏幕 尺寸
        int screenHeight = ScreenUtils.getScreenHeight(MainActivity.this);
        int screenWidth = ScreenUtils.getScreenWidth(MainActivity.this);
        Log.d("Storm", "onCreate: " + screenHeight + " : " + screenWidth );

        // 获取 应用的最高内存大小
        int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
        Log.d("TAG", "Max memory is " + maxMemory + "KB");


        initView();

        findViewById(R.id.pub_mission).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkNetworkAvailable(getBaseContext())) {


                    Intent intent = new Intent(MainActivity.this, MissionTypeSheetActivity.class);
                    startActivity(intent);
                }else{
                    Toast.makeText(MainActivity.this, "请检查网络是否连接", Toast.LENGTH_SHORT).show();
                }
//				UMImage image = new UMImage(MainActivity.this, "http://www.umeng.com/images/pic/social/integrated_3.png");
//
//				new ShareAction(MainActivity.this).setPlatform(SHARE_MEDIA.WEIXIN).setCallback(umShareListener)
//					.withMedia(image)
//						//.withMedia(new UMEmoji(ShareActivity.this,"http://img.newyx.net/news_img/201306/20/1371714170_1812223777.gif"))
//					.withText("hello umeng")
//						//.withTargetUrl(url)
//					.share();
            }
        });


        PushAgent.getInstance(getApplicationContext()).onAppStart();


        WindowManager wm = getWindowManager();
        Point size = new Point();
        wm.getDefaultDisplay().getSize(size);
        ((MyApplication) getApplication()).size = size;
    }

    //判断是否手机是否联网
    public static boolean checkNetworkAvailable(Context context) {
        ConnectivityManager connectivity = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity == null) {
            return false;
        } else {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null) {
                for (int i = 0; i < info.length; i++) {
                    if (info[i].getState() == NetworkInfo.State.CONNECTED) {
                        NetworkInfo netWorkInfo = info[i];
                        if (netWorkInfo.getType() ==
                                ConnectivityManager.TYPE_WIFI) {
                            return true;
                        } else if (netWorkInfo.getType() ==
                                ConnectivityManager.TYPE_MOBILE) {
                            return true;
                        }
                    }
                }
            }
        }

        return false;

    }

    /**
     * 初始化组件
     */
    private void initView() {
        //实例化布局对象
        layoutInflater = LayoutInflater.from(this);

        //实例化TabHost对象，得到TabHost
        mTabHost = (FragmentTabHost) findViewById(android.R.id.tabhost);
        mTabHost.setup(this, getSupportFragmentManager(), R.id.realtabcontent);


        mTabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
            @Override
            public void onTabChanged(String tabId) {


                Log.i("fuck", mTabHost.getTabWidget().getChildCount() + "");
                for (int i = 0; i < mTabHost.getTabWidget().getChildCount(); i++) {

                    ((TextView) mTabHost.getTabWidget().getChildAt(i).findViewById(R.id.bottom_textview)).setTextColor(Color.parseColor("#ababab"));
                }

                ((TextView) mTabHost.getCurrentTabView().findViewById(R.id.bottom_textview)).setTextColor(Color.parseColor("#4cdae4"));


            }
        });


        //得到fragment的个数
        int count = fragmentArray.length;

        for (int i = 0; i < count; i++) {
            //为每一个Tab按钮设置图标、文字和内容
            TabHost.TabSpec tabSpec = mTabHost.newTabSpec(mTextviewArray[i]).setIndicator(getTabItemView(i));

            //将Tab按钮添加进Tab选项卡中
            mTabHost.addTab(tabSpec, fragmentArray[i], null);
            //设置Tab按钮的背景
            //mTabHost.getTabWidget().getChildAt(i).setBackgroundResource(R.drawable.bottom_bg);
        }
    }

    /**
     * 给Tab按钮设置图标和文字
     */
    private View getTabItemView(int index) {
        View view = layoutInflater.inflate(R.layout.tab_item_view, null);

        ImageView imageView = (ImageView) view.findViewById(R.id.imageview);
        imageView.setImageResource(mImageViewArray[index]);

        TextView textView = (TextView) view.findViewById(R.id.bottom_textview);
        textView.setText(mTextviewArray[index]);
        return view;
    }

//    public void showCover() {
//        findViewById(R.id.cover).setVisibility(View.VISIBLE);
//    }
//
//    public void hideCover() {
//        findViewById(R.id.cover).setVisibility(View.GONE);
//    }


    public boolean onKeyDown(int kCode, KeyEvent kEvent) {
        switch (kCode) {
            case KeyEvent.KEYCODE_BACK:
//                Intent intent = new Intent(this, ConfirmActivity.class);
//                intent.putExtra("message", "退出部落星吗？");
//                startActivityForResult(intent, 101);

                // 退出 APP 用 DialogFragment （解除绑定微信 ConfirmActivity 没改 ）
                OutAppDialogFragment dialog = new OutAppDialogFragment();
                dialog.show(getFragmentManager(),"退出");
                return true;

        }
        return super.onKeyDown(kCode, kEvent);
    }

    //  采用 DialogFragment 替代 Activity
//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//
//        if (requestCode == 101) {
//            if (resultCode == 1) {
//                // 确定退出应用
//                L.d("退出部落星应用");
////                mainApp.logout();
//                finish();
//            }
//        }
//    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        if (mainApp != null){
//            if (mainApp.mainActivity != null){
//                mainApp.mainActivity = null;
//            }
//            mainApp = null;
//            System.gc();
//        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();

        MobclickAgent.onPause(this);
    }
}
