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

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Pool;
import com.esotericsoftware.spine.Animation;
import com.esotericsoftware.spine.AnimationState;
import com.esotericsoftware.spine.Bone;
import com.esotericsoftware.spine.Skeleton;
import com.esotericsoftware.spine.SkeletonRenderer;
import com.esotericsoftware.spine.Slot;

import io.piotrjastrzebski.sfg.utils.Locator;

public abstract class EndPoint implements Pool.Poolable {
    final static float END_POINT_HALF_WIDTH = 1.5f;
    protected final World world;

    protected Animation animation;
    protected Body body;

    protected boolean isExecuting = false;
    protected boolean isBloodied = false;

    protected Skeleton skeleton;
    protected AnimationState animationState;
    protected Bone bone;
    protected float y;
    protected float x;
    protected float boneOffset;

    public EndPoint(){
        this.world = Locator.getWorld();
    }

    protected Bone getBone(Skeleton skeleton, String name){
        for(Slot slot: skeleton.getSlots()){
            if (slot.getData().getName().equals(name)){
                return slot.getBone();
            }
        }
        return null;
    }

    public abstract void init(float x, float y, boolean up);

    public void update(float delta){
        // we dont have an idle animation, so we just dont animate until we need to
        if (isExecuting){
            animationState.update(delta);
            animationState.apply(skeleton);
            skeleton.updateWorldTransform();
        }
    }

    public void draw(Batch batch, SkeletonRenderer skeletonRenderer){
        skeletonRenderer.draw(batch, skeleton);
    }

    public void execute(boolean bloodied){
        isBloodied = bloodied;
        isExecuting = true;
    }

    @Override
    public void reset() {
        skeleton.setPosition(0, -10);
        body.setTransform(0, -10, 0);
    }

    public void destroy(){
        if (body != null){
            world.destroyBody(body);
            body = null;
        }
    }
}
