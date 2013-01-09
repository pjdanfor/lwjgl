package com.deeter.obj.builder;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureLoader;
import org.newdawn.slick.util.ResourceLoader;

import com.deeter.ObjectLoaderTest;

public class BuildHelper {

	// load and bind the texture we will be using as a default texture for any missing textures, unspecified textures, and/or 
    // any materials that are not textures, since we are pretty much ignoring/not using those non-texture materials.
    //
    // In general in this simple test code we are only using textures, not 'colors' or (so far) any of the other multitude of things that
    // can be specified via 'materials'. 
    public static int setUpDefaultTexture(String defaultTextureMaterial) {
        int defaultTextureID = -1;
        if (defaultTextureMaterial != null && !defaultTextureMaterial.equals("")) {
        	try {
        		String format = BuildHelper.getTextureFormat(defaultTextureMaterial);
        		Texture texture = TextureLoader.getTexture(format, ResourceLoader.getResourceAsStream(defaultTextureMaterial));
                defaultTextureID = texture.getTextureID();
            } catch (IOException ex) {
                Logger.getLogger(ObjectLoaderTest.class.getName()).log(Level.SEVERE, null, ex);
                System.err.println("ERROR: Got an exception trying to load default texture material = " + defaultTextureMaterial + " , ex=" + ex);
                ex.printStackTrace();
            }
            System.err.println("INFO:  default texture ID = " + defaultTextureID);
        }
        return defaultTextureID;
    }

    // Get the specified Material, bind it as a texture, and return the OpenGL ID.  Returns he default texture ID if we can't
    // load the new texture, or if the material is a non texture and hence we ignore it.  
    public static int getMaterialID(Material material, int defaultTextureID, Build builder) {
        int currentTextureID;
        if (material == null) {
            currentTextureID = defaultTextureID;
        } else if (material.mapKdFilename == null) {
            currentTextureID = defaultTextureID;
        } else {
            try {
                File objFile = new File(builder.objFilename);
                File mapKdFile = new File(objFile.getParent(), material.mapKdFilename);
                System.err.println("Trying to load  " + mapKdFile.getAbsolutePath());
                String format = BuildHelper.getTextureFormat(material.mapKdFilename);
                Texture texture = TextureLoader.getTexture(format, ResourceLoader.getResourceAsStream(mapKdFile.getAbsolutePath()));
                currentTextureID = texture.getTextureID();
            } catch (IOException ex) {
                Logger.getLogger(ObjectLoaderTest.class.getName()).log(Level.SEVERE, null, ex);
                System.err.println("ERROR: Got an exception trying to load  texture material = " + material.mapKdFilename + " , ex=" + ex);
                ex.printStackTrace();
                System.err.println("ERROR: Using default texture ID = " + defaultTextureID);
                currentTextureID = defaultTextureID;
            }
        }
        return currentTextureID;
    }
	
	// iterate over face list from builder, and break it up into a set of face lists by material, i.e. each for each face list, all faces in that specific list use the same material
    public static ArrayList<ArrayList<Face>> createFaceListsByMaterial(Build builder) {
        ArrayList<ArrayList<Face>> facesByTextureList = new ArrayList<ArrayList<Face>>();
        Material currentMaterial = null;
        ArrayList<Face> currentFaceList = new ArrayList<Face>();
        for (Face face : builder.faces) {
            if (face.material != currentMaterial) {
                if (!currentFaceList.isEmpty()) {
                    System.err.println("Adding list of " + currentFaceList.size() + " triangle faces with material " + currentMaterial + "  to our list of lists of faces.");
                    facesByTextureList.add(currentFaceList);
                }
                System.err.println("Creating new list of faces for material " + face.material);
                currentMaterial = face.material;
                currentFaceList = new ArrayList<Face>();
            }
            currentFaceList.add(face);
        }
        if (!currentFaceList.isEmpty()) {
            System.err.println("Adding list of " + currentFaceList.size() + " triangle faces with material " + currentMaterial + "  to our list of lists of faces.");
            facesByTextureList.add(currentFaceList);
        }
        return facesByTextureList;
    }
    
	public static ArrayList<Face> splitQuads(ArrayList<Face> faceList) {
		ArrayList<Face> triangleList = new ArrayList<Face>();
        for (Face face : faceList) {
            if (face.vertices.size() == 3) {
                triangleList.add(face);
            } else if (face.vertices.size() == 4) {
                FaceVertex v1 = face.vertices.get(0);
                FaceVertex v2 = face.vertices.get(1);
                FaceVertex v3 = face.vertices.get(2);
                FaceVertex v4 = face.vertices.get(3);
                Face f1 = new Face();
                f1.map = face.map;
                f1.material = face.material;
                f1.add(v1);
                f1.add(v2);
                f1.add(v3);
                triangleList.add(f1);
                Face f2 = new Face();
                f2.map = face.map;
                f2.material = face.material;
                f2.add(v1);
                f2.add(v3);
                f2.add(v4);
                triangleList.add(f2);
            }
        }
        return triangleList;
	}
	
	public static void calcMissingVertexNormals(ArrayList<Face> triangleList) {
        for (Face face : triangleList) {
            face.calculateTriangleNormal();
            for (int loopv = 0; loopv < face.vertices.size(); loopv++) {
                FaceVertex fv = face.vertices.get(loopv);
                if (face.vertices.get(0).n == null) {
                    FaceVertex newFv = new FaceVertex();
                    newFv.v = fv.v;
                    newFv.t = fv.t;
                    newFv.n = face.faceNormal;
                    face.vertices.set(loopv, newFv);
                }
            }
        }
    }
	
	public static String getTextureFormat(String filename) {
		return filename.substring(filename.lastIndexOf(".") + 1, filename.length()).toUpperCase();
	}
}
