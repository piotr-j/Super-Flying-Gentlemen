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

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;

public class Box2dUtils {
	/**
	 * Creates a static box
	 */
	public static Body createBox(float x, float y, float width, float height){
		return createBox(x, y, width, height, BodyType.StaticBody);
	}

	/**
	 * Creates a box with given type
	 */
	public static Body createBox(float x, float y, float width, float height, BodyType bodyType){
		final BodyDef groundBodyDef = new BodyDef();
		groundBodyDef.type = bodyType;
		groundBodyDef.position.set(x, y);

		final Body body = Locator.getWorld().createBody(groundBodyDef);

		final PolygonShape groundBox = new PolygonShape();
		groundBox.setAsBox(width/2, height/2);
		body.createFixture(groundBox, 0.0f);
		groundBox.dispose();
		
		return body;
	}
}
