package com.yungnickyoung.minecraft.betterportals;

import com.yungnickyoung.minecraft.betterportals.client.BetterPortalsClient;
import com.yungnickyoung.minecraft.betterportals.config.BPSettings;
import com.yungnickyoung.minecraft.betterportals.init.BPBlocks;
import com.yungnickyoung.minecraft.betterportals.init.BPConfig;
import com.yungnickyoung.minecraft.betterportals.init.BPFeatures;
import com.yungnickyoung.minecraft.betterportals.init.BPItems;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(BPSettings.MOD_ID)
public class BetterPortals {
    public static final Logger LOGGER = LogManager.getLogger(BPSettings.MOD_ID);

    public BetterPortals() {
        BPConfig.init();
        BPFeatures.init();
        BPItems.init();
        BPBlocks.init();
        DistExecutor.safeRunWhenOn(Dist.CLIENT, () -> BetterPortalsClient::subscribeClientEvents);
    }
}
