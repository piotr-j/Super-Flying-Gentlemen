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

package io.piotrjastrzebski.sfg.screen.inputhandlers;

import com.badlogic.gdx.Input.Keys;

import io.piotrjastrzebski.sfg.screen.AboutScreen;

public class AboutInputHandler extends InputHandler<AboutScreen> {
	public AboutInputHandler(AboutScreen screen) {
		super(screen);
	}

	@Override
	public boolean keyDown(int keycode) {
		switch (keycode) {
		case Keys.ESCAPE:
		case Keys.BACK:
			screen.handleBack();
			break;
		default: break;
		}
		return false;
	}
}
