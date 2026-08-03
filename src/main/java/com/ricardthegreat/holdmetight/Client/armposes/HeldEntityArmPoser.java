package com.ricardthegreat.holdmetight.client.armposes;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.fml.common.asm.enumextension.EnumProxy;
import net.neoforged.neoforge.client.IArmPoseTransformer;

public class HeldEntityArmPoser implements IArmPoseTransformer{
    public static final EnumProxy<HumanoidModel.ArmPose> HELD_ENTITY_ARM_POSE = new EnumProxy<>(
        HumanoidModel.ArmPose.class, false, new HeldEntityArmPoser());

    @Override
    public void applyTransform(HumanoidModel<?> model, LivingEntity entity, HumanoidArm arm) {
        switch (arm) {
            case RIGHT:
                model.rightArm.xRot = -1.4f;
                break;

            case LEFT:
                model.leftArm.xRot = -1.4f;
                break;
        
            default:
                break;
        }
    }
}
