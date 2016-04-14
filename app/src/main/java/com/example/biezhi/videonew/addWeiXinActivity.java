package com.example.biezhi.videonew;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;

public class addWeiXinActivity extends AppCompatActivity {

    private WebView webView;
    private Button button;
    Data appData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_wein_xin);
        webView = (WebView)findViewById(R.id.addWW);
        button = (Button)findViewById(R.id.addBackBt);
        appData = (Data) this.getApplicationContext();
        appData.setSourcePage("AddWeiXin");
        webView.loadUrl("http://115.29.190.54:12345/vip.aspx?weixin="+appData.getWeixinId()+"&state=4");
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
    @Override
    public void onDestroy()
    {
        super.onDestroy();
        webView = null;
        button = null;
    }
}
