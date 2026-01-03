package com.ohussar.VoxelEngine.Util;

import org.lwjgl.util.vector.Vector3f;

public class Vec3i {
    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getZ() {
        return z;
    }

    public void setZ(int z) {
        this.z = z;
    }

    private int x;
    private int y;
    private int z;


    public Vec3i(int x, int y, int z){
        this.x = x;
        this.y = y;
        this.z = z;
    }
    public Vec3i(Vector3f vec3){
        this.x = (int) vec3.x;
        this.y = (int) vec3.y;
        this.z = (int) vec3.z;
    }

    public Vector3f toVec3f(){
        return new Vector3f(this.x, this.y, this.z);
    }

    public Vec3i copy(){
        return new Vec3i(this.x, this.y, this.z);
    }

    public Vec3i translate(int x, int y, int z){
        this.x += x;
        this.y += y;
        this.z += z;
        return this;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj == null) return false;
        if(obj.getClass() != this.getClass()) return false;
        Vec3i other = (Vec3i) obj;
        return this.getX() == other.getX() && this.getY() == other.getY() && this.getZ() == other.getZ();
    }

    @Override
    public String toString() {
        return "[" + Integer.toString(x) + "," + Integer.toString(y) + "," + Integer.toString(z) + "]";
    }

    @Override
    public int hashCode() {
        return x + y << 4 + z << 8;
    }
}
