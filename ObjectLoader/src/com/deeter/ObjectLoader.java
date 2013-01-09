package com.deeter;

import static org.lwjgl.opengl.ARBBufferObject.*;
import static org.lwjgl.opengl.ARBVertexBufferObject.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;

import java.io.IOException;
import java.nio.FloatBuffer;
import java.util.ArrayList;

import org.lwjgl.BufferUtils;
import org.lwjgl.LWJGLException;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.ContextAttribs;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.PixelFormat;
import org.lwjgl.util.vector.Matrix4f;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureLoader;
import org.newdawn.slick.util.ResourceLoader;

import com.deeter.obj.builder.BuildHelper;
import com.deeter.shader.ShaderProgram;
import com.deeter.utility.LWJGLTimer;
import com.deeter.utility.MahTexturedCube;
import com.deeter.utility.Scene;
import com.deeter.utility.VertexData;

public class ObjectLoader {
	
	private static final String WINDOW_TITLE = "YEAH";
	private static final int WIDTH = 1024, HEIGHT = 768;
	
	private LWJGLTimer timer;
	private PatCamera camera;
	private ArrayList<Scene> scenes;
	
	private ShaderProgram shaderProgram;
	// Quad variables
	private int vao, vbo, ibo;
	private int indicesCount = 0;
	// Texture variables
	private Texture texture;
	// Moving variables
	private int projectionMatrixLocation = 0;
	private int viewMatrixLocation = 0;
	private int modelMatrixLocation = 0;
	private Matrix4f modelMatrix;
	private FloatBuffer matrix44Buffer = null;

	public ObjectLoader() {
		this.setupOpenGL();
		this.setupShaders();
		this.setupScenes();
		this.setupCamera();
		
		while(!Display.isCloseRequested()
				&& !Keyboard.isKeyDown(Keyboard.KEY_ESCAPE)) {
			glViewport(0, 0, Display.getWidth(), Display.getHeight());
			this.loopCycle(timer.update());
			Display.update();
		}
		
		this.destroyOpenGL();
	}
	
	private void setupOpenGL() {
		try {
			PixelFormat pixelFormat = new PixelFormat();
			ContextAttribs contextAttributes = new ContextAttribs(3, 2)
				.withForwardCompatible(true)
				.withProfileCore(true);

			Display.setTitle(WINDOW_TITLE);
			Display.create(pixelFormat, contextAttributes);

			glViewport(0, 0, Display.getDisplayMode().getWidth(), Display.getDisplayMode().getHeight());
		} catch (LWJGLException e) {
			e.printStackTrace();
			System.exit(0);
		}
		
		glEnable(GL_DEPTH_TEST);
		glDepthFunc(GL_LESS);
		glClearColor(0.4f, 0.6f, 0.9f, 0f);
		glViewport(0, 0, WIDTH, HEIGHT);
		
		timer = new LWJGLTimer();
		timer.initialize(WINDOW_TITLE);
		matrix44Buffer = BufferUtils.createFloatBuffer(16);
	}
	
	private void setupShaders() {
    	// Create Shader Program
    	shaderProgram = new ShaderProgram("shader/screen.vert", "shader/screen.frag");
    	// Bind the vertex array object
    	vao = glGenVertexArrays();
		glBindVertexArray(vao);
    	// Bind the fragment data location for variable 'outColor'
    	shaderProgram.bindFragment(ShaderProgram.FRAG_OUT_COLOR);
		// Pass information from our VBO and VAO to the shader variables
    	shaderProgram.bindAttribute(ShaderProgram.VERT_IN_POSITION)
    				 .bindAttribute(ShaderProgram.VERT_IN_NORMAL)
    				 .bindAttribute(ShaderProgram.VERT_IN_TEXTURE);
    	// Link the program
    	shaderProgram.link();
    	
    	// Get matrices uniform location
    	projectionMatrixLocation = shaderProgram.getUniformLocation(ShaderProgram.PROJECTION_MATRIX);
    	viewMatrixLocation = shaderProgram.getUniformLocation(ShaderProgram.VIEW_MATRIX);
    	modelMatrixLocation = shaderProgram.getUniformLocation(ShaderProgram.MODEL_MATRIX);
    	
    	// Detach and tear down the shaders once we have set the program up
    	shaderProgram.detachShaders();
    	shaderProgram.deleteShaders();
    	shaderProgram.validate();
    	shaderProgram.activate();
	}
	
	private void setupCamera() {
		camera = new PatCamera.Builder()
			.setAspectRatio((float) WIDTH / (float) HEIGHT)
			.setFieldOfView(60)
			.setFarClippingPlane(200)
			.build();
		camera.applyPerspectiveMatrix(projectionMatrixLocation);
		camera.applyOptimalStates();
		Mouse.setGrabbed(true);
	}
	
	private void setupScenes() {
		scenes = new ArrayList<Scene>();
		scenes.add(BuildHelper.setupScene("res/bunny.obj", "", 100, 0, 0));
		scenes.add(BuildHelper.setupScene("res/goblin.obj", "", -50, 0, 0));
		scenes.add(BuildHelper.setupScene("res/goblin.obj", "", 50, 0, 100));
		scenes.add(BuildHelper.setupScene("res/goblin.obj", "", 50, 100, -100));
	}
	
	private void loopCycle(int delta) {
		this.logicCycle(delta);
		this.renderCycle();
	}
	
	private void logicCycle(int delta) {
		camera.applyTranslations(viewMatrixLocation);
		if (Mouse.isGrabbed()) {
			camera.processMouse();
			camera.processKeyboard((float) delta, 20.0f);
		}
		modelMatrix = new Matrix4f();
		modelMatrix.store(matrix44Buffer); matrix44Buffer.flip();
		glUniformMatrix4(modelMatrixLocation, false, matrix44Buffer);
	}
	
	private void renderCycle() {
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
		GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_LINE);
		glBindVertexArray(vao);
		for (Scene scene : scenes) {
			scene.render(shaderProgram);
		}
		glBindVertexArray(0);
	}
	
	private void destroyOpenGL() {
		shaderProgram.tearDown();
		Display.destroy();
		System.exit(0);
	}
	
	public static void main(String[] argv) {
    	new ObjectLoader();
    }
}
