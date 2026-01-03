package com.ohussar.VoxelEngine.Shaders;

import com.ohussar.VoxelEngine.Entities.Camera;
import com.ohussar.VoxelEngine.Util.Maths;
import org.lwjgl.util.vector.Matrix4f;

public class UIShader extends ShaderProgram{
    private static final String vertexFile = "src/main/java/com/ohussar/VoxelEngine/Shaders/UivertexShader.txt";
    private static final String fragmentFile = "src/main/java/com/ohussar/VoxelEngine/Shaders/UifragmentShader.txt";
    int location_projectionMatrix;
    int location_TranslationMatrix;
    int location_depth;
    public UIShader() {
        super(vertexFile, fragmentFile);
    }

    @Override
    protected void getAllUniformLocations() {
        // get uniform ids from shader for later access
        location_projectionMatrix = super.getUniformLocation("projectionMatrix");
        location_TranslationMatrix = super.getUniformLocation("translationMatrix");
    }

    public void connectToTextures(){
       // super.loadInt(location_shadowMap, 5);
    }


    @Override
    protected void bindAttributes() { // bind attributes from model to the shader: like vertex positions to in vec3 pos;
        super.bindAttribute("position", 0);
    }

    public void loadProjectionMatrix(Matrix4f matrix4f){
        super.loadMatrix(location_projectionMatrix, matrix4f);
    }

    public void loadTranslationMatrix(Matrix4f matrix){
        super.loadMatrix(location_TranslationMatrix, matrix);
    }

   /* public void loadToShadowMapSpaceMatrix(Matrix4f matrix){
        super.loadMatrix(location_toShadowMapSpace, matrix);
    }



    public void loadProjectionMatrix(Matrix4f matrix4f){
        super.loadMatrix(location_projectionMatrix, matrix4f);
    }

    public void loadViewMatrix(Camera camera){
        super.loadMatrix(location_viewMatrix, Maths.createViewMatrix(camera));
    }*/

}
