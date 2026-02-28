package com.stefanparmezan.atomic_parasites.main;

import com.stefanparmezan.atomic_parasites.events.ParasitesPhaseEventHandler;
import com.stefanparmezan.atomic_parasites.events.PlayerBlockEventHandler;
import com.stefanparmezan.atomic_parasites.init.InitItems;
import com.stefanparmezan.atomic_parasites.proxy.CommonProxy;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(
        modid = AtomicParasitesInfo.MOD_ID,
        name = AtomicParasitesInfo.NAME,
        version = AtomicParasitesInfo.VERSION
)
public class AtomicParasites {

    public static final Logger LOGGER = LogManager.getLogger(AtomicParasitesInfo.MOD_ID);

    @Mod.Instance
    public static AtomicParasites instance;

    @SidedProxy(clientSide = AtomicParasitesInfo.CLIENT, serverSide = AtomicParasitesInfo.COMMON)
    public static CommonProxy proxy;

    @Mod.EventHandler
    public void preinit(FMLPreInitializationEvent preinit) {
        LOGGER.info("\u001B[34mStarted preInit");
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        LOGGER.info("\u001B[34mStarted init");

        // 👇 Регистрируем ВСЕ обработчики событий
        MinecraftForge.EVENT_BUS.register(PlayerBlockEventHandler.class);
        MinecraftForge.EVENT_BUS.register(ParasitesPhaseEventHandler.class);

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