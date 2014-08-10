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

import box2dLight.RayHandler;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Pools;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.esotericsoftware.spine.SkeletonRenderer;

import io.piotrjastrzebski.sfg.SFGApp;
import io.piotrjastrzebski.sfg.events.Event;
import io.piotrjastrzebski.sfg.events.EventListener;
import io.piotrjastrzebski.sfg.events.EventLoop;
import io.piotrjastrzebski.sfg.events.EventType;
import io.piotrjastrzebski.sfg.game.ContactDispatcher;
import io.piotrjastrzebski.sfg.game.PlayerStats;
import io.piotrjastrzebski.sfg.game.SFGGame;
import io.piotrjastrzebski.sfg.game.objects.Background;
import io.piotrjastrzebski.sfg.game.objects.obstacles.Obstacle;
import io.piotrjastrzebski.sfg.game.tutorials.Boost;
import io.piotrjastrzebski.sfg.game.tutorials.Jump;
import io.piotrjastrzebski.sfg.screen.inputhandlers.GameInputHandler;
import io.piotrjastrzebski.sfg.ui.BoostBar;
import io.piotrjastrzebski.sfg.ui.GameOverDialog;
import io.piotrjastrzebski.sfg.utils.Assets;
import io.piotrjastrzebski.sfg.utils.Config;
import io.piotrjastrzebski.sfg.utils.Config.Difficulty;
import io.piotrjastrzebski.sfg.utils.FPSCheck;
import io.piotrjastrzebski.sfg.utils.Locator;

public class GameScreen extends DefaultScreen implements EventListener {
    private SkeletonRenderer skeletonRenderer;

    protected enum GameState {
        PAUSED, RUNNING, DEAD, GAME_OVER
    }
	public final static float VIEWPORT_HEIGHT = 32.0f;

    public final static float VIEWPORT_WIDTH = 20.0f;
    // 1 meter in box2d equals to 48 pixels
	public final static float BOX2D_TO_PIXEL = 1/48.0f;
    // Offset from center for camera
	public static final float PLAYER_OFFSET = VIEWPORT_WIDTH/4;
    private GameState state = GameState.RUNNING;
    private Label scoreLabel;
    private Table scoreContainer;
    private Label livesLabel;
    private Table livesContainer;
    private Label shieldsLabel;
    private Table shieldsContainer;
    private OrthographicCamera gameCamera;

    private final EventLoop eventLoop;
    private PlayerStats playerStats;

    private ExtendViewport gameViewPort;
    private Box2DDebugRenderer debugRenderer = null;
	private SFGGame sfgGame;

    private BoostBar boostBar;
    private Config config;
    private float dashDelay;
    private World world;
    private RayHandler rayHandler;
	private Background background;

	private float deathTimer;

    private boolean isPlayerDead = false;
    private GameOverDialog gameOverDialog;

    float zoom;
    float playerOffset = 0;
    float zoomTimer = 0;
    float zoomScale = 1;
    boolean isZooming = true;

    private Table topContainer;

    private FPSCheck fpsCheck;

    private Jump jumpTut;
    private Boost boostTut;

    private Color toxicColor;
    private Color redColor;
    private Color whiteColor;

    private int lastLives;
    private int lastShields;

	public GameScreen(Difficulty difficulty) {
		super();
        Gdx.input.setInputProcessor(new GameInputHandler(this).getIM());
        Gdx.input.setCatchBackKey(true);
        config = new Config(difficulty);
        eventLoop = Locator.getEvents();
        registerEvents();
        actionResolver = Locator.getActionResolver();
        settings = Locator.getSettings();
        fpsCheck = new FPSCheck(this);
        actionResolver.sendScreenView("GameScreen " + difficulty.toString());
        initGame();
        initUI();
    }

    private void registerEvents() {
        eventLoop.register(this, EventType.SHOW_BOOST_TUT);
        eventLoop.register(this, EventType.PLAYER_ALIVE);
        eventLoop.register(this, EventType.PLAYER_LIVES_CHANGED);
        eventLoop.register(this, EventType.PLAYER_BOOST_CHANGED);
        eventLoop.register(this, EventType.PLAYER_SCORE_CHANGED);
        eventLoop.register(this, EventType.PLAYER_SHIELDS_CHANGED);
    }

    private void unRegisterEvents() {
        eventLoop.unregister(this, EventType.SHOW_BOOST_TUT);
        eventLoop.unregister(this, EventType.PLAYER_ALIVE);
        eventLoop.unregister(this, EventType.PLAYER_LIVES_CHANGED);
        eventLoop.unregister(this, EventType.PLAYER_BOOST_CHANGED);
        eventLoop.unregister(this, EventType.PLAYER_SCORE_CHANGED);
        eventLoop.unregister(this, EventType.PLAYER_SHIELDS_CHANGED);
    }

    private void initGame(){
        skeletonRenderer = new SkeletonRenderer();
        playerStats = Locator.getPlayerStats();
        playerStats.setDifficulty(config.getDifficulty());

        gameCamera = new OrthographicCamera();
		gameViewPort = new ExtendViewport(VIEWPORT_WIDTH, VIEWPORT_HEIGHT, gameCamera);
		gameCamera.position.set(10, 16, 0);
		gameCamera.update();

        world = new World(new Vector2(0, -50), true);
        world.setContactListener(new ContactDispatcher());

        RayHandler.setGammaCorrection(true);
        RayHandler.useDiffuseLight(true);
        rayHandler = new RayHandler(world);
        rayHandler.setAmbientLight(0.35f, 0.35f, 0.5f, 1);
        rayHandler.setCulling(true);
        rayHandler.setBlurNum(1);

        initLocator();

        background = new Background();
		background.setGroundOffset(5.5f);

		sfgGame = new SFGGame(this);
		if (SFGApp.DEBUG_BOX2D) {
            debugRenderer = new Box2DDebugRenderer();
        }
    }

    private Table createContainer(Actor actor){
        Table table = new Table();
        table.add(actor);
        table.setTransform(true);
        return table;
    }

    private Label createLabel(){
        final Skin skin = assets.getSkin();
        return new Label("0", skin, "default-large");
    }

    private void centreOrigin(Actor actor){
        actor.setOrigin(actor.getWidth()/2, actor.getHeight()/2);
    }

	private void initUI(){
		Table root = new Table();
        root.setFillParent(true);

        scoreLabel = createLabel();
        scoreContainer = createContainer(scoreLabel);

        livesLabel = createLabel();
        livesContainer = createContainer(livesLabel);

        shieldsLabel = createLabel();
        shieldsContainer = createContainer(shieldsLabel);

        topContainer = new Table();
        dashDelay = config.getPlayerDashDelay();
        boostBar = new BoostBar(assets, 0, dashDelay, dashDelay/1000.0f);
        boostBar.setAnimateFromValue(config.getPlayerDashDelay());
        boostBar.setEnabled(true);
        boostBar.reset();
        topContainer.add(boostBar).expandX().fillX();
        topContainer.row();

        Table labelContainer = new Table();
        labelContainer.add(new Label(assets.getText(Assets.SCORE), assets.getSkin())).padRight(20);
        labelContainer.add(scoreContainer);
        labelContainer.add().expandX().expandX().fillX();
        labelContainer.add(shieldsContainer).padRight(20);
        labelContainer.add(new Image(assets.getUIRegion("shield_small"))).padRight(20);
        labelContainer.add(livesContainer).padRight(20);
        labelContainer.add(new Image(assets.getUIRegion("hearth_small")));
        topContainer.add(labelContainer).expandX().fillX();
        // invisible at start
        topContainer.setColor(1, 1, 1, 0);

        root.add(topContainer).fillX().expandX().pad(20);
        root.row();

        root.row();
        root.add().expand().fill();
        stage.addActor(root);

        gameOverDialog = new GameOverDialog(assets, playerStats.getMaxScore()){
            @Override
            protected void result(Object object) {
                GameOverDialog.RESULT result = (RESULT) object;
                switch (result){
                    case RESTART:
                        GameScreen.this.reset();
                        actionResolver.sendGameGAEvent("PlayerReSpawned", "");
                        break;
                    case LEADER_BOARDS:
                        playerStats.showLeaderBoards();
                        cancel();
                        break;
                    case ACHIEVEMENTS:
                        playerStats.showAchievements();
                        cancel();
                        break;
                    case PREMIUM:
                        playerStats.buyPremium();
                        cancel();
                        break;
                    case RATE:
                        actionResolver.rateApp();
                        gameOverDialog.removeRateButton();
                        settings.setRated(true);
                        cancel();
                        break;
                }
                playButtonPressSound();
            }
        };
        actionResolver.registerActionListener(gameOverDialog);
        initZoom();

        if (!settings.getTutJumpShowed()){
            jumpTut = new Jump(assets);
        }

        if (!settings.getTutBoostShowed()){
            boostTut = new Boost(assets);
            eventLoop.register(this, EventType.SHOW_BOOST_TUT);
        }

        toxicColor = assets.getSkin().getColor("toxic");
        redColor = assets.getSkin().getColor("red");
        whiteColor = assets.getSkin().getColor("white");
    }

    private void initLocator(){
        Locator.provideConfig(config);
        Locator.provideRayHandler(rayHandler);
        Locator.provideWorld(world);
    }

    private void deInitLocator(){
        Locator.provideConfig(null);
        Locator.provideRayHandler(null);
        Locator.provideWorld(null);
    }

    @Override
    public void resume() {
        super.resume();
        initLocator();
    }

    public void pauseGame(){
        switch (state) {
		case PAUSED:
			backToMenu();
			break;
		case DEAD:
		case RUNNING:
			state = GameState.PAUSED;
			sfgGame.pauseGame();
            boostBar.setEnabled(false);
			break;
		default:
			break;
		}
	}
	
	public void reset(){
        actionResolver.hideAd();
        initZoom();
		deathTimer = 0;
		gameCamera.position.set(10, 16, 0);
		gameCamera.update();
        isPlayerDead = false;
		sfgGame.reset();
        background.reset();
		state = GameState.RUNNING;
        boostBar.setEnabled(true);
        boostBar.reset();
    }
	
	private void backToMenu(){
		Locator.getApp().setScreen(new MainMenuScreen());
	}

    public void resumeGame(){
		if (state != GameState.PAUSED){
			return;
		}
        boostBar.setEnabled(true);
        sfgGame.resumeGame();
		state = GameState.RUNNING;
	}

    private void gameOver(){
        state = GameState.GAME_OVER;
        gameOverDialog.updateScores(
                playerStats.getScore(), playerStats.getMaxScore(), playerStats.isPremium());
        actionResolver.showAd();
        gameOverDialog.show(stage);
        // so keyboard works when dialog is visible
        stage.setKeyboardFocus(null);
    }

    private void initZoom(){
        topContainer.setColor(1, 1, 1, 0);
        isZooming = true;
        zoomTimer = 0;
        zoom = 0.5f;
        playerOffset = 0;
        zoomScale = 1;

        gameCamera.zoom = zoom;
        background.zoom(zoom);
    }

    private void updateZoom(float delta){
        if (state != GameState.RUNNING)
            return;
        zoomTimer +=delta;
        if (isZooming && zoomTimer > 2){
            // starts at 0.5f
            if (zoom < 1){
                zoom += delta*0.1f*zoomScale;
                // move player to the size
                playerOffset += delta*zoomScale;
                // fade in labels
                topContainer.setColor(1, 1, 1, playerOffset * 0.2f);
            } else {
                zoom = 1;
                playerOffset = PLAYER_OFFSET;
                isZooming = false;
                topContainer.setColor(1, 1, 1, 1);
                if (jumpTut != null){
                    jumpTut.init(gameCamera.position.x+8, 16);
                }
            }
            background.zoom(zoom);
            gameCamera.zoom = zoom;
        }
    }

    public void updateCamera(float x){
        gameCamera.position.x = x + playerOffset;
    }

    @Override
	public void update(float delta) {
        super.update(delta);
		if (!isPlayerDead){
			updateCamera(sfgGame.getPlayerX());
		}
        background.update(delta, gameCamera.position.x);
        gameCamera.update();
        if (fpsCheck.isLightsOn()) {
            rayHandler.update();
        }

        if (jumpTut != null){
            jumpTut.update(delta, gameCamera.position.x+2);
            if (jumpTut.isDisabled()) {
                jumpTut = null;
            }
        }

        if (boostTut != null){
            boostTut.update(delta, gameCamera.position.x+2);
            if (boostTut.isDisabled()) {
                boostTut = null;
            }
        }

        switch (state) {
            case PAUSED:
                break;
            case DEAD:
                deathTimer +=delta;
                // fade out labels on death
                if (deathTimer < 1){
                    topContainer.setColor(1, 1, 1, 2 - deathTimer * 2);
                } else {
                    topContainer.setColor(1, 1, 1, 0);
                }
                if (deathTimer > 2){
                    gameOver();
                }
                sfgGame.update(delta);
                updateZoom(delta);
                disableTuts();
                break;
            case GAME_OVER:
                sfgGame.update(delta);
                updateZoom(delta);
                break;
            case RUNNING:
                sfgGame.update(delta);
                updateZoom(delta);
                fpsCheck.updateLightCheck(delta);
                if (isPlayerDead && state == GameState.RUNNING){
                    state = GameState.DEAD;
                }
                break;
            default:
                break;
        }
    }

    @Override
	public void draw() {
		super.draw();
		background.draw(batch);
		rayHandler.setCombinedMatrix(gameCamera.combined);
		batch.setProjectionMatrix(gameCamera.combined);
		sfgGame.draw(batch);
        if (fpsCheck.isLightsOn()) {
            rayHandler.render();
        }
        if (SFGApp.DEBUG_BOX2D) {
            debugRenderer.render(sfgGame.getWorld(), gameCamera.combined);
        }
        if (jumpTut != null || boostTut != null) {
            batch.begin();
            if (jumpTut != null) {
                jumpTut.draw(batch, skeletonRenderer);
            }
            if (boostTut != null) {
                boostTut.draw(batch, skeletonRenderer);
            }
            batch.end();
        }
        stage.draw();
	}

	public void handleTap(){
        if (jumpTut != null && jumpTut.isVisible()) {
            jumpTut.hide();
            settings.setTutJumpShowed(true);
        }
		switch (state) {
		case PAUSED:
			resumeGame();
            break;
		case RUNNING:
			sfgGame.tap();
            // zoom in faster
            if (isZooming) {
                zoomScale = 5;
                zoomTimer = 2;
            }
			break;
		case DEAD:
			// short delay so you dont immediately show game over dialog
			if (deathTimer > 0.25f && deathTimer < 1.75f){
                deathTimer = 1.75f;
			}
			break;
        // this will only work for keyboard, as stage eats all the touch inputs
        case GAME_OVER:
            reset();
            gameOverDialog.hide();
            break;
		default: break;
		}
	}
	
	public void handleFling(){
        if (boostTut!=null && boostTut.isVisible()){
            boostTut.hide();
            settings.setTutBoostShowed(true);
            eventLoop.unregister(this, EventType.SHOW_BOOST_TUT);
        }

        switch (state) {
		case RUNNING:
			sfgGame.swipe();
            if (isZooming) {
                zoomScale = 5;
                zoomTimer = 2;
            }
			break;
		default:
			handleTap();
			break;
		}
	}
	
	public void handleBack(){
        switch (state){
            case RUNNING:
                pauseGame();
                break;
            case DEAD:
                deathTimer = 2;
                break;
            case PAUSED:
                backToMenu();
                break;
            case GAME_OVER:
                reset();
                gameOverDialog.hide();
                break;
            default: break;
        }
	}

    public void handleEnter() {
        switch (state){
            case RUNNING:
                pauseGame();
                break;
            case DEAD:
                deathTimer = 2;
                break;
            case PAUSED:
                resumeGame();
                break;
            case GAME_OVER:
                reset();
                gameOverDialog.hide();
                break;
            default: break;
        }
    }

    @Override
	public void resize(int width, int height) {
        super.resize(width, height);
        gameViewPort.update(width, height);
		sfgGame.updateViewport(gameViewPort);
		background.updateViewport(width, height);
        gameOverDialog.updatePosition();
    }


	@Override
	public void dispose() {
		super.dispose();
        unRegisterEvents();
        actionResolver.unRegisterActionListener(gameOverDialog);
        eventLoop.clear();
        sfgGame.dispose();
		rayHandler.dispose();
        world.dispose();
        deInitLocator();
    }

	public Camera getCamera() {
		return gameCamera;
	}

	@Override
	public void pause() {
		switch (state) {
		case RUNNING:
            pauseGame();
			break;
		default: break;
		}
	}

    public SkeletonRenderer getSkeletonRenderer() {
        return skeletonRenderer;
    }

    @Override
    public void handleEvent(Event e) {
        switch (e.getType()){
            case EventType.SHOW_BOOST_TUT:
                initBoostTut((Vector2)e.getData());
                break;
            case EventType.PLAYER_ALIVE:
                setPlayerDead(!(Boolean) e.getData());
                break;
            case EventType.PLAYER_LIVES_CHANGED:
                setLives((Integer) e.getData());
                break;
            case EventType.PLAYER_SHIELDS_CHANGED:
                setShields((Integer) e.getData());
                break;
            case EventType.PLAYER_SCORE_CHANGED:
                setScore((Integer) e.getData());
                break;
            case EventType.PLAYER_BOOST_CHANGED:
                setBoostProgress((Float) e.getData());
                break;
            default: break;
        }
    }


    private void initBoostTut(Vector2 pos){
        if (boostTut != null){
            boostTut.init(pos.x-14, 16);
        }
    }

    private void setScore(int value){
        scoreLabel.setText(String.valueOf(value));
        positiveValueAction(scoreContainer, scoreLabel);
    }

    private void positiveValueAction(Actor scaleActor, Actor colorActor){
        centreOrigin(scaleActor);
        scaleActor.clearActions();
        scaleActor.addAction(
                Actions.sequence(
                        Actions.parallel(
                                Actions.scaleTo(1.33f, 1.33f, 0.3f)
                        ),
                        Actions.parallel(
                                Actions.scaleTo(1f, 1f, 0.3f)
                        )
        ));
        colorActor.addAction(
                Actions.sequence(
                        Actions.parallel(
                                Actions.color(toxicColor, 0.3f)
                        ),
                        Actions.parallel(
                                Actions.color(whiteColor, 0.3f)
                        )
                ));
    }

    private void negativeValueAction(Actor scaleActor, Actor colorActor){
        centreOrigin(scaleActor);
        scaleActor.clearActions();
        scaleActor.addAction(
                Actions.sequence(
                        Actions.parallel(
                                Actions.scaleTo(0.8f, 0.8f, 0.3f)
                        ),
                        Actions.parallel(
                                Actions.scaleTo(1f, 1f, 0.3f)
                        )
                ));
        colorActor.addAction(
                Actions.sequence(
                        Actions.parallel(
                                Actions.color(redColor, 0.3f)
                        ),
                        Actions.parallel(
                                Actions.color(whiteColor, 0.3f)
                        )
                ));
    }

    private void setLives(int lives) {
        livesLabel.setText(String.valueOf(lives));
        if (lives > lastLives){
            positiveValueAction(livesContainer, livesLabel);
        } else {
            negativeValueAction(livesContainer, livesLabel);
        }
        lastLives = lives;
    }

    private void setShields(int shields) {
        shieldsLabel.setText(String.valueOf(shields));
        if (shields > lastShields){
            positiveValueAction(shieldsContainer, shieldsLabel);
        } else {
            negativeValueAction(shieldsContainer, shieldsLabel);
        }
        lastShields = shields;
    }

    private void setPlayerDead(Boolean isDead){
        isPlayerDead = isDead;
    }

    private void setBoostProgress(float value){
        boostBar.setValue(dashDelay-value);
    }

    private void disableTuts() {
        if (jumpTut != null) {
            jumpTut.disable();
        }
        if (boostTut != null) {
            boostTut.disable();
        }
    }
}
