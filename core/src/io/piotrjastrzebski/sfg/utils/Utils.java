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

public class Utils {
	/**
	 * Return rounded number from given range
	 */
	public static float randomRange(final Range<Float> range){
		return Math.round(range.min + Math.random() * ((range.max - range.min +0.5f)));
	}
	
	/**
	 * Return number from given range where [min, max]
	 */
	public static int randomIntRange(final Range<Integer> range){
		return (int) (range.min + Math.random() * (range.max - range.min +0.5f));
	}
}
