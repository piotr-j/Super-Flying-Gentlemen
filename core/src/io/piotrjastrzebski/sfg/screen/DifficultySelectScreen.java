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

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import io.piotrjastrzebski.sfg.game.objects.Background;
import io.piotrjastrzebski.sfg.screen.inputhandlers.DifficultySelectInputHandler;
import io.piotrjastrzebski.sfg.utils.Assets;
import io.piotrjastrzebski.sfg.utils.Config;
import io.piotrjastrzebski.sfg.utils.Locator;

public class DifficultySelectScreen extends DefaultScreen {
    private final Background background;

    public DifficultySelectScreen() {
        super();
        Gdx.input.setInputProcessor(new DifficultySelectInputHandler(this).getIM());
        Gdx.input.setCatchBackKey(true);
        background = new Background();
        initUI();
    }

    private void initUI(){
        Table root = new Table();
        root.setFillParent(true);
        final Label screenLabel = new Label(assets.getText(Assets.DIFFICULTY_SELECT), assets.getSkin(), "default-large");
        screenLabel.setAlignment(Align.center);

        TextButton brutal = new TextButton(assets.getText(Assets.DIFFICULTY_BRUTAL), assets.getSkin());
        brutal.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                newGame(Config.Difficulty.BRUTAL);
                playButtonPressSound();
            }
        });
        brutal.setTransform(true);
        brutal.setOrigin(brutal.getPrefWidth()/2, brutal.getPrefHeight()/2);
        brutal.setScale(1.2f);
        TextButton veryHard = new TextButton(assets.getText(Assets.DIFFICULTY_VERY_HARD), assets.getSkin());
        veryHard.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                newGame(Config.Difficulty.VERY_HARD);
                playButtonPressSound();
            }
        });
        veryHard.setTransform(true);
        veryHard.setOrigin(veryHard.getPrefWidth()/2, veryHard.getPrefHeight()/2);
        veryHard.setScale(1.1f);
        TextButton hard = new TextButton(assets.getText(Assets.DIFFICULTY_HARD), assets.getSkin());
        hard.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                newGame(Config.Difficulty.HARD);
                playButtonPressSound();
            }
        });
        TextButton custom = new TextButton(assets.getText(Assets.DIFFICULTY_CUSTOM), assets.getSkin());
        custom.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Locator.getApp().setScreen(new CustomDifficultyScreen());
                playButtonPressSound();
            }
        });
        TextButton baby = new TextButton(assets.getText(Assets.DIFFICULTY_BABY), assets.getSkin());
        baby.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                newGame(Config.Difficulty.BABY);
                playButtonPressSound();
            }
        });
        baby.setTransform(true);
        baby.setOrigin(baby.getPrefWidth()/2, baby.getPrefHeight()/2);
        baby.setScale(0.75f);
        root.add(screenLabel).pad(20).top().expand();
        root.row();
        root.add(brutal).pad(30);
        root.row();
        root.add(veryHard).pad(25);
        root.row();
        root.add(hard).pad(20);
        root.row();
        root.add(custom).pad(20);
        root.row();
        root.add(baby).pad(20);
        root.row();
        root.add().expand();
        root.row();
        TextButton back = new TextButton(assets.getText(Assets.BACK), assets.getSkin(), "small");
        back.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                handleBack();
                playButtonPressSound();
            }
        });
        root.add(back).pad(20).left().bottom();
        stage.addActor(root);
    }

    private void newGame(Config.Difficulty difficulty){
        Locator.getConfig().setDifficulty(difficulty);
        Locator.getApp().setScreen(new GameScreen());
    }

    @Override
    public void draw() {
        super.draw();
        background.draw(batch);
        stage.draw();
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

    public void handleBack(){
        Locator.getApp().setScreen(new MainMenuScreen());
    }

    public void handleEnter() {
        newGame(Config.Difficulty.BRUTAL);
    }
}
