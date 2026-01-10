package com.ohussar.VoxelEngine.Entities;

import com.ohussar.VoxelEngine.World.Blocks.IMeshBuildingContext;
import org.lwjgl.util.vector.Vector3f;

public interface IBlockGeometry {

    //    z         y           x
    // 11111111 | 11111111 | 11111111
    int getModelOffsets(IMeshBuildingContext.MeshBuildContext context, Vector3f vertex, int face);

    Vector3f[] getFace( int face);
}
