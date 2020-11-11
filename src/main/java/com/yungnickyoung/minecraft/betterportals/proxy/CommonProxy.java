package com.yungnickyoung.minecraft.betterportals.proxy;

import com.yungnickyoung.minecraft.betterportals.module.ModuleLoader;

public class CommonProxy {
    public void init() {
        ModuleLoader.instance().initModules();
    }
}
