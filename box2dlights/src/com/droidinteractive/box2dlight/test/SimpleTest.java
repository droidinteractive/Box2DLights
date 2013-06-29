package com.droidinteractive.box2dlight.test;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.droidinteractive.box2dlight.PointLight;
import com.droidinteractive.box2dlight.RayHandler;

public class SimpleTest extends ApplicationAdapter {
	/** the camera **/
	OrthographicCamera camera;
	RayHandler rayHandler;
	World world;

	@Override
	public void create() {
		camera = new OrthographicCamera(48, 32);
		camera.update();
		world = new World(new Vector2(0, -10), true);
		rayHandler = new RayHandler(world);
		new PointLight(rayHandler, 32);

	}

	@Override
	public void render() {

		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
		world.step(Gdx.graphics.getDeltaTime(), 8, 3);
		rayHandler.setCombinedMatrix(camera.combined);
		rayHandler.updateAndRender();

	}

}
