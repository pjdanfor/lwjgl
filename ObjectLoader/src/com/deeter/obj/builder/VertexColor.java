package com.deeter.obj.builder;

public class VertexColor {
    public float r = 0;
    public float g = 0;
    public float b = 0;
    public float a = 0;

    public void add(float r, float g, float b, float a) {
    	this.r += r;
    	this.g += g;
    	this.b += b;
    	this.a += a;
    }

    public VertexColor(float r, float g, float b) {
    	this.r = r;
    	this.g = g;
    	this.b = b;
    	this.a = 1;
    }
    
    public VertexColor(float r, float g, float b, float a) {
    	this(r, g, b);
    	this.a = a;
    }

    public String toString() {
    	if (null == this) {
    		return "null";
    	}
    	else {
    		return r + "," + g + "," + b + "," + a;
    	}
    }
}
