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

package io.piotrjastrzebski.sfg.game.objects.obstacles;

import io.piotrjastrzebski.sfg.utils.Locator;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.MathUtils;

public class TileCap {
	private boolean isInit;
	private float y;
	private float rotateSpeed = 0;
	private Sprite fan;
	private Sprite cap;
	
	public TileCap(){
		cap = Locator.getAssets().getScaledSprite("obst_cap");
		fan = Locator.getAssets().getScaledSprite("fan");
		fan.setOriginCenter();
		isInit = false;
	}
	
	public void init(float x, float y, boolean flipY){
        if (!flipY){
            // -3 to account for flip as it flips about origin in the corner
            y -= 3;
        }
        this.y = y;
        isInit = true;
		// 0.1 to 0.5 rotations per second
		rotateSpeed = MathUtils.random(0.1f, 0.5f);
		fan.setPosition(x-1.2f, y+0.3f);
		cap.setPosition(x-1.5f, y);
		cap.setFlip(false, flipY);
		
	}
	
	public void update(float delta){
		fan.rotate(-rotateSpeed*delta*360);
	}
	
	public void draw(Batch batch){
		// dont draw if outside
		if(!isInit || y <= -2 || y >= 35)
			return;
		cap.draw(batch);
		fan.draw(batch);
	}

}
