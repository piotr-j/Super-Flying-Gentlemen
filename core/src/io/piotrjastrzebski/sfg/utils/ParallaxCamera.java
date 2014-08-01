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

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;

public class ParallaxCamera extends OrthographicCamera {
	Matrix4 parallaxView = new Matrix4();
	Matrix4 parallaxCombined = new Matrix4();
	Vector3 tmp = new Vector3();
	Vector3 tmp2 = new Vector3();

	public ParallaxCamera (float viewportWidth, float viewportHeight) {
		super(viewportWidth, viewportHeight);
	}

	public Matrix4 calculateParallaxMatrix (float parallaxX, float parallaxY) {
		update();
		tmp.set(position);
		tmp.x *= parallaxX;
		tmp.y *= parallaxY;

		parallaxView.setToLookAt(tmp, tmp2.set(tmp).add(direction), up);
		parallaxCombined.set(projection);
		Matrix4.mul(parallaxCombined.val, parallaxView.val);
		return parallaxCombined;
	}
}
