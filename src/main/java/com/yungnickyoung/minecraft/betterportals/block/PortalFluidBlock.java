package com.yungnickyoung.minecraft.betterportals.block;

import com.yungnickyoung.minecraft.betterportals.BetterPortals;
import com.yungnickyoung.minecraft.betterportals.init.BPBlocks;
import com.yungnickyoung.minecraft.betterportals.util.BlockUtil;
import com.yungnickyoung.minecraft.betterportals.world.variant.PortalLakeVariantSettings;
import com.yungnickyoung.minecraft.betterportals.world.variant.PortalLakeVariants;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FlowingFluidBlock;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.fluid.FlowingFluid;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Direction;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

public class PortalFluidBlock extends FlowingFluidBlock {
    public PortalFluidBlock() {
        super(
            () -> BPBlocks.PORTAL_FLUID,
            AbstractBlock.Properties.create(Material.WATER)
                .doesNotBlockMovement()
                .hardnessAndResistance(100.0F)
                .setLightLevel((state) -> 7)
                .noDrops()
        );
    }

    public void onEntityCollision(BlockState state, World worldIn, BlockPos pos, Entity entity) {
        if (entity instanceof ServerPlayerEntity) {
            ServerPlayerEntity playerEntity = ((ServerPlayerEntity) entity);

            // Find target dimension for this fluid
            String sourceDimension = worldIn.getDimensionKey().func_240901_a_().toString();
            PortalLakeVariantSettings settings = PortalLakeVariants.get().getVariantForDimension(sourceDimension);
            String targetDimension = settings.getTargetDimension();

            MinecraftServer minecraftServer = playerEntity.getServer(); // the server itself
            ServerWorld targetWorld = minecraftServer.getWorld(RegistryKey.func_240903_a_(Registry.WORLD_KEY, new ResourceLocation(targetDimension)));

            // Prevent crash due to mojang bug that makes mod's json dimensions not exist upload first creation of world on server. A restart fixes this.
            if (targetWorld == null) {
                BetterPortals.LOGGER.error("UNABLE TO ENTER DIMENSION");
                return;
            }

            // Determine player's spawn point in target dimension
            int targetMinY = settings.getPlayerTeleportedMinY();
            int targetMaxY = settings.getPlayerTeleportedMaxY();
            int targetY = -1;
            BlockPos.Mutable targetPos = pos.toMutable();
            targetPos.setY(targetMaxY);

            boolean foundAir = false;
            for (int y = targetMaxY; y >= targetMinY; y--) {
                BlockState blockState = targetWorld.getBlockState(targetPos);
                if (blockState == Blocks.AIR.getDefaultState() || blockState == Blocks.CAVE_AIR.getDefaultState()) {
                    foundAir = true;
                } else if (blockState.getMaterial().isSolid() && foundAir) {
                    targetY = y + 1;
                    BlockUtil.fill(targetWorld, targetPos.getX() - 1, targetY, targetPos.getZ() - 1, targetPos.getX() + 1, targetY + 2, targetPos.getZ() + 1, Blocks.CAVE_AIR.getDefaultState());
                    break;
                }
                targetPos.move(Direction.DOWN);
            }

            if (targetY == -1) {
                if (foundAir) {
                    // Air all the way - we need to spawn a platform somewhere
                    targetY = (targetMaxY + targetMinY) / 2;
                    BlockUtil.fill(targetWorld, targetPos.getX() - 1, targetY - 1, targetPos.getZ() - 1, targetPos.getX() + 1, targetY - 1, targetPos.getZ() + 1, Blocks.STONE.getDefaultState());
                    BlockUtil.fill(targetWorld, targetPos.getX() - 1, targetY, targetPos.getZ() - 1, targetPos.getX() + 1, targetY + 2, targetPos.getZ() + 1, Blocks.CAVE_AIR.getDefaultState());
                } else {
                    // Solid all the way - we need to carve out a spawn point
                    targetY = (targetMaxY + targetMinY) / 2;
                    BlockUtil.fill(targetWorld, targetPos.getX() - 1, targetY - 1, targetPos.getZ() - 1, targetPos.getX() + 1, targetY + 2, targetPos.getZ() + 1, Blocks.CAVE_AIR.getDefaultState());
                }
            }

            // Poof
            playerEntity.teleport(
                targetWorld,
                playerEntity.getPosX(),
                targetY,
                playerEntity.getPosZ(),
                playerEntity.rotationYaw,
                playerEntity.rotationPitch
            );
        }
    }
}