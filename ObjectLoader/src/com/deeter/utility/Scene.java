package com.deeter.utility;

import java.util.*;

import com.deeter.shader.ShaderProgram;

public class Scene {

    ArrayList<VBO> vboList;

    public Scene() {
    	vboList = new ArrayList<VBO>();
    }

    public void addVBO(VBO r) {
        vboList.add(r);
    }

    public void render(ShaderProgram shaderProgram) {
        for (int i = 0; i < vboList.size(); i++) {
            vboList.get(i).render(shaderProgram);
        }
    }
}
