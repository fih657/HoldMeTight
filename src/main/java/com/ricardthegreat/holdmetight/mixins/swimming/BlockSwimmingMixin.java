package com.ricardthegreat.holdmetight.mixins.swimming;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.ricardthegreat.holdmetight.utils.IBlockSwimming;

import net.minecraft.world.entity.Entity;
import net.neoforged.neoforge.common.extensions.IEntityExtension;
import net.neoforged.neoforge.fluids.FluidType;

@Mixin(Entity.class)
public abstract class BlockSwimmingMixin implements IBlockSwimming, IEntityExtension{

    private boolean wasInBlock = false;
    private boolean isInBlock = false;

    @Inject(at = @At("HEAD"), method = "tick()V", cancellable = true)
    public void tick(CallbackInfo info) {
        if (isInBlock) {
            isInBlock = false;
            wasInBlock = true;
        }else{
            wasInBlock = false;
        }

        
    }

    @Inject(at = @At("RETURN"), method = "isInWater()Z", cancellable = true)
    public void isInWater(CallbackInfoReturnable<Boolean> info) {
        if (wasInBlock) {
            info.setReturnValue(true);
        }
    }

    // Entity#getFluidTypeHeight is final in NeoForge, so this needs a mixin
    // instead of the @Override the 1.20.1 Forge version used.
    // getFluidHeight(WATER) routes through getFluidTypeHeight(WATER_TYPE), and
    // LivingEntity's jump-in-water check requires it to be > 0 to trigger
    // jumpInLiquid() (space to rise while swimming).
    @Inject(at = @At("RETURN"), method = "getFluidTypeHeight(Lnet/neoforged/neoforge/fluids/FluidType;)D", cancellable = true)
    public void getFluidTypeHeight(FluidType type, CallbackInfoReturnable<Double> info) {
        if (wasInBlock) {
            info.setReturnValue(1d);
        }
    }



    @Override
    public boolean getInSwimmableBlock() {
        return wasInBlock;
    }

    @Override
    public void setIsInSwimmableBlock(boolean block){
        this.isInBlock = block;
    }
    
}
