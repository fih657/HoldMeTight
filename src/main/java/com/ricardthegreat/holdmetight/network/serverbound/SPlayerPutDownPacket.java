package com.ricardthegreat.holdmetight.network.serverbound;

import java.util.UUID;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class SPlayerPutDownPacket implements CustomPacketPayload {

    public static final Type<SPlayerPutDownPacket> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath("holdmetight", "s_player_put_down"));
    public static final StreamCodec<FriendlyByteBuf, SPlayerPutDownPacket> STREAM_CODEC = StreamCodec.ofMember(SPlayerPutDownPacket::write, SPlayerPutDownPacket::new);

    private final UUID uuid;
    private final Vec3 pos;

    public SPlayerPutDownPacket(UUID uuid, Vec3 pos){
        this.uuid = uuid;
        this.pos = pos;
    }

    public SPlayerPutDownPacket(FriendlyByteBuf buffer){
        this(buffer.readUUID(), new Vec3(buffer.readDouble(), buffer.readDouble(), buffer.readDouble()));
    }

    public void write(FriendlyByteBuf buffer){
        buffer.writeUUID(uuid);
        buffer.writeDouble(pos.x);
        buffer.writeDouble(pos.y);
        buffer.writeDouble(pos.z);
    }

    public UUID getUuid(){
        return this.uuid;
    }

    public Vec3 getVec3(){
        return this.pos;
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(SPlayerPutDownPacket data, IPayloadContext context){
        context.enqueueWork(() -> {
            ServerPlayer player = (ServerPlayer) context.player();
            ServerPlayer target = player.server.getPlayerList().getPlayer(data.uuid);

            if (target != null) {
                target.dismountTo(data.pos.x, data.pos.y, data.pos.z);
            }
        });
    }
}
