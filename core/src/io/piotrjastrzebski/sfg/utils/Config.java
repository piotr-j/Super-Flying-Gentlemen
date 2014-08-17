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
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.utils.ArrayMap;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;

import io.piotrjastrzebski.sfg.SFGApp;

/**
 * Config represents various game settings affecting difficulty
 * The settings are loaded from json files and it uses JsonValue as storage
 * If some of the settings are missing IllegalArgumentException will be thrown
 */
public class Config {
    public enum Difficulty {
        BRUTAL, VERY_HARD, HARD, BABY, CUSTOM_1, CUSTOM_2, CUSTOM_3
	}

    private final static String GRAVITY = "_GRAVITY";
    private final static String OBST_DISTANCE = "_OBST_DST";
    private final static String OBST_GAP = "_OBST_GAP";

    private final static String PICK_LIVES = "_PICK_LIVES";
    private final static String PICK_BOOST = "_PICK_BOOST";
    private final static String PICK_SHIELD = "_PICK_SHIELD";
    private final static String PICK_TOXIC = "_PICK_TOXIC";
    private final static String PICK_SPAWN_CHANCE = "_PICK_SPAWN_CHANCE";
    private final static String PICK_SPAWN_DISTANCE = "_PICK_SPAWN_DISTANCE";

    private final static String PLAY_LIVES = "_PLAY_LIVES";
    private final static String PLAY_SHIELDS  = "_PLAY_SHIELDS";
    private final static String PLAY_FLY_SPEED = "_PLAY_FLY_SPEED";
    private final static String PLAY_FLY_MAX_SPEED = "_PLAY_FLY_MAX_SPEED";
    private final static String PLAY_FLY_IMP = "_PLAY_FLY_IMP";
    private final static String PLAY_LIN_DAMP = "_PLAY_LIN_DAMP";
    private final static String PLAY_SCALE = "_PLAY_SCALE";
    private final static String PLAY_CENTRE_OFFSET = "_PLAY_CENTRE_OFFSET";

    private final static String PLAY_JUMP_IMP = "_PLAY_JUMP_IMP";
    private final static String PLAY_JUMP_DELAY = "_PLAY_JUMP_DELAY";

    private final static String PLAY_DASH_DELAY = "_PLAY_DASH_DELAY";
    private final static String PLAY_DASH_TIME = "_PLAY_DASH_TIME";
    private final static String PLAY_DASH_IMP = "_PLAY_DASH_IMP";
    private final static String HIGH = "_HIGH";

    private final static String LOW = "_LOW";
    private final static String BASE = "_BASE";

	private final JsonReader reader;
    private final Preferences prefs;

    private Difficulty difficulty;
    private ArrayMap<Difficulty, ConfigData> configs;
    private ConfigData currentConfig;
	/**
	 * Create new config with given base difficulty
	 */
	public Config(){
        configs = new ArrayMap<Difficulty , ConfigData>();
        reader = new JsonReader();
        prefs = Gdx.app.getPreferences(SFGApp.PREFS);
    }

    public void setDifficulty(Difficulty difficulty){
        this.difficulty = difficulty;
        switch (difficulty) {
            case HARD:
            case VERY_HARD:
            case BRUTAL:
            case BABY:
                loadJsonConfig(difficulty);
                break;
            case CUSTOM_1:
            case CUSTOM_2:
            case CUSTOM_3:
                loadCustomConfig(difficulty);
                break;
            default:
                break;
        }
        currentConfig = configs.get(difficulty);
    }

    public ConfigData getCurrentConfig(){
        return currentConfig;
    }

    public ConfigData getConfigData(Difficulty difficulty) {
        switch (difficulty){
            case HARD:
            case VERY_HARD:
            case BRUTAL:
            case BABY:
                loadJsonConfig(difficulty);
                break;
            case CUSTOM_1:
            case CUSTOM_2:
            case CUSTOM_3:
                loadCustomConfig(difficulty);
                break;
        }
        return configs.get(difficulty);
    }

    private void save(String name, ClampedValueFloat valueFloat){
        if (valueFloat.isDirty()){
            prefs.putFloat(name, valueFloat.value());
            valueFloat.clean();
        }
    }

    private void save(String name, ClampedValueInt valueInt){
        if (valueInt.isDirty()){
            prefs.putInteger(name, valueInt.value());
            valueInt.clean();
        }
    }

    private void save(String name, ClampedRangeFloat floatRange){
        if (floatRange.isDirty()){
            prefs.putFloat(name + LOW, floatRange.low());
            prefs.putFloat(name + HIGH, floatRange.high());
            floatRange.clean();
        }
    }

    private void save(String name, ClampedRangeInt rangeInt){
        if (rangeInt.isDirty()){
            prefs.putInteger(name + LOW, rangeInt.low());
            prefs.putInteger(name + HIGH, rangeInt.high());
            rangeInt.clean();
        }
    }

    private void saveCustomConfig(ConfigData data){
        final String name = data.getDifficulty().toString();
        prefs.putString(name + BASE, data.getBaseDifficulty().toString());
        save(name + GRAVITY, data.getGravity());

        save(name + PLAY_LIVES, data.getPlayerInitLives());
        save(name + PLAY_SHIELDS, data.getPlayerInitShields());

        save(name + PLAY_SCALE, data.getPlayerScale());
        save(name + PLAY_CENTRE_OFFSET, data.getPlayerCentreOffset());

        save(name + PLAY_FLY_SPEED, data.getPlayerFlySpeed());
        save(name + PLAY_FLY_MAX_SPEED, data.getPlayerFlyMaxSpeed());
        save(name + PLAY_FLY_IMP, data.getPlayerFlyImpulse());

        save(name + PLAY_JUMP_IMP, data.getPlayerJumpImpulse());
        save(name + PLAY_JUMP_DELAY, data.getPlayerJumpDelay());

        save(name + PLAY_DASH_TIME, data.getPlayerDashTime());
        save(name + PLAY_DASH_DELAY, data.getPlayerDashDelay());
        save(name + PLAY_DASH_IMP, data.getPlayerDashImpulse());

        save(name + PLAY_LIN_DAMP, data.getPlayerLinearDampening());

        save(name + OBST_DISTANCE, data.getObstacleDistance());
        save(name + OBST_GAP, data.getObstacleGapSize());

        save(name + PICK_SPAWN_CHANCE, data.getPickupSpawnChance());
        save(name + PICK_SPAWN_DISTANCE, data.getPickupMinSpawnDistance());

        save(name + PICK_LIVES, data.getPickupLives());
        save(name + PICK_SHIELD, data.getPickupShield());
        save(name + PICK_BOOST, data.getPickupBoost());
        save(name + PICK_TOXIC, data.getPickupToxic());

        prefs.flush();
    }

    public void saveCustomConfigs(){
        saveCustomConfig(getConfigData(Difficulty.CUSTOM_1));
        saveCustomConfig(getConfigData(Difficulty.CUSTOM_2));
        saveCustomConfig(getConfigData(Difficulty.CUSTOM_3));
    }

    private void loadCustomConfig(Difficulty difficulty){
        if (!configs.containsKey(difficulty)){
            configs.put(difficulty, createCustomConfig(difficulty));
        }
    }

    private void load(String name, ClampedValueFloat valueFloat, ClampedValueFloat defaultValue){
        valueFloat.set(prefs.getFloat(name, defaultValue.value()));
    }

    private void load(String name, ClampedValueInt valueInt, ClampedValueInt defaultValue){
        valueInt.set(prefs.getInteger(name, defaultValue.value()));
    }

    private void load(String name, ClampedRangeFloat rangeFloat, ClampedRangeFloat defaultRange){
        rangeFloat.set(
                prefs.getFloat(name, defaultRange.low()),
                prefs.getFloat(name, defaultRange.high())
        );
    }

    private void load(String name, ClampedRangeInt valueInt, ClampedRangeInt defaultRange){
        valueInt.set(
                prefs.getInteger(name, defaultRange.low()),
                prefs.getInteger(name, defaultRange.high())
        );
    }

    private ConfigData createCustomConfig(Difficulty difficulty){
        final String name = difficulty.toString();
        // load data from prefs
        ConfigData base;
        final String diff = prefs.getString(name + BASE, "");
        if (diff.equals("VERY_HARD")){
            base = getConfigData(Difficulty.VERY_HARD);
        } else if (diff.equals("HARD")){
            base = getConfigData(Difficulty.VERY_HARD);
        } else {
            base = getConfigData(Difficulty.BRUTAL);
        }

        final ConfigData data = new ConfigData(difficulty);
        data.setBaseDifficulty(base.getDifficulty());

        load(name + GRAVITY, data.getGravity(), base.getGravity());

        load(name + PLAY_LIVES, data.getPlayerInitLives(), base.getPlayerInitLives());
        load(name + PLAY_SHIELDS, data.getPlayerInitShields(), base.getPlayerInitShields());

        load(name + PLAY_SCALE, data.getPlayerScale(), base.getPlayerScale());
        load(name + PLAY_CENTRE_OFFSET, data.getPlayerCentreOffset(), base.getPlayerCentreOffset());

        load(name + PLAY_FLY_SPEED, data.getPlayerFlySpeed(), base.getPlayerFlySpeed());
        load(name + PLAY_FLY_MAX_SPEED, data.getPlayerFlyMaxSpeed(), base.getPlayerFlyMaxSpeed());
        load(name + PLAY_FLY_IMP, data.getPlayerFlyImpulse(), base.getPlayerFlyImpulse());

        load(name + PLAY_JUMP_IMP, data.getPlayerJumpImpulse(), base.getPlayerJumpImpulse());
        load(name + PLAY_JUMP_DELAY, data.getPlayerJumpDelay(), base.getPlayerJumpDelay());

        load(name + PLAY_DASH_TIME, data.getPlayerDashTime(), base.getPlayerDashTime());
        load(name + PLAY_DASH_DELAY, data.getPlayerDashDelay(), base.getPlayerDashDelay());
        load(name + PLAY_DASH_IMP, data.getPlayerDashImpulse(), base.getPlayerDashImpulse());

        load(name + PLAY_LIN_DAMP, data.getPlayerLinearDampening(), base.getPlayerLinearDampening());

        load(name + OBST_DISTANCE, data.getObstacleDistance(), base.getObstacleDistance());
        load(name + OBST_GAP, data.getObstacleGapSize(), base.getObstacleGapSize());

        load(name + PICK_SPAWN_CHANCE, data.getPickupSpawnChance(), base.getPickupSpawnChance());
        load(name + PICK_SPAWN_DISTANCE, data.getPickupMinSpawnDistance(), base.getPickupMinSpawnDistance());

        load(name + PICK_LIVES, data.getPickupLives(), base.getPickupLives());
        load(name + PICK_BOOST, data.getPickupBoost(), base.getPickupBoost());
        load(name + PICK_SHIELD, data.getPickupShield(), base.getPickupShield());
        load(name + PICK_TOXIC, data.getPickupToxic(), base.getPickupToxic());
        data.clean();
        return data;
    }

    private void loadJsonConfig(Difficulty difficulty){
        if (!configs.containsKey(difficulty)){
            configs.put(difficulty, createJsonConfig(difficulty));
        }
    }

    private ConfigData createJsonConfig(Difficulty difficulty){
        final String diff = difficulty.toString();
        ConfigData data = new ConfigData(difficulty);

        final JsonValue cfgRaw = reader.parse(Gdx.files.internal("data/config.json")).get(diff);
        data.getGravity().set(cfgRaw.get("world").getFloat("gravity"));


        final JsonValue player = cfgRaw.get("player");
        data.getPlayerInitLives().set(player.getInt("lives"));
        data.getPlayerInitShields().set(player.getInt("shields"));

        data.getPlayerScale().set(player.getFloat("scale"));
        data.getPlayerCentreOffset().set(player.getFloat("centre_offset"));

        data.getPlayerFlySpeed().set(player.getFloat("fly_speed"));
        data.getPlayerFlyMaxSpeed().set(player.getFloat("max_fly_speed"));
        data.getPlayerFlyImpulse().set(player.getFloat("fly_imp_mult"));

        data.getPlayerJumpImpulse().set(player.getFloat("jump_impulse"));
        data.getPlayerJumpDelay().set(player.getFloat("jump_delay"));

        data.getPlayerDashTime().set(player.getFloat("dash_time"));
        data.getPlayerDashDelay().set(player.getFloat("dash_delay"));
        data.getPlayerDashImpulse().set(player.getFloat("dash_mult"));

        data.getPlayerLinearDampening().set(player.getFloat("linear_dampening"));


        final JsonValue obstacle = cfgRaw.get("obstacle");
        final JsonValue dst = obstacle.get("dst_range");
        data.getObstacleDistance().set(
                dst.getFloat("min"),
                dst.getFloat("max")
        );

        final JsonValue gapSize = obstacle.get("gap_range");
        data.getObstacleGapSize().set(
                gapSize.getFloat("min"),
                gapSize.getFloat("max")
        );


        final JsonValue pickup = cfgRaw.get("pickup");
        data.getPickupSpawnChance().set(pickup.getFloat("spawn_chance"));
        data.getPickupMinSpawnDistance().set(pickup.getInt("spawn_distance"));

        final JsonValue livePickup = pickup.get("live_range");
        data.getPickupLives().set(
                livePickup.getInt("min"),
                livePickup.getInt("max")
        );

        final JsonValue boostPickup = pickup.get("boost_range");
        data.getPickupBoost().set(
                boostPickup.getInt("min"),
                boostPickup.getInt("max")
        );

        final JsonValue shieldPickup = pickup.get("shield_range");
        data.getPickupShield().set(
                shieldPickup.getInt("min"),
                shieldPickup.getInt("max")
        );

        final JsonValue toxicPickup = pickup.get("toxic_range");
        data.getPickupToxic().set(
                toxicPickup.getInt("min"),
                toxicPickup.getInt("max")
        );
        data.clean();
        return data;
    }

    public void setCustomBase(Difficulty custom, Difficulty base){
        getConfigData(custom).set(getConfigData(base));
        getConfigData(custom).clean();
    }

    public Difficulty getDifficulty() {
        return difficulty;
    }
}
