package com.ohussar.VoxelEngine.Entities;

import com.ohussar.VoxelEngine.World.Blocks.IMeshBuildingContext;
import org.lwjgl.Sys;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

public class CubeSmaller implements IBlockGeometry{


    public byte offsetY = (byte) (4) | (0b1000000); // always in increments of 1/128;
    public byte offsetX = (byte) 0; // always in increments of 1/128;
    public byte offsetZ = (byte) 0; // always in increments of 1/128;

    @Override
    public Vector3f[] getFace(int face) {
        return Cube.FACES[face];
    }

    @Override
    public int getModelOffsets(IMeshBuildingContext.MeshBuildContext context, Vector3f vertex, int face) {

        if(context != null){
            if(context.UP() == null){
                if(face != 1 && vertex.y > 0f){
                    return Byte.toUnsignedInt(offsetX) | (offsetY << 9) | (offsetZ << 17);
                }
            }else{
                return 0;
            }
        }else{
            if(face != 1 && vertex.y > 0f){
                return Byte.toUnsignedInt(offsetX) | (offsetY << 9) | (offsetZ << 17);
            }
        }

        return 0;
    }
}
