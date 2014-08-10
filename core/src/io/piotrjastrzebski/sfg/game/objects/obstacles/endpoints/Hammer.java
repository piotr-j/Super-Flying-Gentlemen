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

package io.piotrjastrzebski.sfg.game.objects.obstacles.endpoints;

import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.esotericsoftware.spine.AnimationState;
import com.esotericsoftware.spine.Event;
import com.esotericsoftware.spine.Skeleton;
import com.esotericsoftware.spine.SkeletonData;

import io.piotrjastrzebski.sfg.events.EventType;
import io.piotrjastrzebski.sfg.utils.Assets;
import io.piotrjastrzebski.sfg.utils.Locator;
import io.piotrjastrzebski.sfg.utils.SoundManager;

public class Hammer extends EndPoint {
    private final static String SLOT_NAME = "obst_hammer";

    public Hammer(){
        super();
        final PolygonShape boxShape = new PolygonShape();
        final BodyDef hammerBodyDef = new BodyDef();
        hammerBodyDef.type = BodyDef.BodyType.KinematicBody;
        hammerBodyDef.position.set(0, 0);
        body = world.createBody(hammerBodyDef);
        boxShape.setAsBox(1.5f, 1.5f);
        body.createFixture(boxShape, 0);
        boxShape.dispose();

        // setup animation
        final Assets assets = Locator.getAssets();
        final SkeletonData skeletonData = assets.getSkeletonData(Assets.Animations.OBST_HAMMER);
        skeleton = new Skeleton(skeletonData);
        animation = skeletonData.findAnimation("trigger");
        animationState = new AnimationState(assets.getAnimationStateData(Assets.Animations.OBST_HAMMER));
        animationState.addListener(new AnimationState.AnimationStateListener() {
            @Override
            public void start(int trackIndex) {}
            @Override
            public void event(int trackIndex, Event event) {
                if (event.getData().getName().equals("smashed")){
                    if (isBloodied){
                        skeleton.setAttachment("obst_hammer", "hammer_hammer_bloodied");
                    }
                }
            }
            @Override
            public void end(int trackIndex) {}
            @Override
            public void complete(int trackIndex, int loopCount) {}
        });

        bone = getBone(skeleton, SLOT_NAME);
    }

    public void init(float x, float y, boolean up){
        this.y = y;
        this.x = x;
        isExecuting = false;
        skeleton.setBonesToSetupPose();
        skeleton.setAttachment("obst_hammer", "hammer_hammer");
        // hammer is facing up
        skeleton.setX(x - END_POINT_HALF_WIDTH);
        animationState.setAnimation(0, animation, false);
        skeleton.setFlipY(up);
        if (up){
            boneOffset = 1.6f;
        } else {
            boneOffset = -1.6f ;
        }
        skeleton.setY(y);
        animationState.apply(skeleton);
        skeleton.updateWorldTransform();
        body.setTransform(x, y+bone.getWorldY()+boneOffset, 0);
    }

    public void update(float delta){
        super.update(delta);
        // not ideal, better to use impulses, but it will do
        body.setTransform(x, y+bone.getWorldY()+boneOffset, 0);
    }

    @Override
    public void execute(boolean bloodied) {
        super.execute(bloodied);
        Locator.getEvents().queueEvent(EventType.PLAY_SOUND, SoundManager.HAMMER);
    }
}
