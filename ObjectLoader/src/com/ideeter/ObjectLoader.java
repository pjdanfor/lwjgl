package com.ideeter;

import static org.lwjgl.opengl.GL11.*;

import org.lwjgl.LWJGLException;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.ContextAttribs;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.PixelFormat;

public class ObjectLoader {
	
	private static final String WINDOW_TITLE = "Fuck yeah";
	private static final int WIDTH = 1024, HEIGHT = 768;

	public ObjectLoader() {
		this.setupOpenGL();
		
		while(!Display.isCloseRequested() &&
				!Keyboard.isKeyDown(Keyboard.KEY_ESCAPE)) {
			this.loopCycle();
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
	}
	
	private void loopCycle() {
		this.logicCycle();
		this.renderCycle();
	}
	
	private void logicCycle() {
		
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
