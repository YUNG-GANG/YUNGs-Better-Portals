package com.yungnickyoung.minecraft.betterportals.init;

import com.yungnickyoung.minecraft.betterportals.BetterPortals;
import com.yungnickyoung.minecraft.betterportals.config.BPSettings;
import com.yungnickyoung.minecraft.betterportals.world.variant.MonolithVariants;
import com.yungnickyoung.minecraft.betterportals.world.variant.PortalLakeVariants;
import com.yungnickyoung.minecraft.yungsapi.io.JSON;
import net.minecraftforge.fml.loading.FMLPaths;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class BPConfig {
    public static void initConfigFiles() {
        createDirectory();
        createJsonReadMe();
        loadRiftVariantSettings();
        loadMonolithVariantSettings();
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
                "README\n" +
                    "NOTE -- EDITING THE rifts.json and monoliths.json FILES REQUIRES A MINECRAFT RESTART TO UPDATE!" +
                    "\n" +
                    "The JSON files contains the 'variants' property, which is a list of all the dimensional rifts to spawn.\n" +
                    "Each dimensional rift has the following settings:\n" +
                    " - blockSelector: Describes a set of blocks and the probability of each block being chosen. See an example below.\n" +
                    "   - entries: An object where each entry's key is a block, and each value is that block's probability of being chosen.\n" +
                    "        The total sum of all probabilities SHOULD NOT exceed 1.0!\n" +
                    "   - defaultBlock: The block used for any leftover probability ranges.\n" +
                    "        For example, if the total sum of all the probabilities of the entries is 0.6, then\n" +
                    "        there is a 0.4 chance of the defaultBlock being selected.\n" +
                    " - fluidColor: The color of the portal fluid. In hexadecimal, this value looks like 0xAARRGGBB\n" +
                    "      where A = alpha, R = red, G = green, B = blue\n" +
                    " - spawnDimension: The dimension this rift will spawn in\n" +
                    " - targetDimension: The dimension this rift will teleport you to\n" +
                    " - minY: The minimum y-coordinate the surface of the Rift can spawn at\n" +
                    " - maxY: The maximum y-coordinate the surface of the Rift can spawn at\n" +
                    " - spawnChance: The chance of a Rift spawning in a chunk\n" +
                    "\n" +
                    "Here's an example block selector:\n" +
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
