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

public class PartBottom extends Part {
	
	public PartBottom(Obstacle parent, SpikePool spikePool, HammerPool hammerPool) {
		super(parent, spikePool, hammerPool);
	}

	@Override
	public void init(float x, float y, Type type) {
		super.init(x, y-HEIGHT, type);
        // add sensor so the player will be killed on touch
        if (type != Type.STATIC)
            sensor.setTransform(x, y, 0);

        float tileOffset;
		if (type == Type.STATIC){
            cap.init(x, y, false);
            // height of cap
            tileOffset = 3;
		} else {
            endPoint.init(x, y, true);
            // height of endpoint
            tileOffset = 7;
		}

		for (int i = 0; i < tiles.length; i++) {
			tiles[i].init(x, y-tileOffset-3*i);
		}
	}
}
