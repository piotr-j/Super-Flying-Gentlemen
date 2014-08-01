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

import io.piotrjastrzebski.sfg.screen.GameScreen;
import io.piotrjastrzebski.sfg.utils.Locator;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.esotericsoftware.spine.SkeletonRenderer;

public abstract class Part {
    final static float HEIGHT = GameScreen.VIEWPORT_HEIGHT/2-2;
    public enum Type {STATIC, SPIKE, HAMMER}

    private final SpikePool spikePool;
    private final HammerPool hammerPool;

	protected Body pillar;
	protected Body sensor;

	private boolean canExecute = false;
	protected boolean isExecuting = false;
	
    protected Tile[] tiles;
    protected TileCap cap;
	protected Type type;

    protected EndPoint endPoint;
    protected EndPoint nullEndPoint;

	public Part(Obstacle parent, SpikePool spikePool, HammerPool hammerPool) {
        this.spikePool = spikePool;
        this.hammerPool = hammerPool;
        final World world = Locator.getWorld();
        nullEndPoint = new NullEndPoint();
		tiles = new Tile[8];
		for (int i = 0; i < tiles.length; i++) {
			tiles[i] = new Tile();
		}
		cap = new TileCap();

		final PolygonShape boxShape = new PolygonShape();
		final BodyDef pillarBodyDef = new BodyDef();
		pillarBodyDef.type = BodyType.StaticBody;
		pillarBodyDef.position.set(0, 0);
		pillar = world.createBody(pillarBodyDef);
		boxShape.setAsBox(1.5f, HEIGHT);
		pillar.createFixture(boxShape, 0);

        final BodyDef sensorBodyDef = new BodyDef();
        sensorBodyDef.type = BodyType.StaticBody;
        sensorBodyDef.position.set(0, 0);
        sensor = world.createBody(pillarBodyDef);
        sensor.setUserData(parent);
        boxShape.setAsBox(1.25f, 0.05f);

        sensor.createFixture(boxShape, 0);
		// Clean up
		boxShape.dispose();

	}
	
	public void init(float x, float y, Type type){
		isExecuting = false;
		this.type = type;
        if (endPoint != null){
            endPoint.free();
            endPoint = null;
        }
		switch (type) {
		case SPIKE:
            endPoint = spikePool.obtain();
			canExecute = true;
			break;
		case HAMMER:
            endPoint = hammerPool.obtain();
			canExecute = true;
            break;
		case STATIC:
		default:
            endPoint = nullEndPoint;
			canExecute = false;
			break;
		}
		pillar.setTransform(x, y, 0);
		sensor.setTransform(x, -10, 0);
	}

	public void execute(boolean bloodied) {
		if (canExecute){
            endPoint.execute(bloodied);
			isExecuting = true;
		}
	}
	
	public void draw(Batch batch, SkeletonRenderer skeletonRenderer){
        endPoint.draw(batch, skeletonRenderer);
        cap.draw(batch);
        for (Tile tile : tiles) {
            tile.draw(batch);
        }
    }

	public void update(float delta){
        endPoint.update(delta);
        cap.update(delta);
    }
	
	/**
	 * Destroy underlying box2d bodies
	 */
	public void destroy(){
        if (endPoint!=null){
            endPoint.destroy();
        }
	}
}
