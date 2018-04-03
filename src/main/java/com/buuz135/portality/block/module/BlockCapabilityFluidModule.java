package com.buuz135.portality.block.module;

import com.buuz135.portality.tile.TileEntityFluidModule;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;

import java.util.List;

public class BlockCapabilityFluidModule extends BlockCapabilityModule<IFluidHandler, TileEntityFluidModule> {

    public BlockCapabilityFluidModule() {
        super("module_fluids", TileEntityFluidModule.class);
    }

    @Override
    public Capability<IFluidHandler> getCapability() {
        return CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY;
    }

    @Override
    void internalWork(World current, BlockPos myself, World otherWorld, List<BlockPos> compatibleBlockPos) {
        if (current.getTileEntity(myself).hasCapability(getCapability(), null)) {
            IFluidHandler handler = current.getTileEntity(myself).getCapability(getCapability(), null);
            if (handler.drain(500, false) != null) {
                for (BlockPos pos : compatibleBlockPos) {
                    TileEntity otherTile = otherWorld.getTileEntity(pos);
                    if (otherTile != null && otherTile.hasCapability(getCapability(), null)) {
                        IFluidHandler otherHandler = otherTile.getCapability(getCapability(), null);
                        int filled = otherHandler.fill(handler.drain(500, false), true);
                        handler.drain(filled, true);
                        if (filled > 0) return;
                    }
                }
            }
        }
    }
}
