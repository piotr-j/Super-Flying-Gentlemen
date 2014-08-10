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
    private final static String TUT_1_SHOWED = "TUT_1_SHOWED";
    private final static String TUT_2_SHOWED = "TUT_2_SHOWED";
    private final static String PT_1_SHOWED = "PT_1_SHOWED";
    private final static String PT_2_SHOWED = "PT_2_SHOWED";
    private final static String PT_3_SHOWED = "PT_3_SHOWED";
    private final static String PT_4_SHOWED = "PT_4_SHOWED";
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
    private boolean pt1Showed;
    private boolean pt2Showed;
    private boolean pt3Showed;
    private boolean pt4Showed;
    private boolean immersiveState;
    private boolean rated;

    public Settings(){
        preferences = Gdx.app.getPreferences(SFGApp.PREFS);
        lightsState = preferences.getInteger(LIGHTS_STATE, LIGHTS_CHECK_FPS);
        immersiveState = preferences.getBoolean(IMMERSIVE_MODE_STATE, IMMERSIVE_MODE_ON);
        tut1Showed = preferences.getBoolean(TUT_1_SHOWED, false);
        tut2Showed = preferences.getBoolean(TUT_2_SHOWED, false);
        pt1Showed = preferences.getBoolean(PT_1_SHOWED, false);
        pt2Showed = preferences.getBoolean(PT_2_SHOWED, false);
        pt3Showed = preferences.getBoolean(PT_3_SHOWED, false);
        pt4Showed = preferences.getBoolean(PT_4_SHOWED, false);
        rated = preferences.getBoolean(RATED, false);
        isLightsEnabled = lightsState != LIGHTS_OFF;
    }

    public void saveState(){
        preferences.putInteger(LIGHTS_STATE, lightsState);
        preferences.putBoolean(IMMERSIVE_MODE_STATE, immersiveState);
        preferences.putBoolean(TUT_1_SHOWED, tut1Showed);
        preferences.putBoolean(TUT_1_SHOWED, tut1Showed);
        preferences.putBoolean(PT_1_SHOWED, pt1Showed);
        preferences.putBoolean(PT_2_SHOWED, pt2Showed);
        preferences.putBoolean(PT_3_SHOWED, pt3Showed);
        preferences.putBoolean(PT_4_SHOWED, pt4Showed);
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

    public boolean getPickupTutShowed(Pickup.Type type){
        switch (type){
            case LIVES: return pt1Showed;
            case BOOST: return pt2Showed;
            case SHIELD: return pt3Showed;
            case TOXIC: return pt4Showed;
            default:  return false;
        }
    }

    public void setPickupTutShowed(Pickup.Type type){
        switch (type){
            case LIVES:
                pt1Showed = true;
                break;
            case BOOST:
                pt2Showed = true;
                break;
            case SHIELD:
                pt3Showed = true;
                break;
            case TOXIC:
                pt4Showed = true;
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
        pt1Showed = false;
        pt2Showed = false;
        pt3Showed = false;
        pt4Showed = false;
        tut1Showed = false;
        tut2Showed = false;
        saveState();
    }
}
