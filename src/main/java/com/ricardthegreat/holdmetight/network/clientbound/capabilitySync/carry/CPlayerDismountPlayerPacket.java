package com.ricardthegreat.holdmetight.network.clientbound.capabilitySync.carry;

import java.util.UUID;

import com.ricardthegreat.holdmetight.client.handlers.ClientPacketHandler;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class CPlayerDismountPlayerPacket implements CustomPacketPayload {

    public static final Type<CPlayerDismountPlayerPacket> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath("holdmetight", "c_player_dismount_player"));
    public static final StreamCodec<FriendlyByteBuf, CPlayerDismountPlayerPacket> STREAM_CODEC = StreamCodec.ofMember(CPlayerDismountPlayerPacket::write, CPlayerDismountPlayerPacket::new);

    private final UUID uuid;

    public CPlayerDismountPlayerPacket(UUID uuid){

        this.uuid = uuid;

    }

    public CPlayerDismountPlayerPacket(FriendlyByteBuf buffer){
        this(buffer.readUUID());
    }

    public void write(FriendlyByteBuf buffer){

        buffer.writeUUID(uuid);

    }

    public UUID getUuid(){
        return this.uuid;
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(CPlayerDismountPlayerPacket data, IPayloadContext context){
        context.enqueueWork(() -> ClientPacketHandler.handleDismountPacket(data));
    }
    
}
