package com.ricardthegreat.holdmetight.network.clientbound.carrypositions;

import java.util.UUID;

import com.ricardthegreat.holdmetight.capabilities.carry.CarryPosition;
import com.ricardthegreat.holdmetight.client.handlers.ClientPacketHandler;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class CAddCustomCarryPosPacket implements CustomPacketPayload {

    public static final Type<CAddCustomCarryPosPacket> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath("holdmetight", "c_add_custom_carry_pos"));
    public static final StreamCodec<FriendlyByteBuf, CAddCustomCarryPosPacket> STREAM_CODEC = StreamCodec.ofMember(CAddCustomCarryPosPacket::write, CAddCustomCarryPosPacket::new);

    private final CarryPosition customPos;
    private final UUID uuid;

    public CAddCustomCarryPosPacket(CarryPosition customPos,UUID uuid){
        this.customPos = customPos;
        this.uuid = uuid;
    }

    public CAddCustomCarryPosPacket(FriendlyByteBuf buffer){
        this.customPos = new CarryPosition(buffer.readUtf(), buffer.readInt(), buffer.readDouble(), buffer.readDouble(), buffer.readDouble(), buffer.readBoolean());
        
        this.uuid = buffer.readUUID();
    }

    public void write(FriendlyByteBuf buffer){
        buffer.writeUtf(customPos.posName);
        buffer.writeInt(customPos.RotationOffset);
        buffer.writeDouble(customPos.leftRightMove);
        buffer.writeDouble(customPos.vertOffset);
        buffer.writeDouble(customPos.xymult);
        buffer.writeBoolean(customPos.headLink);

        buffer.writeUUID(uuid);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(CAddCustomCarryPosPacket data, IPayloadContext context){
        context.enqueueWork(() -> ClientPacketHandler.handleAddCarryPosPacket(data));
    }

    public UUID getUuid() {
        return uuid;
    }

    public CarryPosition getCarryPos(){
        return customPos;
    }
}
