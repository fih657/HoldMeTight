package com.ricardthegreat.holdmetight.network.clientbound;

import org.joml.Vector3f;

import com.ricardthegreat.holdmetight.client.handlers.ClientPacketHandler;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class CThrowEntityPacket implements CustomPacketPayload {

    public static final Type<CThrowEntityPacket> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath("holdmetight", "c_throw_entity"));
    public static final StreamCodec<FriendlyByteBuf, CThrowEntityPacket> STREAM_CODEC = StreamCodec.ofMember(CThrowEntityPacket::write, CThrowEntityPacket::new);

    private final int thrown;
    private final Vector3f speed;

    public CThrowEntityPacket(int thrown, Vector3f speed){
        this.thrown = thrown;
        this.speed = speed;
    }

    public CThrowEntityPacket(FriendlyByteBuf buffer){
        this(buffer.readInt(), buffer.readVector3f());
    }

    public void write(FriendlyByteBuf buffer){
        buffer.writeInt(thrown);
        buffer.writeVector3f(speed);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(CThrowEntityPacket data, IPayloadContext context){
        context.enqueueWork(() -> ClientPacketHandler.handleThrowEntityPacket(data));
    }

    public Vector3f getMovement(){
        return this.speed;
    }

    public int getThrownId(){
        return this.thrown;
    }
    
}
