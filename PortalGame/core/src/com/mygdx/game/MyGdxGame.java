package com.mygdx.game;

import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g3d.utils.shapebuilders.BoxShapeBuilder;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ExtendViewport;

import java.util.ArrayList;

public class MyGdxGame extends ApplicationAdapter {
	//

	// Window size is initialized at DesktopLauncher Class
	static final float STEP_TIME = 1f / 60f;
	static final int VELOCITY_ITERATIONS = 6;
	static final int POSITION_ITERATIONS = 2;
	static final float GAME_SCALE = 1.0f/4.0f/4.0f/4.0f/4.0f;

	protected static final float SCENE_WIDTH = 1920f;
	protected static final float SCENE_HEIGHT = 1080f;

	// Maps
	GameMap map;

	// Physics World
	private World world;
	Vector2 gravity = new Vector2(0f,-6);

	// Camera
	OrthographicCamera camera;

	// Objects in the physics world
	Player player;
	WeakEnemyEntity enemy;
	Entity floor;
	ArrayList<Entity> boxes;
	ArrayList<Entity> walls;

	// Rendered variables for the entities
	static Renderer entityRenderer;
	Texture img;
	Texture squareTexture;
	Sprite squareSprite;

	// Rendering Debug Objects
	Box2DDebugRenderer b2dr;

	// lasers are not used rn
	ArrayList<Laser> lasers;
	float angle = 0f;

	@Override
	public void create () {

		// Initialize Debug Renderer for making debug lines and debug shapes for the physics objects
		b2dr = new Box2DDebugRenderer();
//		img = new Texture("badlogic.jpg");
//		textureAtlas = new TextureAtlas();

		// Sprite Render Initialization
		squareTexture = new Texture("shapes/square.jpeg");
		squareSprite = new Sprite(squareTexture);

		// Initialize Sprite Renderer Variables
		entityRenderer = new Renderer(new SpriteBatch());

		// Initialize Camera
		camera = new OrthographicCamera(scale(SCENE_WIDTH), scale(SCENE_HEIGHT));
		camera.translate(camera.viewportWidth/2f, camera.viewportHeight/2f);
		camera.update();



		// Initialize Physics World
		world = new World(gravity, false);
//		System.out.println(world);

		//Maps
		map = new GameMap(world,"DarkMap1/tiledAssets/DarkMap(starterlevel).tmx", this.camera, entityRenderer);

		// Initialize Objects in Physics World
		player = new Player(world, camera, "Player", new Vector2(4f, 3f), new Vector2(0.25f,0.25f),
				BodyDef.BodyType.DynamicBody, new Color(1,0,0,1),
				10f, 0.0f, true, squareSprite);
		entityRenderer.addToRenderLayer(1, player);
		//Create Enemy

		enemy = new WeakEnemyEntity(world, "Enemy1", new Vector2(4f,1f), new Vector2(0.2f,0.2f), BodyDef.BodyType.DynamicBody, new Color(1,0,0,1), 10f, 1f, true, squareSprite);


		walls = new ArrayList<>();
		boxes = new ArrayList<>();

		// Add Boxes To Physics World
		for (int i=0; i<0; i++) {
			addBox(new Vector2(2f ,2f), new Vector2(0.1f, 0.1f));
			addBox(new Vector2(1.4f ,3f), new Vector2(0.2f, 0.2f));
		}

		// Add walls and floor
//		addWall(new Vector2(camera.viewportWidth/2f,0.15f), new Vector2(camera.viewportWidth,0.3f));
//		addWall(new Vector2(0.15f,camera.viewportHeight/2f), new Vector2(0.3f,camera.viewportHeight));
//		addWall(new Vector2(camera.viewportWidth-0.15f,camera.viewportHeight/2f), new Vector2(0.3f,camera.viewportHeight));

		Laser.setProjectionMatrix(camera.combined);
		lasers = new ArrayList<>();
//		lasers.add(new Laser(world, new Vector2(1,1), new Color(1,0,0,1), 0f, 0.01f, 10));



//		box = new Entity(world, new Vector2(1.5f ,1.5f), new Vector2(0.2f,0.2f), BodyDef.BodyType.DynamicBody, new Color(1,0,0,1), 1f);
	}

	// a function for adding a box to the physics world by supplying the entity constructor with proper values
	private void addBox(Vector2 position, Vector2 size) {
		Entity newBox = new Entity(world, "Box", position, size, BodyDef.BodyType.DynamicBody, new Color(0,1,0,1), 10f, 1f, true, squareSprite);
		entityRenderer.addToRenderLayer(1, newBox);
		boxes.add(newBox);
	}

	private void addWall(Vector2 position, Vector2 size) {
		Entity newWall = new Entity(world, "Wall", position, size, BodyDef.BodyType.StaticBody, new Color(0,0,0,1), 0.1f, 0.1f, false, squareSprite);
		walls.add(newWall);
	}

//	private void addSprites() {
//		Array<TextureAtlas.AtlasRegion> regions = textureAtlas.getRegions();
//
//		for (TextureAtlas.AtlasRegion region : regions) {
//			Sprite sprite = textureAtlas.createSprite(region.name);
//
//			float width = scale(sprite.getWidth());
//			float height = scale(sprite.getHeight());
//
//			sprite.setSize(width, height);
//			sprite.setOrigin(0, 0);
//
//			sprites.put(region.name, sprite);
//		}
//	}
	boolean m=true;
	@Override
	public void render () {
//		System.out.println(this.world.getContactCount());
		// Set Screen Background Colour to White with an Alpha of 100%
		ScreenUtils.clear(1, 1, 1, 1);

		// Set the Sprite Batch Renderer Set to The Camera Matrix
		entityRenderer.getBatch().setProjectionMatrix(camera.combined);
//		map.renderBackground();
		// Sprite Batch Draws Sprite/Texture
//		batch.begin();
//		batch.draw(img, 0, 0);
//		batch.end();

		// Entity Methods Testing
//		floor.applyForce(new Vector2(1,0), 10000f);
//		floor.render(camera);
//		box.render(camera);

		// Operate Player Entity
//		player.operate();

		// Render Boxes
//		entityRenderer.getBatch().begin();
//		for (Entity box : boxes) {
//			box.render(entityRenderer, camera);
//		}
//		for (Entity wall : walls) {
//			wall.render(entityRenderer, camera);
//		}
//		player.render(entityRenderer, camera);
//		entityRenderer.getBatch().end();

//		entityRenderer.beginRender();
//
//		entityRenderer.render();
//
//		entityRenderer.endRender();


//		Laser.beginRender();
//		angle+=0.01f;
//		for (Laser laser : lasers) {
//			laser.setAngle(angle);
//			laser.render();
//		}
//		Laser.endRender();

		for (Entity entity : boxes) {
			entity.updateReflection(player.portals);
		}


		player.operate();

		player.updateReflection(player.portals);

		enemy.operate();

		map.renderBackground();

		entityRenderer.beginRender();
		entityRenderer.render();
		entityRenderer.endRender();

		map.renderForeground();

		player.portals.renderPortals(entityRenderer.getBatch());

		// Render Debug Lines for Physics Object in Physics World
		b2dr.render(world, camera.combined);


		// Update the Camera
		camera.update();

		// Next Physics frame
		stepWorld();
	}
	
	@Override
	public void dispose () {
		// MUST LOOK OVER THIS WELL OR ELSE MEMORY LEAKS WILL OCCUR, THROW AWAY EVERYTHING UNNEEDED AFTER GAME IS ENDED
//		batch.dispose();
//		img.dispose();
		Entity.disposeAll();
		world.dispose();
		for (Entity box : boxes) box.dispose();
		for (Entity wall : walls) wall.dispose();
		player.dispose();
		map.dispose();
	}

	private void stepWorld() {
//		float deltaTime = Gdx.graphics.getDeltaTime();
		world.step(STEP_TIME, VELOCITY_ITERATIONS, POSITION_ITERATIONS);
	}

	// This Scale Methods ARE NECESSARY because of libGDXs poor physics scale. Makes everything small with a small camera to enable uses of small force magnitudes
	static public float scale(float x) {
		return x*GAME_SCALE;
	}
	static public Vector2 scale(Vector2 v) {
		return Vector2.Zero.mulAdd(v,GAME_SCALE);
	}



}
