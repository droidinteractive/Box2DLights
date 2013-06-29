package com.droidinteractive.box2dlight;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;

/**
 * @author Trenton Shaffer
 * @author Kalle Hämäläinen (original author)
 * 
 */
public class ConeLight extends PositionalLight {

	float coneDegree;

	/**
	 * @param rayHandler
	 * @param rays
	 * @param directionDegree
	 * @param distance
	 * @param color
	 * @param x
	 * @param y
	 * @param coneDegree
	 */
	public ConeLight(RayHandler rayHandler, int rays, Color color,
			float distance, float x, float y, float directionDegree,
			float coneDegree) {

		super(rayHandler, rays, color, distance, x, y, directionDegree);
		setConeDegree(coneDegree);
		setDirection(this.direction);
		update();
	}

	@Override
	public void setDirection(float direction) {

		this.direction = direction;
		for (int i = 0; i < this.rayNum; i++) {
			float angle = direction + this.coneDegree - 2f * this.coneDegree * i
					/ (this.rayNum - 1f);
			final float s = this.sin[i] = MathUtils.sinDeg(angle);
			final float c = this.cos[i] = MathUtils.cosDeg(angle);
			this.endX[i] = this.distance * c;
			this.endY[i] = this.distance * s;
		}
		if (this.staticLight)
			staticUpdate();
	}

	/**
	 * @return the coneDegree
	 */
	public final float getConeDegree() {
		return this.coneDegree;

	}

	/**
	 * How big is the arc of cone. Arc angle = coneDegree * 2
	 * 
	 * @param coneDegree
	 *            the coneDegree to set
	 */
	public final void setConeDegree(float coneDegree) {
		if (coneDegree < 0)
			coneDegree = 0;
		if (coneDegree > 180)
			coneDegree = 180;
		this.coneDegree = coneDegree;
		setDirection(this.direction);
	}

	/**
	 * setDistance(float dist) MIN capped to 1cm
	 * 
	 * @param dist
	 */
	@Override
	public void setDistance(float dist) {
		dist *= RayHandler.gammaCorrectionParameter;
		this.distance = dist < 0.01f ? 0.01f : dist;
		setDirection(this.direction);
	}

}