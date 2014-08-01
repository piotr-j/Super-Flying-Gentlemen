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

package io.piotrjastrzebski.sfg.screen;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import io.piotrjastrzebski.sfg.game.objects.Background;
import io.piotrjastrzebski.sfg.screen.inputhandlers.SettingsInputHandler;
import io.piotrjastrzebski.sfg.ui.GameButton;
import io.piotrjastrzebski.sfg.utils.Assets;
import io.piotrjastrzebski.sfg.utils.Locator;
import io.piotrjastrzebski.sfg.utils.Settings;
import io.piotrjastrzebski.sfg.utils.SoundManager;

public class SettingsScreen extends DefaultScreen {
    private Background background;
    private SoundManager soundManager;
    private Label soundVolLabel;
    private Label musicVolLabel;

    public SettingsScreen() {
        super();
        Gdx.input.setInputProcessor(new SettingsInputHandler(this).getIM());
        Gdx.input.setCatchBackKey(true);
        actionResolver.sendScreenView("SettingsScreen");
        background = new Background();
        soundManager = assets.getSoundManager();

        createUI();

        updateSoundLabel();
        updateMusicLabel();
    }

    private void createUI(){
        final Table root = new Table();
        root.setFillParent(true);


        final TextButton back = new TextButton(assets.getText(Assets.BACK), assets.getSkin(), "small");
        back.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                handleBack();
                playButtonPressSound();
            }
        });
        root.add(new Label(assets.getText(Assets.SETTINGS), assets.getSkin(), "default-large"));
        root.row();
        final Table container = new Table();
        container.add(createSoundSettings()).expandX().fill();
        container.row();
        container.add(createLightsToggle()).expandX().fill();
        container.row();
        // show the option only on android kitkat+
        if (Gdx.app.getType() == Application.ApplicationType.Android &&
                Gdx.app.getVersion() >= 19) {
            container.add(createImmersiveToggle()).expandX().fill();
            container.row();
        }
        if (Gdx.app.getType() == Application.ApplicationType.Android) {
            container.add(createRestorePurchases()).pad(20);
            container.row();
        }
        if (settings.getTutJumpShowed() || settings.getTutBoostShowed()){
            container.add(createResetTutorials()).pad(20);
            container.row();
        }
        if (settings.isRated()){
            container.add(createRate()).pad(20);
            container.row();
        }

        final ScrollPane scrollPane = new ScrollPane(container, assets.getSkin());
        root.add(scrollPane).fillX().pad(20);
        root.row();
        root.add(back).pad(20).left().bottom().expand();
        stage.addActor(root);
    }

    private Table createSoundSettings(){
        Table settings = new Table();

        final Label sound = new Label(assets.getText(Assets.SOUND_VOLUME), assets.getSkin());
        soundVolLabel = new Label("0", assets.getSkin());
        final Slider soundSlider = new Slider(0, 1, 0.1f, false, assets.getSkin());
        soundSlider.setValue(soundManager.getSoundVolume());
        soundSlider.addListener( new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                soundManager.setSoundVolume(soundSlider.getValue());
                updateSoundLabel();
                playButtonPressSound();
            }
        });

        final Table soundLabels = new Table();
        soundLabels.add(sound);
        soundLabels.add().expandX();
        soundLabels.add(soundVolLabel);
        settings.add(soundLabels).expandX().fillX().pad(20);
        settings.row();
        settings.add(soundSlider).expandX().fillX().pad(20);

        settings.row();

        final Label music = new Label(assets.getText(Assets.MUSIC_VOLUME), assets.getSkin());
        musicVolLabel = new Label("0", assets.getSkin());
        final Slider musicSlider = new Slider(0, 1, 0.1f, false, assets.getSkin());
        musicSlider.setValue(soundManager.getMusicVolume());
        musicSlider.addListener( new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                soundManager.setMusicVolume(musicSlider.getValue());
                updateMusicLabel();
                playButtonPressSound();
            }
        });
        final Table musicLabels = new Table();

        musicLabels.add(music);
        musicLabels.add().expandX();
        musicLabels.add(musicVolLabel);
        settings.add(musicLabels).expandX().fillX().pad(20);
        settings.row();
        settings.add(musicSlider).expandX().fillX().pad(20);
        return settings;
    }

    private Table createLightsToggle(){
        Table container = new Table();

        final Label lightsLabel = new Label("", assets.getSkin());
        if(settings.isLightsEnabled()){
            lightsLabel.setText(assets.getText(Assets.LIGHTS_ENABLED));
        } else {
            lightsLabel.setText(assets.getText(Assets.LIGHTS_DISABLED));
        }
        final Button lightsToggle = new Button(assets.getSkin(), "small");
        lightsToggle.setChecked(settings.isLightsEnabled());

        lightsToggle.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (lightsToggle.isChecked()){
                    lightsLabel.setText(assets.getText(Assets.LIGHTS_ENABLED));
                    settings.setLightsState(Settings.LIGHTS_ON);
                } else {
                    lightsLabel.setText(assets.getText(Assets.LIGHTS_DISABLED));
                    settings.setLightsState(Settings.LIGHTS_OFF);
                }
                playButtonPressSound();
            }
        });
        container.add(lightsLabel).pad(20);
        container.add().expandX();
        container.add(lightsToggle).pad(20);
        return container;
    }

    private Table createImmersiveToggle(){
        Table container = new Table();

        final Label immLabel = new Label("", assets.getSkin());
        if(settings.getImmersiveState()){
            immLabel.setText(assets.getText(Assets.IMMERSIVE_ENABLED));
        } else {
            immLabel.setText(assets.getText(Assets.IMMERSIVE_DISABLED));
        }
        final Button immToggle = new Button(assets.getSkin(), "small");
        immToggle.setChecked(
                settings.getImmersiveState());

        immToggle.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (immToggle.isChecked()) {
                    immLabel.setText(assets.getText(Assets.IMMERSIVE_ENABLED));
                    settings.setImmersiveState(Settings.IMMERSIVE_MODE_ON);
                    actionResolver.toggleImmersive(true);
                } else {
                    immLabel.setText(assets.getText(Assets.IMMERSIVE_DISABLED));
                    settings.setImmersiveState(Settings.IMMERSIVE_MODE_OFF);
                    actionResolver.toggleImmersive(false);
                }
                playButtonPressSound();
            }
        });
        container.add(immLabel).pad(20);
        container.add().expandX();
        container.add(immToggle).pad(20);
        return container;
    }

    private Table createRestorePurchases(){
        TextButton restore = new TextButton(assets.getText(Assets.RESTORE_PURCHASES), assets.getSkin());
        restore.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                actionResolver.restorePurchase();
                playButtonPressSound();
            }
        });
        return restore;
    }

    private Table createResetTutorials(){
        TextButton restore = new TextButton(assets.getText(Assets.RESET_TUTORIALS), assets.getSkin());
        restore.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                settings.setTutBoostShowed(false);
                settings.setTutJumpShowed(false);
                actionResolver.toast(assets.getText(Assets.RESET_TUTORIALS_TOAST));
                playButtonPressSound();
            }
        });
        return restore;
    }

    private Table createRate(){
        final GameButton rateButton = new GameButton(
                assets.getText(Assets.RATE),
                assets.getUIRegion("rate"),
                assets.getSkin());
        rateButton.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                actionResolver.rateApp();
            }
        });
        return rateButton;
    }

    private void updateSoundLabel(){
        if (!soundManager.isSoundEnabled()) {
            soundVolLabel.setText(assets.getText(Assets.SOUND_OFF));
            return;
        }
        int volume = (int)(soundManager.getSoundVolume()*10);
        if (volume == 10){
            soundVolLabel.setText(assets.getText(Assets.SOUND_MAX));
        } else if (volume == 0){
            soundVolLabel.setText(assets.getText(Assets.SOUND_OFF));
        } else {
            soundVolLabel.setText(String.valueOf(volume));
        }
    }

    private void updateMusicLabel(){
        if (!soundManager.isMusicEnabled()) {
            musicVolLabel.setText(assets.getText(Assets.MUSIC_OFF));
            return;
        }
        int volume = (int)(soundManager.getMusicVolume()*10);
        if (volume == 10){
            musicVolLabel.setText(assets.getText(Assets.MUSIC_MAX));
        } else if (volume == 0){
            musicVolLabel.setText(assets.getText(Assets.MUSIC_OFF));
        } else {
            musicVolLabel.setText(String.valueOf(volume));
        }
    }

    public void handleBack() {
        Locator.getApp().setScreen(new MainMenuScreen());
    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
        background.updateViewport(width, height);
    }

    @Override
    public void update(float delta) {
        super.update(delta);
        background.update(delta);
    }

    @Override
    public void draw() {
        super.draw();
        background.draw(batch);

        stage.draw();
    }
}
