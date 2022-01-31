package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;

import java.util.ArrayList;
import java.util.HashMap;

public class WeakEnemyEntity extends EnemyEntity {
    static public ArrayList<WeakEnemyEntity> weakEnemyEntities = new ArrayList<>();
    static private Vector2 regularSize = new Vector2(0.2f,0.35f);


    float closeEnoughCollisionRange = 0.02f;
    int wanderDirection = 1;
    ArrayList<RayHitInfo> raysHitInfo;
    RayHitInfo closestRayHitInfo;

    float maxRayDistance = 100;

    public WeakEnemyEntity(String name, Vector2 position, Vector2 size, BodyDef.BodyType bodyType, Color color, float density, float friction, boolean gravityEnabled, Sprite sprite) {
        super(name, position, size, bodyType, color, density, friction, gravityEnabled, sprite);
        animationTextureSizeScale = 3f;
        addAnimation("Walk", "Characters/imp_axe_demon/imp_axe_demon/redImpWalk.gif", 6, true, 0.5f);
        weakEnemyEntities.add(this);
    }
    static public void initialize(World world){
//        world.setContactListener(new WeakEnemyCollisionListener());
    }

    public static Vector2 getSize() {
        return regularSize;
    }

    private boolean hitWall() {
        raysHitInfo = new ArrayList<>();            // refresh the rays information list
        closestRayHitInfo = null;                   // reset the closest ray to nothing

        // shooting a ray is done by ray callbacks, read about rays on libgdx docs, learn about Vector2 normal, most likely dont need to know about fraction variable
        RayCastCallback callback = new RayCastCallback() {
            @Override
            public float reportRayFixture(Fixture fixture, Vector2 point, Vector2 normal, float fraction) {
                if (fixture == null || point == null || normal == null) return 0;
                // Multiple hits
                raysHitInfo.add(new RayHitInfo(fixture, new Vector2(point), new Vector2(normal), fraction));
                return 1;
            }
        };

        // look at the world.rayCast function on the libgdx docs and see what parameters you must provide
        int xDirection = (int)(this.body.getLinearVelocity().x/Math.abs(this.body.getLinearVelocity().x));
        world.rayCast(callback, this.body.getPosition(), new Vector2(maxRayDistance*xDirection, this.body.getPosition().y));

        // Finding the closest ray hit through a searching algorithm
        if (raysHitInfo != null) {
            if (raysHitInfo.size() == 0) return false;
            for (RayHitInfo rayHitInfo : raysHitInfo) {
                if (!rayHitInfo.fixture.isSensor()) if (closestRayHitInfo == null) closestRayHitInfo = rayHitInfo;
                if (closestRayHitInfo == null) continue;

                float distance1 = PMath.magnitude(PMath.subVector2(closestRayHitInfo.point, this.body.getPosition()));
                float distance2 = PMath.magnitude(PMath.subVector2(rayHitInfo.point, this.body.getPosition()));
                if (distance2 < distance1 && !rayHitInfo.fixture.isSensor()) {
                    closestRayHitInfo = rayHitInfo;
                }
            }
        }
        if (closestRayHitInfo == null) return false;
        float distanceFromWall = PMath.magnitude(PMath.subVector2(closestRayHitInfo.point, this.body.getPosition())) - this.size.x/2f;
        return distanceFromWall < closeEnoughCollisionRange;
    }


//    public void operate() {
//        if(hitWall()) {
//            wanderDirection *= -1;
//        }
//        this.body.setLinearVelocity(this.speed * wanderDirection, this.body.getLinearVelocity().y);
//
//        // animate
//        currentAnimation = "Walk";
//        horizontalFaceDirection = wanderDirection;
//    }

    static public void operate() {
        for (WeakEnemyEntity enemy : weakEnemyEntities) {
            if(enemy.hitWall()) {
                enemy.wanderDirection *= -1;
            }
            enemy.body.setLinearVelocity(enemy.speed * enemy.wanderDirection, enemy.body.getLinearVelocity().y);

            // animate
            enemy.currentAnimation = "Walk";
            enemy.horizontalFaceDirection = enemy.wanderDirection;
<<<<<<< Updated upstream
            enemy.updateReflection(((Player) Entity.entityFromName("Player")).portals);
=======
            enemy.updateReflection(Player.player.portals);

>>>>>>> Stashed changes
        }
    }
}