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

import box2dLight.PointLight;
import box2dLight.RayHandler;
import io.piotrjastrzebski.sfg.utils.Locator;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Pool.Poolable;

public class ExplosionLight implements Poolable, Position {
	boolean isComplete;
	private PointLight pointLight;
	private float timer;
	private float expTime;
	
	public ExplosionLight(){
		isComplete = true;
		pointLight = new PointLight(Locator.getRayHandler(), 16, Color.WHITE, 1, -100, -100);
		pointLight.setColor(1, 0.8f, 0.3f, 1);
		pointLight.setActive(false);
	}
	
	@Override
	public void reset() {
		isComplete = true;
        pointLight.setPosition(-100, -100);
        pointLight.setActive(false);
	}
	
	public void init(float x, float y){
		isComplete = false;
		timer = 0;
		expTime = 1;
		pointLight.setPosition(x, y);
		pointLight.setActive(true);
	}
	
	public void update(float delta){
		if (isComplete)
			return;
		timer+=delta*2;
		if (timer <= expTime){
			pointLight.setColor(1-timer, 0.8f*(1-timer), 0.3f*(1-timer), 1-timer);
			pointLight.setDistance(1+timer*15);
		} else {
			reset();
		}
	}

	public boolean isComplete() {
		return isComplete;
	}

    @Override
    public Vector2 getPos() {
        return pointLight.getPosition();
    }
}
