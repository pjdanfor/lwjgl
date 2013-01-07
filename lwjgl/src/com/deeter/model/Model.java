package com.deeter.model;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.util.vector.Vector3f;

public class Model {
	public List<Vector3f> vertices = new ArrayList<Vector3f>();
	public List<Vector3f> normals = new ArrayList<Vector3f>();
	public List<Face> faces = new ArrayList<Face>();
	
	public Model() {
		
	}
	
	public void addVertex(Vector3f vertex) {
		this.vertices.add(vertex);
	}
	
	public void addVertices(List<Vector3f> vertices) {
		this.vertices.addAll(vertices);
	}
	
	public void addNormal(Vector3f normal) {
		this.vertices.add(normal);
	}
	
	public void addNormals(List<Vector3f> normals) {
		this.normals.addAll(normals);
	}
	
	public void addFace(Face face) {
		this.faces.add(face);
	}
	
	public void addFaces(List<Face> faces) {
		this.faces.addAll(faces);
	}
}
