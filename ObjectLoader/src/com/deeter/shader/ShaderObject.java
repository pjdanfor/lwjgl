package com.deeter.shader;

import static org.lwjgl.opengl.ARBShaderObjects.GL_OBJECT_INFO_LOG_LENGTH_ARB;
import static org.lwjgl.opengl.GL20.glGetProgramInfoLog;
import static org.lwjgl.opengl.GL20.glGetProgrami;

public abstract class ShaderObject {
	
	private String path;
	private int identifier = 0, type = 0;
	
	public ShaderObject() {
		initialize();
	}
	
	public ShaderObject(String path, int type) {
		this.setPath(path);
		this.setType(type);
		initialize();
	}
	
	public abstract void initialize();
	
	public abstract void tearDown();
	
	public abstract void delete();
	
	public boolean isLegit() {
		return identifier != 0;
	}
	
	protected String getLogInfo(int obj) {
		return glGetProgramInfoLog(obj, glGetProgrami(obj, GL_OBJECT_INFO_LOG_LENGTH_ARB));
	}
	
	public int getIdentifier() {
		return identifier;
	}
	
	public void setIdentifier(int identifier) {
		this.identifier = identifier;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}
}