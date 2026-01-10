package com.ohussar.VoxelEngine.Entities;

import com.ohussar.VoxelEngine.World.Blocks.IMeshBuildingContext;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

public final class Cube implements IBlockGeometry {


    public static final int UP = 0; // + y
    public static final int DOWN = 1; // - y
    public static final int EAST = 2; // +x
    public static final int WEST = 3; // -x
    public static final int NORTH = 4;// +z
    public static final int SOUTH = 5; // -z


    public static Vector3f[] PX_POS = {

            new Vector3f(1.0f,1.0f,0.0f),
            new Vector3f(1.0f,0.0f,0.0f),
            new Vector3f(1.0f,0.0f,1.0f),
            new Vector3f(1.0f,0.0f,1.0f),
            new Vector3f(1.0f,1.0f,1.0f),
            new Vector3f(1.0f,1.0f,0.0f),
            new Vector3f(1f, 0.5f, 0.5f) // face center

    };

    public static Vector3f[] NX_POS = {
            new Vector3f(0.0f,1.0f,0.0f),
            new Vector3f(0.0f,1.0f,1.0f),
            new Vector3f(0.0f,0.0f,1.0f),
            new Vector3f(0.0f,0.0f,1.0f),
            new Vector3f(0.0f,0.0f,0.0f),
            new Vector3f(0.0f,1.0f,0.0f),

            new Vector3f(0f, 0.5f, 0.5f) // face center

    };

    public static Vector3f[] PY_POS = {

            new Vector3f(0.0f,1.0f,1.0f),
            new Vector3f(0.0f,1.0f,0.0f),
            new Vector3f(1.0f,1.0f,0.0f),
            new Vector3f(1.0f,1.0f,0.0f),
            new Vector3f(1.0f,1.0f,1.0f),
            new Vector3f(0.0f,1.0f,1.0f),
            new Vector3f(0.5f, 1.0f, 0.5f) // face center

    };

    public static Vector3f[] NY_POS = {

            new Vector3f(0.0f,0.0f,1.0f),
            new Vector3f(1.0f,0.0f,1.0f),
            new Vector3f(1.0f,0.0f,0.0f),
            new Vector3f(1.0f,0.0f,0.0f),
            new Vector3f(0.0f,0.0f,0.0f),
            new Vector3f(0.0f,0.0f,1.0f),
            new Vector3f(0.5f, 0.0f, 0.5f)

    };

    public static Vector3f[] PZ_POS = {

            new Vector3f(0.0f,1.0f,1.0f),
            new Vector3f(1.0f,1.0f,1.0f),
            new Vector3f(1.0f,0.0f,1.0f),
            new Vector3f(1.0f,0.0f,1.0f),
            new Vector3f(0.0f,0.0f,1.0f),
            new Vector3f(0.0f,1.0f,1.0f),
            new Vector3f(0.5f, 0.5f, 1.0f)

    };

    public static Vector3f[] NZ_POS = {

            new Vector3f(0.0f,1.0f,0.0f),
            new Vector3f(0.0f,0.0f,0.0f),
            new Vector3f(1.0f,0.0f,0.0f),
            new Vector3f(1.0f,0.0f,0.0f),
            new Vector3f(1.0f,1.0f,0.0f),
            new Vector3f(0.0f,1.0f,0.0f),
            new Vector3f(0.5f, 0.5f, 0.0f)

    };

    public static Vector3f[][] FACES =  new Vector3f[][]{PY_POS, NY_POS, PX_POS, NX_POS, PZ_POS, NZ_POS};

    @Override
    public Vector3f[] getFace(int face) {
        return FACES[face];
    }

    @Override
    public int getModelOffsets(IMeshBuildingContext.MeshBuildContext context, Vector3f vertex, int face) {
        return 0;
    }
}
