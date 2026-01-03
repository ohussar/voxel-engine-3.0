package com.ohussar.VoxelEngine;

import org.lwjgl.Sys;
import org.lwjgl.glfw.GLFW;

public class Keyboard {
    static boolean[] keys = new boolean[360];

    static int[] lastState = new int[360];

    public static void keyPressedLoopRegister(int key){
        if(keys[key]){
            keys[key] = false;
        }



        int state = lastState[key];
        if(state == GLFW.GLFW_PRESS){
            if(GLFW.glfwGetKey(Main.main.getHandle(), key) == GLFW.GLFW_RELEASE) {
                keys[key] = true;
            }else{
                keys[key] = false;
            }
        }else{
            keys[key] = false;
        }

        lastState[key] = GLFW.glfwGetKey(Main.main.getHandle(), key);

    }


    public static boolean isKeyDown(int key){
        return GLFW.glfwGetKey(Main.main.getHandle(), key) == GLFW.GLFW_PRESS;
    }

    public static boolean isKeyPressed(int key){
        return keys[key];
    }

}
