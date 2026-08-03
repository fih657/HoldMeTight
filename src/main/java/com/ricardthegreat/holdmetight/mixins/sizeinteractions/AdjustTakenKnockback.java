package com.ricardthegreat.holdmetight.mixins.sizeinteractions;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import com.llamalad7.mixinextras.sugar.Local;
import com.ricardthegreat.holdmetight.utils.sizeutils.EntitySizeUtils;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.damagesource.DamageSource;

@Mixin(LivingEntity.class)
public class AdjustTakenKnockback {
    
    @ModifyArg(method = "hurt(Lnet/minecraft/world/damagesource/DamageSource;F)Z", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;knockback(DDD)V"), index = 0)
    private double modifyStrength(double strength, @Local DamageSource source){
        Entity entity = source.getEntity();
        if (entity == null) {
            return strength;
        }
        float scale = EntitySizeUtils.getSize(entity);
        scale = (float) Math.pow(scale, 0.6);
        return strength*scale;
    }
}
