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
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import io.piotrjastrzebski.sfg.game.objects.Background;
import io.piotrjastrzebski.sfg.screen.inputhandlers.AboutInputHandler;
import io.piotrjastrzebski.sfg.utils.Assets;
import io.piotrjastrzebski.sfg.utils.Locator;

public class AboutScreen extends DefaultScreen {
    private final Background background;

    public AboutScreen() {
		super();
		Gdx.input.setInputProcessor(new AboutInputHandler(this).getIM());
		Gdx.input.setCatchBackKey(true);
        actionResolver.sendScreenView("AboutScreen");
        background = new Background();
		initUI();
	}
	
	private void initUI(){
		Table root = new Table();
        root.setFillParent(true);

        Table labelContainer = new Table();
        final Label aboutLabel = new Label(assets.getText(Assets.ABOUT), assets.getSkin(), "default-large");
        aboutLabel.setAlignment(Align.center);
        final Label aboutText1 = new Label(assets.getText(Assets.ABOUT_TEXT_1), assets.getSkin());
        aboutText1.setAlignment(Align.center);
        final Label aboutText2 = new Label(assets.getText(Assets.ABOUT_TEXT_2), assets.getSkin());
        aboutText2.setAlignment(Align.center);
        final Label aboutText3 = new Label(assets.getText(Assets.ABOUT_TEXT_3), assets.getSkin());
        aboutText3.setAlignment(Align.center);

        final Label wwwPiotrj = new Label(assets.getText(Assets.ABOUT_TEXT_WWW_PIOTRJ), assets.getSkin());
        wwwPiotrj.setAlignment(Align.center);
        wwwPiotrj.setColor(assets.getSkin().getColor("premium"));
        wwwPiotrj.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                actionResolver.openWebsite(assets.getText(Assets.ABOUT_TEXT_WWW_PIOTRJ));
            }
        });

        final Label wwwLibgdx = new Label(assets.getText(Assets.ABOUT_TEXT_WWW_LIBGDX), assets.getSkin());
        wwwLibgdx.setAlignment(Align.center);
        wwwLibgdx.setColor(assets.getSkin().getColor("premium"));
        wwwLibgdx.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                actionResolver.openWebsite(assets.getText(Assets.ABOUT_TEXT_WWW_LIBGDX));
            }
        });

        final Label wwwSpine = new Label(assets.getText(Assets.ABOUT_TEXT_WWW_SPINE), assets.getSkin());
        wwwSpine.setAlignment(Align.center);
        wwwSpine.setColor(assets.getSkin().getColor("premium"));
        wwwSpine.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                actionResolver.openWebsite(assets.getText(Assets.ABOUT_TEXT_WWW_SPINE));
            }
        });

        labelContainer.add(aboutText1);
        labelContainer.row();
        labelContainer.add(wwwPiotrj).pad(0,0,24,0);
        labelContainer.row();
        labelContainer.add(aboutText2);
        labelContainer.row();
        labelContainer.add(wwwLibgdx).pad(0,0,24,0);
        labelContainer.row();
        labelContainer.add(aboutText3);
        labelContainer.row();
        labelContainer.add(wwwSpine);

        final ScrollPane scrollPane = new ScrollPane(labelContainer, assets.getSkin());

        TextButton back = new TextButton(assets.getText(Assets.BACK), assets.getSkin(), "small");
        back.addListener(new ClickListener(){
        	@Override
        	public void clicked(InputEvent event, float x, float y) {
        		handleBack();
                playButtonPressSound();
        	}
        });
        root.add(aboutLabel).pad(20);
        root.row();
        root.add(scrollPane).pad(20).expand();
        root.row();
        root.add(back).pad(20).left().bottom();
        stage.addActor(root);
	}
	
	public void handleBack(){
		Locator.getApp().setScreen(new MainMenuScreen());
	}
	
	@Override
	public void draw() {
		super.draw();
	    background.draw(batch);
		stage.draw();
//		Table.drawDebug(stage);
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
}
