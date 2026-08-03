package com.ricardthegreat.holdmetight.mixins.rendering;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;

import com.ricardthegreat.holdmetight.HMTConfig;
import com.ricardthegreat.holdmetight.utils.compat.SablePlotLight;
import com.ricardthegreat.holdmetight.utils.compat.SableCompat;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;

/*
 * Stopgap fix for entities on Sable sub-levels rendering near-black at certain sub-level positions.
 *
 * Sable's entity lighting computes the light from a probe around the entity's GLOBAL position and
 * falls back to the empty main world when that probe misses the sub-level plot, producing dark
 * entities. Whenever the rendered entity is actually on a (client) sub-level we re-sample that
 * sub-level's own light and override the block/sky result, so lighting no longer depends on where
 * the sub-level happens to be parked.
 *
 * We inject on the RETURNS of getSkyLightLevel/getBlockLightLevel rather than getPackedLightCoords,
 * because Sable itself redirects getPackedLightCoords and the inner getBrightness calls; a modifier
 * on these two method returns is a distinct injection site and does not collide with Sable's. When
 * the config switch "sableEntityLightingFix" is off (or Sable is absent), every handler simply
 * returns the original value untouched.
 */
@Mixin(EntityRenderer.class)
public abstract class SableEntityLightingMixin<T extends Entity> {

    @ModifyReturnValue(method = "getSkyLightLevel", at = @At("RETURN"))
    private int holdmetight$skyLightLevel(int original, T entity, BlockPos blockPos) {
        if (!enabled()) {
            return original;
        }
        int[] light = SablePlotLight.sample(entity);
        return light == null ? original : light[1];
    }

    @ModifyReturnValue(method = "getBlockLightLevel", at = @At("RETURN"))
    private int holdmetight$blockLightLevel(int original, T entity, BlockPos blockPos) {
        // A burning entity is always lit to block-light 15; keep the vanilla behaviour.
        if (entity.isOnFire() || !enabled()) {
            return original;
        }
        int[] light = SablePlotLight.sample(entity);
        return light == null ? original : light[0];
    }

    private boolean enabled() {
        return SableCompat.isSableLoaded() && HMTConfig.CLIENT_CONFIG.isSableEntityLightingFixEnabled();
    }
}