package com.yungnickyoung.minecraft.betterportals.capability;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SimpleSound;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.potion.Effects;
import net.minecraft.util.SoundEvents;

import java.util.Random;
import java.util.function.BiConsumer;

public interface IPlayerPortalInfo {
    int PORTAL_COOLDOWN = 10;
    int RECLAIMER_COOLDOWN = 10;

    /*
     * Common getters (both sides)
     */

    int getReclaimerCounter();
    int getReclaimerCooldown();
    boolean isInReclaimer();
    int getPortalFluidCounter();
    int getPortalFluidCooldown();
    boolean isInPortalFluid();

    /*
     * Common setters (both sides)
     */

    void setReclaimerCounter(int counter);
    void setReclaimerCooldown(int cooldown);
    void setInReclaimer(boolean inPortal);
    void setPortalFluidCounter(int counter);
    void setPortalFluidCooldown(int cooldown);
    void setInPortalFluid(boolean inPortal);

    /*
     * Client getters
     */

    float getTimeInPortalFluid();
    float getPrevTimeInPortalFluid();
    float getTimeInReclaimer();
    float getPrevTimeInReclaimer();

    /*
     * Client setters
     */

    void setTimeInPortalFluid(float time);
    void setPrevTimeInPortalFluid(float time);
    void setTimeInReclaimer(float time);
    void setPrevTimeInReclaimer(float time);

    /*
     * Common offset methods (both sides)
     */

    default void offsetReclaimerCounter(int offset) {
        setReclaimerCounter(getReclaimerCounter() + offset);
    }

    default void offsetReclaimerCooldown(int offset) {
        setReclaimerCooldown(getReclaimerCooldown() + offset);
    }

    default void offsetPortalFluidCounter(int offset) {
        setPortalFluidCounter(getPortalFluidCounter() + offset);
    }

    default void offsetPortalFluidCooldown(int offset) {
        setPortalFluidCooldown(getPortalFluidCooldown() + offset);
    }

    /*
     * Client offset methods
     */

    default void offsetTimeInPortalFluid(float offset) {
        setTimeInPortalFluid(getTimeInPortalFluid() + offset);
    }

    default void offsetTimeInReclaimer(float offset) {
        setTimeInReclaimer(getTimeInReclaimer() + offset);
    }

    /*
     * Common behavior methods (both sides)
     */

    default void enterReclaimer() {
        if (this.getReclaimerCooldown() > 0) {
            this.setReclaimerCooldown(RECLAIMER_COOLDOWN);
        } else {
            this.setInReclaimer(true);
        }
    }

    default void enterPortalFluid() {
        if (this.getPortalFluidCooldown() > 0) {
            this.setPortalFluidCooldown(PORTAL_COOLDOWN);
        } else {
            this.setInPortalFluid(true);
        }
    }

    default void reset() {
        setReclaimerCounter(0);
        setReclaimerCooldown(RECLAIMER_COOLDOWN);
        setInReclaimer(false);
        setPortalFluidCounter(0);
        setPortalFluidCooldown(PORTAL_COOLDOWN);
        setInPortalFluid(false);
    }

    default void serverTick(
        PlayerEntity player,
        BiConsumer<Entity, IPlayerPortalInfo> portalTeleportHandler,
        BiConsumer<Entity, IPlayerPortalInfo> reclaimerTeleportHandler
    ) {
        this.setDEBUGserverTickCounter(this.getDEBUGserverTickCounter() + 1);

        // Update player reclaimer teleport status
        if (this.isInReclaimer()) {
            this.offsetReclaimerCounter(1); // Increment counter
            if (!player.isPassenger() && this.getReclaimerCounter() >= 80) {
                this.setReclaimerCounter(80);
                this.setReclaimerCooldown(RECLAIMER_COOLDOWN); // Reset cooldown
                reclaimerTeleportHandler.accept(player, this);
            }
            this.setInReclaimer(false);
        } else {
            if (this.getReclaimerCounter() > 0) {
                this.offsetReclaimerCounter(-4);
            }

            if (this.getReclaimerCounter() < 0) {
                this.setReclaimerCounter(0);
            }
        }

        // Update player portal fluid teleport status
        if (this.isInPortalFluid()) {
            this.offsetPortalFluidCounter(1); // Increment counter
            if (!player.isPassenger() && this.getPortalFluidCounter() >= 80) {
                this.setPortalFluidCounter(80);
                this.setPortalFluidCooldown(PORTAL_COOLDOWN); // Reset cooldown
                portalTeleportHandler.accept(player, this);
            }
            this.setInPortalFluid(false);
        } else {
            if (this.getPortalFluidCounter() > 0) {
                this.offsetPortalFluidCounter(-4);
            }

            if (this.getPortalFluidCounter() < 0) {
                this.setPortalFluidCounter(0);
            }
        }

        // Decrement time until teleport via reclaimer available
        if (this.getReclaimerCooldown() > 0) {
            this.offsetReclaimerCooldown(-1);
        }

        // Decrement time until teleport via portal fluid available
        if (this.getPortalFluidCooldown() > 0) {
            this.offsetPortalFluidCooldown(-1);
        }
    }


    default void clientTick(PlayerEntity player) {
        this.setDEBUGclientTickCounter(this.getDEBUGclientTickCounter() + 1);

        this.setPrevTimeInPortalFluid(this.getTimeInPortalFluid());
        this.setPrevTimeInReclaimer(this.getTimeInReclaimer());

        // Portal fluid rendering
        if (this.isInPortalFluid()) {
            if (Minecraft.getInstance().currentScreen != null && !Minecraft.getInstance().currentScreen.isPauseScreen()) {
                if (Minecraft.getInstance().currentScreen instanceof ContainerScreen) {
                    player.closeScreen();
                }

                Minecraft.getInstance().displayGuiScreen(null);
            }

            if (this.getTimeInPortalFluid() == 0.0F) {
                Minecraft.getInstance().getSoundHandler().play(SimpleSound.ambientWithoutAttenuation(SoundEvents.BLOCK_PORTAL_TRIGGER, (new Random()).nextFloat() * 0.4F + 0.8F, 0.25F));
            }

            this.offsetTimeInPortalFluid(0.0125F);
            if (this.getTimeInPortalFluid() >= 1.0F) {
                this.setTimeInPortalFluid(1.0F);
            }

            this.setInPortalFluid(false);
        } else if (player.isPotionActive(Effects.NAUSEA) && player.getActivePotionEffect(Effects.NAUSEA).getDuration() > 60) {
            this.offsetTimeInPortalFluid(2F / 300F);
            if (this.getTimeInPortalFluid() > 1.0F) {
                this.setTimeInPortalFluid(1.0F);
            }
        } else {
            if (this.getTimeInPortalFluid() > 0.0F) {
                this.offsetTimeInPortalFluid(-.05F);
            }

            if (this.getTimeInPortalFluid() < 0.0F) {
                this.setTimeInPortalFluid(0.0F);
            }
        }

        // Reclaimer rendering
        if (this.isInReclaimer()) {
            if (Minecraft.getInstance().currentScreen != null && !Minecraft.getInstance().currentScreen.isPauseScreen()) {
                if (Minecraft.getInstance().currentScreen instanceof ContainerScreen) {
                    player.closeScreen();
                }

                Minecraft.getInstance().displayGuiScreen(null);
            }

            if (this.getTimeInReclaimer() == 0.0F) {
                Minecraft.getInstance().getSoundHandler().play(SimpleSound.ambientWithoutAttenuation(SoundEvents.BLOCK_PORTAL_TRIGGER, 0.4F + 0.8F, 0.25F));
            }

            this.offsetTimeInReclaimer(0.0125F);
            if (this.getTimeInReclaimer() >= 1.0F) {
                this.setTimeInReclaimer(1.0F);
            }

            this.setInReclaimer(false);
        } else if (player.isPotionActive(Effects.NAUSEA) && player.getActivePotionEffect(Effects.NAUSEA).getDuration() > 60) {
            this.offsetTimeInReclaimer(2F / 300F);
            if (this.getTimeInReclaimer() > 1.0F) {
                this.setTimeInReclaimer(1.0F);
            }
        } else {
            if (this.getTimeInReclaimer() > 0.0F) {
                this.offsetTimeInReclaimer(-.05F);
            }

            if (this.getTimeInReclaimer() < 0.0F) {
                this.setTimeInReclaimer(0.0F);
            }
        }

        // Decrement time until teleport via portal fluid available
        if (this.getPortalFluidCooldown() > 0) {
            this.offsetPortalFluidCooldown(-1);
        }

        // Decrement time until teleport via portal fluid available
        if (this.getReclaimerCooldown() > 0) {
            this.offsetReclaimerCooldown(-1);
        }
    }

    /*
     * DEBUG
     */

    int getDEBUGclientTickCounter();
    int getDEBUGserverTickCounter();
    int getDEBUGportalCounter();
    void setDEBUGclientTickCounter(int counter);
    void setDEBUGserverTickCounter(int counter);
    void setDEBUGportalCounter(int counter);
}
