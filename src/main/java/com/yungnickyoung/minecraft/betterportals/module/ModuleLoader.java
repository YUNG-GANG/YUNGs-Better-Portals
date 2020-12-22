package com.yungnickyoung.minecraft.betterportals.module;

import com.google.common.collect.Maps;
import com.yungnickyoung.minecraft.betterportals.block.BlockModule;
import com.yungnickyoung.minecraft.betterportals.capability.CapabilityModule;
import com.yungnickyoung.minecraft.betterportals.client.ClientModule;
import com.yungnickyoung.minecraft.betterportals.config.ConfigModule;
import com.yungnickyoung.minecraft.betterportals.fluid.FluidModule;
import com.yungnickyoung.minecraft.betterportals.item.ItemModule;
import com.yungnickyoung.minecraft.betterportals.tileentity.TileEntityModule;
import com.yungnickyoung.minecraft.betterportals.world.WorldGenModule;

import java.util.Map;

public class ModuleLoader {
    // Singleton instance
    private static final ModuleLoader instance = new ModuleLoader();

    private ModuleLoader() {
        registerCommonModules();
        registerClientModules();
    }

    public static ModuleLoader instance() {
        return instance;
    }

    // List of Modules
    Map<Class<? extends IModule>, IModule> commonModules = Maps.newHashMap();
    Map<Class<? extends IModule>, IModule> clientModules = Maps.newHashMap();

    /**
     * Initializes all common modules.
     */
    public void initCommonModules() {
        commonModules.values().forEach(IModule::init);
    }

    public void initClientModules() {
        clientModules.values().forEach(IModule::init);
    }

    private void registerCommonModules() {
        registerCommonModule(new ConfigModule());
        registerCommonModule(new BlockModule());
        registerCommonModule(new CapabilityModule());
        registerCommonModule(new FluidModule());
        registerCommonModule(new ItemModule());
        registerCommonModule(new TileEntityModule());
        registerCommonModule(new WorldGenModule());
    }

    private void registerClientModules() {
        registerClientModule(new ClientModule());
    }

    private <T extends IModule> void registerCommonModule(T module) {
        commonModules.put(module.getClass(), module);
    }

    private <T extends IModule> void registerClientModule(T module) {
        clientModules.put(module.getClass(), module);
    }
}
