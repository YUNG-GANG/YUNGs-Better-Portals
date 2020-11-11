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
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.Map;

public class ModuleLoader {
    // Singleton stuff
    private static final ModuleLoader instance = new ModuleLoader();

    private ModuleLoader() {
        commonModules.put(ConfigModule.class, new ConfigModule());
        commonModules.put(BlockModule.class, new BlockModule());
        commonModules.put(CapabilityModule.class, new CapabilityModule());
        commonModules.put(FluidModule.class, new FluidModule());
        commonModules.put(ItemModule.class, new ItemModule());
        commonModules.put(TileEntityModule.class, new TileEntityModule());
        commonModules.put(WorldGenModule.class, new WorldGenModule());
        clientModules.put(ClientModule.class, new ClientModule());
    }

    public static ModuleLoader instance() {
        return instance;
    }

    // List of Modules
    Map<Class<? extends IModule>, IModule> commonModules = Maps.newHashMap();
    Map<Class<? extends IModule>, IModule> clientModules = Maps.newHashMap();

    /**
     * Initializes all modules.
     */
    public void initModules() {
        commonModules.values().forEach(IModule::init);
    }

    @OnlyIn(Dist.CLIENT)
    public void initClientModules() {
        clientModules.values().forEach(IModule::init);
    }
}
