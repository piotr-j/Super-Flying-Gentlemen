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
import io.piotrjastrzebski.sfg.utils.Transform;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.utils.Pool.Poolable;

public class Rocket implements Poolable, FixedUpdatable, VariableUpdatable {
	private Body body;
	private World world;
	private Sprite rocketSprite;
	private boolean isExploded = false;
	private boolean isInit = false;
	private ConeLight coneLight;
    private float lastAngle;
    private final float HALF_WIDTH;
    private final float LIGHT_OFFSET_X = 0.8f;

    private Transform transform;

    public Rocket() {
		this.world = Locator.getWorld();
        transform = new Transform();
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
        fixtureDef.isSensor = true;

        body.createFixture(fixtureDef);
		body.setUserData(this);
		body.setBullet(true);
		circle.dispose();
	}

	/**
	 * Init the bullet and set the position
	 * This must be called outside of world.step()
	 */
	public void init(float x, float y){
        isInit = true;
        body.setAngularVelocity(0);
        body.setTransform(x, y, 0);
        body.applyLinearImpulse(0.15f, 0, 0, 0, true);
        isExploded = false;
        lastAngle = 180+15;
        transform.init(x, y, lastAngle);
	}

    @Override
    public void fixedUpdate() {
        if (!isInit)
            return;
        float angle = body.getLinearVelocity().nor().angle()-90;
        // ignore large spike after collision
        if (Math.abs(lastAngle - angle) > 10f){
            angle = lastAngle;
        } else {
            lastAngle = angle;
        }
        transform.set(body.getPosition(), angle);
    }

    @Override
    public void variableUpdate(float delta, float alpha) {
        if (!isInit)
            return;
        float lerpAngle = transform.getLerpAngle(alpha);
        float lerpX = transform.getLerpX(alpha);
        float lerpY = transform.getLerpY(alpha);

        rocketSprite.setRotation(180-lerpAngle);
        rocketSprite.setPosition(lerpX - HALF_WIDTH, lerpY);

        coneLight.setDirection(-lerpAngle - 90);
        final float radAngle = (-lerpAngle-90)*MathUtils.degreesToRadians;
        final float dX = LIGHT_OFFSET_X * MathUtils.cos(radAngle);
        final float dY = LIGHT_OFFSET_X * MathUtils.sin(radAngle);
        coneLight.setPosition(lerpX+dX, lerpY+dY);
    }
	
	public void draw(Batch batch){
		rocketSprite.draw(batch);
	}

	@Override
	public void reset() {
        isInit = false;
		body.setAngularVelocity(0);
		body.setLinearVelocity(0, 0);
		body.setTransform(-10, -100, 0);
		body.setAwake(false);
        coneLight.setPosition(-100, -100);
        isExploded = false;
    }
	
	public void explode(){
        if (isInit)
		    isExploded = true;
	}
	
	public boolean isExploded(){
		return isExploded;
	}

    public Transform getTransform() {
        return transform;
    }
}
