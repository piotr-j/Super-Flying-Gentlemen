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

package io.piotrjastrzebski.sfg.utils.pools;

import java.util.Iterator;

import com.badlogic.gdx.utils.Array;

public class PoolUtils {
	
	/**
	 * free and remove objects from the array if their is outside if given position
	 */
	public static <T extends PositionFreeable> void clean(Array<T> toClean, float outside){
		for (Iterator<T> iterator = toClean.iterator(); iterator.hasNext();) {
			final T g = iterator.next();
			if (g.getPos().x < outside){
				g.free();
                iterator.remove();
			}
		}
	}
	
	/**
	 * reset objects in the array and clear it
	 */
	public static  <T extends Freeable> void reset(Array<T> toReset){
		for (int i = 0; i < toReset.size; i++) {
			toReset.get(i).free();
		}
		toReset.clear();
	}
}
