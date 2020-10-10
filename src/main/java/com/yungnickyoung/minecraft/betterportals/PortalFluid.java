package com.yungnickyoung.minecraft.betterportals;

import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.WaterFluid;
import net.minecraft.item.Item;
import net.minecraft.state.StateContainer;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraftforge.fluids.FluidAttributes;

@MethodsReturnNonnullByDefault
public abstract class PortalFluid extends WaterFluid {
    @Override
    public Fluid getFlowingFluid() {
        return BetterPortals.FLOWING_PORTAL;
    }

    @Override
    public Fluid getStillFluid() {
        return BetterPortals.SOURCE_PORTAL;
    }

    @Override
    public Item getFilledBucket() {
        return BetterPortals.PORTAL_BUCKET;
    }

    @Override
    protected boolean canSourcesMultiply() {
        return false;
    }

    @Override
    public boolean isEquivalentTo(Fluid fluidIn) {
        return fluidIn == BetterPortals.SOURCE_PORTAL || fluidIn == BetterPortals.FLOWING_PORTAL;
    }

    @Override
    public boolean canDisplace(FluidState fluidState, IBlockReader blockReader, BlockPos pos, Fluid fluid, Direction direction) {
        return direction == Direction.DOWN && !fluid.isIn(FluidTags.WATER);
    }

    @Override
    protected FluidAttributes createAttributes() {
        return FluidAttributes.Water.builder(
            new ResourceLocation("block/portal_liquid_still"),
            new ResourceLocation("block/portal_liquid_flow"))
            .overlay(new ResourceLocation("block/portal_liquid_overlay"))
            .translationKey("block.betterportals.portal_liquid")
            .color(0xFF3076E4).build(this);
    }

    public static class Flowing extends PortalFluid {
        protected void fillStateContainer(StateContainer.Builder<Fluid, FluidState> builder) {
            super.fillStateContainer(builder);
            builder.add(LEVEL_1_8);
        }

        public int getLevel(FluidState p_207192_1_) {
            return p_207192_1_.get(LEVEL_1_8);
        }

        public boolean isSource(FluidState state) {
            return false;
        }
    }

    public static class Source extends PortalFluid {
        public int getLevel(FluidState p_207192_1_) {
            return 8;
        }

        public boolean isSource(FluidState state) {
            return true;
        }
    }
}
