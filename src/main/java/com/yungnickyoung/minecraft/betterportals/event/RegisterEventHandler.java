package com.yungnickyoung.minecraft.betterportals.event;

import com.yungnickyoung.minecraft.betterportals.fluid.PortalFluid;
import com.yungnickyoung.minecraft.betterportals.block.PortalFluidBlock;
import com.yungnickyoung.minecraft.betterportals.config.BPSettings;
import com.yungnickyoung.minecraft.betterportals.init.BPBlocks;
import com.yungnickyoung.minecraft.betterportals.init.BPFeatures;
import com.yungnickyoung.minecraft.betterportals.init.BPItems;
import net.minecraft.block.Block;
import net.minecraft.item.BucketItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.Items;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.placement.Placement;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.ForgeFlowingFluid;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = BPSettings.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class RegisterEventHandler {
    @SubscribeEvent
    public static void registerFeatureHandler(RegistryEvent.Register<Feature<?>> event) {
        registerFeature(BPFeatures.PORTAL_LAKE, "portal_lake");
        registerFeature(BPFeatures.MONOLITH, "monolith");
    }

    @SubscribeEvent
    public static void registerDecoratorHandler(RegistryEvent.Register<Placement<?>> event) {
        registerDecorator(BPFeatures.PORTAL_LAKE_PLACEMENT, "portal_lake");
    }

    @SubscribeEvent
    public void registerBlocks(final RegistryEvent.Register<Block> event) {
        BPBlocks.PORTAL_FLUID_PROPERTIES = new ForgeFlowingFluid.Properties(() -> BPBlocks.PORTAL_FLUID, () -> BPBlocks.PORTAL_FLUID_FLOWING,
            FluidAttributes.builder(new ResourceLocation(BPSettings.MOD_ID, "block/portal_fluid_still"), new ResourceLocation(BPSettings.MOD_ID, "block/portal_fluid_flowing")).overlay(new ResourceLocation(BPSettings.MOD_ID, "block/portal_fluid_overlay")).viscosity(6000).translationKey("block.betterportals.portal_fluid").color(0xEE190040))
            .bucket(() -> BPItems.PORTAL_BUCKET).block(() -> BPBlocks.PORTAL_FLUID_BLOCK);

        BPBlocks.PORTAL_FLUID = Registry.register(Registry.FLUID, new ResourceLocation(BPSettings.MOD_ID, "portal_fluid_still"), new PortalFluid.Source(BPBlocks.PORTAL_FLUID_PROPERTIES));
        BPBlocks.PORTAL_FLUID_FLOWING = Registry.register(Registry.FLUID, new ResourceLocation(BPSettings.MOD_ID, "portal_fluid_flowing"), new PortalFluid.Flowing(BPBlocks.PORTAL_FLUID_PROPERTIES));
        BPBlocks.PORTAL_FLUID_BLOCK = Registry.register(
            Registry.BLOCK,
            new ResourceLocation(BPSettings.MOD_ID, "portal_fluid_block"),
            new PortalFluidBlock()
        );
    }

    public void registerItems(final RegistryEvent.Register<Item> event) {
        BPItems.PORTAL_BUCKET = Registry.register(Registry.ITEM, new ResourceLocation(BPSettings.MOD_ID, "portal_fluid_bucket"),
            new BucketItem(() -> BPBlocks.PORTAL_FLUID, new Item.Properties().containerItem(Items.BUCKET).maxStackSize(1).group(ItemGroup.MISC)));
    }

    private static void registerFeature(Feature<?> feature, String name) {
        Registry.register(Registry.FEATURE, new ResourceLocation(BPSettings.MOD_ID, name), feature);
    }

    private static void registerDecorator(Placement<?> placement, String name) {
        Registry.register(Registry.DECORATOR, new ResourceLocation(BPSettings.MOD_ID, name), placement);
    }
}
