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
import io.piotrjastrzebski.sfg.game.objects.Pickup;

public class Settings {
    private final static String RATED = "RATED";
    private final static String LIGHTS_STATE = "LIGHTS_STATE";
    private final static String TUT_1_SHOWN = "TUT_1_SHOWN";
    private final static String TUT_2_SHOWN = "TUT_2_SHOWN";
    private final static String TUT_3_SHOWN = "TUT_3_SHOWN";
    private final static String PT_1_SHOWN = "PT_1_SHOWN";
    private final static String PT_2_SHOWN = "PT_2_SHOWN";
    private final static String PT_3_SHOWN = "PT_3_SHOWN";
    private final static String PT_4_SHOWN = "PT_4_SHOWN";
    public final static String IMMERSIVE_MODE_STATE = "IMMERSSIVE_MODE_STATE";
    public final static boolean IMMERSIVE_MODE_OFF = false;
    public final static boolean IMMERSIVE_MODE_ON = true;
    public final static int LIGHTS_OFF = 0;
    public final static int LIGHTS_ON = 1;
    public final static int LIGHTS_CHECK_FPS = 2;
    private final Preferences preferences;
    private boolean isLightsEnabled;
    private int lightsState;
    private boolean tut1Shown;
    private boolean tut2Shown;
    private boolean tut3Shown;
    private boolean pt1Shown;
    private boolean pt2Shown;
    private boolean pt3Shown;
    private boolean pt4Show;
    private boolean immersiveState;
    private boolean rated;

    public Settings(){
        preferences = Gdx.app.getPreferences(SFGApp.PREFS);
        lightsState = preferences.getInteger(LIGHTS_STATE, LIGHTS_CHECK_FPS);
        immersiveState = preferences.getBoolean(IMMERSIVE_MODE_STATE, IMMERSIVE_MODE_ON);
        tut1Shown = preferences.getBoolean(TUT_1_SHOWN, false);
        tut2Shown = preferences.getBoolean(TUT_2_SHOWN, false);
        tut3Shown = preferences.getBoolean(TUT_3_SHOWN, false);
        pt1Shown = preferences.getBoolean(PT_1_SHOWN, false);
        pt2Shown = preferences.getBoolean(PT_2_SHOWN, false);
        pt3Shown = preferences.getBoolean(PT_3_SHOWN, false);
        pt4Show = preferences.getBoolean(PT_4_SHOWN, false);
        rated = preferences.getBoolean(RATED, false);
        isLightsEnabled = lightsState != LIGHTS_OFF;
    }

    public void saveState(){
        preferences.putInteger(LIGHTS_STATE, lightsState);
        preferences.putBoolean(IMMERSIVE_MODE_STATE, immersiveState);
        preferences.putBoolean(TUT_1_SHOWN, tut1Shown);
        preferences.putBoolean(TUT_2_SHOWN, tut2Shown);
        preferences.putBoolean(TUT_3_SHOWN, tut3Shown);
        preferences.putBoolean(PT_1_SHOWN, pt1Shown);
        preferences.putBoolean(PT_2_SHOWN, pt2Shown);
        preferences.putBoolean(PT_3_SHOWN, pt3Shown);
        preferences.putBoolean(PT_4_SHOWN, pt4Show);
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
        tut1Shown = showed;
        saveState();
    }

    public boolean getTutJumpShowed(){
        return tut1Shown;
    }

    public void setTutBoostShowed(boolean showed) {
        tut2Shown = showed;
        saveState();
    }

    public boolean getTutBoostShowed(){
        return tut2Shown;
    }

    public void setTutBreakableShowed(boolean showed) {
        tut3Shown = showed;
        saveState();
    }

    public boolean getTutBreakableShowed() {
        return tut3Shown;
    }

    public boolean getPickupTutShowed(Pickup.Type type){
        switch (type){
            case LIVES: return pt1Shown;
            case BOOST: return pt2Shown;
            case SHIELD: return pt3Shown;
            case TOXIC: return pt4Show;
            default:  return false;
        }
    }

    public void setPickupTutShowed(Pickup.Type type){
        switch (type){
            case LIVES:
                pt1Shown = true;
                break;
            case BOOST:
                pt2Shown = true;
                break;
            case SHIELD:
                pt3Shown = true;
                break;
            case TOXIC:
                pt4Show = true;
                break;
            default: break;
        }
        saveState();
    }

    public boolean isRated() {
        return rated;
    }

    public void setRated(boolean rated) {
        this.rated = rated;
        saveState();
    }

    public void resetTutorials() {
        pt1Shown = false;
        pt2Shown = false;
        pt3Shown = false;
        pt4Show = false;
        tut1Shown = false;
        tut2Shown = false;
        tut3Shown = false;
        saveState();
    }
}
