package com.deeter.obj.builder;

public class FaceVertex {

    int index = -1;
    public VertexGeometric v = null;
    public VertexColor c = null;
    public VertexTexture t = null;
    public VertexNormal n = null;

    public String toString() {
        return v + "|" + c + "|" + n + "|" + t;
    }
}
