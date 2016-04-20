package com.example.biezhi.videonew;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.biezhi.videonew.CustomerClass.AES;
import com.example.biezhi.videonew.DataModel.LoginModel;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.util.ArrayList;


public class loginActivity extends AppCompatActivity {

    private WebView webView;
    private ImageButton cacheButton;
    private ImageButton downloadButton;
    private ImageButton searchButton;
    private ImageButton backButton;
    Data appData;


    @Override
    protected void onStart() {
        super.onStart();
        if (appData.getExsit()) {
            finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        appData = (Data) this.getApplicationContext();
        appData.sourcePage = "Login";
        initClass();

    }

    private void initClass() {
        cacheButton = (ImageButton) findViewById(R.id.title_download);
        downloadButton = (ImageButton) findViewById(R.id.title_cache);
        searchButton = (ImageButton) findViewById(R.id.title_search);
        cacheButton.setVisibility(View.INVISIBLE);
        downloadButton.setVisibility(View.INVISIBLE);
        searchButton.setVisibility(View.INVISIBLE);
        backButton = (ImageButton) findViewById(R.id.title_icon);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                appData.setSourcePage("Login");
                startActivity(new Intent(loginActivity.this, defaultActivity.class));

            }
        });
        webView = (WebView) findViewById(R.id.login_view);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.addJavascriptInterface(new InJavaScriptLocalObj(), "local_obj");
        webView.setWebViewClient(new WebViewClient() {
                                     @Override
                                     public boolean shouldOverrideUrlLoading(WebView view, String url) {
                                         view.loadUrl(url);
                                         return true;
                                     }

                                     @Override
                                     public void onPageFinished(WebView view, String url) {
                                         view.loadUrl("javascript:window.local_obj.showSource('<head>'+" +
                                                 "document.getElementsByTagName('html')[0].innerHTML+'</head>');");
                                         super.onPageFinished(view, url);

                                     }
                                 }
        );
        webView.loadUrl("http://115.29.190.54:12345/Login.aspx");
    }

    final class InJavaScriptLocalObj {
        @JavascriptInterface
        public void showSource(String html) {
            if (html.contains("responseData")) {
//                webView.setVisibility(View.INVISIBLE);
                html = html.trim();
                html = html.substring(25, html.length());
                html = html.substring(0, html.length() - 14);
//                webView.setVisibility(View.INVISIBLE);
                //解析当前当前用户信息
                try {
                    JSONObject jsonObject = new JSONObject(html);
                    String responseData = jsonObject.optString("responseData");
                    //解密
                    byte[] tempResult = AES.Decrypt(responseData, "dd358748fcabdda1");
                    responseData = new String(tempResult, "UTF-8");
                    if (responseData.length() > 200) {
                        responseData = responseData.split("\\},\\{")[0];
                        responseData = responseData + "}";
                    }
                    Gson gson = new Gson();
                    LoginModel loginModel = gson.fromJson(responseData, LoginModel.class);
                    String tel = loginModel.getTel();
                    String password = loginModel.getPassword();
                    boolean vip = loginModel.isVip();
                    appData.setUserName(tel);
                    appData.setUserPwd(password);
                    appData.setUserVip(vip);
                } catch (Exception e) {
                    appData.setUserName("");
                    appData.setUserPwd("");
                    appData.setUserVip(false);
                    e.printStackTrace();
                }
                appData.setSourcePage("Login");
                appData.setHtmlString(html);
                Toast.makeText(loginActivity.this,"登陆成功",Toast.LENGTH_SHORT).show();
                startActivity(new Intent(loginActivity.this, defaultActivity.class));
            }
        }
    }

}
