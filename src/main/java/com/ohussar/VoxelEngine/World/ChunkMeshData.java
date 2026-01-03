package com.ohussar.VoxelEngine.World;

import com.ohussar.VoxelEngine.Main;
import org.lwjgl.opengl.GL;

import java.util.ArrayList;
import java.util.List;

public class ChunkMeshData {

    public final List<Float> positionList;
    public final List<Byte> blockTypeList;
    public List<Byte> normalList;
    public int verticesCount = 0;

    public int VAO = -1;
    public ChunkMeshData() {
        this.positionList = new ArrayList<>();
        this.blockTypeList = new ArrayList<>();
        this.normalList = new ArrayList<>();
        //VAO = Main.StaticLoader.updateVAO(VAO, new float[]{}, new byte[]{});
    }
}
