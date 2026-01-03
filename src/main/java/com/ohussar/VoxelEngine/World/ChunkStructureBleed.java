package com.ohussar.VoxelEngine.World;

import com.ohussar.VoxelEngine.Util.Vec3i;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChunkStructureBleed {

    private final Map<Vec3i, ChunkBleed> chunks = new HashMap<>();


    public void setChunkBleed(Vec3i pos, List<Block> bleedBlocks){
        if(chunks.containsKey(pos)){
            List<Block> bl = new ArrayList<>();
            bl.addAll(bleedBlocks);
            bl.addAll(chunks.get(pos).blocks);
            chunks.put(pos, new ChunkBleed(pos, bl));
        }else {
            chunks.put(pos, new ChunkBleed(pos, bleedBlocks));
        }
    }

    public void setChunkBleed(Map<Vec3i, List<Block>> cblcks){
        for(Map.Entry<Vec3i, List<Block>> entry : cblcks.entrySet()){
            if(chunks.containsKey(entry.getKey())){
                List<Block> bl = new ArrayList<>(chunks.get(entry.getKey()).blocks);
                bl.addAll(entry.getValue());
                chunks.put(entry.getKey(), new ChunkBleed(entry.getKey(), bl));
            }else{
                chunks.put(entry.getKey(), new ChunkBleed(entry.getKey(), entry.getValue()));
            }
        }
    }



    public ChunkBleed getChunkBleed(Vec3i pos){
        return chunks.get(pos);
    }


    public class ChunkBleed {
        final Vec3i chunkPos;
        final List<Block> blocks;

        public ChunkBleed(Vec3i chunkPos, List<Block> blocks) {
            this.chunkPos = chunkPos;
            this.blocks = blocks;
        }
    }
}
