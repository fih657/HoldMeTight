package com.ricardthegreat.holdmetight.capabilities.preferences;

import com.ricardthegreat.holdmetight.capabilities.ModAttachments;

import net.minecraft.world.entity.player.Player;

public class PlayerPreferencesProvider {

    public static PlayerPreferences getPlayerPreferencesCapability(Player player){
        return player.getData(ModAttachments.PLAYER_PREFERENCES);
    }
}
