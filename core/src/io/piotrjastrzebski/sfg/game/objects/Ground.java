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

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Pool.Poolable;

import io.piotrjastrzebski.sfg.utils.Assets;
import io.piotrjastrzebski.sfg.utils.Box2dUtils;
import io.piotrjastrzebski.sfg.utils.Locator;

public class Ground implements Poolable, Position, ViewPortUpdate {
	private static final String[] floor_names = {
		"floor_v1", "floor_v2", "floor_v3"
	};
	private static final String[] ground_names = {
		"ground_v1", "ground_v2", "ground_v3"
	};

	private final static float WIDTH = 10;
	private final static float HEIGHT = 4;
	private Body startBody;
	private Body endBody;
	private World world;
	private TextureRegion support_bl, support_br;
	private TextureRegion floor[] = new TextureRegion[floor_names.length];
	private TextureRegion ground[] = new TextureRegion[ground_names.length];
	private int[] floorIds = new int[10];
	private int[][] groundIds = new int[6][10];
	private float viewHalfWidth;
	private float viewHalfHeight;
	private float width;
	private boolean drawLSupport;
	private boolean drawRSupport;
    private boolean init = false;

    private Vector2 pos;

	public Ground() {
		world = Locator.getWorld();
        pos = new Vector2();
		startBody = Box2dUtils.createBox(-100, 0, WIDTH, HEIGHT);
		endBody = Box2dUtils.createBox(-100, 0, WIDTH, HEIGHT);

        final Assets assets = Locator.getAssets();
		support_bl = assets.getRegion("support_v1_bl");
		support_br = assets.getRegion("support_v1_br");
		
		for (int i = 0; i < floor_names.length; i++) {
			floor[i] = assets.getRegion(floor_names[i]);
		}
		
		for (int i = 0; i < ground_names.length; i++) {
			ground[i] = assets.getRegion(ground_names[i]);
		}
	}
	
	/**
	 * Init the Ground and set the position
	 * This must be called outside of world.step()
	 */
	public void init(float x, float y, float width, boolean drawLSupport, boolean drawRSupport){
        // so top of ground is at given pos
        y-=HEIGHT/2;
        pos.set(x, y);
        init = true;
		this.width = width;
		this.drawLSupport = drawLSupport;
		this.drawRSupport = drawRSupport;
		
		startBody.setTransform(x+WIDTH/2+1.5f, y, 0);
		endBody.setTransform(x+width-WIDTH/2-1.5f, y, 0);
		
		for (int i = 0; i < floorIds.length-1; i++) {
			if (i*3 < width-3){
				floorIds[i] = MathUtils.random(floor.length-1);
			} else {
				floorIds[i] = -1;
			}
		}
		for (int i = 0; i < groundIds.length; i++) {
			final int[] ids = groundIds[i];
			for (int j = 0; j < ids.length; j++) {
				if (j*3 < width-3 && y -i*3 >= -1){
					ids[j] = MathUtils.random(ground.length-1);
				} else {
					ids[j] = -1;
				}
			}	
		}
	}

    float camX;
    public void update(float delta, float camX){
        this.camX = camX;
    }

    public void draw(Batch batch){
        if (!init)
            return;
        if (pos.x > camX + viewHalfWidth || pos.x < camX- viewHalfWidth -width)
            return;
		if (drawLSupport)
			batch.draw(support_br, pos.x+1.5f, pos.y+2f, 1.5f, 1.5f);
		if (drawRSupport)
			batch.draw(support_bl, pos.x+width-3f, pos.y+2f, 1.5f, 1.5f);
		
		for (int i = 0; i < floorIds.length; i++) {
			if (floorIds[i] == -1)
				break;
			batch.draw(floor[floorIds[i]], pos.x+1.5f+i*3, pos.y-0.5f, 3, 3);
		}
		for (int i = 0; i < groundIds.length; i++) {
			final int[] ids = groundIds[i];
			for (int j = 0; j < ids.length; j++) {
				if (ids[j] == -1)
					break;
				batch.draw(ground[ids[j]], pos.x+1.5f+j*3, pos.y-3.5f-i*3, 3, 3);
			}	
		}
	}

    @Override
    public void updateViewPort(float width, float height){
        viewHalfWidth = width*0.5f;
        viewHalfHeight = height*0.5f;
    }

	@Override
	public void reset() {
		startBody.setTransform(-100, 0, 0);
		endBody.setTransform(-100, 0, 0);
        init = false;
	}

    @Override
    public Vector2 getPos() {
        return pos;
    }
}
