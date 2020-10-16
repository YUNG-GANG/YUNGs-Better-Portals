package com.yungnickyoung.minecraft.betterportals;

import com.yungnickyoung.minecraft.betterportals.block.PortalFluidBlock;
import com.yungnickyoung.minecraft.betterportals.client.BetterPortalsClient;
import com.yungnickyoung.minecraft.betterportals.config.BPSettings;
import com.yungnickyoung.minecraft.betterportals.init.BPConfig;
import net.minecraft.block.Block;
import net.minecraft.block.FlowingFluidBlock;
import net.minecraft.fluid.FlowingFluid;
import net.minecraft.item.BucketItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.Items;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.ForgeFlowingFluid;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(BPSettings.MOD_ID)
public class BetterPortals {
    public static final Logger LOGGER = LogManager.getLogger(BPSettings.MOD_ID);

    public BetterPortals() {
        BPConfig.initConfigFiles();
        FMLJavaModLoadingContext.get().getModEventBus().addGenericListener(Block.class, this::registerBlocks);
        FMLJavaModLoadingContext.get().getModEventBus().addGenericListener(Item.class, this::registerItems);
        DistExecutor.safeRunWhenOn(Dist.CLIENT, () -> BetterPortalsClient::subscribeClientEvents);
    }

    public static ForgeFlowingFluid.Properties PORTAL_FLUID_PROPERTIES;

    public static FlowingFluid PORTAL_FLUID;
    public static FlowingFluid PORTAL_FLUID_FLOWING;
    public static FlowingFluidBlock PORTAL_FLUID_BLOCK;

    public static final ResourceLocation FLUID_STILL = new ResourceLocation(BPSettings.MOD_ID, "block/portal_fluid_still");
    public static final ResourceLocation FLUID_FLOWING = new ResourceLocation(BPSettings.MOD_ID, "block/portal_fluid_flowing");
    public static final ResourceLocation FLUID_OVERLAY = new ResourceLocation(BPSettings.MOD_ID, "block/portal_fluid_overlay");

    public void registerBlocks(final RegistryEvent.Register<Block> event) {
        PORTAL_FLUID_PROPERTIES = new ForgeFlowingFluid.Properties(() -> PORTAL_FLUID, () -> PORTAL_FLUID_FLOWING,
            FluidAttributes.builder(FLUID_STILL, FLUID_FLOWING).overlay(FLUID_OVERLAY).viscosity(6000).translationKey("block.betterportals.portal_fluid").color(0xEE190040))
            .bucket(() -> PORTAL_BUCKET).block(() -> PORTAL_FLUID_BLOCK);

        PORTAL_FLUID = Registry.register(Registry.FLUID, new ResourceLocation(BPSettings.MOD_ID, "portal_fluid_still"), new PortalFluid.Source(PORTAL_FLUID_PROPERTIES));
        PORTAL_FLUID_FLOWING = Registry.register(Registry.FLUID, new ResourceLocation(BPSettings.MOD_ID, "portal_fluid_flowing"), new PortalFluid.Flowing(PORTAL_FLUID_PROPERTIES));
        PORTAL_FLUID_BLOCK = Registry.register(
            Registry.BLOCK,
            new ResourceLocation(BPSettings.MOD_ID, "portal_fluid_block"),
            new PortalFluidBlock()
        );
    }

    public static Item PORTAL_BUCKET;

    public void registerItems(final RegistryEvent.Register<Item> event) {
        PORTAL_BUCKET = Registry.register(Registry.ITEM, new ResourceLocation(BPSettings.MOD_ID, "portal_fluid_bucket"),
            new BucketItem(() -> PORTAL_FLUID, new Item.Properties().containerItem(Items.BUCKET).maxStackSize(1).group(ItemGroup.MISC)));
    }
}
