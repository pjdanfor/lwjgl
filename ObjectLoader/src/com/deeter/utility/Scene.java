package com.deeter.utility;

import java.util.*;

public class Scene {

    ArrayList<VBO> vboList;

    public Scene() {
    	vboList = new ArrayList<VBO>();
    }

    public void addVBO(VBO r) {
        vboList.add(r);
    }

    public void render() {
        for (int i = 0; i < vboList.size(); i++) {
            vboList.get(i).render();
        }
    }
}
