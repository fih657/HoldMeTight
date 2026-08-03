package com.ricardthegreat.holdmetight.network;

import java.util.function.Supplier;

import com.ricardthegreat.holdmetight.HoldMeTight;
import com.ricardthegreat.holdmetight.network.clientbound.CThrowEntityPacket;
import com.ricardthegreat.holdmetight.network.clientbound.CThrowPlayerPacket;
import com.ricardthegreat.holdmetight.network.clientbound.capabilitySync.carry.CAddPlayerCarrySyncPacket;
import com.ricardthegreat.holdmetight.network.clientbound.capabilitySync.carry.CPlayerCarrySimplePacket;
import com.ricardthegreat.holdmetight.network.clientbound.capabilitySync.carry.CPlayerCarrySyncPacket;
import com.ricardthegreat.holdmetight.network.clientbound.capabilitySync.carry.CPlayerDismountPlayerPacket;
import com.ricardthegreat.holdmetight.network.clientbound.capabilitySync.carry.CRemovePlayerCarrySyncPacket;
import com.ricardthegreat.holdmetight.network.clientbound.capabilitySync.preferences.CPlayerPreferencesSyncPacket;
import com.ricardthegreat.holdmetight.network.clientbound.capabilitySync.size.CPlayerSizeMixinSyncPacket;
import com.ricardthegreat.holdmetight.network.clientbound.carrypositions.CAddCustomCarryPosPacket;
import com.ricardthegreat.holdmetight.network.clientbound.carrypositions.CEditCustomCarryPosPacket;
import com.ricardthegreat.holdmetight.network.clientbound.carrypositions.CRemoveCustomCarryPosPacket;
import com.ricardthegreat.holdmetight.network.serverbound.SEntityPutDownPacket;
import com.ricardthegreat.holdmetight.network.serverbound.SPlayerPutDownPacket;
import com.ricardthegreat.holdmetight.network.serverbound.SSizeRaySync;
import com.ricardthegreat.holdmetight.network.serverbound.capabilitySync.carry.SPlayerCarrySimplePacket;
import com.ricardthegreat.holdmetight.network.serverbound.capabilitySync.carry.SPlayerCarrySyncPacket;
import com.ricardthegreat.holdmetight.network.serverbound.capabilitySync.preferences.SPlayerPreferencesSyncPacket;
import com.ricardthegreat.holdmetight.network.serverbound.capabilitySync.size.SPlayerSizeMixinSyncPacket;
import com.ricardthegreat.holdmetight.network.serverbound.carrypositions.SAddCustomCarryPosPacket;
import com.ricardthegreat.holdmetight.network.serverbound.carrypositions.SEditCustomCarryPosPacket;
import com.ricardthegreat.holdmetight.network.serverbound.carrypositions.SRemoveCustomCarryPosPacket;
import com.ricardthegreat.holdmetight.network.serverbound.itempackets.standinitem.SApplyPlayerEffectPacket;
import com.ricardthegreat.holdmetight.network.serverbound.itempackets.standinitem.SFeedPlayerPacket;
import com.ricardthegreat.holdmetight.network.serverbound.itempackets.standinitem.SOpenStandInItemMenuPacket;
import com.ricardthegreat.holdmetight.network.serverbound.itempackets.standinitem.SPlayerDropItemPacket;
import com.ricardthegreat.holdmetight.network.serverbound.scalepackets.SEntityAddTargetScalePacket;
import com.ricardthegreat.holdmetight.network.serverbound.scalepackets.SEntityMultTargetScalePacket;
import com.ricardthegreat.holdmetight.network.serverbound.scalepackets.SEntitySetTargetScalePacket;

import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

public class PacketHandler {

    private static final String PROTOCOL_VERSION = "1";

    public static void register(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar(HoldMeTight.MODID).versioned(PROTOCOL_VERSION);

        //serverbound
        registrar.playToServer(
            SEntityMultTargetScalePacket.TYPE,
            SEntityMultTargetScalePacket.STREAM_CODEC,
            SEntityMultTargetScalePacket::handle);

        registrar.playToServer(
            SEntitySetTargetScalePacket.TYPE,
            SEntitySetTargetScalePacket.STREAM_CODEC,
            SEntitySetTargetScalePacket::handle);

        registrar.playToServer(
            SEntityAddTargetScalePacket.TYPE,
            SEntityAddTargetScalePacket.STREAM_CODEC,
            SEntityAddTargetScalePacket::handle);

        registrar.playToServer(
            SPlayerCarrySyncPacket.TYPE,
            SPlayerCarrySyncPacket.STREAM_CODEC,
            SPlayerCarrySyncPacket::handle);

        registrar.playToServer(
            SPlayerCarrySimplePacket.TYPE,
            SPlayerCarrySimplePacket.STREAM_CODEC,
            SPlayerCarrySimplePacket::handle);

        registrar.playToServer(
            SEntityPutDownPacket.TYPE,
            SEntityPutDownPacket.STREAM_CODEC,
            SEntityPutDownPacket::handle);

        registrar.playToServer(
            SPlayerPutDownPacket.TYPE,
            SPlayerPutDownPacket.STREAM_CODEC,
            SPlayerPutDownPacket::handle);

        registrar.playToServer(
            SSizeRaySync.TYPE,
            SSizeRaySync.STREAM_CODEC,
            SSizeRaySync::handle);

        registrar.playToServer(
            SPlayerSizeMixinSyncPacket.TYPE,
            SPlayerSizeMixinSyncPacket.STREAM_CODEC,
            SPlayerSizeMixinSyncPacket::handle);

        registrar.playToServer(
            SPlayerPreferencesSyncPacket.TYPE,
            SPlayerPreferencesSyncPacket.STREAM_CODEC,
            SPlayerPreferencesSyncPacket::handle);

        registrar.playToServer(
            SAddCustomCarryPosPacket.TYPE,
            SAddCustomCarryPosPacket.STREAM_CODEC,
            SAddCustomCarryPosPacket::handle);

        registrar.playToServer(
            SRemoveCustomCarryPosPacket.TYPE,
            SRemoveCustomCarryPosPacket.STREAM_CODEC,
            SRemoveCustomCarryPosPacket::handle);

        registrar.playToServer(
            SEditCustomCarryPosPacket.TYPE,
            SEditCustomCarryPosPacket.STREAM_CODEC,
            SEditCustomCarryPosPacket::handle);

        registrar.playToServer(
            SOpenStandInItemMenuPacket.TYPE,
            SOpenStandInItemMenuPacket.STREAM_CODEC,
            SOpenStandInItemMenuPacket::handle);

        registrar.playToServer(
            SApplyPlayerEffectPacket.TYPE,
            SApplyPlayerEffectPacket.STREAM_CODEC,
            SApplyPlayerEffectPacket::handle);

        registrar.playToServer(
            SFeedPlayerPacket.TYPE,
            SFeedPlayerPacket.STREAM_CODEC,
            SFeedPlayerPacket::handle);

        registrar.playToServer(
            SPlayerDropItemPacket.TYPE,
            SPlayerDropItemPacket.STREAM_CODEC,
            SPlayerDropItemPacket::handle);

        //clientbound
        registrar.playToClient(
            CPlayerDismountPlayerPacket.TYPE,
            CPlayerDismountPlayerPacket.STREAM_CODEC,
            CPlayerDismountPlayerPacket::handle);

        registrar.playToClient(
            CPlayerCarrySyncPacket.TYPE,
            CPlayerCarrySyncPacket.STREAM_CODEC,
            CPlayerCarrySyncPacket::handle);

        registrar.playToClient(
            CPlayerCarrySimplePacket.TYPE,
            CPlayerCarrySimplePacket.STREAM_CODEC,
            CPlayerCarrySimplePacket::handle);

        registrar.playToClient(
            CPlayerSizeMixinSyncPacket.TYPE,
            CPlayerSizeMixinSyncPacket.STREAM_CODEC,
            CPlayerSizeMixinSyncPacket::handle);

        registrar.playToClient(
            CAddPlayerCarrySyncPacket.TYPE,
            CAddPlayerCarrySyncPacket.STREAM_CODEC,
            CAddPlayerCarrySyncPacket::handle);

        registrar.playToClient(
            CRemovePlayerCarrySyncPacket.TYPE,
            CRemovePlayerCarrySyncPacket.STREAM_CODEC,
            CRemovePlayerCarrySyncPacket::handle);

        registrar.playToClient(
            CAddCustomCarryPosPacket.TYPE,
            CAddCustomCarryPosPacket.STREAM_CODEC,
            CAddCustomCarryPosPacket::handle);

        registrar.playToClient(
            CRemoveCustomCarryPosPacket.TYPE,
            CRemoveCustomCarryPosPacket.STREAM_CODEC,
            CRemoveCustomCarryPosPacket::handle);

        registrar.playToClient(
            CEditCustomCarryPosPacket.TYPE,
            CEditCustomCarryPosPacket.STREAM_CODEC,
            CEditCustomCarryPosPacket::handle);

        registrar.playToClient(
            CThrowPlayerPacket.TYPE,
            CThrowPlayerPacket.STREAM_CODEC,
            CThrowPlayerPacket::handle);

        registrar.playToClient(
            CThrowEntityPacket.TYPE,
            CThrowEntityPacket.STREAM_CODEC,
            CThrowEntityPacket::handle);

        registrar.playToClient(
            CPlayerPreferencesSyncPacket.TYPE,
            CPlayerPreferencesSyncPacket.STREAM_CODEC,
            CPlayerPreferencesSyncPacket::handle);
    }

    public static void sendToServer(Object msg){
        HoldMeTight.LOGGER.debug("PacketHandler sending packet to server");
        PacketDistributor.sendToServer((CustomPacketPayload) msg);
    }

    //send directly to player
    public static void sendToPlayer(Object msg, Supplier<ServerPlayer> player) {
        HoldMeTight.LOGGER.debug("PacketHandler sending packet to player:" + player.get().getName());
        PacketDistributor.sendToPlayer(player.get(), (CustomPacketPayload) msg);
    }

    public static void sendToAllClients(Object msg){
        HoldMeTight.LOGGER.debug("PacketHandler sending packet to all players");
        PacketDistributor.sendToAllPlayers((CustomPacketPayload) msg);
    }

}
