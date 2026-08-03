package com.ricardthegreat.holdmetight.mixins.carry;

import org.apache.commons.lang3.tuple.Triple;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.ricardthegreat.holdmetight.capabilities.carry.CarryPosition;
import com.ricardthegreat.holdmetight.capabilities.carry.PlayerCarry;
import com.ricardthegreat.holdmetight.capabilities.carry.PlayerCarryProvider;
import com.ricardthegreat.holdmetight.capabilities.preferences.PlayerPreferences;
import com.ricardthegreat.holdmetight.capabilities.preferences.PlayerPreferencesProvider;
import com.ricardthegreat.holdmetight.utils.CheckNonInvSlotUtil;
import com.ricardthegreat.holdmetight.utils.carry.carryPreferencesChecker;
import com.ricardthegreat.holdmetight.utils.sizeutils.EntitySizeUtils;
import com.ricardthegreat.holdmetight.utils.sizeutils.PlayerSizeUtils;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

@Mixin(Player.class)
public abstract class PlayerDismountMixin extends LivingEntity{
    protected PlayerDismountMixin(EntityType<? extends LivingEntity> p_20966_, Level p_20967_) {super(p_20966_, p_20967_);}

    @Inject(at = @At("HEAD"), method = "wantsToStopRiding()Z", cancellable = true)
    protected void wantsToStopRiding(CallbackInfoReturnable<Boolean> info) {
        Player thisP = ((Player)(Object)this);
        if (!carryPreferencesChecker.canStopRiding(thisP, thisP.getVehicle())) {
            info.setReturnValue(false);
        }
    }

    @Override
    public Vec3 getDismountLocationForPassenger(LivingEntity ent) {
        //Triple<Double, Double, Double> triple = calcBodyPosition((Player) (Object) this, ent);
        //Vec3 pos = new Vec3(this.getX() + triple.getLeft(), triple.getMiddle(), this.getZ() + triple.getRight());
        return ent.position();
    }

}
