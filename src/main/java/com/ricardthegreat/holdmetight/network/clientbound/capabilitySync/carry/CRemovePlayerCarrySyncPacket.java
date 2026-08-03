package com.ricardthegreat.holdmetight.network.clientbound.capabilitySync.carry;

import java.util.UUID;

import com.ricardthegreat.holdmetight.capabilities.carry.PlayerCarry;
import com.ricardthegreat.holdmetight.client.handlers.ClientPacketHandler;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class CRemovePlayerCarrySyncPacket implements CustomPacketPayload {

    public static final Type<CRemovePlayerCarrySyncPacket> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath("holdmetight", "c_remove_player_carry_sync"));
    public static final StreamCodec<FriendlyByteBuf, CRemovePlayerCarrySyncPacket> STREAM_CODEC = StreamCodec.ofMember(CRemovePlayerCarrySyncPacket::write, CRemovePlayerCarrySyncPacket::new);

    private final UUID entity;
    private final UUID uuid;

    /**
     * @param entity the entity to be removed from carry list
     * @param uuid the uuid of the player the entity is being removed from
     */
    public CRemovePlayerCarrySyncPacket(UUID entity, UUID uuid){
        this.entity = entity;
        this.uuid = uuid;
    }

    public CRemovePlayerCarrySyncPacket(FriendlyByteBuf buffer){
        this(buffer.readUUID(), buffer.readUUID());
    }

    public void write(FriendlyByteBuf buffer){
        buffer.writeUUID(entity);
        buffer.writeUUID(uuid);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(CRemovePlayerCarrySyncPacket data, IPayloadContext context){
        context.enqueueWork(() -> ClientPacketHandler.handleRemovePlayerPacket(data));
    }

    public void playerSyncablesUpdate(PlayerCarry playerCarry){
        playerCarry.removeCarriedEntity(entity);
    }

    public UUID getUuid() {
        return uuid;
    }

    public UUID getEntity() {
        return entity;
    }
}
