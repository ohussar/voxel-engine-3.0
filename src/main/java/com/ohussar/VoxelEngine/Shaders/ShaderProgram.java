package com.ohussar.VoxelEngine.Shaders;

import com.nishu.utils.Vector2f;
import com.nishu.utils.Vector3f;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.util.vector.Matrix4f;

import java.io.*;
import java.nio.FloatBuffer;

public abstract class ShaderProgram {
    int programID;
    int vertexShaderID;
    int fragmentShaderID;
    FloatBuffer matrixBuffer = BufferUtils.createFloatBuffer(16);
    public ShaderProgram(String vertexFile, String fragmentFile){
        programID = GL20.glCreateProgram();
        vertexShaderID = loadShader(vertexFile, GL20.GL_VERTEX_SHADER);
        fragmentShaderID = loadShader(fragmentFile, GL20.GL_FRAGMENT_SHADER);

        GL20.glAttachShader(programID, vertexShaderID);
        GL20.glAttachShader(programID, fragmentShaderID);
        GL20.glLinkProgram(programID);
        GL20.glValidateProgram(programID);

        getAllUniformLocations();

    }

    protected abstract void getAllUniformLocations();

    protected int getUniformLocation(String varName){
        return GL20.glGetUniformLocation(programID, varName);
    }
    protected void loadInt(int location, int value){
        GL20.glUniform1i(location, value);
    }
    protected void loadFloat(int location, float value){
        GL20.glUniform1f(location, value);
    }

    protected void load2DVector(int location, Vector2f vec) {
        GL20.glUniform2f(location, vec.getX(), vec.getY());
    }
    protected void load3DVector(int location, Vector3f vec) {
        GL20.glUniform3f(location, vec.getX(), vec.getY(), vec.getZ());
    }

    protected void loadMatrix(int location, Matrix4f matrix){
        matrix.store(matrixBuffer);
        matrixBuffer.flip();
        GL20.glUniformMatrix4fv(location, false, matrixBuffer);
    }

    protected void loadBoolean(int location, Boolean value){
        int val = value ? 1 : 0;
        GL20.glUniform1i(location, val);
    }

    protected abstract void bindAttributes();

    protected void bindAttribute(String variableName, int attribute) {
        GL20.glBindAttribLocation(programID, attribute, variableName);
    }

    public void start(){
        GL20.glUseProgram(programID);
    }

    public void stop(){
        GL20.glUseProgram(0);
    }

    public void clean() {
        stop();
        GL20.glDetachShader(programID, vertexShaderID);
        GL20.glDetachShader(programID, fragmentShaderID);
        GL20.glDeleteShader(vertexShaderID);
        GL20.glDeleteShader(fragmentShaderID);
        GL20.glDeleteProgram(programID);
    }



    private int loadShader(String file, int type) {
        StringBuilder shaderSource = new StringBuilder();

        final String dir = System.getProperty("user.dir");
        //System.out.println("current dir = " + dir);
        //System.out.println(file);
        InputStream in = Class.class.getResourceAsStream(file);
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(file));
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        String line;
        try {
            while((line = reader.readLine()) != null){
                shaderSource.append(line).append("\n");
            }
            reader.close();
        } catch (IOException e){
            e.printStackTrace();
            System.err.println("Could not load shader file: " + file);
            System.exit(-1);
        }
        int shaderID = GL20.glCreateShader(type);
        GL20.glShaderSource(shaderID, shaderSource);
        GL20.glCompileShader(shaderID);

        if(GL20.glGetShaderi(shaderID, GL20.GL_COMPILE_STATUS) == GL11.GL_FALSE){
            System.out.println(GL20.glGetShaderInfoLog(shaderID, 1000));
            System.err.println("Could not compiled shader: " + file);
            System.exit(-1);
        }
        return shaderID;
    }
}
