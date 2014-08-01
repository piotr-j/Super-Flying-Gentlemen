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

package io.piotrjastrzebski.sfg.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;

import io.piotrjastrzebski.sfg.SFGApp;

public class Settings {
    private final static String RATED = "RATED";
    private final static String LIGHTS_STATE = "LIGHTS_STATE";
    private final static String TUT_1_SHOWED = "TUT_1_SHOWED";
    private final static String TUT_2_SHOWED = "TUT_2_SHOWED";
    public final static String IMMERSIVE_MODE_STATE = "IMMERSSIVE_MODE_STATE";
    public final static boolean IMMERSIVE_MODE_OFF = false;
    public final static boolean IMMERSIVE_MODE_ON = true;
    public final static int LIGHTS_OFF = 0;
    public final static int LIGHTS_ON = 1;
    public final static int LIGHTS_CHECK_FPS = 2;
    private final Preferences preferences;
    private boolean isLightsEnabled;
    private int lightsState;
    private boolean tut1Showed;
    private boolean tut2Showed;
    private boolean immersiveState;
    private boolean rated;

    public Settings(){
        preferences = Gdx.app.getPreferences(SFGApp.PREFS);
        lightsState = preferences.getInteger(LIGHTS_STATE, LIGHTS_CHECK_FPS);
        immersiveState = preferences.getBoolean(IMMERSIVE_MODE_STATE, IMMERSIVE_MODE_ON);
        tut1Showed = preferences.getBoolean(TUT_1_SHOWED, false);
        tut2Showed = preferences.getBoolean(TUT_2_SHOWED, false);
        rated = preferences.getBoolean(RATED, false);
        isLightsEnabled = lightsState != LIGHTS_OFF;
    }

    public void saveState(){
        preferences.putInteger(LIGHTS_STATE, lightsState);
        preferences.putBoolean(IMMERSIVE_MODE_STATE, immersiveState);
        preferences.putBoolean(TUT_1_SHOWED, tut1Showed);
        preferences.putBoolean(TUT_1_SHOWED, tut1Showed);
        preferences.putBoolean(RATED, rated);
        preferences.flush();
    }

    public boolean isLightsEnabled() {
        return isLightsEnabled;
    }

    public int getLightsState() {
        return lightsState;
    }

    public void setLightsState(int lightsState) {
        this.lightsState = lightsState;
        isLightsEnabled = lightsState != LIGHTS_OFF;
        saveState();
    }

    public boolean getImmersiveState() {
        return immersiveState;
    }

    public void setImmersiveState(boolean immersiveState) {
        this.immersiveState = immersiveState;
        saveState();
    }
    public void setTutJumpShowed(boolean showed) {
        tut1Showed = showed;
        saveState();
    }

    public boolean getTutJumpShowed(){
        return tut1Showed;
    }

    public void setTutBoostShowed(boolean showed) {
        tut2Showed = showed;
        saveState();
    }

    public boolean getTutBoostShowed(){
        return tut2Showed;
    }

    public boolean isRated() {
        return rated;
    }

    public void setRated(boolean rated) {
        this.rated = rated;
        saveState();
    }

}
