package com.deeter.shader;

import static org.lwjgl.opengl.ARBShaderObjects.GL_OBJECT_COMPILE_STATUS_ARB;
import static org.lwjgl.opengl.ARBShaderObjects.GL_OBJECT_INFO_LOG_LENGTH_ARB;
import static org.lwjgl.opengl.ARBShaderObjects.GL_OBJECT_LINK_STATUS_ARB;
import static org.lwjgl.opengl.ARBShaderObjects.GL_OBJECT_VALIDATE_STATUS_ARB;
import static org.lwjgl.opengl.GL11.GL_FALSE;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.glBindFragDataLocation;

import java.util.HashMap;
import java.util.Map;

import org.lwjgl.opengl.ARBFragmentShader;
import org.lwjgl.opengl.ARBVertexShader;

import com.deeter.utility.StringUtils;

public class ShaderProgram {
	
	public static final CharSequence VERTEX_POSITION = "in_Position";
	public static final CharSequence VERTEX_NORMAL = "in_Normal";
	public static final CharSequence VERTEX_TEXTURE = "in_TextureCoord";
	public static final CharSequence LIGHT_POSITION = "lightPosition";
	public static final CharSequence FRAG_OUT_COLOR = "outColor";
	public static final CharSequence PROJECTION_MATRIX = "projectionMatrix";
	public static final CharSequence VIEW_MATRIX = "viewMatrix";
	public static final CharSequence MODEL_MATRIX = "modelMatrix";
	
	private String vertexShaderPath, fragmentShaderPath;
	private Shader vertexShader, fragmentShader;
	private int programID = 0;
	private int attributeCounter, fragmentDataCounter;
	private Map<CharSequence, Integer> attributeMap, fragmentDataMap;
	
	public ShaderProgram(String vertexShaderPath, String fragmentShaderPath) {
		this.vertexShaderPath = vertexShaderPath;
		this.fragmentShaderPath = fragmentShaderPath;
		this.initialize();
	}
	
	public void initialize() {
		attributeCounter = 0;
		fragmentDataCounter = 0;
		attributeMap = new HashMap<CharSequence, Integer>();
		fragmentDataMap = new HashMap<CharSequence, Integer>();
		setupProgram();
		setupShaders();
	}
	
	private void setupProgram() {
		setIdentifier(glCreateProgram());
	}
	
	private void setupShaders() {
		try {
			vertexShader = new Shader(getIdentifier(), vertexShaderPath, GL_VERTEX_SHADER);
            fragmentShader = new Shader(getIdentifier(), fragmentShaderPath, GL_FRAGMENT_SHADER);
		}
		catch(Exception e) {
    		e.printStackTrace();
    	}
    	finally {
    		if (!vertexShader.isLegit() || !fragmentShader.isLegit()) {
    			System.err.println("A shader shit itself");
    			System.exit(0);
    		}
    	}
		attachShader(vertexShader);
		attachShader(fragmentShader);
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
	
	public ShaderProgram attachShader(Shader shader) {
		glAttachShader(getIdentifier(), shader.getIdentifier());
		return this;
	}
	
	public void detachShaders() {
		glDetachShader(getIdentifier(), vertexShader.getIdentifier());
		glDetachShader(getIdentifier(), fragmentShader.getIdentifier());
	}
	
	public void deleteShaders() {
		vertexShader.delete();
		fragmentShader.delete();
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
	
	public void enableAttributes() {
		for (CharSequence attribute : attributeMap.keySet()) {
			enableAttribute(attribute);
		}
	}
	
	public ShaderProgram enableAttribute(CharSequence name) {
		glEnableVertexAttribArray(getAttributeLocation(name));
		return this;
	}
	
	public void disableAttributes() {
		for (CharSequence attribute : attributeMap.keySet()) {
			disableAttribute(attribute);
		}
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
	
	private void setIdentifier(int identifier) {
		this.programID = identifier;
	}
	
	public int getIdentifier() {
		return this.programID;
	}
	
	private int getAttributeLocation(CharSequence name) {
		return attributeMap.get(name);
	}
	
	public void delete() {
		glDeleteProgram(getIdentifier());
	}
	
	public void tearDown() {
		deactivate();
		delete();
	}
	
	private String getLogInfo(int obj) {
		return glGetProgramInfoLog(obj, glGetProgrami(obj, GL_OBJECT_INFO_LOG_LENGTH_ARB));
	}
	
	// Shader class
	
	public class Shader {
		
		private int identifier;
		private int programID;
		
		public Shader(int programID, String shaderPath, int type) {
			this.programID = programID;
			try {
				setIdentifier(glCreateShader(type));
		        
		        if (isLegit()) {
		        	glShaderSource(getIdentifier(), StringUtils.readFileAsString(shaderPath));
			        glCompileShader(getIdentifier());
			        if (glGetShaderi(getIdentifier(), GL_OBJECT_COMPILE_STATUS_ARB) == GL_FALSE) {
			        	System.err.println("Error creating shader: " + getLogInfo(getIdentifier()));
			            throw new RuntimeException("Error creating shader: " + getLogInfo(getIdentifier()));
			        }
		        }
	    	}
	    	catch(Exception e) {
	    		delete();
	    	}
		}
		
		private void setIdentifier(int identifier) {
			this.identifier = identifier;
		}
		
		public int getIdentifier() {
			return this.identifier;
		}
		
		public int getProgramIdentifier() {
			return this.programID;
		}
		
		public void delete() {
			glDeleteShader(getIdentifier());
		}
		
		public boolean isLegit() {
			return getIdentifier() != 0;
		}
		
		private String getLogInfo(int identifier) {
			return glGetShaderInfoLog(identifier, glGetProgrami(identifier, GL_OBJECT_INFO_LOG_LENGTH_ARB));
		}
	}
}