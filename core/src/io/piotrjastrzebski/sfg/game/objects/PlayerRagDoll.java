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
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.esotericsoftware.spine.Bone;
import com.esotericsoftware.spine.Skeleton;
import com.esotericsoftware.spine.Slot;

import io.piotrjastrzebski.sfg.game.PlayerStats;
import io.piotrjastrzebski.sfg.screen.GameScreen;
import io.piotrjastrzebski.sfg.utils.Assets;
import io.piotrjastrzebski.sfg.utils.Collision;
import io.piotrjastrzebski.sfg.utils.Locator;
import io.piotrjastrzebski.sfg.utils.Transform;

public class PlayerRagDoll implements FixedUpdatable, VariableUpdatable {
    // ids for quick access
    private final static int HAT_ID = 0;
    private final static int TORSO_ID = 2;
    private final static int ARM_UPPER_FRONT_ID = 5;
    private final static int ARM_UPPER_BACK_ID = 4;
    private final static int LEG_LOWER_FRONT_ID = 6;
    private final static int LEG_LOWER_BACK_ID = 7;
    private final static int CAPE_ID = 11;

    private final static String[] SLOT_NAMES = {
            "hat_skin", "launcher", "torso", "head",
            "arm_upper_back", "arm_upper_front", "arm_lower", "arm_lower2",
            "leg_upper_back", "leg_upper_front", "leg_lower_back", "leg_lower_front",
            "cape_skin"
    };
    private final static String[] REGION_NAMES = {
            "hat", "launcher", "broken_torso", "broken_head",
            "broken_arm_upper_back", "broken_arm_upper_front", "broken_arm_lower", "broken_arm_lower",
            "broken_leg_upper_back", "broken_leg_upper_front", "broken_leg_lower_back", "broken_leg_lower_front",
            "broken_cape"
    };
    // width of the body also offset for drawing
    private final static float[] WIDTHS = {
            0.2f, 0.3f, 0.4f, 0.2f,
            0.1f, 0.1f, 0.1f, 0.1f,
            0.15f, 0.15f, 0.15f, 0.15f,
            0.2f
    };
    // height of the body also offset for drawing
    private final static float[] HEIGHTS = {
            0.2f, 1.1f, 0.55f, 0.2f,
            0.3f, 0.3f, 0.4f, 0.4f,
            0.45f, 0.45f, 0.4f, 0.4f,
            0.4f
    };
    private Skeleton skeleton;
    private boolean isAttached;
    private Array<Part> parts;
    private PlayerStats.Skin lastSkin;
    private Assets assets;

    public PlayerRagDoll(Skeleton skeleton){
        this.skeleton = skeleton;
        assets = Locator.getAssets();
        final World world = Locator.getWorld();
        parts = new Array<Part>();
        // create parts for the slots we are interested in
        for (int i = 0; i < SLOT_NAMES.length; i++) {
            for (Slot slot:skeleton.getSlots()) {
                if (slot == null)
                    continue;
                if (slot.getData().getName().equals(SLOT_NAMES[i])){
                    final Sprite sprite = scaledSprite(assets.getRegion(REGION_NAMES[i]));
                    final Body body = createBody(world, i);
                    final Part part = new Part(body, slot, sprite, WIDTHS[i], HEIGHTS[i]);
                    parts.add(part);
                }
            }
        }
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
        // without this bodies are actually lower than expected when driven by animation
        body.setGravityScale(0);
        body.createFixture(fixtureDef);
        // Clean up
        rect.dispose();
        return body;
    }

    private Sprite scaledSprite(TextureRegion region){
        Sprite sprite = new Sprite(region);
        sprite.setSize(
                sprite.getWidth() * GameScreen.BOX2D_TO_PIXEL * 1.1f,
                sprite.getHeight() * GameScreen.BOX2D_TO_PIXEL * 1.1f);
        sprite.setOriginCenter();
        return sprite;
    }

    public void detach(){
        isAttached = false;
        for (int i = 0; i < parts.size; i++) {
            parts.get(i).detach();
        }
    }

    public void init(float x, float y, PlayerStats.Skin skin){
        isAttached = true;
        if (lastSkin == null || skin.id != lastSkin.id){
            lastSkin = skin;
            updateSkin(skin);
        }
        for (int i = 0; i < parts.size; i++) {
            parts.get(i).init(x, y);
        }
    }

    private void updateSkin(PlayerStats.Skin skin) {
        if (skin.id == PlayerStats.SKIN_BASIC) {
            parts.get(HAT_ID).setRegion(assets.getRegion("hat"));
        } else {
            parts.get(HAT_ID).setRegion(assets.getRegion("hat_" + skin.name));
        }
        if (skin.id == PlayerStats.SKIN_RUBY){
            parts.get(ARM_UPPER_BACK_ID).setRegion(assets.getRegion("broken_arm_upper_back_ruby"));
            parts.get(ARM_UPPER_FRONT_ID).setRegion(assets.getRegion("broken_arm_upper_front_ruby"));
            parts.get(LEG_LOWER_BACK_ID).setRegion(assets.getRegion("broken_leg_lower_back_ruby"));
            parts.get(LEG_LOWER_FRONT_ID).setRegion(assets.getRegion("broken_leg_lower_front_ruby"));
            parts.get(TORSO_ID).setRegion(assets.getRegion("broken_torso_ruby"));
            parts.get(CAPE_ID).setRegion(assets.getRegion("broken_cape_ruby"));
        } else {
            parts.get(ARM_UPPER_BACK_ID).setRegion(assets.getRegion(REGION_NAMES[ARM_UPPER_BACK_ID]));
            parts.get(ARM_UPPER_FRONT_ID).setRegion(assets.getRegion(REGION_NAMES[ARM_UPPER_FRONT_ID]));
            parts.get(LEG_LOWER_BACK_ID).setRegion(assets.getRegion(REGION_NAMES[LEG_LOWER_BACK_ID]));
            parts.get(LEG_LOWER_FRONT_ID).setRegion(assets.getRegion(REGION_NAMES[LEG_LOWER_FRONT_ID]));
            parts.get(TORSO_ID).setRegion(assets.getRegion(REGION_NAMES[TORSO_ID]));
            parts.get(CAPE_ID).setRegion(assets.getRegion(REGION_NAMES[CAPE_ID]));
        }
    }

    public void draw(Batch batch){
        if (!isAttached){
            for (int i = 0; i < parts.size; i++) {
                parts.get(i).draw(batch);
            }
        }
    }

    @Override
    public void fixedUpdate() {
        for (int i = 0; i < parts.size; i++) {
            parts.get(i).fixedUpdate();
        }
    }

    @Override
    public void variableUpdate(float delta, float alpha) {
        final float x = skeleton.getX();
        final float y = skeleton.getY();
        for (int i = 0; i < parts.size; i++) {
            parts.get(i).variableUpdate(x, y, alpha);
        }
    }

    public class Part implements Position, FixedUpdatable {
        final private Body body;
        final private Bone bone;
        final private Sprite sprite;
        final private float xOffset;
        final private float yOffset;
        final private Transform transform;

        public Part(Body body, Slot slot, Sprite sprite, float xOffset, float yOffset){
            bone = slot.getBone();
            this.sprite = sprite;
            this.xOffset = xOffset;
            this.yOffset = yOffset;
            this.body = body;
            transform = new Transform();
            body.setUserData(this);
        }

        public void init(float skelX, float skelY){
            body.setGravityScale(0);
            final float x = skelX + bone.getWorldX();
            final float y = skelY + bone.getWorldY();
            final float rotation = bone.getWorldRotation() * MathUtils.degreesToRadians;
            body.setTransform(x, y, rotation);
        }

        public void setRegion(TextureRegion region){
            sprite.setRegion(region);
        }

        public void detach(){
            body.setGravityScale(1);
            // explode the part in random direction
            // reset velocities as they spaz out due to transforms
            body.setLinearVelocity(0, 0);
            body.applyLinearImpulse(
                    MathUtils.random(-15, 15)*body.getMass(),
                    MathUtils.random(-10, 30)*body.getMass(), 0, 0, true);
            body.setAngularVelocity(0);
            body.applyAngularImpulse(MathUtils.random(-0.4f, 0.4f), true);

            transform.init(body.getPosition(), body.getAngle()*MathUtils.radiansToDegrees);
            variableUpdate(0, 0, 1);
        }

        public void draw(Batch batch){
            sprite.draw(batch);
        }

        @Override
        public Vector2 getPos() {
            return body.getPosition();
        }

        @Override
        public void fixedUpdate() {
            if (!isAttached){
                transform.set(body.getPosition(), body.getAngle() * MathUtils.radiansToDegrees);
            }
        }

        public void variableUpdate(float skelX, float skelY, float alpha) {
            if (isAttached){
                // this works for parts with bones near the center of the sprite
                // TODO fix position and rotation of body, so it centers in bones attachment position
                final float x = skelX + bone.getWorldX();
                final float y = skelY + bone.getWorldY();
                final float rotation = bone.getWorldRotation() * MathUtils.degreesToRadians;
                body.setTransform(x, y, rotation);
            } else {
                float lerpAngle = transform.getLerpAngle(alpha);
                float lerpX = transform.getLerpX(alpha);
                float lerpY = transform.getLerpY(alpha);

                sprite.setPosition(lerpX-xOffset, lerpY-yOffset);
                sprite.setRotation(lerpAngle);
            }
        }
    }
}
