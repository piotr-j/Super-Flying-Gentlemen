/*
 * Super Flying Gentlemen
 * Copyright (C) 2014  Piotr Jastrzębski <me@piotrjastrzebski.io>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package io.piotrjastrzebski.sfg.android;

import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ConfigurationInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.crittercism.app.Crittercism;
import com.google.android.gms.ads.AdView;

import io.piotrjastrzebski.sfg.SFGApp;
import io.piotrjastrzebski.sfg.utils.Settings;

public class AndroidLauncher extends AndroidApplication {
    private AndroidActionResolver actionResolver;
    private RelativeLayout layout;
    private AdView adView;
    private TextView premiumBanner;

    @Override
	protected void onCreate (Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        // fix for launcher icon starting new activity on top of old one
        if ((getIntent().getFlags() & Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT) != 0) {
            finish();
            return;
        }

        if (isSupported()){
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);

            AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
            config.hideStatusBar = true;
            config.useAccelerometer = false;
            config.useCompass = false;
            config.useImmersiveMode = getSharedPreferences(SFGApp.PREFS, Context.MODE_PRIVATE)
                    .getBoolean(Settings.IMMERSIVE_MODE_STATE, true);

            Crittercism.initialize(getApplicationContext(), getString(R.string.crittercism_id));

            actionResolver = new AndroidActionResolver(this);

            // initialize for view so we can show add on top
            layout = new RelativeLayout(this);
            SFGApp app = new SFGApp(actionResolver);
            View gameView = initializeForView(app, config);
            layout.addView(gameView);
            addPremiumBanner(layout);
            setContentView(layout);
        }
    }

    private boolean isSupported() {
        final ActivityManager activityManager =
                (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        final ConfigurationInfo configurationInfo =
                activityManager.getDeviceConfigurationInfo();
        final boolean supportsEs2 = configurationInfo.reqGlEsVersion >= 0x20000;

        if (!supportsEs2){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(R.string.not_supported_text)
                    .setTitle(R.string.not_supported_title)
                    .setOnCancelListener(new DialogInterface.OnCancelListener() {
                        @Override
                        public void onCancel(DialogInterface dialog) {
                            finish();
                        }
                    })
                    .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    });

            final AlertDialog d = builder.create();

            d.show();
        }
        return supportsEs2;
    }

    private void addPremiumBanner(RelativeLayout layout){
        RelativeLayout.LayoutParams premiumParams =
                new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                        RelativeLayout.LayoutParams.WRAP_CONTENT);
        premiumParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        premiumParams.addRule(RelativeLayout.CENTER_HORIZONTAL);

        premiumBanner = (TextView) LayoutInflater.from(this).inflate(R.layout.premium_banner, null);
        premiumBanner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                actionResolver.buyPremium();
            }
        });
        hidePremiumBanner();
        layout.addView(premiumBanner, premiumParams);
    }

    public void hidePremiumBanner() {
        premiumBanner.setVisibility(View.GONE);
    }

    public void showPremiumBanner() {
        premiumBanner.setVisibility(View.VISIBLE);
    }

    @Override
    public void onConfigurationChanged(Configuration config) {
        super.onConfigurationChanged(config);
        if (actionResolver != null)
            actionResolver.justRotated();
    }

    public void setAdView(AdView newAdView){
        RelativeLayout.LayoutParams adParams =
                new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
                        RelativeLayout.LayoutParams.WRAP_CONTENT);
        adParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        // center as smart banner doesnt take invisible system buttons into account
        adParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
        if (adView != null){
            layout.removeView(adView);
            adView.destroy();
        }
        layout.addView(newAdView, adParams);
        adView = newAdView;

    }

    @Override
    public void onStart(){
        super.onStart();
        if (actionResolver != null)
            actionResolver.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (actionResolver != null)
            actionResolver.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (actionResolver != null)
            actionResolver.onPause();
    }

    @Override
    public void onStop(){
        super.onStop();
        if (actionResolver != null)
            actionResolver.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (actionResolver != null)
            actionResolver.onDestroy();
    }

    @Override
    public void onActivityResult(int request, int response, Intent data) {
        super.onActivityResult(request, response, data);
        if (actionResolver != null)
            actionResolver.onActivityResult(request, response, data);
    }

}
