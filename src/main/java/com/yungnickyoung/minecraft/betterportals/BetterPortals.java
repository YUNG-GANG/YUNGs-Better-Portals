package com.yungnickyoung.minecraft.betterportals;

import com.yungnickyoung.minecraft.betterportals.config.BPSettings;
import com.yungnickyoung.minecraft.betterportals.init.BPConfig;
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
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(BPSettings.MOD_ID)
public class BetterPortals {
    public static final Logger LOGGER = LogManager.getLogger(BPSettings.MOD_ID);

    public BetterPortals() {
//        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
        BPConfig.initConfigFiles();
    }

    public static final FlowingFluid FLOWING_PORTAL = new PortalFluid.Flowing();
    public static final FlowingFluid SOURCE_PORTAL = new PortalFluid.Source();
    public static final Block PORTAL_LIQUID_BLOCK = new FlowingFluidBlock(() -> SOURCE_PORTAL, AbstractBlock.Properties.create(Material.WATER).doesNotBlockMovement().hardnessAndResistance(100.0F).noDrops());
    public static final Item PORTAL_BUCKET = new BucketItem(() -> SOURCE_PORTAL, (new Item.Properties()).containerItem(Items.BUCKET).maxStackSize(1).group(ItemGroup.MISC));

    private void setup(final FMLCommonSetupEvent event)
    {
        Registry.register(Registry.FLUID, new ResourceLocation(BPSettings.MOD_ID, "portal_liquid_flowing"), FLOWING_PORTAL);
        Registry.register(Registry.FLUID, new ResourceLocation(BPSettings.MOD_ID, "portal_liquid"), SOURCE_PORTAL);
        Registry.register(Registry.BLOCK, new ResourceLocation(BPSettings.MOD_ID, "portal_liquid"), PORTAL_LIQUID_BLOCK);
        Registry.register(Registry.ITEM, new ResourceLocation(BPSettings.MOD_ID, "portal_bucket"), PORTAL_BUCKET);
    }
}
