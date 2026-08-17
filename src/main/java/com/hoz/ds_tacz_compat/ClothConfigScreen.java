package com.hoz.ds_tacz_compat;

import by.dragonsurvivalteam.dragonsurvival.registry.dragon.body.DragonBody;
import me.shedaniel.clothconfig2.api.AbstractConfigListEntry;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import me.shedaniel.clothconfig2.impl.builders.SubCategoryBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.function.Consumer;

public final class ClothConfigScreen {
    private ClothConfigScreen() {}

    public static Screen create(Screen parent) {
        ConfigBuilder root = ConfigBuilder.create()
                .setTitle(Component.translatable("ds_tacz_compat.configuration.title"))
                .setParentScreen(parent);
        root.setSavingRunnable(() -> {
            if (Config.SPEC.isLoaded()) Config.SPEC.save();
            if (ServerConfig.SPEC.isLoaded()) ServerConfig.SPEC.save();
            DragonModelConfig.save();
        });
        ConfigEntryBuilder eb = root.entryBuilder();

        addFloatingGun(root, eb);
        addFirstPerson(root, eb);
        addTracer(root, eb);
        addCrosshair(root, eb);
        addRecoil(root, eb);
        addBackGun(root, eb);
        addDragonModels(root, eb);
        addModelTool(root, eb, parent);
        // The server config is only loaded once a world is joined; reading it before then throws.
        if (ServerConfig.SPEC.isLoaded()) {
            addServer(root, eb);
        }

        return root.build();
    }

    private static void addFloatingGun(ConfigBuilder root, ConfigEntryBuilder eb) {
        ConfigCategory cat = root.getOrCreateCategory(Component.translatable("ds_tacz_compat.config.category.floatingGun"));
        cat.addEntry(eb.startBooleanToggle(Component.translatable("ds_tacz_compat.configuration.hideThirdPersonGuns"), Config.HIDE_THIRD_PERSON_GUNS.get())
                .setDefaultValue(false)
                .setTooltip(Component.translatable("ds_tacz_compat.configuration.hideThirdPersonGuns.tooltip"))
                .setSaveConsumer(Config.HIDE_THIRD_PERSON_GUNS::set).build());
        cat.addEntry(eb.startDoubleField(Component.translatable("ds_tacz_compat.configuration.gunHeightOffset"), Config.GUN_HEIGHT_OFFSET.get())
                .setMin(-2).setMax(5).setDefaultValue(1.0)
                .setTooltip(Component.translatable("ds_tacz_compat.configuration.gunHeightOffset.tooltip"))
                .setSaveConsumer(Config.GUN_HEIGHT_OFFSET::set).build());
        cat.addEntry(eb.startDoubleField(Component.translatable("ds_tacz_compat.configuration.gunFloatSpeed"), Config.GUN_FLOAT_SPEED.get())
                .setMin(0).setMax(5).setDefaultValue(0.3)
                .setTooltip(Component.translatable("ds_tacz_compat.configuration.gunFloatSpeed.tooltip"))
                .setSaveConsumer(Config.GUN_FLOAT_SPEED::set).build());
        cat.addEntry(eb.startDoubleField(Component.translatable("ds_tacz_compat.configuration.gunBaseScale"), Config.GUN_BASE_SCALE.get())
                .setMin(0.1).setMax(5).setDefaultValue(0.8)
                .setTooltip(Component.translatable("ds_tacz_compat.configuration.gunBaseScale.tooltip"))
                .setSaveConsumer(Config.GUN_BASE_SCALE::set).build());
        cat.addEntry(eb.startBooleanToggle(Component.translatable("ds_tacz_compat.configuration.gunScaleWithDragon"), Config.GUN_SCALE_WITH_DRAGON.get())
                .setDefaultValue(true)
                .setTooltip(Component.translatable("ds_tacz_compat.configuration.gunScaleWithDragon.tooltip"))
                .setSaveConsumer(Config.GUN_SCALE_WITH_DRAGON::set).build());
        cat.addEntry(eb.startBooleanToggle(Component.translatable("ds_tacz_compat.configuration.gunOffsetScaleWithDragon"), Config.GUN_OFFSET_SCALE_WITH_DRAGON.get())
                .setDefaultValue(true)
                .setTooltip(Component.translatable("ds_tacz_compat.configuration.gunOffsetScaleWithDragon.tooltip"))
                .setSaveConsumer(Config.GUN_OFFSET_SCALE_WITH_DRAGON::set).build());
        cat.addEntry(eb.startDoubleField(Component.translatable("ds_tacz_compat.configuration.gunOffsetX"), Config.GUN_OFFSET_X.get())
                .setMin(-5).setMax(5).setDefaultValue(0.0)
                .setTooltip(Component.translatable("ds_tacz_compat.configuration.gunOffsetX.tooltip"))
                .setSaveConsumer(Config.GUN_OFFSET_X::set).build());
        cat.addEntry(eb.startDoubleField(Component.translatable("ds_tacz_compat.configuration.gunOffsetZ"), Config.GUN_OFFSET_Z.get())
                .setMin(-5).setMax(5).setDefaultValue(0.0)
                .setTooltip(Component.translatable("ds_tacz_compat.configuration.gunOffsetZ.tooltip"))
                .setSaveConsumer(Config.GUN_OFFSET_Z::set).build());
        cat.addEntry(eb.startDoubleField(Component.translatable("ds_tacz_compat.configuration.aimSmoothing"), Config.AIM_SMOOTHING.get())
                .setMin(0).setMax(2).setDefaultValue(0.15)
                .setTooltip(Component.translatable("ds_tacz_compat.configuration.aimSmoothing.tooltip"))
                .setSaveConsumer(Config.AIM_SMOOTHING::set).build());
        cat.addEntry(eb.startDoubleField(Component.translatable("ds_tacz_compat.configuration.gunPitchClamp"), Config.GUN_PITCH_CLAMP.get())
                .setMin(0).setMax(90).setDefaultValue(60.0)
                .setTooltip(Component.translatable("ds_tacz_compat.configuration.gunPitchClamp.tooltip"))
                .setSaveConsumer(Config.GUN_PITCH_CLAMP::set).build());
        cat.addEntry(eb.startBooleanToggle(Component.translatable("ds_tacz_compat.configuration.syncRemoteGunEffects"), Config.SYNC_REMOTE_GUN_EFFECTS.get())
                .setDefaultValue(true)
                .setTooltip(Component.translatable("ds_tacz_compat.configuration.syncRemoteGunEffects.tooltip"))
                .setSaveConsumer(Config.SYNC_REMOTE_GUN_EFFECTS::set).build());
    }

    private static void addFirstPerson(ConfigBuilder root, ConfigEntryBuilder eb) {
        ConfigCategory cat = root.getOrCreateCategory(Component.translatable("ds_tacz_compat.config.category.firstPerson"));
        cat.addEntry(eb.startBooleanToggle(Component.translatable("ds_tacz_compat.configuration.hideFirstPersonArms"), Config.HIDE_FIRST_PERSON_ARMS.get())
                .setDefaultValue(true)
                .setTooltip(Component.translatable("ds_tacz_compat.configuration.hideFirstPersonArms.tooltip"))
                .setSaveConsumer(Config.HIDE_FIRST_PERSON_ARMS::set).build());
    }

    private static void addTracer(ConfigBuilder root, ConfigEntryBuilder eb) {
        ConfigCategory cat = root.getOrCreateCategory(Component.translatable("ds_tacz_compat.config.category.tracer"));
        cat.addEntry(eb.startBooleanToggle(Component.translatable("ds_tacz_compat.configuration.tracerVisible"), Config.TRACER_VISIBLE.get())
                .setDefaultValue(true)
                .setTooltip(Component.translatable("ds_tacz_compat.configuration.tracerVisible.tooltip"))
                .setSaveConsumer(Config.TRACER_VISIBLE::set).build());
        cat.addEntry(eb.startBooleanToggle(Component.translatable("ds_tacz_compat.configuration.hideRemoteTracer"), Config.HIDE_REMOTE_TRACER.get())
                .setDefaultValue(true)
                .setTooltip(Component.translatable("ds_tacz_compat.configuration.hideRemoteTracer.tooltip"))
                .setSaveConsumer(Config.HIDE_REMOTE_TRACER::set).build());
    }

    private static void addCrosshair(ConfigBuilder root, ConfigEntryBuilder eb) {
        ConfigCategory cat = root.getOrCreateCategory(Component.translatable("ds_tacz_compat.config.category.crosshair"));
        cat.addEntry(eb.startBooleanToggle(Component.translatable("ds_tacz_compat.configuration.forceShowCrosshair"), Config.FORCE_SHOW_CROSSHAIR.get())
                .setDefaultValue(true)
                .setTooltip(Component.translatable("ds_tacz_compat.configuration.forceShowCrosshair.tooltip"))
                .setSaveConsumer(Config.FORCE_SHOW_CROSSHAIR::set).build());
        cat.addEntry(eb.startBooleanToggle(Component.translatable("ds_tacz_compat.configuration.adsFeatureEnabled"), Config.ADS_FEATURE_ENABLED.get())
                .setDefaultValue(true)
                .setTooltip(Component.translatable("ds_tacz_compat.configuration.adsFeatureEnabled.tooltip"))
                .setSaveConsumer(Config.ADS_FEATURE_ENABLED::set).build());
        cat.addEntry(eb.startDoubleField(Component.translatable("ds_tacz_compat.configuration.adsFadeAmount"), Config.ADS_FADE_AMOUNT.get())
                .setMin(0).setMax(1).setDefaultValue(0.8)
                .setTooltip(Component.translatable("ds_tacz_compat.configuration.adsFadeAmount.tooltip"))
                .setSaveConsumer(Config.ADS_FADE_AMOUNT::set).build());
    }

    private static void addRecoil(ConfigBuilder root, ConfigEntryBuilder eb) {
        ConfigCategory cat = root.getOrCreateCategory(Component.translatable("ds_tacz_compat.config.category.recoil"));
        cat.addEntry(eb.startBooleanToggle(Component.translatable("ds_tacz_compat.configuration.recoilCancelEnabled"), Config.RECOIL_CANCEL_ENABLED.get())
                .setDefaultValue(true)
                .setTooltip(Component.translatable("ds_tacz_compat.configuration.recoilCancelEnabled.tooltip"))
                .setSaveConsumer(Config.RECOIL_CANCEL_ENABLED::set).build());
        cat.addEntry(eb.startEnumSelector(Component.translatable("ds_tacz_compat.configuration.recoilCancelScope"), Config.RecoilCancelScope.class, Config.RECOIL_CANCEL_SCOPE.get())
                .setDefaultValue(Config.RecoilCancelScope.GLIDING)
                .setEnumNameProvider(scope -> Component.translatable("ds_tacz_compat.configuration.recoilCancelScope." + scope.name()))
                .setTooltip(Component.translatable("ds_tacz_compat.configuration.recoilCancelScope.tooltip"))
                .setSaveConsumer(Config.RECOIL_CANCEL_SCOPE::set).build());
    }

    private static void addBackGun(ConfigBuilder root, ConfigEntryBuilder eb) {
        ConfigCategory cat = root.getOrCreateCategory(Component.translatable("ds_tacz_compat.config.category.backGun"));
        cat.addEntry(eb.startBooleanToggle(Component.translatable("ds_tacz_compat.configuration.backGunEnabled"), Config.BACK_GUN_ENABLED.get())
                .setDefaultValue(true)
                .setTooltip(Component.translatable("ds_tacz_compat.configuration.backGunEnabled.tooltip"))
                .setSaveConsumer(Config.BACK_GUN_ENABLED::set).build());
        cat.addEntry(eb.startDoubleField(Component.translatable("ds_tacz_compat.configuration.backGunScale"), Config.BACK_GUN_SCALE.get())
                .setMin(0.1).setMax(5).setDefaultValue(0.5)
                .setTooltip(Component.translatable("ds_tacz_compat.configuration.backGunScale.tooltip"))
                .setSaveConsumer(Config.BACK_GUN_SCALE::set).build());
    }

    private static void addDragonModels(ConfigBuilder root, ConfigEntryBuilder eb) {
        ConfigCategory cat = root.getOrCreateCategory(Component.translatable("ds_tacz_compat.config.category.dragonModels"));
        Set<ResourceLocation> models = scanModels();
        if (models.isEmpty()) {
            cat.addEntry(eb.startTextDescription(Component.translatable("ds_tacz_compat.config.dragon_models.no_world")).build());
            return;
        }
        for (ResourceLocation model : models) {
            addModelEntry(cat, eb, model);
        }
    }

    private static Set<ResourceLocation> scanModels() {
        if (Minecraft.getInstance().level == null) {
            return Set.of();
        }
        Registry<DragonBody> registry = Minecraft.getInstance().level.registryAccess().registryOrThrow(DragonBody.REGISTRY);
        Set<ResourceLocation> models = new LinkedHashSet<>();
        for (Holder.Reference<DragonBody> holder : registry.holders().toList()) {
            models.add(holder.value().model());
        }
        return models;
    }

    private static void addModelEntry(ConfigCategory cat, ConfigEntryBuilder eb, ResourceLocation model) {
        DragonModelConfig.ModelConfig config = DragonModelConfig.getOrCreate(model.toString());
        SubCategoryBuilder sub = eb.startSubCategory(Component.literal(model.toString()));

        sub.add(eb.startBooleanToggle(Component.translatable("ds_tacz_compat.config.dragon_models.enableFloating"), config.floatingGun.enabled)
                .setDefaultValue(true)
                .setSaveConsumer(v -> config.floatingGun.enabled = v).build());
        sub.add(doubleSlider(eb, "ds_tacz_compat.config.dragon_models.offsetX", config.floatingGun.offsetX, 0.0f, -500, 500, v -> config.floatingGun.offsetX = v / 100.0f));
        sub.add(doubleSlider(eb, "ds_tacz_compat.config.dragon_models.offsetY", config.floatingGun.offsetY, 1.0f, -200, 500, v -> config.floatingGun.offsetY = v / 100.0f));
        sub.add(doubleSlider(eb, "ds_tacz_compat.config.dragon_models.offsetZ", config.floatingGun.offsetZ, 0.0f, -500, 500, v -> config.floatingGun.offsetZ = v / 100.0f));
        sub.add(doubleSlider(eb, "ds_tacz_compat.config.dragon_models.scale", config.floatingGun.scale, 0.8f, 10, 500, v -> config.floatingGun.scale = v / 100.0f));

        sub.add(eb.startBooleanToggle(Component.translatable("ds_tacz_compat.config.dragon_models.enableBack"), config.backGun.enabled)
                .setDefaultValue(true)
                .setSaveConsumer(v -> config.backGun.enabled = v).build());
        sub.add(doubleSlider(eb, "ds_tacz_compat.config.dragon_models.posX", config.backGun.posX, 0.0f, -500, 500, v -> config.backGun.posX = v / 100.0f));
        sub.add(doubleSlider(eb, "ds_tacz_compat.config.dragon_models.posY", config.backGun.posY, 0.36f, -500, 500, v -> config.backGun.posY = v / 100.0f));
        sub.add(doubleSlider(eb, "ds_tacz_compat.config.dragon_models.posZ", config.backGun.posZ, 0.33f, -500, 500, v -> config.backGun.posZ = v / 100.0f));
        sub.add(doubleSlider(eb, "ds_tacz_compat.config.dragon_models.rotX", config.backGun.rotX, 90.0f, -18000, 18000, v -> config.backGun.rotX = v / 100.0f));
        sub.add(doubleSlider(eb, "ds_tacz_compat.config.dragon_models.rotY", config.backGun.rotY, 120.0f, -18000, 18000, v -> config.backGun.rotY = v / 100.0f));
        sub.add(doubleSlider(eb, "ds_tacz_compat.config.dragon_models.rotZ", config.backGun.rotZ, 0.0f, -18000, 18000, v -> config.backGun.rotZ = v / 100.0f));
        sub.add(doubleSlider(eb, "ds_tacz_compat.config.dragon_models.backScale", config.backGun.scale, 0.5f, 10, 500, v -> config.backGun.scale = v / 100.0f));

        cat.addEntry(sub.build());
    }

    // Cloth has no double slider, so store values ×100 and render them as decimals.
    private static AbstractConfigListEntry<Integer> doubleSlider(ConfigEntryBuilder eb, String key, float value,
                                                                 float def, int min100, int max100, Consumer<Integer> save) {
        return eb.startIntSlider(Component.translatable(key), Math.round(value * 100), min100, max100)
                .setDefaultValue(Math.round(def * 100))
                .setTextGetter(v -> Component.literal(String.format("%.2f", v / 100.0)))
                .setSaveConsumer(save)
                .build();
    }

    private static void addModelTool(ConfigBuilder root, ConfigEntryBuilder eb, Screen parent) {
        ConfigCategory cat = root.getOrCreateCategory(Component.translatable("ds_tacz_compat.config.category.modelTool"));
        if (Minecraft.getInstance().level == null) {
            cat.addEntry(eb.startTextDescription(Component.translatable("ds_tacz_compat.config.dragon_models.no_world")).build());
            return;
        }
        cat.addEntry(new ApplyDefaultsEntry(Component.translatable("ds_tacz_compat.config.model_tool.apply"), parent));
    }

    public static void applyGlobalDefaultsToAllModels() {
        for (ResourceLocation model : scanModels()) {
            DragonModelConfig.ModelConfig config = DragonModelConfig.getOrCreate(model.toString());
            config.floatingGun.offsetX = Config.GUN_OFFSET_X.get().floatValue();
            config.floatingGun.offsetY = Config.GUN_HEIGHT_OFFSET.get().floatValue();
            config.floatingGun.offsetZ = Config.GUN_OFFSET_Z.get().floatValue();
            config.floatingGun.scale = Config.GUN_BASE_SCALE.get().floatValue();
            config.backGun.scale = Config.BACK_GUN_SCALE.get().floatValue();
        }
        DragonModelConfig.save();
    }

    private static void addServer(ConfigBuilder root, ConfigEntryBuilder eb) {
        ConfigCategory cat = root.getOrCreateCategory(Component.translatable("ds_tacz_compat.config.category.server"));
        cat.addEntry(eb.startBooleanToggle(Component.translatable("ds_tacz_compat.configuration.glidingShooting"), ServerConfig.GLIDING_SHOOTING.get())
                .setDefaultValue(true)
                .setTooltip(Component.translatable("ds_tacz_compat.configuration.glidingShooting.tooltip"))
                .setSaveConsumer(ServerConfig.GLIDING_SHOOTING::set).build());
        cat.addEntry(eb.startDoubleField(Component.translatable("ds_tacz_compat.configuration.speedInfluenceMultiplier"), ServerConfig.speedInfluenceMultiplier())
                .setMin(0).setMax(3).setDefaultValue(1.0)
                .setTooltip(Component.translatable("ds_tacz_compat.configuration.speedInfluenceMultiplier.tooltip"))
                .setSaveConsumer(ServerConfig.SPEED_INFLUENCE_MULTIPLIER::set).build());
        cat.addEntry(eb.startBooleanToggle(Component.translatable("ds_tacz_compat.configuration.syncBackGunToClients"), ServerConfig.isSyncBackGunToClients())
                .setDefaultValue(true)
                .setTooltip(Component.translatable("ds_tacz_compat.configuration.syncBackGunToClients.tooltip"))
                .setSaveConsumer(ServerConfig.SYNC_BACK_GUN_TO_CLIENTS::set).build());
    }
}
