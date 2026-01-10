package com.ohussar.VoxelEngine.World;

import com.ohussar.VoxelEngine.Main;
import com.ohussar.VoxelEngine.Util.Vec3i;
import org.lwjgl.util.vector.Vector3f;

import java.util.ArrayList;
import java.util.List;

public class ViewCulling {

    public static List<World.ChunkDist> cullChunks(){
        int visibleChunks = 0;
        float yrot = Main.camera.getRotation().y + 180;
        Vector3f lookVec = new Vector3f((float)-Math.sin(Math.toRadians(yrot)), 0, (float)Math.cos(Math.toRadians(yrot)));
        lookVec = lookVec.normalise(lookVec);

        List<World.ChunkDist> chunkDistList = new ArrayList<>();

        for(Chunk chunk : Main.world.getLoadedChunks()) {
            Vector3f cPos = Main.camera.getPosition();
            Vec3i chunkCameraPos = new Vec3i((int)Math.floor(cPos.x/16), 0, (int)Math.floor(cPos.z/16));
            if(chunk != null){
                Vector3f dir = new Vector3f(0, 0, 0);
                Vector3f dist = new Vector3f(0, 0, 0);
                dist = Vector3f.sub(chunk.getPosition(), chunkCameraPos.toVec3f(), dist);
                dir = dist.normalise(null);

                double angle = Vector3f.dot(dir, lookVec);
                if(angle >= 0.3 || dist.length() < 2.5){
                    if(!chunk.meshBuilded && !chunk.generatingMesh && chunk.meshGenerated){
                        chunk.buildMesh();
                    }
                    if(!chunk.meshBuilded && !chunk.generatingMesh && !chunk.meshGenerated){
                        ChunkMeshPreparingHandler.addChunkToQueue(chunk);
                    }

                    chunkDistList.add(new World.ChunkDist(chunk, dist.length()));
                    visibleChunks ++;
                }

            }


        }
        chunkDistList.sort((p1, p2) -> Float.compare(p2.dist, p1.dist));
        return chunkDistList;
    }


}
