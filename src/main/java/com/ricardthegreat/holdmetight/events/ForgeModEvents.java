package com.ricardthegreat.holdmetight.events;

import java.util.UUID;
import java.util.function.Supplier;

import com.ricardthegreat.holdmetight.HoldMeTight;
import com.ricardthegreat.holdmetight.capabilities.ModAttachments;
import com.ricardthegreat.holdmetight.capabilities.carry.PlayerCarry;
import com.ricardthegreat.holdmetight.capabilities.preferences.PlayerPreferences;
import com.ricardthegreat.holdmetight.capabilities.size.PlayerSize;
import com.ricardthegreat.holdmetight.items.EntityStandinItem;
import com.ricardthegreat.holdmetight.network.PacketHandler;
import com.ricardthegreat.holdmetight.network.clientbound.capabilitySync.carry.CRemovePlayerCarrySyncPacket;
import com.ricardthegreat.holdmetight.utils.sizeutils.EntitySizeUtils;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.item.ItemTossEvent;
import net.neoforged.neoforge.event.entity.living.LivingKnockBackEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent.PlayerRespawnEvent;

@EventBusSubscriber(modid = HoldMeTight.MODID)
public class ForgeModEvents {
    
    @SubscribeEvent
    public static void onPlayerCloned(PlayerEvent.Clone event) {
        if(event.isWasDeath()) {
            Player original = event.getOriginal();
            Player entity = event.getEntity();

            entity.setData(ModAttachments.PLAYER_SIZE, original.getData(ModAttachments.PLAYER_SIZE));
            entity.setData(ModAttachments.PLAYER_CARRY, original.getData(ModAttachments.PLAYER_CARRY));
            entity.setData(ModAttachments.PLAYER_PREFERENCES, original.getData(ModAttachments.PLAYER_PREFERENCES));
        }
    }

    @SubscribeEvent
    public static void onPlayerRespawn(PlayerRespawnEvent event){
        Player respawnPlayer = event.getEntity();
        Level level = respawnPlayer.level();
        MinecraftServer server = level.getServer();
        
        if (server != null) {
            ServerPlayer serverJoiner = server.getPlayerList().getPlayer(respawnPlayer.getUUID());
            syncPlayerCapabilities(serverJoiner, server);
        }else{
            //TODO something here if server doesnt exist maybe
        }
    }

    //when item is thrown check if it is player item, if it is then remove it and put the player it represents there with the same momentum
    @SubscribeEvent
    public static void onItemTossEvent(ItemTossEvent event){
        /*
        ItemEntity entity = event.getEntity();
        
        if (entity.getItem().getItem() instanceof EntityStandinItem) {
            Player thrower = event.getPlayer();

            ItemStack stack = entity.getItem();

            if (stack.hasTag()) {
                CompoundTag tag = stack.getTag(); 
                UUID id = tag.getUUID(EntityStandinItem.ENTITY_UUID);

                Level level = thrower.level();

                Entity thrown;
                if (tag.getBoolean(EntityStandinItem.IS_PLAYER)) {
                    thrown = level.getPlayerByUUID(id);
                }else{
                    thrown = level.getEntity(tag.getInt(EntityStandinItem.ENTITY_ID));
                }
                //Player player = thrower.level().getPlayerByUUID(id);

                if (thrown != null) {
                    thrown.stopRiding();
                    thrown.setDeltaMovement(entity.getDeltaMovement()); 
                    thrown.hurtMarked = true;
                    if (!thrower.level().isClientSide) {
                        PlayerCarry playerCarry = PlayerCarryProvider.getPlayerCarryCapability(thrower);
                        playerCarry.removeCarriedEntity(id);
                        DistExecutor.unsafeRunWhenOn(Dist.DEDICATED_SERVER, () -> () -> 
                        PacketHandler.sendToAllClients(new CRemovePlayerCarrySyncPacket(id, thrower.getUUID())));
                    }
                }
            }
        }
             */
    }

    public static void syncPlayerCapabilities(ServerPlayer serverJoiner, MinecraftServer server){
            Supplier<ServerPlayer> supplier = () -> serverJoiner;

            for (ServerPlayer player : server.getPlayerList().getPlayers()) { 
                PlayerSize size = player.getData(ModAttachments.PLAYER_SIZE);
                if (player == serverJoiner) {
                    PacketHandler.sendToAllClients(size.getSyncPacket(player));
                }else{
                    PacketHandler.sendToPlayer(size.getSyncPacket(player), supplier);
                }
                
                PlayerCarry carry = player.getData(ModAttachments.PLAYER_CARRY);
                if (player == serverJoiner) {
                    PacketHandler.sendToAllClients(carry.getClientSyncPacket(player));
                }else{
                    PacketHandler.sendToPlayer(carry.getClientSyncPacket(player), supplier);
                }

                PlayerPreferences preferences = player.getData(ModAttachments.PLAYER_PREFERENCES);
                if (player == serverJoiner) {
                    PacketHandler.sendToAllClients(preferences.getClientSyncPacket(player));
                }else{
                    PacketHandler.sendToPlayer(preferences.getClientSyncPacket(player), supplier);
                }
            }
    }

    @SubscribeEvent
    public static void onLivingKnockbackEvent(LivingKnockBackEvent event){
        float scale = EntitySizeUtils.getSize(event.getEntity());
        float strength = event.getOriginalStrength();
        HoldMeTight.LOGGER.debug("kb event strength:" + strength);
        scale = (float) Math.pow(scale, 0.6);
        strength /= scale;
        HoldMeTight.LOGGER.debug("kb event strength mod:" + strength);
        event.setStrength(strength);
    }

}
