package com.ricardthegreat.holdmetight.network.serverbound.itempackets.standinitem;

import java.util.UUID;

import com.ricardthegreat.holdmetight.inventory.HeldEntityInventoryProvider;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class SOpenStandInItemMenuPacket implements CustomPacketPayload {

    public static final Type<SOpenStandInItemMenuPacket> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath("holdmetight", "s_open_stand_in_item_menu"));
    public static final StreamCodec<FriendlyByteBuf, SOpenStandInItemMenuPacket> STREAM_CODEC = StreamCodec.ofMember(SOpenStandInItemMenuPacket::write, SOpenStandInItemMenuPacket::new);

    private final UUID accessedPlayer;

    public SOpenStandInItemMenuPacket(UUID accessedPlayer){
        this.accessedPlayer = accessedPlayer;
    }
    
    public SOpenStandInItemMenuPacket(FriendlyByteBuf buffer){
        this(buffer.readUUID());
    }

    public void write(FriendlyByteBuf buffer){
        buffer.writeUUID(accessedPlayer);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(SOpenStandInItemMenuPacket data, IPayloadContext context){
        context.enqueueWork(() -> {
            Player sender = context.player();
            Level level = sender.level();
            Player accessed = level.getPlayerByUUID(data.accessedPlayer);

            sender.openMenu(new HeldEntityInventoryProvider(accessed));
        });
    }
}
