package com.ricardthegreat.holdmetight.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import com.ricardthegreat.holdmetight.utils.sizeutils.EntitySizeUtils;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

/*
 * Fix for the slow walking animation on shrunk entities.
 *
 * Vanilla LivingEntity.updateWalkAnimation() drives the leg-swing rate from the per-tick GLOBAL
 * displacement. A small entity's global displacement is proportionally smaller than its stride,
 * so smaller entities animate progressively slower. This happens both in the normal world and on
 * Sable sub-levels, so the fix is size based and location independent.
 *
 * Normalising the incoming speed by 1/size (only when size < 1) makes a small entity's legs swing
 * at the same apparent rate as a full-size one. A 1.0x entity is left untouched.
 *
 * The injection point is the updateWalkAnimation(float) input rather than calculateEntityAnimation()
 * because Sable's @Redirect on calculateEntityAnimation swaps which delta is fed into
 * updateWalkAnimation; this mixin still applies to whatever value updateWalkAnimation receives.
 */
@Mixin(LivingEntity.class)
public abstract class LivingEntityWalkingAnimationMixin {

    @ModifyVariable(method = "updateWalkAnimation", at = @At("HEAD"), argsOnly = true)
    private float holdmetight$normaliseWalkAnimation(float speed) {
        Entity thisEnt = (Entity) (Object) this;
        float size = EntitySizeUtils.getSize(thisEnt);
        if (size > 0.0F && size < 1.0F) {
            return speed / size;
        }
        return speed;
    }
}
