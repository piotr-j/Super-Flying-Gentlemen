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
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.input.GestureDetector;

import io.piotrjastrzebski.sfg.screen.GameScreen;

public class GameInputHandler extends InputHandler<GameScreen> {
	public GameInputHandler(GameScreen screen) {
		super(screen);
	}

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        screen.handleTap();
        return false;
    }

	@Override
	public boolean fling(float velocityX, float velocityY, int button) {
		// fling right
		if (velocityX > 0 && velocityX > Math.abs(velocityY)){
			screen.handleFling();
		} else {
			screen.handleTap();
		}
		return true;
	}
	
	@Override
	public boolean keyDown(int keycode) {
		switch (keycode) {
		case Keys.SPACE:
		case Keys.UP:
			screen.handleTap();
			break;
		case Keys.RIGHT:
		case Keys.D:
			screen.handleFling();
			break;
		case Keys.ESCAPE:
		case Keys.BACK:
			screen.handleBack();
			break;
        case Keys.ENTER:
            screen.handleEnter();
		default: break;
		}
		return false;
	}
}
