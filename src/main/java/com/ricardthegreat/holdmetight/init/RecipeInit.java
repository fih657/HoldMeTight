package com.ricardthegreat.holdmetight.init;

import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionBrewing;
import net.minecraft.world.item.alchemy.Potions;

public class RecipeInit {
    
    public static void register(PotionBrewing.Builder builder){
        //Ingredient.of(PotionUtils.setPotion(new ItemStack(Items.POTION), Potions.AWKWARD));
        //Ingredient.of(new ItemStack(Items.FLOWERING_AZALEA));
        //PotionUtils.setPotion(new ItemStack(Items.POTION), PotionsInit.SHRINK_POTION);
         
        //shrinking pots
        builder.addMix(Potions.AWKWARD, Items.FLOWERING_AZALEA, PotionsInit.SHRINK_POTION);
        builder.addMix(PotionsInit.SHRINK_POTION, Items.GLOWSTONE_DUST, PotionsInit.SHRINK_POTION_1);
        builder.addMix(PotionsInit.SHRINK_POTION_1, Items.GLOWSTONE_DUST, PotionsInit.SHRINK_POTION_2);
        builder.addMix(PotionsInit.SHRINK_POTION_2, Items.GLOWSTONE_DUST, PotionsInit.SHRINK_POTION_3);
        builder.addMix(PotionsInit.SHRINK_POTION_3, Items.GLOWSTONE_DUST, PotionsInit.SHRINK_POTION_4);

        builder.addMix(PotionsInit.SHRINK_POTION, Items.REDSTONE, PotionsInit.SHRINK_POTION_LONG);
        builder.addMix(PotionsInit.SHRINK_POTION_1, Items.REDSTONE, PotionsInit.SHRINK_POTION_1_LONG);
        builder.addMix(PotionsInit.SHRINK_POTION_2, Items.REDSTONE, PotionsInit.SHRINK_POTION_2_LONG);
        builder.addMix(PotionsInit.SHRINK_POTION_3, Items.REDSTONE, PotionsInit.SHRINK_POTION_3_LONG);
        builder.addMix(PotionsInit.SHRINK_POTION_4, Items.REDSTONE, PotionsInit.SHRINK_POTION_4_LONG);

        builder.addMix(PotionsInit.SHRINK_POTION_LONG, Items.GLOWSTONE_DUST, PotionsInit.SHRINK_POTION_1_LONG);
        builder.addMix(PotionsInit.SHRINK_POTION_1_LONG, Items.GLOWSTONE_DUST, PotionsInit.SHRINK_POTION_2_LONG);
        builder.addMix(PotionsInit.SHRINK_POTION_2_LONG, Items.GLOWSTONE_DUST, PotionsInit.SHRINK_POTION_3_LONG);
        builder.addMix(PotionsInit.SHRINK_POTION_3_LONG, Items.GLOWSTONE_DUST, PotionsInit.SHRINK_POTION_4_LONG);

        //growth pots
        builder.addMix(Potions.AWKWARD, Items.AMETHYST_SHARD, PotionsInit.GROW_POTION);
        builder.addMix(PotionsInit.GROW_POTION, Items.GLOWSTONE_DUST, PotionsInit.GROW_POTION_1);
        builder.addMix(PotionsInit.GROW_POTION_1, Items.GLOWSTONE_DUST, PotionsInit.GROW_POTION_2);
        builder.addMix(PotionsInit.GROW_POTION_2, Items.GLOWSTONE_DUST, PotionsInit.GROW_POTION_3);
        builder.addMix(PotionsInit.GROW_POTION_3, Items.GLOWSTONE_DUST, PotionsInit.GROW_POTION_4);

        builder.addMix(PotionsInit.GROW_POTION, Items.REDSTONE, PotionsInit.GROW_POTION_LONG);
        builder.addMix(PotionsInit.GROW_POTION_1, Items.REDSTONE, PotionsInit.GROW_POTION_1_LONG);
        builder.addMix(PotionsInit.GROW_POTION_2, Items.REDSTONE, PotionsInit.GROW_POTION_2_LONG);
        builder.addMix(PotionsInit.GROW_POTION_3, Items.REDSTONE, PotionsInit.GROW_POTION_3_LONG);
        builder.addMix(PotionsInit.GROW_POTION_4, Items.REDSTONE, PotionsInit.GROW_POTION_4_LONG);

        builder.addMix(PotionsInit.GROW_POTION_LONG, Items.GLOWSTONE_DUST, PotionsInit.GROW_POTION_1_LONG);
        builder.addMix(PotionsInit.GROW_POTION_1_LONG, Items.GLOWSTONE_DUST, PotionsInit.GROW_POTION_2_LONG);
        builder.addMix(PotionsInit.GROW_POTION_2_LONG, Items.GLOWSTONE_DUST, PotionsInit.GROW_POTION_3_LONG);
        builder.addMix(PotionsInit.GROW_POTION_3_LONG, Items.GLOWSTONE_DUST, PotionsInit.GROW_POTION_4_LONG);

        
        //super shrink pots
        builder.addMix(PotionsInit.SHRINK_POTION_4, Items.CHERRY_SAPLING, PotionsInit.MASSIVE_SHRINK_POTION);
        builder.addMix(PotionsInit.SHRINK_POTION_4_LONG, Items.CHERRY_SAPLING, PotionsInit.MASSIVE_SHRINK_POTION_LONG);
      
    }
}
