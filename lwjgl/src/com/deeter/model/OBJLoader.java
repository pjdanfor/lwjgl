package com.deeter.model;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import org.lwjgl.util.vector.Vector3f;

public class OBJLoader {
	
	@SuppressWarnings("resource")
	public static Model loadModel(File f) throws FileNotFoundException, IOException {
		BufferedReader reader = new BufferedReader(new FileReader(f));
		Model m = new Model();
		String line;
		String[] choppedLine;
		
		while ((line = reader.readLine()) != null) {
			if (line.startsWith("v ")) {
				choppedLine = line.split(" ");
				float x = Float.valueOf(choppedLine[1]);
				float y = Float.valueOf(choppedLine[2]);
				float z = Float.valueOf(choppedLine[3]);
				m.addVertex(new Vector3f(x, y, z));
			}
			else if (line.startsWith("vn ")) {
				choppedLine = line.split(" ");
				float x = Float.valueOf(choppedLine[1]);
				float y = Float.valueOf(choppedLine[2]);
				float z = Float.valueOf(choppedLine[3]);
				m.addNormal(new Vector3f(x, y, z));
			}
			else if (line.startsWith("f ")) {
				choppedLine = line.split(" ");
				String xIndices[] = choppedLine[1].split("/");
				String yIndices[] = choppedLine[2].split("/");
				String zIndices[] = choppedLine[3].split("/");
				Vector3f vertexIndices = new Vector3f(
						Float.valueOf(xIndices[0]),
						Float.valueOf(yIndices[0]),
						Float.valueOf(zIndices[0])
				);
				Vector3f normalIndices = new Vector3f(
						Float.valueOf(xIndices[2]),
						Float.valueOf(yIndices[2]),
						Float.valueOf(zIndices[2])
				);
				m.addFace(new Face(vertexIndices, normalIndices));
			}
		}
		
		return m;
	}
}
