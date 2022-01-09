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

import com.buuz135.portality.tile.EnergyModuleTile;
import com.hrznstudio.titanium.api.IFactory;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;

import javax.annotation.Nullable;
import java.util.List;

public class CapabilityEnergyModuleBlock extends CapabilityModuleBlock<IEnergyStorage, EnergyModuleTile> {

    public CapabilityEnergyModuleBlock() {
        super("module_energy", EnergyModuleTile.class);
    }

    @Override
    public Capability<IEnergyStorage> getCapability() {
        return CapabilityEnergy.ENERGY;
    }

    @Override
    void internalWork(Level current, BlockPos myself, Level otherWorld, List<BlockPos> compatibleBlockPos) {
        current.getBlockEntity(myself).getCapability(getCapability()).ifPresent(storage -> {
                for (BlockPos pos : compatibleBlockPos) {
                    BlockEntity entity = otherWorld.getBlockEntity(pos);
                    if (entity != null) {
                        entity.getCapability(getCapability()).ifPresent(otherStorage -> {
                            int energy = otherStorage.receiveEnergy(Math.min(storage.getEnergyStored(), 5000), false);
                            storage.extractEnergy(energy, false);
                            if (energy > 0) return;
                        });
                    }
                }
            });
    }

    @Override
    public IFactory<EnergyModuleTile> getTileEntityFactory() {
        return EnergyModuleTile::new;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockGetter worldIn) {
        return new EnergyModuleTile();
    }
}
