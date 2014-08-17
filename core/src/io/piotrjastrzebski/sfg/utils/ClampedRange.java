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

public abstract class ClampedRange<T> {
    public enum Type {FLOAT, INTEGER;}
    private Type type;
    protected boolean dirty;
    protected T min;
    protected T low;
    protected T high;
    protected T max;
    protected T step;

    public ClampedRange(Type type, T min, T max, T step){
        this.type = type;
        this.min = min;
        this.max = max;
        this.step = step;
        dirty = false;
    }

    public Type type() {
        return type;
    }

    public boolean isDirty(){
        return dirty;
    }

    public void clean(){
        dirty = false;
    }

    public void set(ClampedRange<T> range){
        min = range.min;
        low = range.low;
        high = range.high;
        max = range.max;
        step = range.step;
        dirty = true;
    }

    public abstract void set(T low, T high);

    public T min(){
        return min;
    }

    public void min(T min){
        this.min = min;
        dirty = true;
    }

    public T low(){
        return low;
    }

    public abstract void low(T low);

    public T high(){
        return high;
    }

    public abstract void high(T high);

    public T max(){
        return max;
    }

    public void max(T max){
        this.max = max;
    }

    public T step(){
        return step;
    }

    public void step(T step){
        this.step = step;
    }
}
