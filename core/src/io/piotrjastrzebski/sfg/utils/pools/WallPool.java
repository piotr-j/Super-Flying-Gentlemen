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

import com.badlogic.gdx.utils.Pool;

import io.piotrjastrzebski.sfg.game.objects.obstacles.Wall;

public class WallPool extends Pool<WallPool.PooledWall> {
    private final float maxHeight;

    public WallPool(float maxHeight) {
		super();
		this.maxHeight = maxHeight;
	}

	@Override
	protected PooledWall newObject() {
		return new PooledWall();
	}
	
	public class PooledWall extends Wall implements PositionFreeable {
        PooledWall () {
			super(maxHeight);
		}

		@Override
		public void free () {
			WallPool.this.free(this);
		}
	}
}
