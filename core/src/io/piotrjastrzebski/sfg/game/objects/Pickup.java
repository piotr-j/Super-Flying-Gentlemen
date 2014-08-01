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

import io.piotrjastrzebski.sfg.game.objects.obstacles.SensorType;
import io.piotrjastrzebski.sfg.utils.Collision;
import io.piotrjastrzebski.sfg.utils.Config;
import io.piotrjastrzebski.sfg.utils.Locator;
import io.piotrjastrzebski.sfg.utils.Utils;
import box2dLight.PointLight;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.esotericsoftware.spine.Animation;
import com.esotericsoftware.spine.AnimationState;
import com.esotericsoftware.spine.AnimationStateData;
import com.esotericsoftware.spine.Skeleton;
import com.esotericsoftware.spine.SkeletonData;
import com.esotericsoftware.spine.SkeletonRenderer;

public class Pickup implements Poolable, Position {
    public enum Type {
		LIVES, BOOST
	}
	private Body body;
    private boolean done = false;
    private Config config;
    private boolean pickedUp = false;
    private World world;
	private float value = 0;
	private final Fixture f;
	private Type type;
	private boolean isExploded = false;
	private boolean init = false;
	private Skeleton pickupSkeleton;
	private Animation pickupAnim;
	private Animation idleAnim;
	private Animation idleEmptyAnim;
	private AnimationState pickupAnimState;
	private PointLight pointLight;

	public Pickup() {
		this.config = Locator.getConfig();
		this.world = Locator.getWorld();
		
		pointLight = new PointLight(Locator.getRayHandler(), 8, Color.WHITE, 4, -100, -100);
		pointLight.setXray(true);
		// create bodies
		BodyDef obstacleBodyDef = new BodyDef(); 
		obstacleBodyDef.type = BodyType.DynamicBody;
		// y position at the top of bottom tiles
		// +3 for the ground and radius
		body = world.createBody(obstacleBodyDef);
		
		CircleShape circle = new CircleShape();
		circle.setRadius(1f);

		// Create a fixture definition to apply our shape to
		FixtureDef fixtureDef = new FixtureDef();
		fixtureDef.shape = circle;
		fixtureDef.filter.categoryBits = Collision.PICKUP;
		fixtureDef.filter.maskBits = Collision.MASK_PICKUP;
		// Create our fixture and attach it to the body
		f = body.createFixture(fixtureDef);
		body.setUserData(this);
		body.setGravityScale(0);

        final SkeletonData skeletonData = Locator.getAssets().getPickupSkeletonData();
        pickupSkeleton = new Skeleton(skeletonData);

		idleAnim = skeletonData.findAnimation("idle");
		pickupAnim = skeletonData.findAnimation("pickup");
		idleEmptyAnim = skeletonData.findAnimation("idle_empty");
        final AnimationStateData stateData = Locator.getAssets().getPickupAnimationData();
        stateData.setMix(pickupAnim, idleEmptyAnim, 1);
		pickupAnimState = new AnimationState(stateData);
		body.setActive(false);
	}

	public void init(float x, float y){
		if (MathUtils.randomBoolean()){
			init(Type.BOOST, x, y);
		} else {
			init(Type.LIVES, x, y);
		}
	}

	public void init(Type type, float x, float y){
		this.type = type;
        init = true;
        isExploded = false;
		body.setActive(true);
		body.setTransform(x, y, 0);
		body.setLinearVelocity(0, 0);
		body.setAngularVelocity(0);

		f.setUserData(SensorType.PICKUP);
		switch (type) {
		case BOOST:
			value = Utils.randomIntRange(config.getPickupBoost());
			pickupSkeleton.setSkin("boost");
			pointLight.setColor(1, 0.8f, 0.2f, 1);
			break;
		case LIVES:
			value = Utils.randomIntRange(config.getPickupLive());
			pickupSkeleton.setSkin("live");
			pointLight.setColor(1, 0.3f, 0.0f, 1);
			break;
		default:
			break;
		}
		pickupSkeleton.getRootBone().setRotation(0);		
		pickupSkeleton.setPosition(x, y);
		pickupAnimState.setAnimation(0, idleAnim, true);

        pointLight.setPosition(x, y);
	}

    public void updateAngle() {
        final Vector2 v = body.getLinearVelocity();
        float angle = v.nor().angle()-90;
        pickupSkeleton.getRootBone().setRotation(angle);
    }

	public Type getType(){
		return type;
	}

	public float getValue(){
		return value ;
	}
	
	@Override
	public void reset() {
        body.setTransform(0, -10, 90);
		body.setActive(false);
		pickedUp = false;
        init = false;
        pointLight.setPosition(-100, -100);

	}
	
	public void update(float delta){
        if (!init)
            return;
		final Vector2 pos = body.getPosition();
		pickupSkeleton.setPosition(pos.x-0.1f, pos.y);
		pickupAnimState.update(delta);
		pickupAnimState.apply(pickupSkeleton);
		pickupSkeleton.updateWorldTransform();
		
		pointLight.setPosition(pos.x, pos.y);
	}
	
	public void draw(Batch batch, SkeletonRenderer skeletonRenderer){
        if (!init)
            return;
		skeletonRenderer.draw(batch, pickupSkeleton);
	}

	public boolean pickup(){
		if (!pickedUp){
			pickedUp = true;
			pickupAnimState.setAnimation(0, pickupAnim, false);
			pickupAnimState.addAnimation(0, idleEmptyAnim, true, 1);
			return true;
		}
		return false;
	}

    public Vector2 getPos() {
        return body.getPosition();
    }

    public void explode(){
		isExploded = true;
	}
	
	public boolean isExploded(){
		return isExploded;
	}
}
