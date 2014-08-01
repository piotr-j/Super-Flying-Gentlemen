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

package io.piotrjastrzebski.sfg.game.objects;

import io.piotrjastrzebski.sfg.utils.Locator;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;

public class Parachute {
	private Sprite parachute;
	private boolean attached;
	private boolean enabled;
	
	public Parachute(){
		parachute = Locator.getAssets().getScaledSprite("parachute");
		// set origin to main cable of the chute
		parachute.setOrigin(parachute.getWidth()/2, 0);
		init(-100, 0);
	}
	
	public void init(float x, float y){
		// pos offset so its attached to correct place on the player
		parachute.setPosition(x-1.75f, y+0.5f);
		parachute.setRotation(10);
		attached = true;
		enabled = true;		
	}
	
	public void reset(){
		attached = true;
		enabled = false;
	}
	
	public void detach(){
		attached = false;
	}
	
	public void update(float delta, float x, float y){
		if (!enabled)
			return;
		if (attached){
			// follow player
			parachute.setPosition(x-1.75f, y+0.5f);
		} else {
			parachute.setPosition(
					parachute.getX()-2*delta,
					parachute.getY()+delta);
			// rotate a bit as it goes away
			final float angle = parachute.getRotation()+delta*10;
			if (angle < 45f)
				parachute.setRotation(angle);
		}
	}
	
	public void draw(Batch batch){
		if (!enabled)
			return;
		parachute.draw(batch);
	}
	
	public boolean isEnabled(){
		return enabled;
	}
}
