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

public abstract class ClampedValue<T extends Number> {
    public enum Type {FLOAT, INTEGER}
    private Type type;
    protected boolean dirty;
    protected T max;
    protected T min;
    protected T value;
    protected T step;

    public ClampedValue(Type type, T min, T max, T step){
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

    public void set(ClampedValue<T> valueCopy){
        min = valueCopy.min;
        value = valueCopy.value;
        max = valueCopy.max;
        step = valueCopy.step;
        dirty = true;
    }

    public T value(){
        return value;
    }

    public abstract void set(T value);

    public T max() {
        return max;
    }

    public void max(T max) {
        this.max = max;
    }

    public T min() {
        return min;
    }

    public void min(T min) {
        this.min = min;
    }

    public T step() {
        return step;
    }

    public void step(T step) {
        this.step = step;
    }
}
