package com.ricardthegreat.holdmetight.network.serverbound.capabilitySync.preferences;

import com.ricardthegreat.holdmetight.capabilities.preferences.PlayerPreferences;
import com.ricardthegreat.holdmetight.capabilities.preferences.PlayerPreferencesProvider;
import com.ricardthegreat.holdmetight.network.PacketHandler;
import com.ricardthegreat.holdmetight.network.clientbound.capabilitySync.preferences.CPlayerPreferencesSyncPacket;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class SPlayerPreferencesSyncPacket implements CustomPacketPayload {

    public static final Type<SPlayerPreferencesSyncPacket> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath("holdmetight", "s_player_preferences_sync"));
    public static final StreamCodec<FriendlyByteBuf, SPlayerPreferencesSyncPacket> STREAM_CODEC = StreamCodec.ofMember(SPlayerPreferencesSyncPacket::write, SPlayerPreferencesSyncPacket::new);

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

    public SPlayerPreferencesSyncPacket(
        float maxScale, float minScale, float defaultScale, boolean othersCanChangeYourSize, boolean youCanChangeYourSize, 
        boolean enableVaulting,
        boolean inventoryCanBeAccessed, boolean trapCarriedPlayer, boolean canBeTrappedWhileCarried, boolean canBePickedup, boolean canPickupOthers
        ){
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
    }
    
    public SPlayerPreferencesSyncPacket(FriendlyByteBuf buffer){
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
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(SPlayerPreferencesSyncPacket data, IPayloadContext context){
        context.enqueueWork(() -> {
            ServerPlayer player = (ServerPlayer) context.player();

            PlayerPreferences preferences = PlayerPreferencesProvider.getPlayerPreferencesCapability(player);
            if(preferences != null){
                preferences.updateAllSyncables(
                    data.maxScale, data.minScale, data.defaultScale, data.othersCanChangeYourSize, data.youCanChangeYourSize, 
                    data.enableVaulting,
                    data.inventoryCanBeAccessed, data.trapCarriedPlayer, data.canBeTrappedWhileCarried, data.canBePickedup, data.canPickupOthers);

                PacketHandler.sendToAllClients(
                    new CPlayerPreferencesSyncPacket(
                        data.maxScale, data.minScale, data.defaultScale, data.othersCanChangeYourSize, data.youCanChangeYourSize, 
                        data.enableVaulting,
                        data.inventoryCanBeAccessed, data.trapCarriedPlayer, data.canBeTrappedWhileCarried, data.canBePickedup, data.canPickupOthers, 
                        player.getUUID()));
            }
        });
    }
}
