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
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Widget;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.BaseDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.utils.Array;
import com.esotericsoftware.spine.SkeletonRenderer;

import io.piotrjastrzebski.sfg.ActionListener;
import io.piotrjastrzebski.sfg.game.PlayerStats;
import io.piotrjastrzebski.sfg.game.objects.Background;
import io.piotrjastrzebski.sfg.game.objects.PlayerAnimation;
import io.piotrjastrzebski.sfg.screen.inputhandlers.SkinSelectInputHandler;
import io.piotrjastrzebski.sfg.utils.Assets;
import io.piotrjastrzebski.sfg.utils.Locator;

public class SkinSelectScreen extends DefaultScreen implements ActionListener {
    private final Background background;
    private final SkeletonRenderer skeletonRenderer;
    private final NinePatchDrawable select;
    private final BaseDrawable empty;
    private Array<Table> skinTables;
    private Array<Label> skinLabels;
    private Table contTable;

    public SkinSelectScreen() {
		super();
		Gdx.input.setInputProcessor(new SkinSelectInputHandler(this).getIM());
		Gdx.input.setCatchBackKey(true);
        actionResolver.sendScreenView("SkinSelectScreen");
        skeletonRenderer = new SkeletonRenderer();
        background = new Background();
        select = new NinePatchDrawable(assets.getUiAtlas().createPatch("button_glow"));
        empty = new BaseDrawable();
        actionResolver.registerActionListener(this);
		initUI();
	}
	
	private void initUI(){
		Table root = new Table();
        root.setFillParent(true);
        final Label screenLabel = new Label(assets.getText(Assets.OUTFITS_LABEL), assets.getSkin(), "default-large");
        screenLabel.setAlignment(Align.center);
        skinTables = new Array<Table>();
        skinLabels = new Array<Label>();
        contTable = new Table();
        final ScrollPane scrollPane = new ScrollPane(contTable, assets.getSkin());
        refreshUI();
        TextButton back = new TextButton(assets.getText(Assets.BACK), assets.getSkin(), "small");
        back.addListener(new ClickListener(){
        	@Override
        	public void clicked(InputEvent event, float x, float y) {
        		handleBack();
                playButtonPressSound();
        	}
        });
        root.add(screenLabel).pad(20);
        root.row();
        root.add(scrollPane).pad(20).expand().fill();
        root.row();
        root.add(back).pad(20).left().bottom();
        root.debug();
        stage.addActor(root);
	}

    private void refreshUI(){
        contTable.clearChildren();
        skinLabels.clear();
        skinTables.clear();

        PlayerStats.Skin current = playerStats.getPlayerSkin();

        final Array<PlayerStats.Skin> skins = playerStats.getSkins();
        int size = skins.size;
        // dont show last skin on non android
        if (!(Gdx.app.getType() == Application.ApplicationType.Android)) {
            size -= 1;
        }
        for (int i = 0; i < size; i++) {
            final PlayerStats.Skin skin = skins.get(i);
            final Table skinTable = createSkinItem(skin);
            final Label skinLabel = skinLabels.get(i);
            if (current.id == skin.id)
                skinTable.setBackground(select);
            skinTables.add(skinTable);
            skinTable.addListener(new ClickListener(){
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    if (skin.isUnlocked()){
                        if (skinTable.getBackground() == empty) {
                            clearSelect();
                            skinTable.setBackground(select);
                            playerStats.setPlayerSkin(skin);
                        } else {
                            skinTable.setBackground(empty);
                            playerStats.setDefaultPlayerSkin();
                            selectDefault();
                        }
                        skinLabel.clearActions();
                        skinLabel.addAction(Actions.sequence(
                                Actions.color(assets.getSkin().getColor("toxic"), 0.33f),
                                Actions.color(Color.WHITE, 0.33f)
                        ));
                        contTable.invalidateHierarchy();
                    } else {
                        if (skin.id == PlayerStats.SKIN_RUBY){
                            playerStats.buyPremium();
                        } else {
                            skinLabel.clearActions();
                            skinLabel.addAction(Actions.sequence(
                                    Actions.color(Color.RED, 0.25f),
                                    Actions.color(Color.WHITE, 0.25f)
                            ));
                        }
                    }
                    playButtonPressSound();
                }
            });
            contTable.add(skinTable).expandX().fillX();
            contTable.row();
        }
    }

    private void clearSelect(){
        for (Table skinTable : skinTables){
            skinTable.setBackground(empty);
        }
    }

    private void selectDefault(){
        skinTables.get(0).setBackground(select);

    }

    private Table createSkinItem(final PlayerStats.Skin skin){
        final Table table = new Table();
        table.setBackground(empty);
        final AnimationWidget animationWidget = new AnimationWidget(skin);
        table.add(animationWidget).left();

        Label label = new Label("", assets.getSkin());
        setLabelText(label, skin);
        skinLabels.add(label);
        table.add(label).pad(20).expandX();
        table.setTouchable(Touchable.enabled);
        return table;
    }

    private void setLabelText(Label label, PlayerStats.Skin skin){
        String text;
        if (skin.isUnlocked()){
            text = assets.getText(Assets.UNLOCKED);
        } else {
            if (skin.id == PlayerStats.SKIN_RUBY){
                text = assets.getText(Assets.LOCKED_PREMIUM);
            } else {
                text = assets.getText(Assets.LOCKED, skin.points);
            }
        }
        label.setText(text);
    }

	public void handleBack(){
		Locator.getApp().setScreen(new MainMenuScreen());
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

    @Override
    public void handleEvent(int id, Object data) {
        switch (id){
            case ActionListener.PREMIUM_ENABLED:
            case ActionListener.PREMIUM_DISABLED:
                refreshUI();
                break;
            default: break;
        }
    }

    @Override
    public void hide() {
        super.hide();
        actionResolver.unRegisterActionListener(this);
    }

    private class AnimationWidget extends Widget {
        private PlayerAnimation animation;

        public AnimationWidget(PlayerStats.Skin skin){
            animation = new PlayerAnimation();
            // make it bigger
            animation.getSkeleton().getRootBone().setScale(48);
            animation.setSkin(skin);
            animation.init(getX(), getY());
        }

        @Override
        public void draw(Batch batch, float parentAlpha) {
            validate();
            // fixed delta cus we dont care
            animation.update(1/60f, getX()+70, getY()+90);
            animation.draw(batch, skeletonRenderer);
        }

        public float getMinWidth () {
            return getPrefWidth();
        }

        public float getMinHeight () {
            return getPrefHeight();
        }

        public float getPrefWidth () {
            return 120;
        }

        public float getPrefHeight () {
            return 180;
        }

        public float getMaxWidth () {
            return 180;
        }

        public float getMaxHeight () {
            return 180;
        }
    }
}
