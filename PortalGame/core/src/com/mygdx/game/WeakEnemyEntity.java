package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Array;

import java.util.ArrayList;
import java.util.HashMap;

public class WeakEnemyEntity extends EnemyEntity {
    static public ArrayList<WeakEnemyEntity> weakEnemyEntities = new ArrayList<>();
    static private Vector2 regularSize = new Vector2(0.2f,0.35f);
    int wanderDirection = 1;

    public WeakEnemyEntity(String name, Vector2 position, Vector2 size, BodyDef.BodyType bodyType, Color color, float density, float friction, boolean gravityEnabled, Sprite sprite) {
        super(name, position, size, bodyType, color, density, friction, gravityEnabled, sprite);
        animationTextureSizeScale = 3f;
        addAnimation("Walk", "Characters/imp_axe_demon/imp_axe_demon/redImpWalk.gif", 6, true, 0.25f);
        addAnimation("Death", "Characters/imp_axe_demon/imp_axe_demon/imp_red/impded.gif", 0, false, 0.3f);

        weakEnemyEntities.add(this);
        //sounds
        sounds.put("EnemyGrowl", Gdx.audio.newSound(Gdx.files.internal("Characters/imp_axe_demon/imp_axe_demon/imp_red/sounds/enemygrowl.mp3")));

        // idle sounds
        idleSounds = new String[] {"EnemyGrowl"};
    }

    public static Vector2 getRegularSize() {
        return regularSize;
    }

    static public void operate() {
        // delete dead dudes
        for (int i=weakEnemyEntities.size()-1; i>=0; i--) {
            WeakEnemyEntity enemy = weakEnemyEntities.get(i);
            if (!enemy.alive) {
                enemy.die();
                if (enemy.alpha == 0) weakEnemyEntities.remove(i);
            }
        }


        for (WeakEnemyEntity enemy : weakEnemyEntities) {
            if (enemy.getBody() == null) continue;
            if (!enemy.alive) {
                continue;
            }

            if(enemy.hitWall()) enemy.wanderDirection *= -1;
            enemy.body.setLinearVelocity(enemy.speed * enemy.wanderDirection, enemy.body.getLinearVelocity().y);

            // animate
            enemy.currentAnimation = "Walk";
            enemy.horizontalFaceDirection = enemy.wanderDirection;
            enemy.updateReflection(Player.player.portals);

            // idle sounds
            enemy.playRandomIdleSound();
        }
    }
}