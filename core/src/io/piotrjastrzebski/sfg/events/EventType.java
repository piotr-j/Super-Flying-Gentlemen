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

package io.piotrjastrzebski.sfg.events;

public class EventType {
	public final static int DEFAULT = 0;
	
	public final static int ROCKET_HIT = 1;
	
	public final static int PLAYER_PART_TOUCHED = 10;
	public final static int PLAYER_SCORED = 11;
	public final static int PLAYER_RESPAWNED = 12;
	public final static int PLAYER_DIED = 13;
	public final static int PLAYER_CRUSHED = 14;
	public final static int PLAYER_SPIKED = 15;
    public static final int PLAYER_TOUCHED_END = 16;
    public static final int PLAYER_TOUCHED = 17;
    public static final int PLAYER_LIVES_CHANGED = 18;
    public static final int PLAYER_ALIVE = 19;
    public static final int PLAYER_BOOST_CHANGED = 20;
    public static final int PLAYER_SCORE_CHANGED = 21;

    public static final int SPAWN_EXPLOSION = 30;
    public static final int SPAWN_BLOOD = 31;

    public static final int PLAY_MUSIC = 40;
    public static final int STOP_MUSIC = 41;
    public static final int TOGGLE_MUSIC = 42;
    public static final int PLAY_SOUND = 43;
    public static final int TOGGLE_SOUND = 44;

    public static final int SHOW_BOOST_TUT = 50;

    public final static int PICKUP_TOUCHED = 60;
    public final static int PICKUP_DESTROYED = 61;
}
