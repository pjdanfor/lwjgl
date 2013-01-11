package com.deeter.utility;

import static org.lwjgl.opengl.GL15.*;

import com.deeter.obj.builder.FaceVertex;
import com.deeter.obj.builder.Face;

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
                    verticeAttributes.put(1.0f);
                    verticeAttributes.put(1.0f);
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

        return new VBO(triangles.get(0).material, textureID, vertexAttributesVBO, indicesVBO, indicesCount);
    }
}
