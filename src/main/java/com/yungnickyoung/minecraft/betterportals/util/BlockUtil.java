package com.yungnickyoung.minecraft.betterportals.util;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ISeedReader;

import java.util.Random;

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
}
