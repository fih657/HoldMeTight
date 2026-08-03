package com.ricardthegreat.holdmetight.network.serverbound;

import java.util.UUID;

import com.ricardthegreat.holdmetight.HMTConfig;
import com.ricardthegreat.holdmetight.utils.compat.SableCompat;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class SEntityPutDownPacket implements CustomPacketPayload {

    public static final Type<SEntityPutDownPacket> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath("holdmetight", "s_entity_put_down"));
    public static final StreamCodec<FriendlyByteBuf, SEntityPutDownPacket> STREAM_CODEC = StreamCodec.ofMember(SEntityPutDownPacket::write, SEntityPutDownPacket::new);

    private final UUID uuid;
    private final Vec3 pos;

    public SEntityPutDownPacket(UUID uuid, Vec3 pos){
        this.uuid = uuid;
        this.pos = pos;
    }

    public SEntityPutDownPacket(FriendlyByteBuf buffer){
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

    public static void handle(SEntityPutDownPacket data, IPayloadContext context){
        context.enqueueWork(() -> {
            ServerPlayer player = (ServerPlayer) context.player();
            Entity target = null;

            for(Entity entity : player.getPassengers()){
                //need some check where if none of them are correct then do a failsafe
                if (entity.getUUID().compareTo(data.uuid) == 0) {
                    target = entity;
                }
            }

            if (target != null) {
                target.stopRiding();

                target.dismountTo(data.pos.x, data.pos.y, data.pos.z);

                if (HMTConfig.SERVER_CONFIG.isSableCarryPlaceFixEnabled()) {
                    SableCompat.rebindCarriedEntityToSubLevel(target);
                }
            }
        });
    }
}
