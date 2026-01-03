package com.ohussar.VoxelEngine;

import imgui.app.Configuration;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWCursorPosCallback;

public class Mouse {
    public static float lastX;
    public static float lastY;

    public static GLFWCursorPosCallback cursorPosCallback =  new GLFWCursorPosCallback() {

        @Override
        public void invoke(long l, double v, double v1) {
            if(!Main.isImGUI){
                lastX = (float) v;
                lastY = (float) v1;
                GLFW.glfwSetCursorPos(l, (float) 0, (float) 0);
            }

        }
    };



    public static boolean isButtonDown(int btn){
        if(Main.isImGUI) return false;
        return GLFW.glfwGetMouseButton(Main.main.getHandle(), btn) == GLFW.GLFW_PRESS;
    }

    public static float getDY(){
        double[] x = new double[1];
        double[] y = new double[1];
        GLFW.glfwGetCursorPos(Main.main.getHandle(), x, y);
        return  -lastY;
    }
    public static float getDX(){
        double[] x = new double[1];
        double[] y = new double[1];
        GLFW.glfwGetCursorPos(Main.main.getHandle(), x, y);
        return  lastX;

    }


}
