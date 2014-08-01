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

package io.piotrjastrzebski.sfg;

public interface ActionListener {
    public static final int SIGN_IN = 0;
    public static final int SIGN_OUT = 1;
    public static final int SIGN_IN_FAILED = 2;
    public static final int PREMIUM_ENABLED = 3;
    public static final int PREMIUM_DISABLED = 4;
    public static final int TOP_SCORE_UPDATED = 5;
    public static final int ACHIEVEMENTS_LOADED = 6;
    public static final int GAMES_READY = 7;

    public void handleEvent(int id, Object data);
}
