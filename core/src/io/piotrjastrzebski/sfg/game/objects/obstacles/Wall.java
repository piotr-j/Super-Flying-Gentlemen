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

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.physics.box2d.Filter;
import com.badlogic.gdx.utils.Pool;

import io.piotrjastrzebski.sfg.game.objects.Position;
import io.piotrjastrzebski.sfg.utils.Collision;
import io.piotrjastrzebski.sfg.utils.Locator;

public class Wall implements Position, Pool.Poolable{
    private boolean isSmashed;

    private enum Shape {BOT_L, LEFT_T, RIGHT_T}
    private boolean init = false;
    private Vector2 pos;

    private Array<WallPart> parts;

    public Wall(float maxHeight) {
        pos = new Vector2();
        parts = new Array<WallPart>();
        final World world = Locator.getWorld();
        WallPart part;
        part = new WallPart(world, Shape.BOT_L);
        parts.add(part);
        boolean right = true;
        // -3 for height of single T part + bottom
        float offset = 1;
        while (offset < maxHeight-3){
            if (right){
                part = new WallPart(world, Shape.RIGHT_T);
            } else {
                part = new WallPart(world, Shape.LEFT_T);
            }
            parts.add(part);
            offset += 2; // part height
            right = !right;
        }
    }

    public void init(float x, float y, float height){
        pos.set(x, y);
        init = true;
        float offset = 1; // 1 bottom part height
        parts.get(0).init(x, y + offset + 0.5f); // 0.5f so its flush with y
        int id = 1;
        // -3 for height of single T part + bottom
        while (offset < height-3){
            offset += 2; // part height
            parts.get(id).init(x, y+offset);
            id++;
        }
        isSmashed = false;
    }

    public void update(float delta){
        if (!init)
            return;
        for(WallPart part: parts){
            part.update(delta);
        }
    }

    public void draw(Batch batch){
        if (!init)
            return;
        for(WallPart part: parts){
            part.draw(batch);
        }
    }

    @Override
    public Vector2 getPos() {
        return pos;
    }

    @Override
    public void reset() {
        init = false;
        for(WallPart part: parts){
            part.reset();
        }
    }

    public void smash(){
        isSmashed = true;
    }

    public boolean isSmashed(){
        return isSmashed;
    }

    public class WallPart {
        private final static float FRICTION = 0.3f;
        private final static float DENSITY = 0.01f;

        private Sprite graphic;
        private Body body;
        private float xOffset = 0;
        private float yOffset = 0;

        public WallPart(World world, Shape shape){
            final BodyDef bodyDef = new BodyDef();
            bodyDef.type = BodyDef.BodyType.DynamicBody;
            bodyDef.position.set(-10, -20);
            body = world.createBody(bodyDef);
            body.setUserData(Wall.this);
            body.setActive(false);
            final PolygonShape bodyShape = new PolygonShape();
            Fixture f;
            Filter filter = new Filter();
            filter.categoryBits= Collision.PICKUP;
            filter.maskBits = Collision.MASK_PICKUP;

            switch (shape){
                case BOT_L:
                    graphic = Locator.getAssets().getScaledSprite("wall_bot_L");
                    bodyShape.setAsBox(0.5f, 0.5f, new Vector2(0.5f, -0.5f), 0);
                    f = body.createFixture(bodyShape, DENSITY);
                    f.setFriction(FRICTION);
                    f.setFilterData(filter);
                    bodyShape.setAsBox(0.5f, 1, new Vector2(-0.5f, 0), 0);
                    f = body.createFixture(bodyShape, DENSITY);
                    f.setFriction(FRICTION);
                    f.setFilterData(filter);
                    xOffset = 1;
                    yOffset = 1;
                    break;
                case LEFT_T:
                    graphic = Locator.getAssets().getScaledSprite("wall_front_T");
                    bodyShape.setAsBox(0.5f, 0.5f, new Vector2(0.5f, 0), 0);
                    f = body.createFixture(bodyShape, DENSITY);
                    f.setFriction(FRICTION);
                    f.setFilterData(filter);
                    bodyShape.setAsBox(0.5f, 1.5f, new Vector2(-0.5f, 0), 0);
                    f = body.createFixture(bodyShape, DENSITY);
                    f.setFriction(FRICTION);
                    f.setFilterData(filter);
                    xOffset = 1;
                    yOffset = 1.5f;
                    break;
                case RIGHT_T:
                    graphic = Locator.getAssets().getScaledSprite("wall_back_T");
                    bodyShape.setAsBox(0.5f, 0.5f, new Vector2(-0.5f, 0), 0);
                    f = body.createFixture(bodyShape, DENSITY);
                    f.setFriction(FRICTION);
                    f.setFilterData(filter);
                    bodyShape.setAsBox(0.5f, 1.5f, new Vector2(0.5f, 0), 0);
                    f = body.createFixture(bodyShape, DENSITY);
                    f.setFriction(FRICTION);
                    f.setFilterData(filter);
                    xOffset = 1;
                    yOffset = 1.5f;
                    break;
                default: break;
            }
            graphic.setOriginCenter();
            bodyShape.dispose();
        }

        public void init(float x, float y){
            body.setTransform(x, y, 0);
            body.setLinearVelocity(0, 0);
            body.setAngularVelocity(0);
            graphic.setPosition(x - xOffset, y - yOffset);
            graphic.setRotation(0);
            body.setActive(true);
        }

        public void update(float delta){
            final Vector2 pos = body.getPosition();
            graphic.setPosition(pos.x - xOffset, pos.y - yOffset);
            graphic.setRotation(body.getAngle() * MathUtils.radiansToDegrees);
        }

        public void draw(Batch batch){
            graphic.draw(batch);
        }

        public void reset(){
            body.setTransform(-10, -10, 0);
            body.setLinearVelocity(0, 0);
            body.setAngularVelocity(0);
            body.setActive(false);
        }
    }
}
