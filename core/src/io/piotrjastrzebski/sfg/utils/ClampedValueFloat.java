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

package io.piotrjastrzebski.sfg.utils;

import com.badlogic.gdx.math.MathUtils;

public class ClampedValueFloat extends ClampedValue<Float> {
    public ClampedValueFloat() {
        this(Float.MIN_VALUE, Float.MAX_VALUE);
    }

    public ClampedValueFloat(float min, float max){
        this(min, max, 1.0f);
    }

    public ClampedValueFloat(float min, float max, float step) {
        super(Type.FLOAT, min, max, step);
    }

    public void set(Float value) {
        this.value = MathUtils.clamp(value, min, max);
        dirty = true;
    }
}
