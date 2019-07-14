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
package com.buuz135.portality.tile;


import com.buuz135.portality.proxy.CommonProxy;
import com.hrznstudio.titanium.annotation.Save;
import com.hrznstudio.titanium.client.gui.addon.EnergyBarGuiAddon;
import com.hrznstudio.titanium.energy.NBTEnergyHandler;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;


public class TileEntityEnergyModule extends TileModule {

    @Save
    private NBTEnergyHandler energyHandler;
    private LazyOptional<IEnergyStorage> energyCap;

    public TileEntityEnergyModule() {
        super(CommonProxy.BLOCK_CAPABILITY_ENERGY_MODULE);
        this.energyHandler = new NBTEnergyHandler(this, 10000);
        this.energyCap = LazyOptional.of(() -> this.energyHandler);
        this.addGuiAddonFactory(() -> new EnergyBarGuiAddon(10, 20, energyHandler));
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        if (cap == CapabilityEnergy.ENERGY) return energyCap.cast();
        return super.getCapability(cap, side);
    }

    @Override
    public void tick() {
        if (!isInput()) {
            for (Direction facing : Direction.values()) {
                BlockPos checking = this.pos.offset(facing);
                TileEntity checkingTile = this.world.getTileEntity(checking);
                if (checkingTile != null) {
                    checkingTile.getCapability(CapabilityEnergy.ENERGY, facing.getOpposite()).ifPresent(storage -> {
                        int energy = storage.receiveEnergy(Math.min(this.energyHandler.getEnergyStored(), 1000), false);
                        if (energy > 0) {
                            this.energyHandler.extractEnergy(energy, false);
                            return;
                        }
                    });
                }
            }
        }
    }
}
