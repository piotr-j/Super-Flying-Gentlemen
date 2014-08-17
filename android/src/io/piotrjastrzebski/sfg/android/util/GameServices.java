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


import android.content.Intent;
import android.os.Handler;
import android.util.Log;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.utils.ObjectMap;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.games.Games;
import com.google.android.gms.games.GamesStatusCodes;
import com.google.android.gms.games.achievement.Achievement;
import com.google.android.gms.games.achievement.AchievementBuffer;
import com.google.android.gms.games.achievement.Achievements;
import com.google.android.gms.games.leaderboard.LeaderboardVariant;
import com.google.android.gms.games.leaderboard.Leaderboards;

import java.util.concurrent.TimeUnit;

import io.piotrjastrzebski.sfg.ActionListener;
import io.piotrjastrzebski.sfg.SFGApp;
import io.piotrjastrzebski.sfg.android.AndroidActionResolver;
import io.piotrjastrzebski.sfg.android.AndroidLauncher;
import io.piotrjastrzebski.sfg.android.R;
import io.piotrjastrzebski.sfg.android.util.games.GameHelper;
import io.piotrjastrzebski.sfg.game.PlayerStats;
import io.piotrjastrzebski.sfg.utils.Config;

public class GameServices implements GameHelper.GameHelperListener {
    // pref names
    private final static String AUTO_SIGN_IN = "AUTO_SIGN_IN";

    private final static int OUTFIT_BRONZE = 1;
    private final static int OUTFIT_SILVER = 1<<1;
    private final static int OUTFIT_GOLD = 1<<2;
    private final static int OUTFIT_DIAMOND = 1<<3;
    private final static String OUTFITS = "OUTFITS";
    private int outfits = 0;

    private final static int RESULT_LB_SCORE = 100;
    private final static int RESULT_LB_ACHIEV = 101;
    private final static int RESULT_LB_SETTINGS = 102;

    private final AndroidActionResolver actionResolver;
    private final AndroidLauncher activity;
    private GameHelper gh;
    private Tracker gameTracker;
    private Preferences preferences;
    private ObjectMap<PlayerStats.Name, Boolean> achievements;
    private boolean isConnecting;

    public GameServices(AndroidLauncher activity, AndroidActionResolver actionResolver){
        this.activity = activity;
        this.actionResolver = actionResolver;
        achievements = new ObjectMap<PlayerStats.Name, Boolean>();
    }

    public void init(){
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                gh = new GameHelper(activity, GameHelper.CLIENT_GAMES);
                gh.setup(GameServices.this);
                if(SFGApp.DEBUG_GMS)
                    gh.enableDebugLog(true);
                gh.showFailureDialog();
                GoogleAnalytics analytics = GoogleAnalytics.getInstance(activity);
                gameTracker = analytics.newTracker(R.xml.sfg_tracker);

                // initial game start
                sendScreenView("Splash Screen");
                preferences = Gdx.app.getPreferences(SFGApp.PREFS);
                boolean autoConnect = preferences.getBoolean(AUTO_SIGN_IN, false);
                gh.setConnectOnStart(autoConnect);
                if (autoConnect)
                    isConnecting = true;
                gh.onStart(activity);
            }
        });
    }

    public void sendGAEvent(String category, String action, String label){
        gameTracker.send(
                new HitBuilders.EventBuilder()
                        .setCategory(category)
                        .setAction(action)
                        .setLabel(label)
                        .build());
    }

    public void unlockAchievement(PlayerStats.Name achievement) {
        switch (achievement) {
            case YOU_DIED:
                unlockAchievement(activity.getString(R.string.achiev_you_died_id));
                break;
            case BRONZE_HAT:
                unlockAchievement(activity.getString(R.string.achiev_bronze_hat_id));
                outfits |= OUTFIT_BRONZE;
                achievements.put(PlayerStats.Name.BRONZE_HAT, true);
                break;
            case SILVER_HAT:
                unlockAchievement(activity.getString(R.string.achiev_silver_hat_id));
                outfits |= OUTFIT_SILVER;
                achievements.put(PlayerStats.Name.SILVER_HAT, true);
                break;
            case GOLD_HAT:
                unlockAchievement(activity.getString(R.string.achiev_gold_hat_id));
                outfits |= OUTFIT_GOLD;
                achievements.put(PlayerStats.Name.GOLD_HAT, true);
                break;
            case DIAMOND_HAT:
                unlockAchievement(activity.getString(R.string.achiev_diamond_hat_id));
                outfits |= OUTFIT_DIAMOND;
                achievements.put(PlayerStats.Name.DIAMOND_HAT, true);
                break;
            case YOU_GOT_CRUSHED:
                unlockAchievement(activity.getString(R.string.achiev_crushed_id));
                break;
            case YOU_GOT_SPIKED:
                unlockAchievement(activity.getString(R.string.achiev_spiked_id));
                break;
            default:
                break;
        }
        preferences.putInteger(OUTFITS, outfits);
        preferences.flush();
    }

    private void unlockAchievement(String id){
        if (isSignedIn() && !preferences.getBoolean(id, false)) {
            Games.Achievements.unlock(gh.getApiClient(), id);
            preferences.putBoolean(id, true);
            preferences.flush();
        }
    }

    public void incrementAchievement(PlayerStats.IncrementName achievement, int amount) {
        // we have 4 incremental achievements and they are unlock at the same time
        switch (achievement) {
            case DIED:
                incrementAchievement(activity.getString(R.string.achiev_died_25), amount);
                incrementAchievement(activity.getString(R.string.achiev_died_100), amount);
                incrementAchievement(activity.getString(R.string.achiev_died_1000), amount);
                incrementAchievement(activity.getString(R.string.achiev_died_10000), amount);
                break;
            case SCORED:
                incrementAchievement(activity.getString(R.string.achiev_scored_25), amount);
                incrementAchievement(activity.getString(R.string.achiev_scored_100), amount);
                incrementAchievement(activity.getString(R.string.achiev_scored_1000), amount);
                incrementAchievement(activity.getString(R.string.achiev_scored_10000), amount);
                break;
            default:
                break;
        }
    }

    private void incrementAchievement(String id, int amount){
        if (isSignedIn()) {
            Games.Achievements.increment(gh.getApiClient(), id, amount);
        }
    }

    public void sendScreenView(String screen){
        gameTracker.setScreenName(screen);
        gameTracker.send(new HitBuilders.AppViewBuilder().build());
    }

    public void showLeaderBoard(Config.Difficulty difficulty) {
        if (!isSignedIn()){
            signIn();
        } else {
            String id = getLeaderBoardId(difficulty);
            if (id != null) {
                activity.startActivityForResult(
                        Games.Leaderboards.getLeaderboardIntent(
                                gh.getApiClient(),
                                id),
                        RESULT_LB_SCORE
                );
            }
        }
    }

    public boolean hasLeaderboard(Config.Difficulty difficulty) {
        switch (difficulty){
            case BRUTAL:
            case VERY_HARD:
            case HARD:
            case BABY:
                return true;
            default:
                return false;
        }
    }

    private String getLeaderBoardId(Config.Difficulty difficulty){
        switch (difficulty){
            case BRUTAL:
                return activity.getString(R.string.leaderboard_brutal_id);
            case VERY_HARD:
                return activity.getString(R.string.leaderboard_very_hard_id);
            case HARD:
                return activity.getString(R.string.leaderboard_hard_id);
            case BABY:
                return activity.getString(R.string.leaderboard_baby_id);
            default: break;
        }
        return null;
    }

    public void submitScore(int score, Config.Difficulty difficulty) {
        String id = getLeaderBoardId(difficulty);
        if (isSignedIn() && id != null){
            Games.Leaderboards.submitScore(
                    gh.getApiClient(),
                    id,
                    score);
        } else {
            final String diff = difficulty.toString();
            int currentScore = preferences.getInteger(diff, 0);
            if (currentScore < score) {
                preferences.putInteger(diff, score);
                preferences.flush();
            }
        }

    }

    public void queryScore(final Config.Difficulty difficulty) {
        String id = getLeaderBoardId(difficulty);
        if (isSignedIn() && id != null){
            // query the score
            Games.Leaderboards.loadCurrentPlayerLeaderboardScore(
                    gh.getApiClient(),
                    id,
                    LeaderboardVariant.TIME_SPAN_ALL_TIME,
                    LeaderboardVariant.COLLECTION_PUBLIC).setResultCallback(new ResultCallback<Leaderboards.LoadPlayerScoreResult>() {
                @Override
                public void onResult(final Leaderboards.LoadPlayerScoreResult scoreResult) {
                    int score;
                    if (isScoreResultValid(scoreResult)) {
                        // never more than 10000
                        score = (int) scoreResult.getScore().getRawScore();
                    } else {
                        score = preferences.getInteger(difficulty.toString(), 0);
                    }
                    actionResolver.sendEvent(
                            ActionListener.TOP_SCORE_UPDATED, score);
                }
            });
        } else {
            actionResolver.sendEvent(
                    ActionListener.TOP_SCORE_UPDATED,
                    preferences.getInteger(difficulty.toString(), 0));
        }
    }

    private boolean isScoreResultValid(final Leaderboards.LoadPlayerScoreResult scoreResult) {
        return scoreResult != null &&
                GamesStatusCodes.STATUS_OK == scoreResult.getStatus().getStatusCode() &&
                scoreResult.getScore() != null;
    }

    public boolean getAchievementStatus(PlayerStats.Name achievement){
        return achievements.get(achievement, false);
    }

    public void loadAchievements()  {
        if (isSignedIn()){
            Thread thread = new Thread() {
                @Override
                public void run() {
                    long waitTime = 30;

                    PendingResult p = Games.Achievements.load(gh.getApiClient(), false);
                    Achievements.LoadAchievementsResult r = (Achievements.LoadAchievementsResult)p.await(waitTime, TimeUnit.SECONDS);
                    int status = r.getStatus().getStatusCode();
                    if (status != GamesStatusCodes.STATUS_OK)  {
                        r.release();
                        return;
                    }

                    // process the loaded achievements
                    AchievementBuffer buf = r.getAchievements();
                    int bufSize = buf.getCount();
                    for (int i=0; i < bufSize; i++)  {
                        Achievement ach = buf.get(i);
                        String id = ach.getAchievementId();
                        boolean unlocked = ach.getState() == Achievement.STATE_UNLOCKED;
                        addAchievement(id, unlocked);
                    }
                    buf.close();
                    r.release();
                    preferences.flush();
                    actionResolver.sendEvent(ActionListener.ACHIEVEMENTS_LOADED);
                }
            };
            thread.start();
        } else {
            outfits = preferences.getInteger(OUTFITS, 0);
            if (isUnlocked(OUTFIT_BRONZE)){
                achievements.put(PlayerStats.Name.BRONZE_HAT, true);
            }
            if (isUnlocked(OUTFIT_SILVER)){
                achievements.put(PlayerStats.Name.SILVER_HAT, true);
            }
            if (isUnlocked(OUTFIT_GOLD)){
                achievements.put(PlayerStats.Name.GOLD_HAT, true);
            }
            if (isUnlocked(OUTFIT_DIAMOND)){
                achievements.put(PlayerStats.Name.DIAMOND_HAT, true);
            }
            actionResolver.sendEvent(ActionListener.ACHIEVEMENTS_LOADED);
        }
    }

    private void addAchievement(String id, boolean unlocked){
        // we only care about achievements that unlock outfits
        if (activity.getString(R.string.achiev_bronze_hat_id).equals(id)){
            achievements.put(PlayerStats.Name.BRONZE_HAT, unlocked);
            outfits |= OUTFIT_BRONZE;
        } else if (activity.getString(R.string.achiev_silver_hat_id).equals(id)){
            achievements.put(PlayerStats.Name.SILVER_HAT, unlocked);
            outfits |= OUTFIT_SILVER;
        } else if (activity.getString(R.string.achiev_gold_hat_id).equals(id)){
            achievements.put(PlayerStats.Name.GOLD_HAT, unlocked);
            outfits |= OUTFIT_GOLD;
        } else if (activity.getString(R.string.achiev_diamond_hat_id).equals(id)){
            achievements.put(PlayerStats.Name.DIAMOND_HAT, unlocked);
            outfits |= OUTFIT_DIAMOND;
        }
    }

    private boolean isUnlocked(int outfit){
        return (outfits & outfit) != 0;
    }

    public void showAchievements() {
        if (!isSignedIn()){
            signIn();
        } else {
            activity.startActivityForResult(
                    Games.Achievements.getAchievementsIntent(gh.getApiClient()),
                    RESULT_LB_ACHIEV
            );
        }
    }

    public void showSettings() {
        if (!isSignedIn()){
            signIn();
        } else {
            activity.startActivityForResult(
                    Games.getSettingsIntent(gh.getApiClient()),
                    RESULT_LB_SETTINGS
            );
        }
    }

    public void signIn() {
        try {
            isConnecting = true;
            activity.runOnUiThread(new Runnable() {
                public void run() {
                    gh.beginUserInitiatedSignIn();
                }
            });
        } catch (final Exception ex) {
            Log.e("SFG", "GPGS sign in failed " + ex);
        }
    }

    public void signOut() {
        gh.signOut();
        preferences.putBoolean(AUTO_SIGN_IN, false);
        preferences.flush();
        actionResolver.sendEvent(ActionListener.SIGN_OUT);
    }

    public boolean isSignedIn() {
        return gh.isSignedIn();
    }

    public void onActivityResult(int request, int response, Intent data) {
        gh.onActivityResult(request, response, data);
    }

    @Override
    public void onSignInFailed() {
        isConnecting = false;
        actionResolver.sendEvent(ActionListener.SIGN_IN_FAILED);
    }

    @Override
    public void onSignInSucceeded() {
        isConnecting = false;
        actionResolver.sendEvent(ActionListener.SIGN_IN);
        preferences.putBoolean(AUTO_SIGN_IN, true);
        preferences.flush();
        loadAchievements();
    }

    public void onStart(){
        if (gh != null)
            gh.onStart(activity);
    }

    public void onStop(){
        // calling onStop while we are connecting causes infinite login loop
        // if there is no network
        if (gh != null && !isConnecting)
            gh.onStop();
    }
}
