package com.ricardthegreat.holdmetight.capabilities.size;

import com.ricardthegreat.holdmetight.capabilities.ModAttachments;

import net.minecraft.world.entity.player.Player;

public class PlayerSizeProvider {

    //TODO make it so the only file that references this is the playersizeutils file
    public static PlayerSize getPlayerSizeCapability(Player player){
        return player.getData(ModAttachments.PLAYER_SIZE);
    }
}
