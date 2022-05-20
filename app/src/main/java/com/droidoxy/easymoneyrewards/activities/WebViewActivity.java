package com.droidoxy.easymoneyrewards.activities;

import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.droidoxy.easymoneyrewards.R;

public class WebViewActivity extends ActivityBase{

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.webviewex);

        String url = getIntent().getStringExtra("url");

        WebView view = (WebView) findViewById(R.id.webView1);
        view.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return false;
            }
        });
        view.getSettings().setJavaScriptEnabled(true);
        view.loadUrl(url);
    }
}
