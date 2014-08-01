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
import com.esotericsoftware.spine.Animation;
import com.esotericsoftware.spine.AnimationState;
import com.esotericsoftware.spine.AnimationStateData;
import com.esotericsoftware.spine.Skeleton;
import com.esotericsoftware.spine.SkeletonData;
import com.esotericsoftware.spine.SkeletonRenderer;

import io.piotrjastrzebski.sfg.game.PlayerStats;
import io.piotrjastrzebski.sfg.utils.Assets;
import io.piotrjastrzebski.sfg.utils.Locator;

public class PlayerAnimation {
	private Animation fallAnimation;
	private Animation shootAnimation;
	private Animation boostAnimation;
	private Skeleton skeleton;
	private AnimationState animState;
	
	public PlayerAnimation(){

        final SkeletonData skeletonData = Locator.getAssets().getPlayerSkeletonData();
		skeleton = new Skeleton(skeletonData);
		
		fallAnimation = skeletonData.findAnimation("fall");
		shootAnimation = skeletonData.findAnimation("shoot");
		boostAnimation = skeletonData.findAnimation("boost");

        final AnimationStateData stateData = Locator.getAssets().getPlayerAnimationData();

        stateData.setMix(fallAnimation, shootAnimation, 0.5f);
        stateData.setMix(shootAnimation, fallAnimation, 0.5f);

        stateData.setMix(boostAnimation, fallAnimation, 0.5f);
        stateData.setMix(shootAnimation, boostAnimation, 0.5f);
		
		animState = new AnimationState(stateData);
	}
	
	public void init(float x, float y){
		// slight offset to better match the hitbox
		skeleton.setPosition(x, y-0.2f);
		animState.setAnimation(0, fallAnimation, true);
		animState.apply(skeleton);
		skeleton.updateWorldTransform();
	}
	
	public void update(float delta, float x, float y){
		// slight offset to better match the hitbox
		skeleton.setPosition(x, y-0.2f);
		animState.update(delta);
		animState.apply(skeleton);
		skeleton.updateWorldTransform();
	}
	
	public void draw(Batch batch, SkeletonRenderer skeletonRenderer){
		skeletonRenderer.draw(batch, skeleton);
	}
	
	public void boost(){
		animState.setAnimation(0, boostAnimation, false);
		animState.addAnimation(0, fallAnimation, true, 0);
	}
	
	public void shoot(){
		//TODO fix animation jump
		animState.setAnimation(0, shootAnimation, false);
		animState.addAnimation(0, fallAnimation, true, 0);
	}

	public void setSkin(PlayerStats.Skin skin) {
		skeleton.setSkin(skin.name);
	}

    public Skeleton getSkeleton(){
        return skeleton;
    }
}
