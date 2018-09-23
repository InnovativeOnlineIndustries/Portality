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
package com.buuz135.portality.block.module;

import com.buuz135.portality.tile.TileEntityFluidModule;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;

import java.util.List;

public class BlockCapabilityFluidModule extends BlockCapabilityModule<IFluidHandler, TileEntityFluidModule> {

    public BlockCapabilityFluidModule() {
        super("module_fluids", TileEntityFluidModule.class);
    }

    @Override
    public Capability<IFluidHandler> getCapability() {
        return CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY;
    }

    @Override
    void internalWork(World current, BlockPos myself, World otherWorld, List<BlockPos> compatibleBlockPos) {
        if (current.getTileEntity(myself).hasCapability(getCapability(), null)) {
            IFluidHandler handler = current.getTileEntity(myself).getCapability(getCapability(), null);
            if (handler.drain(500, false) != null) {
                for (BlockPos pos : compatibleBlockPos) {
                    TileEntity otherTile = otherWorld.getTileEntity(pos);
                    if (otherTile != null && otherTile.hasCapability(getCapability(), null)) {
                        IFluidHandler otherHandler = otherTile.getCapability(getCapability(), null);
                        int filled = otherHandler.fill(handler.drain(500, false), true);
                        handler.drain(filled, true);
                        if (filled > 0) return;
                    }
                }
            }
        }
    }
}
