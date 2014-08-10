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

package io.piotrjastrzebski.sfg.game;

import io.piotrjastrzebski.sfg.events.EventLoop;
import io.piotrjastrzebski.sfg.events.EventType;
import io.piotrjastrzebski.sfg.game.objects.Pickup;
import io.piotrjastrzebski.sfg.game.objects.Player;
import io.piotrjastrzebski.sfg.game.objects.PlayerRagDoll;
import io.piotrjastrzebski.sfg.game.objects.Rocket;
import io.piotrjastrzebski.sfg.game.objects.obstacles.SensorType;
import io.piotrjastrzebski.sfg.utils.Locator;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.utils.Pools;

public class ContactDispatcher implements ContactListener {
	private EventLoop events;
    private PlayerContactPool playerContactPool;

	public ContactDispatcher(){
		events = Locator.getEvents();
        playerContactPool = new PlayerContactPool();
	}
	
	private boolean checkSensor(Fixture sensor, Fixture other){
		final SensorType type = (SensorType) sensor.getUserData();
		final Object sensorData = sensor.getBody().getUserData();
		final Object otherData = other.getBody().getUserData();
        if (type == null)
            return false;
		switch (type) {
		case SCORE:
			if (otherData instanceof Player){
                events.queueEvent(EventType.PLAYER_SCORED, sensorData);
			}
			return true;
		default:
			break;
		}
        return false;
	}
	
	@Override
	public void beginContact(Contact contact) {
        // TODO this is ass, research better way of doing this
		Fixture fA = contact.getFixtureA();
		Fixture fB = contact.getFixtureB();
		// all sensors in game are static, so only one fixture can be a sensor
		if (fA.isSensor() && checkSensor(fA, fB)){
			return;
		} else if (fB.isSensor() && checkSensor(fB, fA)){
			return;
		}

		final Object o1 = contact.getFixtureA().getBody().getUserData();
		final Object o2 = contact.getFixtureB().getBody().getUserData();

        checkPickup(o1, o2);
        checkRocket(o1, o2);
        // parts are small and dont really intersect, so just getting first one is ok
        final Vector2 position = contact.getWorldManifold().getPoints()[0];
        checkPlayer(o1, o2, position);
    }

    private void checkPickup(Object o1, Object o2){
        if (o1 instanceof Pickup){
            checkPickupCollision(o1, o2);
        } else if (o2 instanceof Pickup){
            checkPickupCollision(o2, o1);
        }
    }

    private void checkPickupCollision(Object pickup, Object o){
        if (o instanceof Player || o instanceof PlayerRagDoll.Part){
            events.queueEvent(EventType.PICKUP_TOUCHED, pickup);
        } else {
            events.queueEvent(EventType.PICKUP_DESTROYED, pickup);
        }
    }

    private void checkRocket(Object o1, Object o2){
        if (o1 instanceof Rocket && rocketCollidesWith(o2)){
             events.queueEvent(EventType.ROCKET_HIT, o1);
        } else if (o2 instanceof Rocket && rocketCollidesWith(o1)){
             events.queueEvent(EventType.ROCKET_HIT, o2);
        }
    }

    private boolean rocketCollidesWith(Object o){
        return !((o instanceof Player) || (o instanceof PlayerRagDoll.Part));
    }

    private void checkPlayer(Object o1, Object o2, Vector2 position){
        if (o1 instanceof Player){
            checkPlayerCollision(o2, position);
        } else if (o2 instanceof Player){
            checkPlayerCollision(o1, position);
        } else if (o1 instanceof PlayerRagDoll.Part){
            checkPlayerCollision(o2, position);
        } else if (o2 instanceof PlayerRagDoll.Part){
            checkPlayerCollision(o1, position);
        }
    }

    private void checkPlayerCollision(Object o, Vector2 position){
        // pickup checked already
        if (o instanceof Pickup)
            return;
        // rocket checked already
        if (o instanceof Rocket)
            return;
        final PlayerContactPool.PlayerContact playerContact = playerContactPool.obtain();
        playerContact.object = o;
        // parts are small and dont really intersect, so just getting first one is ok
        playerContact.position = position;
        events.queueEvent(EventType.PLAYER_PART_TOUCHED, playerContact);
    }

	@Override
	public void endContact(Contact contact) {
        final Object o1 = contact.getFixtureA().getBody().getUserData();
        final Object o2 = contact.getFixtureB().getBody().getUserData();
        checkPlayerEnd(o1, o2);
    }

    private void checkPlayerEnd(Object o1, Object o2){
        if (o1 instanceof Player){
            checkPlayerEndTouch(o2);
        } else if (o2 instanceof Player){
            checkPlayerEndTouch(1);
        } else if (o1 instanceof PlayerRagDoll.Part){
            checkPlayerEndTouch(o2);
        } else if (o2 instanceof PlayerRagDoll.Part){
            checkPlayerEndTouch(o1);
        }
    }

    private void checkPlayerEndTouch(Object o){
        if (o instanceof Pickup)
            return;
        events.queueEvent(EventType.PLAYER_TOUCHED_END);
    }

	@Override
	public void preSolve(Contact contact, Manifold oldManifold) {
		
	}

	@Override
	public void postSolve(Contact contact, ContactImpulse impulse) {
		
	}

    public class PlayerContactPool extends Pool<PlayerContactPool.PlayerContact> {
        @Override
        protected PlayerContact newObject() {
            return new PlayerContact();
        }

        // small class that holds position and object for player contact
        // should be freed after use
        public class PlayerContact {
            private Vector2 position;
            private Object object;
            PlayerContact() {}

            public Vector2 position(){
                return position;
            }

            public Object content(){
                return object;
            }

            public void free () {
                Pools.free(position);
                position = null;
                object = null;
                PlayerContactPool.this.free(this);
            }
        }
    }
}
