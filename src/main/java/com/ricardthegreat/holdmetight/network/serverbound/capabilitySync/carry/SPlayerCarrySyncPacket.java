package com.ricardthegreat.holdmetight.network.serverbound.capabilitySync.carry;

import java.util.ArrayList;

import com.ricardthegreat.holdmetight.capabilities.carry.CarryPosition;
import com.ricardthegreat.holdmetight.capabilities.carry.PlayerCarry;
import com.ricardthegreat.holdmetight.capabilities.carry.PlayerCarryProvider;
import com.ricardthegreat.holdmetight.network.PacketHandler;
import com.ricardthegreat.holdmetight.network.clientbound.capabilitySync.carry.CPlayerCarrySyncPacket;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class SPlayerCarrySyncPacket implements CustomPacketPayload {

    public static final Type<SPlayerCarrySyncPacket> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath("holdmetight", "s_player_carry_sync"));
    public static final StreamCodec<FriendlyByteBuf, SPlayerCarrySyncPacket> STREAM_CODEC = StreamCodec.ofMember(SPlayerCarrySyncPacket::write, SPlayerCarrySyncPacket::new);

    private final ArrayList<CompoundTag> carriedPlayers;
    private final ArrayList<CarryPosition> customPos;

    public SPlayerCarrySyncPacket(ArrayList<CompoundTag> carriedPlayers, ArrayList<CarryPosition> customPos){
        this.carriedPlayers = carriedPlayers;
        this.customPos = customPos;
    }
    
    public SPlayerCarrySyncPacket(FriendlyByteBuf buffer){
        int listSize = buffer.readInt();

        ArrayList<CompoundTag> temp = new ArrayList<CompoundTag>();
        for(int i = 0; i < listSize; i++){
            temp.add(buffer.readNbt());
        }
        this.carriedPlayers = temp;

        listSize = buffer.readInt();
        ArrayList<CarryPosition> tempPos = new ArrayList<CarryPosition>();
        for(int i = 0; i < listSize; i++){
            tempPos.add(new CarryPosition(buffer.readUtf(), buffer.readInt(), buffer.readDouble(), buffer.readDouble(), buffer.readDouble(), buffer.readBoolean()));
        }
        this.customPos = tempPos;
    }

    public void write(FriendlyByteBuf buffer){
        buffer.writeInt(carriedPlayers.size());

        for (CompoundTag compoundTag : carriedPlayers) {
            buffer.writeNbt(compoundTag);
        }

        buffer.writeInt(customPos.size());
        for (CarryPosition carryPos : customPos) {
            buffer.writeUtf(carryPos.posName);
            buffer.writeInt(carryPos.RotationOffset);
            buffer.writeDouble(carryPos.leftRightMove);
            buffer.writeDouble(carryPos.vertOffset);
            buffer.writeDouble(carryPos.xymult);
            buffer.writeBoolean(carryPos.headLink);
        }
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(SPlayerCarrySyncPacket data, IPayloadContext context){
        context.enqueueWork(() -> {
            ServerPlayer player = (ServerPlayer) context.player();

            PlayerCarry carry = PlayerCarryProvider.getPlayerCarryCapability(player);

            if(carry != null){
                carry.updateAllSyncables(data.carriedPlayers, data.customPos);

                PacketHandler.sendToAllClients(new CPlayerCarrySyncPacket(data.carriedPlayers, data.customPos, player.getUUID()));
            }
        });
    }
}
