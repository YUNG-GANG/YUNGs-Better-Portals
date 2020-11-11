package com.yungnickyoung.minecraft.betterportals.world.feature;

import com.mojang.serialization.Codec;
import com.yungnickyoung.minecraft.betterportals.BetterPortals;
import com.yungnickyoung.minecraft.betterportals.block.BlockModule;
import com.yungnickyoung.minecraft.betterportals.util.BlockUtil;
import com.yungnickyoung.minecraft.betterportals.world.variant.MonolithVariantSettings;
import com.yungnickyoung.minecraft.betterportals.world.variant.MonolithVariants;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.block.*;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.NoFeatureConfig;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Objects;
import java.util.Random;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class MonolithFeature extends Feature<NoFeatureConfig> {
    private MonolithVariantSettings settings;

    public MonolithFeature(Codec<NoFeatureConfig> codec) {
        super(codec);
    }

    @Override
    public boolean func_241855_a(ISeedReader world, ChunkGenerator chunkGenerator, Random random, BlockPos pos, NoFeatureConfig config) {
        // Attempt to get dimension name, e.g. "minecraft:the_nether"
        String dimensionName;
        try {
            dimensionName = Objects.requireNonNull(world.getWorld().getDimensionKey().func_240901_a_()).toString();
        } catch (NullPointerException e) {
            BetterPortals.LOGGER.error("ERROR: Unable to get dimension name!");
            return false;
        }

        // Lazy init settings
        if (settings == null || !settings.getSpawnDimension().equals(dimensionName)) {
            settings = MonolithVariants.get().getVariantForDimension(dimensionName);
        }

        if (settings == null) {
            return false;
        }

        // Determine if we should spawn
        if (random.nextFloat() > settings.getSpawnChance()) {
            return false;
        }

        // Structure must fall in bounds declared in settings
        if (pos.getY() < settings.getMinY() || pos.getY() > settings.getMaxY()) {
            return false;
        }

        int startX = pos.getX();
        int startZ = pos.getZ();
        int startY = pos.getY();

        // Prevent feature from spawning in positions where corners would be floating
        BlockPos.Mutable corner = new BlockPos.Mutable();
        for (int xCorner = 0; xCorner <= 1; xCorner++) {
            for (int zCorner = 0; zCorner <= 1; zCorner++) {
                corner.setPos(startX + xCorner * 6, startY - 1, startZ + zCorner * 6);
                if (world.getBlockState(corner) == Blocks.AIR.getDefaultState() || world.getBlockState(corner) == Blocks.CAVE_AIR.getDefaultState()) {
                    return false;
                }
            }
        }

        // Inside blocks
        BlockUtil.fill(world, random, startX + 1, startY, startZ + 1, startX + 7, startY + 1, startZ + 7, settings.getInsideSelector());

        // Blocks under the second level of stairs
        BlockUtil.fill(world, random, startX + 2, startY, startZ + 1, startX + 6, startY, startZ + 1, settings.getInsideSelector());
        BlockUtil.fill(world, random, startX + 2, startY, startZ + 7, startX + 6, startY, startZ + 7, settings.getInsideSelector());
        BlockUtil.fill(world, random, startX + 1, startY, startZ + 2, startX + 1, startY, startZ + 6, settings.getInsideSelector());
        BlockUtil.fill(world, random, startX + 7, startY, startZ + 2, startX + 7, startY, startZ + 6, settings.getInsideSelector());

        // Blocks on either side of each stair case
        BlockUtil.fill(world, random, startX + 2, startY, startZ, startX + 6, startY, startZ, settings.getInsideSelector());
        BlockUtil.fill(world, random, startX + 2, startY, startZ + 8, startX + 6, startY, startZ + 8, settings.getInsideSelector());
        BlockUtil.fill(world, random, startX, startY, startZ + 2, startX, startY, startZ + 6, settings.getInsideSelector());
        BlockUtil.fill(world, random, startX + 8, startY, startZ + 2, startX + 8, startY, startZ + 6, settings.getInsideSelector());

        // Corner blocks
        BlockUtil.fill(world, random, startX + 1, startY, startZ + 1, startX + 1, startY + 1, startZ + 1, settings.getCornerSelector());
        BlockUtil.fill(world, random, startX + 7, startY, startZ + 1, startX + 7, startY + 1, startZ + 1, settings.getCornerSelector());
        BlockUtil.fill(world, random, startX + 1, startY, startZ + 7, startX + 1, startY + 1, startZ + 7, settings.getCornerSelector());
        BlockUtil.fill(world, random, startX + 7, startY, startZ + 7, startX + 7, startY + 1, startZ + 7, settings.getCornerSelector());

        // First level stairs
        BlockUtil.fillStairs(world, random, startX + 3, startY, startZ, startX + 5, startY, startZ, settings.getStairSelector(), Direction.SOUTH);
        BlockUtil.fillStairs(world, random, startX, startY, startZ + 3, startX, startY, startZ + 5, settings.getStairSelector(), Direction.EAST);
        BlockUtil.fillStairs(world, random, startX + 8, startY, startZ + 3, startX + 8, startY, startZ + 5, settings.getStairSelector(), Direction.WEST);
        BlockUtil.fillStairs(world, random, startX + 3, startY, startZ + 8, startX + 5, startY, startZ + 8, settings.getStairSelector(), Direction.NORTH);

        // Second level stairs
        BlockUtil.fillStairs(world, random, startX + 3, startY + 1, startZ + 1, startX + 5, startY + 1, startZ + 1, settings.getStairSelector(), Direction.SOUTH);
        BlockUtil.fillStairs(world, random, startX + 1, startY + 1, startZ + 3, startX + 1, startY + 1, startZ + 5, settings.getStairSelector(), Direction.EAST);
        BlockUtil.fillStairs(world, random, startX + 7, startY + 1, startZ + 3, startX + 7, startY + 1, startZ + 5, settings.getStairSelector(), Direction.WEST);
        BlockUtil.fillStairs(world, random, startX + 3, startY + 1, startZ + 7, startX + 5, startY + 1, startZ + 7, settings.getStairSelector(), Direction.NORTH);

        // Decoration blocks
        BlockUtil.fillHorizontalBlock(world, startX + 3, startY + 1, startZ + 3, startX + 3, startY + 1, startZ + 3, settings.getDecorationBlock(), Direction.NORTH);
        BlockUtil.fillHorizontalBlock(world, startX + 5, startY + 1, startZ + 3, startX + 5, startY + 1, startZ + 3, settings.getDecorationBlock(), Direction.EAST);
        BlockUtil.fillHorizontalBlock(world, startX + 3, startY + 1, startZ + 5, startX + 3, startY + 1, startZ + 5, settings.getDecorationBlock(), Direction.WEST);
        BlockUtil.fillHorizontalBlock(world, startX + 5, startY + 1, startZ + 5, startX + 5, startY + 1, startZ + 5, settings.getDecorationBlock(), Direction.SOUTH);

        // Power blocks
        BlockUtil.fill(world, startX + 4, startY + 1, startZ + 3, startX + 4, startY + 1, startZ + 3, settings.getPowerBlock()); // One block guaranteed placed
        BlockUtil.fill(world, startX + 3, startY + 1, startZ + 4, startX + 3, startY + 1, startZ + 4, random.nextFloat() < .3f ? settings.getPowerBlock() :Blocks.AIR.getDefaultState());
        BlockUtil.fill(world, startX + 5, startY + 1, startZ + 4, startX + 5, startY + 1, startZ + 4, random.nextFloat() < .3f ? settings.getPowerBlock() : Blocks.AIR.getDefaultState());
        BlockUtil.fill(world, startX + 4, startY + 1, startZ + 5, startX + 4, startY + 1, startZ + 5, Blocks.AIR.getDefaultState()); // One block guaranteed missing

        // Fence Blocks
        // Vertical poles
        BlockUtil.fill(world, random, startX + 1, startY + 2, startZ + 1, startX + 1, startY + 5, startZ + 1, settings.getFenceSelector());
        BlockUtil.fill(world, random, startX + 7, startY + 2, startZ + 1, startX + 7, startY + 5, startZ + 1, settings.getFenceSelector());
        BlockUtil.fill(world, random, startX + 1, startY + 2, startZ + 7, startX + 1, startY + 5, startZ + 7, settings.getFenceSelector());
        BlockUtil.fill(world, random, startX + 7, startY + 2, startZ + 7, startX + 7, startY + 5, startZ + 7, settings.getFenceSelector());
        // Bottom corners of poles
        BlockUtil.fillWalls(world, random, startX + 1, startY + 2, startZ + 1, startX + 1, startY + 2, startZ + 1, settings.getFenceSelector(), WallHeight.NONE, WallHeight.LOW, WallHeight.LOW, WallHeight.NONE, true);
        BlockUtil.fillWalls(world, random, startX + 7, startY + 2, startZ + 1, startX + 7, startY + 2, startZ + 1, settings.getFenceSelector(), WallHeight.NONE, WallHeight.NONE, WallHeight.LOW, WallHeight.LOW, true);
        BlockUtil.fillWalls(world, random, startX + 1, startY + 2, startZ + 7, startX + 1, startY + 2, startZ + 7, settings.getFenceSelector(), WallHeight.LOW, WallHeight.LOW, WallHeight.NONE, WallHeight.NONE, true);
        BlockUtil.fillWalls(world, random, startX + 7, startY + 2, startZ + 7, startX + 7, startY + 2, startZ + 7, settings.getFenceSelector(), WallHeight.LOW, WallHeight.NONE, WallHeight.NONE, WallHeight.LOW, true);
        // Fences on the middle area
        BlockUtil.fillWallsRandom(world, random, .6f, startX + 2, startY + 2, startZ + 1, startX + 2, startY + 2, startZ + 1, settings.getFenceSelector(), WallHeight.NONE, WallHeight.NONE, WallHeight.NONE, WallHeight.LOW, true);
        BlockUtil.fillWallsRandom(world, random, .6f, startX + 6, startY + 2, startZ + 1, startX + 6, startY + 2, startZ + 1, settings.getFenceSelector(), WallHeight.NONE, WallHeight.LOW, WallHeight.NONE, WallHeight.NONE, true);
        BlockUtil.fillWallsRandom(world, random, .6f, startX + 1, startY + 2, startZ + 2, startX + 1, startY + 2, startZ + 2, settings.getFenceSelector(), WallHeight.LOW, WallHeight.NONE, WallHeight.NONE, WallHeight.NONE, true);
        BlockUtil.fillWallsRandom(world, random, .6f, startX + 1, startY + 2, startZ + 6, startX + 1, startY + 2, startZ + 6, settings.getFenceSelector(), WallHeight.NONE, WallHeight.NONE, WallHeight.LOW, WallHeight.NONE, true);
        BlockUtil.fillWallsRandom(world, random, .6f, startX + 2, startY + 2, startZ + 7, startX + 2, startY + 2, startZ + 7, settings.getFenceSelector(), WallHeight.NONE, WallHeight.NONE, WallHeight.NONE, WallHeight.LOW, true);
        BlockUtil.fillWallsRandom(world, random, .6f, startX + 6, startY + 2, startZ + 7, startX + 6, startY + 2, startZ + 7, settings.getFenceSelector(), WallHeight.NONE, WallHeight.LOW, WallHeight.NONE, WallHeight.NONE, true);
        BlockUtil.fillWallsRandom(world, random, .6f, startX + 7, startY + 2, startZ + 2, startX + 7, startY + 2, startZ + 2, settings.getFenceSelector(), WallHeight.LOW, WallHeight.NONE, WallHeight.NONE, WallHeight.NONE, true);
        BlockUtil.fillWallsRandom(world, random, .6f, startX + 7, startY + 2, startZ + 6, startX + 7, startY + 2, startZ + 6, settings.getFenceSelector(), WallHeight.NONE, WallHeight.NONE, WallHeight.LOW, WallHeight.NONE, true);

        // Reclaimer
        world.setBlockState(new BlockPos(startX + 4, startY + 1, startZ + 4), BlockModule.RECLAIMER_BLOCK.getDefaultState(), 2);

        return true;
    }
}
