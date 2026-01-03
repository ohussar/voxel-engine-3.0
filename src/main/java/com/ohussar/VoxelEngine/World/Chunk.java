package com.ohussar.VoxelEngine.World;

import com.ohussar.VoxelEngine.Main;
import org.lwjgl.util.vector.Vector3f;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.ohussar.VoxelEngine.Entities.Cube.*;

public class Chunk {

    public static final int CHUNK_SIZE_X = 16;
    public static final int CHUNK_SIZE_Y = 256;
    public static final int CHUNK_SIZE_Z = 16;
    public int compressedDataStored[];
    private Block[] CHUNK_BLOCKS = new Block[CHUNK_SIZE_X * CHUNK_SIZE_Y * CHUNK_SIZE_Z];
    public boolean meshGenerated = false;
    private final Vector3f position;

    public final ChunkMeshData meshData;

    public Chunk(Vector3f position){
        this.position = position;
        this.meshData = new ChunkMeshData();
    }

    public Vector3f getPosition(){
        return position;
    }

    public void addBlockToChunk(Block block){
        int x = (int) block.position.x;
        int y = (int) block.position.y;
        int z = (int) block.position.z;

        int relx = x - (int)position.x*16;
        int rely = y - (int)position.y*16;
        int relz = z - (int)position.z*16;

        Block newblock = new Block(new Vector3f(relx, rely, relz), block.blockTypeId);
        addBlockToChunkInternal(newblock, relx, rely, relz);
    }

    public void removeBlockFromChunk(int x, int y, int z){
        Block block = getBlockAtPos(x, y, z);
        if(block != null){
            addBlockToChunkInternal(null, x, y, z);
        }
    }
    public Block getBlockAtPos(int x, int y, int z){
        int coord = y * CHUNK_SIZE_X + x + z * (CHUNK_SIZE_X * CHUNK_SIZE_Y);
        if(coord >= CHUNK_BLOCKS.length){
            return null;
        }
        if(y >= CHUNK_SIZE_Y){
            return null;
        }

        return CHUNK_BLOCKS[coord];
    }
    protected void addBlockToChunkInternal(Block block, int x, int y, int z){
        CHUNK_BLOCKS[y * CHUNK_SIZE_X + x + z * (CHUNK_SIZE_X * CHUNK_SIZE_Y)] = block;
    }

    public void prepareMesh(){
        Map<Vector3f, Boolean> meshed = new HashMap<>();
        meshData.verticesCount = 0;
        Vector3f[][] vertexes = {PY_POS, NY_POS, PX_POS, NX_POS, PZ_POS, NZ_POS};
        for(int y = 0; y < CHUNK_SIZE_Y; y++){
            for(int x = 0; x< CHUNK_SIZE_X; x++){

                for(int z = 0; z < 16; z++){
                    int start_z = z;
                    int finish_z = start_z;
                    int start_x = x;
                    int finish_x = start_x;


                    Block block = getBlockAtPos(start_x, y, start_z);
                    if(block != null && !meshed.containsKey(block.position)){
                        for(int zz = start_z; zz < CHUNK_SIZE_Z; zz++){
                            Block grow = getBlockAtPos(start_x, y, zz);
                            if(grow == null || grow.blockTypeId != block.blockTypeId){
                                break;
                            }

                            if(meshed.containsKey(grow.position)) {
                                break;
                            }
                            meshed.put(grow.position, true);
                            finish_z = zz;
                        }

                        for(int xx = start_x+1; xx < CHUNK_SIZE_X; xx++){
                            boolean allMatch = true;

                            List<Block> tempList = new ArrayList<>();

                            for(int zz = start_z; zz < finish_z+1; zz++){
                                Block grow = getBlockAtPos(xx, y, zz);
                                if(grow == null || grow.blockTypeId != block.blockTypeId){
                                    allMatch = false;
                                    break;
                                }
                                if(meshed.containsKey(grow.position)) {
                                    allMatch = false;
                                    break;
                                }
                                tempList.add(grow);
                            }
                            Block grow = getBlockAtPos(xx, y, z);
                            if(grow == null || grow.blockTypeId != block.blockTypeId){
                                break;
                            }
                            if(allMatch) {
                                finish_x = xx;
                                for(Block b : tempList){
                                    meshed.put(b.position, true);
                                }
                                tempList.clear();
                            }else{
                                tempList.clear();
                                break;
                            }
                        }


                        meshed.put(block.position, true);
                        for(int k = 0; k < 6; k++) { // 6 directions ( up down left right ....
                            for (int f = 0; f < 6; f++) { // 6 vertices for each face;
                                Vector3f start = vertexes[k][f];
                                Vector3f pos = new Vector3f(start.x + block.position.x, start.y + block.position.y, start.z + block.position.z);
                                if(start.z > 0) {
                                    pos.z += (finish_z - start_z);
                                }
                                if(start.x > 0){
                                    pos.x += (finish_x - start_x);
                                }

                                meshData.positionList.add(pos.x);
                                meshData.positionList.add(pos.y);
                                meshData.positionList.add(pos.z);
                                meshData.normalList.add((byte) (k/2));
                                meshData.blockTypeList.add((byte) BlockTypes.BLOCK_TYPES.get(block.blockTypeId).textureGetter.getTextureIdForSide(k));
                                meshData.verticesCount += 1;

                            }
                        }

                        z = finish_z-1;
                    }
                    if(z == 15) {
                        x = finish_x;
                    }
                }
            }
        }

        int[] compressedData = new int[meshData.verticesCount];
        for(int i = 0; i < meshData.positionList.size(); i+=3){
            int x =  meshData.positionList.get(i).intValue(); // 0 a 16
            int y =  meshData.positionList.get(i+1).intValue(); // 0 a 255
            int z =  meshData.positionList.get(i+2).intValue(); // 0 a 16
            compressedData[i/3] = (x&31) | ((y&511) << 5) | ((z&31) << 14);;
        }

        for(int i = 0; i < meshData.verticesCount; i++){
            int normal = meshData.normalList.get(i) & 3; // 00, 01, 10
            int texture = meshData.blockTypeList.get(i) & 0xFF;
            compressedData[i] = compressedData[i] | (normal << 19) | (texture << 21);
        }
        compressedDataStored = compressedData;
        meshGenerated = false;
    }

    public void buildMesh() {
        meshData.VAO = Main.StaticLoader.updateVAO(meshData.VAO, compressedDataStored);
        meshData.normalList.clear();
        meshData.blockTypeList.clear();
        meshData.positionList.clear();
        meshGenerated = true;
    }
}
