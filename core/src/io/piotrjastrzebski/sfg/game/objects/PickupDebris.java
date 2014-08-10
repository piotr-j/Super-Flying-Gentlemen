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
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;

import io.piotrjastrzebski.sfg.utils.Assets;
import io.piotrjastrzebski.sfg.utils.Collision;
import io.piotrjastrzebski.sfg.utils.Locator;
import io.piotrjastrzebski.sfg.utils.Transform;

public class PickupDebris implements Pool.Poolable, Position, FixedUpdatable, VariableUpdatable {
    private final static int CENTER_PART = 0;
    private Array<DebrisPart> parts;
    // width of the body also offset for drawing
    private final static float[] WIDTHS = {
            0.25f, 0.15f, 0.15f, 0.3f, 0.3f
    };
    // height of the body also offset for drawing
    private final static float[] HEIGHTS = {
            0.3f, 0.2f, 0.2f, 0.15f, 0.15f
    };
    private final static String[] REGION_NAMES = {
            "pickup_middle", "pickup_top", "pickup_bottom",
            "wing_left", "wing_right"
    };
    // width of the body also offset for drawing
    private final static float[] Y_OFFSETS = {
            0f, 1.5f, -1.5f, 0f, 0f
    };
    // height of the body also offset for drawing
    private final static float[] X_OFFSETS = {
            0f, 0f, 0f, -1.5f, 1.5f
    };

    public PickupDebris(){
        parts = new Array<DebrisPart>();
        final World world = Locator.getWorld();
        final Assets assets = Locator.getAssets();
        for (int id = 0; id < WIDTHS.length; id++) {
            final Body body = createBody(world, id);
            final Sprite sprite = assets.getScaledSprite(REGION_NAMES[id]);
            sprite.setOriginCenter();
            parts.add(new DebrisPart(body, sprite));
        }
    }

    public void init(Vector2 pos){
        for (int id = 0; id < parts.size; id++) {
            parts.get(id).init(
                    pos.x + X_OFFSETS[id],
                    pos.y + Y_OFFSETS[id]);
        }
    }

    public void draw(Batch batch){
        for(DebrisPart part:parts){
            part.draw(batch);
        }
    }

    public void reset(){
        for(DebrisPart part:parts){
            part.reset();
        }
    }

    @Override
    public Vector2 getPos() {
        return parts.get(CENTER_PART).getPos();
    }

    private Body createBody(World world, int id){
        final BodyDef obstacleBodyDef = new BodyDef();
        obstacleBodyDef.type = BodyDef.BodyType.DynamicBody;
        final Body body = world.createBody(obstacleBodyDef);
        // large dampening so tiny bodies dont explode super fast
        body.setLinearDamping(0.75f);
        body.setAngularDamping(0.75f);

        final PolygonShape rect = new PolygonShape();
        rect.setAsBox(WIDTHS[id], HEIGHTS[id]);

        final FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = rect;
        fixtureDef.density = 3f;
        fixtureDef.friction = 0.25f;
        fixtureDef.restitution = 0.25f;
        fixtureDef.filter.categoryBits = Collision.BODY_PART;
        fixtureDef.filter.maskBits = Collision.MASK_BODY_PART;
        body.createFixture(fixtureDef);
        // Clean up
        rect.dispose();
        return body;
    }

    @Override
    public void fixedUpdate() {
        for(DebrisPart part:parts){
            part.update();
        }
    }

    @Override
    public void variableUpdate(float delta, float alpha) {
        for(DebrisPart part:parts){
            part.update();
        }
    }

    public class DebrisPart implements FixedUpdatable, VariableUpdatable {
        private Body body;
        private Sprite sprite;
        private float yOffset;
        private float xOffset;
        private Transform transform;

        public DebrisPart(Body body, Sprite sprite){
            this.body = body;
            this.sprite = sprite;
            yOffset = sprite.getWidth()/2;
            xOffset = sprite.getHeight()/2;
            transform = new Transform();
            reset();
        }

        public void init(float x, float y){
            body.setTransform(x, y, 0);
            body.setActive(true);
            body.setLinearVelocity(0, 0);
            body.applyLinearImpulse(
                    MathUtils.random(-15, 15)*body.getMass(),
                    MathUtils.random(-10, 30)*body.getMass(), 0, 0, true);
            body.setAngularVelocity(0);
            body.applyAngularImpulse(MathUtils.random(-0.4f, 0.4f), true);
            transform.init(x, y, 0);
            update();
        }

        public void update(){
            final Vector2 pos = body.getPosition();
            sprite.setPosition(pos.x-yOffset, pos.y-xOffset);
            sprite.setRotation(body.getAngle() * MathUtils.radiansToDegrees);
        }

        public void draw(Batch batch){
            sprite.draw(batch);
        }

        public void reset(){
            body.setTransform(0, -10, 90);
            body.setActive(false);
        }

        public Vector2 getPos() {
            return body.getPosition();
        }

        @Override
        public void fixedUpdate() {
            transform.set(body.getPosition(), body.getAngle() * MathUtils.radiansToDegrees);
        }

        @Override
        public void variableUpdate(float delta, float alpha) {
            float lerpAngle = transform.getLerpAngle(alpha);
            float lerpX = transform.getLerpX(alpha);
            float lerpY = transform.getLerpY(alpha);

            sprite.setPosition(lerpX-xOffset, lerpY-yOffset);
            sprite.setRotation(lerpAngle);
        }
    }
}
