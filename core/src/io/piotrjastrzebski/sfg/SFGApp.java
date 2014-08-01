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

package io.piotrjastrzebski.sfg;

import io.piotrjastrzebski.sfg.events.EventLoop;
import io.piotrjastrzebski.sfg.game.PlayerStats;
import io.piotrjastrzebski.sfg.screen.SplashScreen;
import io.piotrjastrzebski.sfg.utils.Assets;
import io.piotrjastrzebski.sfg.utils.Locator;
import io.piotrjastrzebski.sfg.utils.Settings;
import io.piotrjastrzebski.sfg.utils.SoundManager;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.FPSLogger;

public class SFGApp extends Game {
	public final static boolean DEBUG = true;
	public final static boolean DEBUG_FPS = false;
	// if debug draw of box2d is enabled
	public final static boolean DEBUG_BOX2D = false;
	public final static String TAG = "SFG";
    public static final String PREFS = "SFG_PREFS";
    private PlayerStats playerStats;
    private Assets assets;
    private SoundManager soundManager;
	private FPSLogger fps;
    private ActionResolver actionResolver;
	private Settings settings;
    private EventLoop eventLoop;

	public SFGApp(ActionResolver actionResolver){
        this.actionResolver = actionResolver;
        Locator.provideApp(this);
        fps = new FPSLogger();
        eventLoop = new EventLoop();
        Locator.provideEvents(eventLoop);
        assets = new Assets();
        Locator.provideAssets(assets);
        soundManager = assets.getSoundManager();
        Locator.provideSounds(soundManager);
	}

    public void init() {
        actionResolver.init();
        Locator.provideActionResolver(actionResolver);
        playerStats = new PlayerStats(actionResolver);
        Locator.providePlayerStats(playerStats);
        settings = new Settings();
        Locator.provideSettings(settings);
    }

    @Override
    public void resume() {
        super.resume();
        initLocator();
    }

    private void initLocator(){
        Locator.provideApp(this);
        Locator.provideActionResolver(actionResolver);
        Locator.provideAssets(assets);
        Locator.provideSettings(settings);
        Locator.provideSounds(soundManager);
        Locator.providePlayerStats(playerStats);
        Locator.provideEvents(eventLoop);
    }

    @Override
	public void create () {
		setScreen(new SplashScreen());
	}

	@Override
	public void render () {
		super.render();
		if (DEBUG_FPS) 
			fps.log();
        eventLoop.update();
	}

    @Override
    public void setScreen(Screen screen) {
        Screen prev = getScreen();
        super.setScreen(screen);
        if (prev != null)
            prev.dispose();
    }

    @Override
	public void dispose() {
		super.dispose();
		getScreen().dispose();
		assets.dispose();
        Locator.dispose();
	}
}
