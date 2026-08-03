package com.ricardthegreat.holdmetight.network.serverbound;

import com.ricardthegreat.holdmetight.items.SizeRay;

import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class SSizeRaySync implements CustomPacketPayload {

    public static final Type<SSizeRaySync> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath("holdmetight", "s_size_ray_sync"));
    public static final StreamCodec<FriendlyByteBuf, SSizeRaySync> STREAM_CODEC = StreamCodec.ofMember(SSizeRaySync::write, SSizeRaySync::new);

    private final CompoundTag tag;

    // for scaleType 0 - sets target,  1- mults target
    //probably gonna add more, not sure on the default yet maybe just setting to 1
    public SSizeRaySync(CompoundTag tag){
        this.tag = tag;
    }
    
    public SSizeRaySync(FriendlyByteBuf buffer){
        this(buffer.readNbt());
    }

    public void write(FriendlyByteBuf buffer){
        buffer.writeNbt(tag);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(SSizeRaySync data, IPayloadContext context){
        context.enqueueWork(() -> {
            ServerPlayer sender = (ServerPlayer) context.player();
            ItemStack item = sender.getItemInHand(InteractionHand.MAIN_HAND);
            if(item.getItem() instanceof SizeRay){
                item.set(DataComponents.CUSTOM_DATA, CustomData.of(data.tag));
            }
        });
    }
}
