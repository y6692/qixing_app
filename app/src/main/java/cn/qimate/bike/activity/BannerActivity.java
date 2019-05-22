/*
 * Copyright © 2017 Hubcloud.com.cn. All rights reserved.
 * BannerActivity.java
 * AdHubSDK
 * Use of this source code is governed by a BSD-style license that can be
 * found in the LICENSE file.
 *
 */

package cn.qimate.bike.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.hubcloud.adhubsdk.AdHub;
import com.hubcloud.adhubsdk.AdListener;
import com.hubcloud.adhubsdk.AdRequest;
import com.hubcloud.adhubsdk.AdView;
import com.hubcloud.adhubsdk.internal.utilities.StringUtil;

import cn.qimate.bike.R;

public class BannerActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_banner);
        Intent intent = getIntent();

//        String appId = "2597", adUnitId = "7503";

        String appId = "277", adUnitId = "943";

//        if (intent.hasExtra("appId")) {
//            appId = intent.getStringExtra("appId");
//        }
//        if (StringUtil.isEmpty(appId)) {
//            appId = getString(R.string.app_id);
//        }
//        if (intent.hasExtra("adUnitId")) {
//            adUnitId = intent.getStringExtra("adUnitId");
//        }
//        if (StringUtil.isEmpty(adUnitId)) {
//            adUnitId = "943";
//        }
        // Initialize the Mobile Ads SDK.
        //不建议在activity里面初始化，请放在application里面
        AdHub.initialize(this, appId);
        final AdView adView = findViewById(R.id.adView);
        //设置广告位ID
        adView.setAdUnitId(adUnitId);
        adView.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                Toast.makeText(BannerActivity.this, "onAdLoaded", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAdShown() {
                Toast.makeText(BannerActivity.this, "onAdShown", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAdFailedToLoad(int errorCode) {
                Toast.makeText(BannerActivity.this, "onAdFailedToLoad: " + errorCode, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAdLeftApplication() {
                Toast.makeText(BannerActivity.this, "onAdLeftApplication", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAdClosed() {
                Toast.makeText(BannerActivity.this, "onAdClosed", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAdOpened() {
                Toast.makeText(BannerActivity.this, "onAdOpened", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAdClicked() {
                Toast.makeText(BannerActivity.this, "onAdClicked", Toast.LENGTH_SHORT).show();
            }
        });
        final AdRequest adRequest = new AdRequest.Builder().build();
        //建议使用此种写法，如果你不是在xml中配置的adview，请使用此方式。
        adView.post(new Runnable() {
            @Override
            public void run() {
                adView.loadAd(adRequest);
            }
        });
    }
}
