package com.droidinteractive.box2dlight.test;

import java.util.ArrayList;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL10;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.ChainShape;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.QueryCallback;
import com.badlogic.gdx.physics.box2d.Shape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.joints.MouseJoint;
import com.badlogic.gdx.physics.box2d.joints.MouseJointDef;
import com.droidinteractive.box2dlight.Light;
import com.droidinteractive.box2dlight.PointLight;
import com.droidinteractive.box2dlight.RayHandler;

/**
 * @author Trenton Shaffer
 * @author Kalle Hämäläinen (original author)
 * 
 */
public class Box2dLightTest implements ApplicationListener, InputProcessor {
	/** the camera **/
	private com.badlogic.gdx.graphics.OrthographicCamera camera;
	
	/** Debug Renderer **/
	Box2DDebugRenderer debugRenderer;
	Matrix4 debugMatrix;

	/**
	 * a spritebatch and a font for text rendering and a Texture to draw our
	 * boxes
	 **/
	private static final int RAYS_PER_BALL = 256;
	private static final int BALLSNUM = 3;

	private static float LIGHT_DISTANCE = 16f;
	private static final float radius = 1f;
	private SpriteBatch batch;
	private BitmapFont font;
	private TextureRegion textureRegion;
	private Texture bg;
	private Texture crate;
	private Sprite spriteCrate;
	private Shape shape;
	
	private boolean isDebug = false;		// debug rendering
	private boolean isDiffused = true;		// diffused lighting
	private boolean isGamma = false;		// gamma
	private boolean isBlur = true;			// blurred
	private boolean isCulled = true;		// culled
	private boolean showHelp = true;		// help info

	/** our box2D world **/
	private World world;

	/** our boxes **/
	private ArrayList<Body> balls = new ArrayList<Body>(BALLSNUM);
	

	/** our ground box **/
	Body groundBody;
	
	/** crate body **/
	Body crateBody;

	/** our mouse joint **/
	private MouseJoint mouseJoint = null;

	/** a hit body **/
	Body hitBody = null;

	/** BOX2D LIGHT STUFF BEGIN */
	RayHandler rayHandler;

	/** BOX2D LIGHT STUFF END */

	/** pixel perfect projection for font rendering */
	Matrix4 normalProjection = new Matrix4();

	@Override
	public void create() {

		MathUtils.random.setSeed(Long.MIN_VALUE);

		camera = new OrthographicCamera(48, 32);
		camera.position.set(0, 16, 0);
		camera.update();
		batch = new SpriteBatch();
		font = new BitmapFont();
		font.setColor(Color.RED);
		textureRegion = new TextureRegion(new Texture(
				Gdx.files.internal("data/marble.png")));

		bg = new Texture(Gdx.files.internal("data/bg.png"));
		crate = new Texture(Gdx.files.internal("data/crate.png"));
		spriteCrate = new Sprite(crate);

		createPhysicsWorld();
		Gdx.input.setInputProcessor(this);

		normalProjection.setToOrtho2D(0, 0, Gdx.graphics.getWidth(),
				Gdx.graphics.getHeight());
		
		/** BOX2D LIGHT STUFF BEGIN */
		RayHandler.setGammaCorrection(false);
		RayHandler.useDiffuseLight(true);
		rayHandler = new RayHandler(world);
		rayHandler.setAmbientLight(0.1f, 0.1f, 0.1f, 0.2f);
		rayHandler.setCulling(true);		
		rayHandler.setBlur(true);
		rayHandler.setBlurNum(1);
		rayHandler.setShadows(true);
		camera.update(true);

		for (int i = 0; i < BALLSNUM; i++) {
			Light light = new PointLight(rayHandler, RAYS_PER_BALL);
			light.setDistance(LIGHT_DISTANCE);
			light.attachToBody(balls.get(i), 0, 0.5f);
			light.setColor(MathUtils.random(), MathUtils.random(), MathUtils.random(), 1f);

		}
		/** BOX2D LIGHT STUFF END */
		
		debugRenderer=new Box2DDebugRenderer(true, true, false, true, true, true);

	}

	@Override
	public void render() {

		camera.update();

		// should use fixed step

		boolean stepped = fixedStep(Gdx.graphics.getDeltaTime());
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
		
		batch.setProjectionMatrix(camera.combined);
		batch.disableBlending();
		batch.begin();
		

		batch.draw(bg, -24, 0, 48, 32);

		batch.enableBlending();

		for (int i = 0; i < BALLSNUM; i++) {

			final Body ball = balls.get(i);
			final Vector2 position = ball.getPosition();
			final float angle = MathUtils.radiansToDegrees * ball.getAngle();
			batch.draw(textureRegion, position.x - radius, position.y - radius, radius, radius, radius * 2, radius * 2, 1, 1, angle);
		}
		
		drawCrate();

		batch.end();
		
		if(isDebug)
			debugRenderer.render(world, camera.combined);

		/** BOX2D LIGHT STUFF BEGIN */

		rayHandler.setCombinedMatrix(camera.combined, camera.position.x,
				camera.position.y, camera.viewportWidth * camera.zoom,
				camera.viewportHeight * camera.zoom);

		// rayHandler.setCombinedMatrix(camera.combined);
		if (stepped)
			rayHandler.update();
		rayHandler.render();

		/** BOX2D LIGHT STUFF END */

		long time = System.nanoTime();
		aika += System.nanoTime() - time;

		boolean atShadow = rayHandler.pointAtShadow(testPoint.x,
				testPoint.y);

		/** FONT */
		if (showHelp)
		{
			batch.setProjectionMatrix(normalProjection);
			batch.begin();
			font.draw(batch, "HELP - Press (H) to hide", 0, 176);
			font.draw(batch, "------------------------", 0, 160);
			font.draw(batch, "(D)Debug: " + isDebug , 0, 144);
			font.draw(batch, "(F1)Blurred: " + isBlur , 0, 128);
			font.draw(batch, "(F2)Culled: " + isCulled , 0, 112);
			font.draw(batch, "(F3)Gamma: " + isGamma , 0, 96);
			font.draw(batch, "(F4)Diffused: " + isDiffused , 0, 80);
			font.draw(batch, "Shadow Calc Time: " + aika / ++times + "ns", 0, 64);
			font.draw(batch, "Pointer in shadow: " + atShadow, 0, 48);
			font.draw(batch, "GLES 2.0: " + Gdx.graphics.isGL20Available(), 0, 32);
			font.draw(batch, Integer.toString(Gdx.graphics.getFramesPerSecond()) + "fps", 0, 16);
			batch.end();
		}

	}

	private final static int MAX_FPS = 60;
	private final static int MIN_FPS = 45;
	public final static float TIME_STEP = 1f / MAX_FPS;
	private final static float MAX_STEPS = 1f + MAX_FPS / MIN_FPS;
	private final static float MAX_TIME_PER_FRAME = TIME_STEP * MAX_STEPS;
	private final static int VELOCITY_ITERS = 6;
	private final static int POSITION_ITERS = 2;

	float physicsTimeLeft;
	long aika;
	int times;

	private boolean fixedStep(float delta) {
		physicsTimeLeft += delta;
		if (physicsTimeLeft > MAX_TIME_PER_FRAME)
			physicsTimeLeft = MAX_TIME_PER_FRAME;

		boolean stepped = false;
		while (physicsTimeLeft >= TIME_STEP) {
			world.step(TIME_STEP, VELOCITY_ITERS, POSITION_ITERS);
			physicsTimeLeft -= TIME_STEP;
			stepped = true;
		}
		return stepped;
	}
	
	/**
	 * @author Trenton Shaffer
	 * WorldToScreen
	 * Converts XY world coordinates to screen coordinates and centers texture
	 * @param x - X world coordinate
	 * @param y - Y world coordinate
	 * @param Texture - texture to center
	 * @param divide - divide texture by 2
	 * @param camera - camera
	 * @return Vector2
	 */
	public Vector2 WorldToScreen(float x, float y, Texture texture, boolean divide, Camera camera)
	{	
		Vector3 coords = new Vector3(x, y, 0);
		camera.project(coords);
		if(divide)
		{
			coords.x = coords.x - (texture.getWidth()/2);
			coords.y = coords.y - (texture.getHeight()/2);
		}
		else
		{
			coords.x = coords.x - texture.getWidth();
			coords.y = coords.y - texture.getHeight();
		}
		Vector2 resultPos = new Vector2(coords.x, coords.y);		
		return resultPos;
	}
	
	public void drawCrate() {
		Vector2 cratePos = crateBody.getPosition();
		Vector2 position = new Vector2(cratePos.x - crate.getWidth()/2, cratePos.y - crate.getHeight()/2);
		final float angle = MathUtils.radiansToDegrees * crateBody.getAngle();
		spriteCrate.setPosition(position.x, position.y);
		spriteCrate.setRotation(angle);
		spriteCrate.setScale(0.03f);
		spriteCrate.draw(batch);
	}

	private void createPhysicsWorld() {

		world = new World(new Vector2(0, -10), true);

		ChainShape chainShape = new ChainShape();
		chainShape.createLoop(new Vector2[] { new Vector2(-22, 1),
				new Vector2(22, 1), new Vector2(22, 31), new Vector2(-22, 31) });
		BodyDef chainBodyDef = new BodyDef();
		chainBodyDef.type = BodyType.StaticBody;
		groundBody = world.createBody(chainBodyDef);
		groundBody.createFixture(chainShape, 0.1f);
		chainShape.dispose();
		createBalls();
		createBoxes();
	}
	
	private void createBoxes() {
		
		//Dynamic Body  
        BodyDef bodyDef = new BodyDef();  
        bodyDef.type = BodyType.DynamicBody;
        
        bodyDef.position.set(0, 10f);  
        crateBody = world.createBody(bodyDef);  
        
        shape = new PolygonShape();
        ((PolygonShape)shape).setAsBox(2f, 2f);
        
        FixtureDef fixtureDef = new FixtureDef();  
        fixtureDef.shape = shape;  
        fixtureDef.density = 1.0f;  
        fixtureDef.friction = 0.4f;  
        fixtureDef.restitution = 0.1f;  
        crateBody.createFixture(fixtureDef); 
	}

	private void createBalls() {
		CircleShape ballShape = new CircleShape();
		ballShape.setRadius(radius);

		FixtureDef def = new FixtureDef();
		def.restitution = 0.9f;
		def.friction = 0.01f;
		def.shape = ballShape;
		def.density = 1f;
		BodyDef ballBodyDef = new BodyDef();
		ballBodyDef.type = BodyType.DynamicBody;

		for (int i = 0; i < BALLSNUM; i++) {
			// Create the BodyDef, set a random position above the
			// ground and create a new body
			ballBodyDef.position.x = -20 + (float) (Math.random() * 40);
			ballBodyDef.position.y = 10 + (float) (Math.random() * 15);
			Body boxBody = world.createBody(ballBodyDef);
			boxBody.createFixture(def);
			balls.add(boxBody);
		}
		ballShape.dispose();
	}

	/**
	 * we instantiate this vector and the callback here so we don't irritate the
	 * GC
	 **/
	Vector3 testPoint = new Vector3();
	QueryCallback callback = new QueryCallback() {
		@Override
		public boolean reportFixture(Fixture fixture) {
			if (fixture.getBody() == groundBody)
				return true;

			if (fixture.testPoint(testPoint.x, testPoint.y)) {
				hitBody = fixture.getBody();
				return false;
			} else
				return true;
		}
	};

	@Override
	public boolean touchDown(int x, int y, int pointer, int newParam) {
		// translate the mouse coordinates to world coordinates
		testPoint.set(x, y, 0);
		camera.unproject(testPoint);

		// ask the world which bodies are within the given
		// bounding box around the mouse pointer
		hitBody = null;
		world.QueryAABB(callback, testPoint.x - 0.1f, testPoint.y - 0.1f,
				testPoint.x + 0.1f, testPoint.y + 0.1f);

		// if we hit something we create a new mouse joint
		// and attach it to the hit body.
		if (hitBody != null) {
			MouseJointDef def = new MouseJointDef();
			def.bodyA = groundBody;
			def.bodyB = hitBody;
			def.collideConnected = true;
			def.target.set(testPoint.x, testPoint.y);
			def.maxForce = 1000.0f * hitBody.getMass();

			mouseJoint = (MouseJoint) world.createJoint(def);
			hitBody.setAwake(true);
		}

		return false;
	}

	/** another temporary vector **/
	Vector2 target = new Vector2();

	@Override
	public boolean touchDragged(int x, int y, int pointer) {
		// if a mouse joint exists we simply update
		// the target of the joint based on the new
		// mouse coordinates
		if (mouseJoint != null) {
			camera.unproject(testPoint.set(x, y, 0));
			mouseJoint.setTarget(target.set(testPoint.x, testPoint.y));
		}
		return false;
	}

	@Override
	public boolean touchUp(int x, int y, int pointer, int button) {
		// if a mouse joint exists we simply destroy it
		if (mouseJoint != null) {
			world.destroyJoint(mouseJoint);
			mouseJoint = null;
		}
		return false;
	}

	@Override
	public void dispose() {
		rayHandler.dispose();
		world.dispose();
	}

	@Override
	public boolean keyDown(int keycode) {
		if (keycode == Input.Keys.RIGHT)
			camera.position.x += 3f;
		if (keycode == Input.Keys.LEFT)
			camera.position.x -= 3f;
		if (keycode == Input.Keys.UP)
			camera.position.y += 3f;
		if (keycode == Input.Keys.DOWN)
			camera.position.y -= 3f;
		if (keycode == Input.Keys.H)
		{
			if(showHelp)
				showHelp = false;
			else
				showHelp = true;
		}
		if (keycode == Input.Keys.D)
		{
			if(isDebug)
				isDebug = false;
			else
				isDebug = true;
		}
		if (keycode == Input.Keys.F1)
		{
			if(isBlur)
				isBlur = false;
			else
				isBlur = true;
			rayHandler.setBlur(isBlur);

		}
		if (keycode == Input.Keys.F2)
		{
			if(isCulled)
				isCulled = false;
			else			
				isCulled = true;
			rayHandler.setCulling(isCulled);
		}
		if (keycode == Input.Keys.F3)
		{
			if(isGamma)
				isGamma = false;
			else			
				isGamma = true;
			RayHandler.setGammaCorrection(isGamma);
		}
		if (keycode == Input.Keys.F4)
		{
			if(isDiffused)
				isDiffused = false;
			else			
				isDiffused = true;
			RayHandler.useDiffuseLight(isDiffused);
		}

		return false;
	}

	@Override
	public boolean keyTyped(char character) {
		return false;
	}

	@Override
	public boolean keyUp(int keycode) {

		return false;
	}

	public boolean touchMoved(int x, int y) {
		testPoint.set(x, y, 0);
		camera.unproject(testPoint);
		return false;
	}

	@Override
	public boolean scrolled(int amount) {
		camera.rotate((float) amount * 3f, 0, 0, 1);
		return false;
	}

	@Override
	public void pause() {
	}

	@Override
	public void resize(int arg0, int arg1) {
	}

	@Override
	public void resume() {
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		return false;
	}
}
