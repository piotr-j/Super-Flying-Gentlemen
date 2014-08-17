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
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Pool;
import com.esotericsoftware.spine.Animation;
import com.esotericsoftware.spine.AnimationState;
import com.esotericsoftware.spine.Event;
import com.esotericsoftware.spine.Skeleton;
import com.esotericsoftware.spine.SkeletonData;
import com.esotericsoftware.spine.SkeletonRenderer;

import io.piotrjastrzebski.sfg.utils.Assets;
import io.piotrjastrzebski.sfg.utils.Locator;

public class ShieldBreak implements VariableUpdatable, Position, Pool.Poolable {
    private Animation animation;
    private Skeleton skeleton;
    private AnimationState animState;
    private Vector2 position;
    private boolean isComplete;

    public ShieldBreak(){
        final Assets assets = Locator.getAssets();
        final SkeletonData skeletonData = assets.getSkeletonData(Assets.Animations.SHIELD_BREAK);
        skeleton = new Skeleton(skeletonData);
        animation = skeletonData.findAnimation("animation");
        animState = new AnimationState(assets.getAnimationStateData(Assets.Animations.SHIELD_BREAK));
        // plays once and can be recycled
        animState.addListener(new AnimationState.AnimationStateListener() {
            @Override
            public void event(int trackIndex, Event event) {}
            @Override
            public void complete(int trackIndex, int loopCount) {
                isComplete = true;
            }
            @Override
            public void start(int trackIndex) {}
            @Override
            public void end(int trackIndex) {}
        });
        position = new Vector2();
    }

    public void init(float x, float y){
        isComplete = false;
        position.set(x, y);
        skeleton.setPosition(x, y);
        animState.setAnimation(0, animation, false);
        animState.apply(skeleton);
        skeleton.updateWorldTransform();
    }

    @Override
    public void variableUpdate(float delta, float alpha) {
        animState.update(delta);
        animState.apply(skeleton);
        skeleton.updateWorldTransform();
    }

    public void draw(Batch batch, SkeletonRenderer skeletonRenderer){
        skeletonRenderer.draw(batch, skeleton);
    }

    @Override
    public void reset() {
        position.set(-100, -100);
        skeleton.setPosition(-100, -100);
    }

    public boolean isComplete(){
        return isComplete;
    }

    @Override
    public Vector2 getPos() {
        return position;
    }
}
