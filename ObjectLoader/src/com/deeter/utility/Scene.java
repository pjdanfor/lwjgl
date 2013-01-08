package com.deeter.utility;

import java.util.*;

public class Scene {

    ArrayList<VBO> vboList = new ArrayList<VBO>();

    public Scene() {
    }

    public void addVBO(VBO r) {
        vboList.add(r);
    }

    public void render() {
        for (int loopi = 0; loopi < vboList.size(); loopi++) {
            vboList.get(loopi).render();
        }
    }
}
