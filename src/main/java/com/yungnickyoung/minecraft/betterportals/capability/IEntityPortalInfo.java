package com.yungnickyoung.minecraft.betterportals.capability;

import net.minecraft.entity.Entity;
import java.util.function.BiConsumer;

public interface IEntityPortalInfo {
    int TELEPORT_COOLDOWN = 100;

    boolean isInPortalFluid();
    boolean isInReclaimer();
    void setInPortalFluid(boolean isInPortalFluid);
    void setInReclaimer(boolean isInReclaimer);
    int getTeleportCooldown();
    void setTeleportCooldown(int teleportCooldown);

    default void enterReclaimer() {
        if (this.getTeleportCooldown() > 0) {
            this.setTeleportCooldown(TELEPORT_COOLDOWN);
        } else {
            this.setInReclaimer(true);
        }
    }

    default void enterPortalFluid() {
        if (this.getTeleportCooldown() > 0) {
            this.setTeleportCooldown(TELEPORT_COOLDOWN);
        } else {
            this.setInPortalFluid(true);
        }
    }

    default void reset() {
        setInPortalFluid(false);
        setInReclaimer(false);
        setTeleportCooldown(TELEPORT_COOLDOWN);
    }

    default void serverTick(
        Entity entity,
        BiConsumer<Entity, IEntityPortalInfo> portalTeleportHandler,
        BiConsumer<Entity, IEntityPortalInfo> reclaimerTeleportHandler
    ) {
        if (this.isInReclaimer()) {
            this.setInReclaimer(false);
            reclaimerTeleportHandler.accept(entity, this);
        }

        if (this.isInPortalFluid()) {
            this.setInPortalFluid(false);
            portalTeleportHandler.accept(entity, this);
        }

        // Decrement time until teleport available
        if (this.getTeleportCooldown() > 0) {
            this.setTeleportCooldown(this.getTeleportCooldown() - 1);
        }
    }
}
