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
import com.badlogic.gdx.utils.Pool;

import io.piotrjastrzebski.sfg.utils.Assets;
import io.piotrjastrzebski.sfg.utils.Locator;
import io.piotrjastrzebski.sfg.utils.Transform;

public class TutorialLabel implements Pool.Poolable, Position, VariableUpdatable {
    private Pickup pickup;
    private Vector2 position;
    private boolean isAttached;
    private boolean isVisibile;
    private boolean isShowing;
    private boolean isHiding;
    private Sprite bubble;
    private float animTimer;
    private float width;
    private float height;

    public TutorialLabel(){
        position = new Vector2();
    }

    public void init(Pickup pickup){
        final Assets assets = Locator.getAssets();
        this.pickup = pickup;
        position.set(pickup.getPos());
        isVisibile = false;
        isShowing = false;
        isHiding = false;
        isAttached = true;
        switch (pickup.getType()){
            case TOXIC:
                bubble = assets.getScaledSprite("bubble_toxic");
                break;
            case SHIELD:
                bubble = assets.getScaledSprite("bubble_shield");
                break;
            case BOOST:
                bubble = assets.getScaledSprite("bubble_boost");
                break;
            case LIVES:
                bubble = assets.getScaledSprite("bubble_health");
                break;
            default: break;
        }
        bubble.setAlpha(0);
        width = bubble.getWidth();
        height = bubble.getHeight();
    }

    public void show(){
        if (isShowing || isVisibile)
            return;
        isVisibile = true;
        animTimer = 0;
        isShowing = true;
    }

    public void hide(){
        if (isHiding)
            return;
        animTimer = 0;
        isHiding = true;
    }

    @Override
    public void variableUpdate(float delta, float alpha) {
        if (bubble == null)
            return;
        if (isAttached) {
            final Transform pickupTransform = pickup.getTransform();
            float x = pickupTransform.getLerpX(alpha);
            float y = pickupTransform.getLerpY(alpha);
            position.set(x, y);
            bubble.setPosition(x, y);
            if (pickup.isExploded()){
                hide();
                isAttached = false;
            }
        }
        if (isHiding){
            animTimer += delta;
            if (animTimer >= 0.5f){
                isHiding = false;
                bubble.setAlpha(0);
                bubble.setSize(width*0.5f, height*0.5f);
            } else {
                float value = 1-animTimer*2;
                bubble.setAlpha(value);
                bubble.setSize(width*value, height*value);
            }
        } else if (isShowing){
            animTimer += delta;
            if (animTimer >= 0.5f){
                isShowing = false;
                bubble.setAlpha(1);
                bubble.setSize(width, height);
            } else {
                float value = animTimer*2;
                bubble.setAlpha(value);
                bubble.setSize(width*(0.5f+animTimer), height*(0.5f+animTimer));
            }
        }
    }

    public void draw(Batch batch){
        bubble.draw(batch);
    }


    @Override
    public void reset() {
        this.pickup = null;
        isVisibile = false;
        isShowing = false;
        isHiding = false;
    }

    @Override
    public Vector2 getPos() {
        return position;
    }

}
