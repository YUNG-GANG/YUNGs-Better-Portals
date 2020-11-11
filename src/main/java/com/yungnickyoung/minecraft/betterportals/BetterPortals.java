package com.yungnickyoung.minecraft.betterportals;

import com.yungnickyoung.minecraft.betterportals.config.BPSettings;
import com.yungnickyoung.minecraft.betterportals.proxy.ClientProxy;
import com.yungnickyoung.minecraft.betterportals.proxy.CommonProxy;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(BPSettings.MOD_ID)
public class BetterPortals {
    public static final Logger LOGGER = LogManager.getLogger(BPSettings.MOD_ID);
    public static CommonProxy proxy;

    public BetterPortals() {
        proxy = DistExecutor.runForDist(() -> ClientProxy::new, () -> CommonProxy::new);
        proxy.init();
    }
}
