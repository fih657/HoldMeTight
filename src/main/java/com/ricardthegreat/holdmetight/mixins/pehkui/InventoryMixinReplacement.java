package com.ricardthegreat.holdmetight.mixins.pehkui;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import virtuoel.pehkui.util.ScaleUtils;

@Mixin(Player.class)
public class InventoryMixinReplacement {

	//1.21.1 removed Container.stillValidBlockEntity in favour of Player.canInteractWithBlock,
	//so this replacement scales the new reach check with the same formula as the original
	@Inject(method = "canInteractWithBlock(Lnet/minecraft/core/BlockPos;D)Z", at = @At("HEAD"), cancellable = true)
	private void pehkui$canInteractWithBlock(BlockPos pos, double extraRange, CallbackInfoReturnable<Boolean> cir) {
		Player player = (Player) (Object) this;
		double x = ((double) pos.getX()) + 0.5D;
		double y = ((double) pos.getY()) + 0.5D;
		double z = ((double) pos.getZ()) + 0.5D;
		final Vec3 eyePos = player.getEyePosition();
		x = (x - 0.5D) + ScaleUtils.getBlockXOffset(pos, player) - (eyePos.x() - player.getX());
		y = (y - 0.5D) + ScaleUtils.getBlockYOffset(pos, player) - (eyePos.y() - player.getY());
		z = (z - 0.5D) + ScaleUtils.getBlockZOffset(pos, player) - (eyePos.z() - player.getZ());
		final double reach = ScaleUtils.getBlockReachScale(player) * (player.blockInteractionRange() + extraRange);

		cir.setReturnValue(player.distanceToSqr(x, y, z) <= reach * reach);
	}
}
