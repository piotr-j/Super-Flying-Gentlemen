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

package io.piotrjastrzebski.sfg.android.util;

import android.view.View;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;

import io.piotrjastrzebski.sfg.ActionListener;
import io.piotrjastrzebski.sfg.android.AndroidActionResolver;
import io.piotrjastrzebski.sfg.android.AndroidLauncher;
import io.piotrjastrzebski.sfg.android.R;

public class Ads implements ActionListener {
    private AdView adView;
    private AndroidLauncher activity;
    private boolean isRotated;
    private boolean isEnabled;

    public Ads(AndroidLauncher activity, AndroidActionResolver actionResolver){
        this.activity = activity;
        actionResolver.registerActionListener(this);
        isRotated = false;
        isEnabled = true;
    }

    public void init(){
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (adView != null){
                    adView.setEnabled(false);
                    adView.setVisibility(View.GONE);
                }
                adView = new AdView(activity);
                adView.setAdSize(AdSize.SMART_BANNER);
                adView.setAdUnitId(activity.getString(R.string.ad_unit_id));
                AdRequest adRequest = new AdRequest.Builder()
                        .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                        .addTestDevice("F4BC5C7402FC3807BAC04638277BAE6B") //N4
                        .addTestDevice("D818DFE9116A9B76ADD52D002E9AEC67") //N7
                        .addTestDevice("9507C053B75E5030DC4013E16DACF9CC") //DHD
                        .addTestDevice("5419A1FBE48307E6E6B9371366E793BC") //TF700
                        .build();
                adView.loadAd(adRequest);
                adView.setEnabled(false);
                adView.setVisibility(View.GONE);
                activity.setAdView(adView);
            }
        });
    }

    @Override
    public void handleEvent(int id, Object data) {
        switch (id){
            case ActionListener.PREMIUM_ENABLED:
                disable();
                break;
            case ActionListener.PREMIUM_DISABLED:
                enable();
                break;
            default: break;
        }
    }

    public void enable(){
        isEnabled = true;

    }

    public void disable(){
        hideAd();
        adView.pause();
        isEnabled = false;
    }

    public void show(){
        // init add if its null or need to fix rotation
        if (adView != null && isEnabled) {
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    adView.setEnabled(true);
                    adView.setVisibility(View.VISIBLE);
                    activity.showPremiumBanner();
                }
            });
        }
    }

    private void hideAd(){
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                adView.setEnabled(false);
                adView.setVisibility(View.GONE);
                activity.hidePremiumBanner();
            }
        });
    }

    public void hide(){
        if (adView != null && isEnabled) {
            hideAd();
            // create new add with correct size
            if (isRotated){
                init();
                isRotated = false;
            }
        }
    }

    public void onResume() {
        if (adView != null) {
            adView.resume();
        }
    }

    public void onPause(){
        if (adView != null) {
            adView.pause();
        }
    }

    public void onRotate() {
        if (isRotated)
            return;
        isRotated = true;
    }


}
