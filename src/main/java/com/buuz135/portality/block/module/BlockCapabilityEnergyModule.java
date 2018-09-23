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

import com.buuz135.portality.tile.TileEntityEnergyModule;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;

import java.util.List;

public class BlockCapabilityEnergyModule extends BlockCapabilityModule<IEnergyStorage, TileEntityEnergyModule> {

    public BlockCapabilityEnergyModule() {
        super("module_energy", TileEntityEnergyModule.class);
    }

    @Override
    public Capability<IEnergyStorage> getCapability() {
        return CapabilityEnergy.ENERGY;
    }

    @Override
    void internalWork(World current, BlockPos myself, World otherWorld, List<BlockPos> compatibleBlockPos) {
        if (current.getTileEntity(myself).hasCapability(getCapability(), null)) {
            IEnergyStorage storage = current.getTileEntity(myself).getCapability(getCapability(), null);
            for (BlockPos pos : compatibleBlockPos) {
                TileEntity entity = otherWorld.getTileEntity(pos);
                if (entity.hasCapability(getCapability(), null)) {
                    IEnergyStorage otherStorage = entity.getCapability(getCapability(), null);
                    int energy = otherStorage.receiveEnergy(Math.min(storage.getEnergyStored(), 5000), false);
                    storage.extractEnergy(energy, false);
                    if (energy > 0) return;
                }
            }

        }
    }
}
