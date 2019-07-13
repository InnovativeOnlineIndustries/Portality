package com.buuz135.portality.tile;

import com.hrznstudio.titanium.block.BlockTileBase;
import com.hrznstudio.titanium.block.tile.TileActive;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.BlockPos;

public class TileFrame extends TileActive {

    private BlockPos controllerPos;

    public TileFrame(BlockTileBase base) {
        super(base);
    }

    @Override
    public CompoundNBT write(CompoundNBT compound) {
        compound = super.write(compound);
        if (controllerPos != null) {
            compound.putInt("X", controllerPos.getX());
            compound.putInt("Y", controllerPos.getY());
            compound.putInt("Z", controllerPos.getZ());
        }
        return compound;
    }

    @Override
    public void read(CompoundNBT compound) {
        super.read(compound);
        if (compound.hasUniqueId("X")) {
            controllerPos = new BlockPos(compound.getInt("X"), compound.getInt("Y"), compound.getInt("Z"));
        }
    }

    public BlockPos getControllerPos() {
        return controllerPos;
    }

    public void setControllerPos(BlockPos controllerPos) {
        this.controllerPos = controllerPos;
    }
}
