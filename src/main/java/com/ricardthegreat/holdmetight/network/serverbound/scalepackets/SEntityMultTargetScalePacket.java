package com.ricardthegreat.holdmetight.network.serverbound.scalepackets;

import java.util.UUID;

import com.ricardthegreat.holdmetight.utils.sizeutils.EntitySizeUtils;
import com.ricardthegreat.holdmetight.utils.sizeutils.PlayerSizeUtils;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class SEntityMultTargetScalePacket implements CustomPacketPayload {

    public static final Type<SEntityMultTargetScalePacket> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath("holdmetight", "s_entity_mult_target_scale"));
    public static final StreamCodec<FriendlyByteBuf, SEntityMultTargetScalePacket> STREAM_CODEC = StreamCodec.ofMember(SEntityMultTargetScalePacket::write, SEntityMultTargetScalePacket::new);

    private final float scale;
    private final UUID uuid;
    private final int numericId;
    private final int ticks;
    private final boolean player;

    public SEntityMultTargetScalePacket(float scale, UUID uuid, int numericId, int ticks, boolean player){
        this.scale = scale;
        this.uuid = uuid;
        this.numericId = numericId;
        this.ticks = ticks;
        this.player = player;
    }
    
    public SEntityMultTargetScalePacket(FriendlyByteBuf buffer){
        this(buffer.readFloat(), buffer.readUUID(), buffer.readInt(), buffer.readInt(), buffer.readBoolean());
    }

    public void write(FriendlyByteBuf buffer){
        buffer.writeFloat(scale);
        buffer.writeUUID(uuid);
        buffer.writeInt(numericId);
        buffer.writeInt(ticks);
        buffer.writeBoolean(player);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(SEntityMultTargetScalePacket data, IPayloadContext context){
        context.enqueueWork(() -> {
            ServerPlayer sender = (ServerPlayer) context.player();

            if (data.player) {
                ServerPlayer target = sender.server.getPlayerList().getPlayer(data.uuid);

                if(target != null){
                    PlayerSizeUtils.multSize(sender, target, data.scale, data.ticks);
                }
            }else{
                Entity target = sender.level().getEntity(data.numericId);

                if (target != null && target.getUUID().compareTo(data.uuid) == 0) {
                    EntitySizeUtils.multSize(sender, target, data.scale, data.ticks);
                }
            }
        });
    }
}
