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

package io.piotrjastrzebski.sfg.game;

import io.piotrjastrzebski.sfg.events.EventLoop;
import io.piotrjastrzebski.sfg.events.EventType;
import io.piotrjastrzebski.sfg.game.objects.obstacles.Obstacle;
import io.piotrjastrzebski.sfg.game.objects.obstacles.Part;
import io.piotrjastrzebski.sfg.screen.GameScreen;
import io.piotrjastrzebski.sfg.utils.Config;
import io.piotrjastrzebski.sfg.utils.Locator;
import io.piotrjastrzebski.sfg.utils.Range;
import io.piotrjastrzebski.sfg.utils.Utils;
import io.piotrjastrzebski.sfg.utils.pools.CeilingPool;
import io.piotrjastrzebski.sfg.utils.pools.GroundPool;
import io.piotrjastrzebski.sfg.utils.pools.ObstaclePool;
import io.piotrjastrzebski.sfg.utils.pools.PickupPool;
import io.piotrjastrzebski.sfg.utils.pools.PoolUtils;
import io.piotrjastrzebski.sfg.utils.pools.CeilingPool.PooledCeiling;
import io.piotrjastrzebski.sfg.utils.pools.GroundPool.PooledGround;
import io.piotrjastrzebski.sfg.utils.pools.ObstaclePool.PooledObstacle;
import io.piotrjastrzebski.sfg.utils.pools.PickupPool.PooledPickup;
import io.piotrjastrzebski.sfg.utils.pools.WallPool.PooledWall;
import io.piotrjastrzebski.sfg.utils.pools.WallPool;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.esotericsoftware.spine.SkeletonRenderer;

public class GameWorld {
    private final int PICKUP_SPAWN_DISTANCE;
    private final float PICKUP_SPAWN_CHANGE;
    private final float BOOST_REFILL_TIME;
    private final Range<Float> OBSTACLE_GAP;
	private GroundPool groundPool;
	private Array<PooledGround> ground;
	private CeilingPool ceilingPool;
	private Array<PooledCeiling> ceilings;
	private ObstaclePool obstaclePool;
	private Array<PooledObstacle> obstacles;
	private PickupPool pickupPool;
	private Array<PooledPickup> pickups;
    private WallPool wallPool;
    private Array<PooledWall> walls;
	
	private float lastCleanup;
	
	private float widthOffset;
	private int pickupDelay;
	private Range<Float> obstRange;
	private EventLoop events;
    private float worldWidth;
    private float worldHeight;
    private int obstSinceLastBoost;

    //TODO split the class or something
	public GameWorld(){
        events = Locator.getEvents();
        final Config config = Locator.getConfig();
        obstRange = config.getObstacleDistance();
        groundPool = new GroundPool();
        ground = new Array<PooledGround>();
        ceilingPool = new CeilingPool();
        ceilings = new Array<PooledCeiling>();
        obstaclePool = new ObstaclePool();
        obstacles = new Array<PooledObstacle>();
        pickupPool = new PickupPool();
        pickups = new Array<PooledPickup>();
        wallPool = new WallPool(config.getObstacleGapSize().max);
        walls = new Array<PooledWall>();

        worldWidth = GameScreen.VIEWPORT_WIDTH;
        worldHeight = GameScreen.VIEWPORT_HEIGHT;
        PICKUP_SPAWN_CHANGE = config.getPickupSpawnChance();
        PICKUP_SPAWN_DISTANCE = config.getPickupMinSpawnDistance();
        OBSTACLE_GAP = config.getObstacleGapSize();
        BOOST_REFILL_TIME = config.getPlayerDashDelay();
	}
	
	public void init(float camX){
		reset();
		for (int i = 0; i < 5; i++) {
			populate(camX);
		}
	}
	
	private void reset(){
		PoolUtils.reset(ground);
		PoolUtils.reset(ceilings);
		PoolUtils.reset(obstacles);
		PoolUtils.reset(pickups);
		PoolUtils.reset(walls);
		lastCleanup = 0;
        pickupDelay = 0;
        obstSinceLastBoost = 0;
	}
	
	private void cleanup(float camX){
		final float outside = camX - widthOffset*2;
		if (lastCleanup + widthOffset > outside )
			return;
		lastCleanup = outside;
		PoolUtils.clean(ground, outside);
		PoolUtils.clean(ceilings, outside);
		PoolUtils.clean(obstacles, outside);
		PoolUtils.clean(pickups, outside);
		PoolUtils.clean(walls, outside);
	}
	
	/**
	 * Generates new floor, ceiling and obstacles as needed
	 */
	private void populate(float camX){
		// obstacles
		final float obstacleRight = camX+widthOffset+Obstacle.WIDTH+5;

        // we just started
		if (obstacles.size == 0){
			populateInit(obstacleRight);
		}

        final Obstacle o = obstacles.get(obstacles.size - 1);
        float lastX = o.getX();

		if (lastX <= obstacleRight){
			populateNext(lastX, o.getTopY(), o.getBotY());
        }
	}

    private final static float MIN_Y = 5;
    private final static float MAX_Y = 31;

	private void populateInit(float lastX){
		final PooledObstacle o = obstaclePool.obtain();
        o.updateViewPort(worldWidth, worldHeight);
        // first one biggest possible gap
        float gap = OBSTACLE_GAP.max;
        // first one in middle
        float centerY = (MIN_Y + MAX_Y)/2;
        float botY = centerY - gap*0.5f;
        float topY = centerY + gap*0.5f;

        // 0.5 so its not flush with ground / ceiling
		o.init(lastX, botY + 0.5f, topY - 0.5f);
        obstacles.add(o);

		// ground and ceiling going behind
		for (int i = 1; i <= 5; i++) {
			final PooledGround g = groundPool.obtain();
			// show support on last ground panel
            g.updateViewPort(worldWidth, worldHeight);
			g.init(lastX-i*15-3, MIN_Y,
					18,
                    false,
                    i==1 && botY > MIN_Y+1
            );
			ground.add(g);
			
			final PooledCeiling c = ceilingPool.obtain();
            c.updateViewPort(worldWidth, worldHeight);
            // +6 above top of the screen
			c.init(lastX-i*15-3, MAX_Y+6,
					18,
					false,
                    false);
			ceilings.add(c);
		}
	}
	
	private void populateNext(float lastX, float lastTopY, float lastBotY){
        pickupDelay++;
        float distance = Utils.randomRange(obstRange);

		final PooledObstacle o = obstaclePool.obtain();
        o.updateViewPort(worldWidth, worldHeight);
        float gap = Utils.randomRange(OBSTACLE_GAP);

        float centerY = MathUtils.round(MathUtils.random(MIN_Y+gap*0.5f, MAX_Y-gap*0.5f));
        float botY = centerY-gap*0.5f;
        float topY = centerY+gap*0.5f;
        // 0.5 so its not flush with ground / ceiling
        o.init(lastX+distance, botY + 0.5f, topY - 0.5f);
        obstacles.add(o);

        float groundMaxY = Math.min(lastBotY, botY);
        float groundY = MathUtils.round(MathUtils.random(MIN_Y, groundMaxY));

		final PooledGround g = groundPool.obtain();
        g.updateViewPort(worldWidth, worldHeight);
		g.init(lastX, groundY,
				distance,
				lastBotY - 2 >= groundY,
                botY - 2 >= groundY
        );
		ground.add(g);

        float ceilingMinY = Math.max(lastTopY, topY);
        float ceilingY = MathUtils.round(MathUtils.random(MAX_Y, ceilingMinY));

		final PooledCeiling c = ceilingPool.obtain();
        c.updateViewPort(worldWidth, worldHeight);
		c.init(lastX, ceilingY,
				distance,
				lastTopY + 2 <= ceilingY,
                topY + 2 <= ceilingY
        );
		ceilings.add(c);

        // wait at least one obstacle per BOOST_REFILL_TIME and 25% chance after that
        if (obstSinceLastBoost > BOOST_REFILL_TIME && MathUtils.random() > 0.5f) {
            obstSinceLastBoost = 0;
            // 50/50 for either triple obstacle or wall
            if (MathUtils.randomBoolean() && o.getBotType() == Part.Type.STATIC) {
                // create a wall for the player to destroy with boost
                final PooledWall w = wallPool.obtain();
                w.init(lastX + distance, botY, gap);
                walls.add(w);
            } else {
                // another 2 in a row for boost
                o.disableScore();

                PooledObstacle o2 = obstaclePool.obtain();
                o2.updateViewPort(worldWidth, worldHeight);
                o2.init(lastX + distance + 4, botY + 0.5f, topY - 0.5f, o.getBotType(), o.getTopType());
                o2.disableScore();
                obstacles.add(o2);

                o2 = obstaclePool.obtain();
                o2.updateViewPort(worldWidth, worldHeight);
                o2.init(lastX + distance + 8, botY + 0.5f, topY - 0.5f, o.getBotType(), o.getTopType());
                obstacles.add(o2);

                final PooledGround g2 = groundPool.obtain();
                g2.updateViewPort(worldWidth, worldHeight);
                // 3x3 + 2
                g2.init(lastX + distance - 1.5f, groundY,
                        10,
                        false,
                        false
                );
                ground.add(g2);
            }
            events.queueEvent(EventType.SHOW_BOOST_TUT, o);
        } else {
            obstSinceLastBoost += 1;
        }
		// 1/4 chance to spawn a pickup and at least 3 obstacles since last
		if (MathUtils.random() <= PICKUP_SPAWN_CHANGE && pickupDelay > PICKUP_SPAWN_DISTANCE){
			// 3 for width of obstacles
			final float pickupOffset = 5 + (int) (MathUtils.random()*(distance-10));
			PooledPickup p = pickupPool.obtain();
			float y = MathUtils.random(groundY+2.5f, ceilingY-2.5f);
            p.init(lastX+pickupOffset, y);
			pickups.add(p);
            pickupDelay = 0;
		}
	}
	
	public void update(float delta, float camX){
		cleanup(camX);
		populate(camX);
		
		for (int i = 0; i < obstacles.size; i++) {
			obstacles.get(i).update(delta, camX);
		}
        for (int i = 0; i < ceilings.size; i++) {
            ceilings.get(i).update(delta, camX);
        }
        for (int i = 0; i < ground.size; i++) {
            ground.get(i).update(delta, camX);
        }
        for (int i = 0; i < walls.size; i++) {
            walls.get(i).update(delta);
        }
        for (final PooledPickup p : pickups) {
            p.update(delta);
            if (p.isExploded()) {
                events.queueEvent(EventType.SPAWN_EXPLOSION, p.getPos());
                p.free();
                pickups.removeValue(p, true);
            }
        }
	}
	
	public void draw(Batch batch, SkeletonRenderer skeletonRenderer){
		for (int i = 0; i < ceilings.size; i++) {
			ceilings.get(i).draw(batch);
		}
		for (int i = 0; i < ground.size; i++) {
			ground.get(i).draw(batch);
		}
		for (int i = 0; i < obstacles.size; i++) {
			obstacles.get(i).draw(batch, skeletonRenderer);
		}
		for (int i = 0; i < pickups.size; i++) {
			pickups.get(i).draw(batch, skeletonRenderer);
		}
        for (int i = 0; i < walls.size; i++) {
            walls.get(i).draw(batch);
        }
	}

	public void updateViewport(ExtendViewport gameViewPort) {
        worldWidth = gameViewPort.getWorldWidth();
        worldHeight = gameViewPort.getWorldHeight();

		widthOffset = Math.max(
                worldWidth/2,
                worldHeight/2
		);

        for (int i = 0; i < ceilings.size; i++) {
            ceilings.get(i).updateViewPort(worldWidth, worldHeight);
        }
        for (int i = 0; i < ground.size; i++) {
            ground.get(i).updateViewPort(worldWidth, worldHeight);
        }
        for (int i = 0; i < obstacles.size; i++) {
            obstacles.get(i).updateViewPort(worldWidth, worldHeight);
        }
	}
}
