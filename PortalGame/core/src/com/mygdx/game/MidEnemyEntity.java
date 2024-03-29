package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.RayCastCallback;
import com.badlogic.gdx.physics.box2d.World;

import java.util.ArrayList;

public class MidEnemyEntity extends EnemyEntity{
    static public ArrayList<MidEnemyEntity> midEnemyEntities = new ArrayList<>();
    public static Vector2 regularSize = new Vector2(0.18f,0.4f);

    float closeEnoughCollisionRange = 0.02f;
    int wanderDirection = 1;
    float initialSpeed = 0.3f;
    float doubleSpeed = 1.2f;
    ArrayList<RayHitInfo> raysHitInfo;
    RayHitInfo closestRayHitInfo;

    float maxRayDistance = 100;



    public MidEnemyEntity(String name, Vector2 position, Vector2 size, BodyDef.BodyType bodyType, Color color, float density, float friction, boolean gravityEnabled, Sprite sprite) {
        super(name, position, size, bodyType, color, density, friction, gravityEnabled, sprite);
        this.speed = initialSpeed;
        animationTextureSizeScale = 3f;
        addAnimation("Walk", "Characters/imp_axe_demon/imp_axe_demon/demon_axe_red/ezgif.com-gif-maker.gif", 6, true, 0.16f);
        addAnimation("Run", "Characters/imp_axe_demon/imp_axe_demon/demon_axe_red/axe_demon_run.gif", 6, true, 0.3f);
        addAnimation("Death", "Characters/imp_axe_demon/imp_axe_demon/demon_axe_red/dead/axeguyded.gif", 0, false, 0.3f);

        currentAnimation = "Walk";

        midEnemyEntities.add(this);
        sounds.put("MidEnemyGrowl", Gdx.audio.newSound(Gdx.files.internal("Characters/imp_axe_demon/imp_axe_demon/demon_axe_red/sounds/enemy2growl.mp3")));

        // idle sounds
        idleSounds = new String[] {"MidEnemyGrowl"};

    }

    public static Vector2 getRegularSize() {
        return regularSize;
    }

    private boolean seePlayer() {
        int xDirection = getBody().getLinearVelocity().x == 0 ? 1
                : (int)(this.body.getLinearVelocity().x/Math.abs(this.body.getLinearVelocity().x));
        RayHitInfo sightRay = PMath.getClosestRayHitInfo(world, getPosition(), new Vector2(xDirection*100,0), maxRayDistance, true);
        if (sightRay == null) return false;
        Entity entity = Entity.entityFromBody(sightRay.fixture.getBody());
        String sight = entity.getName();

        return sight.equals("Player");

    }
    public static void operate() {
        // delete dead dudes
        for (int i=midEnemyEntities.size()-1; i>=0; i--) {
            MidEnemyEntity enemy = midEnemyEntities.get(i);
            if (!enemy.alive) {
                enemy.die();
                if (enemy.alpha == 0) midEnemyEntities.remove(i);
            }
        }


        // operate
        for (MidEnemyEntity enemy : midEnemyEntities) {
            if (enemy.getBody() == null) continue;
            if (!enemy.alive) {
                continue;
            }

//            System.out.println(enemy.getBody().getLinearVelocity());
            if (enemy.hitWall()) {
                enemy.wanderDirection *= -1;
                enemy.speed = enemy.initialSpeed;
                enemy.currentAnimation = "Walk";
            }
            if (enemy.seePlayer()) {
                enemy.speed = enemy.doubleSpeed;
                enemy.currentAnimation = "Run";
            }
            enemy.body.setLinearVelocity(enemy.speed * enemy.wanderDirection, enemy.body.getLinearVelocity().y);
            enemy.horizontalFaceDirection = enemy.wanderDirection;

            // reflection
            enemy.updateReflection(Player.player.portals);


//            System.out.println(enemy.getBody().getLinearVelocity());
            // idle sounds
            enemy.playRandomIdleSound();
        }
    }
}
