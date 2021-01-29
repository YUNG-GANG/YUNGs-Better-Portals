package com.yungnickyoung.minecraft.betterportals.util;

import com.yungnickyoung.minecraft.betterportals.BetterPortals;

/**
 * A simple representation of a RGBA color.
 * Each component should be stored as a hex string, e.g. red = "FF".
 * This class is not very flexible, and is not intended for general use;
 * rather, it allows for easy JSON serialization without manually creating a GSON Type Adapter.
 */
public class RGBAColor {
    private String red;
    private String green;
    private String blue;
    private String alpha;

    public RGBAColor(String r, String g, String b, String a) {
        this.red = r;
        this.green = g;
        this.blue = b;
        this.alpha = a;
    }

    public String getRed() {
        return red;
    }

    public void setRed(String red) {
        this.red = red;
    }

    public String getGreen() {
        return green;
    }

    public void setGreen(String green) {
        this.green = green;
    }

    public String getBlue() {
        return blue;
    }

    public void setBlue(String blue) {
        this.blue = blue;
    }

    public String getAlpha() {
        return alpha;
    }

    public void setAlpha(String alpha) {
        this.alpha = alpha;
    }

    /**
     * Returns a vanilla-compatible integer representation of this color.
     * Includes the alpha channel.
     */
    public int getColorValue() {
        String stringColor =  (alpha + red + green + blue).toLowerCase();
        try {
            return (int) Long.parseLong(stringColor, 16); // Long is necessary here since int overflows after 0x7FFFFFFF
        } catch (Exception e) {
            BetterPortals.LOGGER.error("Unable to parse color string {} as base 16 int. Using default color...", stringColor);
            return 0xFFFFFFFF;
        }
    }

    /**
     * Returns a vanilla-compatible float[] representation of this color.
     * Does not include the alpha channel.
     */
    public float[] getColorComponentValues() {
        int colorValue = this.getColorValue();
        int r = (colorValue & 16711680) >> 16;
        int g = (colorValue & '\uff00') >> 8;
        int b = colorValue & 255;
        return new float[] { (float)r / 255f, (float)g / 255f, (float)b / 255f };
    }
}
