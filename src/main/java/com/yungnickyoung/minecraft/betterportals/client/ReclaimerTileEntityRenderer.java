package com.yungnickyoung.minecraft.betterportals.client;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import com.yungnickyoung.minecraft.betterportals.block.ReclaimerTileEntity;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.tileentity.BeaconTileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Matrix3f;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

@OnlyIn(Dist.CLIENT)
public class ReclaimerTileEntityRenderer extends TileEntityRenderer<ReclaimerTileEntity> {
    public static final ResourceLocation TEXTURE_BEACON_BEAM = new ResourceLocation("textures/entity/beacon_beam.png");

    public ReclaimerTileEntityRenderer(TileEntityRendererDispatcher dispatcher) {
        super(dispatcher);
    }

    @Override
    @ParametersAreNonnullByDefault
    public void render(ReclaimerTileEntity tileEntity, float partialTicks, MatrixStack matrixStack, IRenderTypeBuffer renderTypeBuffer, int combinedLightIn, int combinedOverlayIn) {
        long time = tileEntity.getWorld().getGameTime();
        List<ReclaimerTileEntity.BeamPiece> beamSegments = tileEntity.getBeamPieces();
        int yOffset = 0;

        for(int i = 0; i < beamSegments.size(); ++i) {
            ReclaimerTileEntity.BeamPiece beamSegment = beamSegments.get(i);
            renderBeamSegment(matrixStack, renderTypeBuffer, partialTicks, time, yOffset, i == beamSegments.size() - 1 ? 1024 : beamSegment.getHeight(), beamSegment.getColors());
            yOffset += beamSegment.getHeight();
        }
    }

    private static void renderBeamSegment(MatrixStack matrixStack, IRenderTypeBuffer renderTypeBuffer, float partialTicks, long time, int yOffset, int height, float[] colors) {
        renderBeamSegment(matrixStack, renderTypeBuffer, TEXTURE_BEACON_BEAM, partialTicks, 1.0F, time, yOffset, height, colors, 0.2F, 0.25F);
    }

    public static void renderBeamSegment(MatrixStack matrixStack, IRenderTypeBuffer renderTypeBuffer, ResourceLocation texture, float partialTicks, float p_228842_4_, long time, int yOffset, int height, float[] colors, float p_228842_10_, float p_228842_11_) {
        int yVal = yOffset + height;
        matrixStack.push();
        matrixStack.translate(0.5D, 0.0D, 0.5D);
        float lvt_13_1_ = (float)Math.floorMod(time, 40L) + partialTicks;
        float lvt_14_1_ = height < 0 ? lvt_13_1_ : -lvt_13_1_;
        float lvt_15_1_ = MathHelper.frac(lvt_14_1_ * 0.2F - (float)MathHelper.floor(lvt_14_1_ * 0.1F));
        float lvt_16_1_ = colors[0];
        float lvt_17_1_ = colors[1];
        float lvt_18_1_ = colors[2];
        matrixStack.push();
        matrixStack.rotate(Vector3f.YP.rotationDegrees(lvt_13_1_ * 2.25F - 45.0F));
        float lvt_19_2_ = 0.0F;
        float lvt_22_2_ = 0.0F;
        float lvt_23_2_ = -p_228842_10_;
        float lvt_24_1_ = 0.0F;
        float lvt_25_1_ = 0.0F;
        float lvt_26_1_ = -p_228842_10_;
        float lvt_27_2_ = 0.0F;
        float lvt_28_2_ = 1.0F;
        float lvt_29_2_ = -1.0F + lvt_15_1_;
        float lvt_30_2_ = (float)height * p_228842_4_ * (0.5F / p_228842_10_) + lvt_29_2_;
        renderPart(matrixStack, renderTypeBuffer.getBuffer(RenderType.getBeaconBeam(texture, false)), lvt_16_1_, lvt_17_1_, lvt_18_1_, 1.0F, yOffset, yVal, 0.0F, p_228842_10_, p_228842_10_, 0.0F, lvt_23_2_, 0.0F, 0.0F, lvt_26_1_, 0.0F, 1.0F, lvt_30_2_, lvt_29_2_);
        matrixStack.pop();
        lvt_19_2_ = -p_228842_11_;
        float lvt_20_2_ = -p_228842_11_;
        lvt_22_2_ = -p_228842_11_;
        lvt_23_2_ = -p_228842_11_;
        lvt_27_2_ = 0.0F;
        lvt_28_2_ = 1.0F;
        lvt_29_2_ = -1.0F + lvt_15_1_;
        lvt_30_2_ = (float)height * p_228842_4_ + lvt_29_2_;
        renderPart(matrixStack, renderTypeBuffer.getBuffer(RenderType.getBeaconBeam(texture, true)), lvt_16_1_, lvt_17_1_, lvt_18_1_, 0.125F, yOffset, yVal, lvt_19_2_, lvt_20_2_, p_228842_11_, lvt_22_2_, lvt_23_2_, p_228842_11_, p_228842_11_, p_228842_11_, 0.0F, 1.0F, lvt_30_2_, lvt_29_2_);
        matrixStack.pop();
    }

    private static void renderPart(MatrixStack p_228840_0_, IVertexBuilder p_228840_1_, float p_228840_2_, float p_228840_3_, float p_228840_4_, float p_228840_5_, int p_228840_6_, int p_228840_7_, float p_228840_8_, float p_228840_9_, float p_228840_10_, float p_228840_11_, float p_228840_12_, float p_228840_13_, float p_228840_14_, float p_228840_15_, float p_228840_16_, float p_228840_17_, float p_228840_18_, float p_228840_19_) {
        MatrixStack.Entry lvt_20_1_ = p_228840_0_.getLast();
        Matrix4f lvt_21_1_ = lvt_20_1_.getMatrix();
        Matrix3f lvt_22_1_ = lvt_20_1_.getNormal();
        addQuad(lvt_21_1_, lvt_22_1_, p_228840_1_, p_228840_2_, p_228840_3_, p_228840_4_, p_228840_5_, p_228840_6_, p_228840_7_, p_228840_8_, p_228840_9_, p_228840_10_, p_228840_11_, p_228840_16_, p_228840_17_, p_228840_18_, p_228840_19_);
        addQuad(lvt_21_1_, lvt_22_1_, p_228840_1_, p_228840_2_, p_228840_3_, p_228840_4_, p_228840_5_, p_228840_6_, p_228840_7_, p_228840_14_, p_228840_15_, p_228840_12_, p_228840_13_, p_228840_16_, p_228840_17_, p_228840_18_, p_228840_19_);
        addQuad(lvt_21_1_, lvt_22_1_, p_228840_1_, p_228840_2_, p_228840_3_, p_228840_4_, p_228840_5_, p_228840_6_, p_228840_7_, p_228840_10_, p_228840_11_, p_228840_14_, p_228840_15_, p_228840_16_, p_228840_17_, p_228840_18_, p_228840_19_);
        addQuad(lvt_21_1_, lvt_22_1_, p_228840_1_, p_228840_2_, p_228840_3_, p_228840_4_, p_228840_5_, p_228840_6_, p_228840_7_, p_228840_12_, p_228840_13_, p_228840_8_, p_228840_9_, p_228840_16_, p_228840_17_, p_228840_18_, p_228840_19_);
    }

    private static void addQuad(Matrix4f p_228839_0_, Matrix3f p_228839_1_, IVertexBuilder p_228839_2_, float p_228839_3_, float p_228839_4_, float p_228839_5_, float p_228839_6_, int p_228839_7_, int p_228839_8_, float p_228839_9_, float p_228839_10_, float p_228839_11_, float p_228839_12_, float p_228839_13_, float p_228839_14_, float p_228839_15_, float p_228839_16_) {
        addVertex(p_228839_0_, p_228839_1_, p_228839_2_, p_228839_3_, p_228839_4_, p_228839_5_, p_228839_6_, p_228839_8_, p_228839_9_, p_228839_10_, p_228839_14_, p_228839_15_);
        addVertex(p_228839_0_, p_228839_1_, p_228839_2_, p_228839_3_, p_228839_4_, p_228839_5_, p_228839_6_, p_228839_7_, p_228839_9_, p_228839_10_, p_228839_14_, p_228839_16_);
        addVertex(p_228839_0_, p_228839_1_, p_228839_2_, p_228839_3_, p_228839_4_, p_228839_5_, p_228839_6_, p_228839_7_, p_228839_11_, p_228839_12_, p_228839_13_, p_228839_16_);
        addVertex(p_228839_0_, p_228839_1_, p_228839_2_, p_228839_3_, p_228839_4_, p_228839_5_, p_228839_6_, p_228839_8_, p_228839_11_, p_228839_12_, p_228839_13_, p_228839_15_);
    }

    private static void addVertex(Matrix4f p_228838_0_, Matrix3f p_228838_1_, IVertexBuilder p_228838_2_, float p_228838_3_, float p_228838_4_, float p_228838_5_, float p_228838_6_, int p_228838_7_, float p_228838_8_, float p_228838_9_, float p_228838_10_, float p_228838_11_) {
        p_228838_2_.pos(p_228838_0_, p_228838_8_, (float)p_228838_7_, p_228838_9_).color(p_228838_3_, p_228838_4_, p_228838_5_, p_228838_6_).tex(p_228838_10_, p_228838_11_).overlay(OverlayTexture.NO_OVERLAY).lightmap(15728880).normal(p_228838_1_, 0.0F, 1.0F, 0.0F).endVertex();
    }

    @Override
    public boolean isGlobalRenderer(ReclaimerTileEntity p_188185_1_) {
        return true;
    }
}
