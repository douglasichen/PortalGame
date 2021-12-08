package com.mygdx.game;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.PolygonRegion;
import com.badlogic.gdx.graphics.g2d.PolygonSprite;
import com.badlogic.gdx.graphics.g2d.PolygonSpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;

import java.text.Bidi;
import java.util.ArrayList;
import java.util.Map;

public class Entity {
    protected Map<String,Boolean> tags;
    protected Body body;
    protected Vector2 gravity;

    private ShapeRenderer shapeRenderer = new ShapeRenderer();
    private PolygonSpriteBatch polygonSpriteBatch = new PolygonSpriteBatch();

    PolygonRegion polygonRegion;
    TextureRegion textureRegion;
    PolygonSprite polygonSprite;




    private Vector2 size;

    private Color color;



    public Entity(World world, Vector2 position, Vector2 size, BodyDef.BodyType bodyType, Color color, float density, float friction, boolean gravityEnabled) {
        // Initialize Variables
        this.gravity = world.getGravity();
        this.color = color;
        this.size = size;


        // Create a BodyDef and apply to Body Object
        BodyDef bodyDef = new BodyDef();
        bodyDef.position.x = position.x;
        bodyDef.position.y = position.y;
//        bodyDef.fixedRotation = true;
        bodyDef.type = bodyType;
        this.body = world.createBody(bodyDef);

        // Set the Polygon Box shape
        PolygonShape shape = new PolygonShape();
        shape.setAsBox(this.size.x/2f,this.size.y/2f);

        // Fixture is used as a collision mesh for the Physics Body Object
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = density;
        fixtureDef.friction = friction;
        this.body.createFixture(fixtureDef);

        // Free memory
        shape.dispose();

        // Creating an Floating Dynamic Object
        if (!gravityEnabled) this.body.setGravityScale(0f);
    }

    // Attempt to Gather the verticies that make up the FIRST Fixture of the Physics Body Object
    private float[] getVertices() {
        PolygonShape shape = (PolygonShape) this.body.getFixtureList().first().getShape();
        float[] vertices = new float[shape.getVertexCount()*2];
        for (int i=0; i<shape.getVertexCount(); i++) {
            Vector2 vertex = Vector2.Zero;
            shape.getVertex(i, vertex);
            vertices[i * 2] = vertex.x; vertices[i * 2 + 1] = vertex.y;
        }
        for (float f : vertices) System.out.print(f+ " ");
        System.out.println();
        Polygon polygon = new Polygon(vertices);
        polygon.setPosition(this.body.getPosition().x, this.body.getPosition().y);
        polygon.rotate(this.body.getAngle());
        return polygon.getTransformedVertices();
    }


    // Render: NEEDS WORK
    public void render(Camera camera) {
//        System.out.println(this.body.getPosition());
//        PolygonShape polygonShape = (PolygonShape) this.body.getFixtureList().first().getShape();


//        this.shapeRenderer.setProjectionMatrix(camera.combined);
//        this.shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
//        this.shapeRenderer.setColor(color);

//        float[] vertices = getVertices();
//        polygonRegion = new PolygonRegion(new TextureRegion(), vertices, new short[]{});
//        polygonSprite = new PolygonSprite(polyReg);
//        this.shapeRenderer.polygon(vertices);

//        this.shapeRenderer.rect(this.body.getPosition().x-size.x/2f, this.body.getPosition().y-size.y/2f, size.x, size.y);
//        this.shapeRenderer.end();

    }

    // Tag System
    public void addTag(String tag) {
        this.tags.put(tag, true);
    }

    // Adding a Force to the Physics Body Object
    public void applyForce(Vector2 direction, float magnitude) {
        Vector2 forceVector = new Vector2().mulAdd(direction, magnitude);
//        System.out.println("Apply Force: " + forceVector);
        this.body.applyForceToCenter(forceVector, true);
    }
    public void applyForce(Vector2 forceVector) {
        this.body.applyForceToCenter(forceVector, true);
    }





    // Accessor Methods
    public Body getBody() {
        return this.body;
    }
    public Vector2 getPosition() {
        return this.body.getPosition();
    }
//    public Vector2 getSize() {
//        return this.body.getFixtureList().first().getShape().;
//    }

    // Free up memory when Game is closed, MUST LOOK AT CAREFULLY!
    static void dispose() {
        // MUST DISPOSE ALL SHAPE RENDERERS
//        shapeRenderer.dispose();
        // MUST DISPOSE ALL ENTITIES;
    }

}
