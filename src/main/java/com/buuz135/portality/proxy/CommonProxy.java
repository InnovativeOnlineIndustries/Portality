/*
 * This file is part of Worldgen Indicators.
 *
 * Copyright 2018, Buuz135
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in the
 * Software without restriction, including without limitation the rights to use, copy,
 * modify, merge, publish, distribute, sublicense, and/or sell copies of the Software,
 * and to permit persons to whom the Software is furnished to do so, subject to the
 * following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies
 * or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR
 * PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE
 * FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
 * ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package com.buuz135.portality.proxy;

import com.buuz135.portality.Portality;
import com.buuz135.portality.block.BlockController;
import com.buuz135.portality.block.BlockFrame;
import com.buuz135.portality.block.BlockInterdimensionalModule;
import com.buuz135.portality.block.module.BlockCapabilityEnergyModule;
import com.buuz135.portality.block.module.BlockCapabilityFluidModule;
import com.buuz135.portality.block.module.BlockCapabilityItemModule;
import com.buuz135.portality.gui.GuiHandler;
import com.buuz135.portality.handler.ChunkLoaderHandler;
import com.buuz135.portality.network.*;
import com.buuz135.portality.tile.TileController;
import net.minecraftforge.common.ForgeChunkManager;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.relauncher.Side;

public class CommonProxy {

    public static final BlockController BLOCK_CONTROLLER = new BlockController();
    public static final BlockFrame BLOCK_FRAME = new BlockFrame();

    public static final BlockInterdimensionalModule BLOCK_INTERDIMENSIONAL_MODULE = new BlockInterdimensionalModule();
    public static final BlockCapabilityItemModule BLOCK_CAPABILITY_ITEM_MODULE_INPUT = new BlockCapabilityItemModule();
    public static final BlockCapabilityFluidModule BLOCK_CAPABILITY_FLUID_MODULE = new BlockCapabilityFluidModule();
    public static final BlockCapabilityEnergyModule BLOCK_CAPABILITY_ENERGY_MODULE = new BlockCapabilityEnergyModule();

    public void onPreInit(FMLPreInitializationEvent event) {
        NetworkRegistry.INSTANCE.registerGuiHandler(Portality.INSTANCE, new GuiHandler());
        int id = 0;
        Portality.NETWORK.registerMessage(PortalPrivacyToggleMessage.PortalPrivacyToggleHandler.class, PortalPrivacyToggleMessage.class, id++, Side.SERVER);
        Portality.NETWORK.registerMessage(PortalRenameMessage.PortalRenameHandler.class, PortalRenameMessage.class, id++, Side.SERVER);
        Portality.NETWORK.registerMessage(PortalNetworkMessage.HandleRequest.class, PortalNetworkMessage.Request.class, id++, Side.SERVER);
        Portality.NETWORK.registerMessage(PortalNetworkMessage.HandleResponse.class, PortalNetworkMessage.Response.class, id++, Side.CLIENT);
        Portality.NETWORK.registerMessage(PortalLinkMessage.Handler.class, PortalLinkMessage.class, id++, Side.SERVER);
        Portality.NETWORK.registerMessage(PortalCloseMessage.Handler.class, PortalCloseMessage.class, id++, Side.SERVER);
        Portality.NETWORK.registerMessage(PortalTeleportMessage.Handler.class, PortalTeleportMessage.class, id++, Side.CLIENT);
        Portality.NETWORK.registerMessage(PortalDisplayToggleMessage.Handler.class, PortalDisplayToggleMessage.class, id++, Side.SERVER);

        MinecraftForge.EVENT_BUS.register(this);
        MinecraftForge.EVENT_BUS.register(new PortalitySoundHandler());
    }

    public void onInit(FMLInitializationEvent event) {
        ForgeChunkManager.setForcedChunkLoadingCallback(Portality.INSTANCE, new ChunkLoaderHandler());
    }

    public void onPostInit(FMLPostInitializationEvent event) {

    }

    @SubscribeEvent
    public void onInteract(PlayerInteractEvent.RightClickBlock event) {
        if (event.getEntityPlayer().isSneaking() && event.getEntityPlayer().world.getBlockState(event.getPos()).getBlock().equals(BLOCK_CONTROLLER)) {
            TileController controller = (TileController) event.getWorld().getTileEntity(event.getPos());
            if (!controller.getDisplay().isItemEqual(event.getItemStack())) {
                event.setUseBlock(Event.Result.ALLOW);
            }
        }
    }

}
