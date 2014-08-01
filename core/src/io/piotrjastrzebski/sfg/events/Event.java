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

import com.badlogic.gdx.utils.Pool.Poolable;

public class Event implements Poolable{
	private int type;
	private Object data;
	private Object source;
	
	public Event(){
		reset();
	}
	
	public void init(int type){
		init(type, null);
	}
	
	public void init(int type, Object data){
		init(type, data, null);
	}
	
	public void init(int type, Object data, Object source){
		this.type = type;
		this.data = data;
		this.source = source;
	}

	@Override
	public void reset() {
		type = -1;
		data = null;
		source = null;
	}

	public int getType() {
		return type;
	}

	public Object getData() {
		return data;
	}

	public Object getSource() {
		return source;
	}

}
