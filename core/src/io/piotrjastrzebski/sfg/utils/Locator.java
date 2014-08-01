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

import com.badlogic.gdx.physics.box2d.World;

import box2dLight.RayHandler;
import io.piotrjastrzebski.sfg.ActionResolver;
import io.piotrjastrzebski.sfg.SFGApp;
import io.piotrjastrzebski.sfg.events.EventLoop;
import io.piotrjastrzebski.sfg.game.PlayerStats;

/**
 * Locator for various common things, so we dont have to pass the around all the time.
 * DI could be potentially better
 */
public class Locator {
    private static SFGApp app;
    private static Assets assets;
    private static ActionResolver actionResolver;
    private static Settings settings;
    private static PlayerStats stats;
    private static Config config;
    private static SoundManager sounds;
    private static EventLoop events;
    private static RayHandler rayHandler;
    private static World world;

    public static void provideApp(SFGApp app) {
        Locator.app = app;
    }

    public static void provideAssets(Assets assets) {
        Locator.assets = assets;
    }

    public static void provideActionResolver(ActionResolver actionResolver) {
        Locator.actionResolver = actionResolver;
    }

    public static void provideSettings(Settings settings) {
        Locator.settings = settings;
    }

    public static void providePlayerStats(PlayerStats stats) {
        Locator.stats = stats;
    }

    public static void provideConfig(Config config) {
        Locator.config = config;
    }

    public static void provideSounds(SoundManager sounds) {
        Locator.sounds = sounds;
    }

    public static void provideEvents(EventLoop events) {
        Locator.events = events;
    }

    public static void provideRayHandler(RayHandler rayHandler) {
        Locator.rayHandler = rayHandler;
    }

    public static void provideWorld(World world) {
        Locator.world = world;
    }

    public static SFGApp getApp() {
        return app;
    }

    public static Assets getAssets() {
        return assets;
    }

    public static ActionResolver getActionResolver() {
        return actionResolver;
    }

    public static Settings getSettings() {
        return settings;
    }

    public static PlayerStats getPlayerStats() {
        return stats;
    }

    public static Config getConfig() {
        return config;
    }

    public static SoundManager getSounds() {
        return sounds;
    }

    public static EventLoop getEvents() {
        return events;
    }

    public static RayHandler getRayHandler() {
        return rayHandler;
    }

    public static World getWorld() {
        return world;
    }

    public static void dispose() {
        Locator.app = null;
        Locator.assets = null;
        Locator.actionResolver = null;
        Locator.settings = null;
        Locator.stats = null;
        Locator.config = null;
        Locator.sounds = null;
        Locator.events = null;
        Locator.rayHandler = null;
        Locator.world = null;
    }
}
