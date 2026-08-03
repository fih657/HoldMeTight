package com.ricardthegreat.holdmetight.capabilities;

import com.ricardthegreat.holdmetight.HoldMeTight;
import com.ricardthegreat.holdmetight.capabilities.carry.PlayerCarry;
import com.ricardthegreat.holdmetight.capabilities.preferences.PlayerPreferences;
import com.ricardthegreat.holdmetight.capabilities.size.PlayerSize;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

public class ModAttachments {

    public static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES = DeferredRegister.create(NeoForgeRegistries.Keys.ATTACHMENT_TYPES, HoldMeTight.MODID);

    public static final DeferredHolder<AttachmentType<?>, AttachmentType<PlayerCarry>> PLAYER_CARRY = ATTACHMENT_TYPES.register("player_carry",
        () -> AttachmentType.serializable(PlayerCarry::new).copyOnDeath().build());

    public static final DeferredHolder<AttachmentType<?>, AttachmentType<PlayerPreferences>> PLAYER_PREFERENCES = ATTACHMENT_TYPES.register("player_preferences",
        () -> AttachmentType.serializable(PlayerPreferences::new).copyOnDeath().build());

    public static final DeferredHolder<AttachmentType<?>, AttachmentType<PlayerSize>> PLAYER_SIZE = ATTACHMENT_TYPES.register("player_size",
        () -> AttachmentType.serializable(PlayerSize::new).copyOnDeath().build());

    public static void register(IEventBus modEventBus) {
        ATTACHMENT_TYPES.register(modEventBus);
    }
}
