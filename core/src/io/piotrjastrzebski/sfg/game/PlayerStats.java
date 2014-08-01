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

package io.piotrjastrzebski.sfg.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.utils.Array;

import io.piotrjastrzebski.sfg.ActionListener;
import io.piotrjastrzebski.sfg.ActionResolver;
import io.piotrjastrzebski.sfg.SFGApp;
import io.piotrjastrzebski.sfg.events.Event;
import io.piotrjastrzebski.sfg.events.EventListener;
import io.piotrjastrzebski.sfg.events.EventLoop;
import io.piotrjastrzebski.sfg.events.EventType;
import io.piotrjastrzebski.sfg.game.objects.obstacles.ScoreSensor;
import io.piotrjastrzebski.sfg.utils.Config;
import io.piotrjastrzebski.sfg.utils.Locator;

public class PlayerStats implements EventListener, ActionListener {
    public enum Name {
        YOU_DIED, BRONZE_HAT, SILVER_HAT, GOLD_HAT, DIAMOND_HAT, YOU_GOT_CRUSHED, YOU_GOT_SPIKED
    }
    public enum IncrementName {
        DIED, SCORED
    }
    public final static int SKIN_BASIC = 1;
    public final static int SKIN_BRONZE = 1 << 1;
    public final static int SKIN_SILVER = 1 << 2;
    public final static int SKIN_GOLD = 1 << 3;
    public final static int SKIN_DIAMOND = 1 << 4;
    public final static int SKIN_RUBY = 1 << 5;

    private final static String[] SKIN_NAMES = {"basic", "bronze", "silver", "gold", "diamond", "ruby"};
    public final static String[] TO_UNLOCK = {"", "10", "25", "50", "100", ""};
    private final static String SKIN = "SKIN";
    private final Preferences preferences;

    private Skin currentPlayerSkin;

    private ActionResolver actionResolver;
    private int playerScore = 0;
    private int maxPlayerScore = 0;
    private Array<Skin> skins;
    private Config.Difficulty difficulty;
    private EventLoop events;

    public PlayerStats(ActionResolver actionResolver){
        this.actionResolver = actionResolver;
        actionResolver.registerActionListener(this);
        preferences = Gdx.app.getPreferences(SFGApp.PREFS);
        skins = new Array<Skin>();
        for (int i = 0; i < SKIN_NAMES.length; i++) {
            skins.add(new Skin(1 << i, SKIN_NAMES[i], TO_UNLOCK[i]));
        }
        skins.get(0).setUnlocked(true);
        currentPlayerSkin = getPlayerSkinById(SKIN_BASIC);
        setEventLoop(Locator.getEvents());
    }

    public void setDifficulty(Config.Difficulty difficulty){
        this.difficulty = difficulty;
        actionResolver.queryScore(difficulty);
    }

    private void setEventLoop(EventLoop eventLoop){
        events = eventLoop;
        eventLoop.register(this, EventType.PLAYER_RESPAWNED);
        eventLoop.register(this, EventType.PLAYER_DIED);
        eventLoop.register(this, EventType.PLAYER_SCORED);
        eventLoop.register(this, EventType.PLAYER_CRUSHED);
        eventLoop.register(this, EventType.PLAYER_SPIKED);
    }

    private void saveState(){
        preferences.putInteger(SKIN, currentPlayerSkin.id);
        preferences.flush();
    }

    public int getScore(){
        return playerScore;
    }

    public int getMaxScore(){
        return maxPlayerScore;
    }

    public void showAchievements(){
        actionResolver.showAchievements();
    }

    public void showLeaderBoards(){
        actionResolver.showLeaderBoard(difficulty);
    }

    @Override
    public void handleEvent(Event e) {
        // no achievements for baby difficulty
        switch (e.getType()){
            case EventType.PLAYER_RESPAWNED:
                playerScore = 0;
                break;
            case EventType.PLAYER_DIED:
                if (difficulty != Config.Difficulty.BABY) {
                    actionResolver.unlockAchievement(Name.YOU_DIED);
                }
                handlePlayerDied();
                break;
            case EventType.PLAYER_CRUSHED:
                if (difficulty != Config.Difficulty.BABY) {
                    actionResolver.unlockAchievement(Name.YOU_GOT_CRUSHED);
                }
                break;
            case EventType.PLAYER_SPIKED:
                if (difficulty != Config.Difficulty.BABY) {
                    actionResolver.unlockAchievement(Name.YOU_GOT_SPIKED);
                }
                break;
            case EventType.PLAYER_SCORED:
                final ScoreSensor s = (ScoreSensor) e.getData();
                if (s.enabled()) {
                    playerScore += 1;
                    handleScore(playerScore);
                    events.queueEvent(EventType.PLAYER_SCORE_CHANGED, playerScore);
                }
                break;
            default: break;
        }
    }

    private void handleScore(long score){
        if (difficulty != Config.Difficulty.BABY){
            if (score == 10){
                actionResolver.unlockAchievement(Name.BRONZE_HAT);
                unlockSkin(SKIN_BRONZE);
            } else if (score == 25){
                actionResolver.unlockAchievement(Name.SILVER_HAT);
                unlockSkin(SKIN_SILVER);
            } else if (score == 50){
                actionResolver.unlockAchievement(Name.GOLD_HAT);
                unlockSkin(SKIN_GOLD);
            } else if (score == 100){
                actionResolver.unlockAchievement(Name.DIAMOND_HAT);
                unlockSkin(SKIN_DIAMOND);
            }
        }
    }

    private void handlePlayerDied(){
        actionResolver.sendGameGAEvent("Player Death", "Score: " +playerScore);
        //TODO add progressive achievement for total deaths
        if (maxPlayerScore < playerScore) {
            maxPlayerScore = playerScore;
            actionResolver.submitScore(maxPlayerScore, difficulty);
        }
        actionResolver.incrementAchievement(IncrementName.DIED, 1);
        if (playerScore > 0){
            actionResolver.incrementAchievement(IncrementName.SCORED, playerScore);
        }
    }

    public void buyPremium(){
        actionResolver.buyPremium();
    }

    @Override
    public void handleEvent(int id, Object data) {
        switch (id){
            case ActionListener.PREMIUM_ENABLED:
            case ActionListener.PREMIUM_DISABLED:
                checkPremium();
                break;
            case ActionListener.TOP_SCORE_UPDATED:
                maxPlayerScore = (Integer)data;
                handleScore(maxPlayerScore);
                break;
            case ActionListener.GAMES_READY:
                actionResolver.loadAchievements();
                break;
            case ActionListener.ACHIEVEMENTS_LOADED:
                loadAchievements();
                break;
            default: break;
        }
    }

    private void loadAchievements(){
        if (actionResolver.getAchievementStatus(Name.BRONZE_HAT)){
            unlockSkin(SKIN_BRONZE);
        }
        if (actionResolver.getAchievementStatus(Name.SILVER_HAT)){
            unlockSkin(SKIN_SILVER);
        }
        if (actionResolver.getAchievementStatus(Name.GOLD_HAT)){
            unlockSkin(SKIN_GOLD);
        }
        if (actionResolver.getAchievementStatus(Name.DIAMOND_HAT)){
            unlockSkin(SKIN_DIAMOND);
        }
        currentPlayerSkin = getPlayerSkinById(preferences.getInteger(SKIN, SKIN_BASIC));
        if (currentPlayerSkin == null || !currentPlayerSkin.isUnlocked()){
            currentPlayerSkin = getPlayerSkinById(SKIN_BASIC);
        }
        checkPremium();
    }

    public boolean isPremium(){
        return actionResolver.isPremium();
    }

    private void unlockSkin(int id){
        final Skin skin = getPlayerSkinById(id);
        skin.setUnlocked(true);
        saveState();
    }

    private void lockSkin(int id){
        final Skin skin = getPlayerSkinById(id);
        skin.setUnlocked(false);
        saveState();
    }

    private void checkPremium(){
        if (actionResolver.isPremium()){
            unlockSkin(SKIN_RUBY);
        } else {
            if (currentPlayerSkin.id == getPlayerSkinById(SKIN_RUBY).id){
                currentPlayerSkin = getPlayerSkinById(SKIN_BASIC);
            }
            lockSkin(SKIN_RUBY);
        }
    }

    public void setPlayerSkin(Skin skin){
        currentPlayerSkin = skin;
        saveState();
    }

    public void setDefaultPlayerSkin() {
        setPlayerSkin(skins.get(0));
    }

    public Skin getPlayerSkin() {
        return currentPlayerSkin;
    }

    public Skin getPlayerSkinById(int id) {
        for (Skin skin :skins ){
            if (skin.id == id)
                return skin;
        }
        return currentPlayerSkin;
    }

    public Array<Skin> getSkins(){
        checkPremium();
        return skins;
    }

    public class Skin {
        public final int id;
        public final String name;
        // points to unlock or ""
        public final String points;
        private boolean isUnlocked;

        public Skin(int id, String name, String points){
            this.id = id;
            this.name = name;
            this.points = points;
            isUnlocked = false;
        }

        public boolean isUnlocked() {
            return isUnlocked;
        }

        public void setUnlocked(boolean isUnlocked) {
            this.isUnlocked = isUnlocked;
        }
    }
}
