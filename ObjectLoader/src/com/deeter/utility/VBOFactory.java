package com.deeter.utility;

import static org.lwjgl.opengl.GL15.*;

import com.deeter.obj.builder.FaceVertex;
import com.deeter.obj.builder.Face;
import com.deeter.obj.builder.Material;
import com.deeter.obj.builder.ReflectivityTransmiss;

import java.util.*;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import org.lwjgl.BufferUtils;

public class VBOFactory {

    public static VBO build(int textureID, ArrayList<Face> triangles, float vertexOffsetX, float vertexOffsetY, float vertexOffsetZ) {
        if (triangles.size() <= 0) {
            throw new RuntimeException("Can not build a VBO if we have no triangles with which to build it.");
        }
        
        int materialVBO = VBOFactory.handleMaterialInformation(triangles.get(0).material);

        // Now sort out the triangle/vertex indices, so we can use a
        // VertexArray in our VBO.  Note the following is NOT the most efficient way
        // to do this, but hopefully it is clear.  

        // First build a map of the unique FaceVertex objects, since Faces may share FaceVertex objects.
        // And while we're at it, assign each unique FaceVertex object an index as we run across them, storing
        // this index in the map, for use later when we build the "index" buffer that refers to the vertice buffer.
        // And lastly, keep a list of the unique vertice objects, in the order that we find them in.  
        HashMap<FaceVertex, Integer> indexMap = new HashMap<FaceVertex, Integer>();
        int nextVertexIndex = 0;
        ArrayList<FaceVertex> faceVertexList = new ArrayList<FaceVertex>();
        for (Face face : triangles) {
            for (FaceVertex vertex : face.vertices) {
                if (!indexMap.containsKey(vertex)) {
                    indexMap.put(vertex, nextVertexIndex++);
                    faceVertexList.add(vertex);
                }
            }
        }

        // Now build the buffers for the VBO/IBO
        int verticeAttributesCount = nextVertexIndex;
        int indicesCount = triangles.size() * 3;

        int numMissingNormals = 0;
        int numMissingUV = 0;
        FloatBuffer verticeAttributes;
        System.err.println("VBOFactory.build: Creating buffer of size " + verticeAttributesCount + " vertices at " + VBO.elementCount + " floats per vertice for a total of " + (verticeAttributesCount * VBO.elementCount) + " floats.");
        verticeAttributes = BufferUtils.createFloatBuffer(verticeAttributesCount * VBO.elementCount);
        if (null == verticeAttributes) {
            System.err.println("VBOFactory.build: ERROR Unable to allocate verticeAttributes buffer of size " + (verticeAttributesCount * VBO.elementCount) + " floats.");
        }
        for (FaceVertex vertex : faceVertexList) {
        	float xVertex = vertex.v.x + vertexOffsetX;
        	float yVertex = vertex.v.y + vertexOffsetY;
        	float zVertex = vertex.v.z + vertexOffsetZ;
            verticeAttributes.put(xVertex);
            verticeAttributes.put(yVertex);
            verticeAttributes.put(zVertex);
            if (vertex.n == null) {
                verticeAttributes.put(1.0f);
                verticeAttributes.put(1.0f);
                verticeAttributes.put(1.0f);
                numMissingNormals++;
            } else {
                verticeAttributes.put(vertex.n.x);
                verticeAttributes.put(vertex.n.y);
                verticeAttributes.put(vertex.n.z);
            } 
            if (vertex.t == null) {
                    verticeAttributes.put((float)Math.random());
                    verticeAttributes.put((float)Math.random());
                numMissingUV++;
            } else {
                verticeAttributes.put(vertex.t.u);
                verticeAttributes.put(vertex.t.v);
            }
        }
        verticeAttributes.flip();

        System.err.println("Had " + numMissingNormals + " missing normals and " + numMissingUV + " missing UV coords");

        IntBuffer indices;    // indices into the vertices, to specify triangles.
        indices = BufferUtils.createIntBuffer(indicesCount);
        for (Face face : triangles) {
            for (FaceVertex vertex : face.vertices) {
                int index = indexMap.get(vertex);
                indices.put(index);
            }
        }
        indices.flip();

        int vertexAttributesVBO = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vertexAttributesVBO);
        glBufferData(GL_ARRAY_BUFFER, verticeAttributes, GL_STATIC_DRAW);

        int indicesVBO = glGenBuffers();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, indicesVBO);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, indices, GL_STATIC_DRAW);
        
        verticeAttributes = null;
        indices = null;

        return new VBO(textureID, vertexAttributesVBO, indicesVBO, materialVBO, indicesCount);
    }
    
    public static int handleMaterialInformation(Material material) {
    	// Ambient, then diffuse, specular, then shininess lighting information
    	FloatBuffer materialData = BufferUtils.createFloatBuffer(13);
    	int materialVBO = glGenBuffers();
    	if (material != null) {
    		// Ambient
    		ReflectivityTransmiss kA = material.ka;
    		materialData.put((float) kA.rx).put((float) kA.gy).put((float) kA.bz).put(1.0f);
    		// Diffuse
    		ReflectivityTransmiss kD = material.kd;
    		materialData.put((float) kD.rx).put((float) kD.gy).put((float) kD.bz).put(1.0f);
    		// Specular
    		ReflectivityTransmiss kS = material.ks;
    		materialData.put((float) kS.rx).put((float) kS.gy).put((float) kS.bz).put(1.0f);
    		// Shininess
    		float shininess = (float) material.nsExponent;
    		if (shininess <= 0) {
    			System.out.println("No shininess value for " + material.name);
    			shininess = 80;
    		}
    		materialData.put(shininess);
    		System.out.println("kA " + (float)material.ka.rx + "," + (float)material.ka.gy + "," + (float)material.ka.bz);
    		System.out.println("kA " + (float)material.kd.rx + "," + (float)material.kd.gy + "," + (float)material.kd.bz);
    		System.out.println("kA " + (float)material.ks.rx + "," + (float)material.ks.gy + "," + (float)material.ks.bz);
    		System.out.println("Shininess: " + material.nsExponent);
    	}
    	else {
    		// Ambient
    		materialData.put(0.7f).put(0.7f).put(0.7f).put(1.0f);
    		// Diffuse
    		materialData.put(0.1f).put(0.5f).put(0.8f).put(1.0f);
    		// Specular
    		materialData.put(1.0f).put(1.0f).put(1.0f).put(1.0f);
    		// Shininess
    		materialData.put(100f);
    	}
    	
    	materialData.flip();
    	glBindBuffer(GL_ARRAY_BUFFER, materialVBO);
    	glBufferData(GL_ARRAY_BUFFER, materialData, GL_STATIC_DRAW);
    	
    	return materialVBO;
    }
}
