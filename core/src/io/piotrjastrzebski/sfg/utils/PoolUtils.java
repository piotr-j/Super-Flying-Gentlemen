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

import java.util.Iterator;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.utils.Pools;

import io.piotrjastrzebski.sfg.game.objects.Position;
import sun.security.krb5.internal.crypto.Des;

public class PoolUtils {
	
	/**
	 * free and remove objects from the array if their is outside if given position
	 */
	public static <T extends Position> void clean(Array<T> toClean, float outside){
		for (Iterator<T> iterator = toClean.iterator(); iterator.hasNext();) {
			final T g = iterator.next();
			if (g.getPos().x < outside){
                Pools.free(g);
                iterator.remove();
			}
		}
	}
	
	/**
	 * reset objects in the array and clear it
	 */
	public static  <T extends Pool.Poolable> void reset(Array<T> toReset){
		for (int i = 0; i < toReset.size; i++) {
            Pools.free(toReset.get(i));
		}
		toReset.clear();
	}

    /**
     * free and clear all objects
     */
    public static <T extends Pool.Poolable> void dispose(Class<T> classToDispose) {
        dispose(classToDispose, null);
    }

    /**
     * free and clear all objects
     */
    public static <T extends Pool.Poolable> void dispose(Class<T> classToDispose, Array<T> toDispose) {
        Pool<T> pool = Pools.get(classToDispose);
        if (toDispose != null)
            pool.freeAll(toDispose);
        pool.clear();
    }
}
