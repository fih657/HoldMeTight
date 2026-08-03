package com.ricardthegreat.holdmetight.network.clientbound.capabilitySync.carry;

import java.util.UUID;

import com.ricardthegreat.holdmetight.client.handlers.ClientPacketHandler;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class CPlayerCarrySimplePacket implements CustomPacketPayload {

    public static final Type<CPlayerCarrySimplePacket> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath("holdmetight", "c_player_carry_simple"));
    public static final StreamCodec<FriendlyByteBuf, CPlayerCarrySimplePacket> STREAM_CODEC = StreamCodec.ofMember(CPlayerCarrySimplePacket::write, CPlayerCarrySimplePacket::new);

    private final boolean carried;
    private final boolean carrying;
    private final int[] carryPos;
    private final UUID uuid;

    public CPlayerCarrySimplePacket(boolean carried, boolean carrying, int[] carryPos, UUID uuid){
        this.carried = carried;
        this.carrying = carrying;
        this.carryPos = carryPos;
        this.uuid = uuid;
    }

    public CPlayerCarrySimplePacket(FriendlyByteBuf buffer){
        this(buffer.readBoolean(), buffer.readBoolean(), new int[]{buffer.readInt(),buffer.readInt()}, buffer.readUUID());
    }

    public void write(FriendlyByteBuf buffer){
        buffer.writeBoolean(carried);
        buffer.writeBoolean(carrying);
        buffer.writeInt(carryPos[0]);
        buffer.writeInt(carryPos[1]);
        buffer.writeUUID(uuid);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(CPlayerCarrySimplePacket data, IPayloadContext context){
        context.enqueueWork(() -> ClientPacketHandler.handleCarryPositionPacket(data.carried, data.carrying, data.carryPos, data.uuid));
    }
}
