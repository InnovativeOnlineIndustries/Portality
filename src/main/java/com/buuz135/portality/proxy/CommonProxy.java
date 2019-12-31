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
package com.buuz135.portality.proxy;

import com.buuz135.portality.block.BlockController;
import com.buuz135.portality.block.BlockFrame;
import com.buuz135.portality.block.BlockGenerator;
import com.buuz135.portality.block.BlockInterdimensionalModule;
import com.buuz135.portality.block.module.BlockCapabilityEnergyModule;
import com.buuz135.portality.block.module.BlockCapabilityFluidModule;
import com.buuz135.portality.block.module.BlockCapabilityItemModule;
import com.buuz135.portality.tile.TileController;
import com.buuz135.portality.tile.TileFrame;
import com.hrznstudio.titanium.event.handler.EventManager;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.Event;


public class CommonProxy {

    public static final BlockController BLOCK_CONTROLLER = new BlockController();
    public static final BlockFrame BLOCK_FRAME = new BlockFrame("frame", TileFrame.class);
    public static final BlockGenerator BLOCK_GENERATOR = new BlockGenerator();

    public static final BlockInterdimensionalModule BLOCK_INTERDIMENSIONAL_MODULE = new BlockInterdimensionalModule();
    public static final BlockCapabilityItemModule BLOCK_CAPABILITY_ITEM_MODULE = new BlockCapabilityItemModule();
    public static final BlockCapabilityFluidModule BLOCK_CAPABILITY_FLUID_MODULE = new BlockCapabilityFluidModule();
    public static final BlockCapabilityEnergyModule BLOCK_CAPABILITY_ENERGY_MODULE = new BlockCapabilityEnergyModule();

    public void onCommon() {
        EventManager.forge(PlayerInteractEvent.RightClickBlock.class).process(this::onInteract).subscribe();
    }

    @OnlyIn(Dist.CLIENT)
    public void onClient(Minecraft instance) {

    }

    public void onInteract(PlayerInteractEvent.RightClickBlock event) {
        if (event.getPlayer().isCrouching() && event.getPlayer().world.getBlockState(event.getPos()).getBlock().equals(BLOCK_CONTROLLER)) {
            TileController controller = (TileController) event.getWorld().getTileEntity(event.getPos());
            if (!controller.getDisplay().isItemEqual(event.getItemStack())) {
                event.setUseBlock(Event.Result.ALLOW);
            }
        }
    }

}
