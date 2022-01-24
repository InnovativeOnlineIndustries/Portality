/**
 * MIT License
 *
 * Copyright (c) 2018
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.buuz135.portality.proxy.client;

import com.buuz135.portality.proxy.CommonProxy;
import com.buuz135.portality.proxy.client.render.AuraRender;
import com.buuz135.portality.proxy.client.render.TESRPortal;
import com.hrznstudio.titanium.TitaniumClient;
import com.hrznstudio.titanium.block.BasicTileBlock;
import com.hrznstudio.titanium.event.handler.EventManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.client.ClientRegistry;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.TextureStitchEvent;

public class ClientProxy extends CommonProxy {

    public ClientProxy() {
        EventManager.mod(EntityRenderersEvent.RegisterRenderers.class).process(registerRenderers -> {
            System.out.println(((BasicTileBlock)CommonProxy.BLOCK_CONTROLLER.get()).getTileEntityType());
            registerRenderers.registerBlockEntityRenderer(((BasicTileBlock)CommonProxy.BLOCK_CONTROLLER.get()).getTileEntityType(), TESRPortal::new);
        }).subscribe();
        EventManager.mod(EntityRenderersEvent.AddLayers.class).process(registerRenderers -> {
            for (String skin : registerRenderers.getSkins()) {
                PlayerRenderer renderer = registerRenderers.getSkin(skin);
                renderer.addLayer(new AuraRender(renderer));
            }
        }).subscribe();
        EventManager.mod(TextureStitchEvent.Pre.class).process(pre -> {
            if (pre.getAtlas().equals(InventoryMenu.BLOCK_ATLAS)){
                pre.addSprite(TESRPortal.TEXTURE);
            }
        }).subscribe();
    }

    @Override
    public void onClient(Minecraft instance) {
        super.onClient(instance);
        ItemBlockRenderTypes.setRenderLayer(CommonProxy.BLOCK_CONTROLLER.get(), RenderType.cutout());
        ItemBlockRenderTypes.setRenderLayer(CommonProxy.BLOCK_FRAME.get(), RenderType.cutout());
        ItemBlockRenderTypes.setRenderLayer(CommonProxy.BLOCK_CAPABILITY_ENERGY_MODULE.get(), RenderType.cutout());
        ItemBlockRenderTypes.setRenderLayer(CommonProxy.BLOCK_CAPABILITY_FLUID_MODULE.get(), RenderType.cutout());
        ItemBlockRenderTypes.setRenderLayer(CommonProxy.BLOCK_INTERDIMENSIONAL_MODULE.get(), RenderType.cutout());
        ItemBlockRenderTypes.setRenderLayer(CommonProxy.BLOCK_CAPABILITY_ITEM_MODULE.get(), RenderType.cutout());
        Minecraft.getInstance().getBlockColors().register((state, world, pos, index) -> {
            if (index == 0 && world != null) {
                BlockEntity tileEntity = world.getBlockEntity(pos);
                if (tileEntity instanceof IPortalColor) {
                    return ((IPortalColor) tileEntity).getColor();
                }
            }
            return -16739073;
        }, CommonProxy.BLOCK_FRAME.get(), CommonProxy.BLOCK_CONTROLLER.get(), CommonProxy.BLOCK_CAPABILITY_ENERGY_MODULE.get(), CommonProxy.BLOCK_CAPABILITY_FLUID_MODULE.get(), CommonProxy.BLOCK_CAPABILITY_ITEM_MODULE.get(), CommonProxy.BLOCK_INTERDIMENSIONAL_MODULE.get());
    }
}
