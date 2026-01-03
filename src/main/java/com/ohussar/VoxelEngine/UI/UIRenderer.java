package com.ohussar.VoxelEngine.UI;

import com.ohussar.VoxelEngine.Main;
import com.ohussar.VoxelEngine.Shaders.UIShader;
import com.ohussar.VoxelEngine.Textures.Texture;
import com.ohussar.VoxelEngine.Textures.TextureArray;
import org.lwjgl.BufferUtils;
import org.lwjgl.Sys;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Matrix4f;

import java.nio.IntBuffer;
import java.util.HashMap;
import java.util.Map;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL43.GL_TEXTURE_2D_ARRAY;
public class UIRenderer {
    public UIShader uiShader;
    Matrix4f projectionMatrix;
    public static final float FOV = 70f;
    public static final float NEAR_PLANE = 0.1f;
    public static final float FAR_PLANE = 10000f;

    public static final float scale = 4.0f;
    private static final IntBuffer indexes = BufferUtils.createIntBuffer(6);
    // texture VAO map
    public static final HashMap<String, Integer> VAO_TEXTURES = new HashMap<>();

    public static final float texCoords[] = {
            0, 0,
            1, 0,
            1, 1,
            0, 1,
    };


    public UIRenderer(UIShader shader) {
        uiShader = shader;
        createProjectionMatrix();
        int[] i = { 0, 1, 3,
                1, 2, 3 };
        indexes.put(i);
        indexes.flip();
        Map<String, Texture> uiTextureMap = TextureArray.getUiTextureMap();

        for(Map.Entry<String, Texture> entry : uiTextureMap.entrySet()){
            float x = 0;
            float y = 0;
            float xn = x / Main.config.getWidth() * 2f - 1;
            float yn = y / Main.config.getHeight() * 2f - 1;
            float xn1 = (x+entry.getValue().width*scale) / Main.config.getWidth() * 2f - 1;
            float yn1 = (y+entry.getValue().height*scale) / Main.config.getHeight() * 2f - 1;
            Texture t = entry.getValue();
            float vertices[] = {
                    xn, yn, t.id,
                    xn1, yn, t.id,
                    xn1, yn1, t.id,
                    xn, yn1, t.id,
            };
            int VAO = -1;
            VAO = Main.StaticLoader.updateVAO3D(VAO, vertices, texCoords);
            VAO_TEXTURES.put(entry.getKey(), VAO);
        }

    }
    public static int VAO = -1;


    public void renderSprite(String sprite, float x, float y){
        int VAO = VAO_TEXTURES.get(sprite);
        Texture tex = TextureArray.getUiTextureMap().get(sprite);
        Matrix4f view = createTranslationMatrix(x, y, tex.width, tex.height);
        uiShader.loadTranslationMatrix(view);
        uiShader.loadProjectionMatrix(projectionMatrix);
        glBindVertexArray(VAO);
        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);
        glDrawElements(GL_TRIANGLES, indexes);
        glDisableVertexAttribArray(0);
        glDisableVertexAttribArray(1);
        glBindVertexArray(0);
        glMatrixMode(GL_MODELVIEW);
    }

    public void render(){
        uiShader.start();
        glActiveTexture(GL_TEXTURE0);
        GL11.glBindTexture(GL30.GL_TEXTURE_2D_ARRAY, TextureArray.uiId);
        glTexParameteri(GL_TEXTURE_2D_ARRAY, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D_ARRAY, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
        renderSprite("reference", 0, 0);
        renderSprite("reference2", 16, 0);
        renderSprite("reference3", 32, 0);

        uiShader.stop();
    }




    public Matrix4f createTranslationMatrix(float x, float y, float w, float h){
        Matrix4f translationMatrix = new Matrix4f();
        float xn = x*scale / Main.config.getWidth() * 2;
        float yn = (Main.config.getHeight() - y*scale - h*scale) / Main.config.getHeight() * 2;

        translationMatrix.setIdentity();
        translationMatrix.m03 = xn;
        translationMatrix.m13 = yn;
        return translationMatrix;
    }

    public void createProjectionMatrix(){
        projectionMatrix = new Matrix4f();
        projectionMatrix.setIdentity();
        float ratio = (float)Main.config.getWidth() / (float)Main.config.getHeight();

        projectionMatrix.m00 = 1f;
        projectionMatrix.m11 = 1/ratio;

    }
}
