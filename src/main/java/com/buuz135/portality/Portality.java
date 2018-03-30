package com.buuz135.portality;

import com.buuz135.portality.block.BlockBasic;
import com.buuz135.portality.proxy.CommonProxy;
import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;

@Mod(
        modid = Portality.MOD_ID,
        name = Portality.MOD_NAME,
        version = Portality.VERSION
)
public class Portality {

    public static final String MOD_ID = "portality";
    public static final String MOD_NAME = "Portality";
    public static final String VERSION = "1.0-SNAPSHOT";

    @Mod.Instance(MOD_ID)
    public static Portality INSTANCE;

    public static final CreativeTabs TAB = new CreativeTabs(MOD_ID) {
        @Override
        public ItemStack getTabIconItem() {
            return new ItemStack(CommonProxy.BLOCK_CONTROLLER);
        }
    };
    @SidedProxy(clientSide = "com.buuz135.portality.proxy.client.ClientProxy", serverSide = "com.buuz135.portality.proxy.CommonProxy")
    public static CommonProxy proxy;

    @Mod.EventHandler
    public void preinit(FMLPreInitializationEvent event) {
        proxy.onPreInit(event);
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        proxy.onInit(event);
    }

    @Mod.EventHandler
    public void postinit(FMLPostInitializationEvent event) {
        proxy.onPostInit(event);
    }

    @Mod.EventBusSubscriber
    public static class ObjectRegistryHandler {

        @SubscribeEvent
        public static void addItems(RegistryEvent.Register<Item> event) {
            BlockBasic.BLOCKS.forEach(blockBasic -> {
                event.getRegistry().register(new ItemBlock(blockBasic).setRegistryName(blockBasic.getRegistryName()));
                if (FMLCommonHandler.instance().getSide() == Side.CLIENT) blockBasic.registerRender();
            });
        }

        @SubscribeEvent
        public static void addBlocks(RegistryEvent.Register<Block> event) {
            BlockBasic.BLOCKS.forEach(blockBasic -> blockBasic.registerObject(event.getRegistry()));
        }
    }

}
