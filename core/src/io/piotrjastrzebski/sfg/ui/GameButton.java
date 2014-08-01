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

package io.piotrjastrzebski.sfg.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.Align;

public class GameButton extends Button {
    private Image image;
    private Label label;
    private TextButton.TextButtonStyle style;

    public GameButton(String text, TextureRegion image, Skin skin){
        this(text, image, skin, "default");
        style = skin.get(TextButton.TextButtonStyle.class);
    }

    public GameButton(String text, TextureRegion region, Skin skin, String style){
        super(skin, style);
        debug();
        image = new Image(region);
        label = new Label(text, skin);
        label.setAlignment(Align.center);
        add(image);
        add(label).expand();
    }

    public Label getLabel() {
        return label;
    }

    public Image getImage() {
        return image;
    }

    @Override
    public void draw (Batch batch, float parentAlpha) {
        Color fontColor;

        if (isPressed() && style.downFontColor != null)
            fontColor = style.downFontColor;
         else
            fontColor = style.fontColor;
        if (fontColor != null) label.getStyle().fontColor = fontColor;
        super.draw(batch, parentAlpha);
    }

}
