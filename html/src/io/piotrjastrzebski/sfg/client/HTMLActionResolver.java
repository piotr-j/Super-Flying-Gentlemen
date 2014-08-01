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

package io.piotrjastrzebski.sfg.client;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;

import io.piotrjastrzebski.sfg.ActionListener;
import io.piotrjastrzebski.sfg.ActionResolver;
import io.piotrjastrzebski.sfg.SFGApp;
import io.piotrjastrzebski.sfg.game.PlayerStats;
import io.piotrjastrzebski.sfg.utils.Config;


public class HTMLActionResolver implements ActionResolver {
    private final static int OUTFIT_BRONZE = 1;
    private final static int OUTFIT_SILVER = 1<<1;
    private final static int OUTFIT_GOLD = 1<<2;
    private final static int OUTFIT_DIAMOND = 1<<3;
    private final static String OUTFITS = "OUTFITS";
    private int outfits = 0;

    private Array<ActionListener> listeners;
    private boolean isSignedIn = false;
    private boolean isPremium = false;
    private Preferences preferences;
    private ObjectMap<PlayerStats.Name, Boolean> achievements;

    public HTMLActionResolver(){
        listeners = new Array<ActionListener>();
        achievements = new ObjectMap<PlayerStats.Name, Boolean>();
    }

    /**
     * Must be called after Gdx is initialized
     */
    public void init(){
        preferences = Gdx.app.getPreferences(SFGApp.PREFS);
        sendEvent(ActionListener.GAMES_READY);
    }

    @Override
    public void showLeaderBoard() {}

    @Override
    public void showLeaderBoard(Config.Difficulty difficulty) {}

    @Override
    public void showAchievements() {
    }
    @Override
    public void toggleImmersive(boolean enabled) {}

    @Override
    public void submitScore(int score, Config.Difficulty difficulty) {
        final String diff = difficulty.toString();

        int currentScore = preferences.getInteger(diff, 0);
        if (currentScore < score) {
            preferences.putInteger(diff, score);
            preferences.flush();
        }
    }

    @Override
    public void queryScore(Config.Difficulty difficulty) {
        sendEvent(
                ActionListener.TOP_SCORE_UPDATED,
                preferences.getLong(difficulty.toString(), 0));
    }

    @Override
    public void unlockAchievement(PlayerStats.Name achievement) {
        switch (achievement) {
            case BRONZE_HAT:
                outfits |= OUTFIT_BRONZE;
                achievements.put(PlayerStats.Name.BRONZE_HAT, true);
                break;
            case SILVER_HAT:
                outfits |= OUTFIT_SILVER;
                achievements.put(PlayerStats.Name.SILVER_HAT, true);
                break;
            case GOLD_HAT:
                outfits |= OUTFIT_GOLD;
                achievements.put(PlayerStats.Name.GOLD_HAT, true);
                break;
            case DIAMOND_HAT:
                outfits |= OUTFIT_DIAMOND;
                achievements.put(PlayerStats.Name.DIAMOND_HAT, true);
                break;
            default:
                break;
        }
        preferences.putInteger(OUTFITS, outfits);
        preferences.flush();
    }

    @Override
    public void incrementAchievement(PlayerStats.IncrementName achievement, int amount) {}

    @Override
    public void loadAchievements() {
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
        sendEvent(ActionListener.ACHIEVEMENTS_LOADED);
    }

    private boolean isUnlocked(int outfit){
        return (outfits & outfit) != 0;
    }

    @Override
    public boolean getAchievementStatus(PlayerStats.Name achievement){
        return achievements.get(achievement, false);
    }

    @Override
    public void signIn() {
        isSignedIn = true;
        sendEvent(ActionListener.SIGN_IN);
    }

    @Override
    public void signOut() {
        isSignedIn = false;
        sendEvent(ActionListener.SIGN_OUT);
    }

    @Override
    public boolean isSignedIn() {
        return isSignedIn;
    }

    private void sendEvent(int id){
        sendEvent(id, null);
    }

    private void sendEvent(int id, Object data){
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
    public void sendGameGAEvent(String action, String label) {}

    @Override
    public void sendScreenView(String screen){}

    @Override
    public void showAd() {}

    @Override
    public void hideAd() {}

    @Override
    public void buyPremium() {
        isPremium = true;
        sendEvent(ActionListener.PREMIUM_ENABLED);
    }

    @Override
    public void restorePurchase() {}

    @Override
    public boolean isPremium() {
        return isPremium;
    }

    @Override
    public void openWebsite(String url) {}

    @Override
    public void rateApp() {}

    @Override
    public void toast(String message) {}
}
