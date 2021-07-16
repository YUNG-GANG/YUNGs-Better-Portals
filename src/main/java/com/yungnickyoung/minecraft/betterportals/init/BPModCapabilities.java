package com.yungnickyoung.minecraft.betterportals.init;

import com.yungnickyoung.minecraft.betterportals.api.BetterPortalsCapabilities;
import com.yungnickyoung.minecraft.betterportals.api.IEntityPortalInfo;
import com.yungnickyoung.minecraft.betterportals.api.IPlayerPortalInfo;
import com.yungnickyoung.minecraft.betterportals.capability.EntityPortalInfo;
import com.yungnickyoung.minecraft.betterportals.capability.NBTCapabilityProvider;
import com.yungnickyoung.minecraft.betterportals.capability.PlayerPortalInfo;
import com.yungnickyoung.minecraft.betterportals.config.BPSettings;
import com.yungnickyoung.minecraft.betterportals.world.PortalLakeTeleporter;
import com.yungnickyoung.minecraft.betterportals.world.ReclaimerTeleporter;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

public class BPModCapabilities {
    public static void init() {
        MinecraftForge.EVENT_BUS.addGenericListener(Entity.class, BPModCapabilities::onAttachCapabilitiesToEntities);
        MinecraftForge.EVENT_BUS.addListener(BPModCapabilities::onPlayerTick);
        MinecraftForge.EVENT_BUS.addListener(BPModCapabilities::onEntityTick);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(BPModCapabilities::commonSetup);
    }

    /**
     * Common setup. Registers the capabilities with the capability manager.
     */
    private static void commonSetup(FMLCommonSetupEvent event) {
        CapabilityManager.INSTANCE.register(IPlayerPortalInfo.class, new CapabilityStorage<>(), PlayerPortalInfo::new);
        CapabilityManager.INSTANCE.register(IEntityPortalInfo.class, new CapabilityStorage<>(), EntityPortalInfo::new);
    }

    /**
     * Attaches capabilities to player entity.
     */
    private static void onAttachCapabilitiesToEntities(AttachCapabilitiesEvent<Entity> event) {
        if (event.getObject() instanceof PlayerEntity) {
            NBTCapabilityProvider<IPlayerPortalInfo> capabilityProvider = new NBTCapabilityProvider<>(BetterPortalsCapabilities.PLAYER_PORTAL_INFO, new PlayerPortalInfo());
            event.addCapability(new ResourceLocation(BPSettings.MOD_ID, "player_portal_info"), capabilityProvider);
        } else if (event.getObject() instanceof LivingEntity) {
            NBTCapabilityProvider<IEntityPortalInfo> capabilityProvider = new NBTCapabilityProvider<>(BetterPortalsCapabilities.ENTITY_PORTAL_INFO, new EntityPortalInfo());
            event.addCapability(new ResourceLocation(BPSettings.MOD_ID, "entity_portal_info"), capabilityProvider);
        }
    }

    /**
     * Capability helper class.
     */
    private static class CapabilityStorage<T> implements Capability.IStorage<T> {
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
    private static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) {
            return;
        }

        event.player.getCapability(BetterPortalsCapabilities.PLAYER_PORTAL_INFO).ifPresent(playerPortalInfo -> {
            if (event.side == LogicalSide.SERVER) {
                playerPortalInfo.serverTick(event.player, PortalLakeTeleporter::teleportPlayer, ReclaimerTeleporter::initTeleport);
            }
            if (event.side == LogicalSide.CLIENT) {
                playerPortalInfo.clientTick(event.player);
            }
        });
    }

    /**
     * Entity tick event.
     * Handles non-player portal teleportation.
     */
    private static void onEntityTick(LivingEvent.LivingUpdateEvent event) {
        LivingEntity entity = event.getEntityLiving();
        if (!(entity instanceof PlayerEntity) && entity.isServerWorld()) {
            entity.getCapability(BetterPortalsCapabilities.ENTITY_PORTAL_INFO).ifPresent(entityPortalInfo -> {
                entityPortalInfo.serverTick(entity, PortalLakeTeleporter::teleportNonPlayer, ReclaimerTeleporter::teleportNonPlayer);
            });
        }
    }
}