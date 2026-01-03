package com.ohussar.VoxelEngine.World;

import com.ohussar.VoxelEngine.Entities.Camera;
import com.ohussar.VoxelEngine.Main;
import com.ohussar.VoxelEngine.Test.OpenSimplexNoise;
import com.ohussar.VoxelEngine.Test.PerlinNoiseGenerator;
import com.ohussar.VoxelEngine.Util.Vec3i;
import org.lwjgl.Sys;
import org.lwjgl.util.vector.Vector3f;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

public class World {

    private Map<Vec3i, Chunk> chunks = new HashMap<Vec3i, Chunk>();
    private List<Chunk> loadedChunks = new CopyOnWriteArrayList<>();
    private boolean updated = false;
    private Vec3i previousCameraPos = new Vec3i(5, 5, 5);
    private Vector3f prevRot = new Vector3f(0, 0, 0);
    public static int renderDist = 6;
    public void tick(Camera camera){

        Vector3f cPos = camera.getPosition();
        Vec3i chunkCameraPos = new Vec3i((int)Math.floor(cPos.x/16), 0, (int)Math.floor(cPos.z/16));



        if(!chunkCameraPos.equals(previousCameraPos)){
            updated = false;
            previousCameraPos = chunkCameraPos;
            loadedChunks.clear();
            for(int i = -10; i < 10; i++){
                for(int j = -10; j < 10; j++){
                    int finalI = i;
                    int finalJ = j;
                    Vec3i copy = chunkCameraPos.copy();
                    Vec3i chunkPos = copy.translate(finalI, 0, finalJ);
                    if(!isChunkGenerated(chunkPos)) {
                        Thread gen = new Thread(() -> {
                            generateChunk(chunkPos);
                            loadedChunks.add(chunks.get(chunkPos));
                        });
                        gen.start();
                    }else{
                        loadedChunks.add(chunks.get(chunkPos));
                    }

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
        if(chunks.get(chunkpos) == null){
            return false;
        }
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
        chunk.prepareMesh();
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
            chunk.prepareMesh();
        }
    }

    public void generateChunk(Vec3i chunkpos){
        if(!chunks.containsKey(chunkpos)){
           ChunkBundle bundle = generateChunkComplex(chunkpos);
           ChunkStructureBleed bleed = bundle.chunkStructureBleed;
           ChunkStructureBleed.ChunkBleed thisChunk = bleed.getChunkBleed(chunkpos);
            if(thisChunk != null){
                for(Block b : thisChunk.blocks){
                    bundle.centerChunk.addBlockToChunkInternal(b, (int)b.position.x, (int)b.position.y, (int)b.position.z);
                }
            }
            bundle.centerChunk.prepareMesh();
            chunks.put(chunkpos, bundle.centerChunk);
        }
    }

    public ChunkBundle generateChunkComplex(Vec3i chunkpos){
        Chunk generatedChunk = new Chunk(chunkpos.toVec3f());
        ChunkStructureBleed bleed = new ChunkStructureBleed();
        PerlinNoiseGenerator gen = new PerlinNoiseGenerator();
        for(int x = 0; x < 16; x++){
            for(int z = 0; z < 16; z++){
                int height = (int) gen.generateHeight(x + chunkpos.getX() * 16, z + chunkpos.getZ() * 16);
                height += 60;
                Block block;
                if(height < 45){
                    block = new Block(new Vector3f(x, height, z), BlockTypes.SAND.id);
                }else{
                    block = new Block(new Vector3f(x, height, z), BlockTypes.GRASS.id);
                }
                generatedChunk.addBlockToChunkInternal(block, x, height, z);
                for (int y = height-1; y > 0; y--) {
                    Block b = new Block(new Vector3f(x, y, z), BlockTypes.DIRT.id);
                    generatedChunk.addBlockToChunkInternal(b, x, y, z);
                }

                treeGeneration(bleed, chunkpos, new Vec3i(x, height+1, z));
            }
        }

        Vec3i[] around = new  Vec3i[]{
                new Vec3i(chunkpos.getX()-1, chunkpos.getY(), chunkpos.getZ()),
                new Vec3i(chunkpos.getX()+1, chunkpos.getY(), chunkpos.getZ()),
                new Vec3i(chunkpos.getX(), chunkpos.getY(), chunkpos.getZ()-1),
                new Vec3i(chunkpos.getX(), chunkpos.getY(), chunkpos.getZ()+1),
                new Vec3i(chunkpos.getX()+1, chunkpos.getY(), chunkpos.getZ()+1),
                new Vec3i(chunkpos.getX()-1, chunkpos.getY(), chunkpos.getZ()+1),
                new Vec3i(chunkpos.getX()-1, chunkpos.getY(), chunkpos.getZ()-1),
                new Vec3i(chunkpos.getX()+1, chunkpos.getY(), chunkpos.getZ()-1),
        };
        for(int j = 0; j < around.length; j++){
            for(int x = 0; x < 16; x++) {
                for (int z = 0; z < 16; z++) {
                    int height = (int) gen.generateHeight(x + around[j].getX() * 16, z + around[j].getZ() * 16)+60;
                    treeGeneration(bleed, around[j], new Vec3i(x, height+1, z));
                }
            }
        }

        return new ChunkBundle(bleed, generatedChunk);
    }



    public static int seed = 1341923101;

    public void treeGeneration(ChunkStructureBleed bld, Vec3i chunkpos, Vec3i blockpos) {
        float fl = OpenSimplexNoise.noise2(seed + chunkpos.getX() + 15L*chunkpos.getZ(), blockpos.getX(), blockpos.getZ());
        if(fl > 0.85f){
            float f = 4 *fl - 3;
            int height = (int ) (f * 10);

            height = Math.max(3, height);
            height = Math.min(10, height);

            for (int i = 0; i < height; i++) {
                Block block = new Block(new Vector3f(blockpos.getX(), blockpos.getY()+i, blockpos.getZ()), BlockTypes.LOG.id);
                List<Block> b = Collections.synchronizedList(new ArrayList<>());
                b.add(block);
                bld.setChunkBleed(chunkpos, b);
            }
            float radius = 2*fl - 1;
            Map<Vec3i, List<Block>> map = new HashMap<>();
            for(int k = 0; k < 3; k++) {
                for (int j = 0; j < 20; j++) {
                    int angle = 18 * j;
                    double rad = Math.toRadians(angle);
                    for (int i = 0; i < radius + 2 - k; i++) {
                        double xx = Math.cos(rad) * i;
                        double yy = Math.sin(rad) * i;
                        int rxx = blockpos.getX() + (int) Math.round(xx);
                        int ryy = blockpos.getZ() + (int) Math.round(yy);
                        int blx = (rxx+16) % 16;
                        int bly = (ryy+16) % 16;
                        Block leave = new Block(new Vector3f(blx, blockpos.getY() + height - 1 + k, bly), BlockTypes.LEAVES.id);
                        Vec3i newP = chunkpos.copy();
                        if (rxx > Chunk.CHUNK_SIZE_X - 1) {
                            newP.setX(newP.getX() + 1);
                        }
                        if (rxx < 0) {
                            newP.setX(newP.getX() - 1);
                        }
                        if (ryy > Chunk.CHUNK_SIZE_Z - 1) {
                            newP.setZ(newP.getZ() + 1);
                        }
                        if (ryy < 0) {
                            newP.setZ(newP.getZ() - 1);
                        }
                        map.computeIfAbsent(newP, p -> Collections.synchronizedList(new ArrayList<>())).add(leave);

                    }
                }
            }
            bld.setChunkBleed(map);
        }

    }

    public static class ChunkBundle {
        public final ChunkStructureBleed chunkStructureBleed;
        public final Chunk centerChunk;

        public ChunkBundle(ChunkStructureBleed chunkStructureBleed, Chunk centerChunk) {
            this.chunkStructureBleed = chunkStructureBleed;
            this.centerChunk = centerChunk;
        }
    }

    public static class ChunkDist {
        public final Chunk chunk;
        public final float dist;

        public ChunkDist(Chunk chunk, float dist) {
            this.chunk = chunk;
            this.dist = dist;
        }
    }

}
