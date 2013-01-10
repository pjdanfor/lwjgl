package com.deeter.utility;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.util.vector.Vector4f;

import com.deeter.obj.builder.Material;
import com.deeter.obj.builder.ReflectivityTransmiss;
import com.deeter.shader.ShaderProgram;

import static org.lwjgl.opengl.ARBVertexBufferObject.GL_ARRAY_BUFFER_ARB;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;

public class VBO {
    
    // The amount of bytes an element has
 	public static final int elementBytes = 4;
 	
 	// Elements per parameter
 	public static final int positionElementCount = 3;
 	public static final int normalElementCount = 3;
 	public static final int textureElementCount = 2;
 	
 	// Bytes per parameter
 	public static final int positionByteCount = positionElementCount * elementBytes;
 	public static final int normalByteCount = normalElementCount * elementBytes;
 	public static final int textureByteCount = textureElementCount * elementBytes;
 	
 	// Byte offsets per parameter
 	public static final int positionByteOffset = 0;
 	public static final int normalByteOffset = positionByteOffset + positionByteCount;
 	public static final int textureByteOffset = normalByteOffset + normalByteCount;
 	
 	// The amount of elements that a vertex has
 	public static final int elementCount = positionElementCount + normalElementCount + textureElementCount;	
 	// The size of a vertex in bytes, like in C/C++: sizeof(Vertex)
 	public static final int stride = positionByteCount + normalByteCount + textureByteCount;
 	
 	public static final int materialElementBytes = 4;
 	
 	public static final int ambientElementCount = 4;
 	public static final int diffuseElementCount = 4;
 	public static final int specularElementCount = 4;
 	public static final int shininessElementCount = 1;
 	
 	public static final int ambientByteCount = ambientElementCount * materialElementBytes;
 	public static final int diffuseByteCount = diffuseElementCount * materialElementBytes;
 	public static final int specularByteCount = specularElementCount * materialElementBytes;
 	public static final int shininessByteCount = shininessElementCount * materialElementBytes;
 	
 	public static final int ambientByteOffset = 0;
 	public static final int diffuseByteOffset = ambientByteOffset + ambientByteCount;
 	public static final int specularByteOffset = diffuseByteOffset + diffuseByteCount;
 	public static final int shininessByteOffset = specularByteOffset + specularByteCount;
 	
 	public static final int materialElementCount = ambientElementCount 
 			+ diffuseElementCount + specularElementCount + shininessElementCount;
 	public static final int materialStride = ambientByteCount
 			+ diffuseByteCount + specularByteCount + shininessByteCount;
 	

    private int textId = 0;
    private int verticeAttributesID = 0;
    private int indicesID = 0;
    private int indicesCount = 0;
    private Material material;

    public VBO(Material material, int textId, int verticeAttributesID, int indicesID, int indicesCount) {
        this.textId = textId;
        this.setVerticeAttributesID(verticeAttributesID);
        this.indicesID = indicesID;
        this.indicesCount = indicesCount;
        this.material = material;
    }

    public void render(ShaderProgram shaderProgram) {
        if (shaderProgram == null) {
        	this.renderDeprecated();
        }
        else {
        	this.renderWithShader(shaderProgram);
        }
    }
    
    private void renderWithShader(ShaderProgram shaderProgram) {
    	this.sendMaterialUniforms(shaderProgram);
    	glBindBuffer(GL_ARRAY_BUFFER_ARB, getVerticeAttributesID());
    	glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, indicesID);
		shaderProgram.enableAttributes();
		shaderProgram.setAttributeData(ShaderProgram.VERTEX_POSITION, VBO.positionElementCount, GL_FLOAT, false, VBO.stride, VBO.positionByteOffset)
		 			 .setAttributeData(ShaderProgram.VERTEX_NORMAL, VBO.normalElementCount, GL_FLOAT, false, VBO.stride, VBO.normalByteOffset)
		 			 .setAttributeData(ShaderProgram.VERTEX_TEXTURE, VBO.textureElementCount, GL_FLOAT, false, VBO.stride, VBO.textureByteOffset);
    	glDrawElements(GL_TRIANGLES, indicesCount, GL_UNSIGNED_INT, 0);
    	shaderProgram.disableAttributes();
    	glBindBuffer(GL_ARRAY_BUFFER_ARB, 0);
    	glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);
    }
    
    private void renderDeprecated() {
    	glEnable(GL_TEXTURE_2D);
        glBindTexture(GL_TEXTURE_2D, textId);

        glBindBuffer(GL_ARRAY_BUFFER, getVerticeAttributesID());

        glEnableClientState(GL_VERTEX_ARRAY);
        glVertexPointer(3, GL_FLOAT, stride, positionByteOffset);

        glEnableClientState(GL_NORMAL_ARRAY);
        glNormalPointer(GL_FLOAT, stride, normalByteOffset);

        glEnableClientState(GL_TEXTURE_COORD_ARRAY);
        glTexCoordPointer(2, GL_FLOAT, stride, textureByteOffset);

        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, indicesID);
        glDrawElements(GL_TRIANGLES, indicesCount, GL_UNSIGNED_INT, 0);

        glDisableClientState(GL_VERTEX_ARRAY);
        glDisableClientState(GL_NORMAL_ARRAY);
        glDisableClientState(GL_TEXTURE_COORD_ARRAY);
        glDisable(GL_TEXTURE_2D);
    }
    
    private void sendMaterialUniforms(ShaderProgram shaderProgram) {
    	float shininess;
    	Vector4f ambientVector;
    	Vector4f diffuseVector;
    	Vector4f specularVector;
    	
    	if (material != null) {
    		// Ambient
    		ReflectivityTransmiss kA = material.ka;
    		ambientVector = new Vector4f((float) kA.rx, (float) kA.gy, (float) kA.bz, 1);
    		// Diffuse
    		ReflectivityTransmiss kD = material.kd;
    		diffuseVector = new Vector4f((float) kD.rx, (float) kD.gy, (float) kD.bz, 1);
    		// Specular
    		ReflectivityTransmiss kS = material.ks;
    		specularVector = new Vector4f((float) kS.rx, (float) kS.gy, (float) kS.bz, 1);
    		// Shininess
    		shininess = (float) material.nsExponent;
    		if (shininess <= 0) {
    			shininess = 25;
    		}
    	}
    	else {
    		// Ambient
    		ambientVector = new Vector4f(0, 0, 0, 0);
    		// Diffuse
    		diffuseVector = new Vector4f(0.1f, 0.5f, 0.8f, 1);
    		// Specular
    		specularVector = new Vector4f(1, 1, 1, 1);
    		// Shininess
    		shininess = 20;
    	}
    	
    	int ambientLightLocation = shaderProgram.getUniformLocation(ShaderProgram.AMBIENT_LIGHT);
    	glUniform4f(ambientLightLocation, ambientVector.getX(), ambientVector.getY(), ambientVector.getZ(), ambientVector.getW());
    	int diffuseLightLocation = shaderProgram.getUniformLocation(ShaderProgram.DIFFUSE_LIGHT);
    	glUniform4f(diffuseLightLocation, diffuseVector.getX(), diffuseVector.getY(), diffuseVector.getZ(), diffuseVector.getW());
    	int specularLightLocation = shaderProgram.getUniformLocation(ShaderProgram.SPECULAR_LIGHT);
    	glUniform4f(specularLightLocation, specularVector.getX(), specularVector.getY(), specularVector.getZ(), specularVector.getW());
    	int shininessLocation = shaderProgram.getUniformLocation(ShaderProgram.SHININESS);
    	glUniform1f(shininessLocation, shininess);
    }

    public void destroy() {
        IntBuffer ib = BufferUtils.createIntBuffer(1);
        ib.reset();
        ib.put(getVerticeAttributesID());
        glDeleteBuffers(ib);
        ib.reset();
        ib.put(indicesID);
        glDeleteBuffers(ib);
    }

	public int getVerticeAttributesID() {
		return verticeAttributesID;
	}

	public void setVerticeAttributesID(int verticeAttributesID) {
		this.verticeAttributesID = verticeAttributesID;
	}
}
