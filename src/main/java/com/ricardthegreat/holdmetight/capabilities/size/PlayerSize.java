package com.ricardthegreat.holdmetight.capabilities.size;

import com.ricardthegreat.holdmetight.network.PacketHandler;
import com.ricardthegreat.holdmetight.network.clientbound.capabilitySync.size.CPlayerSizeMixinSyncPacket;
import com.ricardthegreat.holdmetight.network.serverbound.capabilitySync.size.SPlayerSizeMixinSyncPacket;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;

import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.common.util.INBTSerializable;

public class PlayerSize implements INBTSerializable<CompoundTag> {

    //TODO remove this class if i find no use for it

    @Override
    public CompoundTag serializeNBT(HolderLookup.Provider provider) {
        CompoundTag tag = new CompoundTag();
        saveNBTData(tag);
        return tag;
    }

    @Override
    public void deserializeNBT(HolderLookup.Provider provider, CompoundTag tag) {
        loadNBTData(tag);
    }

    public void updateSyncables(float maxScale, float minScale, float defaultScale){

    }

    public CPlayerSizeMixinSyncPacket getSyncPacket(Player player){
        return new CPlayerSizeMixinSyncPacket(player.getUUID());
    }

    public void copy(PlayerSize source){
    }

    public void saveNBTData(CompoundTag tag){
    }

    public void loadNBTData(CompoundTag tag){
    }
}
