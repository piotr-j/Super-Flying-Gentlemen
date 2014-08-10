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
import com.badlogic.gdx.math.Vector2;

public class Transform {
    private float x = 0;
    private float y = 0;
    private float angle = 0;
    private float lastX = 0;
    private float lastY = 0;
    private float lastAngle = 0;

    public void init(Vector2 pos) {
        init(pos.x, pos.y, 0);
    }

    public void init(float x, float y) {
        init(x, y, 0);
    }

    public void init(Vector2 pos, float angle) {
        init(pos.x, pos.y, angle);
    }

    public void init(float x, float y, float angle){
        lastX = x;
        this.x = x;
        lastY = y;
        this.y = y;
        lastAngle = angle;
        this.angle = angle;
    }

    public void set(Vector2 pos) {
        set(pos.x, pos.y, 0);
    }

    public void set(float x, float y) {
        set(x, y, 0);
    }

    public void set(Vector2 pos, float angle) {
        set(pos.x, pos.y, angle);
    }

    public void set(float x, float y, float angle) {
        lastX = this.x;
        this.x = x;
        lastY = this.y;
        this.y = y;
        lastAngle = this.angle;
        this.angle = angle;
    }

    public float getX() {
        return x;
    }

    public float getLerpX(float alpha) {
        return MathUtils.lerp(lastX, x, alpha);
    }

    public float getY() {
        return y;
    }

    public float getLerpY(float alpha) {
        return MathUtils.lerp(lastY, y, alpha);
    }

    public float getAngle() {
        return angle;
    }

    public float getLerpAngle(float alpha) {
        return MathUtils.lerp(lastAngle, angle, alpha);
    }

    public float getLastX() {
        return lastX;
    }

    public float getLastY() {
        return lastY;
    }

    public float getLastAngle() {
        return lastAngle;
    }
}
