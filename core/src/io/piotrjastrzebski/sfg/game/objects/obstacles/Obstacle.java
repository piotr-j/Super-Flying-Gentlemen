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

package io.piotrjastrzebski.sfg.game.objects.obstacles;

import io.piotrjastrzebski.sfg.game.objects.Position;
import io.piotrjastrzebski.sfg.game.objects.ViewPortUpdate;
import io.piotrjastrzebski.sfg.game.objects.obstacles.Part.Type;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.esotericsoftware.spine.SkeletonRenderer;

public class Obstacle implements Poolable, Position, ViewPortUpdate {
	// width of the obstacle
	public static final float WIDTH = 3;
	private final PartBottom partBottom;
	private final PartTop partTop;
	private final ScoreSensor scoreSensor;
    private Type topType;
    private Type botType;
    private Type type;
    private float x;
    private float y;
    private float topY;

    private Vector2 pos;
    private float botY;
    private float viewHalfWidth;

    private float viewHalfHeight;
    private boolean init = false;

    public Obstacle() {
        pos = new Vector2();
		scoreSensor = new ScoreSensor(this);
		partTop = new PartTop(this);
		partBottom = new PartBottom(this);
	}

    public void initType() {
		switch (Type.values()[MathUtils.random(Type.values().length-1)]) {
		case SPIKE:
            if (MathUtils.randomBoolean()){
                initType(Type.SPIKE, Type.STATIC);
            } else {
                initType(Type.STATIC, Type.SPIKE);
            }
            type = Type.SPIKE;
			break;
		case HAMMER:
            initType(Type.HAMMER, Type.HAMMER);
            break;
        case MOVING:
            initType(Type.MOVING, Type.MOVING);
            break;
        case STATIC:
		default:
            initType(Type.STATIC, Type.STATIC);
            type = Type.STATIC;
            break;
		}
//        init(x, botY, topY);
//        this.botType = Type.MOVING;
//        this.topType = Type.MOVING;
//        type = Type.MOVING;
    }

    public void initType(Type botType, Type topType) {
        this.botType = botType;
        this.topType = topType;
        // other types are symmetrical
        if (botType == Type.SPIKE || topType == Type.SPIKE){
            type = Type.SPIKE;
        } else {
            type = botType;
        }
    }

    public void init(float x, float botY, float topY){
        this.x = x;
        this.y = (botY+topY)/2;
        this.topY = topY;
        this.botY = botY;
        init = true;
        pos.set(x, (topY + botY)/2);
        // offset from center of obstacle
        scoreSensor.init(x+4);
        partTop.init(x, topY, topType);
        partBottom.init(x, botY, botType);
    }

    public void disableScore(){
        scoreSensor.disable();
    }

	public void execute(boolean playerKilled){
		partTop.execute(playerKilled);
		partBottom.execute(playerKilled);
	}

	float camX;
	public void update(float delta, float camX){
        if (!init)
            return;
        this.camX = camX;
        // offsets as x is the center
        if (x > camX + viewHalfWidth +3 || x < camX- viewHalfWidth -4)
            return;
		partTop.update(delta);
		partBottom.update(delta);
	}
	
	public void draw(Batch batch, SkeletonRenderer skeletonRenderer){
        if (!init)
            return;
        // offsets as x is the center
        if (x > camX + viewHalfWidth +3 || x < camX- viewHalfWidth -4)
            return;
		partTop.draw(batch, skeletonRenderer);
		partBottom.draw(batch, skeletonRenderer);
	}

	@Override
	public void reset() {
        init = false;
		scoreSensor.init(-50);
		partTop.init(-50, 0, Type.STATIC);
		partBottom.init(-50, 0, Type.STATIC);
	}

    @Override
    public void updateViewPort(float width, float height){
        viewHalfWidth = width*0.5f;
        viewHalfHeight = height*0.5f;
    }

    public Type getType() {
        return type;
    }

    public Type getBotType() {
        return botType;
    }

    public Type getTopType() {
        return topType;
    }

    public float getTopY() {
        return topY;
    }


    public float getBotY() {
        return botY;
    }

    public float getY() {
        return y;
    }

    public float getX() {
        return x;
    }

    @Override
    public Vector2 getPos() {
        return pos;
    }
}
