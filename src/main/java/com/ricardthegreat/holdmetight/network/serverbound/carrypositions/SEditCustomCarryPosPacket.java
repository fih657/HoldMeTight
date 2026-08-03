package com.ricardthegreat.holdmetight.network.serverbound.carrypositions;

import com.ricardthegreat.holdmetight.capabilities.carry.CarryPosition;
import com.ricardthegreat.holdmetight.capabilities.carry.PlayerCarry;
import com.ricardthegreat.holdmetight.capabilities.carry.PlayerCarryProvider;
import com.ricardthegreat.holdmetight.network.PacketHandler;
import com.ricardthegreat.holdmetight.network.clientbound.carrypositions.CEditCustomCarryPosPacket;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class SEditCustomCarryPosPacket implements CustomPacketPayload {

    public static final Type<SEditCustomCarryPosPacket> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath("holdmetight", "s_edit_custom_carry_pos"));
    public static final StreamCodec<FriendlyByteBuf, SEditCustomCarryPosPacket> STREAM_CODEC = StreamCodec.ofMember(SEditCustomCarryPosPacket::write, SEditCustomCarryPosPacket::new);

    private final CarryPosition customPos;
    private final int index;

    public SEditCustomCarryPosPacket(CarryPosition customPos, int index){
        this.customPos = customPos;
        this.index = index;
    }
    
    public SEditCustomCarryPosPacket(FriendlyByteBuf buffer){
        this.customPos = new CarryPosition(buffer.readUtf(), buffer.readInt(), buffer.readDouble(), buffer.readDouble(), buffer.readDouble(), buffer.readBoolean());
        this.index = buffer.readInt();
    }

    public void write(FriendlyByteBuf buffer){
        buffer.writeUtf(customPos.posName);
        buffer.writeInt(customPos.RotationOffset);
        buffer.writeDouble(customPos.leftRightMove);
        buffer.writeDouble(customPos.vertOffset);
        buffer.writeDouble(customPos.xymult);
        buffer.writeBoolean(customPos.headLink);

        buffer.writeInt(index);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(SEditCustomCarryPosPacket data, IPayloadContext context){
        context.enqueueWork(() -> {
            ServerPlayer player = (ServerPlayer) context.player();

            PlayerCarry carry = PlayerCarryProvider.getPlayerCarryCapability(player);

            if(carry != null){
                carry.editCustomCarryPos(data.customPos, data.index);

                PacketHandler.sendToAllClients(new CEditCustomCarryPosPacket(data.customPos, data.index, player.getUUID()));
            }
        });
    }
}
