package com.hoz.ds_tacz_compat;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;

/**
 * Server -> client: the back-gun item of a dragon player, sent when it changes so nearby
 * clients can render it on the remote dragon.
 */
public record BackGunSyncPayload(int entityId, ItemStack stack) implements CustomPacketPayload {
    public static final Type<BackGunSyncPayload> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(TaczArmHide.MODID, "back_gun_sync"));
    public static final StreamCodec<RegistryFriendlyByteBuf, BackGunSyncPayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, BackGunSyncPayload::entityId,
            ItemStack.OPTIONAL_STREAM_CODEC, BackGunSyncPayload::stack,
            BackGunSyncPayload::new);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(BackGunSyncPayload message, IPayloadContext context) {
        context.enqueueWork(() -> BackGunSync.clientCache(message.entityId, message.stack));
    }
}
