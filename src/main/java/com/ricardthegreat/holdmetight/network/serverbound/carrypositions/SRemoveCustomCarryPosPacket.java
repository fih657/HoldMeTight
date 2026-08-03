package com.ricardthegreat.holdmetight.network.serverbound.carrypositions;

import java.util.UUID;

import com.ricardthegreat.holdmetight.capabilities.carry.PlayerCarry;
import com.ricardthegreat.holdmetight.capabilities.carry.PlayerCarryProvider;
import com.ricardthegreat.holdmetight.network.PacketHandler;
import com.ricardthegreat.holdmetight.network.clientbound.carrypositions.CRemoveCustomCarryPosPacket;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.type.capability.ICuriosItemHandler;

public class SRemoveCustomCarryPosPacket implements CustomPacketPayload {

    public static final Type<SRemoveCustomCarryPosPacket> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath("holdmetight", "s_remove_custom_carry_pos"));
    public static final StreamCodec<FriendlyByteBuf, SRemoveCustomCarryPosPacket> STREAM_CODEC = StreamCodec.ofMember(SRemoveCustomCarryPosPacket::write, SRemoveCustomCarryPosPacket::new);

    private final String pos;

    public SRemoveCustomCarryPosPacket(String pos){
        this.pos = pos;
    }
    
    public SRemoveCustomCarryPosPacket(FriendlyByteBuf buffer){
        this.pos = buffer.readUtf();
    }

    public void write(FriendlyByteBuf buffer){
        buffer.writeUtf(pos);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(SRemoveCustomCarryPosPacket data, IPayloadContext context){
        context.enqueueWork(() -> {
            ServerPlayer player = (ServerPlayer) context.player();

            PlayerCarry carry = PlayerCarryProvider.getPlayerCarryCapability(player);

            ICuriosItemHandler curiosInventory = CuriosApi.getCuriosInventory(player).orElse(null);
            curiosInventory.removeSlotModifier("custom", ResourceLocation.fromNamespaceAndPath("holdmetight", "custom_" + data.pos));            //curiosInventory.addTransientSlotModifier("custom", UUID.nameUUIDFromBytes(CurioUUIDConstants.CUSTOM_UUID.getBytes()), CurioUUIDConstants.CUSTOM_UUID, -1, AttributeModifier.Operation.ADDITION);

            if(carry != null){
                carry.removeCustomCarryPos(data.pos);

                PacketHandler.sendToAllClients(new CRemoveCustomCarryPosPacket(data.pos, player.getUUID()));
            }
        });
    }
}
