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
import com.esotericsoftware.spine.Animation;
import com.esotericsoftware.spine.AnimationState;
import com.esotericsoftware.spine.AnimationStateData;
import com.esotericsoftware.spine.Skeleton;
import com.esotericsoftware.spine.SkeletonData;
import com.esotericsoftware.spine.SkeletonRenderer;

public class Tutorial {
    private Animation animation;
    private Skeleton skeleton;
    private AnimationState animState;
    private boolean enabled;
    private float alpha;

    public Tutorial(SkeletonData skeletonData, AnimationStateData stateData){
        skeleton = new Skeleton(skeletonData);
        animation = skeletonData.findAnimation("animation");
        animState = new AnimationState(stateData);
    }

    public void init(float x, float y){
        skeleton.setPosition(x, y);
        animState.setAnimation(0, animation, true);
        animState.apply(skeleton);
        skeleton.updateWorldTransform();
        enabled = true;
        alpha = 0;
        skeleton.getColor().a = alpha;
    }

    public void update(float delta, float x){
        if (!enabled)
            return;
        if (alpha < 1) {
            alpha += delta;
        } else {
            alpha = 1;
        }
        skeleton.getColor().a = alpha;
        skeleton.setX(x);
        animState.update(delta);
        animState.apply(skeleton);
        skeleton.updateWorldTransform();
    }

    public void draw(Batch batch, SkeletonRenderer skeletonRenderer){
        if (!enabled)
            return;
        skeletonRenderer.draw(batch, skeleton);
    }

    public void disable(){
        enabled = false;
    }

}
