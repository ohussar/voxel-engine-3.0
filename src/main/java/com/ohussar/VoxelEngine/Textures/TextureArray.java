package com.ohussar.VoxelEngine.Textures;

import org.lwjgl.Sys;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL42;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class TextureArray {

    public static int worldId = -1;
    public static int uiId = -1;

    private static Map<String, Texture> worldTextureMap = new HashMap<>();
    private static Map<String, Texture> uiTextureMap = new HashMap<>();
    public static void loadTextures(){
        loadWorldTextures();
        loadUITextures();
    }

    public static void loadUITextures(){
        uiId = GL11.glGenTextures();
        GL11.glBindTexture(GL30.GL_TEXTURE_2D_ARRAY, uiId);
        String folder = "src/main/resources/UI/";
        loadTextures(folder, uiTextureMap);
        GL11.glBindTexture(GL30.GL_TEXTURE_2D_ARRAY, 0);
    }

    public static void loadWorldTextures(){
        worldId = GL11.glGenTextures();
        GL11.glBindTexture(GL30.GL_TEXTURE_2D_ARRAY, worldId);

        String folder = "src/main/resources/";
        loadTextures(folder, worldTextureMap);
        GL11.glBindTexture(GL30.GL_TEXTURE_2D_ARRAY, 0);
    }

    private static void loadTextures(String folder, Map<String, Texture> map){
        Set<String> files = Stream.of(Objects.requireNonNull(new File(folder).listFiles())).map(File::getName).collect(Collectors.toSet());
        GL42.glTexStorage3D(GL30.GL_TEXTURE_2D_ARRAY, 1, GL11.GL_RGBA8,  16, 16, files.size());
        int iteration = 0;
        for(String file : files){
            if(new File(folder + file).isDirectory()){
                continue;
            }
            BufferedImage image = null;
            try {
                image = ImageIO.read(new File(folder + file));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            if(image.getData().getDataBuffer() instanceof DataBufferByte e){
                byte[] pixels = e.getData();
                ByteBuffer buffer = ByteBuffer.allocateDirect(image.getData().getDataBuffer().getSize());
                byte[] corrected = convertAbgrToRgba(pixels);
                buffer.put(corrected);
                buffer.position(0);
                System.out.println(iteration);
                GL12.glTexSubImage3D(GL30.GL_TEXTURE_2D_ARRAY, 0, 0, 0, iteration, image.getWidth(), image.getHeight(), 1, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, buffer);
                map.put(file.split(".png")[0],
                        new Texture(image.getWidth(), image.getHeight(), iteration, file.split(".png")[0]));
                iteration++;
            }
        }
    }



    public static int getTextureWorld(String name){
        return worldTextureMap.get(name).id;
    }

    public static int getTextureUI(String name){
        return uiTextureMap.get(name).id;
    }

    public static Map<String, Texture> getUiTextureMap(){
        return uiTextureMap;
    }

    public static byte[] convertAbgrToRgba(byte[] abgrData) {
        if (abgrData == null || abgrData.length % 4 != 0) {
            throw new IllegalArgumentException("Input data must be a valid ABGR byte array with a length divisible by 4.");
        }

        byte[] rgbaData = new byte[abgrData.length];

        for (int i = 0; i < abgrData.length; i += 4) {
            // The input array has bytes ordered as: A, B, G, R

            // Read the components from the ABGR array
            byte a = abgrData[i];
            byte b = abgrData[i + 1];
            byte g = abgrData[i + 2];
            byte r = abgrData[i + 3];

            // Write the components to the RGBA array in the correct order: R, G, B, A
            rgbaData[i] = r;
            rgbaData[i + 1] = g;
            rgbaData[i + 2] = b;
            rgbaData[i + 3] = a;
        }

        return rgbaData;
    }

}
