package com.stefanparmezan.atomic_parasites.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class CameraShakePacket implements IMessage {

    private float intensity;

    public CameraShakePacket() {}

    public CameraShakePacket(float intensity) {
        this.intensity = intensity;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        intensity = buf.readFloat();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeFloat(intensity);
    }

    @SideOnly(Side.CLIENT)
    public static class Handler implements IMessageHandler<CameraShakePacket, IMessage> {
        @Override
        public IMessage onMessage(CameraShakePacket message, MessageContext ctx) {
            Minecraft.getMinecraft().addScheduledTask(() -> {
                float shake = message.intensity * (Minecraft.getMinecraft().player.getRNG().nextFloat() - 0.5f) * 2;
                Minecraft mc = Minecraft.getMinecraft();
                if (mc.player != null) {
                    mc.player.rotationPitch += shake * 0.5f;
                    mc.player.rotationYaw += shake;
                }
            });
            return null;
        }
    }
}