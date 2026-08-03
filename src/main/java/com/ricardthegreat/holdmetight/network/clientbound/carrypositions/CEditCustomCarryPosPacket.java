package com.ricardthegreat.holdmetight.network.clientbound.carrypositions;

import java.util.UUID;

import com.ricardthegreat.holdmetight.capabilities.carry.CarryPosition;
import com.ricardthegreat.holdmetight.client.handlers.ClientPacketHandler;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class CEditCustomCarryPosPacket implements CustomPacketPayload {

    public static final Type<CEditCustomCarryPosPacket> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath("holdmetight", "c_edit_custom_carry_pos"));
    public static final StreamCodec<FriendlyByteBuf, CEditCustomCarryPosPacket> STREAM_CODEC = StreamCodec.ofMember(CEditCustomCarryPosPacket::write, CEditCustomCarryPosPacket::new);

    private final CarryPosition customPos;
    private final int index;
    private final UUID uuid;

    public CEditCustomCarryPosPacket(CarryPosition customPos, int index, UUID uuid){
        this.customPos = customPos;
        this.index = index;
        this.uuid = uuid;
    }

    public CEditCustomCarryPosPacket(FriendlyByteBuf buffer){
        this.customPos = new CarryPosition(buffer.readUtf(), buffer.readInt(), buffer.readDouble(), buffer.readDouble(), buffer.readDouble(), buffer.readBoolean());
        
        this.index = buffer.readInt();

        this.uuid = buffer.readUUID();
    }

    public void write(FriendlyByteBuf buffer){
        buffer.writeUtf(customPos.posName);
        buffer.writeInt(customPos.RotationOffset);
        buffer.writeDouble(customPos.leftRightMove);
        buffer.writeDouble(customPos.vertOffset);
        buffer.writeDouble(customPos.xymult);
        buffer.writeBoolean(customPos.headLink);

        buffer.writeInt(index);

        buffer.writeUUID(uuid);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(CEditCustomCarryPosPacket data, IPayloadContext context){
        context.enqueueWork(() -> ClientPacketHandler.handleEditCarryPosPacket(data));
    }

    public UUID getUuid() {
        return uuid;
    }

    public CarryPosition getCarryPos(){
        return customPos;
    }

    public int getIndex(){
        return index;
    }
}
