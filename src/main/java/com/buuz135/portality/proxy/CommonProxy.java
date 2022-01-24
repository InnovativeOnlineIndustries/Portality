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

import com.buuz135.portality.block.ControllerBlock;
import com.buuz135.portality.block.FrameBlock;
import com.buuz135.portality.block.GeneratorBlock;
import com.buuz135.portality.block.InterdimensionalModuleBlock;
import com.buuz135.portality.block.module.CapabilityEnergyModuleBlock;
import com.buuz135.portality.block.module.CapabilityFluidModuleBlock;
import com.buuz135.portality.block.module.CapabilityItemModuleBlock;
import com.buuz135.portality.item.TeleportationTokenItem;
import com.buuz135.portality.tile.BasicFrameTile;
import com.buuz135.portality.tile.ControllerTile;
import com.hrznstudio.titanium.event.handler.EventManager;
import net.minecraft.client.Minecraft;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.registries.RegistryObject;


public class CommonProxy {

    public static RegistryObject<Block> BLOCK_CONTROLLER;
    public static RegistryObject<Block> BLOCK_FRAME;
    public static RegistryObject<Block> BLOCK_GENERATOR;

    public static RegistryObject<Block> BLOCK_INTERDIMENSIONAL_MODULE;
    public static RegistryObject<Block> BLOCK_CAPABILITY_ITEM_MODULE;
    public static RegistryObject<Block> BLOCK_CAPABILITY_FLUID_MODULE;
    public static RegistryObject<Block> BLOCK_CAPABILITY_ENERGY_MODULE;

    public static RegistryObject<Item> TELEPORTATION_TOKEN_ITEM;

    public void onCommon() {
        EventManager.forge(PlayerInteractEvent.RightClickBlock.class).process(this::onInteract).subscribe();
    }

    @OnlyIn(Dist.CLIENT)
    public void onClient(Minecraft instance) {

    }

    public void onInteract(PlayerInteractEvent.RightClickBlock event) {
        if (event.getPlayer().isCrouching() && event.getPlayer().level.getBlockState(event.getPos()).getBlock().equals(BLOCK_CONTROLLER)) {
            ControllerTile controller = (ControllerTile) event.getWorld().getBlockEntity(event.getPos());
            if (!controller.getDisplay().sameItem(event.getItemStack())) {
                event.setUseBlock(Event.Result.ALLOW);
            }
        }
    }

}
