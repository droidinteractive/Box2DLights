package com.droidinteractive.box2dlight;

import java.util.HashMap;

import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;

/**
 * <b>Shader</b><br>
 * Load shader from external file and compile
 * @author Trenton Shaffer
 * @version 6.29.13
 * 
 */
public class Shader {
	private static Shader instance;
	
	private enum ShaderType {
		vsh,
		fsh;
	}

	private static class ShaderStructure {
        private final String name;
        private String vertex;
        private String fragment;
        private ShaderProgram program;

        
        /**
         * ShaderStructure
         * @param name
         */
        public ShaderStructure(String name) {
            this.name = name;
        }

        /**
         * <b>setVertex</b><br>
         * Sets the Vertex Shader
         * @param vertex - name of vertex shader to compile
         */
        public void setVertex(String vertex) {
            this.vertex = vertex;
            compile();
        }

        /**
         * <b>setFragment</b><br>
         * Sets the Fragment Shader
         * @param fragment - name of fragment shader to compile
         */
        public void setFragment(String fragment) {
            this.fragment = fragment;
            compile();
        }

        /**
         * Compiles shader
         */
        private void compile() {
            if (this.vertex != null && this.fragment != null) {
                System.out.println("Compiling shader [" + this.name + "]");
                this.program = new ShaderProgram(this.vertex, this.fragment);
                if (!this.program.isCompiled()) {
                    throw new ShaderException(this.name, this.program);
                }
            }
        }

        /**
         * <b>getShader</b><br>
         * Get shader program
         * @return ShaderProgram
         */
        public ShaderProgram getShader() {
            return this.program;
        }

    }

    private final HashMap<String, ShaderStructure> map = new HashMap<String, ShaderStructure>();

    public Shader() {
        FileHandle dirHandle;

        // Deal with linked assets
        if (Gdx.app.getType() == ApplicationType.Android) {
            dirHandle = Gdx.files.internal("data/shaders");
        } else {
            // ApplicationType.Desktop ..
        	// Assets are in bin folder
            dirHandle = Gdx.files.internal("./bin/data/shaders");
            if(!dirHandle.exists()){
                dirHandle = Gdx.files.internal("data/shaders");
            }
        }

        FileHandle[] list = dirHandle.list();

        for (FileHandle f : list) {

            String ext = f.extension();
            String name = f.nameWithoutExtension();

            ShaderStructure ss;
            if (!this.map.containsKey(name)) {
                ss = new ShaderStructure(name);
            } else {
                ss = this.map.get(name);
            }

            switch (ShaderType.valueOf(ext)) {
                case vsh:
                    ss.setVertex(f.readString());
                    break;
                case fsh:
                    ss.setFragment(f.readString());
                    break;
            }

            if (!this.map.containsKey(name)) {
                this.map.put(name, ss);
            }

        }
    }

    /**
     * @param name
     * @return
     */
    private ShaderStructure getStructure(String name) {
        ShaderStructure value = this.map.get(name);
        if (value == null) {
            throw new IllegalArgumentException("Specified Shader does not exists: " + name);
        }
        return value;
    }

    /**
     * @param name
     * @return
     */
    public static ShaderProgram getShader(String name) {
        if (instance == null) {
            instance = new Shader();
        }
        return instance.getStructure(name).getShader();
    }
}
