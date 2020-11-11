package com.yungnickyoung.minecraft.betterportals.capability;

import com.yungnickyoung.minecraft.betterportals.BetterPortals;
import com.yungnickyoung.minecraft.betterportals.config.BPSettings;
import com.yungnickyoung.minecraft.betterportals.module.IModule;
import com.yungnickyoung.minecraft.betterportals.util.BlockUtil;
import com.yungnickyoung.minecraft.betterportals.world.variant.MonolithVariantSettings;
import com.yungnickyoung.minecraft.betterportals.world.variant.MonolithVariants;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Direction;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

public class CapabilityModule implements IModule {
    // Capabilities
    @CapabilityInject(IPlayerPortalInfo.class)
    public static final Capability<IPlayerPortalInfo> PLAYER_PORTAL_INFO = null;

    @Override
    public void init() {
        MinecraftForge.EVENT_BUS.addGenericListener(Entity.class, CapabilityModule::onAttachCapabilitiesToEntities);
        MinecraftForge.EVENT_BUS.addListener(CapabilityModule::commonSetup);
        MinecraftForge.EVENT_BUS.addListener(this::onPlayerTick);
    }

    /**
     * Common setup. Registers the capabilities with the capability manager.
     */
    private static void commonSetup(FMLCommonSetupEvent event) {
        CapabilityManager.INSTANCE.register(IPlayerPortalInfo.class, new CapabilityFactory<>(), PlayerPortalInfo::new);
    }

    /**
     * Attaches PlayerPortalInfo capability to player entities.
     */
    private static void onAttachCapabilitiesToEntities(AttachCapabilitiesEvent<Entity> event) {
        Entity entity = event.getObject();
        if (entity instanceof PlayerEntity) {
            event.addCapability(
                new ResourceLocation(BPSettings.MOD_ID, "player_portal_counter_info"),
                new PlayerPortalInfoProvider(new PlayerPortalInfo())
            );
        }
    }

    /**
     * Capability Factory helper class.
     */
    private static class CapabilityFactory<T> implements Capability.IStorage<T> {
        @Override
        public INBT writeNBT(Capability<T> capability, T instance, Direction side) {
            if (instance instanceof INBTSerializable)
                return ((INBTSerializable<?>) instance).serializeNBT();
            return null;
        }

        @Override
        @SuppressWarnings("unchecked")
        public void readNBT(Capability<T> capability, T instance, Direction side, INBT nbt) {
            if (nbt instanceof CompoundNBT)
                ((INBTSerializable<INBT>) instance).deserializeNBT(nbt);
        }
    }

    /**
     * Player tick event.
     * Updates custom player portal info.
     */
    private void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (!event.side.isServer()) {
            return;
        }

        // Get portal info capability for player
        IPlayerPortalInfo playerPortalInfo = event.player.getCapability(CapabilityModule.PLAYER_PORTAL_INFO).resolve().orElse(null);
        if (playerPortalInfo == null) {
            return;
        }

        // Update player teleport status
        if (playerPortalInfo.isInPortal()) {
            playerPortalInfo.offsetCounter(1); // Increment counter
            if (!event.player.isPassenger() && playerPortalInfo.getPortalCounter() >= 80) {
                playerPortalInfo.setPortalCounter(80);
                playerPortalInfo.setPortalCooldown(300); // Reset cooldown
                teleportPlayer(event.player);
                playerPortalInfo.setInPortal(false);
            }
        } else {
            if (playerPortalInfo.getPortalCounter() > 0) {
                playerPortalInfo.offsetCounter(-4);
            }

            if (playerPortalInfo.getPortalCounter() < 0) {
                playerPortalInfo.setPortalCounter(0);
            }
        }

        // Decrement time until portal
        if (playerPortalInfo.getPortalCooldown() > 0) {
            playerPortalInfo.offsetCooldown(-1);
        }
    }

    /**
     * Teleports the player to the target dimension given in the monolith's settings.
     */
    private void teleportPlayer(Entity entity) {
        // Must not be riding anything
        if (entity.isPassenger() || entity.isBeingRidden()) {
            return;
        }

        ServerPlayerEntity playerEntity = (ServerPlayerEntity) entity;
        // Find target dimension for this reclaimer
        String sourceDimension = playerEntity.world.getDimensionKey().func_240901_a_().toString();
        MonolithVariantSettings settings = MonolithVariants.get().getVariantForDimension(sourceDimension);
        String targetDimension = settings.getTargetDimension();

        MinecraftServer minecraftServer = playerEntity.getServer(); // the server itself
        ServerWorld targetWorld = minecraftServer.getWorld(RegistryKey.func_240903_a_(Registry.WORLD_KEY, new ResourceLocation(targetDimension)));

        // Prevent crash due to mojang bug that makes mod's json dimensions not exist upload first creation of world on server. A restart fixes this.
        if (targetWorld == null) {
            BetterPortals.LOGGER.error("Unable to enter dimension.");
            BetterPortals.LOGGER.error("This is due to a bug in vanilla Minecraft. Please restart the game to fix this.");
            return;
        }

        // TODO - monolith-specific teleport

        // Determine player's spawn point in target dimension, and ...
        // ... spawn platform under player if necessary.
        int targetMinY = 10;
        int targetMaxY = 80;
        BlockState spawnPlatformBlock = Blocks.COBBLESTONE.getDefaultState();
        int targetY = -1;
        BlockPos.Mutable targetPos = entity.getPosition().toMutable();
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

        // If we didn't find a suitable spawn location...
        if (targetY == -1) {
            if (foundAir) {
                // It's air all the way - we need to spawn a platform somewhere
                targetY = (targetMaxY + targetMinY) / 2;
                BlockUtil.fill(targetWorld, targetPos.getX() - 1, targetY - 1, targetPos.getZ() - 1, targetPos.getX() + 1, targetY - 1, targetPos.getZ() + 1, spawnPlatformBlock);
                BlockUtil.fill(targetWorld, targetPos.getX() - 1, targetY, targetPos.getZ() - 1, targetPos.getX() + 1, targetY + 2, targetPos.getZ() + 1, Blocks.CAVE_AIR.getDefaultState());
            } else {
                // It's solid blocks all the way - we need to carve out a spawn point
                targetY = (targetMaxY + targetMinY) / 2;
                BlockUtil.fill(targetWorld, targetPos.getX() - 1, targetY - 1, targetPos.getZ() - 1, targetPos.getX() + 1, targetY + 2, targetPos.getZ() + 1, Blocks.CAVE_AIR.getDefaultState());
            }
        }

        // Poof!
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
