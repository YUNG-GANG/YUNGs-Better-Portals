package com.yungnickyoung.minecraft.betterportals.world;

import com.yungnickyoung.minecraft.betterportals.BetterPortals;
import com.yungnickyoung.minecraft.betterportals.capability.IEntityPortalInfo;
import com.yungnickyoung.minecraft.betterportals.capability.IPlayerPortalInfo;
import com.yungnickyoung.minecraft.betterportals.util.BlockUtil;
import com.yungnickyoung.minecraft.betterportals.world.variant.PortalLakeVariantSettings;
import com.yungnickyoung.minecraft.betterportals.world.variant.PortalLakeVariants;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.PortalInfo;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Direction;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.DimensionType;
import net.minecraft.world.border.WorldBorder;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.util.ITeleporter;

import javax.annotation.Nullable;
import java.util.function.Function;

public class PortalLakeTeleporter implements ITeleporter {
    @Override
    public Entity placeEntity(Entity entity, ServerWorld currentWorld, ServerWorld destWorld, float yaw, Function<Boolean, Entity> repositionEntity) {
        return repositionEntity.apply(false);
    }

    @Nullable
    @Override
    public PortalInfo getPortalInfo(Entity entity, ServerWorld targetWorld, Function<ServerWorld, PortalInfo> defaultPortalInfo) {
        // Ensure position does not surpass world border
        WorldBorder worldborder = targetWorld.getWorldBorder();
        double minX = Math.max(-2.9999872E7D, worldborder.minX() + 16.0D);
        double minZ = Math.max(-2.9999872E7D, worldborder.minZ() + 16.0D);
        double maxX = Math.min(2.9999872E7D, worldborder.maxX() - 16.0D);
        double maxZ = Math.min(2.9999872E7D, worldborder.maxZ() - 16.0D);

        // Dimension scale factor
        double scale = DimensionType.getCoordinateDifference(entity.getEntityWorld().getDimensionType(), targetWorld.getDimensionType());

        // Set position
        BlockPos.Mutable targetPos = new BlockPos.Mutable(
            MathHelper.clamp(entity.getPosX() * scale, minX, maxX),
            entity.getPosY(),
            MathHelper.clamp(entity.getPosZ() * scale, minZ, maxZ)
        );

        // Get settings for this dimension
        String sourceDimension = entity.getEntityWorld().getDimensionKey().getLocation().toString();
        PortalLakeVariantSettings settings = PortalLakeVariants.get().getVariantForDimension(sourceDimension);

        // Determine player's spawn point in target dimension, and spawn platform under player if necessary.
        int targetMinY = settings.getPlayerTeleportedMinY();
        int targetMaxY = settings.getPlayerTeleportedMaxY();
        BlockState spawnPlatformBlock = settings.getSpawnPlatformBlock();
        int targetY = -1;

        targetPos.setY(targetMaxY);

        boolean foundAir = false;
        for (int y = targetMaxY; y >= targetMinY; y--) {
            BlockState blockState = targetWorld.getBlockState(targetPos);
            if (blockState.getMaterial() == Material.AIR) {
                foundAir = true;
            } else if (blockState.getMaterial().isSolid() && foundAir) {
                targetY = y + 1;
                BlockUtil.fill(targetWorld, targetPos.getX() - 1, targetY, targetPos.getZ() - 1, targetPos.getX() + 1, targetY + 2, targetPos.getZ() + 1, Blocks.CAVE_AIR.getDefaultState());
                BlockUtil.replaceAir(targetWorld, targetPos.getX() - 1, targetY - 1, targetPos.getZ() - 1, targetPos.getX() + 1, targetY - 1, targetPos.getZ() + 1, spawnPlatformBlock);
                break;
            }
            targetPos.move(Direction.DOWN);
        }

        // If we didn't find a suitable spawn location...
        if (targetY == -1) {
            if (foundAir) {
                // It's air all the way - we need to spawn a platform somewhere
                targetY = (targetMaxY + targetMinY) / 2;
                BlockUtil.replaceAir(targetWorld, targetPos.getX() - 1, targetY - 1, targetPos.getZ() - 1, targetPos.getX() + 1, targetY - 1, targetPos.getZ() + 1, spawnPlatformBlock);
                BlockUtil.fill(targetWorld, targetPos.getX() - 1, targetY, targetPos.getZ() - 1, targetPos.getX() + 1, targetY + 2, targetPos.getZ() + 1, Blocks.CAVE_AIR.getDefaultState());
            } else {
                // It's solid blocks all the way - we need to carve out a spawn point
                targetY = (targetMaxY + targetMinY) / 2;
                BlockUtil.fill(targetWorld, targetPos.getX() - 1, targetY - 1, targetPos.getZ() - 1, targetPos.getX() + 1, targetY + 2, targetPos.getZ() + 1, Blocks.CAVE_AIR.getDefaultState());
            }
        }

        targetPos.setY(targetY);

        return new PortalInfo(Vector3d.copy(targetPos), Vector3d.ZERO, entity.rotationYaw, entity.rotationPitch);
    }

    /**
     * Static method for initializing player teleportation.
     */
    public static void initTeleport(Entity entity, IPlayerPortalInfo playerPortalInfo) {
        // Must not be riding anything
        if (entity.isPassenger() || entity.isBeingRidden() || !(entity instanceof ServerPlayerEntity)) {
            return;
        }

        ServerPlayerEntity playerEntity = (ServerPlayerEntity) entity;

        // Find target dimension for this fluid
        String sourceDimension = playerEntity.world.getDimensionKey().getLocation().toString();
        PortalLakeVariantSettings settings = PortalLakeVariants.get().getVariantForDimension(sourceDimension);
        String targetDimension = settings.getTargetDimension();

        MinecraftServer minecraftServer = playerEntity.getServer(); // the server itself
        ServerWorld targetWorld = minecraftServer.getWorld(RegistryKey.getOrCreateKey(Registry.WORLD_KEY, new ResourceLocation(targetDimension)));

        // Prevent crash due to mojang bug that makes mod's json dimensions not exist upload first creation of world on server. A restart fixes this.
        if (targetWorld == null) {
            BetterPortals.LOGGER.error("Unable to enter dimension. You may have entered the dimension name incorrectly: {}", targetDimension);
            BetterPortals.LOGGER.error("Alternatively, this could be due to a bug in vanilla Minecraft. Please restart the game to fix this.");
            return;
        }

        // Update player teleportation state and teleport the player
        playerEntity.changeDimension(targetWorld, new PortalLakeTeleporter());
        playerPortalInfo.reset();
    }

    /**
     * Static method for initializing non-player teleportation.
     */
    public static void initTeleport(Entity entity, IEntityPortalInfo entityPortalInfo) {
        // Must not be riding anything
        if (entity.isPassenger() || entity.isBeingRidden() || !entity.isNonBoss()) {
            return;
        }

        // Find target dimension for this fluid
        String sourceDimension = entity.world.getDimensionKey().getLocation().toString();
        PortalLakeVariantSettings settings = PortalLakeVariants.get().getVariantForDimension(sourceDimension);
        String targetDimension = settings.getTargetDimension();

        MinecraftServer minecraftServer = entity.getServer(); // the server itself
        ServerWorld targetWorld = minecraftServer.getWorld(RegistryKey.getOrCreateKey(Registry.WORLD_KEY, new ResourceLocation(targetDimension)));

        // Prevent crash due to mojang bug that makes mod's json dimensions not exist upload first creation of world on server. A restart fixes this.
        if (targetWorld == null) {
            BetterPortals.LOGGER.error("Unable to enter dimension. You may have entered the dimension name incorrectly: {}", targetDimension);
            BetterPortals.LOGGER.error("Alternatively, this could be due to a bug in vanilla Minecraft. Please restart the game to fix this.");
            return;
        }

        // Teleport entity
        entity.changeDimension(targetWorld, new PortalLakeTeleporter());
    }
}