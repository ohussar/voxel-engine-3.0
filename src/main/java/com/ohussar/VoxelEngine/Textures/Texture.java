package com.ohussar.VoxelEngine.Textures;

public class Texture {

    public final int width;
    public final int height;
    public final int id;
    public final String name;


    public Texture(int width, int height, int id, String name) {
        this.width = width;
        this.height = height;
        this.id = id;
        this.name = name;
    }
}
