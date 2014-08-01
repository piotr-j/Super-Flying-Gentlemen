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

import android.annotation.TargetApi;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.widget.Toast;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Array;

import io.piotrjastrzebski.sfg.ActionListener;
import io.piotrjastrzebski.sfg.ActionResolver;
import io.piotrjastrzebski.sfg.SFGApp;
import io.piotrjastrzebski.sfg.android.util.Ads;
import io.piotrjastrzebski.sfg.android.util.GameServices;
import io.piotrjastrzebski.sfg.android.util.Shop;
import io.piotrjastrzebski.sfg.game.PlayerStats;
import io.piotrjastrzebski.sfg.utils.Config;

public class AndroidActionResolver implements ActionResolver {
    private static final String GA_EVENT_TEST = "Test Event";
    private static final String GA_EVENT_GAME = "Game Event";
    private final AndroidLauncher activity;

    private Array<ActionListener> listeners;

    private Shop shop;
    private Ads ads;
    private GameServices gameServices;

    public AndroidActionResolver(AndroidLauncher activity){
        this.activity = activity;
        listeners = new Array<ActionListener>();

        gameServices = new GameServices(activity, this);
        shop = new Shop(activity, this);
        ads = new Ads(activity, this);
    }

    /**
     * Must be called after Gdx is initialized
     */
    public void init(){
        gameServices.init();
        ads.init();
        shop.init();
    }

    @TargetApi(19)
    @Override
    public void toggleImmersive(final boolean enabled) {
        //TODO make this dynamic
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){
            toast(activity.getString(R.string.restart_required));
        }
    }

    @Override
    public void buyPremium() {
        shop.buyPremium();
    }

    @Override
    public boolean isPremium() {
        return shop.isPremium();
    }

    @Override
    public void restorePurchase() {
        shop.restorePurchase();
    }

    public void justRotated(){
        ads.onRotate();
    }

    @Override
    public void showAd() {
        ads.show();
    }

    @Override
    public void hideAd() {
        ads.hide();
    }

    private void sendGAEvent(String category, String action, String label){
        gameServices.sendGAEvent(category, action, label);
    }

    @Override
    public void sendScreenView(String screen){
        gameServices.sendScreenView(screen);
    }

    @Override
    public void sendGameGAEvent(String action, String label){
        sendGAEvent(GA_EVENT_GAME, action, label);
    }

    @Override
    public void showLeaderBoard() {
        showLeaderBoard(Config.Difficulty.BRUTAL);
    }

    @Override
    public void showLeaderBoard(Config.Difficulty difficulty) {
        gameServices.showLeaderBoard(difficulty);
    }

    @Override
    public void submitScore(int score, Config.Difficulty difficulty) {
        gameServices.submitScore(score, difficulty);
    }

    @Override
    public void queryScore(Config.Difficulty difficulty) {
        gameServices.queryScore(difficulty);
    }

    @Override
    public void unlockAchievement(PlayerStats.Name achievement) {
        gameServices.unlockAchievement(achievement);
    }

    @Override
    public void incrementAchievement(PlayerStats.IncrementName achievement, int amount) {
        gameServices.incrementAchievement(achievement, amount);
    }

    @Override
    public void loadAchievements() {
        gameServices.loadAchievements();
    }

    @Override
    public boolean getAchievementStatus(PlayerStats.Name achievement){
        return gameServices.getAchievementStatus(achievement);
    }

    @Override
    public void showAchievements() {
        gameServices.showAchievements();
    }

    @Override
    public void signIn() {
        gameServices.signIn();
    }

    @Override
    public void signOut() {
        gameServices.signOut();
    }

    @Override
    public boolean isSignedIn() {
        return gameServices.isSignedIn();
    }

    @Override
    public void openWebsite(String url) {
        if (!url.startsWith("http://") && !url.startsWith("https://"))
            url = "http://" + url;
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        activity.startActivity(browserIntent);
    }

    @Override
    public void rateApp() {
        Uri uri = Uri.parse("market://details?id=" + activity.getPackageName());
        Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
        try {
            activity.startActivity(goToMarket);
        } catch (ActivityNotFoundException e) {
            uri = Uri.parse("http://play.google.com/store/apps/details?id="
                    + activity.getPackageName());
            activity.startActivity(new Intent(Intent.ACTION_VIEW, uri));
        }
    }

    public void onStart(){
        gameServices.onStart();
    }

    public void onResume() {
        ads.onResume();
    }

    public void onPause(){
        ads.onPause();
    }

    public void onStop(){
        gameServices.onStop();
    }

    public void onDestroy() {
        shop.onDestroy();
    }

    public void onActivityResult(int request, int response, Intent data) {
        if (!shop.onActivityResult(request, response, data)) {
            gameServices.onActivityResult(request, response, data);
        }
    }


    public void sendEvent(int id){
        sendEvent(id, null);
    }

    public void sendEvent(int id, Object data){
        for (ActionListener listener: listeners){
            listener.handleEvent(id, data);
        }
    }

    @Override
    public void registerActionListener(ActionListener listener) {
        if (!listeners.contains(listener, true)){
            listeners.add(listener);
        }
    }

    @Override
    public void unRegisterActionListener(ActionListener listener) {
        listeners.removeValue(listener, true);
    }

    @Override
    public void toast(final String message) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(activity, message, Toast.LENGTH_LONG).show();
            }
        });
    }
}
