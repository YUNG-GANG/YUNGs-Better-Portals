package com.yungnickyoung.minecraft.betterportals;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.FlowingFluidBlock;
import net.minecraft.block.material.Material;
import net.minecraft.fluid.FlowingFluid;
import net.minecraft.item.BucketItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.Items;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod("betterportals")
public class BetterPortals {
    public static final String MOD_ID = "betterportals";
    private static final Logger LOGGER = LogManager.getLogger(MOD_ID);

    public BetterPortals() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::doClientStuff);

        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);
    }

    public static final FlowingFluid FLOWING_PORTAL = new PortalFluid.Flowing();
    public static final FlowingFluid SOURCE_PORTAL = new PortalFluid.Source();
    public static final Block PORTAL_LIQUID_BLOCK = new FlowingFluidBlock(() -> SOURCE_PORTAL, AbstractBlock.Properties.create(Material.WATER).doesNotBlockMovement().hardnessAndResistance(100.0F).noDrops());
    public static final Item PORTAL_BUCKET = new BucketItem(() -> SOURCE_PORTAL, (new Item.Properties()).containerItem(Items.BUCKET).maxStackSize(1).group(ItemGroup.MISC));

    private void setup(final FMLCommonSetupEvent event)
    {
        Registry.register(Registry.FLUID, new ResourceLocation(MOD_ID, "portal_liquid_flowing"), FLOWING_PORTAL);
        Registry.register(Registry.FLUID, new ResourceLocation(MOD_ID, "portal_liquid"), SOURCE_PORTAL);
        Registry.register(Registry.BLOCK, new ResourceLocation(MOD_ID, "portal_liquid"), PORTAL_LIQUID_BLOCK);
        Registry.register(Registry.ITEM, new ResourceLocation(MOD_ID, "portal_bucket"), PORTAL_BUCKET);
    }

    private void doClientStuff(final FMLClientSetupEvent event) {
        // do something that can only be done on the client
        LOGGER.info("Got game settings {}", event.getMinecraftSupplier().get().gameSettings);
    }

    // You can use SubscribeEvent and let the Event Bus discover methods to call
    @SubscribeEvent
    public void onServerStarting(FMLServerStartingEvent event) {
        // do something when the server starts
        LOGGER.info("HELLO from server starting");
    }

    // You can use EventBusSubscriber to automatically subscribe events on the contained class (this is subscribing to the MOD
    // Event bus for receiving Registry Events)
    @Mod.EventBusSubscriber(bus=Mod.EventBusSubscriber.Bus.MOD)
    public static class RegistryEvents {
        @SubscribeEvent
        public static void onBlocksRegistry(final RegistryEvent.Register<Block> blockRegistryEvent) {
            // register a new block here
            LOGGER.info("HELLO from Register Block");
        }
    }
}
