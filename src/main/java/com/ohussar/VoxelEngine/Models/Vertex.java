package com.ohussar.VoxelEngine.Models;

import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

public class Vertex {

    public Vector3f pos;
    public Vector2f uv;
    public Vertex(Vector3f pos, Vector2f uv){
        this.pos = pos;
        this.uv = uv;
    }
}
