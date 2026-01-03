package com.ohussar.VoxelEngine.World;

import com.ohussar.VoxelEngine.Textures.TextureArray;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BlockTypes {


    public static List<BlockType> BLOCKS = new ArrayList<>();
    public static Map<Integer, BlockType> BLOCK_TYPES = new HashMap<>();
    public static final BlockType DIRT = new BlockType(0,  new AllSideGetter("dirt"));
    public static final BlockType STONE = new BlockType(1, new AllSideGetter("stone"));
    public static final BlockType GRASS = new BlockType(2, new GrassTextureGetter());
    public static class BlockType {

        public final int id;

        public final TextureSideGetter textureGetter;

        public BlockType(int id, TextureSideGetter texture){
            this.id = id;
            this.textureGetter = texture;
            BLOCK_TYPES.put(id, this);
        }

    }

    public interface TextureSideGetter {

        public int getTextureIdForSide(int side);

    }

    public static class AllSideGetter implements TextureSideGetter {
        private String tex;
        public AllSideGetter(String texture){
            this.tex = texture;
        }
        @Override
        public int getTextureIdForSide(int side) {
            return TextureArray.getTextureWorld(tex);
        }
    }

    public static class GrassTextureGetter implements TextureSideGetter{
        public final String SIDES = "grass_side";
        public final String TOP = "grass_top";
        public final String BOTTOM = "grass_bottom";
        @Override
        public int getTextureIdForSide(int side) {

            if(side == 2 || side == 3 || side == 4 || side == 5){
                return TextureArray.getTextureWorld(SIDES);
            }
            if(side == 1){
                return TextureArray.getTextureWorld(BOTTOM);
            }

            return TextureArray.getTextureWorld(TOP);
        }
    }

}
