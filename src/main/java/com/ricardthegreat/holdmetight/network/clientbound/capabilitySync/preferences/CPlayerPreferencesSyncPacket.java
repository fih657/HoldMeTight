package com.ricardthegreat.holdmetight.network.clientbound.capabilitySync.preferences;

import java.util.UUID;

import com.ricardthegreat.holdmetight.capabilities.preferences.PlayerPreferences;
import com.ricardthegreat.holdmetight.client.handlers.ClientPacketHandler;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class CPlayerPreferencesSyncPacket implements CustomPacketPayload {

    public static final Type<CPlayerPreferencesSyncPacket> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath("holdmetight", "c_player_preferences_sync"));
    public static final StreamCodec<FriendlyByteBuf, CPlayerPreferencesSyncPacket> STREAM_CODEC = StreamCodec.ofMember(CPlayerPreferencesSyncPacket::write, CPlayerPreferencesSyncPacket::new);

    private final float maxScale;
    private final float minScale;
    private final float defaultScale;
    private final boolean othersCanChangeYourSize;
    private final boolean youCanChangeYourSize;

    private final boolean enableVaulting;

    private final boolean inventoryCanBeAccessed;
    private final boolean trapCarriedPlayer;
    private final boolean canBeTrappedWhileCarried;
    private final boolean canBePickedup;
    private final boolean canPickupOthers;

    private final UUID uuid;

    public CPlayerPreferencesSyncPacket(
        float maxScale, float minScale, float defaultScale, boolean othersCanChangeYourSize, boolean youCanChangeYourSize, 
        boolean enableVaulting,
        boolean inventoryCanBeAccessed, boolean trapCarriedPlayer, boolean canBeTrappedWhileCarried, boolean canBePickedup, boolean canPickupOthers, 
        UUID uuid){
        this.maxScale = maxScale;
        this.minScale = minScale;
        this.defaultScale = defaultScale;
        this.othersCanChangeYourSize = othersCanChangeYourSize;
        this.youCanChangeYourSize = youCanChangeYourSize;

        this.enableVaulting = enableVaulting;

        this.inventoryCanBeAccessed = inventoryCanBeAccessed;
        this.trapCarriedPlayer = trapCarriedPlayer;
        this.canBeTrappedWhileCarried = canBeTrappedWhileCarried;
        this.canBePickedup = canBePickedup;
        this.canPickupOthers = canPickupOthers;

        this.uuid = uuid;
    }

    public CPlayerPreferencesSyncPacket(FriendlyByteBuf buffer){
        this.maxScale = buffer.readFloat();
        this.minScale = buffer.readFloat();
        this.defaultScale = buffer.readFloat();
        this.othersCanChangeYourSize = buffer.readBoolean();
        this.youCanChangeYourSize = buffer.readBoolean();

        this.enableVaulting = buffer.readBoolean();

        this.inventoryCanBeAccessed = buffer.readBoolean();
        this.trapCarriedPlayer = buffer.readBoolean();
        this.canBeTrappedWhileCarried = buffer.readBoolean();
        this.canBePickedup = buffer.readBoolean();
        this.canPickupOthers = buffer.readBoolean();

        this.uuid = buffer.readUUID();
    }

    public void write(FriendlyByteBuf buffer){
        buffer.writeFloat(maxScale);
        buffer.writeFloat(minScale);
        buffer.writeFloat(defaultScale);
        buffer.writeBoolean(othersCanChangeYourSize);
        buffer.writeBoolean(youCanChangeYourSize);
        
        buffer.writeBoolean(enableVaulting);

        buffer.writeBoolean(inventoryCanBeAccessed);
        buffer.writeBoolean(trapCarriedPlayer);
        buffer.writeBoolean(canBeTrappedWhileCarried);
        buffer.writeBoolean(canBePickedup);
        buffer.writeBoolean(canPickupOthers);

        buffer.writeUUID(uuid);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(CPlayerPreferencesSyncPacket data, IPayloadContext context){
        context.enqueueWork(() -> ClientPacketHandler.handlePreferencesPacket(data));
    }

    public void playerSyncablesUpdate(PlayerPreferences preferences){
        preferences.updateAllSyncables(
                maxScale, minScale, defaultScale, othersCanChangeYourSize, youCanChangeYourSize,
                enableVaulting, 
                inventoryCanBeAccessed, trapCarriedPlayer, canBeTrappedWhileCarried, canBePickedup, canPickupOthers);
    }

    public UUID getUuid() {
        return uuid;
    }
}
