package com.droidinteractive.box2dlight;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;

/**
 * @author Trenton Shaffer
 * @author Kalle Hämäläinen (original author)
 * 
 */
public class PointLight extends PositionalLight {

        /**
         * @param rayHandler
         * @param rays
         * @param color
         * @param distance
         * @param x
         * @param y
         */
        public PointLight(RayHandler rayHandler, int rays, Color color,
                        float distance, float x, float y) {
                super(rayHandler, rays, color, distance, x, y, 0f);
                setEndPoints();
                update();
        }

        /**
         * @param rayHandler
         * @param rays
         * @param color
         *            Note default values: 
         *            Color:WHITE
         *            Distance:15            
         *            Position:origo
         */
        public PointLight(RayHandler rayHandler, int rays) {
                this(rayHandler, rays, Light.DefaultColor, 15f, 0f, 0f);
        }

        final void setEndPoints() {
                float angleNum = 360f / (this.rayNum - 1);
                for (int i = 0; i < this.rayNum; i++) {
                        final float angle = angleNum * i;
                        this.sin[i] = MathUtils.sinDeg(angle);
                        this.cos[i] = MathUtils.cosDeg(angle);
                        this.endX[i] = this.distance * this.cos[i];
                        this.endY[i] = this.distance * this.sin[i];
                }
        }

        @Override
        public void setDirection(float directionDegree) {
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
                setEndPoints();
                if (this.staticLight)
                        staticUpdate();
        }

}