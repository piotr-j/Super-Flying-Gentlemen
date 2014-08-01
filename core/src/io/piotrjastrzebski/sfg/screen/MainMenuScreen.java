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

package io.piotrjastrzebski.sfg.screen;

import io.piotrjastrzebski.sfg.ActionListener;
import io.piotrjastrzebski.sfg.events.EventType;
import io.piotrjastrzebski.sfg.game.objects.Background;
import io.piotrjastrzebski.sfg.screen.inputhandlers.MainMenuInputHandler;
import io.piotrjastrzebski.sfg.ui.GameButton;
import io.piotrjastrzebski.sfg.utils.Assets;
import io.piotrjastrzebski.sfg.utils.Locator;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Scaling;

public class MainMenuScreen extends DefaultScreen implements ActionListener {
    public static final float PAD = 20;

    private Background background;
    private TextureRegion logo;
    private TextButton gPlusSignIn;
    private GameButton achievButton;
    private GameButton leaderButton;

    private boolean isSignedIn = false;

    public MainMenuScreen() {
        super();
        Gdx.input.setInputProcessor(new MainMenuInputHandler(this).getIM());
        Gdx.input.setCatchBackKey(true);
        actionResolver.sendScreenView("MainMenuScreen");
        background = new Background();
        logo = assets.getRegion("game_logo");

        isSignedIn = actionResolver.isSignedIn();
        actionResolver.registerActionListener(this);

        assets.getSoundManager().playMusic();

        addUI();

        toogleSignedInButtons();
    }

    private void addUI() {
        final Table root = new Table();
        root.setFillParent(true);

        final Table topCont = new Table();
        topCont.add(createSoundButton());
        if (Gdx.app.getType() == Application.ApplicationType.Android) {
            topCont.add(createGPlusButton()).expandX().top();
        } else {
            topCont.add().expandX().top();
        }
        topCont.add(createMusicButton());
        topCont.debug();

        root.add(topCont).pad(PAD).expandX().fillX();
        root.row();

        final Table midCont = new Table();
        midCont.add(createPlayButton()).padBottom(PAD*2);
        midCont.row();
        midCont.add(createOutfitButton()).pad(16);
        midCont.row();
        midCont.add(createLeaderButton()).pad(16);
        midCont.row();
        midCont.add(createAchievButton()).padTop(16);

        root.add(midCont).expand().bottom();
        root.row();

        final Table botCont = new Table();
        botCont.add(createAboutButton());
        botCont.add().expandX().fillX();
        botCont.add(createSettingsButton());
        root.add(botCont).fillX().expandX().pad(PAD);

        root.debug();
        stage.addActor(root);
    }

    private Actor createGPlusButton(){
        // player can be automatically logged in
        final String signInText = isSignedIn ?
                assets.getText(Assets.SIGN_OUT) :  assets.getText(Assets.SIGN_IN);
        // special g+ branded skin
        gPlusSignIn = new TextButton(signInText, assets.getSkin(), "gplus");
        gPlusSignIn.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                // this will show native sign in dialog
                if (!isSignedIn){
                    actionResolver.signIn();
                } else {
                    actionResolver.signOut();
                }
            }
        });
        // center origin so its scaled from it
        gPlusSignIn.setOrigin(gPlusSignIn.getWidth()/2, gPlusSignIn.getHeight()/2);
        // allow scaling
        gPlusSignIn.setTransform(true);
        // invisible
        gPlusSignIn.setColor(1, 1, 1, 0);
        gPlusSignIn.setScale(0.5f);
        // move the button off screen and slide it back in
        gPlusSignIn.addAction(
                Actions.parallel(
                        Actions.scaleTo(1, 1, 0.5f, Interpolation.pow2),
                        Actions.fadeIn(0.5f)
                )
        );
        return gPlusSignIn;
    }

    private Actor createOutfitButton(){
        GameButton outfitButton = new GameButton(
                assets.getText(Assets.OUTFITS),
                assets.getUIRegion("dude_white"),
                assets.getSkin()
        );
        outfitButton.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Locator.getApp().setScreen(new SkinSelectScreen());
                playButtonPressSound();
            }
        });
        return outfitButton;
    }

    private Actor createLeaderButton(){
        leaderButton = new GameButton(
                assets.getText(Assets.LEADER_BOARDS),
                assets.getUIRegion("g_leaderboards"),
                assets.getSkin()
        );
        leaderButton.setVisible(false);
        leaderButton.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (isSignedIn)
                    actionResolver.showLeaderBoard();
                playButtonPressSound();
            }
        });
        return leaderButton;
    }

    private Actor createAchievButton(){
        achievButton = new GameButton(
                assets.getText(Assets.ACHIEVEMENTS),
                assets.getUIRegion("g_achievements"),
                assets.getSkin()
        );
        achievButton.setVisible(false);
        achievButton.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (isSignedIn)
                    actionResolver.showAchievements();
                playButtonPressSound();
            }
        });
        return achievButton;
    }

    private Actor createSoundButton(){
        // special sound skin
        final Button soundsButton = new Button(assets.getSkin(), "sound");
        // checked state is disabled so we need to flip it
        soundsButton.setChecked(!assets.getSoundManager().isSoundEnabled());
        soundsButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                eventLoop.queueEvent(EventType.TOGGLE_SOUND, !soundsButton.isChecked());
                playButtonPressSound();
            }
        });
        // invisible
        soundsButton.setColor(1, 1, 1, 0);
        // move the button off screen and slide it back in
        soundsButton.addAction(Actions.fadeIn(0.5f));
        return soundsButton;
    }

    private Actor createMusicButton(){
        // special music  skin
        final Button musicButton = new Button(assets.getSkin(), "music");
        // checked state is disabled so we need to flip it
        musicButton.setChecked(!assets.getSoundManager().isMusicEnabled());
        musicButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                eventLoop.queueEvent(EventType.TOGGLE_MUSIC, !musicButton.isChecked());
                playButtonPressSound();
            }
        });
        // invisible
        musicButton.setColor(1, 1, 1, 0);
        // move the button off screen and slide it back in
        musicButton.addAction(Actions.fadeIn(0.5f));
        return musicButton;
    }

    private Actor createPlayButton(){
        final TextureRegionDrawable rocketOnDrawable = new TextureRegionDrawable(assets.getUIRegion("rocket_on"));
        final Image rocket = new Image(new TextureRegionDrawable(assets.getUIRegion("rocket")));
        // so rocket isnt stretched with glowing background in stack
        rocket.setScaling(Scaling.none);
        // glow to direct player to play button after few seconds
        final Image glow = new Image(new NinePatchDrawable(
                assets.getUiAtlas().createPatch("button_glow")));
        // invisible at start
        glow.setColor(1, 1, 1, 0);
        // after 2s start fading in and out forever
        glow.addAction(Actions.delay(2,
                Actions.forever(Actions.sequence(
                                Actions.fadeIn(1, Interpolation.fade),
                                Actions.fadeOut(1, Interpolation.fade))
                ))
        );

        final Button startGame = new Button(assets.getSkin());
        // pulse the button with glow
        startGame.setTransform(true);
        startGame.setScale(1.25f);
        startGame.setOrigin(startGame.getWidth()/2, startGame.getHeight()/2);
        startGame.addAction(Actions.delay(2,
                        Actions.forever(Actions.sequence(
                                        Actions.scaleTo(1.33f, 1.33f, 1, Interpolation.fade),
                                        Actions.scaleTo(1.25f, 1.25f, 1, Interpolation.fade))
                        )
                )
        );
        startGame.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                // kick back the button a bit
                startGame.clearActions();
                glow.clearActions();
                startGame.addAction(Actions.moveBy(-30, 0, 0.5f, Interpolation.exp5Out));
                rocket.setDrawable(rocketOnDrawable);
                // fire off the rocket and change screen when its off screen
                rocket.addAction(Actions.sequence(
                        Actions.moveBy(stage.getWidth() / 2 + 100, 0, 0.5f, Interpolation.exp5In),
                        // change the screen when rocket moves off screen
                        Actions.run(new Runnable() {
                            @Override
                            public void run() {
                                Locator.getApp().setScreen(new DifficultySelectScreen());
                            }
                        }))
                );
                playButtonPressSound();
            }
        });
        // stack so we can have rocket and glow overlap
        final Stack stack = new Stack();
        stack.add(glow);
        stack.add(rocket);
        // fill the button but leave pad for border
        startGame.add(stack).fill().expand().pad(4);
        return startGame;
    }

    private Actor createSettingsButton(){
        // special settings skin
        final Button settings = new Button(assets.getSkin(), "settings");
        // so it rotate around center not corner
        settings.setOrigin(
                settings.getWidth()/2,
                settings.getHeight()/2
        );
        settings.setColor(1, 1, 1, 0);
        settings.addAction(Actions.fadeIn(0.5f));
        // so it can rotate
        settings.setTransform(true);
        // spin slowly at start
        settings.addAction(
                Actions.forever(Actions.rotateBy(-360, 16))
        );
        settings.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Locator.getApp().setScreen(new SettingsScreen());
                playButtonPressSound();
            }

            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                // fast spinning
                settings.clearActions();
                settings.setColor(1, 1, 1, 1);
                settings.addAction(
                        Actions.forever(Actions.rotateBy(-360, 2f))
                );
                super.enter(event, x, y, pointer, fromActor);

            }

            @Override
            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                // slow spinning
                settings.clearActions();
                settings.addAction(
                        Actions.forever(Actions.rotateBy(-360, 16))
                );
                super.exit(event, x, y, pointer, toActor);
            }
        });
        return settings;
    }

    private Actor createAboutButton(){
        final Button aboutGame = new Button(assets.getSkin(), "about");
        aboutGame.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Locator.getApp().setScreen(new AboutScreen());
                playButtonPressSound();
            }
        });
        // invisible
        aboutGame.setColor(1, 1, 1, 0);
        // move the button off screen and slide it back in
        aboutGame.addAction(Actions.fadeIn(0.5f));
        return aboutGame;
    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
        background.updateViewport(width, height);
    }

    @Override
    public void update(float delta) {
        super.update(delta);
        background.update(delta);
    }

    @Override
    public void draw() {
        super.draw();
        background.draw(batch);
        final Camera cam = stage.getCamera();
        batch.setProjectionMatrix(cam.combined);
        batch.begin();
        batch.draw(logo,
                (cam.viewportWidth - logo.getRegionWidth()) * 0.5f,
                cam.viewportHeight * 0.5f + logo.getRegionHeight());
        batch.end();

        stage.draw();
//		Table.drawDebug(stage);
    }

    public void handleBack() {
        Gdx.app.exit();
    }

    public void handleGo() {
        Locator.getApp().setScreen(new DifficultySelectScreen());
    }


    @Override
    public void dispose() {
        super.dispose();
        actionResolver.unRegisterActionListener(this);
    }

    private void toogleSignedInButtons(){
        if (isSignedIn){
            leaderButton.setVisible(true);
            achievButton.setVisible(true);
            leaderButton.getColor().a = 0;
            achievButton.getColor().a = 0;
            leaderButton.addAction(Actions.fadeIn(0.5f));
            achievButton.addAction(Actions.fadeIn(0.5f));
        } else {
            leaderButton.addAction(Actions.fadeOut(0.5f));
            leaderButton.addAction(Actions.sequence(
                    Actions.fadeOut(0.5f),
                    Actions.run(new Runnable() {
                        @Override
                        public void run() {
                            leaderButton.setVisible(false);
                        }
                    })
            ));
            achievButton.addAction(Actions.sequence(
                    Actions.fadeOut(0.5f),
                    Actions.run(new Runnable() {
                        @Override
                        public void run() {
                            achievButton.setVisible(false);
                        }
                    })
            ));
        }
    }

    @Override
    public void handleEvent(int id, Object data) {
        switch (id){
            case ActionListener.SIGN_IN:
                gPlusSignIn.setText(assets.getText(Assets.SIGN_OUT));
                isSignedIn = true;
                toogleSignedInButtons();
                break;
            case ActionListener.SIGN_IN_FAILED:
            case ActionListener.SIGN_OUT:
                gPlusSignIn.setText(assets.getText(Assets.SIGN_IN));
                isSignedIn = false;
                toogleSignedInButtons();
                break;
            default:break;
        }
    }
}
