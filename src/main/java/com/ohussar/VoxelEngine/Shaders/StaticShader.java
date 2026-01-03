package com.ohussar.VoxelEngine.Shaders;

import com.ohussar.VoxelEngine.Entities.Camera;
import com.ohussar.VoxelEngine.Util.Maths;
import org.lwjgl.util.vector.Matrix4f;

public class StaticShader extends ShaderProgram{
    private static final String vertexFile = "src/main/java/com/ohussar/VoxelEngine/Shaders/vertexShader.txt";
    private static final String fragmentFile = "src/main/java/com/ohussar/VoxelEngine/Shaders/fragmentShader.txt";

    int location_transformationMatrix;
    int location_projectionMatrix;
    int location_viewMatrix;
    int location_toShadowMapSpace;
    int location_shadowMap;
    int location_depth;
    public StaticShader() {
        super(vertexFile, fragmentFile);
    }

    @Override
    protected void getAllUniformLocations() { // get uniform ids from shader for later access
        location_transformationMatrix = super.getUniformLocation("transformationMatrix");
        location_projectionMatrix = super.getUniformLocation("projectionMatrix");
        location_viewMatrix = super.getUniformLocation("viewMatrix");
        location_toShadowMapSpace = super.getUniformLocation("toShadowMapSpace");
        location_shadowMap = super.getUniformLocation("shadowMap");
    }

    public void connectToTextures(){
        super.loadInt(location_shadowMap, 5);
    }


    @Override
    protected void bindAttributes() { // bind attributes from model to the shader: like vertex positions to in vec3 pos;
        super.bindAttribute("compressedData", 0);
    }

    public void loadToShadowMapSpaceMatrix(Matrix4f matrix){
        super.loadMatrix(location_toShadowMapSpace, matrix);
    }

    public void loadTransformationMatrix(Matrix4f matrix){
        super.loadMatrix(location_transformationMatrix, matrix);
    }

    public void loadProjectionMatrix(Matrix4f matrix4f){
        super.loadMatrix(location_projectionMatrix, matrix4f);
    }

    public void loadViewMatrix(Camera camera){
        super.loadMatrix(location_viewMatrix, Maths.createViewMatrix(camera));
    }

}
