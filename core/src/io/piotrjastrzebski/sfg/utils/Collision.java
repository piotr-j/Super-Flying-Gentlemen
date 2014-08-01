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

public class Collision {
	// categories
	public final static short OBSTACLE = 1; // 1 - everything without a mask
	public final static short PLAYER = 1 << 1;
	public final static short PICKUP = 1 << 3;
	public final static short LIGHT = 1 << 4;
	public final static short SENSOR = 1 << 5;
	public final static short BODY_PART = 1 << 6;
	public final static short NONE = 0;

	public final static short LIGHT_GROUP = 1;
	// masks
	public final static short MASK_LIGHTS = OBSTACLE;
	public final static short MASK_PLAYER = OBSTACLE | PICKUP | SENSOR;
	public final static short MASK_PICKUP = PICKUP | PLAYER | OBSTACLE;
	public final static short MASK_SENSOR = PLAYER;
	public final static short MASK_BODY_PART = OBSTACLE;
}
