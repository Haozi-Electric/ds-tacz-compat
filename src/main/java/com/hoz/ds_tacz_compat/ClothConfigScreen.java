package com.hoz.ds_tacz_compat;

import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.List;

public final class ClothConfigScreen {
    private ClothConfigScreen() {}

    public static Screen create(Screen parent) {
        ConfigBuilder root = ConfigBuilder.create()
                .setTitle(Component.translatable("ds_tacz_compat.configuration.title"))
                .setParentScreen(parent);
        root.setSavingRunnable(() -> {
            if (Config.SPEC.isLoaded()) Config.SPEC.save();
            if (ServerConfig.SPEC.isLoaded()) ServerConfig.SPEC.save();
        });
        ConfigEntryBuilder eb = root.entryBuilder();

        addFloatingGun(root, eb);
        addFirstPerson(root, eb);
        addTracer(root, eb);
        addCrosshair(root, eb);
        addRecoil(root, eb);
        addBackGun(root, eb);
        addBlacklist(root, eb);
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
                .setMin(0).setMax(5).setDefaultValue(0.5)
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

    private static void addBlacklist(ConfigBuilder root, ConfigEntryBuilder eb) {
        ConfigCategory cat = root.getOrCreateCategory(Component.translatable("ds_tacz_compat.config.category.blacklist"));
        cat.addEntry(eb.startStrList(Component.translatable("ds_tacz_compat.configuration.disabledGunModels"), List.copyOf(Config.DISABLED_GUN_MODELS.get()))
                .setDefaultValue(List.of("dragonsurvival:dihuang_loong"))
                .setTooltip(Component.translatable("ds_tacz_compat.configuration.disabledGunModels.tooltip"))
                .setSaveConsumer(list -> Config.DISABLED_GUN_MODELS.set(list)).build());
        cat.addEntry(eb.startStrList(Component.translatable("ds_tacz_compat.configuration.disabledBackGunModels"), List.copyOf(Config.DISABLED_BACK_GUN_MODELS.get()))
                .setDefaultValue(List.of("dragonsurvival:dihuang_loong"))
                .setTooltip(Component.translatable("ds_tacz_compat.configuration.disabledBackGunModels.tooltip"))
                .setSaveConsumer(list -> Config.DISABLED_BACK_GUN_MODELS.set(list)).build());
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
    }
}
