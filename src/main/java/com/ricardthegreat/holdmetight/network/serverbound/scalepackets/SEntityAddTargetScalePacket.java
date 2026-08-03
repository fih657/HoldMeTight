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

public class SEntityAddTargetScalePacket implements CustomPacketPayload {

    public static final Type<SEntityAddTargetScalePacket> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath("holdmetight", "s_entity_add_target_scale"));
    public static final StreamCodec<FriendlyByteBuf, SEntityAddTargetScalePacket> STREAM_CODEC = StreamCodec.ofMember(SEntityAddTargetScalePacket::write, SEntityAddTargetScalePacket::new);

    private final float scale;
    private final UUID uuid;
    private final int numericId;
    private final boolean player;

    // for scaleType 0 - sets target,  1- mults target
    //probably gonna add more, not sure on the default yet maybe just setting to 1
    public SEntityAddTargetScalePacket(float scale, UUID uuid, int numericId, boolean player){
        this.scale = scale;
        this.uuid = uuid;
        this.numericId = numericId;
        this.player = player;
    }
    
    public SEntityAddTargetScalePacket(FriendlyByteBuf buffer){
        this(buffer.readFloat(), buffer.readUUID(), buffer.readInt(), buffer.readBoolean());
    }

    public void write(FriendlyByteBuf buffer){
        buffer.writeFloat(scale);
        buffer.writeUUID(uuid);
        buffer.writeInt(numericId);
        buffer.writeBoolean(player);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(SEntityAddTargetScalePacket data, IPayloadContext context){
        context.enqueueWork(() -> {
            ServerPlayer sender = (ServerPlayer) context.player();

            if (data.player) {
                ServerPlayer target = sender.server.getPlayerList().getPlayer(data.uuid);

                if(target != null){
                    PlayerSizeUtils.addSize(sender, target, data.scale);
                }
            }else{
                Entity target = sender.level().getEntity(data.numericId);

                if (target != null && target.getUUID().compareTo(data.uuid) == 0) {
                    EntitySizeUtils.addSize(sender, target, data.scale);
                }
            }
        });
    }
}
