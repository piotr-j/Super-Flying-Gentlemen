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

import io.piotrjastrzebski.sfg.events.Event;
import io.piotrjastrzebski.sfg.events.EventListener;
import io.piotrjastrzebski.sfg.events.EventLoop;
import io.piotrjastrzebski.sfg.events.EventType;
import io.piotrjastrzebski.sfg.game.objects.Pickup;
import io.piotrjastrzebski.sfg.game.objects.Player;
import io.piotrjastrzebski.sfg.game.objects.Rocket;
import io.piotrjastrzebski.sfg.game.objects.obstacles.Obstacle;
import io.piotrjastrzebski.sfg.game.objects.obstacles.ScoreSensor;
import io.piotrjastrzebski.sfg.game.objects.obstacles.Wall;
import io.piotrjastrzebski.sfg.screen.GameScreen;
import io.piotrjastrzebski.sfg.utils.Assets;
import io.piotrjastrzebski.sfg.utils.Collision;
import io.piotrjastrzebski.sfg.utils.Config;
import io.piotrjastrzebski.sfg.utils.Locator;
import io.piotrjastrzebski.sfg.utils.SoundManager;
import io.piotrjastrzebski.sfg.utils.pools.ExplosionLightPool;
import io.piotrjastrzebski.sfg.utils.pools.PoolUtils;
import io.piotrjastrzebski.sfg.utils.pools.ExplosionLightPool.PooledExplosionLight;
import io.piotrjastrzebski.sfg.utils.pools.RocketPool;
import io.piotrjastrzebski.sfg.utils.pools.RocketPool.PooledRocket;
import io.piotrjastrzebski.sfg.game.ContactDispatcher.PlayerContactPool.PlayerContact;
import box2dLight.Light;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.ParticleEffectPool;
import com.badlogic.gdx.graphics.g2d.ParticleEffectPool.PooledEffect;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.esotericsoftware.spine.SkeletonRenderer;

import java.util.Iterator;

public class SFGGame implements EventListener {
	private final static float STEP_TIME = 1.0f/60.0f;

	private World world;

    private RocketPool rocketPool;
	private Array<PooledRocket> rockets;
	private ParticleEffectPool explosionParticles;
	private ParticleEffectPool bloodParticles;
	private Array<PooledEffect> effects;
	private ExplosionLightPool explosionLightPool;
	private Array<PooledExplosionLight> explosionLight;
	
	private Player player;
	private Camera camera;
	private boolean isRunning = true;

	private float actualViewportWidth;
	private float actualViewportHeight;
	public Config config;

	private SkeletonRenderer skeletonRenderer;

	private EventLoop events;

    private GameWorld gameWorld;
	
	private boolean init = true;
    private boolean isPlayerTouching;
    private int numTouching;

	public SFGGame(GameScreen screen){
        config = Locator.getConfig();
        world = Locator.getWorld();
        events = Locator.getEvents();
        registerEvents();

        camera = screen.getCamera();

        final Assets assets = Locator.getAssets();

        explosionParticles = assets.getExplosionParticles();
        bloodParticles = assets.getBloodParticles();

		effects = new Array<ParticleEffectPool.PooledEffect>();
		
		skeletonRenderer = screen.getSkeletonRenderer();

		Light.setContactFilter(
				Collision.LIGHT,
				Collision.LIGHT_GROUP,
				Collision.MASK_LIGHTS);
		
		rocketPool = new RocketPool();
		rockets = new Array<PooledRocket>();
		
		player = new Player();
		
		explosionLightPool = new ExplosionLightPool();
		explosionLight = new Array<PooledExplosionLight>();
		
		gameWorld = new GameWorld();

		// reset() called in updateViewport()	
	}

	private void registerEvents(){
        events.register(this, EventType.ROCKET_HIT);
        events.register(this, EventType.PICKUP_TOUCHED);
        events.register(this, EventType.PICKUP_DESTROYED);
        events.register(this, EventType.PLAYER_SCORED);
        events.register(this, EventType.PLAYER_PART_TOUCHED);
        events.register(this, EventType.PLAYER_TOUCHED_END);
        events.register(this, EventType.PLAYER_TOUCHED);
        events.register(this, EventType.SPAWN_EXPLOSION);
        events.register(this, EventType.SPAWN_BLOOD);
    }

    private void unRegisterEvents(){
        events.unregister(this, EventType.ROCKET_HIT);
        events.unregister(this, EventType.PICKUP_TOUCHED);
        events.unregister(this, EventType.PICKUP_DESTROYED);
        events.unregister(this, EventType.PLAYER_SCORED);
        events.unregister(this, EventType.PLAYER_PART_TOUCHED);
        events.unregister(this, EventType.PLAYER_TOUCHED_END);
        events.unregister(this, EventType.PLAYER_TOUCHED);
        events.unregister(this, EventType.SPAWN_EXPLOSION);
        events.unregister(this, EventType.SPAWN_BLOOD);
    }

	public void reset(){
		PoolUtils.reset(explosionLight);
		
		for (int i = effects.size - 1; i >= 0; i--){
		    effects.get(i).free();
		}
		effects.clear();

		for (int i = rockets.size - 1; i >= 0; i--){
			rockets.get(i).free();
		}
		rockets.clear();

		gameWorld.init(camera.position.x);
        events.clear();
        resetPlayer();
        events.queueEvent(EventType.PLAYER_SCORE_CHANGED, 0);
    }
	
	private void resetPlayer(){
		player.reset(
                0,
				actualViewportHeight*0.75f,
                Locator.getPlayerStats().getPlayerSkin());
		camera.position.x = getPlayerPos().x + GameScreen.PLAYER_OFFSET;
        events.queueEvent(EventType.PLAYER_RESPAWNED);
        events.queueEvent(EventType.PLAYER_ALIVE, true);
        events.queueEvent(EventType.PLAYER_LIVES_CHANGED, player.getLives());
        events.queueEvent(EventType.PLAYER_BOOST_CHANGED, player.getBoostDelay());
        numTouching = 0;
        isPlayerTouching = false;
	}

	public void updateViewport(ExtendViewport gameViewPort){
		actualViewportWidth = gameViewPort.getCamera().viewportWidth;
		actualViewportHeight = gameViewPort.getCamera().viewportHeight;
		gameWorld.updateViewport(gameViewPort);
		// reset hero so width is updated
		if (init){
			init = false;
			reset();
		}
	}

	public void pauseGame(){
		isRunning = false;
	}

	public void resumeGame(){
		isRunning = true;
	}

	public void update(float delta){
		if (!isRunning)
			return;
		//TODO fix for slow devices
		world.step(STEP_TIME, 6, 2);
		gameWorld.update(delta, camera.position.x);

        for (Iterator<PooledRocket> iterator = rockets.iterator(); iterator.hasNext();) {
            final PooledRocket r = iterator.next();
            r.update(delta);
            if (r.isExploded()) {
                r.free();
                iterator.remove();
            }
        }

        for (Iterator<PooledEffect> iterator = effects.iterator(); iterator.hasNext();) {
            final PooledEffect e = iterator.next();
            e.update(delta);
            if (e.isComplete()) {
                e.free();
                iterator.remove();
            }
        }

        for (Iterator<PooledExplosionLight> iterator = explosionLight.iterator(); iterator.hasNext();) {
            final PooledExplosionLight el = iterator.next();
            el.update(delta);
            if (el.isComplete()) {
                el.free();
                iterator.remove();
            }
        }

		if (isPlayerTouching){
            if (player.touched()) {
                events.queueEvent(EventType.SPAWN_BLOOD, player.getPos());
                events.queueEvent(EventType.PLAYER_LIVES_CHANGED, player.getLives());
            }
        }

		player.update(delta);
        events.queueEvent(EventType.PLAYER_BOOST_CHANGED, player.getBoostDelay());
    }

    public void spawnExplosion(Vector2 pos){
        if (effects.size > 16)
            return;
        final PooledEffect effect = explosionParticles.obtain();
        effect.setPosition(pos.x, pos.y);
        effects.add(effect);

        final PooledExplosionLight el = explosionLightPool.obtain();
        el.init(pos.x, pos.y);
        explosionLight.add(el);

        events.queueEvent(EventType.PLAY_SOUND, SoundManager.EXPLOSION);
    }

    public void spawnBlood(Vector2 pos){
        if (effects.size > 16)
            return;
        final PooledEffect effect = bloodParticles.obtain();
        effect.setPosition(pos.x, pos.y);
        effects.add(effect);
        events.queueEvent(EventType.PLAY_SOUND, SoundManager.HURT);
    }

	public void draw(Batch batch){
        batch.begin();
		gameWorld.draw(batch, skeletonRenderer);
		for (int i = 0; i < rockets.size; i++) {
			rockets.get(i).draw(batch);
		}
		player.draw(batch, skeletonRenderer);
		for (int i = effects.size - 1; i >= 0; i--) {
		    PooledEffect effect = effects.get(i);
		    effect.draw(batch);
		}
        batch.end();
	}
	
	@Override
	public void handleEvent(Event e) {
		switch (e.getType()) {
		case EventType.ROCKET_HIT:
            final Rocket b = (Rocket) e.getData();
            if (!b.isExploded()) {
                b.explode();
                events.queueEvent(EventType.SPAWN_EXPLOSION, b.getPos());
            }
			break;
		case EventType.PICKUP_TOUCHED:
			final Pickup pt = (Pickup) e.getData();
            pt.updateAngle();
			if (pt.pickup()){
                events.queueEvent(EventType.PLAY_SOUND, SoundManager.PICKUP);
                switch (pt.getType()) {
				case BOOST:
					playerAddBoost(pt.getValue());
					break;
				case LIVES:
					playerAddLives(pt.getValue());
					break;
				}
			}
			break;
		case EventType.PICKUP_DESTROYED:
			final Pickup pd = (Pickup) e.getData();
			pd.explode();
			break;
		case EventType.PLAYER_SCORED:
			playerScored((ScoreSensor) e.getData());
			break;
		case EventType.PLAYER_PART_TOUCHED:
            final PlayerContact contact = (PlayerContact) e.getData();
            playerPartTouched(contact);
            contact.free();
            break;
        case EventType.PLAYER_TOUCHED_END:
            playerTouchedEnd();
            break;
        case EventType.SPAWN_EXPLOSION:
            spawnExplosion((Vector2) e.getData());
            break;
        case EventType.SPAWN_BLOOD:
            spawnBlood((Vector2) e.getData());
            break;
        default: break;
		}
	}

	private void playerPartTouched(PlayerContact playerContact){
        if (!player.isAlive()) {
            return;
        }
        final Object o = playerContact.object;
        final Vector2 contactPos = playerContact.position;

        if (o instanceof Wall){
            final Wall wall = (Wall) o;
            events.queueEvent(EventType.SPAWN_EXPLOSION, contactPos);
            if (player.isDashing()){
                wall.smash();
            } else {
                if (!wall.isSmashed()) {
                    if (player.touched()){
                        events.queueEvent(EventType.SPAWN_BLOOD, contactPos);
                    }
                }
            }
        } else if (player.touched()){
            events.queueEvent(EventType.SPAWN_BLOOD, contactPos);
            events.queueEvent(EventType.PLAY_SOUND, SoundManager.HURT);

            if (o instanceof Obstacle){
                if (config.getDifficulty() == Config.Difficulty.BABY){
                    // -10 total
                    player.addLives(-9);
                }
            }
        }
		if (o instanceof Obstacle){
            final Obstacle obst = (Obstacle)o;
            obst.execute(true);
            if (config.getDifficulty() != Config.Difficulty.BABY){
                switch (obst.getType()) {
                    case SPIKE:
                        events.queueEvent(EventType.PLAYER_SPIKED);
                        break;
                    case HAMMER:
                        events.queueEvent(EventType.PLAYER_CRUSHED);
                        break;
                    default: break;
                }
                events.queueEvent(EventType.SPAWN_EXPLOSION, contactPos);
                events.queueEvent(EventType.PLAYER_DIED);
                events.queueEvent(EventType.PLAY_SOUND, SoundManager.DEATH);
                player.kill();
            }
            events.queueEvent(EventType.SPAWN_BLOOD, contactPos);
        } else if (!player.isAlive()){
            events.queueEvent(EventType.SPAWN_EXPLOSION, contactPos);
            events.queueEvent(EventType.PLAYER_DIED);
            events.queueEvent(EventType.PLAY_SOUND, SoundManager.DEATH);
			player.kill();
            isPlayerTouching = true;
            numTouching++;
        }

        events.queueEvent(EventType.PLAYER_LIVES_CHANGED, player.getLives());
        events.queueEvent(EventType.PLAYER_ALIVE, player.isAlive());
    }

    private void playerTouchedEnd(){
        numTouching--;
        if (numTouching <= 0) {
            isPlayerTouching = false;
            numTouching = 0;
        }
    }

	public void tap(){
		if (player.isDead())
            return;
        player.jump();
        final PooledRocket rocket = rocketPool.obtain();
        rockets.add(rocket);
        final Vector2 pos = player.getPos();
        final Vector2 vel = player.getVelocity();
        // magic values so the spawn point is correct
        rocket.init(pos.x-0.4f, pos.y-2.75f, 0, vel.x * 0.5f, -7);
        events.queueEvent(EventType.PLAY_SOUND, SoundManager.ROCKET);
	}
	
	public void swipe(){
        if(player.isDead())
            return;
        player.dashForward();
        if (player.isDashing()){
            events.queueEvent(EventType.PLAY_SOUND, SoundManager.BOOST);
        }
	}
	
	private void playerScored(ScoreSensor sensor){
		if(player.isDead())
			return;
        sensor.score();
	}
	
	private void playerAddLives(float lives){
		if(player.isDead())
			return;
        player.addLives((int)lives);
        events.queueEvent(EventType.PLAYER_LIVES_CHANGED, player.getLives());
	}

	private void playerAddBoost(float boostToAdd) {
		if(player.isDead())
			return;
        player.subBoostDelay(boostToAdd);
        events.queueEvent(EventType.PLAYER_BOOST_CHANGED, player.getBoostDelay());
	}

	public Vector2 getPlayerPos() {
		return player.getPos();
	}
	
	public EventLoop getEvents(){
		return events;
	}

	public World getWorld() {
		return world;
	}

    public void dispose() {
        unRegisterEvents();
    }
}
