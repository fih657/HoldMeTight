package com.ricardthegreat.holdmetight.utils;

import net.minecraft.core.Holder;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.core.component.DataComponents;
import net.neoforged.neoforge.common.brewing.IBrewingRecipe;

// BetterBrewingRecipe Class by CAS-ual-TY from https://github.com/CAS-ual-TY/Extra-Potions (MIT License)
// https://github.com/CAS-ual-TY/Extra-Potions/blob/main/LICENSE



//not actually using this file but keeping it around in case i need it
public class BetterBrewingRecipe implements IBrewingRecipe {
    private final Holder<Potion> input;
    private final Item ingredient;
    private final Holder<Potion> output;

    public BetterBrewingRecipe(Holder<Potion> input, Item ingredient, Holder<Potion> output) {
        this.input = input;
        this.ingredient = ingredient;
        this.output = output;
    }

    @Override
    public boolean isInput(ItemStack input) {
        PotionContents potionContents = input.getOrDefault(DataComponents.POTION_CONTENTS, PotionContents.EMPTY);
        return potionContents.is(this.input);
    }

    @Override
    public boolean isIngredient(ItemStack ingredient) {
        return ingredient.getItem() == this.ingredient;
    }

    @Override
    public ItemStack getOutput(ItemStack input, ItemStack ingredient) {
        if(!this.isInput(input) || !this.isIngredient(ingredient)) {
            return ItemStack.EMPTY;
        }

        ItemStack itemStack = new ItemStack(input.getItem());
        itemStack.set(DataComponents.POTION_CONTENTS, new PotionContents(this.output));
        return itemStack;
    }
}