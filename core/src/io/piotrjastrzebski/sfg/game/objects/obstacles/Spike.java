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

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.esotericsoftware.spine.AnimationState;
import com.esotericsoftware.spine.Event;
import com.esotericsoftware.spine.Skeleton;
import com.esotericsoftware.spine.Slot;

import io.piotrjastrzebski.sfg.events.EventType;
import io.piotrjastrzebski.sfg.utils.Locator;
import io.piotrjastrzebski.sfg.utils.SoundManager;

public class Spike extends EndPoint {
    private final static String SLOT_NAME = "spike_top";
    private final static float SPIKE_HEIGHT = 7;

    private final static float[] SPIKE_VERTICES = {
            -1.5f, 0,
            1.5f, 0,
            0, 6.0f};
    // angle for flipping the spike around
    float angle;

    public Spike(SpikePool pool){
        super(pool);
        final BodyDef spikeBodyDef = new BodyDef();
        spikeBodyDef.type = BodyDef.BodyType.KinematicBody;
        spikeBodyDef.position.set(0, 0);
        body = world.createBody(spikeBodyDef);
        final PolygonShape spikeShape = new PolygonShape();
        spikeShape.set(SPIKE_VERTICES);
        body.createFixture(spikeShape, 0);
        spikeShape.dispose();

        // setup animation
        skeleton = new Skeleton(Locator.getAssets().getSpikeSkeletonData());
        animation = Locator.getAssets().getSpikeSkeletonData().findAnimation("trigger");
        animationState = new AnimationState(Locator.getAssets().getSpikeAnimationData());
        animationState.addListener(new AnimationState.AnimationStateListener() {
            @Override
            public void start(int trackIndex) {}
            @Override
            public void event(int trackIndex, Event event) {
                if (event.getData().getName().equals("smashed")){
                    if (isBloodied){
                        skeleton.setAttachment("spike_mid", "spike_mid_bloodied");
                        skeleton.setAttachment("spike_top", "spike_top_bloodied");
                        skeleton.setAttachment("spike_bot", "spike_bot_bloodied");
                    }
                }
            }
            @Override
            public void end(int trackIndex) {}
            @Override
            public void complete(int trackIndex, int loopCount) {}
        });

        for(Slot slot: skeleton.getSlots()){
            if (slot.getData().getName().equals(SLOT_NAME)){
                bone = slot.getBone();
                break;
            }
        }

    }


    public void init(float x, float y, boolean up){
        this.y = y;
        this.x = x;
        isExecuting = false;
        skeleton.setBonesToSetupPose();
        skeleton.setAttachment("spike_bot", "spike_bot");
        skeleton.setAttachment("spike_mid", "spike_mid");
        skeleton.setAttachment("spike_top", "spike_top");
        skeleton.setX(x - END_POINT_HALF_WIDTH);
        animationState.setAnimation(0, animation, false);
        skeleton.setFlipY(up);
        if (up){
            boneOffset = - 4.4f;
            angle = 0;
        } else {
            boneOffset = 4.4f;
            angle = 180 * MathUtils.degreesToRadians;
        }

        skeleton.setY(y);
        animationState.apply(skeleton);
        skeleton.updateWorldTransform();
        body.setTransform(x, y+bone.getWorldY()+boneOffset, angle);
    }

    public void update(float delta){
        super.update(delta);
        // not ideal, better to use impulses, but it will do
        body.setTransform(x, y+bone.getWorldY()+boneOffset, angle);
    }

    @Override
    public void execute(boolean bloodied) {
        super.execute(bloodied);
        Locator.getEvents().queueEvent(EventType.PLAY_SOUND, SoundManager.SPIKE);
    }
}
