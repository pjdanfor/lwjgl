package com.deeter.utils;

import org.lwjgl.util.vector.Matrix4f;

public class MatrixUtils {
	
	private final static double PI = 3.14159265358979323846;
	
	public static Matrix4f createProjectionMatrix(float fov, int width, int height, float nearPlane, float farPlane) {
		Matrix4f projectionMatrix = new Matrix4f();
		float aspectRatio = (float)width / (float)height;
		float yScale = MatrixUtils.coTangent(MatrixUtils.degreesToRadians(fov / 2f));
		float xScale = yScale / aspectRatio;
		float frustrumLength = farPlane - nearPlane;
		
		projectionMatrix.m00 = xScale;
		projectionMatrix.m11 = yScale;
		projectionMatrix.m22 = -((farPlane + nearPlane) / frustrumLength);
		projectionMatrix.m23 = -1;
		projectionMatrix.m32 = -((2 * nearPlane * farPlane) / frustrumLength);
		
		return projectionMatrix;
	}
	
	public static float coTangent(float angle) {
		return (float)(1f / Math.tan(angle));
	}

	public static float degreesToRadians(float degrees) {
		return degrees * (float)(PI / 180d);
	}
}
