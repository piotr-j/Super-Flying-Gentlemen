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

import io.piotrjastrzebski.sfg.game.objects.ExplosionLight;
import io.piotrjastrzebski.sfg.utils.pools.ExplosionLightPool.PooledExplosionLight;

public class ExplosionLightPool extends Pool<PooledExplosionLight> {
    @Override
	protected PooledExplosionLight newObject() {
		return new PooledExplosionLight();
	}
	
	public class PooledExplosionLight extends ExplosionLight implements PositionFreeable {
		PooledExplosionLight () {
			super();
		}

		@Override
		public void free () {
            reset();
			ExplosionLightPool.this.free(this);
		}
	}
}
