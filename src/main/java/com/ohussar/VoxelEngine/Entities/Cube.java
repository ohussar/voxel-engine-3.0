package com.ohussar.VoxelEngine.Entities;

import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

public final class Cube {

    public static Vector3f[] PX_POS = {

            new Vector3f(1.0f,1.0f,0.0f),
            new Vector3f(1.0f,0.0f,0.0f),
            new Vector3f(1.0f,0.0f,1.0f),
            new Vector3f(1.0f,0.0f,1.0f),
            new Vector3f(1.0f,1.0f,1.0f),
            new Vector3f(1.0f,1.0f,0.0f)

    };

    public static Vector3f[] NX_POS = {

            new Vector3f(0.0f,1.0f,0.0f),
            new Vector3f(0.0f,0.0f,0.0f),
            new Vector3f(0.0f,0.0f,1.0f),
            new Vector3f(0.0f,0.0f,1.0f),
            new Vector3f(0.0f,1.0f,1.0f),
            new Vector3f(0.0f,1.0f,0.0f)

    };

    public static Vector3f[] PY_POS = {

            new Vector3f(0.0f,1.0f,1.0f),
            new Vector3f(0.0f,1.0f,0.0f),
            new Vector3f(1.0f,1.0f,0.0f),
            new Vector3f(1.0f,1.0f,0.0f),
            new Vector3f(1.0f,1.0f,1.0f),
            new Vector3f(0.0f,1.0f,1.0f)

    };

    public static Vector3f[] NY_POS = {

            new Vector3f(0.0f,0.0f,1.0f),
            new Vector3f(0.0f,0.0f,0.0f),
            new Vector3f(1.0f,0.0f,0.0f),
            new Vector3f(1.0f,0.0f,0.0f),
            new Vector3f(1.0f,0.0f,1.0f),
            new Vector3f(0.0f,0.0f,1.0f)

    };

    public static Vector3f[] PZ_POS = {

            new Vector3f(0.0f,1.0f,1.0f),
            new Vector3f(0.0f,0.0f,1.0f),
            new Vector3f(1.0f,0.0f,1.0f),
            new Vector3f(1.0f,0.0f,1.0f),
            new Vector3f(1.0f,1.0f,1.0f),
            new Vector3f(0.0f,1.0f,1.0f)

    };

    public static Vector3f[] NZ_POS = {

            new Vector3f(0.0f,1.0f,0.0f),
            new Vector3f(0.0f,0.0f,0.0f),
            new Vector3f(1.0f,0.0f,0.0f),
            new Vector3f(1.0f,0.0f,0.0f),
            new Vector3f(1.0f,1.0f,0.0f),
            new Vector3f(0.0f,1.0f,0.0f)

    };

    public static Vector2f[] UV = {

            new Vector2f(0.f, 0.f),
            new Vector2f(0.f, 1.f),
            new Vector2f(1.f, 1.f),
            new Vector2f(1.f, 1.f),
            new Vector2f(1.f, 0.f),
            new Vector2f(0.f, 0.f)

    };

    public static Vector3f[] NORMALS = {

            new Vector3f(0.f, 0.f, 0.f),
            new Vector3f(0.f, 0.f, 0.f),
            new Vector3f(0.f, 0.f, 0.f),
            new Vector3f(0.f, 0.f, 0.f),
            new Vector3f(0.f, 0.f, 0.f),
            new Vector3f(0.f, 0.f, 0.f)

    };

    public static final float[] vertices = {
            0f,1.0f,0f,
            0f,0f,0f,
            1.0f,0f,0f,
            1.0f,1.0f,0f,

            0f,1.0f,1.0f,
            0f,0f,1.0f,
            1.0f,0f,1.0f,
            1.0f,1.0f,1.0f, //

            1.0f,1.0f,0f,
            1.0f,0f,0f,
            1.0f,0f,1.0f,
            1.0f,1.0f,1.0f, //

            0f,1.0f,0f,
            0f,0f,0f,
            0f,0f,1.0f,
            0f,1.0f,1.0f,

            0f,1.0f,1.0f,
            0f,1.0f,0f,
            1.0f,1.0f,0f,
            1.0f,1.0f,1.0f,

            0f,0f,1.0f,
            0f,0f,0f,
            1.0f,0f,0f,
            1.0f,0f,1.0f
    };
    public static final int[] indices = {
            0,1,3,
            3,1,2,
            4,5,7,
            7,5,6,
            8,9,11,
            11,9,10,
            12,13,15,
            15,13,14,
            16,17,19,
            19,17,18,
            20,21,23,
            23,21,22
    };
    public static final float[] uv = {
            0, 0,
            0, 1,
            1, 1,
            1, 0,
            0, 0,
            0, 1,
            1, 1,
            1, 0,
            0, 0,
            0, 1,
            1, 1,
            1, 0,
            0, 0,
            0, 1,
            1, 1,
            1, 0,
            0, 0,
            0, 1,
            1, 1,
            1, 0,
            0, 0,
            0, 1,
            1, 1,
            1, 0,
    };
}
