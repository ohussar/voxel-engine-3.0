package com.ohussar.VoxelEngine.Entities;

import com.ohussar.VoxelEngine.Keyboard;
import com.ohussar.VoxelEngine.Main;
import com.ohussar.VoxelEngine.Util.Maths;
import com.ohussar.VoxelEngine.Util.Util;
import com.ohussar.VoxelEngine.World.Block;
import com.ohussar.VoxelEngine.World.World;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.util.vector.Vector3f;

public class Player {

    private int VAO = -1;
    private int vertexCount = 0;
    public Vector3f position;
    public float speed = 0.035f;
    public Vector3f velocity;
    public Vector3f[] boundingBox = {
            new Vector3f(0.7f, 0, 0.7f),
            new Vector3f(0.7f, 0, 0.2f),//
            new Vector3f(0.2f, 0, 0.7f),
            new Vector3f(0.2f, 0, 0.2f),//
            new Vector3f(0.7f, 1.75f, 0.7f),
            new Vector3f(0.7f, 1.75f, 0.2f),//
            new Vector3f(0.2f, 1.75f, 0.7f),
            new Vector3f(0.2f, 1.75f, 0.2f),//
    };
    private boolean isGrounded = false;

    public Player(Vector3f position){
        this.position = position;
        this.velocity = new Vector3f(0, 0, 0);
//        Vector3f[] vert = Cube.NX_POS.clone();
//        Vector2f[] uvs = Cube.UV.clone();
//        List<Float> vertC = new ArrayList<>();
//        List<Float> uvC = new ArrayList<>();
//        for(int i = 0; i < vert.length; i++){
//            vertC.add(vert[i].x);
//            vertC.add(vert[i].y);
//            vertC.add(vert[i].z);
//        }
//        for(int i = 0; i < uvs.length; i++) {
//            uvC.add(uvs[i].x);
//            uvC.add(uvs[i].y);
//        }
//        float[] v = new float[vertC.size()];
//        float[] u = new float[uvC.size()];
//        for(int j = 0; j < vertC.size(); j++){
//            v[j] = vertC.get(j);
//            vertexCount++;
//        }
//        for(int j = 0; j < uvC.size(); j++){
//            u[j] = uvC.get(j);
//        }

        //this.VAO = Main.StaticLoader.updateVAO(VAO, v, u);
    }

    public void tick(World world, Camera camera){
        this.velocity.translate(0, -0.01f, 0);
        if(Keyboard.isKeyDown(GLFW.GLFW_KEY_SPACE) && isGrounded){
            this.velocity.y = 0.165f;
        }else if(Keyboard.isKeyDown(GLFW.GLFW_KEY_LEFT_CONTROL)){
            //this.velocity.y = -0.1f;
        }else{
            //this.velocity.y = 0.0f;
        }


        isGrounded = false;
        this.move(camera);
        collision(world);
        this.position.translate(this.velocity.x, this.velocity.y, this.velocity.z);
        camera.position = new Vector3f(this.position.x, this.position.y + 1, this.position.z);
    }

    public void collision(World world){
        for (Vector3f point : boundingBox){
            Vector3f futurePosX = new Vector3f(this.position.x + point.x + this.velocity.x, this.position.y + point.y, this.position.z + point.z);
            futurePosX = Util.floorVector(futurePosX);

            if(world.getBlock(futurePosX) != null){
                this.velocity.x = 0;
            }
            Vector3f futurePosZ = new Vector3f(this.position.x + point.x , this.position.y + point.y, this.position.z + point.z + this.velocity.z);
            futurePosZ = Util.floorVector(futurePosZ);
            if(world.getBlock(futurePosZ) != null){
                this.velocity.z = 0;
            }
            Vector3f futurePosY = new Vector3f(this.position.x +point.x, this.position.y + point.y + this.velocity.y, this.position.z + point.z);
            futurePosY = Util.floorVector(futurePosY);
            if(world.getBlock(futurePosY) != null){
                this.velocity.y = 0;
                isGrounded = true;
            }
        }
    }

    public boolean isInside(World world, Block block){
        for (Vector3f point : boundingBox){
            Vector3f actualPos = new Vector3f(this.position.x + point.x, this.position.y + point.y, this.position.z + point.z);
            if(world.getBlock(actualPos) != null ){
                Block b = world.getBlock(actualPos);
                if(b == block){
                    return true;
                }
            }
        }
        return false;
    }


    public void move(Camera camera){
        float mat = 0f;
        float hor = 0;

        if(Keyboard.isKeyDown(GLFW.GLFW_KEY_LEFT_SHIFT)){
            speed = 0.100f;
        }else{
            speed = 0.075f;
        }

        if(Keyboard.isKeyDown(GLFW.GLFW_KEY_W)) {
            mat = -speed;
        } else if(Keyboard.isKeyDown(GLFW.GLFW_KEY_S)){
            mat = speed;
        }

        if(Keyboard.isKeyDown(GLFW.GLFW_KEY_A)) {
            hor = speed;
        }else if(Keyboard.isKeyDown(GLFW.GLFW_KEY_D)){
            hor = -speed;
        }

        float dx = (float) Math.sin(Math.toRadians(camera.rotation.getY())) * -mat;
        float dy = 0;
        float dz = (float) Math.cos(Math.toRadians(camera.rotation.getY())) * mat;
//        if(Keyboard.isKeyDown(Keyboard.KEY_SPACE)){
//            dy += 0.1f;
//        }
//        if(Keyboard.isKeyDown(Keyboard.KEY_LCONTROL)){
//            dy -= 0.1f;
//        }
//        if(!Keyboard.isKeyDown(Keyboard.KEY_SPACE)){
//            space = false;
//        }

        this.velocity.x = Maths.lerp(this.velocity.x, dx + (float) Math.cos(Math.toRadians(camera.rotation.getY())) * -hor, .2f);
        this.velocity.z = Maths.lerp(this.velocity.z, dz + (float) Math.sin(Math.toRadians(camera.rotation.getY())) * -hor, .2f);
    }
    public int getVAO(){
        return this.
                VAO;
    }

    public int getVertexCount() {
        return vertexCount;
    }
}
