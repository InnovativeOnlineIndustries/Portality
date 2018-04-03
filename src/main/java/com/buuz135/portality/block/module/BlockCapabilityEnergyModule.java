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
