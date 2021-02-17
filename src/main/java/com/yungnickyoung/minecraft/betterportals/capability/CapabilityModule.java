package com.yungnickyoung.minecraft.betterportals.capability;

import com.yungnickyoung.minecraft.betterportals.config.BPSettings;
import com.yungnickyoung.minecraft.betterportals.module.IModule;
import com.yungnickyoung.minecraft.betterportals.world.ReclaimerTeleporter;
import com.yungnickyoung.minecraft.betterportals.world.PortalLakeTeleporter;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

public class CapabilityModule implements IModule {
    // Capabilities
    @CapabilityInject(IPlayerPortalInfo.class)
    public static final Capability<IPlayerPortalInfo> PLAYER_PORTAL_INFO = null;

    @CapabilityInject(IEntityPortalInfo.class)
    public static final Capability<IEntityPortalInfo> ENTITY_PORTAL_INFO = null;

    @Override
    public void init() {
        MinecraftForge.EVENT_BUS.addGenericListener(Entity.class, CapabilityModule::onAttachCapabilitiesToEntities);
        MinecraftForge.EVENT_BUS.addListener(CapabilityModule::commonSetup);
        MinecraftForge.EVENT_BUS.addListener(CapabilityModule::onPlayerTick);
        MinecraftForge.EVENT_BUS.addListener(CapabilityModule::onEntityTick);
    }

    /**
     * Common setup. Registers the capabilities with the capability manager.
     */
    private static void commonSetup(FMLCommonSetupEvent event) {
        CapabilityManager.INSTANCE.register(IPlayerPortalInfo.class, new CapabilityFactory<>(), PlayerPortalInfo::new);
        CapabilityManager.INSTANCE.register(IEntityPortalInfo.class, new CapabilityFactory<>(), EntityPortalInfo::new);
    }

    /**
     * Attaches capabilities to player entity.
     */
    private static void onAttachCapabilitiesToEntities(AttachCapabilitiesEvent<Entity> event) {
        Entity entity = event.getObject();

        if (entity instanceof PlayerEntity && !entity.getCapability(CapabilityModule.PLAYER_PORTAL_INFO).isPresent()) {
            event.addCapability(
                new ResourceLocation(BPSettings.MOD_ID, "player_portal_info"),
                new PlayerPortalInfoProvider(new PlayerPortalInfo())
            );
        } else if (!entity.getCapability(CapabilityModule.ENTITY_PORTAL_INFO).isPresent()) {
            event.addCapability(
                new ResourceLocation(BPSettings.MOD_ID, "entity_portal_info"),
                new EntityPortalInfoProvider(new EntityPortalInfo())
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
    private static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) {
            return;
        }

        event.player.getCapability(CapabilityModule.PLAYER_PORTAL_INFO).ifPresent(playerPortalInfo -> {
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
            entity.getCapability(CapabilityModule.ENTITY_PORTAL_INFO).ifPresent(entityPortalInfo -> {
                entityPortalInfo.serverTick(entity, PortalLakeTeleporter::teleportNonPlayer, ReclaimerTeleporter::teleportNonPlayer);
            });
        }
    }
}