package com.stefanparmezan.atomic_parasites.network;

import com.stefanparmezan.atomic_parasites.main.AtomicParasites;
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

    // === 📦 ОБРАБОТЧИК НА КЛИЕНТЕ ===
    @SideOnly(Side.CLIENT)
    public static class Handler implements IMessageHandler<CameraShakePacket, IMessage> {
        @Override
        public IMessage onMessage(final CameraShakePacket message, final MessageContext ctx) {
            Minecraft.getMinecraft().addScheduledTask(new Runnable() {
                @Override
                public void run() {
                    AtomicParasites.LOGGER.debug("[CameraShake] 📳 Packet RECEIVED! Intensity: {}", message.intensity);

                    Minecraft mc = Minecraft.getMinecraft();
                    if (mc.player != null) {
                        // ✅ ОТДЕЛЬНЫЙ РАНДОМ ДЛЯ КАЖДОЙ ОСИ
                        float shakeX = message.intensity * (mc.player.getRNG().nextFloat() - 0.5f) * 2;
                        float shakeY = message.intensity * (mc.player.getRNG().nextFloat() - 0.5f) * 2;

                        // ✅ ПРИМЕНЯЕМ ТРЯСКУ
                        mc.player.rotationPitch += shakeX;
                        mc.player.rotationYaw += shakeY;

                        AtomicParasites.LOGGER.debug("[CameraShake] 🎮 Applied shake: Pitch={}, Yaw={}", shakeX, shakeY);
                    }
                }
            });
            return null;
        }
    }
}