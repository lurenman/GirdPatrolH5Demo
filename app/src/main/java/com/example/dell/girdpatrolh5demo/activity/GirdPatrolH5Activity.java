package com.example.dell.girdpatrolh5demo.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.dell.girdpatrolh5demo.GlobalApplication;
import com.example.dell.girdpatrolh5demo.R;
import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.util.Date;

import pub.devrel.easypermissions.EasyPermissions;

/**
 * 创建日期：2018/5/31
 * 作者:baiyang
 * 网格巡查h5
 * 本例子用户数据是模拟数据
 */
public class GirdPatrolH5Activity extends AppCompatActivity {
    private static final String TAG = "GirdPatrolH5Activity";
    private WebView mWebView;
    private ProgressBar progressBar;
    private String mUrl;
    private Gson mGson = new Gson();
    private SimpleDateFormat mFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss ");
    private boolean isFirst = false;//这个判断是不是actvity初次建立

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        isFirst = true;
        setContentView(R.layout.activity_gridpatrolh5_web);
        setTitle("网格巡查");
        mWebView = (WebView) findViewById(R.id.webVeiw);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        initSetting();
        // mUrl = "file:///android_asset/index.html";
        mUrl = "http://172.17.229.229:80/gridPatrol/auth/authUser.html?gridPatrol=true&yhUserId=bb4a9da35f820269015f8f46f17a0eff&sourcePlatform=1";
        mWebView.loadUrl(mUrl);
        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                super.onReceivedError(view, request, error);
                view.loadData("<html>    \n" +
                        "<head>    \n" +
                        "<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\" />    \n" +
                        "<title>网页暂时无法访问，请稍候再试</title>    \n" +
                        "<style>    \n" +
                        "body{font-family:Helvetica,Arial,sans-serif;margin:0;background-color:#CCC;background:-webkit-linear-gradient(#CCC,#AAA);background-attachment:fixed;}\n" +
                        "#cell{padding:10px;}\n" +
                        "#box{background-color:white;color:black;font-size:10pt;line-height:18px;margin:right;\n" +
                        "max-width:800px;border-radius:5px;-webkit-box-shadow:2px 5px 12px#555;padding:20px;}    \n" +
                        "ul{margin:0;padding-bottom:0;}    \n" +
                        "li{padding-top:2px;list-style-type:none;}    \n" +
                        "h1{font-size:15pt;line-height:30px;margin:0;}    \n" +
                        ".right{color:#B5B5B5;text-align: right;}    \n" +
                        " a{color:#424343;}    \n" +
                        "</style>    \n" +
                        "</head>    \n" +
                        "<body id=\"t\">    \n" +
                        "<div id=\"cell\">    \n" +
                        "<div id=\"box\">    \n" +
                        "<h1>网页暂无响应，请稍候再试</h1>    \n" +
                        "<p>    \n" +
                        "<ul>    \n" +
                        "<li><a href=\"" + mUrl + "\"> 重试</a></li>      \n" +
                        "</ul>    \n" +
                        "</p>      \n" +
                        "</div>    \n" +
                        "</div>    \n" +
                        "</body>    \n" +
                        "</html>", "text/html; charset=UTF-8", null);
            }
        });
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void initSetting() {
        WebSettings webSetting = mWebView.getSettings();
        webSetting.setAppCacheEnabled(false);
        webSetting.setJavaScriptEnabled(true);
        mWebView.addJavascriptInterface(new JsInterface(), "jsInterface");
        webSetting.setJavaScriptEnabled(true);
        webSetting.setDomStorageEnabled(true);
        mWebView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                if (newProgress == 100) {
                    progressBar.setVisibility(View.GONE);
                } else {
                    if (View.GONE == progressBar.getVisibility()) {
                        progressBar.setVisibility(View.VISIBLE);
                    }
                    progressBar.setProgress(newProgress);
                }
                super.onProgressChanged(view, newProgress);
            }
        });
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            WebView.setWebContentsDebuggingEnabled(true);
        }
    }

    @Override
    public void onBackPressed() {
        if (mWebView != null && mWebView.canGoBack()) {
            mWebView.goBack();
        } else {
            finish();
        }
    }

    /**
     * h5调用的java
     */

    public class JsInterface {
        /**
         * 检测手机能不能定位
         * 对于核实权限可以用自己的库
         *
         * @return
         */
        @JavascriptInterface
        public boolean checkApp() {
            if (!queryPermission()) {
                return false;
            }
            if (!isLocationEnabled()) {
                //"检测到系统定位服务未开启，请先开启"
                Toast.makeText(GirdPatrolH5Activity.this, "检测到系统定位服务未开启，请先开启", Toast.LENGTH_SHORT).show();
                return false;
            }
            return true;
        }

        /**
         * 获取用户信息 json例如
         * {"id":5,"mobile":"11111111111","name":"by@hzsg"}
         *
         * @return
         */
        @JavascriptInterface
        public String loadUserInfo() {
            String userName = "by@hzsg";
            Long orgId = Long.valueOf(5);
            String mobile = "11111111111";
            User user = new User();
            user.setName(userName);
            user.setId(orgId);
            user.setMobile(mobile);
            String jsonString = mGson.toJson(user);
            Log.e(TAG, "loadUserInfo: " + jsonString);
            return jsonString;
        }

        /**
         * 获取定位信息  json例如
         * {"latitude":30.279937,"lontitude":120.13694,"mapType":"百度坐标","time":"2018-06-11 18:19:37 "}
         *
         * @return
         */
        @JavascriptInterface
        public String loadLocationInfo() {
            double latitude = GlobalApplication.getInstance().latitude;
            double longtitude = GlobalApplication.getInstance().longtitude;
            Date curDate = new Date(System.currentTimeMillis());//获取当前时间
            String time = mFormatter.format(curDate);
            LocationInfo locationInfo = new LocationInfo();
            locationInfo.setLatitude(latitude);
            locationInfo.setLontitude(longtitude);
            locationInfo.setTime(time);
            locationInfo.setMapType("百度坐标");
            String jsonString = mGson.toJson(locationInfo);
            Log.e(TAG, "loadLocationInfo: " + jsonString);
            return jsonString;
        }

        /**
         * h5调用关闭接口
         */
        @JavascriptInterface
        public void close() {
            GirdPatrolH5Activity.this.finish();

        }


    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!isFirst) {
            javacallHtmlUploadLocationInfo();
        }
        isFirst = false;
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        javacallHtmlUploadLocationInfo();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    /**
     * 调用h5端上传位置信息方法
     */
    private synchronized void javacallHtmlUploadLocationInfo() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mWebView.loadUrl("javascript: UploadLocationInfo()");
            }
        });
    }

    /**
     * 核实有沒有定位权限
     *
     * @return
     */
    private boolean queryPermission() {
        if (!EasyPermissions.hasPermissions(this, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * 这个判断定位服务开没开启
     *
     * @return
     */
    public boolean isLocationEnabled() {
        int locationMode = 0;
        String locationProviders;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            try {
                locationMode = Settings.Secure.getInt(getContentResolver(), Settings.Secure.LOCATION_MODE);
            } catch (Settings.SettingNotFoundException e) {
                e.printStackTrace();
                return false;
            }
            return locationMode != Settings.Secure.LOCATION_MODE_OFF;
        } else {
            locationProviders = Settings.Secure.getString(getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
            return !TextUtils.isEmpty(locationProviders);
        }
    }

    /**
     * 用户信息
     */
    private class User {
        private String name;//用戶名
        private long id;//用户组织id
        private String mobile;//电话

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public long getId() {
            return id;
        }

        public void setId(long id) {
            this.id = id;
        }

        public String getMobile() {
            return mobile;
        }

        public void setMobile(String mobile) {
            this.mobile = mobile;
        }
    }

    /**
     * 点位数据
     */
    private class LocationInfo {
        private double latitude;
        private double lontitude;
        private String time;//本地坐标
        private String mapType;//坐标类型

        public double getLatitude() {
            return latitude;
        }

        public void setLatitude(double latitude) {
            this.latitude = latitude;
        }

        public double getLontitude() {
            return lontitude;
        }

        public void setLontitude(double lontitude) {
            this.lontitude = lontitude;
        }

        public String getTime() {
            return time;
        }

        public void setTime(String time) {
            this.time = time;
        }

        public String getMapType() {
            return mapType;
        }

        public void setMapType(String mapType) {
            this.mapType = mapType;
        }
    }

}
