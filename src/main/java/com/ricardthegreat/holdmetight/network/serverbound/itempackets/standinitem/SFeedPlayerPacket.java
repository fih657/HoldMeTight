package com.ricardthegreat.holdmetight.network.serverbound.itempackets.standinitem;

import java.util.UUID;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class SFeedPlayerPacket implements CustomPacketPayload {

    public static final Type<SFeedPlayerPacket> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath("holdmetight", "s_feed_player"));
    public static final StreamCodec<RegistryFriendlyByteBuf, SFeedPlayerPacket> STREAM_CODEC = StreamCodec.ofMember(SFeedPlayerPacket::write, SFeedPlayerPacket::new);

    private final ItemStack item; 
    private final UUID targetPlayer;

    public SFeedPlayerPacket(ItemStack item, UUID targetPlayer){
        this.item = item;
        this.targetPlayer = targetPlayer;
    }
    
    public SFeedPlayerPacket(RegistryFriendlyByteBuf buffer){
        this(ItemStack.OPTIONAL_STREAM_CODEC.decode(buffer), buffer.readUUID());
    }

    public void write(RegistryFriendlyByteBuf buffer){
        ItemStack.OPTIONAL_STREAM_CODEC.encode(buffer, item);
        buffer.writeUUID(targetPlayer);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(SFeedPlayerPacket data, IPayloadContext context){
        context.enqueueWork(() -> {
            Player sender = context.player();
            Level level = sender.level();
            Player target = level.getPlayerByUUID(data.targetPlayer);

            if (target != null) {
                data.item.finishUsingItem(level, target);
            }
        });
    }
}
