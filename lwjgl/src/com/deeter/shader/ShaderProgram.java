package com.deeter.shader;

import static org.lwjgl.opengl.ARBShaderObjects.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;

public class ShaderProgram extends ShaderObject {
	
	@Override
	public void initialize() {
		setIdentifier(glCreateProgram());
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
	
	public void bindAttribLocation(int attribIdentifier, CharSequence name) {
		glBindAttribLocation(getIdentifier(), attribIdentifier, name);
	}
	
	public void bindFragDataLocation(int dataIdentifier, CharSequence name) {
		glBindFragDataLocation(getIdentifier(), dataIdentifier, name);
	}
	
	public int getAttributeLocation(CharSequence name) {
		return glGetAttribLocation(getIdentifier(), name);
	}
	
	public int getUniformLocation(CharSequence name) {
		return glGetUniformLocation(getIdentifier(), name);
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
}
