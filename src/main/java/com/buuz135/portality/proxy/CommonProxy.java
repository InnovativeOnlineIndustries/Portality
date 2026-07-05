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

import com.buuz135.portality.tile.ControllerTile;
import com.hrznstudio.titanium.event.handler.EventManager;
import com.hrznstudio.titanium.module.BlockWithTile;
import net.minecraft.client.Minecraft;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.common.util.TriState;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.registries.DeferredHolder;


public class CommonProxy {

    public static BlockWithTile BLOCK_CONTROLLER;
    public static BlockWithTile BLOCK_FRAME;
    public static BlockWithTile BLOCK_GENERATOR;

    public static BlockWithTile BLOCK_INTERDIMENSIONAL_MODULE;
    public static BlockWithTile BLOCK_CAPABILITY_ITEM_MODULE;
    public static BlockWithTile BLOCK_CAPABILITY_FLUID_MODULE;
    public static BlockWithTile BLOCK_CAPABILITY_ENERGY_MODULE;

    public static DeferredHolder<Item, Item> TELEPORTATION_TOKEN_ITEM;

    public void onCommon() {
        EventManager.forge(PlayerInteractEvent.RightClickBlock.class).process(this::onInteract).subscribe();
    }

    @OnlyIn(Dist.CLIENT)
    public void onClient(Minecraft instance) {

    }

    public void onInteract(PlayerInteractEvent.RightClickBlock event) {
        if (event.getEntity().isCrouching() && event.getLevel().getBlockState(event.getPos()).getBlock().equals(BLOCK_CONTROLLER.block().get())) {
            ControllerTile controller = (ControllerTile) event.getLevel().getBlockEntity(event.getPos());
            if (!ItemStack.isSameItemSameComponents(controller.getDisplay(), event.getItemStack())) {
                event.setUseBlock(TriState.TRUE);
            }
        }
    }

}
