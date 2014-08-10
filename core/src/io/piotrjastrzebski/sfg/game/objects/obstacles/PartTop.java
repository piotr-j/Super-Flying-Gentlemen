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

package io.piotrjastrzebski.sfg.game.objects.obstacles;

public class PartTop extends Part {
	public PartTop(Obstacle parent) {
		super(parent);
	}

	@Override
	public void init(float x, float y, Type type) {
		super.init(x, y+HEIGHT, type);
        // add sensor so the player will be killed on touch
        if (type == Type.SPIKE || type == Type.HAMMER)
            sensor.setTransform(x, y, 0);

        float tileOffset;
		if (type == Type.STATIC){
            cap.init(x, y, true);
            tileOffset = 6;
		} else if (type == Type.MOVING) {
            endPoint.init(x, y, false);
            tileOffset = 5.5f;
        } else {
            endPoint.init(x, y, false);
            tileOffset = 10;
		}

		for (int i = 0; i < tiles.length; i++) {
			tiles[i].init(x, y+3*i+tileOffset);
		}
	}
}
