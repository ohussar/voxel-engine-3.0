package com.ohussar.VoxelEngine.Test;

import java.util.Random;

public class PerlinNoiseGenerator {

    public static float AMPLITUDE = 45f;
    public static int OCTAVES = 7;
    public static float ROUGHNESS = 0.4f;
    public float AMP = -1.0f;
    private Random random = new Random();
    private int seed;
    private int xOffset = 0;
    private int zOffset = 0;

    public PerlinNoiseGenerator() {
        this.seed = 0;
    }
    public PerlinNoiseGenerator(int seed, float amplitude) {
        this.seed = seed;
        AMP = amplitude;
    }

    //only works with POSITIVE gridX and gridZ values!
    public PerlinNoiseGenerator(int gridX, int gridZ, int vertexCount, int seed) {
        this.seed = seed;
        xOffset = gridX * (vertexCount-1);
        zOffset = gridZ * (vertexCount-1);
    }

    public float generateHeight(int x, int z) {

        //x = x < 0 ? -x : x;
       // z = z < 0 ? -z : z;

        float total = 0;
        float d = (float) Math.pow(2, OCTAVES-1);
        for(int i=0;i<OCTAVES;i++){
            float freq = (float) (Math.pow(2, i) / d);
            float amp = 1.0f;
            if(AMP != -1f){
                amp = (float) Math.pow(ROUGHNESS, i) * AMP;
            }else{
                amp = (float) Math.pow(ROUGHNESS, i) * AMPLITUDE;
            }

            total += getInterpolatedNoise((x+xOffset)*freq, (z + zOffset)*freq) * amp;
        }

        return total;

    }

    private float getInterpolatedNoise(float x, float z){
        int intX = Math.abs((int) x);
        int intZ = Math.abs((int) z);
        float fracX = Math.abs(x) - Math.abs(intX);
        float fracZ = Math.abs(z) - Math.abs(intZ);

        float v1 = getSmoothNoise(intX, intZ);
        float v2 = getSmoothNoise(intX + 1, intZ);
        float v3 = getSmoothNoise(intX, intZ + 1);
        float v4 = getSmoothNoise(intX + 1, intZ + 1);
        float i1 = interpolate(v1, v2, fracX);
        float i2 = interpolate(v3, v4, fracX);
        return interpolate(i1, i2, fracZ);
    }

    private float interpolate(float a, float b, float blend){
        double theta = blend * Math.PI;
        float f = (float)(1f - Math.cos(theta)) * 0.5f;
        return a * (1f - f) + b * f;
    }

    private float getSmoothNoise(int x, int z) {
        float corners = (getNoise(x - 1, z - 1) + getNoise(x + 1, z - 1) + getNoise(x - 1, z + 1)
                + getNoise(x + 1, z + 1)) / 16f;
        float sides = (getNoise(x - 1, z) + getNoise(x + 1, z) + getNoise(x, z - 1)
                + getNoise(x, z + 1)) / 8f;
        float center = getNoise(x, z) / 4f;
        return corners + sides + center;
    }

    private float getNoise(int x, int z) {
        int factorx = 0;
        if(x < 0){
           // factorx = 3210;
        }
        int factorz = 0;
        if(z < 0){
           // factorz = 1200;
        }
        random.setSeed(Math.abs(x) * (49632 + factorx) + Math.abs(z) * (325176+factorz) + seed);
        return random.nextFloat() * 2f - 1f;
    }

    public float getNoiseT(int x, int z) {
        int factorx = 0;
        if(x < 0){
            //factorx = 3210;
        }
        int factorz = 0;
        if(z < 0){
            //factorz = 1200;
        }
        random.setSeed(x * (49632L + factorx) + z * (325176L+factorz) + seed);
        return random.nextFloat() * 2f - 1f;
    }
    public long hashCoordinates(int x, int y) {
        // Use large prime numbers for better distribution and mix the bits.
        // Bitwise operations (XOR, shifts) help "complexify" the hash.
        long seed = 1664525L * x + 1013904223L;
        seed = seed ^ (seed >> 16);
        seed += 1013904223L * y;
        seed = seed ^ (seed >> 16);
        seed *= 0x5DEECE66DL; // The multiplier used in Java's built-in Random class
        seed += 0xBL;          // The adder used in Java's built-in Random class
        return seed;
    }

    /**
     * Generates a repeatable pseudorandom float value between 0.0 and 1.0
     * for a given (x, y) coordinate pair.
     */
    public float getNoiseValue(int x, int y) {
        // Use the hash result as the seed for a *new* Random object.
        // This ensures the result depends ONLY on the input (x, y) coordinates.
        long seed = hashCoordinates(x, y);
        Random rand = new Random(seed);

        // Use nextFloat() to get a value in the range [0.0, 1.0)
        return rand.nextFloat();
    }


}