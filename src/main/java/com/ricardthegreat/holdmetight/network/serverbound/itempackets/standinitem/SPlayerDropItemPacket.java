package com.ricardthegreat.holdmetight.network.serverbound.itempackets.standinitem;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class SPlayerDropItemPacket implements CustomPacketPayload {

    public static final Type<SPlayerDropItemPacket> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath("holdmetight", "s_player_drop_item"));
    public static final StreamCodec<FriendlyByteBuf, SPlayerDropItemPacket> STREAM_CODEC = StreamCodec.ofMember(SPlayerDropItemPacket::write, SPlayerDropItemPacket::new);

    CompoundTag stack;

    public SPlayerDropItemPacket(CompoundTag stack){
        this.stack = stack;
    }
    
    public SPlayerDropItemPacket(FriendlyByteBuf buffer){
        this.stack = buffer.readNbt();
    }

    public void write(FriendlyByteBuf buffer){
        buffer.writeNbt(stack);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(SPlayerDropItemPacket data, IPayloadContext context){
        context.enqueueWork(() -> {
            Player sender = context.player();

            ItemStack item = ItemStack.parseOptional(sender.registryAccess(), data.stack);

            item.getItem().onDroppedByPlayer(item, sender);
        });
    }
}
