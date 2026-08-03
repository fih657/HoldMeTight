package com.ricardthegreat.holdmetight.network.clientbound.capabilitySync.size;

import java.util.UUID;

import com.ricardthegreat.holdmetight.capabilities.size.PlayerSize;
import com.ricardthegreat.holdmetight.client.handlers.ClientPacketHandler;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class CPlayerSizeMixinSyncPacket implements CustomPacketPayload {

    public static final Type<CPlayerSizeMixinSyncPacket> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath("holdmetight", "c_player_size_mixin_sync"));
    public static final StreamCodec<FriendlyByteBuf, CPlayerSizeMixinSyncPacket> STREAM_CODEC = StreamCodec.ofMember(CPlayerSizeMixinSyncPacket::write, CPlayerSizeMixinSyncPacket::new);

    //TODO remove this class

    //private final float maxScale;
    //private final float minScale;
    //private final float defaultScale;
    private final UUID uuid;

    public CPlayerSizeMixinSyncPacket(UUID uuid){
        this.uuid = uuid;
    }

    public CPlayerSizeMixinSyncPacket(FriendlyByteBuf buffer){
        this(buffer.readUUID());
    }

    public void write(FriendlyByteBuf buffer){
        buffer.writeUUID(uuid);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(CPlayerSizeMixinSyncPacket data, IPayloadContext context){
        context.enqueueWork(() -> ClientPacketHandler.handleSizePacket(data));
    }

    public void playerSyncablesUpdate(PlayerSize playerSize){
    }

    public UUID getUuid() {
        return uuid;
    }
}
