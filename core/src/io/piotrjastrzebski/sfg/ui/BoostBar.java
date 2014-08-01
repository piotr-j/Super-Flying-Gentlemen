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

package io.piotrjastrzebski.sfg.ui;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.ui.Widget;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

import io.piotrjastrzebski.sfg.utils.Assets;

public class BoostBar extends Widget {


    private final float min;
    private final float max;
    private final float stepSize;
    private float value, animateFromValue;
    private float position;

    final Drawable bg;
    final Drawable fill;
    final Drawable knob;
    private float animateDuration;
    private float animateTime;
    private Interpolation animateInterpolation = Interpolation.linear;
    private boolean enabled;

    public BoostBar(Assets assets, float min, float max, float stepSize) {
        if (min > max)
            throw new IllegalArgumentException("min must be > max: " + min + " > " + max);
        if (stepSize <= 0)
            throw new IllegalArgumentException("stepSize must be > 0: " + stepSize);
        this.min = min;
        this.max = max;
        this.stepSize = stepSize;
        this.value = min;

        bg = new NinePatchDrawable(assets.getUiAtlas().createPatch("boost_slider"));
        fill = new TextureRegionDrawable(assets.getUIRegion("boost_slider_fill"));
        knob = new TextureRegionDrawable(assets.getUIRegion("boost_slider_knob"));
        animateDuration = 0.25f;
        animateFromValue = max;
        setSize(getPrefWidth(), getPrefHeight());
    }

    public void setAnimateFromValue(float animateFromValue) {
        this.animateFromValue = animateFromValue;
    }

    public void reset(){
        animateTime = 0;
        value = max;
    }

    @Override
    public void act (float delta) {
        super.act(delta);
        if (enabled)
            animateTime -= delta;
    }

    @Override
    public void draw (Batch batch, float parentAlpha) {
        float x = getX();
        float y = getY();
        float width = getWidth();
        float height = getHeight();
        float knobHeight = knob.getMinHeight();
        float knobWidth = knob.getMinWidth();
        float value = getVisualValue();

        batch.setColor(1, 1, 1, 1 * parentAlpha);

        bg.draw(batch, x, y + (int)((height - bg.getMinHeight()) * 0.5f), width, bg.getMinHeight());

        float positionWidth = width - (bg.getLeftWidth() + bg.getRightWidth());
        if (min != max) {
            position = (value - min) / (max - min) * (positionWidth - knobWidth);
            position = Math.min(positionWidth - knobWidth, position) + bg.getLeftWidth();
            position = Math.max(0, position);
        }

        knob.draw(batch, (int)(x + position), (int)(y + (height - knobHeight) * 0.5f), knobWidth, knobHeight);
        float offset = bg.getLeftWidth();
        fill.draw(batch, x + offset, y + (int)((height - fill.getMinHeight()) * 0.5f),
                (int)(position-knobWidth+3), fill.getMinHeight());
    }

    public float getVisualValue () {
        return animateInterpolation.apply(animateFromValue, value, 1 - animateTime / animateDuration);
    }

    public void setValue(float value){
        value = clamp(Math.round(value / stepSize) * stepSize);
        float oldValue = this.value;
        if (value == oldValue) return;
        float oldVisualValue = getVisualValue();
        this.value = value;
        animateFromValue = oldVisualValue;
        animateTime = animateDuration;
    }

    private float clamp (float value) {
        return MathUtils.clamp(value, min, max);
    }

    public float getPrefWidth () {
        return 140;
    }

    public float getPrefHeight () {
        return Math.max(knob.getMinHeight(), bg.getMinHeight());
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}
