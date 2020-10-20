package com.yungnickyoung.minecraft.betterportals.block;

import com.google.common.collect.Lists;
import com.yungnickyoung.minecraft.betterportals.init.BPBlocks;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.List;

public class ReclaimerTileEntity extends TileEntity {
    private List<BeamPiece> beamSegments = Lists.newArrayList();

    public ReclaimerTileEntity() {
        super(BPBlocks.RECLAIMER_TILE_ENTITY);
    }

    public void read(BlockState state, CompoundNBT nbt) {
        super.read(state, nbt);
    }

    public CompoundNBT write(CompoundNBT compound) {
        super.write(compound);
        return compound;
    }

    @OnlyIn(Dist.CLIENT)
    public List<BeamPiece> getBeamPieces() {
        BeamPiece piece2 = new BeamPiece(new float[]{0, 0, 1});
        piece2.incrementHeight();

        return Lists.newArrayList(
            new BeamPiece(new float[]{0, 1, 0}),
            piece2,
            new BeamPiece(new float[]{1, 0, 0})
        );
//        return this.beamSegments;
    }

    /**
     * Retrieves packet to send to the client whenever this Tile Entity is resynced via World.notifyBlockUpdate. For
     * modded TE's, this packet comes back to you clientside in {@link #onDataPacket}
     */
    @Nullable
    public SUpdateTileEntityPacket getUpdatePacket() {
        return new SUpdateTileEntityPacket(this.pos, 3, this.getUpdateTag());
    }

    public CompoundNBT getUpdateTag() {
        return this.write(new CompoundNBT());
    }

    @OnlyIn(Dist.CLIENT)
    public double getMaxRenderDistanceSquared() {
        return 256.0D;
    }

    public static class BeamPiece {
        /** RGB (0 to 1.0) colors of this beam segment */
        private final float[] colors;
        private int height;

        public BeamPiece(float[] colorsIn) {
            this.colors = colorsIn;
            this.height = 1;
        }

        protected void incrementHeight() {
            ++this.height;
        }

        /**
         * Returns RGB (0 to 1.0) colors of this beam segment
         */
        @OnlyIn(Dist.CLIENT)
        public float[] getColors() {
            return this.colors;
        }

        @OnlyIn(Dist.CLIENT)
        public int getHeight() {
            return this.height;
        }
    }
}
