package com.ohussar.VoxelEngine.Util;

import org.lwjgl.util.vector.Vector3f;

public class Util {
    public static Vector3f EmptyVec3(){
        return new Vector3f(0, 0,0 );
    }
    public static Vector3f floorVector(Vector3f vec){
        return new Vector3f((float)Math.floor(vec.x), (float)Math.floor(vec.y), (float)Math.floor(vec.z));
    }
}
