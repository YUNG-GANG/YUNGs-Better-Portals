package com.yungnickyoung.minecraft.betterportals.world;

import com.yungnickyoung.minecraft.betterportals.BetterPortals;
import com.yungnickyoung.minecraft.betterportals.api.IEntityPortalInfo;
import com.yungnickyoung.minecraft.betterportals.api.IPlayerPortalInfo;
import com.yungnickyoung.minecraft.betterportals.init.BPModFluids;
import com.yungnickyoung.minecraft.betterportals.init.BPModPOIs;
import com.yungnickyoung.minecraft.betterportals.util.BlockUtil;
import com.yungnickyoung.minecraft.betterportals.world.variant.MonolithVariantSettings;
import com.yungnickyoung.minecraft.betterportals.world.variant.MonolithVariants;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.PortalInfo;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Direction;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3i;
import net.minecraft.util.registry.Registry;
import net.minecraft.village.PointOfInterest;
import net.minecraft.village.PointOfInterestManager;
import net.minecraft.world.DimensionType;
import net.minecraft.world.border.WorldBorder;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.server.TicketType;
import net.minecraftforge.common.util.ITeleporter;

import javax.annotation.Nullable;
import java.util.Comparator;
import java.util.Optional;
import java.util.function.Function;

public class ReclaimerTeleporter implements ITeleporter {
    @Override
    public Entity placeEntity(Entity entity, ServerWorld currentWorld, ServerWorld destWorld, float yaw, Function<Boolean, Entity> repositionEntity) {
        return repositionEntity.apply(false); // pass in false to avoid attempting to spawn a vanilla portal
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

        // Set position. Note we cast the x/z to ints before multiplying to ensure consistency in the destination
        BlockPos.Mutable targetPos = new BlockPos.Mutable(
            MathHelper.clamp(((int) entity.getPosX()) * scale, minX, maxX),
            entity.getPosY(),
            MathHelper.clamp(((int) entity.getPosZ()) * scale, minZ, maxZ)
        );

        // First check if there is a portal fluid to teleport to
        PointOfInterestManager pointofinterestmanager = targetWorld.getPointOfInterestManager();
        int blockSearchRange = 128;
        pointofinterestmanager.ensureLoadedAndValid(targetWorld, targetPos, blockSearchRange);

        Optional<BlockPos> optional = pointofinterestmanager
            .getInSquare(poiType -> poiType == BPModPOIs.PORTAL_LAKE_POI, targetPos, blockSearchRange, PointOfInterestManager.Status.ANY)
            .map(PointOfInterest::getPos)
            .filter(pos -> {
                Fluid fluid = targetWorld.getBlockState(pos).getFluidState().getFluid();
                return fluid == BPModFluids.PORTAL_FLUID_FLOWING || fluid == BPModFluids.PORTAL_FLUID;
            })
            .filter(pos -> {
                BlockState above = targetWorld.getBlockState(pos.up());
                return above.getMaterial() == Material.AIR || above.getFluidState().getFluid() != Fluids.EMPTY;
            })
            .min(Comparator.comparingDouble(pos -> xzDist(pos, targetPos)));

        if (optional.isPresent()) {
            // Schedule ticket to force load the target chunk + surrounding chunks
            targetWorld.getChunkProvider().registerTicket(TicketType.PORTAL, new ChunkPos(optional.get()), 3, optional.get());
            return new PortalInfo(Vector3d.copy(optional.get()), Vector3d.ZERO, entity.rotationYaw, entity.rotationPitch);
        }

        // Get settings for this dimension
        String sourceDimension = entity.getEntityWorld().getDimensionKey().getLocation().toString();
        MonolithVariantSettings settings = MonolithVariants.get().getVariantForDimension(sourceDimension);

        // Determine player's spawn point in target dimension, and spawn platform under player if necessary.
        int targetMinY = settings.getPlayerTeleportedMinY();
        int targetMaxY = settings.getPlayerTeleportedMaxY();
        BlockState spawnPlatformBlock = Blocks.COBBLESTONE.getDefaultState();
        int targetY = -1;

        targetPos.setY(targetMaxY);

        boolean foundAir = false;
        int blocksSinceAir = 0;
        for (int y = targetMaxY; y >= targetMinY; y--) {
            BlockState blockState = targetWorld.getBlockState(targetPos);
            if (blockState.getMaterial() == Material.AIR) {
                foundAir = true;
            } else if (blockState.getMaterial().isSolid() && foundAir) {
                if (blocksSinceAir >= 2) {
                    targetY = y + 1;
                    break;
                } else {
                    // Reset the air tracking
                    foundAir = false;
                    blocksSinceAir = 0;
                }
            }
            targetPos.move(Direction.DOWN);
            if (foundAir) blocksSinceAir++;
        }

        // If we didn't find a suitable spawn location...
        if (targetY == -1) {
            targetY = (targetMaxY + targetMinY) / 2;
            // Replace liquid in shell around player to make sure they don't spawn in water/lava
            BlockUtil.replaceLiquid(targetWorld, targetPos.getX() - 2, targetY - 1, targetPos.getZ() - 2, targetPos.getX() + 2, targetY + 3, targetPos.getZ() + 2, spawnPlatformBlock);
            // Replace falling blocks around player to make sure they don't suffocate
            BlockUtil.replaceFallingBlock(targetWorld, targetPos.getX() - 2, targetY, targetPos.getZ() - 2, targetPos.getX() + 2, targetY + 3, targetPos.getZ() + 2, spawnPlatformBlock);
            // Fill surrounding area with air
            BlockUtil.fill(targetWorld, targetPos.getX() - 1, targetY, targetPos.getZ() - 1, targetPos.getX() + 1, targetY + 2, targetPos.getZ() + 1, Blocks.CAVE_AIR.getDefaultState());
            // Ensure platform is under player
            BlockUtil.fill(targetWorld, targetPos.getX() - 1, targetY - 1, targetPos.getZ() - 1, targetPos.getX() + 1, targetY - 1, targetPos.getZ() + 1, spawnPlatformBlock);

        }

        targetPos.setY(targetY);

        // Schedule ticket to force load the target chunk + surrounding chunks
        targetWorld.getChunkProvider().registerTicket(TicketType.PORTAL, new ChunkPos(targetPos), 3, targetPos);

        Vector3d playerPos = new Vector3d(targetPos.getX(), targetPos.getY(), targetPos.getZ());
        playerPos = playerPos.add(0.5, 0, 0.5);
        return new PortalInfo(playerPos, Vector3d.ZERO, entity.rotationYaw, entity.rotationPitch);
    }

    /**
     * Static method for teleporting player.
     */
    public static void initTeleport(Entity entity, IPlayerPortalInfo playerPortalInfo) {
        // Must not be riding anything
        if (entity.isPassenger() || entity.isBeingRidden() || !(entity instanceof ServerPlayerEntity)) {
            return;
        }

        ServerPlayerEntity playerEntity = (ServerPlayerEntity) entity;

        // Find target dimension for this reclaimer
        String sourceDimension = playerEntity.world.getDimensionKey().getLocation().toString();
        MonolithVariantSettings settings = MonolithVariants.get().getVariantForDimension(sourceDimension);

        if (settings == null) {
            BetterPortals.LOGGER.error("Unable to find reclaimer settings for dimension {}.", sourceDimension);
            BetterPortals.LOGGER.error("Have you removed the reclaimer variant for this dimension?");
            return;
        }

        String targetDimension = settings.getTargetDimension();

        MinecraftServer minecraftServer = playerEntity.getServer(); // the server itself
        ServerWorld targetWorld = minecraftServer.getWorld(RegistryKey.getOrCreateKey(Registry.WORLD_KEY, new ResourceLocation(targetDimension)));

        // Prevent crash due to mojang bug that makes modded json dimensions not exist upon first creation of world on server. A restart fixes this.
        if (targetWorld == null) {
            BetterPortals.LOGGER.error("Unable to enter dimension. You may have entered the dimension name {} incorrectly in your monoliths.json file.", targetDimension);
            BetterPortals.LOGGER.error("Alternatively, this could be due to a bug in vanilla Minecraft. Please restart the game to fix this.");
            return;
        }

        // Update player teleportation state and teleport the player
        playerEntity.changeDimension(targetWorld, new ReclaimerTeleporter());
        playerPortalInfo.reset();
    }

    /**
     * Static method for teleporting non-player entities.
     */
    public static void teleportNonPlayer(Entity entity, IEntityPortalInfo entityPortalInfo) {
        // Must not be riding anything
        if (entity.isPassenger() || entity.isBeingRidden() || !entity.isNonBoss()) {
            return;
        }

        // Find target dimension for this fluid
        String sourceDimension = entity.world.getDimensionKey().getLocation().toString();
        MonolithVariantSettings settings = MonolithVariants.get().getVariantForDimension(sourceDimension);

        if (settings == null) {
            return;
        }

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
        entityPortalInfo.reset();
        entity.changeDimension(targetWorld, new ReclaimerTeleporter());
    }

    private int xzDist(Vector3i pos1, Vector3i pos2) {
        int xDiff = pos1.getX() - pos2.getX();
        int zDiff = pos1.getZ() - pos2.getZ();
        return xDiff * xDiff + zDiff * zDiff;
    }
}
