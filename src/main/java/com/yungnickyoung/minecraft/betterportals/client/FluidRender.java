package com.yungnickyoung.minecraft.betterportals.client;

import com.mojang.blaze3d.systems.RenderSystem;
import com.yungnickyoung.minecraft.betterportals.BetterPortals;
import com.yungnickyoung.minecraft.betterportals.config.BPSettings;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldVertexBufferUploader;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RenderBlockOverlayEvent;

//@OnlyIn(Dist.CLIENT)
//public class FluidRender {
//
//    private static final ResourceLocation TEXTURE_UNDERWATER = new ResourceLocation(BPSettings.MOD_ID + ":textures/misc/portal_fluid_underwater.png");
//
//    public static void underwaterOverlay(RenderBlockOverlayEvent event)
//    {
//        if (event.getPlayer().world.getBlockState(event.getBlockPos()).getBlock() == BetterPortals.PORTAL_FLUID_BLOCK)
//        {
//            Minecraft minecraftIn = Minecraft.getInstance();
//            minecraftIn.getTextureManager().bindTexture(TEXTURE_UNDERWATER);
//            BufferBuilder bufferbuilder = Tessellator.getInstance().getBuffer();
//            float f = event.getPlayer().getBrightness();
//            RenderSystem.enableBlend();
//            RenderSystem.defaultBlendFunc();
//            float f7 = -event.getPlayer().rotationYaw / 64.0F;
//            float f8 = event.getPlayer().rotationPitch / 64.0F;
//            Matrix4f matrix4f = event.getMatrixStack().peek().getModel();
//            bufferbuilder.begin(7, DefaultVertexFormats.POSITION_COLOR_TEXTURE);
//            bufferbuilder.adertex(matrix4f, -1.0F, -1.0F, -0.5F).color(f, f, f, 0.42F).texture(4.0F + f7, 4.0F + f8).endVertex();
//            bufferbuilder.vertex(matrix4f, 1.0F, -1.0F, -0.5F).color(f, f, f, 0.42F).texture(0.0F + f7, 4.0F + f8).endVertex();
//            bufferbuilder.vertex(matrix4f, 1.0F, 1.0F, -0.5F).color(f, f, f, 0.42F).texture(0.0F + f7, 0.0F + f8).endVertex();
//            bufferbuilder.vertex(matrix4f, -1.0F, 1.0F, -0.5F).color(f, f, f, 0.42F).texture(4.0F + f7, 0.0F + f8).endVertex();
//            bufferbuilder.finishDrawing();
//            WorldVertexBufferUploader.draw(bufferbuilder);
//            RenderSystem.disableBlend();
//            event.setCanceled(true);
//        }
//    }
//}
