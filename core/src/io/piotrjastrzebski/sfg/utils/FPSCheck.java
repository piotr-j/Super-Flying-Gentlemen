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
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;

import io.piotrjastrzebski.sfg.screen.GameScreen;

public class FPSCheck {
    // lights will be disabled if fps is below this number
    public final static int MIN_FPS = 50;
    public final static int FPS_CHECK_DELAY = 5; // in sec
    private final Assets assets;
    private final Stage stage;
    private final Settings settings;
    private final GameScreen gameScreen;

    private float lightsCheckTimer = 0;
    private int lightsState;
    private boolean isLightsOn;

    public FPSCheck(GameScreen gameScreen){
        this.assets = Locator.getAssets();
        this.settings = Locator.getSettings();
        this.gameScreen = gameScreen;
        this.stage = gameScreen.getStage();

        lightsState = settings.getLightsState();
        isLightsOn = lightsState != Settings.LIGHTS_OFF;
    }

    private void showLowFPSDialog(){
        Dialog fpsDialog = new Dialog("", assets.getSkin()){
            @Override
            protected void result(Object object) {
                super.result(object);
                gameScreen.resumeGame();
            }
        };
        final Table content = fpsDialog.getContentTable();
        content.add(new Label(assets.getText(Assets.DIALOG_LIGHTS_1), assets.getSkin()));
        content.row();
        content.add(new Label(assets.getText(Assets.DIALOG_LIGHTS_2), assets.getSkin())).pad(20);
        final Table buttons = fpsDialog.getButtonTable();
        final TextButton ok = new TextButton(assets.getText(Assets.OK), assets.getSkin(), "small");
        buttons.add(ok).padBottom(20);
        fpsDialog.setObject(ok, null);
        fpsDialog.show(stage);
        gameScreen.pauseGame();
    }

    public void updateLightCheck(float delta){
        if (lightsState != Settings.LIGHTS_CHECK_FPS)
            return;

        lightsCheckTimer += delta;
        // get fps after few seconds as at the start it may be too low
        if (lightsCheckTimer > FPS_CHECK_DELAY){
            if (Gdx.graphics.getFramesPerSecond() < MIN_FPS){
                isLightsOn = false;
                lightsState = Settings.LIGHTS_OFF;
                settings.setLightsState(Settings.LIGHTS_OFF);
                showLowFPSDialog();
            } else {
                isLightsOn = true;
                lightsState = Settings.LIGHTS_ON;
                settings.setLightsState(Settings.LIGHTS_ON);
            }
        }
    }

    public boolean isLightsOn(){
        return isLightsOn;
    }
}
