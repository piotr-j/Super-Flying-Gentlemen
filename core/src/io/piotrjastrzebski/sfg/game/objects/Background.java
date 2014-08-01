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
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.ExtendViewport;

import io.piotrjastrzebski.sfg.screen.GameScreen;
import io.piotrjastrzebski.sfg.utils.Assets;
import io.piotrjastrzebski.sfg.utils.Locator;
import io.piotrjastrzebski.sfg.utils.ParallaxCamera;

public class Background {
	private final static int NUM_STARS = 48;
	private int NUM_FRONT_BG = 3;
	private int NUM_MID_BG = 3;
	private final static float FRONT_SCALE = 0.5f;
	private final static float MID_SCALE = 0.25f;
    private final ExtendViewport viewPort;
    private Sprite moon;
	
	private float width = 0;
	private float height = 0;
	
	private Array<Star> stars;
	
	private TextureRegion bg_back;
	private TextureRegion bg_mid;
	private TextureRegion bg_front;
	
	private float mid_width;
	private float mid_height;
	private float front_width;
	private float front_height;
	
	ParallaxCamera camera;

	private float mid_offset;
	private float front_offset;
	private float offset;
	
	public Background() {
        camera = new ParallaxCamera(
                GameScreen.VIEWPORT_WIDTH, GameScreen.VIEWPORT_HEIGHT);
        viewPort = new ExtendViewport(
                GameScreen.VIEWPORT_WIDTH, GameScreen.VIEWPORT_HEIGHT, camera);
        camera.position.set(10, 16, 0);
        camera.update();
		stars = new Array<Background.Star>();

        final Assets assets = Locator.getAssets();
		bg_back = assets.getRegion("background_back");
		bg_mid = assets.getRegion("background_mid");
		mid_width = bg_mid.getRegionWidth()*GameScreen.BOX2D_TO_PIXEL;
		mid_height = bg_mid.getRegionHeight()*GameScreen.BOX2D_TO_PIXEL;
		
		bg_front = assets.getRegion("background_front");
		
		front_width =  bg_front.getRegionWidth()*GameScreen.BOX2D_TO_PIXEL;
		front_height = bg_front.getRegionHeight()*GameScreen.BOX2D_TO_PIXEL;
		
		moon = assets.getScaledSprite("moon");

		for (int i = 0; i < NUM_STARS; i++) {
			stars.add(new Star(assets, width, height));
		}
		offset = 0;
	}
	
	/**
	 * set offset from bottom from default position
	 */
	public void setGroundOffset(float offset){
		this.offset = offset;
	}

	public void updateViewport(int newWidth, int newHeight){
        viewPort.update(newWidth, newHeight);
		// +2 for full coverage
		width = viewPort.getWorldWidth();
		height = viewPort.getWorldHeight();
		// initial offsets
		reset();
	}

    public void reset(){
        front_offset = -width*0.5f-front_width;
        mid_offset = -width*0.5f-mid_width;
        NUM_FRONT_BG = (int) (width/front_width+2);
        NUM_MID_BG = (int) (width/mid_width+2);
        moon.setPosition(
                -width*0.5f+3,
                height*0.5f-9);

        for (int i = 0; i < NUM_STARS; i++) {
            stars.get(i).randomPos(width, height);
        }
    }

    public void zoom(float zoom){
        camera.zoom = zoom;
    }

	public void update(float delta){
		update(delta, camera.position.x);
	}
	
	public void update(float delta, float playerX) {
		for (int i = 0; i < NUM_STARS; i++) {
			stars.get(i).update(delta);
		}
		camera.position.x = playerX;

		if (mid_offset < playerX*MID_SCALE - mid_width-width*0.5f){
			mid_offset+=mid_width;
		}
		if (front_offset < playerX*FRONT_SCALE - front_width-width*0.5f){
			front_offset+=front_width;
		}
	}

	public void draw(Batch batch) {
		batch.setProjectionMatrix(camera.calculateParallaxMatrix(0, 0));
		batch.disableBlending();
		batch.begin();
		batch.draw(bg_back, -width*0.5f, -height*0.5f+offset, width, height-offset);
		batch.enableBlending();
		for (int i = 0; i < NUM_STARS; i++) {
			stars.get(i).draw(batch);
		}
		moon.draw(batch);

		batch.setProjectionMatrix(camera.calculateParallaxMatrix(MID_SCALE, 0));
		for (int i = 0; i < NUM_MID_BG; i++) {
            float xPos = i * mid_width + mid_offset;
            // dont draw if outside of bounds
            if (xPos <= (camera.position.x*MID_SCALE + width*0.5f))
                batch.draw(bg_mid,
                        xPos, -height*0.5f+offset,
                        mid_width, mid_height);
		}
		batch.setProjectionMatrix(camera.calculateParallaxMatrix(FRONT_SCALE, 0));
		for (int i = 0; i < NUM_FRONT_BG; i++) {
            float xPos = i * front_width + front_offset;
            // dont draw if outside of bounds
            if (xPos <= (camera.position.x*FRONT_SCALE + width*0.5f))
                batch.draw(bg_front,
                        xPos, -height*0.5f+offset-0.9f,
                        front_width, front_height);
		}

		batch.end();
	}

	static class Star {
		private final static String[] types = {"star_v1","star_v2","star_v3"};
		private Sprite star;
		private float scale;
		private float alpha;
		
		public Star(Assets assets, float width, float height){
			final int id = MathUtils.random(types.length-1);
			star = assets.getScaledSprite(types[id]);
			star.setOriginCenter();
			alpha = MathUtils.random(-1, 1);
			scale = MathUtils.random(0.1f, 1);
			star.setAlpha(alpha);
			star.setScale(MathUtils.random(0.5f, 1.5f));
			
			randomPos(width, height);
		}
		
		public void randomPos(float width, float height){
			star.setPosition(
					MathUtils.random(-width*0.5f, width*0.5f),
					MathUtils.random(-height*0.25f, height*0.5f));
		}
	
		public void update(float delta){
			alpha+=delta*scale;
			star.setColor(1,1,1,(MathUtils.sin(alpha)+1)*0.3f);
		}
		
		public void draw(Batch batch){
			star.draw(batch);
		}
	}
}
