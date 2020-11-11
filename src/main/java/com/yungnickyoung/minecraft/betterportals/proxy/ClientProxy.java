package com.yungnickyoung.minecraft.betterportals.proxy;

import com.yungnickyoung.minecraft.betterportals.module.ModuleLoader;

public class ClientProxy extends CommonProxy {
    public void init() {
        super.init();
        ModuleLoader.instance().initClientModules();
    }
}
