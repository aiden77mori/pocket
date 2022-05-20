package com.droidoxy.easymoneyrewards.activities;

import android.annotation.TargetApi;
import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.droidoxy.easymoneyrewards.R;

public class LoadWeb extends Activity {

    private WebView mWebView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.webviewex);

        Log.d("Doronin", "WebView");
        // Get reference of WebView from layout/webviewex.xml
        mWebView = (WebView) findViewById(R.id.webView1);

        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                Log.d("Doronin", "Override");
                view.loadUrl(url);
                return true;
            }
        });

        // Get URL from Intent
        String URL = getIntent().getStringExtra("url");

        // Load website
        mWebView.loadUrl(URL);
        Log.d("Doronin", "Override1");
    }
}
