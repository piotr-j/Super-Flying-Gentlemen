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
import io.piotrjastrzebski.sfg.game.objects.obstacles.Part.Type;
import io.piotrjastrzebski.sfg.screen.GameScreen;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.esotericsoftware.spine.SkeletonRenderer;

public class Obstacle implements Poolable, Position {
	// width of the obstacle
	public static final float WIDTH = 3;
	private final PartBottom bottomObstacle;
	private final PartTop topObstacle;
	private final ScoreSensor scoreSensor;
    private Type topType;
    private Type botType;
    private Type type;
    private float x;
    private float topY;
    private Vector2 pos;

    private float botY;
    private float viewHalfWidth;
    private float viewHalfHeight;

    private boolean init = false;

	public Obstacle(SpikePool spikePool, HammerPool hammerPool) {
        pos = new Vector2();
		scoreSensor = new ScoreSensor(this);
		topObstacle = new PartTop(this, spikePool, hammerPool);
		bottomObstacle = new PartBottom(this, spikePool, hammerPool);
	}

	public void init(float x, float botY, float topY){
		switch (MathUtils.random(3)) {
		case 0:
            if (MathUtils.randomBoolean()){
                init(x, botY, topY, Type.SPIKE, Type.STATIC);
            } else {
                init(x, botY, topY, Type.STATIC, Type.SPIKE);
            }
            type = Type.SPIKE;
			break;
		case 1:
            init(x, botY, topY, Type.HAMMER, Type.HAMMER);
            type = Type.SPIKE;
            break;
		default:
            init(x, botY, topY, Type.STATIC, Type.STATIC);
            type = Type.STATIC;
            break;
		}
	}

    public void init(float x, float botY, float topY, Type botType, Type topType){
        this.x = x;
        this.topY = topY;
        this.botY = botY;
        this.botType = botType;
        this.topType = topType;
        init = true;
        pos.set(x, (topY + botY)/2);
        // offset from center of obstacle
        scoreSensor.init(x+4);

        // 28 - full obstacle height
        topObstacle.init(x, topY, topType);
        bottomObstacle.init(x, botY, botType);
    }

    public void disableScore(){
        scoreSensor.disable();
    }

	public void execute(boolean playerKilled){
		topObstacle.execute(playerKilled);
		bottomObstacle.execute(playerKilled);
	}

	float camX;
	public void update(float delta, float camX){
        if (!init)
            return;
        this.camX = camX;
        // offsets as x is the center
        if (x > camX + viewHalfWidth +3 || x < camX- viewHalfWidth -4)
            return;
		topObstacle.update(delta);
		bottomObstacle.update(delta);
	}
	
	public void draw(Batch batch, SkeletonRenderer skeletonRenderer){
        if (!init)
            return;
        // offsets as x is the center
        if (x > camX + viewHalfWidth +3 || x < camX- viewHalfWidth -4)
            return;
		topObstacle.draw(batch, skeletonRenderer);
		bottomObstacle.draw(batch, skeletonRenderer);
	}
	
	public void destroy(){
		topObstacle.destroy();
		bottomObstacle.destroy();
		scoreSensor.destroy();
	}

	@Override
	public void reset() {
        init = false;
		scoreSensor.init(-50);
		topObstacle.init(-50, 0, Type.STATIC);
		bottomObstacle.init(-50, 0, Type.STATIC);
	}

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

    public float getX() {
        return x;
    }

    @Override
    public Vector2 getPos() {
        return pos;
    }
}
