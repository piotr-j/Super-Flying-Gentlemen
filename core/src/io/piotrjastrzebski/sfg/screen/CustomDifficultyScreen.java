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

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.ButtonGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.ArrayMap;

import io.piotrjastrzebski.sfg.game.objects.Background;
import io.piotrjastrzebski.sfg.screen.inputhandlers.CustomDifficultyInputHandler;
import io.piotrjastrzebski.sfg.utils.Assets;
import io.piotrjastrzebski.sfg.utils.ClampedRangeFloat;
import io.piotrjastrzebski.sfg.utils.ClampedRangeInt;
import io.piotrjastrzebski.sfg.utils.ClampedValueFloat;
import io.piotrjastrzebski.sfg.utils.ClampedValueInt;
import io.piotrjastrzebski.sfg.utils.Config;
import io.piotrjastrzebski.sfg.utils.ConfigData;
import io.piotrjastrzebski.sfg.utils.Locator;

public class CustomDifficultyScreen  extends DefaultScreen {
    private final Background background;

    public CustomDifficultyScreen() {
        super();
        Gdx.input.setInputProcessor(new CustomDifficultyInputHandler(this).getIM());
        Gdx.input.setCatchBackKey(true);
        background = new Background();
        initUI();
    }

    private void initUI() {
        Table root = new Table();
        root.setFillParent(true);
        final Label screenLabel = new Label(assets.getText(Assets.CUSTOM_DIFFICULTY), assets.getSkin(), "default-large");
        screenLabel.setAlignment(Align.center);
        root.add(screenLabel).pad(10).top().expandX();
        root.row();
        root.add(createTabs()).expand().fill();
        root.row();
        TextButton back = new TextButton(assets.getText(Assets.BACK), assets.getSkin(), "small");
        back.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                handleBack();
                playButtonPressSound();
            }
        });
        TextButton go = new TextButton(assets.getText(Assets.START_GAME), assets.getSkin(), "small");
        go.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                newGame(currentTab.getDifficulty());
                playButtonPressSound();
            }
        });
        final Table bottomContainer = new Table();
        bottomContainer.add(back);
        bottomContainer.add().expandX();
        bottomContainer.add(go);
        root.add(bottomContainer).pad(20).bottom().expandX().fillX();
        stage.addActor(root);
    }

    private Stack stack;
    private ButtonGroup buttonGroup;
    private Table buttons;
    private ArrayMap<String, Tab> tabs;
    private Tab currentTab;
    private final static String TAB_1 = "1";
    private final static String TAB_2 = "2";
    private final static String TAB_3 = "3";

    private Table createTabs() {
        buttons = new Table();
        buttonGroup = new ButtonGroup();
        stack = new Stack();
        tabs = new ArrayMap<String, Tab>();

        addTab(TAB_1, Config.Difficulty.CUSTOM_1);
        addTab(TAB_2, Config.Difficulty.CUSTOM_2);
        addTab(TAB_3, Config.Difficulty.CUSTOM_3);
        showTab(TAB_1);

        final Table table = new Table();
        table.add(buttons).pad(0, 10, 10, 10);
        table.row();
        final ScrollPane scrollPane = new ScrollPane(stack);
        // so sliders sort of work
        scrollPane.setCancelTouchFocus(false);
        scrollPane.setScrollingDisabled(true, false);
        table.add(scrollPane).expand().fill();
//        table.debug();
        return table;
    }

    private void addTab(final String name, Config.Difficulty difficulty){
        final TextButton buttonTab = new TextButton(name, assets.getSkin(), "toggle-small");
        final Tab tab = new Tab(difficulty);
        buttonTab.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                showTab(name);
                currentTab = tab;
            }
        });
        buttonGroup.add(buttonTab);
        buttons.add(buttonTab).pad(10);
        stack.add(tab);
        tabs.put(name, tab);
    }

    private void showTab(String tab) {
        if (currentTab!=null)
            currentTab.setVisible(false);
        currentTab = tabs.get(tab);
        currentTab.setVisible(true);
    }

    private void newGame(Config.Difficulty difficulty) {
        Locator.getConfig().setDifficulty(difficulty);
        Locator.getConfig().saveCustomConfigs();
        Locator.getApp().setScreen(new GameScreen());
    }

    @Override
    public void draw() {
        super.draw();
        background.draw(batch);
        stage.draw();
        Table.drawDebug(stage);
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

    public void handleBack() {
        Locator.getApp().setScreen(new DifficultySelectScreen());
    }

    public void handleEnter() {
        newGame(Config.Difficulty.CUSTOM_1);
    }

    InputListener stopTouchDown = new InputListener() {
        public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
            event.stop();
            return false;
        }
    };

    public Table createIntValueContainer(Skin skin, String text, final ClampedValueInt value){
        final Table topContainer = new Table();
        final Table container = new Table();
        final Label nameLabel = new Label(assets.getText(text), skin);
        final Label valueLabel  = new Label("0", assets.getSkin());

        final Slider slider = new Slider(value.min(), value.max(), value.step(), false, skin);
        slider.setValue(value.value());
        valueLabel.setText(String.valueOf((int)slider.getValue()));

        slider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                final int val = (int)slider.getValue();
                value.set(val);
                valueLabel.setText(String.valueOf(val));
            }
        });
        slider.addListener(stopTouchDown);

        container.add(nameLabel);
        container.add().expandX();
        container.add(valueLabel);
        topContainer.add(container).expandX().fillX().pad(10);
        topContainer.row();
        topContainer.add(slider).expandX().fillX().pad(10);
        return topContainer;
    }



    public Table createFloatValueContainer(Skin skin, String text, final ClampedValueFloat value){
        final Table topContainer = new Table();
        final Table container = new Table();
        final Label nameLabel = new Label(assets.getText(text), skin);
        final Label valueLabel  = new Label("0", assets.getSkin());
        final Slider slider = new Slider(value.min(), value.max(), value.step(), false, skin);
        slider.setValue(value.value());
        valueLabel.setText(String.format("%.2f", slider.getValue()));

        slider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                value.set(slider.getValue());
                valueLabel.setText(String.format("%.2f", slider.getValue()));
            }
        });
        slider.addListener(stopTouchDown);

        container.add(nameLabel);
        container.add().expandX();
        container.add(valueLabel);
        topContainer.add(container).expandX().fillX().pad(10);
        topContainer.row();
        topContainer.add(slider).expandX().fillX().pad(10);
        return topContainer;
    }


    private  Table createIntRangeContainer(final ClampedRangeInt range, Skin skin,
                                           String labelMin, String labelMax){
        final Table container = new Table();

        final Table topContainer = new Table();
        final Label minLabel = new Label(assets.getText(labelMin), skin);
        final Label valueMinLabel  = new Label("0", assets.getSkin());
        final Slider minSlider = new Slider(range.min(), range.max(), range.step(), false, skin);
        minSlider.setValue(range.low());

        final Slider maxSlider = new Slider(range.min(), range.max(), range.step(), false, skin);
        maxSlider.setValue(range.high());

        valueMinLabel.setText(String.valueOf(range.low()));

        minSlider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                range.low((int)minSlider.getValue());
                valueMinLabel.setText(String.valueOf((int)minSlider.getValue()));
                if (maxSlider.getValue() < range.low()){
                    maxSlider.setValue(range.low());
                }
            }
        });
        minSlider.addListener(stopTouchDown);

        topContainer.add(minLabel);
        topContainer.add().expandX();
        topContainer.add(valueMinLabel);
        container.add(topContainer).expandX().fillX().pad(10);
        container.row();
        container.add(minSlider).expandX().fillX().pad(10);
        container.row();

        final Table botContainer = new Table();
        final Label maxLabel = new Label(assets.getText(labelMax), skin);
        final Label valueMaxLabel  = new Label("0", assets.getSkin());

        valueMaxLabel.setText(String.valueOf(range.high()));

        maxSlider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                range.high((int)maxSlider.getValue());
                valueMaxLabel.setText(String.valueOf((int)maxSlider.getValue()));
                if (minSlider.getValue() > range.high()){
                    minSlider.setValue(range.high());
                }
            }
        });
        maxSlider.addListener(stopTouchDown);

        botContainer.add(maxLabel);
        botContainer.add().expandX();
        botContainer.add(valueMaxLabel);
        container.add(botContainer).expandX().fillX().pad(10);
        container.row();
        container.add(maxSlider).expandX().fillX().pad(10);

        return container;
    }

    private Table createFloatRangeContainer(final ClampedRangeFloat range, Skin skin,
                                           String labelMin, String labelMax){
        final Table container = new Table();

        final Table topContainer = new Table();
        final Label minLabel = new Label(assets.getText(labelMin), skin);
        final Label valueMinLabel  = new Label("0", assets.getSkin());
        final Slider minSlider = new Slider(range.min(), range.max(), range.step(), false, skin);
        minSlider.setValue(range.low());

        final Slider maxSlider = new Slider(range.min(), range.max(), range.step(), false, skin);
        maxSlider.setValue(range.high());

        valueMinLabel.setText(String.format("%.2f", range.low()));

        minSlider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                range.low(minSlider.getValue());
                valueMinLabel.setText(String.format("%.2f", minSlider.getValue()));
                if (maxSlider.getValue() < range.low()){
                    maxSlider.setValue(range.low());
                }
            }
        });
        minSlider.addListener(stopTouchDown);

        topContainer.add(minLabel);
        topContainer.add().expandX();
        topContainer.add(valueMinLabel);
        container.add(topContainer).expandX().fillX().pad(10);
        container.row();
        container.add(minSlider).expandX().fillX().pad(10);
        container.row();

        final Table botContainer = new Table();
        final Label maxLabel = new Label(assets.getText(labelMax), skin);
        final Label valueMaxLabel  = new Label("0", assets.getSkin());
        valueMaxLabel.setText(String.format("%.2f", range.high()));

        maxSlider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                range.high(maxSlider.getValue());
                valueMaxLabel.setText(String.format("%.2f", maxSlider.getValue()));
                if (minSlider.getValue() > range.high()){
                    minSlider.setValue(range.high());
                }
            }
        });
        maxSlider.addListener(stopTouchDown);

        botContainer.add(maxLabel);
        botContainer.add().expandX();
        botContainer.add(valueMaxLabel);
        container.add(botContainer).expandX().fillX().pad(10);
        container.row();
        container.add(maxSlider).expandX().fillX().pad(10);

        return container;
    }

    private class Tab extends Table {
        private ConfigData configData;
        private Skin skin;
        private Config config;
        private ButtonGroup group;
        private Table buttons;

        public Tab(Config.Difficulty difficulty){
            super();
            config = Locator.getConfig();
            configData = config.getConfigData(difficulty);
            skin = assets.getSkin();
            initUI();
        }

        private void initUI(){
            setVisible(false);
            createSelectBase();
            initSliders();
        }

        private void reInitSliders(){
            // suboptimal since we create all the things from scratch
            clearChildren();
            add(buttons).expandX().fillX().pad(20);
            row();
            initSliders();
        }

        private void initSliders(){
            createPickupSettings();
            createObstacleSettings();
            createPlayerSettings();
            createWorldSettings();
        }

        private TextButton createTB(String text, final Config.Difficulty difficulty){
            final TextButton button = new TextButton(assets.getText(text), skin, "toggle-small");
            button.addListener(new ClickListener(){
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    final Dialog dialog = new Dialog("", skin){
                        @Override
                        protected void result(Object object) {
                            if (object.equals("YES")){
                                config.setCustomBase(configData.getDifficulty(), difficulty);
                                reInitSliders();
                            }
                            setCheckedButton();
                        }
                    };
                    dialog.getButtonTable().defaults().pad(20);
                    dialog.getContentTable().defaults().pad(20);
                    dialog.text(assets.getText(Assets.RESET_DIFFICULTY));
                    final TextButton.TextButtonStyle style = skin.get("small", TextButton.TextButtonStyle.class);
                    dialog.button(assets.getText(Assets.OK), "YES", style);
                    dialog.button(assets.getText(Assets.CANCEL), "NO", style);
                    dialog.show(stage);
                }
            });
            if (configData.getBaseDifficulty().equals(difficulty)){
                button.setChecked(true);
            }
            group.add(button);
            return button;
        }

        private void setCheckedButton(){
            switch (configData.getBaseDifficulty()){
                case BRUTAL:
                    group.setChecked(assets.getText(Assets.DIFFICULTY_BRUTAL));
                    break;
                case VERY_HARD:
                    group.setChecked(assets.getText(Assets.DIFFICULTY_VERY_HARD));
                    break;
                case HARD:
                    group.setChecked(assets.getText(Assets.DIFFICULTY_HARD));
                    break;
            }
        }

        private void createSelectBase(){
            buttons = new Table();
            final Label sectionLabel = new Label(assets.getText(Assets.CUSTOM_BASE_SELECT), skin);
            buttons.add(sectionLabel).colspan(3).pad(0, 10, 10, 10);
            buttons.row();
            group = new ButtonGroup();

            buttons.add(createTB(Assets.DIFFICULTY_BRUTAL, Config.Difficulty.BRUTAL)).pad(10);
            buttons.add(createTB(Assets.DIFFICULTY_VERY_HARD, Config.Difficulty.VERY_HARD)).pad(10);
            buttons.add(createTB(Assets.DIFFICULTY_HARD, Config.Difficulty.HARD)).pad(10);

            add(buttons).expandX().fillX().pad(20);
            row();
        }

        private void createPickupSettings(){
            final Table container = new Table();
            final Label sectionLabel = new Label(assets.getText(Assets.PICKUP_SETTINGS), skin);
            container.add(sectionLabel);
            container.row();

            final Table pickupSpawnDist = createIntValueContainer(skin,
                    Assets.PICKUP_SPAWN_DISTANCE,
                    configData.getPickupMinSpawnDistance());
            container.add(pickupSpawnDist).expandX().fillX();
            container.row();

            final Table pickupSpawnChance = createFloatValueContainer(skin,
                    Assets.PICKUP_SPAWN_CHANCE,
                    configData.getPickupSpawnChance());
            container.add(pickupSpawnChance).expandX().fillX();
            container.row();

            container.add(createIntRangeContainer(
                    configData.getPickupLives(), skin,
                    Assets.PICKUP_LIVES_MIN, Assets.PICKUP_LIVES_MAX
            )).expandX().fillX();
            container.row();

            container.add(createIntRangeContainer(
                    configData.getPickupShield(), skin,
                    Assets.PICKUP_SHIELDS_MIN, Assets.PICKUP_SHIELDS_MAX
            )).expandX().fillX();
            container.row();

            container.add(createIntRangeContainer(
                    configData.getPickupBoost(), skin,
                    Assets.PICKUP_BOOST_MIN, Assets.PICKUP_BOOST_MAX
            )).expandX().fillX();
            container.row();

            container.add(createIntRangeContainer(
                    configData.getPickupToxic(), skin,
                    Assets.PICKUP_TOXIC_MIN, Assets.PICKUP_TOXIC_MAX
            )).expandX().fillX();
            container.row();

            add(container).expandX().fillX().pad(20);
            row();
        }

        private void createObstacleSettings(){
            final Table container = new Table();
            final Label sectionLabel = new Label(assets.getText(Assets.OBSTACLE_SETTINGS), skin);
            container.add(sectionLabel);
            container.row();
            container.add(createFloatRangeContainer(
                    configData.getObstacleDistance(), skin,
                    Assets.OBSTACLE_DISTANCE_MIN, Assets.OBSTACLE_DISTANCE_MAX
            )).expandX().fillX();
            container.row();

            container.add(createFloatRangeContainer(
                    configData.getObstacleGapSize(), skin,
                    Assets.OBSTACLE_GAP_MIN, Assets.OBSTACLE_GAP_MAX
            )).expandX().fillX();
            container.row();

            add(container).expandX().fillX().pad(20);
            row();
        }

        private void createPlayerSettings(){
            final Table container = new Table();
            final Label sectionLabel = new Label(assets.getText(Assets.PLAYER_SETTINGS), skin);
            container.add(sectionLabel);
            container.row();

            final Table initLives = createIntValueContainer(skin,
                    Assets.PLAYER_INIT_LIVES,
                    configData.getPlayerInitLives());
            container.add(initLives).expandX().fillX();
            container.row();

            final Table initShields = createIntValueContainer(skin,
                    Assets.PLAYER_INIT_SHIELDS,
                    configData.getPlayerInitShields());
            container.add(initShields).expandX().fillX();
            container.row();

            final Table centreOffset = createFloatValueContainer(skin,
                    Assets.PLAYER_CENTRE_OFFSET,
                    configData.getPlayerCentreOffset());
            container.add(centreOffset).expandX().fillX();
            container.row();

            final Table scale = createFloatValueContainer(skin,
                    Assets.PLAYER_SCALE,
                    configData.getPlayerScale());
            container.add(scale).expandX().fillX();
            container.row();

            final Label warningLabel = new Label(assets.getText(Assets.CUSTOM_WARNING), skin);
            container.add(warningLabel).pad(20);
            container.row();

            final Table jumpImpulse = createFloatValueContainer(skin,
                    Assets.PLAYER_JUMP_IMPULSE,
                    configData.getPlayerJumpImpulse());
            container.add(jumpImpulse).expandX().fillX();
            container.row();

            final Table flySpeed = createFloatValueContainer(skin,
                    Assets.PLAYER_FLY_SPEED,
                    configData.getPlayerFlySpeed());
            container.add(flySpeed).expandX().fillX();
            container.row();

            final Table flyMaxSpeed = createFloatValueContainer(skin,
                    Assets.PLAYER_FLY_MAX_SPEED,
                    configData.getPlayerFlyMaxSpeed());
            container.add(flyMaxSpeed).expandX().fillX();
            container.row();

            final Table flyImpulse = createFloatValueContainer(skin,
                    Assets.PLAYER_FLY_IMPULSE,
                    configData.getPlayerFlyImpulse());
            container.add(flyImpulse).expandX().fillX();
            container.row();

            final Table dashImpulse = createFloatValueContainer(skin,
                    Assets.PLAYER_DASH_IMPULSE,
                    configData.getPlayerDashImpulse());
            container.add(dashImpulse).expandX().fillX();
            container.row();

            final Table dashDelay = createFloatValueContainer(skin,
                    Assets.PLAYER_DASH_DELAY,
                    configData.getPlayerDashDelay());
            container.add(dashDelay).expandX().fillX();
            container.row();

            final Table dashTime = createFloatValueContainer(skin,
                    Assets.PLAYER_DASH_TIME,
                    configData.getPlayerDashTime());
            container.add(dashTime).expandX().fillX();
            container.row();


            add(container).expandX().fillX().pad(20);
            row();
        }

        private void createWorldSettings(){
            final Table container = new Table();
            final Label sectionLabel = new Label(assets.getText(Assets.WORLD_SETTINGS), skin);
            container.add(sectionLabel);
            container.row();

            final Table gravity = createFloatValueContainer(skin,
                    Assets.GRAVITY,
                    configData.getGravity());
            container.add(gravity).expandX().fillX();
            container.row();

            add(container).expandX().fillX().pad(20);
            row();
        }

        public Config.Difficulty getDifficulty() {
            return configData.getDifficulty();
        }
    }
}