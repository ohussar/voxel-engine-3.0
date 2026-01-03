package com.ohussar.VoxelEngine;

import com.ohussar.VoxelEngine.Entities.Camera;
import com.ohussar.VoxelEngine.Entities.Player;
import com.ohussar.VoxelEngine.Shaders.StaticShader;
import com.ohussar.VoxelEngine.Shaders.UIShader;
import com.ohussar.VoxelEngine.Textures.TextureArray;
import com.ohussar.VoxelEngine.UI.UIRenderer;
import com.ohussar.VoxelEngine.Util.Vec3i;
import com.ohussar.VoxelEngine.World.Chunk;
import com.ohussar.VoxelEngine.World.World;
import imgui.ImGui;
import imgui.app.Application;
import imgui.app.Configuration;
import org.lwjgl.Sys;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.util.vector.Vector3f;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Main extends Application {
    public static final int FRAMERATE = 75;

    public static MemoryLoader StaticLoader = new MemoryLoader();
    public static StaticShader StaticShader = null;
    public static UIShader UIShader = null;
    public static Renderer renderer;
    public static UIRenderer uiRenderer;
    static Map<Vec3i, Chunk> chunks = new HashMap<Vec3i, Chunk>();
    public static Camera camera;
    public static World world;
    public static Player player;

    public static Configuration config;

    public static Main main;
    public static float[] slider = new float[1];

    public static boolean isImGUI = false;
    public long frameNumber = 0;
    public double frameTime = 0;
    public double cullingTime = 0;
    public double renderTimeA = 0;

    public static void main(String[] args) {
        main = new Main();
        launch(main);

    }


    @Override
    protected void configure(Configuration config) {
        config.setWidth(1280);
        config.setHeight(720);
        config.setTitle("Voxel Engine");
        Main.config = config;
    }

    public Main(){

    }

    @Override
    protected void preRun() {
        TextureArray.loadTextures();
        GLFW.glfwShowWindow(main.getHandle());
        StaticShader = new StaticShader();
        Main.UIShader = new UIShader();
        renderer = new Renderer(StaticShader);
        camera = new Camera(new Vector3f(0, 80, 0), new Vector3f(0, 0, 0));
        world = new World();
        player = new Player(new Vector3f(0, 40, 0));

        uiRenderer = new UIRenderer(Main.UIShader);
        GLFW.glfwSetCursorPosCallback(main.getHandle(), Mouse.cursorPosCallback);
        GLFW.glfwSetInputMode(main.getHandle(), GLFW.GLFW_CURSOR, GLFW.GLFW_CURSOR_DISABLED);
    }

    @Override
    public void process() {
        long startTime = System.nanoTime();
        renderer.prepare();

        world.tick(camera);
        player.tick(world, camera);
        camera.tick();
        StaticShader.start();
        StaticShader.loadViewMatrix(camera);


        int visibleChunks = 0;
        float yrot = camera.getRotation().y + 180;
        Vector3f lookVec = new Vector3f((float)-Math.sin(Math.toRadians(yrot)), 0, (float)Math.cos(Math.toRadians(yrot)));
        lookVec = lookVec.normalise(lookVec);

        List<World.ChunkDist> chunkDistList = new ArrayList<>();
        long cullingStartTime = System.nanoTime();
        for(Chunk chunk : world.getLoadedChunks()) {
            Vector3f cPos = camera.getPosition();
            Vec3i chunkCameraPos = new Vec3i((int)Math.floor(cPos.x/16), 0, (int)Math.floor(cPos.z/16));
            if(chunk != null){
                Vector3f dir = new Vector3f(0, 0, 0);
                Vector3f dist = new Vector3f(0, 0, 0);
                dist = Vector3f.sub(chunk.getPosition(), chunkCameraPos.toVec3f(), dist);
                dir = dist.normalise(null);

                double angle = Vector3f.dot(dir, lookVec);
                if(angle >= 0.3 || dist.length() < 2.5){
                    if(!chunk.meshGenerated){
                        chunk.buildMesh();
                    }
                    chunkDistList.add(new World.ChunkDist(chunk, dist.length()));
                    visibleChunks ++;
                }

            }


        }
        chunkDistList.sort((p1, p2) -> Float.compare(p2.dist, p1.dist));
        long cullingEndTime = System.nanoTime() - cullingStartTime;
        long renderStartTime = System.nanoTime();
        for(World.ChunkDist chunkDist : chunkDistList){
            renderer.renderChunk(chunkDist.chunk, StaticShader, null);
        }
        long renderTime =  System.nanoTime() - renderStartTime;


        Keyboard.keyPressedLoopRegister(GLFW.GLFW_KEY_ESCAPE);
        Keyboard.keyPressedLoopRegister(GLFW.GLFW_KEY_SPACE);
        if(Keyboard.isKeyPressed(GLFW.GLFW_KEY_ESCAPE)) {
            isImGUI = !isImGUI;
            if(isImGUI) {
                GLFW.glfwSetInputMode(main.getHandle(), GLFW.GLFW_CURSOR, GLFW.GLFW_CURSOR_NORMAL);
            }else{
                GLFW.glfwSetInputMode(main.getHandle(), GLFW.GLFW_CURSOR, GLFW.GLFW_CURSOR_DISABLED);
            }
        }
        if(camera.selectedBlock != null){
            Main.renderer.drawBlockOutline(camera.selectedBlock.position);
        }

        StaticShader.stop();
        uiRenderer.render();
        Mouse.lastX = 0;
        Mouse.lastY = 0;
        if(isImGUI) {
            ImGui.sliderFloat("translacao", slider, 0f, 1280-64);
        }
        double totaltime = (System.nanoTime() - startTime)/1000000000d;
        frameNumber++;
        frameTime += totaltime;
        cullingTime += cullingEndTime/1000000d;
        renderTimeA += renderTime/1000000d;

        ImGui.begin("Info");
        ImGui.setWindowSize(300, 200);
        ImGui.text("Fps: " + 1/(frameTime/frameNumber));
        ImGui.text("Culling: "+ cullingTime/frameNumber +" ms ");
        ImGui.text("Render: " + renderTimeA/frameNumber + " ms");
        int total = world.getLoadedChunks().size();
        ImGui.text("Visible  Chunks: " + visibleChunks + " Total Chunks: " + total);
        ImGui.end();
        if(frameNumber >= 60){
            frameNumber = 0;
            frameTime = 0;
            cullingTime = 0;
            renderTimeA = 0;
        }

    }
}