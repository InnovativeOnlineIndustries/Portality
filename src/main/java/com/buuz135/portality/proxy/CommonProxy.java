package com.buuz135.portality.proxy;

import com.buuz135.portality.block.BlockController;
import com.buuz135.portality.block.BlockFrame;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class CommonProxy {

    public static final BlockController BLOCK_CONTROLLER = new BlockController();
    public static final BlockFrame BLOCK_FRAME = new BlockFrame();

    public void onPreInit(FMLPreInitializationEvent event) {

    }

    public void onInit(FMLInitializationEvent event) {

    }

    public void onPostInit(FMLPostInitializationEvent event) {

    }
}
