package com.ricardthegreat.holdmetight.mixins.playerextensions;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;

import com.ricardthegreat.holdmetight.HMTConfig;
import com.ricardthegreat.holdmetight.utils.compat.SableCompat;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.phys.Vec3;

/*
 * Stopgap fix for tiny players being unable to crouch on Sable sub-levels.
 *
 * Sable's player_standup mixin wraps Player.canPlayerFitWithinBlocksAndEntitiesWhen() so it also
 * checks sub-level blocks (CanFallAtleastHelper). That helper builds an oriented box with size
 * (aabb.width - 0.1) on X/Z. For a tiny player the crouch box width is 0.6 * scale, so below
 * ~1/3 scale the width-minus-0.1 goes negative/zero and the SAT test against the deck/floor
 * block always reports a collision. canPlayerFitWithinBlocksAndEntitiesWhen(CROUCHING) then
 * returns false, so LocalPlayer.aiStep() sets crouching=false and Player.updatePose() falls back
 * to Pose.SWIMMING: the crouch pose and camera drop vanish even though sneaking (shift) still
 * slows movement and disables head-bob.
 *
 * For tiny players on a sub-level the space genuinely always fits (their box is minuscule), so we
 * only correct the false-negative (original == false) and only on client, behind the config
 * switch "sableTinyCrouchFix". This is a distinct injection site from Sable's inner
 * @WrapOperation on Level.noCollision, so the two do not collide.
 */
@Mixin(Player.class)
public abstract class TinyPlayerSubLevelCrouchMixin {

    @ModifyReturnValue(method = "canPlayerFitWithinBlocksAndEntitiesWhen", at = @At("RETURN"))
    private boolean holdmetight$tinyPlayerFits(boolean original, Pose pose) {
        if (original || !SableCompat.isSableLoaded()) {
            return original;
        }
        if (!HMTConfig.CLIENT_CONFIG.isSableTinyCrouchFixEnabled()) {
            return original;
        }
        try {
            Player player = (Player) (Object) this;
            if (player.getBbWidth() >= 0.2F) {
                return original;
            }
            Vec3 centre = player.position();
            return SableCompat.isOnSubLevel(player.level(), centre, 1.0) ? true : original;
        } catch (Throwable t) {
            return original;
        }
    }
}