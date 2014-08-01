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

package io.piotrjastrzebski.sfg.ui;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;

import io.piotrjastrzebski.sfg.ActionListener;
import io.piotrjastrzebski.sfg.utils.Assets;
import io.piotrjastrzebski.sfg.utils.Locator;

public class GameOverDialog extends Dialog implements ActionListener {
    public  static enum RESULT {RESTART, LEADER_BOARDS, ACHIEVEMENTS, PREMIUM, RATE}
    private final Label scoreLabel;
    private final Label maxLabel;
    private final Group newRecordGroup;
    private Actor getPremiumTable;
    private Actor rateButton;
    private long lastMax = 0;
    private Assets assets;

    public GameOverDialog(Assets assets, long lastMax) {
        super(assets.getText(Assets.GAME_OVER), assets.getSkin());
        this.assets = assets;
        this.lastMax = lastMax;
        setMovable(false);

        final Color toxic = assets.getSkin().getColor("toxic");

        scoreLabel = new Label("", assets.getSkin());
        scoreLabel.setColor(toxic);
        maxLabel = new Label("", assets.getSkin());
        maxLabel.setColor(toxic);
        Label newRecordLabel = new Label(assets.getText(Assets.NEW_RECORD), assets.getSkin(), "toxic");
        newRecordGroup = new Group();

        newRecordGroup.setSize(newRecordLabel.getPrefWidth(), newRecordLabel.getPrefHeight());
        newRecordGroup.setOrigin(newRecordLabel.getPrefWidth() / 2, newRecordLabel.getPrefHeight() / 2);
        newRecordGroup.setTransform(true);
        newRecordGroup.addActor(newRecordLabel);

        final Table scores = new Table();
        scores.add(new Label(assets.getText(Assets.SCORE), assets.getSkin())).padRight(10);
        scores.add(scoreLabel);
        scores.add().expandX();
        scores.add(new Label(assets.getText(Assets.RECORD), assets.getSkin())).padRight(10);
        scores.add(maxLabel);

        final Table content = getContentTable();
        content.add(scores).pad(20, 20, 0, 20).expandX().fillX();
        content.row();

        final Table buttons = getButtonTable();
        buttons.add(createRetryButton()).pad(20);
        if (Gdx.app.getType() == Application.ApplicationType.Android) {
            buttons.row();
            if (!Locator.getSettings().isRated()) {
                rateButton = createRateButton();
                buttons.add(rateButton);
                buttons.row();
            }
            buttons.add(createLeaderButton());
            buttons.row();
            buttons.add(createAchievButton()).pad(20);
            buttons.row();
            getPremiumTable = createPremiumButton();
        }
    }

    public void updatePosition() {
        // can be null before first show()
        if (getStage() == null)
            return;
        setPosition(
                Math.round((getStage().getWidth() - getWidth()) / 2),
                Math.round((getStage().getHeight() - getHeight()) / 2));
    }

    public void updateScores(long score, long max, boolean premium) {
        scoreLabel.setText(""+score);
        maxLabel.setText(""+max);
        handleMaxScore(score, max);
        premiumStatus(premium);
    }

    public void removeRateButton() {
        if (rateButton != null){
            getButtonTable().removeActor(rateButton);
            pack();
            invalidate();
            rateButton = null;
        }
    }

    private void handleMaxScore(long score, long max) {
        final Table content = getContentTable();
        if (lastMax < max && max == score){
            content.add(newRecordGroup);
            newRecordGroup.addAction(
                    Actions.forever(Actions.sequence(
                            Actions.scaleTo(1.1f, 1.1f, 0.5f),
                            Actions.scaleTo(1f, 1f, 0.5f)
                    ))
            );
            lastMax = max;
        } else {
            content.removeActor(newRecordGroup);
        }
    }

    public void premiumStatus(boolean enabled) {
        final Table buttonTable = getButtonTable();
        buttonTable.removeActor(getPremiumTable);

        if (!enabled){
            buttonTable.add(getPremiumTable);
        }
        pack();
        invalidate();
    }

    @Override
    public void handleEvent(int id, Object data) {
        switch (id){
            case ActionListener.PREMIUM_ENABLED:
                premiumStatus(true);
                break;
            case ActionListener.PREMIUM_DISABLED:
                premiumStatus(false);
                break;
            default: break;
        }
    }

    private Actor createRateButton() {
        final GameButton rateButton = new GameButton(
                assets.getText(Assets.RATE),
                assets.getUIRegion("rate"),
                assets.getSkin());

        final Table rateTable = new Table();
        rateTable.add(rateButton).pad(0, 0, 20, 0);
        // dialog doesnt support contained keys, so we have to add both table and button
        setObject(rateButton, RESULT.RATE);
        setObject(rateTable, RESULT.RATE);
        return rateTable;
    }

    private GameButton createRetryButton() {
        // space padding cus positioning the image is pain in the ass
        final GameButton retryButton = new GameButton(
                assets.getText(Assets.RETRY),
                assets.getUIRegion("retry"),
                assets.getSkin());
        final Image image = retryButton.getImage();
        image.setOrigin(image.getDrawable().getMinWidth()/2, image.getDrawable().getMinHeight()/2);

        image.addAction(Actions.forever(Actions.rotateBy(-360, 5)));
        setObject(retryButton, RESULT.RESTART);
        return retryButton;
    }

    private GameButton createLeaderButton() {
        final GameButton leaderButton = new GameButton(
                assets.getText(Assets.LEADER_BOARDS),
                assets.getUIRegion("g_leaderboards"),
                assets.getSkin());
        setObject(leaderButton, RESULT.LEADER_BOARDS);
        return leaderButton;
    }

    private GameButton createAchievButton() {
        final GameButton achievButton = new GameButton(
                assets.getText(Assets.ACHIEVEMENTS),
                assets.getUIRegion("g_achievements"),
                assets.getSkin());
        setObject(achievButton, RESULT.ACHIEVEMENTS);
        return achievButton;
    }

    private Actor createPremiumButton() {
        // we use table container so padding doesnt get stuck in the dialog
        final Table premiumTable = new Table();
        final TextButton premiumButton = new TextButton(
                assets.getText(Assets.GET_PREMIUM),
                assets.getSkin(), "premium");
        premiumButton.getLabel().setFontScale(1.25f);
        premiumTable.add(premiumButton).pad(0, 0, 20, 0);
        // dialog doesnt support contained keys, so we have to add both table and button
        setObject(premiumButton, RESULT.PREMIUM);
        setObject(premiumTable, RESULT.PREMIUM);
        return premiumTable;
    }

    @Override
    public void hide() {
        super.hide();
        newRecordGroup.clearActions();
    }
}
