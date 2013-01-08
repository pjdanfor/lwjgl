package com.deeter;

import static org.lwjgl.opengl.GL11.*;

import org.lwjgl.LWJGLException;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.ContextAttribs;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.PixelFormat;

import com.deeter.utility.PatCamera;
import com.deeter.utility.PatTimer;

public class ObjectLoader {
	
	private static final String WINDOW_TITLE = "Fuck yeah";
	private static final int WIDTH = 1024, HEIGHT = 768;
	
	private PatTimer timer;
	private PatCamera camera;

	public ObjectLoader() {
		this.setupOpenGL();
		this.setupCamera();
		
		while(!Display.isCloseRequested()) {
			glViewport(0, 0, Display.getWidth(), Display.getHeight());
			this.loopCycle(timer.update());
			Display.update();
		}
		
		this.destroyOpenGL();
	}
	
	private void setupOpenGL() {
		try {
			PixelFormat pixelFormat = new PixelFormat();
			ContextAttribs contextAtrributes = new ContextAttribs(3, 2)
				.withForwardCompatible(true)
				.withProfileCore(true);

			Display.setDisplayMode(new DisplayMode(WIDTH, HEIGHT));
			Display.setTitle(WINDOW_TITLE);
			Display.setResizable(true);
			Display.create(pixelFormat, contextAtrributes);

			glViewport(0, 0, WIDTH, HEIGHT);
		} catch (LWJGLException e) {
			e.printStackTrace();
			System.exit(0);
		}
		
		glEnable(GL_DEPTH_TEST);
		glDepthFunc(GL_LESS);
		glClearColor(0.4f, 0.6f, 0.9f, 0f);
		glViewport(0, 0, WIDTH, HEIGHT);
		
		timer = new PatTimer();
		timer.initialize(WINDOW_TITLE);
	}
	
	private void setupCamera() {
		camera = new PatCamera.Builder()
			.setAspectRatio((float) WIDTH / (float) HEIGHT)
			.setFieldOfView(60)
			.build();
		camera.applyPerspectiveMatrix(0);
		camera.applyOptimalStates();
		Mouse.setGrabbed(true);
	}
	
	private void loopCycle(int delta) {
		this.logicCycle(delta);
		this.renderCycle();
	}
	
	private void logicCycle(int delta) {
		// Handle camera
		camera.setAspectRatio((float) Display.getWidth() / (float) Display.getHeight());
		if (Display.wasResized()) {
			camera.applyPerspectiveMatrix(0);
		}
		while (Keyboard.next()) {
            if (Keyboard.isKeyDown(Keyboard.KEY_ESCAPE)) {
                Mouse.setGrabbed(false);
            }
        }
//		System.out.println(camera);
		camera.applyTranslations(1);
		if (Mouse.isGrabbed()) {
			if (delta <= 0)
				delta = 1;
			camera.processMouse();
			camera.processKeyboard((float) delta);
		}
	}
	
	private void renderCycle() {
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
	}
	
	private void destroyOpenGL() {
		Display.destroy();
	}
	
	public static void main(String[] argv) {
    	new ObjectLoader();
    }
}
