package com.ohussar.VoxelEngine.World.Blocks;

import com.ohussar.VoxelEngine.Entities.Cube;
import com.ohussar.VoxelEngine.Entities.IBlockGeometry;
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
    public static final BlockType SAND = new BlockType(3, new AllSideGetter("sand"));
    public static final BlockType LOG = new BlockType(4, new PillarTextureGetter("log_side", "log_vertical"));
    public static final BlockType LEAVES = new BlockType(5, new AllSideGetter("leaves"), true,true, false).setFaceCull(false);
    public static final BlockType WATER = new WaterBlockType(6);

    public static class BlockType {

        public final int id;

        public final TextureSideGetter textureGetter;

        public final boolean isTranslucent;
        public final boolean canGreedyMesh;
        public final boolean isFullBlock;
        public boolean canFaceCull = true;
        public IBlockGeometry geometry;

        public BlockType setFaceCull(boolean canFaceCull) {
            this.canFaceCull = canFaceCull;
            return this;
        }

        public BlockType addGeometry(IBlockGeometry geometry) {
            this.geometry = geometry;
            return this;
        }

        public BlockType(int id, TextureSideGetter texture){
            this.id = id;
            this.textureGetter = texture;
            BLOCK_TYPES.put(id, this);
            isTranslucent = false;
            canGreedyMesh = true;
            geometry = new Cube();
            isFullBlock = true;
        }

        public BlockType(int id, TextureSideGetter texture, boolean isFullBlock, boolean isTranslucent, boolean canGreedyMesh){
            this.id = id;
            this.textureGetter = texture;
            this.canGreedyMesh = canGreedyMesh;
            BLOCK_TYPES.put(id, this);
            this.isTranslucent = isTranslucent;
            geometry = new Cube();
            this.isFullBlock = isFullBlock;
        }

        @Override
        public boolean equals(Object obj) {
            if(obj instanceof BlockType blockType){
                return id == blockType.id;
            }
            return false;
        }
    }


    public static boolean isFluid(Block block){
        if(block == null) return false;
        return block.blockType.equals(WATER);
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
        public final String BOTTOM = "dirt";
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
    public static class PillarTextureGetter implements TextureSideGetter{
        public final String SIDES;
        public final String VERTICAL;
        public PillarTextureGetter(String sides, String vertical){
            this.SIDES = sides;
            this.VERTICAL = vertical;
        }
        @Override
        public int getTextureIdForSide(int side) {
            if(side == 2 || side == 3 || side == 4 || side == 5){
                return TextureArray.getTextureWorld(SIDES);
            }
            return TextureArray.getTextureWorld(VERTICAL);
        }
    }

}
