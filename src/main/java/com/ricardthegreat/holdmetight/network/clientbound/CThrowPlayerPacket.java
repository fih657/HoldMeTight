package com.ricardthegreat.holdmetight.network.clientbound;

import java.util.UUID;

import org.joml.Vector3f;

import com.ricardthegreat.holdmetight.client.handlers.ClientPacketHandler;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class CThrowPlayerPacket implements CustomPacketPayload {

    public static final Type<CThrowPlayerPacket> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath("holdmetight", "c_throw_player"));
    public static final StreamCodec<FriendlyByteBuf, CThrowPlayerPacket> STREAM_CODEC = StreamCodec.ofMember(CThrowPlayerPacket::write, CThrowPlayerPacket::new);

    private final UUID thrown;
    private final Vector3f speed;

    public CThrowPlayerPacket(UUID thrown, Vector3f speed){
        this.thrown = thrown;
        this.speed = speed;
    }

    public CThrowPlayerPacket(FriendlyByteBuf buffer){
        this(buffer.readUUID(), buffer.readVector3f());
    }

    public void write(FriendlyByteBuf buffer){
        buffer.writeUUID(thrown);
        buffer.writeVector3f(speed);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(CThrowPlayerPacket data, IPayloadContext context){
        context.enqueueWork(() -> ClientPacketHandler.handleThrowPlayerPacket(data));
    }

    public Vector3f getMovement(){
        return this.speed;
    }

    public UUID getThrownId(){
        return this.thrown;
    }
    
}
