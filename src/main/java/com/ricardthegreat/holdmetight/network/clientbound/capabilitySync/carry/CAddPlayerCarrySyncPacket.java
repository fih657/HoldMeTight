package com.ricardthegreat.holdmetight.network.clientbound.capabilitySync.carry;

import java.util.UUID;

import com.ricardthegreat.holdmetight.capabilities.carry.PlayerCarry;
import com.ricardthegreat.holdmetight.client.handlers.ClientPacketHandler;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class CAddPlayerCarrySyncPacket implements CustomPacketPayload {

    public static final Type<CAddPlayerCarrySyncPacket> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath("holdmetight", "c_add_player_carry_sync"));
    public static final StreamCodec<FriendlyByteBuf, CAddPlayerCarrySyncPacket> STREAM_CODEC = StreamCodec.ofMember(CAddPlayerCarrySyncPacket::write, CAddPlayerCarrySyncPacket::new);

    private final CompoundTag entity;
    private final UUID uuid;

    public CAddPlayerCarrySyncPacket(CompoundTag entity, UUID uuid){
        this.entity = entity;
        this.uuid = uuid;
    }

    public CAddPlayerCarrySyncPacket(FriendlyByteBuf buffer){
        this(buffer.readNbt(), buffer.readUUID());
    }

    public void write(FriendlyByteBuf buffer){
        buffer.writeNbt(entity);
        buffer.writeUUID(uuid);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(CAddPlayerCarrySyncPacket data, IPayloadContext context){
        context.enqueueWork(() -> ClientPacketHandler.handleAddEntityPacket(data));
    }

    public void playerSyncablesUpdate(PlayerCarry playerCarry){
        playerCarry.addOrUpdateCarriedEntity(entity);
    }

    public UUID getUuid() {
        return uuid;
    }
}
