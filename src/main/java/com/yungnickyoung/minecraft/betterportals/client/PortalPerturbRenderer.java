package com.yungnickyoung.minecraft.betterportals.client;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.yungnickyoung.minecraft.betterportals.capability.CapabilityModule;
import com.yungnickyoung.minecraft.betterportals.capability.IPlayerPortalInfo;
import net.minecraft.client.Minecraft;
import net.minecraft.potion.Effects;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.client.event.RenderWorldLastEvent;

//public class PortalPerturbRenderer {
//    public static void renderPortalPerturb(RenderWorldLastEvent event) {
//        IPlayerPortalInfo playerPortalInfo = Minecraft.getInstance().player.getCapability(CapabilityModule.PLAYER_PORTAL_INFO).resolve().orElse(null);
//
//        if (playerPortalInfo == null) {
//            return;
//        }
//
//        float portalTime = playerPortalInfo.getPrevTimeInPortalFluid() + (playerPortalInfo.getTimeInPortalFluid() - playerPortalInfo.getPrevTimeInPortalFluid()) * event.getPartialTicks();
//
//        // If not in portal, nothing to render
//        if (portalTime <= 0) {
//            return;
//        }
//
//        // Minecraft instance
//        Minecraft minecraft = Minecraft.getInstance();
//
//        // Render screen perturbation
//        float screenPerturb = portalTime * minecraft.gameSettings.screenEffectScale * minecraft.gameSettings.screenEffectScale;
//        float amp = minecraft.player.isPotionActive(Effects.NAUSEA) ? 7f : 20f;
//        float f1 = 5f / (screenPerturb * screenPerturb + 5f) - screenPerturb * 0.04f;
//        f1 = f1 * f1;
//        Vector3f vector3f = new Vector3f(0, MathHelper.SQRT_2 / 2f, MathHelper.SQRT_2 / 2f);
//        float rotDegrees =  (((float) minecraft.player.ticksExisted) + event.getPartialTicks()) * amp;
//        MatrixStack matrixstack = new MatrixStack();
//        matrixstack.getLast().getMatrix().mul(event.getProjectionMatrix());
//        matrixstack.rotate(vector3f.rotationDegrees(rotDegrees));
//        matrixstack.scale(1f / f1, 1f, 1f);
//        matrixstack.rotate(vector3f.rotationDegrees(-rotDegrees));
//        Matrix4f matrix4f = event.getMatrixStack().getLast().getMatrix();
//        minecraft.gameRenderer.resetProjectionMatrix(matrix4f);
//    }
//}
