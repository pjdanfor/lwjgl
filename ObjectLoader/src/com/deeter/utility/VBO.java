package com.deeter.utility;

import java.nio.IntBuffer;

import org.lwjgl.BufferUtils;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;

public class VBO {

    // sizeof float/sizeof int
    public final static int FL_SIZE = 4;
    public final static int INDICE_SIZE_BYTES = 4;
    // Vertex Attribute Data - i.e. x,y,z then normalx, normaly, normalz, then texture u,v - so 8 floats.
    public final static int ATTR_V_FLOATS_PER = 3;
    public final static int ATTR_N_FLOATS_PER = 3;
    public final static int ATTR_T_FLOATS_PER = 2;
    public final static int ATTR_SZ_FLOATS = ATTR_V_FLOATS_PER + ATTR_N_FLOATS_PER + ATTR_T_FLOATS_PER;
    public final static int ATTR_SZ_BYTES = ATTR_SZ_FLOATS * FL_SIZE;
    public final static int ATTR_V_OFFSET_FLOATS = 0;
    public final static int ATTR_V_OFFSET_BYTES = 0;
    public final static int ATTR_N_OFFSET_FLOATS = ATTR_V_FLOATS_PER;
    public final static int ATTR_N_OFFSET_BYTES = ATTR_N_OFFSET_FLOATS * FL_SIZE;
    public final static int ATTR_T_OFFSET_FLOATS = ATTR_V_FLOATS_PER + ATTR_N_FLOATS_PER;
    public final static int ATTR_T_OFFSET_BYTES = ATTR_T_OFFSET_FLOATS * FL_SIZE;
    public final static int ATTR_V_STRIDE2_BYTES = ATTR_SZ_FLOATS * FL_SIZE;
    public final static int ATTR_N_STRIDE2_BYTES = ATTR_SZ_FLOATS * FL_SIZE;
    public final static int ATTR_T_STRIDE2_BYTES = ATTR_SZ_FLOATS * FL_SIZE;

    private int textId = 0;
    private int verticeAttributesID = 0;
    private int indicesID = 0;
    private int indicesCount = 0;

    public VBO(int textId, int verticeAttributesID, int indicesID, int indicesCount) {
        this.textId = textId;
        this.verticeAttributesID = verticeAttributesID;
        this.indicesID = indicesID;
        this.indicesCount = indicesCount;
    }

    public void render() {
        glEnable(GL_TEXTURE_2D);
        glBindTexture(GL_TEXTURE_2D, textId);

        glBindBuffer(GL_ARRAY_BUFFER, verticeAttributesID);

        glEnableClientState(GL_VERTEX_ARRAY);
        glVertexPointer(3, GL_FLOAT, ATTR_V_STRIDE2_BYTES, ATTR_V_OFFSET_BYTES);

        glEnableClientState(GL_NORMAL_ARRAY);
        glNormalPointer(GL_FLOAT, ATTR_N_STRIDE2_BYTES, ATTR_N_OFFSET_BYTES);

        glEnableClientState(GL_TEXTURE_COORD_ARRAY);
        glTexCoordPointer(2, GL_FLOAT, ATTR_T_STRIDE2_BYTES, ATTR_T_OFFSET_BYTES);

        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, indicesID);
        glDrawElements(GL_TRIANGLES, indicesCount, GL_UNSIGNED_INT, 0);

        glDisableClientState(GL_VERTEX_ARRAY);
        glDisableClientState(GL_NORMAL_ARRAY);
        glDisableClientState(GL_TEXTURE_COORD_ARRAY);
        glDisable(GL_TEXTURE_2D);
    }

    public void destroy() {
        IntBuffer ib = BufferUtils.createIntBuffer(1);
        ib.reset();
        ib.put(verticeAttributesID);
        glDeleteBuffers(ib);
        ib.reset();
        ib.put(indicesID);
        glDeleteBuffers(ib);
    }
}
