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
import com.badlogic.gdx.assets.loaders.I18NBundleLoader;
import com.badlogic.gdx.assets.loaders.ParticleEffectLoader;
import com.badlogic.gdx.assets.loaders.SkinLoader;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.ParticleEffectPool;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.I18NBundle;
import com.badlogic.gdx.utils.ObjectMap;
import com.esotericsoftware.spine.AnimationStateData;
import com.esotericsoftware.spine.SkeletonData;
import com.esotericsoftware.spine.SkeletonJson;

import java.util.Locale;

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
    public final static String GAME_OVER = "game_over";
    public final static String DIALOG_LIGHTS_1 = "dialog_lights_1";
    public final static String DIALOG_LIGHTS_2 = "dialog_lights_2";
    public final static String OK = "ok";
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
    public static final String DIFFICULTY_BABY = "diff_baby";
    public static final String LIGHTS_ENABLED = "lights_on";
    public static final String LIGHTS_DISABLED = "lights_off";
    public static final String IMMERSIVE_ENABLED = "immersive_on";
    public static final String IMMERSIVE_DISABLED = "immersive_off";
    public static final String RESTORE_PURCHASES = "restore_purchases";
    public static final String RATE = "rate";
    public static final String RESET_TUTORIALS = "reset_tutorials";
    public static final String RESET_TUTORIALS_TOAST = "reset_tutorials_toast";

    private final static String GAME_ATLAS = "data/pack/sfg.atlas";
    private final static String PARTICLE_ATLAS = "data/pack/sfg_particles.atlas";
    private final static String UI_ATLAS = "data/pack/sfg_ui.atlas";
    private final static String SKIN = "data/skin.json";
    private final static String P_EXPLOSION = "data/particles/explosion.p";
    private final static String P_BLOOD = "data/particles/blood.p";

    private final static String I18N  = "data/i18n/SFGBundle";

    private final AssetManager assetManager;

    private Skin skin;

    private TextureAtlas gameAtlas;
    private TextureAtlas particleAtlas;
    private TextureAtlas uiAtlas;

    private ParticleEffectPool explosionParticles;
    private ParticleEffectPool bloodParticles;

    private SkeletonData playerSkeletonData;
    private SkeletonData spikeSkeletonData;
    private SkeletonData hammerSkeletonData;
    private SkeletonData pickupSkeletonData;
    private SkeletonData tutJumpSkeletonData;
    private SkeletonData tutBoostSkeletonData;

    private AnimationStateData playerAnimationData;
    private AnimationStateData spikeAnimationData;
    private AnimationStateData hammerAnimationData;
    private AnimationStateData pickupAnimationData;
    private AnimationStateData tutJumpAnimationData;
    private AnimationStateData tutBoostAnimationData;

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
        assetManager.load(I18N, I18NBundle.class);

        soundManager = new SoundManager(assetManager);
	}
	
    public boolean update(){
        final boolean isDone = assetManager.update();
        if (isDone){
            finishLoading();
        }
        return isDone;
    }
	
	private void finishLoading(){
        gameAtlas = assetManager.get(GAME_ATLAS, TextureAtlas.class);
        particleAtlas = assetManager.get(PARTICLE_ATLAS, TextureAtlas.class);
        uiAtlas = assetManager.get(UI_ATLAS, TextureAtlas.class);

        skin = assetManager.get(SKIN, Skin.class);

        skin.getFont("default-font").setMarkupEnabled(true);
        explosionParticles = new ParticleEffectPool(
                assetManager.get(P_EXPLOSION, ParticleEffect.class), 16, Integer.MAX_VALUE);
        bloodParticles = new ParticleEffectPool(
                assetManager.get(P_BLOOD, ParticleEffect.class), 8, Integer.MAX_VALUE);

        bundle = assetManager.get(I18N, I18NBundle.class);

		SkeletonJson json = new SkeletonJson(gameAtlas);
		json.setScale(GameScreen.BOX2D_TO_PIXEL);
		spikeSkeletonData = json.readSkeletonData(Gdx.files.internal("data/anim/obst_spike.json"));
		spikeAnimationData = new AnimationStateData(spikeSkeletonData);
		
		hammerSkeletonData = json.readSkeletonData(Gdx.files.internal("data/anim/obst_hammer.json"));
		hammerAnimationData = new AnimationStateData(hammerSkeletonData);
		
		pickupSkeletonData = json.readSkeletonData(Gdx.files.internal("data/anim/pickup.json"));
		pickupAnimationData = new AnimationStateData(pickupSkeletonData);
		
		// we are lazy, just make the player a bit bigger
		// maybe we will fix it later
		json.setScale(GameScreen.BOX2D_TO_PIXEL*1.5f);
		playerSkeletonData = json.readSkeletonData(Gdx.files.internal("data/anim/player.json"));
		playerAnimationData = new AnimationStateData(playerSkeletonData);


        json = new SkeletonJson(uiAtlas);
        json.setScale(GameScreen.BOX2D_TO_PIXEL);
        tutJumpSkeletonData = json.readSkeletonData(Gdx.files.internal("data/anim/jump_tutorial.json"));
        tutJumpAnimationData = new AnimationStateData(tutJumpSkeletonData);

        tutBoostSkeletonData = json.readSkeletonData(Gdx.files.internal("data/anim/boost_tutorial.json"));
        tutBoostAnimationData = new AnimationStateData(tutBoostSkeletonData);

        soundManager.finishLoading();
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

    public ParticleEffectPool getExplosionParticles() {
        return explosionParticles;
    }

    public ParticleEffectPool getBloodParticles() {
        return bloodParticles;
    }

    public SoundManager getSoundManager() {
        return soundManager;
    }

    public SkeletonData getPlayerSkeletonData() {
        return playerSkeletonData;
    }

    public SkeletonData getSpikeSkeletonData() {
        return spikeSkeletonData;
    }

    public SkeletonData getHammerSkeletonData() {
        return hammerSkeletonData;
    }

    public SkeletonData getTutJumpSkeletonData() {
        return tutJumpSkeletonData;
    }

    public SkeletonData getTutBoostSkeletonData() {
        return tutBoostSkeletonData;
    }

    public SkeletonData getPickupSkeletonData() {
        return pickupSkeletonData;
    }

    public AnimationStateData getPlayerAnimationData() {
        return playerAnimationData;
    }

    public AnimationStateData getSpikeAnimationData() {
        return spikeAnimationData;
    }

    public AnimationStateData getHammerAnimationData() {
        return hammerAnimationData;
    }

    public AnimationStateData getPickupAnimationData() {
        return pickupAnimationData;
    }

    public AnimationStateData getTutBoostAnimationData() {
        return tutBoostAnimationData;
    }

    public AnimationStateData getTutJumpAnimationData() {
        return tutJumpAnimationData;
    }

    public TextureAtlas getUiAtlas() {
        return uiAtlas;
    }
}
