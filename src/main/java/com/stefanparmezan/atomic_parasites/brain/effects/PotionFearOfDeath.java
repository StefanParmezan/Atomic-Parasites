package com.stefanparmezan.atomic_parasites.brain.effects;

import com.stefanparmezan.atomic_parasites.main.AtomicParasites;
import com.stefanparmezan.atomic_parasites.brain.BrainConfig;
import com.stefanparmezan.atomic_parasites.brain.BrainManager;
import com.stefanparmezan.atomic_parasites.main.AtomicParasitesInfo;
import com.stefanparmezan.atomic_parasites.network.CameraShakePacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AbstractAttributeMap;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.UUID;

public class PotionFearOfDeath extends Potion {

    public static final Potion INSTANCE = new PotionFearOfDeath();
    private static final ResourceLocation ICON = new ResourceLocation(AtomicParasitesInfo.MOD_ID, "textures/effects/fear_of_death.png");
    private static final UUID SPEED_MODIFIER_UUID = UUID.fromString("a1b2c3d4-e5f6-4a7b-8c9d-0e1f2a3b4c5d");
    private static final AttributeModifier SPEED_MODIFIER = new AttributeModifier(
            SPEED_MODIFIER_UUID, "Fear Speed Boost", BrainConfig.FEAR_SPEED_MULTIPLIER, 1);

    private PotionFearOfDeath() {
        super(false, 0x8B0000);
        this.setRegistryName("fear_of_death");
        this.setPotionName("effect.atomic_parasites.fear_of_death");
    }

    public static void register() {
        ForgeRegistries.POTIONS.register(INSTANCE);
        AtomicParasites.LOGGER.info("[PotionFearOfDeath] ✅ Registered!");
    }

    @Override
    public boolean hasStatusIcon() { return true; }

    @Override
    @SideOnly(Side.CLIENT)
    public void renderInventoryEffect(int x, int y, PotionEffect effect, Minecraft mc) {
        if (effect.getDuration() > 0) {
            mc.getTextureManager().bindTexture(ICON);
            Gui.drawModalRectWithCustomSizedTexture(x + 6, y + 7, 0, 0, 18, 18, 18, 18);
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void renderHUDEffect(int x, int y, PotionEffect effect, Minecraft mc, float alpha) {
        if (effect.getDuration() > 0) {
            mc.getTextureManager().bindTexture(ICON);
            Gui.drawModalRectWithCustomSizedTexture(x + 3, y + 3, 0, 0, 18, 18, 18, 18);
        }
    }

    @Override
    public void performEffect(net.minecraft.entity.EntityLivingBase entity, int amplifier) {
        if (!(entity instanceof EntityPlayerMP)) return;
        EntityPlayerMP player = (EntityPlayerMP) entity;

        // 1. Санити
        float drainPerTick = BrainConfig.FEAR_SANITY_DRAIN_PER_SEC / 20.0f;
        float currentSanity = BrainManager.getCurrentSanity(player);
        float newSanity = Math.max(0.0f, currentSanity - drainPerTick);
        BrainManager.setSanity(player, newSanity);

        // 2. Звук
        if (player.ticksExisted % BrainConfig.FEAR_SOUND_INTERVAL_TICKS == 0) {
            float pitch = 0.85f + player.getRNG().nextFloat() * 0.3f;
            float volume = 0.5f + player.getRNG().nextFloat() * 0.2f;
            player.world.playSound(null, player.posX, player.posY, player.posZ,
                    AtomicParasites.FEAR_GASP, SoundCategory.HOSTILE, volume, pitch);
            AtomicParasites.LOGGER.info("[PotionFearOfDeath] 🔊 Playing sound | Volume: {} | Pitch: {}", volume, pitch);
        }

        // 3. Тряска
        if (player.ticksExisted % 4 == 0) {
            AtomicParasites.network.sendTo(new CameraShakePacket(BrainConfig.FEAR_CAMERA_SHAKE_INTENSITY), player);
        }

        // 4. Скорость
        applyHiddenSpeed(player);

        // 5. Слепота
        if (!player.isPotionActive(MobEffects.BLINDNESS) ||
                player.getActivePotionEffect(MobEffects.BLINDNESS).getDuration() < BrainConfig.FEAR_BLINDNESS_REFRESH_THRESHOLD) {
            player.addPotionEffect(new PotionEffect(MobEffects.BLINDNESS, BrainConfig.FEAR_BLINDNESS_DURATION, 0, false, false));
        }
    }

    private void applyHiddenSpeed(EntityPlayerMP player) {
        IAttributeInstance speedAttr = player.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED);
        if (speedAttr != null && !speedAttr.hasModifier(SPEED_MODIFIER)) {
            speedAttr.applyModifier(SPEED_MODIFIER);
        }
    }

    @Override
    public void removeAttributesModifiersFromEntity(net.minecraft.entity.EntityLivingBase entity, AbstractAttributeMap attributeMapIn, int amplifier) {
        super.removeAttributesModifiersFromEntity(entity, attributeMapIn, amplifier);
        if (entity instanceof EntityPlayerMP) {
            EntityPlayerMP player = (EntityPlayerMP) entity;
            IAttributeInstance speedAttr = player.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED);
            if (speedAttr != null && speedAttr.hasModifier(SPEED_MODIFIER)) {
                speedAttr.removeModifier(SPEED_MODIFIER);
            }
        }
    }

    @Override
    public boolean isReady(int duration, int amplifier) { return true; }
}