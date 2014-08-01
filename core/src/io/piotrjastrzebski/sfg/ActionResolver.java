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

package io.piotrjastrzebski.sfg;

import io.piotrjastrzebski.sfg.game.PlayerStats;
import io.piotrjastrzebski.sfg.utils.Config;

public interface ActionResolver {
    public void showLeaderBoard();
    public void showLeaderBoard(Config.Difficulty difficulty);
    public void showAchievements();

    public void submitScore(int score, Config.Difficulty difficulty);
    public void queryScore(Config.Difficulty difficulty);
    public void unlockAchievement(PlayerStats.Name achievement);
    public void incrementAchievement(PlayerStats.IncrementName achievement, int amount);
    public boolean getAchievementStatus(PlayerStats.Name achievement);

    public void signIn();
    public void signOut();
    public boolean isSignedIn();

    public void registerActionListener(ActionListener listener);
    public void unRegisterActionListener(ActionListener listener);

    public void sendGameGAEvent(String action, String label);
    public void sendScreenView(String screen);

    public void showAd();
    public void hideAd();
    public void buyPremium();
    public boolean isPremium();
    public void restorePurchase();
    public void rateApp();

    public void openWebsite(String url);
    public void toggleImmersive(boolean enabled);
    public void init();

    public void toast(String message);

    public void loadAchievements();
}
