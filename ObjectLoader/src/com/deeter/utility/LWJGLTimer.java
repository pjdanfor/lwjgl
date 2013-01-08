package com.deeter.utility;

import org.lwjgl.Sys;
import org.lwjgl.opengl.Display;

public class LWJGLTimer {

	private int fps = 0;
	private long lastFPS;
	private long lastTime;
	private int elapsedTime;
	private boolean firstRun;
	private String windowTitle;
	
	public LWJGLTimer() {}
	
	public void initialize(String windowTitle) {
		lastTime = getTime();
		lastFPS = getTime();
		firstRun = false;
		this.windowTitle = windowTitle;
	}
	
	public int getElapsedTime() {
		return elapsedTime;
	}
	
	public int update() {
		if (firstRun) {
			firstRun = false;
			lastTime = getTime();
			lastFPS = getTime();
			return 0;
		}
		else {
			long time = getTime();
			elapsedTime = (int) (time - lastTime);
			lastTime = time;
			updateFPS();
			return elapsedTime;
		}
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
