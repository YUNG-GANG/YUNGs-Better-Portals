package com.yungnickyoung.minecraft.betterportals.fluid;

import com.yungnickyoung.minecraft.betterportals.block.BlockModule;
import com.yungnickyoung.minecraft.betterportals.config.BPSettings;
import com.yungnickyoung.minecraft.betterportals.item.ItemModule;
import com.yungnickyoung.minecraft.betterportals.module.IModule;
import net.minecraft.fluid.FlowingFluid;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.Rarity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fluids.ForgeFlowingFluid;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

public class FluidModule implements IModule {
    public static ForgeFlowingFluid.Properties PORTAL_FLUID_PROPERTIES;
    public static FlowingFluid PORTAL_FLUID;
    public static FlowingFluid PORTAL_FLUID_FLOWING;

    @Override
    public void init() {
        FMLJavaModLoadingContext.get().getModEventBus().addGenericListener(Fluid.class, FluidModule::registerFluids);

        // Initialize fluids & properties
        PORTAL_FLUID_PROPERTIES = new ForgeFlowingFluid.Properties(
            () -> PORTAL_FLUID, () -> PORTAL_FLUID_FLOWING,
            PortalFluid.PortalFluidAttributes.builder(
                new ResourceLocation(BPSettings.MOD_ID, "block/portal_fluid_still"),
                new ResourceLocation(BPSettings.MOD_ID, "block/portal_fluid_flowing")
            )
                .overlay(new ResourceLocation(BPSettings.MOD_ID, "block/portal_fluid_overlay"))
                .viscosity(6000)
                .translationKey("block.betterportals.portal_fluid")
                .color(0xEE190040)
                .luminosity(7)
                .rarity(Rarity.RARE)
        )
            .bucket(() -> ItemModule.PORTAL_BUCKET)
            .block(() -> BlockModule.PORTAL_FLUID_BLOCK);

        PORTAL_FLUID = new PortalFluid.Source(PORTAL_FLUID_PROPERTIES);
        PORTAL_FLUID_FLOWING = new PortalFluid.Flowing(PORTAL_FLUID_PROPERTIES);
    }

    /**
     * Registers portal fluids.
     */
    private static void registerFluids(final RegistryEvent.Register<Fluid> event) {
        event.getRegistry().register(PORTAL_FLUID.setRegistryName(new ResourceLocation(BPSettings.MOD_ID, "portal_fluid_still")));
        event.getRegistry().register(PORTAL_FLUID_FLOWING.setRegistryName(new ResourceLocation(BPSettings.MOD_ID, "portal_fluid_flowing")));
    }
}
