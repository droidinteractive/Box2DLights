package com.droidinteractive.box2dlight;

import com.badlogic.gdx.graphics.glutils.ShaderProgram;

/**
 * @author Trenton Shaffer
 * @version 6.29.13
 * 
 */
public class ShaderException extends RuntimeException {

    /**
	 * Generated serial version UID 
	 */
	private static final long serialVersionUID = -4246271194919149865L;

	
	/**
	 * ShaderException
	 * @param name
	 * @param shader
	 */
	public ShaderException(String name, ShaderProgram shader) {
        super("Compilation of shader [" + name + "] was unsuccessful.\n" + shader.getLog() + "|");
    }
}