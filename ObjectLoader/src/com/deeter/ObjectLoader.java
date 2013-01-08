package com.deeter;

import static org.lwjgl.opengl.ARBBufferObject.GL_STATIC_DRAW_ARB;
import static org.lwjgl.opengl.ARBBufferObject.GL_STREAM_DRAW_ARB;
import static org.lwjgl.opengl.ARBVertexBufferObject.GL_ARRAY_BUFFER_ARB;
import static org.lwjgl.opengl.ARBVertexBufferObject.GL_ELEMENT_ARRAY_BUFFER_ARB;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.glBindBuffer;
import static org.lwjgl.opengl.GL15.glBufferData;
import static org.lwjgl.opengl.GL15.glGenBuffers;
import static org.lwjgl.opengl.GL20.glDisableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glUniformMatrix4;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.LWJGLException;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.ARBFragmentShader;
import org.lwjgl.opengl.ARBVertexShader;
import org.lwjgl.opengl.ContextAttribs;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.PixelFormat;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureLoader;
import org.newdawn.slick.util.ResourceLoader;

import com.deeter.shader.Shader;
import com.deeter.shader.ShaderProgram;
import com.deeter.utility.MahTexturedCube;
import com.deeter.utility.PatCamera;
import com.deeter.utility.PatTimer;
import com.deeter.utility.VertexData;

public class ObjectLoader {
	
	private static final String WINDOW_TITLE = "Fuck yeah";
	private static final int WIDTH = 1024, HEIGHT = 768;
	private static final String VERT_IN_POSITION = "in_Position";
	private static final String VERT_IN_COLOR = "in_Color";
	private static final String VERT_IN_TEX_COORD = "in_TextureCoord";
	private static final String FRAG_OUT_COLOR = "outColor";
	private static final String PROJECTION_MATRIX = "projectionMatrix";
	private static final String VIEW_MATRIX = "viewMatrix";
	
	private PatTimer timer;
	private PatCamera camera;
	
	private ShaderProgram shaderProgram;
	private Shader fragmentShader, vertexShader;
	// Quad variables
	private int vao, vbo, ibo;
	private int indicesCount = 0;
	private VertexData[] vertices = null;
	private ByteBuffer verticesByteBuffer = null;
	// Texture variables
	private Texture texture;
	// Moving variables
	private int projectionMatrixLocation = 0;
	private int viewMatrixLocation = 0;

	public ObjectLoader() {
		this.setupOpenGL();
		this.setupShaders();
		this.setupTextures();
		this.setupCamera();
		this.setupCube();
		
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
			ContextAttribs contextAttributes = new ContextAttribs(3, 2)
				.withForwardCompatible(true)
				.withProfileCore(true);

			Display.setDisplayMode(new DisplayMode(WIDTH, HEIGHT));
			Display.setTitle(WINDOW_TITLE);
			Display.create(pixelFormat, contextAttributes);

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
	
	private void setupShaders() {    	
    	try {
            vertexShader = new Shader("shader/screen.vert", ARBVertexShader.GL_VERTEX_SHADER_ARB);
            fragmentShader = new Shader("shader/screen.frag", ARBFragmentShader.GL_FRAGMENT_SHADER_ARB);
    	}
    	catch(Exception e) {
    		e.printStackTrace();
    		return;
    	}
    	finally {
    		if (!vertexShader.isLegit() || !fragmentShader.isLegit())
    			return;
    	}
    	
    	// Create Shader Program
    	shaderProgram = new ShaderProgram();
    	if(!shaderProgram.isLegit())
    		return;
    	
    	// Attach Shaders to Program
    	shaderProgram.attachShader(vertexShader)
    				 .attachShader(fragmentShader);
    	// Link the program
    	shaderProgram.link();
    	
    	// Bind the fragment data location for variable 'outColor'
    	shaderProgram.bindFragment(FRAG_OUT_COLOR);
		// Pass information from our VBO and VAO to the shader variables
    	shaderProgram.bindAttribute(VERT_IN_POSITION)
    				 .bindAttribute(VERT_IN_COLOR)
    				 .bindAttribute(VERT_IN_TEX_COORD);
    	
    	// Get matrices uniform location
    	projectionMatrixLocation = shaderProgram.getUniformLocation(PROJECTION_MATRIX);
    	viewMatrixLocation = shaderProgram.getUniformLocation(VIEW_MATRIX);
    	
    	// Detach and tear down the shaders once we have set the program up
    	shaderProgram.detachShader(fragmentShader)
					 .detachShader(vertexShader);
		fragmentShader.tearDown();
		vertexShader.tearDown();
    	
    	shaderProgram.validate();
	}
	
	private void setupCamera() {
		camera = new PatCamera.Builder()
			.setAspectRatio((float) WIDTH / (float) HEIGHT)
			.setFieldOfView(60)
			.build();
		camera.applyPerspectiveMatrix(projectionMatrixLocation);
		camera.applyOptimalStates();
		Mouse.setGrabbed(true);
	}
	
	private void setupTextures() {
		try {
			texture = TextureLoader.getTexture("PNG", ResourceLoader.getResourceAsStream("res/terrain.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void setupCube() {
		MahTexturedCube daCube = new MahTexturedCube(3);
		indicesCount = daCube.getIndicesCount();
		
		vao = glGenVertexArrays();
		glBindVertexArray(vao);
		vbo = glGenBuffers();
		glBindBuffer(GL_ARRAY_BUFFER_ARB, vbo);
		glBufferData(GL_ARRAY_BUFFER_ARB, daCube.getVerticesFloatBuffer(), GL_STREAM_DRAW_ARB);
		
		shaderProgram.setAttributeData(VERT_IN_POSITION, VertexData.positionElementCount, GL_FLOAT, false, VertexData.stride, VertexData.positionByteOffset)
					 .setAttributeData(VERT_IN_COLOR, VertexData.colorElementCount, GL_FLOAT, false, VertexData.stride, VertexData.colorByteOffset)
					 .setAttributeData(VERT_IN_TEX_COORD, VertexData.textureElementCount, GL_FLOAT, false, VertexData.stride, VertexData.textureByteOffset);
		
		ibo = glGenBuffers();
		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER_ARB, ibo);
		glBufferData(GL_ELEMENT_ARRAY_BUFFER_ARB, daCube.getIndicesByteBuffer(), GL_STATIC_DRAW_ARB);
		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER_ARB, 0);
		
		glBindBuffer(GL_ARRAY_BUFFER_ARB, 0);
		glBindVertexArray(0);
	}
	
	private void loopCycle(int delta) {
		this.logicCycle(delta);
		this.renderCycle();
	}
	
	private void logicCycle(int delta) {
		shaderProgram.activate();
		// Handle camera
		camera.setAspectRatio((float) Display.getWidth() / (float) Display.getHeight());
		if (Display.wasResized()) {
			camera.applyPerspectiveMatrix(projectionMatrixLocation);
		}
		while (Keyboard.next()) {
            if (Keyboard.isKeyDown(Keyboard.KEY_ESCAPE)) {
                Mouse.setGrabbed(false);
            }
        }
//		System.out.println(camera);
		camera.applyTranslations(viewMatrixLocation);
		if (Mouse.isGrabbed()) {
			if (delta <= 0)
				delta = 1;
			camera.processMouse();
			camera.processKeyboard((float) delta);
		}
		shaderProgram.deactivate();
	}
	
	private void renderCycle() {
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
		texture.bind();
		
		shaderProgram.activate();
		glBindVertexArray(vao);
		shaderProgram.enableAttribute(VERT_IN_POSITION)
					 .enableAttribute(VERT_IN_COLOR)
					 .enableAttribute(VERT_IN_TEX_COORD);
		
		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER_ARB, ibo);
		glDrawElements(GL_TRIANGLES, indicesCount, GL_UNSIGNED_BYTE, 0);
		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER_ARB, 0);
		
		shaderProgram.disableAttribute(VERT_IN_POSITION)
		 			 .disableAttribute(VERT_IN_COLOR)
		 			 .disableAttribute(VERT_IN_TEX_COORD);
		glBindVertexArray(0);
		shaderProgram.deactivate();
	}
	
	private void destroyOpenGL() {
		Display.destroy();
	}
	
	public static void main(String[] argv) {
    	new ObjectLoader();
    }
}
