package com.deeter;

import static org.lwjgl.opengl.ARBBufferObject.*;
import static org.lwjgl.opengl.ARBVertexBufferObject.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.LWJGLException;
import org.lwjgl.Sys;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.ARBFragmentShader;
import org.lwjgl.opengl.ARBVertexArrayObject;
import org.lwjgl.opengl.ARBVertexShader;
import org.lwjgl.opengl.ContextAttribs;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.PixelFormat;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureLoader;
import org.newdawn.slick.util.ResourceLoader;

import com.deeter.model.Face;
import com.deeter.model.Model;
import com.deeter.model.OBJLoader;
import com.deeter.shader.Shader;
import com.deeter.shader.ShaderProgram;
import com.deeter.utils.MatrixUtils;
import com.deeter.utils.VertexData;

public class ShaderExample {
	
	// Setup variables
	private final String WINDOW_TITLE = "HEYO";
	private final int WIDTH = 1024, HEIGHT = 768;
	private int fps = 0;
	private long lastFrame = 0, lastFPS = 0;
	// Shader variables
	private ShaderProgram shaderProgram;
	private Shader fragmentShader, vertexShader;
	private int posAttrib = 0, colorAttrib = 0, texAttrib = 0;
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
	private int modelMatrixLocation = 0;
	private Matrix4f projectionMatrix = null;
	private Matrix4f viewMatrix = null;
	private Matrix4f modelMatrix = null;
	private Vector3f modelPos = null;
	private Vector3f modelAngle = null;
	private Vector3f modelScale = null;
	private Vector3f cameraPos = null;
	private float cameraZ = -5.0f;
	private FloatBuffer matrix44Buffer = null;
	
	private static int vboVertexHandle = 0;
	private static int vboNormalHandle = 0;
	
	private static Model m;
	
	public ShaderExample() {
		this.setupOpenGL();
		this.setupShaders();
//		this.setupVBOs();
		this.setupQuad();
		this.setupTextures();
		this.setupMatrices();
		this.setupDelta();
		
		while(!Display.isCloseRequested() &&
				!Keyboard.isKeyDown(Keyboard.KEY_ESCAPE)) {
			this.loopCycle(getDelta());
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
	
	private void setupDelta() {
		getDelta();
		lastFPS = getTime();
	}
	
	private void setupMatrices() {
		projectionMatrix = MatrixUtils.createProjectionMatrix(60f, WIDTH, HEIGHT, 0.1f, 100.0f);
		viewMatrix = new Matrix4f();
		modelMatrix = new Matrix4f();
		matrix44Buffer = BufferUtils.createFloatBuffer(16);
	}
	
	private void setupTextures() {
		try {
			texture = TextureLoader.getTexture("PNG", ResourceLoader.getResourceAsStream("res/terrain.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void setupVBOs() {
		vboVertexHandle = glGenBuffers();
		vboNormalHandle = glGenBuffers();
		
		m = null;
		try {
			m = OBJLoader.loadModel(new File("res/cube.obj"));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			System.exit(1);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
		
		FloatBuffer vertices = reserveData(m.vertices.size() * 36);
		FloatBuffer normals = reserveData(m.normals.size() * 36);
		
		for (Face face : m.faces) {
			vertices.put(asFloats(m.vertices.get((int) (face.vertex.x - 1))));
			vertices.put(asFloats(m.vertices.get((int) (face.vertex.y - 1))));
			vertices.put(asFloats(m.vertices.get((int) (face.vertex.z - 1))));
			normals.put(asFloats(m.normals.get((int) (face.normal.x - 1))));
			normals.put(asFloats(m.normals.get((int) (face.normal.y - 1))));
			normals.put(asFloats(m.normals.get((int) (face.normal.z - 1))));
		}
		
		vertices.flip();
		normals.flip();
		
		glBindBuffer(GL_ARRAY_BUFFER, vboVertexHandle);
		glBufferData(GL_ARRAY_BUFFER, vertices, GL_STATIC_DRAW);
		glBindBuffer(GL_ARRAY_BUFFER, vboNormalHandle);
		glBufferData(GL_ARRAY_BUFFER, normals, GL_STATIC_DRAW);
		glBindBuffer(GL_ARRAY_BUFFER, 0);
		
		this.resetModel();
	}
	
	private void setupQuad() {
		// Front
		VertexData v0 = new VertexData();
		v0.setXYZ(-1.0f, 1.0f, 1.0f); v0.setRGB(1, 0, 0); v0.setST((float)9/16, (float)1/16);
		VertexData v1 = new VertexData();
		v1.setXYZ(1.0f, 1.0f, 1.0f); v1.setRGB(0, 1, 0); v1.setST((float)10/16, (float)1/16);
		VertexData v2 = new VertexData();
		v2.setXYZ(1.0f, -1.0f, 1.0f); v2.setRGB(0, 0, 1); v2.setST((float)10/16, (float)2/16);
		VertexData v3 = new VertexData();
		v3.setXYZ(-1.0f, -1.0f, 1.0f); v3.setRGB(1, 1, 1); v3.setST((float)9/16, (float)2/16);
		// Top
		VertexData v4 = new VertexData();
		v4.setXYZ(-1.0f, 1.0f, -1.0f); v4.setRGB(1, 1, 1); v4.setST((float)9/16, (float)1/16);
		VertexData v5 = new VertexData();
		v5.setXYZ(1.0f, 1.0f, -1.0f); v5.setRGB(0, 1, 0); v5.setST((float)10/16, (float)1/16);
		VertexData v6 = new VertexData();
		v6.setXYZ(1.0f, 1.0f, 1.0f); v6.setRGB(0, 0, 1); v6.setST((float)10/16, (float)2/16);
		VertexData v7 = new VertexData();
		v7.setXYZ(-1.0f, 1.0f, 1.0f); v7.setRGB(1, 1, 1); v7.setST((float)9/16, (float)2/16);
		// Bottom
		VertexData v8 = new VertexData();
		v8.setXYZ(-1.0f, -1.0f, -1.0f); v8.setRGB(1, 1, 1); v8.setST((float)9/16, (float)1/16);
		VertexData v9 = new VertexData();
		v9.setXYZ(1.0f, -1.0f, -1.0f); v9.setRGB(0, 1, 0); v9.setST((float)10/16, (float)1/16);
		VertexData v10 = new VertexData();
		v10.setXYZ(1.0f, -1.0f, 1.0f); v10.setRGB(0, 0, 1); v10.setST((float)10/16, (float)2/16);
		VertexData v11 = new VertexData();
		v11.setXYZ(-1.0f, -1.0f, 1.0f); v11.setRGB(1, 1, 1); v11.setST((float)9/16, (float)2/16);
		// Back
		VertexData v12 = new VertexData();
		v12.setXYZ(-1.0f, 1.0f, -1.0f); v12.setRGB(1, 1, 1); v12.setST((float)9/16, (float)1/16);
		VertexData v13 = new VertexData();
		v13.setXYZ(1.0f, 1.0f, -1.0f); v13.setRGB(0, 1, 0); v13.setST((float)10/16, (float)1/16);
		VertexData v14 = new VertexData();
		v14.setXYZ(1.0f, -1.0f, -1.0f); v14.setRGB(0, 0, 1); v14.setST((float)10/16, (float)2/16);
		VertexData v15 = new VertexData();
		v15.setXYZ(-1.0f, -1.0f, -1.0f); v15.setRGB(1, 1, 1); v15.setST((float)9/16, (float)2/16);
		// Left
		VertexData v16 = new VertexData();
		v16.setXYZ(-1.0f, 1.0f, -1.0f); v16.setRGB(1, 1, 1); v16.setST((float)7/16, (float)7/16);
		VertexData v17 = new VertexData();
		v17.setXYZ(-1.0f, 1.0f, 1.0f); v17.setRGB(0, 1, 0); v17.setST((float)8/16, (float)7/16);
		VertexData v18 = new VertexData();
		v18.setXYZ(-1.0f, -1.0f, 1.0f); v18.setRGB(0, 0, 1); v18.setST((float)8/16, (float)8/16);
		VertexData v19 = new VertexData();
		v19.setXYZ(-1.0f, -1.0f, -1.0f); v19.setRGB(1, 1, 1); v19.setST((float)7/16, (float)8/16);
		// Right
		VertexData v20 = new VertexData();
		v20.setXYZ(1.0f, 1.0f, -1.0f); v20.setRGB(1, 1, 1); v20.setST((float)7/16, (float)7/16);
		VertexData v21 = new VertexData();
		v21.setXYZ(1.0f, 1.0f, 1.0f); v21.setRGB(0, 1, 0); v21.setST((float)8/16, (float)7/16);
		VertexData v22 = new VertexData();
		v22.setXYZ(1.0f, -1.0f, 1.0f); v22.setRGB(0, 0, 1); v22.setST((float)8/16, (float)8/16);
		VertexData v23 = new VertexData();
		v23.setXYZ(1.0f, -1.0f, -1.0f); v23.setRGB(1, 1, 1); v23.setST((float)7/16, (float)8/16);
		
		vertices = new VertexData[] {
				v0,  v1,  v2,  v3,
				v4,  v5,  v6,  v7,
				v8,  v9,  v10, v11,
				v12, v13, v14, v15,
				v16, v17, v18, v19,
				v20, v21, v22, v23
		};
		
		verticesByteBuffer = BufferUtils.createByteBuffer(vertices.length * VertexData.stride);
		FloatBuffer verticesFloatBuffer = verticesByteBuffer.asFloatBuffer();
		for (int i = 0; i < vertices.length; i++) {
			verticesFloatBuffer.put(vertices[i].getElements());
		}
		verticesFloatBuffer.flip();
		
		byte[] indices = {
				0,  1,  2,
				2,  3,  0,
				4,  5,  6,
				6,  7,  4,
				8,  9,  10,
				10, 11, 8,
				12, 13, 14,
				14, 15, 12,
				16, 17, 18,
				18, 19, 16,
				20, 21, 22,
				22, 23, 20
		};
		indicesCount = indices.length;
		ByteBuffer indicesBuffer = BufferUtils.createByteBuffer(indicesCount);
		indicesBuffer.put(indices);
		indicesBuffer.flip();
		
		vao = glGenVertexArrays();
		glBindVertexArray(vao);
		vbo = glGenBuffers();
		glBindBuffer(GL_ARRAY_BUFFER_ARB, vbo);
		glBufferData(GL_ARRAY_BUFFER_ARB, verticesFloatBuffer, GL_STREAM_DRAW_ARB);
		
		posAttrib = shaderProgram.getAttributeLocation("in_Position");
		glVertexAttribPointer(posAttrib, VertexData.positionElementCount, GL_FLOAT, false, VertexData.stride, VertexData.positionByteOffset);
		colorAttrib = shaderProgram.getAttributeLocation("in_Color");
		glVertexAttribPointer(colorAttrib, VertexData.colorElementCount, GL_FLOAT, false, VertexData.stride, VertexData.colorByteOffset);
		texAttrib = shaderProgram.getAttributeLocation("in_TextureCoord");
		glVertexAttribPointer(texAttrib, VertexData.textureElementCount, GL_FLOAT, false, VertexData.stride, VertexData.textureByteOffset);
		
		ibo = glGenBuffers();
		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER_ARB, ibo);
		glBufferData(GL_ELEMENT_ARRAY_BUFFER_ARB, indicesBuffer, GL_STATIC_DRAW_ARB);
		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER_ARB, 0);
		
		glBindBuffer(GL_ARRAY_BUFFER_ARB, 0);
		glBindVertexArray(0);
		
		this.resetModel();
	}
	
	private void setupShaders() {    	
    	try {
            vertexShader = new Shader("shaders/screen.vert", ARBVertexShader.GL_VERTEX_SHADER_ARB);
            fragmentShader = new Shader("shaders/screen.frag", ARBFragmentShader.GL_FRAGMENT_SHADER_ARB);
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
    	shaderProgram.attachShader(vertexShader);
    	shaderProgram.attachShader(fragmentShader);
    	shaderProgram.link();
    	
    	// Bind the fragment data location for variable 'outColor'
    	shaderProgram.bindFragDataLocation(0, "outColor");
		// Pass information from our VBO and VAO to the shader variables
    	shaderProgram.bindAttribLocation(0, "in_Position");
    	shaderProgram.bindAttribLocation(1, "in_Color");
    	shaderProgram.bindAttribLocation(2, "in_TextureCoord");
    	
    	// Get matrices uniform location
    	projectionMatrixLocation = shaderProgram.getUniformLocation("projectionMatrix");
    	viewMatrixLocation = shaderProgram.getUniformLocation("viewMatrix");
    	modelMatrixLocation = shaderProgram.getUniformLocation("modelMatrix");
    	
    	// Detach and tear down the shaders once we have set the program up
    	shaderProgram.detachShader(fragmentShader);
		shaderProgram.detachShader(vertexShader);
		fragmentShader.tearDown();
		vertexShader.tearDown();
    	
    	shaderProgram.validate();
	}
	
	private void destroyOpenGL() {
		texture.release();
		shaderProgram.tearDown();
		glDeleteBuffers(vbo);
		glDeleteBuffers(ibo);
		ARBVertexArrayObject.glDeleteVertexArrays(vao);
		Display.destroy();
	}
	
	private void loopCycle(int delta) {
		this.logicCycle(delta);
		this.renderCycle();
//		this.testRender();
	}
	
	private void logicCycle(int delta) {
		updateFPS();
		float rotationDelta = 0.35f;
		float scaleDelta = 0.005f;
		float posDelta = 0.01f;
		Vector3f scaleAdd = new Vector3f(scaleDelta, scaleDelta, scaleDelta);
		Vector3f scaleMinus = new Vector3f(-scaleDelta, -scaleDelta, -scaleDelta);
		
		if (Keyboard.isKeyDown(Keyboard.KEY_UP)) {
			modelPos.y += posDelta * delta;
		}
		if (Keyboard.isKeyDown(Keyboard.KEY_DOWN)) {
			modelPos.y -= posDelta * delta;
		}
		if (Keyboard.isKeyDown(Keyboard.KEY_LEFT)) {
			modelPos.x -= posDelta * delta;
		}
		if (Keyboard.isKeyDown(Keyboard.KEY_RIGHT)) {
			modelPos.x += posDelta * delta;
		}
		if (Keyboard.isKeyDown(Keyboard.KEY_U)) {
			Vector3f.add(modelScale, scaleAdd, modelScale);
		}
		if (Keyboard.isKeyDown(Keyboard.KEY_J)) {
			Vector3f.add(modelScale, scaleMinus, modelScale);
		}
		if (Keyboard.isKeyDown(Keyboard.KEY_Z)) {
			modelAngle.z += rotationDelta * delta;
		}
		if (Keyboard.isKeyDown(Keyboard.KEY_C)) {
			modelAngle.z -= rotationDelta * delta;
		}
		if (Keyboard.isKeyDown(Keyboard.KEY_Q)) {
			modelAngle.y += rotationDelta * delta;
		}
		if (Keyboard.isKeyDown(Keyboard.KEY_E)) {
			modelAngle.y -= rotationDelta * delta;
		}
		if (Keyboard.isKeyDown(Keyboard.KEY_A)) {
			modelAngle.x += rotationDelta * delta;
		}
		if (Keyboard.isKeyDown(Keyboard.KEY_D)) {
			modelAngle.x -= rotationDelta * delta;
		}
		
		int dWheel = Mouse.getDWheel();
		if (dWheel > 0) {
			cameraZ += 0.5f;
		}
		else if (dWheel < 0) {
			cameraZ -= 0.5f;
		}
		if (cameraZ >= 1.0f) {
			cameraZ = 1.0f;
		}
		
		if (Keyboard.isKeyDown(Keyboard.KEY_SPACE)) {
			this.resetModel();
			cameraZ = -5.0f;
		}
		
		cameraPos = new Vector3f(0, 0, cameraZ);
		viewMatrix = new Matrix4f();
		modelMatrix = new Matrix4f();
		
		Matrix4f.translate(cameraPos, viewMatrix, viewMatrix);
		Matrix4f.scale(modelScale, modelMatrix, modelMatrix);
		Matrix4f.translate(modelPos, modelMatrix, modelMatrix);
		Matrix4f.rotate(MatrixUtils.degreesToRadians(modelAngle.z), new Vector3f(0, 0, 1), modelMatrix, modelMatrix);
		Matrix4f.rotate(MatrixUtils.degreesToRadians(modelAngle.y), new Vector3f(0, 1, 0), modelMatrix, modelMatrix);
		Matrix4f.rotate(MatrixUtils.degreesToRadians(modelAngle.x), new Vector3f(1, 0, 0), modelMatrix, modelMatrix);
		
		shaderProgram.activate();
		projectionMatrix.store(matrix44Buffer); matrix44Buffer.flip();
		glUniformMatrix4(projectionMatrixLocation, false, matrix44Buffer);
		viewMatrix.store(matrix44Buffer); matrix44Buffer.flip();
		glUniformMatrix4(viewMatrixLocation, false, matrix44Buffer);
		modelMatrix.store(matrix44Buffer); matrix44Buffer.flip();
		glUniformMatrix4(modelMatrixLocation, false, matrix44Buffer);
		shaderProgram.deactivate();
	}
	
	private void renderCycle() {
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
//		glPolygonMode(GL_FRONT_AND_BACK, GL_LINE);
		shaderProgram.activate();
		texture.bind();
		
		glBindVertexArray(vao);
		glEnableVertexAttribArray(posAttrib);
		glEnableVertexAttribArray(colorAttrib);
		glEnableVertexAttribArray(texAttrib);
		
		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER_ARB, ibo);
		glDrawElements(GL_TRIANGLES, indicesCount, GL_UNSIGNED_BYTE, 0);
		
		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER_ARB, 0);
		glDisableVertexAttribArray(posAttrib);
		glDisableVertexAttribArray(colorAttrib);
		glDisableVertexAttribArray(texAttrib);
		glBindVertexArray(0);
		shaderProgram.deactivate();
	}
	
	private void testRender() {
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
		shaderProgram.activate();
		glBindVertexArray(vao);
		glEnableVertexAttribArray(posAttrib);
		
		glBindBuffer(GL_ARRAY_BUFFER, vboVertexHandle);
		glDrawArrays(GL_TRIANGLES, 0, m.faces.size() * 3);
		glBindBuffer(GL_ARRAY_BUFFER, 0);
//		glVertexPointer(3, GL_FLOAT, 0, 0L);
//		glBindBuffer(GL_ARRAY_BUFFER, vboNormalHandle);
//		glNormalPointer(GL_FLOAT, 0, 0L);
//		glMaterialf(GL_FRONT, GL_SHININESS, 10f);
		shaderProgram.deactivate();
	}
	
	private void resetModel() {
		modelPos = new Vector3f(0, 0, 0);
		modelAngle = new Vector3f(0, 0, 0);
		modelScale = new Vector3f(1, 1, 1);
	};
	
	public int getDelta() {
	    long time = getTime();
	    int delta = (int) (time - lastFrame);
	    lastFrame = time;
 
	    return delta;
	}
 
	public long getTime() {
	    return (Sys.getTime() * 1000) / Sys.getTimerResolution();
	}
	
	public void updateFPS() {
		if (getTime() - lastFPS > 1000) {
			Display.setTitle(WINDOW_TITLE + " FPS: " + fps);
			fps = 0;
			lastFPS += 1000;
		}
		fps++;
	}
	
	private static float[] asFloats(Vector3f v) {
		return new float[] { v.x, v.y, v.z };
	}
	
	private static FloatBuffer reserveData(int size) {
		FloatBuffer data = BufferUtils.createFloatBuffer(size);
		return data;
	}
	
	public static void main(String[] argv) {
    	new ShaderExample();
    }
}
