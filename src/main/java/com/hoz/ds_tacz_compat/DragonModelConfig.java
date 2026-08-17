package com.hoz.ds_tacz_compat;

import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateProvider;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.neoforged.fml.loading.FMLPaths;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

/**
 * Per-dragon-model overrides for the floating gun and back gun. Keyed by the body model id
 * (e.g. {@code dragonsurvival:dragon_model}); models without an entry fall back to the global
 * client defaults. Stored in config/ds_tacz_compat/dragon_models.json.
 */
public final class DragonModelConfig {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path FILE = FMLPaths.CONFIGDIR.get().resolve("ds_tacz_compat/dragon_models.json");
    private static Map<String, ModelConfig> CONFIGS = new HashMap<>();

    public static class FloatingGunConfig {
        public boolean enabled = true;
        public float offsetX = 0.0f;
        public float offsetY = 1.0f;
        public float offsetZ = 0.0f;
        public float scale = 0.8f;
    }

    public static class BackGunConfig {
        public boolean enabled = true;
        public float posX = 0.0f;
        public float posY = 0.36f;
        public float posZ = 0.33f;
        public float rotX = 90.0f;
        public float rotY = 120.0f;
        public float rotZ = 0.0f;
        public float scale = 0.5f;
    }

    public static class ModelConfig {
        public FloatingGunConfig floatingGun = new FloatingGunConfig();
        public BackGunConfig backGun = new BackGunConfig();
    }

    private DragonModelConfig() {}

    public static void load() {
        try {
            if (!Files.exists(FILE)) {
                copyDefault();
            }
            if (Files.exists(FILE)) {
                Map<String, ModelConfig> loaded = GSON.fromJson(Files.readString(FILE),
                        new TypeToken<Map<String, ModelConfig>>() {}.getType());
                if (loaded != null) {
                    CONFIGS = loaded;
                }
            }
        } catch (IOException e) {
            TaczArmHide.LOGGER.error("Failed to load dragon model config", e);
        }
    }

    private static void copyDefault() {
        try (InputStream in = DragonModelConfig.class.getResourceAsStream("/default_dragon_models.json")) {
            if (in == null) {
                return;
            }
            Files.createDirectories(FILE.getParent());
            Files.copy(in, FILE);
        } catch (IOException e) {
            TaczArmHide.LOGGER.error("Failed to copy default dragon model config", e);
        }
    }

    public static void save() {
        try {
            Files.createDirectories(FILE.getParent());
            Files.writeString(FILE, GSON.toJson(CONFIGS));
        } catch (IOException e) {
            TaczArmHide.LOGGER.error("Failed to save dragon model config", e);
        }
    }

    public static ResourceLocation modelOf(Player player) {
        return DragonStateProvider.getData(player).body().value().model();
    }

    public static FloatingGunConfig floatingGunFor(Player player) {
        return CONFIGS.getOrDefault(modelOf(player).toString(), new ModelConfig()).floatingGun;
    }

    public static BackGunConfig backGunFor(Player player) {
        return CONFIGS.getOrDefault(modelOf(player).toString(), new ModelConfig()).backGun;
    }

    public static ModelConfig getOrCreate(String model) {
        return CONFIGS.computeIfAbsent(model, k -> new ModelConfig());
    }
}
