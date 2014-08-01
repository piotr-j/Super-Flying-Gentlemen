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
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.utils.ObjectMap;

import io.piotrjastrzebski.sfg.SFGApp;
import io.piotrjastrzebski.sfg.events.Event;
import io.piotrjastrzebski.sfg.events.EventListener;
import io.piotrjastrzebski.sfg.events.EventLoop;
import io.piotrjastrzebski.sfg.events.EventType;

public class SoundManager implements EventListener {
    private final static String MUSIC_ENABLED = "MUSIC_ENABLED";
    private final static String MUSIC_VOLUME = "MUSIC_VOLUME";
    private final static String SOUND_ENABLED = "SOUND_ENABLED";
    private final static String SOUND_VOLUME = "SOUND_VOLUME";

    private final static String MUSIC = "data/sound/parasite.ogg";

    public final static String EXPLOSION = "data/sound/explosion.wav";
    public final static String HURT = "data/sound/hurt.wav";
    public final static String DEATH = "data/sound/death.wav";
    public final static String PICKUP = "data/sound/powerup.wav";
    public final static String ROCKET = "data/sound/rocket.wav";
    public final static String BUTTON_PRESS = "data/sound/click.wav";
    public final static String BOOST = "data/sound/boost.wav";
    public final static String HAMMER = "data/sound/hammer.wav";
    public final static String SPIKE = "data/sound/spike.wav";

    private final static String[] SOUNDS = {
        EXPLOSION, ROCKET, BOOST, PICKUP, HAMMER,
        SPIKE, HURT, DEATH, BUTTON_PRESS
    };

    private final AssetManager assetManager;
    private Music gameMusic;
    private ObjectMap<String, Sound> sounds;
    private boolean isMusicEnabled;
    private boolean isSoundEnabled;
    private float soundVolume;
    private float musicVolume;
    private Preferences preferences;

    public SoundManager(AssetManager assetManager){
        this.assetManager = assetManager;
        sounds = new ObjectMap<String, Sound>(8);
        assetManager.load(MUSIC, Music.class);
        for (String SOUND : SOUNDS) {
            assetManager.load(SOUND, Sound.class);
        }
        setEventLoop(Locator.getEvents());
    }

    private void setEventLoop(EventLoop events){
        events.register(this, EventType.PLAY_MUSIC);
        events.register(this, EventType.STOP_MUSIC);
        events.register(this, EventType.TOGGLE_MUSIC);
        events.register(this, EventType.PLAY_SOUND);
        events.register(this, EventType.TOGGLE_SOUND);
    }

    public void finishLoading(){
        gameMusic = assetManager.get(MUSIC, Music.class);
        gameMusic.setLooping(true);
        for (String SOUND : SOUNDS) {
            sounds.put(SOUND, assetManager.get(SOUND, Sound.class));
        }
        preferences = Gdx.app.getPreferences(SFGApp.PREFS);
        loadState();
    }

    public void saveState(){
        preferences.putBoolean(SOUND_ENABLED, isSoundEnabled);
        preferences.putFloat(SOUND_VOLUME, soundVolume);
        preferences.putBoolean(MUSIC_ENABLED, isMusicEnabled);
        preferences.putFloat(MUSIC_VOLUME, musicVolume);
        preferences.flush();
    }

    private void loadState(){
        isSoundEnabled = preferences.getBoolean(SOUND_ENABLED, false);
        soundVolume = preferences.getFloat(SOUND_VOLUME, 1);
        isMusicEnabled = preferences.getBoolean(MUSIC_ENABLED, false);
        musicVolume = preferences.getFloat(MUSIC_VOLUME, 1);
    }


    public void playMusic(){
        gameMusic.setVolume(musicVolume);
        if (isMusicEnabled && !gameMusic.isPlaying())
            gameMusic.play();
    }

    public void pauseMusic(){
        gameMusic.pause();
    }

    public void stopMusic(){
        pauseMusic();
        gameMusic.stop();
    }

    public void playSound(String sound){
        if (isSoundEnabled){
            if (sounds.containsKey(sound)){
                sounds.get(sound).play(soundVolume);
            }
        }
    }

    public void toggleSound(boolean enabled) {
        isSoundEnabled = enabled;
        saveState();
    }

    public void toggleMusic(boolean enabled) {
        isMusicEnabled = enabled;
        if (!enabled){
            stopMusic();
        } else {
            playMusic();
        }
        saveState();
    }

    public boolean isSoundEnabled(){
        return isSoundEnabled;
    }

    public boolean isMusicEnabled(){
        return isMusicEnabled;
    }

    public void setSoundVolume(float volume){
        soundVolume = volume;
        isSoundEnabled = volume >= 0.01f;
    }

    public float getSoundVolume() {
        return soundVolume;
    }

    public void setMusicVolume(float volume){
        musicVolume = volume;
        isMusicEnabled = volume >= 0.01f;
        gameMusic.setVolume(volume);
        if (isMusicEnabled)
            gameMusic.play();
    }

    public float getMusicVolume() {
        return musicVolume;
    }

    public void dispose(){
        saveState();
    }

    @Override
    public void handleEvent(Event e) {
        switch (e.getType()){
            case EventType.PLAY_MUSIC:
                playMusic();
                break;
            case EventType.STOP_MUSIC:
                stopMusic();
                break;
            case EventType.TOGGLE_MUSIC:
                final Boolean toggleM = (Boolean) e.getData();
                toggleMusic(toggleM);
                break;
            case EventType.TOGGLE_SOUND:
                final Boolean toggleS = (Boolean) e.getData();
                toggleSound(toggleS);
                break;
            case EventType.PLAY_SOUND:
                final String sound = (String) e.getData();
                playSound(sound);
                break;
            default: break;
        }
    }
}
