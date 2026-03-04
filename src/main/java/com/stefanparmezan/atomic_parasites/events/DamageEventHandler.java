package com.stefanparmezan.atomic_parasites.events;

import com.stefanparmezan.atomic_parasites.brain.BrainManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.world.ExplosionEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class DamageEventHandler {

    @SubscribeEvent
    public static void onPlayerDamaged(LivingHurtEvent event) {
        if (event.getEntity() instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) event.getEntity();
            if (player.world.isRemote) {  // Только на клиенте
                BrainManager.onPlayerDamaged();
            }
        }
    }

    @SubscribeEvent
    public static void onExplosion(ExplosionEvent.Detonate event) {
        for (Object obj : event.getAffectedEntities()) {
            if (obj instanceof EntityPlayer) {
                EntityPlayer player = (EntityPlayer) obj;
                if (player.world.isRemote) {  // Только на клиенте
                    BrainManager.onPlayerExploded();
                }
            }
        }
    }
}