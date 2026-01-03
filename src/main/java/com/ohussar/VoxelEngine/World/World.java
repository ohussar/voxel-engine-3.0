package com.ohussar.VoxelEngine.World;

import com.ohussar.VoxelEngine.Entities.Camera;
import com.ohussar.VoxelEngine.Test.PerlinNoiseGenerator;
import com.ohussar.VoxelEngine.Util.Vec3i;
import org.lwjgl.util.vector.Vector3f;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class World {

    private Map<Vec3i, Chunk> chunks = new HashMap<Vec3i, Chunk>();
    private List<Chunk> loadedChunks = new ArrayList<>();

    private Vec3i previousCameraPos = new Vec3i(5, 5, 5);

    public void tick(Camera camera){

        Vector3f cPos = camera.getPosition();
        Vec3i chunkCameraPos = new Vec3i((int)Math.floor(cPos.x/16), 0, (int)Math.floor(cPos.z/16));

        if(!chunkCameraPos.equals(previousCameraPos)){
            previousCameraPos = chunkCameraPos;
            loadedChunks.clear();
            for(int i = -10; i < 10; i++){
                for(int j = -10; j < 10; j++){
                    Vec3i copy = chunkCameraPos.copy();
                    Vec3i chunkPos = copy.translate(i, 0, j);
                    if(!isChunkGenerated(chunkPos)){
                        generateChunk(chunkPos);
                    }
                    loadedChunks.add(chunks.get(chunkPos));
                }
            }
        }

    }

    public List<Chunk> getLoadedChunks(){
        return loadedChunks;
    }

    public boolean isChunkGenerated(int x, int z){
        return isChunkGenerated(new Vec3i(x, 0, z));
    }

    public boolean isChunkGenerated(Vec3i chunkpos){
        return chunks.containsKey(chunkpos);
    }
    public void generateChunk(int chunkx, int chunkz){
        generateChunk(new Vec3i(chunkx, 0, chunkz));
    }
    public Block getBlock(Vector3f pos){
        int xx = (int) pos.x;
        int yy = (int) pos.y;
        int zz = (int) pos.z;

        int chunkx = (int) Math.floor((double)xx/(double)16);
        int chunkz = (int) Math.floor((double)zz/(double)16);
        Vec3i chunkpos = new Vec3i(chunkx, 0, chunkz);
        if(!isChunkGenerated(chunkpos)){
            return null;
        }
        Chunk chunk = chunks.get(chunkpos);

        int relx = xx - chunkx * 16;
        int rely = yy;
        int relz = zz - chunkz * 16;

        if(rely >= Chunk.CHUNK_SIZE_Y){
            return null;
        }

        Block ret = chunk.getBlockAtPos(relx, rely, relz);
        if(ret!=null){
            return new Block(new Vector3f(relx + chunkx * 16, rely, relz + chunkz * 16), ret.blockTypeId);
        }
        return null;
    }
    public void placeBlock(Block block){

        int xx = (int) block.position.getX();
        int yy = (int) block.position.getY();
        int zz = (int) block.position.getZ();

        int chunkx = (int) Math.floor((double)xx/(double)16);
        int chunkz = (int) Math.floor((double)zz/(double)16);
        Vec3i chunkpos = new Vec3i(chunkx, 0, chunkz);
        if(!isChunkGenerated(chunkpos)){
            generateChunk(chunkpos);
        }

        Chunk chunk = chunks.get(chunkpos);
        chunk.addBlockToChunk(block);
        chunk.buildMesh();
    }

    public void removeBlock(Vec3i pos){
        int xx = (int) pos.getX();
        int yy = (int) pos.getY();
        int zz = (int) pos.getZ();

        int chunkx = (int) Math.floor((double)xx/(double)16);
        int chunkz = (int) Math.floor((double)zz/(double)16);
        Vec3i chunkpos = new Vec3i(chunkx, 0, chunkz);
        Chunk chunk = chunks.get(chunkpos);
        if(chunk != null){
            chunk.removeBlockFromChunk(xx-chunkx*16, yy, zz-chunkz*16);
            chunk.buildMesh();
        }
    }

    public void generateChunk(Vec3i chunkpos){
        if(!chunks.containsKey(chunkpos)){
            Chunk generatedChunk = new Chunk(chunkpos.toVec3f());
            PerlinNoiseGenerator gen = new PerlinNoiseGenerator();
            for(int x = 0; x < 16; x++){
                for(int z = 0; z < 16; z++){

                    int height = (int) gen.generateHeight(x + chunkpos.getX() * 16, z + chunkpos.getZ() * 16);
                    height += 60;
                    Block block = new Block(new Vector3f(x, height, z), BlockTypes.GRASS.id);
                    generatedChunk.addBlockToChunkInternal(block, x, height, z);
                   // for (int y = height-1; y > 0; y--) {
                    //    Block b = new Block(new Vector3f(x, y, z), BlockTypes.DIRT.worldId);
                    //    generatedChunk.addBlockToChunkInternal(b, x, y, z);
                    //}



                }
            }
            generatedChunk.buildMesh();
            chunks.put(chunkpos, generatedChunk);
        }
    }

}
