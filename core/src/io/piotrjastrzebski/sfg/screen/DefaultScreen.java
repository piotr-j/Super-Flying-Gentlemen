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

import io.piotrjastrzebski.sfg.ActionResolver;
import io.piotrjastrzebski.sfg.events.EventLoop;
import io.piotrjastrzebski.sfg.events.EventType;
import io.piotrjastrzebski.sfg.game.PlayerStats;
import io.piotrjastrzebski.sfg.utils.Assets;
import io.piotrjastrzebski.sfg.utils.Locator;
import io.piotrjastrzebski.sfg.utils.Settings;
import io.piotrjastrzebski.sfg.utils.SoundManager;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.ExtendViewport;

public class DefaultScreen implements Screen {
	protected Batch batch;
    protected Stage stage;
    protected Assets assets;
	protected EventLoop eventLoop;
    protected Settings settings;
    protected ActionResolver actionResolver;
    protected PlayerStats playerStats;

    protected boolean isDisposed = false;

	public DefaultScreen(){
		assets = Locator.getAssets();
	    eventLoop = Locator.getEvents();
        settings = Locator.getSettings();
        actionResolver = Locator.getActionResolver();
        playerStats = Locator.getPlayerStats();
		batch = new SpriteBatch();
		stage = new Stage(new ExtendViewport(768, 1280), batch);
    }

	public Stage getStage(){
		return stage;
	}

	@Override
	public void render(float delta) {
        if (isDisposed)
            return;
        draw();
        update(delta);
	}
	
	public void update(float delta){
		stage.act(delta);
	}
	
	public void draw(){
		Gdx.gl.glClearColor(0f, 0f, 0f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        // reset the batch color as scene2d actions dont cleanup
        batch.setColor(1,1,1,1);
    }

    protected void playButtonPressSound(){
        eventLoop.queueEvent(EventType.PLAY_SOUND, SoundManager.BUTTON_PRESS);
    }

	@Override
	public void resize (int width, int height) {
	    stage.getViewport().update(width, height, true);
	}

	@Override
	public void show() {
		
	}

	@Override
	public void hide() {
		
	}

	@Override
	public void pause() {
		
	}

	@Override
	public void resume() {
		
	}

	@Override
	public void dispose() {
        if (isDisposed)
            return;
        isDisposed = true;
        batch.dispose();
        stage.dispose();
	}
}
