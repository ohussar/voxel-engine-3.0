package com.ohussar.VoxelEngine.World;

import org.lwjgl.util.vector.Vector3f;

public class Block {
    public final Vector3f position;
    public final int blockTypeId;
    public Block(Vector3f pos, int blockTypeId){
        this.position = pos;
        this.blockTypeId = blockTypeId;
    }
}
