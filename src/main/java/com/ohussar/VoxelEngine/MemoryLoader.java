package com.ohussar.VoxelEngine;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

public class MemoryLoader {

    public int updateVAO(int VAO, float[] vertices, byte[] types){
        if(VAO == -1){
            VAO = createVAO();
        }else{
            GL30.glDeleteVertexArrays(VAO);
            VAO = createVAO();
        }
        storeDataInAttributeList(vertices, 0, 3);
        storeDataInAttributeList(types, 1, 1);
        GL30.glBindVertexArray(0);
        return VAO;
    }

    public int updateVAO3D(int VAO, float[] vertices, float[] textureCoordinates){
        if(VAO == -1){
            VAO = createVAO();
        }else{
            GL30.glDeleteVertexArrays(VAO);
            VAO = createVAO();
        }
        storeDataInAttributeList(vertices, 0, 3);
        storeDataInAttributeList(textureCoordinates, 1, 2);
        GL30.glBindVertexArray(0);
        return VAO;
    }
    public int updateVAO4D(int VAO, float[] vertices){
        if(VAO == -1){
            VAO = createVAO();
        }else{
            GL30.glDeleteVertexArrays(VAO);
            VAO = createVAO();
        }
        storeDataInAttributeList(vertices, 0, 4);
        GL30.glBindVertexArray(0);
        return VAO;
    }
    public int updateVAO(int VAO, int[] compressedData){
        if(VAO == -1){
            VAO = createVAO();
        }else{
            GL30.glDeleteVertexArrays(VAO);
            VAO = createVAO();
        }
        storeDataInAttributeList(compressedData, 0, 1);
        GL30.glBindVertexArray(0);
        return VAO;
    }

    private int createVAO() {

        int vaoID = GL30.glGenVertexArrays();
        //vaos.add(vaoID);
        GL30.glBindVertexArray(vaoID);
        return vaoID;
    }

    private void storeDataInAttributeList(float[] data, int attributeNumber, int dimentions) {
        int vboID = GL15.glGenBuffers();
        //vbos.add(vboID);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboID);
        FloatBuffer buffer = this.toFloatBuffer(data);
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW); // binding the buffer
        GL20.glVertexAttribPointer(attributeNumber, dimentions, GL11.GL_FLOAT, false, 0, 0);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0); // unbinding the buffer
    }
    private void storeDataInAttributeList(int[] data, int attributeNumber, int dimentions) {
        int vboID = GL15.glGenBuffers();
        //vbos.add(vboID);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboID);
        IntBuffer buffer = this.toIntBuffer(data);
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW); // binding the buffer
        GL20.glVertexAttribPointer(attributeNumber, dimentions, GL11.GL_FLOAT, false, 0, 0);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0); // unbinding the buffer
    }

    private void storeDataInAttributeList(byte[] data, int attributeNumber, int dimentions) {
        int vboID = GL15.glGenBuffers();
        //vbos.add(vboID);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboID);
        ByteBuffer buffer = this.toByteBuffer(data);
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW); // binding the buffer
        GL20.glVertexAttribPointer(attributeNumber, dimentions, GL11.GL_FLOAT, false, 0, 0);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0); // unbinding the buffer
    }

    private IntBuffer toIntBuffer(int[] data){
        IntBuffer buffer = BufferUtils.createIntBuffer(data.length);
        buffer.put(data);
        buffer.flip();
        return buffer;
    }

    private FloatBuffer toFloatBuffer(float[] data){
        FloatBuffer buffer = BufferUtils.createFloatBuffer(data.length);
        buffer.put(data);
        buffer.flip();
        return buffer;
    }
    private ByteBuffer toByteBuffer(byte[] data){
        ByteBuffer buffer = BufferUtils.createByteBuffer(data.length);
        buffer.put(data);
        buffer.flip();
        return buffer;
    }






    public void cleanUp() {

    }
}
