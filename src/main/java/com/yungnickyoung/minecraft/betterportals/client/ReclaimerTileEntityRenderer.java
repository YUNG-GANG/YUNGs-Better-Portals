package com.yungnickyoung.minecraft.betterportals.client;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import com.yungnickyoung.minecraft.betterportals.block.ReclaimerTileEntity;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
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
    public static final ResourceLocation BEACON_BEAM_TEXTURE = new ResourceLocation("textures/entity/beacon_beam.png");
    public static final ResourceLocation END_GATEWAY_BEAM_TEXTURE = new ResourceLocation("textures/entity/end_gateway_beam.png");

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
            int height = i == beamSegments.size() - 1 ? 1024 : beamSegment.getHeight();
            renderBeamSegment(
                matrixStack,
                renderTypeBuffer,
                tileEntity.isPowered() ? BEACON_BEAM_TEXTURE : END_GATEWAY_BEAM_TEXTURE,
                partialTicks,
                1f,
                time,
                yOffset,
                height,
                beamSegment.getColors(),
                tileEntity.isPowered() ? .4f : .15f,
                tileEntity.isPowered() ? .6f : .2f,
                tileEntity.isPowered() ? 2.5f : .5f
            );
            yOffset += beamSegment.getHeight();
        }
    }

    public static void renderBeamSegment(MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, ResourceLocation textureLocation, float partialTicks, float textureScale, long totalWorldTime, int yMin, int height, float[] colors, float beamRadius, float glowRadius, float rotationAmp) {
        int yMax = yMin + height;
        matrixStackIn.push();
        matrixStackIn.translate(0.5D, 0.0D, 0.5D);
        float rotation = rotationAmp * (float)Math.floorMod(totalWorldTime, (long)(40f / rotationAmp)) + partialTicks;
        float f1 = height < 0 ? rotation : -rotation;
        float f2 = MathHelper.frac(f1 * 0.2F - (float)MathHelper.floor(f1 * 0.1F));
        float red = colors[0];
        float green = colors[1];
        float blue = colors[2];

        matrixStackIn.push();
        matrixStackIn.rotate(Vector3f.YP.rotationDegrees(rotation * 2.25f - (45f / rotationAmp)));

        float f15 = -1.0F + f2;
        float f16 = (float)height * textureScale * (0.5F / beamRadius) + f15;

        // Render beam
        renderPart(
            matrixStackIn,
            bufferIn.getBuffer(RenderType.getBeaconBeam(textureLocation, false)),
            red, green, blue, 1.0F,
            yMin, yMax,
            0, beamRadius, beamRadius, 0,
            -beamRadius, 0, 0, -beamRadius,
            0.0F, 1.0F, f16, f15
        );

        matrixStackIn.pop();

        f15 = -1.0F + f2;
        f16 = (float)height * textureScale + f15;

        // Render glow
        renderPart(
            matrixStackIn,
            bufferIn.getBuffer(RenderType.getBeaconBeam(textureLocation, true)),
            red, green, blue, 0.125F,
            yMin, yMax,
            -glowRadius, -glowRadius, glowRadius, -glowRadius,
            -glowRadius, glowRadius, glowRadius, glowRadius,
            0.0F, 1.0F, f16, f15
        );
        matrixStackIn.pop();
    }

    private static void renderPart(MatrixStack matrixStackIn, IVertexBuilder bufferIn, float red, float green, float blue, float alpha, int yMin, int yMax, float x1, float z1, float x2, float z2, float p_228840_12_, float p_228840_13_, float p_228840_14_, float p_228840_15_, float u1, float u2, float v1, float v2) {
        MatrixStack.Entry matrixstack$entry = matrixStackIn.getLast();
        Matrix4f matrix4f = matrixstack$entry.getMatrix();
        Matrix3f matrix3f = matrixstack$entry.getNormal();
        addQuad(matrix4f, matrix3f, bufferIn, red, green, blue, alpha, yMin, yMax, x1, z1, x2, z2, u1, u2, v1, v2);
        addQuad(matrix4f, matrix3f, bufferIn, red, green, blue, alpha, yMin, yMax, p_228840_14_, p_228840_15_, p_228840_12_, p_228840_13_, u1, u2, v1, v2);
        addQuad(matrix4f, matrix3f, bufferIn, red, green, blue, alpha, yMin, yMax, x2, z2, p_228840_14_, p_228840_15_, u1, u2, v1, v2);
        addQuad(matrix4f, matrix3f, bufferIn, red, green, blue, alpha, yMin, yMax, p_228840_12_, p_228840_13_, x1, z1, u1, u2, v1, v2);
    }

    private static void addQuad(Matrix4f matrixPos, Matrix3f matrixNormal, IVertexBuilder bufferIn, float red, float green, float blue, float alpha, int yMin, int yMax, float x1, float z1, float x2, float z2, float u1, float u2, float v1, float v2) {
        addVertex(matrixPos, matrixNormal, bufferIn, red, green, blue, alpha, yMax, x1, z1, u2, v1);
        addVertex(matrixPos, matrixNormal, bufferIn, red, green, blue, alpha, yMin, x1, z1, u2, v2);
        addVertex(matrixPos, matrixNormal, bufferIn, red, green, blue, alpha, yMin, x2, z2, u1, v2);
        addVertex(matrixPos, matrixNormal, bufferIn, red, green, blue, alpha, yMax, x2, z2, u1, v1);
    }

    private static void addVertex(Matrix4f matrixPos, Matrix3f matrixNormal, IVertexBuilder bufferIn, float red, float green, float blue, float alpha, int y, float x, float z, float texU, float texV) {
        bufferIn.pos(matrixPos, x, (float)y, z).color(red, green, blue, alpha).tex(texU, texV).overlay(OverlayTexture.NO_OVERLAY).lightmap(15728880).normal(matrixNormal, 0.0F, 1.0F, 0.0F).endVertex();
    }

    @Override
    public boolean isGlobalRenderer(ReclaimerTileEntity tileEntity) {
        return true;
    }
}
