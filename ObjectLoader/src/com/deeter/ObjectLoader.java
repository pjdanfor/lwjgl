package com.deeter;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;

import java.nio.FloatBuffer;
import java.util.ArrayList;

import org.lwjgl.BufferUtils;
import org.lwjgl.LWJGLException;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.ContextAttribs;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.PixelFormat;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector4f;

import com.deeter.obj.builder.BuildHelper;
import com.deeter.shader.ShaderProgram;
import com.deeter.utility.LWJGLTimer;
import com.deeter.utility.Scene;

public class ObjectLoader {
	
	private static final String WINDOW_TITLE = "YEAH";
	private static final int WIDTH = 1024, HEIGHT = 768;
	
	private LWJGLTimer timer;
	private PatCamera camera;
	private ArrayList<Scene> scenes;
	private ShaderProgram shaderProgram;
	private int vao = 0;
	private int projectionMatrixLocation = 0;
	private int viewMatrixLocation = 0;
	private int modelMatrixLocation = 0;
	private int lightPositionLocation = 0;
	private Matrix4f modelMatrix;
	private FloatBuffer matrix44Buffer = null;
	private float lightX = 100;
	private int lightDirection = -1;

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
//		glClearColor(0.4f, 0.6f, 0.9f, 0f);
		glViewport(0, 0, WIDTH, HEIGHT);
		
		timer = new LWJGLTimer();
		timer.initialize(WINDOW_TITLE);
		matrix44Buffer = BufferUtils.createFloatBuffer(16);
	}
	
	private void setupShaders() {
    	// Create Shader Program
    	shaderProgram = new ShaderProgram("shader/light.vert", "shader/light.frag");
    	// Bind the vertex array object
    	vao = glGenVertexArrays();
		glBindVertexArray(vao);
    	// Bind the fragment data location for variable 'outColor'
    	shaderProgram.bindFragment(ShaderProgram.FRAG_OUT_COLOR);
		// Pass information from our VBO and VAO to the shader variables
    	shaderProgram.bindAttribute(ShaderProgram.VERTEX_POSITION)
    				 .bindAttribute(ShaderProgram.VERTEX_NORMAL)
    				 .bindAttribute(ShaderProgram.VERTEX_TEXTURE);
    	// Link the program
    	shaderProgram.link();
    	
    	// Get matrices uniform location
    	projectionMatrixLocation = shaderProgram.getUniformLocation(ShaderProgram.PROJECTION_MATRIX);
    	viewMatrixLocation = shaderProgram.getUniformLocation(ShaderProgram.VIEW_MATRIX);
    	modelMatrixLocation = shaderProgram.getUniformLocation(ShaderProgram.MODEL_MATRIX);
    	lightPositionLocation = shaderProgram.getUniformLocation(ShaderProgram.LIGHT_POSITION);
    	
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
			.setFarClippingPlane(500)
			.build();
		camera.applyPerspectiveMatrix(projectionMatrixLocation);
		camera.applyOptimalStates();
		camera.setPosition(30, 150, 300);
		Mouse.setGrabbed(true);
	}
	
	private void setupScenes() {
		scenes = new ArrayList<Scene>();
		scenes.add(BuildHelper.setupScene("res/floor.obj", "res/lava.png", 0, -114, 0));
		scenes.add(BuildHelper.setupScene("res/floor.obj", "res/lava.png", 300, -114, 0));
		scenes.add(BuildHelper.setupScene("res/floor.obj", "res/lava.png", -300, -114, 0));
		scenes.add(BuildHelper.setupScene("res/floor.obj", "res/lava.png", 0, -114, 300));
		scenes.add(BuildHelper.setupScene("res/floor.obj", "res/lava.png", 300, -114, 300));
		scenes.add(BuildHelper.setupScene("res/floor.obj", "res/lava.png", -300, -114, 300));
		scenes.add(BuildHelper.setupScene("res/floor.obj", "res/lava.png", 0, -114, -300));
		scenes.add(BuildHelper.setupScene("res/floor.obj", "res/lava.png", 300, -114, -300));
		scenes.add(BuildHelper.setupScene("res/floor.obj", "res/lava.png", -300, -114, -300));
		scenes.add(BuildHelper.setupScene("res/bunny.obj", "", 100, -50, 0));
		scenes.add(BuildHelper.setupScene("res/goblin.obj", "", -100, 0, 0));
		scenes.add(BuildHelper.setupScene("res/goblin.obj", "", 90, 0, 100));
		scenes.add(BuildHelper.setupScene("res/goblin.obj", "", 90, 0, -100));
	}
	
	private void loopCycle(int delta) {
		this.logicCycle(delta);
		this.renderCycle();
	}
	
	private void logicCycle(int delta) {
		camera.applyTranslations(viewMatrixLocation);
		if (Mouse.isGrabbed()) {
			camera.processMouse();
			camera.processKeyboard((float) delta, 30.0f);
		}
		lightX += 0.2f * delta * lightDirection;
		if (lightX <= -590) {
			lightX = -590;
			lightDirection = 1;
		}
		else if (lightX >= 590) {
			lightX = 590;
			lightDirection = -1;
		}
		glUniform4f(lightPositionLocation, lightX, 200, -50, 1);
		for (Scene scene : scenes) {
			scene.update(shaderProgram, delta);
		}
	}
	
	private void renderCycle() {
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
//		GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_LINE);
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
