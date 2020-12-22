package com.yungnickyoung.minecraft.betterportals.util;

import com.yungnickyoung.minecraft.yungsapi.world.BlockSetSelector;
import net.minecraft.block.*;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ISeedReader;

import java.util.Random;

/**
 * Miscellaneous methods for setting blocks in the world.
 */
public class BlockUtil {
    public static void fill(ISeedReader world, int minX, int minY, int minZ, int maxX, int maxY, int maxZ, BlockState blockState) {
        for (int x = minX; x <= maxX; ++x) {
            for (int y = minY; y <= maxY; ++y) {
                for (int z = minZ; z <= maxZ; ++z) {
                    world.setBlockState(new BlockPos(x, y, z), blockState, 2);
                }
            }
        }
    }

    public static void replaceAir(ISeedReader world, int minX, int minY, int minZ, int maxX, int maxY, int maxZ, BlockState blockState) {
        BlockPos.Mutable currPos = new BlockPos.Mutable();
        BlockState currBlock;
        for (int x = minX; x <= maxX; ++x) {
            for (int y = minY; y <= maxY; ++y) {
                for (int z = minZ; z <= maxZ; ++z) {
                    currPos.setPos(x, y, z);
                    currBlock = world.getBlockState(currPos);
                    if (currBlock == Blocks.AIR.getDefaultState() || currBlock == Blocks.CAVE_AIR.getDefaultState()) {
                        world.setBlockState(currPos, blockState, 2);
                    }
                }
            }
        }
    }

    public static void fillRandom(ISeedReader world, Random random, float chance, int minX, int minY, int minZ, int maxX, int maxY, int maxZ, BlockState blockState) {
        for (int x = minX; x <= maxX; ++x) {
            for (int y = minY; y <= maxY; ++y) {
                for (int z = minZ; z <= maxZ; ++z) {
                    if (random.nextFloat() < chance)
                        world.setBlockState(new BlockPos(x, y, z), blockState, 2);
                }
            }
        }
    }

    public static void fill(ISeedReader world, Random random, int minX, int minY, int minZ, int maxX, int maxY, int maxZ, BlockSetSelector selector) {
        for (int x = minX; x <= maxX; ++x) {
            for (int y = minY; y <= maxY; ++y) {
                for (int z = minZ; z <= maxZ; ++z) {
                    world.setBlockState(new BlockPos(x, y, z), selector.get(random), 2);
                }
            }
        }
    }

    public static void fillStairs(ISeedReader world, Random random, int minX, int minY, int minZ, int maxX, int maxY, int maxZ, BlockSetSelector selector, Direction direction) {
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

    public static void fillHorizontalBlock(ISeedReader world, int minX, int minY, int minZ, int maxX, int maxY, int maxZ, BlockState blockState, Direction direction) {
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

    public static void fillWalls(ISeedReader world, Random random, int minX, int minY, int minZ, int maxX, int maxY, int maxZ, BlockSetSelector selector, WallHeight north, WallHeight east, WallHeight south, WallHeight west, boolean up) {
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

    public static void fillWallsRandom(ISeedReader world, Random random, float chance, int minX, int minY, int minZ, int maxX, int maxY, int maxZ, BlockSetSelector selector, WallHeight north, WallHeight east, WallHeight south, WallHeight west, boolean up) {
        for (int x = minX; x <= maxX; ++x) {
            for (int y = minY; y <= maxY; ++y) {
                for (int z = minZ; z <= maxZ; ++z) {
                    BlockState blockState = selector.get(random);
                    if (random.nextFloat() < chance && blockState.getBlock() instanceof WallBlock) {
                        blockState = blockState
                            .with(WallBlock.WALL_HEIGHT_NORTH, north)
                            .with(WallBlock.WALL_HEIGHT_EAST, east)
                            .with(WallBlock.WALL_HEIGHT_SOUTH, south)
                            .with(WallBlock.WALL_HEIGHT_WEST, west)
                            .with(WallBlock.UP, up);
                        world.setBlockState(new BlockPos(x, y, z), blockState, 2);
                    }
                }
            }
        }
    }
}
