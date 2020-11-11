package com.yungnickyoung.minecraft.betterportals.fluid;

import com.yungnickyoung.minecraft.betterportals.block.BlockModule;
import com.yungnickyoung.minecraft.betterportals.config.BPSettings;
import com.yungnickyoung.minecraft.betterportals.item.ItemModule;
import com.yungnickyoung.minecraft.betterportals.module.IModule;
import net.minecraft.fluid.FlowingFluid;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.Rarity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fluids.ForgeFlowingFluid;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

public class FluidModule implements IModule {
    public static FlowingFluid PORTAL_FLUID;
    public static FlowingFluid PORTAL_FLUID_FLOWING;
    public static ForgeFlowingFluid.Properties PORTAL_FLUID_PROPERTIES;

    @Override
    public void init() {
        FMLJavaModLoadingContext.get().getModEventBus().addGenericListener(Fluid.class, FluidModule::registerFluids);
    }

    /**
     * Registers portal fluid.
     */
    private static void registerFluids(final RegistryEvent.Register<Fluid> event) {
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

        PORTAL_FLUID = Registry.register(
            Registry.FLUID,
            new ResourceLocation(BPSettings.MOD_ID, "portal_fluid_still"),
            new PortalFluid.Source(PORTAL_FLUID_PROPERTIES)
        );
        PORTAL_FLUID_FLOWING = Registry.register(
            Registry.FLUID,
            new ResourceLocation(BPSettings.MOD_ID, "portal_fluid_flowing"),
            new PortalFluid.Flowing(PORTAL_FLUID_PROPERTIES)
        );
    }
}
