package com.ohussar.VoxelEngine.World;

import com.ohussar.VoxelEngine.Entities.Cube;
import com.ohussar.VoxelEngine.Main;
import com.ohussar.VoxelEngine.World.Blocks.Block;
import com.ohussar.VoxelEngine.World.Blocks.BlockTypes;
import com.ohussar.VoxelEngine.World.Blocks.IMeshBuildingContext;
import org.lwjgl.util.vector.Vector3f;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Chunk {

    public static final int CHUNK_SIZE_X = 16;
    public static final int CHUNK_SIZE_Y = 256;
    public static final int CHUNK_SIZE_Z = 16;
    public long compressedDataStored[];
    public long translucentCompressedDataStored[];
    private Block[] CHUNK_BLOCKS = new Block[CHUNK_SIZE_X * CHUNK_SIZE_Y * CHUNK_SIZE_Z];
    public boolean meshGenerated = false;
    public boolean generatingMesh = false;
    public boolean meshBuilded = false;
    private final Vector3f position;
    public List<FaceDistance> faceDistanceList;
    public final ChunkMeshData meshData;
    public final ChunkMeshData translucentMeshData;

    private final Map<Block, IMeshBuildingContext.MeshBuildContext> meshBuildMap = new HashMap<>();

    public Chunk(Vector3f position){
        this.position = position;
        this.translucentMeshData = new ChunkMeshData();
        this.meshData = new ChunkMeshData();
        this.faceDistanceList = new ArrayList<>();
    }

    public Vector3f getPosition(){
        return position;
    }

    public void addBlockToChunk(Block block){
        int x = (int) block.position.x;
        int y = (int) block.position.y;
        int z = (int) block.position.z;

        int relx = x - (int)position.x*CHUNK_SIZE_X;
        int rely = y - (int)position.y*CHUNK_SIZE_Y;
        int relz = z - (int)position.z*CHUNK_SIZE_Z;

        Block newblock = new Block(new Vector3f(relx, rely, relz), block.blockType);
        addBlockToChunkInternal(newblock, relx, rely, relz);
    }

    public void removeBlockFromChunk(int x, int y, int z){
        Block block = getBlockAtPos(x, y, z);
        if(block != null){
            addBlockToChunkInternal(null, x, y, z);
        }
    }

    public Block getBlockAtPos(Vector3f position){
        return getBlockAtPos((int) position.x, (int) position.y, (int) position.z);
    }

    public Block getBlockAtPos(int x, int y, int z){
        int coord = y * CHUNK_SIZE_X + x + z * (CHUNK_SIZE_X * CHUNK_SIZE_Y);
        if(coord >= CHUNK_BLOCKS.length){
            return null;
        }
        if(y<=0){
            return null;
        }
        if(y >= CHUNK_SIZE_Y){
            return null;
        }
        if(x<0){
            return null;
        }
        if(x >= CHUNK_SIZE_X){
            return null;
        }
        if(z<0){
            return null;
        }
        if(z >= CHUNK_SIZE_Z){
            return null;
        }

        return CHUNK_BLOCKS[coord];
    }
    protected void addBlockToChunkInternal(Block block, int x, int y, int z){
        CHUNK_BLOCKS[y * CHUNK_SIZE_X + x + z * (CHUNK_SIZE_X * CHUNK_SIZE_Y)] = block;
    }

    public static boolean canHideFace(Block from, Block target){
        if(target == null) return false;
        return canHideFace(from.blockType, target.blockType);
    }

    public static boolean canHideFace(BlockTypes.BlockType from, BlockTypes.BlockType target){
        assert from != null;
        if(target == null) return false;
        if(from.isTranslucent){
            return true;
        }else{
            if(target.isTranslucent){
                return false;
            }else{
                return true;
            }
        }
    }
    private float getMinDistanceSq(FaceDistance face, Vector3f playerPos) {
        // vertex[6] is min, vertex[7] is max
        float closestX = Math.max(face.vertex[6].x, Math.min(playerPos.x, face.vertex[7].x));
        float closestY = Math.max(face.vertex[6].y, Math.min(playerPos.y, face.vertex[7].y));
        float closestZ = Math.max(face.vertex[6].z, Math.min(playerPos.z, face.vertex[7].z));

        float dx = closestX - playerPos.x;
        float dy = closestY - playerPos.y;
        float dz = closestZ - playerPos.z;

        return dx * dx + dy * dy + dz * dz;
    }
    public void buildTranslucentMesh(){
        translucentMeshData.clear(true);
        faceDistanceList.sort((p1, p2) -> Float.compare(
                Vector3f.sub(p2.vertex[6], Main.player.position, null).length(),
                Vector3f.sub(p1.vertex[6], Main.player.position, null).length()
        ));

        for(FaceDistance d : faceDistanceList){
            for(int i = 0; i < 6; i++) {
                addBlockInfoToData(translucentMeshData, d.vertex[i], d.normals[i], d.tex[i]);
                translucentMeshData.modelOffsets.add(d.offsets[i]);
            }

        }
        if(translucentMeshData.verticesCount > 0) {
            translucentCompressedDataStored = assembleAndCompress(translucentMeshData);
        }
        meshBuilded = false;
    }


    public void faceCulling(List<Block> upFaces, List<Block> downFaces,
                            List<Block> northFaces, List<Block> southFaces,
                            List<Block> westFaces, List<Block> eastFaces, boolean translucent){
        for(int y = 0; y < CHUNK_SIZE_Y; y++){
            for(int x = 0; x < CHUNK_SIZE_X; x++){
                for(int z = 0; z < CHUNK_SIZE_Z; z++){
                    Block block = getBlockAtPos(x, y, z);
                    if(block != null && (block.blockType.isTranslucent == translucent)){
                        int xx = x + (int) position.x * CHUNK_SIZE_X;
                        int yy = y + (int) position.y * CHUNK_SIZE_Y;
                        int zz = z + (int) position.z * CHUNK_SIZE_Z;

                        boolean up, dw, rt, lt, fr, ba;

                        Block bUp = Main.world.getBlockSpecial(this, xx, yy+1, zz);
                        Block bDw = Main.world.getBlockSpecial(this, xx, yy-1, zz);
                        Block bRt = Main.world.getBlockSpecial(this, xx+1, yy, zz);
                        Block bLt = Main.world.getBlockSpecial(this, xx-1, yy, zz);
                        Block bFr = Main.world.getBlockSpecial(this, xx, yy, zz+1);
                        Block bBa = Main.world.getBlockSpecial(this, xx, yy, zz-1);
                        up = canHideFace( block, bUp);
                        dw = canHideFace( block, bDw);
                        rt = canHideFace( block, bRt);
                        lt = canHideFace( block, bLt);
                        fr = canHideFace( block, bFr);
                        ba = canHideFace( block, bBa);

                        if(block.blockType instanceof IMeshBuildingContext context){
                            IMeshBuildingContext.MeshBuildContext ctx = new IMeshBuildingContext.MeshBuildContext(
                                    this, block, Main.world,
                                    bUp, bDw, bRt, bLt, bFr, bBa
                            );
                            boolean[] result = context.processBuildingContext(ctx);
                            // i need to redo this...
                            up = result[0];
                            dw = result[1];
                            rt = result[2];
                            lt = result[3];
                            fr = result[4];
                            ba = result[5];
                            meshBuildMap.put(block, ctx);
                        }
                        if(!block.blockType.canFaceCull){
                            up = dw = rt = lt = fr = ba =false;
                        }

                        if(!up){
                            upFaces.add(block);
                        }
                        if(!dw){
                            downFaces.add(block);
                        }
                        if(!rt){
                            eastFaces.add(block);
                        }
                        if(!lt){
                            westFaces.add(block);
                        }
                        if(!fr){
                            northFaces.add(block);
                        }
                        if(!ba){
                            southFaces.add(block);
                        }
                    }
                }
            }
        }
    }

    public void prepareMesh(){
        faceDistanceList.clear();
        translucentMeshData.clear(true);
        meshData.clear(true);
        meshBuildMap.clear();
        // face culling
        List<Block> upFaces = new ArrayList<>();
        List<Block> downFaces = new ArrayList<>();
        List<Block> northFaces = new ArrayList<>();
        List<Block> southFaces = new ArrayList<>();
        List<Block> westFaces = new ArrayList<>();
        List<Block> eastFaces = new ArrayList<>();
        faceCulling(upFaces, downFaces, northFaces, southFaces, westFaces, eastFaces, true);
        greedyMesh(upFaces, translucentMeshData,  Cube.UP, true);
        greedyMesh(downFaces, translucentMeshData,  Cube.DOWN, true);
        greedyMesh(northFaces, translucentMeshData,  Cube.NORTH, true);
        greedyMesh(southFaces, translucentMeshData,  Cube.SOUTH, true);
        greedyMesh(eastFaces, translucentMeshData,  Cube.EAST, true);
        greedyMesh(westFaces, translucentMeshData,  Cube.WEST, true);
        upFaces.clear();
        downFaces.clear();
        northFaces.clear();
        southFaces.clear();
        westFaces.clear();
        eastFaces.clear();
        buildTranslucentMesh();

        faceCulling(upFaces, downFaces, northFaces, southFaces, westFaces, eastFaces, false);
        greedyMesh(upFaces, meshData,  Cube.UP, false);
        greedyMesh(downFaces, meshData,  Cube.DOWN, false);
        greedyMesh(northFaces, meshData, Cube.NORTH, false);
        greedyMesh(southFaces, meshData, Cube.SOUTH, false);
        greedyMesh(eastFaces, meshData, Cube.EAST, false);
        greedyMesh(westFaces, meshData, Cube.WEST, false);
        if(meshData.verticesCount > 0) {
            compressedDataStored = assembleAndCompress(meshData);
        }
        meshGenerated = true;
        generatingMesh = false;
    }

    public void greedyMesh(List<Block> faces, ChunkMeshData data, int axis, boolean sorted){
        List<Block> meshed = new ArrayList<>();
        List<FaceDistance> f = new ArrayList<>();
        for(Block b : faces){
            IMeshBuildingContext.MeshBuildContext ctx = meshBuildMap.get(b);
            Vector3f p = b.position;
            Vector3f[] face = b.blockType.geometry.getFace(axis);
            if(meshed.contains(b)){
                continue;
            }


            if(!b.blockType.canGreedyMesh){

                Vector3f end2;
                if(axis == 0 || axis == 1) {
                    end2 = new Vector3f(0.5f, 0, 0.5f);
                }else if(axis == 2 || axis == 3){
                    end2 = new Vector3f(0, 0.5f, 0.5f);
                }else{
                    end2 = new Vector3f(0.5f, 0.5f, 0);
                }

                if(!sorted) {

                    addBigFaceToData(ctx, data, face,  p, b, axis);
                }else{

                    FaceDistance d = addBigFaceToData(ctx,face, p, b, axis);
                    Vector3f middle = new Vector3f(this.position.x * CHUNK_SIZE_X + p.x + end2.x, this.position.y * CHUNK_SIZE_Y + p.y + end2.y, this.position.z * CHUNK_SIZE_Z + p.z + end2.z);
                    Vector3f[] v = new Vector3f[8];
                    for(int i = 0; i < 6; i++){
                        v[i] = d.vertex[i];
                    }
                    v[6] = middle;
                    v[7] = middle;
                    f.add(new FaceDistance(d.offsets,v, d.normals, d.tex, 0));
                    d=null;
                }
                continue;
            }

            int finish_i = 0;
            for (int i = 1; i <  Chunk.CHUNK_SIZE_X; i++) {
                Vector3f newPos;
                if(axis == 0 || axis == 1) {
                    newPos = Vector3f.add(p, new Vector3f(i, 0, 0), null);
                }else if(axis == 2 || axis == 3){
                    newPos = Vector3f.add(p, new Vector3f(0, 0, i), null);
                }else{
                    newPos = Vector3f.add(p, new Vector3f(0, i, 0), null);
                }
                Block b1 = getBlockAtPos((int) newPos.x, (int) newPos.y, (int) newPos.z);
                if(b1 == null){
                    break;
                }
                if(!b1.blockType.equals(b.blockType)){
                    break;
                }
                if(!faces.contains(b1)){
                    break;
                }
                if(meshed.contains(b1)){
                    break;
                }
                meshed.add(b1);
                finish_i = i;
            }
            int finish_j = 0;
            for(int j = 1; j <  Chunk.CHUNK_SIZE_Z; j++) {
                boolean allEqual = true;
                List<Block> subMeshed = new ArrayList<>();

                for(int i = 0; i < finish_i+1; i++){
                    Vector3f newPos;
                    if(axis == 0 || axis == 1) {
                        newPos = Vector3f.add(p, new Vector3f(i, 0, j), null);
                    }else if(axis == 2 || axis == 3){
                        newPos = Vector3f.add(p, new Vector3f(0, j, i), null);
                    }else{
                        newPos = Vector3f.add(p, new Vector3f(j, i, 0), null);
                    }
                    Block b1 = getBlockAtPos((int) newPos.x, (int) newPos.y, (int) newPos.z);
                    if(b1 == null || !b1.blockType.equals(b.blockType)){
                        allEqual = false;
                        break;
                    }
                    if(!faces.contains(b1)){
                        allEqual = false;
                        break;
                    }
                    if(meshed.contains(b1)){
                        allEqual = false;
                        break;
                    }
                    subMeshed.add(b1);
                }
                if(allEqual){
                    finish_j = j;
                    meshed.addAll(subMeshed);
                    subMeshed.clear();
                }else{
                    subMeshed.clear();
                    break;
                }
            }
            Vector3f end;
            Vector3f end2;
            if(axis == 0 || axis == 1) {
                end = new Vector3f(p.x + finish_i, p.y, p.z + finish_j);
                end2 = new Vector3f(finish_i, 0, finish_j);
            }else if(axis == 2 || axis == 3){
                end = new Vector3f(p.x, p.y + finish_j, p.z + finish_i);
                end2 = new Vector3f(0, finish_j, finish_i);
            }else{
                end = new Vector3f(p.x + finish_j, p.y + finish_i, p.z );
                end2 = new Vector3f(finish_j, finish_i, 0);
            }
            if(!sorted) {
                addBigFaceToData(ctx, data, face, end, b, axis);
            }else{
                FaceDistance d = addBigFaceToData(ctx, face,end, b, axis);
                Vector3f middle = new Vector3f(this.position.x * CHUNK_SIZE_X + p.x + end2.x, this.position.y * CHUNK_SIZE_Y + p.y + end2.y, this.position.z * CHUNK_SIZE_Z + p.z + end2.z);
                Vector3f[] v = new Vector3f[8];
                for(int i = 0; i < 6; i++){
                    v[i] = d.vertex[i];
                }
                v[6] = new Vector3f(this.position.x * CHUNK_SIZE_X + p.x,  this.position.y * CHUNK_SIZE_Y + p.y, this.position.z * CHUNK_SIZE_Z + p.z);
                v[7] = middle;
                f.add(new FaceDistance(d.offsets, v, d.normals, d.tex, 0));
                d=null;
            }
        }
        if(sorted){
            faceDistanceList.addAll(f);
            f.clear();
        }
    }


    public static void addBlockInfoToData(IMeshBuildingContext.MeshBuildContext context, ChunkMeshData data, Vector3f pos, Block block, int side){
        data.positionList.add(pos.x);
        data.positionList.add(pos.y);
        data.positionList.add(pos.z);
        data.normalList.add((byte) (side/2));
        data.blockTypeList.add((byte) block.blockType.textureGetter.getTextureIdForSide(side));
        data.modelOffsets.add(block.blockType.geometry.getModelOffsets(context, Vector3f.sub(pos, block.position, null), side));
        data.verticesCount += 1;
    }

    public static void addBlockInfoToData(ChunkMeshData data, Vector3f pos, byte normal, byte tex){
        data.positionList.add(pos.x);
        data.positionList.add(pos.y);
        data.positionList.add(pos.z);
        data.normalList.add(normal);
        data.blockTypeList.add(tex);
        data.verticesCount += 1;
    }


    public static void addBigFaceToData(IMeshBuildingContext.MeshBuildContext context, ChunkMeshData data, Vector3f[] face, Vector3f endPos, Block block, int side){
        for(int i = 0; i < 6; i++){
            Vector3f start = face[i];
            Vector3f pos = new Vector3f(start.x + block.position.x, start.y + block.position.y, start.z + block.position.z);
            if(start.z > 0) {
                pos.z += (endPos.z - block.position.z);
            }
            if(start.x > 0){
                pos.x += (endPos.x - block.position.x);
            }
            if(start.y > 0){
                pos.y += (endPos.y - block.position.y);
            }

            addBlockInfoToData(context, data, pos, block, side);
        }
    }
    public static FaceDistance addBigFaceToData(IMeshBuildingContext.MeshBuildContext context, Vector3f[] face, Vector3f endPos, Block block, int side){
        Vector3f[] vertexes = new Vector3f[6];
        byte[] normals = new byte[6];
        byte[] texes =  new byte[6];
        int[] offsets = new int[6];
        for(int i = 0; i < 6; i++){
            Vector3f start = face[i];
            Vector3f pos = new Vector3f(start.x + block.position.x, start.y + block.position.y, start.z + block.position.z);
            if(start.z > 0) {
                pos.z += (endPos.z - block.position.z);
            }
            if(start.x > 0){
                pos.x += (endPos.x - block.position.x);
            }
            if(start.y > 0){
                pos.y += (endPos.y - block.position.y);
            }
            vertexes[i] = new Vector3f(pos.x, pos.y, pos.z);
            normals[i] = (byte) (side/2);
            texes[i] = (byte) block.blockType.textureGetter.getTextureIdForSide(side);
            offsets[i] = block.blockType.geometry.getModelOffsets(context, Vector3f.sub(pos, block.position, null), side);
        }
        return new FaceDistance(offsets, vertexes, normals, texes, 0);
    }

    public static long[] assembleAndCompress(ChunkMeshData data){
        long meshData[] = new long[data.verticesCount];
        for(int j = 0; j < data.positionList.size(); j+=3){
            int x =  data.positionList.get(j).intValue(); // 0 a 16
            int y =  data.positionList.get(j+1).intValue(); // 0 a 255
            int z =  data.positionList.get(j+2).intValue(); // 0 a 16
            meshData[j/3] = (x&31) | ((y&511) << 5) | ((z&31) << 14);;
        }
        for(int i = 0; i < data.verticesCount; i++){
            int normal = data.normalList.get(i) & 3; // 00, 01, 10
            int texture = data.blockTypeList.get(i) & 0xFF;
            int modelOffsets = data.modelOffsets.get(i);
            meshData[i] = (meshData[i] | (normal << 19) | (texture << 21)) | ((long) modelOffsets << 32);
        }
        return meshData;
    }


    public void buildMesh() {
        if(translucentCompressedDataStored != null) {
            translucentMeshData.VAO = Main.StaticLoader.updateVAO(translucentMeshData.VAO, translucentCompressedDataStored);
            translucentMeshData.clear(false);
        }
        if(meshData != null) {
            meshData.VAO = Main.StaticLoader.updateVAO(meshData.VAO, compressedDataStored);
            meshData.clear(false);
        }
        meshBuilded = true;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof Chunk other){
            return this.position.equals(other.position);
        }
        return false;
    }


    public record FaceDistance(int[] offsets, Vector3f[] vertex, byte[] normals, byte[] tex, float distance){};



}
