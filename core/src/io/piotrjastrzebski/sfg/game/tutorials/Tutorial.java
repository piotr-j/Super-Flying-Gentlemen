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

package io.piotrjastrzebski.sfg.game.tutorials;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.MathUtils;
import com.esotericsoftware.spine.Animation;
import com.esotericsoftware.spine.AnimationState;
import com.esotericsoftware.spine.AnimationStateData;
import com.esotericsoftware.spine.Event;
import com.esotericsoftware.spine.Skeleton;
import com.esotericsoftware.spine.SkeletonData;
import com.esotericsoftware.spine.SkeletonRenderer;

public class Tutorial {
    private Animation animation;
    private Skeleton skeleton;
    private AnimationState animState;
    private boolean enabled;
    private boolean done;
    private float alpha;
    private float x;
    private float hideTimer;
    private boolean isHiding;
    private boolean isVisible;

    public Tutorial(SkeletonData skeletonData, AnimationStateData stateData){
        skeleton = new Skeleton(skeletonData);
        animation = skeletonData.findAnimation("animation");
        animState = new AnimationState(stateData);
    }

    public void init(float x, float y){
        this.x = x;
        skeleton.setPosition(x, y);
        animState.setAnimation(0, animation, true);
        animState.apply(skeleton);
        skeleton.updateWorldTransform();
        enabled = true;
        done = false;
        alpha = 0;
        skeleton.getColor().a = alpha;
        isHiding = false;
        isVisible = true;
    }

    public void update(float delta, float x){
        if (!enabled)
            return;
        if (isHiding){
            hideTimer += delta;
            if (hideTimer >= 0.5f){
                alpha = 0;
                disable();
            } else {
                alpha = 1-hideTimer*2;
            }
        } else {
            if (alpha < 1) {
                alpha += delta;
                alpha = MathUtils.clamp(alpha, 0, 1);
            } else {
                alpha = 1;
            }
        }
        skeleton.getColor().a = alpha;
        // at initial position until it gets to x
        if (x > this.x) {
            this.x = x;
            skeleton.setX(x);
        }
        animState.update(delta);
        animState.apply(skeleton);
        skeleton.updateWorldTransform();
    }

    public void draw(Batch batch, SkeletonRenderer skeletonRenderer){
        if (!enabled)
            return;
        skeletonRenderer.draw(batch, skeleton);
    }

    public void hide() {
        if (!enabled){
            done = true;
            return;
        }
        animState.addListener(new AnimationState.AnimationStateListener() {
            @Override
            public void event(int trackIndex, Event event) {}
            @Override
            public void complete(int trackIndex, int loopCount) {
                isHiding = true;
            }
            @Override
            public void start(int trackIndex) {}
            @Override
            public void end(int trackIndex) {}
        });
    }

    public void disable(){
        enabled = false;
        done = true;
        isVisible = false;
    }

    public boolean isDisabled(){
        return done;
    }

    public boolean isVisible() {
        return isVisible;
    }
}
