package com.ricardthegreat.holdmetight.items;

import org.jetbrains.annotations.NotNull;

import com.mojang.datafixers.util.Pair;
import com.ricardthegreat.holdmetight.client.ClientHooks;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurioItem;
import top.theillusivec4.curios.common.inventory.CurioSlot;

public class CollarItem extends Item implements ICurioItem {

    public CollarItem(Properties properties) {
        super(properties);
    }
    
    public int getColor(ItemStack stack) {
        CompoundTag tag = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
        CompoundTag display = tag.getCompound("display");

        return display != null && display.contains("color", 99) ? display.getInt("color") : MapColor.COLOR_RED.col;
    }

    @Override
    public void curioTick(SlotContext slotContext, ItemStack stack) {
        List<MobEffectInstance> effects = getEffect(stack);
        for(int i = 0; i < effects.size(); i++){
            slotContext.entity().addEffect(effects.get(i));
        }
        ICurioItem.super.curioTick(slotContext, stack);
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slot, boolean selected) {
        if (!stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag().contains("owners")) {
            if (entity instanceof Player player) {
                setupNbt(stack, player);
            }
        }
        super.inventoryTick(stack, level, entity, slot, selected);
    }

    @Override
    public boolean canUnequip(SlotContext slotContext, ItemStack stack) {
        if (slotContext.entity() instanceof Player player && player.getAbilities().instabuild) {
            return true;
        }
        return !getIsLocked(stack);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> components, TooltipFlag flag) {
        super.appendHoverText(stack, context, components, flag);
        Pair<UUID, String> owner = getFirstOwner(stack);
        if (owner != null) {
            components.add(Component.translatable("item.holdmetight.collar_item.owner", owner.getSecond()));
        }
        boolean locked = getIsLocked(stack);
        if (locked) {
            components.add(Component.translatable("item.holdmetight.collar_item.locked").withStyle(ChatFormatting.YELLOW));
        }else{
            components.add(Component.translatable("item.holdmetight.collar_item.unlocked").withStyle(ChatFormatting.BLUE));
        }
        
    }

    @Override
    public void onCraftedBy(ItemStack stack, Level level, Player player) {
        setupNbt(stack, player);
        super.onCraftedBy(stack, level, player);
    }

    @Override
    public boolean overrideOtherStackedOnMe(ItemStack stackThis, ItemStack stackOther, Slot slot, ClickAction action, Player player, SlotAccess access) {
        if (action == ClickAction.SECONDARY) {
                if (!(slot instanceof CurioSlot)) {

                    PotionContents potionContents = stackOther.getOrDefault(DataComponents.POTION_CONTENTS, PotionContents.EMPTY);
                    if (!potionContents.equals(PotionContents.EMPTY)) {
                        setEffect(stackThis, potionContents);
                        return true;
                    }

                setLocked(stackThis);
                if (player.level().isClientSide) {
                    playSound(stackThis);
                }
                return true;
            }else if (stackOther.getItem() instanceof CollarKeyItem key) {
                Pair<UUID, String> pair = key.getOwner(stackOther);

                if (pair != null && pair.getFirst().compareTo(getFirstOwner(stackThis).getFirst()) == 0) {
                    setLocked(stackThis);
                    if (player.level().isClientSide) {
                       playSound(stackThis);
                    }
                    return true;
                }
            }
        }
        return super.overrideOtherStackedOnMe(stackThis, stackOther, slot, action, player, access);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        if (level.isClientSide()) {
            ClientHooks.openCollarScreen(player, hand);
        }
        return InteractionResultHolder.success(player.getItemInHand(hand));
    }
    
    //TODO get more than 1 owner
    public Pair<UUID, String> getFirstOwner(ItemStack stack){
        CompoundTag tag = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag().getCompound("owners");
        if (tag != null && tag.contains("uuid"+0) && tag.contains("name"+0)) {
            return new Pair<UUID,String>(tag.getUUID("uuid"+0), tag.getString("name"+0));
        }
        return null;
    }

    public List<Pair<UUID, String>> getOwners(ItemStack stack){
        CompoundTag tag = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag().getCompound("owners");

        List<Pair<UUID, String>> owners = new ArrayList<>();

        if (tag != null && tag.contains("numOwners")) {
            
            for(int i = 0; i < tag.getInt("numOwners"); i++){
                owners.add(new Pair<UUID,String>(tag.getUUID("uuid"+i), tag.getString("name"+i)));
            }

            return owners;
        }
        return null;
    }

    public void addOwner(ItemStack stack, @NotNull UUID uuid, @NotNull String name){
        CompoundTag stackTag = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
        CompoundTag tag = stackTag.getCompound("owners");
        if (!stackTag.contains("owners")) {
            stackTag.put("owners", tag);
        }

        tag.putUUID("uuid" + tag.getInt("numOwners"), uuid);
        tag.putString("name" + tag.getInt("numOwners"), name);
        tag.putInt("numOwners", tag.getInt("numOwners") + 1);

        stack.set(DataComponents.CUSTOM_DATA, CustomData.of(stackTag));
    }

    public void removeOwner(ItemStack stack, UUID uuid, String name){

    }

    public boolean getIsLocked(ItemStack stack){
        CompoundTag tag = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag().getCompound("locked");
        return tag.getBoolean("isLocked");
    }

    public void setLocked(ItemStack stack){
        CompoundTag stackTag = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
        CompoundTag tag = stackTag.getCompound("locked");
        if (!stackTag.contains("locked")) {
            stackTag.put("locked", tag);
        }
        tag.putBoolean("isLocked", !tag.getBoolean("isLocked"));
        stack.set(DataComponents.CUSTOM_DATA, CustomData.of(stackTag));
    }

    public void setEffect(ItemStack stack, PotionContents potionContents){
        CompoundTag stackTag = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
        stackTag.remove("potion");
        CompoundTag tag = new CompoundTag();
        stackTag.put("potion", tag);

        List<MobEffectInstance> effects = new ArrayList<>();
        for (MobEffectInstance effect : potionContents.getAllEffects()) {
            effects.add(effect);
        }

        tag.putInt("numEffects", effects.size());

        for(int i = 0; i < effects.size(); i++){
            tag.putString("effectId" + i, effects.get(i).getEffect().getRegisteredName());
        }
        stack.set(DataComponents.CUSTOM_DATA, CustomData.of(stackTag));
    }

    public List<MobEffectInstance> getEffect(ItemStack stack){
        CompoundTag tag = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag().getCompound("potion");
        List<MobEffectInstance> effects = new ArrayList<>();
        for(int i = 0; i < tag.getInt("numEffects"); i++){
            effects.add(new MobEffectInstance(BuiltInRegistries.MOB_EFFECT.getHolderOrThrow(ResourceKey.create(Registries.MOB_EFFECT, ResourceLocation.parse(tag.getString("effectId" + i)))), 20));
        }
        return effects;
    }

    private void setupNbt(ItemStack stack, Player player){
        CompoundTag stackTag = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
        CompoundTag tag = new CompoundTag();
        stackTag.put("locked", tag);
        tag.putBoolean("isLocked", false);
        stack.set(DataComponents.CUSTOM_DATA, CustomData.of(stackTag));

        addOwner(stack, player.getUUID(), player.getName().getString());
    }

    @OnlyIn(Dist.CLIENT)
    private void playSound(ItemStack stack){
        if (getIsLocked(stack)) {
            Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.WOODEN_PRESSURE_PLATE_CLICK_OFF, 1.0F));
        }else{
            Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
        }
    }
}
