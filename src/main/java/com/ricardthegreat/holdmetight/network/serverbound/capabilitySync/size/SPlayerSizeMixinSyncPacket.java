package com.ricardthegreat.holdmetight.network.serverbound.capabilitySync.size;

import java.util.UUID;

import com.ricardthegreat.holdmetight.capabilities.size.PlayerSize;
import com.ricardthegreat.holdmetight.capabilities.size.PlayerSizeProvider;
import com.ricardthegreat.holdmetight.network.PacketHandler;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class SPlayerSizeMixinSyncPacket implements CustomPacketPayload {

    public static final Type<SPlayerSizeMixinSyncPacket> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath("holdmetight", "s_player_size_mixin_sync"));
    public static final StreamCodec<FriendlyByteBuf, SPlayerSizeMixinSyncPacket> STREAM_CODEC = StreamCodec.ofMember(SPlayerSizeMixinSyncPacket::write, SPlayerSizeMixinSyncPacket::new);

    private final float maxScale;
    private final float minScale;
    private final float defaultScale;
    private final UUID uuid;

    public SPlayerSizeMixinSyncPacket(float maxScale, float minScale, float defaultScale, UUID uuid){
        this.maxScale = maxScale;
        this.minScale = minScale;
        this.defaultScale = defaultScale;
        this.uuid = uuid;
    }

    public SPlayerSizeMixinSyncPacket(FriendlyByteBuf buffer){
        this(buffer.readFloat(), buffer.readFloat(), buffer.readFloat(), buffer.readUUID());
    }

    public void write(FriendlyByteBuf buffer){
        buffer.writeFloat(maxScale);
        buffer.writeFloat(minScale);
        buffer.writeFloat(defaultScale);
        buffer.writeUUID(uuid);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(SPlayerSizeMixinSyncPacket data, IPayloadContext context){
        context.enqueueWork(() -> {
            ServerPlayer player = (ServerPlayer) context.player();
        
            ServerPlayer target = player.server.getPlayerList().getPlayer(data.uuid);

            if(target != null){
                PlayerSize playerSize = PlayerSizeProvider.getPlayerSizeCapability(player);

                if (playerSize != null) {
                    playerSize.updateSyncables(data.maxScale, data.minScale, data.defaultScale);
                    PacketHandler.sendToAllClients(playerSize.getSyncPacket(player));
                }
            }
        });
    }
}
