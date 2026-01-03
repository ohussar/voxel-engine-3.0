package com.ohussar.VoxelEngine.Util;

import com.ohussar.VoxelEngine.Entities.Camera;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

public class Maths {

    public static Matrix4f createTransformationMatrix(Vector3f translation, Vector3f rotation, float scale){
        Matrix4f matrix = new Matrix4f();
        matrix.setIdentity();
        Matrix4f.translate(translation, matrix, matrix);
        Matrix4f.rotate((float) Math.toRadians(rotation.x), new Vector3f(1, 0, 0), matrix, matrix);
        Matrix4f.rotate((float) Math.toRadians(rotation.y), new Vector3f(0, 1, 0), matrix, matrix);
        Matrix4f.rotate((float) Math.toRadians(rotation.z), new Vector3f(0, 0, 1), matrix, matrix);
        Matrix4f.scale(new Vector3f(scale, scale, scale), matrix, matrix);

        return matrix;
    }
    public static Matrix4f createViewMatrix(Camera camera){
        Matrix4f matrix = new Matrix4f();

        Matrix4f.rotate((float) Math.toRadians(camera.getRotation().x), new Vector3f(1, 0, 0), matrix, matrix);
        Matrix4f.rotate((float) Math.toRadians(camera.getRotation().y), new Vector3f(0, 1, 0), matrix, matrix);
        Matrix4f.rotate((float) Math.toRadians(camera.getRotation().z), new Vector3f(0, 0, 1), matrix, matrix);
        Matrix4f.translate(new Vector3f(-camera.getPosition().x, -camera.getPosition().y, -camera.getPosition().z), matrix, matrix);

        return matrix;
    }
    public static float lerp(float min, float max, float val)
    {
        return (1.0f - val) * min + val * max;
    }
}
