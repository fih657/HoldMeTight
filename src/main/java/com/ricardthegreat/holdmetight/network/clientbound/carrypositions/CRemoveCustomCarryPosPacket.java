package com.ricardthegreat.holdmetight.network.clientbound.carrypositions;

import java.util.UUID;

import com.ricardthegreat.holdmetight.client.handlers.ClientPacketHandler;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class CRemoveCustomCarryPosPacket implements CustomPacketPayload {

    public static final Type<CRemoveCustomCarryPosPacket> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath("holdmetight", "c_remove_custom_carry_pos"));
    public static final StreamCodec<FriendlyByteBuf, CRemoveCustomCarryPosPacket> STREAM_CODEC = StreamCodec.ofMember(CRemoveCustomCarryPosPacket::write, CRemoveCustomCarryPosPacket::new);

    private final String pos;
    private final UUID uuid;

    public CRemoveCustomCarryPosPacket(String pos,UUID uuid){
        this.pos = pos;
        this.uuid = uuid;
    }

    public CRemoveCustomCarryPosPacket(FriendlyByteBuf buffer){
        this.pos = buffer.readUtf();
        
        this.uuid = buffer.readUUID();
    }

    public void write(FriendlyByteBuf buffer){
        buffer.writeUtf(pos);

        buffer.writeUUID(uuid);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(CRemoveCustomCarryPosPacket data, IPayloadContext context){
        context.enqueueWork(() -> ClientPacketHandler.handleRemoveCarryPosPacket(data));
    }

    public UUID getUuid() {
        return uuid;
    }

    public String getCarryPos(){
        return pos;
    }
}
