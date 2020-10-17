package com.yungnickyoung.minecraft.betterportals.world.feature;

import com.mojang.serialization.Codec;
import com.yungnickyoung.minecraft.betterportals.BetterPortals;
import com.yungnickyoung.minecraft.betterportals.world.variant.MonolithVariantSettings;
import com.yungnickyoung.minecraft.betterportals.world.variant.MonolithVariants;
import com.yungnickyoung.minecraft.yungsapi.world.BlockSetSelector;
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
        fill(world, random, startX + 1, startY, startZ + 1, startX + 5, startY, startZ + 5, settings.getInsideSelector());

        // Corner blocks
        fill(world, random, startX + 1, startY, startZ + 1, startX + 1, startY + 1, startZ + 1, settings.getCornerSelector());
        fill(world, random, startX + 5, startY, startZ + 1, startX + 5, startY + 1, startZ + 1, settings.getCornerSelector());
        fill(world, random, startX + 1, startY, startZ + 5, startX + 1, startY + 1, startZ + 5, settings.getCornerSelector());
        fill(world, random, startX + 5, startY, startZ + 5, startX + 5, startY + 1, startZ + 5, settings.getCornerSelector());

        // First level stairs
        fillStairs(world, random, startX + 2, startY, startZ, startX + 4, startY, startZ, settings.getStairSelector(), Direction.SOUTH);
        fillStairs(world, random, startX, startY, startZ + 2, startX, startY, startZ + 4, settings.getStairSelector(), Direction.EAST);
        fillStairs(world, random, startX + 6, startY, startZ + 2, startX + 6, startY, startZ + 4, settings.getStairSelector(), Direction.WEST);
        fillStairs(world, random, startX + 2, startY, startZ + 6, startX + 4, startY, startZ + 6, settings.getStairSelector(), Direction.NORTH);

        // Second level stairs
        fillStairs(world, random, startX + 2, startY + 1, startZ + 1, startX + 4, startY + 1, startZ + 1, settings.getStairSelector(), Direction.SOUTH);
        fillStairs(world, random, startX + 1, startY + 1, startZ + 2, startX + 1, startY + 1, startZ + 4, settings.getStairSelector(), Direction.EAST);
        fillStairs(world, random, startX + 5, startY + 1, startZ + 2, startX + 5, startY + 1, startZ + 4, settings.getStairSelector(), Direction.WEST);
        fillStairs(world, random, startX + 2, startY + 1, startZ + 5, startX + 4, startY + 1, startZ + 5, settings.getStairSelector(), Direction.NORTH);

        // Decoration blocks
        fillHorizontalBlock(world, random, startX + 2, startY + 1, startZ + 2, startX + 2, startY + 1, startZ + 2, settings.getDecorationBlock(), Direction.NORTH);
        fillHorizontalBlock(world, random, startX + 4, startY + 1, startZ + 2, startX + 4, startY + 1, startZ + 2, settings.getDecorationBlock(), Direction.EAST);
        fillHorizontalBlock(world, random, startX + 2, startY + 1, startZ + 4, startX + 2, startY + 1, startZ + 4, settings.getDecorationBlock(), Direction.WEST);
        fillHorizontalBlock(world, random, startX + 4, startY + 1, startZ + 4, startX + 4, startY + 1, startZ + 4, settings.getDecorationBlock(), Direction.SOUTH);

        // Power blocks
        world.setBlockState(new BlockPos(startX + 3, startY + 1, startZ + 2), settings.getPowerBlock(), 2);
        world.setBlockState(new BlockPos(startX + 3, startY + 1, startZ + 4), settings.getPowerBlock(), 2);
        world.setBlockState(new BlockPos(startX + 2, startY + 1, startZ + 3), settings.getPowerBlock(), 2);
        world.setBlockState(new BlockPos(startX + 4, startY + 1, startZ + 3), settings.getPowerBlock(), 2);

        // Top blocks
        // Vertical parts
        fill(world, random, startX + 1, startY + 2, startZ + 1, startX + 1, startY + 4, startZ + 1, settings.getTopSelector());
        fill(world, random, startX + 5, startY + 2, startZ + 1, startX + 5, startY + 4, startZ + 1, settings.getTopSelector());
        fill(world, random, startX + 1, startY + 2, startZ + 5, startX + 1, startY + 4, startZ + 5, settings.getTopSelector());
        fill(world, random, startX + 5, startY + 2, startZ + 5, startX + 5, startY + 4, startZ + 5, settings.getTopSelector());
        // Topmost vertical parts
        fillWalls(world, random, startX + 1, startY + 5, startZ + 1, startX + 1, startY + 5, startZ + 1, settings.getTopSelector(), WallHeight.NONE, WallHeight.LOW, WallHeight.LOW, WallHeight.NONE, true);
        fillWalls(world, random, startX + 5, startY + 5, startZ + 1, startX + 5, startY + 5, startZ + 1, settings.getTopSelector(), WallHeight.NONE, WallHeight.NONE, WallHeight.LOW, WallHeight.LOW, true);
        fillWalls(world, random, startX + 1, startY + 5, startZ + 5, startX + 1, startY + 5, startZ + 5, settings.getTopSelector(), WallHeight.LOW, WallHeight.LOW, WallHeight.NONE, WallHeight.NONE, true);
        fillWalls(world, random, startX + 5, startY + 5, startZ + 5, startX + 5, startY + 5, startZ + 5, settings.getTopSelector(), WallHeight.LOW, WallHeight.NONE, WallHeight.NONE, WallHeight.LOW, true);
        // Horizontal parts
        fillWalls(world, random, startX + 2, startY + 5, startZ + 1, startX + 4, startY + 5, startZ + 1, settings.getTopSelector(), WallHeight.NONE, WallHeight.LOW, WallHeight.NONE, WallHeight.LOW, false);
        fillWalls(world, random, startX + 2, startY + 5, startZ + 5, startX + 4, startY + 5, startZ + 5, settings.getTopSelector(), WallHeight.NONE, WallHeight.LOW, WallHeight.NONE, WallHeight.LOW, false);
        fillWalls(world, random, startX + 1, startY + 5, startZ + 2, startX + 1, startY + 5, startZ + 4, settings.getTopSelector(), WallHeight.LOW, WallHeight.NONE, WallHeight.LOW, WallHeight.NONE, false);
        fillWalls(world, random, startX + 5, startY + 5, startZ + 2, startX + 5, startY + 5, startZ + 4, settings.getTopSelector(), WallHeight.LOW, WallHeight.NONE, WallHeight.LOW, WallHeight.NONE, false);

        return true;
    }

    private void fill(ISeedReader world, Random random, int minX, int minY, int minZ, int maxX, int maxY, int maxZ, BlockSetSelector selector) {
        for (int x = minX; x <= maxX; ++x) {
            for (int y = minY; y <= maxY; ++y) {
                for (int z = minZ; z <= maxZ; ++z) {
                    world.setBlockState(new BlockPos(x, y, z), selector.get(random), 2);
                }
            }
        }
    }

    private void fillStairs(ISeedReader world, Random random, int minX, int minY, int minZ, int maxX, int maxY, int maxZ, BlockSetSelector selector, Direction direction) {
        for (int x = minX; x <= maxX; ++x) {
            for (int y = minY; y <= maxY; ++y) {
                for (int z = minZ; z <= maxZ; ++z) {
                    BlockState blockState = selector.get(random);
                    if (blockState.getBlock() instanceof StairsBlock) {
                        blockState = blockState.with(StairsBlock.FACING, direction);
                    }
                    world.setBlockState(new BlockPos(x, y, z), blockState, 2);
                }
            }
        }
    }

    private void fillHorizontalBlock(ISeedReader world, Random random, int minX, int minY, int minZ, int maxX, int maxY, int maxZ, BlockState blockState, Direction direction) {
        for (int x = minX; x <= maxX; ++x) {
            for (int y = minY; y <= maxY; ++y) {
                for (int z = minZ; z <= maxZ; ++z) {
                    if (blockState.getBlock() instanceof HorizontalBlock) {
                        blockState = blockState.with(HorizontalBlock.HORIZONTAL_FACING, direction);
                    }
                    world.setBlockState(new BlockPos(x, y, z), blockState, 2);
                }
            }
        }
    }

    private void fillWalls(ISeedReader world, Random random, int minX, int minY, int minZ, int maxX, int maxY, int maxZ, BlockSetSelector selector, WallHeight north, WallHeight east, WallHeight south, WallHeight west, boolean up) {
        for (int x = minX; x <= maxX; ++x) {
            for (int y = minY; y <= maxY; ++y) {
                for (int z = minZ; z <= maxZ; ++z) {
                    BlockState blockState = selector.get(random);
                    if (blockState.getBlock() instanceof WallBlock) {
                        blockState = blockState
                            .with(WallBlock.WALL_HEIGHT_NORTH, north)
                            .with(WallBlock.WALL_HEIGHT_EAST, east)
                            .with(WallBlock.WALL_HEIGHT_SOUTH, south)
                            .with(WallBlock.WALL_HEIGHT_WEST, west)
                            .with(WallBlock.UP, up);
                    }
                    world.setBlockState(new BlockPos(x, y, z), blockState, 2);
                }
            }
        }
    }
}
