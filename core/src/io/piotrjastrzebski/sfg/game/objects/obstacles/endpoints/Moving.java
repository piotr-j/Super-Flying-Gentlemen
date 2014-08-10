package io.piotrjastrzebski.sfg.game.objects.obstacles.endpoints;

import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.esotericsoftware.spine.AnimationState;
import com.esotericsoftware.spine.Skeleton;
import com.esotericsoftware.spine.SkeletonData;

import io.piotrjastrzebski.sfg.utils.Assets;
import io.piotrjastrzebski.sfg.utils.Locator;

public class Moving extends EndPoint {
    private final static String SLOT_NAME = "obst_mov_top";

    public Moving(){
        super();
        final PolygonShape boxShape = new PolygonShape();
        final BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.KinematicBody;
        bodyDef.position.set(0, 0);
        body = world.createBody(bodyDef);
        boxShape.setAsBox(1.5f, 6f);
        body.createFixture(boxShape, 0);
        boxShape.dispose();

        // setup animation
        final Assets assets = Locator.getAssets();
        final SkeletonData skeletonData = assets.getSkeletonData(Assets.Animations.OBST_MOVING);
        skeleton = new Skeleton(skeletonData);
        animation = skeletonData.findAnimation("animation");
        animationState = new AnimationState(assets.getAnimationStateData(Assets.Animations.OBST_MOVING));

        bone = getBone(skeleton, SLOT_NAME);
    }

    public void init(float x, float y, boolean up){
        this.y = y;
        this.x = x;
        isExecuting = true;
        skeleton.setBonesToSetupPose();
        // hammer is facing up
        skeleton.setX(x - END_POINT_HALF_WIDTH);
        skeleton.setFlipY(!up);
        animationState.setAnimation(0, animation, true);
        float skeletonYOffset;
        if (up){
            boneOffset =  -8f;
            skeletonYOffset = -2.5f;
        } else {
            boneOffset = 8f;
            skeletonYOffset = 2.5f;
            // fast forward the anim so top and bottom are offset
            animationState.update(animation.getDuration()/2);
        }

        skeleton.setY(y + skeletonYOffset);
        animationState.apply(skeleton);
        skeleton.updateWorldTransform();
        body.setTransform(x, y+bone.getWorldY()+boneOffset, 0);
    }

    public void update(float delta){
        super.update(delta);
        // not ideal, better to use impulses, but it will do
        body.setTransform(x, y+bone.getWorldY()+boneOffset, 0);
    }
}
