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
package com.buuz135.portality.tile;


import com.buuz135.portality.proxy.CommonProxy;
import com.hrznstudio.titanium.annotation.Save;
import com.hrznstudio.titanium.block.BasicTileBlock;
import com.hrznstudio.titanium.client.screen.addon.EnergyBarScreenAddon;
import com.hrznstudio.titanium.component.energy.EnergyStorageComponent;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.energy.IEnergyStorage;

import javax.annotation.Nonnull;


public class EnergyModuleTile extends ModuleTile<EnergyModuleTile> {

    @Save
    private final EnergyStorageComponent<EnergyModuleTile> energyStorage;

    public EnergyModuleTile(BlockPos pos, BlockState state) {
        super((BasicTileBlock<EnergyModuleTile>) CommonProxy.BLOCK_CAPABILITY_ENERGY_MODULE.block().get(), CommonProxy.BLOCK_CAPABILITY_ENERGY_MODULE.type().get(), pos, state);
        this.energyStorage = new EnergyStorageComponent<>(10000, 10000, 10000, 10, 20);
        this.energyStorage.setComponentHarness(this.getSelf());
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void initClient() {
        super.initClient();
        this.addGuiAddonFactory(() -> new EnergyBarScreenAddon(10, 20, energyStorage));
    }

    @Nonnull
    public EnergyStorageComponent<EnergyModuleTile> getEnergyStorage() {
        return energyStorage;
    }

    @Override
    public void serverTick(Level level, BlockPos pos, BlockState state, EnergyModuleTile blockEntity) {
        super.serverTick(level, pos, state, blockEntity);
        if (level.getGameTime() % 10 == 0) {
            syncObject(this.energyStorage);
        }
        if (!isInput()) {
            for (Direction facing : Direction.values()) {
                BlockPos checking = this.worldPosition.relative(facing);
                IEnergyStorage storage = this.level.getCapability(Capabilities.EnergyStorage.BLOCK, checking, facing.getOpposite());
                if (storage != null) {
                    int energy = storage.receiveEnergy(Math.min(this.energyStorage.getEnergyStored(), 1000), false);
                    if (energy > 0) {
                        this.energyStorage.extractEnergy(energy, false);
                        return;
                    }
                }
            }
        }
    }

    @Nonnull
    @Override
    public EnergyModuleTile getSelf() {
        return this;
    }
}
