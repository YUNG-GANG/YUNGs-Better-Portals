package com.yungnickyoung.minecraft.betterportals.mixin;

import com.yungnickyoung.minecraft.betterportals.config.Configuration;
import net.minecraft.block.PortalSize;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PortalSize.class)
public class MixinPortalSize {
    @Inject(at = @At("INVOKE"), method="func_242974_d()I", cancellable = true)
    private void func_242974_d(CallbackInfoReturnable<Integer> callbackInfoReturnable) {
        if (!Configuration.enableVanillaPortals.get()) {
            callbackInfoReturnable.setReturnValue(0);
        }
    }
}
