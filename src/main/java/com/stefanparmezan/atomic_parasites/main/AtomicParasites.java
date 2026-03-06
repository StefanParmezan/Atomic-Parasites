package com.stefanparmezan.atomic_parasites.main;

import com.stefanparmezan.atomic_parasites.brain.BrainOverlayHandler;
import com.stefanparmezan.atomic_parasites.brain.BrainSleepHandler;
import com.stefanparmezan.atomic_parasites.brain.BrainTickHandler;
import com.stefanparmezan.atomic_parasites.brain.effects.FearOfDeathHandler;
import com.stefanparmezan.atomic_parasites.brain.effects.PotionFearOfDeath;
import com.stefanparmezan.atomic_parasites.events.ParasitesPhaseEventHandler;
import com.stefanparmezan.atomic_parasites.events.FireBreakingEventHandler;
import com.stefanparmezan.atomic_parasites.network.CameraShakePacket;
import com.stefanparmezan.atomic_parasites.proxy.CommonProxy;
import com.stefanparmezan.atomic_parasites.player_avatar.FaceOverlayHandler;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.relauncher.Side;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(
        modid = AtomicParasitesInfo.MOD_ID,
        name = AtomicParasitesInfo.NAME,
        version = AtomicParasitesInfo.VERSION
)
public class AtomicParasites {

    public static final Logger LOGGER = LogManager.getLogger(AtomicParasitesInfo.MOD_ID);

    public static final SoundEvent FEAR_GASP = new SoundEvent(new ResourceLocation(AtomicParasitesInfo.MOD_ID, "mob.player.fear_gasp"))
            .setRegistryName("mob.player.fear_gasp");

    public static final SimpleNetworkWrapper network = new SimpleNetworkWrapper(AtomicParasitesInfo.MOD_ID);


    @SidedProxy(clientSide = AtomicParasitesInfo.CLIENT, serverSide = AtomicParasitesInfo.COMMON)
    public static CommonProxy proxy;

    @Mod.EventHandler
    public void preinit(FMLPreInitializationEvent preinit) {
        ForgeRegistries.SOUND_EVENTS.register(FEAR_GASP);
        network.registerMessage(CameraShakePacket.Handler.class, CameraShakePacket.class, 0, Side.CLIENT);
        PotionFearOfDeath.register();
        LOGGER.info("[Atomic Parasites] PreInit completed");
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        LOGGER.info("\u001B[34mStarted init");

        // 👇 Регистрируем ВСЕ обработчики событий
        MinecraftForge.EVENT_BUS.register(FireBreakingEventHandler.class);
        MinecraftForge.EVENT_BUS.register(ParasitesPhaseEventHandler.class);
        MinecraftForge.EVENT_BUS.register(new FaceOverlayHandler());
        MinecraftForge.EVENT_BUS.register(new BrainOverlayHandler());
        MinecraftForge.EVENT_BUS.register(new BrainTickHandler());
        MinecraftForge.EVENT_BUS.register(BrainSleepHandler.class);
        MinecraftForge.EVENT_BUS.register(FearOfDeathHandler.class);

        LOGGER.info("\u001B[34mEvent handlers registered");
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        LOGGER.info("\u001B[34mStarted postInit");
    }

    public static CreativeTabs creativeTab = new CreativeTabs("Atomic Parasites") {
        @Override
        public ItemStack createIcon() {
            return new ItemStack(Items.NETHER_WART);
        }
    };
}