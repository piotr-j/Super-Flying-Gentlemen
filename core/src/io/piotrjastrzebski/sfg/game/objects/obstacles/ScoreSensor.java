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

import io.piotrjastrzebski.sfg.screen.GameScreen;
import io.piotrjastrzebski.sfg.utils.Collision;
import io.piotrjastrzebski.sfg.utils.Locator;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;

public class ScoreSensor {
	private World world;
	private Body sensorBody;
	private boolean scored;
	private boolean scoreEnabled;
	private float x = 0;
	private Obstacle parent;

	public ScoreSensor(Obstacle obstacle){
		world = Locator.getWorld();
		this.parent = obstacle;
		
		final BodyDef obstacleBodyDef = new BodyDef(); 
		obstacleBodyDef.type = BodyType.StaticBody;
		obstacleBodyDef.position.set(0, 2);  
		sensorBody = world.createBody(obstacleBodyDef);
		
		final PolygonShape rect = new PolygonShape();  
		rect.setAsBox(0.5f, GameScreen.VIEWPORT_HEIGHT/2.0f);
		
		final FixtureDef fixtureDef = new FixtureDef();
		fixtureDef.shape = rect;
		fixtureDef.isSensor = true; // wont collide
		fixtureDef.filter.categoryBits = Collision.SENSOR;
		fixtureDef.filter.maskBits = Collision.MASK_SENSOR;

		final Fixture f = sensorBody.createFixture(fixtureDef);
		f.setUserData(SensorType.SCORE);
		sensorBody.setUserData(this);
		
		// Clean up
		rect.dispose();
	}

	public void init(float x){
		this.x = x;
		scored = false;
        scoreEnabled = true;
		sensorBody.setTransform(x, GameScreen.VIEWPORT_HEIGHT/2.0f, 0);
	}

	public void destroy(){
		if (sensorBody!=null){
			world.destroyBody(sensorBody);
			sensorBody = null;
		}
	}

	public float x(){
		return x;
	}
	
	public boolean score() {
        // bad
        parent.execute(false);
        if (!scored && scoreEnabled){
            scored = true;
			return true;
		}
		return false;
	}

    public void disable(){
        scoreEnabled = false;
    }

    public boolean enabled() {
        return scoreEnabled;
    }
}
