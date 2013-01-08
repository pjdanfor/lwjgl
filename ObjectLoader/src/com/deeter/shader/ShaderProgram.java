package com.deeter.shader;

import static org.lwjgl.opengl.ARBShaderObjects.GL_OBJECT_LINK_STATUS_ARB;
import static org.lwjgl.opengl.ARBShaderObjects.GL_OBJECT_VALIDATE_STATUS_ARB;
import static org.lwjgl.opengl.GL11.GL_FALSE;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.glBindFragDataLocation;

import java.util.HashMap;
import java.util.Map;

public class ShaderProgram extends ShaderObject {
	
	private int attributeCounter;
	private int fragmentDataCounter;
	private Map<CharSequence, Integer> attributeMap;
	private Map<CharSequence, Integer> fragmentDataMap;
	
	@Override
	public void initialize() {
		setIdentifier(glCreateProgram());
		attributeCounter = 0;
		fragmentDataCounter = 0;
		attributeMap = new HashMap<CharSequence, Integer>();
		fragmentDataMap = new HashMap<CharSequence, Integer>();
	}
	
	public void link() {
		glLinkProgram(getIdentifier());
        if (glGetProgrami(getIdentifier(), GL_OBJECT_LINK_STATUS_ARB) == GL_FALSE) {
            System.err.println(getLogInfo(getIdentifier()));
            return;
        }
	}
	
	public void validate() {
		glValidateProgram(getIdentifier());
        if (glGetProgrami(getIdentifier(), GL_OBJECT_VALIDATE_STATUS_ARB) == GL_FALSE) {
        	System.err.println(getLogInfo(getIdentifier()));
        	return;
        }
	}
	
	public void activate() {
		glUseProgram(getIdentifier());
	}
	
	public void deactivate() {
		glUseProgram(0);
	}
	
	public void attachShader(Shader shader) {
		glAttachShader(getIdentifier(), shader.getIdentifier());
	}
	
	public void detachShader(Shader shader) {
		glDetachShader(getIdentifier(), shader.getIdentifier());
	}
	
	public ShaderProgram bindAttribute(CharSequence name) {
		attributeMap.put(name, attributeCounter);
		glBindAttribLocation(getIdentifier(), attributeCounter++, name);
		return this;
	}
	
	public int getAttribute(CharSequence name) {
		return getAttributeLocation(name);
	}
	
	public ShaderProgram setAttributeData(CharSequence name, int size, int type, boolean normalized, int stride, long bufferOffset) {
		glVertexAttribPointer(getAttributeLocation(name), size, type, normalized, stride, bufferOffset);
		return this;
	}
	
	public ShaderProgram enableAttribute(CharSequence name) {
		glEnableVertexAttribArray(getAttributeLocation(name));
		return this;
	}
	
	public ShaderProgram disableAttribute(CharSequence name) {
		glDisableVertexAttribArray(getAttributeLocation(name));
		return this;
	}
	
	public int getUniformLocation(CharSequence name) {
		return glGetUniformLocation(getIdentifier(), name);
	}
	
	public void bindFragment(CharSequence name) {
		fragmentDataMap.put(name, fragmentDataCounter);
		glBindFragDataLocation(getIdentifier(), fragmentDataCounter++, name);
	}
	
	@Override
	public void delete() {
		glDeleteProgram(getIdentifier());
	}
	
	@Override
	public void tearDown() {
		deactivate();
		delete();
	}
	
	private int getAttributeLocation(CharSequence name) {
		return attributeMap.get(name);
	}
}