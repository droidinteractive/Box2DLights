package com.droidinteractive.box2dlights.test;

import com.badlogic.gdx.backends.lwjgl.LwjglApplet;
import com.droidinteractive.box2dlight.test.Box2dLightTest;

/** THIS IS NOT INDEPENDENT AND RUNNABLE */
@SuppressWarnings("serial")
public class Box2dApplet extends LwjglApplet {
	public Box2dApplet() {
		super(new Box2dLightTest(), true);
	}
}
