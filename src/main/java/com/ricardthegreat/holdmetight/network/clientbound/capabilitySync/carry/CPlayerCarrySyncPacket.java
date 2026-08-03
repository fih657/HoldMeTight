package com.ricardthegreat.holdmetight.network.clientbound.capabilitySync.carry;

import java.util.ArrayList;
import java.util.UUID;

import com.ricardthegreat.holdmetight.capabilities.carry.CarryPosition;
import com.ricardthegreat.holdmetight.capabilities.carry.PlayerCarry;
import com.ricardthegreat.holdmetight.client.handlers.ClientPacketHandler;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class CPlayerCarrySyncPacket implements CustomPacketPayload {

    public static final Type<CPlayerCarrySyncPacket> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath("holdmetight", "c_player_carry_sync"));
    public static final StreamCodec<FriendlyByteBuf, CPlayerCarrySyncPacket> STREAM_CODEC = StreamCodec.ofMember(CPlayerCarrySyncPacket::write, CPlayerCarrySyncPacket::new);

    private final ArrayList<CompoundTag> carriedPlayers;
    private final ArrayList<CarryPosition> customPos;
    private final UUID uuid;

    public CPlayerCarrySyncPacket(ArrayList<CompoundTag> carriedPlayers, ArrayList<CarryPosition> customPos,UUID uuid){
        this.carriedPlayers = carriedPlayers;
        this.customPos = customPos;
        this.uuid = uuid;
    }

    public CPlayerCarrySyncPacket(FriendlyByteBuf buffer){
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
        
        this.uuid = buffer.readUUID();
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

        buffer.writeUUID(uuid);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(CPlayerCarrySyncPacket data, IPayloadContext context){
        context.enqueueWork(() -> ClientPacketHandler.handleCarryPacket(data));
    }

    public void playerSyncablesUpdate(PlayerCarry playerCarry){
        playerCarry.updateAllSyncables(carriedPlayers, customPos);
    }

    public UUID getUuid() {
        return uuid;
    }
}
