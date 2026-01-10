package com.ohussar.VoxelEngine;

import com.ohussar.VoxelEngine.Entities.Camera;
import com.ohussar.VoxelEngine.Entities.Player;
import com.ohussar.VoxelEngine.Shaders.StaticShader;
import com.ohussar.VoxelEngine.Shaders.UIShader;
import com.ohussar.VoxelEngine.Textures.TextureArray;
import com.ohussar.VoxelEngine.UI.UIRenderer;
import com.ohussar.VoxelEngine.Util.Vec3i;
import com.ohussar.VoxelEngine.World.Chunk;
import com.ohussar.VoxelEngine.World.ChunkMeshPreparingHandler;
import com.ohussar.VoxelEngine.World.ViewCulling;
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
import java.util.concurrent.TimeUnit;

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

    public int timer = 0;

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
        camera = new Camera(new Vector3f(0, 45, 0), new Vector3f(0, 0, 0));
        world = new World();
        player = new Player(new Vector3f(0, 45, 0));
        world.tick(camera);
        uiRenderer = new UIRenderer(Main.UIShader);


        Thread worldTickThread = new Thread(this::tick);
        worldTickThread.setName("World Tick Thread");
        worldTickThread.start();
        Thread chunkPrepare = new Thread(ChunkMeshPreparingHandler::run);
        chunkPrepare.setName("Chunk Preparer");
        chunkPrepare.start();

        GLFW.glfwSetCursorPosCallback(main.getHandle(), Mouse.cursorPosCallback);
        GLFW.glfwSetInputMode(main.getHandle(), GLFW.GLFW_CURSOR, GLFW.GLFW_CURSOR_DISABLED);
    }

    public void tick()  {
        double timerPerTickIdeal = 1000f*(1/20f);

        while(true) {
            long startTime = System.nanoTime();
            world.tick(camera);
            long endTime = System.nanoTime();

            double timeTaken = (endTime - startTime)/100000d;

            if(!(timeTaken > timerPerTickIdeal)){
                double timeRemaining = timerPerTickIdeal - timeTaken;
                try {
                    Thread.sleep((long) timeRemaining);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }


    @Override
    public void process() {
        timer++;
        if(timer < 120){
            return;
        }
        long startTime = System.nanoTime();
        renderer.prepare();
        player.tick(world, camera);
        camera.tick();

        long translucentStart =  System.nanoTime();
        if(!player.previousBlockPos.equals(player.blockPos)){
            Chunk c = world.getChunkFromBPos(player.blockPos);
            if(c!= null){
                Chunk c1 = world.getChunkFromPos(new Vec3i((int)(c.getPosition().getX()) + 1, (int)c.getPosition().getY(), (int)c.getPosition().getZ()));
                Chunk c2 = world.getChunkFromPos(new Vec3i((int)c.getPosition().getX() - 1, (int)c.getPosition().getY(), (int)c.getPosition().getZ()));
                Chunk c3 = world.getChunkFromPos(new Vec3i((int)c.getPosition().getX(), (int)c.getPosition().getY(), (int)c.getPosition().getZ()+1));
                Chunk c4 = world.getChunkFromPos(new Vec3i((int)c.getPosition().getX(), (int)c.getPosition().getY(), (int)c.getPosition().getZ()-1));
                c.buildTranslucentMesh();
                c1.buildTranslucentMesh();
                c2.buildTranslucentMesh();
                c3.buildTranslucentMesh();
                c4.buildTranslucentMesh();
            }
        }
        long translucentTime = System.nanoTime() - translucentStart;


        StaticShader.start();
        StaticShader.loadViewMatrix(camera);

        long cullingStartTime = System.nanoTime();
        List<World.ChunkDist>  chunkDistList = ViewCulling.cullChunks();
        int visibleChunks = chunkDistList.size();
        long cullingEndTime = System.nanoTime() - cullingStartTime;

        long renderStartTime = System.nanoTime();
        for(World.ChunkDist chunkDist : chunkDistList){
            renderer.renderChunk(chunkDist.chunk, StaticShader, null);
        }

        long renderTime =  System.nanoTime() - renderStartTime;


        Keyboard.keyPressedLoopRegister(GLFW.GLFW_KEY_ESCAPE);
        Keyboard.keyPressedLoopRegister(GLFW.GLFW_KEY_SPACE);
        Keyboard.keyPressedLoopRegister(GLFW.GLFW_KEY_E);
        if(Keyboard.isKeyPressed(GLFW.GLFW_KEY_E)){
            renderer.renderMesh = !renderer.renderMesh;
        }
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
        cullingTime += (double) TimeUnit.NANOSECONDS.toMillis(cullingEndTime);
        renderTimeA += (double) TimeUnit.NANOSECONDS.toMillis(renderTime);

        ImGui.begin("Info");
        ImGui.setWindowSize(300, 200);
        ImGui.text("Fps: " + 1/(frameTime/frameNumber));
        ImGui.text("Culling: "+ cullingTime/frameNumber +" ms ");
        ImGui.text("Render: " + renderTimeA/frameNumber + " ms");
        ImGui.text("Translucent build: " + translucentTime + " ns");
        ImGui.text("x: " + player.blockPos.getX() + " y: " + player.blockPos.getY() + " z: " + player.blockPos.getZ());
        Chunk c = world.getChunkFromBPos(player.blockPos);
        ImGui.text("ChunkPos | x: " + c.getPosition().getX() + " y: " + c.getPosition().getY() + " z: " + c.getPosition().getZ());
        int total = world.getLoadedChunks().size();
        ImGui.text("Visible  Chunks: " + visibleChunks + " Total Chunks: " + total);
        ImGui.text("Chunks awaiting prepare: " + ChunkMeshPreparingHandler.awaitingPrepare.size());
        ImGui.end();
        if(frameNumber >= 60){
            frameNumber = 0;
            frameTime = 0;
            cullingTime = 0;
            renderTimeA = 0;
        }
        chunkDistList.clear();

    }
}