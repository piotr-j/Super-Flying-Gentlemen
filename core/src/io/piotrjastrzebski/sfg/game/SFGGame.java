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
import io.piotrjastrzebski.sfg.game.objects.ExplosionLight;
import io.piotrjastrzebski.sfg.game.objects.Pickup;
import io.piotrjastrzebski.sfg.game.objects.Player;
import io.piotrjastrzebski.sfg.game.objects.Rocket;
import io.piotrjastrzebski.sfg.game.objects.ShieldBreak;
import io.piotrjastrzebski.sfg.game.objects.obstacles.Obstacle;
import io.piotrjastrzebski.sfg.game.objects.obstacles.ScoreSensor;
import io.piotrjastrzebski.sfg.game.objects.obstacles.Wall;
import io.piotrjastrzebski.sfg.screen.GameScreen;
import io.piotrjastrzebski.sfg.utils.Assets;
import io.piotrjastrzebski.sfg.utils.Collision;
import io.piotrjastrzebski.sfg.utils.Config;
import io.piotrjastrzebski.sfg.utils.Locator;
import io.piotrjastrzebski.sfg.utils.SoundManager;
import io.piotrjastrzebski.sfg.utils.PoolUtils;
import io.piotrjastrzebski.sfg.game.ContactDispatcher.PlayerContactPool.PlayerContact;
import box2dLight.Light;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.ParticleEffectPool;
import com.badlogic.gdx.graphics.g2d.ParticleEffectPool.PooledEffect;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pools;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.esotericsoftware.spine.SkeletonRenderer;

import java.util.Iterator;

public class SFGGame implements EventListener {
    public final static float STEP_TIME = 1.0f/60.0f;
    private final static int MAX_STEPS = 3;

    private World world;

	private Array<Rocket> rockets;
	private ParticleEffectPool explosionParticles;
	private ParticleEffectPool bloodParticles;
	private ParticleEffectPool toxicParticles;
	private Array<PooledEffect> effects;
	private Array<ExplosionLight> explosionLight;
	private Array<ShieldBreak> shieldBreaks;
	private Player player;
	private Camera camera;
	private boolean isRunning = true;
	private boolean isResuming = false;
    private float resumeTimer;
    private final static  float RESUME_TIME = 1.5f;

	private float actualViewportWidth;
	private float actualViewportHeight;
	public Config config;

	private SkeletonRenderer skeletonRenderer;

	private EventLoop events;

    private GameWorld gameWorld;
	
	private boolean init = true;
    private boolean isPlayerTouching;
    private int numTouching;

    private float accumulator = 0;
    private float alpha = 0;

    public SFGGame(GameScreen screen){
        config = Locator.getConfig();
        world = Locator.getWorld();
        events = Locator.getEvents();
        registerEvents();

        camera = screen.getCamera();

        final Assets assets = Locator.getAssets();

        explosionParticles = assets.getParticles(Assets.Particles.EXPLOSION);
        bloodParticles = assets.getParticles(Assets.Particles.BLOOD);
        toxicParticles = assets.getParticles(Assets.Particles.TOXIC);

		effects = new Array<ParticleEffectPool.PooledEffect>();
		skeletonRenderer = screen.getSkeletonRenderer();

		Light.setContactFilter(
				Collision.LIGHT,
				Collision.LIGHT_GROUP,
				Collision.MASK_LIGHTS);
		
		rockets = new Array<Rocket>();
        explosionLight = new Array<ExplosionLight>();
        shieldBreaks = new Array<ShieldBreak>();

        player = new Player();
		gameWorld = new GameWorld();

		// reset() called in updateViewPort()
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
        events.register(this, EventType.SPAWN_TOXIC_CLOUD);
        events.register(this, EventType.SPAWN_SHIELD_BREAK);
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
        events.unregister(this, EventType.SPAWN_TOXIC_CLOUD);
        events.unregister(this, EventType.SPAWN_SHIELD_BREAK);
    }

	public void reset(){
		PoolUtils.reset(explosionLight);
		
		for (int i = effects.size - 1; i >= 0; i--){
		    effects.get(i).free();
		}
		effects.clear();

		for (int i = rockets.size - 1; i >= 0; i--){
            Pools.free(rockets.get(i));
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
                actualViewportHeight * 0.75f,
                Locator.getPlayerStats().getPlayerSkin());
        events.queueEvent(EventType.PLAYER_RESPAWNED);
        events.queueEvent(EventType.PLAYER_ALIVE, true);
        events.queueEvent(EventType.PLAYER_LIVES_CHANGED, player.getLives());
        events.queueEvent(EventType.PLAYER_BOOST_CHANGED, player.getBoostDelay());
        events.queueEvent(EventType.PLAYER_SHIELDS_CHANGED, player.getShields());
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
        isResuming = true;
	}

    private void finishResuming(){
        isRunning = true;
        isResuming = false;
        resumeTimer = 0;
    }

	public void update(float delta){
		if (!(isRunning || isResuming))
			return;
        // slow down game if we are resuming
        if (isResuming){
            resumeTimer += delta;
            if (resumeTimer >= RESUME_TIME) {
                finishResuming();
            } else {
                delta *= resumeTimer/RESUME_TIME;
            }
        }

        accumulator += delta;
        int steps = 0;
        while (STEP_TIME < accumulator && MAX_STEPS > steps){
            world.clearForces();
            world.step(STEP_TIME, 6, 2);
            events.update();
            accumulator -= STEP_TIME;
            steps++;
            fixedUpdate();
        }
        alpha = accumulator/STEP_TIME;
        variableUpdate(delta);
    }

    private void fixedUpdate(){
        for (Rocket rocket:rockets){
            rocket.fixedUpdate();
        }
        gameWorld.fixedUpdate();
        player.fixedUpdate();
    }

    private void variableUpdate(float delta){
        gameWorld.variableUpdate(delta, alpha, camera.position.x);

        for (Iterator<Rocket> iterator = rockets.iterator(); iterator.hasNext();) {
            final Rocket r = iterator.next();
            r.variableUpdate(delta, alpha);
            if (r.isExploded()) {
                Pools.free(r);
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

        for (Iterator<ExplosionLight> iterator = explosionLight.iterator(); iterator.hasNext();) {
            final ExplosionLight el = iterator.next();
            el.update(delta);
            if (el.isComplete()) {
                Pools.free(el);
                iterator.remove();
            }
        }

        for (Iterator<ShieldBreak> iterator = shieldBreaks.iterator(); iterator.hasNext();) {
            final ShieldBreak sb = iterator.next();
            sb.variableUpdate(delta, alpha);
            if (sb.isComplete()) {
                Pools.free(sb);
                iterator.remove();
            }
        }

        if (isPlayerTouching){
            if (player.touched()) {
                events.queueEvent(EventType.SPAWN_BLOOD, obtainVec2(
                        getPlayerX(),
                        getPlayerY()
                ));
                events.queueEvent(EventType.PLAYER_LIVES_CHANGED, player.getLives());
            }
        }

        player.variableUpdate(delta, alpha);
        events.queueEvent(EventType.PLAYER_BOOST_CHANGED, player.getBoostDelay());
    }

    public void spawnExplosion(Vector2 pos){
        if (effects.size > 16)
            return;
        final PooledEffect effect = explosionParticles.obtain();
        effect.setPosition(pos.x, pos.y);
        effects.add(effect);

        final ExplosionLight el = Pools.obtain(ExplosionLight.class);
        el.init(pos.x, pos.y);
        explosionLight.add(el);

        freeVec2(pos);

        events.queueEvent(EventType.PLAY_SOUND, SoundManager.EXPLOSION);
    }

    public void spawnBlood(Vector2 pos){
        if (effects.size > 16)
            return;
        final PooledEffect effect = bloodParticles.obtain();
        effect.setPosition(pos.x, pos.y);
        effects.add(effect);
        freeVec2(pos);
        events.queueEvent(EventType.PLAY_SOUND, SoundManager.HURT);
    }

    public void spawnToxicCloud(Vector2 pos){
        if (effects.size > 16)
            return;
        final PooledEffect effect = toxicParticles.obtain();
        effect.setPosition(pos.x, pos.y);
        effects.add(effect);
        freeVec2(pos);
    }

    private void spawnShieldBreak(Vector2 pos) {
        final ShieldBreak shieldBreak = Pools.obtain(ShieldBreak.class);
        shieldBreak.init(pos.x, pos.y);
        shieldBreaks.add(shieldBreak);
        freeVec2(pos);
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
        for (int i = 0; i < shieldBreaks.size; i++) {
            shieldBreaks.get(i).draw(batch, skeletonRenderer);
        }
        batch.end();
	}
	
	@Override
	public void handleEvent(Event e) {
		switch (e.getType()) {
		case EventType.ROCKET_HIT:
            final Rocket r = (Rocket) e.getData();
            if (!r.isExploded()) {
                r.explode();
                events.queueEvent(EventType.SPAWN_EXPLOSION, obtainVec2(
                        r.getTransform().getLerpX(alpha),
                        r.getTransform().getLerpY(alpha)
                ));
            }
			break;
		case EventType.PICKUP_TOUCHED:
			final Pickup pt = (Pickup) e.getData();
            handlePickupTouched(pt);
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
        case EventType.SPAWN_TOXIC_CLOUD:
            spawnToxicCloud((Vector2) e.getData());
            break;
        case EventType.SPAWN_SHIELD_BREAK:
            spawnShieldBreak((Vector2) e.getData());
            break;
        default: break;
		}
	}

    private void handlePickupTouched(Pickup pickup){
        pickup.updateAngle();
        if (pickup.pickup()){
            events.queueEvent(EventType.PLAY_SOUND, SoundManager.PICKUP);
            switch (pickup.getType()) {
                case BOOST:
                    playerAddBoost(pickup.getValue());
                    break;
                case LIVES:
                    playerAddLives((int) pickup.getValue());
                    break;
                case SHIELD:
                    playerAddShields((int) pickup.getValue());
                    break;
                case TOXIC:
                    events.queueEvent(EventType.SPAWN_TOXIC_CLOUD, obtainVec2(pickup.getPos()));
                    int damage = (int) pickup.getValue();
                    int shields = player.getShields();
                    if (shields > 0)
                        events.queueEvent(EventType.SPAWN_SHIELD_BREAK, obtainVec2(pickup.getPos()));
                    while (shields > 0 && damage > 0){
                        shields--;
                        damage--;
                    }
                    player.setShields(shields);
                    events.queueEvent(EventType.PLAYER_SHIELDS_CHANGED, player.getShields());
                    if (damage > 0){
                        int lives = player.getLives();
                        while (lives > 0 && damage > 0){
                            lives--;
                            damage--;
                        }
                        player.setLives(lives);
                        events.queueEvent(EventType.SPAWN_BLOOD, obtainVec2(
                                getPlayerX(),
                                getPlayerY()
                        ));
                        events.queueEvent(EventType.PLAY_SOUND, SoundManager.HURT);
                        events.queueEvent(EventType.PLAYER_LIVES_CHANGED, player.getLives());
                    }
                    checkPlayerDeath();
                    break;
            }
        }
    }

    private void checkPlayerDeath() {
        if (player.isDead()){
            events.queueEvent(EventType.PLAYER_DIED);
            events.queueEvent(EventType.PLAY_SOUND, SoundManager.DEATH);
            player.kill();
            events.queueEvent(EventType.PLAYER_ALIVE, player.isAlive());
        }
    }

    private void playerPartTouched(PlayerContact playerContact){
        if (!player.isAlive()) {
            return;
        }
        final Object o = playerContact.content();
        final Vector2 contactPos = playerContact.position();

        if (o instanceof Wall){
            final Wall wall = (Wall) o;
            events.queueEvent(EventType.SPAWN_EXPLOSION, obtainVec2(contactPos));
            if (player.isDashing()){
                wall.smash();
            } else {
                if (!wall.isSmashed()) {
                    if (player.touched()){
                        events.queueEvent(EventType.SPAWN_BLOOD, obtainVec2(contactPos));
                    }
                }
            }
        } else if (o instanceof Obstacle){
            final Obstacle obst = (Obstacle)o;
            if (player.hasShield()){
                obst.execute(false);
            } else if (player.useShield()) {
                obst.execute(false);
                events.queueEvent(EventType.PLAYER_SHIELDS_CHANGED, player.getShields());
                events.queueEvent(EventType.SPAWN_SHIELD_BREAK, obtainVec2(contactPos));
            } else {
                events.queueEvent(EventType.SPAWN_BLOOD, obtainVec2(contactPos));
                obst.execute(true);
                if (config.getDifficulty() != Config.Difficulty.BABY){
                    switch (obst.getType()) {
                        case SPIKE:
                            events.queueEvent(EventType.PLAYER_SPIKED);
                            break;
                        case HAMMER:
                            events.queueEvent(EventType.PLAYER_CRUSHED);
                            break;
                        default:
                            break;
                    }
                    events.queueEvent(EventType.SPAWN_EXPLOSION, obtainVec2(contactPos));
                    events.queueEvent(EventType.PLAYER_DIED);
                    events.queueEvent(EventType.PLAY_SOUND, SoundManager.DEATH);
                    player.kill();
                } else {
                    player.addLives(-9);
                }
            }
        } else if (player.touched()){
            events.queueEvent(EventType.SPAWN_BLOOD, obtainVec2(contactPos));
            events.queueEvent(EventType.PLAY_SOUND, SoundManager.HURT);
        }
        if (player.isDead()){
            events.queueEvent(EventType.SPAWN_EXPLOSION, obtainVec2(contactPos));
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
        finishResuming();
        player.jump();
        final Rocket rocket = Pools.obtain(Rocket.class);
        // magic values so the spawn point is correct
        rocket.init(getPlayerX(), getPlayerY() - 2.75f);
        rockets.add(rocket);
        events.queueEvent(EventType.PLAY_SOUND, SoundManager.ROCKET);
	}
	
	public void swipe(){
        if(player.isDead())
            return;
        finishResuming();
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
	
	private void playerAddLives(int lives){
		if(player.isDead())
			return;
        player.addLives(lives);
        events.queueEvent(EventType.PLAYER_LIVES_CHANGED, player.getLives());
	}

	private void playerAddBoost(float boostToAdd) {
		if(player.isDead())
			return;
        player.subBoostDelay(boostToAdd);
        events.queueEvent(EventType.PLAYER_BOOST_CHANGED, player.getBoostDelay());
	}

    private void playerAddShields(int value) {
        if(player.isDead())
            return;
        player.addShields(value);
        events.queueEvent(EventType.PLAYER_SHIELDS_CHANGED, player.getShields());
    }

    public float getPlayerX(){
        return player.getTransform().getLerpX(alpha);
    }

    public float getPlayerY(){
        return player.getTransform().getLerpY(alpha);
    }

	public EventLoop getEvents(){
		return events;
	}

	public World getWorld() {
		return world;
	}

    public void dispose() {
        unRegisterEvents();
        PoolUtils.dispose(Rocket.class, rockets);
        PoolUtils.dispose(ExplosionLight.class, explosionLight);
        PoolUtils.dispose(ShieldBreak.class, shieldBreaks);
        gameWorld.dispose();
    }

    private Vector2 obtainVec2(Vector2 vec){
        return Pools.obtain(Vector2.class).set(vec);
    }

    private Vector2 obtainVec2(float x, float y){
        return Pools.obtain(Vector2.class).set(x, y);
    }

    private void freeVec2(Vector2 vec){
        Pools.free(vec);
    }
}
