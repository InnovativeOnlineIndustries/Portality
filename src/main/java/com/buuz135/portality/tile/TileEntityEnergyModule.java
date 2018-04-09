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
