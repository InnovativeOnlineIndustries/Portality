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
package com.buuz135.portality.tile;

import com.buuz135.portality.proxy.CommonProxy;
import com.hrznstudio.titanium.annotation.Save;
import com.hrznstudio.titanium.block.BasicTileBlock;
import com.hrznstudio.titanium.component.inventory.SidedInventoryComponent;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nonnull;

public class ItemModuleTile extends ModuleTile<ItemModuleTile> {

    @Save
    public SidedInventoryComponent<ItemModuleTile> handler;

    public ItemModuleTile(BlockPos pos, BlockState state) {
        super((BasicTileBlock<ItemModuleTile>) CommonProxy.BLOCK_CAPABILITY_ITEM_MODULE.get(), pos, state);
        this.addInventory(this.handler = (SidedInventoryComponent<ItemModuleTile>) new SidedInventoryComponent<ItemModuleTile>("inventory", 52, 20, 3 * 4, 0)
                .setColor(DyeColor.YELLOW)
                .setColorGuiEnabled(false)
                .setComponentHarness(this)
                .setRange(4, 3));
    }

    @Nonnull
    @Override
    public ItemModuleTile getSelf() {
        return this;
    }
}
