package com.ohussar.VoxelEngine.World;

import com.ohussar.VoxelEngine.Main;
import org.lwjgl.util.vector.Vector3f;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class ChunkMeshPreparingHandler {


    public static final List<Chunk> awaitingPrepare = Collections.synchronizedList(new ArrayList<>());


    public static void run(){
        while(true) {

            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                System.exit(400);
            }
            List<Chunk> indexesToRemove = new ArrayList<>();
            int sizeAtStart =  awaitingPrepare.size();
            for (int j = 0; j < sizeAtStart; j++) {
                Chunk chunk = awaitingPrepare.get(j);
                Chunk[] adjacent = getAdjacentChunks(chunk);
                boolean ready = true;
                for (int i = 0; i < adjacent.length; i++) {
                    if (adjacent[i] == null) {
                        ready = false;
                        break;
                    }
                }
                if (ready) {
                    chunk.generatingMesh = true;
                    chunk.prepareMesh();
                    indexesToRemove.add(chunk);
                }
                if(!Main.world.getLoadedChunks().contains(chunk)){
                    indexesToRemove.add(chunk);
                }
            }
            int c = 0;
            for(Chunk index : indexesToRemove){
                c++;
                awaitingPrepare.remove(index);
            }
            if(c>0) {
                System.out.println("Attempted remove " + c + " chunks.");
            }
        }
    }

    public static void addChunkToQueue(Chunk chunk){
        if(!awaitingPrepare.contains(chunk)){
            awaitingPrepare.add(chunk);
        }
    }



    public static Chunk[] getAdjacentChunks(Chunk chunk){
        Vector3f left = Vector3f.add(chunk.getPosition(), new Vector3f(-1, 0, 0), null);
        Vector3f right = Vector3f.add(chunk.getPosition(), new Vector3f(1, 0, 0), null);
        Vector3f front = Vector3f.add(chunk.getPosition(), new Vector3f(0, 0, 1), null);
        Vector3f back =  Vector3f.add(chunk.getPosition(), new Vector3f(0, 0, -1), null);
        Chunk c1 = Main.world.getChunkFromPos(left);
        Chunk c2 = Main.world.getChunkFromPos(right);
        Chunk c3 = Main.world.getChunkFromPos(front);
        Chunk c4 = Main.world.getChunkFromPos(back);
        return new Chunk[]{c1, c2, c3, c4};
    }

}
