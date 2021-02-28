package com.yungnickyoung.minecraft.betterportals.block;

import com.yungnickyoung.minecraft.betterportals.api.BetterPortalsCapabilities;
import com.yungnickyoung.minecraft.betterportals.fluid.FluidModule;
import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

import javax.annotation.ParametersAreNonnullByDefault;

public class PortalFluidBlock extends FlowingFluidBlock {
    public PortalFluidBlock() {
        super(
            () -> FluidModule.PORTAL_FLUID,
            AbstractBlock.Properties.create(Material.LAVA)
                .doesNotBlockMovement()
                .hardnessAndResistance(100.0F)
                .setLightLevel((state) -> 7)
                .noDrops()
        );
    }

    @Override
    public void onEntityCollision(BlockState state, World worldIn, BlockPos pos, Entity entity) {
        if (entity instanceof PlayerEntity && !entity.isPassenger() && !entity.isBeingRidden()) {
            entity.getCapability(BetterPortalsCapabilities.PLAYER_PORTAL_INFO).ifPresent(playerPortalInfo -> {
                playerPortalInfo.setDEBUGportalCounter(playerPortalInfo.getDEBUGportalCounter() + 1);
                playerPortalInfo.enterPortalFluid();
            });
        } else if (!entity.isPassenger() && !entity.isBeingRidden() && entity.isNonBoss() && !worldIn.isRemote) { // Non-player entities get insta-teleported
            entity.getCapability(BetterPortalsCapabilities.ENTITY_PORTAL_INFO).ifPresent(entityPortalInfo -> {
                entityPortalInfo.enterPortalFluid();
            });
        }
    }

    @Override
    @ParametersAreNonnullByDefault
    public void onBlockAdded(BlockState state, World worldIn, BlockPos pos, BlockState oldState, boolean isMoving) {
        if (this.reactWithNeighbors(worldIn, pos, state)) {
            worldIn.getPendingFluidTicks().scheduleTick(pos, state.getFluidState().getFluid(), 35); // 35 is the tick rate, found w/ this.fluid.getTickRate() (which is private)
        }
    }

    @Override
    @ParametersAreNonnullByDefault
    public void neighborChanged(BlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos, boolean isMoving) {
        if (this.reactWithNeighbors(worldIn, pos, state)) {
            worldIn.getPendingFluidTicks().scheduleTick(pos, state.getFluidState().getFluid(), 35); // 35 is the tick rate, found w/ this.fluid.getTickRate() (which is private)
        }
    }

    // Method taken from FlowingFluidBlock. Adjusted to create cobble/obsidian when interacting with any fluid
    private boolean reactWithNeighbors(World worldIn, BlockPos pos, BlockState state) {
        boolean isAboveSoulSoil = worldIn.getBlockState(pos.down()).isIn(Blocks.SOUL_SOIL);

        for(Direction direction : Direction.values()) {
            if (direction != Direction.DOWN) {
                BlockPos blockpos = pos.offset(direction);
                Fluid fluid = worldIn.getFluidState(blockpos).getFluidState().getFluid();
                if (fluid != Fluids.EMPTY && fluid != FluidModule.PORTAL_FLUID && fluid != FluidModule.PORTAL_FLUID_FLOWING) {
                    Block replacementBlock = worldIn.getFluidState(pos).isSource() ? Blocks.OBSIDIAN : Blocks.COBBLESTONE;
                    worldIn.setBlockState(pos, net.minecraftforge.event.ForgeEventFactory.fireFluidPlaceBlockEvent(worldIn, pos, pos, replacementBlock.getDefaultState()));
                    this.triggerMixEffects(worldIn, pos);
                    return false;
                }

                if (isAboveSoulSoil && worldIn.getBlockState(blockpos).isIn(Blocks.BLUE_ICE)) {
                    worldIn.setBlockState(pos, net.minecraftforge.event.ForgeEventFactory.fireFluidPlaceBlockEvent(worldIn, pos, pos, Blocks.BASALT.getDefaultState()));
                    this.triggerMixEffects(worldIn, pos);
                    return false;
                }
            }
        }

        return true;
    }

    // Taken from FlowingFluidBlock
    private void triggerMixEffects(IWorld worldIn, BlockPos pos) {
        worldIn.playEvent(1501, pos, 0);
    }
}