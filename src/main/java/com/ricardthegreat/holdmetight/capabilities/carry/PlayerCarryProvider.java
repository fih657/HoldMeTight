package com.ricardthegreat.holdmetight.capabilities.carry;

import com.ricardthegreat.holdmetight.capabilities.ModAttachments;

import net.minecraft.world.entity.player.Player;

public class PlayerCarryProvider {

    public static PlayerCarry getPlayerCarryCapability(Player player){
        return player.getData(ModAttachments.PLAYER_CARRY);
    }
}
