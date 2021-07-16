package com.yungnickyoung.minecraft.betterportals.client;

import com.mojang.blaze3d.systems.RenderSystem;
import com.yungnickyoung.minecraft.betterportals.api.BetterPortalsCapabilities;
import com.yungnickyoung.minecraft.betterportals.init.BPModBlocks;
import com.yungnickyoung.minecraft.betterportals.config.BPSettings;
import com.yungnickyoung.minecraft.betterportals.world.variant.MonolithVariantSettings;
import com.yungnickyoung.minecraft.betterportals.world.variant.MonolithVariants;
import com.yungnickyoung.minecraft.betterportals.world.variant.PortalLakeVariantSettings;
import com.yungnickyoung.minecraft.betterportals.world.variant.PortalLakeVariants;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldVertexBufferUploader;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.potion.Effects;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraftforge.client.event.RenderBlockOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;

public class OverlayRenderer {
    private static final ResourceLocation TEXTURE_UNDERWATER = new ResourceLocation(BPSettings.MOD_ID, "textures/misc/portal_fluid_underwater.png");
    private static final ResourceLocation TEXTURE_RECLAIMER = ReclaimerTileEntityRenderer.END_GATEWAY_BEAM_TEXTURE;

    public static void renderUnderwaterOverlay(RenderBlockOverlayEvent event) {
        double eyeHeight = event.getPlayer().getPosYEye() - (1F / 9F);
        BlockPos blockpos = new BlockPos(event.getPlayer().getPosX(), eyeHeight, event.getPlayer().getPosZ());
        FluidState fluidstate = event.getPlayer().world.getFluidState(blockpos);
        BlockState blockState = event.getPlayer().world.getBlockState(blockpos);

        if (blockState.getBlock() == BPModBlocks.PORTAL_FLUID_BLOCK) {
            double fluidHeight = (float)blockpos.getY() + fluidstate.getActualHeight(event.getPlayer().world, blockpos);
            if (fluidHeight > eyeHeight) {
                Minecraft minecraft = Minecraft.getInstance();
                minecraft.getTextureManager().bindTexture(TEXTURE_UNDERWATER);
                BufferBuilder bufferbuilder = Tessellator.getInstance().getBuffer();
                float brightness = minecraft.player.getBrightness();
                float alpha = 0.8f;
                RenderSystem.enableBlend();
                RenderSystem.defaultBlendFunc();
                float yaw = -minecraft.player.rotationYaw / 64.0F;
                float pitch = minecraft.player.rotationPitch / 64.0F;
                Matrix4f matrix4f = event.getMatrixStack().getLast().getMatrix();
                bufferbuilder.begin(7, DefaultVertexFormats.POSITION_COLOR_TEX);
                bufferbuilder.pos(matrix4f, -1.0F, -1.0F, -0.5F).color(brightness, brightness, brightness, alpha).tex(4.0F + yaw, 4.0F + pitch).endVertex();
                bufferbuilder.pos(matrix4f, 1.0F, -1.0F, -0.5F).color(brightness, brightness, brightness, alpha).tex(0.0F + yaw, 4.0F + pitch).endVertex();
                bufferbuilder.pos(matrix4f, 1.0F, 1.0F, -0.5F).color(brightness, brightness, brightness, alpha).tex(0.0F + yaw, 0.0F + pitch).endVertex();
                bufferbuilder.pos(matrix4f, -1.0F, 1.0F, -0.5F).color(brightness, brightness, brightness, alpha).tex(4.0F + yaw, 0.0F + pitch).endVertex();
                bufferbuilder.finishDrawing();
                WorldVertexBufferUploader.draw(bufferbuilder);
                RenderSystem.disableBlend();
            }
        }
    }

    public static void renderPortalOverlay(RenderGameOverlayEvent.Pre event) {
        if (event.getType() != RenderGameOverlayEvent.ElementType.ALL) {
            return;
        }

        Minecraft.getInstance().player.getCapability(BetterPortalsCapabilities.PLAYER_PORTAL_INFO).ifPresent(playerPortalInfo -> {
            // Current amount of time in portal
            float portalTime = playerPortalInfo.getPrevTimeInPortalFluid() + (playerPortalInfo.getTimeInPortalFluid() - playerPortalInfo.getPrevTimeInPortalFluid()) * event.getPartialTicks();

            // If not in portal, nothing to render
            if (portalTime <= 0) {
                return;
            }

            // Minecraft instance
            Minecraft minecraft = Minecraft.getInstance();

            // Vanilla behavior - don't render nether portal overlay while nauseous
            if (minecraft.player.isPotionActive(Effects.NAUSEA)) {
                return;
            }

            // Get color information for overlay in this dimension
            String sourceDimension = minecraft.world.getDimensionKey().getLocation().toString();
            PortalLakeVariantSettings settings = PortalLakeVariants.get().getVariantForDimension(sourceDimension);

            if (settings == null) {
                return;
            }

            float[] colors = settings.getFluidColor().getColorComponentValues();

            // Alpha channel over time uses an adjusted portalTime value for a smoother transition
            if (portalTime < 1.0F) {
                portalTime = portalTime * 0.8F + 0.2F;
            }

            // Render nether portal overlay
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
            minecraft.getTextureManager().bindTexture(AtlasTexture.LOCATION_BLOCKS_TEXTURE);
            TextureAtlasSprite textureatlassprite = minecraft.getBlockRendererDispatcher().getBlockModelShapes().getTexture(Blocks.NETHER_PORTAL.getDefaultState());
            float minU = textureatlassprite.getMinU();
            float minV = textureatlassprite.getMinV();
            float maxU = textureatlassprite.getMaxU();
            float maxV = textureatlassprite.getMaxV();
            Tessellator tessellator = Tessellator.getInstance();
            BufferBuilder bufferbuilder = tessellator.getBuffer();
            bufferbuilder.begin(7, DefaultVertexFormats.POSITION_COLOR_TEX);
            double scaledWidth = event.getWindow().getScaledWidth();
            double scaledHeight = event.getWindow().getScaledHeight();
            bufferbuilder.pos(0.0D, scaledHeight, -90.0D).color(colors[0], colors[1], colors[2], portalTime).tex(minU, maxV).endVertex();
            bufferbuilder.pos(scaledWidth, scaledHeight, -90.0D).color(colors[0], colors[1], colors[2], portalTime).tex(maxU, maxV).endVertex();
            bufferbuilder.pos(scaledWidth, 0.0D, -90.0D).color(colors[0], colors[1], colors[2], portalTime).tex(maxU, minV).endVertex();
            bufferbuilder.pos(0.0D, 0.0D, -90.0D).color(colors[0], colors[1], colors[2], portalTime).tex(minU, minV).endVertex();
            tessellator.draw();
            RenderSystem.disableBlend();
        });
    }

    public static void renderReclaimerOverlay(RenderGameOverlayEvent.Pre event) {
        if (event.getType() != RenderGameOverlayEvent.ElementType.ALL) {
            return;
        }

        Minecraft.getInstance().player.getCapability(BetterPortalsCapabilities.PLAYER_PORTAL_INFO).ifPresent(playerPortalInfo -> {
            // Current amount of time in reclaimer
            float reclaimerTime = playerPortalInfo.getPrevTimeInReclaimer() + (playerPortalInfo.getTimeInReclaimer() - playerPortalInfo.getPrevTimeInReclaimer()) * event.getPartialTicks();

            // If not in reclaimer, nothing to render
            if (reclaimerTime <= 0) {
                return;
            }

            // Minecraft instance
            Minecraft minecraft = Minecraft.getInstance();

            // Vanilla behavior - don't render reclaimer overlay while nauseous
            if (minecraft.player.isPotionActive(Effects.NAUSEA)) {
                return;
            }

            // Get color information for overlay in this dimension
            String sourceDimension = minecraft.world.getDimensionKey().getLocation().toString();
            MonolithVariantSettings settings = MonolithVariants.get().getVariantForDimension(sourceDimension);

            if (settings == null) {
                return;
            }

            float[] colors = settings.getPoweredBeamColor().getColorComponentValues();

            // Alpha channel over time uses an adjusted reclaimerTime value for a smoother transition
            float alpha = reclaimerTime;
            if (alpha < 1.0F) {
                alpha = alpha * alpha;
                alpha = alpha * alpha;
                alpha = alpha * 0.8F + 0.2F;
            }
            alpha *= .8f;

            // Texture y-offset
            float speed = reclaimerTime * reclaimerTime;
            speed = speed * speed;
            speed = speed * .8f + .2f;
            float yOffset = speed * .8f * (minecraft.player.ticksExisted % 16);

            // Render reclaimer overlay
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
            minecraft.getTextureManager().bindTexture(TEXTURE_RECLAIMER);
            Tessellator tessellator = Tessellator.getInstance();
            BufferBuilder bufferbuilder = tessellator.getBuffer();
            bufferbuilder.begin(7, DefaultVertexFormats.POSITION_COLOR_TEX);
            double scaledWidth = event.getWindow().getScaledWidth();
            double scaledHeight = event.getWindow().getScaledHeight();
            bufferbuilder.pos(0.0D, scaledHeight, -90.0D).color(colors[0], colors[1], colors[2], alpha).tex(4.0F, 4.0F + yOffset).endVertex();
            bufferbuilder.pos(scaledWidth, scaledHeight, -90.0D).color(colors[0], colors[1], colors[2], alpha).tex(0.0F, 4.0F + yOffset).endVertex();
            bufferbuilder.pos(scaledWidth, 0.0D, -90.0D).color(colors[0], colors[1], colors[2], alpha).tex(0.0F, yOffset).endVertex();
            bufferbuilder.pos(0.0D, 0.0D, -90.0D).color(colors[0], colors[1], colors[2], alpha).tex(4.0F, yOffset).endVertex();
            tessellator.draw();
            RenderSystem.disableBlend();
        });
    }

    public static void renderDebugOverlay(RenderGameOverlayEvent.Text event) {
        PlayerEntity player = Minecraft.getInstance().player;
        player.getCapability(BetterPortalsCapabilities.PLAYER_PORTAL_INFO).ifPresent(playerPortalInfo -> {
//            event.getLeft().add("Reclaimer: ");
//            event.getLeft().add("  isIn: " + playerPortalInfo.isInReclaimer());
//            event.getLeft().add("  counter: " + playerPortalInfo.getReclaimerCounter());
//            event.getLeft().add("  cooldown: " + playerPortalInfo.getReclaimerCooldown());
//
//            event.getRight().add("Portal Fluid: ");
//            event.getRight().add("  isIn: " + playerPortalInfo.isInPortalFluid());
//            event.getRight().add("  counter: " + playerPortalInfo.getPortalFluidCounter());
//            event.getRight().add("  cooldown: " + playerPortalInfo.getPortalFluidCooldown());
//
//            event.getRight().add("");
//            event.getRight().add("");
//            event.getRight().add("");
//            event.getRight().add("");

            event.getRight().add("Render time: " + playerPortalInfo.getTimeInPortalFluid());
            event.getRight().add("Prev render time: " + playerPortalInfo.getPrevTimeInPortalFluid());
        });
    }
}
