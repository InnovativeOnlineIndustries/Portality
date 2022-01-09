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

import com.buuz135.portality.tile.FluidModuleTile;
import com.hrznstudio.titanium.api.IFactory;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;

import javax.annotation.Nullable;
import java.util.List;

public class CapabilityFluidModuleBlock extends CapabilityModuleBlock<IFluidHandler, FluidModuleTile> {

    public CapabilityFluidModuleBlock() {
        super("module_fluids", FluidModuleTile.class);
    }

    @Override
    public Capability<IFluidHandler> getCapability() {
        return CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY;
    }

    @Override
    void internalWork(Level current, BlockPos myself, Level otherWorld, List<BlockPos> compatibleBlockPos) {
        current.getBlockEntity(myself).getCapability(getCapability(), null).ifPresent(handler -> {
            if (!handler.drain(500, IFluidHandler.FluidAction.SIMULATE).isEmpty()) {
                for (BlockPos pos : compatibleBlockPos) {
                    BlockEntity otherTile = otherWorld.getBlockEntity(pos);
                    if (otherTile != null) {
                        otherTile.getCapability(getCapability(), null).ifPresent(otherHandler -> {
                            int filled = otherHandler.fill(handler.drain(500, IFluidHandler.FluidAction.SIMULATE), IFluidHandler.FluidAction.EXECUTE);
                            handler.drain(filled, IFluidHandler.FluidAction.EXECUTE);
                            if (filled > 0) return;
                        });
                    }
                }
            }
        });
    }

    @Override
    public IFactory<FluidModuleTile> getTileEntityFactory() {
        return FluidModuleTile::new;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockGetter worldIn) {
        return new FluidModuleTile();
    }
}
