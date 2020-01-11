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
package com.buuz135.portality.block.module;

import com.buuz135.portality.tile.ItemModuleTile;
import com.hrznstudio.titanium.api.IFactory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;

import javax.annotation.Nullable;
import java.util.List;

public class CapabilityItemModuleBlock extends CapabilityModuleBlock<IItemHandler, ItemModuleTile> {

    public CapabilityItemModuleBlock() {
        super("module_items", ItemModuleTile.class);
    }

    @Override
    public Capability<IItemHandler> getCapability() {
        return CapabilityItemHandler.ITEM_HANDLER_CAPABILITY;
    }

    @Override
    void internalWork(World current, BlockPos myself, World otherWorld, List<BlockPos> compatibleBlockPos) {
        current.getTileEntity(myself).getCapability(this.getCapability(), null).ifPresent(handlerSelf -> {
            for (BlockPos otherPos : compatibleBlockPos) {
                TileEntity otherTile = otherWorld.getTileEntity(otherPos);
                if (otherTile != null) {
                    otherTile.getCapability(this.getCapability(), null).ifPresent(handlerOther -> {
                        for (int i = 0; i < handlerSelf.getSlots(); i++) {
                            ItemStack stack = handlerSelf.getStackInSlot(i);
                            if (stack.isEmpty()) continue;
                            if (ItemHandlerHelper.insertItem(handlerOther, stack, true).isEmpty()) {
                                ItemHandlerHelper.insertItem(handlerOther, stack.copy(), false);
                                handlerSelf.getStackInSlot(i).setCount(0);
                                return;
                            }
                        }
                    });
                }
            }
        });
    }

    @Override
    public IFactory<ItemModuleTile> getTileEntityFactory() {
        return ItemModuleTile::new;
    }

    @Nullable
    @Override
    public TileEntity createNewTileEntity(IBlockReader worldIn) {
        return new ItemModuleTile();
    }
}
