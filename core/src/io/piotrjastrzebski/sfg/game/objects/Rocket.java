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

import box2dLight.ConeLight;
import io.piotrjastrzebski.sfg.utils.Locator;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.utils.Pool.Poolable;

public class Rocket implements Poolable, Position {
	private Body body;
	private World world;
	private Sprite rocketSprite;
	private boolean isExploded = false;
	private ConeLight coneLight;
    private float lastAngle;
    private final float HALF_WIDTH;
    private final float LIGHT_OFFSET_X = 0.8f;

    public Rocket() {
		this.world = Locator.getWorld();
		rocketSprite = Locator.getAssets().getScaledSprite("rocket");
        HALF_WIDTH = rocketSprite.getWidth()*0.5f;
        rocketSprite.setOrigin(HALF_WIDTH, 0);
        coneLight = new ConeLight(Locator.getRayHandler(), 3, Color.WHITE, 5,
				-100, -100, 90, 15);
		coneLight.setColor(1, 0.5f, 0.3f, 1);
		coneLight.setSoft(false);
		coneLight.setXray(true);

		BodyDef bodyDef = new BodyDef();
		bodyDef.type = BodyType.DynamicBody;
		bodyDef.position.set(-10, -100);

		body = world.createBody(bodyDef);
		CircleShape circle = new CircleShape();
		circle.setRadius(0.1f);
		
		FixtureDef fixtureDef = new FixtureDef();
		fixtureDef.shape = circle;
		fixtureDef.density = 1f; 
		fixtureDef.friction = 0.1f;
		fixtureDef.restitution = 0.1f;
		
		body.createFixture(fixtureDef);
		body.setUserData(this);
		body.setBullet(true);
		circle.dispose();
	}


	/**
	 * Init the bullet and set the position
	 * This must be called outside of world.step()
	 */
	public void init(float x, float y, float angle, float vx, float vy){
		isExploded = false;
		body.setAngularVelocity(0);
		body.setLinearVelocity(vx, vy);
		body.setTransform(x, y, angle);
		body.setGravityScale(1);
		body.setAwake(true);

        lastAngle = body.getLinearVelocity().nor().angle()-90;
        update(0);
	}

	public void update(float delta){
		final Vector2 pos = body.getPosition();
		float angle = body.getLinearVelocity().nor().angle()-90;
        // ignore large spike after collision
        if (Math.abs(lastAngle - angle) > 10f){
            angle = lastAngle;
        } else {
            lastAngle = angle;
        }
        rocketSprite.setRotation(180-angle);
		coneLight.setDirection(-angle - 90);
        rocketSprite.setPosition(pos.x-HALF_WIDTH, pos.y);
        final float radAngle = (-angle-90)*MathUtils.degreesToRadians;
        final float dX = LIGHT_OFFSET_X * MathUtils.cos(radAngle);
        final float dY = LIGHT_OFFSET_X * MathUtils.sin(radAngle);
        coneLight.setPosition(pos.x+dX, pos.y+dY);
	}
	
	public void draw(Batch batch){
		rocketSprite.draw(batch);
	}
	
	public void destroy(){
		if (body!=null){
			world.destroyBody(body);
			body = null;
		}
	}

	@Override
	public void reset() {
		body.setAngularVelocity(0);
		body.setLinearVelocity(0, 0);
		body.setTransform(-10, -100, 0);
		body.setGravityScale(0);
		body.setAwake(false);
        coneLight.setPosition(-100, -100);
    }
	
	public void explode(){
		isExploded = true;
	}
	
	public boolean isExploded(){
		return isExploded;
	}

    @Override
    public Vector2 getPos() {
        return body.getPosition();
    }
}
