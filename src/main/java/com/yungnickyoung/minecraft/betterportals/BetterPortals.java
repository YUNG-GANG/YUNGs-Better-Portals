package com.yungnickyoung.minecraft.betterportals;

import com.yungnickyoung.minecraft.betterportals.init.*;
import com.yungnickyoung.minecraft.betterportals.init.BPModCapabilities;
import com.yungnickyoung.minecraft.betterportals.config.BPSettings;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(BPSettings.MOD_ID)
public class BetterPortals {
    public static final Logger LOGGER = LogManager.getLogger(BPSettings.MOD_ID);

    public BetterPortals() {
        init();
    }

    private void init() {
        BPModBlocks.init();
        BPModCapabilities.init();
        BPModFluids.init();
        BPModItems.init();
        BPModTileEntities.init();
        BPModPlacements.init();
        BPModFeatures.init();
        BPModPOIs.init();
        BPModConfig.init();
        DistExecutor.safeRunWhenOn(Dist.CLIENT, () -> BPModClient::init);
    }
}
