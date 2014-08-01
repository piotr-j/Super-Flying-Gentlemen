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

import io.piotrjastrzebski.sfg.game.PlayerStats;
import io.piotrjastrzebski.sfg.utils.Collision;
import io.piotrjastrzebski.sfg.utils.Config;
import box2dLight.ConeLight;
import box2dLight.PointLight;
import io.piotrjastrzebski.sfg.utils.Locator;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.esotericsoftware.spine.SkeletonRenderer;

public class Player implements Position {
    // max touches per second
    private final static float MIN_TOUCH_DELAY = 1.0f/2.0f; // 2 per second
	private Body body;
    // remaining delay of the jump
	private float jumpDelay;
	// cached mass of the player
	private float mass;
	private boolean isAlive = true;
	private boolean isDashing = false;
	private boolean justSpawned = false;
	private float dashTimer;
	private float dashDelayTimer;
	private int lives = 0;
	private Config cfg;
	// cache values that are needed each frame, calls are expensive
	private final float dashTime;
	private final float flySpeed;
	private final float maxFlySpeed;
	private final float flyImpMult;
	private final float dashImpMult;
	
	private ConeLight coneLight;
	private PointLight pointLight;
	
	private PlayerAnimation animation;
	
	private Parachute parachute;
	private PlayerRagDoll ragDoll;
    private float touchDelay = 0;

    /**
	 * Initialize a player, it is represented as a ball in box2d world
     */
	public Player(){
		this.cfg = Locator.getConfig();
		pointLight = new PointLight(Locator.getRayHandler(), 8, Color.WHITE, 3, 0, 0);
		pointLight.setColor(1, 0.8f, 0.3f, 1);
		// this light is not blocked as its ambient to the character
		pointLight.setXray(true);
		
		coneLight = new ConeLight(Locator.getRayHandler(), 16, Color.WHITE, 16,
				0, 0, 0, 20);
		coneLight.setColor(1, 0.8f, 0.3f, 1);
		
		lives = cfg.getPlayerMaxLives();
		// cache values that are needed each frame, calls are expensive
		dashTime = cfg.getPlayerDashTime();
		flySpeed = cfg.getPlayerFlySpeed();
		maxFlySpeed = cfg.getPlayerMaxFlySpeed();
		flyImpMult = cfg.getPlayerFlyImpMult();
		dashImpMult = cfg.getPlayerDashMult();
		
		BodyDef bodyDef = new BodyDef();
		bodyDef.type = BodyType.DynamicBody;
		bodyDef.fixedRotation = true;

		body = Locator.getWorld().createBody(bodyDef);
        CircleShape circle = new CircleShape();
		circle.setRadius(0.9f);

		FixtureDef fixtureDef = new FixtureDef();
		fixtureDef.shape = circle;
		fixtureDef.density = 0.5f; 
		fixtureDef.friction = 0.0f;
		fixtureDef.restitution = 0.0f;
		fixtureDef.filter.categoryBits = Collision.PLAYER;
		fixtureDef.filter.maskBits = Collision.MASK_PLAYER;
		
		body.createFixture(fixtureDef);
		body.setUserData(this);
		body.setLinearDamping(cfg.getPlayerLinearDampening());
		// mass is used for various impulse strength calculation
		mass = body.getMass();
		circle.dispose();
		
		animation = new PlayerAnimation();
        ragDoll = new PlayerRagDoll(animation.getSkeleton());
        parachute = new Parachute();
	}

	public void reset(float x, float y, PlayerStats.Skin skin){
		body.setAngularVelocity(0);
		body.setLinearVelocity(0, 0);
		body.setTransform(x, y, 0);
		body.setAwake(true);
		// apply initial impulse so player moves on spawn
		body.applyLinearImpulse(mass*3f, 0, 0, 0, true);
		
		lives = cfg.getPlayerMaxLives();
		isAlive = true;
		isDashing = false;
		jumpDelay = 0;
		dashDelayTimer = 0;
		pointLight.setPosition(x, y);
		coneLight.setPosition(x-1, y);
		animation.init(x, y);
		animation.setSkin(skin);
		pointLight.setActive(true);
		coneLight.setActive(true);
		
		justSpawned = true;
		
		parachute.init(x, y);
        ragDoll.init(x, y, skin);
	}
	
	private void clearJustSpawned(){
		justSpawned = false;
		parachute.detach();
	}

	public void jump(){
		if (jumpDelay > 0 || !isAlive || isDashing){
			return;	
		}
		if (justSpawned){
			clearJustSpawned();
		}
		jumpDelay = cfg.getPlayerJumpDelay();
		
		final float vY = body.getLinearVelocity().y;
		float imp = cfg.getPlayerJumpImpulse();
		// counteract the gravity if falling down and limit the max jump speed
		imp += -vY;
		body.applyLinearImpulse(0.0f, mass*imp, 0.0f, 0.0f, true);
		animation.shoot();
	}

	public void dashForward(){
		if (dashDelayTimer > 0 || !isAlive){
			return;
		}
		if (justSpawned){
			clearJustSpawned();
		}
		dashDelayTimer = cfg.getPlayerDashDelay();
		isDashing = true;
		// vX ~20 + default speed
		body.applyLinearImpulse(mass*dashImpMult, 0.0f, 0.0f, 0.0f, true);		
		animation.boost();
	}

	public void update(float delta){
		final Vector2 pos = body.getPosition();
		if (isAlive){
            if (touchDelay > 0){
                touchDelay -=delta;
            }
			if (jumpDelay > 0){
				jumpDelay-=delta;
			}
			if (dashDelayTimer > 0){
				dashDelayTimer-=delta;
			}
			final float vX = body.getLinearVelocity().x;
			final float vY = body.getLinearVelocity().y;
			
			if (isDashing){
				dashTimer+=delta;
				// counteract gravity while dashing
				body.applyLinearImpulse(0.0f, -mass*vY, 0.0f, 0.0f, true);
				// when dash ends slow down

				if (dashTimer >= dashTime){
					isDashing = false;
					dashTimer = 0;
                    dashDelayTimer = cfg.getPlayerDashDelay();
				}
			} else {
				// slow at spawn
				if (justSpawned){
					body.applyLinearImpulse(0, -mass*vY*0.95f, 0.0f, 0.0f, true);
				} else if ( Math.abs(vX) <= flySpeed){
					// vX ~5
					float impX = mass*delta*flyImpMult;
					body.applyLinearImpulse(impX, 0.0f, 0.0f, 0.0f, true);
				} else if ( Math.abs(vX) > maxFlySpeed){
					body.applyLinearImpulse(-mass*vX*delta*10, 0.0f, 0.0f, 0.0f, true);
				}
			}
			pointLight.setPosition(pos);
			coneLight.setPosition(pos.x-1, pos.y);
			animation.update(delta, pos.x, pos.y);
			
		}
        ragDoll.update(delta);
		parachute.update(delta, pos.x, pos.y);
	}

	public void draw(Batch batch, SkeletonRenderer skeletonRenderer){
		if (isAlive){
			animation.draw(batch, skeletonRenderer);
		} else {
            ragDoll.draw(batch);
		}
		parachute.draw(batch);
	}
	
	public void kill() {
		isAlive = false;
        pointLight.setPosition(-100, -100);
		pointLight.setActive(false);
		coneLight.setActive(false);
		coneLight.setPosition(-100, -100);
		body.setTransform(-100, -100, 0);
		body.setLinearVelocity(0, 0);
        ragDoll.detach();
	}

	public boolean touched() {
        if (touchDelay <= 0) {
            if (lives > 0)
                lives -= 1;
            if (lives <= 0) {
                isAlive = false;
            }
            touchDelay = MIN_TOUCH_DELAY;
            return true;
        } else {
            return false;
        }
	}
	public int getLives() {
		return lives;
	}

	public int addLives(int val) {
		lives += val;
		return lives;
	}
	public boolean isAlive() {
		return isAlive;
	}

	public float getBoostDelay() {
        if (isDashing)
            return cfg.getPlayerDashDelay();
		return dashDelayTimer;
	}
	public float subBoostDelay(float boostToAdd) {
		dashDelayTimer-=boostToAdd;
		return dashDelayTimer;
	}

	public Vector2 getVelocity() {
		return body.getLinearVelocity();
	}

	public boolean isDead() {
		return !isAlive;
	}

    public Vector2 getPos() {
        return body.getPosition();
    }

    public boolean isDashing() {
        return isDashing;
    }
}
