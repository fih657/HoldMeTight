package com.ricardthegreat.holdmetight.items.remotes;

import com.ricardthegreat.holdmetight.client.ClientHooks;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.neoforged.api.distmarker.Dist;


public class BasicSizeRemoteItem extends AbstractSizeRemoteItem {

    public BasicSizeRemoteItem(Item.Properties properties) {
        super(properties);
    }

    @Override
    protected void openScreen(Player player, InteractionHand hand) {
        ClientHooks.openBasicSizeRemoteScreen(player, hand);
    }
}
