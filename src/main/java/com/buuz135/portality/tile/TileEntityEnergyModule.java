package com.buuz135.portality.tile;

import com.buuz135.portality.handler.CustomEnergyStorageHandler;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.energy.CapabilityEnergy;

import javax.annotation.Nullable;


public class TileEntityEnergyModule extends TileBase {

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

}
