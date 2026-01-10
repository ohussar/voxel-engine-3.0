package com.ohussar.VoxelEngine.World.Blocks;

import com.ohussar.VoxelEngine.Entities.CubeSmaller;
import com.ohussar.VoxelEngine.Main;
import com.ohussar.VoxelEngine.World.Chunk;
import org.lwjgl.util.vector.Vector3f;

public class WaterBlockType extends BlockTypes.BlockType implements IMeshBuildingContext{

    public WaterBlockType(int id){
        super(id, new BlockTypes.AllSideGetter("water"), false, true,true);
        this.addGeometry(new CubeSmaller());
    }

    public WaterBlockType(int id, BlockTypes.TextureSideGetter texture) {
        super(id, texture);
    }

    public WaterBlockType(int id, BlockTypes.TextureSideGetter texture, boolean isFullBlock, boolean isTranslucent, boolean canGreedyMesh) {
        super(id, texture, isFullBlock, isTranslucent, canGreedyMesh);
    }



    @Override
    public boolean[] processBuildingContext(MeshBuildContext context) {
        boolean up, dw, rt, lt, fr, ba = false;



        up = Chunk.canHideFace( context.original(), context.UP());
        dw = Chunk.canHideFace( context.original(), context.DOWN());
        rt = Chunk.canHideFace( context.original(), context.EAST());
        lt = Chunk.canHideFace( context.original(), context.WEST());
        fr = Chunk.canHideFace( context.original(), context.NORTH());
        ba = Chunk.canHideFace( context.original(), context.SOUTH());


        if(up){

        }


        return new boolean[]{up, dw, rt, lt, fr, ba};
    }
}
