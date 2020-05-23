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
import com.buuz135.portality.proxy.client.render.TESRPortal;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraftforge.fml.client.registry.ClientRegistry;

public class ClientProxy extends CommonProxy {

    @Override
    public void onClient(Minecraft instance) {
        super.onClient(instance);
        ClientRegistry.bindTileEntityRenderer(CommonProxy.BLOCK_CONTROLLER.getTileEntityType(), TESRPortal::new);
        RenderTypeLookup.setRenderLayer(CommonProxy.BLOCK_CONTROLLER, RenderType.getCutout());
        RenderTypeLookup.setRenderLayer(CommonProxy.BLOCK_FRAME, RenderType.getCutout());
        RenderTypeLookup.setRenderLayer(CommonProxy.BLOCK_CAPABILITY_ENERGY_MODULE, RenderType.getCutout());
        RenderTypeLookup.setRenderLayer(CommonProxy.BLOCK_CAPABILITY_FLUID_MODULE, RenderType.getCutout());
        RenderTypeLookup.setRenderLayer(CommonProxy.BLOCK_INTERDIMENSIONAL_MODULE, RenderType.getCutout());
        RenderTypeLookup.setRenderLayer(CommonProxy.BLOCK_CAPABILITY_ITEM_MODULE, RenderType.getCutout());
    }
}
