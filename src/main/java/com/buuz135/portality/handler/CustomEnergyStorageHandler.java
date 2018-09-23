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
package com.buuz135.portality.handler;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.energy.EnergyStorage;

public class CustomEnergyStorageHandler extends EnergyStorage {

    public CustomEnergyStorageHandler(int capacity, int maxReceive, int maxExtract, int energy) {
        super(capacity, maxReceive, maxExtract, energy);
    }

    public int extractEnergyInternal(int maxExtract, boolean simulate) {
        int before = this.maxExtract;
        this.maxExtract = Integer.MAX_VALUE;

        int toReturn = this.extractEnergy(maxExtract, simulate);

        this.maxExtract = before;
        return toReturn;
    }

    public int receiveEnergyInternal(int maxReceive, boolean simulate) {
        int before = this.maxReceive;
        this.maxReceive = Integer.MAX_VALUE;

        int toReturn = this.receiveEnergy(maxReceive, simulate);

        this.maxReceive = before;
        return toReturn;
    }

    @Override
    public int receiveEnergy(int maxReceive, boolean simulate) {
        if (!this.canReceive()) {
            return 0;
        }
        int energy = this.getEnergyStored();

        int energyReceived = Math.min(this.capacity - energy, Math.min(this.maxReceive, maxReceive));
        if (!simulate) {
            this.setEnergyStored(energy + energyReceived);
        }

        return energyReceived;
    }

    @Override
    public int extractEnergy(int maxExtract, boolean simulate) {
        if (!this.canExtract()) {
            return 0;
        }
        int energy = this.getEnergyStored();

        int energyExtracted = Math.min(energy, Math.min(this.maxExtract, maxExtract));
        if (!simulate) {
            this.setEnergyStored(energy - energyExtracted);
        }
        return energyExtracted;
    }

    public void readFromNBT(NBTTagCompound compound) {
        this.setEnergyStored(compound.getInteger("Energy"));
    }

    public void writeToNBT(NBTTagCompound compound) {
        compound.setInteger("Energy", this.getEnergyStored());
    }

    public void setEnergyStored(int energy) {
        this.energy = energy;
    }
}
