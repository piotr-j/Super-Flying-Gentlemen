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

import io.piotrjastrzebski.sfg.screen.GameScreen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.ParticleEffectLoader;
import com.badlogic.gdx.assets.loaders.SkinLoader;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.ParticleEffectPool;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.I18NBundle;
import com.badlogic.gdx.utils.ObjectMap;
import com.esotericsoftware.spine.AnimationStateData;
import com.esotericsoftware.spine.SkeletonData;
import com.esotericsoftware.spine.SkeletonJson;

public class Assets {
    /** Constants for i18n */
    public final static String SIGN_IN = "g_sign_in";
    public final static String SIGN_OUT = "g_sign_out";
    public final static String SCORE = "score";
    public final static String RECORD = "record";
    public final static String NEW_RECORD = "new_record";
    public final static String ACHIEVEMENTS = "achievements";
    public final static String LEADER_BOARDS = "leaderboards";
    public final static String RETRY = "retry";
    public final static String RESUME = "resume";
    public final static String GAME_OVER = "game_over";
    public final static String DIALOG_LIGHTS_1 = "dialog_lights_1";
    public final static String DIALOG_LIGHTS_2 = "dialog_lights_2";
    public final static String OK = "ok";
    public final static String CANCEL = "cancel";
    public final static String ABOUT = "about";
    public final static String ABOUT_TEXT_1 = "about_text_1";
    public final static String ABOUT_TEXT_2 = "about_text_2";
    public final static String ABOUT_TEXT_3 = "about_text_3";
    public final static String ABOUT_TEXT_WWW_PIOTRJ = "about_text_site_piotrj";
    public final static String ABOUT_TEXT_WWW_LIBGDX = "about_text_site_libgdx";
    public final static String ABOUT_TEXT_WWW_SPINE = "about_text_site_spine";
    public final static String BACK = "back";
    public final static String SOUND_VOLUME = "sound_volume";
    public final static String SOUND_MAX = "sound_max";
    public final static String SOUND_OFF = "sound_off";
    public final static String MUSIC_VOLUME = "music_volume";
    public final static String MUSIC_MAX = "music_max";
    public final static String MUSIC_OFF = "music_off";
    public final static String SETTINGS = "settings";
    public final static String UNLOCKED = "unlocked";
    public final static String LOCKED = "locked";
    public final static String LOCKED_PREMIUM = "locked_premium";
    public final static String OUTFITS = "outfits";
    public final static String OUTFITS_LABEL = "outfits_label";
    public final static String GET_PREMIUM = "get_premium";
    public static final String DIFFICULTY_SELECT = "difficulty_select";
    public static final String DIFFICULTY_BRUTAL = "diff_brutal";
    public static final String DIFFICULTY_VERY_HARD = "diff_very_hard";
    public static final String DIFFICULTY_HARD = "diff_hard";
    public static final String DIFFICULTY_CUSTOM = "diff_custom";
    public static final String DIFFICULTY_BABY = "diff_baby";
    public static final String LIGHTS_ENABLED = "lights_on";
    public static final String LIGHTS_DISABLED = "lights_off";
    public static final String IMMERSIVE_ENABLED = "immersive_on";
    public static final String IMMERSIVE_DISABLED = "immersive_off";
    public static final String RESTORE_PURCHASES = "restore_purchases";
    public static final String RATE = "rate";
    public static final String RESET_TUTORIALS = "reset_tutorials";
    public static final String RESET_TUTORIALS_TOAST = "reset_tutorials_toast";
    public static final String CUSTOM_DIFFICULTY = "custom_difficulty";
    public static final String START_GAME = "start_game";
    public static final String CUSTOM_BASE_SELECT = "custom_base_select";
    public static final String RESET_DIFFICULTY = "reset_difficulty";

    public static final String PICKUP_SETTINGS = "pickup_settings";
    public static final String PICKUP_SPAWN_CHANCE = "pickup_spawn_chance";
    public static final String PICKUP_SPAWN_DISTANCE = "pickup_spawn_distance";
    public static final String PICKUP_LIVES_MIN = "pickup_lives_min";
    public static final String PICKUP_LIVES_MAX = "pickup_lives_max";
    public static final String PICKUP_SHIELDS_MIN = "pickup_shields_min";
    public static final String PICKUP_SHIELDS_MAX = "pickup_shields_max";
    public static final String PICKUP_BOOST_MIN = "pickup_boost_min";
    public static final String PICKUP_BOOST_MAX = "pickup_boost_max";
    public static final String PICKUP_TOXIC_MIN = "pickup_toxic_min";
    public static final String PICKUP_TOXIC_MAX = "pickup_toxic_max";

    public static final String OBSTACLE_SETTINGS = "obstacle_settings";
    public static final String OBSTACLE_DISTANCE_MIN = "obstacle_distance_min";
    public static final String OBSTACLE_DISTANCE_MAX = "obstacle_distance_max";
    public static final String OBSTACLE_GAP_MIN = "obstacle_gap_min";
    public static final String OBSTACLE_GAP_MAX = "obstacle_gap_max";

    public static final String PLAYER_SETTINGS = "player_settings";
    public static final String PLAYER_INIT_LIVES = "player_init_lives";
    public static final String PLAYER_INIT_SHIELDS = "player_init_shields";
    public static final String PLAYER_SCALE = "player_scale";
    public static final String PLAYER_CENTRE_OFFSET = "player_centre_offset";
    public static final String PLAYER_FLY_SPEED = "player_fly_speed";
    public static final String PLAYER_FLY_MAX_SPEED = "player_fly_max_speed";
    public static final String PLAYER_FLY_IMPULSE = "player_fly_impulse";
    public static final String PLAYER_LINEAR_DAMPENING = "player_linear_dampening";
    public static final String PLAYER_JUMP_IMPULSE = "player_jump_impulse";
    public static final String PLAYER_JUMP_DELAY = "player_jump_delay";
    public static final String PLAYER_DASH_TIME = "player_dash_time";
    public static final String PLAYER_DASH_DELAY = "player_dash_delay";
    public static final String PLAYER_DASH_IMPULSE = "player_dash_impulse";

    public static final String WORLD_SETTINGS = "world_settings";
    public static final String GRAVITY = "world_gravity";
    public static final String CUSTOM_WARNING = "custom_warning";


    private final static String GAME_ATLAS = "data/pack/sfg.atlas";
    private final static String PARTICLE_ATLAS = "data/pack/sfg_particles.atlas";
    private final static String UI_ATLAS = "data/pack/sfg_ui.atlas";
    private final static String SKIN = "data/skin.json";
    private final static String P_EXPLOSION = "data/particles/explosion.p";
    private final static String P_BLOOD = "data/particles/blood.p";
    private final static String P_TOXIC = "data/particles/toxic.p";

    private final static String I18N  = "data/i18n/SFGBundle";

    private final AssetManager assetManager;

    private Skin skin;

    private TextureAtlas gameAtlas;
    private TextureAtlas particleAtlas;
    private TextureAtlas uiAtlas;
    private SkeletonJson json;

    public static enum Particles {
        EXPLOSION, BLOOD, TOXIC
    }
    public static enum Animations {
        PLAYER, OBST_SPIKE, OBST_HAMMER, OBST_MOVING, PICKUP, TUT_JUMP, TUT_BOOST, TUT_BREAKABLE, SHIELD_BREAK
    }

    private ObjectMap<Particles, ParticleEffectPool> particleEffects;
    private ObjectMap<Animations, SkeletonData> skeletons;
    private ObjectMap<Animations, AnimationStateData> animationStates;

    private ObjectMap<String, TextureRegion> gameRegions;
	private ObjectMap<String, TextureRegion> uiRegions;

    private SoundManager soundManager;

    private I18NBundle bundle;

    public Assets(){
        gameRegions = new ObjectMap<String, TextureRegion>();
        uiRegions = new ObjectMap<String, TextureRegion>();
        assetManager = new AssetManager();
        assetManager.load(GAME_ATLAS, TextureAtlas.class);
        assetManager.load(UI_ATLAS, TextureAtlas.class);

        SkinLoader.SkinParameter skinParameter = new SkinLoader.SkinParameter(UI_ATLAS);
        assetManager.load(SKIN, Skin.class, skinParameter);

        ParticleEffectLoader.ParticleEffectParameter pep = new ParticleEffectLoader.ParticleEffectParameter();
        pep.atlasFile = PARTICLE_ATLAS;
        assetManager.load(P_EXPLOSION, ParticleEffect.class, pep);
        assetManager.load(P_BLOOD, ParticleEffect.class, pep);
        assetManager.load(P_TOXIC, ParticleEffect.class, pep);
        assetManager.load(I18N, I18NBundle.class);

        soundManager = new SoundManager(assetManager);
	}

    private boolean loaded = false;

    public boolean update(){
        final boolean isDone = assetManager.update();
        if (isDone && !loaded){
            finishLoading();
            loaded = true;
        }
        return isDone;
    }
	
	private void finishLoading(){
        gameAtlas = assetManager.get(GAME_ATLAS, TextureAtlas.class);
        particleAtlas = assetManager.get(PARTICLE_ATLAS, TextureAtlas.class);
        uiAtlas = assetManager.get(UI_ATLAS, TextureAtlas.class);

        skin = assetManager.get(SKIN, Skin.class);

        skin.getFont("default-font").setMarkupEnabled(true);
        final ParticleEffectPool explosionParticles = new ParticleEffectPool(
                assetManager.get(P_EXPLOSION, ParticleEffect.class), 16, Integer.MAX_VALUE);
        final ParticleEffectPool bloodParticles = new ParticleEffectPool(
                assetManager.get(P_BLOOD, ParticleEffect.class), 8, Integer.MAX_VALUE);
        final ParticleEffectPool toxicParticles = new ParticleEffectPool(
                assetManager.get(P_TOXIC, ParticleEffect.class), 8, Integer.MAX_VALUE);
        particleEffects = new ObjectMap<Particles, ParticleEffectPool>();
        particleEffects.put(Particles.BLOOD, bloodParticles);
        particleEffects.put(Particles.EXPLOSION, explosionParticles);
        particleEffects.put(Particles.TOXIC, toxicParticles);

        bundle = assetManager.get(I18N, I18NBundle.class);

        loadGameAnimations();
        loadUIAnimations();

        soundManager.finishLoading();
	}

    private void loadGameAnimations(){
        skeletons = new ObjectMap<Animations, SkeletonData>();
        animationStates = new ObjectMap<Animations, AnimationStateData>();

        json = new SkeletonJson(gameAtlas);
        json.setScale(GameScreen.BOX2D_TO_PIXEL);

        final String[] animFiles = {
                "data/anim/obst_spike.json",
                "data/anim/obst_hammer.json",
                "data/anim/obst_moving.json",
                "data/anim/pickup.json",
                "data/anim/broken_shield.json"
        };
        final Animations[] animNames = {
                Animations.OBST_SPIKE,
                Animations.OBST_HAMMER,
                Animations.OBST_MOVING,
                Animations.PICKUP,
                Animations.SHIELD_BREAK
        };

        for (int i = 0; i < animFiles.length; i++) {
            final SkeletonData skeletonData = json.readSkeletonData(Gdx.files.internal(animFiles[i]));
            final AnimationStateData animationData = new AnimationStateData(skeletonData);
            skeletons.put(animNames[i], skeletonData);
            animationStates.put(animNames[i], animationData);
        }
        initPlayerAnimations(1.5f);
    }
    // default
    private float lastScale = -1;

    public void initPlayerAnimations(float scale){
        // dont reinit if same scale as last time
        if (MathUtils.isEqual(lastScale, scale))
            return;
        lastScale = scale;
        json.setScale(GameScreen.BOX2D_TO_PIXEL*scale);
        final SkeletonData playerSkeletonData = json.readSkeletonData(Gdx.files.internal("data/anim/player.json"));
        final AnimationStateData playerAnimationData = new AnimationStateData(playerSkeletonData);
        skeletons.put(Animations.PLAYER, playerSkeletonData);
        animationStates.put(Animations.PLAYER, playerAnimationData);
    }

    private void loadUIAnimations(){
        final SkeletonJson json = new SkeletonJson(uiAtlas);
        json.setScale(GameScreen.BOX2D_TO_PIXEL);

        final String[] animTutFiles = {
                "data/anim/jump_tutorial.json",
                "data/anim/boost_tutorial.json",
                "data/anim/breakable_tutorial.json"
        };
        final Animations[] animTutNames = {
                Animations.TUT_JUMP,
                Animations.TUT_BOOST,
                Animations.TUT_BREAKABLE
        };

        for (int i = 0; i < animTutFiles.length; i++) {
            final SkeletonData skeletonData = json.readSkeletonData(Gdx.files.internal(animTutFiles[i]));
            final AnimationStateData animationData = new AnimationStateData(skeletonData);
            skeletons.put(animTutNames[i], skeletonData);
            animationStates.put(animTutNames[i], animationData);
        }
    }
	
	public void dispose(){
		skin.dispose();
        gameAtlas.dispose();
		particleAtlas.dispose();
		uiAtlas.dispose();
        assetManager.dispose();
        soundManager.dispose();

        gameRegions = null;
        uiRegions = null;
	}

    public TextureRegion getRegion(String name){
        if (!gameRegions.containsKey(name)){
            gameRegions.put(name, gameAtlas.findRegion(name));
        }
        return gameRegions.get(name);
    }

    public TextureRegion getUIRegion(String name){
        if (!uiRegions.containsKey(name)){
            uiRegions.put(name, uiAtlas.findRegion(name));
        }
        return uiRegions.get(name);
    }

    /**
	 * Return sprite scaled to box2d size
	 */
	public Sprite getScaledSprite(String name) {
		final Sprite sprite = new Sprite(getRegion(name));
		sprite.setSize(
				sprite.getWidth()*GameScreen.BOX2D_TO_PIXEL,
				sprite.getHeight()*GameScreen.BOX2D_TO_PIXEL);
		return sprite;
	}

    public String getText(String key){
        return bundle.get(key);
    }

    public String getText(String key, Object... args){
        return bundle.format(key, args);
    }

    public Skin getSkin() {
        return skin;
    }

    public ParticleEffectPool getParticles(Particles particle){
        return particleEffects.get(particle);
    }

    public SkeletonData getSkeletonData(Animations animation){
        return skeletons.get(animation);
    }

    public AnimationStateData getAnimationStateData(Animations animation){
        return animationStates.get(animation);
    }

    public SoundManager getSoundManager() {
        return soundManager;
    }

    public TextureAtlas getUiAtlas() {
        return uiAtlas;
    }
}
