package com.github.awruff.brandswitcher;

import net.minecraft.client.Minecraft;
import net.minecraftforge.common.config.Configuration;

import java.io.File;
import java.util.Objects;

public final class BrandSwitcherConfig {
    public static boolean enabled = true;
    public static String brand = Preset.VANILLA.brand;
    public static boolean antiForge = true;

    private static String sentBrand;
    private static boolean sentAntiForge;

    private static Configuration config;

    static void load(File file) {
        config = new Configuration(file);
        enabled = config.get(Configuration.CATEGORY_GENERAL, "enabled", enabled).getBoolean();
        brand = config.get(Configuration.CATEGORY_GENERAL, "brand", brand).getString();
        antiForge = config.get(Configuration.CATEGORY_GENERAL, "antiForge", antiForge, "Hide Forge's handshake and mod channels from servers").getBoolean();
        if (config.hasChanged()) config.save();
    }

    static void save() {
        config.get(Configuration.CATEGORY_GENERAL, "enabled", enabled).set(enabled);
        config.get(Configuration.CATEGORY_GENERAL, "brand", brand).set(brand);
        config.get(Configuration.CATEGORY_GENERAL, "antiForge", antiForge).set(antiForge);
        config.save();
    }

    public static String sendBrand() {
        sentBrand = enabled ? brand : null;
        return sentBrand;
    }

    public static boolean antiForgeForNewConnection() {
        Minecraft mc = Minecraft.getMinecraft();
        sentAntiForge = enabled && antiForge && !mc.isSingleplayer() && !mc.isIntegratedServerRunning();
        return sentAntiForge;
    }

    static boolean needsRejoin() {
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.theWorld == null || mc.isSingleplayer()) return false;
        return !Objects.equals(enabled ? brand : null, sentBrand) || (enabled && antiForge) != sentAntiForge;
    }

    public enum Preset {
        VANILLA("Vanilla", "vanilla"),
        FABRIC("Fabric", "fabric"),
        FORGE("Forge", "fml,forge"),
        LUNAR("Lunar Client", "lunarclient:v2.22.44-2640"),
        CUSTOM("Custom", null);

        public final String label;
        public final String brand;

        Preset(String label, String brand) {
            this.label = label;
            this.brand = brand;
        }

        public static Preset of(String brand) {
            for (Preset preset : values()) {
                if (brand.equals(preset.brand)) return preset;
            }
            return CUSTOM;
        }
    }
}
