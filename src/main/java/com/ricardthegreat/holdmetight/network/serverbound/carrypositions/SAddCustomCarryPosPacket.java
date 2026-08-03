package com.ricardthegreat.holdmetight.network.serverbound.carrypositions;

import java.util.UUID;

import com.ricardthegreat.holdmetight.capabilities.carry.CarryPosition;
import com.ricardthegreat.holdmetight.capabilities.carry.PlayerCarry;
import com.ricardthegreat.holdmetight.capabilities.carry.PlayerCarryProvider;
import com.ricardthegreat.holdmetight.network.PacketHandler;
import com.ricardthegreat.holdmetight.network.clientbound.carrypositions.CAddCustomCarryPosPacket;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.type.capability.ICuriosItemHandler;

public class SAddCustomCarryPosPacket implements CustomPacketPayload {

    public static final Type<SAddCustomCarryPosPacket> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath("holdmetight", "s_add_custom_carry_pos"));
    public static final StreamCodec<FriendlyByteBuf, SAddCustomCarryPosPacket> STREAM_CODEC = StreamCodec.ofMember(SAddCustomCarryPosPacket::write, SAddCustomCarryPosPacket::new);

    private final CarryPosition customPos;

    public SAddCustomCarryPosPacket(CarryPosition customPos){
        this.customPos = customPos;
    }
    
    public SAddCustomCarryPosPacket(FriendlyByteBuf buffer){
        this.customPos = new CarryPosition(buffer.readUtf(), buffer.readInt(), buffer.readDouble(), buffer.readDouble(), buffer.readDouble(), buffer.readBoolean());
    }

    public void write(FriendlyByteBuf buffer){
        buffer.writeUtf(customPos.posName);
        buffer.writeInt(customPos.RotationOffset);
        buffer.writeDouble(customPos.leftRightMove);
        buffer.writeDouble(customPos.vertOffset);
        buffer.writeDouble(customPos.xymult);
        buffer.writeBoolean(customPos.headLink);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(SAddCustomCarryPosPacket data, IPayloadContext context){
        context.enqueueWork(() -> {
            ServerPlayer player = (ServerPlayer) context.player();

            PlayerCarry carry = PlayerCarryProvider.getPlayerCarryCapability(player);

            ICuriosItemHandler curiosInventory = CuriosApi.getCuriosInventory(player).orElse(null);
            curiosInventory.addTransientSlotModifier("custom", ResourceLocation.fromNamespaceAndPath("holdmetight", "custom_" + data.customPos.posName), 1, AttributeModifier.Operation.ADD_VALUE);

            if(carry != null){
                carry.addCustomCarryPos(data.customPos);

                PacketHandler.sendToAllClients(new CAddCustomCarryPosPacket(data.customPos, player.getUUID()));
            }
        });
    }
}
