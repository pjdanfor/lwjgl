package com.deeter.utility;

import org.lwjgl.Sys;
import org.lwjgl.opengl.Display;

public class LWJGLTimer {

	private int fps = 0;
	private long lastFPS;
	private long lastTime;
	private int elapsedTime;
	private String windowTitle;
	
	public LWJGLTimer() {}
	
	public void initialize(String windowTitle) {
		lastTime = getTime();
		lastFPS = getTime();
		this.windowTitle = windowTitle;
	}
	
	public int getElapsedTime() {
		return elapsedTime;
	}
	
	public int update() {
		updateFPS();
		long time = getTime();
		elapsedTime = (int) (time - lastTime);
		lastTime = time;
		return elapsedTime;
	}
	
	public void updateFPS() {
		if (getTime() - lastFPS > 1000) {
			Display.setTitle(windowTitle + " FPS: " + fps);
			fps = 0;
			lastFPS += 1000;
		}
		fps++;
	}
	
	private long getTime() {
	    return (Sys.getTime() * 1000) / Sys.getTimerResolution();
	}
}
