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

import io.piotrjastrzebski.sfg.utils.Assets;
import io.piotrjastrzebski.sfg.utils.Locator;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;

public class Tile {	
	// number of sides, unless we make hexagonal tiles this wont ever change
	private final static int SIDES = 4;
	// available types of tile
	private static final String[][] types = {
		{"plate_v1_tl", "plate_v2_tl",
		 "beam_v1_tl", "beam_v2_tl", "beam_v3_tl", "beam_v4_tl", "beam_v5_tl"},
		{"plate_v1_tr", "plate_v2_tr",
		 "beam_v1_tr", "beam_v2_tr", "beam_v3_tr", "beam_v4_tr", "beam_v5_tr"},
		{"plate_v1_bl", "plate_v2_bl",
		 "beam_v1_bl", "beam_v2_bl", "beam_v3_bl", "beam_v4_bl", "beam_v5_bl"},
		{"plate_v1_br", "plate_v2_br",
		 "beam_v1_br", "beam_v2_br", "beam_v3_br", "beam_v4_br", "beam_v5_br"}
	};
	private float x = 0;
	private float y = 0;
	private Assets assets;
	private TextureRegion[] regions;
	private boolean isInit;
	
	public Tile(){
		assets = Locator.getAssets();
		regions = new TextureRegion[4];
		isInit = false;
	}
	
	public void init(float x, float y){
		this.x = x;
		this.y = y;
		isInit = true;
		// full plate 30% of the time, else fully random
		// cap is basically how far into the types array we can go, inclusive
		int cap = types.length-1;
		if (MathUtils.random() >= 0.7f){
			cap = 1;
		}
		for (int i = 0; i < SIDES; i++) {
			// 0 and 1 are plates
			int type = MathUtils.random(cap);
			regions[i] = assets.getRegion(types[i][type]);
		}
	}
	
	public void draw(Batch batch){
		// dont draw if outside
		if(!isInit || y < -2 || y > 37)
			return;
		// 1.5f is the size of one tile
		batch.draw(regions[0], x-1.5f, 	y-1.5f, 1.5f, 1.5f);
		batch.draw(regions[1], x, 		y-1.5f, 1.5f, 1.5f);
		batch.draw(regions[2], x-1.5f, 	y-3, 	1.5f, 1.5f);
		batch.draw(regions[3], x, 		y-3, 	1.5f, 1.5f);
	}

}
