package com.yungnickyoung.minecraft.betterportals.fluid;

import com.yungnickyoung.minecraft.betterportals.BetterPortals;
import com.yungnickyoung.minecraft.betterportals.block.BlockModule;
import com.yungnickyoung.minecraft.betterportals.item.ItemModule;
import com.yungnickyoung.minecraft.betterportals.util.RGBAColor;
import com.yungnickyoung.minecraft.betterportals.world.variant.PortalLakeVariants;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.FlowingFluidBlock;
import net.minecraft.client.renderer.chunk.ChunkRenderCache;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.Item;
import net.minecraft.particles.IParticleData;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.state.StateContainer;
import net.minecraft.tags.FluidTags;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.*;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.ForgeFlowingFluid;

import java.util.Objects;
import java.util.Random;
import java.util.function.BiFunction;

@MethodsReturnNonnullByDefault
public abstract class PortalFluid extends ForgeFlowingFluid {
    protected PortalFluid(Properties properties) {
        super(properties);
    }

    @Override
    public Fluid getFlowingFluid() {
        return FluidModule.PORTAL_FLUID_FLOWING;
    }

    @Override
    public Fluid getStillFluid() {
        return FluidModule.PORTAL_FLUID;
    }

    @Override
    public Item getFilledBucket() {
        return ItemModule.PORTAL_BUCKET;
    }

    @Override
    protected boolean canSourcesMultiply() {
        return false;
    }

    @Override
    public boolean isEquivalentTo(Fluid fluidIn) {
        return fluidIn == FluidModule.PORTAL_FLUID || fluidIn == FluidModule.PORTAL_FLUID_FLOWING;
    }

    @Override
    public boolean canDisplace(FluidState fluidState, IBlockReader blockReader, BlockPos pos, Fluid fluid, Direction direction) {
        return direction == Direction.DOWN && !fluid.isIn(FluidTags.WATER);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    protected void animateTick(World worldIn, BlockPos pos, FluidState state, Random random) {
        if (!state.isSource() && !state.get(FALLING)) {
            if (random.nextInt(100) == 0) {
                worldIn.playSound(
                    (double) pos.getX() + 0.5D,
                    (double) pos.getY() + 0.5D,
                    (double) pos.getZ() + 0.5D,
                    SoundEvents.BLOCK_PORTAL_AMBIENT,
                    SoundCategory.BLOCKS,
                    .5f,
                    random.nextFloat() * 0.4F + 0.8F,
                    false
                );
            }
        }
        if (random.nextInt(3) == 0) {
            worldIn.addParticle(ParticleTypes.PORTAL,
                (double) pos.getX() + (double) random.nextFloat(),
                (double) pos.getY() + (double) random.nextFloat(),
                (double) pos.getZ() + (double) random.nextFloat(),
                0.0D,
                0.0D,
                0.0D);
        }
    }

    public int getTickRate(IWorldReader world) {
        return 35;
    }

    @Override
    protected float getExplosionResistance() {
        return 100.0F;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public IParticleData getDripParticleData() {
        return ParticleTypes.DRIPPING_OBSIDIAN_TEAR;
    }

    @Override
    protected void beforeReplacingBlock(IWorld world, BlockPos pos, BlockState state) {
        TileEntity tileentity = state.hasTileEntity() ? world.getTileEntity(pos) : null;
        Block.spawnDrops(state, world, pos, tileentity);
    }

    @Override
    public int getSlopeFindDistance(IWorldReader world) {
        return 2;
    }

    @Override
    public int getLevelDecreasePerBlock(IWorldReader world) {
        return 2;
    }

    @Override
    public BlockState getBlockState(FluidState state) {
        return BlockModule.PORTAL_FLUID_BLOCK.getDefaultState().with(FlowingFluidBlock.LEVEL, getLevelFromState(state));
    }

    public static class Flowing extends PortalFluid {
        public Flowing(Properties properties) {
            super(properties);
            setDefaultState(getStateContainer().getBaseState().with(LEVEL_1_8, 7));
        }

        protected void fillStateContainer(StateContainer.Builder<Fluid, FluidState> builder) {
            super.fillStateContainer(builder);
            builder.add(LEVEL_1_8);
        }

        @Override
        public int getLevel(FluidState state) {
            return state.get(LEVEL_1_8);
        }

        @Override
        public boolean isSource(FluidState state) {
            return false;
        }
    }

    public static class Source extends PortalFluid {
        public Source(Properties properties) {
            super(properties);
            setDefaultState(getStateContainer().getBaseState().with(LEVEL_1_8, 7));
        }

        protected void fillStateContainer(StateContainer.Builder<Fluid, FluidState> builder) {
            super.fillStateContainer(builder);
            builder.add(LEVEL_1_8);
        }

        public int getLevel(FluidState state) {
            return 8;
        }

        public boolean isSource(FluidState state) {
            return true;
        }
    }

    public static class PortalFluidAttributes extends FluidAttributes {
        protected PortalFluidAttributes(Builder builder, Fluid fluid) {
            super(builder, fluid);
        }

        @Override
        public int getColor(IBlockDisplayReader world, BlockPos pos) {
            // Attempt to get dimension name, e.g. "minecraft:the_nether"
            String dimensionName;
            try {
                dimensionName = Objects.requireNonNull( ((ChunkRenderCache)world).world.getDimensionKey().func_240901_a_()).toString();
            } catch (Exception e) {
                BetterPortals.LOGGER.error("ERROR: Unable to get dimension name! Using default portal color...");
                return 0xEE190040;
            }

            RGBAColor fluidColor = PortalLakeVariants.get().getVariantForDimension(dimensionName).getFluidColor();
            return fluidColor.toInt();
        }

        public static Builder builder(ResourceLocation stillTexture, ResourceLocation flowingTexture) {
            return new PortalBuilder(stillTexture, flowingTexture, PortalFluidAttributes::new);
        }

        public static class PortalBuilder extends FluidAttributes.Builder {
            protected PortalBuilder(ResourceLocation stillTexture, ResourceLocation flowingTexture, BiFunction<FluidAttributes.Builder, Fluid, FluidAttributes> factory) {
                super(stillTexture, flowingTexture, factory);
            }
        }
    }
}
