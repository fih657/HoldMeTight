package com.ricardthegreat.holdmetight.network.serverbound.itempackets.standinitem;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class SApplyPlayerEffectPacket implements CustomPacketPayload {

    public static final Type<SApplyPlayerEffectPacket> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath("holdmetight", "s_apply_player_effect"));
    public static final StreamCodec<FriendlyByteBuf, SApplyPlayerEffectPacket> STREAM_CODEC = StreamCodec.ofMember(SApplyPlayerEffectPacket::write, SApplyPlayerEffectPacket::new);

    private final List<MobEffectInstance> effects; 
    private final UUID targetPlayer;

    public SApplyPlayerEffectPacket(List<MobEffectInstance> effects, UUID targetPlayer){
        this.effects = effects;
        this.targetPlayer = targetPlayer;
    }
    
    public SApplyPlayerEffectPacket(FriendlyByteBuf buffer){
        int listSize = buffer.readInt();
        if (listSize >= 1) {
            this.effects = new ArrayList<>();
        }else{
            this.effects = List.of();
        }
        
        for(int i = 0; i < listSize; i++){
            MobEffectInstance temp = MobEffectInstance.load(buffer.readNbt());
            effects.add(temp);
        }
        
        this.targetPlayer = buffer.readUUID();
    }

    public void write(FriendlyByteBuf buffer){
        buffer.writeInt(effects.size());

        for(int i = 0; i < effects.size(); i++){
            buffer.writeNbt(effects.get(i).save());
        }

        buffer.writeUUID(targetPlayer);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(SApplyPlayerEffectPacket data, IPayloadContext context){
        context.enqueueWork(() -> {
            Player sender = context.player();
            Level level = sender.level();
            Player target = level.getPlayerByUUID(data.targetPlayer);

            if (target != null) {
                target.playSound(SoundEvents.AMBIENT_UNDERWATER_EXIT);
                if (data.effects.size() >= 1) {
                    for(MobEffectInstance mobeffectinstance : data.effects) {
                        if (mobeffectinstance.getEffect().value().isInstantenous()) {
                            mobeffectinstance.getEffect().value().applyInstantenousEffect(sender, sender, target, mobeffectinstance.getAmplifier(), 1.0D);
                        } else {
                            target.addEffect(new MobEffectInstance(mobeffectinstance));
                        }
                    }
                }else{
                    target.removeAllEffects();
                }
            }
        });
    }
}
