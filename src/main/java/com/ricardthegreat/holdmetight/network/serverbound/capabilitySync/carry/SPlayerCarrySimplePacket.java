package com.ricardthegreat.holdmetight.network.serverbound.capabilitySync.carry;

import java.util.UUID;

import com.ricardthegreat.holdmetight.capabilities.carry.PlayerCarry;
import com.ricardthegreat.holdmetight.capabilities.carry.PlayerCarryProvider;
import com.ricardthegreat.holdmetight.network.PacketHandler;
import com.ricardthegreat.holdmetight.network.clientbound.capabilitySync.carry.CPlayerCarrySimplePacket;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class SPlayerCarrySimplePacket implements CustomPacketPayload {

    public static final Type<SPlayerCarrySimplePacket> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath("holdmetight", "s_player_carry_simple"));
    public static final StreamCodec<FriendlyByteBuf, SPlayerCarrySimplePacket> STREAM_CODEC = StreamCodec.ofMember(SPlayerCarrySimplePacket::write, SPlayerCarrySimplePacket::new);

    private final boolean carried;
    private final boolean carrying;
    private final int[] carryPos;
    private final UUID uuid;

    public SPlayerCarrySimplePacket(boolean carried, boolean carrying, int[] carryPos, UUID uuid){
        this.carried = carried;
        this.carrying = carrying;
        this.carryPos = carryPos;
        this.uuid = uuid;
    }
    
    public SPlayerCarrySimplePacket(FriendlyByteBuf buffer){
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

    public static void handle(SPlayerCarrySimplePacket data, IPayloadContext context){
        context.enqueueWork(() -> {
            ServerPlayer player = (ServerPlayer) context.player();

            PlayerCarry carry = PlayerCarryProvider.getPlayerCarryCapability(player);

            if(carry != null){
                PacketHandler.sendToAllClients(new CPlayerCarrySimplePacket(data.carried, data.carrying, data.carryPos, player.getUUID()));
            }
        });
    }
    
}
