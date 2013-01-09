package com.deeter;

import java.util.ArrayList;

import org.lwjgl.Sys;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;

import com.deeter.obj.builder.Build;
import com.deeter.obj.builder.BuildHelper;
import com.deeter.obj.builder.Face;
import com.deeter.obj.parser.Parse;
import com.deeter.utility.Scene;
import com.deeter.utility.VBO;
import com.deeter.utility.VBOFactory;

public class ObjectLoaderTest {

    public final String WINDOW_TITLE = "Test OBJ loader";
    /** Desired frame time */
    private final int FRAMERATE = 60;
    private boolean finished;
    private String filename = "res/goblin.obj";
    private String defaultTextureMaterial = "";
    boolean fullscreen = false;
    private PatCamera camera;
    private Scene scene;
    
    public ObjectLoaderTest() {
        try {
        	this.setupOpenGL(fullscreen);
            this.setupCamera();
            this.setupScene();
            this.gameLoop(filename, defaultTextureMaterial);
        } catch (Exception e) {
            e.printStackTrace(System.err);
            Sys.alert(WINDOW_TITLE, "An error occured and the program will exit.");
        } finally {
            this.cleanup();
        }
        System.exit(0);
    }

    private void setupOpenGL(boolean fullscreen) throws Exception {
        Display.setTitle(WINDOW_TITLE);
        Display.setFullscreen(fullscreen);
        Display.setVSyncEnabled(true);
        Display.create();

        GL11.glMatrixMode(GL11.GL_MODELVIEW);
        GL11.glLoadIdentity();
        GL11.glViewport(0, 0, Display.getDisplayMode().getWidth(), Display.getDisplayMode().getHeight());
    }
    
    private void setupCamera() {
		camera = new PatCamera.Builder()
			.setAspectRatio((float) Display.getDisplayMode().getWidth() / (float) Display.getDisplayMode().getHeight())
			.setFieldOfView(60)
			.build();
		camera.applyPerspectiveMatrix();
		camera.applyOptimalStates();
		Mouse.setGrabbed(true);
	}
    
    private void setupScene() {
    	scene = new Scene();

        System.err.println("Parsing WaveFront OBJ file");
        Build builder = new Build();
        Parse obj = null;
        try {
            obj = new Parse(builder, filename);
        } catch (java.io.FileNotFoundException e) {
            System.err.println("Exception loading object!  e=" + e);
            e.printStackTrace();
        } catch (java.io.IOException e) {
            System.err.println("Exception loading object!  e=" + e);
            e.printStackTrace();
        }
        System.err.println("Done parsing WaveFront OBJ file");

        System.err.println("Splitting OBJ file faces into list of faces per material");
        ArrayList<ArrayList<Face>> facesByTextureList = BuildHelper.createFaceListsByMaterial(builder);
        System.err.println("Done splitting OBJ file faces into list of faces per material, ended up with " + facesByTextureList.size() + " lists of faces.");

        System.err.println("Loading default texture =" + defaultTextureMaterial);
        int defaultTextureID = BuildHelper.setUpDefaultTexture(defaultTextureMaterial);
        System.err.println("Done loading default texture =" + defaultTextureMaterial);

        int currentTextureID = -1;
        for (ArrayList<Face> faceList : facesByTextureList) {
            if (faceList.isEmpty()) {
                System.err.println("ERROR: got an empty face list.  That shouldn't be possible.");
                continue;
            }
            System.err.println("Getting material " + faceList.get(0).material);
            currentTextureID = BuildHelper.getMaterialID(faceList.get(0).material, defaultTextureID, builder);
            System.err.println("Splitting any quads and throwing any faces with > 4 vertices.");
            ArrayList<Face> triangleList = BuildHelper.splitQuads(faceList);
            System.err.println("Calculating any missing vertex normals.");
            BuildHelper.calcMissingVertexNormals(triangleList);
            System.err.println("Ready to build VBO of " + triangleList.size() + " triangles");;

            if (triangleList.size() <= 0) {
                continue;
            }
            System.err.println("Building VBO");

            VBO vbo = VBOFactory.build(currentTextureID, triangleList);

            System.err.println("Adding VBO with text id " + currentTextureID + ", with " + triangleList.size() + " triangles to scene.");
            scene.addVBO(vbo);
        }
    }

    private void gameLoop(String filename, String defaultTextureMaterial) {
        while (!finished) {
            GL11.glLoadIdentity();
            
            if (Display.isCloseRequested()) {
                finished = true;
            } // The window is in the foreground, so render!
            else if (Display.isActive()) {
            	this.logic();
                this.render();
                Display.sync(FRAMERATE);
            } // The window is not in the foreground, so we can allow other stuff to run and infrequently update
            else {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                }
                this.logic();

                // Only bother rendering if the window is visible or dirty
                if (Display.isVisible() || Display.isDirty()) {
                    System.err.print(".");
                    this.render();
                }
            }
            
            Display.update();
        }
    }

    private void cleanup() {
        Display.destroy();
    }

    private void logic() {
    	if (Keyboard.isKeyDown(Keyboard.KEY_ESCAPE)) {
    		finished = true;
    	}
    	camera.applyTranslations();
        if (Mouse.isGrabbed()) {
			camera.processMouse();
			camera.processKeyboard(16, 20.0f);
		}
    }
    
    private void render() {
    	GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_STENCIL_BUFFER_BIT);
        GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_LINE);
        
        GL11.glColor3f(0.49f, 0.2f, 0.043f);
        for (int i = 0; i < 10; i++) {
        	scene.render();
            GL11.glTranslatef(100.0f, 0, 0);
        }
    }
    
    public static void main(String[] args) {
    	new ObjectLoaderTest();
    }
}
