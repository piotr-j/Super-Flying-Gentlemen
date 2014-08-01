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

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Texture;

import io.piotrjastrzebski.sfg.utils.Locator;

public class SplashScreen extends DefaultScreen {
	private final static float SPLASH_TIME = 1.5f; // delay
	
	Texture splash;
	private float timer = 0;
	public SplashScreen(){
		super();
		splash = new Texture("data/splash_screen.png");
        Locator.getApp().init();
    }
	
	@Override
	public void update(float delta) {
		timer +=delta;
		// change to main menu screen when all assets are loaded
		if (assets.update() && timer > SPLASH_TIME){
			Locator.getApp().setScreen(new MainMenuScreen());
		}
	}
	
	@Override
	public void draw() {
		super.draw();
		final Camera cam = stage.getCamera();
		final float splashHeight = cam.viewportHeight;
		final float ratio = splashHeight/splash.getHeight();
		final float splashWidth = splash.getWidth()*ratio;
		batch.setProjectionMatrix(cam.combined);
		batch.begin();
		batch.draw(splash,
				cam.viewportWidth/2-splashWidth/2,
				0,
				splashWidth,
				splashHeight);
		batch.end();
	}
	
	@Override
	public void dispose() {
		super.dispose();
		splash.dispose();
	}
}
