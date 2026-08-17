package com.hoz.ds_tacz_compat;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;

@EventBusSubscriber(modid = TaczArmHide.MODID, bus = EventBusSubscriber.Bus.MOD)
public final class NetworkHandler {
    private static final String VERSION = "1.0.0";

    private NetworkHandler() {}

    @SubscribeEvent
    public static void register(RegisterPayloadHandlersEvent event) {
        // optional(): the client can join a server that lacks this mod without being kicked
        // for a missing channel; the back-gun sync just becomes unavailable.
        event.registrar(VERSION).optional().playToClient(
                BackGunSyncPayload.TYPE, BackGunSyncPayload.STREAM_CODEC, BackGunSyncPayload::handle);
    }

    public static void sendBackGun(Entity player, ItemStack stack) {
        PacketDistributor.sendToPlayersTrackingEntityAndSelf(player, new BackGunSyncPayload(player.getId(), stack));
    }

    public static void sendBackGunTo(ServerPlayer player, int entityId, ItemStack stack) {
        PacketDistributor.sendToPlayer(player, new BackGunSyncPayload(entityId, stack));
    }
}
