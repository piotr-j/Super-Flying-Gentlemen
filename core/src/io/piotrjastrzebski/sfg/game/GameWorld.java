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
import io.piotrjastrzebski.sfg.game.objects.Ceiling;
import io.piotrjastrzebski.sfg.game.objects.Ground;
import io.piotrjastrzebski.sfg.game.objects.Pickup;
import io.piotrjastrzebski.sfg.game.objects.PickupDebris;
import io.piotrjastrzebski.sfg.game.objects.TutorialLabel;
import io.piotrjastrzebski.sfg.game.objects.obstacles.Obstacle;
import io.piotrjastrzebski.sfg.game.objects.obstacles.Part;
import io.piotrjastrzebski.sfg.game.objects.obstacles.Wall;
import io.piotrjastrzebski.sfg.game.objects.obstacles.endpoints.Hammer;
import io.piotrjastrzebski.sfg.game.objects.obstacles.endpoints.Moving;
import io.piotrjastrzebski.sfg.game.objects.obstacles.endpoints.Spike;
import io.piotrjastrzebski.sfg.screen.GameScreen;
import io.piotrjastrzebski.sfg.utils.Config;
import io.piotrjastrzebski.sfg.utils.Locator;
import io.piotrjastrzebski.sfg.utils.Range;
import io.piotrjastrzebski.sfg.utils.Settings;
import io.piotrjastrzebski.sfg.utils.Utils;
import io.piotrjastrzebski.sfg.utils.PoolUtils;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pools;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.esotericsoftware.spine.SkeletonRenderer;

public class GameWorld {
    private final int PICKUP_SPAWN_DISTANCE;
    private final float PICKUP_SPAWN_CHANGE;
    private final float BOOST_REFILL_TIME;
    private final Range<Float> OBSTACLE_GAP;
    private final Settings settings;
    private Array<Ground> ground;
	private Array<Ceiling> ceilings;
	private Array<Obstacle> obstacles;
	private Array<Pickup> pickups;
    private Array<Wall> walls;
    private Array<PickupDebris> pickupDebris;
    private Array<TutorialLabel> tutorialLabels;

    private float lastCleanup;
	
	private int pickupDelay;
    private Range<Float> obstRange;
    private EventLoop events;
    private float worldWidth;
    private float worldHeight;
    private float widthOffset;
    private int obstSinceLastBoost;
    private float pickupLabelOffset;

    //TODO split the class or something
	public GameWorld(){
        settings = Locator.getSettings();
        events = Locator.getEvents();
        final Config config = Locator.getConfig();
        obstRange = config.getObstacleDistance();
        ground = new Array<Ground>();
        ceilings = new Array<Ceiling>();
        obstacles = new Array<Obstacle>();
        pickups = new Array<Pickup>();
        walls = new Array<Wall>();
        pickupDebris = new Array<PickupDebris>();
        tutorialLabels = new Array<TutorialLabel>();

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
		PoolUtils.reset(pickupDebris);
		PoolUtils.reset(tutorialLabels);
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
		PoolUtils.clean(pickupDebris, outside);
		PoolUtils.clean(tutorialLabels, outside);
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
        float lastY = o.getY();

		if (lastX <= obstacleRight){
			populateNext(lastX, lastY, o.getTopY(), o.getBotY());
        }
	}

    private final static float MIN_Y = 5;
    private final static float MAX_Y = 31;
    private final static float MAX_DIFF = 8;

	private void populateInit(float lastX){
		final Obstacle o = obtainObstacle();
        // first one biggest possible gap
        float gap = OBSTACLE_GAP.max;
        // first one in middle
        float centerY = (MIN_Y + MAX_Y)/2;
        float botY = centerY - gap*0.5f;
        float topY = centerY + gap*0.5f;

        o.initType(Part.Type.STATIC, Part.Type.STATIC);
        // 0.5 so its not flush with ground / ceiling
        o.init(lastX, botY + 0.5f, topY - 0.5f);
        obstacles.add(o);

		// ground and ceiling going behind
		for (int i = 1; i <= 5; i++) {
			final Ground g = obtainGround();
			// show support on last ground panel
			g.init(lastX-i*15-3, MIN_Y,
					18,
                    false,
                    i==1 && botY > MIN_Y+1
            );
			ground.add(g);
			
			final Ceiling c = obtainCeiling();
            // +6 above top of the screen
			c.init(lastX-i*15-3, MAX_Y+6,
					18,
					false,
                    false);
			ceilings.add(c);
		}
	}
	
	private void populateNext(float lastX, float lastY, float lastTopY, float lastBotY){
        pickupDelay++;
        float distance = Utils.randomRange(obstRange);

		final Obstacle o = obtainObstacle();
        obstacles.add(o);
        float gap = Utils.randomRange(OBSTACLE_GAP);
        if (o.getType() == Part.Type.MOVING){
            // slightly more than min gap size
            gap += 13;
        }
        float halfGap = gap*0.5f;
        float centerY = MathUtils.round(MathUtils.random(MIN_Y+halfGap, MAX_Y-halfGap));

        centerY = MathUtils.clamp(centerY, lastY-MAX_DIFF, lastY+MAX_DIFF);
        float botY = Math.max(centerY-halfGap, MIN_Y);
        float topY = Math.min(centerY+halfGap, MAX_Y);
        // 0.5 so its not flush with ground / ceiling
        o.init(lastX+distance, botY + 0.5f, topY - 0.5f);

        // moving obstacles needs more vertical space
        float groundMaxY = Math.min(lastBotY, botY);
        float groundY = MathUtils.round(MathUtils.random(MIN_Y, groundMaxY));

        float ceilingMinY = Math.max(lastTopY, topY);
        float ceilingY = MathUtils.round(MathUtils.random(MAX_Y, ceilingMinY));

		final Ground g = obtainGround();
		g.init(lastX, groundY,
				distance,
				lastBotY - 2 >= groundY,
                botY - 2 >= groundY
        );
		ground.add(g);

		final Ceiling c = obtainCeiling();
		c.init(lastX, ceilingY,
				distance,
				lastTopY + 2 <= ceilingY,
                topY + 2 <= ceilingY
        );
		ceilings.add(c);

        // wait at least one obstacle per BOOST_REFILL_TIME and 25% chance after that
        // moving doesnt work here
        if (obstSinceLastBoost > BOOST_REFILL_TIME && MathUtils.random() > 0.5f && o.getType() != Part.Type.MOVING) {
            obstSinceLastBoost = 0;
            // either triple obstacle or wall i static bottom
            if (o.getBotType() == Part.Type.STATIC) {
                // create a wall for the player to destroy with boost
                final Wall w = obtainWall();
                w.init(lastX + distance, botY, gap);
                walls.add(w);
            } else {
                // another 2 in a row for boost
                o.disableScore();

                Obstacle o2 = obtainObstacle();
                o2.initType(o.getBotType(), o.getTopType());
                o2.init(lastX + distance + 4, botY + 0.5f, topY - 0.5f);
                o2.disableScore();
                obstacles.add(o2);

                o2 = obtainObstacle();
                o2.initType(o.getBotType(), o.getTopType());
                o2.init(lastX + distance + 8, botY + 0.5f, topY - 0.5f);
                obstacles.add(o2);

                final Ground g2 = obtainGround();
                // 3x3 + 2
                g2.init(lastX + distance - 1.5f, groundY,
                        10,
                        false,
                        false
                );
                ground.add(g2);
            }
            events.queueEvent(EventType.SHOW_BOOST_TUT, o.getPos());
        } else {
            obstSinceLastBoost += 1;
        }
		// 1/4 chance to spawn a pickup and at least 3 obstacles since last
		if (MathUtils.random() <= PICKUP_SPAWN_CHANGE && pickupDelay > PICKUP_SPAWN_DISTANCE){
			// 3 for width of obstacles
			final float pickupOffset = 5 + (int) (MathUtils.random()*(distance-10));
			final Pickup p = obtainPickup();
			float y = MathUtils.random(groundY+2.5f, ceilingY-2.5f);
            p.init(lastX+pickupOffset, y);
			pickups.add(p);
            pickupDelay = 0;
            if (!settings.getPickupTutShowed(p.getType())) {
                final TutorialLabel label = Pools.obtain(TutorialLabel.class);
                label.init(p);
                tutorialLabels.add(label);
                settings.setPickupTutShowed(p.getType());
            }
		}
	}

    public void fixedUpdate(){
        for (final Pickup p : pickups) {
            p.fixedUpdate();
        }
        for (int i = 0; i < pickupDebris.size; i++) {
            pickupDebris.get(i).fixedUpdate();
        }
    }

	public void variableUpdate(float delta, float alpha, float camX){
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
        for (int i = 0; i < pickupDebris.size; i++) {
            pickupDebris.get(i).variableUpdate(delta, alpha);
        }
        for (final Pickup p : pickups) {
            p.variableUpdate(delta, alpha);
            if (p.isExploded()) {
                final PickupDebris debris = obtainPickupDebris();
                debris.init(p.getPos());
                pickupDebris.add(debris);
                events.queueEvent(EventType.SPAWN_EXPLOSION, Pools.obtain(Vector2.class).set(p.getPos()));
                Pools.free(p);
                pickups.removeValue(p, true);
            }
        }
        float outside = camX + pickupLabelOffset;
        for (TutorialLabel label:tutorialLabels) {
            label.variableUpdate(delta, alpha);
            if (label.getPos().x < outside){
                label.show();
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
        for (int i = 0; i < tutorialLabels.size; i++) {
            tutorialLabels.get(i).draw(batch);
        }
		for (int i = 0; i < pickups.size; i++) {
			pickups.get(i).draw(batch, skeletonRenderer);
		}
        for (int i = 0; i < walls.size; i++) {
            walls.get(i).draw(batch);
        }
        for (int i = 0; i < pickupDebris.size; i++) {
            pickupDebris.get(i).draw(batch);
        }
	}

	public void updateViewport(ExtendViewport gameViewPort) {
        worldWidth = gameViewPort.getWorldWidth();
        worldHeight = gameViewPort.getWorldHeight();
        pickupLabelOffset = worldWidth/2-3;
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

    public void dispose() {
        // clear the pools
        PoolUtils.dispose(Ground.class, ground);
        PoolUtils.dispose(Ceiling.class, ceilings);
        PoolUtils.dispose(Obstacle.class, obstacles);
        PoolUtils.dispose(Pickup.class, pickups);
        PoolUtils.dispose(Wall.class, walls);
        PoolUtils.dispose(PickupDebris.class, pickupDebris);
        PoolUtils.dispose(TutorialLabel.class, tutorialLabels);

        // pools from obstacles
        PoolUtils.dispose(Spike.class);
        PoolUtils.dispose(Hammer.class);
        PoolUtils.dispose(Moving.class);
    }

    private Obstacle obtainObstacle(){
        final Obstacle o = Pools.obtain(Obstacle.class);
        o.updateViewPort(worldWidth, worldHeight);
        o.initType();
        return o;
    }

    private Ground obtainGround(){
        final Ground g = Pools.obtain(Ground.class);
        g.updateViewPort(worldWidth, worldHeight);
        return g;
    }

    private Ceiling obtainCeiling(){
        final Ceiling c= Pools.obtain(Ceiling.class);
        c.updateViewPort(worldWidth, worldHeight);
        return c;
    }

    private Wall obtainWall(){
        return Pools.obtain(Wall.class);
    }


    private Pickup obtainPickup(){
        return Pools.obtain(Pickup.class);
    }

    private PickupDebris obtainPickupDebris(){
        return Pools.obtain(PickupDebris.class);
    }
}
