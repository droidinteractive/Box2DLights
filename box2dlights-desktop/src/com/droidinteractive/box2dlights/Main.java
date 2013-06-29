package com.droidinteractive.box2dlights;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.droidinteractive.box2dlight.test.Box2dLightTest;

public class Main {
	public static void main(String[] args) {
		LwjglApplicationConfiguration cfg = new LwjglApplicationConfiguration();
		cfg.title = "Box2DLights Test";
		cfg.useGL20 = true;
		cfg.width = 800;
		cfg.height = 480;
		cfg.samples = 4;
		cfg.depth = 0;
		cfg.vSyncEnabled = true;
		
		new LwjglApplication(new Box2dLightTest(), cfg);
	}
}
