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
import org.lwjgl.glfw.GLFW;
import org.lwjgl.util.vector.Vector3f;

import java.util.HashMap;
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
        renderer.prepare();

        StaticShader.start();
        StaticShader.loadViewMatrix(camera);
        world.tick(camera);

        player.tick(world, camera);


        for(Chunk chunk : world.getLoadedChunks()) {
            renderer.renderChunk(chunk, StaticShader, null);
        }
        Keyboard.keyPressedLoopRegister(GLFW.GLFW_KEY_ESCAPE);
        if(Keyboard.isKeyPressed(GLFW.GLFW_KEY_ESCAPE)) {
            isImGUI = !isImGUI;
            //ImGui.setWindowCollapsed(false);
            //ImGui.setWindowFocus();
            if(isImGUI) {
                GLFW.glfwSetInputMode(main.getHandle(), GLFW.GLFW_CURSOR, GLFW.GLFW_CURSOR_NORMAL);
            }else{
                GLFW.glfwSetInputMode(main.getHandle(), GLFW.GLFW_CURSOR, GLFW.GLFW_CURSOR_DISABLED);
            }
        }


        camera.tick();
        StaticShader.stop();
        uiRenderer.render();
        Mouse.lastX = 0;
        Mouse.lastY = 0;
        if(isImGUI) {
            ImGui.sliderFloat("translacao", slider, 0f, 1280-64);
        }

    }
}