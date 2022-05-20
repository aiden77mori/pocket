package com.droidoxy.easymoneyrewards.activities;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import com.droidoxy.easymoneyrewards.R;
import com.droidoxy.easymoneyrewards.utils.AppUtils;

public class TestActivity extends ActivityBase {
    @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            Log.d("Doronin", "testActivity");
            setContentView(R.layout.test_layout);
        }
}
