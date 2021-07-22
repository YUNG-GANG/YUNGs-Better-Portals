package com.yungnickyoung.minecraft.betterportals.world.feature;

import com.mojang.serialization.Codec;
import com.yungnickyoung.minecraft.betterportals.BetterPortals;
import com.yungnickyoung.minecraft.betterportals.init.BPModBlocks;
import com.yungnickyoung.minecraft.betterportals.world.variant.PortalLakeVariantSettings;
import com.yungnickyoung.minecraft.betterportals.world.variant.PortalLakeVariants;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.material.Material;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.SectionPos;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.LightType;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.IFeatureConfig;
import net.minecraft.world.gen.feature.NoFeatureConfig;
import net.minecraft.world.gen.feature.structure.Structure;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Objects;
import java.util.Random;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class PortalLake2Feature extends Feature<NoFeatureConfig> {
    private PortalLakeVariantSettings settings;
    private static final BlockState AIR = Blocks.CAVE_AIR.getDefaultState();
    private static final BlockState PORTAL_FLUID = BPModBlocks.PORTAL_FLUID_BLOCK.getDefaultState();

    public PortalLake2Feature() {
        super(NoFeatureConfig.field_236558_a_);
    }

    @Override
    public boolean generate(ISeedReader world, ChunkGenerator chunkGenerator, Random random, BlockPos pos, NoFeatureConfig config) {
        // Attempt to get dimension name, e.g. "minecraft:the_nether"
        String dimensionName;
        try {
            dimensionName = Objects.requireNonNull(world.getWorld().getDimensionKey().getLocation()).toString();
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
        if (random.nextFloat() > settings.getSpawnChance() / 2) { // divide by two because we have two portal lake variants
            return false;
        }

        // Set position
        BlockPos.Mutable mutable = pos.toMutable();
        mutable.setY((settings.getMaxY() - settings.getMinY()) + settings.getMinY() + 1); // offset by 1 since this variant has the liquid a little lower

        /*
         * Vanilla lake gen logic starts here.
         */
        while(mutable.getY() > 5 && world.isAirBlock(mutable)) {
            mutable.move(Direction.DOWN);
        }

        if (mutable.getY() <= 4) {
            return false;
        }

        mutable.move(Direction.DOWN, 4);
        if (world.func_241827_a(SectionPos.from(mutable), Structure.VILLAGE).findAny().isPresent()) {
            return false;
        }

        boolean[] liquidMask = new boolean[2048]; // Keeps track of where liquid blocks should be placed at
        int i = random.nextInt(4) + 4;

        // Determine where to place liquids
        for (int j = 0; j < i; ++j) {
            double d0 = random.nextDouble() * 6.0D + 3.0D;
            double d1 = random.nextDouble() * 4.0D + 2.0D;
            double d2 = random.nextDouble() * 6.0D + 3.0D;
            double d3 = random.nextDouble() * (16.0D - d0 - 2.0D) + 1.0D + d0 / 2.0D;
            double d4 = random.nextDouble() * (8.0D - d1 - 4.0D) + 2.0D + d1 / 2.0D;
            double d5 = random.nextDouble() * (16.0D - d2 - 2.0D) + 1.0D + d2 / 2.0D;

            for (int x = 1; x < 15; ++x) {
                for (int z = 1; z < 15; ++z) {
                    for (int y = 1; y < 7; ++y) {
                        double d6 = ((double)x - d3) / (d0 / 2.0D);
                        double d7 = ((double)y - d4) / (d1 / 2.0D);
                        double d8 = ((double)z - d5) / (d2 / 2.0D);
                        double d9 = d6 * d6 + d7 * d7 + d8 * d8;
                        if (d9 < 1.0D) {
                            liquidMask[(x * 16 + z) * 8 + y] = true;
                        }
                    }
                }
            }
        }

        // Make sure we aren't overwriting other liquids
        for (int x = 0; x < 16; ++x) {
            for (int z = 0; z < 16; ++z) {
                for (int y = 0; y < 8; ++y) {
                    boolean flag = !liquidMask[(x * 16 + z) * 8 + y] && (x < 15 && liquidMask[((x + 1) * 16 + z) * 8 + y] || x > 0 && liquidMask[((x - 1) * 16 + z) * 8 + y] || z < 15 && liquidMask[(x * 16 + z + 1) * 8 + y] || z > 0 && liquidMask[(x * 16 + (z - 1)) * 8 + y] || y < 7 && liquidMask[(x * 16 + z) * 8 + y + 1] || y > 0 && liquidMask[(x * 16 + z) * 8 + (y - 1)]);
                    if (flag) {
                        Material material = world.getBlockState(mutable.add(x, y, z)).getMaterial();
                        if (y >= 4 && material.isLiquid()) {
                            return false;
                        }

                        if (y < 4 && !material.isSolid() && world.getBlockState(mutable.add(x, y, z)) != PORTAL_FLUID) {
                            return false;
                        }
                    }
                }
            }
        }

        // Spawn liquid blocks
        for(int x = 0; x < 16; ++x) {
            for(int z = 0; z < 16; ++z) {
                for(int y = 0; y < 8; ++y) {
                    if (liquidMask[(x * 16 + z) * 8 + y]) {
                        BlockPos newPos = mutable.add(x, y, z);
                        world.setBlockState(newPos, y >= 4 ? AIR : PORTAL_FLUID, 2);
                        world.getPendingFluidTicks().scheduleTick(newPos, PORTAL_FLUID.getFluidState().getFluid(), PORTAL_FLUID.getFluidState().getFluid().getTickRate(world));
                    }
                }
            }
        }

        // Make lakes blend in with biome if they spawn on the surface
        for(int x = 0; x < 16; ++x) {
            for(int z = 0; z < 16; ++z) {
                for(int y = 4; y < 8; ++y) {
                    if (liquidMask[(x * 16 + z) * 8 + y]) {
                        BlockPos blockpos = mutable.add(x, y - 1, z);
                        if (isDirt(world.getBlockState(blockpos).getBlock()) && world.getLightFor(LightType.SKY, mutable.add(x, y, z)) > 0) {
                            Biome biome = world.getBiome(blockpos);
                            if (biome.getGenerationSettings().getSurfaceBuilderConfig().getTop().isIn(Blocks.MYCELIUM)) {
                                world.setBlockState(blockpos, Blocks.MYCELIUM.getDefaultState(), 2);
                            } else {
                                world.setBlockState(blockpos, Blocks.GRASS_BLOCK.getDefaultState(), 2);
                            }
                        }
                    }
                }
            }
        }

        // Surround lake with blocks from the BlockSetSelector
        for(int x = 0; x < 16; ++x) {
            for(int z = 0; z < 16; ++z) {
                for(int y = 0; y < 8; ++y) {
                    boolean flag1 = !liquidMask[(x * 16 + z) * 8 + y] && (x < 15 && liquidMask[((x + 1) * 16 + z) * 8 + y] || x > 0 && liquidMask[((x - 1) * 16 + z) * 8 + y] || z < 15 && liquidMask[(x * 16 + z + 1) * 8 + y] || z > 0 && liquidMask[(x * 16 + (z - 1)) * 8 + y] || y < 7 && liquidMask[(x * 16 + z) * 8 + y + 1] || y > 0 && liquidMask[(x * 16 + z) * 8 + (y - 1)]);
                    if (flag1 && (y < 4 || random.nextInt(2) != 0) && world.getBlockState(mutable.add(x, y, z)).getMaterial().isSolid()) {
                        world.setBlockState(mutable.add(x, y, z), settings.getBlockSelector().get(random), 2);
                    }
                }
            }
        }

        return true;
    }
}
