package com.yungnickyoung.minecraft.betterportals.world.feature;

import com.mojang.serialization.Codec;
import com.yungnickyoung.minecraft.betterportals.BetterPortals;
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
        BlockUtil.fill(world, random, startX + 1, startY, startZ + 1, startX + 5, startY, startZ + 5, settings.getInsideSelector());

        // Corner blocks
        BlockUtil.fill(world, random, startX + 1, startY, startZ + 1, startX + 1, startY + 1, startZ + 1, settings.getCornerSelector());
        BlockUtil.fill(world, random, startX + 5, startY, startZ + 1, startX + 5, startY + 1, startZ + 1, settings.getCornerSelector());
        BlockUtil.fill(world, random, startX + 1, startY, startZ + 5, startX + 1, startY + 1, startZ + 5, settings.getCornerSelector());
        BlockUtil.fill(world, random, startX + 5, startY, startZ + 5, startX + 5, startY + 1, startZ + 5, settings.getCornerSelector());

        // First level stairs
        BlockUtil.fillStairs(world, random, startX + 2, startY, startZ, startX + 4, startY, startZ, settings.getStairSelector(), Direction.SOUTH);
        BlockUtil.fillStairs(world, random, startX, startY, startZ + 2, startX, startY, startZ + 4, settings.getStairSelector(), Direction.EAST);
        BlockUtil.fillStairs(world, random, startX + 6, startY, startZ + 2, startX + 6, startY, startZ + 4, settings.getStairSelector(), Direction.WEST);
        BlockUtil.fillStairs(world, random, startX + 2, startY, startZ + 6, startX + 4, startY, startZ + 6, settings.getStairSelector(), Direction.NORTH);

        // Second level stairs
        BlockUtil.fillStairs(world, random, startX + 2, startY + 1, startZ + 1, startX + 4, startY + 1, startZ + 1, settings.getStairSelector(), Direction.SOUTH);
        BlockUtil.fillStairs(world, random, startX + 1, startY + 1, startZ + 2, startX + 1, startY + 1, startZ + 4, settings.getStairSelector(), Direction.EAST);
        BlockUtil.fillStairs(world, random, startX + 5, startY + 1, startZ + 2, startX + 5, startY + 1, startZ + 4, settings.getStairSelector(), Direction.WEST);
        BlockUtil.fillStairs(world, random, startX + 2, startY + 1, startZ + 5, startX + 4, startY + 1, startZ + 5, settings.getStairSelector(), Direction.NORTH);

        // Decoration blocks
        BlockUtil.fillHorizontalBlock(world, startX + 2, startY + 1, startZ + 2, startX + 2, startY + 1, startZ + 2, settings.getDecorationBlock(), Direction.NORTH);
        BlockUtil.fillHorizontalBlock(world, startX + 4, startY + 1, startZ + 2, startX + 4, startY + 1, startZ + 2, settings.getDecorationBlock(), Direction.EAST);
        BlockUtil.fillHorizontalBlock(world, startX + 2, startY + 1, startZ + 4, startX + 2, startY + 1, startZ + 4, settings.getDecorationBlock(), Direction.WEST);
        BlockUtil.fillHorizontalBlock(world, startX + 4, startY + 1, startZ + 4, startX + 4, startY + 1, startZ + 4, settings.getDecorationBlock(), Direction.SOUTH);

        // Power blocks
        world.setBlockState(new BlockPos(startX + 3, startY + 1, startZ + 2), settings.getPowerBlock(), 2);
        world.setBlockState(new BlockPos(startX + 3, startY + 1, startZ + 4), settings.getPowerBlock(), 2);
        world.setBlockState(new BlockPos(startX + 2, startY + 1, startZ + 3), settings.getPowerBlock(), 2);
        world.setBlockState(new BlockPos(startX + 4, startY + 1, startZ + 3), settings.getPowerBlock(), 2);

        // Top blocks
        // Vertical parts
        BlockUtil.fill(world, random, startX + 1, startY + 2, startZ + 1, startX + 1, startY + 4, startZ + 1, settings.getTopSelector());
        BlockUtil.fill(world, random, startX + 5, startY + 2, startZ + 1, startX + 5, startY + 4, startZ + 1, settings.getTopSelector());
        BlockUtil.fill(world, random, startX + 1, startY + 2, startZ + 5, startX + 1, startY + 4, startZ + 5, settings.getTopSelector());
        BlockUtil.fill(world, random, startX + 5, startY + 2, startZ + 5, startX + 5, startY + 4, startZ + 5, settings.getTopSelector());
        // Topmost vertical parts
        BlockUtil.fillWalls(world, random, startX + 1, startY + 5, startZ + 1, startX + 1, startY + 5, startZ + 1, settings.getTopSelector(), WallHeight.NONE, WallHeight.LOW, WallHeight.LOW, WallHeight.NONE, true);
        BlockUtil.fillWalls(world, random, startX + 5, startY + 5, startZ + 1, startX + 5, startY + 5, startZ + 1, settings.getTopSelector(), WallHeight.NONE, WallHeight.NONE, WallHeight.LOW, WallHeight.LOW, true);
        BlockUtil.fillWalls(world, random, startX + 1, startY + 5, startZ + 5, startX + 1, startY + 5, startZ + 5, settings.getTopSelector(), WallHeight.LOW, WallHeight.LOW, WallHeight.NONE, WallHeight.NONE, true);
        BlockUtil.fillWalls(world, random, startX + 5, startY + 5, startZ + 5, startX + 5, startY + 5, startZ + 5, settings.getTopSelector(), WallHeight.LOW, WallHeight.NONE, WallHeight.NONE, WallHeight.LOW, true);
        // Horizontal parts
        BlockUtil.fillWalls(world, random, startX + 2, startY + 5, startZ + 1, startX + 4, startY + 5, startZ + 1, settings.getTopSelector(), WallHeight.NONE, WallHeight.LOW, WallHeight.NONE, WallHeight.LOW, false);
        BlockUtil.fillWalls(world, random, startX + 2, startY + 5, startZ + 5, startX + 4, startY + 5, startZ + 5, settings.getTopSelector(), WallHeight.NONE, WallHeight.LOW, WallHeight.NONE, WallHeight.LOW, false);
        BlockUtil.fillWalls(world, random, startX + 1, startY + 5, startZ + 2, startX + 1, startY + 5, startZ + 4, settings.getTopSelector(), WallHeight.LOW, WallHeight.NONE, WallHeight.LOW, WallHeight.NONE, false);
        BlockUtil.fillWalls(world, random, startX + 5, startY + 5, startZ + 2, startX + 5, startY + 5, startZ + 4, settings.getTopSelector(), WallHeight.LOW, WallHeight.NONE, WallHeight.LOW, WallHeight.NONE, false);

        return true;
    }
}
