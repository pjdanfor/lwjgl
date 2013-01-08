package com.deeter.obj.builder;

import java.util.*;
import java.text.*;
import java.io.*;
import java.io.IOException;

public class FaceVertex {

    int index = -1;
    public VertexGeometric v = null;
    public VertexTexture t = null;
    public VertexNormal n = null;

    public String toString() {
        return v + "|" + n + "|" + t;
    }
}
