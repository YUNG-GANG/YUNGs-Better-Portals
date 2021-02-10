package com.yungnickyoung.minecraft.betterportals.config;

import com.yungnickyoung.minecraft.betterportals.BetterPortals;
import com.yungnickyoung.minecraft.betterportals.module.IModule;
import com.yungnickyoung.minecraft.betterportals.world.variant.MonolithVariants;
import com.yungnickyoung.minecraft.betterportals.world.variant.PortalLakeVariants;
import com.yungnickyoung.minecraft.yungsapi.io.JSON;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.loading.FMLPaths;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ConfigModule implements IModule  {
    @Override
    public void init() {
        createDirectory();
        createJsonReadMe();
        loadRiftVariantSettings();
        loadMonolithVariantSettings();
        // Register config with Forge
        ModLoadingContext.get().registerConfig(net.minecraftforge.fml.config.ModConfig.Type.COMMON, Configuration.SPEC, BPSettings.BASE_CONFIG_NAME);
    }

    private static void createDirectory() {
        File parentDir = new File(FMLPaths.CONFIGDIR.get().toString(), BPSettings.CUSTOM_CONFIG_PATH);
        File customConfigDir = new File(parentDir, BPSettings.VERSION_PATH);
        try {
            String filePath = customConfigDir.getCanonicalPath();
            if (customConfigDir.mkdirs()) {
                BetterPortals.LOGGER.info("Creating directory for Better Portals configs at {}", filePath);
            }
        } catch (IOException e) {
            BetterPortals.LOGGER.error("ERROR creating Better Portals config directory: {}", e.toString());
        }
    }

    private static void createJsonReadMe() {
        Path path = Paths.get(FMLPaths.CONFIGDIR.get().toString(), BPSettings.CUSTOM_CONFIG_PATH, BPSettings.VERSION_PATH, "README.txt");
        File readme = new File(path.toString());
        if (!readme.exists()) {
            String readmeText =
                "######################################################\n" +
                "# README for the rifts.json and monoliths.json files #\n" +
                "######################################################\n" +
                "NOTE -- EDITING THE rifts.json and monoliths.json FILES REQUIRES A MINECRAFT RESTART TO UPDATE!" +
                "\n" +
                "Each of these JSON files contains the 'variants' property, which is a list of all the rifts and monoliths to spawn, respectively.\n" +
                "\n" +
                "Each dimensional rift in rifts.json is an object with the following properties:\n" +
                " - blockSelector: A BlockSetSelector describing the blocks this rift is made of.\n" +
                "       See the bottom of this README for documentation on BlockSetSelectors.\n" +
                " - fluidColor: The color of ANY DIMENSIONAL PLASMA IN THIS DIMENSION. Keep in mind this hue is added to the base purple hue\n" +
                "   - red: Any hex string from 00 to FF.\n" +
                "   - green: Any hex string from 00 to FF.\n" +
                "   - blue: Any hex string from 00 to FF.\n" +
                "   - alpha: Any hex string from 00 to FF.\n" +
                " - minY: The minimum y-coordinate a rift can spawn at.\n" +
                " - maxY: The maximum y-coordinate a rift can spawn at.\n" +
                " - playerTeleportedMinY: The minimum y-coordinate a player can be teleported to in the target dimension.\n" +
                " - playerTeleportedMaxY: The maximum y-coordinate a player can be teleported to in the target dimension.\n" +
                " - spawnDimension: The dimension this rift will spawn in. This MUST be distinct for each variant! In other words - only ONE variant per dimension!\n" +
                " - targetDimension: The dimension this rift will teleport you to. This does not have to be distinct for each variant.\n" +
                " - spawnChance: The chance of a Rift spawning.\n" +
                " - spawnPlatformBlock: Sometimes, when teleporting the player, a small 3x3 platform is generated for the player to stand on.\n" +
                "       This is the block the platform will be made of. Should be a block that matches the target dimension well.\n" +
                "\n" +
                "Each monolith in monoliths.json is an object with the following properties:\n" +
                " - stairSelector: A BlockSetSelector describing the stairs on the perimeter of this monolith.\n" +
                " - cornerSelector: A BlockSetSelector describing the blocks on the corners of this monolith.\n" +
                " - insideSelector: A BlockSetSelector describing the blocks making up the inside of this monolith.\n" +
                " - fenceSelector: A BlockSetSelector describing the fences used in this monolith.\n" +
                " - powerBlock: The block required to power this monolith.\n" +
                " - decorationBlock: The block in the four corners of the power grid at the center of this monolith.\n" +
                " - beamStopBlocks: A list of blocks through which the beams of ANY RECLAIMERS PLACED IN THIS DIMENSION cannot pass.\n" +
                " - unpoweredBeamColor: The color of the beam of ANY RECLAIMER IN THIS DIMENSION, when UNPOWERED.\n" +
                "   - red: Any hex string from 00 to FF.\n" +
                "   - green: Any hex string from 00 to FF.\n" +
                "   - blue: Any hex string from 00 to FF.\n" +
                "   - alpha: Any hex string from 00 to FF.\n" +
                " - poweredBeamColor: The color of the beam of ANY RECLAIMER IN THIS DIMENSION, when POWERED.\n" +
                "   - red: Any hex string from 00 to FF.\n" +
                "   - green: Any hex string from 00 to FF.\n" +
                "   - blue: Any hex string from 00 to FF.\n" +
                "   - alpha: Any hex string from 00 to FF.\n" +
                " - minY: The minimum y-coordinate this monolith can spawn at.\n" +
                " - maxY: The maximum y-coordinate this monolith can spawn at.\n" +
                " - playerTeleportedMinY: The minimum y-coordinate a player can be teleported to in the target dimension.\n" +
                " - playerTeleportedMaxY: The maximum y-coordinate a player can be teleported to in the target dimension.\n" +
                " - spawnDimension: The dimension this monolith will spawn in. This MUST be distinct for each variant! In other words - only ONE variant per dimension!\n" +
                " - targetDimension: The dimension this monolith will teleport you to. This does not have to be distinct for each variant.\n" +
                " - spawnChance: The chance of a Monolith spawning.\n" +
                "\n" +
                "BlockSetSelector information:\n" +
                "A BlockSetSelector describes a set of blocks and the probability of each block being chosen.\n" +
                "Each BlockSetSelector has the following two fields:\n" +
                "   - entries: An object where each entry's key is a block, and each value is that block's probability of being chosen.\n" +
                "        The total sum of all probabilities SHOULD NOT exceed 1.0!\n" +
                "   - defaultBlock: The block used for any leftover probability ranges.\n" +
                "        For example, if the total sum of all the probabilities of the entries is 0.6, then\n" +
                "        there is a 0.4 chance of the defaultBlock being selected.\n" +
                "\n" +
                "Here's an example BlockSetSelector:\n" +
                "\"entries\": {\n" +
                "  \"minecraft:cobblestone\": 0.25,\n" +
                "  \"minecraft:air\": 0.2,\n" +
                "  \"minecraft:stonebrick[variant=stonebrick]\": 0.1\n" +
                "},\n" +
                "\"defaultBlock\": \"minecraft:planks[variant=oak]\"\n" +
                "\n" +
                "For each block, this selector has a 25% chance of returning cobblestone, 20% chance of choosing air,\n" +
                "10% chance of choosing stone bricks, and a 100 - (25 + 20 + 10) = 45% chance of choosing oak planks (since it's the default block).\n";

            try {
                Files.write(path, readmeText.getBytes());
            } catch (IOException e) {
                BetterPortals.LOGGER.error("Unable to create README file!");
            }
        }
    }

    public static void loadRiftVariantSettings() {
        String fileName = "rifts.json";
        Path jsonPath = Paths.get(FMLPaths.CONFIGDIR.get().toString(), BPSettings.CUSTOM_CONFIG_PATH, BPSettings.VERSION_PATH, fileName);
        File jsonFile = new File(jsonPath.toString());

        if (!jsonFile.exists()) {
            // Create default file if JSON file doesn't already exist
            try {
                JSON.createJsonFileFromObject(jsonPath, PortalLakeVariants.get());
            } catch (IOException e) {
                BetterPortals.LOGGER.error("Error creating Better Portals {} file! - {}", fileName, e.toString());
            }
        } else {
            // If file already exists, load data into PortalLakeVariants
            if (!jsonFile.canRead()) {
                BetterPortals.LOGGER.error("Better Portals {} file not readable! Using default configuration...", fileName);
                return;
            }

            try {
                PortalLakeVariants.instance = JSON.loadObjectFromJsonFile(jsonPath, PortalLakeVariants.class);
            } catch (IOException e) {
                BetterPortals.LOGGER.error("Error loading Better Portals {} file: {}", fileName, e.toString());
                BetterPortals.LOGGER.error("Using default configuration...");
            }
        }
    }

    public static void loadMonolithVariantSettings() {
        String fileName = "monoliths.json";
        Path jsonPath = Paths.get(FMLPaths.CONFIGDIR.get().toString(), BPSettings.CUSTOM_CONFIG_PATH, BPSettings.VERSION_PATH, fileName);
        File jsonFile = new File(jsonPath.toString());

        if (!jsonFile.exists()) {
            // Create default file if JSON file doesn't already exist
            try {
                JSON.createJsonFileFromObject(jsonPath, MonolithVariants.get());
            } catch (IOException e) {
                BetterPortals.LOGGER.error("Error creating Better Portals {} file! - {}", fileName, e.toString());
            }
        } else {
            // If file already exists, load data into PortalLakeVariants
            if (!jsonFile.canRead()) {
                BetterPortals.LOGGER.error("Better Portals {} file not readable! Using default configuration...", fileName);
                return;
            }

            try {
                MonolithVariants.instance = JSON.loadObjectFromJsonFile(jsonPath, MonolithVariants.class);
            } catch (IOException e) {
                BetterPortals.LOGGER.error("Error loading Better Portals {} file: {}", fileName, e.toString());
                BetterPortals.LOGGER.error("Using default configuration...");
            }
        }
    }
}
