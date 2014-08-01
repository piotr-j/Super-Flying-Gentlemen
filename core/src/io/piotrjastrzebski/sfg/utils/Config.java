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

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;

/**
 * Config represents various game settings affecting difficulty
 * The settings are loaded from json files and it uses JsonValue as storage
 * If some of the settings are missing IllegalArgumentException will be thrown
 */
public class Config {
    public enum Difficulty {
        BRUTAL, VERY_HARD, HARD, BABY, CUSTOM, TEST
	}

	private Range<Float> obstacleDistance;
	private Range<Float> obstacleGapSize;
	private Range<Integer> pickupLive;
	private Range<Integer> pickupBoost;
	
	private JsonValue world;
	private JsonValue player;
	private JsonValue pickup;
	private JsonReader reader;
    private Difficulty difficulty;

	/**
	 * Create new config with given base difficulty
	 */
	public Config(Difficulty difficulty){
        this.difficulty = difficulty;
        reader = new JsonReader();
		switch (difficulty) {
		case TEST:
			loadConfig("test");
			break;
		case HARD:
			loadConfig("hard");
			break;
		case VERY_HARD:
			loadConfig("very_hard");
			break;
		case BRUTAL:
			loadConfig("brutal");
			break;
        case BABY:
            loadConfig("baby");
            break;
		case CUSTOM:
			// got to save custom json or something
//			loadConfig("data/config/custom.json");
			break;
		default:
			break;
		}
	}
	/**
	 * Load json config from given path
	 * @param diff name of difficulty
	 */
	private void loadConfig(String diff){
		final JsonValue cfgRaw = reader.parse(Gdx.files.internal("data/config.json")).get(diff);
		world = cfgRaw.get("world");
		player = cfgRaw.get("player");
        final JsonValue obstacle = cfgRaw.get("obstacle");
		pickup = cfgRaw.get("pickup");
		
		final JsonValue dst = obstacle.get("dst_range");
		obstacleDistance = new Range<Float>(dst.getFloat("min"), dst.getFloat("max"));
		
		final JsonValue gapSize = obstacle.get("gap_range");
		obstacleGapSize = new Range<Float>(gapSize.getFloat("min"), gapSize.getFloat("max"));

		final JsonValue livePickup = pickup.get("live_range");
		pickupLive = new Range<Integer>(livePickup.getInt("min"), livePickup.getInt("max"));
		
		final JsonValue boostPickup = pickup.get("boost_range");
		pickupBoost = new Range<Integer>(boostPickup.getInt("min"), boostPickup.getInt("max"));
	}

    public Difficulty getDifficulty() {
        return difficulty;
    }

	/**
	 * @return the gravity of the world
	 */
	public float getGravity() {
		return world.getFloat("gravity");
	}
	/**
	 * @return the playerMaxLives
	 */
	public int getPlayerMaxLives() {
		return player.getInt("lives");
	}
	/**
	 * @return the playerFlySpeed
	 */
	public float getPlayerFlySpeed() {
		return player.getFloat("fly_speed");
	}
	/**
	 * @return the playerFlySpeed
	 */
	public float getPlayerMaxFlySpeed() {
		return player.getFloat("max_fly_speed");
	}
	/**
	 * @return the playerFlyImpMult
	 */
	public float getPlayerFlyImpMult() {
		return player.getFloat("fly_imp_mult");
	}
	/**
	 * @return the playerJumpDelay
	 */
	public float getPlayerJumpDelay() {
		return player.getFloat("jump_delay");
	}
	/**
	 * @return the playerDashTime
	 */
	public float getPlayerDashTime() {
		return player.getFloat("dash_time");
	}
	/**
	 * @return the playerDashDelay
	 */
	public float getPlayerDashDelay() {
		return player.getFloat("dash_delay");
	}
	/**
	 * @return the playerDashDelay
	 */
	public float getPlayerDashMult() {
		return player.getFloat("dash_mult");
	}
	/**
	 * @return the playerJumpImpulse
	 */
	public float getPlayerJumpImpulse() {
		return player.getFloat("jump_impulse");
	}
	/**
	 * @return the playerLinearDampening
	 */
	public float getPlayerLinearDampening() {
		return player.getFloat("linear_dampening");
	}
	/**
	 * @return the obstacleDistance
	 */
	public Range<Float> getObstacleDistance() {
		return obstacleDistance;
	}
	/**
	 * @return the obstacleGapSize
	 */
	public Range<Float> getObstacleGapSize() {
		return obstacleGapSize;
	}

    public Range<Integer> getPickupLive() {
        return pickupLive;
    }

    public Range<Integer> getPickupBoost() {
        return pickupBoost;
    }

    public float getPickupSpawnChance(){
        return pickup.getFloat("spawn_chance");
    }

    public int getPickupMinSpawnDistance(){
        return pickup.getInt("spawn_distance");
    }
}
