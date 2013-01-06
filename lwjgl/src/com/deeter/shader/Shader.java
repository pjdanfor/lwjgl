package com.deeter.shader;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;

import static org.lwjgl.opengl.ARBShaderObjects.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.*;

public class Shader extends ShaderObject {
	
	public Shader(String path, int type) {
		super(path, type);
	}

	@Override
	public void initialize() {
    	try {
	        setIdentifier(glCreateShaderObjectARB(getType()));
	        
	        if(isLegit()) {
	        	glShaderSourceARB(getIdentifier(), readFileAsString(getPath()));
		        glCompileShaderARB(getIdentifier());
		        
		        if (glGetObjectParameteriARB(getIdentifier(), GL_OBJECT_COMPILE_STATUS_ARB) == GL_FALSE)
		            throw new RuntimeException("Error creating shader: " + getLogInfo(getIdentifier()));
	        }
    	}
    	catch(Exception e) {
    		delete();
    	}
	}
	
	private String readFileAsString(String filename) throws Exception {
        StringBuilder source = new StringBuilder();
        
        FileInputStream in = new FileInputStream(filename);
        
        Exception exception = null;
        
        BufferedReader reader;
        try{
            reader = new BufferedReader(new InputStreamReader(in,"UTF-8"));
            
            Exception innerExc= null;
            try {
            	String line;
                while((line = reader.readLine()) != null)
                    source.append(line).append('\n');
            }
            catch(Exception exc) {
            	exception = exc;
            }
            finally {
            	try {
            		reader.close();
            	}
            	catch(Exception exc) {
            		if(innerExc == null)
            			innerExc = exc;
            		else
            			exc.printStackTrace();
            	}
            }
            
            if(innerExc != null)
            	throw innerExc;
        }
        catch(Exception exc) {
        	exception = exc;
        }
        finally {
        	try {
        		in.close();
        	}
        	catch(Exception exc) {
        		if(exception == null)
        			exception = exc;
        		else
					exc.printStackTrace();
        	}
        	
        	if(exception != null)
        		throw exception;
        }
        return source.toString();
    }

	@Override
	public void delete() {
		glDeleteShader(getIdentifier());
	}

	@Override
	public void tearDown() {
		delete();
	}
}
