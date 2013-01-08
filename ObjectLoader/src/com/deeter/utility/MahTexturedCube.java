package com.deeter.utility;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;

public class MahTexturedCube {

	private VertexData[] vertices;
	private byte[] indices;
	private FloatBuffer verticesFloatBuffer;
	private ByteBuffer indicesByteBuffer;
	
	public MahTexturedCube(int scale) {
		this.setupVertices(scale);
		this.setupIndices();
	}
	
	public VertexData[] getVertices() {
		return vertices;
	}
	
	public byte[] getIndices() {
		return indices;
	}
	
	public FloatBuffer getVerticesFloatBuffer() {
		return verticesFloatBuffer;
	}
	
	public ByteBuffer getIndicesByteBuffer() {
		return indicesByteBuffer;
	}
	
	public int getIndicesCount() {
		return this.indices.length;
	}
	
	private void setupVertices(int scale) {
		float vertex = (float) 1.0/scale;
		// Front
		VertexData v0 = new VertexData();
		v0.setXYZ(-vertex, vertex, vertex); v0.setRGB(1, 0, 0); v0.setST((float)9/16, (float)1/16);
		VertexData v1 = new VertexData();
		v1.setXYZ(vertex, vertex, vertex); v1.setRGB(0, 1, 0); v1.setST((float)10/16, (float)1/16);
		VertexData v2 = new VertexData();
		v2.setXYZ(vertex, -vertex, vertex); v2.setRGB(0, 0, 1); v2.setST((float)10/16, (float)2/16);
		VertexData v3 = new VertexData();
		v3.setXYZ(-vertex, -vertex, vertex); v3.setRGB(1, 1, 1); v3.setST((float)9/16, (float)2/16);
		// Top
		VertexData v4 = new VertexData();
		v4.setXYZ(-vertex, vertex, -vertex); v4.setRGB(1, 1, 1); v4.setST((float)9/16, (float)1/16);
		VertexData v5 = new VertexData();
		v5.setXYZ(vertex, vertex, -vertex); v5.setRGB(0, 1, 0); v5.setST((float)10/16, (float)1/16);
		VertexData v6 = new VertexData();
		v6.setXYZ(vertex, vertex, vertex); v6.setRGB(0, 0, 1); v6.setST((float)10/16, (float)2/16);
		VertexData v7 = new VertexData();
		v7.setXYZ(-vertex, vertex, vertex); v7.setRGB(1, 1, 1); v7.setST((float)9/16, (float)2/16);
		// Bottom
		VertexData v8 = new VertexData();
		v8.setXYZ(-vertex, -vertex, -vertex); v8.setRGB(1, 1, 1); v8.setST((float)9/16, (float)1/16);
		VertexData v9 = new VertexData();
		v9.setXYZ(vertex, -vertex, -vertex); v9.setRGB(0, 1, 0); v9.setST((float)10/16, (float)1/16);
		VertexData v10 = new VertexData();
		v10.setXYZ(vertex, -vertex, vertex); v10.setRGB(0, 0, 1); v10.setST((float)10/16, (float)2/16);
		VertexData v11 = new VertexData();
		v11.setXYZ(-vertex, -vertex, vertex); v11.setRGB(1, 1, 1); v11.setST((float)9/16, (float)2/16);
		// Back
		VertexData v12 = new VertexData();
		v12.setXYZ(-vertex, vertex, -vertex); v12.setRGB(1, 1, 1); v12.setST((float)9/16, (float)1/16);
		VertexData v13 = new VertexData();
		v13.setXYZ(vertex, vertex, -vertex); v13.setRGB(0, 1, 0); v13.setST((float)10/16, (float)1/16);
		VertexData v14 = new VertexData();
		v14.setXYZ(vertex, -vertex, -vertex); v14.setRGB(0, 0, 1); v14.setST((float)10/16, (float)2/16);
		VertexData v15 = new VertexData();
		v15.setXYZ(-vertex, -vertex, -vertex); v15.setRGB(1, 1, 1); v15.setST((float)9/16, (float)2/16);
		// Left
		VertexData v16 = new VertexData();
		v16.setXYZ(-vertex, vertex, -vertex); v16.setRGB(1, 1, 1); v16.setST((float)7/16, (float)7/16);
		VertexData v17 = new VertexData();
		v17.setXYZ(-vertex, vertex, vertex); v17.setRGB(0, 1, 0); v17.setST((float)8/16, (float)7/16);
		VertexData v18 = new VertexData();
		v18.setXYZ(-vertex, -vertex, vertex); v18.setRGB(0, 0, 1); v18.setST((float)8/16, (float)8/16);
		VertexData v19 = new VertexData();
		v19.setXYZ(-vertex, -vertex, -vertex); v19.setRGB(1, 1, 1); v19.setST((float)7/16, (float)8/16);
		// Right
		VertexData v20 = new VertexData();
		v20.setXYZ(vertex, vertex, -vertex); v20.setRGB(1, 1, 1); v20.setST((float)7/16, (float)7/16);
		VertexData v21 = new VertexData();
		v21.setXYZ(vertex, vertex, vertex); v21.setRGB(0, 1, 0); v21.setST((float)8/16, (float)7/16);
		VertexData v22 = new VertexData();
		v22.setXYZ(vertex, -vertex, vertex); v22.setRGB(0, 0, 1); v22.setST((float)8/16, (float)8/16);
		VertexData v23 = new VertexData();
		v23.setXYZ(vertex, -vertex, -vertex); v23.setRGB(1, 1, 1); v23.setST((float)7/16, (float)8/16);
		
		vertices = new VertexData[] {
				v0,  v1,  v2,  v3,
				v4,  v5,  v6,  v7,
				v8,  v9,  v10, v11,
				v12, v13, v14, v15,
				v16, v17, v18, v19,
				v20, v21, v22, v23
		};
		
		ByteBuffer verticesByteBuffer = BufferUtils.createByteBuffer(vertices.length * VertexData.stride);
		verticesFloatBuffer = verticesByteBuffer.asFloatBuffer();
		for (int i = 0; i < vertices.length; i++) {
			verticesFloatBuffer.put(vertices[i].getElements());
		}
		verticesFloatBuffer.flip();
	}
	
	public void setupIndices() {
		indices = new byte[] {
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
		indicesByteBuffer = BufferUtils.createByteBuffer(indices.length);
		indicesByteBuffer.put(indices);
		indicesByteBuffer.flip();
	}
}
