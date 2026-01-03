package com.ohussar.VoxelEngine.Entities;

import com.ohussar.VoxelEngine.Main;
import com.ohussar.VoxelEngine.Mouse;
import com.ohussar.VoxelEngine.Textures.TextureArray;
import com.ohussar.VoxelEngine.Util.Vec3i;
import com.ohussar.VoxelEngine.World.Block;
import com.ohussar.VoxelEngine.World.BlockTypes;
import org.lwjgl.Sys;
import org.lwjgl.util.vector.Vector3f;

import java.util.ArrayList;
import java.util.List;

import static com.ohussar.VoxelEngine.Entities.Cube.*;
import static com.ohussar.VoxelEngine.Entities.Cube.NZ_POS;

public class Camera {
    Vector3f position;
    Vector3f rotation;
    Vector3f momentum;
    float speed = 0.1f;
    private boolean space = false;
    private boolean mouse0 = false;
    private boolean mouse1 = false;


    public int VAOBlockSelect = -1;
    public Block selectedBlock = null;

    public int VAO = -1;

    public Camera(Vector3f position, Vector3f rotation) {
        this.position = position;
        this.rotation = rotation;
        this.momentum = new Vector3f(0, 0, 0);
        //this.VAO = Main.StaticLoader.updateVAO(VAO, new float[]{}, new float[]{});
    }

    public void tick() {
        if (Mouse.isButtonDown(1) && !mouse1) {
            mouse1 = true;
            placeBlock();
        }
        if (!Mouse.isButtonDown(1)) {
            mouse1 = false;
        }
        if(!Mouse.isButtonDown(0)){
            mouse0 = false;
        }
        if (Mouse.isButtonDown(0) && !mouse0) {
            mouse0 = true;
            Block block = raycastFromCameraRotation();
            if(block != null){
                Main.world.removeBlock(new Vec3i(block.position));
            }
        }


        Block block = raycastFromCameraRotation();
        selectedBlock = block;

        rotation.x += 0.4f*-Mouse.getDY();
        rotation.y += 0.4f*Mouse.getDX();
        //this.position.translate(momentum.x, momentum.y, momentum.z);
    }

    public void placeBlock(){
        float stepsize = 0.005f;
        float maxSteps = 1000;
        float maxBlockDist = stepsize * maxSteps;
        float dy = -(float) Math.sin(Math.toRadians(rotation.getX())) * stepsize;
        int i = 0;
        float xx = this.position.x;
        float yy = this.position.y;
        float zz = this.position.z;

        float angy = (float) Math.toRadians(rotation.getX());
        float dx = -(1 / (float) Math.tan(angy)) * dy * (float) Math.sin(Math.toRadians(rotation.getY()));
        float dz = (1 / (float) Math.tan(angy)) * dy * (float) Math.cos(Math.toRadians(rotation.getY()));

         Vec3i previousPos = new Vec3i(0, 0, 0);

        float delta = 1 - (float) Math.sqrt((Math.abs(Math.sin(Math.toRadians(rotation.getX())))));
        Vec3i playerPos =  new Vec3i(Math.round(Main.player.position.getX()),
                Math.round(Main.player.position.getY()),
                Math.round(Main.player.position.getZ()));
        Vec3i playerPos1 = new Vec3i(Math.round(Main.player.position.getX()),
                Math.round(Main.player.position.getY()) + 1,
                Math.round(Main.player.position.getZ()));
        while (i < maxSteps) {
            i++;
            float beforeX = xx;
            float beforeY = yy;
            float beforeZ = zz;
            xx += dx;
            yy += dy;
            zz += dz;
            Vec3i pos = new Vec3i(new Vector3f((int) Math.round(xx), (int) Math.round(yy), (int) Math.round(zz)));

            if (!previousPos.equals(pos)) {
                previousPos = pos;
                Block block = Main.world.getBlock(pos.toVec3f());
                if (block != null) {
                    Vec3i ray1 = new Vec3i(new Vector3f((int) Math.round(xx), (int) Math.round(beforeY), (int) Math.round(beforeZ)));
                    Block block1 = Main.world.getBlock(ray1.toVec3f());
                    if(block1 != null){
                        Vec3i newPos = ray1.translate(-(int)Math.signum(dx), 0,0);
                        System.out.println(newPos);
                        System.out.println(playerPos);
                        System.out.println(playerPos1);

                        if(newPos.equals(playerPos) || newPos.equals(playerPos1)){

                            break;
                        }

                        Main.world.placeBlock(new Block(newPos.toVec3f(), BlockTypes.STONE.id));
                        break;
                    }
                    Vec3i ray2 = new Vec3i(new Vector3f((int) Math.round(beforeX), (int) Math.round(yy), (int) Math.round(beforeZ)));
                    Block block2 = Main.world.getBlock(ray2.toVec3f());
                    if(block2 != null){
                        Vec3i newPos = ray2.translate(0, -(int)Math.signum(dy),0);
                        if(newPos.equals(playerPos) || newPos.equals(playerPos1)){
                            break;
                        }
                        Main.world.placeBlock(new Block(newPos.toVec3f(), BlockTypes.STONE.id));
                        break;
                    }
                    Vec3i ray3 = new Vec3i(new Vector3f((int) Math.round(beforeX), (int) Math.round(beforeY), (int) Math.round(zz)));
                    Block block3 = Main.world.getBlock(ray3.toVec3f());
                    if(block3 != null){
                        Vec3i newPos = ray3.translate(0, 0,-(int)Math.signum(dz));
                        if(newPos.equals(playerPos) || newPos.equals(playerPos1)){
                            break;
                        }
                        Main.world.placeBlock(new Block(newPos.toVec3f(), BlockTypes.STONE.id));
                        break;
                    }
                    break;
                }
            }
        }
    }



    public Block raycastFromCameraRotation() {
        float stepsize = 0.005f;
        float maxSteps = 1000;
        float maxBlockDist = stepsize * maxSteps;
        float dy = -(float) Math.sin(Math.toRadians(rotation.getX())) * stepsize;
        int i = 0;
        float xx = this.position.x;
        float yy = this.position.y;
        float zz = this.position.z;

        float angy = (float) Math.toRadians(rotation.getX());
        float dx = -(1 / (float) Math.tan(angy)) * dy * (float) Math.sin(Math.toRadians(rotation.getY()));
        float dz = (1 / (float) Math.tan(angy)) * dy * (float) Math.cos(Math.toRadians(rotation.getY()));

        Vec3i previousPos = new Vec3i(0, 0, 0);

        float delta = 1 - (float) Math.sqrt((Math.abs(Math.sin(Math.toRadians(rotation.getX())))));
        Block found = null;
        while (i < maxSteps) {
            i++;
            xx += dx;
            yy += dy;
            zz += dz;
            Vec3i pos = new Vec3i(new Vector3f((int) Math.round(xx), (int) Math.round(yy), (int) Math.round(zz)));
            if (!previousPos.equals(pos)) {
                previousPos = pos;
                Block block = Main.world.getBlock(pos.toVec3f());
                if (block != null) {
                    found = block;
                    break;
                }
            }
        }
        return found;
    }

    public void move(){
        float mat = 0f;
        float hor = 0;
//        if(Keyboard.isKeyDown(Keyboard.KEY_W)) {
//            mat = -speed;
//        } else if(Keyboard.isKeyDown(Keyboard.KEY_S)){
//            mat = speed;
//        }
//
//        if(Keyboard.isKeyDown(Keyboard.KEY_A)) {
//            hor = speed;
//        }else if(Keyboard.isKeyDown(Keyboard.KEY_D)){
//            hor = -speed;
//        }


        float dx = (float) Math.sin(Math.toRadians(rotation.getY())) * -mat;
        float dy = 0;
        float dz = (float) Math.cos(Math.toRadians(rotation.getY())) * mat;
//        if(Keyboard.isKeyDown(Keyboard.KEY_SPACE)){
//            dy += 0.1f;
//        }
//        if(Keyboard.isKeyDown(Keyboard.KEY_LCONTROL)){
//            dy -= 0.1f;
//        }
//        if(!Keyboard.isKeyDown(Keyboard.KEY_SPACE)){
//            space = false;
//        }

        position.translate(dx, dy, dz);
        dx = (float) Math.cos(Math.toRadians(rotation.getY())) * -hor;
        dz = (float) Math.sin(Math.toRadians(rotation.getY())) * -hor;
        position.translate(dx, 0, dz);
    }


    public Vector3f getPosition() {
        return position;
    }
    public Vector3f getRotation() {
        return rotation;
    }
}
