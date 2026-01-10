package com.ohussar.VoxelEngine.World.Blocks;

import org.lwjgl.util.vector.Vector3f;

public class Block {
    public final Vector3f position;
    public final BlockTypes.BlockType blockType;
    public Block(Vector3f pos, BlockTypes.BlockType type) {
        this.position = pos;
        this.blockType = type;
    }


    @Override
    public boolean equals(Object obj) {
        if(obj  instanceof Block block){
            return block.position.equals(this.position) && block.blockType.equals(this.blockType);
        }
        return false;
    }
}
