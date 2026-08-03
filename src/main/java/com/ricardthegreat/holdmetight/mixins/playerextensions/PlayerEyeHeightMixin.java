package com.ricardthegreat.holdmetight.mixins.playerextensions;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;

import virtuoel.pehkui.api.ScaleData;
import virtuoel.pehkui.api.ScaleType;
import virtuoel.pehkui.api.ScaleTypes;

/*
 * Keep the player's eye height (and therefore the first person camera) tracking the visual scale.
 *
 * Since 1.20.2 eye height lives inside EntityDimensions (Entity.getEyeHeight() returns
 * getDimensions().eyeHeight()), and Pehkui scales the dimensions using the hitbox height scale.
 * HoldMeTight deliberately caps the hitbox at maxHitboxScale (default 8) in PlayerSizeUtils so
 * giant players don't get stuck in doorways, which means the eye height also stops growing past
 * that point while the model keeps rendering at the full BASE scale. At scale 50 the head renders
 * ~81 blocks up but the camera stays at ~13.
 *
 * When the effective hitbox height scale is below the base scale (i.e. the hitbox has been
 * capped), scale the eye height back up by baseScale/heightScale so the camera sits at the head.
 * When the hitbox matches the base scale this is an identity so nothing else is affected.
 */
@Mixin(Entity.class)
public abstract class PlayerEyeHeightMixin {

    @ModifyReturnValue(method = "getEyeHeight()F", at = @At("RETURN"))
    private float holdmetight$eyeHeightTracksVisualScale(float original) {
        if (!((Object) this instanceof Player player)) {
            return original;
        }
        try {
            ScaleData baseData = ScaleTypes.BASE.getScaleData(player);
            ScaleData heightData = ScaleTypes.HITBOX_HEIGHT.getScaleData(player);
            float baseScale = baseData.getScale();
            float heightScale = heightData.getScale();
            if (heightScale >= baseScale || heightScale <= 0.0F) {
                return original;
            }
            return original * (baseScale / heightScale);
        } catch (Throwable t) {
            return original;
        }
    }
}
