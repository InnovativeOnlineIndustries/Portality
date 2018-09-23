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

import com.buuz135.portality.handler.CustomEnergyStorageHandler;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;

import javax.annotation.Nullable;


public class TileEntityEnergyModule extends TileBase implements ITickable {

    private CustomEnergyStorageHandler energy;

    public TileEntityEnergyModule() {
        energy = new CustomEnergyStorageHandler(100000, 5000, 5000, 0);
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        energy.readFromNBT(compound);
        super.readFromNBT(compound);
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        compound = super.writeToNBT(compound);
        energy.writeToNBT(compound);
        return compound;
    }

    @Override
    public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing) {
        return capability == CapabilityEnergy.ENERGY || super.hasCapability(capability, facing);
    }

    @Nullable
    @Override
    public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing) {
        if (capability == CapabilityEnergy.ENERGY) return (T) energy;
        return super.getCapability(capability, facing);
    }

    @Override
    public void update() {
        for (EnumFacing facing : EnumFacing.VALUES) {
            BlockPos checking = this.pos.offset(facing);
            TileEntity checkingTile = this.world.getTileEntity(checking);
            if (checkingTile != null) {
                if (checkingTile.hasCapability(CapabilityEnergy.ENERGY, facing.getOpposite())) {
                    IEnergyStorage storage = checkingTile.getCapability(CapabilityEnergy.ENERGY, facing.getOpposite());
                    int energy = storage.receiveEnergy(Math.min(this.energy.getEnergyStored(), 1000), false);
                    if (energy > 0) {
                        this.energy.extractEnergy(energy, false);
                        return;
                    }
                }
            }
        }
    }
}
