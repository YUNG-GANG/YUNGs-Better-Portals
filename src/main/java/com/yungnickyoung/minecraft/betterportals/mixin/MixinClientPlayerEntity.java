package com.yungnickyoung.minecraft.betterportals.mixin;

import com.mojang.authlib.GameProfile;
import com.yungnickyoung.minecraft.betterportals.api.BetterPortalsCapabilities;
import com.yungnickyoung.minecraft.betterportals.config.Configuration;
import com.yungnickyoung.minecraft.betterportals.world.PortalLakeTeleporter;
import com.yungnickyoung.minecraft.betterportals.world.ReclaimerTeleporter;
import net.minecraft.block.PortalSize;
import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraftforge.fml.LogicalSide;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nullable;

/**
 * Mixin to prevent creating vanilla nether portals, if enabled.
 */
@Mixin(ClientPlayerEntity.class)
public abstract class MixinClientPlayerEntity extends AbstractClientPlayerEntity {
    public MixinClientPlayerEntity(ClientWorld world, GameProfile profile) {
        super(world, profile);
    }

    @Inject(at = @At("HEAD"), method="removeActivePotionEffect", cancellable = true)
    private void resetPortalAndReclaimerCounters(@Nullable Effect potioneffectin, CallbackInfoReturnable<EffectInstance> cir) {
        if (potioneffectin == Effects.NAUSEA) {
            this.getCapability(BetterPortalsCapabilities.PLAYER_PORTAL_INFO).ifPresent(playerPortalInfo -> {
                playerPortalInfo.setTimeInPortalFluid(0f);
                playerPortalInfo.setPrevTimeInPortalFluid(0f);
                playerPortalInfo.setTimeInReclaimer(0f);
                playerPortalInfo.setPrevTimeInReclaimer(0f);
            });
        }
    }
}