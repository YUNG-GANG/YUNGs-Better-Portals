package com.yungnickyoung.minecraft.betterportals.world.feature;

import com.mojang.serialization.Codec;
import com.yungnickyoung.minecraft.betterportals.BetterPortals;
import com.yungnickyoung.minecraft.betterportals.block.BlockModule;
import com.yungnickyoung.minecraft.betterportals.util.BlockUtil;
import com.yungnickyoung.minecraft.betterportals.world.variant.PortalLakeVariantSettings;
import com.yungnickyoung.minecraft.betterportals.world.variant.PortalLakeVariants;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.block.Blocks;
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
public class PortalLakeFeature extends Feature<NoFeatureConfig> {
    private PortalLakeVariantSettings settings;

    public PortalLakeFeature(Codec<NoFeatureConfig> codec) {
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
            settings = PortalLakeVariants.get().getVariantForDimension(dimensionName);
        }

        if (settings == null) {
            return false;
        }

        // Determine if we should spawn
        if (random.nextFloat() > settings.getSpawnChance()) {
            return false;
        }

        // Prevent floating lakes
        while(world.getBlockState(pos) == Blocks.CAVE_AIR.getDefaultState()) {
            pos = pos.down();
        }

        int startX = pos.getX();
        int startZ = pos.getZ();
        int startY = settings.getMinY() + random.nextInt(settings.getMaxY() - settings.getMinY() + 1);

        BlockUtil.fill(world, random, startX, startY - 8, startZ, startX + 6, startY, startZ + 6, settings.getBlockSelector());
        BlockUtil.fill(world,startX + 1, startY - 7, startZ + 1, startX + 5, startY - 1, startZ + 5, Blocks.CAVE_AIR.getDefaultState());
        BlockUtil.fill(world,startX + 1, startY - 7, startZ + 1, startX + 5, startY - 3, startZ + 5, BlockModule.PORTAL_FLUID_BLOCK.getDefaultState());

        return true;
    }
}
