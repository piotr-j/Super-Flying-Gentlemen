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

public class Ceiling implements Poolable, Position, ViewPortUpdate {
	private final static float WIDTH = 10;
    private final static float HEIGHT = 4;

    private static final String[] ceiling_names = {
		"ceiling_v1", "ceiling_v2", "ceiling_v3"
	};
	private Body startBody;
	private Body endBody;	private World world;
	private TextureRegion support_tl, support_tr;
	private TextureRegion ceiling[] = new TextureRegion[ceiling_names.length];
	private int[] ceilingIds = new int[10];

	private float width;
	private boolean drawRSupport;
	private boolean drawLSupport;
    private boolean init = false;

    private Vector2 pos;

    private float viewHalfWidth;
    private float viewHeight;

	public Ceiling() {
		this.world = Locator.getWorld();
        pos = new Vector2();
		startBody = Box2dUtils.createBox(-100, 0, WIDTH, 4);
		endBody = Box2dUtils.createBox(-100, 0, WIDTH, 4);
        final Assets assets = Locator.getAssets();
		support_tl = assets.getRegion("support_v1_tl");
		support_tr = assets.getRegion("support_v1_tr");
		
		for (int i = 0; i < ceiling_names.length; i++) {
			ceiling[i] = assets.getRegion(ceiling_names[i]);
		}
	}
	
	/**
	 * Init the Ceiling and set the position
	 * This must be called outside of world.step()
	 */
	public void init(float x, float y, float width,  boolean drawLSupport, boolean drawRSupport) {
        // so bottom of ceiling is at given pos
        init = true;
        y+=HEIGHT/2;
        pos.set(x, y);
		this.width = width;
		this.drawRSupport = drawRSupport;
		this.drawLSupport = drawLSupport;
		
		startBody.setTransform(x+WIDTH/2+1.5f, y, 0);
		endBody.setTransform(x+width-WIDTH/2-1.5f, y, 0);
		for (int i = 0; i < ceilingIds.length-1; i++) {
			if (i*3 < width-3){
				ceilingIds[i] = MathUtils.random(ceiling.length-1);
			} else {
				ceilingIds[i] = -1;
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
        if (pos.x > camX + viewHalfWidth || pos.x < camX- viewHalfWidth - width)
            return;
        if (pos.y > viewHeight+2)
            return;

		// y-5 for height of support and ceiling
		if (drawRSupport)
			batch.draw(support_tl, pos.x+width-3f, pos.y-3.5f, 1.5f, 1.5f);
		if (drawLSupport)
			batch.draw(support_tr, pos.x+1.5f, pos.y-3.5f, 1.5f, 1.5f);
		float posX;
        float posY;
		for (int i = 0; i < ceilingIds.length; i++) {
			if (ceilingIds[i] == -1)
				break;
            posX = pos.x+1.5f+i*3;
            posY =  pos.y-2f;
			batch.draw(ceiling[ceilingIds[i]], posX, posY, 3, 3);
		}
	}

    @Override
    public void updateViewPort(float width, float height){
        viewHalfWidth = width*0.5f;
        viewHeight = height;
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
