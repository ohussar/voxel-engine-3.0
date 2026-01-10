package com.ohussar.VoxelEngine.World.Blocks;

import com.ohussar.VoxelEngine.World.Chunk;
import com.ohussar.VoxelEngine.World.World;

public interface IMeshBuildingContext {

    public boolean[] processBuildingContext(MeshBuildContext context);
    public record MeshBuildContext(Chunk chunk, Block original, World world, Block UP, Block DOWN, Block EAST, Block WEST, Block NORTH, Block SOUTH){};
}


