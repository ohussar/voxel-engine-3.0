package com.ohussar.VoxelEngine;

import com.nishu.utils.Vector2f;
import com.ohussar.VoxelEngine.Shaders.StaticShader;
import com.ohussar.VoxelEngine.Textures.TextureArray;
import com.ohussar.VoxelEngine.Util.Maths;
import com.ohussar.VoxelEngine.Util.Util;
import com.ohussar.VoxelEngine.World.Chunk;
import org.lwjgl.BufferUtils;
import org.lwjgl.Sys;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

import java.nio.FloatBuffer;
import java.util.Arrays;

import static com.ohussar.VoxelEngine.Entities.Cube.*;
import static com.ohussar.VoxelEngine.Entities.Cube.NX_POS;
import static com.ohussar.VoxelEngine.Entities.Cube.NZ_POS;
import static com.ohussar.VoxelEngine.Entities.Cube.PZ_POS;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.*;
import static org.lwjgl.opengl.GL20.glUniformMatrix4fv;
import static org.lwjgl.opengl.GL43.GL_TEXTURE_2D_ARRAY;

public class Renderer {

    Matrix4f projectionMatrix;
    public static final float FOV = 70f;
    public static final float NEAR_PLANE = 0.1f;
    public static final float FAR_PLANE = 10000f;

    public boolean renderMesh = false;

    public Renderer(StaticShader shader){
        createProjectionMatrix();
        shader.start();
        shader.loadProjectionMatrix(projectionMatrix);
        shader.stop();
    }



    public void prepare() {
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glClearColor(0.4f, 0.7f, 1.0f, 1f);
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
        glActiveTexture(GL13.GL_TEXTURE5);
    }
    public void drawBlockOutline(Vector3f pos){
        Main.StaticShader.stop();
        glColor3f(0.0f, 0.0f, 0.0f);
        glEnable(GL_MULTISAMPLE);
        glMatrixMode(GL_MODELVIEW_MATRIX);
        glLoadIdentity();
        glDisable(GL_DEPTH_TEST);
        FloatBuffer buff1 = BufferUtils.createFloatBuffer(16);
        Matrix4f view = Maths.createViewMatrix(Main.camera);
        view.store(buff1);
        buff1.flip();
        glLoadMatrixf(buff1);
        glMatrixMode(GL_PROJECTION);
        glLoadIdentity();
        FloatBuffer buff = BufferUtils.createFloatBuffer(16);
        projectionMatrix.store(buff);
        buff.flip();
        glLoadMatrixf(buff);
        glPushMatrix();
        glMatrixMode(GL_MODELVIEW);
        glTranslatef(pos.x, pos.y, pos.z);
        glLineWidth(3.0f);
        glScalef(1.005f, 1.005f, 1.005f);
        GL11.glBegin(GL11.GL_LINES);
        GL11.glVertex3f(-0.5f, -0.5f, -0.5f);
        GL11.glVertex3f( 0.5f, -0.5f, -0.5f);

        GL11.glVertex3f( 0.5f, -0.5f, -0.5f);
        GL11.glVertex3f( 0.5f,  0.5f, -0.5f);

        GL11.glVertex3f( 0.5f,  0.5f, -0.5f);
        GL11.glVertex3f(-0.5f,  0.5f, -0.5f);

        GL11.glVertex3f(-0.5f,  0.5f, -0.5f);
        GL11.glVertex3f(-0.5f, -0.5f, -0.5f);

        // --- Top Face (z = 0.5) ---
        GL11.glVertex3f(-0.5f, -0.5f, 0.5f);
        GL11.glVertex3f( 0.5f, -0.5f, 0.5f);

        GL11.glVertex3f( 0.5f, -0.5f, 0.5f);
        GL11.glVertex3f( 0.5f,  0.5f, 0.5f);

        GL11.glVertex3f( 0.5f,  0.5f, 0.5f);
        GL11.glVertex3f(-0.5f,  0.5f, 0.5f);

        GL11.glVertex3f(-0.5f,  0.5f, 0.5f);
        GL11.glVertex3f(-0.5f, -0.5f, 0.5f);

        // --- Vertical Pillars (Connecting Bottom to Top) ---
        GL11.glVertex3f(-0.5f, -0.5f, -0.5f); // Bottom-Back-Left
        GL11.glVertex3f(-0.5f, -0.5f,  0.5f); // Top-Back-Left

        GL11.glVertex3f( 0.5f, -0.5f, -0.5f); // Bottom-Back-Right
        GL11.glVertex3f( 0.5f, -0.5f,  0.5f); // Top-Back-Right

        GL11.glVertex3f( 0.5f,  0.5f, -0.5f); // Bottom-Front-Right
        GL11.glVertex3f( 0.5f,  0.5f,  0.5f); // Top-Front-Right

        GL11.glVertex3f(-0.5f,  0.5f, -0.5f); // Bottom-Front-Left
        GL11.glVertex3f(-0.5f,  0.5f,  0.5f); // Top-Front-Left
        glEnd();
        glDisable(GL_MULTISAMPLE);
        glMatrixMode(GL_PROJECTION);
        glLoadIdentity();
        glMatrixMode(GL_MODELVIEW);
        glLoadIdentity();
        glMatrixMode(GL_MODELVIEW_MATRIX);
        glLoadIdentity();
        glEnable(GL_DEPTH_TEST);
        Main.StaticShader.start();

    }


    public void renderChunk(Chunk chunk, StaticShader shader, Matrix4f toShadowMapSpace){
        //shader.loadToShadowMapSpaceMatrix(toShadowMapSpace);
        if(chunk == null) return;
        GL11.glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        if(renderMesh) {
            glPolygonMode(GL_FRONT_AND_BACK, GL_LINE);
        }else{
            glPolygonMode(GL_FRONT_AND_BACK, GL_FILL);
        }
        glActiveTexture(GL_TEXTURE0);
        GL11.glBindTexture(GL30.GL_TEXTURE_2D_ARRAY, TextureArray.worldId);
        glTexParameteri(GL_TEXTURE_2D_ARRAY, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D_ARRAY, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D_ARRAY, GL_TEXTURE_WRAP_S, GL_REPEAT);
        glTexParameteri(GL_TEXTURE_2D_ARRAY, GL_TEXTURE_WRAP_T, GL_REPEAT);
        Vector3f p = new Vector3f(chunk.getPosition().x*16f -0.5f, chunk.getPosition().y*16-0.5f, chunk.getPosition().z*16f -0.5f);
        Matrix4f transformMatrix = Maths.createTransformationMatrix(p, Util.EmptyVec3(), 1f);
        shader.loadTransformationMatrix(transformMatrix);
        GL30.glBindVertexArray(chunk.meshData.VAO);
        GL20.glEnableVertexAttribArray(0);
        GL11.glDrawArrays(GL11.GL_TRIANGLES, 0,chunk.meshData.verticesCount);
        GL20.glDisableVertexAttribArray(0);
        GL30.glBindVertexArray(0);
    }


    public void createProjectionMatrix(){

        projectionMatrix = new Matrix4f();



        float ratio = (float)Main.config.getWidth() / (float)Main.config.getHeight();
        float yscale = 1f / (float)Math.tan(Math.toRadians(FOV) / 2f);
        float xscale = yscale / ratio;
        float zp = FAR_PLANE + NEAR_PLANE;
        float zm = FAR_PLANE - NEAR_PLANE;

        projectionMatrix.m00 = xscale;
        projectionMatrix.m11 = yscale;
        projectionMatrix.m22 = -zp/zm;
        projectionMatrix.m32 = -(2*FAR_PLANE*NEAR_PLANE)/zm;
        projectionMatrix.m23 = -1;
        projectionMatrix.m33 = 0;
    }
    /* projection matrix
        x scale   0         0         0
        0         y scale   0         0
        0         0         -zp/zm    -(2*ZFar*ZNear)/zm
        0         0         -1        0
        aspeactratio = width / height
        yscale = 1/tan(FOV/2)
        xscale = yscale / aspectRatio
        zfar = far plane dist
        znear = near plane dist
        zp = zfar + znear;
        zm = zfar - znear;
     */

}
